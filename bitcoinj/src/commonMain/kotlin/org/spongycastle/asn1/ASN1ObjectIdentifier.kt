/*
 * Copyright (c) 2000-2021 The Legion of the Bouncy Castle Inc. (https://www.bouncycastle.org)
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Modified from original source: https://github.com/bcgit/bc-java
 */

package org.spongycastle.asn1

import co.touchlab.stately.collections.ConcurrentMutableMap
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.MultiplatformByteArrayOutputStream
import com.mangala.wallet.bitcoinj.utils.shiftLeft
import com.mangala.wallet.bitcoinj.utils.shiftRight
import com.mangala.wallet.bitcoinj.utils.toInt
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.clone
import org.spongycastle.util.Arrays.hashCode
import okio.IOException
import kotlin.jvm.JvmStatic
import com.mangala.wallet.bitcoinj.utils.Synchronized

/**
 * Class representing the ASN.1 OBJECT IDENTIFIER type.
 */
class ASN1ObjectIdentifier : ASN1Primitive {
    /**
     * Return the OID as a string.
     *
     * @return the string representation of the OID carried by this object.
     */
    val id: String
    private var body: ByteArray? = null

    internal constructor(
        bytes: ByteArray
    ) {
        val objId = StringBuilder()
        var value: Long = 0
        var bigValue: BigInteger? = null
        var first = true
        for (i in bytes.indices) {
            val b = bytes[i].toInt() and 0xff
            if (value <= LONG_LIMIT) {
                value += (b and 0x7f).toLong()
                if (b and 0x80 == 0) // end of number reached
                {
                    if (first) {
                        if (value < 40) {
                            objId.append('0')
                        } else if (value < 80) {
                            objId.append('1')
                            value -= 40
                        } else {
                            objId.append('2')
                            value -= 80
                        }
                        first = false
                    }
                    objId.append('.')
                    objId.append(value)
                    value = 0
                } else {
                    value = value shl 7
                }
            } else {
                if (bigValue == null) {
                    bigValue = BigInteger.fromLong(value)
                }
                bigValue = bigValue!!.or(BigInteger.fromLong((b and 0x7f).toLong()))
                if (b and 0x80 == 0) {
                    if (first) {
                        objId.append('2')
                        bigValue = bigValue.subtract(BigInteger.fromLong(80))
                        first = false
                    }
                    objId.append('.')
                    objId.append(bigValue)
                    bigValue = null
                    value = 0
                } else {
                    bigValue = bigValue.shiftLeft(7)
                }
            }
        }
        id = objId.toString()
        body = clone(bytes)
    }

    /**
     * Create an OID based on the passed in String.
     *
     * @param identifier a string representation of an OID.
     */
    constructor(
        identifier: String?
    ) {
        requireNotNull(identifier) { "'identifier' cannot be null" }
        require(isValidIdentifier(identifier)) { "string $identifier not an OID" }
        id = identifier
    }

    /**
     * Create an OID that creates a branch under the current one.
     *
     * @param branchID node numbers for the new branch.
     * @return the OID for the new created branch.
     */
    internal constructor(oid: ASN1ObjectIdentifier, branchID: String) {
        require(isValidBranchID(branchID, 0)) { "string $branchID not a valid OID branch" }
        id = oid.id + "." + branchID
    }

    /**
     * Return an OID that creates a branch under the current one.
     *
     * @param branchID node numbers for the new branch.
     * @return the OID for the new created branch.
     */
    fun branch(branchID: String): ASN1ObjectIdentifier {
        return ASN1ObjectIdentifier(this, branchID)
    }

    /**
     * Return true if this oid is an extension of the passed in branch - stem.
     *
     * @param stem the arc or branch that is a possible parent.
     * @return true if the branch is on the passed in stem, false otherwise.
     */
    fun on(stem: ASN1ObjectIdentifier): Boolean {
        val id = id
        val stemId = stem.id
        return id.length > stemId.length && id[stemId.length] == '.' && id.startsWith(stemId)
    }

    private fun writeField(
        out: MultiplatformByteArrayOutputStream,
        fieldValue: Long
    ) {
        var fieldValue = fieldValue
        val result = ByteArray(9)
        var pos = 8
        result[pos] = (fieldValue.toInt() and 0x7f).toByte()
        while (fieldValue >= 1L shl 7) {
            fieldValue = fieldValue shr 7
            result[--pos] = (fieldValue.toInt() and 0x7f or 0x80).toByte()
        }
        out.write(result, pos, 9 - pos)
    }

    private fun writeField(
        out: MultiplatformByteArrayOutputStream,
        fieldValue: BigInteger
    ) {
        val byteCount = (fieldValue.bitLength() + 6) / 7
        if (byteCount == 0) {
            out.write(0)
        } else {
            var tmpValue = fieldValue
            val tmp = ByteArray(byteCount)
            for (i in byteCount - 1 downTo 0) {
                tmp[i] = (tmpValue.toInt() and 0x7f or 0x80).toByte()
                tmpValue = tmpValue.shiftRight(7)
            }
            tmp[byteCount - 1] = (tmp[byteCount - 1].toInt() and 0x7f).toByte()
            out.write(tmp, 0, tmp.size)
        }
    }

    private fun doOutput(aOut: MultiplatformByteArrayOutputStream) {
        val tok = OIDTokenizer(id)
        val first = tok.nextToken().toInt() * 40
        val secondToken = tok.nextToken()
        if (secondToken.length <= 18) {
            writeField(aOut, first + secondToken.toLong())
        } else {
            writeField(aOut, BigInteger.parseString(secondToken).add(BigInteger.fromLong(first.toLong())))
        }
        while (tok.hasMoreTokens()) {
            val token = tok.nextToken()
            if (token.length <= 18) {
                writeField(aOut, token.toLong())
            } else {
                writeField(aOut, BigInteger.parseString(token))
            }
        }
    }

    @Synchronized
    private fun getBody(): ByteArray {
        if (body == null) {
            val bOut = MultiplatformByteArrayOutputStream()
            doOutput(bOut)
            body = bOut.toByteArray()
        }
        return body!!
    }

    public override fun isConstructed(): Boolean {
        return false
    }

    @Throws(IOException::class)
    public override fun encodedLength(): Int {
        val length = getBody()!!.size
        return 1 + StreamUtil.calculateBodyLength(length) + length
    }

    @Throws(IOException::class)
    public override fun encode(
        out: ASN1OutputStream
    ) {
        val enc = getBody()
        out.write(BERTags.OBJECT_IDENTIFIER)
        out.writeLength(enc!!.size)
        out.write(enc)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    public override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        if (o === this) {
            return true
        }
        return if (o !is ASN1ObjectIdentifier) {
            false
        } else id == o.id
    }

    override fun toString(): String {
        return id
    }

    /**
     * Intern will return a reference to a pooled version of this object, unless it
     * is not present in which case intern will add it.
     *
     *
     * The pool is also used by the ASN.1 parsers to limit the number of duplicated OID
     * objects in circulation.
     *
     *
     * @return a reference to the identifier in the pool.
     */
    fun intern(): ASN1ObjectIdentifier {
        val hdl = OidHandle(getBody())
        var oid = pool[hdl]
        if (oid == null) {
            oid = pool.getOrPut(hdl) { this }
            if (oid == null) {
                oid = this
            }
        }
        return oid
    }

    private class OidHandle internal constructor(private val enc: ByteArray) {
        private val key: Int

        init {
            key = hashCode(enc)
        }

        override fun hashCode(): Int {
            return key
        }

        override fun equals(o: Any?): Boolean {
            return if (o is OidHandle) {
                areEqual(enc, o.enc)
            } else false
        }
    }

    companion object {
        /**
         * Return an OID from the passed in object
         *
         * @param obj an ASN1ObjectIdentifier or an object that can be converted into one.
         * @return an ASN1ObjectIdentifier instance, or null.
         * @throws IllegalArgumentException if the object cannot be converted.
         */
        @JvmStatic
        fun getInstance(
            obj: Any?
        ): ASN1ObjectIdentifier? {
            if (obj == null || obj is ASN1ObjectIdentifier) {
                return obj as ASN1ObjectIdentifier?
            }
            if (obj is ASN1Encodable && obj.toASN1Primitive() is ASN1ObjectIdentifier) {
                return obj.toASN1Primitive() as ASN1ObjectIdentifier
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray(obj) as ASN1ObjectIdentifier
                } catch (e: IOException) {
                    throw IllegalArgumentException("failed to construct object identifier from byte[]: " + e.message)
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * Return an OBJECT IDENTIFIER from a tagged object.
         *
         * @param obj      the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @return an ASN1ObjectIdentifier instance, or null.
         * @throws IllegalArgumentException if the tagged object cannot
         * be converted.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1ObjectIdentifier? {
            val o = obj.getObject()
            return if (explicit || o is ASN1ObjectIdentifier) {
                getInstance(o)
            } else {
                fromOctetString(ASN1OctetString.getInstance(obj.getObject()).octets)
            }
        }

        private const val LONG_LIMIT = (Long.MAX_VALUE shr 7) - 0x7f
        private fun isValidBranchID(
            branchID: String, start: Int
        ): Boolean {
            var periodAllowed = false
            var pos = branchID.length
            while (--pos >= start) {
                val ch = branchID[pos]

                // TODO Leading zeroes?
                if ('0' <= ch && ch <= '9') {
                    periodAllowed = true
                    continue
                }
                if (ch == '.') {
                    if (!periodAllowed) {
                        return false
                    }
                    periodAllowed = false
                    continue
                }
                return false
            }
            return periodAllowed
        }

        private fun isValidIdentifier(
            identifier: String
        ): Boolean {
            if (identifier.length < 3 || identifier[1] != '.') {
                return false
            }
            val first = identifier[0]
            return if (first < '0' || first > '2') {
                false
            } else isValidBranchID(identifier, 2)
        }

        private val pool: ConcurrentMutableMap<OidHandle, ASN1ObjectIdentifier> = ConcurrentMutableMap()
        @JvmStatic
        fun fromOctetString(enc: ByteArray): ASN1ObjectIdentifier {
            val hdl = OidHandle(enc)
            return pool[hdl] ?: return ASN1ObjectIdentifier(enc)
        }
    }
}

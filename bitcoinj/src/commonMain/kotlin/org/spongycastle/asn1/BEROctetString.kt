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

import co.touchlab.stately.collections.ConcurrentMutableList
import com.mangala.MultiplatformByteArrayOutputStream
import okio.IOException

/**
 * ASN.1 OctetStrings, with indefinite length rules, and *constructed form* support.
 *
 *
 * The Basic Encoding Rules (BER) format allows encoding using so called "*constructed form*",
 * which DER and CER formats forbid allowing only "primitive form".
 *
 *
 * This class **always** produces the constructed form with underlying segments
 * in an indefinite length array.  If the input wasn't the same, then this output
 * is not faithful reproduction.
 *
 *
 *
 * See [ASN1OctetString] for X.690 encoding rules of OCTET-STRING objects.
 *
 */
class BEROctetString : ASN1OctetString {
    private var octs: Array<ASN1OctetString>? = null

    /**
     * Create an OCTET-STRING object from a byte[]
     * @param string the octets making up the octet string.
     */
    constructor(
        string: ByteArray
    ) : super(string)

    /**
     * Multiple [ASN1OctetString] data blocks are input,
     * the result is *constructed form*.
     *
     * @param octs an array of OCTET STRING to construct the BER OCTET STRING from.
     */
    constructor(
        octs: Array<ASN1OctetString>
    ) : super(toBytes(octs)) {
        this.octs = octs
    }

    override val octets: ByteArray
        /**
         * Return a concatenated byte array of all the octets making up the constructed OCTET STRING
         * @return the full OCTET STRING.
         */
        get() = string
    val objects: Iterator<ASN1Encodable>
        /**
         * Return the OCTET STRINGs that make up this string.
         *
         * @return an Iterator of the component OCTET STRINGs.
         */
        get() = if (octs == null) {
            generateOcts().iterator()
        } else object : Iterator<ASN1Encodable> {
            var counter = 0

            override fun hasNext(): Boolean {
                return counter < octs!!.size
            }

            override fun next(): ASN1Encodable {
                return octs!![counter++]
            }
        }

    private fun generateOcts(): ConcurrentMutableList<ASN1Encodable> {
        val vec: ConcurrentMutableList<ASN1Encodable> = ConcurrentMutableList<ASN1Encodable>()
        var i = 0
        while (i < string.size) {
            var end: Int
            end = if (i + MAX_LENGTH > string.size) {
                string.size
            } else {
                i + MAX_LENGTH
            }
            val nStr = ByteArray(end - i)
            string.copyInto(destination = nStr, destinationOffset = 0, startIndex = i, endIndex = i + nStr.size)
            vec.add(DEROctetString(nStr))
            i += MAX_LENGTH
        }
        return vec
    }

    override fun isConstructed(): Boolean {
        return true
    }

    @Throws(IOException::class)
    override fun encodedLength(): Int {
        var length = 0
        val e = objects
        while (e.hasNext()) {
            length += (e.next() as ASN1Encodable).toASN1Primitive().encodedLength()
        }
        return 2 + length + 2
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        out.write(BERTags.CONSTRUCTED or BERTags.OCTET_STRING)
        out.write(0x80)

        //
        // write out the octet array
        //
        val e = objects
        while (e.hasNext()) {
            out.writeObject(e.next() as ASN1Encodable)
        }
        out.write(0x00)
        out.write(0x00)
    }

    companion object {
        private const val MAX_LENGTH = 1000

        /**
         * Convert a vector of octet strings into a single byte string
         */
        private fun toBytes(
            octs: Array<ASN1OctetString>
        ): ByteArray {
            val bOut = MultiplatformByteArrayOutputStream()
            for (i in octs.indices) {
                try {
                    val o = octs[i] as DEROctetString
                    bOut.write(o.octets)
                } catch (e: ClassCastException) {
                    throw IllegalArgumentException(octs[i]::class.simpleName + " found in input should only contain DEROctetString")
                } catch (e: IOException) {
                    throw IllegalArgumentException("exception converting octets $e")
                }
            }
            return bOut.toByteArray()
        }

        fun fromSequence(seq: ASN1Sequence): BEROctetString {
            val vA = mutableListOf<ASN1OctetString>()
//            val v = vA.toTypedArray()
            val e = seq.objects
            var index = 0
            while (e.hasNext()) {
                vA[index++] = e.next() as ASN1OctetString
            }
            return BEROctetString(vA.toTypedArray())
        }
    }
}

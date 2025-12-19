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

package org.spongycastle.asn1.x9

import org.spongycastle.asn1.ASN1Object
import org.spongycastle.asn1.ASN1OctetString
import org.spongycastle.asn1.ASN1Primitive
import org.spongycastle.asn1.DEROctetString
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.util.Arrays.clone
import kotlin.jvm.JvmOverloads
import com.mangala.wallet.bitcoinj.utils.Synchronized

/**
 * class for describing an ECPoint as a DER object.
 */
class X9ECPoint : ASN1Object {
    private val encoding: ASN1OctetString
    private var c: ECCurve? = null
    private var p: ECPoint? = null

    @JvmOverloads
    constructor(
        p: ECPoint,
        compressed: Boolean = false
    ) {
        this.p = p.normalize()
        encoding = DEROctetString(p.getEncoded(compressed))
    }

    constructor(
        c: ECCurve,
        encoding: ByteArray
    ) {
        this.c = c
        this.encoding = DEROctetString(clone(encoding))
    }

    constructor(
        c: ECCurve,
        s: ASN1OctetString
    ) : this(c, s.octets)

    val pointEncoding: ByteArray
        get() = clone(encoding.octets)

    @get:Synchronized
    val point: ECPoint
        get() {
            if (p == null) {
                p = c!!.decodePoint(encoding.octets).normalize()
            }
            return p!!
        }
    val isPointCompressed: Boolean
        get() {
            val octets = encoding.octets
            return octets != null && octets.size > 0 && (octets[0].toInt() == 2 || octets[0].toInt() == 3)
        }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * ECPoint ::= OCTET STRING
    </pre> *
     *
     *
     * Octet string produced using ECPoint.getEncoded().
     */
    override fun toASN1Primitive(): ASN1Primitive {
        return encoding
    }
}

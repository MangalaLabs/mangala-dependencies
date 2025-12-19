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

package org.spongycastle.crypto.util

import org.spongycastle.crypto.Digest
//import org.spongycastle.crypto.digests.MD5Digest
import org.spongycastle.crypto.digests.SHA1Digest

//import org.spongycastle.crypto.digests.SHA224Digest
//import org.spongycastle.crypto.digests.SHA256Digest
//import org.spongycastle.crypto.digests.SHA384Digest
//import org.spongycastle.crypto.digests.SHA3Digest
//import org.spongycastle.crypto.digests.SHA512Digest
//import org.spongycastle.crypto.digests.SHA512tDigest

/**
 * Basic factory class for message digests.
 */
object DigestFactory {
//    fun createMD5(): Digest {
//        return MD5Digest()
//    }

    fun createSHA1(): Digest {
        return SHA1Digest()
    }

//    fun createSHA224(): Digest {
//        return SHA224Digest()
//    }
//
//    fun createSHA256(): Digest {
//        return SHA256Digest()
//    }
//
//    fun createSHA384(): Digest {
//        return SHA384Digest()
//    }
//
//    fun createSHA512(): Digest {
//        return SHA512Digest()
//    }
//
//    fun createSHA512_224(): Digest {
//        return SHA512tDigest(224)
//    }
//
//    fun createSHA512_256(): Digest {
//        return SHA512tDigest(256)
//    }
//
//    fun createSHA3_224(): Digest {
//        return SHA3Digest(224)
//    }
//
//    fun createSHA3_256(): Digest {
//        return SHA3Digest(256)
//    }
//
//    fun createSHA3_384(): Digest {
//        return SHA3Digest(384)
//    }
//
//    fun createSHA3_512(): Digest {
//        return SHA3Digest(512)
//    }
}

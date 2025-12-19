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

//package org.spongycastle.asn1.gm
//
//import org.spongycastle.asn1.ASN1ObjectIdentifier
//
//interface GMObjectIdentifiers {
//    companion object {
//        val sm_scheme = ASN1ObjectIdentifier("1.2.156.10197.1")
//        val sm6_ecb = sm_scheme.branch("101.1")
//        val sm6_cbc = sm_scheme.branch("101.2")
//        val sm6_ofb128 = sm_scheme.branch("101.3")
//        val sm6_cfb128 = sm_scheme.branch("101.4")
//        val sm1_ecb = sm_scheme.branch("102.1")
//        val sm1_cbc = sm_scheme.branch("102.2")
//        val sm1_ofb128 = sm_scheme.branch("102.3")
//        val sm1_cfb128 = sm_scheme.branch("102.4")
//        val sm1_cfb1 = sm_scheme.branch("102.5")
//        val sm1_cfb8 = sm_scheme.branch("102.6")
//        val ssf33_ecb = sm_scheme.branch("103.1")
//        val ssf33_cbc = sm_scheme.branch("103.2")
//        val ssf33_ofb128 = sm_scheme.branch("103.3")
//        val ssf33_cfb128 = sm_scheme.branch("103.4")
//        val ssf33_cfb1 = sm_scheme.branch("103.5")
//        val ssf33_cfb8 = sm_scheme.branch("103.6")
//        val sms4_ecb = sm_scheme.branch("104.1")
//        val sms4_cbc = sm_scheme.branch("104.2")
//        val sms4_ofb128 = sm_scheme.branch("104.3")
//        val sms4_cfb128 = sm_scheme.branch("104.4")
//        val sms4_cfb1 = sm_scheme.branch("104.5")
//        val sms4_cfb8 = sm_scheme.branch("104.6")
//        val sms4_ctr = sm_scheme.branch("104.7")
//        val sms4_gcm = sm_scheme.branch("104.8")
//        val sms4_ccm = sm_scheme.branch("104.9")
//        val sms4_xts = sm_scheme.branch("104.10")
//        val sms4_wrap = sm_scheme.branch("104.11")
//        val sms4_wrap_pad = sm_scheme.branch("104.12")
//        val sms4_ocb = sm_scheme.branch("104.100")
//        val sm5 = sm_scheme.branch("201")
//        val sm2p256v1 = sm_scheme.branch("301")
//        val sm2sign = sm_scheme.branch("301.1")
//        val sm2exchange = sm_scheme.branch("301.2")
//        val sm2encrypt = sm_scheme.branch("301.3")
//        val wapip192v1 = sm_scheme.branch("301.101")
//        val sm2encrypt_recommendedParameters = sm2encrypt.branch("1")
//        val sm2encrypt_specifiedParameters = sm2encrypt.branch("2")
//        val sm2encrypt_with_sm3 = sm2encrypt.branch("2.1")
//        val sm2encrypt_with_sha1 = sm2encrypt.branch("2.2")
//        val sm2encrypt_with_sha224 = sm2encrypt.branch("2.3")
//        val sm2encrypt_with_sha256 = sm2encrypt.branch("2.4")
//        val sm2encrypt_with_sha384 = sm2encrypt.branch("2.5")
//        val sm2encrypt_with_sha512 = sm2encrypt.branch("2.6")
//        val sm2encrypt_with_rmd160 = sm2encrypt.branch("2.7")
//        val sm2encrypt_with_whirlpool = sm2encrypt.branch("2.8")
//        val sm2encrypt_with_blake2b512 = sm2encrypt.branch("2.9")
//        val sm2encrypt_with_blake2s256 = sm2encrypt.branch("2.10")
//        val sm2encrypt_with_md5 = sm2encrypt.branch("2.11")
//        val id_sm9PublicKey = sm_scheme.branch("302")
//        val sm9sign = sm_scheme.branch("302.1")
//        val sm9keyagreement = sm_scheme.branch("302.2")
//        val sm9encrypt = sm_scheme.branch("302.3")
//        val sm3 = sm_scheme.branch("401")
//        val hmac_sm3 = sm3.branch("2")
//        val sm2sign_with_sm3 = sm_scheme.branch("501")
//        val sm2sign_with_sha1 = sm_scheme.branch("502")
//        val sm2sign_with_sha256 = sm_scheme.branch("503")
//        val sm2sign_with_sha512 = sm_scheme.branch("504")
//        val sm2sign_with_sha224 = sm_scheme.branch("505")
//        val sm2sign_with_sha384 = sm_scheme.branch("506")
//        val sm2sign_with_rmd160 = sm_scheme.branch("507")
//        val sm2sign_with_whirlpool = sm_scheme.branch("520")
//        val sm2sign_with_blake2b512 = sm_scheme.branch("521")
//        val sm2sign_with_blake2s256 = sm_scheme.branch("522")
//    }
//}

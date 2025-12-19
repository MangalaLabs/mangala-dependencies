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

package org.spongycastle.crypto.ec

import co.touchlab.stately.collections.ConcurrentMutableList
import co.touchlab.stately.collections.ConcurrentMutableMap
import com.ionspin.kotlin.bignum.integer.BigInteger
import org.spongycastle.asn1.ASN1ObjectIdentifier

import org.spongycastle.asn1.sec.SECObjectIdentifiers
import org.spongycastle.asn1.x9.X9ECParameters
import org.spongycastle.asn1.x9.X9ECParametersHolder
import org.spongycastle.asn1.x9.X9ECPoint
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.custom.sec.SecP256K1Curve
import org.spongycastle.math.ec.custom.sec.SecP256R1Curve
import org.spongycastle.math.ec.endo.GLVTypeBEndomorphism
import org.spongycastle.math.ec.endo.GLVTypeBParameters
import org.spongycastle.util.Strings
import org.spongycastle.util.encoders.Hex

object CustomNamedCurves {
    private fun configureCurve(curve: ECCurve): ECCurve {
        return curve
    }

    private fun configureCurveGLV(c: ECCurve, p: GLVTypeBParameters): ECCurve {
        return c.configure().setEndomorphism(GLVTypeBEndomorphism(c, p)).create()
    }

    /*
     * curve25519
     */
//    var curve25519: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(Curve25519())
//
//            /*
//             * NOTE: Curve25519 was specified in Montgomery form. Rewriting in Weierstrass form
//             * involves substitution of variables, so the base-point x coordinate is 9 + (486662 / 3).
//             *
//             * The Curve25519 paper doesn't say which of the two possible y values the base
//             * point has. The choice here is guided by language in the Ed25519 paper.
//             *
//             * (The other possible y value is 5F51E65E475F794B1FE122D388B72EB36DC2B28192839E4DD6163A5D81312C14)
//             */
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD245A"
//                            + "20AE19A1B8A086B4E01EDD2C7748D14C923D4D7E6D7C61B229E9C5A27ECED3D9"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp128r1
//     */
//    var secp128r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("000E0D4D696E6768756151750CC03A4473D03679")
//            val curve = configureCurve(SecP128R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "161FF7528B899B2D0C28607CA52C5B86"
//                            + "CF5AC8395BAFEB13C02DA292DDED7A83"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp160k1
//     */
//    var secp160k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val glv = GLVTypeBParameters(
//                BigInteger("9ba48cba5ebcb9b6bd33b92830b2a2e0e192f10a", 16),
//                BigInteger("c39c6c3b3a36d7701b9c71a1f5804ae5d0003f4", 16), arrayOf(
//                    BigInteger("9162fbe73984472a0a9e", 16),
//                    BigInteger("-96341f1138933bc2f505", 16)
//                ), arrayOf(
//                    BigInteger("127971af8721782ecffa3", 16),
//                    BigInteger("9162fbe73984472a0a9e", 16)
//                ),
//                BigInteger("9162fbe73984472a0a9d0590", 16),
//                BigInteger("96341f1138933bc2f503fd44", 16),
//                176
//            )
//            val curve = configureCurveGLV(SecP160K1Curve(), glv)
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "3B4C382CE37AA192A4019E763036F4F5DD4D7EBB"
//                            + "938CF935318FDCED6BC28286531733C3F03C4FEE"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp160r1
//     */
//    var secp160r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("1053CDE42C14D696E67687561517533BF3F83345")
//            val curve = configureCurve(SecP160R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "4A96B5688EF573284664698968C38BB913CBFC82"
//                            + "23A628553168947D59DCC912042351377AC5FB32"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp160r2
//     */
//    var secp160r2: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("B99B99B099B323E02709A4D696E6768756151751")
//            val curve = configureCurve(SecP160R2Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "52DCB034293A117E1F4FF11B30F7199D3144CE6D"
//                            + "FEAFFEF2E331F296E071FA0DF9982CFEA7D43F2E"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp192k1
//     */
//    var secp192k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val glv = GLVTypeBParameters(
//                BigInteger("bb85691939b869c1d087f601554b96b80cb4f55b35f433c2", 16),
//                BigInteger("3d84f26c12238d7b4f3d516613c1759033b1a5800175d0b1", 16), arrayOf(
//                    BigInteger("71169be7330b3038edb025f1", 16),
//                    BigInteger("-b3fb3400dec5c4adceb8655c", 16)
//                ), arrayOf(
//                    BigInteger("12511cfe811d0f4e6bc688b4d", 16),
//                    BigInteger("71169be7330b3038edb025f1", 16)
//                ),
//                BigInteger("71169be7330b3038edb025f1d0f9", 16),
//                BigInteger("b3fb3400dec5c4adceb8655d4c94", 16),
//                208
//            )
//            val curve = configureCurveGLV(SecP192K1Curve(), glv)
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "DB4FF10EC057E9AE26B07D0280B7F4341DA5D1B1EAE06C7D"
//                            + "9B2F2F6D9C5628A7844163D015BE86344082AA88D95E2F9D"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp192r1
//     */
//    var secp192r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("3045AE6FC8422F64ED579528D38120EAE12196D5")
//            val curve = configureCurve(SecP192R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012"
//                            + "07192B95FFC8DA78631011ED6B24CDD573F977A11E794811"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp224k1
//     */
//    var secp224k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val glv = GLVTypeBParameters(
//                BigInteger("fe0e87005b4e83761908c5131d552a850b3f58b749c37cf5b84d6768", 16),
//                BigInteger("60dcd2104c4cbc0be6eeefc2bdd610739ec34e317f9b33046c9e4788", 16), arrayOf(
//                    BigInteger("6b8cf07d4ca75c88957d9d670591", 16),
//                    BigInteger("-b8adf1378a6eb73409fa6c9c637d", 16)
//                ), arrayOf(
//                    BigInteger("1243ae1b4d71613bc9f780a03690e", 16),
//                    BigInteger("6b8cf07d4ca75c88957d9d670591", 16)
//                ),
//                BigInteger("6b8cf07d4ca75c88957d9d67059037a4", 16),
//                BigInteger("b8adf1378a6eb73409fa6c9c637ba7f5", 16),
//                240
//            )
//            val curve = configureCurveGLV(SecP224K1Curve(), glv)
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "A1455B334DF099DF30FC28A169A467E9E47075A90F7E650EB6B7A45C"
//                            + "7E089FED7FBA344282CAFBD6F7E319F7C0B0BD59E2CA4BDB556D61A5"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp224r1
//     */
//    var secp224r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("BD71344799D5C7FCDC45B59FA3B9AB8F6A948BC5")
//            val curve = configureCurve(SecP224R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21"
//                            + "BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }

    /*
     * secp256k1
     */
    var secp256k1: X9ECParametersHolder = object : X9ECParametersHolder() {
        override fun createParameters(): X9ECParameters {
            val S: ByteArray? = null
            val glv = GLVTypeBParameters(
                BigInteger.parseString("7ae96a2b657c07106e64479eac3434e99cf0497512f58995c1396c28719501ee", 16),
                BigInteger.parseString("5363ad4cc05c30e0a5261c028812645a122e22ea20816678df02967c1b23bd72", 16),
                arrayOf(
                    BigInteger.parseString("3086d221a7d46bcde86c90e49284eb15", 16),
                    BigInteger.parseString("-e4437ed6010e88286f547fa90abfe4c3", 16)
                ),
                arrayOf(
                    BigInteger.parseString("114ca50f7a8e2f3f657c1108d9d44cfd8", 16),
                    BigInteger.parseString("3086d221a7d46bcde86c90e49284eb15", 16)
                ),
                BigInteger.parseString("3086d221a7d46bcde86c90e49284eb153dab", 16),
                BigInteger.parseString("e4437ed6010e88286f547fa90abfe4c42212", 16),
                272
            )
            val curve = configureCurveGLV(SecP256K1Curve(), glv)
            val G = X9ECPoint(
                curve, Hex.decode(
                    "04"
                            + "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798"
                            + "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8"
                )
            )
            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
        }
    }

    /*
     * secp256r1
     */
    var secp256r1: X9ECParametersHolder = object : X9ECParametersHolder() {
        override fun createParameters(): X9ECParameters {
            val S = Hex.decode("C49D360886E704936A6678E1139D26B7819F7E90")
            val curve = configureCurve(SecP256R1Curve())
            val G = X9ECPoint(
                curve, Hex.decode(
                    "04"
                            + "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296"
                            + "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5"
                )
            )
            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
        }
    }

    /*
     * secp384r1
     */
//    var secp384r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("A335926AA319A27A1D00896A6773A4827ACDAC73")
//            val curve = configureCurve(SecP384R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7"
//                            + "3617DE4A96262C6F5D9E98BF9292DC29F8F41DBD289A147CE9DA3113B5F0B8C00A60B1CE1D7E819D7A431D7C90EA0E5F"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * secp521r1
//     */
//    var secp521r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("D09E8800291CB85396CC6717393284AAA0DA64BA")
//            val curve = configureCurve(SecP521R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "00C6858E06B70404E9CD9E3ECB662395B4429C648139053FB521F828AF606B4D3DBAA14B5E77EFE75928FE1DC127A2FFA8DE3348B3C1856A429BF97E7E31C2E5BD66"
//                            + "011839296A789A3BC0045C8A5FB42C7D1BD998F54449579B446817AFBD17273E662C97EE72995EF42640C550B9013FAD0761353C7086A272C24088BE94769FD16650"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect113r1
//     */
//    var sect113r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("10E723AB14D696E6768756151756FEBF8FCB49A9")
//            val curve = configureCurve(SecT113R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "009D73616F35F4AB1407D73562C10F"
//                            + "00A52830277958EE84D1315ED31886"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect113r2
//     */
//    var sect113r2: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("10C0FB15760860DEF1EEF4D696E676875615175D")
//            val curve = configureCurve(SecT113R2Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "01A57A6A7B26CA5EF52FCDB8164797"
//                            + "00B3ADC94ED1FE674C06E695BABA1D"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect131r1
//     */
//    var sect131r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("4D696E676875615175985BD3ADBADA21B43A97E2")
//            val curve = configureCurve(SecT131R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "0081BAF91FDF9833C40F9C181343638399"
//                            + "078C6E7EA38C001F73C8134B1B4EF9E150"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect131r2
//     */
//    var sect131r2: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("985BD3ADBAD4D696E676875615175A21B43A97E3")
//            val curve = configureCurve(SecT131R2Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "0356DCD8F2F95031AD652D23951BB366A8"
//                            + "0648F06D867940A5366D9E265DE9EB240F"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect163k1
//     */
//    var sect163k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(SecT163K1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "02FE13C0537BBC11ACAA07D793DE4E6D5E5C94EEE8"
//                            + "0289070FB05D38FF58321F2E800536D538CCDAA3D9"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect163r1
//     */
//    var sect163r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("24B7B137C8A14D696E6768756151756FD0DA2E5C")
//            val curve = configureCurve(SecT163R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "0369979697AB43897789566789567F787A7876A654"
//                            + "00435EDB42EFAFB2989D51FEFCE3C80988F41FF883"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect163r2
//     */
//    var sect163r2: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("85E25BFE5C86226CDB12016F7553F9D0E693A268")
//            val curve = configureCurve(SecT163R2Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "03F0EBA16286A2D57EA0991168D4994637E8343E36"
//                            + "00D51FBC6C71A0094FA2CDD545B11C5C0C797324F1"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect193r1
//     */
//    var sect193r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("103FAEC74D696E676875615175777FC5B191EF30")
//            val curve = configureCurve(SecT193R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "01F481BC5F0FF84A74AD6CDF6FDEF4BF6179625372D8C0C5E1"
//                            + "0025E399F2903712CCF3EA9E3A1AD17FB0B3201B6AF7CE1B05"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect193r2
//     */
//    var sect193r2: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("10B7B4D696E676875615175137C8A16FD0DA2211")
//            val curve = configureCurve(SecT193R2Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "00D9B67D192E0367C803F39E1A7E82CA14A651350AAE617E8F"
//                            + "01CE94335607C304AC29E7DEFBD9CA01F596F927224CDECF6C"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect233k1
//     */
//    var sect233k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(SecT233K1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "017232BA853A7E731AF129F22FF4149563A419C26BF50A4C9D6EEFAD6126"
//                            + "01DB537DECE819B7F70F555A67C427A8CD9BF18AEB9B56E0C11056FAE6A3"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect233r1
//     */
//    var sect233r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("74D59FF07F6B413D0EA14B344B20A2DB049B50C3")
//            val curve = configureCurve(SecT233R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "00FAC9DFCBAC8313BB2139F1BB755FEF65BC391F8B36F8F8EB7371FD558B"
//                            + "01006A08A41903350678E58528BEBF8A0BEFF867A7CA36716F7E01F81052"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect239k1
//     */
//    var sect239k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(SecT239K1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "29A0B6A887A983E9730988A68727A8B2D126C44CC2CC7B2A6555193035DC"
//                            + "76310804F12E549BDB011C103089E73510ACB275FC312A5DC6B76553F0CA"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect283k1
//     */
//    var sect283k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(SecT283K1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "0503213F78CA44883F1A3B8162F188E553CD265F23C1567A16876913B0C2AC2458492836"
//                            + "01CCDA380F1C9E318D90F95D07E5426FE87E45C0E8184698E45962364E34116177DD2259"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect283r1
//     */
//    var sect283r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("77E2B07370EB0F832A6DD5B62DFC88CD06BB84BE")
//            val curve = configureCurve(SecT283R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "05F939258DB7DD90E1934F8C70B0DFEC2EED25B8557EAC9C80E2E198F8CDBECD86B12053"
//                            + "03676854FE24141CB98FE6D4B20D02B4516FF702350EDDB0826779C813F0DF45BE8112F4"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect409k1
//     */
//    var sect409k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(SecT409K1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "0060F05F658F49C1AD3AB1890F7184210EFD0987E307C84C27ACCFB8F9F67CC2C460189EB5AAAA62EE222EB1B35540CFE9023746"
//                            + "01E369050B7C4E42ACBA1DACBF04299C3460782F918EA427E6325165E9EA10E3DA5F6C42E9C55215AA9CA27A5863EC48D8E0286B"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect409r1
//     */
//    var sect409r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("4099B5A457F9D69F79213D094C4BCD4D4262210B")
//            val curve = configureCurve(SecT409R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "015D4860D088DDB3496B0C6064756260441CDE4AF1771D4DB01FFE5B34E59703DC255A868A1180515603AEAB60794E54BB7996A7"
//                            + "0061B1CFAB6BE5F32BBFA78324ED106A7636B9C5A7BD198D0158AA4F5488D08F38514F1FDF4B4F40D2181B3681C364BA0273C706"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect571k1
//     */
//    var sect571k1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(SecT571K1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "026EB7A859923FBC82189631F8103FE4AC9CA2970012D5D46024804801841CA44370958493B205E647DA304DB4CEB08CBBD1BA39494776FB988B47174DCA88C7E2945283A01C8972"
//                            + "0349DC807F4FBF374F4AEADE3BCA95314DD58CEC9F307A54FFC61EFC006D8A2C9D4979C0AC44AEA74FBEBBB9F772AEDCB620B01A7BA7AF1B320430C8591984F601CD4C143EF1C7A3"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sect571r1
//     */
//    var sect571r1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S = Hex.decode("2AA058F73A0E33AB486B0F610410C53A7F132310")
//            val curve = configureCurve(SecT571R1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "0303001D34B856296C16C0D40D3CD7750A93D1D2955FA80AA5F40FC8DB7B2ABDBDE53950F4C0D293CDD711A35B67FB1499AE60038614F1394ABFA3B4C850D927E1E7769C8EEC2D19"
//                            + "037BF27342DA639B6DCCFFFEB73D69D78C6C27A6009CBBCA1980F8533921E8A684423E43BAB08A576291AF8F461BB2A8B3531D2F0485C19B16E2F1516E23DD3C1A4827AF1B8AC15B"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
//
//    /*
//     * sm2p256v1
//     */
//    var sm2p256v1: X9ECParametersHolder = object : X9ECParametersHolder() {
//        override fun createParameters(): X9ECParameters {
//            val S: ByteArray? = null
//            val curve = configureCurve(SM2P256V1Curve())
//            val G = X9ECPoint(
//                curve, Hex.decode(
//                    "04"
//                            + "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7"
//                            + "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"
//                )
//            )
//            return X9ECParameters(curve, G, curve.getCOrder(), curve.getCCofactor(), S)
//        }
//    }
    val nameToCurve: ConcurrentMutableMap<String, X9ECParametersHolder> = ConcurrentMutableMap<String, X9ECParametersHolder>()
    val nameToOID: ConcurrentMutableMap<String, ASN1ObjectIdentifier> = ConcurrentMutableMap<String, ASN1ObjectIdentifier>()
    val oidToCurve: ConcurrentMutableMap<ASN1ObjectIdentifier, X9ECParametersHolder> = ConcurrentMutableMap<ASN1ObjectIdentifier, X9ECParametersHolder>()
    val oidToName: ConcurrentMutableMap<ASN1ObjectIdentifier, String> = ConcurrentMutableMap<ASN1ObjectIdentifier, String>()
    val names: ConcurrentMutableList<String> = ConcurrentMutableList<String>()
    fun defineCurve(name: String, holder: X9ECParametersHolder) {
        var name = name
        names.add(name)
        name = Strings.toLowerCase(name)
        nameToCurve[name] = holder
    }

    fun defineCurveWithOID(
        name: String,
        oid: ASN1ObjectIdentifier,
        holder: X9ECParametersHolder
    ) {
        var name = name
        names.add(name)
        oidToName[oid] = name
        oidToCurve[oid] = holder
        name = Strings.toLowerCase(name)
        nameToOID[name] = oid
        nameToCurve[name] = holder
    }

    fun defineCurveAlias(name: String, oid: ASN1ObjectIdentifier) {
        var name = name
        val curve = oidToCurve[oid] ?: throw IllegalStateException()
        name = Strings.toLowerCase(name)
        nameToOID[name] = oid
        nameToCurve[name] = curve
    }

    init {
//        defineCurve("curve25519", curve25519)

//        defineCurveWithOID("secp112r1", SECObjectIdentifiers.secp112r1, secp112r1);
//        defineCurveWithOID("secp112r2", SECObjectIdentifiers.secp112r2, secp112r2);
//        defineCurveWithOID("secp128r1", SECObjectIdentifiers.secp128r1, secp128r1)
        //        defineCurveWithOID("secp128r2", SECObjectIdentifiers.secp128r2, secp128r2);
//        defineCurveWithOID("secp160k1", SECObjectIdentifiers.secp160k1, secp160k1)
//        defineCurveWithOID("secp160r1", SECObjectIdentifiers.secp160r1, secp160r1)
//        defineCurveWithOID("secp160r2", SECObjectIdentifiers.secp160r2, secp160r2)
//        defineCurveWithOID("secp192k1", SECObjectIdentifiers.secp192k1, secp192k1)
//        defineCurveWithOID("secp192r1", SECObjectIdentifiers.secp192r1, secp192r1)
//        defineCurveWithOID("secp224k1", SECObjectIdentifiers.secp224k1, secp224k1)
//        defineCurveWithOID("secp224r1", SECObjectIdentifiers.secp224r1, secp224r1)
        defineCurveWithOID("secp256k1", SECObjectIdentifiers.secp256k1, secp256k1)
        defineCurveWithOID("secp256r1", SECObjectIdentifiers.secp256r1, secp256r1)
//        defineCurveWithOID("secp384r1", SECObjectIdentifiers.secp384r1, secp384r1)
//        defineCurveWithOID("secp521r1", SECObjectIdentifiers.secp521r1, secp521r1)
//        defineCurveWithOID("sect113r1", SECObjectIdentifiers.sect113r1, sect113r1)
//        defineCurveWithOID("sect113r2", SECObjectIdentifiers.sect113r2, sect113r2)
//        defineCurveWithOID("sect131r1", SECObjectIdentifiers.sect131r1, sect131r1)
//        defineCurveWithOID("sect131r2", SECObjectIdentifiers.sect131r2, sect131r2)
//        defineCurveWithOID("sect163k1", SECObjectIdentifiers.sect163k1, sect163k1)
//        defineCurveWithOID("sect163r1", SECObjectIdentifiers.sect163r1, sect163r1)
//        defineCurveWithOID("sect163r2", SECObjectIdentifiers.sect163r2, sect163r2)
//        defineCurveWithOID("sect193r1", SECObjectIdentifiers.sect193r1, sect193r1)
//        defineCurveWithOID("sect193r2", SECObjectIdentifiers.sect193r2, sect193r2)
//        defineCurveWithOID("sect233k1", SECObjectIdentifiers.sect233k1, sect233k1)
//        defineCurveWithOID("sect233r1", SECObjectIdentifiers.sect233r1, sect233r1)
//        defineCurveWithOID("sect239k1", SECObjectIdentifiers.sect239k1, sect239k1)
//        defineCurveWithOID("sect283k1", SECObjectIdentifiers.sect283k1, sect283k1)
//        defineCurveWithOID("sect283r1", SECObjectIdentifiers.sect283r1, sect283r1)
//        defineCurveWithOID("sect409k1", SECObjectIdentifiers.sect409k1, sect409k1)
//        defineCurveWithOID("sect409r1", SECObjectIdentifiers.sect409r1, sect409r1)
//        defineCurveWithOID("sect571k1", SECObjectIdentifiers.sect571k1, sect571k1)
//        defineCurveWithOID("sect571r1", SECObjectIdentifiers.sect571r1, sect571r1)
//        defineCurveWithOID("sm2p256v1", GMObjectIdentifiers.sm2p256v1, sm2p256v1)
//        defineCurveAlias("B-163", SECObjectIdentifiers.sect163r2)
//        defineCurveAlias("B-233", SECObjectIdentifiers.sect233r1)
//        defineCurveAlias("B-283", SECObjectIdentifiers.sect283r1)
//        defineCurveAlias("B-409", SECObjectIdentifiers.sect409r1)
//        defineCurveAlias("B-571", SECObjectIdentifiers.sect571r1)
//        defineCurveAlias("K-163", SECObjectIdentifiers.sect163k1)
//        defineCurveAlias("K-233", SECObjectIdentifiers.sect233k1)
//        defineCurveAlias("K-283", SECObjectIdentifiers.sect283k1)
//        defineCurveAlias("K-409", SECObjectIdentifiers.sect409k1)
//        defineCurveAlias("K-571", SECObjectIdentifiers.sect571k1)
//        defineCurveAlias("P-192", SECObjectIdentifiers.secp192r1)
//        defineCurveAlias("P-224", SECObjectIdentifiers.secp224r1)
//        defineCurveAlias("P-256", SECObjectIdentifiers.secp256r1)
//        defineCurveAlias("P-384", SECObjectIdentifiers.secp384r1)
//        defineCurveAlias("P-521", SECObjectIdentifiers.secp521r1)
    }

    fun getByName(name: String): X9ECParameters? {
        val holder = nameToCurve[Strings.toLowerCase(name)]
        return holder?.parameters
    }

    /**
     * return the X9ECParameters object for the named curve represented by the passed in object
     * identifier. Null if the curve isn't present.
     *
     * @param oid
     * an object identifier representing a named curve, if present.
     */
    fun getByOID(oid: ASN1ObjectIdentifier?): X9ECParameters? {
        val holder = oidToCurve[oid]
        return holder?.parameters
    }

    /**
     * return the object identifier signified by the passed in name. Null if there is no object
     * identifier associated with name.
     *
     * @return the object identifier associated with name, if present.
     */
    fun getOID(name: String): ASN1ObjectIdentifier? {
        return nameToOID[Strings.toLowerCase(name)]
    }

    /**
     * return the named curve name represented by the given object identifier.
     */
    fun getName(oid: ASN1ObjectIdentifier): String? {
        return oidToName[oid]
    }

    /**
     * returns an enumeration containing the name strings for curves contained in this structure.
     */
    fun getNames(): Iterator<String> {
        return names.iterator()
    }
}

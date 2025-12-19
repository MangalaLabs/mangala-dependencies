/*
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
 * Copyright 2023-2025 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from original source: https://github.com/bitcoinj/bitcoinj
 */

package org.bitcoinj.core

import com.google.common.kotlin.base.ObjectsG
import com.google.common.kotlin.base.PreconditionsG
import com.google.common.kotlin.primitives.IntsG
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.mangala.wallet.bitcoinj.utils.shiftRight
import org.bitcoinj.core.Utils.bigIntegerToBytes
//import org.bitcoinj.core.Utils.currentTimeSeconds
//import org.bitcoinj.core.Utils.isAndroidRuntime
import org.bitcoinj.crypto.EncryptableItem
import org.bitcoinj.crypto.EncryptedData
import org.bitcoinj.crypto.KeyCrypter
import org.bitcoinj.crypto.KeyCrypterException
import org.bitcoinj.crypto.LazyECPoint
//import org.bitcoinj.crypto.LinuxSecureRandom
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.ASN1OctetString
import org.spongycastle.asn1.ASN1TaggedObject
import org.spongycastle.asn1.DERBitString
import org.spongycastle.asn1.DLSequence
import org.spongycastle.asn1.x9.X9IntegerConverter
import org.spongycastle.crypto.ec.CustomNamedCurves
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECKeyGenerationParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.math.ec.ECAlgorithms
import org.spongycastle.math.ec.ECPoint
import org.spongycastle.math.ec.FixedPointCombMultiplier
import org.spongycastle.math.ec.FixedPointUtil
import org.spongycastle.math.ec.custom.sec.SecP256K1Curve
import kotlin.jvm.JvmOverloads

//import java.io.ByteArrayOutputStream
//import okio.IOException
//import java.security.SecureRandom
//import java.util.Arrays

//import com.google.common.annotations.VisibleForTesting;
//import org.bitcoin.NativeSecp256k1;
//import org.bitcoin.NativeSecp256k1Util;
//import org.bitcoin.Secp256k1Context;
//import org.bitcoinj.wallet.Protos;
//import org.bitcoinj.wallet.Wallet;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
// TODO: Move this class to tracking compression state itself.
// The Bouncy Castle developers are deprecating their own tracking of the compression state.
/**
 *
 * Represents an elliptic curve public and (optionally) private key, usable for digital signatures but not encryption.
 * Creating a new ECKey with the empty constructor will generate a new random keypair. Other static methods can be used
 * when you already have the public or private parts. If you create a key with only the public part, you can check
 * signatures but not create them.
 *
 *
 * ECKey also provides access to Bitcoin Core compatible text message signing, as accessible via the UI or JSON-RPC.
 * This is slightly different to signing raw bytes - if you want to sign your own data and it won't be exposed as
 * text to people, you don't want to use this. If in doubt, ask on the mailing list.
 *
 *
 * The ECDSA algorithm supports *key recovery* in which a signature plus a couple of discriminator bits can
 * be reversed to find the public key used to calculate it. This can be convenient when you have a message and a
 * signature and want to find out who signed it, rather than requiring the user to provide the expected identity.
 *
 *
 * This class supports a variety of serialization forms. The methods that accept/return byte arrays serialize
 * private keys as raw byte arrays and public keys using the SEC standard byte encoding for public keys. Signatures
 * are encoded using ASN.1/DER inside the Bitcoin protocol.
 *
 *
 * A key can be *compressed* or *uncompressed*. This refers to whether the public key is represented
 * when encoded into bytes as an (x, y) coordinate on the elliptic curve, or whether it's represented as just an X
 * co-ordinate and an extra byte that carries a sign bit. With the latter form the Y coordinate can be calculated
 * dynamically, however, **because the binary serialization is different the address of a key changes if its
 * compression status is changed**. If you deviate from the defaults it's important to understand this: money sent
 * to a compressed version of the key will have a different address to the same key in uncompressed form. Whether
 * a public key is compressed or not is recorded in the SEC binary serialisation format, and preserved in a flag in
 * this class so round-tripping preserves state. Unless you're working with old software or doing unusual things, you
 * can usually ignore the compressed/uncompressed distinction.
 */
class ECKey : EncryptableItem {
    // The two parts of the key. If "priv" is set, "pub" can always be calculated. If "pub" is set but not "priv", we
    // can only verify signatures not make them.
    protected var priv // A field element.
            : BigInteger? = null
    protected var pub: LazyECPoint? = null

    // Creation time of the key in seconds since the epoch, or zero if the key was deserialized from a version that did
    // not have this field.
//    protected var mCreationTimeSeconds: Long = 0

    /**
     * Returns the KeyCrypter that was used to encrypt to encrypt this ECKey. You need this to decrypt the ECKey.
     */
    var keyCrypter: KeyCrypter? = null
        protected set

    /**
     * Returns the the encrypted private key bytes and initialisation vector for this ECKey, or null if the ECKey
     * is not encrypted.
     */
    var encryptedPrivateKey: EncryptedData? = null
        protected set
//    private val pubKeyHash: ByteArray
    /**
     * Generates an entirely new keypair with the given [SecureRandom] object. Point compression is used so the
     * resulting public key will be 33 bytes (32 for the co-ordinate and 1 byte to represent the y bit).
     */
    /**
     * Generates an entirely new keypair. Point compression is used so the resulting public key will be 33 bytes
     * (32 for the co-ordinate and 1 byte to represent the y bit).
     */
    @JvmOverloads
    constructor() {
        val generator = ECKeyPairGenerator()
        val keygenParams = ECKeyGenerationParameters(CURVE)
        generator.init(keygenParams)
        val keypair = generator.generateKeyPair()
        val privParams = keypair.private as ECPrivateKeyParameters
        val pubParams = keypair.public as ECPublicKeyParameters
        priv = privParams.d
        val bits = pubParams.q?.getEncoded(true)
        bits?.let {
            pub = LazyECPoint(CURVE.curve, bits)
        }
//        mCreationTimeSeconds = currentTimeSeconds()
    }


    protected constructor(priv: BigInteger?, pub: ECPoint) {
        if (priv != null) {
            // Try and catch buggy callers or bad key imports, etc. Zero and one are special because these are often
            // used as sentinel values and because scripting languages have a habit of auto-casting true and false to
            // 1 and 0 or vice-versa. Type confusion bugs could therefore result in private keys with these values.
            PreconditionsG.checkArgument(priv != BigInteger.ZERO)
            PreconditionsG.checkArgument(priv != BigInteger.ONE)
        }
        this.priv = priv
        this.pub = LazyECPoint(PreconditionsG.checkNotNull(pub))
    }

    protected constructor(priv: BigInteger?, pub: LazyECPoint) {
        this.priv = priv
        this.pub = PreconditionsG.checkNotNull(pub)
    }

    /**
     * Returns a copy of this key, but with the public point represented in uncompressed form. Normally you would
     * never need this: it's for specialised scenarios or when backwards compatibility in encoded form is necessary.
     */
    fun decompress(): ECKey {
        return if (pub?.isCompressed == false) this else ECKey(
            priv,
            decompressPoint(pub?.get()!!)
        )
    }

    /**
     * Creates an ECKey given only the private key bytes. This is the same as using the BigInteger constructor, but
     * is more convenient if you are importing a key from elsewhere. The public key will be automatically derived
     * from the private key.
     */
    @Deprecated("")
    constructor(
        privKeyBytes: ByteArray?,
        pubKey: ByteArray
    ) : this(if (privKeyBytes == null) null else BigInteger.fromByteArray(privKeyBytes, Sign.POSITIVE), pubKey)


//    constructor(): this()

    /**
     * Create a new ECKey with an encrypted private key, a public key and a KeyCrypter.
     *
     * @param encryptedPrivateKey The private key, encrypted,
     * @param pubKey The keys public key
     * @param keyCrypter The KeyCrypter that will be used, with an AES key, to encrypt and decrypt the private key
     */
    @Deprecated("")
    constructor(
        encryptedPrivateKey: EncryptedData?,
        pubKey: ByteArray,
        keyCrypter: KeyCrypter
    ) : this(null as ByteArray?, pubKey) {
        this.keyCrypter = keyCrypter
        this.encryptedPrivateKey = encryptedPrivateKey
    }

    /**
     * Creates an ECKey given either the private key only, the public key only, or both. If only the private key
     * is supplied, the public key will be calculated from it (this is slow). If both are supplied, it's assumed
     * the public key already correctly matches the private key. If only the public key is supplied, this ECKey cannot
     * be used for signing.
     * @param compressed If set to true and pubKey is null, the derived public key will be in compressed form.
     */
    @Deprecated("")
    constructor(privKey: BigInteger?, pubKey: ByteArray?, compressed: Boolean) {
        require(!(privKey == null && pubKey == null)) { "ECKey requires at least private or public key" }
        priv = privKey
        if (pubKey == null) {
            // Derive public from private.
            var point = publicPointFromPrivate(privKey)
            point = getPointWithCompression(point, compressed)
            pub = LazyECPoint(point)
        } else {
            // We expect the pubkey to be in regular encoded form, just as a BigInteger. Therefore the first byte is
            // a special marker byte.
            // TODO: This is probably not a useful API and may be confusing.
            pub = LazyECPoint(CURVE!!.curve, pubKey)
        }
    }

    /**
     * Creates an ECKey given either the private key only, the public key only, or both. If only the private key
     * is supplied, the public key will be calculated from it (this is slow). If both are supplied, it's assumed
     * the public key already correctly matches the public key. If only the public key is supplied, this ECKey cannot
     * be used for signing.
     */
    @Deprecated("")
    private constructor(privKey: BigInteger?, pubKey: ByteArray) : this(privKey, pubKey, false)

    val isPubKeyOnly: Boolean
        /**
         * Returns true if this key doesn't have unencrypted access to private key bytes. This may be because it was never
         * given any private key bytes to begin with (a watching key), or because the key is encrypted. You can use
         * [.isEncrypted] to tell the cases apart.
         */
        get() = priv == null

    /**
     * Returns true if this key has unencrypted access to private key bytes. Does the opposite of
     * [.isPubKeyOnly].
     */
    fun hasPrivKey(): Boolean {
        return priv != null
    }

    val isWatching: Boolean
        /** Returns true if this key is watch only, meaning it has a public key but no private key.  */
        get() = isPubKeyOnly && !isEncrypted()

    /**
     * Output this ECKey as an ASN.1 encoded private key, as understood by OpenSSL or used by Bitcoin Core
     * in its wallet storage format.
     * @throws org.bitcoinj.core.ECKey.MissingPrivateKeyException if the private key is missing or encrypted.
     */
//    fun toASN1(): ByteArray {
//        return try {
//            val privKeyBytes = privKeyBytes
//            val baos = ByteArrayOutputStream(400)
//
//            // ASN1_SEQUENCE(EC_PRIVATEKEY) = {
//            //   ASN1_SIMPLE(EC_PRIVATEKEY, version, LONG),
//            //   ASN1_SIMPLE(EC_PRIVATEKEY, privateKey, ASN1_OCTET_STRING),
//            //   ASN1_EXP_OPT(EC_PRIVATEKEY, parameters, ECPKPARAMETERS, 0),
//            //   ASN1_EXP_OPT(EC_PRIVATEKEY, publicKey, ASN1_BIT_STRING, 1)
//            // } ASN1_SEQUENCE_END(EC_PRIVATEKEY)
//            val seq = DERSequenceGenerator(baos)
//            seq.addObject(ASN1Integer(1)) // version
//            seq.addObject(DEROctetString(privKeyBytes!!))
//            seq.addObject(DERTaggedObject(0, CURVE_PARAMS?.toASN1Primitive()!!))
//            seq.addObject(DERTaggedObject(1, DERBitString(pubKey)))
//            seq.close()
//            baos.toByteArray()
//        } catch (e: IOException) {
//            throw RuntimeException(e) // Cannot happen, writing to memory stream.
//        }
//    }

    /** Gets the hash160 form of the public key (as seen in addresses).  */ //    public byte[] getPubKeyHash() {
    //        if (pubKeyHash == null)
    //            pubKeyHash = Utils.sha256hash160(this.pub.getEncoded());
    //        return pubKeyHash;
    //    }
    val pubKey: ByteArray
        /**
         * Gets the raw public key value. This appears in transaction scriptSigs. Note that this is **not** the same
         * as the pubKeyHash/address.
         */
        get() = pub?.encoded!!
    val pubKeyPoint: ECPoint
        /** Gets the public key in the form of an elliptic curve point object from Bouncy Castle.  */
        get() = pub!!.get()!!
    val privKey: BigInteger
        /**
         * Gets the private key in the form of an integer field element. The public key is derived by performing EC
         * point addition this number of times (i.e. point multiplying).
         *
         * @throws java.lang.IllegalStateException if the private key bytes are not available.
         */
        get() {
            if (priv == null) throw MissingPrivateKeyException()
            return priv!!
        }
    val isCompressed: Boolean
        /**
         * Returns whether this key is using the compressed form or not. Compressed pubkeys are only 33 bytes, not 64.
         */
        get() = pub!!.isCompressed
    /**
     * Returns the address that corresponds to the public part of this ECKey. Note that an address is derived from
     * the RIPEMD-160 hash of the public key and is not the public key itself (which is too large to be convenient).
     */
    //    public Address toAddress(NetworkParameters params) {
    //        return new Address(params, getPubKeyHash());
    //    }
    /**
     * Groups the two components that make up a signature, and provides a way to encode to DER form, which is
     * how ECDSA signatures are represented when embedded in other data structures in the Bitcoin protocol. The raw
     * components can be useful for doing further EC maths on them.
     */
    class ECDSASignature
    /**
     * Constructs a signature with the given components. Does NOT automatically canonicalise the signature.
     */(
        /** The two components of the signature.  */
        val r: BigInteger, val s: BigInteger
    ) {
        val isCanonical: Boolean
            /**
             * Returns true if the S component is "low", that means it is below [ECKey.HALF_CURVE_ORDER]. See [BIP62](https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures).
             */
            get() = s.compareTo(HALF_CURVE_ORDER!!) <= 0

        /**
         * Will automatically adjust the S component to be less than or equal to half the curve order, if necessary.
         * This is required because for every signature (r,s) the signature (r, -s (mod N)) is a valid signature of
         * the same message. However, we dislike the ability to modify the bits of a Bitcoin transaction after it's
         * been signed, as that violates various assumed invariants. Thus in future only one of those forms will be
         * considered legal and the other will be banned.
         */
        fun toCanonicalised(): ECDSASignature {
            return if (!isCanonical) {
                // The order of the curve is the number of valid points that exist on that curve. If S is in the upper
                // half of the number of valid points, then bring it back to the lower half. Otherwise, imagine that
                //    N = 10
                //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
                //    10 - 8 == 2, giving us always the latter solution, which is canonical.
                ECDSASignature(
                    r, CURVE!!.n.subtract(
                        s
                    )
                )
            } else {
                this
            }
        }

        /**
         * DER is an international standard for serializing data structures which is widely used in cryptography.
         * It's somewhat like protocol buffers but less convenient. This method returns a standard DER encoding
         * of the signature, as recognized by OpenSSL and other libraries.
         */
//        fun encodeToDER(): ByteArray {
//            return try {
//                derByteStream().toByteArray()
//            } catch (e: IOException) {
//                throw RuntimeException(e) // Cannot happen.
//            }
//        }

//        @Throws(IOException::class)
//        protected fun derByteStream(): ByteArrayOutputStream {
//            // Usually 70-72 bytes.
//            val bos = ByteArrayOutputStream(72)
//            val seq = DERSequenceGenerator(bos)
//            seq.addObject(ASN1Integer(r))
//            seq.addObject(ASN1Integer(s))
//            seq.close()
//            return bos
//        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ECDSASignature) return false

            return r == other.r && s == other.s
        }

        override fun hashCode(): Int {
            return ObjectsG.hashCode(r, s)
        }

        companion object {
//            fun decodeFromDER(bytes: ByteArray): ECDSASignature {
//                var decoder: ASN1InputStream? = null
//                return try {
//                    decoder = ASN1InputStream(bytes)
//                    val seq = decoder.readObject() as DLSequence
//                        ?: throw RuntimeException("Reached past end of ASN.1 stream.")
//                    val r: ASN1Integer
//                    val s: ASN1Integer
//                    try {
//                        r = seq.getObjectAt(0) as ASN1Integer
//                        s = seq.getObjectAt(1) as ASN1Integer
//                    } catch (e: ClassCastException) {
//                        throw IllegalArgumentException(e)
//                    }
//                    // OpenSSL deviates from the DER spec by interpreting these values as unsigned, though they should not be
//                    // Thus, we always use the positive versions. See: http://r6.ca/blog/20111119T211504Z.html
//                    ECDSASignature(r.positiveValue, s.positiveValue)
//                } catch (e: IOException) {
//                    throw RuntimeException(e)
//                } finally {
//                    if (decoder != null) try {
//                        decoder.close()
//                    } catch (x: IOException) {
//                    }
//                }
//            }
        }
    }

    val privKeyBytes: ByteArray?
        /**
         * Returns a 32 byte array containing the private key.
         * @throws org.bitcoinj.core.ECKey.MissingPrivateKeyException if the private key bytes are missing/encrypted.
         */
        get() = bigIntegerToBytes(privKey, 32)
    /**
     * Exports the private key in the form used by Bitcoin Core's "dumpprivkey" and "importprivkey" commands. Use
     * the [org.bitcoinj.core.DumpedPrivateKey.toString] method to get the string.
     *
     * @param params The network this key is intended for use on.
     * @return Private key bytes as a [DumpedPrivateKey].
     * @throws IllegalStateException if the private key is not available.
     */
    //    public DumpedPrivateKey getPrivateKeyEncoded(NetworkParameters params) {
    //        return new DumpedPrivateKey(params, getPrivKeyBytes(), isCompressed());
    //    }
    /**
     * Returns the creation time of this key or zero if the key was deserialized from a version that did not store
     * that data.
     */
//    override fun getCreationTimeSeconds(): Long {
//        return mCreationTimeSeconds
//    }

    /**
     * Sets the creation time of this key. Zero is a convention to mean "unavailable". This method can be useful when
     * you have a raw key you are importing from somewhere else.
     */
//    fun setCreationTimeSeconds(newCreationTimeSeconds: Long) {
//        require(newCreationTimeSeconds >= 0) { "Cannot set creation time to negative value: $newCreationTimeSeconds" }
//        mCreationTimeSeconds = newCreationTimeSeconds
//    }

    /**
     * Create an encrypted private key with the keyCrypter and the AES key supplied.
     * This method returns a new encrypted key and leaves the original unchanged.
     *
     * @param keyCrypter The keyCrypter that specifies exactly how the encrypted bytes are created.
     * @param aesKey The KeyParameter with the AES encryption key (usually constructed with keyCrypter#deriveKey and cached as it is slow to create).
     * @return encryptedKey
     */
//    @Throws(KeyCrypterException::class)
//    fun encrypt(keyCrypter: KeyCrypter, aesKey: KeyParameter?): ECKey {
//        PreconditionsG.checkNotNull(keyCrypter)
//        val privKeyBytes = privKeyBytes
//        val encryptedPrivateKey = keyCrypter.encrypt(privKeyBytes, aesKey)
//        val result = fromEncrypted(encryptedPrivateKey!!, keyCrypter, pubKey)
//        result.setCreationTimeSeconds(mCreationTimeSeconds)
//        return result
//    }

    /**
     * Create a decrypted private key with the keyCrypter and AES key supplied. Note that if the aesKey is wrong, this
     * has some chance of throwing KeyCrypterException due to the corrupted padding that will result, but it can also
     * just yield a garbage key.
     *
     * @param keyCrypter The keyCrypter that specifies exactly how the decrypted bytes are created.
     * @param aesKey The KeyParameter with the AES encryption key (usually constructed with keyCrypter#deriveKey and cached).
     */
    @Throws(KeyCrypterException::class)
    fun decrypt(keyCrypter: KeyCrypter, aesKey: KeyParameter?): ECKey {
        PreconditionsG.checkNotNull(keyCrypter)
        // Check that the keyCrypter matches the one used to encrypt the keys, if set.
        if (this.keyCrypter != null && this.keyCrypter != keyCrypter) throw KeyCrypterException("The keyCrypter being used to decrypt the key is different to the one that was used to encrypt it")
        PreconditionsG.checkState(encryptedPrivateKey != null, "This key is not encrypted")
        val unencryptedPrivateKey = keyCrypter.decrypt(encryptedPrivateKey, aesKey)
        var key = fromPrivate(unencryptedPrivateKey!!)
        if (!isCompressed) key = key.decompress()
//        if (!Arrays.equals(
//                key.pubKey,
//                pubKey
//            )
//        ) throw KeyCrypterException("Provided AES key is wrong")

        if (!key.pubKey.contentEquals(pubKey)) {
            throw KeyCrypterException("Provided AES key is wrong")
        }

//        key.setCreationTimeSeconds(mCreationTimeSeconds)
        return key
    }

    /**
     * Create a decrypted private key with AES key. Note that if the AES key is wrong, this
     * has some chance of throwing KeyCrypterException due to the corrupted padding that will result, but it can also
     * just yield a garbage key.
     *
     * @param aesKey The KeyParameter with the AES encryption key (usually constructed with keyCrypter#deriveKey and cached).
     */
    @Throws(KeyCrypterException::class)
    fun decrypt(aesKey: KeyParameter?): ECKey {
        val crypter = keyCrypter ?: throw KeyCrypterException("No key crypter available")
        return decrypt(crypter, aesKey)
    }

    /**
     * Creates decrypted private key if needed.
     */
    @Throws(KeyCrypterException::class)
    fun maybeDecrypt(aesKey: KeyParameter?): ECKey {
        return if (isEncrypted() && aesKey != null) decrypt(aesKey) else this
    }

    /**
     * Indicates whether the private key is encrypted (true) or not (false).
     * A private key is deemed to be encrypted when there is both a KeyCrypter and the encryptedPrivateKey is non-zero.
     */
    override fun isEncrypted(): Boolean {
        return keyCrypter != null && encryptedPrivateKey != null && encryptedPrivateKey!!.encryptedBytes.size > 0
    }
    //    @Nullable
    //    @Override
    //    public Protos.Wallet.EncryptionType getEncryptionType() {
    //        return keyCrypter != null ? keyCrypter.getUnderstoodEncryptionType() : Protos.Wallet.EncryptionType.UNENCRYPTED;
    //    }
    /**
     * A wrapper for [.getPrivKeyBytes] that returns null if the private key bytes are missing or would have
     * to be derived (for the HD key case).
     */
    override fun getSecretBytes(): ByteArray? {
        return if (hasPrivKey()) privKeyBytes else null
    }

    /** An alias for [.getEncryptedPrivateKey]  */
    override fun getEncryptedData(): EncryptedData? {
        return encryptedPrivateKey
    }

    open class MissingPrivateKeyException : RuntimeException()
    class KeyIsEncryptedException : MissingPrivateKeyException()

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || o !is ECKey) return false
        val other = o
        return (ObjectsG.equal(priv, other.priv)
                && ObjectsG.equal(pub, other.pub)
//                && ObjectsG.equal(mCreationTimeSeconds, other.mCreationTimeSeconds)
                && ObjectsG.equal(keyCrypter, other.keyCrypter)
                && ObjectsG.equal(encryptedPrivateKey, other.encryptedPrivateKey))
    }

    override fun hashCode(): Int {
        // Public keys are random already so we can just use a part of them as the hashcode. Read from the start to
        // avoid picking up the type code (compressed vs uncompressed) which is tacked on the end.
        val bits = pubKey
        return IntsG.fromBytes(bits[0], bits[1], bits[2], bits[3])
    }

    override fun toString(): String {
//        return toString(false, null);
        return ""
    }

    /**
     * Produce a string rendering of the ECKey INCLUDING the private key.
     * Unless you absolutely need the private key it is better for security reasons to just use [.toString].
     */
    //    public String toStringWithPrivate(NetworkParameters params) {
    //        return toString(true, params);
    //    }
    //    public String getPrivateKeyAsHex() {
    //        return Utils.HEX.encode(getPrivKeyBytes());
    //    }
    //
    //    public String getPublicKeyAsHex() {
    //        return Utils.HEX.encode(pub.getEncoded());
    //    }
    //
    //    public String getPrivateKeyAsWiF(NetworkParameters params) {
    //        return getPrivateKeyEncoded(params).toString();
    //    }
    //    private String toString(boolean includePrivate, NetworkParameters params) {
    //        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this).omitNullValues();
    //        helper.add("pub HEX", getPublicKeyAsHex());
    //        if (includePrivate) {
    //            try {
    //                helper.add("priv HEX", getPrivateKeyAsHex());
    //                helper.add("priv WIF", getPrivateKeyAsWiF(params));
    //            } catch (IllegalStateException e) {
    //                // TODO: Make hasPrivKey() work for deterministic keys and fix this.
    //            } catch (Exception e) {
    //                final String message = e.getMessage();
    //                helper.add("priv EXCEPTION", e.getClass().getName() + (message != null ? ": " + message : ""));
    //            }
    //        }
    //        if (creationTimeSeconds > 0)
    //            helper.add("creationTimeSeconds", creationTimeSeconds);
    //        helper.add("keyCrypter", keyCrypter);
    //        if (includePrivate)
    //            helper.add("encryptedPrivateKey", encryptedPrivateKey);
    //        helper.add("isEncrypted", isEncrypted());
    //        helper.add("isPubKeyOnly", isPubKeyOnly());
    //        return helper.toString();
    //    }
    //    public void formatKeyWithAddress(boolean includePrivateKeys, StringBuilder builder, NetworkParameters params) {
    //        final Address address = toAddress(params);
    //        builder.append("  addr:");
    //        builder.append(address.toString());
    //        builder.append("  hash160:");
    //        builder.append(Utils.HEX.encode(getPubKeyHash()));
    //        if (creationTimeSeconds > 0)
    //            builder.append("  creationTimeSeconds:").append(creationTimeSeconds);
    //        builder.append("\n");
    //        if (includePrivateKeys) {
    //            builder.append("  ");
    //            builder.append(toStringWithPrivate(params));
    //            builder.append("\n");
    //        }
    //    }
    companion object {
        //    private static final Logger log = LoggerFactory.getLogger(ECKey.class);
        /** Sorts oldest keys first, newest last.  */
//        val AGE_COMPARATOR =
//            Comparator<ECKey> { k1, k2 -> if (k1.mCreationTimeSeconds == k2.mCreationTimeSeconds) 0 else if (k1.mCreationTimeSeconds > k2.mCreationTimeSeconds) 1 else -1 }

        /** Compares pub key bytes using [com.google.common.primitives.UnsignedBytesG.lexicographicalComparator]  */
//        val PUBKEY_COMPARATOR: Comparator<ECKey> = object : Comparator<ECKey> {
//            private val comparator = UnsignedBytesG.lexicographicalComparator()
//            override fun compare(k1: ECKey, k2: ECKey): Int {
//                return comparator.compare(k1.pubKey, k2.pubKey)
//            }
//        }

        // The parameters of the secp256k1 curve that Bitcoin uses.
        private val CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1")

        /** The parameters of the secp256k1 curve that Bitcoin uses.  */
        lateinit var CURVE: ECDomainParameters

        /**
         * Equal to CURVE.getN().shiftRight(1), used for canonicalising the S value of a signature. If you aren't
         * sure what this is about, you can ignore it.
         */
        var HALF_CURVE_ORDER: BigInteger? = null
//        private lateinit var secureRandom: SecureRandom

        init {
            // Init proper random number generator, as some old Android installations have bugs that make it unsecure.
//            if (isAndroidRuntime) LinuxSecureRandom()

            // Tell Bouncy Castle to precompute data that's needed during secp256k1 calculations. Increasing the width
            // number makes calculations faster, but at a cost of extra memory usage and with decreasing returns. 12 was
            // picked after consulting with the BC team.
            FixedPointUtil.precompute(CURVE_PARAMS?.getG(), 12)
            val cur = CURVE_PARAMS?.curve
            val g = CURVE_PARAMS?.getG()
            val n = CURVE_PARAMS?.n
            val h = CURVE_PARAMS?.h

            if(cur != null && g != null && n != null && h != null){
                CURVE = ECDomainParameters(cur, g, n, h)
            }

            HALF_CURVE_ORDER = CURVE_PARAMS?.n?.shiftRight(1)
//            secureRandom = SecureRandom()
        }

        /**
         * Utility for compressing an elliptic curve point. Returns the same point if it's already compressed.
         * See the ECKey class docs for a discussion of point compression.
         */
        fun compressPoint(point: ECPoint): ECPoint {
            return getPointWithCompression(point, true)
        }

        fun compressPoint(point: LazyECPoint): LazyECPoint {
            return if (point.isCompressed) point else LazyECPoint(compressPoint(point.get()!!))
        }

        /**
         * Utility for decompressing an elliptic curve point. Returns the same point if it's already compressed.
         * See the ECKey class docs for a discussion of point compression.
         */
        fun decompressPoint(point: ECPoint): ECPoint {
            return getPointWithCompression(point, false)
        }

        fun decompressPoint(point: LazyECPoint): LazyECPoint {
            return if (!point.isCompressed) point else LazyECPoint(decompressPoint(point.get()!!))
        }

        private fun getPointWithCompression(point: ECPoint, compressed: Boolean): ECPoint {
            var point = point
            if (point.isCompressed == compressed) return point
            point = point.normalize()
            val x = point.affineXCoord?.toBigInteger()
            val y = point.affineYCoord?.toBigInteger()
            return CURVE!!.curve.createPoint(x, y, compressed)
        }

        /**
         * Construct an ECKey from an ASN.1 encoded private key. These are produced by OpenSSL and stored by Bitcoin
         * Core in its wallet. Note that this is slow because it requires an EC point multiply.
         */
        fun fromASN1(asn1privkey: ByteArray): ECKey {
            return extractKeyFromASN1(asn1privkey)
        }
        /**
         * Creates an ECKey given the private key only. The public key is calculated from it (this is slow), either
         * compressed or not.
         */
        /**
         * Creates an ECKey given the private key only. The public key is calculated from it (this is slow). The resulting
         * public key is compressed.
         */
        @JvmOverloads
        fun fromPrivate(privKey: BigInteger, compressed: Boolean = true): ECKey {
            val point = publicPointFromPrivate(privKey)
            return ECKey(privKey, getPointWithCompression(point, compressed))
        }

        /**
         * Creates an ECKey given the private key only. The public key is calculated from it (this is slow). The resulting
         * public key is compressed.
         */
        fun fromPrivate(privKeyBytes: ByteArray): ECKey {
            return fromPrivate(BigInteger.fromByteArray(privKeyBytes, Sign.POSITIVE))
        }

        /**
         * Creates an ECKey given the private key only. The public key is calculated from it (this is slow), either
         * compressed or not.
         */
        fun fromPrivate(privKeyBytes: ByteArray, compressed: Boolean): ECKey {
            return fromPrivate(BigInteger.fromByteArray(privKeyBytes, Sign.POSITIVE), compressed)
        }

        /**
         * Creates an ECKey that simply trusts the caller to ensure that point is really the result of multiplying the
         * generator point by the private key. This is used to speed things up when you know you have the right values
         * already. The compression state of pub will be preserved.
         */
        fun fromPrivateAndPrecalculatedPublic(priv: BigInteger?, pub: ECPoint): ECKey {
            return ECKey(priv, pub)
        }

        /**
         * Creates an ECKey that simply trusts the caller to ensure that point is really the result of multiplying the
         * generator point by the private key. This is used to speed things up when you know you have the right values
         * already. The compression state of the point will be preserved.
         */
//        fun fromPrivateAndPrecalculatedPublic(priv: ByteArray, pub: ByteArray): ECKey {
//            PreconditionsG.checkNotNull(priv)
//            PreconditionsG.checkNotNull(pub)
//            return ECKey(BigInteger(1, priv), CURVE!!.curve.decodePoint(pub))
//        }

        /**
         * Creates an ECKey that cannot be used for signing, only verifying signatures, from the given point. The
         * compression state of pub will be preserved.
         */
//        fun fromPublicOnly(pub: ECPoint): ECKey {
//            return ECKey(null, pub)
//        }

        /**
         * Creates an ECKey that cannot be used for signing, only verifying signatures, from the given encoded point.
         * The compression state of pub will be preserved.
         */
        fun fromPublicOnly(pub: ByteArray): ECKey {
            return ECKey(null, CURVE!!.curve.decodePoint(pub))
        }

        /**
         * Constructs a key that has an encrypted private component. The given object wraps encrypted bytes and an
         * initialization vector. Note that the key will not be decrypted during this call: the returned ECKey is
         * unusable for signing unless a decryption key is supplied.
         */
        fun fromEncrypted(
            encryptedPrivateKey: EncryptedData,
            crypter: KeyCrypter,
            pubKey: ByteArray
        ): ECKey {
            val key = fromPublicOnly(pubKey)
            key.encryptedPrivateKey = PreconditionsG.checkNotNull(encryptedPrivateKey)
            key.keyCrypter = PreconditionsG.checkNotNull(crypter)
            return key
        }

        /**
         * Returns public key bytes from the given private key. To convert a byte array into a BigInteger, use <tt>
         * new BigInteger(1, bytes);</tt>
         */
//        fun publicKeyFromPrivate(privKey: BigInteger, compressed: Boolean): ByteArray {
//            val point = publicPointFromPrivate(privKey)
//            return point.getEncoded(compressed)
//        }

        /**
         * Returns public key point from the given private key. To convert a byte array into a BigInteger, use <tt>
         * new BigInteger(1, bytes);</tt>
         */
        fun publicPointFromPrivate(privKey: BigInteger?): ECPoint {
            /*
         * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group order,
         * but that could change in future versions.
         */
            var privKey = privKey
            if ((privKey?.bitLength() ?: 0) > CURVE!!.n.bitLength()) {
                privKey = privKey?.mod(CURVE!!.n)
            }
            return FixedPointCombMultiplier().multiply(CURVE!!.g, privKey!!)
        }
        /**
         * Signs the given hash and returns the R and S components as BigIntegers. In the Bitcoin protocol, they are
         * usually encoded using ASN.1 format, so you want [org.bitcoinj.core.ECKey.ECDSASignature.toASN1]
         * instead. However sometimes the independent components can be useful, for instance, if you're going to do
         * further EC maths on them.
         * @throws KeyCrypterException if this ECKey doesn't have a private part.
         */
        //    public ECDSASignature sign(Sha256Hash input) throws KeyCrypterException {
        //        return sign(input, null);
        //    }
        /**
         * If this global variable is set to true, sign() creates a dummy signature and verify() always returns true.
         * This is intended to help accelerate unit tests that do a lot of signing/verifying, which in the debugger
         * can be painfully slow.
         */
        //    @VisibleForTesting
        var FAKE_SIGNATURES = false
        /**
         * Signs the given hash and returns the R and S components as BigIntegers. In the Bitcoin protocol, they are
         * usually encoded using DER format, so you want [org.bitcoinj.core.ECKey.ECDSASignature.encodeToDER]
         * instead. However sometimes the independent components can be useful, for instance, if you're doing to do further
         * EC maths on them.
         *
         * @param aesKey The AES key to use for decryption of the private key. If null then no decryption is required.
         * @throws KeyCrypterException if there's something wrong with aesKey.
         * @throws ECKey.MissingPrivateKeyException if this key cannot sign because it's pubkey only.
         */
        //    public ECDSASignature sign(Sha256Hash input, @Nullable KeyParameter aesKey) throws KeyCrypterException {
        //        KeyCrypter crypter = getKeyCrypter();
        //        if (crypter != null) {
        //            if (aesKey == null)
        //                throw new KeyIsEncryptedException();
        //            return decrypt(aesKey).sign(input);
        //        } else {
        //            // No decryption of private key required.
        //            if (priv == null)
        //                throw new MissingPrivateKeyException();
        //        }
        //        return doSign(input, priv);
        //    }
        //    protected ECDSASignature doSign(Sha256Hash input, BigInteger privateKeyForSigning) {
        //        if (Secp256k1Context.isEnabled()) {
        //            try {
        //                byte[] signature = NativeSecp256k1.sign(
        //                        input.getBytes(),
        //                        Utils.bigIntegerToBytes(privateKeyForSigning, 32)
        //                );
        //                return ECDSASignature.decodeFromDER(signature);
        //            } catch (NativeSecp256k1Util.AssertFailException e) {
        ////                log.error("Caught AssertFailException inside secp256k1", e);
        //                throw new RuntimeException(e);
        //            }
        //        }
        //        if (FAKE_SIGNATURES)
        //            return TransactionSignature.dummy();
        //        checkNotNull(privateKeyForSigning);
        //        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        //        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKeyForSigning, CURVE);
        //        signer.init(true, privKey);
        //        BigInteger[] components = signer.generateSignature(input.getBytes());
        //        return new ECDSASignature(components[0], components[1]).toCanonicalised();
        //    }
        /**
         *
         * Verifies the given ECDSA signature against the message bytes using the public key bytes.
         *
         *
         * When using native ECDSA verification, data must be 32 bytes, and no element may be
         * larger than 520 bytes.
         *
         * @param data      Hash of the data to verify.
         * @param signature ASN.1 encoded signature.
         * @param pub       The public key bytes to use.
         */
        //    public static boolean verify(byte[] data, ECDSASignature signature, byte[] pub) {
        //        if (FAKE_SIGNATURES)
        //            return true;
        //
        //        if (Secp256k1Context.isEnabled()) {
        //            try {
        //                return NativeSecp256k1.verify(data, signature.encodeToDER(), pub);
        //            } catch (NativeSecp256k1Util.AssertFailException e) {
        ////                log.error("Caught AssertFailException inside secp256k1", e);
        //                return false;
        //            }
        //        }
        //
        //        ECDSASigner signer = new ECDSASigner();
        //        ECPublicKeyParameters params = new ECPublicKeyParameters(CURVE.getCurve().decodePoint(pub), CURVE);
        //        signer.init(false, params);
        //        try {
        //            return signer.verifySignature(data, signature.r, signature.s);
        //        } catch (NullPointerException e) {
        //            // Bouncy Castle contains a bug that can cause NPEs given specially crafted signatures. Those signatures
        //            // are inherently invalid/attack sigs so we just fail them here rather than crash the thread.
        ////            log.error("Caught NPE inside bouncy castle", e);
        //            return false;
        //        }
        //    }
        /**
         * Verifies the given ASN.1 encoded ECDSA signature against a hash using the public key.
         *
         * @param data      Hash of the data to verify.
         * @param signature ASN.1 encoded signature.
         * @param pub       The public key bytes to use.
         */
        //    public static boolean verify(byte[] data, byte[] signature, byte[] pub) {
        //        if (Secp256k1Context.isEnabled()) {
        //            try {
        //                return NativeSecp256k1.verify(data, signature, pub);
        //            } catch (NativeSecp256k1Util.AssertFailException e) {
        ////                log.error("Caught AssertFailException inside secp256k1", e);
        //                return false;
        //            }
        //        }
        //        return verify(data, ECDSASignature.decodeFromDER(signature), pub);
        //    }
        //    /**
        //     * Verifies the given ASN.1 encoded ECDSA signature against a hash using the public key.
        //     *
        //     * @param hash      Hash of the data to verify.
        //     * @param signature ASN.1 encoded signature.
        //     */
        //    public boolean verify(byte[] hash, byte[] signature) {
        //        return ECKey.verify(hash, signature, getPubKey());
        //    }
        //
        //    /**
        //     * Verifies the given R/S pair (signature) against a hash using the public key.
        //     */
        //    public boolean verify(Sha256Hash sigHash, ECDSASignature signature) {
        //        return ECKey.verify(sigHash.getBytes(), signature, getPubKey());
        //    }
        /**
         * Verifies the given ASN.1 encoded ECDSA signature against a hash using the public key, and throws an exception
         * if the signature doesn't match
         * @throws java.security.SignatureException if the signature does not match.
         */
        //    public void verifyOrThrow(byte[] hash, byte[] signature) throws SignatureException {
        //        if (!verify(hash, signature))
        //            throw new SignatureException();
        //    }
        //
        //    /**
        //     * Verifies the given R/S pair (signature) against a hash using the public key, and throws an exception
        //     * if the signature doesn't match
        //     * @throws java.security.SignatureException if the signature does not match.
        //     */
        //    public void verifyOrThrow(Sha256Hash sigHash, ECDSASignature signature) throws SignatureException {
        //        if (!ECKey.verify(sigHash.getBytes(), signature, getPubKey()))
        //            throw new SignatureException();
        //    }
        /**
         * Returns true if the given pubkey is canonical, i.e. the correct length taking into account compression.
         */
        fun isPubKeyCanonical(pubkey: ByteArray): Boolean {
            if (pubkey.size < 33) return false
            if (pubkey[0].toInt() == 0x04) {
                // Uncompressed pubkey
                if (pubkey.size != 65) return false
            } else if (pubkey[0].toInt() == 0x02 || pubkey[0].toInt() == 0x03) {
                // Compressed pubkey
                if (pubkey.size != 33) return false
            } else return false
            return true
        }

        private fun extractKeyFromASN1(asn1privkey: ByteArray): ECKey {
            // To understand this code, see the definition of the ASN.1 format for EC private keys in the OpenSSL source
            // code in ec_asn1.c:
            //
            // ASN1_SEQUENCE(EC_PRIVATEKEY) = {
            //   ASN1_SIMPLE(EC_PRIVATEKEY, version, LONG),
            //   ASN1_SIMPLE(EC_PRIVATEKEY, privateKey, ASN1_OCTET_STRING),
            //   ASN1_EXP_OPT(EC_PRIVATEKEY, parameters, ECPKPARAMETERS, 0),
            //   ASN1_EXP_OPT(EC_PRIVATEKEY, publicKey, ASN1_BIT_STRING, 1)
            // } ASN1_SEQUENCE_END(EC_PRIVATEKEY)
            //
            return try {
                val decoder = ASN1InputStream(asn1privkey)
                val seq = decoder.readObject() as DLSequence
                PreconditionsG.checkArgument(
                    decoder.readObject() == null,
                    "Input contains extra bytes"
                )
                decoder.close()
                PreconditionsG.checkArgument(
                    seq.size() == 4,
                    "Input does not appear to be an ASN.1 OpenSSL EC private key"
                )
                PreconditionsG.checkArgument(
                    (seq.getObjectAt(0) as ASN1Integer).value == BigInteger.ONE,
                    "Input is of wrong version"
                )
                val privbits = (seq.getObjectAt(1) as ASN1OctetString).octets
                val privkey = BigInteger.fromByteArray( privbits, Sign.POSITIVE)
                val pubkey = seq.getObjectAt(3) as ASN1TaggedObject
                PreconditionsG.checkArgument(
                    pubkey.tagNo == 1,
                    "Input has 'publicKey' with bad tag number"
                )
                val pubbits = (pubkey.getObject() as DERBitString).bytes
                PreconditionsG.checkArgument(
                    pubbits?.size == 33 || pubbits?.size == 65,
                    "Input has 'publicKey' with invalid length"
                )
                val encoding = pubbits!![0].toInt() and 0xFF
                // Only allow compressed(2,3) and uncompressed(4), not infinity(0) or hybrid(6,7)
                PreconditionsG.checkArgument(
                    encoding >= 2 && encoding <= 4,
                    "Input has 'publicKey' with invalid encoding"
                )

                // Now sanity check to ensure the pubkey bytes match the privkey.
                val compressed = pubbits.size == 33
                val key = ECKey(privkey, null, compressed)
                require(
//                    Arrays.equals(
//                        key.pubKey,
//                        pubbits
//                    )
                    key.pubKey.contentEquals(pubbits)
                ) { "Public key in ASN.1 structure does not match private key." }
                key
            } catch (e: Exception) {
                throw RuntimeException(e) // Cannot happen, reading from memory stream.
            }
        }
        /**
         * Signs a text message using the standard Bitcoin messaging signing format and returns the signature as a base64
         * encoded string.
         *
         * @throws IllegalStateException if this ECKey does not have the private part.
         * @throws KeyCrypterException if this ECKey is encrypted and no AESKey is provided or it does not decrypt the ECKey.
         */
        //    public String signMessage(String message) throws KeyCrypterException {
        //        return signMessage(message, null);
        //    }
        /**
         * Signs a text message using the standard Bitcoin messaging signing format and returns the signature as a base64
         * encoded string.
         *
         * @throws IllegalStateException if this ECKey does not have the private part.
         * @throws KeyCrypterException if this ECKey is encrypted and no AESKey is provided or it does not decrypt the ECKey.
         */
        //    public String signMessage(String message, @Nullable KeyParameter aesKey) throws KeyCrypterException {
        //        byte[] data = Utils.formatMessageForSigning(message);
        //        Sha256Hash hash = Sha256Hash.twiceOf(data);
        //        ECDSASignature sig = sign(hash, aesKey);
        //        // Now we have to work backwards to figure out the recId needed to recover the signature.
        //        int recId = -1;
        //        for (int i = 0; i < 4; i++) {
        //            ECKey k = ECKey.recoverFromSignature(i, sig, hash, isCompressed());
        //            if (k != null && k.pub.equals(pub)) {
        //                recId = i;
        //                break;
        //            }
        //        }
        //        if (recId == -1)
        //            throw new RuntimeException("Could not construct a recoverable key. This should never happen.");
        //        int headerByte = recId + 27 + (isCompressed() ? 4 : 0);
        //        byte[] sigData = new byte[65];  // 1 header + 32 bytes for R + 32 bytes for S
        //        sigData[0] = (byte)headerByte;
        //        System.arraycopy(Utils.bigIntegerToBytes(sig.r, 32), 0, sigData, 1, 32);
        //        System.arraycopy(Utils.bigIntegerToBytes(sig.s, 32), 0, sigData, 33, 32);
        //        return new String(Base64.encode(sigData), Charset.forName("UTF-8"));
        //    }
        /**
         * Given an arbitrary piece of text and a Bitcoin-format message signature encoded in base64, returns an ECKey
         * containing the public key that was used to sign it. This can then be compared to the expected public key to
         * determine if the signature was correct. These sorts of signatures are compatible with the Bitcoin-Qt/bitcoind
         * format generated by signmessage/verifymessage RPCs and GUI menu options. They are intended for humans to verify
         * their communications with each other, hence the base64 format and the fact that the input is text.
         *
         * @param message Some piece of human readable text.
         * @param signatureBase64 The Bitcoin-format message signature in base64
         * @throws SignatureException If the public key could not be recovered or if there was a signature format error.
         */
        //    public static ECKey signedMessageToKey(String message, String signatureBase64) throws SignatureException {
        //        byte[] signatureEncoded;
        //        try {
        //            signatureEncoded = Base64.decode(signatureBase64);
        //        } catch (RuntimeException e) {
        //            // This is what you get back from Bouncy Castle if base64 doesn't decode :(
        //            throw new SignatureException("Could not decode base64", e);
        //        }
        //        // Parse the signature bytes into r/s and the selector value.
        //        if (signatureEncoded.length < 65)
        //            throw new SignatureException("Signature truncated, expected 65 bytes and got " + signatureEncoded.length);
        //        int header = signatureEncoded[0] & 0xFF;
        //        // The header byte: 0x1B = first key with even y, 0x1C = first key with odd y,
        //        //                  0x1D = second key with even y, 0x1E = second key with odd y
        //        if (header < 27 || header > 34)
        //            throw new SignatureException("Header byte out of range: " + header);
        //        BigInteger r = new BigInteger(1, Arrays.copyOfRange(signatureEncoded, 1, 33));
        //        BigInteger s = new BigInteger(1, Arrays.copyOfRange(signatureEncoded, 33, 65));
        //        ECDSASignature sig = new ECDSASignature(r, s);
        //        byte[] messageBytes = Utils.formatMessageForSigning(message);
        //        // Note that the C++ code doesn't actually seem to specify any character encoding. Presumably it's whatever
        //        // JSON-SPIRIT hands back. Assume UTF-8 for now.
        //        Sha256Hash messageHash = Sha256Hash.twiceOf(messageBytes);
        //        boolean compressed = false;
        //        if (header >= 31) {
        //            compressed = true;
        //            header -= 4;
        //        }
        //        int recId = header - 27;
        //        ECKey key = ECKey.recoverFromSignature(recId, sig, messageHash, compressed);
        //        if (key == null)
        //            throw new SignatureException("Could not recover public key from signature");
        //        return key;
        //    }
        /**
         * Convenience wrapper around [ECKey.signedMessageToKey]. If the key derived from the
         * signature is not the same as this one, throws a SignatureException.
         */
        //    public void verifyMessage(String message, String signatureBase64) throws SignatureException {
        //        ECKey key = ECKey.signedMessageToKey(message, signatureBase64);
        //        if (!key.pub.equals(pub))
        //            throw new SignatureException("Signature did not match for message");
        //    }
        /**
         *
         * Given the components of a signature and a selector value, recover and return the public key
         * that generated the signature according to the algorithm in SEC1v2 section 4.1.6.
         *
         *
         * The recId is an index from 0 to 3 which indicates which of the 4 possible keys is the correct one. Because
         * the key recovery operation yields multiple potential keys, the correct key must either be stored alongside the
         * signature, or you must be willing to try each recId in turn until you find one that outputs the key you are
         * expecting.
         *
         *
         * If this method returns null it means recovery was not possible and recId should be iterated.
         *
         *
         * Given the above two points, a correct usage of this method is inside a for loop from 0 to 3, and if the
         * output is null OR a key that is not the one you expect, you try again with the next recId.
         *
         * @param recId Which possible key to recover.
         * @param sig the R and S components of the signature, wrapped.
         * @param message Hash of the data that was signed.
         * @param compressed Whether or not the original pubkey was compressed.
         * @return An ECKey containing only the public part, or null if recovery wasn't possible.
         */
        fun recoverFromSignature(
            recId: Int,
            sig: ECDSASignature,
            message: Sha256Hash,
            compressed: Boolean
        ): ECKey? {
            PreconditionsG.checkArgument(recId >= 0, "recId must be positive")
            PreconditionsG.checkArgument(sig.r.signum() >= 0, "r must be positive")
            PreconditionsG.checkArgument(sig.s.signum() >= 0, "s must be positive")
            PreconditionsG.checkNotNull(message)
            // 1.0 For j from 0 to h   (h == recId here and the loop is outside this function)
            //   1.1 Let x = r + jn
            val n = CURVE!!.n // Curve order.
            val i = BigInteger.fromLong(recId.toLong() / 2)
            val x = sig.r.add(i.multiply(n))
            //   1.2. Convert the integer x to an octet string X of length mlen using the conversion routine
            //        specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
            //   1.3. Convert the octet string (16 set binary digits)||X to an elliptic curve point R using the
            //        conversion routine specified in Section 2.3.4. If this conversion routine outputs “invalid”, then
            //        do another iteration of Step 1.
            //
            // More concisely, what these points mean is to use X as a compressed public key.
            val prime = SecP256K1Curve.q
            if (x.compareTo(prime) >= 0) {
                // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
                return null
            }
            // Compressed keys require you to know an extra bit of data about the y-coord as there are two possibilities.
            // So it's encoded in the recId.
            val R = decompressKey(x, recId and 1 == 1)
            //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers responsibility).
            if (!R.multiply(n).isInfinity) return null
            //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
            val e = message.toBigInteger()
            //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via iterating recId)
            //   1.6.1. Compute a candidate public key as:
            //               Q = mi(r) * (sR - eG)
            //
            // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
            //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
            // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n). In the above equation
            // ** is point multiplication and + is point addition (the EC group operator).
            //
            // We can find the additive inverse by subtracting e from zero then taking the mod. For example the additive
            // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod 11 = 8.
            val eInv = BigInteger.ZERO.subtract(e).mod(n)
            val rInv = sig.r.modInverse(n)
            val srInv = rInv.multiply(sig.s).mod(n)
            val eInvrInv = rInv.multiply(eInv).mod(n)
            val q = ECAlgorithms.sumOfTwoMultiplies(CURVE!!.g, eInvrInv, R, srInv)
            return fromPublicOnly(q.getEncoded(compressed))
        }

        /** Decompress a compressed public key (x co-ord and low-bit of y-coord).  */
        private fun decompressKey(xBN: BigInteger, yBit: Boolean): ECPoint {
            val x9 = X9IntegerConverter()
            val compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE!!.curve))
            compEnc[0] = (if (yBit) 0x03 else 0x02).toByte()
            return CURVE!!.curve.decodePoint(compEnc)
        }

        /**
         *
         * Check that it is possible to decrypt the key with the keyCrypter and that the original key is returned.
         *
         *
         * Because it is a critical failure if the private keys cannot be decrypted successfully (resulting of loss of all
         * bitcoins controlled by the private key) you can use this method to check when you *encrypt* a wallet that
         * it can definitely be decrypted successfully.
         *
         *
         * See [example usage.&lt;/p&gt;][true]
         */
//        fun encryptionIsReversible(
//            originalKey: ECKey,
//            encryptedKey: ECKey,
//            keyCrypter: KeyCrypter,
//            aesKey: KeyParameter?
//        ): Boolean {
//            return try {
//                val rebornUnencryptedKey = encryptedKey.decrypt(keyCrypter, aesKey)
//                val originalPrivateKeyBytes = originalKey.privKeyBytes
//                val rebornKeyBytes = rebornUnencryptedKey.privKeyBytes
//                if (!Arrays.equals(originalPrivateKeyBytes, rebornKeyBytes)) {
////                log.error("The check that encryption could be reversed failed for {}", originalKey);
//                    false
//                } else true
//            } catch (kce: KeyCrypterException) {
////            log.error(kce.getMessage());
//                false
//            }
//        }
    }
}

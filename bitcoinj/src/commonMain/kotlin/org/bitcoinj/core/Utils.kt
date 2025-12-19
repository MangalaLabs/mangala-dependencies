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

import com.google.common.kotlin.primitives.IntsG
import com.google.common.kotlin.primitives.UnsignedLongsG
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.mangala.wallet.bitcoinj.utils.shiftRight
import io.ktor.utils.io.core.toByteArray
import kotlin.jvm.JvmStatic
//import okio.IOException
//import java.io.InputStream
//import java.io.OutputStream
//import java.io.UnsupportedEncodingException
//import java.text.DateFormat
//import java.text.SimpleDateFormat
//import java.util.Arrays
//import java.util.Locale
//import java.util.TimeZone
//import java.util.concurrent.ArrayBlockingQueue
import kotlin.math.min

//import com.google.common.base.Charsets;
//import com.google.common.base.Joiner;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Ordering;
//import com.google.common.io.BaseEncoding;
//import com.google.common.io.Resources;
//import com.google.common.io.BaseEncoding;
//import org.spongycastle.crypto.digests.RIPEMD160Digest;
//import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
/**
 * A collection of various utility methods that are helpful for working with the Bitcoin protocol.
 * To enable debug logging from the library, run with -Dbitcoinj.logging=true on your command line.
 */
object Utils {
    /** The string that prefixes all text messages signed using Bitcoin keys.  */
    const val BITCOIN_SIGNED_MESSAGE_HEADER = "Bitcoin Signed Message:\n"

    //    public static final byte[] BITCOIN_SIGNED_MESSAGE_HEADER_BYTES = BITCOIN_SIGNED_MESSAGE_HEADER.getBytes(Charsets.UTF_8);
    //
    //    private static final Joiner SPACE_JOINER = Joiner.on(" ");
//    private var mockSleepQueue: BlockingQueue<Boolean>? = null

    /**
     * The regular [java.math.BigInteger.toByteArray] method isn't quite what we often need: it appends a
     * leading zero to indicate that the number is positive and may need padding.
     *
     * @param b the integer to format into a byte array
     * @param numBytes the desired size of the resulting byte array
     * @return numBytes byte long array.
     */
//    @JvmStatic
//    fun bigIntegerToBytes(b: BigInteger?, numBytes: Int): ByteArray? {
//        if (b == null) {
//            return null
//        }
//        val bytes = ByteArray(numBytes)
//        val biBytes = b.toByteArray()
//        val start = if (biBytes.size == numBytes + 1) 1 else 0
//        val length = min(biBytes.size, numBytes)
//        System.arraycopy(biBytes, start, bytes, numBytes - length, length)
//        return bytes
//    }

    @JvmStatic
    fun bigIntegerToBytes(b: BigInteger?, numBytes: Int): ByteArray? {
        if (b == null) {
            return null
        }
        val bytes = ByteArray(numBytes)
        val biBytes = b.toByteArray()
        val start = if (biBytes.size == numBytes + 1) 1 else 0
        val length = min(biBytes.size, numBytes)
        biBytes.copyInto(bytes, destinationOffset = numBytes - length, startIndex = start, endIndex = start + length)
        return bytes
    }

    fun uint32ToByteArrayBE(`val`: Long, out: ByteArray, offset: Int) {
        out[offset] = (0xFFL and (`val` shr 24)).toByte()
        out[offset + 1] = (0xFFL and (`val` shr 16)).toByte()
        out[offset + 2] = (0xFFL and (`val` shr 8)).toByte()
        out[offset + 3] = (0xFFL and `val`).toByte()
    }

    fun uint32ToByteArrayLE(`val`: Long, out: ByteArray, offset: Int) {
        out[offset] = (0xFFL and `val`).toByte()
        out[offset + 1] = (0xFFL and (`val` shr 8)).toByte()
        out[offset + 2] = (0xFFL and (`val` shr 16)).toByte()
        out[offset + 3] = (0xFFL and (`val` shr 24)).toByte()
    }

    fun uint64ToByteArrayLE(`val`: Long, out: ByteArray, offset: Int) {
        out[offset] = (0xFFL and `val`).toByte()
        out[offset + 1] = (0xFFL and (`val` shr 8)).toByte()
        out[offset + 2] = (0xFFL and (`val` shr 16)).toByte()
        out[offset + 3] = (0xFFL and (`val` shr 24)).toByte()
        out[offset + 4] = (0xFFL and (`val` shr 32)).toByte()
        out[offset + 5] = (0xFFL and (`val` shr 40)).toByte()
        out[offset + 6] = (0xFFL and (`val` shr 48)).toByte()
        out[offset + 7] = (0xFFL and (`val` shr 56)).toByte()
    }

//    @Throws(IOException::class)
//    fun uint32ToByteStreamLE(`val`: Long, stream: OutputStream) {
//        stream.write((0xFFL and `val`).toInt())
//        stream.write((0xFFL and (`val` shr 8)).toInt())
//        stream.write((0xFFL and (`val` shr 16)).toInt())
//        stream.write((0xFFL and (`val` shr 24)).toInt())
//    }
//
//    @Throws(IOException::class)
//    fun int64ToByteStreamLE(`val`: Long, stream: OutputStream) {
//        stream.write((0xFFL and `val`).toInt())
//        stream.write((0xFFL and (`val` shr 8)).toInt())
//        stream.write((0xFFL and (`val` shr 16)).toInt())
//        stream.write((0xFFL and (`val` shr 24)).toInt())
//        stream.write((0xFFL and (`val` shr 32)).toInt())
//        stream.write((0xFFL and (`val` shr 40)).toInt())
//        stream.write((0xFFL and (`val` shr 48)).toInt())
//        stream.write((0xFFL and (`val` shr 56)).toInt())
//    }
//
//    @Throws(IOException::class)
//    fun uint64ToByteStreamLE(`val`: BigInteger, stream: OutputStream) {
//        var bytes = `val`.toByteArray()
//        if (bytes.size > 8) {
//            throw RuntimeException("Input too large to encode into a uint64")
//        }
//        bytes = reverseBytes(bytes)
//        stream.write(bytes)
//        if (bytes.size < 8) {
//            for (i in 0 until 8 - bytes.size) stream.write(0)
//        }
//    }

    /**
     * Work around lack of unsigned types in Java.
     */
    fun isLessThanUnsigned(n1: Long, n2: Long): Boolean {
        return UnsignedLongsG.compare(n1, n2) < 0
    }

    /**
     * Work around lack of unsigned types in Java.
     */
    fun isLessThanOrEqualToUnsigned(n1: Long, n2: Long): Boolean {
        return UnsignedLongsG.compare(n1, n2) <= 0
    }
    /**
     * Hex encoding used throughout the framework. Use with HEX.encode(byte[]) or HEX.decode(CharSequence).
     */
    //    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();
    /**
     * Returns a copy of the given byte array in reverse order.
     */
    fun reverseBytes(bytes: ByteArray): ByteArray {
        // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
        // performance issue the matter can be revisited.
        val buf = ByteArray(bytes.size)
        for (i in bytes.indices) buf[i] = bytes[bytes.size - 1 - i]
        return buf
    }

    /**
     * Returns a copy of the given byte array with the bytes of each double-word (4 bytes) reversed.
     *
     * @param bytes length must be divisible by 4.
     * @param trimLength trim output to this length.  If positive, must be divisible by 4.
     */
//    fun reverseDwordBytes(bytes: ByteArray, trimLength: Int): ByteArray {
//        PreconditionsG.checkArgument(bytes.size % 4 == 0)
//        PreconditionsG.checkArgument(trimLength < 0 || trimLength % 4 == 0)
//        val rev =
//            ByteArray(if (trimLength >= 0 && bytes.size > trimLength) trimLength else bytes.size)
//        var i = 0
//        while (i < rev.size) {
//            System.arraycopy(bytes, i, rev, i, 4)
//            for (j in 0..3) {
//                rev[i + j] = bytes[i + 3 - j]
//            }
//            i += 4
//        }
//        return rev
//    }

    /** Parse 4 bytes from the byte array (starting at the offset) as unsigned 32-bit integer in little endian format.  */
    fun readUint32(bytes: ByteArray, offset: Int): Long {
        return bytes[offset].toLong() and 0xffL or
                (bytes[offset + 1].toLong() and 0xffL shl 8) or
                (bytes[offset + 2].toLong() and 0xffL shl 16) or
                (bytes[offset + 3].toLong() and 0xffL shl 24)
    }

    /** Parse 8 bytes from the byte array (starting at the offset) as signed 64-bit integer in little endian format.  */
    fun readInt64(bytes: ByteArray, offset: Int): Long {
        return bytes[offset].toLong() and 0xffL or
                (bytes[offset + 1].toLong() and 0xffL shl 8) or
                (bytes[offset + 2].toLong() and 0xffL shl 16) or
                (bytes[offset + 3].toLong() and 0xffL shl 24) or
                (bytes[offset + 4].toLong() and 0xffL shl 32) or
                (bytes[offset + 5].toLong() and 0xffL shl 40) or
                (bytes[offset + 6].toLong() and 0xffL shl 48) or
                (bytes[offset + 7].toLong() and 0xffL shl 56)
    }

    /** Parse 4 bytes from the byte array (starting at the offset) as unsigned 32-bit integer in big endian format.  */
    fun readUint32BE(bytes: ByteArray, offset: Int): Long {
        return bytes[offset].toLong() and 0xffL shl 24 or
                (bytes[offset + 1].toLong() and 0xffL shl 16) or
                (bytes[offset + 2].toLong() and 0xffL shl 8) or
                (bytes[offset + 3].toLong() and 0xffL)
    }

    /** Parse 2 bytes from the byte array (starting at the offset) as unsigned 16-bit integer in big endian format.  */
    fun readUint16BE(bytes: ByteArray, offset: Int): Int {
        return bytes[offset].toInt() and 0xff shl 8 or
                (bytes[offset + 1].toInt() and 0xff)
    }
    /**
     * Calculates RIPEMD160(SHA256(input)). This is used in Address calculations.
     */
    //    public static byte[] sha256hash160(byte[] input) {
    //        byte[] sha256 = Sha256Hash.hash(input);
    //        RIPEMD160Digest digest = new RIPEMD160Digest();
    //        digest.update(sha256, 0, sha256.length);
    //        byte[] out = new byte[20];
    //        digest.doFinal(out, 0);
    //        return out;
    //    }
    /**
     * MPI encoded numbers are produced by the OpenSSL BN_bn2mpi function. They consist of
     * a 4 byte big endian length field, followed by the stated number of bytes representing
     * the number in big endian format (with a sign bit).
     * @param hasLength can be set to false if the given array is missing the 4 byte length field
     */
//    fun decodeMPI(mpi: ByteArray, hasLength: Boolean): BigInteger {
//        val buf: ByteArray
//        if (hasLength) {
//            val length = readUint32BE(mpi, 0).toInt()
//            buf = ByteArray(length)
//            System.arraycopy(mpi, 4, buf, 0, length)
//        } else buf = mpi
//        if (buf.size == 0) return BigInteger.ZERO
//        val isNegative = buf[0].toInt() and 0x80 == 0x80
//        if (isNegative) buf[0] = (buf[0].toInt() and 0x7f).toByte()
//        val result = BigInteger.fromByteArray(buf, Sign.POSITIVE)
//        return if (isNegative) result.negate() else result
//    }

    fun decodeMPI(mpi: ByteArray, hasLength: Boolean): BigInteger {
        val buf: ByteArray
        if (hasLength) {
            val length = readUint32BE(mpi, 0).toInt()
            buf = ByteArray(length)
            mpi.copyInto(buf, destinationOffset = 0, startIndex = 4, endIndex = 4 + length)
        } else {
            buf = mpi
        }
        if (buf.isEmpty()) return BigInteger.ZERO
        val isNegative = buf[0].toInt() and 0x80 == 0x80
        if (isNegative) buf[0] = (buf[0].toInt() and 0x7f).toByte()
        val result = BigInteger.fromByteArray(buf, Sign.POSITIVE)
        return if (isNegative) result.negate() else result
    }

    /**
     * MPI encoded numbers are produced by the OpenSSL BN_bn2mpi function. They consist of
     * a 4 byte big endian length field, followed by the stated number of bytes representing
     * the number in big endian format (with a sign bit).
     * @param includeLength indicates whether the 4 byte length field should be included
     */
//    fun encodeMPI(value: BigInteger, includeLength: Boolean): ByteArray {
//        var value = value
//        if (value == BigInteger.ZERO) {
//            return if (!includeLength) byteArrayOf() else byteArrayOf(0x00, 0x00, 0x00, 0x00)
//        }
//        val isNegative = value.signum() < 0
//        if (isNegative) value = value.negate()
//        val array = value.toByteArray()
//        var length = array.size
//        if (array[0].toInt() and 0x80 == 0x80) length++
//        return if (includeLength) {
//            val result = ByteArray(length + 4)
//            System.arraycopy(array, 0, result, length - array.size + 3, array.size)
//            uint32ToByteArrayBE(length.toLong(), result, 0)
//            if (isNegative) result[4] = (result[4].toInt() or 0x80).toByte()
//            result
//        } else {
//            val result: ByteArray
//            if (length != array.size) {
//                result = ByteArray(length)
//                System.arraycopy(array, 0, result, 1, array.size)
//            } else result = array
//            if (isNegative) result[0] = (result[0].toInt() or 0x80).toByte()
//            result
//        }
//    }

    /**
     *
     * The "compact" format is a representation of a whole number N using an unsigned 32 bit number similar to a
     * floating point format. The most significant 8 bits are the unsigned exponent of base 256. This exponent can
     * be thought of as "number of bytes of N". The lower 23 bits are the mantissa. Bit number 24 (0x800000) represents
     * the sign of N. Therefore, N = (-1^sign) * mantissa * 256^(exponent-3).
     *
     *
     * Satoshi's original implementation used BN_bn2mpi() and BN_mpi2bn(). MPI uses the most significant bit of the
     * first byte as sign. Thus 0x1234560000 is compact 0x05123456 and 0xc0de000000 is compact 0x0600c0de. Compact
     * 0x05c0de00 would be -0x40de000000.
     *
     *
     * Bitcoin only uses this "compact" format for encoding difficulty targets, which are unsigned 256bit quantities.
     * Thus, all the complexities of the sign bit and using base 256 are probably an implementation accident.
     */
    fun decodeCompactBits(compact: Long): BigInteger {
        val size = (compact shr 24).toInt() and 0xFF
        val bytes = ByteArray(4 + size)
        bytes[3] = size.toByte()
        if (size >= 1) bytes[4] = (compact shr 16 and 0xFFL).toByte()
        if (size >= 2) bytes[5] = (compact shr 8 and 0xFFL).toByte()
        if (size >= 3) bytes[6] = (compact and 0xFFL).toByte()
        return decodeMPI(bytes, true)
    }

    /**
     * @see Utils.decodeCompactBits
     */
    fun encodeCompactBits(value: BigInteger): Long {
        var result: Long
        var size = value.toByteArray().size
        result =
            if (size <= 3) value.longValue() shl 8 * (3 - size) else value.shiftRight(8 * (size - 3))
                .longValue()
        // The 0x00800000 bit denotes the sign.
        // Thus, if it is already set, divide the mantissa by 256 and increase the exponent.
        if (result and 0x00800000L != 0L) {
            result = result shr 8
            size++
        }
        result = result or (size shl 24).toLong()
        result = result or (if (value.signum() == -1) 0x00800000 else 0).toLong()
        return result
    }

    /**
     * If non-null, overrides the return value of now().
     */
//    @Volatile
//    var mockTime: Date? = null

    /**
     * Advances (or rewinds) the mock clock by the given number of seconds.
     */
//    fun rollMockClock(seconds: Int): Date? {
//        return rollMockClockMillis((seconds * 1000).toLong())
//    }

    /**
     * Advances (or rewinds) the mock clock by the given number of milliseconds.
     */
//    fun rollMockClockMillis(millis: Long): Date? {
//        checkNotNull(mockTime) { "You need to use setMockClock() first." }
//        mockTime = Date(mockTime!!.time + millis)
//        return mockTime
//    }

    /**
     * Sets the mock clock to the current time.
     */
//    fun setMockClock() {
//        mockTime = Date()
//    }

    /**
     * Sets the mock clock to the given time (in seconds).
     */
//    fun setMockClock(mockClockSeconds: Long) {
//        mockTime = Date(mockClockSeconds * 1000)
//    }

    /**
     * Returns the current time, or a mocked out equivalent.
     */
//    fun now(): Date? {
//        return if (mockTime != null) mockTime else Date()
//    }
    // TODO: Replace usages of this where the result is / 1000 with currentTimeSeconds.
    /** Returns the current time in milliseconds since the epoch, or a mocked out equivalent.  */
//    fun currentTimeMillis(): Long {
//        return if (mockTime != null) mockTime!!.time else System.currentTimeMillis()
//    }

//    @JvmStatic
//    fun currentTimeSeconds(): Long {
//        return currentTimeMillis() / 1000
//    }

//    private val UTC = TimeZone.getTimeZone("UTC")

    /**
     * Formats a given date+time value to an ISO 8601 string.
     * @param dateTime value to format, as a Date
     */
//    fun dateTimeFormat(dateTime: Date?): String {
//        val iso8601: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
//        iso8601.timeZone = UTC
//        return iso8601.format(dateTime)
//    }

    /**
     * Formats a given date+time value to an ISO 8601 string.
     * @param dateTime value to format, unix time (ms)
     */
//    fun dateTimeFormat(dateTime: Long): String {
//        val iso8601: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
//        iso8601.timeZone = UTC
//        return iso8601.format(dateTime)
//    }

    //    /**
    //     * Returns a string containing the string representation of the given items,
    //     * delimited by a single space character.
    //     *
    //     * @param items the items to join
    //     * @param <T> the item type
    //     * @return the joined space-delimited string
    //     */
    //    public static <T> String join(Iterable<T> items) {
    //        return SPACE_JOINER.join(items);
    //    }
//    fun copyOf(`in`: ByteArray, length: Int): ByteArray {
//        val out = ByteArray(length)
//        System.arraycopy(`in`, 0, out, 0, min(length, `in`.size))
//        return out
//    }

    fun copyOf(`in`: ByteArray, length: Int): ByteArray {
        val out = ByteArray(length)
        `in`.copyInto(out, destinationOffset = 0, startIndex = 0, endIndex = min(length, `in`.size))
        return out
    }


    /**
     * Creates a copy of bytes and appends b to the end of it
     */
//    fun appendByte(bytes: ByteArray, b: Byte): ByteArray {
//        val result = Arrays.copyOf(bytes, bytes.size + 1)
//        result[result.size - 1] = b
//        return result
//    }

    /**
     * Constructs a new String by decoding the given bytes using the specified charset.
     *
     *
     * This is a convenience method which wraps the checked exception with a RuntimeException.
     * The exception can never occur given the charsets
     * US-ASCII, ISO-8859-1, UTF-8, UTF-16, UTF-16LE or UTF-16BE.
     *
     * @param bytes the bytes to be decoded into characters
     * @param charsetName the name of a supported [charset][java.nio.charset.Charset]
     * @return the decoded String
     */
//    fun toString(bytes: ByteArray?, charsetName: String?): String {
//        return try {
//            String(bytes!!, charset(charsetName!!))
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//    }

    /**
     * Encodes the given string into a sequence of bytes using the named charset.
     *
     *
     * This is a convenience method which wraps the checked exception with a RuntimeException.
     * The exception can never occur given the charsets
     * US-ASCII, ISO-8859-1, UTF-8, UTF-16, UTF-16LE or UTF-16BE.
     *
     * @param str the string to encode into bytes
     * @param charsetName the name of a supported [charset][java.nio.charset.Charset]
     * @return the encoded bytes
     */
//    fun toBytes(str: CharSequence, charsetName: String?): ByteArray {
//        return try {
//            str.toString().toByteArray(charset(charsetName!!))
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//    }

//    val isWindows: Boolean
//        /**
//         * Attempts to parse the given string as arbitrary-length hex or base58 and then return the results, or null if
//         * neither parse was successful.
//         */
//        get() = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")

    /**
     *
     * Given a textual message, returns a byte buffer formatted as follows:
     *
     * <tt>
     *
     *[24] "Bitcoin Signed Message:\n" [message.length as a varint] message</tt>
     */
    //    public static byte[] formatMessageForSigning(String message) {
    //        try {
    //            ByteArrayOutputStream bos = new ByteArrayOutputStream();
    //            bos.write(BITCOIN_SIGNED_MESSAGE_HEADER_BYTES.length);
    //            bos.write(BITCOIN_SIGNED_MESSAGE_HEADER_BYTES);
    //            byte[] messageBytes = message.getBytes(Charsets.UTF_8);
    //            VarInt size = new VarInt(messageBytes.length);
    //            bos.write(size.encode());
    //            bos.write(messageBytes);
    //            return bos.toByteArray();
    //        } catch (IOException e) {
    //            throw new RuntimeException(e);  // Cannot happen.
    //        }
    //    }
    // 00000001, 00000010, 00000100, 00001000, ...
    private val bitMask = intArrayOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80)

    /** Checks if the given bit is set in data, using little endian (not the same as Java native big endian)  */
    fun checkBitLE(data: ByteArray, index: Int): Boolean {
        return data[index ushr 3].toInt() and bitMask[7 and index] != 0
    }

    /** Sets the given bit in data to one, using little endian (not the same as Java native big endian)  */
    fun setBitLE(data: ByteArray, index: Int) {
        data[index ushr 3] = (data[index ushr 3].toInt() or bitMask[7 and index]).toByte()
    }
    /** Sleep for a span of time, or mock sleep if enabled  */ //    public static void sleep(long millis) {
    //        if (mockSleepQueue == null) {
    //            sleepUninterruptibly(millis, TimeUnit.MILLISECONDS);
    //        } else {
    //            try {
    //                boolean isMultiPass = mockSleepQueue.take();
    //                rollMockClockMillis(millis);
    //                if (isMultiPass)
    //                    mockSleepQueue.offer(true);
    //            } catch (InterruptedException e) {
    //                // Ignored.
    //            }
    //        }
    //    }
    /** Enable or disable mock sleep.  If enabled, set mock time to current time.  */
//    fun setMockSleep(isEnable: Boolean) {
//        if (isEnable) {
//            mockSleepQueue = ArrayBlockingQueue(1)
//            mockTime = Date(System.currentTimeMillis())
//        } else {
//            mockSleepQueue = null
//        }
//    }

    /** Let sleeping thread pass the synchronization point.   */
//    fun passMockSleep() {
//        mockSleepQueue!!.offer(false)
//    }
//
//    /** Let the sleeping thread pass the synchronization point any number of times.  */
//    fun finishMockSleep() {
//        if (mockSleepQueue != null) {
//            mockSleepQueue!!.offer(true)
//        }
//    }

//    private var isAndroid = -1
//    @JvmStatic
//    val isAndroidRuntime: Boolean
//        get() {
//            if (isAndroid == -1) {
//                val runtime = System.getProperty("java.runtime.name")
//                isAndroid = if (runtime != null && runtime == "Android Runtime") 1 else 0
//            }
//            return isAndroid == 1
//        }

    //    public static int maxOfMostFreq(int... items) {
    //        // Java 6 sucks.
    //        ArrayList<Integer> list = new ArrayList<Integer>(items.length);
    //        for (int item : items) list.add(item);
    //        return maxOfMostFreq(list);
    //    }
    //    public static int maxOfMostFreq(List<Integer> items) {
    //        if (items.isEmpty())
    //            return 0;
    //        // This would be much easier in a functional language (or in Java 8).
    //        items = Ordering.natural().reverse().sortedCopy(items);
    //        LinkedList<Pair> pairs = Lists.newLinkedList();
    //        pairs.add(new Pair(items.get(0), 0));
    //        for (int item : items) {
    //            Pair pair = pairs.getLast();
    //            if (pair.item != item)
    //                pairs.add((pair = new Pair(item, 0)));
    //            pair.count++;
    //        }
    //        // pairs now contains a uniqified list of the sorted inputs, with counts for how often that item appeared.
    //        // Now sort by how frequently they occur, and pick the max of the most frequent.
    //        Collections.sort(pairs);
    //        int maxCount = pairs.getFirst().count;
    //        int maxItem = pairs.getFirst().item;
    //        for (Pair pair : pairs) {
    //            if (pair.count != maxCount)
    //                break;
    //            maxItem = max(maxItem, pair.item);
    //        }
    //        return maxItem;
    //    }
    //
    //    /**
    //     * Reads and joins together with LF char (\n) all the lines from given file. It's assumed that file is in UTF-8.
    //     */
    //    public static String getResourceAsString(URL url) throws IOException {
    //        List<String> lines = Resources.readLines(url, Charsets.UTF_8);
    //        return Joiner.on('\n').join(lines);
    //    }
    // Can't use Closeable here because it's Java 7 only and Android devices only got that with KitKat.
//    fun closeUnchecked(stream: InputStream): InputStream {
//        return try {
//            stream.close()
//            stream
//        } catch (e: IOException) {
//            throw RuntimeException(e)
//        }
//    }
//
//    fun closeUnchecked(stream: OutputStream): OutputStream {
//        return try {
//            stream.close()
//            stream
//        } catch (e: IOException) {
//            throw RuntimeException(e)
//        }
//    }

    private class Pair(var item: Int, var count: Int) : Comparable<Pair> {
        // note that in this implementation compareTo() is not consistent with equals()
        override fun compareTo(o: Pair): Int {
            return -IntsG.compare(count, o.count)
        }
    }
}

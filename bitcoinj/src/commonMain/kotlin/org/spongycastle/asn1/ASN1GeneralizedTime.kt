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

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.offsetAt
import org.spongycastle.util.Arrays.areEqual
import org.spongycastle.util.Arrays.hashCode
import org.spongycastle.util.Strings
import okio.IOException
import kotlinx.datetime.format.*

/**
 * Base class representing the ASN.1 GeneralizedTime type.
 *
 *
 * The main difference between these and UTC time is a 4 digit year.
 *
 *
 *
 * One second resolution date+time on UTC timezone (Z)
 * with 4 digit year (valid from 0001 to 9999).
 *
 *
 * Timestamp format is:  yyyymmddHHMMSS'Z'
 *
 *
 * <h2>X.690</h2>
 * This is what is called "restricted string",
 * and it uses ASCII characters to encode digits and supplemental data.
 *
 * <h3>11: Restrictions on BER employed by both CER and DER</h3>
 * <h4>11.7 GeneralizedTime </h4>
 * **11.7.1** The encoding shall terminate with a "Z",
 * as described in the ITU-T Rec. X.680 | ISO/IEC 8824-1 clause on
 * GeneralizedTime.
 *
 *
 * **11.7.2** The seconds element shall always be present.
 *
 *
 *
 * **11.7.3** The fractional-seconds elements, if present,
 * shall omit all trailing zeros; if the elements correspond to 0,
 * they shall be wholly omitted, and the decimal point element also
 * shall be omitted.
 *
 */
@OptIn(FormatStringsInDatetimeFormats::class)
class ASN1GeneralizedTime : ASN1Primitive {
    private var time: ByteArray

//    /**
//     * The correct format for this is YYYYMMDDHHMMSS[.f]Z, or without the Z
//     * for local time, or Z+-HHMM on the end, for difference between local
//     * time and UTC time. The fractional second amount f must consist of at
//     * least one number with trailing zeroes removed.
//     *
//     * @param time the time string.
//     * @throws IllegalArgumentException if String is an illegal format.
//     */
//    constructor(
//        time: String
//    ) {
//        this.time = toByteArray(time!!)
//        try {
//            date
//        } catch (e: ParseException) {
//            throw IllegalArgumentException("invalid date string: " + e.message)
//        }
//    }

//    /**
//     * Base constructor from a java.util.date object
//     *
//     * @param time a date object representing the time of interest.
//     */
//    constructor(
//        time: Date
//    ) {
//        val dateF = SimpleDateFormat("yyyyMMddHHmmss'Z'")
//        dateF.timeZone = SimpleTimeZone(0, "Z")
//        this.time = toByteArray(dateF.format(time))
//    }

//    /**
//     * Base constructor from a java.util.date and Locale - you may need to use this if the default locale
//     * doesn't use a Gregorian calender so that the GeneralizedTime produced is compatible with other ASN.1 implementations.
//     *
//     * @param time a date object representing the time of interest.
//     * @param locale an appropriate Locale for producing an ASN.1 GeneralizedTime value.
//     */
//    constructor(
//        time: Date,
//        locale: Locale
//    ) {
//        val dateF = SimpleDateFormat("yyyyMMddHHmmss'Z'", locale)
//        dateF.timeZone = SimpleTimeZone(0, "Z")
//        this.time = toByteArray(dateF.format(time))
//    }

    internal constructor(
        bytes: ByteArray
    ) {
        time = bytes
    }

    val timeString: String
        /**
         * Return the time.
         *
         * @return The time string as it appeared in the encoded object.
         */
        get() = Strings.fromByteArray(time)

    /**
     * return the time - always in the form of
     * YYYYMMDDhhmmss(+hh:mm|-hh:mm).
     *
     *
     * Normally in a certificate we would expect "Z" rather than "GMT",
     * however adding the "GMT" means we can just use:
     * <pre>
     * dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
    </pre> *
     * To read in the time and get a date which is compatible with our local
     * time zone.
     *
     * @return a String representation of the time.
     */
    fun getTime(): String {
        val stime = Strings.fromByteArray(time)

        //
        // standardise the format.
        //
        if (stime[stime.length - 1] == 'Z') {
            return stime.substring(0, stime.length - 1) + "+00:00"
        } else {
            var signPos = stime.length - 5
            var sign = stime[signPos]
            if (sign == '-' || sign == '+') {
                return (stime.substring(0, signPos)
                        + stime.substring(signPos, signPos + 3)
                        + ":"
                        + stime.substring(signPos + 3))
            } else {
                signPos = stime.length - 3
                sign = stime[signPos]
                if (sign == '-' || sign == '+') {
                    return (stime.substring(0, signPos)
                            + stime.substring(signPos)
                            + ":00")
                }
            }
        }
        return stime + calculateGMTOffset()
    }

    fun calculateGMTOffset(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
        var sign = "+"
        var offset = timeZone.offsetAt(date).totalSeconds.toLong() * 1000
        if (offset < 0) {
            sign = "-"
            offset = -offset
        }
        val hours = offset / (60 * 60 * 1000)
        val minutes = (offset - hours * 60 * 60 * 1000) / (60 * 1000)
        return "GMT" + sign + convert(hours) + ":" + convert(minutes)
    }

    private fun convert(time: Long): String {
        return if (time < 10) {
            "0$time"
        } else time.toString()
    }

    val date: Instant
        get() {
            val stime = Strings.fromByteArray(time) // Assuming `time` is a ByteArray
            var d = stime
            val hasFractionalSeconds = hasFractionalSeconds() // This needs to be defined somewhere
            var hasTimeZoneInfo = false

            val patternString = if (stime.endsWith("Z")) {
                if (hasFractionalSeconds) {
                    "yyyyMMddHHmmss.SSS'Z"
                } else {
                    "yyyyMMddHHmmss'Z'"
                }
            } else if (stime.indexOf('-') > 0 || stime.indexOf('+') > 0) {
                d = getTime()
                hasTimeZoneInfo = true
                if (hasFractionalSeconds) {
                    "yyyyMMddHHmmss.SSS"
                } else {
                    "yyyyMMddHHmmss"
                }
            } else {
                if (hasFractionalSeconds) {
                    "yyyyMMddHHmmss.SSS"
                } else {
                    "yyyyMMddHHmmss"
                }
            }
            val useUTC = stime.endsWith("Z") || stime.indexOf('-') > 0 || stime.indexOf('+') > 0

            if (hasFractionalSeconds()) {
                var frac = d.substring(14)
                var index: Int
                index = 1
                while (index < frac.length) {
                    val ch = frac[index]
                    if (!('0' <= ch && ch <= '9')) {
                        break
                    }
                    index++
                }
                if (index - 1 > 3) {
                    frac = frac.substring(0, 4) + frac.substring(index)
                    d = d.substring(0, 14) + frac
                } else if (index - 1 == 1) {
                    frac = frac.substring(0, index) + "00" + frac.substring(index)
                    d = d.substring(0, 14) + frac
                } else if (index - 1 == 2) {
                    frac = frac.substring(0, index) + "0" + frac.substring(index)
                    d = d.substring(0, 14) + frac
                }
            }

            // Parse to LocalDateTime, assuming all dates are in UTC for simplicity
            val localDateTime = Instant.parse(d, DateTimeComponents.Format {
                byUnicodePattern(patternString)
                if (hasTimeZoneInfo) {
                    alternativeParsing({
                        offsetHours()
                    }) {
                        offset(UtcOffset.Formats.ISO)
                    }
                }
            })

            // If the original time string used 'Z', assume UTC, otherwise use system default
            val timeZone = if (useUTC) TimeZone.UTC else TimeZone.currentSystemDefault()
            val instant = localDateTime

            // Return LocalDateTime
            return instant
        }

    private fun hasFractionalSeconds(): Boolean {
        for (i in time.indices) {
            if (time[i] == '.'.code.toByte()) {
                if (i == 14) {
                    return true
                }
            }
        }
        return false
    }

    override fun isConstructed(): Boolean {
        return false
    }

    override fun encodedLength(): Int {
        val length = time.size
        return 1 + StreamUtil.calculateBodyLength(length) + length
    }

    @Throws(IOException::class)
    override fun encode(
        out: ASN1OutputStream
    ) {
        out.writeEncoded(BERTags.GENERALIZED_TIME, time)
    }

    override fun asn1Equals(
        o: ASN1Primitive
    ): Boolean {
        return if (o !is ASN1GeneralizedTime) {
            false
        } else areEqual(time, o.time)
    }

    override fun hashCode(): Int {
        return hashCode(time)
    }

    companion object {
        /**
         * return a generalized time from the passed in object
         *
         * @param obj an ASN1GeneralizedTime or an object that can be converted into one.
         * @return an ASN1GeneralizedTime instance, or null.
         * @throws IllegalArgumentException if the object cannot be converted.
         */
        fun getInstance(
            obj: Any
        ): ASN1GeneralizedTime {
            if (obj is ASN1GeneralizedTime) {
                return obj
            }
            if (obj is ByteArray) {
                return try {
                    fromByteArray((obj as ByteArray?)!!) as ASN1GeneralizedTime
                } catch (e: Exception) {
                    throw IllegalArgumentException("encoding error in getInstance: $e")
                }
            }
            throw IllegalArgumentException("illegal object in getInstance: " + obj::class.simpleName)
        }

        /**
         * return a Generalized Time object from a tagged object.
         *
         * @param obj      the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         * tagged false otherwise.
         * @return an ASN1GeneralizedTime instance.
         * @throws IllegalArgumentException if the tagged object cannot
         * be converted.
         */
        fun getInstance(
            obj: ASN1TaggedObject,
            explicit: Boolean
        ): ASN1GeneralizedTime? {
            val o = obj.getObject()
            return if (explicit || o is ASN1GeneralizedTime) {
                getInstance(o)
            } else {
                ASN1GeneralizedTime((o as ASN1OctetString).octets)
            }
        }
    }
}

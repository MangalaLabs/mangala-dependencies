/*
 * ORIGINAL COPYRIGHT:
 * Copyright (c) 2019-2023 AlphaWallet
 * Licensed under the MIT License (MIT).
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
 * ----------------------------------------------------------------
 * SOURCE:
 * Derived from: https://github.com/AlphaWallet/alpha-wallet-android
 *
 * ----------------------------------------------------------------
 * MODIFICATIONS:
 * Modified by Mangala Wallet for Kotlin Multiplatform compatibility.
 * ----------------------------------------------------------------
 */

package com.alphawallet.token.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Ethereum unit conversion functions.
 */
public final class Convert {
    private Convert() { }

    public static BigDecimal fromWei(String number, Unit unit) {
        return fromWei(new BigDecimal(number), unit);
    }

    public static BigDecimal fromWei(BigDecimal number, Unit unit) {
        return number.divide(unit.getWeiFactor());
    }

    public static BigDecimal toWei(String number, Unit unit) {
        return toWei(new BigDecimal(number), unit);
    }

    public static BigDecimal toWei(BigDecimal number, Unit unit) {
        return number.multiply(unit.getWeiFactor());
    }

    public enum Unit {
        WEI("wei", 0),
        KWEI("kwei", 3),
        MWEI("mwei", 6),
        GWEI("gwei", 9),
        SZABO("szabo", 12),
        FINNEY("finney", 15),
        ETHER("ether", 18),
        KETHER("kether", 21),
        METHER("mether", 24),
        GETHER("gether", 27);

        private final String name;
        private final BigDecimal weiFactor;
        private final int factor;

        Unit(String name, int factor) {
            this.name = name;
            this.weiFactor = BigDecimal.TEN.pow(factor);
            this.factor = factor;
        }

        public BigDecimal getWeiFactor() {
            return weiFactor;
        }
        public int getFactor() { return factor; }

        @Override
        public String toString() {
            return name;
        }

        public static Unit fromString(String name) {
            if (name != null) {
                for (Unit unit : Unit.values()) {
                    if (name.equalsIgnoreCase(unit.name)) {
                        return unit;
                    }
                }
            }
            return Unit.valueOf(name);
        }
    }

    public static String getEthString(double ethPrice)
    {
        DecimalFormat df = new DecimalFormat("0.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(ethPrice);
    }

    public static String getEthString(double ethFiatValue, int decimals)
    {
        DecimalFormat df = new DecimalFormat("0.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        df.setMaximumFractionDigits(decimals);
        return df.format(ethFiatValue);
    }

    public static String getConvertedValue(BigDecimal rawValue, int divisor)
    {
        BigDecimal convertedValue = rawValue.divide(new BigDecimal(Math.pow(10, divisor)));
        DecimalFormat df = new DecimalFormat("0.#####");
        df.setRoundingMode(RoundingMode.HALF_DOWN);
        return df.format(convertedValue);
    }

    public static String getEthStringSzabo(BigInteger szabo)
    {
        BigDecimal ethPrice = fromWei(toWei(new BigDecimal(szabo), Unit.SZABO), Unit.ETHER);
        DecimalFormat df = new DecimalFormat("0.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(ethPrice);
    }
}

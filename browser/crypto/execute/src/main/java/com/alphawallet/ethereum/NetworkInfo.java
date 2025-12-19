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

package com.alphawallet.ethereum;

/* it's some kind of Trust Ethereum Wallet naming convention that a
 * class that has no behaviour (equivalent of C's struct) is called
 * SomethingInfo. I didn't agree with this naming convention but I'll
 * keep it here */

public class NetworkInfo {
    public final String name;
    public final String symbol;
    public final String rpcServerUrl;
    public final String etherscanUrl; // This is used by the Transaction Detail page for the user to visit a website with detailed transaction information
    public final long chainId;
    public final boolean isCustom;

    public NetworkInfo(
            String name,
            String symbol,
            String rpcServerUrl,
            String etherscanUrl,
            long chainId,
            boolean isCustom) {
        this.name = name;
        this.symbol = symbol;
        this.rpcServerUrl = rpcServerUrl;
        this.etherscanUrl = etherscanUrl;
        this.chainId = chainId;
        this.isCustom = isCustom;
    }

}

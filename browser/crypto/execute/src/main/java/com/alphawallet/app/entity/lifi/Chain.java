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

package com.alphawallet.app.entity.lifi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chain
{
    @SerializedName("key")
    @Expose
    public String key;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("coin")
    @Expose
    public String coin;

    @SerializedName("id")
    @Expose
    public long id;

    @SerializedName("mainnet")
    @Expose
    public String mainnet;

    @SerializedName("logoURI")
    @Expose
    public String logoURI;

    @SerializedName("tokenlistUrl")
    @Expose
    public String tokenlistUrl;

    @SerializedName("multicallAddress")
    @Expose
    public String multicallAddress;

    @SerializedName("metamask")
    @Expose
    public Metamask metamask;

    public String balance;

    public static class Metamask
    {
        @SerializedName("chainId")
        @Expose
        public String chainId;

        @SerializedName("blockExplorerUrls")
        @Expose
        public List<String> blockExplorerUrls;

        @SerializedName("chainName")
        @Expose
        public String chainName;

        @SerializedName("nativeCurrency")
        @Expose
        public NativeCurrency nativeCurrency;

        @SerializedName("rpcUrls")
        @Expose
        public List<String> rpcUrls;

        public static class NativeCurrency
        {
            @SerializedName("name")
            @Expose
            public String name;

            @SerializedName("symbol")
            @Expose
            public String symbol;

            @SerializedName("decimals")
            @Expose
            public long decimals;
        }
    }
}

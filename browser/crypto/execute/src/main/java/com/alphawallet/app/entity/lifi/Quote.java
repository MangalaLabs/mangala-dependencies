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

public class Quote
{
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("tool")
    @Expose
    public String tool;

    @SerializedName("action")
    @Expose
    public Action action;

    public static class Action
    {
        @SerializedName("fromChainId")
        @Expose
        public long fromChainId;

        @SerializedName("toChainId")
        @Expose
        public long toChainId;

        @SerializedName("fromToken")
        @Expose
        public Connection.LToken fromToken;

        @SerializedName("toToken")
        @Expose
        public Connection.LToken toToken;

        @SerializedName("fromAmount")
        @Expose
        public String fromAmount;

        @SerializedName("slippage")
        @Expose
        public double slippage;

        @SerializedName("fromAddress")
        @Expose
        public String fromAddress;

        @SerializedName("toAddress")
        @Expose
        public String toAddress;
    }

    @SerializedName("estimate")
    @Expose
    public Estimate estimate;

    public static class Estimate
    {
        @SerializedName("fromAmount")
        @Expose
        public String fromAmount;

        @SerializedName("toAmount")
        @Expose
        public String toAmount;

        @SerializedName("toAmountMin")
        @Expose
        public String toAmountMin;

        @SerializedName("approvalAddress")
        @Expose
        public String approvalAddress;

        @SerializedName("executionDuration")
        @Expose
        public long executionDuration;

//        @SerializedName("feeCosts")
//        @Expose
//        public JSONArray feeCosts;
//
//        @SerializedName("gasCosts")
//        @Expose
//        public JSONArray gasCosts;

        @SerializedName("data")
        @Expose
        public Data data;

        public static class Data
        {
            @SerializedName("blockNumber")
            @Expose
            public long blockNumber;

            @SerializedName("network")
            @Expose
            public long network;

            @SerializedName("srcToken")
            @Expose
            public String srcToken;

            @SerializedName("srcDecimals")
            @Expose
            public long srcDecimals;

            @SerializedName("srcAmount")
            @Expose
            public String srcAmount;

            @SerializedName("destToken")
            @Expose
            public String destToken;

            @SerializedName("destDecimals")
            @Expose
            public long destDecimals;

            @SerializedName("destAmount")
            @Expose
            public String destAmount;

            @SerializedName("gasCostUSD")
            @Expose
            public String gasCostUSD;

            @SerializedName("gasCost")
            @Expose
            public String gasCost;

            @SerializedName("buyAmount")
            @Expose
            public String buyAmount;

            @SerializedName("sellAmount")
            @Expose
            public String sellAmount;
        }

        @SerializedName("fromAmountUSD")
        @Expose
        public String fromAmountUSD;

        @SerializedName("toAmountUSD")
        @Expose
        public String toAmountUSD;
    }

    @SerializedName("transactionRequest")
    @Expose
    public TransactionRequest transactionRequest;

    public static class TransactionRequest
    {
        @SerializedName("from")
        @Expose
        public String from;

        @SerializedName("to")
        @Expose
        public String to;

        @SerializedName("chainId")
        @Expose
        public long chainId;

        @SerializedName("data")
        @Expose
        public String data;

        @SerializedName("value")
        @Expose
        public String value;

        @SerializedName("gasLimit")
        @Expose
        public String gasLimit;

        @SerializedName("gasPrice")
        @Expose
        public String gasPrice;
    }
}

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

package com.alphawallet.app.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by James on 25/02/2019.
 * Stormbird in Singapore
 */
public class EIP681Request
{
    private final String PROTOCOL = "ethereum:";
    private final String address;
    private final BigDecimal weiAmount;
    private final long chainId;
    private String contractAddress;

    public EIP681Request(String displayAddress, long chainId, BigDecimal weiAmount)
    {
        this.address = displayAddress;
        this.chainId = chainId;
        this.weiAmount = weiAmount;
    }

    public EIP681Request(String userAddress, String contractAddress, long chainId, BigDecimal weiAmount)
    {
        this.address = userAddress;
        this.chainId = chainId;
        this.weiAmount = weiAmount;
        this.contractAddress = contractAddress;
    }

    public String generateRequest()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(PROTOCOL);
        sb.append(address);
        sb.append("@");
        sb.append(chainId);
        sb.append("?value=");
        sb.append(format(weiAmount));

        return sb.toString();
    }

    private String format(BigDecimal x)
    {
        NumberFormat formatter = new DecimalFormat("0.#E0");
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMaximumFractionDigits(6);
        return formatter.format(x).replace(",", ".");
    }

    public String generateERC20Request()
    {
        //ethereum:0x744d70fdbe2ba4cf95131626614a1763df805b9e/transfer?address=0x3d597789ea16054a084ac84ce87f50df9198f415&uint256=314e17
        StringBuilder sb = new StringBuilder();
        sb.append(PROTOCOL);
        sb.append(contractAddress);
        sb.append("@");
        sb.append(chainId);
        sb.append("/transfer?address=");
        sb.append(address);
        sb.append("?uint256=");
        sb.append(format(weiAmount));

        return sb.toString();
    }
}

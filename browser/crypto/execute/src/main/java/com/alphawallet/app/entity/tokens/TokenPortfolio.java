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

package com.alphawallet.app.entity.tokens;

public class TokenPortfolio {
    private String balance;
    private String returns;
    private String profit24Hrs;
    private String profitTotal;
    private String share;
    private String averageCost;
    private String fees;

    public String getBalance()
    {
        return balance;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public String getReturns()
    {
        return returns;
    }

    public void setReturns(String returns)
    {
        this.returns = returns;
    }

    public String getProfit24Hrs()
    {
        return profit24Hrs;
    }

    public void setProfit24Hrs(String profit24Hrs)
    {
        this.profit24Hrs = profit24Hrs;
    }

    public String getProfitTotal()
    {
        return profitTotal;
    }

    public void setProfitTotal(String profitTotal)
    {
        this.profitTotal = profitTotal;
    }

    public String getShare()
    {
        return share;
    }

    public void setShare(String share)
    {
        this.share = share;
    }

    public String getAverageCost()
    {
        return averageCost;
    }

    public void setAverageCost(String averageCost)
    {
        this.averageCost = averageCost;
    }

    public String getFees()
    {
        return fees;
    }

    public void setFees(String fees)
    {
        this.fees = fees;
    }
}

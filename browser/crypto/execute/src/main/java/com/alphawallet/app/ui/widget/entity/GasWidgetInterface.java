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

package com.alphawallet.app.ui.widget.entity;

import com.alphawallet.app.web3.entity.Web3Transaction;
import com.alphawallet.app.widget.ActionSheetMode;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by JB on 23/03/2022.
 *
 * This might be better done using a common class containing these functions which the 1559 and legacy widgets derive from
 * All common code can go into this class.
 */
public interface GasWidgetInterface
{
    boolean isSendingAll(Web3Transaction tx);
    BigInteger getValue();
    BigInteger getGasPrice(BigInteger defaultPrice);
    BigInteger getGasLimit();
    long getNonce();
    BigInteger getGasMax();
    BigInteger getPriorityFee();
    BigInteger getGasPrice();
    void setGasEstimate(BigInteger estimate);
    void onDestroy();
    boolean checkSufficientGas();
    void setupResendSettings(ActionSheetMode mode, BigInteger gasPrice);
    void setCurrentGasIndex(int gasSelectionIndex, BigInteger maxFeePerGas, BigInteger maxPriorityFee, BigDecimal customGasLimit, long expectedTxTime, long customNonce);
    long getExpectedTransactionTime();
}

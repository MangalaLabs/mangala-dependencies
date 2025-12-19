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

package com.alphawallet.app.widget;

/**
 * Created by JB on 15/01/2021.
 */

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.ActionSheetInterface;
import com.alphawallet.app.entity.Transaction;
import com.alphawallet.app.entity.TransactionInput;
import com.alphawallet.app.web3.entity.Web3Transaction;

import org.jetbrains.annotations.NotNull;

public class TransactionDetailWidget extends LinearLayout
{
    private final TextView textTransactionSummary;
    private final TextView textFullDetails;
    private final LinearLayout layoutDetails;
    private final LinearLayout layoutHolder;
    private final LinearLayout layoutSummary;
    private ActionSheetInterface sheetInterface;

    public TransactionDetailWidget(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        inflate(context, R.layout.transaction_detail_widget, this);
        textTransactionSummary = findViewById(R.id.text_transaction_summary);
        textFullDetails = findViewById(R.id.text_full_details);
        layoutDetails = findViewById(R.id.layout_detail);
        layoutHolder = findViewById(R.id.layout_holder);
        layoutSummary = findViewById(R.id.layout_summary);
    }

    public void setupTransaction(Web3Transaction w3tx, long chainId, String walletAddress, String symbol,
                                 @NotNull ActionSheetInterface asIf)
    {
        layoutHolder.setVisibility(View.VISIBLE);
        textFullDetails.setText(w3tx.getFormattedTransaction(getContext(), chainId, symbol));
        sheetInterface = asIf;

        if (!TextUtils.isEmpty(w3tx.description))
        {
            textTransactionSummary.setText(w3tx.description);
        }
        else
        {
            TransactionInput transactionInput = Transaction.decoder.decodeInput(w3tx, chainId, walletAddress);
            textTransactionSummary.setText(transactionInput.getOperationTitle(getContext()));
        }

        layoutHolder.setOnClickListener(v -> {
            if (layoutDetails.getVisibility() == View.GONE)
            {
                layoutDetails.setVisibility(View.VISIBLE);
                layoutSummary.setVisibility(View.GONE);
                sheetInterface.lockDragging(true);
            }
            else
            {
                layoutDetails.setVisibility(View.GONE);
                layoutSummary.setVisibility(View.VISIBLE);
                sheetInterface.lockDragging(false);
            }
        });
    }
}

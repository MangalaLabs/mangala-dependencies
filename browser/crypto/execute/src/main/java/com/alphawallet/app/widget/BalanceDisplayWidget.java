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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.Transaction;
import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.service.TokensService;
import com.alphawallet.app.util.BalanceUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

import timber.log.Timber;

public class BalanceDisplayWidget extends LinearLayout
{
    public final TextView balance;
    public final TextView newBalance;
    private final TokenIcon tokenIcon;
    private Transaction transaction;

    public BalanceDisplayWidget(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        inflate(context, R.layout.item_balance_display, this);
        balance = findViewById(R.id.text_balance);
        newBalance = findViewById(R.id.text_new_balance);
        tokenIcon = findViewById(R.id.token_icon);
    }

    public void setupBalance(Token token, TokensService tokenService, Transaction tx)
    {
        if (token.isNonFungible())
        {
            tokenIcon.setVisibility(View.VISIBLE);
            tokenIcon.bindData(token, tokenService);
        }
        else
        {
            tokenIcon.setVisibility(View.GONE);
            Timber.d("1991 setupBalance balance " + token.balance);
            Timber.d("1991 setupBalance getStringBalanceForUI " + token.getStringBalanceForUI(5));
            balance.setText(getContext().getString(R.string.total_cost, token.getStringBalanceForUI(5), token.getSymbol()));
        }
        transaction = tx;
    }

    public void setNewBalanceText(Token token, BigDecimal transactionAmount, BigInteger networkFee, BigInteger balanceAfterTransaction)
    {
        Timber.d("1991 setNewBalanceText transactionAmount " + transactionAmount + " networkFee " + networkFee + " balanceAfterTransaction " + balanceAfterTransaction);
        if (token.isEthereum())
        {
            balanceAfterTransaction = balanceAfterTransaction.subtract(networkFee).max(BigInteger.ZERO);
        }
        else if (transaction == null || transaction.transactionInput == null || transaction.transactionInput.isSendOrReceive(transaction))
        {
            balanceAfterTransaction = token.getBalanceRaw().subtract(transactionAmount).toBigInteger();
        }
        else
        {
            newBalance.setVisibility(View.GONE);
            return;
        }

        //convert to ETH amount
        String newBalanceVal = BalanceUtils.getScaledValueScientific(new BigDecimal(balanceAfterTransaction), token.tokenInfo.decimals);
        newBalance.setText(getContext().getString(R.string.new_balance, newBalanceVal, token.getSymbol()));
    }
}

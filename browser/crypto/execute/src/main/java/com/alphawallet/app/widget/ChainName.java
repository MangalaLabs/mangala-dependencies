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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alphawallet.app.R;
import com.alphawallet.app.repository.EthereumNetworkBase;

/**
 * Created by JB on 3/12/2020.
 */
public class ChainName extends LinearLayout
{
    private final TextView chainName;
    private boolean invertNameColour;

    public ChainName(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        inflate(context, R.layout.item_chain_name, this);
        chainName = findViewById(R.id._text_chain_name);
        getAttrs(context, attrs);
    }

    public void setChainID(long chainId)
    {
        chainName.setText(EthereumNetworkBase.getShortChainName(chainId));
        if (invertNameColour)
        {
            chainName.setTextColor(getContext().getColor(EthereumNetworkBase.getChainColour(chainId)));
            chainName.setBackgroundResource(com.schoolonair.wallet.component.resources.R.drawable.background_chain_inverse);
        }
        else
        {
            chainName.setTextColor(getContext().getColor(com.schoolonair.wallet.component.resources.R.color.white));
            chainName.getBackground().setTint(ContextCompat.getColor(getContext(),
                    EthereumNetworkBase.getChainColour(chainId)));
        }
    }

    private void getAttrs(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.InputView,
                0, 0
        );

        try
        {
            int fontSize = a.getInteger(R.styleable.InputView_font_size, 12);
            chainName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            invertNameColour = a.getBoolean(R.styleable.InputView_invert, false);
        }
        finally
        {
            a.recycle();
        }
    }
}

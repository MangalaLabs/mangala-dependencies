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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alphawallet.app.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Created by JB on 26/08/2021.
 */
public class StandardHeader extends LinearLayout
{
    private TextView headerText;
    private ChainName chainName;
    private SwitchMaterial switchMaterial;
    private View separator;

    public StandardHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        inflate(context, R.layout.item_standard_header, this);
        getAttrs(context, attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StandardHeader,
                0, 0
        );

        try
        {
            int headerId = a.getResourceId(R.styleable.StandardHeader_headerText, R.string.empty);
            boolean showSwitch = a.getBoolean(R.styleable.StandardHeader_showSwitch, false);
            boolean showChainName = a.getBoolean(R.styleable.StandardHeader_showChain, false);

            headerText = findViewById(R.id.text_header);
            chainName = findViewById(R.id.chain_name);
            switchMaterial = findViewById(R.id.switch_material);
            separator = findViewById(R.id.separator);

            headerText.setText(headerId);

            if (showSwitch)
            {
                switchMaterial.setVisibility(View.VISIBLE);
            }
            else
            {
                switchMaterial.setVisibility(View.GONE);
            }

            if (showChainName)
            {
                chainName.setVisibility(View.VISIBLE);
            }
            else
            {
                chainName.setVisibility(View.GONE);
            }
        }
        finally
        {
            a.recycle();
        }
    }

    public void setText(String text)
    {
        headerText.setText(text);
    }

    public void setText(int resId)
    {
        headerText.setText(resId);
    }

    public ChainName getChainName()
    {
        return chainName;
    }

    public SwitchMaterial getSwitch()
    {
        return switchMaterial;
    }

    public void hideSeparator()
    {
        separator.setVisibility(View.GONE);
    }
}

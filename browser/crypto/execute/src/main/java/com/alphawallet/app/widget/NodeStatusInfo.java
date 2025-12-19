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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.alphawallet.app.R;

import timber.log.Timber;

public class NodeStatusInfo extends LinearLayout
{
    Context context;
    ImageView icon;
    TextView message;

    public NodeStatusInfo(Context context, AttributeSet attr)
    {
        super(context, attr);
        this.context = context;
        inflate(context, R.layout.item_node_status_info, this);

        icon = findViewById(R.id.image);
        message = findViewById(R.id.text);

        setupAttrs(context, attr);
    }

    private void setupAttrs(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NodeStatusInfo,
                0, 0
        );

        try
        {
            setIcon(a.getResourceId(R.styleable.NodeStatusInfo_android_icon, com.schoolonair.wallet.component.resources.R.drawable.ic_help));
            setMessage(a.getString(R.styleable.NodeStatusInfo_android_text));
        }
        catch (Exception e)
        {
            Timber.e(e);
        }
    }

    public void setMessage(String msg)
    {
        message.setText(context.getString(R.string.node_status_label, msg));
    }

    public void setIcon(@DrawableRes int res)
    {
        icon.setImageDrawable(ContextCompat.getDrawable(context, res));
    }
}

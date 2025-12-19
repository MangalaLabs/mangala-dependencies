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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.alphawallet.app.R;

import javax.annotation.Nullable;

public class DialogInfoItem extends LinearLayout {

    private final TextView label;
    private final TextView message;
    private final TextView actionText;

    public DialogInfoItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.item_dialog_info, this);
        label = findViewById(R.id.text_label);
        message = findViewById(R.id.text_message);
        actionText = findViewById(R.id.text_action);
        getAttrs(context, attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.DialogInfoItem,
                0, 0);

        boolean showAction = a.getBoolean(R.styleable.DialogInfoItem_showActionText, false);
        setLabel(a.getString(R.styleable.DialogInfoItem_title));
        setMessage(a.getString(R.styleable.DialogInfoItem_text));
        actionText.setVisibility( showAction ? VISIBLE : INVISIBLE);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setMessage(String msg) {
        this.message.setText(msg);
    }

    public void setMessageTextColor(@ColorRes int color) {
        this.message.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    public void setActionText(String text) {
        this.actionText.setText(text);
    }

    public void setActionListener(OnClickListener listener) {
        actionText.setOnClickListener(listener);
    }
}

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alphawallet.app.R;
import com.bumptech.glide.Glide;

public class BottomSheetToolbarView extends RelativeLayout
{
    private TextView title;
    private ImageView logo;
    private ImageView closeBtn;

    public BottomSheetToolbarView(Context ctx, @Nullable AttributeSet attrs)
    {
        super(ctx, attrs);
        inflate(ctx, R.layout.layout_bottom_sheet_toolbar, this);
        title = findViewById(R.id.title);
        logo = findViewById(R.id.logo);
        closeBtn = findViewById(R.id.image_close);

        getAttrs(ctx, attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BottomSheetToolbarView,
                0, 0
        );

        try
        {
            int titleRes = a.getResourceId(R.styleable.BottomSheetToolbarView_title, R.string.empty);
            title.setText(titleRes);
        }
        finally
        {
            a.recycle();
        }
    }

    public void setTitle(int titleRes)
    {
        title.setText(titleRes);
    }

    public void setTitle(CharSequence titleText)
    {
        title.setText(titleText);
    }

    public void setLogo(Context context, String imageUrl)
    {
        Glide.with(context)
                .load(imageUrl)
                .circleCrop()
                .into(logo);
    }

    public void setCloseListener(OnClickListener listener)
    {
        closeBtn.setOnClickListener(listener);
    }
}

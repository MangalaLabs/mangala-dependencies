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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.util.Utils;

/**
 * Created by JB on 28/11/2020.
 */
public class AddressDetailView extends LinearLayout
{
    private final TextView textAddressSummary;
    private final TextView textFullAddress;
    private final TextView textEnsName;
    private final ImageView recipientDetails;
    private final UserAvatar userAvatar;
    private final LinearLayout layoutDetails;
    private final LinearLayout layoutHolder;

    public AddressDetailView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        inflate(context, R.layout.item_address_detail, this);
        textAddressSummary = findViewById(R.id.text_recipient);
        textFullAddress = findViewById(R.id.text_recipient_address);
        textEnsName = findViewById(R.id.text_ens_name);
        recipientDetails = findViewById(R.id.image_more);
        userAvatar = findViewById(R.id.blockie);
        layoutDetails = findViewById(R.id.layout_detail);
        layoutHolder = findViewById(R.id.layout_holder);
        getAttrs(context, attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.InputView,
                0, 0);

        TextView recipientText = findViewById(R.id.text_recipient_title);
        recipientText.setText(a.getResourceId(R.styleable.InputView_label, R.string.recipient));
    }

    public void setupAddress(String address, String ensName, Token destToken)
    {
        String destStr = (!TextUtils.isEmpty(ensName) ? ensName + " | " : "") + Utils.formatAddress(address);
        textAddressSummary.setText(destStr);
        userAvatar.bind(new Wallet(address), wallet -> { /*NOP, here to enable lookup of ENS avatar*/ });
        textFullAddress.setText(address);
        textEnsName.setText(ensName);

        if (TextUtils.isEmpty(ensName) && destToken != null && !destToken.isEthereum())
        {
            ((TextView)findViewById(R.id.label_ens)).setText(R.string.token_text);
            textEnsName.setText(destToken.getFullName());
        }

        layoutHolder.setOnClickListener(v -> {
            if (layoutDetails.getVisibility() == View.GONE)
            {
                layoutDetails.setVisibility(View.VISIBLE);
                textAddressSummary.setVisibility(View.INVISIBLE);
                recipientDetails.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_expand_less_black);
            }
            else
            {
                layoutDetails.setVisibility(View.GONE);
                textAddressSummary.setVisibility(View.VISIBLE);
                recipientDetails.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_expand_more);
            }
        });
    }

    public void setupRequester(String requesterUrl)
    {
        setVisibility(View.VISIBLE);
        recipientDetails.setVisibility(View.GONE);
        //shorten requesterURL if required
        requesterUrl = abbreviateURL(requesterUrl);
        textAddressSummary.setText(requesterUrl);
        ViewGroup.LayoutParams param = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 3.4f);
        textAddressSummary.setLayoutParams(param);
    }

    private String abbreviateURL(String inputURL)
    {
        if (inputURL.length() > 32)
        {
            int index = inputURL.indexOf("/", 20);
            return index >= 0 ? inputURL.substring(0,index) : inputURL;
        }
        else
        {
            return inputURL;
        }
    }
}

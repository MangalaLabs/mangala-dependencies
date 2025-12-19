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

package com.alphawallet.app.util;


import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.alphawallet.app.R;
import com.google.android.material.tabs.TabLayout;

public class TabUtils {
    public static void setSelectedTabFont(TabLayout tabLayout, TabLayout.Tab tab, Typeface typeface) {
        LinearLayout layout = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
        TextView tabTextView = (TextView) layout.getChildAt(1);
        if (tabTextView != null) {
            tabTextView.setTypeface(typeface);
        }
    }

    public static void decorateTabLayout(Context context, TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setSelectedTabFont(tabLayout, tab, ResourcesCompat.getFont(context, R.font.font_semibold));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setSelectedTabFont(tabLayout, tab, ResourcesCompat.getFont(context, R.font.font_regular));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab firstTab = tabLayout.getTabAt(0);
        if (firstTab != null) {
            TabUtils.setSelectedTabFont(tabLayout, firstTab, ResourcesCompat.getFont(context, R.font.font_semibold));
        }
    }

    public static void setHighlightedTabColor(Context context, TabLayout tabLayout) {
        int tabCount = tabLayout.getTabCount();

        if (tabCount > 3) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tabCount - 1);
            ViewGroup.MarginLayoutParams tabParams = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            tabParams.rightMargin = Utils.dp2px(context, 12);
            tab.requestLayout();
        }
    }
}

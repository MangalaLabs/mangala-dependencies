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

package com.alphawallet.app.repository;

import android.content.Context;
import android.text.TextUtils;

import com.alphawallet.app.entity.LocaleItem;
import com.alphawallet.app.util.LocaleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class LocaleRepository implements LocaleRepositoryType {
    private static final String[] LOCALES = {
            "en",
            "zh",
            "es",
            "fr",
            "vi",
            "my"
    };

    private final PreferenceRepositoryType preferences;

    public LocaleRepository(PreferenceRepositoryType preferenceRepository) {
        this.preferences = preferenceRepository;
    }

    @Override
    public void setLocale(Context context, String locale) {
        LocaleUtils.setLocale(context, locale);
    }

    @Override
    public String getUserPreferenceLocale()
    {
        return preferences.getUserPreferenceLocale();
    }

    @Override
    public void setUserPreferenceLocale(String locale)
    {
        preferences.setUserPreferenceLocale(locale);
    }

    @Override
    public String getActiveLocale()
    {
        String useLocale = preferences.getUserPreferenceLocale();
        if (TextUtils.isEmpty(useLocale)) useLocale = preferences.getDefaultLocale();
        return useLocale;
    }

    @Override
    public ArrayList<LocaleItem> getLocaleList(Context context) {
        ArrayList<LocaleItem> list = new ArrayList<>();
        for (String locale : LOCALES) {
            Locale l = new Locale(locale);
            list.add(new LocaleItem(LocaleUtils.getDisplayLanguage(locale, getActiveLocale()), locale));
        }
        return list;
    }

    @Override
    public boolean isLocalePresent(String locale) {
        return Arrays.asList(LOCALES).contains(locale);
    }
}

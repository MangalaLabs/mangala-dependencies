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

import com.alphawallet.app.R;
import com.alphawallet.app.entity.CurrencyItem;

import java.util.ArrayList;
import java.util.Arrays;

public class CurrencyRepository implements CurrencyRepositoryType {
    public static final CurrencyItem[] CURRENCIES = {
            new CurrencyItem("USD", "American Dollar", "$", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_usa),
            new CurrencyItem("EUR", "Euro", "€", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_euro),
            new CurrencyItem("GBP", "British Pound", "£", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_uk),
            new CurrencyItem("AUD", "Australian Dollar", "$", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_australia),
            new CurrencyItem("CNY", "China Yuan Renminbi","¥", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_china),
            new CurrencyItem("INR", "Indian Rupee","₹", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_india),
            new CurrencyItem("SGD", "Singapore Dollar","$", com.schoolonair.wallet.component.resources.R.drawable.ic_flag_sgd),
            new CurrencyItem("JPY", "Japanese Yen","¥", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_japan),
            new CurrencyItem("KRW", "Korean Won","₩", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_korea),
            new CurrencyItem("RUB", "Russian Ruble","₽", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_russia),
            new CurrencyItem("VND", "Vietnamese đồng", "₫", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_vietnam),
            new CurrencyItem("PKR", "Pakistani rupee", "Rs", com.schoolonair.wallet.component.resources.R.drawable.ic_flags_pakistan),
            new CurrencyItem("MMK", "Myanmar Kyat", "Ks",com.schoolonair.wallet.component.resources.R.drawable.ic_flags_myanmar)
    };

    private final PreferenceRepositoryType preferences;

    public CurrencyRepository(PreferenceRepositoryType preferenceRepository) {
        this.preferences = preferenceRepository;
    }

    @Override
    public void setDefaultCurrency(String currencyCode) {
        CurrencyItem currencyItem = getCurrencyByISO(currencyCode);
        preferences.setDefaultCurrency(currencyItem);
    }

    public String getDefaultCurrency() {
        return preferences.getDefaultCurrency();
    }

    @Override
    public ArrayList<CurrencyItem> getCurrencyList() {
        return new ArrayList<>(Arrays.asList(CURRENCIES));
    }

    public static CurrencyItem getCurrencyByISO(String currencyIsoCode) {
        for (CurrencyItem c : CURRENCIES) {
            if (currencyIsoCode.equals(c.getCode())) {
                return c;
            }
        }
        return null;
    }

    public static CurrencyItem getCurrencyByName(String currencyName) {
        for (CurrencyItem c : CURRENCIES) {
            if (currencyName.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    public static int getFlagByISO(String currencyIsoCode) {
        for (CurrencyItem c : CURRENCIES) {
            if (currencyIsoCode.equals(c.getCode())) {
                return c.getFlag();
            }
        }
        return 0;
    }
}

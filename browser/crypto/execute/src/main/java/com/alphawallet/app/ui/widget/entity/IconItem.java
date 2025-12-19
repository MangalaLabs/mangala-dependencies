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

package com.alphawallet.app.ui.widget.entity;

import static com.alphawallet.app.repository.TokensRealmSource.databaseKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IconItem {
    private final String url;
    private final UseIcon useText;

    private final static Map<String, Boolean> iconLoadType = new ConcurrentHashMap<>();

    public IconItem(String url, long chainId, String correctedAddress) {
        this.url = url;
        this.useText = getLoadType(chainId, correctedAddress);
    }

    private UseIcon getLoadType(long chainId, String correctedAddress)
    {
        String key = databaseKey(chainId, correctedAddress);
        return iconLoadType.containsKey(key)
                ? (iconLoadType.get(key) ? UseIcon.SECONDARY : UseIcon.NO_ICON)
                : UseIcon.PRIMARY;
    }

    public String getUrl() {
        return url;
    }

    public boolean useTextSymbol() {
        return useText == UseIcon.NO_ICON;
    }

    public boolean usePrimary() {
        return useText == UseIcon.PRIMARY;
    }

    //Use Secondary icon
    public static void secondaryFound(long chainId, String address)
    {
        iconLoadType.put(databaseKey(chainId, address.toLowerCase()), true);
    }

    //Use TextIcon
    public static void noIconFound(long chainId, String address)
    {
        iconLoadType.put(databaseKey(chainId, address.toLowerCase()), false);
    }

    /**
     * Resets the failed icon fetch checking - try again to load failed icons
     */
    public static void resetCheck()
    {
        iconLoadType.clear();
    }
}

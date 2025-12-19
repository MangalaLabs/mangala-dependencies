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

import com.alphawallet.app.entity.CurrencyItem;

public interface PreferenceRepositoryType {
    String getCurrentWalletAddress();

    void setCurrentWalletAddress(String address);

    long getActiveBrowserNetwork();

    void setActiveBrowserNetwork(long networkId);

    String getNetworkFilterList();

    void setNetworkFilterList(String filters);

    String getCustomRPCNetworks();

    void setCustomRPCNetworks(String networks);

    boolean getNotificationsState();

    void setNotificationState(boolean state);

    String getDefaultLocale();

    boolean isFindWalletAddressDialogShown();

    void setFindWalletAddressDialogShown(boolean isShown);

    String getDefaultCurrency();

    void setDefaultCurrency(CurrencyItem currency);

    String getDefaultCurrencySymbol();

    String getUserPreferenceLocale();

    void setUserPreferenceLocale(String locale);

    boolean getFullScreenState();

    void setFullScreenState(boolean state);

    void setUse1559Transactions(boolean toggleState);
    boolean getUse1559Transactions();

    boolean isActiveMainnet();

    void setActiveMainnet(boolean state);

    boolean hasShownTestNetWarning();

    void setShownTestNetWarning();

    void setPriceAlerts(String json);

    String getPriceAlerts();
    void setHasSetNetworkFilters();
    boolean hasSetNetworkFilters();
    void blankHasSetNetworkFilters();

    void commit();

    void incrementLaunchCount();
    int getLaunchCount();
    void resetLaunchCount();

    void setRateAppShown();
    boolean getRateAppShown();

    void setShowZeroBalanceTokens(boolean shouldShow);
    boolean shouldShowZeroBalanceTokens();

    int getUpdateWarningCount();
    void setUpdateWarningCount(int count);

    int getUpdateAsksCount();
    void setUpdateAsksCount(int count);

    long getInstallTime();
    void setInstallTime(long time);

    String getUniqueId();
    void setUniqueId(String uuid);

    boolean isMarshMallowWarningShown();
    void setMarshMallowWarning(boolean shown);

    void storeLastFragmentPage(int ordinal);
    int getLastFragmentPage();

    int getLastVersionCode(int currentCode);
    void setLastVersionCode(int code);

    int getTheme();
    void setTheme(int state);
}

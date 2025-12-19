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

package com.alphawallet.app.service;

import com.alphawallet.app.BuildConfig;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.repository.AWRealmMigration;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class RealmManager {

    private final Map<String, RealmConfiguration> realmConfigurations = new HashMap<>();

    public String getRealmInstanceName(Wallet wallet) {
        return wallet.address.toLowerCase() + "-db.realm";
    }

    public Realm getRealmInstance(Wallet wallet) {
        return getRealmInstanceInternal(getRealmInstanceName(wallet));
    }

    public Realm getRealmInstance(String walletAddress) {
        return getRealmInstanceInternal(walletAddress.toLowerCase() + "-db.realm");
    }

    Realm getRealmInstanceInternal(String name) {
        try
        {
            RealmConfiguration config = realmConfigurations.get(name);
            if (config == null)
            {
                config = new RealmConfiguration.Builder().name(name)
                        .schemaVersion(BuildConfig.DB_VERSION)
                        .migration(new AWRealmMigration())
                        .build();
                realmConfigurations.put(name, config);
            }
            return Realm.getInstance(config);
        }
        catch (RealmMigrationNeededException e)
        {
            //we require a realm migration, but this wasn't provided.
            RealmConfiguration config = realmConfigurations.get(name);
            if (config == null)
            {
                config = new RealmConfiguration.Builder().name(name)
                        .schemaVersion(BuildConfig.DB_VERSION)
                        .deleteRealmIfMigrationNeeded()
                        .build();
                realmConfigurations.put(name, config);
            }
            return Realm.getInstance(config);
        }
    }

    public Realm getWalletDataRealmInstance() {
        return getRealmInstanceInternal("WalletData-db.realm");
    }

    public Realm getWalletTypeRealmInstance() {
        return getRealmInstanceInternal("WalletType-db.realm");
    }
}

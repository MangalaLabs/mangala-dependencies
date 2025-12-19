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

import com.alphawallet.app.entity.Wallet;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.realm.Realm;

public interface WalletRepositoryType {
    Single<Wallet[]> fetchWallets();

    Single<Wallet> findWallet(String address);

    Single<Wallet> createWallet(String password);

    Single<Wallet> importKeystoreToWallet(String store, String password, String newPassword);

    Single<Wallet> importPrivateKeyToWallet(String privateKey, String newPassword);

    Single<String> exportWallet(Wallet wallet, String password, String newPassword);

    Completable deleteWallet(String address, String password);
    Single<Wallet> deleteWalletFromRealm(Wallet wallet);

    Completable setDefaultWallet(Wallet wallet);

    Single<Wallet> getDefaultWallet();

    Single<Wallet[]> storeWallets(Wallet[] wallets);

    Single<Wallet> storeWallet(Wallet wallet);
    void updateWalletData(Wallet wallet, Realm.Transaction.OnSuccess onSuccess);
    void updateWalletItem(Wallet wallet, WalletItem item, Realm.Transaction.OnSuccess onSuccess);

    Single<String> getName(String address);

    void updateBackupTime(String walletAddr);
    void updateWarningTime(String walletAddr);

    Single<Boolean> getWalletBackupWarning(String walletAddr);

    Single<String> getWalletRequiresBackup(String walletAddr);
    void setIsDismissed(String walletAddr, boolean isDismissed);

    boolean keystoreExists(String address);

    Realm getWalletRealm();
}

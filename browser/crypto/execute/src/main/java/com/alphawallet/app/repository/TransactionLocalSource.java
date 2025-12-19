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

import com.alphawallet.app.entity.ActivityMeta;
import com.alphawallet.app.entity.Transaction;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.repository.entity.RealmAuxData;

import java.util.List;

import io.reactivex.Single;
import io.realm.Realm;

public interface TransactionLocalSource {
	Transaction fetchTransaction(Wallet wallet, String hash);
	void putTransaction(Wallet wallet, Transaction tx);
	Realm getRealmInstance(Wallet wallet);

	Single<ActivityMeta[]> fetchActivityMetas(Wallet wallet, List<Long> networkFilters, long fetchTime, int fetchLimit);
	Single<ActivityMeta[]> fetchActivityMetas(Wallet wallet, long chainId, String tokenAddress, int historyCount);
	Single<ActivityMeta[]> fetchEventMetas(Wallet wallet, List<Long> networkFilters);

	void markTransactionBlock(String walletAddress, String hash, long blockValue);
	Transaction[] fetchPendingTransactions(String currentAddress);

	RealmAuxData fetchEvent(String walletAddress, String eventKey);

    long fetchTxCompletionTime(Wallet wallet, String hash);

    Single<Boolean> deleteAllForWallet(String currentAddress);

    Single<Boolean> deleteAllTickers();
}

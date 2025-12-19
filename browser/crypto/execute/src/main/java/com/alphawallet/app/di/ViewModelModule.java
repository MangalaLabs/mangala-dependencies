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

package com.alphawallet.app.di;//package com.alphawallet.app.di;
//
//
//import com.alphawallet.app.interact.CreateTransactionInteract;
//import com.alphawallet.app.interact.FetchTransactionsInteract;
//import com.alphawallet.app.interact.FindDefaultNetworkInteract;
//import com.alphawallet.app.interact.GenericWalletInteract;
//import com.alphawallet.app.repository.CurrencyRepository;
//import com.alphawallet.app.repository.CurrencyRepositoryType;
//import com.alphawallet.app.repository.EthereumNetworkRepositoryType;
//import com.alphawallet.app.repository.LocaleRepository;
//import com.alphawallet.app.repository.LocaleRepositoryType;
//import com.alphawallet.app.repository.PreferenceRepositoryType;
//import com.alphawallet.app.repository.TokenRepositoryType;
//import com.alphawallet.app.repository.TransactionRepositoryType;
//import com.alphawallet.app.repository.WalletRepositoryType;
//
//import dagger.Module;
//import dagger.Provides;
//import dagger.hilt.InstallIn;
//import dagger.hilt.android.components.ViewModelComponent;
//
//@Module
//@InstallIn(ViewModelComponent.class)
//
//public class ViewModelModule {
//
//
//    @Provides
//    FindDefaultNetworkInteract provideFindDefaultNetworkInteract(
//            EthereumNetworkRepositoryType networkRepository) {
//        return new FindDefaultNetworkInteract(networkRepository);
//    }
//
//    @Provides
//    FetchTransactionsInteract provideFetchTransactionsInteract(TransactionRepositoryType transactionRepository,
//                                                               TokenRepositoryType tokenRepositoryType) {
//        return new FetchTransactionsInteract(transactionRepository, tokenRepositoryType);
//    }
//
//    @Provides
//    CreateTransactionInteract provideCreateTransactionInteract(TransactionRepositoryType transactionRepository) {
//        return new CreateTransactionInteract(transactionRepository);
//    }
//
//    @Provides
//    LocaleRepositoryType provideLocaleRepository(PreferenceRepositoryType preferenceRepository) {
//        return new LocaleRepository(preferenceRepository);
//    }
//
//    @Provides
//    CurrencyRepositoryType provideCurrencyRepository(PreferenceRepositoryType preferenceRepository) {
//        return new CurrencyRepository(preferenceRepository);
//    }
//
//
//    @Provides
//    GenericWalletInteract provideGenericWalletInteract(WalletRepositoryType walletRepository) {
//        return new GenericWalletInteract(walletRepository);
//    }
//}

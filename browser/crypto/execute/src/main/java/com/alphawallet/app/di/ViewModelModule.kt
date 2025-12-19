/*
 * Copyright (c) 2019-2023 AlphaWallet
 * Copyright (c) 2023-2025 Mangala Wallet
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
 * Modified from original source: https://github.com/AlphaWallet/alpha-wallet-android
 */

package com.alphawallet.app.di

import com.alphawallet.app.C
import com.alphawallet.app.interact.CreateTransactionInteract
import com.alphawallet.app.interact.FetchTransactionsInteract
import com.alphawallet.app.interact.FindDefaultNetworkInteract
import com.alphawallet.app.interact.GenericWalletInteract
import com.alphawallet.app.repository.CurrencyRepository
import com.alphawallet.app.repository.EthereumNetworkRepository
import com.alphawallet.app.repository.EthereumNetworkRepositoryType
import com.alphawallet.app.repository.LocaleRepository
import com.alphawallet.app.repository.OnRampRepository
import com.alphawallet.app.repository.OnRampRepositoryType
import com.alphawallet.app.repository.PreferenceRepositoryType
import com.alphawallet.app.repository.SharedPreferenceRepository
import com.alphawallet.app.repository.TokenLocalSource
import com.alphawallet.app.repository.TokenRepository
import com.alphawallet.app.repository.TokenRepositoryType
import com.alphawallet.app.repository.TokensRealmSource
import com.alphawallet.app.repository.TransactionLocalSource
import com.alphawallet.app.repository.TransactionRepository
import com.alphawallet.app.repository.TransactionRepositoryType
import com.alphawallet.app.repository.TransactionsRealmCache
import com.alphawallet.app.repository.WalletDataRealmSource
import com.alphawallet.app.repository.WalletRepository
import com.alphawallet.app.repository.WalletRepositoryType
import com.alphawallet.app.service.AccountKeystoreService
import com.alphawallet.app.service.AlphaWalletService
import com.alphawallet.app.service.AnalyticsService
import com.alphawallet.app.service.AnalyticsServiceType
import com.alphawallet.app.service.AssetDefinitionService
import com.alphawallet.app.service.GasService
import com.alphawallet.app.service.KeyService
import com.alphawallet.app.service.KeystoreAccountService
import com.alphawallet.app.service.KeystoreAccountService.KEYSTORE_FOLDER
import com.alphawallet.app.service.NotificationService
import com.alphawallet.app.service.OpenSeaService
import com.alphawallet.app.service.RealmManager
import com.alphawallet.app.service.SwapService
import com.alphawallet.app.service.TickerService
import com.alphawallet.app.service.TokensService
import com.alphawallet.app.service.TransactionsNetworkClient
import com.alphawallet.app.service.TransactionsNetworkClientType
import com.alphawallet.app.service.TransactionsService
import com.alphawallet.app.viewmodel.CustomNetworkViewModel
import com.alphawallet.app.viewmodel.GasSettingsViewModel
import com.alphawallet.app.viewmodel.NodeStatusViewModel
import com.alphawallet.app.viewmodel.SelectNetworkFilterViewModel
import com.alphawallet.app.viewmodel.SelectNetworkViewModel
import com.alphawallet.app.viewmodel.TransactionSuccessViewModel
import com.alphawallet.app.viewmodel.WalletConnectViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

val viewModelModule = module {
    single { FindDefaultNetworkInteract(get()) }
    single { FetchTransactionsInteract(get(), get()) }
    single { CreateTransactionInteract(get()) }
    single { LocaleRepository(get()) }
    single { CurrencyRepository(get()) }
    single { GenericWalletInteract(get()) }
    viewModel { WalletConnectViewModel(get(), get(), get(), get(), get(),get(), get(), get(), get(), get(), get()) }
    viewModel { TransactionSuccessViewModel(get()) }
    viewModel { SelectNetworkViewModel(get(), get(), get()) }
    viewModel { SelectNetworkFilterViewModel(get(), get(), get()) }
    viewModel { NodeStatusViewModel(get()) }
    viewModel { GasSettingsViewModel(get()) }
    viewModel { CustomNetworkViewModel(get()) }

    single { Gson() }
    single { RealmManager() }

    single {
        OkHttpClient.Builder()
            //.addInterceptor(LogInterceptor())
            .connectTimeout(C.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(C.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(C.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    single<PreferenceRepositoryType> { SharedPreferenceRepository(androidContext()) }
    single<AccountKeystoreService> {
        val file = File(androidContext().filesDir, KEYSTORE_FOLDER)
        KeystoreAccountService(file, androidContext().filesDir, get<KeyService>())
    }

    single<TickerService> {
        TickerService(get<OkHttpClient>(), get<PreferenceRepositoryType>(), get<TokenLocalSource>())
    }

    single<EthereumNetworkRepositoryType> {
        EthereumNetworkRepository(get<PreferenceRepositoryType>(), androidContext())
    }

    single<WalletRepositoryType> {
        WalletRepository(
            get<PreferenceRepositoryType>(),
            get<AccountKeystoreService>(),
            get<EthereumNetworkRepositoryType>(),
            get<WalletDataRealmSource>(),
            get<KeyService>()
        )
    }

    single<TransactionRepositoryType> {
        TransactionRepository(
            get<EthereumNetworkRepositoryType>(),
            get<AccountKeystoreService>(),
            get<TransactionLocalSource>(),
            get<TransactionsService>()
        )
    }

    single<OnRampRepositoryType> {
        OnRampRepository(androidContext(), get())
    }

    single<TransactionLocalSource> {
        TransactionsRealmCache(get<RealmManager>())
    }

    single<TransactionsNetworkClientType> {
        TransactionsNetworkClient(get<OkHttpClient>(), get<Gson>(), get<RealmManager>())
    }

    single<TokenRepositoryType> {
        TokenRepository(
            get<EthereumNetworkRepositoryType>(),
            get<TokenLocalSource>(),
            get<OkHttpClient>(),
            androidContext(),
            get<TickerService>()
        )
    }

    single<TokenLocalSource> {
        TokensRealmSource(get<RealmManager>(), get<EthereumNetworkRepositoryType>())
    }

    single<WalletDataRealmSource> {
        WalletDataRealmSource(get<RealmManager>())
    }

    single<TokensService> {
        TokensService(
            get<EthereumNetworkRepositoryType>(),
            get<TokenRepositoryType>(),
            get<TickerService>(),
            get<OpenSeaService>()
        )
    }

    single<TransactionsService> {
        TransactionsService(
            get<TokensService>(),
            get<EthereumNetworkRepositoryType>(),
            get<TransactionsNetworkClientType>(),
            get<TransactionLocalSource>()
        )
    }

    single<GasService> {
        GasService(
            get<EthereumNetworkRepositoryType>(),
            get<OkHttpClient>(),
            get<RealmManager>()
        )
    }

    single<OpenSeaService> {
        OpenSeaService()
    }

    single<SwapService> {
        SwapService()
    }

    single<AlphaWalletService> {
        AlphaWalletService(
            get<OkHttpClient>(),
            get<TransactionRepositoryType>(),
            get<Gson>()
        )
    }

    single<NotificationService> {
        NotificationService(androidContext()) // context will be provided
    }

    single<AssetDefinitionService> {
        AssetDefinitionService(
            get<OkHttpClient>(),
            get(), // context will be provided
            get<NotificationService>(),
            get<RealmManager>(),
            get<TokensService>(),
            get<TokenLocalSource>(),
            get<TransactionRepositoryType>(),
            get<AlphaWalletService>()
        )
    }

    single<KeyService> {
        KeyService(
            get(),
        )
    }

//    single {
//        AnalyticsService() // context will be provided
//    }
}

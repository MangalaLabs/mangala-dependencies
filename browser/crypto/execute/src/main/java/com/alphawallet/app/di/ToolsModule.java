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
//import com.alphawallet.app.C;
//import com.alphawallet.app.service.RealmManager;
//import com.google.gson.Gson;
//
//import java.util.concurrent.TimeUnit;
//
//import javax.inject.Singleton;
//
//import dagger.Module;
//import dagger.Provides;
//import dagger.hilt.InstallIn;
//import dagger.hilt.components.SingletonComponent;
//import okhttp3.OkHttpClient;
//
//@Module
//@InstallIn(SingletonComponent.class)
//public class ToolsModule {
//    @Singleton
//    @Provides
//    Gson provideGson() {
//        return new Gson();
//    }
//
//    @Singleton
//    @Provides
//    OkHttpClient okHttpClient() {
//        return new OkHttpClient.Builder()
//                //.addInterceptor(new LogInterceptor())
//                .connectTimeout(C.CONNECT_TIMEOUT, TimeUnit.SECONDS)
//                .readTimeout(C.READ_TIMEOUT, TimeUnit.SECONDS)
//                .writeTimeout(C.WRITE_TIMEOUT, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(false)
//                .build();
//    }
//
//    @Singleton
//    @Provides
//    RealmManager provideRealmManager() {
//        return new RealmManager();
//    }
//}

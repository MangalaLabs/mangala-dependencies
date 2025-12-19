/*
 * Copyright (c) DuckDuckGo, Inc.
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from original source: https://github.com/duckduckgo/Android
 */


package com.mangala.securestorage.impl.di

import android.content.Context
import androidx.room.Room
import com.mangala.securestorage.api.SecureStorage
import com.mangala.securestorage.impl.*
import com.mangala.securestorage.impl.encryption.EncryptionHelper
import com.mangala.securestorage.impl.encryption.RandomBytesGenerator
import com.mangala.securestorage.impl.encryption.RealEncryptionHelper
import com.mangala.securestorage.impl.encryption.RealRandomBytesGenerator
import com.mangala.securestorage.impl.DerivedKeySecretFactory
import com.mangala.securestorage.impl.L2DataTransformer
import com.mangala.securestorage.impl.LegacyDerivedKeySecretFactory
import com.mangala.securestorage.impl.RealDerivedKeySecretFactory
import com.mangala.securestorage.impl.RealL2DataTransformer
import com.mangala.securestorage.impl.RealSecureStorage
import com.mangala.securestorage.impl.RealSecureStorageKeyGenerator
import com.mangala.securestorage.impl.RealSecureStorageKeyProvider
import com.mangala.securestorage.impl.SecureStorageKeyGenerator
import com.mangala.securestorage.impl.SecureStorageKeyProvider
import com.mangala.securestorage.store.RealSecureStorageKeyRepository
import com.mangala.securestorage.store.RealSecureStorageRepository
import com.mangala.securestorage.store.SecureStorageKeyRepository
import com.mangala.securestorage.store.SecureStorageRepository
import com.mangala.securestorage.store.db.ALL_MIGRATIONS
import com.mangala.securestorage.store.db.SecureStorageDatabase
import com.mangala.securestorage.store.db.WebsiteLoginCredentialsDao
import com.mangala.securestorage.store.keys.RealSecureStorageKeyStore
import com.mangala.securestorage.store.keys.SecureStorageKeyStore
import dagger.Binds
import dagger.Module
import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import javax.inject.Named
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object SecureStorageModule {
//
//    @Provides
//    @Singleton
//    fun providesSecureStorageKeyStore(@ApplicationContext context: Context): SecureStorageKeyRepository =
//        RealSecureStorageKeyRepository(RealSecureStorageKeyStore(context))
//
//    @Provides
//    @Singleton
//    fun providesSecureStorageDatabase(
//        @ApplicationContext context: Context,
//        keyProvider: SecureStorageKeyProvider
//    ): SecureStorageDatabase {
//        return Room.databaseBuilder(
//            context,
//            SecureStorageDatabase::class.java,
//            "secure_storage_database_encrypted.db"
//        ).openHelperFactory(SupportFactory(keyProvider.getl1Key()))
//            .addMigrations(*ALL_MIGRATIONS)
//            .enableMultiInstanceInvalidation()
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Provides
//    fun providesWebsiteLoginCredentialsDao(db: SecureStorageDatabase): WebsiteLoginCredentialsDao {
//        return db.websiteLoginCredentialsDao()
//    }
//
//    @Provides
//    fun providesSecureStorageRepository(websiteLoginCredentialsDao: WebsiteLoginCredentialsDao): SecureStorageRepository =
//        RealSecureStorageRepository(websiteLoginCredentialsDao)
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//object SecureStorageKeyModule {
//    @Provides
//    @Named("DerivedKeySecretFactoryFor26Up")
//    fun provideDerivedKeySecretFactoryFor26Up(): DerivedKeySecretFactory = RealDerivedKeySecretFactory()
//
//    @Provides
//    @Named("DerivedKeySecretFactoryForLegacy")
//    fun provideDerivedKeySecretFactoryForLegacy(): DerivedKeySecretFactory = LegacyDerivedKeySecretFactory()
//
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//interface Binding {
//    @Binds
//    fun bindRealRandomBytesGenerator(realRandomBytesGenerator: RealRandomBytesGenerator): RandomBytesGenerator
//
//    @Binds
//    fun bindRealEncryptionHelper(realEncryptionHelper: RealEncryptionHelper): EncryptionHelper
//
//    @Binds
//    @Singleton
//    fun bindRealL2DataTransformer(realL2DataTransformer: RealL2DataTransformer): L2DataTransformer
//
//    @Binds
//    fun bindRealSecureStorageKeyGenerator(realSecureStorageKeyGenerator: RealSecureStorageKeyGenerator): SecureStorageKeyGenerator
//
//    @Binds
//    @Singleton
//    fun bindRealSecureStorage(realSecureStorage: RealSecureStorage): SecureStorage
//
//    @Binds
//    fun bindRealSecureStorageKeyProvider(realSecureStorageKeyProvider: RealSecureStorageKeyProvider): SecureStorageKeyProvider
//}
//


val secureStorageModule = module {

    single< SecureStorageKeyStore> {
        RealSecureStorageKeyStore(androidContext())
    }
    single<SecureStorageKeyRepository> {
        RealSecureStorageKeyRepository(get())
    }

    single<SecureStorageDatabase> {
        val context = androidContext()
        val keyProvider: SecureStorageKeyProvider = get()

        Room.databaseBuilder(
            context,
            SecureStorageDatabase::class.java,
            "secure_storage_database_encrypted.db"
        )
            .openHelperFactory(SupportOpenHelperFactory(keyProvider.getl1Key()))
            .addMigrations(*ALL_MIGRATIONS)
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }

    single<WebsiteLoginCredentialsDao> { get<SecureStorageDatabase>().websiteLoginCredentialsDao() }

    single<SecureStorageRepository> {
        RealSecureStorageRepository(get())
    }

    single<DerivedKeySecretFactory>(named("DerivedKeySecretFactoryFor26Up")) { RealDerivedKeySecretFactory() }
    single<DerivedKeySecretFactory>(named("DerivedKeySecretFactoryForLegacy")) { LegacyDerivedKeySecretFactory() }

    single<RandomBytesGenerator> { RealRandomBytesGenerator(get()) }
    single<EncryptionHelper> { RealEncryptionHelper() }
    single<L2DataTransformer> { RealL2DataTransformer(get(), get()) }
    single<SecureStorageKeyGenerator> { RealSecureStorageKeyGenerator(get(),get(named("DerivedKeySecretFactoryFor26Up")), get(named("DerivedKeySecretFactoryForLegacy"))) }
    single<SecureStorage> { RealSecureStorage(get(), get(), get()) }
    single<SecureStorageKeyProvider> { RealSecureStorageKeyProvider(get(), get(), get(), get()) }
}
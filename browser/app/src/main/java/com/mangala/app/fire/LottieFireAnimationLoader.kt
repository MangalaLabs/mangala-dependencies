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


package com.mangala.app.fire

import android.content.Context
import androidx.annotation.UiThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.airbnb.lottie.LottieCompositionFactory
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.settings.db.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface FireAnimationLoader : DefaultLifecycleObserver {
    fun preloadSelectedAnimation()
}

class LottieFireAnimationLoader (
    private val context: Context,
    private val settingsDataStore: SettingsDataStore,
    private val dispatchers: DispatcherProvider,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope
) : FireAnimationLoader {

    override fun onCreate(owner: LifecycleOwner) {
        preloadSelectedAnimation()
    }

    @UiThread
    override fun preloadSelectedAnimation() {
        appCoroutineScope.launch(dispatchers.io()) {
            if (animationEnabled()) {
                val selectedFireAnimation = settingsDataStore.selectedFireAnimation
                LottieCompositionFactory.fromRawRes(context, selectedFireAnimation.resId)
            }
        }
    }

    private fun animationEnabled() = settingsDataStore.fireAnimationEnabled
}

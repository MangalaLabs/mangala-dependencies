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


package com.mangala.app.browser.remotemessage

import com.mangala.mobile.android.ui.view.MessageCta.Message
import com.mangala.remote.messaging.api.Content.BigSingleAction
import com.mangala.remote.messaging.api.Content.BigTwoActions
import com.mangala.remote.messaging.api.Content.Medium
import com.mangala.remote.messaging.api.Content.Placeholder
import com.mangala.remote.messaging.api.Content.Placeholder.ANNOUNCE
import com.mangala.remote.messaging.api.Content.Placeholder.APP_UPDATE
import com.mangala.remote.messaging.api.Content.Placeholder.CRITICAL_UPDATE
import com.mangala.remote.messaging.api.Content.Placeholder.DDG_ANNOUNCE
import com.mangala.remote.messaging.api.Content.Small
import com.mangala.remote.messaging.api.RemoteMessage

fun RemoteMessage.asMessage(): Message {
    return when (val content = this.content) {
        is Small -> Message(
            title = content.titleText ?: "",
            subtitle = content.descriptionText ?: ""
        )
        is BigSingleAction -> Message(
            illustration = content.placeholder?.drawable() ?: 0,
            title = content.titleText ?: "",
            subtitle = content.descriptionText ?: "",
            action = content.primaryActionText ?: ""
        )
        is BigTwoActions -> Message(
            illustration = content.placeholder?.drawable() ?: 0,
            title = content.titleText ?: "",
            subtitle = content.descriptionText ?: "",
            action = content.primaryActionText ?: "",
            action2 = content.secondaryActionText ?: ""
        )
        is Medium -> Message(
            illustration = content.placeholder?.drawable() ?: 0,
            title = content.titleText ?: "",
            subtitle = content.descriptionText ?: ""
        )
        else -> Message(
                illustration = 0,
                title = "",
                subtitle = ""
            )
    }
}

private fun Placeholder.drawable(): Int {
    return when (this) {
        ANNOUNCE -> com.schoolonair.wallet.component.resources.R.drawable.ic_announce
        DDG_ANNOUNCE -> com.schoolonair.wallet.component.resources.R.drawable.logo_full
        CRITICAL_UPDATE -> com.schoolonair.wallet.component.resources.R.drawable.ic_critical_update
        APP_UPDATE -> com.schoolonair.wallet.component.resources.R.drawable.ic_app_update
    }
}

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



package com.mangala.autofill.ui

import com.mangala.autofill.CredentialAutofillDialogFactory
import com.mangala.autofill.CredentialAutofillPickerDialog
import com.mangala.autofill.CredentialSavePickerDialog
import com.mangala.autofill.CredentialUpdateExistingCredentialsDialog
import com.mangala.autofill.domain.app.LoginCredentials
import com.mangala.autofill.ui.credential.saving.AutofillSavingCredentialsDialogFragment
import com.mangala.autofill.ui.credential.saving.AutofillSavingUpdatingExistingCredentialsDialogFragment
import com.mangala.autofill.ui.credential.selecting.AutofillSelectCredentialsDialogFragment

class CredentialAutofillDialogAndroidFactory :
    CredentialAutofillDialogFactory {

    override fun autofillSelectCredentialsDialog(url: String, credentials: List<LoginCredentials>): CredentialAutofillPickerDialog {
        return AutofillSelectCredentialsDialogFragment.instance(url, credentials)
    }

    override fun autofillSavingCredentialsDialog(url: String, credentials: LoginCredentials): CredentialSavePickerDialog {
        return AutofillSavingCredentialsDialogFragment.instance(url, credentials)
    }

    override fun autofillSavingUpdateCredentialsDialog(url: String, credentials: LoginCredentials): CredentialUpdateExistingCredentialsDialog {
        return AutofillSavingUpdatingExistingCredentialsDialogFragment.instance(url, credentials)
    }
}

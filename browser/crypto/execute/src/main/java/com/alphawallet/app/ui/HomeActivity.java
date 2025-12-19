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

package com.alphawallet.app.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alphawallet.app.R;

//import dagger.hilt.android.AndroidEntryPoint;

//@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    public static final int RC_DOWNLOAD_EXTERNAL_WRITE_PERM = 222;
    public static final int RC_ASSET_EXTERNAL_WRITE_PERM = 223;
    public static final int RC_ASSET_NOTIFICATION_PERM = 224;

    public static final int DAPP_BARCODE_READER_REQUEST_CODE = 1;
    public static final int DAPP_TRANSACTION_SEND_REQUEST = 2;
    public static final String STORED_PAGE = "currentPage";
    public static final String RESET_TOKEN_SERVICE = "HOME_reset_ts";
    public static final String AW_MAGICLINK = "aw.app/";
    public static final String AW_MAGICLINK_DIRECT = "openurl?url=";

    public static final String KEY_ADDRESS = "key_address";
    public static final String KEY_MODE = "mode";
    public static final String OVERRIDE_DEFAULT = "override";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}

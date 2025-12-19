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

package com.alphawallet.app.entity;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class TokenManageType {

    //Define the list of accepted constants
    @IntDef({SHOW_ZERO_BALANCE,
            LABEL_DISPLAY_TOKEN, DISPLAY_TOKEN,
            LABEL_HIDDEN_TOKEN, HIDDEN_TOKEN,
            LABEL_POPULAR_TOKEN, POPULAR_TOKEN})

    //Tell the compiler not to store annotation data in the .class file
    @Retention(RetentionPolicy.SOURCE)
    //Declare the TokenManager annotation
    public @interface ManageType {}

    //Declare the constants
    public static final int SHOW_ZERO_BALANCE = 0;
    public static final int LABEL_DISPLAY_TOKEN = 1;
    public static final int DISPLAY_TOKEN = 2;
    public static final int LABEL_HIDDEN_TOKEN = 3;
    public static final int HIDDEN_TOKEN = 4;
    public static final int LABEL_POPULAR_TOKEN = 5;
    public static final int POPULAR_TOKEN = 6;

    @ManageType
    private int mType;

    @ManageType
    public int getTokenManageType(){
        return mType;
    }

    public void setTokenManageType(@ManageType int type){
        mType = type;
    }
}

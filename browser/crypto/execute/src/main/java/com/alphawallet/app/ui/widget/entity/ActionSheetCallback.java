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

package com.alphawallet.app.ui.widget.entity;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.alphawallet.app.entity.SignAuthenticationCallback;
import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.web3.entity.Web3Transaction;

/**
 * Created by JB on 27/11/2020.
 */
public interface ActionSheetCallback
{
    void getAuthorisation(SignAuthenticationCallback callback);
    void sendTransaction(Web3Transaction tx);
    void dismissed(String txHash, long callbackId, boolean actionCompleted);
    void notifyConfirm(String mode);
    ActivityResultLauncher<Intent> gasSelectLauncher();
    default void signTransaction(Web3Transaction tx) { } // only WalletConnect uses this so far

    default void buttonClick(long callbackId, Token baseToken) { }; //for message only actionsheet

    default void notifyWalletConnectApproval(long chainId) { };    // used by WalletConnectRequest
    default void denyWalletConnect() { };
    default void openChainSelection() { };      // used by WalletConnectRequest
    default void buttonClick(String action, int Id) { };    // for passing
}

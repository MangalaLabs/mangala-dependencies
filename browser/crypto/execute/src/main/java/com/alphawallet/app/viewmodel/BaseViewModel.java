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

package com.alphawallet.app.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphawallet.app.C;
import com.alphawallet.app.entity.ErrorEnvelope;
import com.alphawallet.app.entity.ServiceException;
import com.alphawallet.app.entity.tokens.Token;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class BaseViewModel extends ViewModel
{
	protected final MutableLiveData<ErrorEnvelope> error = new MutableLiveData<>();
	protected final MutableLiveData<Boolean> progress = new MutableLiveData<>();
	protected Disposable disposable;
	protected static final MutableLiveData<Integer> queueCompletion = new MutableLiveData<>();
	protected static final MutableLiveData<String> pushToastMutable = new MutableLiveData<>();
	protected static final MutableLiveData<Integer> successDialogMutable = new MutableLiveData<>();
	protected static final MutableLiveData<Integer> errorDialogMutable = new MutableLiveData<>();
	protected static final MutableLiveData<Boolean> refreshTokens = new MutableLiveData<>();

	@Override
	protected void onCleared()
	{
		cancel();
	}

	private void cancel()
	{
		if (disposable != null && !disposable.isDisposed())
		{
			disposable.dispose();
		}
	}

	public LiveData<ErrorEnvelope> error()
	{
		return error;
	}

	public LiveData<Boolean> progress()
	{
		return progress;
	}

	public LiveData<Integer> queueProgress()
	{
		return queueCompletion;
	}

	public LiveData<String> pushToast()
	{
		return pushToastMutable;
	}

	public LiveData<Boolean> refreshTokens() {
		return refreshTokens;
	}

	protected void onError(Throwable throwable)
	{
		Timber.tag("TAG").d(throwable, "Err");
		if (throwable instanceof ServiceException)
		{
			error.postValue(((ServiceException) throwable).error);
		}
		else
		{
			String message = throwable.getMessage();
			if (TextUtils.isEmpty(message))
			{
				error.postValue(new ErrorEnvelope(C.ErrorCode.UNKNOWN, null, throwable));
			}
			else
			{
				error.postValue(new ErrorEnvelope(C.ErrorCode.UNKNOWN, message, throwable));
			}
		}
	}

	public static void onQueueUpdate(int complete)
	{
		queueCompletion.postValue(complete);
	}

	public static void onPushToast(String message)
	{
		pushToastMutable.postValue(message);
	}

	public void showSendToken(Context context, String address, String symbol, int decimals, Token token) {
		//do nothing
	}

	public void showTokenList(Activity activity, Token token) {
		//do nothing
	}

	public void showErc20TokenDetail(Activity context, String address, String symbol, int decimals, Token token) {
		//do nothing
	}
}

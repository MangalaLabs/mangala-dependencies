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

package com.alphawallet.app.service;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.alphawallet.app.BuildConfig;
import com.alphawallet.app.C;
import com.alphawallet.app.entity.AnalyticsProperties;
import com.alphawallet.app.entity.ServiceErrorException;

import org.json.JSONException;
import org.json.JSONObject;

public class AnalyticsService<T> implements AnalyticsServiceType<T> {

//    private final MixpanelAPI mixpanelAPI;
//    private final FirebaseAnalytics firebaseAnalytics;

    public static native String getAnalyticsKey();

    static {
        System.loadLibrary("keys");
    }

    public AnalyticsService()
    {
//        mixpanelAPI = MixpanelAPI.getInstance(context, getAnalyticsKey());
//        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void track(String eventName)
    {
        //firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, eventName);
//        mixpanelAPI.track(eventName);
    }

    @Override
    public void track(String eventName, T event)
    {
        AnalyticsProperties analyticsProperties = (AnalyticsProperties) event;

        trackFirebase(analyticsProperties, eventName);
        trackMixpanel(analyticsProperties, eventName);
    }

    private void trackFirebase(AnalyticsProperties analyticsProperties, String eventName)
    {
        Bundle props = new Bundle();
        if(!TextUtils.isEmpty(analyticsProperties.getWalletType()))
        {
            props.putString(C.AN_WALLET_TYPE, analyticsProperties.getWalletType());
        }

        if(!TextUtils.isEmpty(analyticsProperties.getData()))
        {
            props.putString(C.AN_USE_GAS, analyticsProperties.getData());
        }

//        props.putString(C.APP_NAME, BuildConfig.APPLICATION_ID);

//        firebaseAnalytics.logEvent(eventName, props);
    }

    private void trackMixpanel(AnalyticsProperties analyticsProperties, String eventName)
    {
        try
        {
            JSONObject props = new JSONObject();

            if (!TextUtils.isEmpty(analyticsProperties.getWalletType()))
            {
                props.put(C.AN_WALLET_TYPE, analyticsProperties.getWalletType());
            }

            if (!TextUtils.isEmpty(analyticsProperties.getData()))
            {
                props.put(C.AN_USE_GAS, analyticsProperties.getData());
            }

//            mixpanelAPI.track(eventName, props);
        }
        catch(JSONException e)
        {
            //Something went wrong
        }
    }

    @Override
    public void identify(String uuid)
    {
//        firebaseAnalytics.setUserId(uuid);
//        mixpanelAPI.identify(uuid);
//        mixpanelAPI.getPeople().identify(uuid);
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful())
//                    {
//                        String token = Objects.requireNonNull(task.getResult()).getToken();
//                        mixpanelAPI.getPeople().setPushRegistrationId(token);
//                    }
//                });
    }

    @Override
    public void flush()
    {
        //Nothing like flush in firebase
//        mixpanelAPI.flush();
    }

    @Override
    public void recordException(ServiceErrorException e)
    {
//        FirebaseCrashlytics.getInstance().recordException(e);
    }
}

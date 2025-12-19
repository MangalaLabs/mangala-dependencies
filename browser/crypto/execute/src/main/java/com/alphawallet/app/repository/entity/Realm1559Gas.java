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

package com.alphawallet.app.repository.entity;

import com.alphawallet.app.entity.EIP1559FeeOracleResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JB on 19/01/2022.
 */
public class Realm1559Gas extends RealmObject
{
    @PrimaryKey
    private long chainId;

    private long timeStamp;
    private String resultData; //JSON format string

    public Map<Integer, EIP1559FeeOracleResult> getResult()
    {
        Type entry = new TypeToken<Map<Integer, EIP1559FeeOracleResult>>() {}.getType();
        return new Gson().fromJson(resultData, entry);
    }

    public void setResultData(Map<Integer, EIP1559FeeOracleResult> result, long ts)
    {
        //form JSON string and write to DB
        resultData = new Gson().toJson(result);
        timeStamp = ts;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }
}

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

import com.alphawallet.token.entity.SignMessageType;
import com.alphawallet.token.entity.Signable;

/**
 * Created by James on 30/01/2018.
 */

public class MessagePair implements Signable
{
    public final String selection;
    public final String message;

    public MessagePair(String selection, String message)
    {
        this.selection = selection;
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    // TODO: Question to JB: actually, do we add the prefix here?
    @Override
    public byte[] getPrehash() {
        return message.getBytes();
    }

    @Override
    public String getOrigin()
    {
        return null;
    }

    // TODO: I actually don't know where to return to … -Weiwu
    @Override
    public long getCallbackId() {
        return 0;
    }

    @Override
    public CharSequence getUserMessage() {
        return "";
    }

    @Override
    public SignMessageType getMessageType()
    {
        return null;
    }
}

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

package com.alphawallet.token.entity;

import static com.alphawallet.token.entity.MessageUtils.encodeParams;
import static com.alphawallet.token.entity.MessageUtils.encodeValues;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EthereumTypedMessage implements Signable {

    byte[] structuredData;
    String displayOrigin;
    long leafPosition;
    CharSequence userMessage;
    SignMessageType messageType;

    public EthereumTypedMessage(byte[] value, CharSequence userMessage, String displayOrigin, long leafPosition) {
        this.structuredData = value;
        this.displayOrigin = displayOrigin;
        this.leafPosition = leafPosition;
        this.userMessage = userMessage;
        messageType = SignMessageType.SIGN_ERROR;
    }

    public EthereumTypedMessage(String messageData, String domainName, long callbackId, CryptoFunctionsInterface cryptoFunctions)
    {
        try
        {
            try
            {
                ProviderTypedData[] rawData = new Gson().fromJson(messageData, ProviderTypedData[].class);
                ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream();
                writeBuffer.write(cryptoFunctions.keccak256(encodeParams(rawData)));
                writeBuffer.write(cryptoFunctions.keccak256(encodeValues(rawData)));
                this.userMessage = cryptoFunctions.formatTypedMessage(rawData);
                this.structuredData = writeBuffer.toByteArray();
                messageType = SignMessageType.SIGN_TYPED_DATA;
            }
            catch (JsonSyntaxException e)
            {
                this.structuredData = cryptoFunctions.getStructuredData(messageData);
                this.userMessage = cryptoFunctions.formatEIP721Message(messageData);
                messageType = SignMessageType.SIGN_TYPED_DATA_V3;
            }
        }
        catch (IOException e)
        {
            this.userMessage = "";
            messageType = SignMessageType.SIGN_ERROR;
            e.printStackTrace();
        }

        this.displayOrigin = domainName;
        this.leafPosition = callbackId;
    }

    // User message is the text shown in the popup window - note CharSequence is used because message contains text formatting
    public CharSequence getUserMessage() {
        return userMessage;
    }

    public long getCallbackId() {
        return this.leafPosition;
    }

    public byte[] getPrehash() {
        return structuredData;
    }

    @Override
    public String getOrigin()
    {
        return displayOrigin;
    }

    @Override
    public String getMessage()
    {
        return userMessage.toString();
    }

    @Override
    public SignMessageType getMessageType()
    {
        return messageType;
    }
}

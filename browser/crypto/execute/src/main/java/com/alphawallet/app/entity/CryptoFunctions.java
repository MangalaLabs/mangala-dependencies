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

import android.util.Base64;

import com.alphawallet.app.util.Utils;
import com.alphawallet.app.web3j.StructuredDataEncoder;
import com.alphawallet.token.entity.CryptoFunctionsInterface;
import com.alphawallet.token.entity.ProviderTypedData;

import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Arrays;

import timber.log.Timber;
import wallet.core.jni.Hash;

public class CryptoFunctions implements CryptoFunctionsInterface
{
    @Override
    public byte[] Base64Decode(String message)
    {
        return Base64.decode(message, Base64.URL_SAFE);
    }

    @Override
    public byte[] Base64Encode(byte[] data)
    {
        return Base64.encode(data, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    @Override
    public BigInteger signedMessageToKey(byte[] data, byte[] signature) throws SignatureException
    {
        Sign.SignatureData sigData = sigFromByteArray(signature);
        if (sigData == null) return BigInteger.ZERO;
        return Sign.signedMessageToKey(data, sigData);
    }

    @Override
    public String getAddressFromKey(BigInteger recoveredKey)
    {
        return Keys.getAddress(recoveredKey);
    }

    @Override
    public byte[] keccak256(byte[] message)
    {
        return Hash.keccak256(message);
    }

    @Override
    public CharSequence formatTypedMessage(ProviderTypedData[] rawData)
    {
        return Utils.formatTypedMessage(rawData);
    }

    @Override
    public CharSequence formatEIP721Message(String messageData)
    {
        CharSequence msgData = "";
        try
        {
            StructuredDataEncoder eip721Object = new StructuredDataEncoder(messageData);
            msgData = Utils.formatEIP721Message(eip721Object);
        }
        catch (Exception e)
        {
            Timber.e(e);
        }

        return msgData;
    }

    @Override
    public byte[] getStructuredData(String messageData)
    {
        try
        {
            StructuredDataEncoder eip721Object = new StructuredDataEncoder(messageData);
            return eip721Object.getStructuredData();
        }
        catch (Exception e)
        {
            Timber.e(e);
        }

        return new byte[0];
    }

    public static Sign.SignatureData sigFromByteArray(byte[] sig)
    {
        if (sig.length < 64 || sig.length > 65) return null;

        byte   subv = sig[64];
        if (subv < 27) subv += 27;

        byte[] subrRev = Arrays.copyOfRange(sig, 0, 32);
        byte[] subsRev = Arrays.copyOfRange(sig, 32, 64);

        BigInteger r = new BigInteger(1, subrRev);
        BigInteger s = new BigInteger(1, subsRev);

        return new Sign.SignatureData(subv, subrRev, subsRev);
    }
}

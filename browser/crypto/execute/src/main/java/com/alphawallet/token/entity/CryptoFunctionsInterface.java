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

import java.math.BigInteger;
import java.security.SignatureException;

public interface CryptoFunctionsInterface
{
    byte[] Base64Decode(String message);
    byte[] Base64Encode(byte[] data);
    BigInteger signedMessageToKey(byte[] data, byte[] signature) throws SignatureException;
    String getAddressFromKey(BigInteger recoveredKey);
    byte[] keccak256(byte[] message);
    CharSequence formatTypedMessage(ProviderTypedData[] rawData); // see class Utils: Uses Android text formatting
    CharSequence formatEIP721Message(String messageData);         // see class Utils: Uses web3j: you need to provide this function to decode EIP712.
                                                                  // --- Currently web3j uses a different library for Android and Generic Java packages.
                                                                  // --- One day web3j could be united, then we can remove these functions
    byte[] getStructuredData(String messageData);                 // see class Utils: Uses web3j
}

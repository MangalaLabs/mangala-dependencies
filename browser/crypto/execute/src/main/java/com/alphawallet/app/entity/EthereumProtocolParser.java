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

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * Created by James on 22/02/2019.
 * Stormbird in Singapore
 */
public class EthereumProtocolParser
{
    public static final int ADDRESS_LENGTH = 42;
    public EthereumProtocolParser()
    {

    }

    public QRResult readProtocol(String protocol, String data)
    {
        QRResult result = null;
        List<EthTypeParam> args = new ArrayList<>();
        try
        {
            List<DataItem> stream = tokeniseStream(data);
            if (stream.size() == 0) return null;
            DataItem address = stream.get(0);
            if (address.type != DataType.STRING) return null;
            if (address.value.length() < 2) return null;
            if (address.value.startsWith("0x") && address.value.length() != ADDRESS_LENGTH) return null;

            result = new QRResult(protocol, address.value);
            ParseState readState = ParseState.ADDRESS;

            String type = null;
            boolean readError = false;

            for (DataItem item : stream)
            {
                switch (item.type)
                {
                    case SLASH:
                        readState = ParseState.FUNCTION;
                        break;
                    case QUESTION:
                        //no action, keep state the same
                        break;
                    case AT:
                        readState = ParseState.CHAIN_ID;
                        break;
                    case AMPERSAND:
                        readState = ParseState.READ_DIRECTIVE;
                        break;

                    case STRING:
                        switch (readState)
                        {
                            case ADDRESS:
                                readState = ParseState.READ_DIRECTIVE; //we already read the address
                                break;
                            case FUNCTION:
                                result.setFunction(item.value);
                                readState = ParseState.READ_TYPE;
                                break;
                            case READ_PARAM_VALUE:
                                if (type != null)
                                {
                                    args.add(new EthTypeParam(type, item.value));
                                }
                                readState = ParseState.READ_DIRECTIVE; //reset
                                type = null;
                                break;
                            case VALUE:
                                result.weiValue = item.getValueBI();
                                readState = ParseState.READ_DIRECTIVE;
                                break;
                            case GAS_PRICE:
                                result.gasPrice = item.getValueBI();
                                readState = ParseState.READ_DIRECTIVE;
                                break;
                            case GAS_LIMIT:
                                result.gasLimit = item.getValueBI();
                                readState = ParseState.READ_DIRECTIVE;
                                break;
                            case CHAIN_ID:
                                result.chainId = item.getValueBI().longValue();
                                readState = ParseState.READ_DIRECTIVE;
                                break;
                            case READ_TYPE:
                                type = item.value;
                                readState = ParseState.READ_PARAM_VALUE;
                                break;
                            case READ_DIRECTIVE:
                                readState = interpretDirective(item);
                                if (readState == ParseState.READ_PARAM_VALUE) type = item.value;
                                break;
                            case ERROR:
                                throw new Exception("Invalid QR code: " + data);
                        }
                        break;
                    default:
                        break;
                }
            }
            result.createFunctionPrototype(args);
        }
        catch (Exception e)
        {
            Timber.e(e);
            return null;
        }

        return result;
    }

    private ParseState interpretDirective(DataItem item)
    {
        switch (item.value)
        {
            case "value":
                return ParseState.VALUE;
            case "gasPrice":
                return ParseState.GAS_PRICE;
            case "gasLimit":
                return ParseState.GAS_LIMIT;
            default:
                return ParseState.READ_PARAM_VALUE;
        }
    }

    private List<DataItem> tokeniseStream(String data)
    {
        List<DataItem> stream = new ArrayList<>();
        String[] tokens = data.split("(?=[/&@?=])");

        for (String item : tokens)
        {
            if (TextUtils.isEmpty(item)) continue;
            switch (item.charAt(0))
            {
                case '@':
                    stream.add(new DataItem(DataType.AT));
                    addString(stream, item);
                    break;
                case '/':
                    stream.add(new DataItem(DataType.SLASH));
                    addString(stream, item);
                    break;
                case '?':
                    stream.add(new DataItem(DataType.QUESTION));
                    addString(stream, item);
                    break;
                case '=':
                    stream.add(new DataItem(DataType.EQUAL));
                    addString(stream, item);
                    break;
                case '&':
                    stream.add(new DataItem(DataType.AMPERSAND));
                    addString(stream, item);
                    break;
                default:
                    stream.add(new DataItem(item));
                    break;
            }
        }

        return stream;
    }

    private void addString(List<DataItem> stream, String item)
    {
        if (item.length() > 1)
        {
            stream.add(new DataItem(item.substring(1)));
        }
    }

    private class DataItem
    {
        public DataType type;
        public String value;

        public DataItem(DataType d)
        {
            type = d;
            value = null;
        }

        public DataItem(String v)
        {
            type = DataType.STRING;
            value = v;
        }

        public BigInteger getValueBI()
        {
            try
            {
                return new BigDecimal(value).toBigInteger();
            }
            catch (Exception e)
            {
                return BigInteger.ZERO;
            }
        }
    }

    private enum DataType
    {
        SLASH, AT, QUESTION, EQUAL, AMPERSAND, STRING
    }

    private enum ParseState
    {
        ADDRESS,
        VALUE,
        FUNCTION,
        PARAM,
        GAS_PRICE,
        GAS_LIMIT,
        CHAIN_ID,
        READ_TYPE,
        READ_PARAM_VALUE,
        READ_DIRECTIVE,

        ERROR
    }
}

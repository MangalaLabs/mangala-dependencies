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

import com.alphawallet.token.tools.Numeric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JB on 27/08/2020.
 */
public class MessageUtils
{
    /**
     * Encode params for hashing - the algorithm is very simple, reduce the types like this:
     * (string Message, uint32 value, bytes32 data) into a string list like this:
     * "string Messageuint32 valuebytes32 data"
     *
     * @param rawData
     * @return
     */
    public static byte[] encodeParams(ProviderTypedData[] rawData)
    {
        //form the params for hashing
        StringBuilder sb = new StringBuilder();
        int len = rawData.length;
        for (int i = 0; i < len; i++)
        {
            sb.append(rawData[i].type).append(" ").append(rawData[i].name);
        }

        return sb.toString().getBytes();
    }

    /**
     * This routine ported from the reference implementation code at https://github.com/MetaMask/eth-sig-util
     *
     * @param rawData
     * @return
     */
    public static byte[] encodeValues(ProviderTypedData[] rawData) throws IOException
    {
        int size;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        EthereumWriteBuffer eb = new EthereumWriteBuffer(buffer);

        for (ProviderTypedData data : rawData)
        {
            String type = data.type;
            String value = (String) data.value;

            if (type.equals("bytes"))
            {
                eb.write(Numeric.hexStringToByteArray(value));
            }
            else if (type.equals("string"))
            {
                eb.write(value.getBytes());
            }
            else if (type.equals("bool"))
            {
                eb.write(((boolean) data.value) ? (byte) 0x01 : (byte) 0x00);
            }
            else if (type.equals("address"))
            {
                eb.writeAddress(value);
            }
            else if (type.startsWith("bytes"))
            {
                size = parseTypeN(type);
                if (size < 1 || size > 32)
                {
                    throw new NumberFormatException("Invalid bytes<N> width: " + size);
                }

                eb.writeBytes(value, size);
            }
            else if (type.startsWith("uint"))
            {
                size = parseTypeN(type);
                if ((size < 8) || (size > 256))
                {
                    throw new NumberFormatException("Invalid uint<N> width: " + size);
                }

                eb.writeValue(value, size / 8);
            }
            else if (type.startsWith("int"))
            {
                size = parseTypeN(type);
                if ((size < 8) || (size > 256))
                {
                    throw new NumberFormatException("Invalid uint<N> width: " + size);
                }

                eb.writeValue(value, size / 8);
            }
            else
            {
                // FIXME: support all other types
                throw new NumberFormatException("Unsupported or invalid type: " + type);
            }
        }

        return buffer.toByteArray();
    }

    private static int parseTypeN(String type)
    {
        Matcher m = Pattern.compile("^\\D+(\\d+)$").matcher(type);
        if (m.find())
        {
            String match = m.group(1);
            if (match != null && match.length() > 0)
            {
                return Integer.parseInt(match);
            }
        }

        return 256; //if no value then default to 256
    }
}

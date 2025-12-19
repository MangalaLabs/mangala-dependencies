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

import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by James on 14/05/2019.
 * Stormbird in Sydney
 */
public class TokenScriptResult
{
    public static final class Attribute {
        public final String id;
        public String name;
        public String text;
        public final BigInteger value;
        public final boolean userInput;
        public Attribute(String attributeId, String name, BigInteger value, String text) {
            this.id = attributeId;
            this.name = name;
            this.text = text;
            this.value = value;
            this.userInput = false;
        }

        public Attribute(String attributeId, String name, BigInteger value, String text, boolean userInput) {
            this.id = attributeId;
            this.name = name;
            this.text = text;
            this.value = value;
            this.userInput = userInput;
        }
    }

    private Map<String, Attribute> attrs = new HashMap<>();

    public void setAttribute(String key, Attribute attr)
    {
        attrs.put(key, attr);
    }

    public Map<String, Attribute> getAttributes()
    {
        return attrs;
    }

    public Attribute getAttribute(String attributeId) {
        if (attrs != null)
        {
            return attrs.get(attributeId);
        }
        else
        {
            return null;
        }
    }

    public static <T> void addPair(StringBuilder attrs, String attrId, T attrValue)
    {
        attrs.append(attrId);
        attrs.append(": ");

        if (attrValue == null)
        {
            attrs.append("\"\"");
        }
        else if (attrValue instanceof BigInteger)
        {
            attrs.append("\"");
            attrs.append(((BigInteger)attrValue).toString(10));
            attrs.append("\"");
        }
        else if (attrValue instanceof List)
        {
            attrs.append("\'");
            attrs.append(new Gson().toJson(attrValue));
            attrs.append("\'");
        }
        else
        {
            String attrValueStr = (String) attrValue;
            if (attrValueStr.length() == 0 || (attrValueStr.charAt(0) != '{')) attrs.append("\"");
            attrs.append(attrValueStr);
            if (attrValueStr.length() == 0 || (attrValueStr.charAt(0) != '{')) attrs.append("\"");
        }

        attrs.append(",\n");
    }
}

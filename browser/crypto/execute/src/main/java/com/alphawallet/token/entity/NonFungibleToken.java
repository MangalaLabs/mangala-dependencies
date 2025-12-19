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

import com.alphawallet.token.tools.TokenDefinition;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiwu on 1/3/18.  Each NonFungibleToken is a
 * non-fungible token identified by a byte32 tokenID (other forms of
 * IDs may be added if tests proves that they can be more efficient).
 */

public class NonFungibleToken
{
    public BigInteger id;

    public static final class Attribute {
        public final String id;
        public String name;
        public String text;
        public final BigInteger value;
        public Attribute(String attributeId, String name, BigInteger value, String text) {
            this.id = attributeId;
            this.name = name;
            this.text = text;
            this.value = value;
        }
    }

    protected HashMap<String, Attribute> attributes;

    public HashMap<String, Attribute> getAttributes()
    {
        return attributes;
    }

    public Attribute getAttribute(String attributeId) {
        if (attributes != null)
        {
            return attributes.get(attributeId);
        }
        else
        {
            return null;
        }
    }

    public void setAttribute(String attributeId, Attribute attribute) {
        attributes.put(attributeId, attribute);
    }

    public NonFungibleToken(BigInteger tokenId, TokenDefinition ad, Map<String, FunctionDefinition> functionMappings) {
        this(tokenId);
        ad.parseField(tokenId, this, functionMappings);
    }

    public NonFungibleToken(BigInteger tokenId, TokenScriptResult tsr) {
        this(tokenId);
        for (TokenScriptResult.Attribute attr : tsr.getAttributes().values())
        {
            attributes.put(attr.id, new Attribute(attr.id, attr.name, attr.value, attr.text));
        }
    }

    public NonFungibleToken(BigInteger tokenId, TokenDefinition ad) {
        this(tokenId);
        ad.parseField(tokenId, this);
    }

    public NonFungibleToken(BigInteger tokenId) {
        id = tokenId;
        attributes = new HashMap<>();
    }

    public String getRangeStr(TicketRange data)
    {
        int ticketStart = getAttribute("category").value.intValue();
        String ticketRange = String.valueOf(ticketStart);
        if (data.tokenIds != null)
        {
            int lastValue = ticketStart + (data.tokenIds.size() - 1);
            if (data.tokenIds.size() > 1)
            {
                ticketRange = ticketRange + "-" + lastValue;
            }
        }

        return ticketRange;
    }
}

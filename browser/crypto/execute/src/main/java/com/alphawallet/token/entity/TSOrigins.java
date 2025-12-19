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

import org.xml.sax.SAXException;

/**
 * Created by JB on 27/07/2020.
 */
public class TSOrigins
{
    private TSOriginType type;
    private String originName;
    private EventDefinition event;

    public static class Builder
    {
        private TSOriginType type;
        private String originName;
        private EventDefinition ev;

        public Builder(TSOriginType type)
        {
            this.type = type;
            this.ev = null;
        }

        public Builder name(String name)
        {
            this.originName = name;
            return this;
        }

        public Builder event(EventDefinition event)
        {
            this.ev = event;
            return this;
        }

        public TSOrigins build() throws SAXException
        {
            TSOrigins origins = new TSOrigins();
            origins.type = this.type;
            if (originName == null) throw new SAXException("Origins must have contract or type field.");
            origins.originName = this.originName;
            if (type == TSOriginType.Event && ev == null)
            {
                throw new SAXException("Event origin must have Filter spec.");
            }

            origins.event = this.ev;

            return origins;
        }
    }

    private TSOrigins()
    {

    }

    public String getOriginName()
    {
        return originName;
    }

    public EventDefinition getOriginEvent()
    {
        return event;
    }

    public boolean isType(TSOriginType checkType)
    {
        return type == checkType;
    }
}

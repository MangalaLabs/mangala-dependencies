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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JB on 20/03/2020 for namedType in ASN.X included in TokenScript. It's used for events & attestations.
 */
public class NamedType
{
    public final String name;
    public List<SequenceElement> sequence = new ArrayList<>();

    public NamedType(String moduleName)
    {
        name = moduleName;
    }

    public void addSequenceElement(Element element, String sequenceName) throws SAXException
    {
        SequenceElement se = new SequenceElement();

        for (int i = 0; i < element.getAttributes().getLength(); i++)
        {
            Node thisAttr = element.getAttributes().item(i);
            String value = thisAttr.getNodeValue();
            switch (thisAttr.getLocalName())
            {
                case "type":
                    se.type = value;
                    break;
                case "name":
                    se.name = value;
                    break;
                case "indexed":
                    if (value.equalsIgnoreCase("true")) se.indexed = true;
                    break;
                default:
                    throw new SAXException("Unexpected event attribute in: " + sequenceName + " attribute: " + thisAttr.getLocalName());
            }
        }

        if (se.type == null || se.type.length() == 0)
        {
            throw new SAXException("Malformed sequence element in: " + sequenceName + " name: " + se.name);
        }
        else if (se.name == null || se.name.length() == 0)
        {
            throw new SAXException("Malformed sequence element in: " + sequenceName + " type: " + se.type);
        }

        sequence.add(se);
    }


    public List<SequenceElement> getSequenceArgs()
    {
        return sequence;
    }

    public List<String> getArgNames(boolean indexed)
    {
        List<String> argNameIndexedList = new ArrayList<>();
        for (SequenceElement se : sequence)
        {
            if (se.indexed == indexed)
            {
                argNameIndexedList.add(se.name);
            }
        }

        return argNameIndexedList;
    }

    int getTopicIndex(String filterTopic)
    {
        int topicIndex = -1;
        int currentIndex = 0;
        for (SequenceElement se : sequence)
        {
            if (se.indexed)
            {
                if (se.name.equals(filterTopic))
                {
                    topicIndex = currentIndex;
                    break;
                }
                else
                {
                    currentIndex++;
                }
            }
        }

        return topicIndex;
    }

    int getNonIndexedIndex(String topic)
    {
        int topicIndex = -1;
        int currentIndex = 0;
        for (SequenceElement se : sequence)
        {
            if (!se.indexed)
            {
                if (se.name.equals(topic))
                {
                    topicIndex = currentIndex;
                    break;
                }
                else
                {
                    currentIndex++;
                }
            }
        }

        return topicIndex;
    }

    public class SequenceElement
    {
        public String name;
        public String type;
        public boolean indexed = false;
    }
}

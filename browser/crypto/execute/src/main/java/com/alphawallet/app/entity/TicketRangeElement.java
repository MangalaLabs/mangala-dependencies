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

import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.service.AssetDefinitionService;
import com.alphawallet.token.entity.TokenScriptResult;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class TicketRangeElement
{
    public final BigInteger id;
    public final long time;

    public TicketRangeElement(AssetDefinitionService assetService, Token token, BigInteger v)
    {
        TokenScriptResult.Attribute timeAttr = assetService.getAttribute(token, v, "time");
        id = v;

        if (timeAttr != null)
        {
            time = timeAttr.value.longValue();
        }
        else
        {
            time = 0;
        }
    }

    public static void sortElements(List<TicketRangeElement> elementList)
    {
        Collections.sort(elementList, (e1, e2) -> {
            long w1 = e1.time;
            long w2 = e2.time;
            if (e1.time == 0 && e2.time == 0)
            {
                w1 = e1.id.longValue();
                w2 = e2.id.longValue();
            }
            return Long.compare(w1, w2);
        });
    }
}

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

package com.alphawallet.app.ui.widget.entity;

import static com.alphawallet.app.repository.EthereumNetworkBase.getChainOrdinal;

import com.alphawallet.app.entity.tokendata.TokenGroup;

/**
 * Created by JB on 10/01/2022.
 */
public class TokenPosition
{
    //position consists of group, chain and weighting
    public final TokenGroup group;
    public final int chainOrdinal;
    public final long weighting;
    public final boolean isGroupHeader;
    public final boolean singleton;

    public TokenPosition(TokenGroup group, long chainId, long weighting, boolean isGroupHeader)
    {
        this.group = group;
        this.chainOrdinal = getChainOrdinal(chainId);
        this.weighting = weighting;
        this.isGroupHeader = isGroupHeader;
        this.singleton = false;
    }

    public TokenPosition(TokenGroup group, long chainId, long weighting)
    {
        this.group = group;
        this.chainOrdinal = getChainOrdinal(chainId);
        this.weighting = weighting;
        this.isGroupHeader = false;
        this.singleton = false;
    }

    public TokenPosition(long weighting)
    {
        this.group = TokenGroup.ASSET;
        this.chainOrdinal = 1;
        this.weighting = weighting;
        this.isGroupHeader = false;
        this.singleton = true;
    }

    public int compare(TokenPosition other)
    {
        return compare(other, Long.compare(weighting, other.weighting));
    }

    private int compareGroupHeader(TokenPosition other)
    {
        if (other.isGroupHeader)
        {
            return group.compareTo(other.group);
        }
        else
        {
            if (group != other.group)
            {
                return group.compareTo(other.group);
            }
            else
            {
                return -1;
            }
        }
    }

    public int compare(TokenPosition other, int valueCompare)
    {
        if (weighting == 0 || singleton || other.singleton) //zero weighting always at top; only compare weighting if one of the items is a singleton
        {
            return Long.compare(weighting, other.weighting);
        }
        else if (isGroupHeader)
        {
            return compareGroupHeader(other);
        }
        else //normal compare, use weighting
        {
            //first compare group headers
            if (group != other.group)
            {
                return Integer.compare(group.ordinal(), other.group.ordinal());
            }
            //next compare chain ordinals
            else if (chainOrdinal != other.chainOrdinal)
            {
                return Integer.compare(chainOrdinal, other.chainOrdinal);
            }
            else
            {
                return valueCompare;
            }
        }
    }
}

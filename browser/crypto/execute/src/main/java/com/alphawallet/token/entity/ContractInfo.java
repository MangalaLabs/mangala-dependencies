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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by James on 2/05/2019.
 * Stormbird in Sydney
 */
public class ContractInfo
{
    public final String contractInterface;
    public final Map<Long, List<String>> addresses = new HashMap<>();

    public ContractInfo(String contractType, Map<Long, List<String>> addresses)
    {
        this.contractInterface = contractType;
        this.addresses.putAll(addresses);
    }

    public ContractInfo(String contractType)
    {
        this.contractInterface = contractType;
    }

    public boolean hasContractTokenScript(long chainId, String address)
    {
        List<String> addrs = addresses.get(chainId);
        return addrs != null && addrs.contains(address);
    }

    public long getfirstChainId()
    {
        if (addresses.keySet().size() > 0) return addresses.keySet().iterator().next();
        else return 0;
    }

    public String getFirstAddress()
    {
        long chainId = getfirstChainId();
        return addresses.get(chainId).size() > 0 ? addresses.get(chainId).get(0) : "";
    }
}

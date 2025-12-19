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

package com.alphawallet.app.service;

import android.text.TextUtils;

import com.alphawallet.app.repository.TokenRepository;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class NodeService
{
    private static ConcurrentHashMap<Long, BigInteger> currentBlocks;

    public static void updateCurrentBlock(final long chainId)
    {
        if (currentBlocks == null) initBlocks();
        fetchCurrentBlock(chainId).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(blockValue -> currentBlocks.put(chainId, blockValue), onError -> currentBlocks.put(chainId, BigInteger.ZERO)).isDisposed();
    }

    public static BigInteger getCurrentBlock(long chainId)
    {
//        if (currentBlocks == null) initBlocks();
//        if (!currentBlocks.containsKey(chainId))
//        {
//            currentBlocks.put(chainId, fetchCurrentBlock(chainId).blockingGet());
//        }
//
//        return currentBlocks.get(chainId);
        return NodeService.getCurrentBlock(chainId);

    }

    private static Single<BigInteger> fetchCurrentBlock(final long chainId)
    {
        return Single.fromCallable(() -> {
            Web3j web3j = TokenRepository.getWeb3jService(chainId);
            EthBlock ethBlock =
                    web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            String blockValStr = ethBlock.getBlock().getNumberRaw();
            if (!TextUtils.isEmpty(blockValStr) && blockValStr.length() > 2)
            {
                return Numeric.toBigInt(blockValStr);
            }
            else if (currentBlocks.containsKey(chainId))
            {
                return currentBlocks.get(chainId);
            }
            else
            {
                return BigInteger.ZERO;
            }
        });
    }

    private static void initBlocks()
    {
        currentBlocks = new ConcurrentHashMap<>();
    }
}

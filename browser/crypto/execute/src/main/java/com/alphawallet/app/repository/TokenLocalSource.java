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

package com.alphawallet.app.repository;

import android.util.Pair;

import com.alphawallet.app.entity.ContractType;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.entity.nftassets.NFTAsset;
import com.alphawallet.app.entity.tokendata.TokenGroup;
import com.alphawallet.app.entity.tokendata.TokenTicker;
import com.alphawallet.app.entity.tokens.Token;
import com.alphawallet.app.entity.tokens.TokenCardMeta;
import com.alphawallet.app.service.AssetDefinitionService;
import com.alphawallet.token.entity.ContractAddress;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.realm.Realm;

public interface TokenLocalSource {
    Single<Token> saveToken(Wallet wallet, Token token);
    Single<Token[]> saveTokens(Wallet wallet, Token[] items);
    boolean updateTokenBalance(Wallet wallet, Token token, BigDecimal balance, List<BigInteger> balanceArray);
    Token fetchToken(long chainId, Wallet wallet, String address);
    void setEnable(Wallet wallet, Token token, boolean isEnabled);
    String getTokenImageUrl(long chainId, String address);
    void deleteRealmToken(long chainId, Wallet wallet, String address);
    void storeTokenUrl(long chainId, String address, String imageUrl);
    Token[] initNFTAssets(Wallet wallet, Token[] tokens);

    Single<TokenCardMeta[]> fetchTokenMetas(Wallet wallet, List<Long> networkFilters,
                                            AssetDefinitionService svs);

    Single<TokenCardMeta[]> fetchAllTokenMetas(Wallet wallet, List<Long> networkFilters,
                                             String seachTerm);

    TokenCardMeta[] fetchTokenMetasForUpdate(Wallet wallet, List<Long> networkFilters);

    Single<Token[]> fetchAllTokensWithNameIssue(String walletAddress, List<Long> networkFilters);
    Single<ContractAddress[]> fetchAllTokensWithBlankName(String walletAddress, List<Long> networkFilters);

    Single<Integer> fixFullNames(Wallet wallet, AssetDefinitionService svs);

    void updateEthTickers(Map<Long, TokenTicker> ethTickers);
    void updateERC20Tickers(long chainId, Map<String, TokenTicker> erc20Tickers);
    void removeOutdatedTickers();

    Realm getRealmInstance(Wallet wallet);
    Realm getTickerRealmInstance();

    TokenTicker getCurrentTicker(Token token);
    TokenTicker getCurrentTicker(String key);

    void setVisibilityChanged(Wallet wallet, Token token);

    boolean hasVisibilityBeenChanged(Token token);
    boolean getEnabled(Token token);

    void updateNFTAssets(String wallet, Token erc721Token, List<BigInteger> additions, List<BigInteger> removals);
    void storeAsset(String wallet, Token token, BigInteger tokenId, NFTAsset asset);

    int storeTokensMapping(Pair<Map<String, ContractAddress>, Map<String, TokenGroup>> mappings);
    long getLastMappingsUpdate();

    Single<Pair<Double, Double>> getTotalValue(String currentAddress, List<Long> networkFilters);

    Map<String, Long> getTickerTimeMap(long chainId, List<TokenCardMeta> erc20Tokens);

    void deleteTickers();

    Single<List<String>> getTickerUpdateList(List<Long> networkFilter);

    ContractAddress getBaseToken(long chainId, String address);
    TokenGroup getTokenGroup(long chainId, String address, ContractType type);

    void updateTicker(long chainId, String address, TokenTicker ticker);
}

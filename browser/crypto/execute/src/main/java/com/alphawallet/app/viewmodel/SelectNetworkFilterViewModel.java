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

package com.alphawallet.app.viewmodel;

import com.alphawallet.app.entity.NetworkInfo;
import com.alphawallet.app.interact.GenericWalletInteract;
import com.alphawallet.app.repository.EthereumNetworkRepository;
import com.alphawallet.app.repository.EthereumNetworkRepositoryType;
import com.alphawallet.app.repository.PreferenceRepositoryType;
import com.alphawallet.app.service.TokensService;
import com.alphawallet.app.ui.widget.entity.NetworkItem;

import java.util.ArrayList;
import java.util.List;


public class SelectNetworkFilterViewModel extends BaseViewModel {
    private final EthereumNetworkRepositoryType networkRepository;
    private final TokensService tokensService;
    private final PreferenceRepositoryType preferenceRepository;

    public SelectNetworkFilterViewModel(EthereumNetworkRepositoryType ethereumNetworkRepositoryType,
                                        TokensService tokensService,
                                        PreferenceRepositoryType preferenceRepository) {
        this.networkRepository = ethereumNetworkRepositoryType;
        this.tokensService = tokensService;
        this.preferenceRepository = preferenceRepository;
    }

    public NetworkInfo[] getNetworkList() {
        return networkRepository.getAvailableNetworkList();
    }

    public void setFilterNetworks(List<Long> selectedItems, boolean mainnetActive, boolean hasSelected, boolean shouldBlankUserSelection)
    {
        preferenceRepository.setActiveMainnet(mainnetActive);

        NetworkInfo activeNetwork = networkRepository.getActiveBrowserNetwork();
        long activeNetworkId = -99;
        if (activeNetwork != null) {
            activeNetworkId = networkRepository.getActiveBrowserNetwork().chainId;
        }

        //Mark dappbrowser network as deselected if appropriate
        boolean deselected = true;
        Long[] selectedIds = new Long[selectedItems.size()];
        int index = 0;
        for (Long selectedId : selectedItems) {
            if (EthereumNetworkRepository.hasRealValue(selectedId) == mainnetActive && activeNetworkId == selectedId) {
                deselected = false;
            }
            selectedIds[index++] = selectedId;
        }

        if (deselected) networkRepository.setActiveBrowserNetwork(null);
        networkRepository.setFilterNetworkList(selectedIds);
        tokensService.setupFilter(hasSelected && !shouldBlankUserSelection);

        if (shouldBlankUserSelection) preferenceRepository.blankHasSetNetworkFilters();

        preferenceRepository.commit();
    }

    public boolean hasShownTestNetWarning()
    {
        return preferenceRepository.hasShownTestNetWarning();
    }

    public void setShownTestNetWarning()
    {
        preferenceRepository.setShownTestNetWarning();
    }

    public NetworkInfo getNetworkByChain(long chainId)
    {
        return networkRepository.getNetworkByChain(chainId);
    }

    public boolean mainNetActive()
    {
        return preferenceRepository.isActiveMainnet();
    }

    public List<NetworkItem> getNetworkList(boolean isMainNet)
    {
        List<NetworkItem> networkList = new ArrayList<>();
        List<Long> filterIds = networkRepository.getSelectedFilters(isMainNet);

        for (NetworkInfo info : getNetworkList())
        {
            if (EthereumNetworkRepository.hasRealValue(info.chainId) == isMainNet)
            {
                networkList.add(new NetworkItem(info.name, info.chainId, filterIds.contains(info.chainId)));
            }
        }

        return networkList;
    }

    public void removeCustomNetwork(long chainId) {
        networkRepository.removeCustomRPCNetwork(chainId);
    }

    public TokensService getTokensService() {
        return tokensService;
    }
}

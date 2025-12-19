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

package com.alphawallet.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.alphawallet.app.C;
import com.alphawallet.app.R;
import com.alphawallet.app.entity.CustomViewSettings;
import com.alphawallet.app.entity.NetworkInfo;
import com.alphawallet.app.repository.EthereumNetworkRepository;
import com.alphawallet.app.ui.widget.adapter.SingleSelectNetworkAdapter;
import com.alphawallet.app.ui.widget.entity.NetworkItem;
import com.alphawallet.app.viewmodel.SelectNetworkViewModel;
import com.alphawallet.app.widget.TestNetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;


public class SelectNetworkActivity extends SelectNetworkBaseActivity implements TestNetDialog.TestNetDialogCallback {
    private static final int REQUEST_SELECT_ACTIVE_NETWORKS = 2000;

    boolean localSelectionMode;
    boolean isTestNet = false;
    private SelectNetworkViewModel viewModel;
    private SingleSelectNetworkAdapter mainNetAdapter;
    private SingleSelectNetworkAdapter testNetAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SelectNetworkViewModel.class);

        isTestNet = getIntent().getBooleanExtra(C.EXTRA_IS_TEST_NET, false);
        viewModel.setMainNetActive(!isTestNet);
        prepare(getIntent());
    }

    void prepare(Intent intent) {
        if (intent != null) {
            localSelectionMode = intent.getBooleanExtra(C.EXTRA_LOCAL_NETWORK_SELECT_FLAG, false);
            long selectedChainId = intent.getLongExtra(C.EXTRA_CHAIN_ID, -1);

            Timber.d("105 isTestNet " + isTestNet);

            // Previous active network was deselected, get the first item in filtered networks
            if (selectedChainId == -1) {
                selectedChainId = viewModel.getSelectedNetwork();
            } //try network from settings
            if (selectedChainId == -1 || !viewModel.getFilterNetworkList().contains(selectedChainId)) {
                selectedChainId = viewModel.getFilterNetworkList().get(0);
            } //use first network known on list if there's still any kind of issue

            if (localSelectionMode) {
                setTitle(getString(R.string.choose_network_preference));
                List<NetworkInfo> allNetworks = Arrays.asList(viewModel.getNetworkList());
                setupList(selectedChainId, allNetworks);
            } else {
                setTitle(getString(R.string.select_dappbrowser_network));

                hideSwitches();
                List<NetworkInfo> filteredNetworks = new ArrayList<>();
                for (Long chainId : viewModel.getFilterNetworkList()) {
                    filteredNetworks.add(viewModel.getNetworkByChain(chainId));
                }
                Timber.d("105 size filteredNetworks " + filteredNetworks.size());
                setupList(selectedChainId, filteredNetworks);
            }

            initTestNetDialog(this);
        } else {
            finish();
        }
    }

    void setupList(Long selectedNetwork, List<NetworkInfo> availableNetworks) {

//        mainnetSwitch.setOnCheckedChangeListener(null);
//        testnetSwitch.setOnCheckedChangeListener(null);
//
//        mainnetSwitch.setChecked(!isTestNet);
//        testnetSwitch.setChecked(isTestNet);
//
//        CompoundButton.OnCheckedChangeListener mainnetListener = (compoundButton, checked) -> {
//            testnetSwitch.setChecked(!checked);
//        };
//
//        CompoundButton.OnCheckedChangeListener testnetListener = (compoundButton, checked) -> {
//            mainnetSwitch.setOnCheckedChangeListener(null);
//            mainnetSwitch.setChecked(!checked);
//            mainnetSwitch.setOnCheckedChangeListener(mainnetListener);
//
//            toggleListVisibility(!checked);
//
//            if (!checked) {
//                mainNetAdapter.selectDefault();
//            } else {
//                testnetDialog.show();
//            }
//        };
//
//        mainnetSwitch.setOnCheckedChangeListener(mainnetListener);
//        testnetSwitch.setOnCheckedChangeListener(testnetListener);

        toggleListVisibility(!isTestNet);

        setupFilters(selectedNetwork, availableNetworks);
    }

    private void setupFilters(Long selectedNetwork, List<NetworkInfo> availableNetworks) {
        ArrayList<NetworkItem> mainNetList = new ArrayList<>();
        ArrayList<NetworkItem> testNetList = new ArrayList<>();

        for (NetworkInfo info : availableNetworks) {
            if (EthereumNetworkRepository.hasRealValue(info.chainId)) {
                mainNetList.add(new NetworkItem(info.name, info.chainId, selectedNetwork.equals(info.chainId)));
            } else {
                testNetList.add(new NetworkItem(info.name, info.chainId, selectedNetwork.equals(info.chainId)));
            }
        }

        Timber.d("105 mainNetList " + mainNetList.size());
        Timber.d("105 testNetList " + testNetList.size());
        mainNetAdapter = new SingleSelectNetworkAdapter(mainNetList, new SingleSelectNetworkAdapter.NetWorkClickListener() {
            @Override
            public void clickNetwork(NetworkItem network) {
                handleSetNetworks();
            }
        });
        mainnetRecyclerView.setAdapter(mainNetAdapter);

        testNetAdapter = new SingleSelectNetworkAdapter(testNetList, new SingleSelectNetworkAdapter.NetWorkClickListener() {
            @Override
            public void clickNetwork(NetworkItem network) {
                handleSetNetworks();
            }
        });
        testnetRecyclerView.setAdapter(testNetAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!localSelectionMode && !CustomViewSettings.showAllNetworks()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_filter_network, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            viewModel.openSelectNetworkFilters(this, REQUEST_SELECT_ACTIVE_NETWORKS, isTestNet);
        } else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SELECT_ACTIVE_NETWORKS) {
            if (resultCode == RESULT_OK) {
                prepare(data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void handleSetNetworks() {
        long selectedNetwork = mainnetSwitch.isChecked() ? mainNetAdapter.getSelectedItem() : testNetAdapter.getSelectedItem();
        Intent intent = new Intent();
        intent.putExtra(C.EXTRA_CHAIN_ID, selectedNetwork);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onTestNetDialogClosed() {
        testnetSwitch.setChecked(false);
    }

    @Override
    public void onTestNetDialogConfirmed(long newChainId) {
        testNetAdapter.selectDefault();
    }

    @Override
    public void onTestNetDialogCancelled() {
        testnetSwitch.setChecked(false);
    }
}

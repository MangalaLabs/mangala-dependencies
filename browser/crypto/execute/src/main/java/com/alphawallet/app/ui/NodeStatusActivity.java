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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphawallet.app.R;
import com.alphawallet.app.repository.EthereumNetworkRepository;
import com.alphawallet.app.ui.widget.adapter.NodeStatusAdapter;
import com.alphawallet.app.viewmodel.NodeStatusViewModel;
import com.alphawallet.app.widget.ActionSheetDialog;
import com.alphawallet.app.widget.ActionSheetMode;
import com.alphawallet.app.widget.StandardHeader;
import com.alphawallet.ethereum.NetworkInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;


public class NodeStatusActivity extends BaseActivity
{

    RecyclerView mainnetRecyclerView;
    RecyclerView testnetRecyclerView;
    StandardHeader mainnetHeader;
    StandardHeader testnetHeader;

    NodeStatusAdapter mainnetAdapter;
    NodeStatusAdapter testnetAdapter;

    NodeStatusViewModel viewModel;

    ActionSheetDialog sheetDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_status);
        toolbar();

        setTitle("Node Status");

        viewModel = new ViewModelProvider(this)
                .get(NodeStatusViewModel.class);

        initViews();

        setupList(Arrays.asList(viewModel.getNetworkList()));
    }

    private void initViews()
    {
        mainnetHeader = findViewById(R.id.mainnet_header);
        testnetHeader = findViewById(R.id.testnet_header);

        mainnetRecyclerView = findViewById(R.id.main_list);
        testnetRecyclerView = findViewById(R.id.test_list);

        mainnetRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        testnetRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupList(List<NetworkInfo> networkInfoList)
    {
        ArrayList<NetworkInfo> mainNetList = new ArrayList<>();
        ArrayList<NetworkInfo> testNetList = new ArrayList<>();

        for (NetworkInfo info : networkInfoList)
        {
            if (EthereumNetworkRepository.hasRealValue(info.chainId))
            {
                mainNetList.add(info);
            }
            else
            {
                testNetList.add(info);
            }
        }

        try
        {
            mainnetAdapter = new NodeStatusAdapter(mainNetList);
            testnetAdapter = new NodeStatusAdapter(testNetList);
            mainnetRecyclerView.setAdapter(mainnetAdapter);
            testnetRecyclerView.setAdapter(testnetAdapter);
        }
        catch (Exception e)
        {
            Timber.e(e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_help)
        {
            // show bottom sheet
            showNodeStatusHelp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNodeStatusHelp()
    {
        sheetDialog = new ActionSheetDialog(this, ActionSheetMode.NODE_STATUS_INFO);
        sheetDialog.show();
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            if (mainnetAdapter != null)
            {
                mainnetAdapter.dispose();
            }
            if (testnetAdapter != null)
            {
                testnetAdapter.dispose();
            }
        }
        catch (Exception e)
        {
            Timber.e(e, "onDestroy: exception while disposing adapter");
        }

        super.onDestroy();
    }
}

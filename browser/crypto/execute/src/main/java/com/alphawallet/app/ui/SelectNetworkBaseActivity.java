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
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphawallet.app.R;
import com.alphawallet.app.widget.StandardHeader;
import com.alphawallet.app.widget.TestNetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import timber.log.Timber;

public abstract class SelectNetworkBaseActivity extends BaseActivity
{
    RecyclerView mainnetRecyclerView;
    RecyclerView testnetRecyclerView;
    StandardHeader mainnetHeader;
    StandardHeader testnetHeader;
    SwitchMaterial mainnetSwitch;
    SwitchMaterial testnetSwitch;
    TestNetDialog testnetDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_network);

        toolbar();

        initViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_select_network_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        else if (item.getItemId() == R.id.action_add)
        {
            startActivity(new Intent(this, AddCustomRPCNetworkActivity.class));
        }
        else if (item.getItemId() == R.id.action_node_status)
        {
            startActivity(new Intent(this, NodeStatusActivity.class));
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        handleSetNetworks();
    }

    protected abstract void handleSetNetworks();

    protected void initTestNetDialog(TestNetDialog.TestNetDialogCallback callback)
    {
        testnetDialog = new TestNetDialog(this, 0, callback);
    }

    private void initViews()
    {
        mainnetHeader = findViewById(R.id.mainnet_header);
        testnetHeader = findViewById(R.id.testnet_header);

        mainnetSwitch = mainnetHeader.getSwitch();
        testnetSwitch = testnetHeader.getSwitch();

        hideSwitches();
        mainnetRecyclerView = findViewById(R.id.main_list);
        testnetRecyclerView = findViewById(R.id.test_list);

        mainnetRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        testnetRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    void hideSwitches()
    {
        mainnetHeader.setVisibility(View.GONE);
        testnetHeader.setVisibility(View.GONE);
    }

    void toggleListVisibility(boolean isMainNetActive)
    {
        if (isMainNetActive)
        {
            testnetRecyclerView.setVisibility(View.GONE);
            mainnetRecyclerView.setVisibility(View.VISIBLE);
//            mainnetHeader.setVisibility(View.VISIBLE);
//            testnetHeader.setVisibility(View.GONE);
            Timber.d("105 mainnetRecyclerView");
        }
        else
        {
            Timber.d("105 testnetRecyclerView");
            testnetRecyclerView.setVisibility(View.VISIBLE);
            mainnetRecyclerView.setVisibility(View.GONE);
//            mainnetHeader.setVisibility(View.GONE);
//            testnetHeader.setVisibility(View.VISIBLE);
        }
    }
}

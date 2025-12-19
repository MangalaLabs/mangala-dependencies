/*
 * Copyright (c) DuckDuckGo, Inc.
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from original source: https://github.com/duckduckgo/Android
 */


package com.mangala.app.location.ui

import android.Manifest
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.mobile.android.ui.view.gone
import com.mangala.app.global.view.html
import com.mangala.mobile.android.ui.view.show
import com.mangala.app.global.view.websiteFromGeoLocationsApiOrigin
import com.mangala.app.location.data.LocationPermissionEntity
import com.mangala.app.location.data.LocationPermissionType
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.databinding.ActivityLocationPermissionsBinding
import com.schoolonair.wallet.component.resources.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.inject

class LocationPermissionsActivity : MangalaBrowserActivity(),
    SiteLocationPermissionDialog.SiteLocationPermissionDialogListener {

    val faviconManager: FaviconManager by inject()

    private val binding: ActivityLocationPermissionsBinding by viewBinding()
    lateinit var adapter: LocationPermissionsAdapter
    private var deleteDialog: AlertDialog? = null

    private val viewModel: LocationPermissionsViewModel by viewModel()

    private val toolbar
        get() = binding.includeToolbar.findViewById<Toolbar>(com.schoolonair.wallet.browser.app.R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(toolbar)
        setupRecyclerView()
        observeViewModel()
        loadSystemPermission()
    }

    private fun setupRecyclerView() {
        adapter = LocationPermissionsAdapter(viewModel, this, faviconManager)
        binding.recycler.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(this) { viewState ->
            viewState?.let {
                if (!it.systemLocationPermissionGranted) {
                    binding.recycler.gone()
                    binding.locationPermissionsNoSystemPermission.text = getString(R.string.preciseLocationNoSystemPermission).html(this)
                    binding.locationPermissionsNoSystemPermission.show()
                } else {
                    binding.recycler.show()
                    binding.locationPermissionsNoSystemPermission.gone()
                    adapter.updatePermissions(it.locationPermissionEnabled, it.locationPermissionEntities)
                }
            }
        }

        viewModel.command.observe(this) {
            when (it) {
                is LocationPermissionsViewModel.Command.ConfirmDeleteLocationPermission -> confirmDeleteWebsite(it.entity)
                is LocationPermissionsViewModel.Command.EditLocationPermissions -> editSiteLocationPermission(it.entity)
            }
        }
    }

    private fun loadSystemPermission() {
        viewModel.loadLocationPermissions(
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    @Suppress("deprecation")
    private fun confirmDeleteWebsite(entity: LocationPermissionEntity) {
        val message = getString(R.string.preciseLocationDeleteConfirmMessage, entity.domain.websiteFromGeoLocationsApiOrigin()).html(this)
        val title = getString(R.string.dialogConfirmTitle)
        deleteDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes) { _, _ -> viewModel.delete(entity) }
            .setNegativeButton(android.R.string.no) { _, _ -> }
            .create()
        deleteDialog?.show()
    }

    private fun editSiteLocationPermission(entity: LocationPermissionEntity) {
        val dialog = SiteLocationPermissionDialog.instance(
            origin = entity.domain,
            isEditingPermission = true,
            tabId = ""
        )
        dialog.show(supportFragmentManager,
            SiteLocationPermissionDialog.SITE_LOCATION_PERMISSION_TAG
        )
    }

    override fun onDestroy() {
        deleteDialog?.dismiss()
        super.onDestroy()
    }

    override fun onSiteLocationPermissionSelected(
        domain: String,
        permission: LocationPermissionType
    ) {
        viewModel.onSiteLocationPermissionSelected(domain, permission)
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, LocationPermissionsActivity::class.java)
        }
    }
}

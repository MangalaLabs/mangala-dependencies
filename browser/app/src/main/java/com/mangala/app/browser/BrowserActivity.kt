/*
 * Copyright 2023-2024 Mangala Wallet
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
 * This file uses patterns and conventions from eos-jvm
 * (https://github.com/memtrip/eos-jvm) by memtrip LTD.
 */

package com.mangala.app.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import androidx.webkit.ServiceWorkerClientCompat
import com.alphawallet.ethereum.EthereumNetworkBase
import com.mangala.app.bookmarks.ui.BookmarksActivity
import com.mangala.app.browser.BrowserViewModel.Command
import com.mangala.app.browser.BrowserViewModel.Command.Query
import com.mangala.app.browser.BrowserViewModel.Command.Refresh
import com.mangala.app.browser.rating.ui.AppEnjoymentDialogFragment
import com.mangala.app.browser.rating.ui.GiveFeedbackDialogFragment
import com.mangala.app.browser.rating.ui.RateAppDialogFragment
import com.mangala.app.browser.shortcut.ShortcutBuilder
import com.mangala.app.cta.ui.CtaViewModel
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.downloads.DownloadsActivity
import com.mangala.app.feedback.ui.common.FeedbackActivity
import com.mangala.app.fire.AutomaticDataClearer
import com.mangala.app.fire.DataClearer
import com.mangala.app.fire.DataClearerForegroundAppRestartPixel
import com.mangala.app.global.ApplicationClearDataState
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.app.global.events.db.UserEventsStore
import com.mangala.app.global.intentText
import com.mangala.app.global.sanitize
import com.mangala.app.global.view.ClearDataAction
import com.mangala.app.global.view.renderIfChanged
import com.mangala.app.location.ui.LocationPermissionsActivity
import com.mangala.app.onboarding.ui.page.DefaultBrowserPage
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.playstore.PlayStoreUtils
import com.mangala.app.privacy.ui.PrivacyDashboardActivity
import com.mangala.app.settings.SettingsActivity
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.tabs.model.TabEntity
import com.mangala.mobile.android.ui.view.gone
import com.mangala.mobile.android.ui.view.show
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.mangala.navigation.BrowserActivityNavigationUtils
import com.mangala.navigation.BrowserActivityNavigationUtils.FAVORITES_ONBOARDING_EXTRA
import com.mangala.navigation.BrowserActivityNavigationUtils.LAUNCH_FROM_DEFAULT_BROWSER_DIALOG
import com.mangala.navigation.BrowserActivityNavigationUtils.LAUNCH_FROM_FAVORITES_WIDGET
import com.mangala.navigation.BrowserActivityNavigationUtils.NEW_SEARCH_EXTRA
import com.mangala.navigation.BrowserActivityNavigationUtils.NOTIFY_DATA_CLEARED_EXTRA
import com.mangala.navigation.BrowserActivityNavigationUtils.PERFORM_FIRE_ON_ENTRY_EXTRA
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivityBrowserBinding
import com.schoolonair.wallet.browser.app.databinding.IncludeOmnibarToolbarMockupBinding
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

// open class so that we can test BrowserApplicationStateInfo

open class BrowserActivity : MangalaBrowserActivity(), CoroutineScope by MainScope() {

//    @Inject
    val settingsDataStore: SettingsDataStore by inject()

//    @Inject
    val clearPersonalDataAction: ClearDataAction by inject()

//    @Inject
    val dataClearer: AutomaticDataClearer by inject()

//    @Inject
    val pixel: Pixel by inject()

//    @Inject
    val playStoreUtils: PlayStoreUtils by inject()

//    @Inject
    val dataClearerForegroundAppRestartPixel: DataClearerForegroundAppRestartPixel by inject()

//    @Inject
    val ctaViewModel: CtaViewModel by inject()

//    @Inject
    val variantManager: VariantManager by inject()

//    @Inject
    val userEventsStore: UserEventsStore by inject()

//    @Inject
    @AppCoroutineScope
    val appCoroutineScope: CoroutineScope by inject(named("AppCoroutineScope"))

    private var currentTab: BrowserTabFragment? = null

    private val viewModel: BrowserViewModel by viewModel()

    private var instanceStateBundles: CombinedInstanceState? = null

    private var lastIntent: Intent? = null

    private lateinit var renderer: BrowserStateRenderer

    private val binding: ActivityBrowserBinding by viewBinding()

    private lateinit var toolbarMockupBinding: IncludeOmnibarToolbarMockupBinding

    private var openMessageInNewTabJob: Job? = null

    @VisibleForTesting
    var destroyedByBackPress: Boolean = false

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
//        injectBeforeOnCreate()
        intent?.sanitize()
        Timber.i("1991 onCreate called. freshAppLaunch: ${dataClearer.isFreshAppLaunch}, savedInstanceState: $savedInstanceState")
        dataClearerForegroundAppRestartPixel.registerIntent(intent)
        renderer = BrowserStateRenderer()
        val newInstanceState = if (dataClearer.isFreshAppLaunch) null else savedInstanceState
        instanceStateBundles = CombinedInstanceState(originalInstanceState = savedInstanceState, newInstanceState = newInstanceState)

        super.onCreate(savedInstanceState = newInstanceState, daggerInject = false)
        toolbarMockupBinding = IncludeOmnibarToolbarMockupBinding.bind(binding.root)
        setContentView(binding.root)
        viewModel.viewState.observe(this) {
            renderer.renderBrowserViewState(it)
        }
        viewModel.awaitClearDataFinishedNotification()

//        VerifyTransactionDialogFragment.showDialogFragment(supportFragmentManager)
    }

    override fun onStop() {
        openMessageInNewTabJob?.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        currentTab = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.i("onNewIntent: $intent")

        intent?.sanitize()

        dataClearerForegroundAppRestartPixel.registerIntent(intent)

        if (dataClearer.dataClearerState.value == ApplicationClearDataState.FINISHED) {
            Timber.i("Automatic data clearer has finished, so processing intent now")
            launchNewSearchOrQuery(intent)
        } else {
            Timber.i("Automatic data clearer not yet finished, so deferring processing of intent")
            lastIntent = intent
        }
    }

    private fun openNewTab(
        tabId: String,
        url: String? = null,
        skipHome: Boolean,
        accountId: String?,
        chainId: Long?,
        prevTabId: String? = null
    ): BrowserTabFragment {
        Timber.i("Opening new tab, url: $url, tabId: $tabId")
//        val chainId = intent?.getLongExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_ID, 1L) ?: 1L
        val address = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_ADDRESS) ?: ""
        val rpcServerUrl = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_RPC_SERVER_URL) ?: ""
        val chainNetworks = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK) ?: ""
        val accountId = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_ACCOUNT_ID) ?: ""
        Timber.d("1991 openNewTab accountId $accountId")
        val fragment = BrowserTabFragment.newInstance(tabId, url, skipHome, chainId ?: EthereumNetworkBase.BINANCE_MAIN_ID, address, rpcServerUrl, chainNetworks, accountId, prevTabId = prevTabId)
        addOrReplaceNewTab(fragment, tabId)
        currentTab = fragment
        return fragment
    }

    private fun openFavoritesOnboardingNewTab(tabId: String): BrowserTabFragment {
        pixel.fire(AppPixelName.APP_EMPTY_VIEW_WIDGET_LAUNCH)
//        val chainId = intent?.getLongExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_ID, 1L) ?: 1L
        val address = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_ADDRESS) ?: ""
        val chainNetworks = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK) ?: ""
        Timber.d("1994 openFavoritesOnboardingNewTab address $address")
        val rpcServerUrl = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_RPC_SERVER_URL) ?: ""
        val fragment = BrowserTabFragment.newInstanceFavoritesOnboarding(tabId, EthereumNetworkBase.BINANCE_MAIN_ID, address, rpcServerUrl, chainNetworks)
        addOrReplaceNewTab(fragment, tabId)
        currentTab = fragment
        return fragment
    }

    private fun addOrReplaceNewTab(
        fragment: BrowserTabFragment,
        tabId: String
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        val tab = currentTab
        if (tab == null) {
            transaction.replace(R.id.fragmentContainer, fragment, tabId)
        } else {
            transaction.hide(tab)
            transaction.add(R.id.fragmentContainer, fragment, tabId)
        }
        transaction.commit()
    }

    fun selectTab(tab: TabEntity?) {
        Timber.v("Select tab: $tab")

        if (tab == null) return

        if (tab.tabId == currentTab?.tabId) return

        val fragment = supportFragmentManager.findFragmentByTag(tab.tabId) as? BrowserTabFragment
        if (fragment == null) {
            openNewTab(tab.tabId, tab.url, tab.skipHome, tab.accountId, tab.chainId)
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        currentTab?.let {
            transaction.hide(it)
        }
        Timber.d("1995 show selectTab " + tab?.tabId)
        transaction.show(fragment)
        transaction.commit()
        currentTab = fragment
    }

    private fun removeTabs(fragments: List<BrowserTabFragment>) {
        val transaction = supportFragmentManager.beginTransaction()
        fragments.forEach { transaction.remove(it) }
        transaction.commit()
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            currentTab?.onLongPressBackButton()
            true
        } else {
            super.onKeyLongPress(keyCode, event)
        }
    }

    private fun launchNewSearchOrQuery(intent: Intent?) {

        Timber.i("launchNewSearchOrQuery: $intent")

        if (intent == null) {
            return
        }

        if (intent.getBooleanExtra(LAUNCH_FROM_DEFAULT_BROWSER_DIALOG, false)) {
            setResult(DefaultBrowserPage.DEFAULT_BROWSER_RESULT_CODE_DIALOG_INTERNAL)
            finish()
            return
        }

        if (intent.getBooleanExtra(PERFORM_FIRE_ON_ENTRY_EXTRA, false)) {

            Timber.i("Clearing everything as a result of $PERFORM_FIRE_ON_ENTRY_EXTRA flag being set")
            appCoroutineScope.launch {
                clearPersonalDataAction.clearTabsAndAllDataAsync(appInForeground = true, shouldFireDataClearPixel = true)
                clearPersonalDataAction.setAppUsedSinceLastClearFlag(false)
                clearPersonalDataAction.killAndRestartProcess(notifyDataCleared = false)
            }

            return
        }

        if (intent.getBooleanExtra(NOTIFY_DATA_CLEARED_EXTRA, false)) {
            Timber.i("Should notify data cleared")
            Toast.makeText(applicationContext, com.schoolonair.wallet.component.resources.R.string.fireDataCleared, Toast.LENGTH_LONG).show()
        }

        if (intent.getBooleanExtra(FAVORITES_ONBOARDING_EXTRA, false)) {
            launch {
                val tabId = viewModel.onNewTabRequested()
                openFavoritesOnboardingNewTab(tabId)
            }
            return
        }

        if (launchNewSearch(intent)) {
            Timber.w("new tab requested")
            launch { viewModel.onNewTabRequested() }
            return
        }

        val sharedText = intent.intentText
        if (sharedText != null) {
            if (intent.getBooleanExtra(ShortcutBuilder.SHORTCUT_EXTRA_ARG, false)) {
                Timber.d("Shortcut opened with url $sharedText")
                launch { viewModel.onOpenShortcut(sharedText) }
            } else if (intent.getBooleanExtra(LAUNCH_FROM_FAVORITES_WIDGET, false)) {
                Timber.d("Favorite clicked from widget $sharedText")
                launch { viewModel.onOpenFavoriteFromWidget(query = sharedText) }
                return
            } else {
                Timber.w("opening in new tab requested for $sharedText")
                launch { viewModel.onOpenInNewTabRequested(query = sharedText, skipHome = true) }
                return
            }
        }
    }

    private fun configureObservers() {
        viewModel.command.observe(this) {
            processCommand(it)
        }
        viewModel.selectedTab.observe(this) {
            if (it != null) selectTab(it)
        }
        viewModel.tabs.observe(this) {
            clearStaleTabs(it)
            launch { viewModel.onTabsUpdated(it) }
        }
    }

    private fun removeObservers() {
        viewModel.command.removeObservers(this)
        viewModel.selectedTab.removeObservers(this)
        viewModel.tabs.removeObservers(this)
    }

    private fun clearStaleTabs(updatedTabs: List<TabEntity>?) {
        if (updatedTabs == null) {
            return
        }

        val stale = supportFragmentManager
            .fragments.mapNotNull { it as? BrowserTabFragment }
            .filter { fragment -> updatedTabs.none { it.tabId == fragment.tabId } }

        if (stale.isNotEmpty()) {
            removeTabs(stale)
        }
    }

    private fun processCommand(command: Command?) {
        Timber.i("Processing command: $command")
        when (command) {
            is Query -> currentTab?.submitQuery(command.query)
            is Refresh -> currentTab?.onRefreshRequested()
            is Command.LaunchPlayStore -> launchPlayStore()
            is Command.ShowAppEnjoymentPrompt -> showAppEnjoymentPrompt(AppEnjoymentDialogFragment.create(command.promptCount, viewModel))
            is Command.ShowAppRatingPrompt -> showAppEnjoymentPrompt(RateAppDialogFragment.create(command.promptCount, viewModel))
            is Command.ShowAppFeedbackPrompt -> showAppEnjoymentPrompt(GiveFeedbackDialogFragment.create(command.promptCount, viewModel))
            is Command.LaunchFeedbackView -> startActivity(FeedbackActivity.intent(this))
            else -> {}
        }
    }

    private fun launchNewSearch(intent: Intent): Boolean {
        return intent.getBooleanExtra(NEW_SEARCH_EXTRA, false)
    }

    fun launchPrivacyDashboard() {
        currentTab?.tabId?.let {
            startActivityForResult(PrivacyDashboardActivity.intent(this, it), DASHBOARD_REQUEST_CODE)
        }
    }

    fun launchNewTab() {
        launch { viewModel.onNewTabRequested() }
    }

    fun openInNewTab(
        query: String,
        sourceTabId: String?
    ) {
        launch {
            viewModel.onOpenInNewTabRequested(query = query, sourceTabId = sourceTabId)
        }
    }

    fun openMessageInNewTab(
        message: Message,
        sourceTabId: String?,
        prevTabId: String?,
        chainId: Long?,
        accountId: String?
    ) {
        openMessageInNewTabJob = launch {
            val tabId = viewModel.onNewTabRequested(sourceTabId = sourceTabId)
            val fragment = openNewTab(tabId, null, false, accountId, chainId, prevTabId)
            fragment.messageFromPreviousTab = message
//            Timber.d("1995 openMessageInNewTab tabId $tabId")
//            Timber.d("1995 openMessageInNewTab tabId $tabId message $message")
        }
    }

    fun openTab(tabId: String){
        launch {
            val tab = viewModel.onTabSelected(tabId)
            selectTab(tab)
        }
    }

    fun launchSettings() {
        startActivity(SettingsActivity.intent(this))
    }

    fun launchLocationSettings() {
        startActivity(LocationPermissionsActivity.intent(this))
    }

    fun launchBookmarks() {
        startActivity(BookmarksActivity.intent(this))
    }

    fun launchDownloads() {
        startActivity(DownloadsActivity.intent(this))
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == DASHBOARD_REQUEST_CODE) {
            viewModel.receivedDashboardResult(resultCode)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (currentTab?.onBackPressed() != true) {
            // signal user press back button to exit the app so that BrowserApplicationStateInfo
            // can call the right callback
            destroyedByBackPress = true
            super.onBackPressed()
        }
    }

    override fun onAttachFragment(fragment: androidx.fragment.app.Fragment) {
        super.onAttachFragment(fragment)
        hideMockupOmnibar()
    }

    private fun hideMockupOmnibar() {
        // Delaying this code to avoid race condition when fragment and activity recreated
        Handler().postDelayed(
            {
                if (this::toolbarMockupBinding.isInitialized) {
                    toolbarMockupBinding.appBarLayoutMockup.visibility = View.GONE
                }
            },
            300
        )
    }

    companion object {

        fun intent(
            context: Context,
            queryExtra: String? = null,
            newSearch: Boolean = false,
            notifyDataCleared: Boolean = false
        ): Intent {
            val intent = Intent(context, BrowserActivity::class.java)
            intent.putExtra(EXTRA_TEXT, queryExtra)
            intent.putExtra(NEW_SEARCH_EXTRA, newSearch)
            intent.putExtra(NOTIFY_DATA_CLEARED_EXTRA, notifyDataCleared)
            return intent
        }

        private const val APP_ENJOYMENT_DIALOG_TAG = "AppEnjoyment"

        private const val DASHBOARD_REQUEST_CODE = 100
    }

    inner class BrowserStateRenderer {

        private var lastSeenBrowserState: BrowserViewModel.ViewState? = null
        private var processedOriginalIntent = false

        fun renderBrowserViewState(viewState: BrowserViewModel.ViewState) {
            renderIfChanged(viewState, lastSeenBrowserState) {
                lastSeenBrowserState = viewState

                if (viewState.hideWebContent) {
                    hideWebContent()
                } else {
                    showWebContent()
                }
            }
        }

        private fun showWebContent() {
            Timber.d("1991 BrowserActivity can now start displaying web content. instance state is $instanceStateBundles")
            configureObservers()
            binding.clearingInProgressView.gone()

            if (lastIntent != null) {
                Timber.i("There was a deferred intent to process; handling now")
                launchNewSearchOrQuery(lastIntent)
                lastIntent = null
                return
            }

            if (!processedOriginalIntent && instanceStateBundles?.originalInstanceState == null && !intent.launchedFromRecents) {
                Timber.i("Original instance state is null, so will inspect intent for actions to take. $intent")
                launchNewSearchOrQuery(intent)
                processedOriginalIntent = true
            }
        }
    }

    private val Intent.launchedFromRecents: Boolean
        get() = (flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY

    private fun showAppEnjoymentPrompt(prompt: DialogFragment) {
        (supportFragmentManager.findFragmentByTag(APP_ENJOYMENT_DIALOG_TAG) as? DialogFragment)?.dismiss()
        prompt.show(supportFragmentManager, APP_ENJOYMENT_DIALOG_TAG)
    }

    private fun hideWebContent() {
        Timber.d("1991 Hiding web view content")
        removeObservers()
        binding.clearingInProgressView.show()
    }

    private fun launchPlayStore() {
        playStoreUtils.launchPlayStore()
    }

//    private fun injectBeforeOnCreate() {
//        // Manual injection for dependencies that's needed before we call super.onCreate()
//        val factory = EntryPointAccessors.fromApplication(this, BrowserActivityDependenciesFactory::class.java)
//        with(factory) {
//            dataClearer = getDataClearer()
//            dataClearerForegroundAppRestartPixel = getDataClearerForegroundAppRestartPixel()
//        }
//    }
//
//    @EntryPoint
//    @InstallIn(SingletonComponent::class)
//    interface BrowserActivityDependenciesFactory {
//        fun getSettingsDataStore(): SettingsDataStore
//        fun getClearDataAction(): ClearDataAction
//        fun getDataClearer(): DataClearer
//        fun getPixel(): Pixel
//        fun getPlayStoreUtils(): PlayStoreUtils
//        fun getDataClearerForegroundAppRestartPixel(): DataClearerForegroundAppRestartPixel
//        fun getCtaViewModel(): CtaViewModel
//        fun getVariantManager(): VariantManager
//        fun getUserEventsStore(): UserEventsStore
//        fun getServiceWorkerClientCompat(): ServiceWorkerClientCompat
//        @AppCoroutineScope
//        fun getCoroutineScope(): CoroutineScope
//    }

    private data class CombinedInstanceState(
        val originalInstanceState: Bundle?,
        val newInstanceState: Bundle?
    )
}

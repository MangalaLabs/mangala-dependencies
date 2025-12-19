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

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ActivityOptions
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.*
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.Settings
import android.text.Editable
import android.view.*
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.webkit.WebView.FindListener
import android.webkit.WebView.HitTestResult
import android.webkit.WebView.HitTestResult.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.AnyThread
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.*
import androidx.fragment.app.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphawallet.app.C
import com.alphawallet.app.entity.*
import com.alphawallet.app.entity.tokens.Token
import com.alphawallet.app.service.WalletConnectService
import com.alphawallet.app.ui.AddEthereumChainPrompt
import com.alphawallet.app.ui.widget.entity.ActionSheetCallback
import com.alphawallet.app.util.BalanceUtils
import com.alphawallet.app.walletconnect.WCClient
import com.alphawallet.app.walletconnect.entity.WCUtils
import com.alphawallet.app.web3.*
import com.alphawallet.app.web3.entity.Address
import com.alphawallet.app.web3.entity.WalletAddEthereumChainObject
import com.alphawallet.app.web3.entity.Web3Call
import com.alphawallet.app.web3.entity.Web3Transaction
import com.alphawallet.app.widget.AWalletAlertDialog
import com.alphawallet.app.widget.ActionSheetDialog
import com.alphawallet.app.widget.TestNetDialog
import com.alphawallet.ethereum.EthereumNetworkBase
import com.alphawallet.token.entity.EthereumMessage
import com.alphawallet.token.entity.EthereumTypedMessage
import com.alphawallet.token.entity.SignMessageType
import com.alphawallet.token.entity.Signable
import com.mangala.app.accessibility.data.AccessibilitySettingsDataStore
import com.mangala.app.autocomplete.api.AutoComplete.AutoCompleteSuggestion
import com.mangala.app.autocomplete.api.AutoComplete.AutoCompleteSuggestion.AutoCompleteBookmarkSuggestion
import com.mangala.app.autocomplete.api.AutoComplete.AutoCompleteSuggestion.AutoCompleteSearchSuggestion
import com.mangala.app.bookmarks.model.SavedSite
import com.mangala.app.bookmarks.model.SavedSite.Bookmark
import com.mangala.app.bookmarks.model.SavedSite.Favorite
import com.mangala.app.bookmarks.ui.EditSavedSiteDialogFragment
import com.mangala.app.brokensite.BrokenSiteActivity
import com.mangala.app.brokensite.BrokenSiteData
import com.mangala.app.browser.BrowserTabViewModel.*
import com.mangala.app.browser.BrowserTabViewModel.Command.NavigateToHistory
import com.mangala.app.browser.BrowserTabViewModel.Command.ShowBackNavigationHistory
import com.mangala.app.browser.DownloadConfirmationFragment.DownloadConfirmationDialogListener
import com.mangala.app.browser.autocomplete.BrowserAutoCompleteSuggestionsAdapter
import com.mangala.app.browser.autofill.AutofillCredentialsSelectionResultHandler
import com.mangala.app.browser.cookies.ThirdPartyCookieManager
import com.mangala.app.browser.downloader.BlobConverterInjector
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.browser.favorites.FavoritesQuickAccessAdapter
import com.mangala.app.browser.favorites.FavoritesQuickAccessAdapter.Companion.QUICK_ACCESS_ITEM_MAX_SIZE_DP
import com.mangala.app.browser.favorites.FavoritesQuickAccessAdapter.QuickAccessFavorite
import com.mangala.app.browser.favorites.QuickAccessDragTouchItemListener
import com.mangala.app.browser.filechooser.FileChooserIntentBuilder
import com.mangala.app.browser.history.NavigationHistorySheet
import com.mangala.app.browser.history.NavigationHistorySheet.NavigationHistorySheetListener
import com.mangala.app.browser.httpauth.WebViewHttpAuthStore
import com.mangala.app.browser.logindetection.DOMLoginDetector
import com.mangala.app.browser.menu.BrowserPopupMenu
import com.mangala.app.browser.model.BasicAuthenticationCredentials
import com.mangala.app.browser.model.BasicAuthenticationRequest
import com.mangala.app.browser.model.LongPressTarget
import com.mangala.app.browser.omnibar.OmnibarScrolling
import com.mangala.app.browser.omnibar.QueryOrigin.FromAutocomplete
import com.mangala.app.browser.print.PrintInjector
import com.mangala.app.browser.remotemessage.asMessage
import com.mangala.app.browser.session.WebViewSessionStorage
import com.mangala.app.browser.shortcut.ShortcutBuilder
import com.mangala.app.browser.tabpreview.WebViewPreviewGenerator
import com.mangala.app.browser.tabpreview.WebViewPreviewPersister
import com.mangala.app.browser.ui.HttpAuthenticationDialogFragment
import com.mangala.app.browser.urlextraction.DOMUrlExtractor
import com.mangala.app.browser.urlextraction.UrlExtractingWebView
import com.mangala.app.browser.urlextraction.UrlExtractingWebViewClient
import com.mangala.app.browser.useragent.UserAgentProvider
import com.mangala.app.browser.webview.enableDarkMode
import com.mangala.app.browser.webview.enableLightMode
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.downloads.DownloadsFileActions
import com.mangala.app.email.EmailAutofillTooltipFragment
import com.mangala.app.email.EmailInjector
import com.mangala.app.fire.fireproofwebsite.data.FireproofWebsiteEntity
import com.mangala.app.fire.fireproofwebsite.data.website
import com.mangala.app.global.model.orderedTrackingEntities
import com.mangala.app.location.data.LocationPermissionType
import com.mangala.app.location.ui.SiteLocationPermissionDialog
import com.mangala.app.location.ui.SystemLocationPermissionDialog
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.playstore.PlayStoreUtils
import com.mangala.app.privacy.renderer.icon
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.survey.model.Survey
import com.mangala.app.survey.ui.SurveyActivity
import com.mangala.app.tabs.model.TabEntity
import com.mangala.app.tabs.ui.GridViewColumnCalculator
import com.mangala.app.tabs.ui.TabSwitcherActivity
import com.mangala.app.utils.ConflatedJob
import com.mangala.app.widget.AddWidgetLauncher
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.autofill.CredentialAutofillPickerDialog.Companion.RESULT_KEY_CREDENTIAL_PICKER
import com.mangala.autofill.CredentialSavePickerDialog.Companion.RESULT_KEY_CREDENTIAL_RESULT_SAVE
import com.mangala.autofill.CredentialUpdateExistingCredentialsDialog.Companion.RESULT_KEY_CREDENTIAL_RESULT_UPDATE
import com.mangala.autofill.domain.app.LoginCredentials
import com.mangala.autofill.store.AutofillStore.ContainsCredentialsResult.*
import com.mangala.autofill.ui.ExistingCredentialMatchDetector
import com.mangala.downloads.api.FileDownloader.PendingFileDownload
import com.mangala.mobile.android.ui.MangalaTheme
import com.mangala.mobile.android.ui.store.ThemingDataStore
import com.mangala.remote.messaging.api.RemoteMessage
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mangala.app.cta.ui.BubbleCta
import com.mangala.app.cta.ui.Cta
import com.mangala.app.cta.ui.CtaViewModel
import com.mangala.app.cta.ui.DaxBubbleCta
import com.mangala.app.cta.ui.DialogCta
import com.mangala.app.cta.ui.HomePanelCta
import com.mangala.app.global.view.DaxDialog
import com.mangala.app.global.view.DaxDialogListener
import com.mangala.app.global.view.NonDismissibleBehavior
import com.mangala.app.global.view.TextChangedWatcher
import com.mangala.app.global.view.TypeAnimationTextView
import com.mangala.app.global.view.disableAnimation
import com.mangala.app.global.view.enableAnimation
import com.mangala.app.global.view.html
import com.mangala.app.global.view.isDifferent
import com.mangala.app.global.view.isImmersiveModeEnabled
import com.mangala.app.global.view.launchDefaultAppActivity
import com.mangala.app.global.view.renderIfChanged
import com.mangala.app.global.view.toggleFullScreen
import com.mangala.app.global.view.websiteFromGeoLocationsApiOrigin
import com.mangala.autofill.BrowserAutofill
import com.mangala.autofill.Callback
import com.mangala.autofill.CredentialAutofillDialogFactory
import com.mangala.autofill.CredentialAutofillPickerDialog
import com.mangala.autofill.CredentialSavePickerDialog
import com.mangala.autofill.CredentialUpdateExistingCredentialsDialog
import com.mangala.browser_bridge_api.ActionTransactionCallback
import com.mangala.browser_bridge_api.ConfirmTransactionCallback
import com.mangala.browser_bridge_api.SignPersonalMessageCallback
import com.mangala.browser_bridge_api.SwitchChainCallback
import com.mangala.downloads.api.DOWNLOAD_SNACKBAR_DELAY
import com.mangala.downloads.api.DOWNLOAD_SNACKBAR_LENGTH
import com.mangala.downloads.api.DownloadCommand
import com.mangala.downloads.api.DownloadFailReason
import com.mangala.downloads.api.FileDownloader
import com.mangala.mobile.android.ui.view.KeyboardAwareEditText
import com.mangala.mobile.android.ui.view.MessageCta
import com.mangala.mobile.android.ui.view.gone
import com.mangala.mobile.android.ui.view.hide
import com.mangala.mobile.android.ui.view.hideKeyboard
import com.mangala.mobile.android.ui.view.makeSnackbarWithNoBottomInset
import com.mangala.mobile.android.ui.view.show
import com.mangala.mobile.android.ui.view.showKeyboard
import com.mangala.navigation.BrowserActivityNavigationUtils
import com.mangala.navigation.NavigationUtils
//import com.mangala.web3.ConfirmTransactionBottomDialog
//import com.schoolonair.wallet.core.IAccountManager
//import com.schoolonair.wallet.core.IAdapterManager
//import com.schoolonair.wallet.entities.AccountType
//import com.schoolonair.wallet.entities.network.ChainNetwork
//import com.schoolonair.wallet.features.shared.domain.usecase.GetTestnetEnabledUseCase
//import com.schoolonair.wallet.local.AccountsStorage
//import com.schoolonair.wallet.modules.balance.BalanceAccountsViewModel
//import com.schoolonair.wallet.modules.balance.BalanceViewModel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
//import org.web3j.protocol.Web3j
//import org.web3j.protocol.core.methods.request.Transaction.createFunctionCallTransaction
//import org.web3j.protocol.core.methods.response.EthCall
import timber.log.Timber
import java.io.File
import java.math.BigDecimal
import kotlin.coroutines.CoroutineContext
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.FragmentBrowserTabBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class BrowserTabFragment :
    Fragment(),
    FindListener,
    CoroutineScope,
    DaxDialogListener,
    TrackersAnimatorListener,
    DownloadConfirmationDialogListener,
    SiteLocationPermissionDialog.SiteLocationPermissionDialogListener,
    SystemLocationPermissionDialog.SystemLocationPermissionDialogListener,
    OnSignMessageListener,
    OnSignPersonalMessageListener,
    OnSignTransactionListener,
    OnSignTypedMessageListener,
    OnEthCallListener,
    OnWalletAddEthereumChainObjectListener,
    OnWalletActionListener,
    URLLoadInterface
{

    private val supervisorJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = supervisorJob + Dispatchers.Main

//    @Inject
    val webViewClient: BrowserWebViewClient by inject()

//    @Inject
    val webChromeClient: BrowserChromeClient by inject()

//    @Inject
    val fileChooserIntentBuilder: FileChooserIntentBuilder by inject()

//    @Inject
    val fileDownloader: FileDownloader by inject()

//    @Inject
    val webViewSessionStorage: WebViewSessionStorage by inject()

//    @Inject
    val shortcutBuilder: ShortcutBuilder by inject()

//    @Inject
    val clipboardManager: ClipboardManager by inject()

//    @Inject
    val pixel: Pixel by inject()

//    @Inject
    val ctaViewModel: CtaViewModel by inject()

//    @Inject
    val omnibarScrolling: OmnibarScrolling by inject()

//    @Inject
    val previewGenerator: WebViewPreviewGenerator by inject()

//    @Inject
    val previewPersister: WebViewPreviewPersister by inject()

//    @Inject
    val variantManager: VariantManager by inject()

//    @Inject
    val loginDetector: DOMLoginDetector by inject()

//    @Inject
    val blobConverterInjector: BlobConverterInjector by inject()

    val tabId get() = requireArguments()[TAB_ID_ARG] as String

//    @Inject
    val userAgentProvider: UserAgentProvider by inject()

//    @Inject
    val webViewHttpAuthStore: WebViewHttpAuthStore by inject()

//    @Inject
    val thirdPartyCookieManager: ThirdPartyCookieManager by inject()

//    @Inject
    val emailInjector: EmailInjector by inject()

//    @Inject
    val browserAutofill: BrowserAutofill by inject()

//    @Inject
    val faviconManager: FaviconManager by inject()

//    @Inject
    val gridViewColumnCalculator: GridViewColumnCalculator by inject()

//    @Inject
    val themingDataStore: ThemingDataStore by inject()

//    @Inject
    val accessibilitySettingsDataStore: AccessibilitySettingsDataStore by inject()

//    @Inject
    val playStoreUtils: PlayStoreUtils by inject()

//    @Inject
    @AppCoroutineScope
    val appCoroutineScope: CoroutineScope by inject(named("AppCoroutineScope"))

//    @Inject
    val appBuildConfig: AppBuildConfig by inject()

//    @Inject
    val addWidgetLauncher: AddWidgetLauncher by inject()

//    @Inject
    val downloadsFileActions: DownloadsFileActions by inject()

//    @Inject
    val urlExtractingWebViewClient: UrlExtractingWebViewClient by inject()

//    @Inject
    val urlExtractor: DOMUrlExtractor by inject()

//    @Inject
    val urlExtractorUserAgent: UserAgentProvider by inject()

//    @Inject
    val printInjector: PrintInjector by inject()

//    @Inject
    val credentialAutofillDialogFactory: CredentialAutofillDialogFactory by inject()

//    @Inject
    val autofillCredentialsSelectionResultHandler: AutofillCredentialsSelectionResultHandler by inject()

//    @Inject
    val existingCredentialMatchDetector: ExistingCredentialMatchDetector by inject()

    private val confirmTransactionCallback: ConfirmTransactionCallback by inject()
    private val switchChainCallback: SwitchChainCallback by inject()
    private val signPersonalMessageCallback: SignPersonalMessageCallback by inject()

    private var urlExtractingWebView: UrlExtractingWebView? = null

    var messageFromPreviousTab: Message? = null

    private val initialUrl get() = requireArguments().getString(URL_EXTRA_ARG)

    private val skipHome get() = requireArguments().getBoolean(SKIP_HOME_ARG)

    private val favoritesOnboarding get() = requireArguments().getBoolean(FAVORITES_ONBOARDING_ARG, false)

    private lateinit var popupMenu: BrowserPopupMenu

    private lateinit var autoCompleteSuggestionsAdapter: BrowserAutoCompleteSuggestionsAdapter

    // Used to represent a file to download, but may first require permission
    private var pendingFileDownload: PendingFileDownload? = null

    private var pendingUploadTask: ValueCallback<Array<Uri>>? = null

    private lateinit var renderer: BrowserTabFragmentRenderer

    private lateinit var decorator: BrowserTabFragmentDecorator

    private lateinit var quickAccessAdapter: FavoritesQuickAccessAdapter
    private lateinit var quickAccessItemTouchHelper: ItemTouchHelper

    private lateinit var omnibarQuickAccessAdapter: FavoritesQuickAccessAdapter
    private lateinit var omnibarQuickAccessItemTouchHelper: ItemTouchHelper

    private val downloadMessagesJob = ConflatedJob()

    private val viewModel: BrowserTabViewModel by viewModel()

    // lazy {
    //     val viewModel = ViewModelProvider(this, viewModelFactory).get(BrowserTabViewModel::class.java)
    //     viewModel.loadData(tabId, initialUrl, skipHome, favoritesOnboarding)
    //     launchDownloadMessagesJob()
    //     viewModel
    // }

    private val animatorHelper by lazy { BrowserTrackersAnimatorHelper() }

    private val smoothProgressAnimator by lazy { SmoothProgressAnimator(view?.findViewById(R.id.pageLoadingIndicator) ?: ProgressBar(requireContext())) }

    // Optimization to prevent against excessive work generating WebView previews; an existing job will be cancelled if a new one is launched
    private var bitmapGeneratorJob: Job? = null

    private val browserActivity
        get() = activity as? BrowserActivity

    private val tabsButton: TabSwitcherButton?
        get() = view?.findViewById<TabSwitcherButton>(R.id.tabsMenu)

    private val menuButton: ViewGroup?
        get() = view?.findViewById<ViewGroup>(R.id.browserMenu)

    private var webView: MangalaWebView? = null

    private val errorSnackbar: Snackbar? by lazy {
        view?.findViewById<FrameLayout>(R.id.browserLayout)?.makeSnackbarWithNoBottomInset(com.schoolonair.wallet.component.resources.R.string.crashedWebViewErrorMessage, Snackbar.LENGTH_INDEFINITE)
            ?.setBehavior(NonDismissibleBehavior())
    }

    private val findInPageTextWatcher = object : TextChangedWatcher() {
        override fun afterTextChanged(editable: Editable) {
            viewModel.userFindingInPage(view?.findViewById<EditText>(R.id.findInPageInput)?.text.toString() ?: "")
        }
    }

    private val omnibarInputTextWatcher = object : TextChangedWatcher() {
        override fun afterTextChanged(editable: Editable) {
            viewModel.onOmnibarInputStateChanged(view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.text.toString() ?: "", view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.hasFocus() ?: false, true)
        }
    }

    private val autofillCallback = object : Callback {
        override suspend fun onCredentialsAvailableToInject(credentials: List<LoginCredentials>) {
            showAutofillDialogChooseCredentials(credentials)
        }

        override fun noCredentialsAvailable(originalUrl: String) {
            viewModel.returnNoCredentialsWithPage(originalUrl)
        }

        override suspend fun onCredentialsAvailableToSave(currentUrl: String, credentials: LoginCredentials) {
            val username = credentials.username
            val password = credentials.password

            if (username == null) {
                Timber.w("Not saving credentials with null username")
                return
            }

            if (password == null) {
                Timber.w("Not saving credentials with null password")
                return
            }

            // we need this delay to ensure web navigation / form submission events aren't blocked
            delay(100)

            when (existingCredentialMatchDetector.determine(currentUrl, username, password)) {
                ExactMatch -> Timber.w("Credentials already exist for %s", currentUrl)
                UsernameMatch -> showAutofillDialogUpdateCredentials(currentUrl, credentials)
                NoMatch -> showAutofillDialogSaveCredentials(currentUrl, credentials)
                UrlOnlyMatch -> showAutofillDialogSaveCredentials(currentUrl, credentials)
            }
        }
    }

    private val homeBackgroundLogo by lazy { HomeBackgroundLogo(view?.findViewById<ImageView>(R.id.ddgLogo) ?: ImageView(requireContext())) }

    private val ctaViewStateObserver = Observer<CtaViewState> {
        it?.let { renderer.renderCtaViewState(it) }
    }

    private var alertDialog: AlertDialog? = null

    private var appLinksSnackBar: Snackbar? = null

    private var loginDetectionDialog: AlertDialog? = null

    private var automaticFireproofDialog: AlertDialog? = null

    private var emailAutofillTooltipDialog: EmailAutofillTooltipFragment? = null

    private val pulseAnimation: PulseAnimation = PulseAnimation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        removeDaxDialogFromActivity()
        renderer = BrowserTabFragmentRenderer()
        decorator = BrowserTabFragmentDecorator()
    }

    private fun resumeWebView() {
        webView?.let {
            if (it.isShown) it.onResume()
        }
    }

    private var _binding: FragmentBrowserTabBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadChainId()
        Timber.d("1991 loadData tab fragment")
        viewModel.loadData(tabId, initialUrl, skipHome, favoritesOnboarding)
        launchDownloadMessagesJob()

        _binding = FragmentBrowserTabBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        fetchWallet()
//        activeNetwork()
        onNetworkClick()
        configureObservers()
        configurePrivacyGrade()
//        getAllWallet2()
//        fetchWallet()

//        loadNewNetwork
        configureWebView()
        configureSwipeRefresh()
        viewModel.registerWebViewListener(webViewClient, webChromeClient)
        configureOmnibarTextInput()
        configureFindInPage()
        configureAutoComplete()
        configureOmnibarQuickAccessGrid()
        configureHomeTabQuickAccessGrid()



        decorator.decorateWithFeatures()

        animatorHelper.setListener(this)

        if (savedInstanceState == null) {
            viewModel.onViewReady()
            messageFromPreviousTab?.let {
                processMessage(it)
            }
        } else {
            viewModel.onViewRecreated()
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                if (isVisible) {
                    updateOrDeleteWebViewPreview()
                }
            }
        })
    }

    private fun getDaxDialogFromActivity(): Fragment? = activity?.supportFragmentManager?.findFragmentByTag(
        DAX_DIALOG_DIALOG_TAG
    )

    private fun removeDaxDialogFromActivity() {
        val fragment = getDaxDialogFromActivity()
        fragment?.let {
            activity?.supportFragmentManager?.transaction { remove(it) }
        }
    }

    private fun processMessage(message: Message) {
        val transport = message.obj as WebView.WebViewTransport
        transport.webView = webView

        viewModel.onMessageReceived()
        message.sendToTarget()

        decorator.animateTabsCount()
        viewModel.onMessageProcessed()
    }

    private fun updateOrDeleteWebViewPreview() {
        val url = viewModel.url
        Timber.d("Updating or deleting WebView preview for $url")
        if (url == null) {
            viewModel.deleteTabPreview(tabId)
        } else {
            generateWebViewPreviewImage()
        }
    }

    private fun launchTabSwitcher() {
        val activity = activity ?: return
        startActivity(TabSwitcherActivity.intent(activity))
        activity.overridePendingTransition(com.schoolonair.wallet.component.resources.R.anim.tab_anim_fade_in, com.schoolonair.wallet.component.resources.R.anim.slide_to_bottom_browser)
    }

    override fun onResume() {
        super.onResume()

        view?.findViewById<AppBarLayout>(R.id.appBarLayout)?.setExpanded(true)
        viewModel.onViewResumed()

        // onResume can be called for a hidden/backgrounded fragment, ensure this tab is visible.
        if (fragmentIsVisible()) {
            viewModel.onViewVisible()
        }

        addTextChangedListeners()
        resumeWebView()

        webView?.setWebLoadCallback(this)
//        comeIntoFocus()
//        startBalanceListener()
    }

    override fun onPause() {
        dismissDownloadFragment()
        dismissAuthenticationDialog()
        super.onPause()
    }

    override fun onStop() {
        alertDialog?.dismiss()
//        leaveFocus()
        super.onStop()
    }

    private fun dismissAuthenticationDialog() {
        if (isAdded) {
            val fragment = parentFragmentManager.findFragmentByTag(AUTHENTICATION_DIALOG_TAG) as? HttpAuthenticationDialogFragment
            fragment?.dismiss()
        }
    }

    private fun dismissDownloadFragment() {
        val fragment = fragmentManager?.findFragmentByTag(DOWNLOAD_CONFIRMATION_TAG) as? DownloadConfirmationFragment
        fragment?.dismiss()
    }

    private fun addHomeShortcut(
        homeShortcut: Command.AddHomeShortcut,
        context: Context
    ) {
        shortcutBuilder.requestPinShortcut(context, homeShortcut)
    }

    private fun configureObservers() {
        viewModel.autoCompleteViewState.observe(
            viewLifecycleOwner,
            Observer {
                it?.let { renderer.renderAutocomplete(it) }
            }
        )

        viewModel.globalLayoutState.observe(
            viewLifecycleOwner,
            Observer {
                it?.let { renderer.renderGlobalViewState(it) }
            }
        )

        viewModel.browserViewState.observe(
            viewLifecycleOwner,
            Observer {
                it?.let { renderer.renderBrowserViewState(it) }
            }
        )

        viewModel.loadingViewState.observe(
            viewLifecycleOwner,
            Observer {
                it?.let { renderer.renderLoadingIndicator(it) }
            }
        )

        viewModel.omnibarViewState.observe(
            viewLifecycleOwner,
            Observer {
                it?.let { renderer.renderOmnibar(it) }
            }
        )

        viewModel.findInPageViewState.observe(
            viewLifecycleOwner,
            Observer {
                it?.let { renderer.renderFindInPageState(it) }
            }
        )

        viewModel.accessibilityViewState.observe(
            viewLifecycleOwner,
            Observer {
                it?.let { renderer.applyAccessibilitySettings(it) }
            }
        )

        viewModel.ctaViewState.observe(viewLifecycleOwner, ctaViewStateObserver)

        viewModel.command.observe(
            viewLifecycleOwner,
            Observer {
                processCommand(it)
            }
        )

        viewModel.survey.observe(
            viewLifecycleOwner,
            Observer {
                it.let { viewModel.onSurveyChanged(it) }
            }
        )

        viewModel.privacyGradeViewState.observe(
            viewLifecycleOwner,
            Observer {
                it.let { renderer.renderPrivacyGrade(it) }
            }
        )

        addTabsObserver()
    }

    private fun processFileDownloadedCommand(command: DownloadCommand) {
        when (command) {
            is DownloadCommand.ShowDownloadStartedMessage -> downloadStarted(command)
            is DownloadCommand.ShowDownloadFailedMessage -> downloadFailed(command)
            is DownloadCommand.ShowDownloadSuccessMessage -> downloadSucceeded(command)
        }
    }

    @SuppressLint("WrongConstant")
    private fun downloadStarted(command: DownloadCommand.ShowDownloadStartedMessage) {
        view?.makeSnackbarWithNoBottomInset(getString(command.messageId, command.fileName), DOWNLOAD_SNACKBAR_LENGTH)?.show()
    }

    private fun downloadFailed(command: DownloadCommand.ShowDownloadFailedMessage) {
        val downloadFailedSnackbar = when {
            command.showEnableDownloadManagerAction ->
                view?.makeSnackbarWithNoBottomInset(getString(command.messageId), Snackbar.LENGTH_LONG)
                    ?.apply {
                        this.setAction(com.schoolonair.wallet.component.resources.R.string.enable) {
                            showDownloadManagerAppSettings()
                        }
                    }
            else -> view?.makeSnackbarWithNoBottomInset(getString(command.messageId), Snackbar.LENGTH_LONG)
        }
        view?.postDelayed({ downloadFailedSnackbar ?.show() }, DOWNLOAD_SNACKBAR_DELAY)
    }

    private fun downloadSucceeded(command: DownloadCommand.ShowDownloadSuccessMessage) {
        val downloadSucceededSnackbar = view?.makeSnackbarWithNoBottomInset(getString(command.messageId, command.fileName), Snackbar.LENGTH_LONG)
            ?.apply {
                this.setAction(com.schoolonair.wallet.component.resources.R.string.downloadsDownloadFinishedActionName) {
                    val result = downloadsFileActions.openFile(requireActivity(), File(command.filePath))
                    if (!result) {
                        view.makeSnackbarWithNoBottomInset(getString(com.schoolonair.wallet.component.resources.R.string.downloadsCannotOpenFileErrorMessage), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        view?.postDelayed({ downloadSucceededSnackbar?.show() }, DOWNLOAD_SNACKBAR_DELAY)
    }

    private fun addTabsObserver() {
        viewModel.tabs.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    decorator.renderTabIcon(it)
                }
            }
        )
    }

    private fun fragmentIsVisible(): Boolean {
        // using isHidden rather than isVisible, as isVisible will incorrectly return false when windowToken is not yet initialized.
        // changes on isHidden will be received in onHiddenChanged
        return !isHidden
    }

    private fun showHome() {
        viewModel.clearPreviousAppLink()
        dismissAppLinkSnackBar()
        errorSnackbar?.dismiss()
        view?.findViewById<ScrollView>(R.id.newTabLayout)?.show()
        view?.findViewById<FrameLayout>(R.id.browserLayout)?.gone()
        view?.findViewById<AppBarLayout>(R.id.appBarLayout)?.setExpanded(true)
        webView?.onPause()
        webView?.hide()
    }

    private fun showBrowser() {
        view?.findViewById<ScrollView>(R.id.newTabLayout)?.gone()
        view?.findViewById<FrameLayout>(R.id.browserLayout)?.show()
        webView?.show()
        webView?.onResume()
    }

    fun submitQuery(query: String) {
        viewModel.onUserSubmittedQuery(query)
    }

    private fun navigate(
        url: String,
        headers: Map<String, String>
    ) {
        hideKeyboard()
        renderer.hideFindInPage()
        viewModel.registerDaxBubbleCtaDismissed()
        webView?.loadUrl(url, headers)
    }

    fun onRefreshRequested() {
        viewModel.onRefreshRequested()
    }

    fun refresh() {
        webView?.resetView()
        webView?.reload()
        viewModel.onWebViewRefreshed()
    }

    private fun processCommand(it: Command?) {
        if (it !is Command.DaxCommand) {
            renderer.cancelTrackersAnimation()
        }
        when (it) {
            is Command.Refresh -> refresh()
            is Command.OpenInNewTab -> {
                browserActivity?.openInNewTab(it.query, it.sourceTabId)
            }
            is Command.OpenMessageInNewTab -> {
                browserActivity?.openMessageInNewTab(it.message, it.sourceTabId, tabId, chainId, accountId)
            }
            is Command.OpenTabById ->{
                Timber.d("1995 OpenTabById tabId " + it.tab?.tabId)
                browserActivity?.selectTab(it.tab)
            }
            is Command.OpenInNewBackgroundTab -> {
                openInNewBackgroundTab()
            }
            is Command.LaunchNewTab -> {
                browserActivity?.launchNewTab()
            }
            is Command.ShowSavedSiteAddedConfirmation -> savedSiteAdded(it.savedSiteChangedViewState)
            is Command.ShowEditSavedSiteDialog -> editSavedSite(it.savedSiteChangedViewState)
            is Command.DeleteSavedSiteConfirmation -> confirmDeleteSavedSite(it.savedSite)
            is Command.ShowFireproofWebSiteConfirmation -> fireproofWebsiteConfirmation(it.fireproofWebsiteEntity)
            is Command.DeleteFireproofConfirmation -> removeFireproofWebsiteConfirmation(it.fireproofWebsiteEntity)
            is Command.ShowPrivacyProtectionEnabledConfirmation -> privacyProtectionEnabledConfirmation(it.domain)
            is Command.ShowPrivacyProtectionDisabledConfirmation -> privacyProtectionDisabledConfirmation(it.domain)
            is Command.Navigate -> {
                dismissAppLinkSnackBar()
                navigate(it.url, it.headers)
            }
            is Command.NavigateBack -> {
                dismissAppLinkSnackBar()
                webView?.goBackOrForward(-it.steps)
            }
            is Command.NavigateForward -> {
                dismissAppLinkSnackBar()
                webView?.goForward()
            }
            is Command.ResetHistory -> {
                resetWebView()
            }
            is Command.DialNumber -> {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${it.telephoneNumber}")
                openExternalDialog(intent = intent, fallbackUrl = null, fallbackIntent = null, useFirstActivityFound = false)
            }
            is Command.SendEmail -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse(it.emailAddress)
                openExternalDialog(intent)
            }
            is Command.SendSms -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${it.telephoneNumber}"))
                openExternalDialog(intent)
            }
            is Command.ShowKeyboard -> {
                showKeyboard()
            }
            is Command.HideKeyboard -> {
                hideKeyboard()
            }
            is Command.BrokenSiteFeedback -> {
                launchBrokenSiteFeedback(it.data)
            }
            is Command.ShowFullScreen -> {
                binding.webViewFullScreenContainer?.addView(
                    it.view,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
            is Command.DownloadImage -> requestImageDownload(it.url, it.requestUserConfirmation)
            is Command.FindInPageCommand -> webView?.findAllAsync(it.searchTerm)
            is Command.DismissFindInPage -> webView?.findAllAsync("")
            is Command.ShareLink -> launchSharePageChooser(it.url)
            is Command.CopyLink -> clipboardManager.setPrimaryClip(ClipData.newPlainText(null, it.url))
            is Command.ShowFileChooser -> {
                launchFilePicker(it)
            }
            is Command.AddHomeShortcut -> {
                context?.let { context ->
                    addHomeShortcut(it, context)
                }
            }
            is Command.ShowAppLinkPrompt -> {
                showAppLinkSnackBar(it.appLink)
            }
            is Command.OpenAppLink -> {
                Timber.d("1995 OpenAppLink")
                openAppLink(it.appLink)
            }
            is Command.HandleNonHttpAppLink -> {
                openExternalDialog(
                    intent = it.nonHttpAppLink.intent,
                    fallbackUrl = it.nonHttpAppLink.fallbackUrl,
                    fallbackIntent = it.nonHttpAppLink.fallbackIntent,
                    useFirstActivityFound = false,
                    headers = it.headers
                )
            }
            is Command.ExtractUrlFromCloakedAmpLink -> {
                Timber.d("1995 ExtractUrlFromCloakedAmpLink")
                extractUrlFromAmpLink(it.initialUrl)
            }
            is Command.LoadExtractedUrl -> {
                webView?.loadUrl(it.extractedUrl)
                destroyUrlExtractingWebView()
            }
            is Command.LaunchSurvey -> launchSurvey(it.survey)
            is Command.LaunchPlayStore -> launchPlayStore(it.appPackage)
            is Command.SubmitUrl -> submitQuery(it.url)
            is Command.LaunchAddWidget -> addWidgetLauncher.launchAddWidget(activity)
            is Command.LaunchDefaultBrowser -> launchDefaultBrowser()
            is Command.RequiresAuthentication -> showAuthenticationDialog(it.request)
            is Command.SaveCredentials -> saveBasicAuthCredentials(it.request, it.credentials)
            is Command.GenerateWebViewPreviewImage -> generateWebViewPreviewImage()
            is Command.LaunchTabSwitcher -> launchTabSwitcher()
            is Command.ShowErrorWithAction -> showErrorSnackbar(it)
            is Command.DaxCommand.FinishTrackerAnimation -> finishTrackerAnimation()
            is Command.DaxCommand.HideDaxDialog -> showHideTipsDialog(it.cta)
            is Command.HideWebContent -> webView?.hide()
            is Command.ShowWebContent -> webView?.show()
            is Command.CheckSystemLocationPermission -> checkSystemLocationPermission(it.domain, it.deniedForever)
            is Command.RequestSystemLocationPermission -> requestLocationPermissions()
            is Command.AskDomainPermission -> askSiteLocationPermission(it.domain)
            is Command.RefreshUserAgent -> refreshUserAgent(it.url, it.isDesktop)
            is Command.AskToFireproofWebsite -> askToFireproofWebsite(requireContext(), it.fireproofWebsite)
            is Command.AskToAutomateFireproofWebsite -> askToAutomateFireproofWebsite(requireContext(), it.fireproofWebsite)
            is Command.AskToDisableLoginDetection -> askToDisableLoginDetection(requireContext())
            is Command.ShowDomainHasPermissionMessage -> showDomainHasLocationPermission(it.domain)
            is Command.ConvertBlobToDataUri -> convertBlobToDataUri(it)
            is Command.RequestFileDownload -> requestFileDownload(it.url, it.contentDisposition, it.mimeType, it.requestUserConfirmation)
            is Command.ChildTabClosed -> processUriForThirdPartyCookies()
            is Command.CopyAliasToClipboard -> copyAliasToClipboard(it.alias)
            is Command.InjectEmailAddress -> injectEmailAddress(it.address)
            is Command.ShowEmailTooltip -> showEmailTooltip(it.address)
            is Command.InjectCredentials -> injectAutofillCredentials(it.url, it.credentials)
            is Command.CancelIncomingAutofillRequest -> injectAutofillCredentials(it.url, null)
            is Command.EditWithSelectedQuery -> {
                view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.setText(it.query)
                view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.setSelection(it.query.length)
            }
            is ShowBackNavigationHistory -> showBackNavigationHistory(it)
            is NavigateToHistory -> navigateBackHistoryStack(it.historyStackIndex)
            is Command.EmailSignEvent -> {
                notifyEmailSignEvent()
            }
            is Command.PrintLink -> launchPrint(it.url)
            else -> {}
        }
    }

    private fun extractUrlFromAmpLink(initialUrl: String) {
        context?.let {
            val client = urlExtractingWebViewClient
            client.urlExtractionListener = viewModel

            Timber.d("AMP link detection: Creating WebView for URL extraction")
            urlExtractingWebView = UrlExtractingWebView(requireContext(), client, urlExtractorUserAgent, urlExtractor)

            urlExtractingWebView?.urlExtractionListener = viewModel

            Timber.d("AMP link detection: Loading AMP URL for extraction")
            urlExtractingWebView?.loadUrl(initialUrl)
        }
    }

    private fun destroyUrlExtractingWebView() {
        urlExtractingWebView?.destroyWebView()
        urlExtractingWebView = null
    }

    private fun injectEmailAddress(alias: String) {
        webView?.let {
            emailInjector.injectAddressInEmailField(it, alias, it.url)
        }
    }

    private fun notifyEmailSignEvent() {
        webView?.let {
            emailInjector.notifyWebAppSignEvent(it, it.url)
        }
    }

    private fun copyAliasToClipboard(alias: String) {
        context?.let {
            val clipboard: ClipboardManager? = ContextCompat.getSystemService(it, ClipboardManager::class.java)
            val clip: ClipData = ClipData.newPlainText("Alias", alias)
            clipboard?.setPrimaryClip(clip)
            binding.rootView.makeSnackbarWithNoBottomInset(
                getString(com.schoolonair.wallet.component.resources.R.string.aliasToClipboardMessage),
                Snackbar.LENGTH_LONG
            )?.show()
        }
    }

    private fun processUriForThirdPartyCookies() {
        webView?.let {
            val url = it.url ?: return
            launch {
                thirdPartyCookieManager.processUriForThirdPartyCookies(it, url.toUri())
            }
        }
    }

    private fun locationPermissionsHaveNotBeenGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun checkSystemLocationPermission(
        domain: String,
        deniedForever: Boolean
    ) {
        if (locationPermissionsHaveNotBeenGranted()) {
            if (deniedForever) {
                viewModel.onSystemLocationPermissionDeniedOneTime()
            } else {
                val dialog = SystemLocationPermissionDialog.instance(domain)
                dialog.show(childFragmentManager, SystemLocationPermissionDialog.SYSTEM_LOCATION_PERMISSION_TAG)
            }
        } else {
            viewModel.onSystemLocationPermissionGranted()
        }
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_GEO_LOCATION
        )
    }

    private fun askSiteLocationPermission(domain: String) {
        val dialog = SiteLocationPermissionDialog.instance(domain, false, tabId)
        dialog.show(childFragmentManager, SiteLocationPermissionDialog.SITE_LOCATION_PERMISSION_TAG)
    }

    private fun launchBrokenSiteFeedback(data: BrokenSiteData) {
        context?.let {
            val options = ActivityOptions.makeSceneTransitionAnimation(browserActivity).toBundle()
            startActivity(BrokenSiteActivity.intent(it, data), options)
        }
    }

    private fun showErrorSnackbar(command: Command.ShowErrorWithAction) {
        // Snackbar is global and it should appear only the foreground fragment
        if (errorSnackbar?.view?.isAttachedToWindow == false && isVisible) {
            errorSnackbar?.setText(command.textResId)
            errorSnackbar?.setAction(com.schoolonair.wallet.component.resources.R.string.crashedWebViewErrorAction) { command.action() }?.show()
        }
    }

    private fun showDomainHasLocationPermission(domain: String) {
        val snackbar = binding.rootView.makeSnackbarWithNoBottomInset(
            getString(com.schoolonair.wallet.component.resources.R.string.preciseLocationSnackbarMessage, domain.websiteFromGeoLocationsApiOrigin()),
            Snackbar.LENGTH_SHORT
        )
        snackbar?.view?.setOnClickListener {
            browserActivity?.launchLocationSettings()
        }
        snackbar?.show()
    }

    private fun generateWebViewPreviewImage() {
        webView?.let { webView ->

            // if there's an existing job for generating a preview, cancel that in favor of the new request
            bitmapGeneratorJob?.cancel()

            bitmapGeneratorJob = launch {
                Timber.d("Generating WebView preview")
                try {
                    val preview = previewGenerator.generatePreview(webView)
                    val fileName = previewPersister.save(preview, tabId)
                    viewModel.updateTabPreview(tabId, fileName)
                    Timber.d("Saved and updated tab preview")
                } catch (e: Exception) {
                    Timber.d(e, "Failed to generate WebView preview")
                }
            }
        }
    }

    private fun openInNewBackgroundTab() {
        view?.findViewById<AppBarLayout>(R.id.appBarLayout)?.setExpanded(true, true)
        viewModel.tabs.removeObservers(this)
        decorator.incrementTabs()
    }

    private fun showAppLinkSnackBar(appLink: SpecialUrlDetector.UrlType.AppLink) {
        view?.let { view ->

            val message: String?
            val action: String?

            if (appLink.appIntent != null) {
                val packageName = appLink.appIntent!!.component?.packageName ?: return
                message = getString(com.schoolonair.wallet.component.resources.R.string.appLinkSnackBarMessage, getAppName(packageName))
                action = getString(com.schoolonair.wallet.component.resources.R.string.appLinkSnackBarAction)
            } else {
                message = getString(com.schoolonair.wallet.component.resources.R.string.appLinkMultipleSnackBarMessage)
                action = getString(com.schoolonair.wallet.component.resources.R.string.appLinkMultipleSnackBarAction)
            }

            appLinksSnackBar = view.makeSnackbarWithNoBottomInset(
                message,
                Snackbar.LENGTH_LONG
            ).setAction(action) {
                pixel.fire(AppPixelName.APP_LINKS_SNACKBAR_OPEN_ACTION_PRESSED)
                openAppLink(appLink)
            }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    pixel.fire(AppPixelName.APP_LINKS_SNACKBAR_SHOWN)
                }

                override fun onDismissed(
                    transientBottomBar: Snackbar?,
                    event: Int
                ) {
                    super.onDismissed(transientBottomBar, event)
                }
            })

            appLinksSnackBar?.setDuration(6000)?.show()
        }
    }

    private fun getAppName(packageName: String): String? {
        val packageManager: PackageManager? = context?.packageManager
        val applicationInfo: ApplicationInfo? = try {
            packageManager?.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return if (applicationInfo != null) {
            packageManager?.getApplicationLabel(applicationInfo).toString()
        } else {
            null
        }
    }

    @Suppress("NewApi") // we use appBuildConfig
    private fun openAppLink(appLink: SpecialUrlDetector.UrlType.AppLink) {
        if (appLink.appIntent != null) {
            appLink.appIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(appLink.appIntent)
        } else if (appLink.excludedComponents != null && appBuildConfig.sdkInt >= Build.VERSION_CODES.N) {
            val title = getString(com.schoolonair.wallet.component.resources.R.string.appLinkIntentChooserTitle)
            val chooserIntent = getChooserIntent(appLink.uriString, title,
                appLink.excludedComponents!!
            )
            startActivity(chooserIntent)
        }
        viewModel.clearPreviousUrl()
    }

    private fun dismissAppLinkSnackBar() {
        appLinksSnackBar?.dismiss()
        appLinksSnackBar = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getChooserIntent(
        url: String?,
        title: String,
        excludedComponents: List<ComponentName>
    ): Intent {
        val urlIntent = Intent.parseUri(url, Intent.URI_ANDROID_APP_SCHEME)
        urlIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val chooserIntent = Intent.createChooser(urlIntent, title)
        chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponents.toTypedArray())
        return chooserIntent
    }

    private fun openExternalDialog(
        intent: Intent,
        fallbackUrl: String? = null,
        fallbackIntent: Intent? = null,
        useFirstActivityFound: Boolean = true,
        headers: Map<String, String> = emptyMap()
    ) {
        context?.let {
            val pm = it.packageManager
            val activities = pm.queryIntentActivities(intent, 0)

            if (activities.isEmpty()) {
                when {
                    fallbackIntent != null -> {
                        val fallbackActivities = pm.queryIntentActivities(fallbackIntent, 0)
                        launchDialogForIntent(it, pm, fallbackIntent, fallbackActivities, useFirstActivityFound)
                    }
                    fallbackUrl != null -> {
                        webView?.loadUrl(fallbackUrl, headers)
                    }
                    else -> {
                        showToast(com.schoolonair.wallet.component.resources.R.string.unableToOpenLink)
                    }
                }
            } else {
                launchDialogForIntent(it, pm, intent, activities, useFirstActivityFound)
            }
        }
    }

    private fun launchDialogForIntent(
        context: Context,
        pm: PackageManager?,
        intent: Intent,
        activities: List<ResolveInfo>,
        useFirstActivityFound: Boolean
    ) {
        if (activities.size == 1 || useFirstActivityFound) {
            val activity = activities.first()
            val appTitle = pm?.let { activity.loadLabel(it) } ?: activity.activityInfo.packageName
            Timber.i("Exactly one app available for intent: $appTitle")
            launchExternalAppDialog(context) { context.startActivity(intent) }
        } else {
            val title = getString(com.schoolonair.wallet.component.resources.R.string.openExternalApp)
            val intentChooser = Intent.createChooser(intent, title)
            launchExternalAppDialog(context) { context.startActivity(intentChooser) }
        }
    }

    private fun askToFireproofWebsite(
        context: Context,
        fireproofWebsite: FireproofWebsiteEntity
    ) {
        val isShowing = loginDetectionDialog?.isShowing

        if (isShowing != true) {
            loginDetectionDialog = AlertDialog.Builder(context)
                .setTitle(getString(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteLoginDialogTitle, fireproofWebsite.website()))
                .setMessage(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteLoginDialogDescription)
                .setPositiveButton(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteLoginDialogPositive) { _, _ ->
                    viewModel.onUserConfirmedFireproofDialog(fireproofWebsite.domain)
                }.setNegativeButton(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteLoginDialogNegative) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.onUserDismissedFireproofLoginDialog()
                }.setOnCancelListener {
                    viewModel.onUserDismissedFireproofLoginDialog()
                }.show()

            viewModel.onFireproofLoginDialogShown()
        }
    }

    private fun askToAutomateFireproofWebsite(
        context: Context,
        fireproofWebsite: FireproofWebsiteEntity
    ) {
        val isShowing = automaticFireproofDialog?.isShowing

        if (isShowing != true) {
            automaticFireproofDialog = AlertDialog.Builder(context)
                .setTitle(getString(com.schoolonair.wallet.component.resources.R.string.automaticFireproofWebsiteLoginDialogTitle))
                .setMessage(com.schoolonair.wallet.component.resources.R.string.automaticFireproofWebsiteLoginDialogDescription)
                .setPositiveButton(com.schoolonair.wallet.component.resources.R.string.automaticFireproofWebsiteLoginDialogFirstOption) { _, _ ->
                    viewModel.onUserEnabledAutomaticFireproofLoginDialog(fireproofWebsite.domain)
                }.setNegativeButton(com.schoolonair.wallet.component.resources.R.string.automaticFireproofWebsiteLoginDialogSecondOption) { _, _ ->
                    viewModel.onUserFireproofSiteInAutomaticFireproofLoginDialog(fireproofWebsite.domain)
                }.setNeutralButton(com.schoolonair.wallet.component.resources.R.string.automaticFireproofWebsiteLoginDialogThirdOption) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.onUserDismissedAutomaticFireproofLoginDialog()
                }.setOnCancelListener { it.dismiss() }
                .show()

            viewModel.onFireproofLoginDialogShown()
        }
    }

    private fun askToDisableLoginDetection(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(getString(com.schoolonair.wallet.component.resources.R.string.disableLoginDetectionDialogTitle))
            .setMessage(com.schoolonair.wallet.component.resources.R.string.disableLoginDetectionDialogDescription)
            .setPositiveButton(com.schoolonair.wallet.component.resources.R.string.disableLoginDetectionDialogPositive) { _, _ ->
                viewModel.onUserConfirmedDisableLoginDetectionDialog()
            }
            .setNegativeButton(com.schoolonair.wallet.component.resources.R.string.disableLoginDetectionDialogNegative) { dialog, _ ->
                dialog.dismiss()
                viewModel.onUserDismissedDisableLoginDetectionDialog()
            }.setOnCancelListener {
                viewModel.onUserDismissedDisableLoginDetectionDialog()
            }.show()

        viewModel.onDisableLoginDetectionDialogShown()
    }

    private fun launchExternalAppDialog(
        context: Context,
        onClick: () -> Unit
    ) {
        val isShowing = alertDialog?.isShowing

        if (isShowing != true) {
            alertDialog = AlertDialog.Builder(context)
                .setTitle(com.schoolonair.wallet.component.resources.R.string.launchingExternalApp)
                .setMessage(getString(com.schoolonair.wallet.component.resources.R.string.confirmOpenExternalApp))
                .setPositiveButton(com.schoolonair.wallet.component.resources.R.string.open) { _, _ ->
                    onClick()
                }
                .setNeutralButton(com.schoolonair.wallet.component.resources.R.string.closeTab) { dialog, _ ->
                    dialog.dismiss()
                    launch {
                        viewModel.closeCurrentTab()
                        destroyWebView()
                    }
                }
                .setNegativeButton(com.schoolonair.wallet.component.resources.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_CODE_CHOOSE_FILE) {
            handleFileUploadResult(resultCode, data)
        }
    }

    private fun handleFileUploadResult(
        resultCode: Int,
        intent: Intent?
    ) {
        if (resultCode != RESULT_OK || intent == null) {
            Timber.i("Received resultCode $resultCode (or received null intent) indicating user did not select any files")
            pendingUploadTask?.onReceiveValue(null)
            return
        }

        val uris = fileChooserIntentBuilder.extractSelectedFileUris(intent)
        pendingUploadTask?.onReceiveValue(uris)
    }

    private fun showToast(@StringRes messageId: Int) {
        Toast.makeText(context?.applicationContext, messageId, Toast.LENGTH_LONG).show()
    }

    private fun showAuthenticationDialog(request: BasicAuthenticationRequest) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            val dialog = HttpAuthenticationDialogFragment.createHttpAuthenticationDialog(request.site)
            dialog.show(fragmentManager, AUTHENTICATION_DIALOG_TAG)
            dialog.listener = viewModel
            dialog.request = request
        }
    }

    private fun saveBasicAuthCredentials(
        request: BasicAuthenticationRequest,
        credentials: BasicAuthenticationCredentials
    ) {
        webView?.let {
            webViewHttpAuthStore.setHttpAuthUsernamePassword(
                it,
                host = request.host,
                realm = request.realm,
                username = credentials.username,
                password = credentials.password
            )
        }
    }

    private fun configureAutoComplete() {
        val context = context ?: return
        view?.findViewById<RecyclerView>(R.id.autoCompleteSuggestionsList)?.layoutManager = LinearLayoutManager(context)
        autoCompleteSuggestionsAdapter = BrowserAutoCompleteSuggestionsAdapter(
            immediateSearchClickListener = {
                userSelectedAutocomplete(it)
            },
            editableSearchClickListener = {
                viewModel.onUserSelectedToEditQuery(it.phrase)
            }
        )
        view?.findViewById<RecyclerView>(R.id.autoCompleteSuggestionsList)?.adapter = autoCompleteSuggestionsAdapter
    }

    private fun configureOmnibarQuickAccessGrid() {
        configureQuickAccessGridLayout(binding.quickAccessSuggestionsRecyclerView)
        omnibarQuickAccessAdapter = createQuickAccessAdapter(originPixel = AppPixelName.FAVORITE_OMNIBAR_ITEM_PRESSED) { viewHolder ->
            binding.quickAccessSuggestionsRecyclerView.enableAnimation()
            omnibarQuickAccessItemTouchHelper.startDrag(viewHolder)
        }
        omnibarQuickAccessItemTouchHelper = createQuickAccessItemHolder(binding.quickAccessSuggestionsRecyclerView, omnibarQuickAccessAdapter)
        binding.quickAccessSuggestionsRecyclerView.adapter = omnibarQuickAccessAdapter
        binding.quickAccessSuggestionsRecyclerView.disableAnimation()
    }

    private fun configureHomeTabQuickAccessGrid() {
        configureQuickAccessGridLayout(view?.findViewById(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView))
        quickAccessAdapter = createQuickAccessAdapter(originPixel = AppPixelName.FAVORITE_HOMETAB_ITEM_PRESSED) { viewHolder ->
            view?.findViewById<RecyclerView>(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView)?.enableAnimation()
            quickAccessItemTouchHelper.startDrag(viewHolder)
        }
        quickAccessItemTouchHelper = createQuickAccessItemHolder(view?.findViewById<RecyclerView>(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView), quickAccessAdapter)
        view?.findViewById<RecyclerView>(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView)?.adapter = quickAccessAdapter
        view?.findViewById<RecyclerView>(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView)?.disableAnimation()
    }

    private fun createQuickAccessItemHolder(
        recyclerView: RecyclerView?,
        apapter: FavoritesQuickAccessAdapter
    ): ItemTouchHelper {
        return ItemTouchHelper(
            QuickAccessDragTouchItemListener(
                apapter,
                object : QuickAccessDragTouchItemListener.DragDropListener {
                    override fun onListChanged(listElements: List<QuickAccessFavorite>) {
                        viewModel.onQuickAccessListChanged(listElements)
                        recyclerView?.disableAnimation()
                    }
                }
            )
        ).also {
            it.attachToRecyclerView(recyclerView)
        }
    }

    private fun createQuickAccessAdapter(
        originPixel: AppPixelName,
        onMoveListener: (RecyclerView.ViewHolder) -> Unit
    ): FavoritesQuickAccessAdapter {
        return FavoritesQuickAccessAdapter(
            this, faviconManager, onMoveListener,
            {
                pixel.fire(originPixel)
                viewModel.onUserSubmittedQuery(it.favorite.url)
            },
            { viewModel.onEditSavedSiteRequested(it.favorite) },
            { viewModel.onDeleteQuickAccessItemRequested(it.favorite) }
        )
    }

    private fun configureQuickAccessGridLayout(recyclerView: RecyclerView?) {
        val numOfColumns = gridViewColumnCalculator.calculateNumberOfColumns(QUICK_ACCESS_ITEM_MAX_SIZE_DP, QUICK_ACCESS_GRID_MAX_COLUMNS)
        val layoutManager = GridLayoutManager(requireContext(), numOfColumns)
        recyclerView?.layoutManager = layoutManager
        val sidePadding = gridViewColumnCalculator.calculateSidePadding(QUICK_ACCESS_ITEM_MAX_SIZE_DP, numOfColumns)
        recyclerView?.setPadding(sidePadding, recyclerView.paddingTop, sidePadding, recyclerView.paddingBottom)
    }

    private fun configurePrivacyGrade() {
        view?.findViewById<ImageButton>(R.id.privacyGradeButton)?.setOnClickListener {
            browserActivity?.launchPrivacyDashboard()
        }
    }

    private fun configureFindInPage() {
        view?.findViewById<EditText>(R.id.findInPageInput)?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && view?.findViewById<EditText>(R.id.findInPageInput)?.text.toString() != viewModel.findInPageViewState.value?.searchTerm) {
                viewModel.userFindingInPage(view?.findViewById<EditText>(R.id.findInPageInput)?.text.toString())
            }
        }

        view?.findViewById<ImageView>(R.id.previousSearchTermButton)?.setOnClickListener { webView?.findNext(false) }
        view?.findViewById<ImageView>(R.id.nextSearchTermButton)?.setOnClickListener { webView?.findNext(true) }
        view?.findViewById<ImageView>(R.id.closeFindInPagePanel)?.setOnClickListener {
            viewModel.dismissFindInView()
        }
    }

    private fun configureOmnibarTextInput() {
        view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus: Boolean ->
                viewModel.onOmnibarInputStateChanged(view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.text.toString(), hasFocus, false)
                if (!hasFocus) {
                    view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.hideKeyboard()
                    binding.focusDummy.requestFocus()
                }
            }

        view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.onBackKeyListener = object : KeyboardAwareEditText.OnBackKeyListener {
            override fun onBackKey(): Boolean {
                view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.hideKeyboard()
                binding.focusDummy.requestFocus()
                return true
            }
        }

        view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.setOnEditorActionListener(
            TextView.OnEditorActionListener { _, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_GO || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    userEnteredQuery(view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.text.toString())
                    return@OnEditorActionListener true
                }
                false
            }
        )

        view?.findViewById<ImageView>(R.id.clearTextButton)?.setOnClickListener { view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.setText("") }
    }

    private fun userSelectedAutocomplete(suggestion: AutoCompleteSuggestion) {
        // send pixel before submitting the query and changing the autocomplete state to empty; otherwise will send the wrong params
        appCoroutineScope.launch {
            viewModel.fireAutocompletePixel(suggestion)
            withContext(Dispatchers.Main) {
                val origin = when (suggestion) {
                    is AutoCompleteBookmarkSuggestion -> FromAutocomplete(isNav = true)
                    is AutoCompleteSearchSuggestion -> FromAutocomplete(isNav = suggestion.isUrl)
                }
                viewModel.onUserSubmittedQuery(suggestion.phrase, origin)
            }
        }
    }

    private fun userEnteredQuery(query: String) {
        viewModel.onUserSubmittedQuery(query)
    }

    private val gson by lazy { Gson() }
//    private val chainNetworks = mutableMapOf<Long, ChainNetwork>()

    //First time get chain Id, check wallet active by accountId then get address.
//    val chainIdDefault = requireArguments()[BrowserActivityNavigationUtils.EXTRA_CHAIN_ID] as Long
//    private fun loadAddress(){
//        loadChainNetworks()
//        chainId = chainIdDefault
//    }

    private fun loadChainNetworks(){
//        val data = arguments?.getString(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK) ?: ""
//        if(!data.isNullOrEmpty()){
//            val empMapType = object : TypeToken<Map<Long, ChainNetwork>>() {}.type
//            val result: Map<Long, ChainNetwork> = gson.fromJson(data, empMapType)
//            chainNetworks.clear()
//            chainNetworks.putAll(result)
//        }
    }

    private fun loadChainId(){
        loadChainNetworks()
        chainId = arguments?.getLong(BrowserActivityNavigationUtils.EXTRA_CHAIN_ID) ?: 1L
        accountId = arguments?.getString(BrowserActivityNavigationUtils.EXTRA_ACCOUNT_ID) ?: ""

        addressConnect = arguments?.getString(BrowserActivityNavigationUtils.EXTRA_ADDRESS) ?: ""
//        balance = chainNetwork?.balance ?: BigDecimal(0)
//        coinDecimals = chainNetwork?.coinDecimals ?: 18
        Timber.d("1991 loadChainId addressConnect $addressConnect  chainId $chainId accountId $accountId")
        walletConnect = Wallet(addressConnect)
        walletConnect?.type = WalletType.HDKEY
        activeNetwork = viewModel.getNetworkInfo(chainId)
        rpcServerUrl = arguments?.getString(BrowserActivityNavigationUtils.EXTRA_RPC_SERVER_URL) ?: ""
        viewModel.setCurrentAddress(addressConnect)
        viewModel.setCurrentWalletAddress(addressConnect)
        viewModel.onDefaultWallet(walletConnect)

//        activeNetwork = NetworkInfo(wallet.coin.name, wallet.coin.code, rpcServerUrl, rpcServerUrl, chainId, "", "")

        viewModel.setNetwork(chainId)
//            onNetworkChanged(viewModel.getNetworkInfo(newNetworkId))
//        startBalanceListener()
//        configureWebView()
//        viewModel.updateGasPrice(chainId)

        initWalletConnectSessions(accountId, addressConnect)
//        loadNewNetwork(chainId)
//        val chainNetwork = chainNetworks[chainId]
//        addressConnect = chainNetwork?.address ?: ""
//        activeNetwork = viewModel.getNetworkInfo(chainId)
//        rpcServerUrl = activeNetwork?.rpcServerUrl ?: ""
        Timber.d("1991 loadChainId addressConnect $addressConnect")
        Timber.d("1991 loadChainId activeNetwork name" + activeNetwork?.name)
    }

    private fun handleScroll(){
        view?.findViewById<AppBarLayout>(R.id.appBarLayout)?.addOnOffsetChangedListener(OnOffsetChangedListener { _, verticalOffset ->
            val result = verticalOffset*-1f
            val bottomBar = view?.findViewById<ConstraintLayout>(R.id.toolbarContainer2)
            bottomBar?.translationY = result
        })
    }

    private var walletConnectSession: String = ""
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView = layoutInflater.inflate(
            R.layout.include_mangala_browser_webview,
            binding.webViewContainer,
            true
        ).findViewById(R.id.browserWebView) as MangalaWebView

        handleScroll()

        webView?.setBrowserWebViewClient(webViewClient)
        webView?.setChainId(chainId)
        webView?.setRpcUrl(rpcServerUrl)
        Timber.d("1991 configureWebView addressConnect $addressConnect")
        Timber.d("1991 configureWebView chainId $chainId")
        Timber.d("1991 configureWebView rpcServerUrl $rpcServerUrl")
        if(!addressConnect.isNullOrEmpty()){
            webView?.setWalletAddress(Address(addressConnect))
        }
        webView?.setOnSignMessageListener(this)
        webView?.setOnSignPersonalMessageListener(this)
        webView?.setOnSignTransactionListener(this)
        webView?.setOnSignTypedMessageListener(this)
        webView?.setOnEthCallListener(this)
        webView?.setOnWalletAddEthereumChainObjectListener(this)
        webView?.setOnWalletActionListener(this)

        webView?.let {
            // it.webViewClient = webViewClient
//            it.setBrowserWebViewClient(webViewClient)
            it.webChromeClient = webChromeClient
//            it.init()
            it.settings.apply {
                 userAgentString = userAgentProvider.userAgent()
                 javaScriptEnabled = true
                 cacheMode = WebSettings.LOAD_DEFAULT
                 domStorageEnabled = true
                 loadWithOverviewMode = true
                 useWideViewPort = true
                 builtInZoomControls = true
                 displayZoomControls = false


                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                javaScriptCanOpenWindowsAutomatically = appBuildConfig.isTest // only allow when running tests
                setSupportMultipleWindows(true)
                disableWebSql(this)
                setSupportZoom(true)
                configureDarkThemeSupport(this)
                if (accessibilitySettingsDataStore.overrideSystemFontSize) {
                    textZoom = accessibilitySettingsDataStore.fontSize.toInt()
                }
            }

            it.addJavascriptInterface(
                SignCallbackJSInterface(
                    it,
                    it.innerOnSignTransactionListener,
                    it.innerOnSignMessageListener,
                    it.innerOnSignPersonalMessageListener,
                    it.innerOnSignTypedMessageListener,
                    it.innerOnEthCallListener,
                    it.innerAddChainListener,
                    it.innerOnWalletActionListener
                ),
                "alpha"
            )

            it.setDownloadListener { url, _, contentDisposition, mimeType, _ ->
                viewModel.requestFileDownload(url, contentDisposition, mimeType, true)
            }

            it.setOnTouchListener { _, _ ->
                if (view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.isFocused == true) {
                    binding.focusDummy.requestFocus()
                }
                dismissAppLinkSnackBar()
                false
            }

            it.setEnableSwipeRefreshCallback { enable ->
                binding.swipeRefreshContainer?.isEnabled = enable
            }

            registerForContextMenu(it)

            it.setFindListener(this)
            loginDetector.addLoginDetection(it) { viewModel.loginDetected() }
            blobConverterInjector.addJsInterface(it) { url, mimeType -> viewModel.requestFileDownload(url, null, mimeType, true) }
            emailInjector.addJsInterface(it) { viewModel.showEmailTooltip() }
            configureWebViewForAutofill(it)
            printInjector.addJsInterface(it) { viewModel.printFromWebView() }
        }

        if (appBuildConfig.isDebug) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        //

        webView?.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                Timber.d("1995 dapp shouldOverrideUrlLoading $url")
                Timber.d("1995 dapp shouldOverrideUrlLoading tabId $tabId")
                val prefixCheck = url.split(":").toTypedArray()
                if (prefixCheck.size > 1) {
                    val intent: Intent
                    when (prefixCheck[0]) {
                        C.DAPP_PREFIX_TELEPHONE -> {
                            intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse(url)
                            startActivity(Intent.createChooser(intent, "Call " + prefixCheck[1]))
                            return true
                        }
                        C.DAPP_PREFIX_MAILTO -> {
                            intent = Intent(Intent.ACTION_SENDTO)
                            intent.data = Uri.parse(url)
                            startActivity(Intent.createChooser(intent, "Email: " + prefixCheck[1]))
                            return true
                        }
                        C.DAPP_PREFIX_ALPHAWALLET -> if (prefixCheck[1] == C.DAPP_SUFFIX_RECEIVE) {
                            Timber.d("dapp DAPP_PREFIX_ALPHAWALLET")
                            //                                viewModel.showMyAddress(getContext());
                            return true
                        }
                        C.DAPP_PREFIX_WALLETCONNECT -> {
                            // start walletconnect
                            Timber.d("1991 dapp DAPP_PREFIX_WALLETCONNECT")
                            walletConnectSession = url

                            handleWalletConnect(url)
                            return true
                        }
                        else -> {}
                    }
                }

//                setUrlText(url);
                return false
            }
        })
    }

    private fun configureWebViewForAutofill(it: MangalaWebView) {
        browserAutofill.addJsInterface(it, autofillCallback)

        setFragmentResultListener(RESULT_KEY_CREDENTIAL_PICKER) { _, result ->
            autofillCredentialsSelectionResultHandler.processAutofillCredentialSelectionResult(result, this, viewModel)
        }

        setFragmentResultListener(RESULT_KEY_CREDENTIAL_RESULT_SAVE) { _, result ->
            autofillCredentialsSelectionResultHandler.processSaveCredentialsResult(result, viewModel)
        }

        setFragmentResultListener(RESULT_KEY_CREDENTIAL_RESULT_UPDATE) { _, result ->
            autofillCredentialsSelectionResultHandler.processUpdateCredentialsResult(result, viewModel)
        }
    }

    private fun injectAutofillCredentials(url: String, credentials: LoginCredentials?) {
        webView?.let {
            if (it.url != url) {
                Timber.w("WebView url has changed since autofill request; bailing")
                return
            }
            browserAutofill.injectCredentials(credentials)
        }
    }

    private fun showAutofillDialogChooseCredentials(credentials: List<LoginCredentials>) {
        Timber.v("onCredentialsAvailable. %d creds to choose from", credentials.size)
        val url = webView?.url ?: return
        val dialog = credentialAutofillDialogFactory.autofillSelectCredentialsDialog(url, credentials)
        showDialogHidingPrevious(dialog.asDialogFragment(), CredentialAutofillPickerDialog.TAG)
    }

    private fun showAutofillDialogSaveCredentials(currentUrl: String, credentials: LoginCredentials) {
        val url = webView?.url ?: return
        if (url != currentUrl) return

        val dialog = credentialAutofillDialogFactory.autofillSavingCredentialsDialog(url, credentials)
        showDialogHidingPrevious(dialog.asDialogFragment(), CredentialSavePickerDialog.TAG)
    }

    private fun showAutofillDialogUpdateCredentials(currentUrl: String, credentials: LoginCredentials) {
        val url = webView?.url ?: return
        if (url != currentUrl) return

        val dialog = credentialAutofillDialogFactory.autofillSavingUpdateCredentialsDialog(url, credentials)
        showDialogHidingPrevious(dialog.asDialogFragment(), CredentialUpdateExistingCredentialsDialog.TAG)
    }

    private fun showDialogHidingPrevious(dialog: DialogFragment, tag: String) {
        childFragmentManager.findFragmentByTag(tag)?.let {
            Timber.i("Found existing dialog for %s; removing it now", tag)
            childFragmentManager.commitNow(allowStateLoss = true) { remove(it) }
        }
        dialog.show(childFragmentManager, tag)
    }

    private fun configureDarkThemeSupport(webSettings: WebSettings) {
        when (themingDataStore.theme) {
            MangalaTheme.LIGHT -> webSettings.enableLightMode()
            MangalaTheme.DARK -> webSettings.enableDarkMode()
            MangalaTheme.SYSTEM_DEFAULT -> {
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    webSettings.enableDarkMode()
                } else {
                    webSettings.enableLightMode()
                }
            }
        }
    }

    private fun configureSwipeRefresh() {
        val metrics = resources.displayMetrics
        val distanceToTrigger = (DEFAULT_CIRCLE_TARGET_TIMES_1_5 * metrics.density).toInt()
        binding.swipeRefreshContainer.setDistanceToTriggerSync(distanceToTrigger)
        binding.swipeRefreshContainer.setColorSchemeColors(ContextCompat.getColor(requireContext(), com.schoolonair.wallet.component.resources.R.color.cornflowerBlue))

        binding.swipeRefreshContainer.setOnRefreshListener {
            onRefreshRequested()
        }

        binding.swipeRefreshContainer.setCanChildScrollUpCallback {
            webView?.canScrollVertically(-1) ?: false
        }

        // avoids progressView from showing under toolbar
        binding.swipeRefreshContainer.progressViewStartOffset = binding.swipeRefreshContainer.progressViewStartOffset - 15
    }

    /**
     * Explicitly disable database to try protect against Magellan WebSQL/SQLite vulnerability
     */
    private fun disableWebSql(settings: WebSettings) {
        settings.databaseEnabled = false
    }

    private fun addTextChangedListeners() {
        view?.findViewById<EditText>(R.id.findInPageInput)?.replaceTextChangedListener(findInPageTextWatcher)
        view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.replaceTextChangedListener(omnibarInputTextWatcher)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        view: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        webView?.hitTestResult?.let {
            val target = getLongPressTarget(it) ?: return
            viewModel.userLongPressedInWebView(target, menu)
        }
    }

    /**
     * Use requestFocusNodeHref to get the a tag url for the touched image.
     */
    private fun getTargetUrlForImageSource(): String? {
        val handler = Handler()
        val message = handler.obtainMessage()

        webView?.requestFocusNodeHref(message)

        return message.data.getString(URL_BUNDLE_KEY)
    }

    private fun getLongPressTarget(hitTestResult: HitTestResult): LongPressTarget? {
        return when {
            hitTestResult.extra == null -> null
            hitTestResult.type == UNKNOWN_TYPE -> null
            hitTestResult.type == IMAGE_TYPE -> LongPressTarget(
                url = hitTestResult.extra,
                imageUrl = hitTestResult.extra,
                type = hitTestResult.type
            )
            hitTestResult.type == SRC_IMAGE_ANCHOR_TYPE -> LongPressTarget(
                url = getTargetUrlForImageSource(),
                imageUrl = hitTestResult.extra,
                type = hitTestResult.type
            )
            else -> LongPressTarget(
                url = hitTestResult.extra,
                type = hitTestResult.type
            )
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        webView?.hitTestResult?.let {
            val target = getLongPressTarget(it)
            if (target != null && viewModel.userSelectedItemFromLongPressMenu(target, item)) {
                return true
            }
        }

        return super.onContextItemSelected(item)
    }

    private fun savedSiteAdded(savedSiteChangedViewState: SavedSiteChangedViewState) {
        val snackbarMessage = when (savedSiteChangedViewState.savedSite) {
            is Bookmark -> com.schoolonair.wallet.component.resources.R.string.bookmarkAddedMessage
            is Favorite -> com.schoolonair.wallet.component.resources.R.string.favoriteAddedMessage
        }
        binding.browserLayout.makeSnackbarWithNoBottomInset(snackbarMessage, Snackbar.LENGTH_LONG)
            .setAction(com.schoolonair.wallet.component.resources.R.string.edit) {
                editSavedSite(savedSiteChangedViewState)
            }
            .show()
    }

    private fun editSavedSite(savedSiteChangedViewState: SavedSiteChangedViewState) {
        val addBookmarkDialog = EditSavedSiteDialogFragment.instance(
            savedSiteChangedViewState.savedSite,
            savedSiteChangedViewState.bookmarkFolder?.id ?: 0,
            savedSiteChangedViewState.bookmarkFolder?.name
        )
        addBookmarkDialog.show(childFragmentManager, ADD_SAVED_SITE_FRAGMENT_TAG)
        addBookmarkDialog.listener = viewModel
    }

    private fun confirmDeleteSavedSite(savedSite: SavedSite) {
        val message = when (savedSite) {
            is Favorite -> getString(com.schoolonair.wallet.component.resources.R.string.favoriteDeleteConfirmationMessage)
            is Bookmark -> getString(com.schoolonair.wallet.component.resources.R.string.bookmarkDeleteConfirmationMessage, savedSite.title).html(requireContext())
        }
        viewModel.deleteQuickAccessItem(savedSite)
        binding.rootView.makeSnackbarWithNoBottomInset(
            message,
            Snackbar.LENGTH_LONG
        ).setAction(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteSnackbarAction) {
            viewModel.insertQuickAccessItem(savedSite)
        }.show()
    }

    private fun fireproofWebsiteConfirmation(entity: FireproofWebsiteEntity) {
        binding.rootView.makeSnackbarWithNoBottomInset(
            HtmlCompat.fromHtml(getString(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteSnackbarConfirmation, entity.website()), FROM_HTML_MODE_LEGACY),
            Snackbar.LENGTH_LONG
        ).setAction(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteSnackbarAction) {
            viewModel.onFireproofWebsiteSnackbarUndoClicked(entity)
        }.show()
    }

    private fun removeFireproofWebsiteConfirmation(entity: FireproofWebsiteEntity) {
        binding.rootView.makeSnackbarWithNoBottomInset(
            getString(com.schoolonair.wallet.component.resources.R.string.fireproofDeleteConfirmationMessage),
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteSnackbarAction) {
                viewModel.onRemoveFireproofWebsiteSnackbarUndoClicked(entity)
            }
            show()
        }
    }

    private fun privacyProtectionEnabledConfirmation(domain: String) {
        binding.rootView.makeSnackbarWithNoBottomInset(
            HtmlCompat.fromHtml(getString(com.schoolonair.wallet.component.resources.R.string.privacyProtectionEnabledConfirmationMessage, domain), FROM_HTML_MODE_LEGACY),
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(com.schoolonair.wallet.component.resources.R.string.undoSnackbarAction) {
                viewModel.onEnablePrivacyProtectionSnackbarUndoClicked(domain)
            }
            show()
        }
    }

    private fun privacyProtectionDisabledConfirmation(domain: String) {
        binding.rootView.makeSnackbarWithNoBottomInset(
            HtmlCompat.fromHtml(getString(com.schoolonair.wallet.component.resources.R.string.privacyProtectionDisabledConfirmationMessage, domain), FROM_HTML_MODE_LEGACY),
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(com.schoolonair.wallet.component.resources.R.string.undoSnackbarAction) {
                viewModel.onDisablePrivacyProtectionSnackbarUndoClicked(domain)
            }
            show()
        }
    }

    private fun launchSharePageChooser(url: String) {
        val intent = Intent(Intent.ACTION_SEND).also {
            it.type = "text/plain"
            it.putExtra(Intent.EXTRA_TEXT, url)
        }
        try {
            startActivity(Intent.createChooser(intent, null))
        } catch (e: ActivityNotFoundException) {
            Timber.w(e, "Activity not found")
        }
    }

    override fun onFindResultReceived(
        activeMatchOrdinal: Int,
        numberOfMatches: Int,
        isDoneCounting: Boolean
    ) {
        viewModel.onFindResultsReceived(activeMatchOrdinal, numberOfMatches)
    }

    private fun EditText.replaceTextChangedListener(textWatcher: TextChangedWatcher) {
        removeTextChangedListener(textWatcher)
        addTextChangedListener(textWatcher)
    }

    private fun hideKeyboardImmediately() {
        if (!isHidden) {
            Timber.v("Keyboard now hiding")
            view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.hideKeyboard()
            binding.focusDummy.requestFocus()
        }
    }

    private fun hideKeyboard() {
        if (!isHidden) {
            Timber.v("Keyboard now hiding")
            view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.postDelayed(
                KEYBOARD_DELAY
            ) { view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.hideKeyboard() }
            binding.focusDummy?.requestFocus()
        }
    }

    private fun showKeyboardImmediately() {
        if (!isHidden) {
            Timber.v("Keyboard now showing")
            view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.showKeyboard()
        }
    }

    private fun showKeyboard() {
        if (!isHidden) {
            Timber.v("Keyboard now showing")
            view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.postDelayed(
                KEYBOARD_DELAY
            ) { view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.showKeyboard() }
        }
    }

    private fun refreshUserAgent(
        url: String?,
        isDesktop: Boolean
    ) {
        val currentAgent = webView?.settings?.userAgentString
        val newAgent = userAgentProvider.userAgent(url, isDesktop)
        if (newAgent != currentAgent) {
            webView?.settings?.userAgentString = newAgent
        }
        Timber.d("User Agent is $newAgent")
    }

    /**
     * Attempting to save the WebView's state can result in a TransactionTooLargeException being thrown.
     * This will only happen if the bundle size is too large - but the exact size is undefined.
     * Instead of saving using normal Android state mechanism - use our own implementation instead.
     */
    override fun onSaveInstanceState(bundle: Bundle) {
        viewModel.saveWebViewState(webView, tabId)
        super.onSaveInstanceState(bundle)
    }

    override fun onViewStateRestored(bundle: Bundle?) {
        viewModel.restoreWebViewState(webView, view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.text.toString())
        viewModel.determineShowBrowser()
        super.onViewStateRestored(bundle)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        when {
            hidden -> onTabHidden()
            else -> onTabVisible()
        }
    }

    private fun onTabHidden() {
        viewModel.onViewHidden()
        downloadMessagesJob.cancel()
        webView?.onPause()
    }

    private fun onTabVisible() {
        webView?.onResume()
        launchDownloadMessagesJob()
        viewModel.onViewVisible()
    }

    private fun launchDownloadMessagesJob() {
        downloadMessagesJob += lifecycleScope.launch {
            viewModel.downloadCommands().cancellable().collect {
                processFileDownloadedCommand(it)
            }
        }
    }

    /**
     * We don't destroy the activity on config changes like orientation, so we need to ensure we update resources which might change based on config
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        view?.findViewById<ImageView>(R.id.ddgLogo)?.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.logo_full)
        if (view?.findViewById<LinearLayout>(R.id.ctaContainer)?.isNotEmpty() == true) {
            renderer.renderHomeCta()
        }
        configureQuickAccessGridLayout(view?.findViewById(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView))
        configureQuickAccessGridLayout(binding.quickAccessSuggestionsRecyclerView)
        decorator.recreatePopupMenu()
        viewModel.onConfigurationChanged()
    }

    fun onBackPressed(): Boolean {
        if (!isAdded) return false
        return viewModel.onUserPressedBack()
    }

    private fun resetWebView() {
        destroyWebView()
        configureWebView()
    }

    override fun onDestroy() {
        dismissAppLinkSnackBar()
        pulseAnimation.stop()
        animatorHelper.removeListener()
        supervisorJob.cancel()
        popupMenu.dismiss()
        loginDetectionDialog?.dismiss()
        automaticFireproofDialog?.dismiss()
        emailAutofillTooltipDialog?.dismiss()
        browserAutofill.removeJsInterface()
        destroyWebView()
        viewModel.onDestroy()
//        stopBalanceListener()
        super.onDestroy()
    }

    private fun destroyWebView() {
        binding.webViewContainer?.removeAllViews()
        webView?.destroy()
        webView = null
    }

    private fun convertBlobToDataUri(blob: Command.ConvertBlobToDataUri) {
        webView?.let {
            blobConverterInjector.convertBlobIntoDataUriAndDownload(it, blob.url, blob.mimeType)
        }
    }

    private fun requestFileDownload(
        url: String,
        contentDisposition: String?,
        mimeType: String,
        requestUserConfirmation: Boolean
    ) {
        pendingFileDownload = PendingFileDownload(
            url = url,
            contentDisposition = contentDisposition,
            mimeType = mimeType,
            userAgent = userAgentProvider.userAgent(),
            subfolder = Environment.DIRECTORY_DOWNLOADS
        )

        if (hasWriteStoragePermission()) {
            downloadFile(requestUserConfirmation && !URLUtil.isDataUrl(url))
        } else {
            requestWriteStoragePermission()
        }
    }

    private fun requestImageDownload(
        url: String,
        requestUserConfirmation: Boolean
    ) {
        pendingFileDownload = PendingFileDownload(
            url = url,
            userAgent = userAgentProvider.userAgent(),
            subfolder = Environment.DIRECTORY_PICTURES
        )

        if (hasWriteStoragePermission()) {
            downloadFile(requestUserConfirmation)
        } else {
            requestWriteStoragePermission()
        }
    }

    @AnyThread
    private fun downloadFile(requestUserConfirmation: Boolean) {
        val pendingDownload = pendingFileDownload ?: return

        pendingFileDownload = null

        if (requestUserConfirmation) {
            requestDownloadConfirmation(pendingDownload)
        } else {
            continueDownload(pendingDownload)
        }
    }

    private fun requestDownloadConfirmation(pendingDownload: PendingFileDownload) {
        if (isStateSaved) return

        val downloadConfirmationFragment = DownloadConfirmationFragment.instance(pendingDownload)
        showDialogHidingPrevious(downloadConfirmationFragment, DOWNLOAD_CONFIRMATION_TAG)
    }

    private fun launchFilePicker(command: Command.ShowFileChooser) {
        pendingUploadTask = command.filePathCallback
        val canChooseMultipleFiles = command.fileChooserParams.mode == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE
        val intent = fileChooserIntentBuilder.intent(command.fileChooserParams.acceptTypes, canChooseMultipleFiles)
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE)
    }

    private fun minSdk29(): Boolean {
        return appBuildConfig.sdkInt >= Build.VERSION_CODES.Q
    }

    @Suppress("NewApi") // we use appBuildConfig
    private fun hasWriteStoragePermission(): Boolean {
        return minSdk29() ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteStoragePermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("Write external storage permission granted")
                    downloadFile(requestUserConfirmation = true)
                } else {
                    Timber.i("Write external storage permission refused")
                    view?.findViewById<Toolbar>(R.id.toolbar)?.makeSnackbarWithNoBottomInset(com.schoolonair.wallet.component.resources.R.string.permissionRequiredToDownload, Snackbar.LENGTH_LONG)?.show()
                }
            }
            PERMISSION_REQUEST_GEO_LOCATION -> {
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onSystemLocationPermissionGranted()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                        viewModel.onSystemLocationPermissionDeniedOneTime()
                    } else {
                        viewModel.onSystemLocationPermissionDeniedForever()
                    }
                }
            }
        }
    }

    private fun launchPlayStore(appPackage: String) {
        playStoreUtils.launchPlayStore(appPackage)
    }

    @Suppress("NewApi") // we use appBuildConfig
    private fun launchDefaultBrowser() {
        if (appBuildConfig.sdkInt >= Build.VERSION_CODES.N) {
            requireActivity().launchDefaultAppActivity()
        }
    }

    private fun launchSurvey(survey: Survey) {
        context?.let {
            startActivity(SurveyActivity.intent(it, survey))
        }
    }

    private fun finishTrackerAnimation() {
        animatorHelper.finishTrackerAnimation(omnibarViews(), view?.findViewById(R.id.animationContainer) ?: ConstraintLayout(requireContext()))
    }

    private fun showHideTipsDialog(cta: Cta) {
        context?.let {
            launchHideTipsDialog(it, cta)
        }
    }

    override fun onDaxDialogDismiss() {
        viewModel.onDaxDialogDismissed()
    }

    override fun onDaxDialogHideClick() {
        viewModel.onUserHideDaxDialog()
    }

    override fun onDaxDialogPrimaryCtaClick() {
        viewModel.onUserClickCtaOkButton()
    }

    override fun onDaxDialogSecondaryCtaClick() {
        viewModel.onUserClickCtaSecondaryButton()
    }

    private fun showBackNavigationHistory(history: ShowBackNavigationHistory) {
        activity?.let { context ->
            NavigationHistorySheet(
                context, viewLifecycleOwner, faviconManager, tabId, history,
                object : NavigationHistorySheetListener {
                    override fun historicalPageSelected(stackIndex: Int) {
                        viewModel.historicalPageSelected(stackIndex)
                    }
                }
            ).show()
        }
    }

    private fun navigateBackHistoryStack(index: Int) {
        val stepsToMove = (index + 1) * -1
        webView?.goBackOrForward(stepsToMove)
    }

    fun onLongPressBackButton() {
        /*
         It is possible that this can be invoked before Fragment is attached
         If viewModelFactory isn't initialized, ignore long press
         */
//        if (this::viewModelFactory.isInitialized) {
            viewModel.onUserLongPressedBack()
//        }
    }

    private fun launchHideTipsDialog(
        context: Context,
        cta: Cta
    ) {
        AlertDialog.Builder(context)
            .setTitle(com.schoolonair.wallet.component.resources.R.string.hideTipsTitle)
            .setMessage(getString(com.schoolonair.wallet.component.resources.R.string.hideTipsText))
            .setPositiveButton(com.schoolonair.wallet.component.resources.R.string.hideTipsButton) { dialog, _ ->
                dialog.dismiss()
                launch {
                    ctaViewModel.hideTipsForever(cta)
                }
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun omnibarViews(): List<View> = listOf(view?.findViewById<ImageView>(R.id.clearTextButton) ?: View(requireContext()), view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput) ?: View(requireContext()), view?.findViewById<ImageView>(R.id.searchIcon) ?: View(requireContext()))

    override fun onAnimationFinished() {
        viewModel.stopShowingEmptyGrade()
    }

    private fun showEmailTooltip(address: String) {
        context?.let {
            val isShowing: Boolean? = emailAutofillTooltipDialog?.isShowing
            if (isShowing != true) {
                emailAutofillTooltipDialog = EmailAutofillTooltipFragment(it, address)
                emailAutofillTooltipDialog?.show()
                emailAutofillTooltipDialog?.setOnCancelListener { viewModel.cancelAutofillTooltip() }
                emailAutofillTooltipDialog?.useAddress = { viewModel.useAddress() }
                emailAutofillTooltipDialog?.usePrivateAlias = { viewModel.consumeAlias() }
            }
        }
    }

    companion object {
        private const val TAB_ID_ARG = "TAB_ID_ARG"
        private const val URL_EXTRA_ARG = "URL_EXTRA_ARG"
        private const val SKIP_HOME_ARG = "SKIP_HOME_ARG"
        private const val FAVORITES_ONBOARDING_ARG = "FAVORITES_ONBOARDING_ARG"

        private const val ADD_SAVED_SITE_FRAGMENT_TAG = "ADD_SAVED_SITE"
        private const val KEYBOARD_DELAY = 200L

        private const val REQUEST_CODE_CHOOSE_FILE = 100
        private const val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 200
        private const val PERMISSION_REQUEST_GEO_LOCATION = 300

        private const val URL_BUNDLE_KEY = "url"

        private const val AUTHENTICATION_DIALOG_TAG = "AUTH_DIALOG_TAG"
        private const val DOWNLOAD_CONFIRMATION_TAG = "DOWNLOAD_CONFIRMATION_TAG"
        private const val DAX_DIALOG_DIALOG_TAG = "DAX_DIALOG_TAG"

        private const val MAX_PROGRESS = 100
        private const val TRACKERS_INI_DELAY = 500L
        private const val TRACKERS_SECONDARY_DELAY = 200L

        private const val DEFAULT_CIRCLE_TARGET_TIMES_1_5 = 96

        private const val QUICK_ACCESS_GRID_MAX_COLUMNS = 6

        fun newInstance(
            tabId: String,
            query: String? = null,
            skipHome: Boolean,
            chainId: Long,
            address: String,
            rpcServerUrl: String,
            chainNetworks: String,
            accountId: String? = null,
            prevTabId: String? = null
        ): BrowserTabFragment {
            val fragment = BrowserTabFragment()
            val args = Bundle()
            args.putString(TAB_ID_ARG, tabId)
            args.putBoolean(SKIP_HOME_ARG, skipHome)
            Timber.d("1991 newInstance accountId $accountId chainId $chainId address $address")
            args.putLong(BrowserActivityNavigationUtils.EXTRA_CHAIN_ID, chainId)
            args.putString(BrowserActivityNavigationUtils.EXTRA_ADDRESS, address)
            args.putString(BrowserActivityNavigationUtils.EXTRA_RPC_SERVER_URL, rpcServerUrl)
            args.putString(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK, chainNetworks)
            args.putString(BrowserActivityNavigationUtils.EXTRA_ACCOUNT_ID, accountId)
            args.putString(C.EXTRA_TAB_ID, prevTabId)
            query.let {
                args.putString(URL_EXTRA_ARG, query)
            }
            fragment.arguments = args
            return fragment
        }

        fun newInstanceFavoritesOnboarding(
            tabId: String,
            chainId: Long,
            address: String,
            rpcServerUrl: String,
            chainNetworks: String,
            accountId: String? = null,
        ): BrowserTabFragment {
            val fragment = BrowserTabFragment()
            val args = Bundle()
            args.putString(TAB_ID_ARG, tabId)
            args.putBoolean(FAVORITES_ONBOARDING_ARG, true)
            args.putLong(BrowserActivityNavigationUtils.EXTRA_CHAIN_ID, chainId)
            Timber.d("1991 newInstanceFavoritesOnboarding address $address")
            args.putString(BrowserActivityNavigationUtils.EXTRA_ADDRESS, address)
            args.putString(BrowserActivityNavigationUtils.EXTRA_RPC_SERVER_URL, rpcServerUrl)
            args.putString(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK, chainNetworks)
            args.putString(BrowserActivityNavigationUtils.EXTRA_ACCOUNT_ID, accountId)
            fragment.arguments = args
            return fragment
        }
    }

    inner class BrowserTabFragmentDecorator {

        fun decorateWithFeatures() {
            decorateToolbarWithButtons()
            createPopupMenu()
            configureShowTabSwitcherListener()
            configureLongClickOpensNewTabListener()
            goToWallet()
        }

        fun recreatePopupMenu() {
            popupMenu.dismiss()
            createPopupMenu()
        }

        fun goToWallet(){
//            view?.findViewById<FrameLayout>(R.id.imageHome)?.setOnClickListener {
//                requireActivity().finish()
//            }

            view?.findViewById<FrameLayout>(R.id.browserClose)?.setOnClickListener {
                requireActivity().finish()
            }
        }

        fun updateToolbarActionsVisibility(viewState: BrowserViewState) {
            tabsButton?.isVisible = viewState.showTabsButton
            menuButton?.isVisible = viewState.showMenuButton is HighlightableButton.Visible

            val targetView = if (viewState.showMenuButton.isHighlighted()) {
                view?.findViewById<ImageView>(R.id.browserMenuImageView)
            } else {
                null
            }

            // omnibar only scrollable when browser showing and the fire button is not promoted
            if (targetView != null) {
                omnibarScrolling.disableOmnibarScrolling(view?.findViewById<ConstraintLayout>(R.id.toolbarContainer) ?: View(requireContext()))
                playPulseAnimation(targetView)
            } else {
                if (viewState.browserShowing) {
                    omnibarScrolling.enableOmnibarScrolling(view?.findViewById<ConstraintLayout>(R.id.toolbarContainer) ?: View(requireContext()))
                }
                pulseAnimation.stop()
            }
        }

        private fun playPulseAnimation(targetView: View) {
            view?.findViewById<ConstraintLayout>(R.id.toolbarContainer)?.doOnLayout {
                pulseAnimation.playOn(targetView)
            }
        }

        private fun decorateToolbarWithButtons() {
            tabsButton?.show()
        }

        private fun createPopupMenu() {
            popupMenu = BrowserPopupMenu(
                context = requireContext(),
                layoutInflater = layoutInflater
            )
            val view = popupMenu.contentView
            popupMenu.apply {
                onMenuItemClicked(view.findViewById(R.id.forwardMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_NAVIGATE_FORWARD_PRESSED)
                    viewModel.onUserPressedForward()
                }
                onMenuItemClicked(view.findViewById(R.id.backMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_NAVIGATE_BACK_PRESSED)
                    activity?.onBackPressed()
                }
                onMenuItemLongClicked(view.findViewById(R.id.backMenuItem)) {
                    viewModel.onUserLongPressedBack()
                }
                onMenuItemClicked(view.findViewById(R.id.refreshMenuItem)) {
                    viewModel.onRefreshRequested()
                    pixel.fire(AppPixelName.MENU_ACTION_REFRESH_PRESSED.pixelName)
                }
                onMenuItemClicked(view.findViewById(R.id.newTabMenuItem)) {
                    viewModel.userRequestedOpeningNewTab()
                    pixel.fire(AppPixelName.MENU_ACTION_NEW_TAB_PRESSED.pixelName)
                }
                onMenuItemClicked(view.findViewById(R.id.bookmarksMenuItem)) {
                    browserActivity?.launchBookmarks()
                    pixel.fire(AppPixelName.MENU_ACTION_BOOKMARKS_PRESSED.pixelName)
                }
                onMenuItemClicked(view.findViewById(R.id.fireproofWebsiteMenuItem)) {
                    viewModel.onFireproofWebsiteMenuClicked()
                }
                onMenuItemClicked(view.findViewById(R.id.addBookmarksMenuItem)) {
                    viewModel.onBookmarkMenuClicked()
                }
                onMenuItemClicked(view.findViewById(R.id.addFavoriteMenuItem)) {
                    viewModel.onFavoriteMenuClicked()
                }
                onMenuItemClicked(view.findViewById(R.id.findInPageMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_FIND_IN_PAGE_PRESSED)
                    viewModel.onFindInPageSelected()
                }
                onMenuItemClicked(view.findViewById(R.id.privacyProtectionMenuItem)) { viewModel.onPrivacyProtectionMenuClicked() }
                onMenuItemClicked(view.findViewById(R.id.brokenSiteMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_REPORT_BROKEN_SITE_PRESSED)
                    viewModel.onBrokenSiteSelected()
                }
                onMenuItemClicked(view.findViewById(R.id.downloadsMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_DOWNLOADS_PRESSED)
                    browserActivity?.launchDownloads()
                }
                onMenuItemClicked(view.findViewById(R.id.settingsMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_SETTINGS_PRESSED)
                    browserActivity?.launchSettings()
                }
                onMenuItemClicked(view.findViewById(R.id.changeBrowserModeMenuItem)) {
                    viewModel.onChangeBrowserModeClicked()
                }
                onMenuItemClicked(view.findViewById(R.id.sharePageMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_SHARE_PRESSED)
                    viewModel.onShareSelected()
                }
                onMenuItemClicked(view.findViewById(R.id.addToHomeMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_ADD_TO_HOME_PRESSED)
                    viewModel.onPinPageToHomeSelected()
                }
                onMenuItemClicked(view.findViewById(R.id.createAliasMenuItem)) { viewModel.consumeAliasAndCopyToClipboard() }
                onMenuItemClicked(view.findViewById(R.id.openInAppMenuItem)) {
                    pixel.fire(AppPixelName.MENU_ACTION_APP_LINKS_OPEN_PRESSED)
                    viewModel.openAppLink()
                }
                onMenuItemClicked(view.findViewById(R.id.printPageMenuItem)) {
                    viewModel.onPrintSelected()
                }
            }
            view.findViewById<ScrollView>(R.id.menuScrollableContent).setOnScrollChangeListener { _, _, _, _, _ ->
                view.findViewById<View>(R.id.dividerShadow).isVisible = view.findViewById<ScrollView>(R.id.menuScrollableContent).canScrollVertically(-1)
            }
            binding.root.findViewById<FrameLayout>(R.id.browserMenu).setOnClickListener {
                viewModel.onBrowserMenuClicked()
                hideKeyboardImmediately()
                launchTopAnchoredPopupMenu()
            }
        }

        private fun launchTopAnchoredPopupMenu() {
            popupMenu.show(binding.rootView, view?.findViewById<Toolbar>(R.id.toolbar) ?: View(requireContext())) {
                viewModel.onBrowserMenuClosed()
            }
            pixel.fire(AppPixelName.MENU_ACTION_POPUP_OPENED.pixelName)
        }

        private fun configureShowTabSwitcherListener() {
            tabsButton?.setOnClickListener {
                launch { viewModel.userLaunchingTabSwitcher() }
            }
        }

        private fun configureLongClickOpensNewTabListener() {
            tabsButton?.setOnLongClickListener {
                launch { viewModel.userRequestedOpeningNewTab() }
                return@setOnLongClickListener true
            }
        }

        fun animateTabsCount() {
            tabsButton?.animateCount()
        }

        fun renderTabIcon(tabs: List<TabEntity>) {
            context?.let {
                tabsButton?.count = tabs.count()
                tabsButton?.hasUnread = tabs.firstOrNull { !it.viewed } != null
            }
        }

        fun incrementTabs() {
            tabsButton?.increment {
                addTabsObserver()
            }
        }
    }

    inner class BrowserTabFragmentRenderer {

        private var lastSeenOmnibarViewState: OmnibarViewState? = null
        private var lastSeenLoadingViewState: LoadingViewState? = null
        private var lastSeenFindInPageViewState: FindInPageViewState? = null
        private var lastSeenBrowserViewState: BrowserViewState? = null
        private var lastSeenGlobalViewState: GlobalLayoutViewState? = null
        private var lastSeenAutoCompleteViewState: AutoCompleteViewState? = null
        private var lastSeenCtaViewState: CtaViewState? = null
        private var lastSeenPrivacyGradeViewState: PrivacyGradeViewState? = null

        fun renderPrivacyGrade(viewState: PrivacyGradeViewState) {

            renderIfChanged(viewState, lastSeenPrivacyGradeViewState) {

                val oldGrade = lastSeenPrivacyGradeViewState?.privacyGrade
                val oldShowEmptyGrade = lastSeenPrivacyGradeViewState?.showEmptyGrade
                val grade = viewState.privacyGrade
                val newShowEmptyGrade = viewState.showEmptyGrade

                val canChangeGrade = (oldGrade != grade && !newShowEmptyGrade) || (oldGrade == grade && oldShowEmptyGrade != newShowEmptyGrade)
                lastSeenPrivacyGradeViewState = viewState

                if (canChangeGrade) {
                    context?.let {
                        val drawable = if (viewState.showEmptyGrade || viewState.shouldAnimate) {
                            ContextCompat.getDrawable(it, com.schoolonair.wallet.component.resources.R.drawable.privacygrade_icon_loading)
                        } else {
                            ContextCompat.getDrawable(it, viewState.privacyGrade.icon())
                        }
                        view?.findViewById<ImageButton>(R.id.privacyGradeButton)?.setImageDrawable(drawable)
                    }
                }

                view?.findViewById<ImageButton>(R.id.privacyGradeButton)?.isEnabled = viewState.isEnabled

                if (viewState.shouldAnimate) {
                    animatorHelper.startPulseAnimation(view?.findViewById<ImageButton>(R.id.privacyGradeButton) ?: ImageButton(requireContext()))
                } else {
                    animatorHelper.stopPulseAnimation()
                }
            }
        }

        fun renderAutocomplete(viewState: AutoCompleteViewState) {
            renderIfChanged(viewState, lastSeenAutoCompleteViewState) {
                lastSeenAutoCompleteViewState = viewState

                if (viewState.showSuggestions || viewState.showFavorites) {
                    if (viewState.favorites.isNotEmpty() && viewState.showFavorites) {
                        binding.autoCompleteSuggestionsList.gone()
                        binding.quickAccessSuggestionsRecyclerView.show()
                        omnibarQuickAccessAdapter.submitList(viewState.favorites)
                    } else {
                        binding.autoCompleteSuggestionsList.show()
                        binding.quickAccessSuggestionsRecyclerView.gone()
                        autoCompleteSuggestionsAdapter.updateData(viewState.searchResults.query, viewState.searchResults.suggestions)
                    }
                } else {
                    binding.autoCompleteSuggestionsList.gone()
                    binding.quickAccessSuggestionsRecyclerView.gone()
                }
            }
        }

        fun renderOmnibar(viewState: OmnibarViewState) {
            renderIfChanged(viewState, lastSeenOmnibarViewState) {
                lastSeenOmnibarViewState = viewState

                if (viewState.isEditing) {
                    cancelTrackersAnimation()
                }

                if (shouldUpdateOmnibarTextInput(viewState, viewState.omnibarText)) {
                    view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.setText(viewState.omnibarText)
                    view?.findViewById<AppBarLayout>(R.id.appBarLayout)?.setExpanded(true, true)
                    if (viewState.shouldMoveCaretToEnd) {
                        view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.setSelection(viewState.omnibarText.length)
                    }
                }

                lastSeenBrowserViewState?.let {
                    renderToolbarMenus(it)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun renderLoadingIndicator(viewState: LoadingViewState) {
            renderIfChanged(viewState, lastSeenLoadingViewState) {
                lastSeenLoadingViewState = viewState

                view?.findViewById<ProgressBar>(R.id.pageLoadingIndicator)?.apply {
                    if (viewState.isLoading) show()
                    smoothProgressAnimator.onNewProgress(viewState.progress) { if (!viewState.isLoading) hide() }
                }

                if (viewState.privacyOn) {
                    if (lastSeenOmnibarViewState?.isEditing == true) {
                        cancelTrackersAnimation()
                    }

                    if (viewState.progress == MAX_PROGRESS) {
                        createTrackersAnimation()
                    }
                }

                if (!viewState.isLoading && lastSeenBrowserViewState?.browserShowing == true) {
                    binding.swipeRefreshContainer.isRefreshing = false
                }
            }
        }

        private fun createTrackersAnimation() {
            launch {
                delay(TRACKERS_INI_DELAY)
                viewModel.refreshCta()
                delay(TRACKERS_SECONDARY_DELAY)
                if (lastSeenOmnibarViewState?.isEditing != true) {
                    val site = viewModel.siteLiveData.value
                    val events = site?.orderedTrackingEntities()

                    activity?.let { activity ->
                        animatorHelper.startTrackersAnimation(lastSeenCtaViewState?.cta, activity, view?.findViewById<ConstraintLayout>(R.id.animationContainer) ?: ConstraintLayout(requireContext()), omnibarViews(), events)
                    }
                }
            }
        }

        fun cancelTrackersAnimation() {
            animatorHelper.cancelAnimations(omnibarViews(), view?.findViewById<ConstraintLayout>(R.id.animationContainer) ?: ConstraintLayout(requireContext()))
        }

        fun renderGlobalViewState(viewState: GlobalLayoutViewState) {
            if (lastSeenGlobalViewState is GlobalLayoutViewState.Invalidated &&
                viewState is GlobalLayoutViewState.Browser
            ) {
                throw IllegalStateException("Invalid state transition")
            }

            renderIfChanged(viewState, lastSeenGlobalViewState) {
                lastSeenGlobalViewState = viewState

                when (viewState) {
                    is GlobalLayoutViewState.Browser -> {
                        if (viewState.isNewTabState) {
                            binding.browserLayout.hide()
                        } else {
                            binding.browserLayout.show()
                        }
                    }
                    is GlobalLayoutViewState.Invalidated -> destroyWebView()
                }
            }
        }

        fun renderBrowserViewState(viewState: BrowserViewState) {
            renderIfChanged(viewState, lastSeenBrowserViewState) {
                val browserShowing = viewState.browserShowing

                val browserShowingChanged = viewState.browserShowing != lastSeenBrowserViewState?.browserShowing
                lastSeenBrowserViewState = viewState
                if (browserShowingChanged) {
                    if (browserShowing) {
                        showBrowser()
                    } else {
                        showHome()
                    }
                }

                renderToolbarMenus(viewState)
                popupMenu.renderState(browserShowing, viewState)
                handleFavorite(viewState)
                renderFullscreenMode(viewState)
            }
        }

        private fun renderFullscreenMode(viewState: BrowserViewState) {
            activity?.isImmersiveModeEnabled()?.let {
                if (viewState.isFullScreen) {
                    if (!it) goFullScreen()
                } else {
                    if (it) exitFullScreen()
                }
            }
        }

        fun applyAccessibilitySettings(viewState: AccessibilityViewState) {
            Timber.v("Accessibility: render state applyAccessibilitySettings $viewState")
            val webView = webView ?: return

            val fontSizeChanged = webView.settings.textZoom != viewState.fontSize.toInt()
            if (fontSizeChanged) {
                Timber.v(
                    "Accessibility: UpdateAccessibilitySetting fontSizeChanged " +
                            "from ${webView.settings.textZoom} to ${viewState.fontSize.toInt()}"
                )

                webView.settings.textZoom = viewState.fontSize.toInt()
            }

            if (this@BrowserTabFragment.isHidden && viewState.refreshWebView) return
            if (viewState.refreshWebView) {
                Timber.v("Accessibility: UpdateAccessibilitySetting forceZoomChanged")
                refresh()
            }
        }

        private fun renderToolbarMenus(viewState: BrowserViewState) {
            if (viewState.browserShowing) {
                view?.findViewById<ImageView>(R.id.daxIcon)?.isVisible = viewState.showDaxIcon
                view?.findViewById<ImageButton>(R.id.privacyGradeButton)?.isInvisible = !viewState.showPrivacyGrade || viewState.showDaxIcon
                view?.findViewById<ImageView>(R.id.clearTextButton)?.isVisible = viewState.showClearButton
                view?.findViewById<ImageView>(R.id.searchIcon)?.isVisible = viewState.showSearchIcon
            } else {
                view?.findViewById<ImageView>(R.id.daxIcon)?.isVisible = false
                view?.findViewById<ImageButton>(R.id.privacyGradeButton)?.isVisible = false
                view?.findViewById<ImageView>(R.id.clearTextButton)?.isVisible = viewState.showClearButton
                view?.findViewById<ImageView>(R.id.searchIcon)?.isVisible = true
            }

            decorator.updateToolbarActionsVisibility(viewState)
        }

        fun renderFindInPageState(viewState: FindInPageViewState) {
            if (viewState == lastSeenFindInPageViewState) {
                return
            }

            lastSeenFindInPageViewState = viewState

            if (viewState.visible) {
                showFindInPageView(viewState)
            } else {
                hideFindInPage()
            }
        }

        fun renderCtaViewState(viewState: CtaViewState) {
            if (isHidden) {
                return
            }

            renderIfChanged(viewState, lastSeenCtaViewState) {
                val newMessage = (viewState.message?.id != lastSeenCtaViewState?.message?.id)
                lastSeenCtaViewState = viewState
                removeNewTabLayoutClickListener()
                Timber.v("RMF: render $newMessage, $viewState")
                when {
                    viewState.cta != null -> {
                        showCta(viewState.cta, viewState.favorites)
                    }
                    viewState.message != null -> {
                        showRemoteMessage(viewState.message, newMessage)
                        showHomeBackground(viewState.favorites, hideLogo = true)
                        hideHomeCta()
                    }
                    else -> {
                        hideHomeCta()
                        hideDaxCta()
                        view?.findViewById<MessageCta>(R.id.messageCta)?.gone()
                        showHomeBackground(viewState.favorites)
                    }
                }
            }
        }

        private fun showRemoteMessage(
            message: RemoteMessage,
            newMessage: Boolean
        ) {
            val shouldRender = newMessage || view?.findViewById<MessageCta>(R.id.messageCta)?.isGone == true

            if (shouldRender) {
                Timber.i("RMF: render $message")
                view?.findViewById<MessageCta>(R.id.messageCta)?.show()
                viewModel.onMessageShown()
                view?.findViewById<MessageCta>(R.id.messageCta)?.setMessage(message.asMessage())
                view?.findViewById<MessageCta>(R.id.messageCta)?.onCloseButtonClicked {
                    viewModel.onMessageCloseButtonClicked()
                }
                view?.findViewById<MessageCta>(R.id.messageCta)?.onPrimaryActionClicked {
                    viewModel.onMessagePrimaryButtonClicked()
                }
                view?.findViewById<MessageCta>(R.id.messageCta)?.onSecondaryActionClicked {
                    viewModel.onMessageSecondaryButtonClicked()
                }
            }
        }

        private fun showCta(
            configuration: Cta,
            favorites: List<QuickAccessFavorite>
        ) {
            when (configuration) {
                is HomePanelCta -> showHomeCta(configuration, favorites)
                is DaxBubbleCta -> showDaxCta(configuration)
                is BubbleCta -> showBubbleCta(configuration)
                is DialogCta -> showDaxDialogCta(configuration)
            }
            view?.findViewById<MessageCta>(R.id.messageCta)?.gone()
        }

        private fun showDaxDialogCta(configuration: DialogCta) {
            hideHomeCta()
            hideDaxCta()
            activity?.let { activity ->
                val daxDialog = getDaxDialogFromActivity() as? DaxDialog
                if (daxDialog != null) {
                    daxDialog.setDaxDialogListener(this@BrowserTabFragment)
                    return
                }
                configuration.createCta(activity).apply {
                    setDaxDialogListener(this@BrowserTabFragment)
                    getDaxDialog().show(activity.supportFragmentManager, DAX_DIALOG_DIALOG_TAG)
                }
                viewModel.onCtaShown()
            }
        }

        private fun showDaxCta(configuration: DaxBubbleCta) {
            hideHomeBackground()
            hideHomeCta()
            configuration.showCta(view?.findViewById<ConstraintLayout>(com.schoolonair.wallet.browser.app.R.id.daxCtaContainer) ?: View(requireContext()))
            view?.findViewById<ScrollView>(R.id.newTabLayout)?.setOnClickListener { view?.findViewById<TypeAnimationTextView>(
                com.schoolonair.wallet.browser.app.R.id.dialogTextCta)?.finishAnimation() }

            viewModel.onCtaShown()
        }

        private fun showBubbleCta(configuration: BubbleCta) {
            hideHomeBackground()
            hideHomeCta()
            configuration.showCta(view?.findViewById<ConstraintLayout>(com.schoolonair.wallet.browser.app.R.id.daxCtaContainer) ?: View(requireContext()))
            view?.findViewById<ScrollView>(R.id.newTabLayout)?.setOnClickListener { view?.findViewById<TypeAnimationTextView>(com.schoolonair.wallet.browser.app.R.id.dialogTextCta)?.finishAnimation() }
            viewModel.onCtaShown()
        }

        private fun removeNewTabLayoutClickListener() {
            view?.findViewById<ScrollView>(R.id.newTabLayout)?.setOnClickListener(null)
        }

        private fun showHomeCta(
            configuration: HomePanelCta,
            favorites: List<QuickAccessFavorite>
        ) {
            hideDaxCta()
            if (view?.findViewById<LinearLayout>(R.id.ctaContainer)?.isEmpty() == true) {
                renderHomeCta()
            } else {
                configuration.showCta(view?.findViewById<LinearLayout>(R.id.ctaContainer) ?: LinearLayout(requireContext()))
            }
            showHomeBackground(favorites)
            viewModel.onCtaShown()
        }

        private fun showHomeBackground(
            favorites: List<QuickAccessFavorite>,
            hideLogo: Boolean = false
        ) {
            if (favorites.isEmpty()) {
                if (hideLogo) homeBackgroundLogo.hideLogo() else homeBackgroundLogo.showLogo()
                view?.findViewById<RecyclerView>(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView)?.gone()
            } else {
                homeBackgroundLogo.hideLogo()
                quickAccessAdapter.submitList(favorites)
                view?.findViewById<RecyclerView>(com.schoolonair.wallet.browser.app.R.id.quickAccessRecyclerView)?.show()
            }

            view?.findViewById<LinearLayout>(R.id.newTabQuickAccessItemsLayout)?.show()
        }

        private fun hideHomeBackground() {
            homeBackgroundLogo.hideLogo()
            view?.findViewById<LinearLayout>(R.id.newTabQuickAccessItemsLayout)?.gone()
        }

        private fun hideDaxCta() {
            view?.findViewById<TypeAnimationTextView>(com.schoolonair.wallet.browser.app.R.id.dialogTextCta)?.cancelAnimation()
            view?.findViewById<ConstraintLayout>(com.schoolonair.wallet.browser.app.R.id.daxCtaContainer)?.hide()
        }

        private fun hideHomeCta() {
            view?.findViewById<LinearLayout>(R.id.ctaContainer)?.gone()
        }

        fun renderHomeCta() {
            val context = context ?: return
            val cta = lastSeenCtaViewState?.cta ?: return
            val configuration = if (cta is HomePanelCta) cta else return

            view?.findViewById<LinearLayout>(R.id.ctaContainer)?.removeAllViews()

            inflate(context, R.layout.include_cta, view?.findViewById<LinearLayout>(R.id.ctaContainer))

            configuration.showCta(view?.findViewById<LinearLayout>(R.id.ctaContainer) ?: LinearLayout(requireContext()))
            view?.findViewById<View>(com.schoolonair.wallet.browser.app.R.id.ctaOkButton)?.setOnClickListener {
                viewModel.onUserClickCtaOkButton()
            }

            view?.findViewById<View>(com.schoolonair.wallet.browser.app.R.id.ctaDismissButton)?.setOnClickListener {
                viewModel.onUserDismissedCta()
            }
        }

        fun hideFindInPage() {
            if (view?.findViewById<LinearLayout>(R.id.findInPageContainer)?.visibility != GONE) {
                binding.focusDummy.requestFocus()
                view?.findViewById<LinearLayout>(R.id.findInPageContainer)?.gone()
                view?.findViewById<EditText>(R.id.findInPageInput)?.hideKeyboard()
            }
        }

        private fun showFindInPageView(viewState: FindInPageViewState) {

            if (view?.findViewById<LinearLayout>(R.id.findInPageContainer)?.visibility != VISIBLE) {
                view?.findViewById<LinearLayout>(R.id.findInPageContainer)?.show()
                view?.findViewById<EditText>(R.id.findInPageInput)?.postDelayed(KEYBOARD_DELAY) {
                    view?.findViewById<EditText>(R.id.findInPageInput)?.showKeyboard()
                }
            }

            if (viewState.showNumberMatches) {
                view?.findViewById<TextView>(R.id.findInPageMatches)?.text = getString(com.schoolonair.wallet.component.resources.R.string.findInPageMatches, viewState.activeMatchIndex, viewState.numberMatches)
                view?.findViewById<TextView>(R.id.findInPageMatches)?.show()
            } else {
                view?.findViewById<TextView>(R.id.findInPageMatches)?.hide()
            }
        }

        private fun goFullScreen() {
            Timber.i("Entering full screen")
            binding.webViewFullScreenContainer.show()
            activity?.toggleFullScreen()
        }

        private fun exitFullScreen() {
            Timber.i("Exiting full screen")
            binding.webViewFullScreenContainer.removeAllViews()
            binding.webViewFullScreenContainer.gone()
            activity?.toggleFullScreen()
            binding.focusDummy.requestFocus()
        }

        private fun shouldUpdateOmnibarTextInput(
            viewState: OmnibarViewState,
            omnibarInput: String?
        ) =
            (!viewState.isEditing || omnibarInput.isNullOrEmpty()) && view?.findViewById<KeyboardAwareEditText>(R.id.omnibarTextInput)?.isDifferent(omnibarInput) == true
    }

    private fun showDownloadManagerAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = DownloadFailReason.DOWNLOAD_MANAGER_SETTINGS_URI
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.w(e, "Could not open DownloadManager settings")
            view?.findViewById<Toolbar>(R.id.toolbar)?.makeSnackbarWithNoBottomInset(com.schoolonair.wallet.component.resources.R.string.downloadManagerIncompatible, Snackbar.LENGTH_INDEFINITE)?.show()
        }
    }

    private fun launchPrint(url: String) {
        (activity?.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->
            webView?.createPrintDocumentAdapter(url)?.let { printAdapter ->
                printManager.print(
                    url,
                    printAdapter,
                    PrintAttributes.Builder().build()
                )
            }
        }
    }

    override fun continueDownload(pendingFileDownload: PendingFileDownload) {
        Timber.i("Continuing to download %s", pendingFileDownload)
        viewModel.download(pendingFileDownload)
    }

    override fun cancelDownload() {
        viewModel.closeAndReturnToSourceIfBlankTab()
    }

    fun onFireDialogVisibilityChanged(isVisible: Boolean) {
        if (isVisible) {
            viewModel.ctaViewState.removeObserver(ctaViewStateObserver)
        } else {
            viewModel.ctaViewState.observe(viewLifecycleOwner, ctaViewStateObserver)
        }
    }

    override fun onSiteLocationPermissionSelected(
        domain: String,
        permission: LocationPermissionType
    ) {
        viewModel.onSiteLocationPermissionSelected(domain, permission)
    }

    override fun onSystemLocationPermissionAllowed() {
        viewModel.onSystemLocationPermissionAllowed()
    }

    override fun onSystemLocationPermissionNotAllowed() {
        viewModel.onSystemLocationPermissionNotAllowed()
    }

    override fun onSystemLocationPermissionNeverAllowed() {
        viewModel.onSystemLocationPermissionNeverAllowed()
    }

    /*----------------------WEB LISTENER -----------------------*/

    override fun onEthCall(call: Web3Call?) {
//        Timber.d("1991 onEthCall " + call?.payload)

//        Single.fromCallable {
//
//            //let's make the call
//            val web3j: Web3j = TokenRepository.getWeb3jService(chainId)
//            //construct call
//            val transaction: org.web3j.protocol.core.methods.request.Transaction =
//                createFunctionCallTransaction(
//                    addressConnect,
//                    null,
//                    null,
//                    call?.gasLimit,
//                    call?.to.toString(),
//                    call?.value,
//                    call?.payload
//                )
//            web3j.ethCall(transaction, call?.blockParam).send()
//        }.map { obj: EthCall -> obj.getValue() }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ result -> webView?.onCallFunctionSuccessful(call?.leafPosition ?: 0L, result) }
//            ) { error -> webView?.onCallFunctionError(call?.leafPosition ?: 0L, error.message ?: "") }
//            .isDisposed
    }

    override fun onSignMessage(message: EthereumMessage?) {
        Timber.d("1991 onSignMessage " + message?.message)
        handleSignMessage(message)
    }

    override fun onSignPersonalMessage(message: EthereumMessage?) {
        Timber.d("1991 onSignPersonalMessage " + message?.message)
        handleSignMessage(message)
    }

    private var walletConnect: Wallet? = null

    private val actionTransactionCallback = object : ActionTransactionCallback {

        override fun dismissed(callbackId: Long, actionCompleted: Boolean) {
            webView?.onSignCancel(callbackId)
        }

        override fun notifyConfirm(mode: String?) {
        }

//        override fun buttonClick(
//            callbackId: Long,
//            baseToken: com.mangala.wallet.model.token.Token?
//        ) {
//        }
        override fun notifyWalletConnectApproval(chainId: Long) {
        }

        override fun denyWalletConnect() {
        }

        override fun openChainSelection() {
        }

        override fun transactionSuccess(
            callbackId: Long,
            value: String,
            recipient: String,
            payload: String,
            nonce: Long,
            isLegacyTransaction: Boolean,
            hashData: String
        ) {
            webView?.onSignTransactionSuccessful(callbackId, hashData)
        }

        override fun transactionError(callbackId: Long, error: Throwable?) {
            Timber.d("1991 transactionError " + callbackId)
            webView?.onSignCancel(callbackId)
        }

        override fun onSignMessageSuccessful(callbackId: Long, signHex: String) {
            webView?.onSignMessageSuccessful(callbackId, signHex)
        }

        override fun onSwitchChainSuccessful(callbackId: Long, newChainId: Long) {
            switchNetwork(newChainId)
        }

    }

    override fun onSignTransaction(
        transaction: Web3Transaction?,
        url: String?
    ) {
        Timber.d("1991 onSignTransaction " + url)
        Timber.d("1991 onSignTransaction payload " + transaction?.payload )
        Timber.d("1991 onSignTransaction recipient " + transaction?.recipient?.toString() )
        Timber.d("1991 onSignTransaction description " + transaction?.description )
        Timber.d("1991 onSignTransaction contract " + transaction?.contract?.toString() )
        Timber.d("1991 onSignTransaction gasLimit " + transaction?.gasLimit )
        Timber.d("1991 onSignTransaction gasPrice " + transaction?.gasPrice )
        Timber.d("1991 onSignTransaction nonce " + transaction?.nonce )
        Timber.d("1991 onSignTransaction value " + transaction?.value )

        if(transaction != null && transaction.recipient.equals(Address.EMPTY) && transaction.payload != null || !transaction!!.recipient.equals(Address.EMPTY) && (transaction!!.payload != null || transaction.value != null)){
            val balance = BalanceUtils.convertBalanceForGas(balance, coinDecimals.toLong())
//            val dappUrl = java.net.URL(url ?: "")
            val dialog = confirmTransactionCallback.showDialogFragment(
                childFragmentManager,
                url ?: "",
                accountId,
                coinDecimals.toLong(),
                chainId,
                transaction!!.leafPosition,
                transaction.value.toString(),
                transaction.recipient.toString(),
                transaction.payload,
                transaction.nonce,
                transaction.isLegacyTransaction,
                transaction.gasLimit?.toLong() ?: 0L,
                actionTransactionCallback
            )
        } else {
            transaction.let {
                onInvalidTransaction(transaction)
                webView?.onSignCancel(transaction.leafPosition)
            }
        }
//        try {
//            //minimum for transaction to be valid: recipient and value or payload
//            if ((confirmationDialog == null || !confirmationDialog!!.isShowing) && transaction!!.recipient.equals(Address.EMPTY) && transaction.payload != null || !transaction!!.recipient.equals(Address.EMPTY) && (transaction!!.payload != null || transaction.value != null)
//            ) // Raw or Function TX
//            {
//                val token: Token = viewModel.getTokenService()
//                    .getTokenOrBase(chainId, transaction.recipient.toString())
//                token.balance = BalanceUtils.convertBalanceForGas(balance, coinDecimals.toLong())
////                token.pendingBalance = balance
//                token.tokenInfo.decimals = coinDecimals
//                Timber.d("1991 onSignTransaction balance " + token.balance)
//                confirmationDialog = ActionSheetDialog(
//                    requireActivity(), transaction, token,
//                    "", transaction.recipient.toString(), viewModel.getTokenService(), actionSheetCallBack
//                )
//                Timber.d("1991 onSignTransaction token " + token.fullName)
//                confirmationDialog?.setURL(url ?: "")
//                confirmationDialog?.setCanceledOnTouchOutside(false)
//                confirmationDialog?.show()
////                confirmationDialog?.fullExpand()
////                viewModel.calculateGasEstimate(
////                    walletConnect,
////                    Numeric.hexStringToByteArray(
////                        transaction.payload
////                    ),
////                    chainId,
////                    transaction.recipient.toString(),
////                    BigDecimal(transaction.value),
////                    transaction.gasLimit
////                )
////                    ?.subscribeOn(Schedulers.io())
////                    ?.observeOn(AndroidSchedulers.mainThread())
////                    ?.subscribe { estimate -> confirmationDialog!!.setGasEstimate(estimate) }
////                    ?.isDisposed
//                return
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        transaction?.let {
//            onInvalidTransaction(transaction)
//            webView?.onSignCancel(transaction!!.leafPosition)
//        }
    }

    override fun onSignTypedMessage(message: EthereumTypedMessage?) {
        Timber.d("1991 onSignTypedMessage " + message?.message)
        if(message?.prehash == null || message?.messageType == SignMessageType.SIGN_ERROR){
            webView?.onSignCancel(message?.callbackId ?: 0L)
        }else{
            handleSignMessage(message)
        }
    }

    override fun onRequestAccounts(callbackId: Long) {
        Timber.d("1991 onRequestAccounts " + callbackId)
        webView?.onWalletActionSuccessful(callbackId, "[\"$addressConnect\"]")
    }

    override fun onWalletSwitchEthereumChain(
        callbackId: Long,
        chainObj: WalletAddEthereumChainObject?
    ) {
        Timber.d("1991 onWalletSwitchEthereumChain callbackId " + callbackId)

        val chainId = chainObj?.getChainId() ?: 0L
        val info: NetworkInfo? = viewModel.getNetworkInfo(chainId)
        if (info == null) {
            chainSwapDialog = AWalletAlertDialog(requireContext())
            chainSwapDialog?.setTitle(com.alphawallet.app.R.string.unknown_network_title)
            chainSwapDialog?.setMessage(getString(com.alphawallet.app.R.string.unknown_network, chainId.toString()))
            chainSwapDialog?.setButton(
                com.alphawallet.app.R.string.dialog_ok,
                OnClickListener { v: View? -> if (chainSwapDialog?.isShowing() == true) chainSwapDialog?.dismiss() })
            chainSwapDialog?.setSecondaryButton(com.alphawallet.app.R.string.action_cancel,
                OnClickListener { v: View? -> chainSwapDialog?.dismiss() })
            chainSwapDialog?.setCancelable(false)
            chainSwapDialog?.show()
        } else {
//            changeChainRequest(callbackId, info)
            switchChainCallback.showDialogFragment(childFragmentManager, this.chainId, chainId, callbackId, actionTransactionCallback)
        }

    }

    /**
     * This will pop the ActionSheetDialog to request a chain change, with appropriate warning
     * if switching between mainnets and testnets
     *
     * @param callbackId
     * @param newNetwork
     */
    private fun showChainChangeDialog(callbackId: Long, newNetwork: NetworkInfo) {
        val baseToken: Token = viewModel.getTokenService().getTokenOrBase(newNetwork.chainId, addressConnect)
        confirmationDialog = ActionSheetDialog(
            requireActivity(), actionSheetCallBack, com.alphawallet.app.R.string.switch_chain_request, com.alphawallet.app.R.string.switch_and_reload,
            callbackId, baseToken, activeNetwork, newNetwork
        )
        confirmationDialog?.setCanceledOnTouchOutside(true)
        confirmationDialog?.show()
        confirmationDialog?.fullExpand()
    }

    private var addCustomChainDialog: AddEthereumChainPrompt? = null
    override fun onWalletAddEthereumChainObject(
        callbackId: Long,
        chainObject: WalletAddEthereumChainObject?
    ) {
        Timber.d("1991 onWalletAddEthereumChainObject " + chainObject?.chainName)
        val chainId = chainObject?.getChainId()
        Timber.d("1991 onWalletAddEthereumChainObject chainId " + chainId)

        val info: NetworkInfo? = viewModel.getNetworkInfo(chainId ?: 1L)
//
//        if (forceChainChange !== 0 || context == null) {
//            return  //No action if chain change is forced
//        }

        // handle unknown network

        // handle unknown network
        if (info == null) {
            Timber.d("1991 onWalletAddEthereumChainObject null")
            // show add custom chain dialog
            addCustomChainDialog = AddEthereumChainPrompt(
                requireContext(),
                chainObject
            ) { chainObject: WalletAddEthereumChainObject? ->
                chainObject?.let {
                    viewModel.addCustomChain(chainObject)
                }
//                loadNewNetwork(chainObject?.getChainId() ?: 1L)
                addCustomChainDialog?.dismiss()
            }
            addCustomChainDialog?.show()
        } else {
//            changeChainRequest(callbackId, info)
            switchChainCallback.showDialogFragment(childFragmentManager, this.chainId, chainId ?: 1L, callbackId, actionTransactionCallback)
        }
    }

    /*----------------------WEB LISTENER FUNC-----------------------*/

    private var dAppFunction: DAppFunction? = null
    private var confirmationDialog: ActionSheetDialog? = null
    private fun handleSignMessage(message: Signable?){
//        dAppFunction = object : DAppFunction{
//            override fun DAppError(error: Throwable?, message: Signable?) {
//                webView?.onSignCancel(message?.callbackId ?: 0L)
//            }
//
//            override fun DAppReturn(data: ByteArray, message: Signable) {
//                val signHex = Numeric.toHexString(data)
//                Timber.d("Initial Msg: " + message?.message)
//                webView?.onSignMessageSuccessful(message.callbackId, signHex)
//            }
//        }

//        if (confirmationDialog == null || !confirmationDialog!!.isShowing) {
//            confirmationDialog = ActionSheetDialog(requireActivity(), actionSheetCallBack, signAuthenticationCallback, message)
//            confirmationDialog?.setCanceledOnTouchOutside(false)
//            confirmationDialog?.show()
//            confirmationDialog?.fullExpand()
//        }
//
        val dappUrl = java.net.URL(webView?.url ?: "")

        signPersonalMessageCallback.showDialogFragment(childFragmentManager, dappUrl.host ?: "", message?.callbackId ?: 0L, message?.prehash, actionTransactionCallback)
    }

    var getGasSettings = registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        confirmationDialog!!.setCurrentGasIndex(
            result
        )
    }

    private val actionSheetCallBack = object : ActionSheetCallback {
        override fun getAuthorisation(callback: SignAuthenticationCallback?) {
            Timber.d("1991 getAuthorisation1  ")
            viewModel.getAuthorisation(walletConnect, requireActivity(), callback)
        }

        override fun sendTransaction(tx: Web3Transaction?) {
            Timber.d("1991 sendTransaction " + tx?.payload)
            val callback: SendTransactionInterface = object : SendTransactionInterface {
                override fun transactionSuccess(web3Tx: Web3Transaction, hashData: String) {
                    confirmationDialog?.transactionWritten(hashData)
                    webView?.onSignTransactionSuccessful(web3Tx.leafPosition, hashData)
                }

                override fun transactionError(callbackId: Long, error: Throwable) {
                    confirmationDialog!!.dismiss()
                    txError(error)
                    webView?.onSignCancel(callbackId)
                }
            }

            if(tx != null) {
                viewModel.sendTransaction(tx!!, chainId, callback)
            }
        }

        override fun dismissed(txHash: String?, callbackId: Long, actionCompleted: Boolean) {
            Timber.d("1991 dismissed " + txHash?.toString())
            if(!actionCompleted){
                webView?.onSignCancel(callbackId)
            }
        }

        override fun notifyConfirm(mode: String?) {
            Timber.d("1991 notifyConfirm " + mode)
//            Toast.makeText(requireContext(), "Use Action Sheet $mode", Toast.LENGTH_SHORT).show()
        }

        override fun gasSelectLauncher(): ActivityResultLauncher<Intent> {
            return getGasSettings
        }

        override fun buttonClick(callbackId: Long, baseToken: Token?) {
            //handle button click
            if (confirmationDialog != null && confirmationDialog!!.isShowing)
            {
                confirmationDialog?.dismiss()
            }

            //switch network
            Timber.d("1991 buttonClick chainId " + baseToken?.tokenInfo?.chainId)
//            loadNewNetwork(baseToken?.tokenInfo?.chainId ?: 1L)
            webView?.onWalletActionSuccessful(callbackId, null)
        }

    }

    private fun txError(throwable: Throwable) {
        if (resultDialog != null && resultDialog!!.isShowing) resultDialog!!.dismiss()
        resultDialog = AWalletAlertDialog(requireContext())
        resultDialog?.setIcon(com.schoolonair.wallet.component.resources.R.drawable.ic_error)
        resultDialog?.setTitle(com.alphawallet.app.R.string.error_transaction_failed)
        resultDialog?.setMessage(throwable.message)
        resultDialog?.setButtonText(com.alphawallet.app.R.string.button_ok)
        resultDialog?.setButtonListener { v: View? -> resultDialog!!.dismiss() }
        resultDialog?.show()
        if (confirmationDialog != null && confirmationDialog!!.isShowing) confirmationDialog!!.dismiss()
    }

    private val signAuthenticationCallback = object : SignAuthenticationCallback{
        override fun gotAuthorisation(gotAuth: Boolean) {
            Timber.d("1991 gotAuthorisation 2 " + gotAuth)
            if (confirmationDialog != null && confirmationDialog!!.isShowing())
            {
                confirmationDialog?.dismiss()
            }
        }

        override fun cancelAuthentication() {
            Timber.d("1991 cancelAuthentication  ")
            Toast.makeText(requireContext(), "cancelAuthentication", Toast.LENGTH_SHORT).show()
        }

    }
    private var chainSwapDialog: AWalletAlertDialog? = null
    private var resultDialog: AWalletAlertDialog? = null
    private fun onInvalidTransaction(transaction: Web3Transaction) {
        if (!isAdded) return
        resultDialog = AWalletAlertDialog(requireContext())
        resultDialog?.setIcon(AWalletAlertDialog.ERROR)
        resultDialog?.setTitle(getString(com.alphawallet.app.R.string.invalid_transaction))
        if (transaction.recipient == Address.EMPTY && (transaction.payload == null || transaction.value != null)) {
            resultDialog?.setMessage(getString(com.alphawallet.app.R.string.contains_no_recipient))
        } else if (transaction.payload == null && transaction.value == null) {
            resultDialog?.setMessage(getString(com.alphawallet.app.R.string.contains_no_value))
        } else {
            resultDialog?.setMessage(getString(com.alphawallet.app.R.string.contains_no_data))
        }
        resultDialog?.setButtonText(com.alphawallet.app.R.string.button_ok)
        resultDialog?.setButtonListener { v -> resultDialog?.dismiss() }
        resultDialog?.setCancelable(true)
        resultDialog?.show()
    }

    private fun changeChainRequest(callbackId: Long, info: NetworkInfo) {
        //Don't show dialog if network doesn't need to be changed or if already showing
        Timber.d("1991 changeChainRequest activeNetwork " + activeNetwork?.chainId)
        if (activeNetwork != null && activeNetwork?.chainId === info.chainId || chainSwapDialog != null && chainSwapDialog!!.isShowing) {
            webView?.onWalletActionSuccessful(callbackId, "[\"$addressConnect\"]")
            return
        }

        //if we're switching between mainnet and testnet we need to pop open the 'switch to testnet' dialog (class TestNetDialog)
        // - after the user switches to testnet, go straight to switching the network (loadNewNetwork)
        // - if user is switching form testnet to mainnet, simply add the title below

        // at this stage, we know if it's testnet or not
        Timber.d("1991 changeChainRequest")
        if (!info.hasRealValue() && activeNetwork != null && activeNetwork?.hasRealValue() == true) {
            Timber.d("1991 changeChainRequest testnetDialog")
            val testnetDialog = TestNetDialog(requireContext(), info.chainId, object : TestNetDialog.TestNetDialogCallback{
                override fun onTestNetDialogClosed() {

                }

                override fun onTestNetDialogConfirmed(chainId: Long) {
//                    viewModel.setMainNetsSelected(false)
//                    //proceed with new network change, no need to pop a second dialog, we are swapping from a main net to a testnet
//                    //proceed with new network change, no need to pop a second dialog, we are swapping from a main net to a testnet
//                    val newNetwork: NetworkInfo = viewModel.getNetworkInfo(chainId)
//                    if (newNetwork != null) {
//                        loadNewNetwork(chainId)
//                    }
                }

            })
            testnetDialog.show()
        } else {
            //go straight to chain change dialog
            Timber.d("1991 changeChainRequest showChainChangeDialog")
            showChainChangeDialog(callbackId, info)
        }
    }

    override fun onWebpageLoaded(url: String?, title: String?) {
        onWebpageLoadComplete()
    }

    override fun onWebpageLoadComplete() {

    }

    fun switchNetwork(chainId: Long){
//        activeNetwork = viewModel.getNetworkInfo(chainId)
//        updateNetworkMenuItem()
        this.chainId = chainId
        viewModel.setNetwork(chainId)
        configureWebView()
//        startBalanceListener()

        webView?.resetView()
        webView?.reload()
    }

    fun switchNetworkAndLoadUrl(chainId: Long, url: String) {
//        forceChainChange = chainId //avoid prompt to change chain for 1inch
//        loadUrlAfterReload =
//            url //after reload with new chain inject, page is clean to load the correct site
//        if (viewModel == null) {
//            initViewModel()
//            return
//        }
//        activeNetwork = viewModel.getNetworkInfo(chainId)
//        updateNetworkMenuItem()
//        viewModel.setNetwork(chainId)
//        startBalanceListener()
//        setupWeb3()
//        web3.resetView()
//        web3.reload()
    }

//    private fun loadNewNetwork(newNetworkId: Long) {
//        Timber.d("Web3 loadNewNetwork $newNetworkId")
////        if (activeNetwork == null || activeNetwork!!.chainId != newNetworkId) {
////            balance.setVisibility(GONE)
////            symbol.setVisibility(GONE)
//            Timber.d("1991 loadNewNetwork newNetworkId $newNetworkId")
//            chainId = newNetworkId
////            setActiveNetwork(chainId)
////            getAllWallet2()
//        val chainNetwork = chainNetworks[newNetworkId]
//        addressConnect = chainNetwork?.address ?: ""
//        balance = chainNetwork?.balance ?: BigDecimal(0)
//        coinDecimals = chainNetwork?.coinDecimals ?: 18
//        walletConnect = Wallet(addressConnect)
//        walletConnect?.type = WalletType.HDKEY
//        activeNetwork = viewModel.getNetworkInfo(chainId)
//        rpcServerUrl = activeNetwork?.rpcServerUrl ?: ""
//        viewModel.setCurrentAddress(addressConnect)
//        viewModel.setCurrentWalletAddress(addressConnect)
//        viewModel.onDefaultWallet(walletConnect)
////        activeNetwork = NetworkInfo(wallet.coin.name, wallet.coin.code, rpcServerUrl, rpcServerUrl, chainId, "", "")
//
//            viewModel.setNetwork(newNetworkId)
////            onNetworkChanged(viewModel.getNetworkInfo(newNetworkId))
//            startBalanceListener()
//            configureWebView()
//            viewModel.updateGasPrice(newNetworkId)
////        }
//        //refresh URL page
////        reloadPage()
//        refresh()
//    }

    private fun reloadPage(){

        webView?.reload()
    }

    private fun onNetworkChanged(networkInfo: NetworkInfo?) {
//        val networkChanged =
//            networkInfo != null && (activeNetwork == null || activeNetwork!!.chainId != networkInfo.chainId)
//        activeNetwork = networkInfo
//        if (networkInfo != null) {
//            if (networkChanged) {
//                viewModel.findWallet()
//                updateNetworkMenuItem()
//            }
//            if (networkChanged && isOnHomePage()) resetDappBrowser() //trigger a reset if on homepage
//            updateFilters(networkInfo)
//        } else {
//            openNetworkSelection()
//            resetDappBrowser()
//        }
    }

    private fun updateFilters(networkInfo: NetworkInfo) {
//        if (networkInfo.hasRealValue() && !viewModel.isMainNetsSelected()) {
//            //switch to main net, no need to ask user
//            viewModel.setMainNetsSelected(true)
//        }
//        viewModel.addNetworkToFilters(networkInfo)
//        parentFragmentManager.setFragmentResult(
//            RESET_TOKEN_SERVICE,
//            Bundle()
//        ) //reset tokens service and wallet page with updated filters
    }

    //FETCH WALLET

//    @Inject
//    lateinit var accountManagerInjected: IAccountManager
//
//    @Inject
//    lateinit var accountStorage: AccountsStorage
//
//    @Inject
//    lateinit var adapterManagerInjected: IAdapterManager
//
//    @Inject
//    lateinit var getTestnetEnabledUseCase: GetTestnetEnabledUseCase
//
//    private val testMode get() = runBlocking {
//        return@runBlocking getTestnetEnabledUseCase.invoke()
//    }
//
////    val balanceViewModel: BalanceViewModel by viewModels()
//    val balanceAccountsViewModel: BalanceAccountsViewModel by viewModels()

    private var activeNetwork: NetworkInfo? = null
    private var chainId = EthereumNetworkBase.BINANCE_MAIN_ID
    private var rpcServerUrl = EthereumNetworkBase.BINANCE_MAIN_RPC_URL
    private var addressConnect = "0x5e02D3c495d65173670cdfF30f48b4C5e6e3C7A6"
    private var accountId = ""
    private var balance = BigDecimal.ZERO
    private var coinDecimals = 18

//    private var realmUpdate: RealmResults<RealmToken>? = null
//    private var realm: Realm? = null
//    private fun startBalanceListener() {
//        if (walletConnect == null || activeNetwork == null) return
//        if (realm == null || realm?.isClosed() == true) realm = viewModel.getRealmInstance(walletConnect)
//        if (realmUpdate != null) realmUpdate?.removeAllChangeListeners()
//        realmUpdate = realm?.where(RealmToken::class.java)
//            ?.equalTo("address", TokensRealmSource.databaseKey(activeNetwork!!.chainId, "eth"))
//            ?.findAllAsync()
//
//        realmUpdate?.addChangeListener { realmTokens ->
//            //update balance
//            if (realmTokens!!.size === 0) return@addChangeListener
//            val realmToken: RealmToken? = realmTokens!!.first()
////            balance.setVisibility(VISIBLE)
////            symbol.setVisibility(VISIBLE)
//            val newBalanceStr = BalanceUtils.getScaledValueFixed(
//                BigDecimal(realmToken?.balance ?: ""),
//                ETHER_DECIMALS.toLong(),
//                TOKEN_BALANCE_PRECISION
//            )
////            balance = newBalanceStr
//            Timber.d("1991 Balance $newBalanceStr  symbol " + activeNetwork?.shortName)
////            balance.setText(newBalanceStr)
////            symbol.setText(activeNetwork!!.shortName)
//        }
//    }

//    private fun stopBalanceListener() {
//        if (realmUpdate != null) {
//            realmUpdate?.removeAllChangeListeners()
//            realmUpdate = null
//        }
//        if (realm != null && !realm!!.isClosed()) realm?.close()
//    }
//
//    private var focusFlag: Boolean = false
//    fun comeIntoFocus() {
//        if (viewModel != null) {
//            if (viewModel.getActiveNetwork() == null || activeNetwork?.chainId != viewModel.getActiveNetwork()?.chainId) {
//                viewModel.checkForNetworkChanges()
//                Timber.d("1991 checkForNetworkChanges")
//            } else {
//                Timber.d("1991 updateGasPrice")
//                viewModel.startBalanceUpdate()
//                startBalanceListener()
//                viewModel.updateGasPrice(activeNetwork?.chainId ?: 0L)
//            }
//        }
////        if (urlTv != null) {
////            urlTv.clearFocus()
////            KeyboardUtils.hideKeyboard(urlTv)
////        }
//        focusFlag = true
//    }
//
//    fun leaveFocus() {
//        focusFlag = false
//        webView?.requestFocus()
////        if (urlTv != null) urlTv.clearFocus()
//        if (viewModel != null) viewModel.stopBalanceUpdate()
//        stopBalanceListener()
//    }

//    private var mnemonic = ""
//    private var passphrase = ""

//    private fun fetchWallet(){
//        view?.findViewById<ComposeView>(R.id.composable_wallet)?.setContent {
//            MaterialTheme {
//                getAllWallet()
//            }
//        }
//        val accountActive = accountStorage.activeAccountId
//        Timber.d("1998 accountActive " + accountActive)
//        accountStorage.allAccounts().forEach {
//            if(accountActive == it.id){
//                when(it.type){
//                    is AccountType.Mnemonic -> {
//                        val w = (it.type as AccountType.Mnemonic)
//                        w.words.forEach { word ->
//                            mnemonic += "$word "
//                        }
//                        passphrase = w.passphrase
//                        mnemonic = mnemonic.trim()
//                        viewModel.setMnemonicAndPassphrase(mnemonic, passphrase)
//                    }
//                }
//            }
////            when(it.type){
////                is AccountType.Mnemonic -> {
////                    val w = (it.type as AccountType.Mnemonic)
////                    Timber.d("1998 words2  " + w.words.size)
////                    Timber.d("1998 passphrase2  " + w.passphrase)
////                }
////            }
//        }
//    }

    var getNetwork = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.data == null) return@registerForActivityResult
        if(result.resultCode == Activity.RESULT_OK) {
            val networkId: Long = result.data!!.getLongExtra(C.EXTRA_CHAIN_ID, 1)
//        forceChainChange = networkId
            Timber.d("1991 networkId $networkId")

//            loadNewNetwork(networkId)
            initWalletConnectSessions(accountId, addressConnect)
            //might have adjusted the filters
//            parentFragmentManager.setFragmentResult(
//                RESET_TOKEN_SERVICE,
//                Bundle()
//            )
        }
    }

    var getNewNetwork = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.data == null) return@registerForActivityResult
        val networkId: Long? = result.data?.getLongExtra("EXTRA_CHAIN_ID", 1)
        Timber.d("1991 networkId $networkId")
        if(networkId != chainId){
            switchNetwork(networkId ?: 1L)
        }
//        loadNewNetwork(networkId ?: 1L)
    }

//    private fun activeNetwork(){
//        view?.findViewById<View>(R.id.browserNetwork)?.setOnClickListener {
//            val intent = Intent(requireContext(), SelectNetworkActivity::class.java)
//            Timber.d("105 testNet $testMode")
//            intent.putExtra(C.EXTRA_SINGLE_ITEM, true)
//            intent.putExtra(C.EXTRA_IS_TEST_NET, testMode)
//            intent.putExtra(C.EXTRA_CHAIN_ID, chainId)
//            getNetwork.launch(intent)
//        }
//
//        view?.findViewById<View>(R.id.browserFavorite)?.setOnClickListener {
//            viewModel.onFavoriteMenuClicked()
//        }
//    }

    private fun handleFavorite(viewState: BrowserViewState){
        val isFavorite = viewState.favorite != null
        val viewFav = view?.findViewById<ImageView>(R.id.browserFavoriteImageView)
        viewFav?.setImageResource(if (isFavorite) com.schoolonair.wallet.component.resources.R.drawable.ic_favorite_solid_16 else com.schoolonair.wallet.component.resources.R.drawable.ic_favorite_16)
    }

//    @Composable
//    private fun getAllWallet(){
//        val account = balanceAccountsViewModel.accountViewItem
//        val accountId = account?.id
//        viewModel.setAccountId(accountId)
//        Timber.d("1992 accountId $accountId")
////        val uiState = balanceViewModel.uiState
////        Crossfade(uiState.viewState) { viewState ->
////            when (viewState) {
////                is ViewState.Success -> {
////                    val balanceViewItems = uiState.balanceViewItems
////                    if (balanceViewItems.isNotEmpty()) {
////                        balanceViewItems.forEach {viewItem ->
////                            val wallet = viewItem.wallet
//////                            val receiveAdapter = adapterManagerInjected.getReceiveAdapterForWallet(viewItem.wallet) ?: throw ReceiveViewModel.NoReceiverAdapter()
//////                            val testNet = !receiveAdapter.isMainnet
//////                            val receiveAddress = receiveAdapter.receiveAddress
//////                            Timber.d("1991 ----------------")
////                        }
////                    } else {
//////                        BalanceItemsEmpty(navController, accountViewItem)
////                    }
////                }
////                else -> {}
////            }
////        }
//    }

    //CONNECT WC
    private fun launchWalletConnectSessionCancel() {
        Timber.d("1991 Web3 launchWalletConnectSessionCancel ")
        val sessionId =
            if (walletConnectSession != null) viewModel.getSessionId(walletConnectSession) else ""
        val bIntent = Intent(context, WalletConnectService::class.java)
        bIntent.action = WalletConnectActions.CLOSE.ordinal.toString()
        bIntent.putExtra("session", sessionId)
        requireActivity().startService(bIntent)
        refresh()
    }

    private val goToWalletConnect = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if(result.resultCode == RESULT_OK){
            val tabId = result.data?.getStringExtra(C.EXTRA_TAB_ID) ?: ""
            Timber.d("1995 goToWalletConnect tabId $tabId")
            viewModel.openTabById(tabId)
//            launch {
//                val tab = viewModel.onTabSelected(result.data?.getStringExtra(C.EXTRA_TAB_ID) ?: "")
//                browserActivity?.selectTab(tab)
//            }
//            refresh()
        }
    }



    private fun handleWalletConnect(url: String) {
        val importPassData: String = "wclocal:" + url
        Timber.d("103 url " + url)
        Timber.d("103 importPassData " + importPassData)
        val prevTabId = arguments?.getString(C.EXTRA_TAB_ID) ?: ""
        val intent = NavigationUtils.goToWalletConnect(requireActivity(), chainId, addressConnect, prevTabId, importPassData, url, accountId)
        goToWalletConnect.launch(intent)
    }

    private fun displayCloseWC() {
        Handler(Looper.getMainLooper()).post {
            if (resultDialog != null && resultDialog!!.isShowing) resultDialog!!.dismiss()
            resultDialog = AWalletAlertDialog(requireContext())
            resultDialog!!.setIcon(com.schoolonair.wallet.component.resources.R.drawable.ic_warning)
            resultDialog?.setTitle(com.alphawallet.app.R.string.title_wallet_connect)
            resultDialog!!.setMessage(getString(com.alphawallet.app.R.string.unsupported_walletconnect))
            resultDialog!!.setButtonText(com.alphawallet.app.R.string.button_ok)
            resultDialog!!.setButtonListener { v: View? ->
                launchWalletConnectSessionCancel()
                launchNetworkPicker()
                resultDialog!!.dismiss()
            }
            resultDialog!!.show()
        }
    }

    private fun onNetworkClick(){
        view?.findViewById<View>(R.id.browserNetwork)?.setOnClickListener {
            launchNetworkPicker()
        }

        view?.findViewById<View>(R.id.browserFavorite)?.setOnClickListener {
            viewModel.onFavoriteMenuClicked()
        }
    }

    private fun launchNetworkPicker() {
        val className = Class.forName("com.mangala.wallet.android.SelectNetworkActivity")
        val intent = Intent(requireContext(), className)
        getNewNetwork.launch(intent)
    }

    private fun initWalletConnectSessions(accountId: String, address: String){
//        val data = viewModel.getAllSession(accountId)
//        val clientMap = mutableListOf<WCClient>()
//        if(!data.isNullOrEmpty()){
//            data.forEach {
//                val peerId = it.peerId
//                if(!peerId.isNullOrEmpty()){
////                    clientMap.add(
////                        WCUtils.createWalletConnectSession(getString(com.schoolonair.wallet.component.resources.R.string.app_name_wallet),
////                            address,
////                            it.session,
////                            peerId,
////                            it.remotePeerId
////                        )
////                    )
//                }
//            }
//        }
//        if(!clientMap.isNullOrEmpty()){
//            connectServiceAddClients(clientMap)
//        }
    }

    private fun connectServiceAddClients(clientMap: List<WCClient>){
        val connection = object : ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val walletConnectService = (service as? WalletConnectService.LocalBinder)?.service
                walletConnectService?.addClients(clientMap)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {

            }
        }
        WCUtils.startServiceLocal(requireContext(), connection, WalletConnectActions.CONNECT)
    }

}

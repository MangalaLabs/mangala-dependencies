/*
 * Copyright (c) 2019-2023 AlphaWallet
 * Copyright (c) 2023-2025 Mangala Wallet
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
 * Modified from original source: https://github.com/AlphaWallet/alpha-wallet-android
 */

package com.alphawallet.app.widget

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.preference.PreferenceManager
import com.alphawallet.app.C
import com.alphawallet.app.R
import com.alphawallet.app.entity.*
import com.alphawallet.app.entity.tokens.Token
import com.alphawallet.app.repository.SharedPreferenceRepository
import com.alphawallet.app.repository.entity.Realm1559Gas
import com.alphawallet.app.repository.entity.RealmTransaction
import com.alphawallet.app.service.TokensService
import com.alphawallet.app.ui.TransactionSuccessActivity
import com.alphawallet.app.ui.widget.entity.ActionSheetCallback
import com.alphawallet.app.ui.widget.entity.GasWidgetInterface
import com.alphawallet.app.util.Utils
import com.alphawallet.app.web3.entity.Web3Transaction
import com.alphawallet.token.entity.Signable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.trustwallet.walletconnect.models.WCPeerMeta
import io.realm.Realm
import timber.log.Timber
import java.math.BigDecimal
import java.math.BigInteger

//import com.alphawallet.app.ui.WalletConnectActivity;
class ActionSheetDialog : BottomSheetDialog, StandardFunctionInterface, ActionSheetInterface {
    //    private final BottomSheetToolbarView toolbar;
    private var gasWidget: GasWidget2? = null
    private var gasWidgetLegacy: GasWidget? = null
    private var balanceDisplay: BalanceDisplayWidget? = null
    private var networkDisplay: NetworkDisplayWidget? = null
    private val confirmationWidget: ConfirmationWidget?
    private var textDappName: AppCompatTextView? = null
    private val textTitle: AppCompatTextView?
    private var addressDetail: AddressDetailView? = null
    private var amountDisplay: AmountDisplayWidget? = null
    private var assetDetailView: AssetDetailView? = null
    private val functionBar: FunctionButtonBar?
    private val detailWidget: TransactionDetailWidget?
    private val walletConnectRequestWidget: WalletConnectRequestWidget?
    private val activity: Activity
    private val gasWidgetInterface: GasWidgetInterface?
    private val token: Token?
    private val tokensService: TokensService?
    private val candidateTransaction: Web3Transaction?
    private val actionSheetCallback: ActionSheetCallback?
    private val callbackId: Long
    private var signCallback: SignAuthenticationCallback? = null
    private var mode: ActionSheetMode
    private var txHash: String? = null
    private var actionCompleted = false
    private var use1559Transactions = false
    private var transaction: Transaction? = null

    constructor(
        activity: Activity, tx: Web3Transaction, t: Token?,
        destName: String?, destAddress: String?, ts: TokensService,
        aCallBack: ActionSheetCallback?
    ) : super(activity) {
        Timber.d("103 dialog_action_sheet")
        val view = View.inflate(context, R.layout.dialog_action_sheet, null)
        setContentView(view)
        val behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

//        toolbar = findViewById(R.id.bottom_sheet_toolbar);
        textTitle = findViewById(R.id.text_title)
        gasWidget = findViewById(R.id.gas_widgetx)
        gasWidgetLegacy = findViewById(R.id.gas_widget_legacy)
        balanceDisplay = findViewById(R.id.balance)
        networkDisplay = findViewById(R.id.network_display_widget)
        confirmationWidget = findViewById(R.id.confirmation_view)
        detailWidget = findViewById(R.id.detail_widget)
        addressDetail = findViewById(R.id.recipient)
        amountDisplay = findViewById(R.id.amount_display)
        assetDetailView = findViewById(R.id.asset_detail)
        functionBar = findViewById(R.id.layoutButtons)
        this.activity = activity
        //        if (activity instanceof HomeActivity)
//        {
        mode = ActionSheetMode.SEND_TRANSACTION_DAPP
        //        }
//        else if (activity instanceof WalletConnectActivity)
//        {
//            mode = ActionSheetMode.SEND_TRANSACTION_WC;
//        }
//        else
//        {
//            mode = ActionSheetMode.SEND_TRANSACTION;
//        }
        signCallback = null
        walletConnectRequestWidget = null
        actionSheetCallback = aCallBack
        actionCompleted = false
        token = t
        tokensService = ts
        candidateTransaction = tx
        callbackId = tx.leafPosition
        transaction = Transaction(tx, token!!.tokenInfo.chainId, ts.currentAddress)
        transaction!!.transactionInput = Transaction.decoder.decodeInput(
            candidateTransaction,
            token.tokenInfo.chainId,
            token.wallet
        )
        balanceDisplay!!.setupBalance(token, tokensService, transaction)
        networkDisplay!!.setNetwork(token.tokenInfo.chainId)
        functionBar!!.setupFunctions(this, ArrayList(listOf(R.string.action_confirm)))
        functionBar.revealButtons()
        gasWidgetInterface = setupGasWidget()
        if (tx.gasLimit != BigInteger.ZERO) {
            setGasEstimate(tx.gasLimit)
        }
        updateAmount()
        setUpAmount()
        setUpNetWork(token.tokenInfo.chainId)
        findViewById<AppCompatTextView>(R.id.text_from_address)?.text = token.tokenInfo.address

        findViewById<AppCompatTextView>(R.id.text_to_address)?.text = destAddress

//        Timber.d("103 destName " + destName + " destAddress " + destAddress);
        addressDetail!!.setupAddress(
            destAddress,
            destName,
            tokensService.getToken(token.tokenInfo.chainId, destAddress)
        )
        if (token.isNonFungible) {
            balanceDisplay!!.visibility = View.GONE
            if (token.interfaceSpec == ContractType.ERC1155) {
                val assetList = token.getAssetListFromTransaction(transaction)
                amountDisplay!!.visibility = View.GONE
                amountDisplay!!.setAmountFromAssetList(assetList)
                setupTransactionDetails()
            } else {
                amountDisplay!!.visibility = View.GONE
                assetDetailView!!.setupAssetDetail(token, eRC721TokenId, this)
                assetDetailView!!.visibility = View.VISIBLE
            }
        }
        setupCancelListeners()
    }

    constructor(
        activity: Activity,
        aCallback: ActionSheetCallback?,
        sCallback: SignAuthenticationCallback?,
        message: Signable?
    ) : super(activity) {
        Timber.d("103 dialog_action_sheet_sign")
        setContentView(R.layout.dialog_action_sheet_sign)

//        toolbar = findViewById(R.id.bottom_sheet_toolbar);
        textTitle = findViewById(R.id.text_title)
        //        gasWidget = findViewById(R.id.gas_widgetx);
//        gasWidgetLegacy = findViewById(R.id.gas_widget_legacy);
//        balanceDisplay = findViewById(R.id.balance);
//        networkDisplay = findViewById(R.id.network_display_widget);
        confirmationWidget = findViewById(R.id.confirmation_view)
        addressDetail = findViewById(R.id.requester)
        //        amountDisplay = findViewById(R.id.amount_display);
//        assetDetailView = findViewById(R.id.asset_detail);
        functionBar = findViewById(R.id.layoutButtons)
        detailWidget = null
        mode = ActionSheetMode.SIGN_MESSAGE
        callbackId = message?.callbackId ?: 0L
        this.activity = activity
        actionSheetCallback = aCallback
        Timber.d("1991 ActionSheetDialog")
        signCallback = sCallback
        token = null
        tokensService = null
        candidateTransaction = null
        actionCompleted = false
        walletConnectRequestWidget = null
        gasWidgetInterface = null
        addressDetail!!.setupRequester(message?.origin)
        val signWidget = findViewById<SignDataWidget>(R.id.sign_widget)
        signWidget!!.setupSignData(message)
        signWidget.setLockCallback(this)
        textTitle!!.setText(Utils.getSigningTitle(message))
        functionBar!!.setupFunctions(this, ArrayList(listOf(R.string.action_confirm)))
        functionBar.revealButtons()
        setupCancelListeners()
    }

    constructor(
        activity: Activity,
        aCallback: ActionSheetCallback?,
        titleId: Int,
        message: String?,
        buttonTextId: Int,
        cId: Long,
        baseToken: Token?
    ) : super(activity) {
        Timber.d("103 dialog_action_sheet_message")
        setContentView(R.layout.dialog_action_sheet_message)
        textTitle = findViewById(R.id.text_title)
        val messageView = findViewById<TextView>(R.id.text_message)
        functionBar = findViewById(R.id.layoutButtons)
        this.activity = activity
        actionSheetCallback = aCallback
        mode = ActionSheetMode.MESSAGE
        textTitle!!.setText(titleId)
        messageView!!.text = message
        gasWidget = null
        balanceDisplay = null
        networkDisplay = null
        confirmationWidget = null
        addressDetail = null
        amountDisplay = null
        assetDetailView = null
        detailWidget = null
        callbackId = cId
        token = baseToken
        tokensService = null
        candidateTransaction = null
        walletConnectRequestWidget = null
        gasWidgetLegacy = null
        gasWidgetInterface = null
        functionBar!!.setupFunctions(this, ArrayList(listOf(buttonTextId)))
        functionBar.revealButtons()
        setupCancelListeners()
    }

    // wallet connect request
    constructor(
        activity: Activity,
        wcPeerMeta: WCPeerMeta,
        chainIdOverride: Long,
        iconUrl: String?,
        actionSheetCallback: ActionSheetCallback
    ) : super(activity) {
        Timber.d("103 dialog_wallet_connect_sheet")
        setContentView(R.layout.dialog_wallet_connect_sheet)
        mode = ActionSheetMode.WALLET_CONNECT_REQUEST
        functionBar = findViewById(R.id.layoutButtons)
        textTitle = findViewById(R.id.text_title)
        this.activity = activity
        this.actionSheetCallback = actionSheetCallback
        walletConnectRequestWidget = findViewById(R.id.wallet_connect_widget)
        gasWidget = null
        balanceDisplay = null
        networkDisplay = null
        confirmationWidget = null
        addressDetail = null
        amountDisplay = null
        assetDetailView = null
        detailWidget = null
        token = null
        tokensService = null
        candidateTransaction = null
        callbackId = 0
        gasWidgetLegacy = null
        gasWidgetInterface = null

//        textTitle.setLogo(activity, iconUrl);
        textTitle!!.text = wcPeerMeta.name
        //        toolbar.setCloseListener(v -> actionSheetCallback.denyWalletConnect());
        walletConnectRequestWidget!!.setupWidget(
            wcPeerMeta,
            chainIdOverride
        ) { actionSheetCallback.openChainSelection() }
        val functionList = ArrayList<Int>()
        functionList.add(R.string.approve)
        functionList.add(R.string.dialog_reject)
        functionBar!!.setupFunctions(this, functionList)
        functionBar.revealButtons()
    }

    // switch chain
    constructor(
        activity: Activity, aCallback: ActionSheetCallback?, titleId: Int, buttonTextId: Int,
        cId: Long, baseToken: Token?, oldNetwork: NetworkInfo?, newNetwork: NetworkInfo?
    ) : super(activity) {
        Timber.d("103 dialog_action_sheet_switch_chain")
        setContentView(R.layout.dialog_action_sheet_switch_chain)
        textTitle = findViewById(R.id.text_title)
        val switchChainWidget = findViewById<SwitchChainWidget>(R.id.switch_chain_widget)
        switchChainWidget!!.setupSwitchChainData(oldNetwork, newNetwork)
        functionBar = findViewById(R.id.layoutButtons)
        this.activity = activity
        actionSheetCallback = aCallback
        mode = ActionSheetMode.MESSAGE
        textTitle!!.setText(titleId)
        gasWidget = null
        balanceDisplay = null
        networkDisplay = null
        confirmationWidget = null
        addressDetail = null
        amountDisplay = null
        assetDetailView = null
        detailWidget = null
        callbackId = cId
        token = baseToken
        tokensService = null
        candidateTransaction = null
        walletConnectRequestWidget = null
        gasWidgetLegacy = null
        gasWidgetInterface = null
        functionBar!!.setupFunctions(this, ArrayList(listOf(buttonTextId)))
        functionBar.revealButtons()
        setupCancelListeners()
    }

    private fun setupGasWidget(): GasWidgetInterface? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val canUse1559Transactions =
            prefs.getBoolean(SharedPreferenceRepository.EXPERIMENTAL_1559_TX, false)
        use1559Transactions =
            (canUse1559Transactions && has1559Gas() //1559 Transactions toggled on in settings and this chain supports 1559
                    && !(token!!.isEthereum && candidateTransaction!!.leafPosition == -2L) //User not sweeping wallet (if so we need to use legacy tx)
                    && !tokensService!!.hasLockedGas(token.tokenInfo.chainId) //Service has locked gas, can only use legacy (eg Optimism).
                    && !candidateTransaction!!.isConstructor) //Currently cannot use EIP1559 for constructors due to gas calculation issues
        return if (use1559Transactions) {
            gasWidget!!.setupWidget(
                tokensService,
                token,
                candidateTransaction,
                actionSheetCallback!!.gasSelectLauncher()
            )
            gasWidget
        } else {
            gasWidget!!.visibility = View.GONE
            gasWidgetLegacy!!.visibility = View.VISIBLE
            gasWidgetLegacy!!.setupWidget(
                tokensService,
                token,
                candidateTransaction,
                this,
                actionSheetCallback!!.gasSelectLauncher()
            )
            gasWidgetLegacy
        }
    }

    constructor(activity: Activity, mode: ActionSheetMode) : super(activity) {
        this.activity = activity
        this.mode = mode
        if (mode == ActionSheetMode.NODE_STATUS_INFO) {
            setContentView(R.layout.dialog_action_sheet_node_status)
        }
        textTitle = null
        gasWidget = null
        gasWidgetLegacy = null
        balanceDisplay = null
        networkDisplay = null
        confirmationWidget = null
        addressDetail = null
        amountDisplay = null
        assetDetailView = null
        functionBar = null
        detailWidget = null
        walletConnectRequestWidget = null
        gasWidgetInterface = null
        token = null
        tokensService = null
        candidateTransaction = null
        actionSheetCallback = null
        callbackId = 0
    }

    fun setSignOnly() {
        //sign only, and return signature to process
        mode = ActionSheetMode.SIGN_TRANSACTION
    }

    fun onDestroy() {
        gasWidgetInterface?.onDestroy()
        if (assetDetailView != null) assetDetailView!!.onDestroy()
    }

    private fun setUpAmount(){
        val textAmount = findViewById<AppCompatTextView>(R.id.text_dapp_amount)
        textAmount?.text = amountDisplay?.getAmount()
    }

    private fun setUpNetWork(chainId: Long){
        val tokenIcon = findViewById<TokenIcon>(R.id.network_icon)
        val networkName = findViewById<AppCompatTextView>(R.id.text_network_name)
        tokenIcon?.bindData(chainId)
        networkName?.text = com.alphawallet.app.repository.EthereumNetworkBase.getShortChainName(chainId)
    }

    fun setURL(url: String) {
//        AddressDetailView requester = findViewById(R.id.requester);
//        requester.setupRequester(url);
        textDappName = findViewById(R.id.text_dapp_name)
        if (textDappName != null) {
            var dappName = url?.replace("https://", "")?.replace("http://", "") ?: ""
            if(dappName.contains("/")){
                dappName = dappName.split("/")[0]
            }
            textDappName?.text = dappName
        }
        detailWidget!!.setupTransaction(
            candidateTransaction, token!!.tokenInfo.chainId, tokensService!!.currentAddress,
            tokensService.getNetworkSymbol(token.tokenInfo.chainId), this
        )
        if (candidateTransaction!!.isConstructor) {
            addressDetail!!.visibility = View.GONE
        }
        if (candidateTransaction.value == BigInteger.ZERO) {
            amountDisplay!!.visibility = View.GONE
        } else {
            amountDisplay!!.visibility = View.GONE
            amountDisplay!!.setAmountUsingToken(
                candidateTransaction.value, tokensService.getServiceToken(
                    token.tokenInfo.chainId
                ), tokensService
            )
        }
    }

    private fun setupTransactionDetails() {
        detailWidget!!.setupTransaction(
            candidateTransaction, token!!.tokenInfo.chainId, tokensService!!.currentAddress,
            tokensService.getNetworkSymbol(token.tokenInfo.chainId), this
        )
        detailWidget.visibility = View.VISIBLE
    }

    fun setCurrentGasIndex(result: ActivityResult?) {
        if (result == null || result.data == null) return
        val gasSelectionIndex = result.data!!
            .getIntExtra(C.EXTRA_SINGLE_ITEM, TXSpeed.STANDARD.ordinal)
        val customNonce = result.data!!
            .getLongExtra(C.EXTRA_NONCE, -1)
        val maxFeePerGas = if (result.data!!.hasExtra(C.EXTRA_GAS_PRICE)) BigInteger(
            result.data!!.getStringExtra(C.EXTRA_GAS_PRICE)
        ) else BigInteger.ZERO
        val maxPriorityFee = if (result.data!!.hasExtra(C.EXTRA_MIN_GAS_PRICE)) BigInteger(
            result.data!!.getStringExtra(C.EXTRA_MIN_GAS_PRICE)
        ) else BigInteger.ZERO
        val customGasLimit = BigDecimal(result.data!!.getStringExtra(C.EXTRA_GAS_LIMIT))
        val expectedTxTime = result.data!!
            .getLongExtra(C.EXTRA_AMOUNT, 0)
        gasWidgetInterface!!.setCurrentGasIndex(
            gasSelectionIndex,
            maxFeePerGas,
            maxPriorityFee,
            customGasLimit,
            expectedTxTime,
            customNonce
        )
    }

    private val isSendingTransaction: Boolean
        private get() = mode != ActionSheetMode.SIGN_MESSAGE && mode != ActionSheetMode.SIGN_TRANSACTION

    fun setupResendTransaction(callingMode: ActionSheetMode) {
        mode = callingMode
        gasWidgetInterface!!.setupResendSettings(mode, candidateTransaction!!.gasPrice)
        balanceDisplay!!.visibility = View.GONE
        networkDisplay!!.visibility = View.GONE
        addressDetail!!.visibility = View.GONE
        detailWidget!!.visibility = View.GONE
        amountDisplay!!.visibility = View.GONE
    }

    override fun updateAmount() {
        showAmount(transactionAmount.toBigInteger())
    }

    override fun handleClick(action: String, id: Int) {
        Timber.d("1991 handleClick mode $mode action $action id $id")
        when (mode) {
            ActionSheetMode.SEND_TRANSACTION_WC, ActionSheetMode.SEND_TRANSACTION, ActionSheetMode.SEND_TRANSACTION_DAPP, ActionSheetMode.SPEEDUP_TRANSACTION, ActionSheetMode.CANCEL_TRANSACTION ->                 //check gas and warn user
                if (!gasWidgetInterface!!.checkSufficientGas()) {
                    askUserForInsufficientGasConfirm()
                } else {
                    Timber.d("1991 handleClick sendTransaction")
                    sendTransaction()
                }
            ActionSheetMode.SIGN_MESSAGE -> signMessage()
            ActionSheetMode.SIGN_TRANSACTION -> signTransaction()
            ActionSheetMode.MESSAGE -> actionSheetCallback!!.buttonClick(callbackId, token)
            ActionSheetMode.WALLET_CONNECT_REQUEST -> if (id == R.string.approve) {
                actionSheetCallback!!.notifyWalletConnectApproval(walletConnectRequestWidget!!.chainIdOverride)
                tryDismiss()
            } else {
                actionSheetCallback!!.denyWalletConnect()
            }
            else -> {}
        }
        actionSheetCallback!!.notifyConfirm(mode.toString())
    }

    private val transactionAmount: BigDecimal
        private get() {
            val txAmount: BigDecimal
            txAmount = if (token!!.isEthereum) {
                BigDecimal(gasWidgetInterface!!.value)
            } else if (isSendingTransaction) {
                BigDecimal(token.getTransferValueRaw(transaction!!.transactionInput))
            } else {
                BigDecimal.ZERO
            }
            return txAmount
        }
    private val eRC721TokenId: String
        private get() = if (!token!!.isERC721) "" else token.getTransferValueRaw(transaction!!.transactionInput)
            .toString()

    private fun signMessage() {
        //get authentication
        functionBar!!.visibility = View.GONE

        //authentication screen
        val localSignCallback: SignAuthenticationCallback = object : SignAuthenticationCallback {
            val signWidget = findViewById<SignDataWidget>(R.id.sign_widget)
            override fun gotAuthorisation(gotAuth: Boolean) {
                actionCompleted = true
                //display success and hand back to calling function
                if (gotAuth) {
                    confirmationWidget!!.startProgressCycle(1)
                    Timber.d("1991 signMessage gotAuthorisation $gotAuth")
                    signCallback!!.gotAuthorisationForSigning(gotAuth, signWidget!!.signable)
                } else {
                    cancelAuthentication()
                }
            }

            override fun cancelAuthentication() {
                confirmationWidget!!.hide()
                Timber.d("1991 signMessage cancelAuthentication")
                signCallback!!.gotAuthorisationForSigning(false, signWidget!!.signable)
            }
        }
        actionSheetCallback!!.getAuthorisation(localSignCallback)
    }

    /**
     * Popup a dialogbox to ask user if they really want to try to send this transaction,
     * as we calculate it will fail due to insufficient gas. User knows best though.
     */
    private fun askUserForInsufficientGasConfirm() {
        val dialog = AWalletAlertDialog(context)
        dialog.setIcon(AWalletAlertDialog.WARNING)
        dialog.setTitle(R.string.insufficient_gas)
        dialog.setMessage(context.getString(R.string.not_enough_gas_message))
        dialog.setButtonText(R.string.action_send)
        dialog.setSecondaryButtonText(R.string.cancel_transaction)
        dialog.setButtonListener { v: View? ->
            dialog.dismiss()
            sendTransaction()
        }
        dialog.setSecondaryButtonListener { v: View? -> dialog.dismiss() }
        dialog.show()
    }

    fun transactionWritten(tx: String) {
        txHash = tx
        //dismiss on message completion
        confirmationWidget!!.completeProgressMessage(txHash) { showTransactionSuccess() }
        if (!TextUtils.isEmpty(tx) && tx.startsWith("0x")) {
            updateRealmTransactionFinishEstimate(tx)
        }
    }

    private fun showTransactionSuccess() {
        when (mode) {
            ActionSheetMode.SEND_TRANSACTION -> {
                //Display transaction success dialog
                val intent = Intent(context, TransactionSuccessActivity::class.java)
                intent.putExtra(C.EXTRA_TXHASH, txHash)
                intent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                activity.startActivityForResult(intent, C.COMPLETED_TRANSACTION)
                tryDismiss()
            }
            ActionSheetMode.SEND_TRANSACTION_WC, ActionSheetMode.SEND_TRANSACTION_DAPP, ActionSheetMode.SPEEDUP_TRANSACTION, ActionSheetMode.CANCEL_TRANSACTION, ActionSheetMode.SIGN_TRANSACTION ->                 //return to dapp
                tryDismiss()
            else -> {}
        }
    }

    private fun tryDismiss() {
        if (Utils.stillAvailable(activity) && isShowing) dismiss()
    }

    private fun updateRealmTransactionFinishEstimate(txHash: String) {
        val expectedTime =
            System.currentTimeMillis() + gasWidgetInterface!!.expectedTransactionTime * 1000
        try {
            tokensService!!.walletRealmInstance.use { realm ->
                realm.executeTransactionAsync { r: Realm ->
                    val rt = r.where(
                        RealmTransaction::class.java
                    )
                        .equalTo("hash", txHash)
                        .findFirst()
                    if (rt != null) {
                        rt.expectedCompletion = expectedTime
                        r.insertOrUpdate(rt)
                    }
                }
            }
        } catch (e: Exception) {
            //
        }
    }

    private fun setupCancelListeners() {
//        toolbar.setCloseListener(v -> dismiss());
        val imageClose = findViewById<AppCompatImageView>(R.id.image_close)
        imageClose?.setOnClickListener { v: View? -> dismiss() }
        setOnDismissListener { v: DialogInterface? ->
            actionSheetCallback!!.dismissed(txHash, callbackId, actionCompleted)
            gasWidgetInterface?.onDestroy()
        }
    }

    private fun signTransaction() {
        functionBar!!.visibility = View.GONE

        //get approval and push transaction
        //authentication screen
        signCallback = object : SignAuthenticationCallback {
            override fun gotAuthorisation(gotAuth: Boolean) {
                actionCompleted = true
                confirmationWidget!!.startProgressCycle(4)
                //send the transaction
                actionSheetCallback!!.signTransaction(formTransaction())
            }

            override fun cancelAuthentication() {
                confirmationWidget!!.hide()
                functionBar.visibility = View.VISIBLE
            }
        }
        Timber.d("1991 signTransaction ")
        actionSheetCallback!!.getAuthorisation(signCallback)
    }

    fun completeSignRequest(gotAuth: Boolean) {
        if (signCallback != null) {
            actionCompleted = true
            when (mode) {
                ActionSheetMode.SEND_TRANSACTION_WC, ActionSheetMode.SEND_TRANSACTION, ActionSheetMode.SEND_TRANSACTION_DAPP, ActionSheetMode.SPEEDUP_TRANSACTION, ActionSheetMode.CANCEL_TRANSACTION, ActionSheetMode.SIGN_TRANSACTION -> {
                    Timber.d("1991 completeSignRequest SIGN_TRANSACTION")
                    signCallback!!.gotAuthorisation(gotAuth)
                }
                ActionSheetMode.SIGN_MESSAGE -> {
                    actionCompleted = true
                    //display success and hand back to calling function
                    Timber.d("1991 completeSignRequest SIGN_MESSAGE")
                    confirmationWidget!!.startProgressCycle(1)
                    signCallback!!.gotAuthorisation(gotAuth)
                }
                else -> {}
            }
        }
    }

    private fun formTransaction(): Web3Transaction {
        //form Web3Transaction
        return if (!use1559Transactions) {
            val currentGasPrice = gasWidgetInterface!!.getGasPrice(
                candidateTransaction!!.gasPrice
            ) // also recalculates the transaction value
            Web3Transaction(
                candidateTransaction.recipient,
                candidateTransaction.contract,
                gasWidgetInterface.value,
                currentGasPrice,
                gasWidgetInterface.gasLimit,
                gasWidgetInterface.nonce,
                candidateTransaction.payload,
                candidateTransaction.leafPosition
            )
        } else {
            Web3Transaction(
                candidateTransaction!!.recipient,
                candidateTransaction.contract,
                gasWidgetInterface!!.value,
                gasWidgetInterface.gasMax,
                gasWidgetInterface.priorityFee,
                gasWidgetInterface.gasLimit,
                gasWidgetInterface.nonce,
                candidateTransaction.payload,
                candidateTransaction.leafPosition
            )
        }
    }

    private fun sendTransaction() {
        functionBar!!.visibility = View.GONE

        //get approval and push transaction
        //authentication screen
        signCallback = object : SignAuthenticationCallback {
            override fun gotAuthorisation(gotAuth: Boolean) {
                Timber.d("1991 sendTransaction gotAuthorisation $gotAuth")
                actionCompleted = true
                if (!gotAuth) {
                    cancelAuthentication()
                    return
                }
                confirmationWidget!!.startProgressCycle(4)
                //send the transaction
                actionSheetCallback!!.sendTransaction(formTransaction())
            }

            override fun cancelAuthentication() {
                confirmationWidget!!.hide()
                functionBar.visibility = View.VISIBLE
            }
        }
        Timber.d("1991 sendTransaction actionSheetCallback")
        actionSheetCallback!!.getAuthorisation(signCallback)
    }

    override fun lockDragging(lock: Boolean) {
        behavior.isDraggable = !lock

        //ensure view fully expanded when locking scroll. Otherwise we may not be able to see our expanded view
        if (lock) {
            val bottomSheet =
                findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) BottomSheetBehavior.from(bottomSheet).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun fullExpand() {
        val bottomSheet =
            findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) BottomSheetBehavior.from(bottomSheet).state =
            BottomSheetBehavior.STATE_EXPANDED
    }

    //Takes gas estimate from calling activity (eg WalletConnectActivity) and updates dialog
    fun setGasEstimate(estimate: BigInteger?) {
        gasWidgetInterface!!.setGasEstimate(estimate)
        functionBar!!.setPrimaryButtonEnabled(true)
    }

    private fun showAmount(amountVal: BigInteger) {
        amountDisplay!!.setAmountUsingToken(amountVal, token, tokensService)
        val networkFee = gasWidgetInterface!!.getGasPrice(
            candidateTransaction!!.gasPrice
        ).multiply(gasWidgetInterface.gasLimit)
        val balanceAfterTransaction = token!!.balance.toBigInteger().subtract(
            gasWidgetInterface.value
        )
        balanceDisplay!!.setNewBalanceText(
            token,
            transactionAmount,
            networkFee,
            balanceAfterTransaction
        )
    }

    fun success() {
        if (!activity.isFinishing && Utils.stillAvailable(activity) && isShowing) {
            confirmationWidget!!.completeProgressMessage(".") { dismiss() }
        }
    }

    fun forceDismiss() {
        setOnDismissListener { v: DialogInterface? -> }
        dismiss()
    }

    fun waitForEstimate() {
        functionBar!!.setPrimaryButtonWaiting()
    }

    fun updateChain(chainId: Long) {
        walletConnectRequestWidget!!.updateChain(chainId)
    }

    fun getTransaction(): Web3Transaction? {
        return candidateTransaction
    }

    private fun has1559Gas(): Boolean {
        try {
            tokensService!!.tickerRealmInstance.use { realm ->
                val rgs = realm.where(Realm1559Gas::class.java)
                    .equalTo("chainId", token!!.tokenInfo.chainId)
                    .findFirst()
                if (rgs != null) {
                    return true
                }
            }
        } catch (e: Exception) {
            //
        }
        return false
    }
}

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

//package com.mangala.web3
//
//import android.app.Activity
//import android.content.DialogInterface
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.fragment.app.FragmentManager
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import com.alphawallet.app.C
//import com.alphawallet.app.web3.entity.Web3Transaction
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import com.mangala.app.browser.BrowserTabFragment
//import com.mangala.browser_bridge_base.ActionTransactionCallback
//import com.mangala.browser_bridge_base.ConfirmTransactionViewModel
//import com.schoolonair.wallet.browser.app.databinding.DialogConfirmTransactionBinding
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import org.koin.androidx.viewmodel.ext.android.viewModel
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//import timber.log.Timber
//import java.math.BigDecimal
//
//class ConfirmTransactionBottomDialog : BottomSheetDialogFragment(), KoinComponent {
//
//    private var _binding: DialogConfirmTransactionBinding? = null
//    private val binding get() = _binding!!
//
//    private val viewModel: ConfirmTransactionViewModel by inject()
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = DialogConfirmTransactionBinding.inflate(inflater, container, false)
//        lifecycle.addObserver(lifecycleEventObserver)
//        return binding.root
//    }
//
//    private var callbackId: Long = 0
//    private val web3Transaction get() = requireArguments()[EXTRA_WEB3_TRANSACTION] as Web3Transaction
//    private val url get() = requireArguments()[EXTRA_URL] as? String
//    private val balance get() = requireArguments()[EXTRA_BALANCE] as? String
//    private val coinDecimals get() = requireArguments()[EXTRA_COIN_DECIMAL] as? Long
//    private val chainId get() = requireArguments()[EXTRA_CHAIN_ID] as? Long
//
//    private val lifecycleEventObserver = LifecycleEventObserver { source, event ->
//        when (event) {
//            Lifecycle.Event.ON_RESUME -> {
//
//            }
//
//            Lifecycle.Event.ON_CREATE -> {
//                setUpToken()
//                viewModel.getFeeHistory()
//                setUpData()
//                confirmClicked()
//                cancelClicked()
//
//                subscribeEventSignTransaction()
//            }
//
//            Lifecycle.Event.ON_PAUSE -> {
//
//            }
//
//            else -> {}
//        }
//    }
//
//    private fun setUpData(){
//        callbackId = web3Transaction?.leafPosition ?: 0
//        binding.textDappName.text = url
//
////            web3Transaction?.let {
//                val amount = BigDecimal(web3Transaction!!.value)
//                Timber.d("1991 amount: ${amount.toPlainString()} web3Transaction!!.value ${web3Transaction!!.value}")
//                viewModel.estimateGas(web3Transaction!!.recipient.toString(), web3Transaction!!.payload.toString(), amount.toPlainString())
////            }
//
//    }
//    private fun setUpToken() {
//        binding.networkIcon.bindData(chainId ?: 1L)
//    }
//
//    private val unlockPin = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result: ActivityResult ->
//        if(result.resultCode == Activity.RESULT_OK){
//            web3Transaction?.let {
//                val amount = BigDecimal(web3Transaction!!.value)
//                Timber.d("1991 amount: $amount")
//                viewModel.signTransaction(
//                    web3Transaction.recipient.toString(),
//                    web3Transaction.isLegacyTransaction,
//                    web3Transaction.nonce,
//                    web3Transaction.payload,
//                    amount.toPlainString())
//                dismiss()
//            }
//        }
//    }
//
//    private fun confirmClicked() {
//        binding.buttonConfirm.setOnClickListener {
//            val className = Class.forName("com.mangala.wallet.android.UnlockPinActivity")
//            val intent = Intent(requireContext(), className)
//            unlockPin.launch(intent)
//        }
//    }
//
//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        Timber.d("1991 onDismiss")
//
//    }
//
//    private fun cancelClicked() {
//        binding.imageClose.setOnClickListener {
//            dismiss()
//            actionTransactionCallback?.dismissed(callbackId, false)
//        }
//        binding.buttonDecline.setOnClickListener {
//            dismiss()
//            actionTransactionCallback?.dismissed(callbackId, false)
//        }
//    }
//
//    private fun subscribeEventSignTransaction() {
//        val response = viewModel.signTransactionResult.asStateFlow()
//        if(response.value != null){
//            if(response.value?.result.isNullOrEmpty()){
//                actionTransactionCallback?.transactionError(callbackId, null)
//            }else{
////                actionTransactionCallback?.transactionSuccess(web3Transaction, response.value?.result ?: "")
//            }
//        }
//    }
//
//    private var actionTransactionCallback: ActionTransactionCallback? = null
//    fun setActionTransactionCallback(actionTransactionCallback: ActionTransactionCallback?) {
//        this.actionTransactionCallback = actionTransactionCallback
//    }
//
//    companion object {
//
//        private const val EXTRA_URL = "EXTRA_URL"
//        private const val EXTRA_WEB3_TRANSACTION = "EXTRA_WEB3_TRANSACTION"
//        private const val EXTRA_BALANCE = "EXTRA_BALANCE"
//        private const val EXTRA_COIN_DECIMAL = "EXTRA_COIN_DECIMAL"
//        private const val EXTRA_CHAIN_ID = "EXTRA_CHAIN_ID"
//
//        private fun newInstance(
//            url: String,
//            web3Transaction: Web3Transaction,
//            balance: String,
//            coinDecimals: Long,
//            chainId: Long
//        ) =
//            ConfirmTransactionBottomDialog().apply {
//                arguments = Bundle().apply {
//                    putString(EXTRA_URL, url)
//                    putParcelable(EXTRA_WEB3_TRANSACTION, web3Transaction)
//                    putString(EXTRA_BALANCE, balance)
//                    putLong(EXTRA_COIN_DECIMAL, coinDecimals)
//                    putLong(EXTRA_CHAIN_ID, chainId)
//                }
//            }
//
//        fun showDialogFragment(
//            fragmentManager: FragmentManager?,
//            url: String,
//            web3Transaction: Web3Transaction,
//            balance: String,
//            coinDecimals: Long,
//            chainId: Long
//        ): ConfirmTransactionBottomDialog {
//            val transaction = fragmentManager?.beginTransaction()
//            val previous =
//                fragmentManager?.findFragmentByTag("ConfirmTransactionBottomDialog")
//            if (previous != null) {
//                transaction?.remove(previous)
//            }
//            transaction?.addToBackStack(null)
//            val dialogFragment = newInstance(url, web3Transaction, balance, coinDecimals, chainId)
//            transaction?.let {
//                dialogFragment.show(
//                    transaction!!,
//                    "ConfirmTransactionBottomDialog"
//                )
//            }
//            return dialogFragment
//        }
//    }
//}

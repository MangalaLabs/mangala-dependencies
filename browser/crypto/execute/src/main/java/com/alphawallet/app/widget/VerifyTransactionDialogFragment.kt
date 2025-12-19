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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.preference.PreferenceManager
import com.alphawallet.app.R
import com.alphawallet.app.databinding.DialogVerifyTransactionBinding
import com.alphawallet.app.entity.tokens.Token
import com.alphawallet.app.repository.SharedPreferenceRepository
import com.alphawallet.app.repository.entity.Realm1559Gas
import com.alphawallet.app.service.TokensService
import com.alphawallet.app.ui.widget.entity.ActionSheetCallback
import com.alphawallet.app.ui.widget.entity.GasWidgetInterface
import com.alphawallet.app.web3.entity.Web3Transaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.schoolonair.wallet.component.views.BaseBottomSheetDialogFragment

class VerifyTransactionDialogFragment : BaseBottomSheetDialogFragment() {

    private var _binding: DialogVerifyTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogVerifyTransactionBinding.inflate(inflater, container, false)
        lifecycle.addObserver(lifecycleEventObserver)
        return binding.root
    }

    private val lifecycleEventObserver = LifecycleEventObserver { source, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {

            }
            Lifecycle.Event.ON_CREATE -> {
                setUpToken()
            }
            Lifecycle.Event.ON_PAUSE -> {

            }
            else -> {}
        }
    }

    private fun setUpToken() {
        binding.networkIcon.bindData(1L)
    }

    fun setUpData(
        tx: Web3Transaction,
        token: Token?,
        destName: String?,
        destAddress: String?,
        ts: TokensService,
        aCallBack: ActionSheetCallback?,
        url: String?
    ) {
        if (isAdded) {
            var dappName = url?.replace("https://", "")?.replace("http://", "") ?: ""
            if(dappName.contains("/")){
                dappName = dappName.split("/")[0]
            }
            binding.textTitle.text = dappName
            binding.networkIcon.bindData(token?.tokenInfo?.chainId ?: 1L)
        }
    }

//    private var use1559Transactions = false
//    private fun setupGasWidget(): GasWidgetInterface {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        val canUse1559Transactions = prefs.getBoolean(SharedPreferenceRepository.EXPERIMENTAL_1559_TX, false)
//        use1559Transactions =
//            (canUse1559Transactions && has1559Gas() //1559 Transactions toggled on in settings and this chain supports 1559
//                    && !(token!!.isEthereum && candidateTransaction!!.leafPosition == -2L) //User not sweeping wallet (if so we need to use legacy tx)
//                    && !tokensService!!.hasLockedGas(token.tokenInfo.chainId) //Service has locked gas, can only use legacy (eg Optimism).
//                    && !candidateTransaction!!.isConstructor) //Currently cannot use EIP1559 for constructors due to gas calculation issues
//        return if (use1559Transactions) {
//            binding.gasWidget.setupWidget(
//                tokensService,
//                token,
//                candidateTransaction,
//                actionSheetCallback!!.gasSelectLauncher()
//            )
//            binding.gasWidget
//        } else {
//            binding.gasWidget.visibility = View.GONE
//            binding.gasWidgetLegacy.visibility = View.GONE
//            binding.gasWidgetLegacy.setupWidget(
//                tokensService,
//                token,
//                candidateTransaction,
//                this,
//                actionSheetCallback!!.gasSelectLauncher()
//            )
//            binding.gasWidgetLegacy
//        }
//    }
//
//    private fun has1559Gas(tokensService: TokensService?, chainId: Long): Boolean {
//        try {
//            tokensService?.tickerRealmInstance.use { realm ->
//                val rgs = realm?.where(Realm1559Gas::class.java)
//                    .equalTo("chainId", token!!.tokenInfo.chainId)
//                    .findFirst()
//                if (rgs != null) {
//                    return true
//                }
//            }
//        } catch (e: Exception) {
//            //
//        }
//        return false
//    }

    companion object {

        private fun newInstance() =
            VerifyTransactionDialogFragment().apply {
                arguments = Bundle().apply {
//                    putString(EXTRA_STORAGE_UNIT_ID, storageUnitId)
                }
            }

        fun showDialogFragment(
            fragmentManager: FragmentManager?
        ): VerifyTransactionDialogFragment {
            val transaction = fragmentManager?.beginTransaction()
            val previous =
                fragmentManager?.findFragmentByTag("VerifyTransactionDialogFragment")
            if (previous != null) {
                transaction?.remove(previous)
            }
            transaction?.addToBackStack(null)
            val dialogFragment = newInstance()
            transaction?.let {
                dialogFragment.show(
                    transaction!!,
                    "VerifyTransactionDialogFragment"
                )
            }
            return dialogFragment
        }
    }

}

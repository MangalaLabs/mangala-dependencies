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
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.alphawallet.app.web3.entity.Web3Transaction
//import com.alphawallet.token.tools.Numeric
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
//import com.ionspin.kotlin.bignum.integer.BigInteger
//import com.mangala.wallet.core.address.domain.usecases.DeriveEthereumAddressUseCase
//import com.mangala.wallet.core.hdwallet.domain.model.HDKey
//import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
//import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
//import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
//import com.mangala.wallet.features.chains.erc20.contract.TransferMethod
//import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
//import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.FeeHistoryDto
//import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.FeeHistoryModel
//import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
//import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetFeeHistoryUseCase
//import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendTokenUseCase
//import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignTransactionUseCase
//import com.mangala.wallet.features.chains.evmcompatible.model.Address
//import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
//import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
//import com.mangala.wallet.features.chains.evmcompatible.model.TransactionDataResponse
//import com.mangala.wallet.model.blockchain.AddressType
//import com.mangala.wallet.model.blockchain.Blockchain
//import com.mangala.wallet.model.blockchain.BlockchainType
//import com.mangala.wallet.model.blockchain.Chain
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.launch
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//import timber.log.Timber
//import java.util.concurrent.atomic.AtomicInteger
//import kotlin.random.Random
//
//class ConfirmTransactionViewModel: ViewModel(), KoinComponent {
//
//    private val getFeeHistoryUseCase: GetFeeHistoryUseCase by inject()
//    private val signTransactionUseCase: SignTransactionUseCase by inject()
//    private val estimateGasUseCase: EstimateGasUseCase by inject()
//    private val generateHDKeyUseCase: GenerateHDKeyUseCase by inject()
//    private val deriveAddressUseCase: DeriveEthereumAddressUseCase by inject()
//
//    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase by inject()
//
//    private val getSelectedWalletUseCase: GetSelectedWalletUseCase by inject()
//
//    val blockchainType = BlockchainType.BinanceSmartChain
//    val chain = Chain.BinanceSmartChain
//    val rpcUrl = blockchainType.getRpcUrl().first()
//
//    private var gasPrice: GasPrice = GasPrice.Legacy(20_000_000_000)
//
//    val json : Gson = GsonBuilder().create()
//
//    private val currentId = AtomicInteger(Random.nextInt(100))
//
//    private var hdKey: HDKey? = null
//
//    private val _getFeeHistory = MutableStateFlow<FeeHistoryModel?>(null)
//    val getFeeHistory = _getFeeHistory
//
//    fun getFeeHistory() {
//        viewModelScope.launch {
//            val data = getFeeHistoryUseCase.invoke(rpcUrl, currentId.getAndIncrement())
//            try {
//                val feeHistoryDto = json.fromJson(data, FeeHistoryDto::class.java)
//                val feeHistoryModel = feeHistoryDto?.result?.mapToDomainModel()
//                feeHistoryModel?.let {
//                    handleFeeHistory(it)
//                }
//                _getFeeHistory.value = feeHistoryModel
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun handleFeeHistory(feeHistory: FeeHistoryModel) {
//        var recommendedBaseFee: Long? = null
//        var recommendedPriorityFee: Long? = null
//
//        feeHistory.baseFeePerGas.lastOrNull()?.let { currentBaseFee ->
//            recommendedBaseFee = currentBaseFee
//        }
//
//        var priorityFeeSum: Long = 0
//        var priorityFeesCount = 0
//        feeHistory.reward.forEach { priorityFeeArray ->
//            priorityFeeArray.firstOrNull()?.let { priorityFee ->
//                priorityFeeSum += priorityFee
//                priorityFeesCount += 1
//            }
//        }
//
//        if (priorityFeesCount > 0) {
//            recommendedPriorityFee = priorityFeeSum / priorityFeesCount
//        }
//
//        recommendedBaseFee?.let { baseFee ->
//            recommendedPriorityFee?.let { tip ->
//                gasPrice = if (chain.isEIP1559Supported) {
//                    GasPrice.Eip1559(
//                        maxFeePerGas = baseFee + tip,
//                        maxPriorityFeePerGas = tip,
//                        baseFee = baseFee
//                    )
//                } else {
//                    GasPrice.Legacy(baseFee + tip)
//                }
//
//                println("set gasPrice: $gasPrice")
//            }
//
//        }
//    }
//
//    private val _estimateGas = MutableStateFlow<Long?>(null)
//    val estimateGas = _estimateGas
//    fun estimateGas(web3Transaction: Web3Transaction, amount: String) {
//        restrictHDKey()
//        viewModelScope.launch {
//            val transactionData = buildPayloadTransactionData(
//                Address(web3Transaction.recipient.toString()),
//                Numeric.hexStringToByteArray(web3Transaction.payload)
//            )
//
//            val data = estimateGasUseCase.invoke(
//                rpcUrl,
//                currentId.getAndIncrement(),
//                getAddress(hdKey!!.publicKey),
//                Address(web3Transaction?.recipient.toString()),
//                amount.amountToBigInt(),
//                gasPrice,
//                transactionData
//            )
//            Timber.d("1991 estimateGas " + data)
//            estimateGas.value = data
//        }
//    }
//
//    private fun buildPayloadTransactionData(
//        contractAddress: Address,
//        input: ByteArray
//    ): TransactionData {
//        return TransactionData(
//            to = contractAddress,
//            value = BigInteger.ZERO,
//            input = input
//        )
//    }
//
//    private val _signTransaction = MutableStateFlow<TransactionDataResponse?>(null)
//    val signTransactionResult = _signTransaction
//
//    fun signTransaction(web3Transaction: Web3Transaction, amount: String) {
//        viewModelScope.launch {
//            val data = signTransactionUseCase.invoke(
//                hdKey!!,
//                chain,
//                web3Transaction.isLegacyTransaction,
//                getAddress(hdKey!!.publicKey),
//                web3Transaction.recipient.toString(),
//                amount,
//                Numeric.hexStringToByteArray(web3Transaction.payload),
//                gasPrice,
//                estimateGas.value,
//                rpcUrl,
//                web3Transaction.nonce
//            )
//            Timber.d("1991 signTransaction " + data)
//            try {
//                val result = json.fromJson(data, TransactionDataResponse::class.java)
//                _signTransaction.value = result
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _signTransaction.value = TransactionDataResponse(null, null, null)
//            }
//
//        }
//    }
//
////    fun getHDKey(): HDKey {
////        val defaultsWords = "level violin bridge knife salt provide indicate aim siren love output pause"
////        val words = defaultsWords.split(" ")
////        val hdKey = generateHDKeyUseCase.invoke(words,"", Blockchain(blockchainType, blockchainType.uid, ""), AddressType.Bip44)
////        return hdKey
////    }
//
//
//    fun restrictHDKey() {
//        val wallet = getSelectedWalletUseCase()
//        val words = wallet?.words?.split(" ")
//        hdKey = generateHDKeyUseCase.invoke(
//            words ?: listOf(),
//            "",
//            Blockchain(blockchainType, blockchainType.uid, ""),
//            AddressType.Bip44
//        )
//    }
//
//    fun getAddress(publicKey: ByteArray): Address {
//        return Address(deriveAddressUseCase.invoke(publicKey))
//    }
//}

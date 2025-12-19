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

package com.schoolonair.wallet.local.walletconnect

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Date

@Entity(primaryKeys = ["signTime"])
data class WCSignElement( val remotePeerId: String?,
                          @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
                          val signMessage: ByteArray?,
                          val signTime: Date = Date(),
                          val signType: String?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WCSignElement

        if (remotePeerId != other.remotePeerId) return false
        if (signMessage != null) {
            if (other.signMessage == null) return false
            if (!signMessage.contentEquals(other.signMessage)) return false
        } else if (other.signMessage != null) return false
        if (signTime != other.signTime) return false
        if (signType != other.signType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = remotePeerId?.hashCode() ?: 0
        result = 31 * result + (signMessage?.contentHashCode() ?: 0)
        result = 31 * result + signTime.hashCode()
        result = 31 * result + (signType?.hashCode() ?: 0)
        return result
    }
}

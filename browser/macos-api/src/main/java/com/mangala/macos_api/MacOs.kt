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

package com.mangala.macos_api

/** Public interface for MacOs wait lsit */
interface MacOsWaitlist {
    /**
     * This method returns the current state of the user in the MacOs waitlist
     * @return a [MacWaitlistState]
     */
    fun getWaitlistState(): MacWaitlistState
}

/** Public data class for MacOs wait list */
sealed class MacWaitlistState {
    object NotJoinedQueue : MacWaitlistState()
    object JoinedWaitlist : MacWaitlistState()
    object InBeta : MacWaitlistState()
}

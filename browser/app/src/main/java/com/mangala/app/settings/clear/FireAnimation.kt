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

package com.mangala.app.settings.clear

import com.mangala.app.statistics.pixels.Pixel
import com.schoolonair.wallet.browser.app.R
import java.io.Serializable

sealed class FireAnimation(
    val resId: Int,
    val nameResId: Int
) : Serializable {
    object HeroFire : FireAnimation(R.raw.hero_fire_inferno, com.schoolonair.wallet.component.resources.R.string.settingsHeroFireAnimation)
    object HeroWater : FireAnimation(R.raw.hero_water_whirlpool, com.schoolonair.wallet.component.resources.R.string.settingsHeroWaterAnimation)
    object HeroAbstract : FireAnimation(R.raw.hero_abstract_airstream, com.schoolonair.wallet.component.resources.R.string.settingsHeroAbstractAnimation)
    object None : FireAnimation(-1, com.schoolonair.wallet.component.resources.R.string.settingsNoneAnimation)
}

fun FireAnimation.getPixelValue() = when (this) {
    FireAnimation.HeroFire -> Pixel.PixelValues.FIRE_ANIMATION_INFERNO
    FireAnimation.HeroWater -> Pixel.PixelValues.FIRE_ANIMATION_WHIRLPOOL
    FireAnimation.HeroAbstract -> Pixel.PixelValues.FIRE_ANIMATION_AIRSTREAM
    FireAnimation.None -> Pixel.PixelValues.FIRE_ANIMATION_NONE
}

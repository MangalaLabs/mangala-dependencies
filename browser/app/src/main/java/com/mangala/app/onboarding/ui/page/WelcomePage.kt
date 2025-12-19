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

package com.mangala.app.onboarding.ui.page

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mangala.app.global.view.TypeAnimationTextView
import com.mangala.app.global.view.html
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.google.android.material.button.MaterialButton
import com.schoolonair.wallet.browser.app.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@ExperimentalCoroutinesApi
class WelcomePage : OnboardingPageFragment() {

    val viewModelFactory: WelcomePageViewModelFactory by inject()

    val appBuildConfig: AppBuildConfig by inject()

    private var ctaText: String = ""
    private var welcomeAnimation: ViewPropertyAnimatorCompat? = null
    private var typingAnimation: ViewPropertyAnimatorCompat? = null
    private var welcomeAnimationFinished = false

    // we use a BroadcastChannel because we don't want to emit the last value upon subscription
    private val events = MutableSharedFlow<WelcomePageView.Event>()

    private val welcomePageViewModel: WelcomePageViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(WelcomePageViewModel::class.java)
    }

    override fun layoutResource(): Int = R.layout.content_onboarding_welcome

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        configureDaxCta()
        scheduleWelcomeAnimation()
        setSkipAnimationListener()

        lifecycleScope.launch {
            events
                .flatMapLatest { welcomePageViewModel.reduce(it) }
                .collect(::render)
        }
    }

    private fun render(state: WelcomePageView.State) {
        when (state) {
            WelcomePageView.State.Idle -> {}
            is WelcomePageView.State.ShowDefaultBrowserDialog -> {
                showDefaultBrowserDialog(state.intent)
            }
            WelcomePageView.State.Finish -> {
                onContinuePressed()
            }
        }
    }

    private fun event(event: WelcomePageView.Event) {
        lifecycleScope.launch {
            events.emit(event)
        }
    }

    private fun showDefaultBrowserDialog(intent: Intent) {
        startActivityForResult(intent, DEFAULT_BROWSER_ROLE_MANAGER_DIALOG)
    }

    override fun onResume() {
        super.onResume()
        applyFullScreenFlags()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        welcomeAnimation?.cancel()
        typingAnimation?.cancel()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == DEFAULT_BROWSER_ROLE_MANAGER_DIALOG) {
            if (resultCode == RESULT_OK) {
                event(WelcomePageView.Event.OnDefaultBrowserSet)
            } else {
                event(WelcomePageView.Event.OnDefaultBrowserNotSet)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun applyFullScreenFlags() {
        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility += View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
        ViewCompat.requestApplyInsets(view?.findViewById<ConstraintLayout>(R.id.longDescriptionContainer) ?: View(requireContext()))
    }

    private fun configureDaxCta() {
        context?.let {
            ctaText = it.getString(com.schoolonair.wallet.component.resources.R.string.onboardingDaxText)
            view?.findViewById<TextView>(R.id.hiddenTextCta)?.text = ctaText.html(it)
            view?.findViewById<TypeAnimationTextView>(R.id.dialogTextCta)?.textInDialog = ctaText.html(it)
            view?.findViewById<TypeAnimationTextView>(R.id.dialogTextCta)?.setTextColor(ContextCompat.getColor(it, com.schoolonair.wallet.component.resources.R.color.grayishBrown))
            view?.findViewById<CardView>(R.id.cardView)?.backgroundTintList = ContextCompat.getColorStateList(it, com.schoolonair.wallet.component.resources.R.color.white)
        }
        view?.findViewById<AppCompatImageView>(R.id.triangle)?.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_triangle_bubble_white)
    }

    private fun setSkipAnimationListener() {
        view?.findViewById<ConstraintLayout>(R.id.longDescriptionContainer)?.setOnClickListener {
            if (view?.findViewById<TypeAnimationTextView>(R.id.dialogTextCta)?.hasAnimationStarted() == true) {
                finishTypingAnimation()
            } else if (!welcomeAnimationFinished) {
                welcomeAnimation?.cancel()
                scheduleWelcomeAnimation(0L)
            }
            welcomeAnimationFinished = true
        }
    }

    private fun scheduleWelcomeAnimation(startDelay: Long = ANIMATION_DELAY) {
        welcomeAnimation = ViewCompat.animate(view?.findViewById<View>(R.id.welcomeContent) as View)
            .alpha(MIN_ALPHA)
            .setDuration(ANIMATION_DURATION)
            .setStartDelay(startDelay)
            .withEndAction {
                typingAnimation = ViewCompat.animate(view?.findViewById<ConstraintLayout>(R.id.daxCtaContainer) ?: View(requireContext()))
                    .alpha(MAX_ALPHA)
                    .setDuration(ANIMATION_DURATION)
                    .withEndAction {
                        welcomeAnimationFinished = true
                        view?.findViewById<TypeAnimationTextView>(R.id.dialogTextCta)?.startTypingAnimation(ctaText)
                        setPrimaryCtaListenerAfterWelcomeAlphaAnimation()
                    }
            }
    }

    private fun finishTypingAnimation() {
        welcomeAnimation?.cancel()
        view?.findViewById<TypeAnimationTextView>(R.id.dialogTextCta)?.finishAnimation()
        setPrimaryCtaListenerAfterWelcomeAlphaAnimation()
    }

    private fun setPrimaryCtaListenerAfterWelcomeAlphaAnimation() {
        view?.findViewById<MaterialButton>(R.id.primaryCta)?.setOnClickListener { event(
            WelcomePageView.Event.OnPrimaryCtaClicked) }
    }

    companion object {
        private const val MIN_ALPHA = 0f
        private const val MAX_ALPHA = 1f
        private const val ANIMATION_DURATION = 400L
        private const val ANIMATION_DELAY = 1400L

        private const val DEFAULT_BROWSER_ROLE_MANAGER_DIALOG = 101
    }
}

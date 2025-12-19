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

package com.mangala.app.browser.menu

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.isVisible
import com.mangala.app.browser.BrowserTabViewModel
import com.mangala.mobile.android.ui.menu.PopupMenu
import com.mangala.mobile.android.ui.view.MenuItemView
import com.mangala.mobile.android.ui.view.SectionDivider
import com.mangala.mobile.android.R.dimen
import com.schoolonair.wallet.browser.app.R

class BrowserPopupMenu(
    context: Context,
    layoutInflater: LayoutInflater
) : PopupMenu(
    layoutInflater,
    resourceId = R.layout.popup_window_browser_menu,
    width = getPopupMenuWidth(context)
) {

    fun renderState(
        browserShowing: Boolean,
        viewState: BrowserTabViewModel.BrowserViewState
    ) {
        contentView.apply {
            findViewById<ImageButton>(R.id.backMenuItem).isEnabled = viewState.canGoBack
            findViewById<ImageButton>(R.id.forwardMenuItem).isEnabled = viewState.canGoForward
            findViewById<ImageButton>(R.id.refreshMenuItem).isEnabled = browserShowing
            findViewById<MenuItemView>(R.id.printPageMenuItem).isEnabled = browserShowing

            findViewById<MenuItemView>(R.id.newTabMenuItem).isVisible = browserShowing
            findViewById<MenuItemView>(R.id.sharePageMenuItem)?.isVisible = viewState.canSharePage
            findViewById<MenuItemView>(R.id.addBookmarksMenuItem)?.isVisible = viewState.canSaveSite
            val isBookmark = viewState.bookmark != null
            findViewById<MenuItemView>(R.id.addBookmarksMenuItem)?.label {
                context.getString(if (isBookmark) com.schoolonair.wallet.component.resources.R.string.editBookmarkMenuTitle else com.schoolonair.wallet.component.resources.R.string.addBookmarkMenuTitle)
            }
            findViewById<MenuItemView>(R.id.addBookmarksMenuItem)?.setIcon(if (isBookmark) com.schoolonair.wallet.component.resources.R.drawable.ic_bookmark_solid_16 else com.schoolonair.wallet.component.resources.R.drawable.ic_bookmark_16)

            val isFavorite = viewState.favorite != null
            findViewById<MenuItemView>(R.id.addFavoriteMenuItem)?.isVisible = viewState.addFavorite.isEnabled()
            findViewById<MenuItemView>(R.id.addFavoriteMenuItem).label {
                when {
                    viewState.addFavorite.isHighlighted() -> context.getString(com.schoolonair.wallet.component.resources.R.string.addFavoriteMenuTitleHighlighted)
                    isFavorite -> context.getString(com.schoolonair.wallet.component.resources.R.string.removeFavoriteMenuTitle)
                    else -> context.getString(com.schoolonair.wallet.component.resources.R.string.addFavoriteMenuTitle)
                }
            }
            findViewById<MenuItemView>(R.id.addFavoriteMenuItem).setIcon(if (isFavorite) com.schoolonair.wallet.component.resources.R.drawable.ic_favorite_solid_16 else com.schoolonair.wallet.component.resources.R.drawable.ic_favorite_16)

            findViewById<MenuItemView>(R.id.fireproofWebsiteMenuItem)?.isVisible = viewState.canFireproofSite
            findViewById<MenuItemView>(R.id.fireproofWebsiteMenuItem)?.label {
                context.getString(
                    if (viewState.isFireproofWebsite) {
                        com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteMenuTitleRemove
                    } else {
                        com.schoolonair.wallet.component.resources.R.string.fireproofWebsiteMenuTitleAdd
                    }
                )
            }
            findViewById<MenuItemView>(R.id.fireproofWebsiteMenuItem)?.setIcon(if (viewState.isFireproofWebsite) com.schoolonair.wallet.component.resources.R.drawable.ic_fire_16 else com.schoolonair.wallet.component.resources.R.drawable.ic_fireproofed_16)

            findViewById<MenuItemView>(R.id.createAliasMenuItem)?.isVisible = viewState.isEmailSignedIn

            findViewById<MenuItemView>(R.id.changeBrowserModeMenuItem)?.isVisible = viewState.canChangeBrowsingMode
            findViewById<MenuItemView>(R.id.changeBrowserModeMenuItem).label {
                context.getString(
                    if (viewState.isDesktopBrowsingMode) {
                        com.schoolonair.wallet.component.resources.R.string.requestMobileSiteMenuTitle
                    } else {
                        com.schoolonair.wallet.component.resources.R.string.requestDesktopSiteMenuTitle
                    }
                )
            }
            findViewById<MenuItemView>(R.id.changeBrowserModeMenuItem)?.setIcon(
                if (viewState.isDesktopBrowsingMode) com.schoolonair.wallet.component.resources.R.drawable.ic_device_mobile_16 else com.schoolonair.wallet.component.resources.R.drawable.ic_device_desktop_16
            )

            findViewById<MenuItemView>(R.id.openInAppMenuItem).isVisible = viewState.previousAppLink != null
            findViewById<MenuItemView>(R.id.findInPageMenuItem).isVisible = viewState.canFindInPage
//            findViewById<MenuItemView>(R.id.addToHomeMenuItem).isVisible = viewState.addToHomeVisible && viewState.addToHomeEnabled
            findViewById<MenuItemView>(R.id.privacyProtectionMenuItem)?.isVisible = viewState.canChangePrivacyProtection
            findViewById<MenuItemView>(R.id.privacyProtectionMenuItem)?.label {
                context.getText(
                    if (viewState.isPrivacyProtectionEnabled) {
                        com.schoolonair.wallet.component.resources.R.string.enablePrivacyProtection
                    } else {
                        com.schoolonair.wallet.component.resources.R.string.disablePrivacyProtection
                    }
                ).toString()
            }
            findViewById<MenuItemView>(R.id.privacyProtectionMenuItem)?.setIcon(
                if (viewState.isPrivacyProtectionEnabled) com.schoolonair.wallet.component.resources.R.drawable.ic_protections_16 else com.schoolonair.wallet.component.resources.R.drawable.ic_protections_blocked_16
            )
            findViewById<MenuItemView>(R.id.brokenSiteMenuItem)?.isVisible = viewState.canReportSite

            findViewById<SectionDivider>(R.id.siteOptionsMenuDivider).isVisible = viewState.browserShowing
            findViewById<SectionDivider>(R.id.browserOptionsMenuDivider).isVisible = viewState.browserShowing
            findViewById<SectionDivider>(R.id.settingsMenuDivider).isVisible = viewState.browserShowing
            findViewById<MenuItemView>(R.id.printPageMenuItem)?.isVisible = viewState.canPrintPage
        }
    }
}

private fun getPopupMenuWidth(context: Context): Int {
    val orientation = context.resources.configuration.orientation
    return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        ViewGroup.LayoutParams.WRAP_CONTENT
    } else {
        context.resources.getDimensionPixelSize(dimen.popupMenuWidth)
    }
}

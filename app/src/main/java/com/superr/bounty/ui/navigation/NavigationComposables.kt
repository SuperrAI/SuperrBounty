package com.superr.bounty.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.superr.bounty.R
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.fdp
import com.superr.bounty.utils.flatClickable

private const val TAG = "Superr.NavigationComposables"

@Composable
fun SideNavigation(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit,
    isLoggedIn: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .drawBehind {
                drawLine(
                    Color.Black,
                    Offset(size.width - 2, 0f),
                    Offset(size.width - 2, size.height),
                    2f,
                )
            }
            .border(0.fdp, SuperrTheme.colorScheme.Black)
            .padding(top = 32.fdp, bottom = 24.fdp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.fdp)) {
            NavigationItem(
                icon = R.drawable.ic_superr_icon,
                route = "NONE",
                selectedRoute = selectedRoute,
            )
            NavigationItem(
                icon = R.drawable.ic_superr_icon,
                route = "NONE",
                selectedRoute = selectedRoute,
                isVisible = false
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.fdp)) {
            NavigationItem(
                icon = R.drawable.ic_three_pin_notebook,
                route = "",
                selectedRoute = selectedRoute,
                onItemClick = { },
                isVisible = true
            )
            NavigationItem(
                icon = R.drawable.ic_scholar_hat,
                route = "",
                selectedRoute = selectedRoute,
                onItemClick = { },
                isVisible = true
            )
            NavigationItem(
                icon = R.drawable.ic_file_drawer,
                route = "",
                selectedRoute = selectedRoute,
                onItemClick = { },
                isVisible = true
            )
            NavigationItem(
                icon = R.drawable.ic_magnifying_glass,
                route = "NONE",
                selectedRoute = selectedRoute,
                onItemClick = { },
                isVisible = true
            )
            NavigationItem(
                icon = R.drawable.ic_user_id_badge,
                route = "",
                selectedRoute = selectedRoute,
                onItemClick = { },
                isVisible = true
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.fdp)) {
            MiniNavigationItem(
                icon = R.drawable.ic_wifi,
                onItemClick = {}
            )
            MiniNavigationItem(
                icon = R.drawable.ic_battery_medium,
                onItemClick = {}
            )
            MiniNavigationItem(
                icon = R.drawable.ic_settings_slider,
                onItemClick = {}
            )
        }
    }
}

@Composable
fun NavigationItem(
    icon: Int,
    route: String,
    selectedRoute: String,
    onItemClick: () -> Unit = {},
    isVisible: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(88.fdp, 48.fdp)
            .border(0.dp, SuperrTheme.colorScheme.White)
            .flatClickable(
                onClick = onItemClick
            )
            .alpha(if (isVisible) 1f else 0f)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Navigation Icon",
            modifier = Modifier
                .size(48.fdp)
                .align(Alignment.Center),
        )
        if (isRouteSelected(selectedRoute, route)) {
            Box(
                modifier = Modifier
                    .size(4.fdp, 48.fdp)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(100.fdp))
                    .background(SuperrTheme.colorScheme.Black)
            )
        }
    }
}

@Composable
fun MiniNavigationItem(
    icon: Int,
    onItemClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(88.fdp, 28.fdp)
            .border(0.dp, SuperrTheme.colorScheme.White)
            .flatClickable { onItemClick() }
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Navigation Icon",
            modifier = Modifier
                .size(28.fdp)
                .align(Alignment.Center),
        )
    }
}

private fun isRouteSelected(selectedRoute: String, itemRoute: String): Boolean {
    return selectedRoute != itemRoute
}
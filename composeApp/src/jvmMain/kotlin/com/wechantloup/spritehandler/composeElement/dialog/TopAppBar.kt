package com.wechantloup.spritehandler.composeElement.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBar(
    titleRes: StringResource,
    navigationIcon: ImageVector,
    navigationIconDescriptionRes: StringResource?,
    onNavigationPressed: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(stringResource(titleRes)) },
        navigationIcon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNavigationPressed() },
            ) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconDescriptionRes?.let { stringResource(it) },
                )
            }
        },
        actions = actions,
    )
}

package com.wechantloup.spritehandler

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
internal fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = MAIN_SCREEN, modifier) {
        composable(MAIN_SCREEN) {
            MainScreen(
                showSpriteCreation = { navController.navigate(SPRITE_CREATION_SCEEN) },
                showAnimCreation = {},
            )
        }
        composable(SPRITE_CREATION_SCEEN) {
            TODO()
        }
        composable(ANIM_CREATION_SCREEN) {
            TODO()
        }
    }
}

internal const val MAIN_SCREEN = "main_screen"
internal const val SPRITE_CREATION_SCEEN = "sprite_creation_sceen"
internal const val ANIM_CREATION_SCREEN = "anim_creation_screen"

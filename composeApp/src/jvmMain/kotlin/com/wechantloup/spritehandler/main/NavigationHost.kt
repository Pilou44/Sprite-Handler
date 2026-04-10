package com.wechantloup.spritehandler.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wechantloup.spritehandler.animCreation.AnimCreationScreen
import com.wechantloup.spritehandler.spriteCreation.SpriteCreationViewModel
import com.wechantloup.spritehandler.spriteCreation.SpriteCreationScreen

internal const val MAIN_SCREEN = "main_screen"
internal const val SPRITE_CREATION_SCEEN = "sprite_creation_sceen"
internal const val ANIM_CREATION_SCREEN = "anim_creation_screen"

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
            val viewModel = getSpriteChooserViewModel()
            SpriteCreationScreen(
                viewModel = viewModel,
                back = navController::popBackStack,
            )
        }
        composable(ANIM_CREATION_SCREEN) {
            AnimCreationScreen()
        }
    }
}

@Composable
private fun getSpriteChooserViewModel(): SpriteCreationViewModel {
    val owner = checkNotNull(LocalViewModelStoreOwner.current)
    return viewModel<SpriteCreationViewModel>(
        viewModelStoreOwner = owner,
        factory = SpriteCreationViewModel.Factory()
    )
}

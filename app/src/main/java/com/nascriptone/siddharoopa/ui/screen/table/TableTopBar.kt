package com.nascriptone.siddharoopa.ui.screen.table

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.ui.screen.Routes
import com.nascriptone.siddharoopa.utils.extensions.sharedViewModelOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreenTopBar(
    fromSelectionMode: Boolean,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val tableViewModel: TableViewModel? =
        navHostController.sharedViewModelOrNull("${Routes.Table.withRoot}/{id}?sm={sm}")
    if (tableViewModel == null) return

    val sabda by tableViewModel.sabda.collectAsStateWithLifecycle()

    TopAppBar(
        title = {
            Text("Declension")
        },
        navigationIcon = {
            CustomToolTip("Back") {
                IconButton(onClick = { navHostController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                }
            }
        },
        actions = {
            if (!fromSelectionMode && sabda != null) {
                var checked by rememberSaveable { mutableStateOf(sabda?.isFavorite ?: false) }
                CustomToolTip("Favorite") {
                    IconToggleButton(
                        checked = checked,
                        onCheckedChange = {
                            tableViewModel.toggleFavoriteSabda()
                            checked = it
                        }
                    ) {
                        if (checked) Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        else Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}
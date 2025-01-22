package com.ourcqspot.client.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NewsScreenContent() {
    // Fonctionnement identique à MapScreenContent.
    // Ce @Composable est intégré dans HomeNavGraph (dans NavHost {composable() { ... } })

    Column ( // Tu peux changer Column en Row, en Box, en LazyColumn, ...etc
        Modifier
            .fillMaxSize()
            .background(Color.Cyan) // À retirer
    ) {
        // -------------------
        // À remplacer par le contenu
        Box (
            Modifier
                .padding(15.dp)
                .fillMaxSize()
                .background(Color.Green)
        ) {

        }
        // -------------------
    }
}
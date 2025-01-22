package com.ourcqspot.client.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ourcqspot.client.R

@Composable
fun MapScreenContent() {
    Box (
        Modifier
            .clipToBounds() // forbids overflow
            .fillMaxSize(),
        Alignment.Center
    ) {
        Image(
            painterResource(R.drawable.map_default),
            contentDescription = "",
            Modifier
                .fillMaxSize()
                .scale(1.925F)
                .offset(x = (15).dp, y = 66.dp),
            contentScale = ContentScale.Crop
        )
        Image(
            painterResource(R.drawable.icon_user_position),
            contentDescription = "Vous êtes ici",
            Modifier
                .size(50.dp)
                .offset(x = (-133).dp, y = (-50).dp)
        )
    }
}
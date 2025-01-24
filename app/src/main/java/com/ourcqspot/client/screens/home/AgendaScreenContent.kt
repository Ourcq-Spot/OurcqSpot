package com.ourcqspot.client.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ourcqspot.client.R
import com.ourcqspot.client.ui.theme.NUNITO_FONT

@Composable
fun AgendaScreenContent() {
    Column (
        Modifier
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(bottom = 100.dp),
        Arrangement.spacedBy(40.dp),
    ) {
        Box(
            Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Image(
                painterResource(R.drawable.tmp_section_agenda),
                "Section progression",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text("Les événements de Janvier")
        Box(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Image(
                painterResource(R.drawable.tmp_item_avenir),
                "Section progression",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Box(
            Modifier
                .padding(horizontal = 50.dp)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Button(
                onClick = {},
                Modifier
                    .background(
                        Color(0xFF0E2176),
                        RoundedCornerShape(50.dp)
                    )
            ) {
                Text("Me prévenir lorsqu’un événement est ajouté",
                    fontFamily = NUNITO_FONT,
                    modifier = Modifier
                        .padding(vertical = 2.5.dp, horizontal = 10.dp)
                )
            }
        }
    }
}
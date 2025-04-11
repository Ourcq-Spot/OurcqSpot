package com.ourcqspot.client.screens.home

import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import android.util.Log
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import java.time.YearMonth
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.Alignment // Correct import pour 'Alignment'
import androidx.compose.ui.platform.LocalContext // Correct import pour 'LocalContext'
import android.widget.Toast // Correct import pour 'Toast'
import androidx.compose.foundation.lazy.grid.items

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.automirrored.filled.ArrowBack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgendaScreenContent()
        }
    }
}

@Composable
fun AgendaScreenContent() {
    var selectedEvent by remember { mutableStateOf<String?>(null) }

    MonthNavigator(selectedEvent = selectedEvent, onEventSelected = { event ->
        selectedEvent = event
    })

    selectedEvent?.let {
        showEvent(it)
    }
}

@Composable
fun MonthNavigator(selectedEvent: String?, onEventSelected: (String) -> Unit) {
    val months = listOf(
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Decembre"
    )

    var currentMonthIndex by remember { mutableStateOf(3) } // Avril (index 3)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Navigation entre les mois
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { if (currentMonthIndex > 0) currentMonthIndex-- },
                enabled = currentMonthIndex > 0
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }

            Text(text = "${months[currentMonthIndex]} 2025", fontSize = 20.sp)

            IconButton(
                onClick = { if (currentMonthIndex < months.size - 1) currentMonthIndex++ },
                enabled = currentMonthIndex < months.size - 1
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                Text(text = day, fontSize = 16.sp)
            }
        }

        DaysGrid(monthIndex = currentMonthIndex, onDayClick = { day ->
            if (currentMonthIndex == 3 && day == 27) { // Avril = index 3
                onEventSelected("Journée Portes Ouvertes au Parc de l'Ourcq")
            }
        })
    }
}

fun getDaysInMonth(monthIndex: Int): List<Int> {
    val yearMonth = YearMonth.of(2025, monthIndex + 1)
    return (1..yearMonth.lengthOfMonth()).toList()
}

@Composable
fun DaysGrid(monthIndex: Int, onDayClick: (Int) -> Unit) {
    val days = getDaysInMonth(monthIndex)
    val eventDays = listOf(27) // jour avec un événement codé en dur (27 avril)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(16.dp)
    ) {
        items(days) { day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable {
                        // Appel de la fonction onDayClick pour gérer l'affichage du Toast
                        onDayClick(day) // Appel à la fonction de gestion du jour
                    }
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 18.sp,
                    color = if (eventDays.contains(day)) Color.Red else Color.Black
                )
                if (eventDays.contains(day)) {
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .width(16.dp)
                            .background(Color.Red)
                    )
                }
            }
        }
    }
}

@Composable
fun showEvent(event: String) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Événement du jour") },
        text = { Text(event) },
        confirmButton = {
            TextButton(onClick = {}) {
                Text("OK")
            }
        }
    )
}

@Composable
fun onDayClick(day: Int) {
    // Affichage d'un Toast dans un LaunchedEffect pour exécuter un effet secondaire.
    val context = LocalContext.current

    LaunchedEffect(day) {
        Toast.makeText(context, "Jour cliqué : $day", Toast.LENGTH_SHORT).show()
    }
}

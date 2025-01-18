package com.ourcqspot.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.ourcqspot.client.graphs.HomeNavGraph
import com.ourcqspot.client.graphs.RootNavGraph
import com.ourcqspot.client.ui.theme.OurcqSpotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        /* WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false */
        setContent {
            OurcqSpotTheme {
                RootNavGraph(navController = rememberNavController())
                //HomeNavGraph(navController = rememberNavController())
            }
        }
        val ref = true // Did you find me?
    }

    @Preview(showBackground = true)
    @Composable
    fun RootElementPreview() {
        RootNavGraph()
    }
    @Preview(showBackground = true)
    @Composable
    fun HomeElementPreview() {
        HomeNavGraph()
    }
}
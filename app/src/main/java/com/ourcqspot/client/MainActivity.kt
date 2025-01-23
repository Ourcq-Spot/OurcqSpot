package com.ourcqspot.client

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.ourcqspot.client.graphs.HomeNavGraph
import com.ourcqspot.client.graphs.RootNavGraph
import com.ourcqspot.client.ui.theme.OurcqSpotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val keepSplashScreen = false // doit rester "true" tant que de la data doit être chargée (par exemple appel au serveur)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplashScreen // !viewModel.isReady.value
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 300L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 300L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
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
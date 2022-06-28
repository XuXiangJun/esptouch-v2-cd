// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import view.MainView
import view.MainViewModel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "EspTouchV2CD",
        icon = painterResource("esptouch.png"),
        state = WindowState(width = 800.dp, height = 700.dp)
    ) {
        MaterialTheme(colors) {
            App()
        }
    }
}

private val colors = Colors(
    primary = Color(0xFFFF53242),
    primaryVariant = Color(0xFFCE2E32),
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
    isLight = true
)

@Composable
@Preview
fun App() {
    val mainVM = MainViewModel()
    val mainView = MainView(mainVM)

    mainView.getWifiProgressContent()
    mainView.configContent()
    mainView.provisioningContent()
    mainView.updateNetworkInfo()
}

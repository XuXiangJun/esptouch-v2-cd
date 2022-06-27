// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.espressif.iot.esptouch2.provision.TouchNetUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.AppState
import view.MainViewModel
import view.MainView

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "EspTouchV2CD",
        icon = painterResource("esptouch.png"),
        state = WindowState(width = 800.dp, height = 700.dp)
    ) {
        App()
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

    MaterialTheme(colors) {
        mainView.getWifiProgressContent()
        mainView.configContent()
        mainView.provisioningContent()
    }

    MainScope().launch {
        val netInfo = withContext(Dispatchers.IO) {
            TouchNetUtil.getNetInfo()
        }

        mainVM.bssid.value = netInfo.hardBssid
        mainVM.localAddress.value = netInfo.localAddress.hostAddress
        mainVM.state.value = AppState.Config
    }
}

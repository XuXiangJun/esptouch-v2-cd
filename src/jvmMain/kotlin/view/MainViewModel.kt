package view

import androidx.compose.runtime.*
import model.AppState

class MainViewModel {
    val state = mutableStateOf(AppState.Init)

    val ssid = mutableStateOf("")
    val bssid = mutableStateOf("")
    val password = mutableStateOf("")
    val deviceCount = mutableStateOf("")
    val aesKey = mutableStateOf("")
    val customText = mutableStateOf("")
    val hintMessage = mutableStateOf("")

    val localAddress = mutableStateOf("")

    val provisioningProgress = mutableStateOf(true)
}

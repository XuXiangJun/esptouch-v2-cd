package view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.espressif.iot.esptouch2.provision.EspProvisioningRequest
import com.espressif.iot.esptouch2.provision.TouchNetUtil
import kotlinx.coroutines.MainScope
import localizable.Localizable
import model.AppState

class MainView(
    private val viewModel: MainViewModel
) {
    private val strings = Localizable.strings()

    private val scope = MainScope()

    private var deviceCount = -1

    @Composable
    fun getWifiProgressContent() {
        AnimatedVisibility(viewModel.state.value == AppState.Init) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )

                Text(
                    text = strings.gettingNetworkInfo,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
        }
    }

    @Composable
    fun configContent() {
        var ssid by remember { viewModel.ssid }
        var bssid by remember { viewModel.bssid }
        var password by remember { viewModel.password }
        var deviceCount by remember { viewModel.deviceCount }
        var aesKey by remember { viewModel.aesKey }
        var customText by remember { viewModel.customText }
        var hintMessage by remember { viewModel.hintMessage }

        val startClickListener = click@{
            val ssidData = if (ssid.isNotEmpty()) {
                ssid.toByteArray()
            } else {
                hintMessage = strings.ssidError
                return@click
            }
            val bssidData = try {
                TouchNetUtil.convertBssid2Bytes(bssid)
            } catch (ignore: Exception) {
                hintMessage = strings.bssidError
                return@click
            }
            val passwordData = password.toByteArray()
            this.deviceCount = try {
                if (deviceCount.isNotEmpty()) {
                    deviceCount.toInt()
                } else {
                    -1
                }
            } catch (ignore: Exception) {
                hintMessage = strings.deviceCountError
                return@click
            }
            val aesKeyData = aesKey.toByteArray()
            if (aesKeyData.isNotEmpty() && aesKeyData.size != 16) {
                hintMessage = strings.aesKeyError
                return@click
            }
            val customData = customText.toByteArray()

            viewModel.state.value = AppState.Provisioning
            viewModel.provisioningProgress.value = true
            val request = EspProvisioningRequest.Builder().apply {
                setSSID(ssidData)
                setBSSID(bssidData)
                setPassword(passwordData)
                if (aesKeyData.isNotEmpty()) {
                    setAESKey(aesKeyData)
                }
                if (customData.isNotEmpty()) {
                    setReservedData(customData)
                }
                setAddress(viewModel.localAddress.value)
            }.build()
//            startProvisioning(request)
        }

        AnimatedVisibility(viewModel.state.value == AppState.Config) {
            Box(
                modifier = Modifier.padding(16.dp)
                    .fillMaxSize()
            ) {
                Button(
                    onClick = startClickListener,
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(strings.startProvisioning)
                }

                Column(
                    modifier = Modifier.padding(
                        bottom = 96.dp
                    )
                        .fillMaxSize()
                ) {
                    OutlinedTextField(
                        value = ssid,
                        onValueChange = {
                            ssid = it
                            hintMessage = ""
                        },
                        label = { Text(strings.wifiSSIDLabel) },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = bssid,
                        onValueChange = {
                            bssid = it
                            hintMessage = ""
                        },
                        label = { Text(strings.wifiBSSIDLabel) },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            hintMessage = ""
                        },
                        label = { Text(strings.wifiPasswordLabel) },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = deviceCount,
                        onValueChange = {
                            println(it)
                            deviceCount = it.filter { it.code in '0'.code..'9'.code }
                            hintMessage = ""
                        },
                        label = { Text(strings.deviceCountLabel) },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = aesKey,
                        onValueChange = {
                            println("AES $it")
                            if (it.toByteArray().size <= 16) aesKey = it
                            hintMessage = ""
                        },
                        label = { Text(strings.aesKeyLabel) },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = customText,
                        onValueChange = {
                            if (it.toByteArray().size <= 64) customText = it
                            hintMessage = ""
                        },
                        label = { Text(strings.customDataLabel) },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                    )

                    Text(
                        text = hintMessage,
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        color = MaterialTheme.colors.error,
                    )
                } // Column
            } // Box


        } // AnimatedVisibility
    }

    @Composable
    fun provisioningContent() {
        var showProgress by remember { viewModel.provisioningProgress }

        AnimatedVisibility(
            visible = viewModel.state.value == AppState.Provisioning,
        ) {
            Box(
                modifier = Modifier.padding(16.dp)
                    .fillMaxSize()
            ) {
                Button(
                    onClick = {
                        showProgress = false
                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(strings.stopProvisioning)
                }

                Column(
                    modifier = Modifier.padding(
                        bottom = 64.dp
                    )
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource("arrow_back.png"),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp)
                            .padding(8.dp)
                            .clickable {
                                viewModel.state.value = AppState.Config
                                println("Prov set config")
                            }
                    )

                    AnimatedVisibility(
                        visible = showProgress,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .wrapContentSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.requiredSize(96.dp)
                                .padding(16.dp)
                        )
                    }
                } // Column
            } // Box
        } // AnimatedVisibility
    }
}

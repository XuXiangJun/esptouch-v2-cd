package view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.espressif.iot.esptouch2.provision.*
import kotlinx.coroutines.*
import localizable.Localizable
import model.AppState
import java.net.InetAddress
import kotlin.math.sin

class MainView(
    private val viewModel: MainViewModel
) {
    private val strings = Localizable.strings()

    private val scope = MainScope()

    private var deviceCount = -1

    private val provisioner = EspProvisioner()

    fun updateNetworkInfo() {
        scope.launch {
            val netInfo = withContext(Dispatchers.IO) {
                TouchNetUtil.getNetInfo()
            }

            viewModel.bssid.value = netInfo.hardBssid
            viewModel.localAddress.value = netInfo.localAddress.hostAddress
            viewModel.state.value = AppState.Config
        }
    }

    @Composable
    fun getWifiProgressContent() {
        println("getWifiProgressContent")
        val visible = viewModel.state.value == AppState.Init
        AnimatedVisibility(visible) {
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
        } // AnimatedVisibility
    }

    @Composable
    fun configContent() {
        println("configContent")
        var localIP by remember { viewModel.localAddress }
        var ssid by remember { viewModel.ssid }
        var bssid by remember { viewModel.bssid }
        var password by remember { viewModel.password }
        var deviceCount by remember { viewModel.deviceCount }
        var aesKey by remember { viewModel.aesKey }
        var customText by remember { viewModel.customText }
        var hintMessage by remember { viewModel.hintMessage }

        var startButtonEnabled by remember { mutableStateOf(true) }
        val startClickListener = click@{
            val localAddress = if (localIP.isNotEmpty()) {
                try {
                    InetAddress.getByName(localIP)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } else {
                null
            }
            if (localAddress == null) {
                hintMessage = strings.localAddressError
                return@click
            }

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

            startButtonEnabled = false
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
                setAddress(localAddress)
            }.build()

            scope.launch {
                withContext(Dispatchers.IO) {
                    stopSync()
                    while (true) {
                        delay(100)
                        if (!provisioner.isSyncing) {
                            break
                        }
                    }
                }

                startButtonEnabled = true
                viewModel.state.value = AppState.Provisioning
                startProvisioning(request)
            }
        }

        val visible = viewModel.state.value == AppState.Config
        AnimatedVisibility(visible) {
            Box(
                modifier = Modifier.padding(16.dp)
                    .fillMaxSize()
            ) {
                Button(
                    onClick = startClickListener,
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.BottomCenter),
                    enabled = startButtonEnabled
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
                        value = localIP,
                        onValueChange = {
                            localIP = it
                            hintMessage = ""
                        },
                        label = { Text(strings.localAddressLabel) },
                        singleLine = true,
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    )

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

        if (visible && !provisioner.isSyncing) {
            startSync()
        }
    }

    @Composable
    fun provisioningContent() {
        println("provisioningContent")
        val showProgress by remember { viewModel.provisioningProgress }
        var backEnable by remember { mutableStateOf(true) }
        val results = viewModel.provisioningResults
        results.clear()

        val visible = viewModel.state.value == AppState.Provisioning
        AnimatedVisibility(visible) {
            Box(
                modifier = Modifier.padding(16.dp)
                    .fillMaxSize()
            ) {
                Button(
                    onClick = {
                        stopProvisioning()
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
                            .clickable(enabled = backEnable) {
                                backEnable = false
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        stopProvisioning()
                                        while (provisioner.isProvisioning) {
                                            delay(100)
                                        }
                                    }

                                    backEnable = true
                                    viewModel.state.value = AppState.Config
                                }
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

                    Text(
                        text = strings.provisioningResultsLabel,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colors.primary
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .scrollable(
                                rememberScrollState(),
                                Orientation.Vertical
                            ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results) { result ->
                            Text(result.toString())
                        }
                    }
                } // Column
            } // Box
        } // AnimatedVisibility
    }

    private fun startSync() {
        println("StartSync")
        provisioner.startSync(object: EspSyncListener {
            override fun onStart() {
                println("StartSync onStart")
            }

            override fun onStop() {
                println("StartSync onStop")
            }

            override fun onError(e: Exception) {
                println("StartSync onError")
                e.printStackTrace()
                provisioner.stopSync()
            }

        })
    }

    private fun stopSync() {
        println("stopSync")
        if (provisioner.isSyncing) {
            provisioner.stopSync()
        }
    }

    private fun startProvisioning(request: EspProvisioningRequest) {
        println("startProvisioning")
        val deviceCount = try {
            viewModel.deviceCount.value.let {
                if (it.isNotEmpty()) it.toInt() else -1
            }
        } catch (e: Exception) {
            -1
        }
        val results = viewModel.provisioningResults
        provisioner.startProvisioning(request, object : EspProvisioningListener{
            override fun onStart() {
                println("startProvisioning onStart")
            }

            override fun onResponse(result: EspProvisioningResult) {
                println("startProvisioning onResponse: $result")
                results.add(result)
                if (deviceCount > 0 && results.size >= deviceCount) {
                    stopProvisioning()
                }
            }

            override fun onStop() {
                println("startProvisioning onStop")
                stopProvisioning()
            }

            override fun onError(e: Exception) {
                println("startProvisioning onError")
                e.printStackTrace()
                provisioner.stopProvisioning()
            }

        })
    }

    private fun stopProvisioning() {
        if (viewModel.provisioningProgress.value){
            viewModel.provisioningProgress.value = false
        }
        if (provisioner.isProvisioning) {
            provisioner.stopProvisioning()
        }
    }
}

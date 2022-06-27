package com.espressif.iot.esptouch2.provision;

import java.io.Closeable;

public interface IEspProvisioner extends Closeable {
    String ESPTOUCH_VERSION = BuildConfig.VERSION_NAME;

    int DEVICE_PORT = 7001;
    int DEVICE_ACK_PORT = 7002;
    int[] APP_PORTS = {18266, 28266, 38266, 48266};

    void startSync(EspSyncListener listener);

    void stopSync();

    boolean isSyncing();

    void startProvisioning(EspProvisioningRequest request, EspProvisioningListener listener);

    void stopProvisioning();

    boolean isProvisioning();

}

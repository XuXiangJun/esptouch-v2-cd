package com.espressif.iot.esptouch2.provision;

import java.net.InetAddress;

public class EspProvisioner implements IEspProvisioner {
    private final EspProvisionerImpl mDelegate;

    public EspProvisioner() {
        mDelegate = new EspProvisionerImpl();
    }

    @Override
    public boolean isSyncing() {
        return mDelegate.isSyncing();
    }

    @Override
    public void startSync(EspSyncListener listener) {
        mDelegate.startSync(listener);
    }

    @Override
    public void stopSync() {
        mDelegate.stopSync();
    }

    @Override
    public boolean isProvisioning() {
        return mDelegate.isProvisioning();
    }

    @Override
    public void startProvisioning(EspProvisioningRequest request, EspProvisioningListener listener) {
        mDelegate.startProvisioning(request, listener);
    }

    @Override
    public void stopProvisioning() {
        mDelegate.stopProvisioning();
    }

    @Override
    public void close() {
        mDelegate.close();
    }
}

package com.espressif.iot.esptouch2.provision;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EspProvisioningRequest {
    private static final int SSID_LENGTH_MAX = 32;
    private static final int PASSWORD_LENGTH_MAX = 64;
    private static final int BSSID_LENGTH = 6;
    private static final int AES_KEY_LENGTH = 16;

    public static final int RESERVED_LENGTH_MAX = 64;

    public final InetAddress address;

    public final byte[] ssid;
    public final byte[] bssid;
    public final byte[] password;

    public final byte[] reservedData;

    public final byte[] aesKey;

    private EspProvisioningRequest(InetAddress address, byte[] ssid, byte[] bssid, byte[] password,
                                   byte[] reservedData, byte[] aesKey) {
        this.address = address;
        this.ssid = ssid;
        this.bssid = bssid;
        this.password = password;
        this.reservedData = reservedData;
        this.aesKey = aesKey;
    }

    public static class Builder {
        private InetAddress address;

        private byte[] ssid = null;
        private byte[] bssid = null;
        private byte[] password = null;

        private byte[] reservedData;

        private byte[] aesKey;

        public Builder() {
        }

        public Builder setSSID(byte[] ssid) {
            if (ssid != null && ssid.length > SSID_LENGTH_MAX) {
                throw new IllegalArgumentException("SSID length is greater than 32");
            }
            this.ssid = ssid;
            return this;
        }

        public Builder setBSSID(byte[] bssid) {
            if (bssid.length != BSSID_LENGTH) {
                throw new IllegalArgumentException("Invalid BSSID data");
            }
            this.bssid = bssid;
            return this;
        }

        public Builder setPassword(byte[] password) {
            if (password != null && password.length > PASSWORD_LENGTH_MAX) {
                throw new IllegalArgumentException("Password length is greater than 64");
            }

            this.password = password;
            return this;
        }

        public Builder setReservedData(byte[] data) {
            if (data != null && data.length > RESERVED_LENGTH_MAX) {
                throw new IllegalArgumentException("ReservedData length is greater than 64");
            }

            this.reservedData = data;
            return this;
        }

        public Builder setAESKey(byte[] aesKey) {
            if (aesKey != null && aesKey.length != AES_KEY_LENGTH) {
                throw new IllegalArgumentException("AES Key must be null or 16 bytes");
            }

            this.aesKey = aesKey;
            return this;
        }

        public Builder setAddress(InetAddress address) {
            this.address = address;
            return this;
        }

        public Builder setAddress(String host) {
            try {
                this.address = InetAddress.getByName(host);
            } catch (UnknownHostException ignored) {
            }
            return this;
        }

        public EspProvisioningRequest build() {
            if (address == null) {
                try {
                    address = InetAddress.getLocalHost();
                } catch (UnknownHostException ignored) {
                }
            }
            if (address == null) {
                try {
                    address = InetAddress.getByName("255.255.255.255");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

            return new EspProvisioningRequest(address, ssid, bssid, password, reservedData, aesKey);
        }
    }
}

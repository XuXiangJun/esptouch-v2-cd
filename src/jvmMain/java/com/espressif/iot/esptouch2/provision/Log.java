package com.espressif.iot.esptouch2.provision;

public class Log {
    public static void w(String tag, String message) {
        System.out.println("WARN: " + tag + ": " + message);
    }

    public static void e(String tag, String message) {
        System.err.println("ERROR: " + tag + ": " + message);
    }

    public static void d(String tag, String message) {
        System.out.println("DEBUG: " + tag + ": " + message);
    }

    public static void i(String tag, String message) {
        System.out.println("INFO: " + tag + ": " + message);
    }
}

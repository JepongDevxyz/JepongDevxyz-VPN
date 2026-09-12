package com.kimchangyoun.rootbeerFresh;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Java-only AIDE compatibility implementation of the RootBeer API used by this app.
 * It preserves the public checks the app calls without requiring the native library.
 */
public class RootBeer {
    private final Context context;

    private static final String[] ROOT_PACKAGES = new String[] {
        "com.topjohnwu.magisk",
        "com.noshufou.android.su",
        "com.noshufou.android.su.elite",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser"
    };

    private static final String[] DANGEROUS_PACKAGES = new String[] {
        "com.koushikdutta.rommanager",
        "com.koushikdutta.rommanager.license",
        "com.dimonvideo.luckypatcher",
        "com.chelpus.lackypatch"
    };

    private static final String[] BIN_PATHS = new String[] {
        "/system/bin/", "/system/xbin/", "/sbin/", "/vendor/bin/", "/su/bin/",
        "/data/local/bin/", "/data/local/xbin/", "/data/local/"
    };

    public RootBeer(Context context) {
        this.context = context;
    }

    public boolean isRooted() {
        return detectRootManagementApps()
            || detectPotentiallyDangerousApps()
            || checkForBinary("su")
            || checkForDangerousProps()
            || checkForRWPaths()
            || detectTestKeys()
            || checkSuExists()
            || checkForRootNative()
            || checkForMagiskBinary();
    }

    public boolean isRootedWithoutBusyBoxCheck() {
        return isRooted();
    }

    public boolean detectTestKeys() {
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    public boolean detectRootManagementApps() {
        return isAnyPackageInstalled(ROOT_PACKAGES);
    }

    public boolean detectPotentiallyDangerousApps() {
        return isAnyPackageInstalled(DANGEROUS_PACKAGES);
    }

    public boolean checkForBinary(String filename) {
        for (int i = 0; i < BIN_PATHS.length; i++) {
            if (new File(BIN_PATHS[i], filename).exists()) return true;
        }
        return false;
    }

    public boolean checkForMagiskBinary() {
        return checkForBinary("magisk");
    }

    public boolean checkForDangerousProps() {
        BufferedReader reader = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if ((line.contains("[ro.debuggable]") && line.contains("[1]"))
                    || (line.contains("[ro.secure]") && line.contains("[0]"))) return true;
            }
        } catch (Throwable ignored) {
        } finally {
            try { if (reader != null) reader.close(); } catch (Throwable ignored) {}
        }
        return false;
    }

    public boolean checkForRWPaths() {
        BufferedReader reader = null;
        try {
            Process process = Runtime.getRuntime().exec("mount");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String lower = line.toLowerCase();
                boolean protectedPath = lower.contains(" /system ")
                    || lower.contains(" /vendor ")
                    || lower.contains(" /system_root ");
                if (protectedPath && (lower.contains("(rw,") || lower.contains(",rw,") || lower.endsWith(" rw"))) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            try { if (reader != null) reader.close(); } catch (Throwable ignored) {}
        }
        return false;
    }

    public boolean checkSuExists() {
        BufferedReader reader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] {"which", "su"});
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine() != null;
        } catch (Throwable ignored) {
            return false;
        } finally {
            try { if (reader != null) reader.close(); } catch (Throwable ignored) {}
            if (process != null) process.destroy();
        }
    }

    public boolean checkForRootNative() {
        // Native checker is not bundled in the classic AIDE conversion; keep equivalent
        // practical coverage via the binary check instead of failing compilation/runtime.
        return checkForBinary("su");
    }

    private boolean isAnyPackageInstalled(String[] packages) {
        if (context == null) return false;
        PackageManager pm = context.getPackageManager();
        for (int i = 0; i < packages.length; i++) {
            try {
                pm.getPackageInfo(packages[i], 0);
                return true;
            } catch (PackageManager.NameNotFoundException ignored) {
            } catch (Throwable ignored) {
            }
        }
        return false;
    }
}

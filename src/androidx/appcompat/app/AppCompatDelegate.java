package androidx.appcompat.app;
public abstract class AppCompatDelegate {
 public static final int MODE_NIGHT_FOLLOW_SYSTEM=-1;
 public static final int MODE_NIGHT_NO=1;
 public static final int MODE_NIGHT_YES=2;
 private static int mode=MODE_NIGHT_FOLLOW_SYSTEM;
 public static void setDefaultNightMode(int m){mode=m;}
 public static int getDefaultNightMode(){return mode;}
 public void setLocalNightMode(int m){mode=m;}
 public int getLocalNightMode(){return mode;}
}

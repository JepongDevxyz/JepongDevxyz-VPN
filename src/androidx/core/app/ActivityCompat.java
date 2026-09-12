package androidx.core.app;
import android.app.Activity;
public class ActivityCompat {
 public static void requestPermissions(Activity a,String[] p,int r){ if(android.os.Build.VERSION.SDK_INT>=23)a.requestPermissions(p,r); }
 public static boolean shouldShowRequestPermissionRationale(Activity a,String p){ return android.os.Build.VERSION.SDK_INT>=23 && a.shouldShowRequestPermissionRationale(p); }
}

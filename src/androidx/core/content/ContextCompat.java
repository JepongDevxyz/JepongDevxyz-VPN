package androidx.core.content;
import android.content.*; import android.content.pm.PackageManager; import java.io.File;
public class ContextCompat {
 public static int checkSelfPermission(Context c,String p){return c.checkCallingOrSelfPermission(p);}
 public static File getDataDir(Context c){if(android.os.Build.VERSION.SDK_INT>=24)return c.getDataDir(); return c.getApplicationInfo().dataDir==null?null:new File(c.getApplicationInfo().dataDir);}
 public static int getColor(Context c,int id){return c.getResources().getColor(id);}
 public static android.graphics.drawable.Drawable getDrawable(Context c,int id){return c.getResources().getDrawable(id);}
 public static ComponentName startForegroundService(Context c, Intent i){return c.startService(i);}
}

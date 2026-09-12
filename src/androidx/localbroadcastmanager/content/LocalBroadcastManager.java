package androidx.localbroadcastmanager.content;
import android.content.*;
public class LocalBroadcastManager {
 private static LocalBroadcastManager s; private final Context c;
 private LocalBroadcastManager(Context x){c=x.getApplicationContext();}
 public static synchronized LocalBroadcastManager getInstance(Context c){if(s==null)s=new LocalBroadcastManager(c);return s;}
 public boolean sendBroadcast(Intent i){c.sendBroadcast(i);return true;}
 public void registerReceiver(BroadcastReceiver r,IntentFilter f){c.registerReceiver(r,f);}
 public void unregisterReceiver(BroadcastReceiver r){try{c.unregisterReceiver(r);}catch(Exception e){}}
}

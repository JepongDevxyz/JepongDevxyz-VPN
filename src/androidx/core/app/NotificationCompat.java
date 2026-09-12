package androidx.core.app;
import android.app.*; import android.content.Context; import android.graphics.Bitmap; import android.net.Uri;
public class NotificationCompat {
 public static final int PRIORITY_DEFAULT=0; public static final int PRIORITY_HIGH=1; public static final int PRIORITY_LOW=-1; public static final int FLAG_ONLY_ALERT_ONCE=Notification.FLAG_ONLY_ALERT_ONCE;
 public static class Builder {
   private final Notification.Builder b;
   public Builder(Context c){b=new Notification.Builder(c);}
   public Builder(Context c,String channel){b=android.os.Build.VERSION.SDK_INT>=26?new Notification.Builder(c,channel):new Notification.Builder(c);}
   public Builder setSmallIcon(int i){b.setSmallIcon(i);return this;} public Builder setContentTitle(CharSequence s){b.setContentTitle(s);return this;}
   public Builder setContentText(CharSequence s){b.setContentText(s);return this;} public Builder setContentIntent(PendingIntent p){b.setContentIntent(p);return this;}
   public Builder setOngoing(boolean x){b.setOngoing(x);return this;} public Builder setAutoCancel(boolean x){b.setAutoCancel(x);return this;}
   public Builder setOnlyAlertOnce(boolean x){b.setOnlyAlertOnce(x);return this;} public Builder setShowWhen(boolean x){b.setShowWhen(x);return this;} public Builder setPriority(int x){b.setPriority(x);return this;}
   public Builder setLargeIcon(Bitmap x){b.setLargeIcon(x);return this;} public Builder setSound(Uri u){b.setSound(u);return this;}
   public Builder setVibrate(long[] v){b.setVibrate(v);return this;} public Builder setDefaults(int d){b.setDefaults(d);return this;}
   public Builder setStyle(Object s){return this;} public Notification build(){return b.build();}
 }
 public static class BigTextStyle { public BigTextStyle bigText(CharSequence s){return this;} }
}

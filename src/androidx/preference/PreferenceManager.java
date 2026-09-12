package androidx.preference;
import android.content.*;
public class PreferenceManager {
 private final Context context;
 public PreferenceManager(Context c){context=c;}
 public static SharedPreferences getDefaultSharedPreferences(Context c){return android.preference.PreferenceManager.getDefaultSharedPreferences(c);}
 public SharedPreferences getDefaultSharedPreferences(){return android.preference.PreferenceManager.getDefaultSharedPreferences(context);}
 public Preference findPreference(CharSequence k){return null;}
}

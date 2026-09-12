package androidx.preference;
import android.os.Bundle; import android.content.Context; import androidx.fragment.app.Fragment;
public class PreferenceFragmentCompat extends Fragment {
 public interface OnPreferenceStartFragmentCallback { boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref); }
 private PreferenceManager manager;
 public void onCreatePreferences(Bundle b,String rootKey){}
 public void setPreferencesFromResource(int resId,String rootKey){}
 public Preference findPreference(CharSequence key){return null;}
 public PreferenceManager getPreferenceManager(){if(manager==null)manager=new PreferenceManager(getActivity());return manager;}
 public PreferenceScreen getPreferenceScreen(){return null;}
 public Context getContext(){return getActivity();}
}

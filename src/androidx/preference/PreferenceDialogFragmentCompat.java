package androidx.preference;
import androidx.fragment.app.DialogFragment;
public abstract class PreferenceDialogFragmentCompat extends DialogFragment {
 public abstract void onDialogClosed(boolean positiveResult);
 protected Preference getPreference(){return null;}
}

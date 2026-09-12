package androidx.preference;
import android.content.Context; import android.util.AttributeSet;
public class SwitchPreferenceCompat extends Preference {
 private boolean checked;
 public SwitchPreferenceCompat(Context c){super(c);} public SwitchPreferenceCompat(Context c,AttributeSet a){super(c,a);} public SwitchPreferenceCompat(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setChecked(boolean b){checked=b;persistBoolean(b);} public boolean isChecked(){return getPersistedBoolean(checked);}
}

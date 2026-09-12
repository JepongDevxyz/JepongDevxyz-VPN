package androidx.preference;
import android.content.Context; import android.util.AttributeSet;
public class CheckBoxPreference extends Preference {
 private boolean checked;
 public CheckBoxPreference(Context c){super(c);} public CheckBoxPreference(Context c,AttributeSet a){super(c,a);} public CheckBoxPreference(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setChecked(boolean b){checked=b;persistBoolean(b);} public boolean isChecked(){return getPersistedBoolean(checked);}
}

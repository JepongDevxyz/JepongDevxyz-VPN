package androidx.preference;
import android.content.Context; import android.util.AttributeSet;
public class ListPreference extends Preference {
 private String value;
 public ListPreference(Context c){super(c);} public ListPreference(Context c,AttributeSet a){super(c,a);} public ListPreference(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setValue(String v){value=v;persistString(v);} public String getValue(){return getPersistedString(value);}
}

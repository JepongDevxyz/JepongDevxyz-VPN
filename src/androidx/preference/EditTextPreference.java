package androidx.preference;
import android.content.Context; import android.util.AttributeSet;
public class EditTextPreference extends Preference {
 private String text="";
 public EditTextPreference(Context c){super(c);} public EditTextPreference(Context c,AttributeSet a){super(c,a);} public EditTextPreference(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setText(String t){text=t;persistString(t);} public String getText(){String p=getPersistedString(text); return p==null?text:p;}
}

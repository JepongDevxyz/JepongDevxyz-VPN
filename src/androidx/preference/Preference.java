package androidx.preference;
import android.content.Context; import android.os.Bundle; import android.util.AttributeSet;
public class Preference extends android.preference.Preference {
 public interface OnPreferenceChangeListener { boolean onPreferenceChange(Preference p,Object v); }
 private OnPreferenceChangeListener listener; private String fragment;
 public Preference(Context c){super(c);} public Preference(Context c,AttributeSet a){super(c,a);} public Preference(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setOnPreferenceChangeListener(final OnPreferenceChangeListener l){listener=l;super.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener(){public boolean onPreferenceChange(android.preference.Preference p,Object v){return l==null||l.onPreferenceChange(Preference.this,v);}});}
 public String getFragment(){return fragment;} public void setFragment(String f){fragment=f;}
 public Bundle getExtras(){return super.getExtras();}
}

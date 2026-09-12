package androidx.fragment.app;
import android.app.Activity; import android.os.Bundle; import android.view.*;
public class Fragment extends android.app.Fragment {
    public Fragment(){super();}
    public static Fragment instantiate(android.content.Context context, String fname) {
        return instantiate(context,fname,null);
    }
    public static Fragment instantiate(android.content.Context context, String fname, Bundle args) {
        try {
            Fragment f=(Fragment)Class.forName(fname).newInstance();
            if(args!=null) f.setArguments(args);
            return f;
        } catch(Exception e){ throw new RuntimeException(e); }
    }
    public android.content.Context getContext(){ return getActivity(); }
    public FragmentManager getFragmentManagerCompat(){ Activity a=getActivity(); return a==null?null:new FragmentManager(a.getFragmentManager()); }
}

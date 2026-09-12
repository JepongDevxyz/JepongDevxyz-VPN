package androidx.fragment.app;
public class FragmentManager {
    final android.app.FragmentManager fm;
    public FragmentManager(android.app.FragmentManager f){fm=f;}
    public FragmentTransaction beginTransaction(){return new FragmentTransaction(fm.beginTransaction());}
}

package androidx.fragment.app;
public class FragmentTransaction {
    final android.app.FragmentTransaction tx;
    FragmentTransaction(android.app.FragmentTransaction t){tx=t;}
    public FragmentTransaction replace(int id, android.app.Fragment f){tx.replace(id,f); return this;}
    public FragmentTransaction add(int id, android.app.Fragment f){tx.add(id,f); return this;}
    public FragmentTransaction addToBackStack(String n){tx.addToBackStack(n); return this;}
    public int commit(){return tx.commit();}
}

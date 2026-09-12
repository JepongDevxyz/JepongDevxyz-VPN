package androidx.appcompat.app;
import android.app.Activity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
public class AppCompatActivity extends Activity {
 private final ActionBar actionBar=new ActionBar();
 private final AppCompatDelegate delegate=new AppCompatDelegate(){};
 public void setSupportActionBar(Toolbar t){}
 public ActionBar getSupportActionBar(){return actionBar;}
 public FragmentManager getSupportFragmentManager(){return new FragmentManager(getFragmentManager());}
 public AppCompatDelegate getDelegate(){return delegate;}
 public boolean onSupportNavigateUp(){finish(); return true;}
}

package androidx.appcompat.app;
import android.app.Activity; import android.content.res.Configuration; import android.view.MenuItem; import android.view.Gravity;
import androidx.appcompat.widget.Toolbar; import androidx.drawerlayout.widget.DrawerLayout;
public class ActionBarDrawerToggle implements DrawerLayout.DrawerListener {
 private final DrawerLayout drawer;
 public ActionBarDrawerToggle(Activity a, DrawerLayout d, Toolbar t, int open, int close){drawer=d;}
 public void syncState(){} public void onConfigurationChanged(Configuration c){}
 public boolean onOptionsItemSelected(MenuItem item){
   if(item!=null && item.getItemId()==android.R.id.home){
     if(drawer.isDrawerOpen(Gravity.START)) drawer.closeDrawer(Gravity.START); else drawer.openDrawer(Gravity.START); return true;
   }
   return false;
 }
 public void onDrawerSlide(android.view.View v,float o){} public void onDrawerOpened(android.view.View v){}
 public void onDrawerClosed(android.view.View v){} public void onDrawerStateChanged(int s){}
}

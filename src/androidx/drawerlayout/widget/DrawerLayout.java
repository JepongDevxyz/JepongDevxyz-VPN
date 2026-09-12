package androidx.drawerlayout.widget;
import android.content.Context; import android.util.AttributeSet; import android.view.View;
public class DrawerLayout extends android.widget.FrameLayout {
 public interface DrawerListener {void onDrawerSlide(View v,float o);void onDrawerOpened(View v);void onDrawerClosed(View v);void onDrawerStateChanged(int s);}
 private DrawerListener listener; private boolean open;
 public DrawerLayout(Context c){super(c);} public DrawerLayout(Context c,AttributeSet a){super(c,a);} public DrawerLayout(Context c,AttributeSet a,int s){super(c,a,s);}
 public void addDrawerListener(DrawerListener l){listener=l;}
 public void setDrawerListener(DrawerListener l){addDrawerListener(l);}
 public void openDrawer(int g){open=true;if(listener!=null)listener.onDrawerOpened(this);}
 public void closeDrawer(int g){open=false;if(listener!=null)listener.onDrawerClosed(this);}
 public void closeDrawers(){closeDrawer(android.view.Gravity.START);}
 public boolean isDrawerOpen(int g){return open;}
}

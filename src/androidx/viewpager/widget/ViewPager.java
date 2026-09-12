package androidx.viewpager.widget;
import android.content.Context; import android.util.AttributeSet;
public class ViewPager extends android.widget.FrameLayout {
 private PagerAdapter a; private int current; private int offscreenPageLimit=1;
 public ViewPager(Context c){super(c);} public ViewPager(Context c,AttributeSet x){super(c,x);}
 public void setAdapter(PagerAdapter p){a=p;} public PagerAdapter getAdapter(){return a;}
 public void setCurrentItem(int p){current=p;} public int getCurrentItem(){return current;}
 public void setOffscreenPageLimit(int limit){offscreenPageLimit=Math.max(1,limit);}
 public int getOffscreenPageLimit(){return offscreenPageLimit;}
}

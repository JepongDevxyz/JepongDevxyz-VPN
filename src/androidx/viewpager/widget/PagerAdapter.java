package androidx.viewpager.widget;
import android.view.*;
public abstract class PagerAdapter {
 public static final int POSITION_NONE=-2, POSITION_UNCHANGED=-1;
 public abstract int getCount();
 public boolean isViewFromObject(View v,Object o){return v==o;}
 public Object instantiateItem(ViewGroup c,int p){return null;}
 public void destroyItem(ViewGroup c,int p,Object o){}
 public CharSequence getPageTitle(int p){return null;}
 public void notifyDataSetChanged(){}
 public int getItemPosition(Object o){return POSITION_UNCHANGED;}
}

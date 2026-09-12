package com.google.android.material.tabs;
import android.content.Context; import android.util.AttributeSet; import androidx.viewpager.widget.ViewPager;
public class TabLayout extends android.widget.HorizontalScrollView {
 public static final int MODE_FIXED=1, MODE_SCROLLABLE=0, GRAVITY_FILL=0, GRAVITY_CENTER=1;
 public TabLayout(Context c){super(c);} public TabLayout(Context c,AttributeSet a){super(c,a);} public TabLayout(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setTabMode(int m){} public void setTabGravity(int g){} public void setupWithViewPager(ViewPager v){}
}

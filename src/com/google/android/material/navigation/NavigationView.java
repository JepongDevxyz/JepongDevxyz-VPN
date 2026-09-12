package com.google.android.material.navigation;
import android.content.Context; import android.util.AttributeSet; import android.view.*; import java.util.*;
public class NavigationView extends android.widget.FrameLayout {
 public interface OnNavigationItemSelectedListener { boolean onNavigationItemSelected(MenuItem item); }
 private OnNavigationItemSelectedListener listener;
 public NavigationView(Context c){super(c);} public NavigationView(Context c,AttributeSet a){super(c,a);init(c,a);} public NavigationView(Context c,AttributeSet a,int s){super(c,a,s);init(c,a);} private void init(Context c,AttributeSet a){if(a==null)return;int r=a.getAttributeResourceValue("http://schemas.android.com/apk/res-auto","headerLayout",0);if(r!=0){android.view.View v=android.view.LayoutInflater.from(c).inflate(r,this,false);addView(v);}}
 public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener l){listener=l;}
 public View getHeaderView(int i){return getChildCount()>i?getChildAt(i):this;}
 public Menu getMenu(){return new SimpleMenu();}
 static class SimpleMenu implements Menu {
   public MenuItem add(CharSequence t){return null;} public MenuItem add(int t){return null;} public MenuItem add(int g,int i,int o,CharSequence t){return null;} public MenuItem add(int g,int i,int o,int t){return null;}
   public SubMenu addSubMenu(CharSequence t){return null;} public SubMenu addSubMenu(int t){return null;} public SubMenu addSubMenu(int g,int i,int o,CharSequence t){return null;} public SubMenu addSubMenu(int g,int i,int o,int t){return null;}
   public int addIntentOptions(int g,int i,int o,android.content.ComponentName c,android.content.Intent[] s,android.content.Intent in,int f,MenuItem[] out){return 0;}
   public void removeItem(int i){} public void removeGroup(int g){} public void clear(){} public void setGroupCheckable(int g,boolean c,boolean e){} public void setGroupVisible(int g,boolean v){} public void setGroupEnabled(int g,boolean e){}
   public boolean hasVisibleItems(){return false;} public MenuItem findItem(int id){return null;} public int size(){return 0;} public MenuItem getItem(int i){return null;}
   public void close(){} public boolean performShortcut(int k,android.view.KeyEvent e,int f){return false;} public boolean isShortcutKey(int k,android.view.KeyEvent e){return false;}
   public boolean performIdentifierAction(int i,int f){return false;} public void setQwertyMode(boolean q){}
 }
}

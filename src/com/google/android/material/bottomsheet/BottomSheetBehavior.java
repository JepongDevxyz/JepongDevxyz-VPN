package com.google.android.material.bottomsheet;
import android.view.View;
public class BottomSheetBehavior<V extends View> {
 public static final int STATE_EXPANDED=3,STATE_COLLAPSED=4,STATE_HIDDEN=5;
 public static abstract class BottomSheetCallback { public abstract void onStateChanged(View bottomSheet,int newState); public abstract void onSlide(View bottomSheet,float slideOffset); }
 public static <V extends View> BottomSheetBehavior<V> from(V v){return new BottomSheetBehavior<V>();}
 public void setState(int s){} public int getState(){return STATE_COLLAPSED;} public void addBottomSheetCallback(BottomSheetCallback c){} public void setHideable(boolean h){} public void setPeekHeight(int h){}
}

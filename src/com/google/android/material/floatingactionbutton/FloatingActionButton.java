package com.google.android.material.floatingactionbutton;
import android.content.Context; import android.util.AttributeSet;
public class FloatingActionButton extends android.widget.ImageButton {
 public FloatingActionButton(Context c){super(c);} public FloatingActionButton(Context c,AttributeSet a){super(c,a);} public FloatingActionButton(Context c,AttributeSet a,int s){super(c,a,s);}
 public void show(){setVisibility(VISIBLE);} public void hide(){setVisibility(GONE);}
}

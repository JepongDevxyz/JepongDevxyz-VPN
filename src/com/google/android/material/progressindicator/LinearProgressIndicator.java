package com.google.android.material.progressindicator;
import android.content.Context; import android.util.AttributeSet;
public class LinearProgressIndicator extends android.widget.ProgressBar {
 public LinearProgressIndicator(Context c){super(c,null,android.R.attr.progressBarStyleHorizontal);} public LinearProgressIndicator(Context c,AttributeSet a){super(c,a);} public LinearProgressIndicator(Context c,AttributeSet a,int s){super(c,a,s);}
 public void show(){setVisibility(VISIBLE);} public void hide(){setVisibility(GONE);}
}

package com.google.android.gms.ads;
import android.content.Context; import android.util.AttributeSet;
public class AdView extends android.widget.FrameLayout {
 private AdListener listener;
 public AdView(Context c){super(c);} public AdView(Context c,AttributeSet a){super(c,a);} public AdView(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setAdListener(AdListener l){listener=l;} public void loadAd(AdRequest r){if(listener!=null)listener.onAdLoaded();}
}

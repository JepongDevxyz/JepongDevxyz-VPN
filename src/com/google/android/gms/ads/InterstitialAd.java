package com.google.android.gms.ads;
import android.content.Context;
public class InterstitialAd {
 private AdListener listener; private boolean loaded; private boolean loading;
 public InterstitialAd(Context c){} public void setAdUnitId(String s){} public void setAdListener(AdListener l){listener=l;}
 public void loadAd(AdRequest r){loading=true;loaded=true;loading=false;if(listener!=null)listener.onAdLoaded();}
 public boolean isLoaded(){return loaded;} public boolean isLoading(){return loading;}
 public void show(){loaded=false;if(listener!=null)listener.onAdClosed();}
}

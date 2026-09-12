package com.google.android.material.textfield;
import android.content.Context; import android.util.AttributeSet; import android.widget.EditText;
public class TextInputLayout extends android.widget.LinearLayout {
 private CharSequence error; private android.view.View.OnClickListener endIconClickListener; public TextInputLayout(Context c){super(c);} public TextInputLayout(Context c,AttributeSet a){super(c,a);} public TextInputLayout(Context c,AttributeSet a,int s){super(c,a,s);}
 public void setError(CharSequence e){error=e;} public CharSequence getError(){return error;} public void setErrorEnabled(boolean e){}
 public EditText getEditText(){for(int i=0;i<getChildCount();i++)if(getChildAt(i) instanceof EditText)return (EditText)getChildAt(i);return null;}
 public void setHint(CharSequence h){} public CharSequence getHint(){return null;}
 public void setEndIconOnClickListener(android.view.View.OnClickListener l){endIconClickListener=l;}
}

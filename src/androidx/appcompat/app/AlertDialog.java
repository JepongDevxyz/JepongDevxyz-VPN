package androidx.appcompat.app;
import android.content.*; import android.view.View; import android.widget.Button;
public class AlertDialog extends android.app.Dialog {
    private final android.app.AlertDialog delegate;
    private AlertDialog(Context c, android.app.AlertDialog d){super(c); delegate=d;}
    public void show(){delegate.show();}
    public void dismiss(){delegate.dismiss();}
    public void cancel(){delegate.cancel();}
    public Button getButton(int which){return delegate.getButton(which);}
    public void setMessage(CharSequence message){delegate.setMessage(message);}
    public void setMessage(int resId){delegate.setMessage(getContext().getText(resId));}
    public void setButton(int which, CharSequence text, DialogInterface.OnClickListener listener){delegate.setButton(which,text,listener);}
    public void setButton(int which, int textId, DialogInterface.OnClickListener listener){delegate.setButton(which,getContext().getText(textId),listener);}
    public void setCanceledOnTouchOutside(boolean b){delegate.setCanceledOnTouchOutside(b);}
    public static class Builder {
        protected final Context context;
        protected final android.app.AlertDialog.Builder b;
        public Builder(Context c){context=c;b=new android.app.AlertDialog.Builder(c);}
        public Builder(Context c,int theme){context=c;b=new android.app.AlertDialog.Builder(c,theme);}
        public Builder setTitle(CharSequence x){b.setTitle(x);return this;}
        public Builder setTitle(int x){b.setTitle(x);return this;}
        public Builder setMessage(CharSequence x){b.setMessage(x);return this;}
        public Builder setMessage(int x){b.setMessage(x);return this;}
        public Builder setView(View v){b.setView(v);return this;}
        public Builder setView(int layout){b.setView(layout);return this;}
        public Builder setCancelable(boolean v){b.setCancelable(v);return this;}
        public Builder setIcon(int r){b.setIcon(r);return this;}
        public Builder setPositiveButton(CharSequence s, DialogInterface.OnClickListener l){b.setPositiveButton(s,l);return this;}
        public Builder setPositiveButton(int s, DialogInterface.OnClickListener l){b.setPositiveButton(s,l);return this;}
        public Builder setNegativeButton(CharSequence s, DialogInterface.OnClickListener l){b.setNegativeButton(s,l);return this;}
        public Builder setNegativeButton(int s, DialogInterface.OnClickListener l){b.setNegativeButton(s,l);return this;}
        public Builder setNeutralButton(CharSequence s, DialogInterface.OnClickListener l){b.setNeutralButton(s,l);return this;}
        public Builder setSingleChoiceItems(CharSequence[] a,int c,DialogInterface.OnClickListener l){b.setSingleChoiceItems(a,c,l);return this;}
        public AlertDialog create(){return new AlertDialog(context,b.create());}
        public AlertDialog show(){AlertDialog d=create();d.show();return d;}
    }
}

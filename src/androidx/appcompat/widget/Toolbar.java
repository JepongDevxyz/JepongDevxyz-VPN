package androidx.appcompat.widget;
import android.content.Context;
import android.util.AttributeSet;
public class Toolbar extends android.widget.LinearLayout {
    public Toolbar(Context c){super(c);}
    public Toolbar(Context c, AttributeSet a){super(c,a);}
    public Toolbar(Context c, AttributeSet a, int s){super(c,a,s);}
    public void setTitle(CharSequence t){}
    public void setSubtitle(CharSequence t){}
    public void setNavigationOnClickListener(android.view.View.OnClickListener l){super.setOnClickListener(l);}
}

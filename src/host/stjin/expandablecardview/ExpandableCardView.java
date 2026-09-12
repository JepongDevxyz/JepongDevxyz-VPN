package host.stjin.expandablecardview;
import android.content.Context; import android.util.AttributeSet;
public class ExpandableCardView extends android.widget.LinearLayout {
 private boolean expanded;
 public ExpandableCardView(Context c){super(c);} public ExpandableCardView(Context c,AttributeSet a){super(c,a);} public ExpandableCardView(Context c,AttributeSet a,int s){super(c,a,s);}
 public void expand(){expanded=true;} public void collapse(){expanded=false;} public boolean isExpanded(){return expanded;} public void toggle(){expanded=!expanded;}
}

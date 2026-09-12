package androidx.recyclerview.widget;
import android.content.Context;

/** Minimal LinearLayoutManager compatibility surface used by the app. */
public class LinearLayoutManager extends RecyclerView.LayoutManager {
 public static final int HORIZONTAL=0,VERTICAL=1;
 public LinearLayoutManager(Context c){}
 public LinearLayoutManager(Context c,int orientation,boolean reverse){}
 public int findFirstVisibleItemPosition(){return 0;}
 @Override public int getItemCount(){return super.getItemCount();}
 @Override public void scrollToPosition(int position){}
}

package androidx.recyclerview.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Small classic-AIDE compatibility implementation covering the RecyclerView API
 * surface used by this project. It is intentionally limited, but the adapter
 * lifecycle and item-count plumbing match the calls made by the app sources.
 */
public class RecyclerView extends android.widget.FrameLayout {
 private Adapter adapter;
 private LayoutManager layoutManager;

 public RecyclerView(Context c){super(c);}
 public RecyclerView(Context c, AttributeSet a){super(c,a);}
 public RecyclerView(Context c, AttributeSet a, int s){super(c,a,s);}

 public void setLayoutManager(LayoutManager manager){
   layoutManager=manager;
   if(manager!=null) manager.attachToRecyclerView(this);
 }
 public LayoutManager getLayoutManager(){return layoutManager;}

 public void setAdapter(Adapter newAdapter){
   if(adapter==newAdapter) return;
   if(adapter!=null) adapter.onDetachedFromRecyclerView(this);
   adapter=newAdapter;
   if(adapter!=null) adapter.onAttachedToRecyclerView(this);
 }
 public Adapter getAdapter(){return adapter;}
 public void scrollToPosition(int position){
   if(layoutManager!=null) layoutManager.scrollToPosition(position);
 }

 public static abstract class ViewHolder {
   public final View itemView;
   public ViewHolder(View v){itemView=v;}
   public int getAdapterPosition(){return 0;}
   public int getLayoutPosition(){return 0;}
 }

 public static abstract class Adapter<VH extends ViewHolder> {
   private final List<AdapterDataObserver> observers=new ArrayList<AdapterDataObserver>();
   public abstract VH onCreateViewHolder(ViewGroup parent,int viewType);
   public abstract void onBindViewHolder(VH holder,int position);
   public abstract int getItemCount();
   public int getItemViewType(int position){return 0;}
   public long getItemId(int position){return position;}
   public void notifyDataSetChanged(){for(AdapterDataObserver o:observers)o.onChanged();}
   public void notifyItemInserted(int position){for(AdapterDataObserver o:observers)o.onItemRangeInserted(position,1);}
   public void notifyItemRemoved(int position){for(AdapterDataObserver o:observers)o.onItemRangeRemoved(position,1);}
   public void notifyItemChanged(int position){for(AdapterDataObserver o:observers)o.onItemRangeChanged(position,1);}
   public void registerAdapterDataObserver(AdapterDataObserver o){if(o!=null&&!observers.contains(o))observers.add(o);}
   public void unregisterAdapterDataObserver(AdapterDataObserver o){observers.remove(o);}
   public void setHasStableIds(boolean b){}
   public void onAttachedToRecyclerView(RecyclerView recyclerView){}
   public void onDetachedFromRecyclerView(RecyclerView recyclerView){}
 }

 public static abstract class AdapterDataObserver {
   public void onChanged(){}
   public void onItemRangeChanged(int positionStart,int itemCount){}
   public void onItemRangeInserted(int positionStart,int itemCount){}
   public void onItemRangeRemoved(int positionStart,int itemCount){}
 }

 public static abstract class LayoutManager {
   private RecyclerView recyclerView;
   void attachToRecyclerView(RecyclerView rv){recyclerView=rv;}
   protected RecyclerView getRecyclerView(){return recyclerView;}
   public int getItemCount(){
     Adapter a=recyclerView==null?null:recyclerView.getAdapter();
     return a==null?0:a.getItemCount();
   }
   public void scrollToPosition(int position){}
 }
}

package androidx.preference;
import android.view.View;
public class PreferenceViewHolder { public final View itemView; public PreferenceViewHolder(View v){itemView=v;} public View findViewById(int id){return itemView.findViewById(id);} }

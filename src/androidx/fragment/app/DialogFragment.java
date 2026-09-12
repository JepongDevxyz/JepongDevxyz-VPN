package androidx.fragment.app;
public class DialogFragment extends android.app.DialogFragment {
    public DialogFragment() { super(); }
    public void show(FragmentManager manager, String tag) { super.show(manager.fm, tag); }
}

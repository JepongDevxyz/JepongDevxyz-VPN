package androidx.core.widget;
import android.widget.CompoundButton; import android.content.res.ColorStateList;
public class CompoundButtonCompat { public static void setButtonTintList(CompoundButton b, ColorStateList c){ if(android.os.Build.VERSION.SDK_INT>=21)b.setButtonTintList(c); } }

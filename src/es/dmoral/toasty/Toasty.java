package es.dmoral.toasty;

import android.content.Context;
import android.widget.Toast;

public class Toasty {
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    private static Toast t(Context c, CharSequence s, int d) { return Toast.makeText(c, s, d); }
    private static Toast t(Context c, int resId, int d) { return Toast.makeText(c, resId, d); }
    public static Toast success(Context c, CharSequence s) { return t(c, s, LENGTH_SHORT); }
    public static Toast success(Context c, CharSequence s, int d) { return t(c, s, d); }
    public static Toast success(Context c, CharSequence s, int d, boolean i) { return t(c, s, d); }
    public static Toast success(Context c, int resId, int d) { return t(c, resId, d); }
    public static Toast success(Context c, int resId, int d, boolean i) { return t(c, resId, d); }
    public static Toast error(Context c, CharSequence s) { return t(c, s, LENGTH_SHORT); }
    public static Toast error(Context c, CharSequence s, int d) { return t(c, s, d); }
    public static Toast error(Context c, CharSequence s, int d, boolean i) { return t(c, s, d); }
    public static Toast error(Context c, int resId, int d) { return t(c, resId, d); }
    public static Toast error(Context c, int resId, int d, boolean i) { return t(c, resId, d); }
    public static Toast info(Context c, CharSequence s) { return t(c, s, LENGTH_SHORT); }
    public static Toast info(Context c, CharSequence s, int d) { return t(c, s, d); }
    public static Toast info(Context c, CharSequence s, int d, boolean i) { return t(c, s, d); }
    public static Toast info(Context c, int resId, int d) { return t(c, resId, d); }
    public static Toast info(Context c, int resId, int d, boolean i) { return t(c, resId, d); }
    public static Toast warning(Context c, CharSequence s) { return t(c, s, LENGTH_SHORT); }
    public static Toast warning(Context c, CharSequence s, int d) { return t(c, s, d); }
    public static Toast warning(Context c, CharSequence s, int d, boolean i) { return t(c, s, d); }
    public static Toast warning(Context c, int resId, int d) { return t(c, resId, d); }
    public static Toast warning(Context c, int resId, int d, boolean i) { return t(c, resId, d); }
    public static Toast normal(Context c, CharSequence s) { return t(c, s, LENGTH_SHORT); }
    public static Toast normal(Context c, CharSequence s, int d) { return t(c, s, d); }
    public static Toast normal(Context c, int resId, int d) { return t(c, resId, d); }
}

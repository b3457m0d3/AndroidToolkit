package net.eledge.android.toolkit.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import net.eledge.android.toolkit.R;

public class GuiUtils {

	public static void toast(Context context, int text) {
		Toast.makeText(context, text,
				Toast.LENGTH_SHORT).show();
	}
	
	public static void toast(Context context, String text) {
		Toast.makeText(context, text,
				Toast.LENGTH_SHORT).show();
	}
	
	public static void notImplementedYet(Context context) {
		toast(context, R.string.androidToolkit_niy);
	}
	
	public static void startTopActivity(Context context, Class<? extends Activity> clazz) {
		final Intent intent = new Intent(context, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
    public static String getString(Context context, int resId) {
       return context.getResources().getString(resId);
    }

    public static String format(Context context, int resId, Object... params) {
      return String.format(getString(context, resId), params);
    }
    
}

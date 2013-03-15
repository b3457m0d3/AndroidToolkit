package net.eledge.android.toolkit.gui;

import net.eledge.android.toolkit.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GuiUtils {

	public static void toast(Context context, int text) {
		Toast.makeText(context, text,
				Toast.LENGTH_SHORT).show();
	}
	
	public static void notImplementedYet(Context context) {
		toast(context, R.string.niy);
	}
	
	public static void startTopActivity(Context context, Class<? extends Activity> clazz) {
		final Intent intent = new Intent(context, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}
	
//    public static View buildIndicator(TabActivity activity, int textRes) {
//        final TextView indicator = (TextView) activity.getLayoutInflater().inflate(R.layout.tab_indicator,
//        		activity.getTabWidget(), false);
//        indicator.setText(textRes);
//        return indicator;
//    }
    
    public static String getString(Context context, int resId) {
       return context.getResources().getString(resId);
    }

    public static String format(Context context, int resId, Object... params) {
      return String.format(getString(context, resId), params);
    }
    
}

package se7kn8.servercontroller.app.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class Util {

	public static float convertDpToPixel(float dp, Context context) {
		return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}

}

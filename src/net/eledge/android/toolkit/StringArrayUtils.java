package net.eledge.android.toolkit;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

public class StringArrayUtils {

	public static String[] toStringArray(List<String> list) {
	    return list.toArray(new String[list.size()]);
	}
	
	public static String[] toStringArray(SparseArray<List<String>> sparseArray) {
		List<String> list = new ArrayList<String>();
		if ( (sparseArray != null) && (sparseArray.size() > 0)) {
			for (int i=0; i<sparseArray.size(); i++) {
				list.addAll(sparseArray.valueAt(i));
			}
		}
	    return list.toArray(new String[list.size()]);
    }

}

package net.eledge.android.toolkit;

import android.util.SparseArray;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringArrayUtils {

	public static boolean isNotBlank(String[] array) {
		return ((array != null) && (array.length > 0)) && StringUtils.join(array).trim().length() > 0;
	}

	public static boolean isBlank(String[] array) {
		return !isNotBlank(array);
	}

    public static boolean areAllBlank(String[]... arrays) {
        boolean allBlank = true;
        for (String[] array: arrays) {
            allBlank &= isBlank(array);
        }
        return allBlank;
    }

	public static String[] toArray(String... items) {
		return items;
	}
	
	public static String[] toArray(List<String> list) {
		if (list != null) {
			return list.toArray(new String[list.size()]);
		}
		return new String[] {};
	}
	
	public static String[] toArray(SparseArray<List<String>> sparseArray) {
		List<String> list = new ArrayList<>();
		if ( (sparseArray != null) && (sparseArray.size() > 0)) {
			for (int i=0; i<sparseArray.size(); i++) {
				list.addAll(sparseArray.valueAt(i));
			}
		}
	    return list.toArray(new String[list.size()]);
    }

}

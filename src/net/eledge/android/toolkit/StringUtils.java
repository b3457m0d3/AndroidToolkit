package net.eledge.android.toolkit;

import java.util.Locale;

public class StringUtils {
	
	public static boolean isEmpty(String s) {
		if (s != null) {
			if (!"".equals(s.trim())) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}
	
	public static String defaultValue(String s, String def) {
		return isEmpty(s)?def:s;
	}
	
	public static boolean contains(String s, String... values) {
		if (isNotEmpty(s)) {
			for (String value : values) {
				if (s.toLowerCase(Locale.ENGLISH).contains(value.toLowerCase(Locale.ENGLISH))) {
					return true;
				}
			}
		}
		return false;
	}

	public static String trimToNull(String s) {
		if (isNotEmpty(s)) {
			return s.trim();
		}
		return null;
	}

    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}

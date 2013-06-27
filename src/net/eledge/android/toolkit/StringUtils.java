package net.eledge.android.toolkit;

import java.util.Locale;

import android.text.TextUtils;

public class StringUtils {
	
	public static final int INDEX_NOT_FOUND = -1;

	public static boolean isEmpty(String s) {
		return TextUtils.isEmpty(s);
	}

	public static boolean isNotEmpty(String s) {
		return !TextUtils.isEmpty(s);
	}

	public static boolean isBlank(String s) {
		boolean isEmpty = isEmpty(s);
		if (!isEmpty) {
			isEmpty = isEmpty(s.trim());
		}
		return isEmpty;
	}

	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	public static String defaultValue(String s, String def) {
		return TextUtils.isEmpty(s) ? def : s;
	}

	public static boolean equalsIgnoreCase(String s1, String s2) {
		if ((s1 == null) || (s2 == null)) {
			return false;
		}
		return TextUtils.equals(s1.toLowerCase(Locale.ENGLISH),
				s2.toLowerCase(Locale.ENGLISH));
	}

	public static boolean contains(String s, String... values) {
		if (StringUtils.isNotEmpty(s)) {
			for (String value : values) {
				if (s.toLowerCase(Locale.ENGLISH).contains(
						value.toLowerCase(Locale.ENGLISH))) {
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

	public static String join(String[] array) {
		if ((array != null) && (array.length > 0)) {
			StringBuilder sb = new StringBuilder();
			for (String s : array) {
				sb.append(s);
			}
			return sb.toString();
		}
		return null;
	}

	public static String stripStart(String str, String stripChars) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;

		}
		int start = 0;
		if (stripChars == null) {
			while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
				start++;
			}
		} else if (stripChars.length() == 0) {
			return str;
		} else {
			while ((start != strLen)
					&& (stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND)) {
				start++;
			}
		}
		return str.substring(start);
	}

}

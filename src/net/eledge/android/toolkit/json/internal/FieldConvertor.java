package net.eledge.android.toolkit.json.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.eledge.android.toolkit.json.annotations.JsonField;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public enum FieldConvertor {

	STRING(String.class) {
		@Override
		public void setFieldValue(Field field, Object target, JSONObject json, String key)
				throws IllegalArgumentException, IllegalAccessException, JSONException {
			field.set(target, json.getString(key));
		}

	},
	STRINGARRAY(String[].class) {
		@Override
		public void setFieldValue(Field field, Object target, JSONObject json, String key)
				throws IllegalArgumentException, IllegalAccessException, JSONException {
			JSONArray jsonArray = json.getJSONArray(key);
			String[] array = new String[jsonArray.length()];
			for (int i=0; i<jsonArray.length(); i++) {
				array[i] = jsonArray.getString(i);
			}
			field.set(target, array);
		}
	},
	LONG(Long.class, long.class) {
		@Override
		public void setFieldValue(Field field, Object target, JSONObject json, String key)
				throws IllegalArgumentException, IllegalAccessException, JSONException {
			long value = json.getLong(key);
			if (field.getType().equals(Long.class)) {
				field.set(target, Long.valueOf(value));
			} else {
				field.setLong(target, value);
			}
		}
	},
	DOUBLE(Double.class, double.class) {
		@Override
		public void setFieldValue(Field field, Object target, JSONObject json, String key)
				throws IllegalArgumentException, IllegalAccessException, JSONException {
			double value = json.getDouble(key);
			if (field.getType().equals(Double.class)) {
				field.set(target, Double.valueOf(value));
			} else {
				field.setDouble(target, value);
			}
		}
	},
	BOOLEAN(Boolean.class, boolean.class) {
		@Override
		public void setFieldValue(Field field, Object target, JSONObject json, String key)
				throws IllegalArgumentException, IllegalAccessException, JSONException {
			boolean value = json.getBoolean(key);
			if (field.getClass().equals(Long.class)) {
				field.set(target, Boolean.valueOf(value));
			} else {
				field.setBoolean(target, value);
			}
		}
	},
	ENUM() {
		@Override
		public void setFieldValue(Field field, Object target, JSONObject json, String key)
				throws IllegalArgumentException, IllegalAccessException, JSONException {
			JsonField jsonField = field.getAnnotation(JsonField.class);
			String methodName = StringUtils.defaultIfBlank(jsonField.enumMethod(), "valueOf");
			Class<?> enumClass = field.getType();
			try {
				Method method = enumClass.getMethod(methodName, String.class);
				field.set(target, method.invoke(enumClass, json.getString(key)));
			} catch (Exception e) {
				// ignore
			}
		}
	};

	public final Class<?>[] clazzes;

	private FieldConvertor(Class<?>... clazzes) {
		this.clazzes = clazzes;
	}

	public abstract void setFieldValue(Field field, Object target, JSONObject json, String key)
			throws IllegalArgumentException, IllegalAccessException, JSONException;

	public static FieldConvertor getConvertor(Field field) {
		Class<?> clazz = field.getType();
		if (clazz.isEnum()) {
			return ENUM;
		}
		for (FieldConvertor convertor : FieldConvertor.values()) {
			for (Class<?> c : convertor.clazzes) {
				if (clazz.equals(c)) {
					return convertor;
				}
			}
		}
		return null;
	}

}

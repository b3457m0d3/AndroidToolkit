package net.eledge.android.toolkit.db.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import net.eledge.android.toolkit.StringUtils;

import android.database.Cursor;

public enum FieldType {

	INTEGER("INTEGER") {
		@Override
		public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
				throws IllegalArgumentException, IllegalAccessException {
			int i = cursor.getInt(columnIndex);
			if (field.getType().isPrimitive()) {
				field.setInt(instance, i);
			} else {
				field.set(instance, Integer.valueOf(i));
			}
		}
		@Override
		public Long toLong(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
			return Long.valueOf(field.getInt(instance));
		}
	},
	LONG("INTEGER") {
		@Override
		public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
				throws IllegalArgumentException, IllegalAccessException {
			long l = cursor.getInt(columnIndex);
			if (field.getType().isPrimitive()) {
				field.setLong(instance, l);
			} else {
				field.set(instance, Long.valueOf(l));
			}
		}
		@Override
		public Long toLong(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
			return Long.valueOf(field.getInt(instance));
		}
	},
	BOOLEAN("INTEGER") {
		@Override
		public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
				throws IllegalArgumentException, IllegalAccessException {
			boolean b = cursor.getInt(columnIndex) == 1;
			if (field.getType().isPrimitive()) {
				field.setBoolean(instance, b);
			} else {
				field.set(instance, Boolean.valueOf(b));
			}
		}

		@Override
		public String toString(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
			boolean b = field.getBoolean(instance);
			return b ? "1" : "0";
		}
	},
	ENUM("TEXT") {
		@Override
		public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
				throws IllegalArgumentException, IllegalAccessException {
			String value = cursor.getString(columnIndex);
			Class<?> enumClass = field.getType();
			try {
				Method method = enumClass.getMethod("valueOf", String.class);
				field.set(instance, method.invoke(enumClass, value));
			} catch (Exception e) {
				// ignore
			}
		}
	},
	STRING("TEXT");

	public String columnType;

	private FieldType(String columnType) {
		this.columnType = columnType;
	}

	public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
			throws IllegalArgumentException, IllegalAccessException {
		field.set(instance, cursor.getString(columnIndex));
	}

	public String toString(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
		Object value = field.get(instance);
		if (value != null) {
			return StringUtils.trimToNull(value.toString());
		}
		return null;
	}
	
	public Long toLong(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
		return null;
	}

	public static FieldType getType(Class<?> clazz) {
		if (clazz.isEnum()) {
			return ENUM;
		}
		String type = clazz.getSimpleName();
		if ("int".equals(type)) {
			type = "INTEGER";
		}
		return valueOf(type.toUpperCase(Locale.ENGLISH));
	}

}

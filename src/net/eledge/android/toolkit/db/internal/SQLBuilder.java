package net.eledge.android.toolkit.db.internal;

import java.lang.reflect.Field;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.eledge.android.toolkit.StringUtils;

public class SQLBuilder {

	public static String create(Class<?> clazz) {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(getTableName(clazz));
		sb.append(" (");
		boolean first = true;
		for (Field field : clazz.getFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				if (!first) {
					sb.append(", ");
				}
				sb.append(getFieldName(field));
				sb.append(" ").append(getType(field.getType()));
				if (field.isAnnotationPresent(Id.class)) {
					sb.append(" PRIMARY KEY");
				}
				first = false;
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public static String[] update(Class<?> clazz, int oldVersion, int newVersion) {
		// TODO
		return new String[]{};
	}
	
	public static String findById(Class<?> clazz) {
		StringBuilder sb = new StringBuilder(findAll(clazz));
		sb.append(" WHERE ").append(getIdField(clazz)).append(" = ?");
		return sb.toString();
	}
	
	public static String findAll(Class<?> clazz) {
		StringBuilder sb = new StringBuilder("SELECT * FROM ");
		sb.append(getTableName(clazz));
		return sb.toString();
	}
	
	public static String getFieldName(Field field) {
		Column column = field.getAnnotation(Column.class);
		return StringUtils.defaultValue(column.name(), field.getName().toLowerCase(Locale.ENGLISH));
	}
	
	public static String getTableName(Class<?> clazz) {
		Entity entity = clazz.getAnnotation(Entity.class);
		return StringUtils.defaultValue(entity.name(), clazz.getName().toLowerCase(Locale.ENGLISH));
	}

	public static String getIdField(Class<?> clazz) {
		for (Field field : clazz.getFields()) {
			if (field.isAnnotationPresent(Id.class)) {
				Column column = field.getAnnotation(Column.class);
				return StringUtils.defaultValue(column.name(), field.getName().toLowerCase(Locale.ENGLISH));
			}
		}
		return null;
	}
	
	private static String getType(Class<?> clazz) {
		return FieldType.getType(clazz).columnType;
	}

}

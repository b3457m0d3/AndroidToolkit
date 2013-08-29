package net.eledge.android.toolkit.db.internal;

import java.lang.reflect.Field;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;

public class SQLBuilder {

	public static String findById(Class<?> clazz) {
		StringBuilder sb = new StringBuilder(findAll(clazz));
		sb.append(" WHERE ").append(getWhereIdClause(clazz));
		return sb.toString();
	}

	public static String findAll(Class<?> clazz) {
		StringBuilder sb = new StringBuilder("SELECT * FROM ");
		sb.append(getTableName(clazz));
		return sb.toString();
	}
	
	public static String getWhereIdClause(Class<?> clazz) {
		StringBuilder sb = new StringBuilder(getIdField(clazz));
		sb.append(" = ?");
		return sb.toString();
	}

	public static String getFieldName(Field field) {
		Column column = field.getAnnotation(Column.class);
		return StringUtils.defaultIfBlank(column.name(), field.getName().toLowerCase(Locale.ENGLISH));
	}

	public static String getFieldName(Field field, Column column) {
		return StringUtils.defaultIfBlank(column.name(), field.getName().toLowerCase(Locale.ENGLISH));
	}

	public static String getTableName(Class<?> clazz) {
		Entity entity = clazz.getAnnotation(Entity.class);
		return StringUtils.defaultIfBlank(entity.name(), clazz.getName().toLowerCase(Locale.ENGLISH));
	}

	public static String getIdField(Class<?> clazz) {
		for (Field field : clazz.getFields()) {
			if (field.isAnnotationPresent(Id.class)) {
				Column column = field.getAnnotation(Column.class);
				return StringUtils.defaultIfBlank(column.name(), field.getName().toLowerCase(Locale.ENGLISH));
			}
		}
		return null;
	}

}

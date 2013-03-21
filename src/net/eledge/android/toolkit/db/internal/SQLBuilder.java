package net.eledge.android.toolkit.db.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import android.database.sqlite.SQLiteDatabase;

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
				sb.append(createFieldDef(clazz, field));
				first = false;
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public static String[] update(SQLiteDatabase db, Class<?> clazz, int oldVersion, int newVersion) {
		List<String> updates = new ArrayList<String>();
		
/*		db.
		
		StringBuilder sb = new StringBuilder("ALTER TABLE ");
		sb.append(getTableName(clazz));
		sb.append(" (");
		for (Field field : clazz.getFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				int columnIndex = cursor.getColumnIndex(SQLBuilder.getFieldName(field));
				if (columnIndex == -1) {
					// add new column...
				}
			}
		}
*/		// TODO
		return updates.toArray(new String[updates.size()]);
	}
	
	private static String createFieldDef(Class<?> clazz, Field field) {
		FieldType type = FieldType.getType(clazz);
		Column column = field.getAnnotation(Column.class);
		if (StringUtils.isNotEmpty(column.columnDefinition())) {
			return column.columnDefinition();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getFieldName(field, column));
		sb.append(" ").append(type.columnType);
		if (column.unique()) {
			sb.append(" UNIQUE");
		}
		try {
			if (!column.nullable()) {
				sb.append(" NOT NULL");
				sb.append(" DEFAULT ").append(type.defaultValue(clazz.newInstance(), field));
			}
		} catch (Exception e) {
		}
		if (field.isAnnotationPresent(Id.class)) {
			sb.append(" PRIMARY KEY");
		}
		return sb.toString();
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

	public static String getFieldName(Field field, Column column) {
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

}

package net.eledge.android.toolkit.db.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import net.eledge.android.toolkit.StringUtils;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TableBuilder {
	
	private SQLiteDatabase db;
	
	public TableBuilder(SQLiteDatabase db) {
	    this.db = db;
    }

	public String create(Class<?> clazz) {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(SQLBuilder.getTableName(clazz));
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

	public String[] update(Class<?> clazz, int oldVersion, int newVersion) {
		List<String> updates = new ArrayList<String>();
		if (doesTableExists(clazz)) {
			updates.add(create(clazz));
		} else {
			/*	
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
			
		}
		return updates.toArray(new String[updates.size()]);
	}
	
	private String createFieldDef(Class<?> clazz, Field field) {
		FieldType type = FieldType.getType(clazz);
		Column column = field.getAnnotation(Column.class);
		if (StringUtils.isNotEmpty(column.columnDefinition())) {
			return column.columnDefinition();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(SQLBuilder.getFieldName(field, column));
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
	
	public boolean doesTableExists(Class<?> clazz) {
		final String query = "SELECT name FROM sqlite_master WHERE type='table' AND name = ?;";
		Cursor cursor = db.rawQuery(query, new String[]{SQLBuilder.getTableName(clazz)});
		return cursor.getCount() == 1;
	}
	
}

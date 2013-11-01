package net.eledge.android.toolkit.db.internal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.db.annotations.ModelUpdate;
import net.eledge.android.toolkit.db.annotations.ModelUpdates;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

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
		SparseArray<List<String>> versionUpdates = new SparseArray<>();
		versionUpdates.put(-1, new ArrayList<String>());
		for (int i = oldVersion + 1; i <= newVersion; i++) {
			versionUpdates.put(i, new ArrayList<String>());
		}
		if (doesTableExists(clazz)) {
			versionUpdates.get(-1).add(create(clazz));
		} else {
			versionUpdates.get(-1).addAll(createTableUpdates(clazz));
		}
		collectTableUpdatesAnnotations(versionUpdates, clazz);
		collectFieldUpdatesAnnotations(versionUpdates, clazz);
		return StringArrayUtils.toArray(versionUpdates);
	}

	private String createFieldDef(Class<?> clazz, Field field) {
		FieldType type = FieldType.getType(field.getType());
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
            Log.e(this.getClass().getName(), e.getMessage(), e);
		}
		if (field.isAnnotationPresent(Id.class)) {
			sb.append(" PRIMARY KEY");
		}
		return sb.toString();
	}

	public List<String> createTableUpdates(Class<?> clazz) {
		List<String> updates = new ArrayList<>();
		final List<String> names = getExistingFields(clazz);
		for (Field field : clazz.getFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				String name = SQLBuilder.getFieldName(field);
				if (!names.contains(name)) {
					StringBuilder sb = new StringBuilder("ALTER TABLE ");
					sb.append(SQLBuilder.getTableName(clazz));
					sb.append(" ADD COLUMN ");
					sb.append(createFieldDef(clazz, field));
					sb.append(";");
					updates.add(sb.toString());
				}
			}
		}
		return updates;
	}

	private void collectTableUpdatesAnnotations(SparseArray<List<String>> versionUpdates, Class<?> clazz) {
		if (clazz.isAnnotationPresent(ModelUpdate.class)) {
			addUpdateIfNeeded(versionUpdates, clazz.getAnnotation(ModelUpdate.class));
		} else if (clazz.isAnnotationPresent(ModelUpdates.class)) {
			ModelUpdates updates = clazz.getAnnotation(ModelUpdates.class);
			for (ModelUpdate update: updates.value()) {
				addUpdateIfNeeded(versionUpdates, update);
			}
		}
	}

	private void collectFieldUpdatesAnnotations(SparseArray<List<String>> versionUpdates, Class<?> clazz) {
		for (Field field : clazz.getFields()) {
			if (field.isAnnotationPresent(ModelUpdate.class)) {
				addUpdateIfNeeded(versionUpdates, field.getAnnotation(ModelUpdate.class));
			} else if (field.isAnnotationPresent(ModelUpdates.class)) {
				ModelUpdates updates = field.getAnnotation(ModelUpdates.class);
				for (ModelUpdate update: updates.value()) {
					addUpdateIfNeeded(versionUpdates, update);
				}
			}
		}
	}

	private void addUpdateIfNeeded(SparseArray<List<String>> versionUpdates, ModelUpdate update) {
		if (versionUpdates.get(update.version()) != null) {
			versionUpdates.get(update.version()).add(update.sql());
		}
	}

	private boolean doesTableExists(Class<?> clazz) {
		final String query = "SELECT name FROM sqlite_master WHERE type='table' AND name = ?;";
		Cursor cursor = db.rawQuery(query, new String[] { SQLBuilder.getTableName(clazz) });
		return cursor.getCount() == 1;
	}

	private List<String> getExistingFields(Class<?> clazz) {
		List<String> names = new ArrayList<>();
		StringBuilder sb = new StringBuilder("pragma table_info(");
		sb.append(SQLBuilder.getTableName(clazz));
		sb.append(");");
		Cursor cursor = db.rawQuery(sb.toString(), new String[] { SQLBuilder.getTableName(clazz) });
		if ((cursor != null) && cursor.moveToFirst()) {
			do {
				names.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}
		return names;
	}

}

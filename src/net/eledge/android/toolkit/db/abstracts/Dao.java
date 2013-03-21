package net.eledge.android.toolkit.db.abstracts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import net.eledge.android.toolkit.StringUtils;
import net.eledge.android.toolkit.db.internal.FieldType;
import net.eledge.android.toolkit.db.internal.SQLBuilder;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class Dao<E> {

	private Class<E> clazz;
	private SQLiteDatabase db;

	public Dao(Class<E> clazz, SQLiteOpenHelper helper) {
		this.clazz = clazz;
		db = helper.getReadableDatabase();
	}

	public void close() {
		if (db.isOpen()) {
			db.close();
		}
	}

	public int count() {
		Cursor cursor = db.rawQuery(SQLBuilder.findAll(clazz), null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public E findById(Long id) {
		if (id != null) {
			return findOne(SQLBuilder.findById(clazz), new String[] { String.valueOf(id) });
		}
		return null;
	}
	
	public E findOne(String rawQuery, String... params) {
		if (StringUtils.isNotEmpty(rawQuery)) {
			Cursor cursor = db.rawQuery(rawQuery, params);
			if ((cursor != null) && (cursor.getCount() == 1)) {
				return mapToEntities(cursor).get(0);
			}
		}
		return null;
	}

	public List<E> find(String rawQuery) {
		return find(rawQuery, (String[])null);
	}
	
	public List<E> find(String rawQuery, String... params) {
		Cursor cursor = db.rawQuery(rawQuery, params);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			return mapToEntities(cursor);
		}
		return new ArrayList<E>();
	}
	
	public List<E> findAll() {
		return find(SQLBuilder.findAll(clazz));
	}
	
	public long store(E entity) {
		String idField = SQLBuilder.getIdField(clazz);
		String table = SQLBuilder.getTableName(clazz);
		ContentValues values = mapToContentValues(entity);
		if (values.get(idField) == null) {
			return db.insert(table, null, values);
		} else {
			long id = values.getAsLong(idField);
			StringBuilder sb = new StringBuilder("WHERE ");
			sb.append(SQLBuilder.getIdField(clazz)).append(" = ?");
			db.update(table, values, sb.toString(), new String[] { String.valueOf(id) });
			return id;
		}
	}

	private List<E> mapToEntities(Cursor cursor) {
		List<E> list = new ArrayList<E>();
		if ((cursor != null) && cursor.moveToFirst()) {
			do {
				try {
					E instance = clazz.newInstance();
					for (Field field : clazz.getFields()) {
						if (field.isAnnotationPresent(Column.class)) {
							int columnIndex = cursor.getColumnIndex(SQLBuilder.getFieldName(field));
							if (!cursor.isNull(columnIndex)) {
								FieldType fieldType = FieldType.getType(field.getType());
								fieldType.convertToField(instance, field, cursor, columnIndex);
							}
						}
					}
					list.add(instance);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InstantiationException e) {
				}
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	private ContentValues mapToContentValues(E entity) {
		ContentValues values = new ContentValues();
		try {
			for (Field field : clazz.getFields()) {
				if (field.isAnnotationPresent(Column.class)) {
					final FieldType fieldType = FieldType.getType(field.getType());
					final String key = SQLBuilder.getFieldName(field);
					final String value = fieldType.toString(entity, field);
					values.put(key, value);
				}
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		return values;
	}

}

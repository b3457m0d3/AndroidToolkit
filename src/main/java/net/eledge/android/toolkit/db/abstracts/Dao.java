package net.eledge.android.toolkit.db.abstracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.eledge.android.toolkit.db.internal.FieldType;
import net.eledge.android.toolkit.db.internal.SQLBuilder;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

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

    public void deleteAll() {
        db.delete(getTableName(), null, null);
    }

    public void delete(Long id) {
        if (id != null) {
            db.delete(getTableName(), SQLBuilder.getWhereIdClause(clazz), new String[]{id.toString()});
        }
    }

    public E findById(Long id) {
        if (id != null) {
            return findOne(SQLBuilder.findById(clazz), String.valueOf(id));
        }
        return null;
    }

    public E findOne(String rawQuery, String... params) {
        if (StringUtils.isNotEmpty(rawQuery)) {
            Cursor cursor = db.rawQuery(rawQuery, params);
            if ((cursor != null) && (cursor.getCount() == 1)) {
                E entity = mapToEntities(cursor).get(0);
                cursor.close();
                return entity;
            }
        }
        return null;
    }

    public List<E> find(String rawQuery) {
        return find(rawQuery, (String[]) null);
    }

    public List<E> find(String rawQuery, String... params) {
        Cursor cursor = db.rawQuery(rawQuery, params);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            // mapToEntities will close cursor
            return mapToEntities(cursor);
        }
        return new ArrayList<>();
    }

    public List<E> findAll() {
        return find(SQLBuilder.findAll(clazz));
    }

    public void store(List<E> entities) {
        for (E e : entities) {
            store(e);
        }
    }

    public long store(E entity) {
        String idField = SQLBuilder.getIdField(clazz);
        ContentValues values = mapToContentValues(entity);
        if (values.get(idField) == null) {
            return db.insert(getTableName(), null, values);
        } else {
            long id = values.getAsLong(idField);
            StringBuilder sb = new StringBuilder("WHERE ");
            sb.append(SQLBuilder.getIdField(clazz)).append(" = ?");
            db.update(getTableName(), values, sb.toString(), new String[]{String.valueOf(id)});
            return id;
        }
    }

    private List<E> mapToEntities(Cursor cursor) {
        List<E> list = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
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
                    } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
                        Log.e(this.getClass().getName(), e.getMessage(), e);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
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
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        return values;
    }

    private String mTableName = null;

    private String getTableName() {
        if (mTableName == null) {
            mTableName = SQLBuilder.getTableName(clazz);
        }
        return mTableName;
    }

}

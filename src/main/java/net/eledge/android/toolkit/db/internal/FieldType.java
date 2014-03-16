/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.toolkit.db.internal;

import android.database.Cursor;
import android.util.Log;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

public enum FieldType {

    INTEGER("INTEGER") {
        @Override
        public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
                throws IllegalArgumentException, IllegalAccessException {
            int i = cursor.getInt(columnIndex);
            if (field.getType().isPrimitive()) {
                field.setInt(instance, i);
            } else {
                field.set(instance, i);
            }
        }

        @Override
        public Long toLong(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
            return (long) field.getInt(instance);
        }

        @Override
        public String defaultValue(Object instance, Field field) {
            try {
                Long l = toLong(instance, field);
                if (l != null) {
                    return l.toString();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
            return "0";
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
                field.set(instance, l);
            }
        }

        @Override
        public Long toLong(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
            return (long) field.getInt(instance);
        }

        @Override
        public String defaultValue(Object instance, Field field) {
            try {
                Long l = toLong(instance, field);
                if (l != null) {
                    return l.toString();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
            return "0";
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
                field.set(instance, b);
            }
        }

        @Override
        public String toString(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
            boolean b = field.getBoolean(instance);
            return b ? "1" : "0";
        }

        @Override
        public String defaultValue(Object instance, Field field) {
            try {
                return toString(instance, field);
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
            return "0";
        }
    },
    DATE("INTEGER") {
        @Override
        public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
                throws IllegalArgumentException, IllegalAccessException {
            long l = cursor.getLong(columnIndex);
            field.set(instance, new Date(l));
        }

        @Override
        public Long toLong(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
            Date date = (Date) field.get(instance);
            return date.getTime();
        }

        @Override
        public String toString(Object instance, Field field) throws IllegalArgumentException, IllegalAccessException {
            return defaultValue(instance, field);
        }

        @Override
        public String defaultValue(Object instance, Field field) {
            try {
                Long l = toLong(instance, field);
                if (l != null) {
                    return l.toString();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
            return String.valueOf(new Date().getTime());
        }
    },
    ENUM("TEXT") {
        @Override
        public void convertToField(Object instance, Field field, Cursor cursor, int columnIndex)
                throws IllegalArgumentException, IllegalAccessException {
            String value = cursor.getString(columnIndex);
            if (StringUtils.isNotEmpty(value)) {
                Class<?> enumClass = field.getType();
                try {
                    Method method = enumClass.getMethod("valueOf", String.class);
                    field.set(instance, method.invoke(enumClass, value));
                } catch (Exception e) {
                    // ignore
                }
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

    public String defaultValue(Object instance, Field field) {
        try {
            StringBuilder sb = new StringBuilder("'");
            sb.append(toString(instance, field));
            sb.append("'");
            return sb.toString();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        return "''";
    }

    public static FieldType getType(Class<?> clazz) {
        if (clazz.isEnum()) {
            return ENUM;
        }
        String type = clazz.getSimpleName();
        if ("int".equals(type)) {
            return INTEGER;
        }
        return valueOf(type.toUpperCase(Locale.ENGLISH));
    }

}

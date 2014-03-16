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

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

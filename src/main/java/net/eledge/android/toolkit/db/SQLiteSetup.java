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

package net.eledge.android.toolkit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.eledge.android.toolkit.db.annotations.Model;
import net.eledge.android.toolkit.db.internal.TableBuilder;

public class SQLiteSetup extends SQLiteOpenHelper {

    public SQLiteSetup(Context context, Class<? extends SQLiteSetup> clazz) {
        super(context, clazz.getAnnotation(Model.class).name(), null, clazz.getAnnotation(Model.class).version());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableBuilder tb = new TableBuilder(db);
        for (Class<?> clazz : getModel()) {
            db.execSQL(tb.create(clazz));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableBuilder tb = new TableBuilder(db);
        db.beginTransaction();
        try {
            for (Class<?> clazz : getModel()) {
                String[] updates = tb.update(clazz, oldVersion, newVersion);
                for (String update : updates) {
                    db.execSQL(update);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int getModelVersion() {
        return this.getClass().getAnnotation(Model.class).version();
    }

    public Class<?>[] getModel() {
        return this.getClass().getAnnotation(Model.class).entities();
    }

}

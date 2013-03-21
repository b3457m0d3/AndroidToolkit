package net.eledge.android.toolkit.db;

import net.eledge.android.toolkit.db.annotations.Model;
import net.eledge.android.toolkit.db.internal.SQLBuilder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteSetup extends SQLiteOpenHelper {

	public SQLiteSetup(Context context, Class<? extends SQLiteSetup> clazz) {
		super(context, clazz.getAnnotation(Model.class).name(), null, clazz.getAnnotation(Model.class).version());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (Class<?> clazz : getModel()) {
			db.execSQL(SQLBuilder.create(clazz));
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		try {
			for (Class<?> clazz : getModel()) {
				String[] updates = SQLBuilder.update(clazz, oldVersion, newVersion);
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

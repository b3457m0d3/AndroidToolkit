package net.eledge.android.toolkit.db;

import net.eledge.android.toolkit.db.abstracts.DatabaseConfig;
import net.eledge.android.toolkit.db.internal.SQLBuilder;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteUtils {

	public static void createTables(SQLiteDatabase db, DatabaseConfig config) {
		for (Class<?> clazz : config.getModelClasses()) {
			db.execSQL(SQLBuilder.create(clazz));
		}
	}

}

package net.eledge.android.toolkit.db.abstracts;

public interface DatabaseConfig {
	
	public int getVersion();
	
	public Class<?>[] getModelClasses();
	
}

package excel2er.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigUtil {
	public static final String FILE_NAME = "excel2er.properties";
	public static final String ENTITY_LATEST_LOADED_FILE = "latest_loaded_file_for_entity";
	public static final String DOMAIN_LATEST_LOADED_FILE = "latest_loaded_file_for_domain";
	
	private static final String FILE_PATH;
	private static Properties config;
	
	static {
		config = new Properties();
		
		StringBuilder builder = new StringBuilder();
		builder.append(System.getProperty("user.home"));
		builder.append(File.separator);
		builder.append(".astah");
		builder.append(File.separator);
		builder.append("professional");
		builder.append(File.separator);
		builder.append(FILE_NAME);
		
		FILE_PATH = builder.toString();
		
		load();
	}
	
	public static String getEntityLatestLoadedFile() {
		return config.getProperty(ENTITY_LATEST_LOADED_FILE);
	}

	public static void saveEntityLatestLoadedFilePath(String path) {
		config.put(ENTITY_LATEST_LOADED_FILE, path);
		store();
	}

	public static String getDomainLatestLoadedFile() {
		return config.getProperty(DOMAIN_LATEST_LOADED_FILE);
	}

	public static void saveDomainLatestLoadedFilePath(String path) {
		config.put(DOMAIN_LATEST_LOADED_FILE, path);
		store();
	}
	
	public static void load() {
		InputStream stream = null;
		try {
			stream = new FileInputStream(FILE_PATH);
			config.load(stream);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}
	
	public static void store() {
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(FILE_PATH);
			config.store(stream, null);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}
}

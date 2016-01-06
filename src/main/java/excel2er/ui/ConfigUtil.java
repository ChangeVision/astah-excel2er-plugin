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
	private static final String FILE_NAME = "excel2er.properties";
	private static final String ENTITY_LATEST_LOADED_FILE = "ENTITY_LATEST_LOADED_FILE";
	private static final String ENTITY_LATEST_START_ROW = "ENTITY_LATEST_START_ROW";
	private static final String ENTITY_LATEST_LOGICAL_COL = "ENTITY_LATEST_LOGICAL_COL";
	private static final String ENTITY_LATEST_PHYSICAL_COL = "ENTITY_LATEST_PHYSICAL_COL";
	private static final String ENTITY_LATEST_PRIMARY_KEY_COL = "ENTITY_LATEST_PRIMARY_KEY_COL";
	private static final String ENTITY_LATEST_NOTNULL_COL = "ENTITY_LATEST_NOTNULL_COL";
	private static final String ENTITY_LATEST_DATATYPE_COL = "ENTITY_LATEST_DATATYPE_COL";
	private static final String ENTITY_LATEST_LENGTH_COL = "ENTITY_LATEST_LENGTH_COL";
	private static final String ENTITY_LATEST_DEFAULTVALUE_COL = "ENTITY_LATEST_DEFAULTVALUE_COL";
	private static final String ENTITY_LATEST_DEFINITION_COL = "ENTITY_LATEST_DEFINITION_COL";
	private static final String ENTITY_LATEST_ADVANCE_LOGICAL_ROW = "ENTITY_LATEST_ADVANCE_LOGICAL_ROW";
	private static final String ENTITY_LATEST_ADVANCE_LOGICAL_COL = "ENTITY_LATEST_ADVANCE_LOGICAL_COL";
	private static final String ENTITY_LATEST_ADVANCE_PHYSICAL_ROW = "ENTITY_LATEST_ADVANCE_PHYSICAL_ROW";
	private static final String ENTITY_LATEST_ADVANCE_PHYSICAL_COL = "ENTITY_LATEST_ADVANCE_PHYSICAL_COL";
	private static final String DOMAIN_LATEST_STARTROW = "DOMAIN_LATEST_STARTROW";
	private static final String DOMAIN_LATEST_LOGICAL_COL = "DOMAIN_LATEST_LOGICAL_COL";
	private static final String DOMAIN_LATEST_PHYSICAL_COL = "DOMAIN_LATEST_PHYSICAL_COL";
	private static final String DOMAIN_LATEST_DATATYPE_COL = "DOMAIN_LATEST_DATATYPE_COL";
    private static final String DOMAIN_LATEST_PARENT_DOMAIN_COL = "DOMAIN_LATEST_PARENT_DOMAIN_COL";
	private static final String DOMAIN_LATEST_DEFINITION_COL = "DOMAIN_LATEST_DEFINITION_COL";
	
	private static final String DOMAIN_LATEST_LOADED_FILE = "DOMAIN_LATEST_LOADED_FILE";
	
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
	
	public static void clear(){
		config = new Properties();
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

	public static String getEntityStartRow(){
		return config.getProperty(ENTITY_LATEST_START_ROW);
	}
	
	public static void saveEntityStartRow(String startRow) {
		config.put(ENTITY_LATEST_START_ROW, startRow);
		store();
	}

	public static String getEntityLogicalCol(){
		return config.getProperty(ENTITY_LATEST_LOGICAL_COL);
	}
	
	public static void saveEntityLogicalCol(String logicalCol) {
		config.put(ENTITY_LATEST_LOGICAL_COL, logicalCol);
		store();
	}

	public static String getEntityPhysicalCol(){
		return config.getProperty(ENTITY_LATEST_PHYSICAL_COL);
	}
	
	public static void saveEntityPhysicalCol(String physicalCol) {
		config.put(ENTITY_LATEST_PHYSICAL_COL, physicalCol);
		store();
	}

	public static String getEntityPrimaryKeyCol(){
		return config.getProperty(ENTITY_LATEST_PRIMARY_KEY_COL);
	}
	
	public static void saveEntityPrimaryKeyCol(String primaryKeyCol) {
		config.put(ENTITY_LATEST_PRIMARY_KEY_COL, primaryKeyCol);
		store();
	}

	public static String getEntityNotNullCol(){
		return config.getProperty(ENTITY_LATEST_NOTNULL_COL);
	}
	
	public static void saveEntityNotNullCol(String notNullCol) {
		config.put(ENTITY_LATEST_NOTNULL_COL, notNullCol);
		store();
	}

	public static String getEntityDataTypeCol(){
		return config.getProperty(ENTITY_LATEST_DATATYPE_COL);
	}
	
	public static void saveEntityDataTypeCol(String dataTypeCol) {
		config.put(ENTITY_LATEST_DATATYPE_COL, dataTypeCol);
		store();
	}

	public static String getEntityLengthCol(){
		return config.getProperty(ENTITY_LATEST_LENGTH_COL);
	}
	
	public static void saveEntityLengthCol(String lengthCol) {
		config.put(ENTITY_LATEST_LENGTH_COL, lengthCol);
		store();
	}

	public static String getEntityDefaultValueCol(){
		return config.getProperty(ENTITY_LATEST_DEFAULTVALUE_COL);
	}
	
	public static void saveEntityDefaultValueCol(String defaultValueCol) {
		config.put(ENTITY_LATEST_DEFAULTVALUE_COL, defaultValueCol);
		store();
	}

	public static String getEntityDefinitionCol(){
		return config.getProperty(ENTITY_LATEST_DEFINITION_COL);
	}
	
	public static void saveEntityDefinitionCol(String definitionCol) {
		config.put(ENTITY_LATEST_DEFINITION_COL, definitionCol);
		store();
	}

	public static String getEntityAdvanceLogicalRow(){
		return config.getProperty(ENTITY_LATEST_ADVANCE_LOGICAL_ROW);
	}
	
	public static void saveEntityAdvanceLogicalRow(String logicalRow) {
		config.put(ENTITY_LATEST_ADVANCE_LOGICAL_ROW, logicalRow);
		store();
	}

	public static String getEntityAdvanceLogicalCol() {
		return config.getProperty(ENTITY_LATEST_ADVANCE_LOGICAL_COL);
	}
	
	public static void saveEntityAdvanceLogicalCol(String logicalCol) {
		config.put(ENTITY_LATEST_ADVANCE_LOGICAL_COL, logicalCol);
		store();
	}

	public static String getEntityAdvancePhysicalRow() {
		return config.getProperty(ENTITY_LATEST_ADVANCE_PHYSICAL_ROW);
	}
	
	public static void saveEntityAdvancePhysicalRow(String physicalRow) {
		config.put(ENTITY_LATEST_ADVANCE_PHYSICAL_ROW, physicalRow);
		store();
	}

	public static String getEntityAdvancePhysicalCol() {
		return config.getProperty(ENTITY_LATEST_ADVANCE_PHYSICAL_COL);
	}
	
	public static void saveEntityAdvancePhysicalCol(String physicalCol) {
		config.put(ENTITY_LATEST_ADVANCE_PHYSICAL_COL, physicalCol);
		store();
	}

	public static String getDomainStartRow() {
		return config.getProperty(DOMAIN_LATEST_STARTROW);
	}

	public static void saveDomainStartRow(String startRow) {
		config.put(DOMAIN_LATEST_STARTROW, startRow);
		store();
	}

	public static String getDomainLogicalCol() {
		return config.getProperty(DOMAIN_LATEST_LOGICAL_COL);
	}

	public static void saveDomainLogicalCol(String logicalCol) {
		config.put(DOMAIN_LATEST_LOGICAL_COL, logicalCol);
		store();
	}

	public static String getDomainPhysicalCol() {
		return config.getProperty(DOMAIN_LATEST_PHYSICAL_COL);
	}

	public static void saveDomainPhysicalCol(String physicalCol) {
		config.put(DOMAIN_LATEST_PHYSICAL_COL, physicalCol);
		store();
	}

	public static String getDomainDataTypeCol() {
		return config.getProperty(DOMAIN_LATEST_DATATYPE_COL);
	}

	public static void saveDomainDataTypeCol(String dataTypeCol) {
		config.put(DOMAIN_LATEST_DATATYPE_COL, dataTypeCol);
		store();
	}

    public static String getDomainParentDomainCol() {
        return config.getProperty(DOMAIN_LATEST_PARENT_DOMAIN_COL);
    }

    public static void saveDomainParentDomainCol(String parentDomainCol) {
        config.put(DOMAIN_LATEST_PARENT_DOMAIN_COL, parentDomainCol);
        store();
    }

	public static String getDomainDefinitionCol() {
		return config.getProperty(DOMAIN_LATEST_DEFINITION_COL);
	}

	public static void saveDomainDefinitionCol(String definitionCol) {
		config.put(DOMAIN_LATEST_DEFINITION_COL, definitionCol);
		store();
	}

}

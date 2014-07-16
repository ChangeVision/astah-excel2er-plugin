package excel2er.models;

import java.util.ArrayList;
import java.util.List;

public class Entity {

	private String entityLogicalName;
	private String entityPhysicalName;
	private List<Attribute> attrs;

	public List<Attribute> getAttrs() {
		return attrs;
	}

	public Entity() {
		attrs = new ArrayList<Attribute>();
	}
	
	public String getEntityLogicalName() {
		return entityLogicalName;
	}

	public String getEntityPhysicalName() {
		return entityPhysicalName;
	}

	public void setEntityLogicalName(String value) {
		this.entityLogicalName = value;
	}

	public void setEntityPhysicalName(String value) {
		this.entityPhysicalName = value;
	}

	public void addAttribute(Attribute attr) {
		this.attrs.add(attr);
	}

	public List<Attribute> getAttributes() {
		return this.attrs;
	}

}

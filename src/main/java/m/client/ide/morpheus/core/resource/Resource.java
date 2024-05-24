package m.client.ide.morpheus.core.resource;

public class Resource {
	private String id;
	private String name;
	private ResourceType resourceType;
	private String description;
	private String revision;
	private String history;
	
	public Resource() {
		
	}
	
	public Resource(String id, String name, ResourceType resourceType, String description, String revision, String history) {
		this.id = id;
		this.name = name;
		this.resourceType = resourceType;
		this.description = description;
		this.revision = revision;
		this.history = history;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ResourceType getResourceType() {
		return resourceType;
	}
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
}

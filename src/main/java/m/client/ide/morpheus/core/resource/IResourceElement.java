package m.client.ide.morpheus.core.resource;


import javax.swing.*;

public interface IResourceElement {
	
	public Icon getImage();
	
	public IResourceElement[] getChildren();

	public String getName();

	public boolean hasChildren();

	public IResourceElement getParent();
}

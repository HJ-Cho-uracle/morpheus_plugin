package m.client.ide.morpheus.framework.eclipse.library;

import java.io.File;
import java.io.FilenameFilter;

public class ResourceConfigFileNameFilter implements FilenameFilter {

	private String resourceConfigFileName;

	public ResourceConfigFileNameFilter(String resourceConfigFileName) {
		this.resourceConfigFileName = resourceConfigFileName;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.equals(resourceConfigFileName);
	}

}

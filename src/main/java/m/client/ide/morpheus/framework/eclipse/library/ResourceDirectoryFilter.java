package m.client.ide.morpheus.framework.eclipse.library;

import java.io.File;
import java.io.FileFilter;

public class ResourceDirectoryFilter implements FileFilter {
	
	private String resourceConfigFileName;
	
	public ResourceDirectoryFilter(String resourceConfigFileName) {
		this.resourceConfigFileName = resourceConfigFileName;
	}

	@Override
	public boolean accept(File arg0) {

		if (arg0.isDirectory()) {
			if (arg0.listFiles(new ResourceConfigFileNameFilter(resourceConfigFileName)).length > 0)
				return true;
		}
		
		return false;
	}

}

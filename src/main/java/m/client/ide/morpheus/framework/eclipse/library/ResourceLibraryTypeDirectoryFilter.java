package m.client.ide.morpheus.framework.eclipse.library;

import m.client.ide.morpheus.core.resource.LibraryType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;


public class ResourceLibraryTypeDirectoryFilter implements FileFilter {

	public ResourceLibraryTypeDirectoryFilter() {
	}

	@Override
	public boolean accept(@NotNull File arg0) {
		if (arg0.isDirectory() && LibraryType.isLibraryType(arg0.getName()))
			return true;
		
		return false;
	}

}

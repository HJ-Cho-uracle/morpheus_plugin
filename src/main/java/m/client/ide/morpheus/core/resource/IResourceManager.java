package m.client.ide.morpheus.core.resource;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProcessCanceledException;
import org.w3c.dom.Document;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public interface IResourceManager<T> {

	public void init();
	
	public File getResourceHome(Resource resource);

	public T getInstalledResource(Resource resource);

	public ArrayList<T> getInstalledResourceList();

	public ArrayList<T> getLatestResourceList();

	public T getResource(Document doc);

	public T getResource(File configFile);

	public T getResource(String tagUrl);

	public ResourceInstalledStatus getResourceStatus(Resource resource);

	public String getResourceTag(T resource);

	public void installResourcesProcess(ArrayList<T> resourceList, ProgressIndicator progressIndicator) throws UnknownHostException,
			ProcessCanceledException;

	public void updateLatestResourceList(HashMap<String, T> resources);

	public void updateLatestResourceListProcess(ProgressIndicator progressIndicator) throws UnknownHostException, ProcessCanceledException;

	public void resetResourceProcess(ProgressIndicator progressIndicator) throws UnknownHostException, ProcessCanceledException;
}

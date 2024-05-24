package m.client.ide.morpheus.core.config.webserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappingInfo {
	private int index;
	private String name;
	private String patternStr;
	private String className;
	private boolean singleton;
	private Pattern pattern;
	
	public MappingInfo(String name, String patternStr, String className, boolean singleton) {
		this.name = name;
		this.patternStr = patternStr;
		this.className = className;
		this.singleton = singleton;
		
		init();
	}
	
	private void init() {
		pattern = Pattern.compile(patternStr);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPatternStr() {
		return patternStr;
	}
	
	public String getClassName() {
		return className;
	}
	
	public boolean isSingleton() {
		return singleton;
	}
	
	public boolean matcher(String url) {
		Matcher m = pattern.matcher(url);
		return m.matches();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name=").append(name).append("\n");
		sb.append("pattern=").append(patternStr).append("\n");
		sb.append("className=").append(className).append("\n");
		sb.append("singleton=").append(singleton).append("\n");
		
		return sb.toString();
	}
	
	
}

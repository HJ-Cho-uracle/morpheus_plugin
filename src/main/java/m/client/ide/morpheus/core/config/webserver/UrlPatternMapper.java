package m.client.ide.morpheus.core.config.webserver;

import com.intellij.openapi.diagnostic.Logger;
import m.client.ide.morpheus.framework.cli.jsonParam.AbstractJsonElement;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class UrlPatternMapper {
	private static final Logger LOG = Logger.getInstance(UrlPatternMapper.class);
	public static final String mapFileName = "mapping.json";
	
	private List<MappingInfo> mapList = new ArrayList<MappingInfo>();
	
	private static UrlPatternMapper instance = new UrlPatternMapper();
	
	public static UrlPatternMapper getInstance() {
		return instance;
	}
	
	private UrlPatternMapper() {
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error(e);
		}
	}
	
	public void init(List<MappingInfo> mapList) throws IOException, ParseException {
		this.mapList = mapList;
	}
	
	public void init() throws IOException, ParseException {
		mapList.clear();
		loadMappingInfo();
	}
	
	public void init(File file) throws IOException, ParseException {
		mapList.clear();
		loadMappingInfo(file);
	}
	
	public void init(String jsonString) throws IOException, ParseException {
		mapList.clear();
		loadMappingInfo(jsonString);
	}
	
	public void loadMappingInfo() throws IOException, ParseException {
		String jsonString = "";
		InputStream in = null;
		try {
			in = UrlPatternMapper.class.getResourceAsStream(mapFileName);
			byte[] buff = new byte[1024];
			
			for(int count = 0; (count = in.read(buff)) != -1; ) {
				jsonString += new String(buff, 0, count);
			}
		} finally {
			if(in != null) in.close();
		}
		loadMappingInfo(jsonString);
	}
	
	public void loadMappingInfo(File file) throws IOException, ParseException {
		String jsonString = "";
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] buff = new byte[1024];
			
			for(int count = 0; (count = in.read(buff)) != -1; ) {
				jsonString += new String(buff, 0, count);
			}
		} finally {
			if(in != null) in.close();
		}
		loadMappingInfo(jsonString);
	}
	
	public void loadMappingInfo(String jsonString) throws ParseException {
		mapList.clear();
		mapList.addAll(makeMappingInfoList(jsonString));
	}
	
	public static List<MappingInfo> makeMappingInfoList(String jsonString) throws ParseException {
		List<MappingInfo> list = new ArrayList<MappingInfo>();
		JSONParser sp = new JSONParser(AbstractJsonElement.JSONPARSER_MODE);
		Object jsonObject = sp.parse(jsonString);
		JSONArray jsonArray = (JSONArray)jsonObject;
		for(int index = 0; index < jsonArray.size(); index++) {
			JSONObject json = (JSONObject)jsonArray.get(index);
			MappingInfo mapInfo = new MappingInfo((String)json.get("name"), (String)json.get("pattern"), (String)json.get("className"), (Boolean)json.get("singleton"));
			list.add(mapInfo);
		}
		return list;
	}
	
	public void clear() {
		mapList.clear();
	}
	
	public void saveMappingInfo(String outFile) throws IOException {
		String jsonString = getJSONString();
		FileWriter writer = new FileWriter(outFile);
		writer.write(jsonString);
		writer.close();
	}
	
	@SuppressWarnings("unchecked")
	public String getJSONString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append("\n");
		JSONArray array = new JSONArray();
		for(MappingInfo mapInfo : mapList) {
			JSONObject object = new JSONObject();
			object.put("name", mapInfo.getName());
			object.put("pattern", mapInfo.getPatternStr());
			object.put("singleton", mapInfo.isSingleton());
			object.put("className", mapInfo.getClassName());
			array.add(object);
			sb.append("   " + object.toJSONString(JSONStyle.LT_COMPRESS)).append("\n");
		}
		//return array.toJSONString(JSONStyle.LT_COMPRESS);
		sb.append("]").append("\n");
		return sb.toString();
	}
	
	public List<MappingInfo> getMappingInfoList() {
		return mapList;
	}
	
	public void update(MappingInfo mapInfo) {
		int index = getIndex(mapInfo.getName());
		if(index > 0) {
			mapList.remove(index);
			mapList.add(index, mapInfo);
		}
	}
	
	public int getIndex(String name) {
		for(int index = 0; index < mapList.size(); index++) {
			MappingInfo mapInfo = mapList.get(index);
			if(name.equals(mapInfo.getName())) {
				return index;
			}
		}
		return -1;
	}
	
	public int getIndex(MappingInfo mapInfo) {
		return mapList.indexOf(mapInfo);
	}
	
	public int remove(MappingInfo mapInfo) {
		int index = mapList.indexOf(mapInfo);
		if(index > 0) {
			mapList.remove(index);
			return index;
		}
		throw new RuntimeException("Can't find MappingInfo :" + mapInfo.getName());
	}
	
	public void insertFirst(MappingInfo mapInfo) {
		mapList.add(0, mapInfo);
	}
	
	public void moveUp(MappingInfo info) {
		int index = mapList.indexOf(info);
		if(index == 0) return;
		mapList.remove(index);
		mapList.add(index - 1, info);
	}
	
	public void moveDown(MappingInfo info) {
		int index = mapList.indexOf(info);
		if(index >= mapList.size() - 1) return;
		mapList.remove(index);
		mapList.add(index + 1, info);
	}
}

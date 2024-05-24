package m.client.ide.morpheus.core.config;

import com.esotericsoftware.minlog.Log;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import net.minidev.json.parser.ParseException;
import org.gradle.internal.impldep.org.eclipse.jgit.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public abstract class AbstractJasonFileManager {
    protected String filePath = null;

    public AbstractJasonFileManager() {
        try {
            init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage());
        }
    }

    public void init() throws IOException, ParseException {
        readJsonString();
    }

    public void init(@NotNull String filePath) throws IOException, ParseException {
        this.filePath = filePath;
        init(new File(filePath));
    }

    public void init(@NotNull File file) throws IOException, ParseException {
        filePath = file.getPath();
        readJsonString(file);
    }

    public void readJsonString() throws IOException, ParseException {
        if(filePath == null || filePath.isEmpty()) {
            return;
        }

        readJsonString(new File(filePath));
    }

    public void readJsonString(File file) throws IOException, ParseException {
        if(!file.exists()) {
            return;
        }

        InputStream in = null;
        try {
            String jsonString = "";
            in = new FileInputStream(file);
            byte[] buff = new byte[1024];

            for(int count = 0; (count = in.read(buff)) != -1; ) {
                jsonString += new String(buff, 0, count);
            }
            loadJsonString(jsonString);
        } finally {
            if(in != null) in.close();
        }
    }

    public void saveToFile() {
        try {
            saveToFile(filePath);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, AbstractJasonFileManager.class, null, e.getMessage());
        }
    }

    public void saveToFile(String outFile) throws IOException {
        String jsonString = getJSONString();

        File file = new File(outFile);
        if(!file.getParentFile().exists()) {
            FileUtils.mkdirs(file.getParentFile());
        }

        FileWriter writer = new FileWriter(outFile);
        writer.write(jsonString);
        writer.close();
    }

    public abstract String getJSONString();
    public abstract void loadJsonString(String jsonString) throws ParseException, net.minidev.json.parser.ParseException;
}

package m.client.ide.morpheus.framework.eclipse;

import com.esotericsoftware.minlog.Log;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 클래스설명 :
 *
 * @author : johyeongjin
 * @version : Dec 16, 2021
 * @분류 :
 * m.client.ide.ui / package m.client.ide.ui.gradle;
 */

@SuppressWarnings({"restriction"/* , "unused" */})
public class BuildGradleUtils {

    public static final String BUILD_GRADLE_FILE = "build.gradle";

    private static final String COMPILE_SDK_VERSION = "compileSdkVersion";
    private static final String BUILD_TOOL_VERSION = "buildToolsVersion";
//	private static final String COMMENT_TOKEN = "//";

    private File buildGradleFile;

    private String sdkVersion;
    private String buildToolVersion;

    private boolean existMCoreGradle = false;

    /**
     * FileName		: BuildGradleUtils.java
     * Package		: m.client.ide.core.utils.gradle
     * Commnet		:
     * Author		: johyeongjin
     * Datetime		: Sep 2, 2022 3:10:04 PM
     */
    public class DependencyInfo {
        private String name;
        private Version revision;
        private String dependency;

        public DependencyInfo(String name, String revision, String dependency) {
            super();
            this.name = name;
            this.revision = new Version(revision);
            this.dependency = dependency;
        }

        public String getName() {
            return name;
        }

        public Version getRevisioin() {
            return revision;
        }

        public String getDependency() {
            return dependency;
        }
    }

    public BuildGradleUtils(@NotNull File projectFolder) {
        this.buildGradleFile = FileUtil.getChildFile(projectFolder, BUILD_GRADLE_FILE);
        if (buildGradleFile.exists()) {
            parseFile();
        }
    }

    private void parseFile() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(COMPILE_SDK_VERSION) >= 0 /* && line.indexOf(COMMENT_TOKEN) < 0 */) {
                    sdkVersion = line.substring(line.indexOf(COMPILE_SDK_VERSION)).trim();
                } else if (line.indexOf(BUILD_TOOL_VERSION) >= 0 /* && line.indexOf(COMMENT_TOKEN) < 0 */) {
                    buildToolVersion = getBuildToolVersion(line);
                } else if (line.indexOf(MCOREGRADLE_FILE) >= 0) {
                    existMCoreGradle = true;
                }
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    private @NotNull String getBuildToolVersion(String line) {
        // TODO Auto-generated method stub
        line = line.substring(line.indexOf(BUILD_TOOL_VERSION)).trim();
        StringBuilder buildToolVersion = new StringBuilder();
        String[] segments = line.split("\"");

        if (segments.length < 2) {
            return line;
        }

        buildToolVersion.append(segments[0]).append("\"").append(segments[1]).append("\"");

        return buildToolVersion.toString();
    }

    protected void appendLine(StringBuffer sb, String str) {
        appendLine(sb, str, 0);
    }

    private void appendLine(StringBuffer sb, String str, int depth) {
        if (depth > 0) {
            for (int i = 0; i < depth; i++) {
                sb.append("\t");
            }
        }
        sb.append(str);
        sb.append("\n");
    }

    private static final String APPLYPLUGIN = "apply plugin";
    private static final String DEPENCENCY_TAG = "dependencies ";
    private static final String IMPLEMENTATION_FILETREE = "implementation fileTree";
    private static final String DEPENDENCY_API = "api '";

    private static final String MCOREGRADLE_FILE = "mcoreLib.gradle";
    private static final String MCOREGRADLE_CONTENTS = "\ndependencies {\n" +
            "}\n";
    private static final String MCOREGRADLE_CHECK_CONTENTS = "\n" +
            "if (project.rootProject.file('mcoreLib.gradle’).exists()) {\n" +
            "	apply from: 'mcoreLib.gradle'\n" +
            "}\n";

    private static final String DEFAULT_CONFIG = "defaultConfig";
    private static final String DEF_ASSERTDIR = "def assetDir";
    private static final String KEYWORDS_buildscript = "buildscript";
    private static final String KEYWORDS_repositories = "repositories";
    private static final String SOURCE_SETS_MAIN = "main {";

    private static final String SOURCE_SETS_CONTENTS = "\t\t\tassets.srcDirs = ['../../assets', './src/main/assets']\n" +
            "\t\t\tjniLibs {\n" +
            "\t\t\t\tsrcDir 'libs'\n" +
            "\t\t\t}";

    private static final String MCORELIB_FILETREE = "implementation fileTree(dir: 'mcoreLibs'";

    protected void insertMCoreGradle() {
        if (existMCoreGradle) {
            return;
        }

        BufferedReader br = null;
        try {
            String str = FileUtils.readFileToString(buildGradleFile, StandardCharsets.UTF_8);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            int insertOffset = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(APPLYPLUGIN) >= 0 /* && line.indexOf(COMMENT_TOKEN) < 0 */) {
                    insertOffset = str.lastIndexOf(line) + line.length() + 1;
                    break;
                }
            }

            StringBuffer script = new StringBuffer(str);
            script.insert(insertOffset, MCOREGRADLE_CHECK_CONTENTS);
            FileUtil.writeToFile(script.toString(), buildGradleFile);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    public File getMCoreGradleFile() {
        File mcoreGradleFile = FileUtil.getChildFile(buildGradleFile.getParentFile(), MCOREGRADLE_FILE);
        if (!mcoreGradleFile.exists()) {
            FileUtil.writeToFile(MCOREGRADLE_CONTENTS, mcoreGradleFile);
        }

        return mcoreGradleFile;
    }

    private DependencyInfo makeDependencyInfo(@NotNull String str, String line) {
        DependencyInfo info = null;

        if (!str.isEmpty() && !line.isEmpty()) {
            String dependency = line.substring(line.indexOf(DEPENDENCY_API)).trim();
            String[] tokens = dependency.substring(line.indexOf('\'') + 1, line.lastIndexOf('\'')).split(":");
            if (tokens.length > 1) {
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < tokens.length - 1; i++) {
                    if (i > 0) {
                        name.append(':');
                    }
                    name.append(tokens[i]);
                }
                String revision = tokens[tokens.length - 1];

                info = new DependencyInfo(name.toString(), revision, line);
            }
        }
        return info;
    }

    private int parseMCoreLibGradle(Hashtable<String, DependencyInfo> dependencyInfos) {
        int insertOffset = -1;

        File mcoreGradleFile = getMCoreGradleFile();
        String str;
        try {
            str = FileUtils.readFileToString(mcoreGradleFile, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mcoreGradleFile)));

            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(DEPENCENCY_TAG) >= 0 ||
                        line.indexOf(IMPLEMENTATION_FILETREE) >= 0) {
                    insertOffset = str.lastIndexOf(line) + line.length() + 1;
                } else if (line.trim().indexOf(DEPENDENCY_API) >= 0) {
                    String dependency = line.trim();
                    DependencyInfo info = makeDependencyInfo(str, dependency);

                    if (info != null) {
                        dependencyInfos.put(info.getName(), info);
                        if (insertOffset < str.lastIndexOf(dependency)) {
                            insertOffset = str.lastIndexOf(line) + line.length() + 1;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return insertOffset;
    }

    public List<Node> getInsertDependencyList(@NotNull Element configGradleElement) {
        List<Node> nodeList = new ArrayList<Node>();

        Hashtable<String, DependencyInfo> dependencyInfos = new Hashtable<String, DependencyInfo>();
        this.parseMCoreLibGradle(dependencyInfos);

        NodeList dependencies = configGradleElement.getChildNodes();
        for (int i = 0; i < dependencies.getLength(); i++) {
            Node node = dependencies.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node instanceof Element) {
                Element srcEle = (Element) node;
                String attrName = srcEle.getAttribute("name");
                String attrRevision = srcEle.getAttribute("revision");
                String dependency = srcEle.getTextContent();

                if (attrName != null && !attrName.isEmpty() &&
                        attrRevision != null && !attrRevision.isEmpty() &&
                        dependency != null && !dependency.isEmpty()) {
                    DependencyInfo info = dependencyInfos.get(attrName);
                    if (info == null || !info.getRevisioin().equals(new Version(attrRevision))) {
                        nodeList.add(node);
                    }
                }
            }
        }

        return nodeList;
    }

    public String getInsertedDependencyContents(@NotNull List<Node> dependencies) throws IOException {
        File mcoreGradleFile = FileUtil.getChildFile(buildGradleFile.getParentFile(), MCOREGRADLE_FILE);
        String str = FileUtils.readFileToString(mcoreGradleFile, StandardCharsets.UTF_8);

        Hashtable<String, DependencyInfo> dependencyInfos = new Hashtable<String, DependencyInfo>();
        int insertOffset = this.parseMCoreLibGradle(dependencyInfos);

        List<Node> insertList = new ArrayList<Node>();
        for (Node node : dependencies) {
            if (node.getNodeType() == Node.ELEMENT_NODE && node instanceof Element) {
                Element srcEle = (Element) node;
                String attrName = srcEle.getAttribute("name");
                String attrRevision = srcEle.getAttribute("revision");
                String dependency = srcEle.getTextContent();

                if (!attrName.isEmpty() && !attrRevision.isEmpty() && !dependency.isEmpty()) {
                    DependencyInfo info = dependencyInfos.get(attrName);
                    if (info == null) {
                        insertList.add(node);
                    } else {
                        int start = str.lastIndexOf(info.dependency);
                        str = str.replaceAll(info.dependency.substring(info.dependency.indexOf(DEPENDENCY_API)).trim(), dependency);
                        if (insertOffset < str.lastIndexOf(dependency)) {
                            int replaceLength = info.dependency.substring(info.dependency.indexOf(DEPENDENCY_API)).trim().length();
                            insertOffset = start - replaceLength + dependency.length();
                        }
                    }
                }
            }
        }

        StringBuffer insertScript = new StringBuffer();
        for (Node node : insertList) {
            appendLine(insertScript, node.getTextContent(), 1);
        }
        StringBuffer script = new StringBuffer(str);
        script.insert(insertOffset, insertScript);

        return script.toString();
    }


    private static final String APPLY_FROM = "apply from";

    public class ApplyFromInfo {
        private String name;
        private Version revision;
        private String applyfrom;

        public ApplyFromInfo(String fileName) {

        }

        public ApplyFromInfo(String name, String revision, String applyfrom) {
            super();
            this.name = name;
            this.revision = (revision == null || revision.isEmpty()) ? null : new Version(revision);
            this.applyfrom = applyfrom;
        }

        public String getName() {
            return name;
        }

        public Version getRevisioin() {
            return revision;
        }

        public String getDependency() {
            return applyfrom;
        }
    }

    protected boolean existApplyFrom(String gradleFile) {
        boolean existFile = false;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(MCOREGRADLE_FILE) >= 0) {
                    existMCoreGradle = true;
                } else if (gradleFile != null && !gradleFile.isEmpty() && line.indexOf(gradleFile) >= 0) {
                    existFile = true;
                }
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }

        return (gradleFile == null || gradleFile.isEmpty()) ? existMCoreGradle : existFile;
    }

    class Offset {
        int start, end;
    }

    public void removeMCoreLibsFileTree() {
        ArrayList<Offset> remove = new ArrayList<>();
        BufferedReader br = null;
        try {
            String str = FileUtils.readFileToString(buildGradleFile, Charset.defaultCharset());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            String line = null;

            int index = 0;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(MCORELIB_FILETREE) >= 0) {
                    Offset offset = new Offset();
                    offset.start = index;
                    offset.end = index + line.length() + 1;
                    remove.add(offset);
                }
                index += line.length() + 1;
            }

            remove.sort((o1, o2) -> o2.start - o1.start);

            StringBuffer script = new StringBuffer(str);
            boolean isModified = false;
            for (Offset offset : remove) {
                if (offset.start > 0 && offset.start < offset.end && offset.end < script.length()) {
                    script.replace(offset.start, offset.end, "");
                    isModified = true;
                }
            }

            if (isModified) {
                FileUtil.writeToFile(script.toString(), buildGradleFile);
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    private boolean removeBlock(String keywords) {
        int removeStart = -1, removeEnd = -1;
        int braceCount = 0;
        boolean findBlackEnd = false;
        boolean isModified = false;

        BufferedReader br = null;
        try {
            String str = FileUtils.readFileToString(buildGradleFile, Charset.defaultCharset());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            String line = null;

            int readCount = 0;
            while ((line = br.readLine()) != null) {
                if (!findBlackEnd && line.indexOf(keywords) >= 0 && line.indexOf("{") > 0) {
                    removeStart = removeEnd = readCount;
                    findBlackEnd = true;
                }

                if (findBlackEnd) {
                    removeEnd += line.length() + System.lineSeparator().length();
                    if (line.indexOf('{') >= 0) {
                        braceCount++;
                    }
                    if (line.indexOf('}') >= 0) {
                        braceCount--;
                        if (braceCount <= 0) {
                            break;
                        }
                    }
                }
                readCount += line.length() + System.lineSeparator().length();
            }

            StringBuffer script = new StringBuffer(str);
            if (removeStart >= 0 && removeEnd < script.length()) {
                script.replace(removeStart, removeEnd, "");
                isModified = true;
            }

            if (isModified) {
                FileUtil.writeToFile(script.toString(), buildGradleFile);
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
        return isModified;
    }

    public void removeBuildScript() {
        while(removeBlock(KEYWORDS_buildscript));
    }

    public void removeRepositories() {
        while(removeBlock(KEYWORDS_repositories));
    }

    public void refactorSourceSets() {
        int commentStart = -1, commentEnd = -1;
        boolean findCommentEnd = false;
        int sourceSetMainStart = -1, sourceSetMainEnd = -1;
        boolean findSourceSet = false;
        boolean isModified = false;

        BufferedReader br = null;
        try {
            String str = FileUtils.readFileToString(buildGradleFile, Charset.defaultCharset());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            String line = null;

            while ((line = br.readLine()) != null) {
                if (line.indexOf(DEF_ASSERTDIR) >= 0) {
                    commentStart = commentEnd = str.lastIndexOf(line);
                    findCommentEnd = true;
                } else if (line.indexOf(SOURCE_SETS_MAIN) >= 0) {
                    sourceSetMainStart = str.lastIndexOf(line) + line.length() + 1;
                    sourceSetMainEnd = str.lastIndexOf(line);
                    findSourceSet = true;
                }

                if (findCommentEnd) {
                    commentEnd += line.length() + 1;
                    if (line.indexOf('}') >= 0) {
                        findCommentEnd = false;
                    }
                }
                if (findSourceSet) {
                    sourceSetMainEnd += line.length() + 1;
                    if (line.indexOf('}') >= 0) {
                        break;
                    }
                }
            }

            StringBuffer script = new StringBuffer(str);
            if (commentStart > 0 && commentEnd < script.length()) {
                String start = "/*" + System.lineSeparator();
                script.insert(commentStart, start);
                commentEnd += start.length();
                sourceSetMainStart += start.length();
                sourceSetMainEnd += start.length();

                String end = "*/" + System.lineSeparator();
                script.insert(commentEnd, end);
                sourceSetMainStart += end.length();
                sourceSetMainEnd += end.length();

                isModified = true;
            }
            String sourceSetMain = SOURCE_SETS_CONTENTS + System.lineSeparator();
            if (sourceSetMainStart > 0 && sourceSetMainStart < sourceSetMainEnd && sourceSetMainEnd < script.length()) {
                script.replace(sourceSetMainStart, sourceSetMainEnd, sourceSetMain);
                isModified = true;
            }

            if (isModified) {
                FileUtil.writeToFile(script.toString(), buildGradleFile);
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    public void insertAppId(String applicationId) {
        int insertOffset = -1;

        BufferedReader br = null;
        try {
            String str = FileUtils.readFileToString(buildGradleFile, Charset.defaultCharset());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            String line = null;

            while ((line = br.readLine()) != null) {
                if (line.indexOf(DEFAULT_CONFIG) >= 0) {
                    insertOffset = str.lastIndexOf(line) + line.length() + 1;
                    break;
                }
            }

            String appIdLine = String.format("\t\tapplicationId \"%s\"", applicationId);
            StringBuffer script = new StringBuffer(str);
            if (insertOffset >= script.length()) {
                insertOffset = script.length();
                appIdLine = System.lineSeparator() + appIdLine;
            }

            if (script.toString().trim().indexOf(appIdLine) < 0) {
                script.insert(insertOffset, appIdLine + System.lineSeparator());
                FileUtil.writeToFile(script.toString(), buildGradleFile);
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    public void insertApplyFrom(@NotNull File file) {
        insertApplyFrom(file.getName());
    }

    public void insertApplyFrom(String fileName) {
        int insertOffset = -1;

        ApplyFromInfo insert = makeApplyFromInfo(fileName);
        if (insert == null) {
            CommonUtil.log(Log.LEVEL_ERROR, "File Name : " + fileName + ", Make ApplyInfo Failed!");
            return;
        }

        BufferedReader br = null;
        try {
            String str = FileUtils.readFileToString(buildGradleFile, Charset.defaultCharset());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            String replaceString = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(APPLYPLUGIN) >= 0 /* && line.indexOf(COMMENT_TOKEN) < 0 */) {
                    insertOffset = str.lastIndexOf(line) + line.length() + 1;
                } else if (line.indexOf(APPLY_FROM) >= 0) {
                    String applyfrom = line.trim();
                    ApplyFromInfo info = makeApplyFromInfo(applyfrom);

                    if (info != null && info.getName().equals(insert.getName())) {
                        insertOffset = str.lastIndexOf(applyfrom);
                        replaceString = applyfrom;
                        break;
                    }
                }
            }

            StringBuffer script = new StringBuffer(str);
            if (insertOffset >= script.length()) {
                insertOffset = script.length();
                insert.applyfrom = System.lineSeparator() + insert.applyfrom;
            }
            if (!replaceString.isEmpty()) {
                script.replace(insertOffset, insertOffset + replaceString.length(), insert.applyfrom);
            } else {
                script.insert(insertOffset, insert.applyfrom + System.lineSeparator());
            }
            FileUtil.writeToFile(script.toString(), buildGradleFile);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

    private ApplyFromInfo makeApplyFromInfo(String line) {
        ApplyFromInfo info = null;

        if (line != null && !line.isEmpty()) {
            int offset = line.indexOf(APPLY_FROM);
            String applyFrom = offset > 0 ? line.substring(offset).trim() : line.trim();

            String[] tokens = null;
            if (applyFrom.indexOf('\'') >= 0 && applyFrom.indexOf('\'') < applyFrom.lastIndexOf('\'')) {
                applyFrom = applyFrom.substring(applyFrom.indexOf('\'') + 1, applyFrom.lastIndexOf('\''));
                tokens = applyFrom.split("_");
            } else {
                tokens = applyFrom.split("_");
                applyFrom = APPLY_FROM + ":'" + applyFrom + "'";
            }

            if (tokens.length > 1) {
                String name = tokens[0];
                String revision = tokens[1].substring(0, tokens[1].lastIndexOf('.'));

                info = new ApplyFromInfo(name, revision, applyFrom);
            } else {
                info = new ApplyFromInfo(tokens[0], "", applyFrom);
            }
        }
        return info;
    }

    public void deleteApplyFrom(File file) {
        BufferedReader br = null;
        try {
            String str = FileUtils.readFileToString(buildGradleFile);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile)));

            int deleteOffset = -1, length = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(file.getName()) >= 0) {
                    deleteOffset = str.lastIndexOf(line);
                    length = line.length() + 1;
                }
            }

            StringBuffer script = new StringBuffer(str);
            if (deleteOffset > 0 && length > 0) {
                script.replace(deleteOffset, deleteOffset + length, "");
            }
            FileUtil.writeToFile(script.toString(), buildGradleFile);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            }
        }
    }

}

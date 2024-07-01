package m.client.ide.morpheus.core.utils;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EclipseProjectNatureUtil {
    public static final String FILENAME_DOT_PROJECT = ".project";
    public static final String nature_application_project = "kr.co.morpheus.ide.frameworks.natures.ApplicationProjectNature";
	public static final String nature_android_project = "org.eclipse.andmore.AndroidNature";
	public static final String nature_jdt_java_project = "org.eclipse.jdt.core.javanature";
	public static final String nature_gradle_project = "org.eclipse.buildship.core.gradleprojectnature";
    public static final String nature_morpheus_spa = "kr.co.morpheus.ide.frameworks.natures.SinglePageApplicationProjectNature";

    public static final @NotNull List<String> getNatures(File file) {
        List<String> natureList = new ArrayList<>();
        if(file == null || !file.getName().equals(FILENAME_DOT_PROJECT)) {
            return natureList;
        }

        Document manifestDoc = XMLUtil.getDocument(file);
        if (manifestDoc != null) {
            Element docElement = manifestDoc.getDocumentElement();

            if (docElement  != null && docElement.getTagName().equals("projectDescription")) {
                Element natures = XMLUtil.getFirstChildElementByName(docElement, "natures");
                NodeList childNodes = natures.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if(node instanceof Element) {
                        Element ele = (Element) node;
                        if(ele.getTagName().equals("nature")) {
                            String nature = ele.getTextContent();
                            if (nature != null && !nature.isEmpty()) {
                                natureList.add(nature);
                            }
                        }
                    }
                }
            }
        }

        return natureList;
    }

    public static boolean isEclipseMorpheusProject(File projectRoot) {
        if(projectRoot == null || !projectRoot.isDirectory()) {
            return false;
        }

        File projectSettingFile = new File(projectRoot, FILENAME_DOT_PROJECT);
        if(!projectSettingFile.exists()) {
            return false;
        }

        @NotNull List<String> natureList = getNatures(projectSettingFile);
        return (natureList.contains(nature_android_project) &&
                natureList.contains(nature_application_project) &&
                natureList.contains(nature_gradle_project) &&
                natureList.contains(nature_jdt_java_project));
    }

    public static boolean isSPAProject(File projectFolder) {
        if(projectFolder == null || !projectFolder.isDirectory()) {
            return false;
        }

        File projectSettingFile = new File(projectFolder, FILENAME_DOT_PROJECT);
        if(!projectSettingFile.exists()) {
            return false;
        }

        @NotNull List<String> natureList = getNatures(projectSettingFile);
        return natureList.contains(nature_morpheus_spa);
    }

}

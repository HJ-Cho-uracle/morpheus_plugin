package m.client.ide.morpheus.framework.eclipse;

import com.esotericsoftware.minlog.Log;
import m.client.ide.morpheus.core.constants.SettingConstants;
import m.client.ide.morpheus.core.resource.TagType;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnExport;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SVNUtil {

    public static void export(SVNURL url, File workingCopy, boolean singleFile) {
        final SvnOperationFactory svnOperationFactory = getOperationFactory();

        /**
         * Comment		: SVN server 에 문제가 있을 경우 기존 정보까지 모두 날아가게 되므로
         * 					temp 파일로 백업, 삭제 후 문제 발생 시 복구 하도록 수정
         * Author		: johyeongjin
         * Datetime		: Nov 24, 2022 2:20:58 PM
         */
        File tempFile = null;
        try {
            if (workingCopy.isFile()) {
                tempFile = new File(workingCopy.getParent(), workingCopy.getName() + System.currentTimeMillis());
                FileUtil.copyFile(workingCopy, tempFile);
                workingCopy.delete();
            }
            try {

                SvnExport export = svnOperationFactory.createExport();
                if (singleFile)
                    export.setSingleTarget(SvnTarget.fromFile(workingCopy, SVNRevision.UNDEFINED));
                else
                    export.addTarget(SvnTarget.fromFile(workingCopy, SVNRevision.UNDEFINED));
                export.setSource(SvnTarget.fromURL(url));
                export.run();
            } catch (SVNException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
                if (tempFile != null) {
                    FileUtil.copyFile(tempFile, workingCopy);
                }
            } finally {
                svnOperationFactory.dispose();
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    public static void checkout(SVNURL url, File workingCopyDirectory, boolean singleFile) {
        final SvnOperationFactory svnOperationFactory = getOperationFactory();
        try {
            SvnCheckout checkout = svnOperationFactory.createCheckout();
            if (singleFile)
                checkout.setSingleTarget(SvnTarget.fromFile(workingCopyDirectory));
            else
                checkout.addTarget(SvnTarget.fromFile(workingCopyDirectory));
            checkout.setSource(SvnTarget.fromURL(url));
            checkout.run();
        } catch (SVNException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        } finally {
            svnOperationFactory.dispose();
        }
    }

    public static SvnOperationFactory getOperationFactory() {
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        ISVNAuthenticationManager authenticationManager = SVNWCUtil.createDefaultAuthenticationManager("client_ide_30",
                "uracle00~!");
        svnOperationFactory.setAuthenticationManager(authenticationManager);
        return svnOperationFactory;
    }

    public static SVNURL getSVNURL(TagType type, String path) throws UnknownHostException {
        String host = null;
        SVNURL url = null;
        int port = 0;

        String docURL = CommonUtil.getDocURLString();
        if (docURL.toLowerCase().startsWith("svn")) {
            host = docURL.substring(docURL.indexOf("//") + 2, docURL.length());
            port = 3690;
        } else {
            host = CommonUtil.getDocURL().getHost();
            port = 80;
        }
        CommonUtil.log(Log.LEVEL_DEBUG, "getSVNURL : " + docURL + ", host : " + host + ", port : " + port);

        if (!CommonUtil.isNetworkAvailable(host, port)) {
            return url;
//			throw new UnknownHostException();
        }

        try {
            String pPath = null;
            if (type != null) {
                pPath = getSVNTagHome() + type.toString() + path;
            } else {
                pPath = getSVNTagHome() + path;
            }

            if (docURL.toLowerCase().startsWith("svn")) {
                url = SVNURL.create("svn", "", host, 3690, pPath, true);
            } else {
                url = SVNURL.create("http", "", host, 80, pPath, true);
            }
        } catch (SVNException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
        return url;
    }

    private static @NotNull String getSVNTagHome() {
        String rMode = PreferenceUtil.getSdkMode();
        String tagHome = CommonUtil.getApplicationProperty("svnTagHome");
        String[] arr = tagHome.split("/");

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];

            if (s.length() > 0) {
                if (i == arr.length - 1) {
                    if (s.equals(SettingConstants.SDK_MODE_MORPHEUS) || s.equals(SettingConstants.SDK_MODE_DEV)) {
                        continue;
                    }
                }

                sb.append(s);
                sb.append("/");
            }

        }
        sb.append(rMode + "/");
        return sb.toString();
    }

    public static void main(String[] args) {
        String docURL = "http://";
        System.out.println(docURL);
    }

    @Contract(pure = true)
    public static @NotNull String getTag(String type, String id, String api, String revision) {
        return "/" + type + "@" + id + "." + api + "." + revision;
    }

    @SuppressWarnings("rawtypes")
    public static @NotNull ArrayList<String> getTagList(TagType tagType) throws UnknownHostException {

        ArrayList<String> tagList = new ArrayList<String>();

        DAVRepositoryFactory.setup();
        SVNRepository repository = null;

        SVNURL svnURL = getSVNURL(tagType, "");

        if (svnURL == null)
            return tagList;

        try {
            repository = SVNRepositoryFactory.create(svnURL);

            ISVNAuthenticationManager authenticationManager = SVNWCUtil.createDefaultAuthenticationManager("", "");
            repository.setAuthenticationManager(authenticationManager);

            Collection entries = null;

            entries = repository.getDir("", -1, null, (Collection) null);

            Iterator iterator = entries.iterator();
            while (iterator.hasNext()) {
                SVNDirEntry entry = (SVNDirEntry) iterator.next();
                String tagName = entry.getName();
                tagList.add(tagName);
            }

        } catch (SVNException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }

        return tagList;
    }
}

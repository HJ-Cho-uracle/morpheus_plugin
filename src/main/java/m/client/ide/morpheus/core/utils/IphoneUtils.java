package m.client.ide.morpheus.core.utils;


import com.intellij.openapi.diagnostic.Logger;
import m.client.ide.morpheus.core.messages.CoreMessages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * iPhone 관련 Util
 *
 * @author hclee
 */
public class IphoneUtils {
    private static final Logger LOG = Logger.getInstance(IphoneUtils.class);

    /**
     * 커맨드 실행.
     *
     * @return
     */
    public static String executeCommand(String[] command) {
        StringBuilder stopOutBuffCheck = new StringBuilder();
//		ExecUtils.exec(stopOutBuffCheck, null, command);
        try {
            ExecCommandUtil.excuteCommand(command);
        } catch (IOException e) {
            LOG.error(e);
        }
        return stopOutBuffCheck.toString();
    }

    /**
     * 애플 개발자 인증서를 리턴한다.
     *
     * @return
     */
    public static List<String> getCertificateIdentities() {
        List<String> certIdentityList = new ArrayList<String>();
        String[] command = new String[]{"security", "find-identity"};
        String result = executeCommand(command);
        StringTokenizer tokens = new StringTokenizer(result, System.getProperty("line.separator"));
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.matches("^(.+)\\\"(.+)\\\"")) {
                token = token.substring(token.indexOf("\"")).replace("\"", "");
                if (!certIdentityList.contains(token)) {
                    certIdentityList.add(token);
                }
            }
        }

        return certIdentityList;
    }

    public static void main(String[] args) {
        getCertificateIdentities();
    }

    public static String checkCertificate(String strIphoneCertificate) {
        if ((strIphoneCertificate == null)
                || (strIphoneCertificate.length() == 0)) {
            return CoreMessages.get(CoreMessages.IphoneUtils_1);
        }

        List<?> certList = getCertificateIdentities();
        if ((certList == null) || (!certList.contains(strIphoneCertificate))) {
            return CoreMessages.get(CoreMessages.IphoneUtils_0);
        }

        return null;
    }
}

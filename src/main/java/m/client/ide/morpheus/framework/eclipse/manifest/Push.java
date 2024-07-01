package m.client.ide.morpheus.framework.eclipse.manifest;

import m.client.ide.morpheus.core.constants.Const;

import java.util.Arrays;
import java.util.List;


public class Push {
    public static final List<String> SERVER_VERSIONS = Arrays.asList(new String[]{"5.0"}); //$NON-NLS-1$
    public static final List<String> AGENT_SERVICE_TYPES = Arrays.asList(new String[]{"inapp"}); //$NON-NLS-1$
    public static final List<String> ANDROID_PUSH_TYPES = Arrays.asList(new String[]{"FCM", "UPNS", "ALL", "MULTI"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    public static final List<String> POLICY_TYPE = Arrays.asList(new String[]{"user", "device"});  //$NON-NLS-1$ //$NON-NLS-2$
    public static final List<String> USE_PHONE_NUMBER = Arrays.asList(new String[]{"Y", "N"}); //$NON-NLS-1$ //$NON-NLS-2$
    public static final List<String> USE_PERMISSION = Arrays.asList(new String[]{"Y", "N"}); //$NON-NLS-1$ //$NON-NLS-2$
    public static final List<String> USE_LOG = Arrays.asList(new String[]{"y", "n"}); //$NON-NLS-1$ //$NON-NLS-2$
    public static final String DEFAULT_TIMEOUT = "20000"; //$NON-NLS-1$
    public static final String DEFAULT_AGENT_RESTART_INTERVAL = "120"; //$NON-NLS-1$
    public static final String DEFAULT_RECONNECT_INTERVAL = "10,20,30"; //$NON-NLS-1$
    public static final String DEFAULT_REALLOCATE_INTERVAL = "10,30,50"; //$NON-NLS-1$
    public static final String DEFAULT_RETRY_REGIST_COUNT = "3"; //$NON-NLS-1$

    private String log;
    private String securityIndexes;
    private String version;
    private String server;
    private String timeout;
    private String fcmSenderId;
    private String androidPushType;
    private String policy;
    private String usePhoneNumber;
    private String usePermission;

    private String agentServiceType;
    private String agentRestartInterval;
    private String reconnectInterval;
    private String reallocateInterval;
    private String retryRegistCount;

    public Push() {
        this.log = USE_LOG.get(0);
        this.securityIndexes = Const.EMPTY_STRING;
        this.version = SERVER_VERSIONS.get(0);
        this.server = Const.EMPTY_STRING;
        this.timeout = DEFAULT_TIMEOUT;
        this.fcmSenderId = Const.EMPTY_STRING;
        this.androidPushType = ANDROID_PUSH_TYPES.get(2);
        this.policy = POLICY_TYPE.get(1);
        this.usePhoneNumber = USE_PHONE_NUMBER.get(1);
        this.usePermission = USE_PERMISSION.get(0);

        this.agentServiceType = AGENT_SERVICE_TYPES.get(0);
        this.agentRestartInterval = Const.EMPTY_STRING;
        this.reconnectInterval = DEFAULT_RECONNECT_INTERVAL;
        this.reallocateInterval = DEFAULT_REALLOCATE_INTERVAL;
        this.retryRegistCount = DEFAULT_RETRY_REGIST_COUNT;
        this.agentRestartInterval = DEFAULT_AGENT_RESTART_INTERVAL;
    }

    public Push(String log, String securityIndexes, String version, String server, String timeout, String fcmSenderId,
                String androidPushType, String policy, String usePhoneNumber, String usePermission, String agentServiceType, String agentRestartInterval,
                String reconnectInterval, String reallocateInterval, String retryRegistCount) {
        this.log = log;
        this.securityIndexes = securityIndexes;
        this.version = version;
        this.server = server;
        this.timeout = timeout;
        this.fcmSenderId = fcmSenderId;
        this.androidPushType = androidPushType;
        this.policy = policy;
        this.usePhoneNumber = usePhoneNumber;
        this.usePermission = usePermission;

        this.agentServiceType = agentServiceType;
        this.agentRestartInterval = agentRestartInterval;
        this.reconnectInterval = reconnectInterval;
        this.reallocateInterval = reallocateInterval;
        this.retryRegistCount = retryRegistCount;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getSecurityIndexes() {
        return securityIndexes;
    }

    public void setSecurityIndexes(String securityIndexes) {
        this.securityIndexes = securityIndexes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getFcmSenderId() {
        return fcmSenderId;
    }

    public void setFcmSenderId(String fcmSenderId) {
        this.fcmSenderId = fcmSenderId;
    }

    public String getAndroidPushType() {
        return androidPushType;
    }

    public void setAndroidPushType(String androidPushType) {
        this.androidPushType = androidPushType;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getUsePhoneNumber() {
        return usePhoneNumber;
    }

    public void setUsePhoneNumber(String usePhoneNumber) {
        this.usePhoneNumber = usePhoneNumber;
    }

    public String getUsePermission() {
        return usePermission;
    }

    public void setUsePermission(String usePermission) {
        this.usePermission = usePermission;
    }

    public String getAgentServiceType() {
        return agentServiceType;
    }

    public void setAgentServiceType(String agentServiceType) {
        this.agentServiceType = agentServiceType;
    }

    public String getAgentRestartInterval() {
        return agentRestartInterval;
    }

    public void setAgentRestartInterval(String agentRestartInterval) {
        this.agentRestartInterval = agentRestartInterval;
    }

    public String getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(String reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public String getReallocateInterval() {
        return reallocateInterval;
    }

    public void setReallocateInterval(String reallocateInterval) {
        this.reallocateInterval = reallocateInterval;
    }

    public String getRetryRegistCount() {
        return retryRegistCount;
    }

    public void setRetryRegistCount(String retryRegistCount) {
        this.retryRegistCount = retryRegistCount;
    }

}

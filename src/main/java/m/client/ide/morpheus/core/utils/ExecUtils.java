package m.client.ide.morpheus.core.utils;

import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ExecUtils {
    private static final Logger LOG = Logger.getInstance(ExecUtils.class);

    public static final int EXIT_CODE_OK = 0;
    public static final int EXIT_CODE_ERROR = -2147483648;


    private ExecUtils() {
    }


    public static String[] removeSpaceItems(String[] stringArr) {
        if (stringArr == null) {
            return null;
        }

        if (stringArr.length == 0) {
            return new String[0];
        }

        List<String> ret = new ArrayList<String>();
        int nLen = stringArr.length;
        for (int i = 0; i < nLen; i++) {
            if (stringArr[i] == null) {
                continue;
            }
            if (stringArr[i].trim().length() == 0) {
                continue;
            }
            ret.add(stringArr[i]);
        }

        return (String[]) ret.toArray(new String[ret.size()]);
    }


    public static synchronized String exec(StringBuffer out, StringBuffer err, String[] cmdarray) {
        return exec(out, err, cmdarray, null);
    }

    public static synchronized String exec(StringBuffer out, StringBuffer err, String[] cmdarray, File dir) {
        return exec(out, err, cmdarray, null, dir, false);
    }

    public static synchronized String exec(StringBuffer out, StringBuffer err, String[] cmdarray, File dir, boolean useConsole) {
        return exec(out, err, cmdarray, null, dir, useConsole);
    }

    public static synchronized String exec(StringBuffer out, StringBuffer err, String[] cmdarray, Map<String, String> envs, File dir) {
        return exec(out, err, cmdarray, envs, dir, false);
    }

    public static synchronized String exec(StringBuffer out, StringBuffer err, String[] cmdarray, Map<String, String> envs, File dir, boolean useConsole) {
        Process process = null;
        try {
            String[] commandArr = removeSpaceItems(cmdarray);
            ProcessBuilder processBuilder = new ProcessBuilder(commandArr);


            if (dir != null) {
                processBuilder.directory(dir);
            }

            if (envs != null) {
                Map<String, String> sysEnv = processBuilder.environment();
                Iterator<String> iter = envs.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = envs.get(key);

                    String sysValue = sysEnv.get(key);
                    if (sysValue == null) {
                        sysValue = value;
                    } else {
                        if (!sysValue.endsWith(":")) {
                            sysValue += ":";
                        }
                        sysValue += value;
                    }
                    sysEnv.put(key, sysValue);
                }
            }

            process = processBuilder.start();

            process.waitFor();
            int nRet = process.exitValue();

            StringBuffer errorBuffer = new StringBuffer();
            StringBuffer inputBuffer = new StringBuffer();
            if (process.exitValue() != 0) {
                InputStream errorStream = process.getErrorStream();
                int c = 0;
                while ((c = errorStream.read()) != -1) {
                    errorBuffer.append(c);
                }
                InputStream inputStream = process.getInputStream();
                while ((c = inputStream.read()) != -1) {
                    inputBuffer.append(c);
                }
            }
            process.destroy();

            String errorString = errorBuffer.toString();
            if (errorString != null && errorString.length() > 0) {
                LOG.error(errorString);
            }

            return inputBuffer.toString();
        } catch (Exception ex) {
            if (err != null)
                err.append(ex.getMessage());
        } finally {
            try {
                if (process != null) {
                    if (process.getOutputStream() != null) process.getOutputStream().close();
                    if (process.getInputStream() != null) process.getInputStream().close();
                    if (process.getErrorStream() != null) process.getErrorStream().close();
                    process.destroy();
                }
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        return null;
    }

    public static synchronized void execAsync(String[] cmdarray) {
        try {
            String[] commandArr = removeSpaceItems(cmdarray);
            ProcessBuilder processBuilder = new ProcessBuilder(commandArr);
            Process process = processBuilder.start();
            process.getErrorStream().close();
            process.getInputStream().close();
            process.getOutputStream().close();
            process.waitFor();

        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public static synchronized void execAsync(String[] cmdarray, File dir) {
        try {
            String[] commandArr = removeSpaceItems(cmdarray);
            ProcessBuilder processBuilder = new ProcessBuilder(commandArr);
            processBuilder.directory(dir);
            Process process = processBuilder.start();
            process.getErrorStream().close();
            process.getInputStream().close();
            process.getOutputStream().close();
            process.waitFor();

        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
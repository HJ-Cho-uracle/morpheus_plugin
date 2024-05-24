package m.client.ide.morpheus.core.utils;

import com.intellij.openapi.progress.ProgressIndicator;
import m.client.ide.morpheus.core.messages.CoreMessages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private static final int BUFFER_SIZE = 4096;
    public static final String extension = ".zip";

    private static void extractFile(ZipInputStream in, File outdir, String name)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(new File(outdir, name)));
        int count = -1;
        while ((count = in.read(buffer)) != -1)
            out.write(buffer, 0, count);
        out.close();
    }

    private static void mkdirs(File outdir, String path) {
        File d = new File(outdir, path);
        if (!d.exists())
            d.mkdirs();
    }

    private static String dirpart(String name) {
        int s = name.lastIndexOf(File.separatorChar);
        return s == -1 ? null : name.substring(0, s);
    }

    /**
     * zip 파일 압축을 푼다.
     *
     * @param zipfile : 압축된 파일
     * @param outdir  : 압축을 풀 디렉터리
     * @throws IOException
     */
    public static void extract(File zipfile, File outdir, ProgressMonitor monitor) throws IOException {
        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
        ZipEntry entry;
        String name, dir;

        while ((entry = zin.getNextEntry()) != null) {
            name = entry.getName();

            if (entry.isDirectory()) {
                mkdirs(outdir, name);
                continue;
            }
            dir = dirpart(name);
            if (dir != null)
                mkdirs(outdir, dir);

            extractFile(zin, outdir, name);
        }
        zin.close();
    }


    /**
     * 폴더를 압축 한다.
     *
     * @param dirName     : 압축하려는 디렉터리
     * @param nameZipFile : 경로를 포함한 압축 파일 이름
     * @throws IOException
     */
    public static void compress(String dirName, String nameZipFile, @NotNull ProgressIndicator indicator) throws IOException {
        List<File> fileList = new ArrayList<File>();
        generateFileList(new File(dirName), fileList, dirName);

        indicator.setText(CoreMessages.get(CoreMessages.ZipUtils));

        byte[] buffer = new byte[1024];
        FileOutputStream fos = new FileOutputStream(nameZipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (File file : fileList) {
            String exportPath = generateZipEntry(dirName, file);
            indicator.setText2(exportPath);
            ZipEntry ze = new ZipEntry(exportPath);
            zos.putNextEntry(ze);

            FileInputStream in = new FileInputStream(file);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            in.close();
        }
        zos.closeEntry();
        zos.close();
    }

    private static void generateFileList(@NotNull File node, List<File> fileList, String sourceDir) {
        if (node.isFile()) {
            fileList.add(node);
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename), fileList, sourceDir);
            }
        }
    }

    private static @NotNull String generateZipEntry(@NotNull String sourceDir, @NotNull File file) {
        File source = new File(sourceDir);
        String filePath = source.toPath().relativize(file.toPath()).toString();
        return filePath;
    }
}
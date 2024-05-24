package m.client.ide.morpheus.core.utils;

import com.intellij.openapi.progress.ProgressIndicator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class CompressionUtil {
	private static final String defaultCharsetName = "utf-8";

	public static void addLibraryFileToJar(File source, JarOutputStream target) throws IOException {
		BufferedInputStream in = null;
		try {
//			if (source.isDirectory()) {
//				String name = source.getCanonicalPath()
//						.substring(parentDir.getCanonicalPath().length() + 1, source.getCanonicalPath().length()).replace("\\", "/");
//				if (!name.isEmpty()) {
//					if (!name.endsWith("/"))
//						name += "/";
//					JarEntry entry = new JarEntry(name);
//					entry.setTime(source.lastModified());
//					target.putNextEntry(entry);
//					target.closeEntry();
//				}
//				for (File nestedFile : source.listFiles())
//					addToJar(nestedFile, target);
//				return;
//			}

			String name = CommonUtil.getPathString("lib", "armeabi", source.getName()).replace("\\", "/");  
			JarEntry entry = new JarEntry(name);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));

			byte[] buffer = new byte[1024];
			while (true) {
				int count = in.read(buffer);
				if (count == -1)
					break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		} finally {
			if (in != null)
				in.close();
		}
	}

	public static void unzip(File zippedFile) throws IOException {
		unzip(zippedFile, defaultCharsetName);
	}

	public static void unzip(File zippedFile, String charsetName) throws IOException {
		unzip(zippedFile, zippedFile.getParentFile(), charsetName);
	}

	public static void unzip(File zippedFile, File destDir) throws IOException {
		unzip(new FileInputStream(zippedFile), destDir, defaultCharsetName);
	}

	public static void unzip(File zippedFile, File destDir, String charsetName) throws IOException {
		unzip(new FileInputStream(zippedFile), destDir, charsetName);
	}

	public static void unzip(InputStream is, File destDir) throws IOException {
		unzip(is, destDir, defaultCharsetName);
	}

	public static void unzip(InputStream is, File destDir, String charsetName) throws IOException {
		ZipArchiveInputStream zis = null;
		ZipArchiveEntry entry;
		String name;
		File target;
		int nWritten = 0;
		BufferedOutputStream bos = null;
		byte[] buf = new byte[1024];

		zis = new ZipArchiveInputStream(is, charsetName, false);
		while ((entry = zis.getNextZipEntry()) != null) {
			name = entry.getName();
			target = new File(destDir, name);
			if (entry.isDirectory()) {
				target.mkdirs();
			} else {
				target.getParentFile().mkdirs();
				target.createNewFile();
				bos = new BufferedOutputStream(new FileOutputStream(target));
				while ((nWritten = zis.read(buf)) >= 0) {
					bos.write(buf, 0, nWritten);
				}
				bos.close();
			}
		}
		zis.close();
	}

	public static boolean hasFiles(File zippedFile, ArrayList<String> fileList) throws IOException {
		return hasFiles(new FileInputStream(zippedFile), defaultCharsetName, fileList);
	}

	public static boolean hasFiles(InputStream is, String charsetName, ArrayList<String> fileList) throws IOException {
		boolean hasFiles = false;
		ZipArchiveInputStream zis = null;
		ZipArchiveEntry entry;
		String name;

		zis = new ZipArchiveInputStream(is, charsetName, false);
		while ((entry = zis.getNextZipEntry()) != null) {
			name = entry.getName();
			fileList.contains(name);
			fileList.remove(name);
		}
		zis.close();

		if (fileList.size() == 0)
			hasFiles = true;

		return hasFiles;
	}
	
	public static boolean hasFile(File file, String fileName) throws IOException {
		ZipArchiveInputStream zis = new ZipArchiveInputStream(new FileInputStream(file), defaultCharsetName, false);
		boolean hasFile = hasFile(zis, fileName);
		zis.close();
		return hasFile;
	}
	
	public static boolean hasFile(ZipArchiveInputStream zis, String fileName) throws IOException {
		ZipArchiveEntry entry;
		while ((entry = zis.getNextZipEntry()) != null) {
			String name = entry.getName();
			if(fileName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * compresses the given file(or dir) and creates new file under the same
	 * directory.
	 * 
	 * @param src
	 *            file or directory
	 * @throws IOException
	 */
	public static void zip(File src) throws IOException {
		zip(src, defaultCharsetName, true);
	}

	/**
	 * zips the given file(or dir) and create
	 * 
	 * @param src
	 *            file or directory to compress
	 * @param includeSrc
	 *            if true and src is directory, then src is not included in the
	 *            compression. if false, src is included.
	 * @throws IOException
	 */
	public static void zip(File src, boolean includeSrc) throws IOException {
		zip(src, defaultCharsetName, includeSrc);
	}

	/**
	 * compresses the given src file (or directory) with the given encoding
	 * 
	 * @param src
	 * @param charSetName
	 * @param includeSrc
	 * @throws IOException
	 */
	public static void zip(File src, String charSetName, boolean includeSrc) throws IOException {
		zip(src, src.getParentFile(), charSetName, includeSrc);
	}

	/**
	 * compresses the given src file(or directory) and writes to the given
	 * output stream.
	 * 
	 * @param src
	 * @param os
	 * @throws IOException
	 */
	public static void zip(File src, OutputStream os) throws IOException {
		zip(src, os, defaultCharsetName, true);
	}

	/**
	 * compresses the given src file(or directory) and create the compressed
	 * file under the given destDir.
	 * 
	 * @param src
	 * @param destFile
	 * @param charSetName
	 * @param includeSrc
	 * @throws IOException
	 */

	public static void zip(File src, File destFile, String charSetName, boolean includeSrc) throws IOException {
		if (!destFile.exists())
			destFile.createNewFile();
		zip(src, new FileOutputStream(destFile), charSetName, includeSrc);
	}

	public static void zip(File src, File destFile, ProgressIndicator monitor) throws IOException {
		if (!destFile.exists())
			destFile.createNewFile();
		zip(src, new FileOutputStream(destFile), defaultCharsetName, false, monitor);
	}

	public static int getWorkCount(File src, int count) {
		File[] files = src.listFiles();

		if (files != null)
			for (File file : files) {
				if (file.isDirectory()) {
					count = getWorkCount(file, count);
				} else {
					count += 1;
				}
			}
		return count;
	}

	public static void zip(File src, OutputStream os, String charsetName, boolean includeSrc, ProgressIndicator monitor) throws IOException {
		ZipArchiveOutputStream zos = new ZipArchiveOutputStream(os);
		zos.setEncoding(charsetName);
		FileInputStream fis;

		int length;
		ZipArchiveEntry ze;
		byte[] buf = new byte[8 * 1024];
		String name;

		Stack<File> stack = new Stack<File>();
		File root;
		if (src.isDirectory()) {
			if (includeSrc) {
				stack.push(src);
				root = src.getParentFile();
			} else {
				File[] fs = src.listFiles();
				for (int i = 0; i < fs.length; i++) {
					stack.push(fs[i]);
				}
				root = src;
			}
		} else {
			stack.push(src);
			root = src.getParentFile();
		}

		while (!stack.isEmpty()) {
			File f = stack.pop();
			name = toPath(root, f);
			if (f.isDirectory()) {
				File[] fs = f.listFiles();
				for (int i = 0; i < fs.length; i++) {
					if (fs[i].isDirectory())
						stack.push(fs[i]);
					else
						stack.add(0, fs[i]);
				}
			} else {
				ze = new ZipArchiveEntry(name);
				zos.putArchiveEntry(ze);
				fis = new FileInputStream(f);
				while ((length = fis.read(buf, 0, buf.length)) >= 0) {
					zos.write(buf, 0, length);
				}
				fis.close();
				zos.closeArchiveEntry();
			}
		}
		zos.close();
	}

	public static void zip(File src, OutputStream os, String charsetName, boolean includeSrc) throws IOException {
		ZipArchiveOutputStream zos = new ZipArchiveOutputStream(os);
		zos.setEncoding(charsetName);
		FileInputStream fis;

		int length;
		ZipArchiveEntry ze;
		byte[] buf = new byte[8 * 1024];
		String name;

		Stack<File> stack = new Stack<File>();
		File root;
		if (src.isDirectory()) {
			if (includeSrc) {
				stack.push(src);
				root = src.getParentFile();
			} else {
				File[] fs = src.listFiles();
				for (int i = 0; i < fs.length; i++) {
					stack.push(fs[i]);
				}
				root = src;
			}
		} else {
			stack.push(src);
			root = src.getParentFile();
		}

		while (!stack.isEmpty()) {
			File f = stack.pop();
			name = toPath(root, f);
			if (f.isDirectory()) {
				File[] fs = f.listFiles();
				for (int i = 0; i < fs.length; i++) {
					if (fs[i].isDirectory())
						stack.push(fs[i]);
					else
						stack.add(0, fs[i]);
				}
			} else {
				ze = new ZipArchiveEntry(name);
				zos.putArchiveEntry(ze);
				fis = new FileInputStream(f);
				while ((length = fis.read(buf, 0, buf.length)) >= 0) {
					zos.write(buf, 0, length);
				}
				fis.close();
				zos.closeArchiveEntry();
			}
		}
		zos.close();
	}

	private static String toPath(File root, File dir) {
		String path = dir.getAbsolutePath();
		path = path.substring(root.getAbsolutePath().length()).replace(File.separatorChar, '/');
		if (path.startsWith("/"))
			path = path.substring(1);
		if (dir.isDirectory() && !path.endsWith("/"))
			path += "/";
		return path;
	}
}

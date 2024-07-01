package m.client.ide.morpheus.core.utils;


import com.esotericsoftware.minlog.Log;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.plugins.PluginSet;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import m.client.ide.morpheus.Constants;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import net.minidev.json.parser.ParseException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.*;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipInputStream;

public class FileUtil {
	private static final Logger LOG = Logger.getInstance(FileUtil.class);

	/**
	 * 파일 확장자 리턴.
	 * @param file
	 * @return
	 */
	public static String getFileExtension(@NotNull File file) {
		String name = file.getName();		
		return getFileExtension(name);
	}
	
	public static String getFileExtension(@NotNull String name) {
		String ext = "";
		if(name.contains(".")) {
			ext = name.substring(name.lastIndexOf(".") + 1, name.length());
		}
		return ext;
	}


	public static void copyFile(File source, @NotNull File target) throws IOException {
		File parent = target.getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}

		if(!target.exists()) {
			writeToFile(source, target);
		} else {
			FileUtils.copyFile(source, target);
		}
	}

	public static boolean deleteDirectory(@NotNull File path) {
        if(!path.exists()) {
            return false;
        }
        
        if(path.isDirectory()) {
        	File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        
        return path.delete();
    }
	
	public static String copyDirectory(@NotNull File sorceDir, @NotNull File dstDir) {
		String[] command = {"rsync", "-av", "--delete", 
				"--exclude=.project", "--exclude=.svn", "--exclude=.DS_Store", "--exclude=config.emulator.json",
				sorceDir.getAbsolutePath() + File.separator, dstDir.getAbsolutePath() + File.separator};
		StringBuffer out = new StringBuffer();
		StringBuffer err = new StringBuffer();
		
		StringBuffer sb = new StringBuffer();
		for(String s : command) {
			sb.append(s);
			sb.append(" ");
		}		
		return ExecUtils.exec(out, err, command);
	}
	
	/**
	 * 폴더가 포함된 파일을 복사한다.
	 * @param sourceLocation 원본파일
	 * @param targetLocation 복사될 파일
	 * @throws IOException
	 */
	public static void copyDirectory3(@NotNull File sourceLocation, File targetLocation)	throws IOException {
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.isDirectory()) {
				targetLocation.mkdirs();
			}
			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {
			if (!targetLocation.getParentFile().isDirectory()) {
				targetLocation.getParentFile().mkdirs();
			}
			
			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	@NotNull
	public static String toSystemDependentName(@NotNull String path) {
		return toSystemDependentName(path, File.separatorChar);
	}

	@NotNull
	public static String toSystemDependentName(@NotNull String path, char separatorChar) {
		return path.replace('/', separatorChar).replace('\\', separatorChar);
	}

	public static @Nullable Project getProject(File file) {
		VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
		if(virtualFile != null) {
			return ProjectLocator.getInstance().guessProjectForFile(virtualFile);
		}

		return null;
	}

	public static File getChildFile(@NotNull Project project, @NotNull String childName) {
		@Nullable @SystemIndependent @NonNls String path = project.getBasePath();
		File projectFile = new File(path);

		return getChildFile(projectFile, childName.split(File.separator));
	}

	public static File getChildFile(@NotNull File parent, @NotNull String childName) {
		return getChildFile(parent, childName.split(File.separator));
	}

	public static File getChildFile(@NotNull Project project, String... paths) {
		@Nullable @SystemIndependent @NonNls String path = project.getBasePath();
		File projectFile = new File(path);

		return FileUtils.getFile(projectFile, paths);
	}

	public static File getChildFile(File parent, String... paths) {
		return FileUtils.getFile(parent, paths);
	}

	public static File getBundleFile(String path) {
		return getBundleFile(Constants.MORPHEUS_PLUGIN_ID, path);
	}

	public static @Nullable File getBundleFile(String pluginId, String path) {
		PluginSet pluginSet = PluginManagerCore.getPluginSet();
		IdeaPluginDescriptorImpl result = null;
		if (pluginSet != null && pluginId.contains(".")) {
			Iterator var3 = pluginSet.allPlugins.iterator();

			while(var3.hasNext()) {
				IdeaPluginDescriptorImpl descriptor = (IdeaPluginDescriptorImpl)var3.next();
				CommonUtil.log(Log.LEVEL_DEBUG, String.valueOf(descriptor.getPluginId()));
				if (String.valueOf(descriptor.getPluginId()).equals(pluginId)) {
					result = descriptor;
					break;
				}
			}
		}

		if(result != null) {
			return new File(result.getPath(), path);
		} else {
			Log.debug("FileUtil.getBundleFile( " + pluginId + " ) is null!!");
		}

		return null;
	}

	public static void writeToFile(String srcString, String path) {
		File file = new File(path);
		writeToFile(srcString, file);
	}
	
	public static void writeToFile(String srcString, @NotNull File file) {
		if(!file.getParentFile().isDirectory()) {
			file.getParentFile().mkdirs();
		}
		BufferedWriter writer = null;
		try	{
		    writer = new BufferedWriter(new FileWriter(file));
		    writer.write(srcString);
		} catch (IOException e) {
			e.printStackTrace();
		} finally	{
		    try {
		        if ( writer != null)
		        writer.close( );
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
	}

	public static void writeToFile(File srcFile, File destFile) {
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), "utf-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), "utf-8"));

			String line = null;

			while ((line = br.readLine()) != null) {
				bw.write(line + "\n");
			}
			bw.flush();
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			try {
				if (br != null)
					br.close();
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
	}

	public static @NotNull String readFileAsString(File file) throws IOException {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}
	
	public static @NotNull String getFileContent(InputStream fis, String encoding) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
	}

	public static boolean openEditor(VirtualFile file) {
		return openEditor(file, 0, 0);
	}

	public static boolean openEditor(VirtualFile file, int line, int column) {
		Project project = getOpenProject();
		if(project == null) { return false; }

		FileEditorManager manager = FileEditorManager.getInstance(project);
		if(manager == null) { return false; }

		OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file, line, column);
		return !manager.openEditor(descriptor, true).isEmpty();
	}

	private static @Nullable  Project getOpenProject() {
		ProjectManager projectmanager = ProjectManager.getInstance();
		@NotNull Project[] projects = projectmanager.getOpenProjects();

		return projects.length > 0 ? projects[0] : null;
	}

	@Contract("_ -> new")
	public static @NotNull File getResourceFile(@NotNull String path) {
		URL file = FileUtil.class.getResource(path);
		CommonUtil.log(Log.LEVEL_DEBUG, "FileUtil.getResourceFile(\"" + path + "\") --------> " + file);
		return new File(file.getFile());
	}


	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final int EOF = -1;
	/**
	 * The file copy buffer size (30 MB)
	 */
	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;
	private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

	public static boolean downloadFileWithURL(@NotNull URL url, File destFile) {
		// TODO Auto-generated method stub
		InputStream input = null;
		try {
			input = url.openStream();

			FileOutputStream output = openOutputStream(destFile, false);
			try {
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

				int n = 0;
				while (EOF != (n = input.read(buffer))) {
					output.write(buffer, 0, n);
				}
				output.close(); // don't swallow close Exception if copy completes normally
			} catch (IOException e) {
				CommonUtil.log(Log.LEVEL_ERROR, "Copy URL to File failed : " + e.getLocalizedMessage(), e); // $NON-NLS-1#
				return false;
			} finally {
				IOUtils.closeQuietly(output);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			CommonUtil.log(Log.LEVEL_ERROR, "Copy URL to File failed" + e.getLocalizedMessage(), e); // $NON-NLS-1#
			return false;
		} finally {
			IOUtils.closeQuietly(input);
		}

		return true;
	}

	@Contract("_, _ -> new")
	public static @NotNull FileOutputStream openOutputStream(@NotNull File file, boolean append) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file + "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					throw new IOException("Directory '" + parent + "' could not be created");
				}
			}
		}
		return new FileOutputStream(file, append);
	}

	/**
	 * MethodName	: extract
	 * ClassName	: AndroidSDKInstallUtils
	 * Commnet		:
	 * Author		: johyeongjin
	 * Datetime		: Jan 10, 2022 8:43:26 AM
	 *
	 * @param zipfile
	 * @param outdir
	 * @return boolean
	 * @return
	 * @throws IOException
	 */
	public static boolean extract(File zipfile, File outdir) throws IOException {
		// 파일 정상적으로 압축이 해제가 되어는가.
		boolean isChk = false;

		FileInputStream fis = null;
		ZipArchiveInputStream zis = null;
		ArchiveEntry zipentry = null;
		try {
			int entryCount = getZipEntryCount(zipfile);
			if (entryCount <= 0) {
				return isChk;
			}
			// zipFileName을 통해서 폴더 만들기
			if (makeFolder(outdir) == false) {
				return isChk;
			}
			// 파일 스트림
			fis = new FileInputStream(zipfile);
			// Zip 파일 스트림
			zis = new ZipArchiveInputStream(fis);
			// 압축되어 있는 ZIP 파일의 목록 조회
			while ((zipentry = zis.getNextEntry()) != null) {
				String filename = zipentry.getName();
				File file = new File(outdir, filename);
				// entiry가 폴더면 폴더 생성
				if (zipentry.isDirectory()) {
					file.mkdirs();
				} else {
					try {
						createFile(file, zis);
					} catch (Throwable e) {
						CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
					}
				}
			}
			isChk = true;
		} catch (Exception e) {
			CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
			isChk = false;
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
		return isChk;
	}

	private static int getZipEntryCount(File zipfile) {
		// TODO Auto-generated method stub
		int count = 0;
		FileInputStream fis = null;
		ZipInputStream zis = null;
		try {
			// 파일 스트림
			fis = new FileInputStream(zipfile);
			// Zip 파일 스트림
			zis = new ZipInputStream(fis);
			CommonUtil.log(Log.LEVEL_DEBUG, zipfile.getName() + " available : " + zis.available());
			// 압축되어 있는 ZIP 파일의 목록 조회
			while (zis.getNextEntry() != null) {
				count++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}

		return count;
	}

	private static boolean makeFolder(File outdir) {
		if (outdir.length() < 0) {
			return false;
		}

		if (!outdir.exists()) {
			try {
				outdir.mkdir(); // 폴더 생성합니다.
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		return true;
	}

	private static void createFile(File file, ZipArchiveInputStream zis) throws Throwable {
		// 디렉토리 확인
		File parentDir = new File(file.getParent());
		// 디렉토리가 없으면 생성하자
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		FileOutputStream fos = null;
		// 파일 스트림 선언
		try {
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[256];
			int size = 0;
			// Zip스트림으로부터 byte뽑아내기
			while ((size = zis.read(buffer)) > 0) {
				// byte로 파일 만들기
				fos.write(buffer, 0, size);
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Copies a filtered directory to a new location.
	 * <p>
	 * This method copies the contents of the specified source directory to within
	 * the specified destination directory.
	 * <p>
	 * The destination directory is created if it does not exist. If the destination
	 * directory did exist, then this method merges the source with the destination,
	 * with the source taking precedence.
	 * <p>
	 * <strong>Note:</strong> Setting <code>preserveFileDate</code> to {@code true}
	 * tries to preserve the files' last modified date/times using
	 * {@link File#setLastModified(long)}, however it is not guaranteed that those
	 * operations will succeed. If the modification operation fails, no indication
	 * is provided.
	 *
	 * <h4>Example: Copy directories only</h4>
	 *
	 * <pre>
	 * // only copy the directory structure
	 * FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY, false);
	 * </pre>
	 *
	 * <h4>Example: Copy directories and txt files</h4>
	 *
	 * <pre>
	 * // Create a filter for ".txt" files
	 * IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
	 * IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
	 *
	 * // Create a filter for either directories or ".txt" files
	 * FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
	 *
	 * // Copy using the filter
	 * FileUtils.copyDirectory(srcDir, destDir, filter, false);
	 * </pre>
	 *
	 * @param srcDir           an existing directory to copy, must not be
	 *                         {@code null}
	 * @param destDir          the new directory, must not be {@code null}
	 * @param filter           the filter to apply, null means copy all directories
	 *                         and files
	 * @param preserveFileDate true if the file date of the copy should be the same
	 *                         as the original
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 * @since 1.4
	 */
	public static void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (srcDir.exists() == false) {
			throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
		}
		if (srcDir.isDirectory() == false) {
			throw new IOException("Source '" + srcDir + "' exists but is not a directory");
		}
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		}

		// Cater for destination being directory within the source directory (see
		// IO-141)
		List<String> exclusionList = null;
		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
			File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
			if (srcFiles != null && srcFiles.length > 0) {
				exclusionList = new ArrayList<String>(srcFiles.length);
				for (File srcFile : srcFiles) {
					File copiedFile = new File(destDir, srcFile.getName());
					exclusionList.add(copiedFile.getCanonicalPath());
				}
			}
		}

		doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
	}

	/**
	 * Internal copy directory method.
	 *
	 * @param srcDir           the validated source directory, must not be
	 *                         {@code null}
	 * @param destDir          the validated destination directory, must not be
	 *                         {@code null}
	 * @param filter           the filter to apply, null means copy all directories
	 *                         and files
	 * @param preserveFileDate whether to preserve the file date
	 * @param exclusionList    List of files and directories to exclude from the
	 *                         copy, may be null
	 * @throws IOException if an error occurs
	 * @since 1.1
	 */
	private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate,
										List<String> exclusionList) throws IOException {
		// recurse
		File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
		if (srcFiles == null) { // null if abstract pathname does not denote a directory, or if an I/O error
			// occurs
			throw new IOException("Failed to list contents of " + srcDir);
		}
		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
			}
		} else {
			if (!destDir.mkdirs() && !destDir.isDirectory()) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created");
			}
		}
		if (destDir.canWrite() == false) {
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}
		for (File srcFile : srcFiles) {
			File dstFile = new File(destDir, srcFile.getName());
			if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
				if (srcFile.isDirectory()) {
					doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
				} else {
					doCopyFile(srcFile, dstFile, preserveFileDate);
				}
			}
		}

		// Do this last, as the above has probably affected directory metadata
		if (preserveFileDate) {
			destDir.setLastModified(srcDir.lastModified());
		}
	}

	/**
	 * Internal copy file method.
	 *
	 * @param srcFile          the validated source file, must not be {@code null}
	 * @param destFile         the validated destination file, must not be
	 *                         {@code null}
	 * @param preserveFileDate whether to preserve the file date
	 * @throws IOException if an error occurs
	 */
	public static void doCopyFile(File srcFile, @NotNull File destFile, boolean preserveFileDate) throws IOException {
		if (destFile.exists()) {
			if (destFile.isDirectory()) {
				throw new IOException("Destination '" + destFile + "' exists but is a directory");
			} else if (destFile.canWrite() == false) {
				destFile.delete();
			}
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel input = null;
		FileChannel output = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			input = fis.getChannel();
			output = fos.getChannel();
			long size = input.size();
			long pos = 0;
			long count = 0;
			while (pos < size) {
				count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
				pos += output.transferFrom(input, pos, count);
			}
		} catch (Exception e) {
			CommonUtil.log(Log.LEVEL_ERROR, "Copy File failed : " + e.getLocalizedMessage(), e); // $NON-NLS-1#
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(fis);
		}

		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
		}

		if (OSUtil.isMac() && destFile.getName().indexOf('.') < 0) {
			destFile.setExecutable(true);
		}

		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	public static void writeUTF8ToFile(@NotNull String contents, @NotNull File toFile) throws IOException {
		if(!toFile.exists()) {
			toFile.createNewFile();
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toFile), StandardCharsets.UTF_8));

		String line;
		while ((line = br.readLine()) != null) {
			bw.write(line + "\n");
		}
		bw.flush();
	}

	public static String readResFile(String fileName) {
		try {
			InputStream in = null;
			try {
				in = LibraryParam.class.getResourceAsStream(fileName);
				byte[] buff = new byte[1024];

				String contents = "";
				for (int count = 0; (count = in.read(buff)) != -1; ) {
					contents += new String(buff, 0, count);
				}

				return contents;
			} finally {
				if (in != null) in.close();
			}
		} catch (IOException e) {
			CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
		}

		return "";
	}

}

package m.client.ide.morpheus.core.initializers;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InitializationUtils {
	private static final Logger LOG = Logger.getInstance(InitializationUtils.class);

	public static Date getCurrentDate() throws IOException, ParseException {
		final String host = "http://docs.morpheus.kr/ide/getTime.php";

		Calendar cal = Calendar.getInstance();
		Date currentDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		StringBuilder sb = new StringBuilder();
		int responseCode = 0;

		try {
			URL url = new URL(host);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");

			responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;

			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();

		} catch (UnknownHostException e) {
			// TODO: handle exception
			LOG.error(e);
		} finally {
			if (responseCode == 200) {
				currentDate = sdf.parse(sb.toString().trim());
			} else {
				currentDate = cal.getTime();
			}
		}

		return currentDate;
	}

	public static String getMD5Checksum(File file) throws IOException {
		return DigestUtils.md5Hex(new FileInputStream(file));
	}

	public static String getMD5Checksum(String str) {
		return DigestUtils.md5Hex(str);
	}
}

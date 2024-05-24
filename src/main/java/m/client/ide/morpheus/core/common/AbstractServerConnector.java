package m.client.ide.morpheus.core.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public abstract class AbstractServerConnector extends AbstractConnector {
	public enum HttpMethod {
		GET, POST;

		public static HttpMethod fromString(String method) {
			String str = method.toLowerCase();
			if (str.equals(POST.toString()))
				return POST;
			else
				return GET;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			switch (this) {
			case POST:
				return "post";
			default:
				return "get";
			}
		}
	}
	
	private CloseableHttpClient httpClient = null;

	private int timeout;
	private int resultTimeout;
	private boolean handleRedirect;
	private String url;
	private HttpMethod method;
	private String encoding;
	private String postEntity;
	private List<NameValuePair> configurations;
	private List<NameValuePair> requestParam;
	private static Pattern ENTITY_PARAM_EXPR = Pattern.compile("(\\$|#)\\{([^}]*)\\}");

	protected static List<NameValuePair> convertParam(Map<String, String> params) {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		Iterator<String> keys = params.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
		}

		return paramList;
	}
	
	public AbstractServerConnector(String url, HttpMethod method, String encoding, String postEntity,
			Map<String, String> configurations, Map<String, String> requestParam, int connectTimeout, int resultTimeout,
			boolean handleRedirect) {
		this(url, method, encoding, postEntity, convertParam(configurations), convertParam(requestParam),
				connectTimeout, resultTimeout, handleRedirect);
	}

	public AbstractServerConnector(String url, HttpMethod method, String encoding, String postEntity,
			List<NameValuePair> configurations, List<NameValuePair> requestParam, int connectTimeout, int resultTimeout,
			boolean handleRedirect) {
		super();
		this.url = url;
		this.method = method;
		this.encoding = encoding;
		this.postEntity = postEntity;
		this.configurations = configurations;
		this.requestParam = requestParam;
		this.timeout = connectTimeout;
		this.resultTimeout = resultTimeout;
		this.handleRedirect = handleRedirect;
	}

	@Override
	public boolean connect() {
		try {
			if (url != null && url.length() > 5 && url.substring(0, 5).equals("https")) {
				httpClient = getHttpsClient();
			} else {
				httpClient = getHttpClient();
			}

			HttpUriRequest uriRequest = null;
			RequestConfig rquestConfig = createRequestConfig(timeout, resultTimeout, handleRedirect);
			if (method.toString().toLowerCase().equals(HttpMethod.POST.toString())) {
				uriRequest = createPostConnect(url, configurations, requestParam, postEntity, encoding, rquestConfig);
			} else if (method.toString().toLowerCase().equals(HttpMethod.GET.toString())) {
				uriRequest = createGetConnect(url, configurations, requestParam, rquestConfig);
			} 
			execute(httpClient, uriRequest, encoding);
		} catch (Exception e) {
			e.printStackTrace();
			onReceiveResult(IConnectionResultReceiver.CONNECTION_FAIL, e);
		}

		return true;
	}

	@Override
	public boolean disconnect() {
		return false;
	}

	/**
	 * 
	 * @param httpClient
	 * @param uriRequest
	 * @param charset
	 * @throws Exception
	 */
	private void execute(CloseableHttpClient httpClient, HttpUriRequest uriRequest, String charset) throws Exception {
		CloseableHttpResponse response = httpClient.execute(uriRequest);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {

				InputStreamReader bis = new InputStreamReader(resEntity.getContent(), charset);
				BufferedReader reader = new BufferedReader(bis);

				StringBuffer buffer = new StringBuffer();
				String str = null;
				while ((str = reader.readLine()) != null) {
					buffer.append(str);
				}
				if (bis != null) {
					bis.close();
				}
				onReceiveResult(IConnectionResultReceiver.RESULT_OK, buffer.toString());
			}
			onReceiveResult(IConnectionResultReceiver.CONNECTION_OK);
		} else {
			onReceiveResult(IConnectionResultReceiver.CONNECTION_FAIL, new Exception(response.toString()));
		}
	}

	/**
	 * post용 커넥터 생성
	 * 
	 * @param url
	 * @param configurations
	 * @param requestParam
	 * @param entity
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private HttpUriRequest createPostConnect(String url, List<NameValuePair> configurations,
			List<NameValuePair> requestParam, String entity, String charset, RequestConfig requestConfig)
			throws Exception {

		HttpPost httpPost = getHttpPost(url);

		if (requestConfig != null) {
			httpPost.setConfig(requestConfig);
		}

		// 추가 요청 헤더를 넣음
		if (configurations != null && configurations.size() > 0) {
			for (NameValuePair c : configurations) {
				httpPost.addHeader(c.getName(), c.getValue());
			}
		}

		// 요청 파라메타 적용
		// entity 가 null이 아닌 경우 entity를 보낸다.
		if (entity != null && !entity.equals("")) {
			entity = applyParamToEntity(entity, requestParam);
			httpPost.setEntity(new StringEntity(entity, charset));
		} else {
			httpPost.setEntity(new UrlEncodedFormEntity(requestParam, charset));
		}

		return httpPost;
	}

	/**
	 * entity 스트링에서 치환 문자를 request 파라메터의 값으로 바꾼다.
	 * 
	 * @param entity
	 * @param reqParams
	 * @return String
	 */
	private String applyParamToEntity(String entity, List<NameValuePair> reqParams) {
		if (entity != null) {
			StringBuffer sb = new StringBuffer();
			int i = 0;
			Matcher get = ENTITY_PARAM_EXPR.matcher(entity);
			while (get.find()) {
				// entity 스트링에 파라메터 치환 문자가 있으면 치환 로직을 실행한다.
				// 리퀘스트 파라메터에 매칭되는 값이 없으면 빈 문자로 치환.
				int j = get.start();
				if (i < j) {
					String prev = entity.substring(i, j);
					sb.append(prev);
				}
				String type = get.group(1);// # or $
				String parameter = get.group(2).trim();

				if ("#".equals(type)) { // #{} 형식일 경우 escape 처리.
					escape(sb, getParameterValue(parameter, reqParams));
				} else {
					sb.append(getParameterValue(parameter, reqParams));
				}

				i = get.end();
			}
			String last = entity.substring(i);
			if (last.length() > 0) {
				sb.append(last);
			}

			return sb.toString();
		}
		return entity;
	}

	/**
	 * 해당하는 이름의 파라메터 값을 찾아 리턴.
	 * 
	 * @param paramKey
	 * @param params   - 파라메터 목록
	 * @return String
	 */
	private String getParameterValue(String paramKey, List<NameValuePair> params) {
		String rt = "";
		if (params != null) {
			for (NameValuePair param : params) {
				String name = param.getName();
				if (name != null && name.equals(paramKey)) {
					String value = param.getValue();
					if (value == null)
						value = "";
					rt = value;
					break;
				}
			}
		}

		return rt;
	}

	private void escape(StringBuffer buf, String s) {
		if (s != null) {
			int l = s.length();
			for (int i = 0; i < l; i++) {
				char c = s.charAt(i);
				switch (c) {
				case '<':
					buf.append("&lt;");
					break;
				case '>':
					buf.append("&gt;");
					break;
				case '&':
					buf.append("&amp;");
					break;
				case '"':
					buf.append("&quot;");
					break;
				default:
					buf.append(c);
					break;
				}
			}
		}
	}

	/**
	 * get용 커넥터 생성
	 * 
	 * @param url
	 * @param configurations
	 * @param requestParam
	 * @return
	 * @throws Exception
	 */
	protected HttpUriRequest createGetConnect(String url, List<NameValuePair> configurations,
			List<NameValuePair> requestParam, RequestConfig requestConfig) throws Exception {

		String paramStr = getRequestParameterStr(requestParam);
		String newUrl = url;
		if (paramStr.length() > 0) {
			newUrl = newUrl + "?" + paramStr;
		}

		HttpGet httpGet = getHttpGet(newUrl);
		if (requestConfig != null) {
			httpGet.setConfig(requestConfig);
		}

		// 추가 요청 헤더를 넣음
		if (configurations != null && configurations.size() > 0) {
			for (NameValuePair c : configurations) {
				httpGet.addHeader(c.getName(), c.getValue());
			}
		}
		return httpGet;
	}

	private RequestConfig createRequestConfig(int connectTimeout, int resultTimeout, boolean redirect) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(resultTimeout)
				.setConnectTimeout(connectTimeout).setSocketTimeout(connectTimeout).setRedirectsEnabled(redirect)
				.build();
		return requestConfig;
	}

	private CloseableHttpClient getHttpClient() {
		return HttpClients.createDefault();
	}

	private CloseableHttpClient getHttpsClient() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} }, new SecureRandom());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		return httpClient;
	}

	protected HttpPost getHttpPost(String url) {
		return new HttpPost(url);
	}

	protected HttpGet getHttpGet(String url) {
		return new HttpGet(url);
	}

	/**
	 * 리퀘스트 파라메타를 URL 전송용 스트링으로 변환하여 리턴한다.
	 * 
	 * @return
	 */
	protected abstract String getRequestParameterStr(List<NameValuePair> requestParam);

}

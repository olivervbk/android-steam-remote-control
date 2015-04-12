package olivervbk.steam.steamremote.api.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

public class HttpClientManager {
	private static final String TAG = "HttpClientManager";
	
	private HttpClient httpClient;
	
	
	/**
	 * @return the httpClient
	 */
	public HttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * @param httpClient the httpClient to set
	 */
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public HttpClientManager() throws HttpConnectionManagerException{
		this(true);
	}
	
	public HttpClientManager(final boolean ignoreSslCert) throws HttpConnectionManagerException{
		final HttpClient createHttpClient = createHttpClient(ignoreSslCert);
		setHttpClient(createHttpClient);
		
		/*
		java.util.logging.Logger.getLogger("org.apache.http.wire")
        .setLevel(java.util.logging.Level.FINER);
java.util.logging.Logger.getLogger("org.apache.http.headers")
        .setLevel(java.util.logging.Level.FINER);

System.setProperty("org.apache.commons.logging.Log",
        "org.apache.commons.logging.impl.SimpleLog");
System.setProperty(
        "org.apache.commons.logging.simplelog.showdatetime",
        "true");
System.setProperty(
        "org.apache.commons.logging.simplelog.log.httpclient.wire",
        "debug");
System.setProperty(
        "org.apache.commons.logging.simplelog.log.org.apache.http",
        "debug");
System.setProperty(
        "org.apache.commons.logging.simplelog.log.org.apache.http.headers",
        "debug");
		*/
	}
	
	protected HttpClient createHttpClient(final boolean ignoreSslCert)
			throws HttpConnectionManagerException {
		if (ignoreSslCert) {
			KeyStore trustStore;
			try {
				trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			} catch (KeyStoreException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				throw new HttpConnectionManagerException(e2);
			}

			try {
				trustStore.load(null, null);
			} catch (NoSuchAlgorithmException | CertificateException
					| IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				throw new HttpConnectionManagerException(e2);
			}

			SSLSocketFactory sf;
			try {
				sf = new DummySSLSocketFactory(trustStore);
			} catch (KeyManagementException | UnrecoverableKeyException
					| NoSuchAlgorithmException | KeyStoreException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				throw new HttpConnectionManagerException(e2);

			}
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			final PlainSocketFactory socketFactory = PlainSocketFactory
					.getSocketFactory();
			registry.register(new Scheme("http", socketFactory, 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			HttpClient httpClient = new DefaultHttpClient(ccm, params);
			return httpClient;
		} else {
			return new DefaultHttpClient();
		}
	}
	
	public JSONObject sendRequest(HttpMethod method, final String url,
			final Map<String, String> params) throws IOException, JSONException {

		final HttpUriRequest request = createRequest(method, url, params);
		request.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
		//request.setHeader("Cookie", "device_token=12345678");
		
		final boolean shouldLog = false;
		if(shouldLog && Log.isLoggable(TAG, Log.INFO)){
			
			final URI uri = request.getURI();
			final String uriStr = uri.toString();
			final String requestMethod = request.getMethod();
			
			final Header[] allHeaders = request.getAllHeaders();
			final String headers = Arrays.toString(allHeaders);
			
			final RequestLine requestLine = request.getRequestLine();
			final String requestLineStr = requestLine.toString();
			
			final String msg = String.format("Making request: %s\nURL: %s\nrequest line: %s\nHeaders: %s", requestMethod, uriStr, requestLineStr, headers);
			
			Log.i(TAG, msg);
		}

		final String response;
		try {
			final Pair<String, HttpResponse> responseInfo;
			responseInfo = doRequest(request);
			response = responseInfo.first;
			
			
			if(shouldLog && Log.isLoggable(TAG, Log.INFO)){
				HttpResponse httpResponse = responseInfo.second;
				final Header[] allHeaders = httpResponse.getAllHeaders();
				
				final String headersString = Arrays.toString(allHeaders);
				
				final StatusLine statusLine = httpResponse.getStatusLine();
				final int statusCode = statusLine.getStatusCode();
				
				final String msg = String.format("Response:%s\n%s\nHeaders:\n%s", statusCode, response, headersString);
				Log.i(TAG, msg);
			}
			
		} catch (HttpConnectionManagerException e) {
			throw new IOException(e);
		}
		
		final JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
		} catch (JSONException e) {
			throw new IOException(e);
		}
		return jsonObject;

	}

	protected HttpUriRequest createRequest(HttpMethod method,
			final String url, final Map<String, String> params)
			throws UnsupportedEncodingException {
		final int size = params.size();
		final List<BasicNameValuePair> entity = new ArrayList<BasicNameValuePair>(
				size);
		
		for(Entry<String, String> entry:params.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, value);
			entity.add(basicNameValuePair);
		}
		
		
		
		final HttpUriRequest request;
		
		switch (method) {
		case POST:
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(entity);
			final HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(urlEncodedFormEntity);
			request = httpPost;
			break;

		case GET:
			final String parametersString = URLEncodedUtils.format(entity, "utf-8");
			final String urlWithParameters = url+"?"+parametersString;
			
			HttpGet httpGet = new HttpGet(urlWithParameters);
			request = httpGet;
			break;

		default:
			throw new RuntimeException("Not implemented.");
		}
		return request;
	}

	protected Pair<String, HttpResponse> doRequest(HttpUriRequest request)
			throws ClientProtocolException, IOException,
			HttpConnectionManagerException {
		
		final HttpClient httpClient = getHttpClient();
		HttpResponse response = httpClient.execute(request);
		
		StatusLine statusLine = response.getStatusLine();
		HttpEntity responseEntity = response.getEntity();
		final String responseString = EntityUtils.toString(responseEntity);
		return new Pair<>(responseString, response);
	}
}

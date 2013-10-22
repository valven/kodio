package com.valven.kodio.util;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.valven.kodio.model.Response;

import android.util.Log;

public class HttpUtils {
	
	private static final DefaultHttpClient client;

	static {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.USER_AGENT, "Android");
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(),80));
		schreg.register(new Scheme("https",PlainSocketFactory.getSocketFactory(),443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schreg);
		client = new DefaultHttpClient(cm, params);
		client.setCookieStore(new BasicCookieStore());
	}

	public enum HttpRequest {
		POST, GET
	}
	
	public static void clearCookies(){
		client.getCookieStore().clear();
	}
	
	public static Response sendRequest(String uri, List<NameValuePair> params,
			Map<String, String> headers, HttpRequest type, InputStream is) {
		HttpUriRequest request = null;
		if (type == HttpRequest.POST) {
			try {
				request = post(uri, params, headers, is);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("HttpRequest must be POST");
		}
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(request);			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.d("http","HttpUtils : " + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("http","HttpUtils : " + e.toString());
		}
		
		return parseResponse(httpResponse);
	}

	public static Response sendRequest(String uri, List<NameValuePair> params,
			Map<String, String> headers, HttpRequest type) {
		HttpUriRequest request = null;
		if (type == HttpRequest.POST) {
			try {
				request = post(uri, params, headers);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			try {
				request = get(uri, params, headers, type);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return parseResponse(httpResponse);
	}
	
	private static String convertToString(InputStream is){
		BufferedInputStream bis = new BufferedInputStream(is);
	    ByteArrayOutputStream buf = new ByteArrayOutputStream();
	    int result;
		try {
			result = bis.read();
		    while(result != -1) {
			      byte b = (byte)result;
			      buf.write(b);
			      result = bis.read();
			    }        
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return buf.toString();

	}

	private static Response parseResponse(HttpResponse httpResponse){
		final Response response = new Response();
		Log.d("http","Api Headers: " + response.getHeader());
		Log.d("http","Api Headers - content: " + response.getContent());
		StringBuffer sb = new StringBuffer();

		if(httpResponse == null){
			return null;
		}
		HttpEntity entity = httpResponse.getEntity();
		Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
		if(contentEncoding == null){
			Header[] headers = httpResponse.getAllHeaders();
			for (Header header : headers) {
				Log.d("http","Header : " + header.getName() + " " + header.getValue());
				if(header.getName().equals("Content-Encoding")){
					contentEncoding = header;
					Log.d("http","Content-Encoding " + header.getName() + " " + header.getValue());
				}
			}
		}
		InputStream is = null;
		
		try {
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				is = entity.getContent();
			    is = new GZIPInputStream(is);
			    String s = convertToString(is);
				sb.append(s);
			}else{
				try {
					String s = EntityUtils.toString(entity, HTTP.UTF_8);
					sb.append(s);
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		response.setContent(sb.toString());
		
		response.setReason(httpResponse.getStatusLine().getReasonPhrase());
		response.setProtocol(httpResponse.getProtocolVersion().getProtocol());
		response.setHeader(Arrays.asList(httpResponse.getAllHeaders()));
		response.setStatusCode(httpResponse.getStatusLine().getStatusCode());

		return response;
	}
	private static HttpPost post(String uri, List<NameValuePair> params,
			Map<String, String> headers, InputStream is) throws UnsupportedEncodingException{
		HttpPost post = new HttpPost(uri);

		if (headers != null) {
			for (String key : headers.keySet()) {
				String val = headers.get(key);
				post.addHeader(key, val);
			}
		}

		InputStreamEntity e = new InputStreamEntity(is, -1); 
		e.setChunked(true);

		post.setEntity(e);

		return post;
	}
	
	
	private static HttpPost post(String uri, List<NameValuePair> params,
			Map<String, String> headers)
			throws UnsupportedEncodingException {
		HttpPost post = new HttpPost(uri);

		if (headers != null) {
			for (String key : headers.keySet()) {
				String val = headers.get(key);
				post.addHeader(key, val);
			}
		}

		HttpEntity entity = null;

		if (params != null) {
			if (params.size() == 1) {
				entity = new StringEntity(params.get(0).getValue());
			} else {
				entity = new UrlEncodedFormEntity(params, "UTF-8");
			}
		}
		post.setEntity(entity);

		return post;
	}

	private static HttpGet get(String url, List<NameValuePair> params,
			Map<String, String> headers, HttpRequest type)
			throws URISyntaxException {
		HttpGet get = new HttpGet();

		if (headers != null) {
			for (String key : headers.keySet()) {
				String val = headers.get(key);
				get.addHeader(key, val);
			}
		}

		try {
			URL completeURL = new URL(url);
			URI uri = URIUtils.createURI(completeURL.getProtocol(),
					completeURL.getHost(), completeURL.getPort(),
					completeURL.getPath(),
					params==null?"":URLEncodedUtils.format(params, "UTF-8"), null);
			Log.d("http","URI: " + uri.toString());
			get.setURI(uri);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return get;
	}

}

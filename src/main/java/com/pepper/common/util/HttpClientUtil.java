package com.pepper.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.common.consts.Code;
import com.pepper.common.exception.CustomException;



/**
 * 封装了发送HTTP请求的工具类
 */
@SuppressWarnings("deprecation")
public final class HttpClientUtil {
	private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
	
	public static final String DEFAULT_CHARSET = "UTF-8"; // 设置默认通信报文编码为UTF-8
	private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 5; // 设置默认连接超时为5s
	private static final int DEFAULT_SO_TIMEOUT = 1000 * 60; // 设置默认读取超时为60s

	private HttpClientUtil() {
	}

	/**
	 * 发送HTTP_POST请求
	 */
	public static String post(String reqURL, String reqData) {
		return post(reqURL, reqData, null);
	}

	/**
	 * 发送HTTP_POST请求
	 * 
	 * @see 1)该方法允许自定义任何格式和内容的HTTP请求报文体
	 * @see 2)该方法会自动关闭连接,释放资源
	 * @see 3)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
	 * @see 4)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
	 * @see 5)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html;
	 *      charset=GBK]的charset值
	 * @see 6)若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
	 * @param reqURL
	 *            请求地址
	 * @param reqData
	 *            请求报文,无参数时传null即可,多个参数则应拼接为param11=value11&22=value22&33=
	 *            value33的形式
	 * @return 远程主机响应正文
	 */
	@SuppressWarnings("resource")
	public static String post(String reqURL, String reqData, String contentType) {
		log.debug("请求{}的报文为-->>[{}]", reqURL, reqData);
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer
		// not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in
		// certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
			}

			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
			}

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// 由于下面使用的是new
			// StringEntity(....),所以默认发出去的请求报文头中CONTENT_TYPE值为text/plain;
			// charset=ISO-8859-1
			// 这就有可能会导致服务端接收不到POST过去的参数,比如运行在Tomcat6.0.36中的Servlet,所以我们手工指定CONTENT_TYPE头消息
			if (StringUtils.isBlank(contentType)) {
				httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET);
			} else {
				httpPost.setHeader(HTTP.CONTENT_TYPE, contentType);
			}
			httpPost.setEntity(new StringEntity(null == reqData ? "" : reqData, DEFAULT_CHARSET));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			log.debug("请求{}得到应答<<--[{}]", reqURL, respData);
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 发送HTTPS_POST请求
	 * 
	 * @see 1)该方法亦可处理HTTP_POST请求
	 * @see 2)该方法会自动关闭连接,释放资源
	 * @see 3)方法内自动注册443作为HTTPS端口,即处理HTTPS请求时,默认请求对方443端口
	 * @see 4)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
	 * @see 5)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
	 * @see 6)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html;
	 *      charset=GBK]的charset值
	 * @see 若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
	 * @param reqURL
	 *            请求地址
	 * @param params
	 *            请求参数,无参数时传null即可
	 * @return 远程主机响应正文
	 */
	@SuppressWarnings("resource")
	public static String post(String reqURL, Map<String, String> params) {
		log.debug("请求{}的报文为-->>{}", reqURL, JsonUtil.toJson(params));
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer
		// not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in
		// certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
			}

			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
			}

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// 由于下面使用的是new
			// UrlEncodedFormEntity(....),所以这里不需要手工指定CONTENT_TYPE为application/x-www-form-urlencoded
			// 因为在查看了HttpClient的源码后发现,UrlEncodedFormEntity所采用的默认CONTENT_TYPE就是application/x-www-form-urlencoded
			// httpPost.setHeader(HTTP.CONTENT_TYPE,
			// "application/x-www-form-urlencoded; charset=" + encodeCharset);
			if (null != params) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
			}
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			log.debug("请求{}得到应答<<--[{}]", reqURL, respData);
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	
	@SuppressWarnings("resource")
	public static String post(String reqURL, Map<String, String> params, Map<String, String> heads)  {
		log.debug("请求{}的报文为-->>{}", reqURL,JsonUtil.toJson(params));
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer
		// not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in
		// certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
			}

			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
			}

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// 由于下面使用的是new
			// UrlEncodedFormEntity(....),所以这里不需要手工指定CONTENT_TYPE为application/x-www-form-urlencoded
			// 因为在查看了HttpClient的源码后发现,UrlEncodedFormEntity所采用的默认CONTENT_TYPE就是application/x-www-form-urlencoded
			// httpPost.setHeader(HTTP.CONTENT_TYPE,
			// "application/x-www-form-urlencoded; charset=" + encodeCharset);
			if (null != params) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
			}
			if(null != heads){
				for(Map.Entry<String,String> entry : heads.entrySet()){
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			log.debug("请求{}得到应答<<--[{}]", reqURL, respData);
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public static Map<String,Object> postMap(String reqURL, Map<String, String> params, Map<String, String> heads){
		String result = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		X509TrustManager trustManager = new X509TrustManager(){
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier(){
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {}
			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {}
			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {}
			@Override
			public boolean verify(String arg0, SSLSession arg1) {return true;}
		};
		Map<String,Object> mapResponse = new HashMap<String,Object>();
		try {
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			sslContext.init(null, new TrustManager[]{trustManager}, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			if(null != params){
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for(Map.Entry<String,String> entry : params.entrySet()){
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
			}
			if(null != heads){
				for(Map.Entry<String,String> entry : heads.entrySet()){
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			result = entity==null?"":EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
			log.debug(reqURL+"请求返回状态:" + response.getStatusLine().getStatusCode() + "  返回信息:" + result);
			mapResponse = JsonUtil.jsonToObject(result, Map.class);
			return mapResponse;
		}catch(Exception e){
			log.error("发送请求异常："+e);
			throw new CustomException(Code.CONNECT_TIMEOUT);
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 发送上传文件的HTTP_POST请求
	 * 
	 * @see 1)该方法用来上传文件
	 * @see 2)该方法会自动关闭连接,释放资源
	 * @see 3)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
	 * @see 4)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
	 * @see 5)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html;
	 *      charset=GBK]的charset值
	 * @see 若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
	 * @param reqURL
	 *            请求地址
	 * @param filename
	 *            待上传的文件名
	 * @param is
	 *            待上传的文件流
	 * @param fileBodyName
	 *            远程主机接收文件域的名字,相当于前台表单中的文件域名称
	 *            <input type="file" name="fileBodyName">
	 * @param params
	 *            请求参数,无参数时传null即可
	 * @return 远程主机响应正文
	 */
	@SuppressWarnings("resource")
	public static String postWithUpload(String reqURL, String filename, InputStream is, String fileBodyName,
			Map<String, String> params) {
		log.debug("请求{}的报文为-->>{}", reqURL, JsonUtil.toJson(params));
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer
		// not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in
		// certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
			}

			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
			}

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};
		File tmpFile = new File(filename);
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// Charset用来保证文件域中文名不乱码,非文件域中文不乱码的话还要像下面StringBody中再设置一次Charset
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null,
					Charset.forName(DEFAULT_CHARSET));
			FileUtils.copyInputStreamToFile(is, tmpFile);
			reqEntity.addPart(fileBodyName, new FileBody(tmpFile));
			if (null != params) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					reqEntity.addPart(entry.getKey(),
							new StringBody(entry.getValue(), Charset.forName(DEFAULT_CHARSET)));
				}
			}
			httpPost.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			log.debug("请求{}得到应答<<--[{}]", reqURL, respData);
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
			tmpFile.delete();
		}
	}

	/**
	 * 发送下载文件的HTTP_POST请求
	 * 
	 * @see 1)该方法用来下载文件
	 * @see 2)该方法会自动关闭连接,释放资源
	 * @see 3)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
	 * @see 4)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
	 * @see 5)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html;
	 *      charset=GBK]的charset值
	 * @see 若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
	 * @see 6)下载的文件会保存在java.io.tmpdir环境变量指定的目录中
	 * @see CentOS6.5下是/tmp,CentOS6.5下的Tomcat中是/app/tomcat/temp,Win7下是C:\Users\
	 *      Jadyer\AppData\Local\Temp\
	 * @see 7)下载的文件若比较大,可能导致程序假死或内存溢出,此时可考虑在本方法内部直接输出流
	 * @param reqURL
	 *            请求地址
	 * @param params
	 *            请求参数,无参数时传null即可
	 * @return 应答Map有两个key,isSuccess--yes or
	 *         no,fullPath--isSuccess为yes时返回文件完整保存路径,failReason--
	 *         isSuccess为no时返回下载失败的原因
	 */
	@SuppressWarnings("resource")
	public static Map<String, String> postWithDownload(String reqURL, Map<String, String> params) {
		log.debug("请求{}的报文为-->>{}", reqURL,JsonUtil.toJson(params));
		Map<String, String> resultMap = new HashMap<String, String>();
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer
		// not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in
		// certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
			}

			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
			}

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};
		HttpEntity entity = null;
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// 由于下面使用的是new
			// UrlEncodedFormEntity(....),所以这里不需要手工指定CONTENT_TYPE为application/x-www-form-urlencoded
			// 因为在查看了HttpClient的源码后发现,UrlEncodedFormEntity所采用的默认CONTENT_TYPE就是application/x-www-form-urlencoded
			// httpPost.setHeader(HTTP.CONTENT_TYPE,
			// "application/x-www-form-urlencoded; charset=" + encodeCharset);
			if (null != params) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
			}
			HttpResponse response = httpClient.execute(httpPost);
			entity = response.getEntity();
			if (null != entity
					&& (entity.getContentType().getValue()
							.startsWith(ContentType.APPLICATION_OCTET_STREAM.getMimeType()))
					|| entity.getContentType().getValue().contains("image/jpeg")) {
				// 文件下载成功
				// respData = IOUtils.toByteArray(entity.getContent());
				String filename = null;
				for (Header header : response.getAllHeaders()) {
					if (header.toString().startsWith("Content-Disposition")) {
						filename = header.toString().substring(header.toString().indexOf("filename=") + 10);
						filename = filename.substring(0, filename.length() - 1);
						break;
					}
				}
				if (StringUtils.isBlank(filename)) {
					Header contentHeader = response.getFirstHeader("Content-Disposition");
					if (null != contentHeader) {
						HeaderElement[] values = contentHeader.getElements();
						if (values.length == 1) {
							NameValuePair param = values[0].getParameterByName("filename");
							if (null != param) {
								filename = param.getValue();
							}
						}
					}
				}
				if (StringUtils.isBlank(filename)) {
					filename = UUID.randomUUID().toString().replaceAll("-", "");
				}
				File _file = new File(System.getProperty("java.io.tmpdir") + "/" + filename);
				FileUtils.copyInputStreamToFile(entity.getContent(), _file);
				resultMap.put("isSuccess", "yes");
				resultMap.put("fullPath", _file.getCanonicalPath());
			} else {
				// 文件下载失败
				resultMap.put("isSuccess", "no");
				resultMap.put("failReason", EntityUtils.toString(entity, HttpClientUtil.DEFAULT_CHARSET));
			}
			log.debug("请求{}得到应答<<--{}", reqURL, JsonUtil.toJson(resultMap));
			return resultMap;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("请求通信[" + reqURL + "]时关闭远程应答文件流时发生异常,堆栈轨迹如下", e);
			}
			httpClient.getConnectionManager().shutdown();
		}
	}

	public static HttpGet getHttpGet(String url, Map<String, Object> params, String encode) {
		StringBuffer buf = new StringBuffer(url);
		if (params != null) {
			// 地址增加?或者&
			String flag = (url.indexOf('?') == -1) ? "?" : "&";
			// 添加参数
			for (String name : params.keySet()) {
				buf.append(flag);
				buf.append(name);
				buf.append("=");
				try {
					String param = String.valueOf(params.get(name));
					if (param == null) {
						param = "";
					}
					buf.append(URLEncoder.encode(param, encode));
				} catch (UnsupportedEncodingException e) {
				}
				flag = "&";
			}
		}
		HttpGet httpGet = new HttpGet(buf.toString());
		return httpGet;
	}

	public static DefaultHttpClient wrapClient() throws Exception {
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", 443, ssf));
		ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);

		return new DefaultHttpClient(mgr);
	}

	@SuppressWarnings("resource")
	public static String getHttpPost(String url, Map<String, Object> params, List<Header> headers, String encoding)
			throws IOException {
		log.debug("请求{}的报文为-->>[{}]", url, params);
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer
		// not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in
		// certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
			}

			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
			}

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(url);
			// 由于下面使用的是new
			// StringEntity(....),所以默认发出去的请求报文头中CONTENT_TYPE值为text/plain;
			// charset=ISO-8859-1
			// 这就有可能会导致服务端接收不到POST过去的参数,比如运行在Tomcat6.0.36中的Servlet,所以我们手工指定CONTENT_TYPE头消息
			if (headers != null && !headers.isEmpty()) {
				for (Header header : headers) {
					httpPost.setHeader(header);
				}
			}
			// 判断url参数是否为null
			if (params != null && !params.isEmpty()) {
				// 对参数进行编码后设置httpPost实体内容。
				// 说明：UrlEncodedFormEntity对POST请求中的参数进行转码，encoding及希望的类型（例如UTF-8）
				setEntity(httpPost, JsonUtil.toJson(params), "application/json", DEFAULT_CHARSET);
				if (params.get("filepath") != null) {
					setFileEntity(httpPost, params.get("filepath"));
				}
			}
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			log.debug("请求{}得到应答<<--[{}]", url, respData);
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + url + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + url + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + url + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public static void setFileEntity(HttpPost httpPost, Object fileName) throws IOException {

		if (httpPost instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRequest eeMethod = (HttpEntityEnclosingRequest) httpPost;
			try {
				InputStreamEntity inputStreamEntity = new InputStreamEntity(new FileInputStream((String) fileName),
						new File((String) fileName).length());
				eeMethod.setEntity(inputStreamEntity);
			} catch (IOException e) {
				throw new IOException(e.getMessage());
			}
		}
	}

	public static void setEntity(HttpPost httpPost, String content, String contentType, String charset)
			throws IOException {

		HttpEntityEnclosingRequest eeMethod = (HttpEntityEnclosingRequest) httpPost;
		if (content != null && content.trim().length() > 0) {
			try {
				AbstractHttpEntity entity = new ByteArrayEntity(content.getBytes(charset));
				entity.setContentType(getFormattedContentType(contentType, charset));
				eeMethod.setEntity(entity);
				EntityUtils.consume(entity);
			} catch (IOException e) {
				throw new IOException(e.getMessage());
			}
		}
	}

	/**
	 * Method formats content-type and charset for use as HTTP header value
	 * 
	 * @param contentType
	 * @param charset
	 * @return The formatted content-type and charset.
	 */
	private static String getFormattedContentType(final String contentType, final String charset) {
		String charsetFormatted = StringUtils.isEmpty(charset) ? "" : "; charset=" + charset;
		return contentType + charsetFormatted;
	}

	public static String httpGet(String url, Map<String, Object> params, String charset) {
		// 创建HttpClient对象
		HttpClient client = null;
		try {
			if (url.startsWith("https")) {
				client = wrapClient();
			}else{
				PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		        cm.setMaxTotal(128);
		        cm.setDefaultMaxPerRoute(128);
		        client = HttpClients.custom().setConnectionManager(cm).build();
			}
			// 获得HttpGet对象
			HttpGet httpGet = HttpClientUtil.getHttpGet(url, params, charset);
			// 发送请求获得返回结果
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			// 如果成功
			if (null != entity && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result =  EntityUtils.toString(entity);
				return  result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return null;
	}
	
	
	@SuppressWarnings("resource")
	public static String get(String reqURL, Map<String, String> params, Map<String, String> heads){
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		X509TrustManager trustManager = new X509TrustManager(){
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier(){
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {}
			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {}
			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {}
			@Override
			public boolean verify(String arg0, SSLSession arg1) {return true;}
		};
		
		String result = null;
		try{
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			sslContext.init(null, new TrustManager[]{trustManager}, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			String queryString = "";
			if(null != params){
				for(Map.Entry<String,String> entry : params.entrySet()){
					queryString += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), DEFAULT_CHARSET);
				}
			}
			HttpGet httpGet = new HttpGet(reqURL + (StringUtils.isNotBlank(queryString)?"?"+queryString.substring(1):""));
			if(null != heads){
				for(Map.Entry<String,String> entry : heads.entrySet()){
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			result = entity==null?"":EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
			log.debug(reqURL+"请求返回状态:" + response.getStatusLine().getStatusCode() + "  返回信息:" + result);
		}catch(Exception e){
			log.error("发送请求异常："+e);
			throw new CustomException(Code.CONNECT_TIMEOUT);
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
		
		return result;
	}

	public static String getServerContextPath(HttpServletRequest request){
		return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/"+request.getContextPath();
	}
}
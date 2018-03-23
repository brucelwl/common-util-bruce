package com.bruce.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * http请求工具
 * @author bruce(liwenlong)  2016年9月9日 上午11:53:31
 */
public class HttpUtilslwl {
	
	private String paramMapToQueryStr(Map<String, String> params) {
		Set<Entry<String, String>> entrySet = params.entrySet();
		Iterator<Entry<String, String>> iterator = entrySet.iterator();
		StringBuilder builder = new StringBuilder();
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			builder.append(param.getKey()).append("=").append(param.getValue()).append("&");
		}
		String sub = builder.substring(0, builder.length() - 1);
		return sub;
	}

	/**
	 * get方式请求网络数据
	 * 
	 * @param strUrl           网络地址
	 * @param params           请求参数 Map<参数名,参数值>
	 * @param encoding         设置读取网络返回数据时的编码,默认为UTF-8
	 * @return
	 */
	public String httpdoGet(String strUrl, Map<String, String> params, String encoding) {
		if (encoding == null) {
			encoding = "UTF-8";
		}
		StringBuilder strBuilder = new StringBuilder();
		InputStream inStream = null;
		InputStreamReader streamReader = null;
		BufferedReader bufReader = null;
		String sendParam = null;
		try {
			URL url = null;
			if (params != null && !params.isEmpty()) {
				sendParam = paramMapToQueryStr(params);
				strUrl = strUrl.contains("?") ? (strUrl + "&" + sendParam) : (strUrl + "?" + sendParam);
				url = new URL(strUrl); // 根据网址,创建URL对象
			} else {
				url = new URL(strUrl);
			}
			// 打开网络连接
			HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
			int responseCode = urlconn.getResponseCode();
			if(responseCode==200){
				// 获取网络连接输入流
				inStream = urlconn.getInputStream();
				strBuilder = this.readSrtFromInputStream(inStream, encoding);
			}else{
				String responseMessage = urlconn.getResponseMessage();
				Logger.getLogger("lwl").info("responseCode: "+responseCode+", message: "+responseMessage);
				return responseCode+"";
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufReader != null) {
					bufReader.close();
				}
				if (streamReader != null) {
					streamReader.close();
				}
				if (inStream != null) {
					inStream.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return strBuilder.toString().trim();
	}

	/**
	 * Post方式请求网络数据,
	 * 
	 * @param strUrl          网络地址
	 * @param params          请求参数 Map<参数名,参数值>
	 * @param encoding        请求编码方式,默认 UTF-8
	 */
	public String httpdoPost(String strUrl, Map<String, String> params, String encoding) {
		if (encoding == null) {
			encoding = "UTF-8";
		}
		StringBuilder builder = new StringBuilder();
		try {
			URL url = new URL(strUrl);
			HttpURLConnection httpURLConn = (HttpURLConnection) url.openConnection();
			// 设置编码方式
			httpURLConn.addRequestProperty("encoding", encoding);
			httpURLConn.setDoInput(true); // 使httpURLConn可以从网络获取数据
			httpURLConn.setDoOutput(true); // 使httpURLConn可以向互联网传输数据
			httpURLConn.setRequestMethod("POST"); // 设置POST请求方式
			if (params != null) {
				// 拼接请求参数
				String sendParam = paramMapToQueryStr(params);
				// System.out.println(sendParam ); //打印请求参数
				// 获取输出流
				OutputStream outStream = httpURLConn.getOutputStream();
				this.writeStrToOutputStream(sendParam, outStream);
			}
			int responseCode = httpURLConn.getResponseCode();
			if(responseCode==200){
				// 获取输入流
				InputStream inStream = httpURLConn.getInputStream();
				builder = readSrtFromInputStream(inStream,encoding);
			}else{
				String responseMessage = httpURLConn.getResponseMessage();
				Logger.getLogger("lwl").info("responseCode: "+responseCode+", message: "+responseMessage);
				return responseCode+"";
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString().trim();
	}

	/**
	 * Post方式请求网络数据,
	 *
	 * @param strUrl          网络地址
	 * @param body            请求body
	 * @param encoding        请求编码方式,默认 UTF-8
	 */
	public String httpdoPostBodyHeader(String strUrl, String body, Map<String, String> headers, String encoding) {
		if (encoding == null) {  encoding = "UTF-8";  }
		StringBuilder builder = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection httpURLConn = (HttpURLConnection) url.openConnection();
			// 设置编码方式
			httpURLConn.addRequestProperty("encoding", encoding);
			httpURLConn.setDoInput(true); // 使httpURLConn可以从网络获取数据
			httpURLConn.setDoOutput(true); // 使httpURLConn可以向互联网传输数据
			httpURLConn.setRequestMethod("POST"); // 设置POST请求方式
			if (headers != null) {
				Iterator<String> iteHeaders = headers.keySet().iterator();
				while(iteHeaders.hasNext()) {
					String header = iteHeaders.next();
					//设置请求头
					httpURLConn.addRequestProperty(header, headers.get(header));
				}
			}
			//httpURLConn.setRequestProperty("Content-Length", body.getBytes().length + "");  //设置文件请求的长度
			if (body != null) {
				// 获取输出流
				OutputStream outStream = httpURLConn.getOutputStream();
				writeStrToOutputStream(body, outStream);
			}
			int responseCode = httpURLConn.getResponseCode();
			if(responseCode==200){
				// 获取输入流
				InputStream inStream = httpURLConn.getInputStream();
				builder = readSrtFromInputStream(inStream,encoding);
			}else{
				String responseMessage = httpURLConn.getResponseMessage();
				Logger.getLogger("lwl").info("responseCode: "+responseCode+", message: "+responseMessage);
				return responseCode+"";
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * 输出字符串到输出流
	 */
	private void writeStrToOutputStream(String body, OutputStream outStream){
		OutputStreamWriter outWriter = null;
		BufferedWriter bufWriter = null;
		try {
		    outWriter = new OutputStreamWriter(outStream);
		    bufWriter = new BufferedWriter(outWriter);
			// 输出请求体
			bufWriter.write(body);
			bufWriter.flush();
			// 关闭输出流
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bufWriter.close();
				outWriter.close();
				outStream.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
	}
    /**
     * 从输入流中读取字符串
     */
	private StringBuilder readSrtFromInputStream(InputStream inStream,String encoding) {
		StringBuilder builder = new StringBuilder();
		InputStreamReader inReader = null;
		BufferedReader bufReader = null;
		try {
			 inReader = new InputStreamReader(inStream, encoding);
			 bufReader = new BufferedReader(inReader);
			 String line;
			 while ((line = bufReader.readLine()) != null) {
				builder.append(line);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(bufReader!=null) bufReader.close();
				if(inReader!=null) inReader.close();
				if(inStream!=null) inStream.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		return builder;
	}

	HttpClientContext httpClientContext = HttpClientContext.create();
	/**
	 * 使用Apache 的HttpClient 进行get请求
	 *
	 * @param strUrl           网络地址
	 * @param params           请求参数 Map<参数名,参数值>
	 * @param charset          请求编码方式,默认 UTF-8
	 * @return                 返回服务端返回的字符串
	 */
	public HttpResp httpClientdoGet(String strUrl, Map<String, String> params, String charset)
	{
		if (charset == null) {	charset = "UTF-8";	}
		HttpClient httpClient = HttpClients.createDefault();
		String sendParam = null;
		HttpGet httpGet = null;
		HttpResp httpResp = new HttpResp();
		if (params != null) {
			sendParam = paramMapToQueryStr(params);
			strUrl = strUrl.contains("?") ? (strUrl+"&"+sendParam) : (strUrl+"?"+sendParam);
			httpGet = new HttpGet(strUrl);
		} else {
			httpGet = new HttpGet(strUrl);
		}
		try {
			//System.out.println("访问前获取cookie");
			/*List<Cookie> cookies1 = httpClientContext.getCookieStore().getCookies();
			for (Cookie cookie : cookies1) {
				System.out.println(JSON.toJSONString(cookie));
			}*/

			HttpResponse resp = httpClient.execute(httpGet,httpClientContext);
			//List<Cookie> cookies = httpClientContext.getCookieStore().getCookies();
			//System.out.println("访问后获取cookie");
			/*for (Cookie cookie : cookies) {
				System.out.println(JSON.toJSONString(cookie));
			}*/
			//cookies.forEach(System.out::println);

			int statusCode = resp.getStatusLine().getStatusCode();
			httpResp.setStatusCode(statusCode);
			if(statusCode == HttpURLConnection.HTTP_OK){
				HttpEntity entity=resp.getEntity();
				httpResp.setBody(EntityUtils.toString(entity, charset));
			}else{
				String reasonPhrase = resp.getStatusLine().getReasonPhrase();
				Logger.getLogger("lwl").info("statusCode: "+statusCode+", reasonPhrase: "+reasonPhrase);
				httpResp.setErrorMesg("statusCode: "+statusCode+", reasonPhrase: "+reasonPhrase);
			}
		} catch (IOException e) {
			httpResp.setStatusCode(500);
			httpResp.setErrorMesg(e.getMessage());
			e.printStackTrace();
		}
		return httpResp;
	}

	/**
	 * 使用Apache 的HttpClient 进行post请求
	 * @param strUrl          网络地址
	 * @param params          请求参数 Map<参数名,参数值>
	 * @param charset         请求编码方式,默认 UTF-8
	 * @return
	 */
	public HttpResp httpClientdoPost(String strUrl, Map<String, String> params, String charset) {
		if (charset == null) {
			charset = "UTF-8";
		}
		HttpClient httpClient = HttpClients.createDefault();
		//创建请求设置
		RequestConfig reqConfig = RequestConfig.custom()
				.setConnectTimeout(60000)  //设置连接超时时间,单位毫秒
				//.setConnectionRequestTimeout(1000) //设置从connect Manager获取Connection超时时间,单位毫秒.
				//.setSocketTimeout(50000) //请求获取数据的超时时间
				.build();
		//创建请求并设置请求设置
		HttpPost post = new HttpPost(strUrl);
		post.setConfig(reqConfig);

		HttpResp httpResp = new HttpResp();
		try {
			if (params != null) {
				List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
				Set<String> set = params.keySet();
				for (String param : set) {
					parameters.add(new BasicNameValuePair(param, params.get(param)));
				}
				post.setEntity(new UrlEncodedFormEntity(parameters, charset));
			}
			HttpResponse resp = httpClient.execute(post,httpClientContext);
			int statusCode = resp.getStatusLine().getStatusCode();
			httpResp.setStatusCode(statusCode);
			if(statusCode == HttpURLConnection.HTTP_OK){
				HttpEntity entity=resp.getEntity();
				httpResp.setBody(EntityUtils.toString(entity, charset));
			}else{
				String reasonPhrase = resp.getStatusLine().getReasonPhrase();
				Logger.getLogger("lwl").info("statusCode: "+statusCode+", reasonPhrase: "+reasonPhrase);
				httpResp.setErrorMesg("statusCode: "+statusCode+", reasonPhrase: "+reasonPhrase);
			}
		} catch (IOException e) {
			httpResp.setStatusCode(500);
			httpResp.setErrorMesg(e.getMessage());
			e.printStackTrace();
		}
		return httpResp;
	}

	/**
	 * 使用Apache 的HttpClient 进行get请求
	 *
	 * @param strUrl            网络地址
	 * @param params            请求参数 Map<参数名,参数值>
	 * @param charset           请求编码方式,默认 UTF-8
	 * @return
	 */
	public String httpClientdoGetHeaders(String strUrl, Map<String, String> params, Map<String, String> headers, String charset)
	{
		String result = null;
		try {
			if (charset == null) {	charset = "UTF-8";   }
			HttpClient httpClient = HttpClients.createDefault();
			String sendParam = null;
			HttpGet httpGet = new HttpGet();
			if (headers != null) {
				Iterator<String> iteHeaders = headers.keySet().iterator();
				while (iteHeaders.hasNext()) {
					String header = iteHeaders.next();
					//设置请求头
					httpGet.addHeader(header, headers.get(header));
				}
			}
			if (params != null) {
				sendParam = paramMapToQueryStr(params);
				httpGet.setURI(new URI(strUrl + "?" + sendParam));
			} else {
				httpGet.setURI(new URI(strUrl));
			}
			HttpResponse resp = httpClient.execute(httpGet);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode==200){
				HttpEntity entity=resp.getEntity();
				result=EntityUtils.toString(entity, charset);

			}else{
				String reasonPhrase = resp.getStatusLine().getReasonPhrase();
				Logger.getLogger("lwl").info("statusCode: "+statusCode+", reasonPhrase: "+reasonPhrase);
				result=String.valueOf(statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 使用Apache 的HttpClient 进行post请求
	 *
	 * @param strUrl           网络地址
	 * @param params           请求参数 Map<参数名,参数值>
	 * @param charset          请求编码方式,默认 UTF-8
	 * @return
	 */
	public String httpClientdoPostHeaders(String strUrl, Map<String, String> params, Map<String, String> headers, String charset)
	{
		if (charset == null) {
			charset = "UTF-8";
		}
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(strUrl);
		String result = null;
		try {
			if (headers != null) {
				Iterator<String> iteHeaders = headers.keySet().iterator();
				while (iteHeaders.hasNext()) {
					String header = iteHeaders.next();
					//设置请求头
					post.setHeader(header, headers.get(header));
				}
			}
			if (params != null) {
				List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
				Set<String> set = params.keySet();
				for (String param : set) {
					parameters.add(new BasicNameValuePair(param, params.get(param)));
				}
				post.setEntity(new UrlEncodedFormEntity(parameters, charset));
			}
			HttpResponse resp = httpClient.execute(post);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode==200){
				HttpEntity entity=resp.getEntity();
				result=EntityUtils.toString(entity, charset);
			}else{
				String reasonPhrase = resp.getStatusLine().getReasonPhrase();
				Logger.getLogger("lwl").info("statusCode: "+statusCode+", reasonPhrase: "+reasonPhrase);
				result=String.valueOf(statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 使用Apache 的HttpClient 进行post请求
	 *
	 * @param strUrl        网络地址
	 * @param params        请求参数 Map<参数名,参数值>
	 * @param charset       请求编码方式,默认 UTF-8
	 * @return
	 */
	public String httpClientdoOptionsHeaders(String strUrl, Map<String, String> params, Map<String, String> headers, String charset)
	{
		if (charset == null) {
			charset = "UTF-8";
		}
		HttpClient httpClient = HttpClients.createDefault();
		HttpOptions options = new HttpOptions(strUrl);
		String result = null;
		try {
			if (headers != null) {
				Iterator<String> iteHeaders = headers.keySet().iterator();
				while (iteHeaders.hasNext()) {
					String header = iteHeaders.next();
					//设置请求头
					options.setHeader(header, headers.get(header));
				}
			}
			if (params != null) {
				List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
				Set<String> set = params.keySet();
				for (String param : set) {
					parameters.add(new BasicNameValuePair(param, params.get(param)));
				}
				// options.setEntity(new UrlEncodedFormEntity(parameters,charset));
			}
			HttpResponse resp = httpClient.execute(options);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(statusCode==200){
				HttpEntity entity=resp.getEntity();
				result=EntityUtils.toString(entity, charset);
			}else{
				String reasonPhrase = resp.getStatusLine().getReasonPhrase();
				Logger.getLogger("lwl").info("statusCode: "+statusCode+", reasonPhrase: "+reasonPhrase);
				result=String.valueOf(statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	public static class HttpResp{
		private int statusCode;
		private String body;
		private String errorMesg;

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getErrorMesg() {
			return errorMesg;
		}

		public void setErrorMesg(String errorMesg) {
			this.errorMesg = errorMesg;
		}
	}




}

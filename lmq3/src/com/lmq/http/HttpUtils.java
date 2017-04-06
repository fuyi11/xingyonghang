package com.lmq.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.lmq.main.api.MyLog;

/**
 *  * HttpUtil Class Capsule Most Functions of Http Operations
 */
public class HttpUtils {
	private static Header[] headers = new BasicHeader[1];
	private static String TAG = "http";
	private static int TIMEOUT = 5 * 1000;
	private static final String BOUNDARY = "---------------------------7db1c523809b2";

	/**
	 * Your header of http op       
	 */
	static {
		headers[0] = new BasicHeader("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
	}
	

    public static HttpClient httpClient;
    /**
     * 单例模式
     * */
    public static HttpClient init()
    {
    	if (null == httpClient) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams
                    .setUserAgent(
                            params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // 超时设置
            /* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 1000);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 4000);
            
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            httpClient = new DefaultHttpClient(conMgr, params);
        }
        return httpClient; 
    }
    public static void shutdown()
    {
      //关闭连接
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
            httpClient = null;
        }
    }
    
	/**
	 * 
	 * @param url
	 * @param json
	 * @return
	 */
	public static JSONObject getJsonWithJson(String url, Object json) {
		System.out.println("-------Http:url:"+url);
		System.out.println("-------Http:json:"+json.toString()); 
		
	    init();
	    
		HttpPost httpPost = new HttpPost(url);
		String retSrc = null;
		JSONObject result = null;
		try {
			StringEntity stringEntity = new StringEntity(json.toString(), HTTP.UTF_8);
//                        //设置类型 编码
//			stringEntity.setContentType("application/json;charset=UTF-8");
//			stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
//					"application/json;charset=UTF-8"));
			httpPost.setEntity(stringEntity);
			
//		    HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);   
//			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);
//			HttpConnectionParams.setSoTimeout(httpClient.getParams(), TIMEOUT);
//			ConnManagerParams.setTimeout(httpClient.getParams(), TIMEOUT);
			
			// 发送请求
			HttpResponse httpResponse = httpClient.execute(httpPost);
			System.out.println("http code"+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 得到应答的字符串
				retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                System.out.println("-------Http:retSrc:"+retSrc);
				// 生成 JSON 对象
				result = new JSONObject(retSrc);
//                System.out.println("-------Http:result:"+result.toString());
			} else {
				result = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}  
		//返回json对象
		return result;
	}

	/**
	 * 
	 * @param murl
	 * @return
	 * @throws Exception
	 */
	public static boolean delete(String murl) throws Exception {
		URL url = new URL(murl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("DELETE");
		conn.setConnectTimeout(5000);
		if (conn.getResponseCode() == 204) {
			MyLog.e(conn.toString());
			return true;
		}
		MyLog.e(conn.getRequestMethod());
		MyLog.e(conn.getResponseCode() + "");
		return false;
	}

	 

	public static String get(String url) {
		return get(url, null);
	}

	/**
	 * Op Http get request
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String get(String url, HashMap<String, String> map) {

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		ConnManagerParams.setTimeout(client.getParams(), TIMEOUT);
		String result = "ERROR";
		if (null != map) {
			int i = 0;
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (i == 0) {
					url = url + "?" + entry.getKey() + "=" + entry.getValue();
				} else {
					url = url + "&" + entry.getKey() + "=" + entry.getValue();
				}
				i++;
			}
		}
		HttpGet get = new HttpGet(url);
		get.setHeaders(headers);
		MyLog.i(TAG, url);
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// setCookie(response);
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				result = EntityUtils.toString(response.getEntity(), "UTF-8")
						+ response.getStatusLine().getStatusCode() + "ERROR";
			}
		} catch (ConnectTimeoutException e) {
			result = "TIMEOUTERROR";
		}

		catch (Exception e) {
			result = "OTHERERROR";
			e.printStackTrace();
		}
		MyLog.i(TAG, "result =>" + result);
		return result;
	}

	/**
	 * Op Http post request , "404error" response if failed  
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String post(String url, HashMap<String, String> map) {
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		ConnManagerParams.setTimeout(client.getParams(), TIMEOUT);
		HttpPost post = new HttpPost(url);
		MyLog.i(TAG, url);
		post.setHeaders(headers);
		String result = "ERROR";
		ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				MyLog.i(TAG, entry.getKey() + "=>" + entry.getValue());
				BasicNameValuePair pair = new BasicNameValuePair(
						entry.getKey(), entry.getValue());
				pairList.add(pair);
			}
		}
		try {
			HttpEntity entity = new UrlEncodedFormEntity(pairList, "UTF-8");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				result = EntityUtils.toString(response.getEntity(), "UTF-8")
						+ response.getStatusLine().getStatusCode() + "ERROR";
			}
		} catch (ConnectTimeoutException e) {
			result = "TIMEOUTERROR";
		} catch (Exception e) {
			result = "OTHERERROR";
			e.printStackTrace();
		}
		MyLog.i(TAG, "result =>" + result);
		return result;
	}

	/**
	 * 发送Json请求，获得Json结果
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String postJson(String url, HashMap<String, String> map) {
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		ConnManagerParams.setTimeout(client.getParams(), TIMEOUT);
		HttpPost post = new HttpPost(url);
		MyLog.i(TAG, url);
		post.setHeaders(headers);
		String resultJson = "ERROR";
		// 先封装一个 JSON 对象
		JSONObject param = new JSONObject();
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				try {
					param.put(entry.getKey(), entry.getValue());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			post.setEntity(se);

			// 发送请求
			HttpResponse response = client.execute(post);

			// 得到应答的字符串，这也是一个 JSON 格式保存的数据
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultJson = EntityUtils
						.toString(response.getEntity(), "UTF-8");
			} else {
				resultJson = EntityUtils
						.toString(response.getEntity(), "UTF-8")
						+ response.getStatusLine().getStatusCode() + "ERROR";
			}
		} catch (ConnectTimeoutException e) {
			resultJson = "TIMEOUTERROR";
		} catch (Exception e) {
			resultJson = "OTHERERROR";
			e.printStackTrace();
		}
		MyLog.i(TAG, "result =>" + resultJson);
		return resultJson;
	}

	/**
	 * 自定义的http请求可以设置为DELETE PUT等而不是GET
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @return
	 */
	public static String customrequest(String url,
			HashMap<String, String> params, String method) {
		try {
			URL postUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) postUrl
					.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod(method);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
			conn.connect();
			OutputStream out = conn.getOutputStream();
			StringBuilder sb = new StringBuilder();
			if (null != params) {
				int i = params.size();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (i == 1) {
						sb.append(entry.getKey() + "=" + entry.getValue());
					} else {
						sb.append(entry.getKey() + "=" + entry.getValue() + "&");
					}
					i--;
				}
			}
			String content = sb.toString();
			out.write(content.getBytes("UTF-8"));
			out.flush();
			out.close();
			InputStream inStream = conn.getInputStream();
			String result = inputStream2String(inStream);
			MyLog.i(TAG, "result>" + result);
			conn.disconnect();
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 必须严格限制get请求所以增加这个方法 这个方法也可以自定义请求
	 * 
	 * @param url
	 * @param map
	 * @param method
	 * @return
	 */
	public static String customrequestget(String url,
			HashMap<String, String> map, String method) {
		if (null != map) {
			int i = 0;
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (i == 0) {
					url = url + "?" + entry.getKey() + "=" + entry.getValue();
				} else {
					url = url + "&" + entry.getKey() + "=" + entry.getValue();
				}
				i++;
			}
		}
		try {

			URL murl = new URL(url);
			System.out.print(url);
			HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod(method);
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
			InputStream inStream = conn.getInputStream();
			String result = inputStream2String(inStream);
			MyLog.i(TAG, "result>" + result);
			conn.disconnect();
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 *  传多张图片          
	 */
	public static void post(String actionUrl, Map<String, String> params,
			Map<String, File> files) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);
		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		InputStream in = null;
		// 发送文件数据
		if (files != null) {
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"source\"; filename=\""
						+ file.getValue().getName() + "\"" + LINEND);
				sb1.append("Content-Type: image/pjpeg; " + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
			}
			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			int res = conn.getResponseCode();
			// if (res == 200) {
			in = conn.getInputStream();
			int ch;
			StringBuilder sb2 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
			outStream.close();
			conn.disconnect();
		}
	}

	/**
	 * is转String
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	/**
	 * check net work
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			Toast.makeText(context, "当前无网络连接,请稍后重试", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * check if the string is null
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNull(String string) {
		boolean t1 = "".equals(string);
		boolean t2 = string == null;
		boolean t3 = string.equals("null");
		if (t1 || t2 || t3) {
			return true;
		} else {
			return false;
		}
	}

	public static byte[] getBytes(File file) throws IOException {
		InputStream ios = null;
		ByteArrayOutputStream ous = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1) {
				ous.write(buffer, 0, read);
			}
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
			}
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}

		return ous.toByteArray();
	}

	/**
	 *  上传文件
	 * @param file 需要上传的文件
	 * @param url  上传地址
	 * @return  
	 */
	public static String uploadFile(File file,String urlStr,String name)throws Exception{
		URL url=new URL(urlStr);
		HttpURLConnection httpURLConn=(HttpURLConnection)url.openConnection();
		httpURLConn.setDoInput(true);
		httpURLConn.setDoOutput(true);
		httpURLConn.setRequestMethod("POST");
		httpURLConn.setRequestProperty("Connection","Keep-Alive");
		httpURLConn.setRequestProperty("Charset","UTF-8");
		httpURLConn.setRequestProperty("Content-Type","multipart/form-data;boundary=******");
		DataOutputStream dos=new DataOutputStream(httpURLConn.getOutputStream());
		dos.writeBytes("--******\r\n");
		dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getName()+"\"\r\n");
		dos.writeBytes("\r\n");
		FileInputStream fis=new FileInputStream(file);
		byte[] buffer=new byte[8192];//8k
		int count=0;
		while((count=fis.read(buffer))!=-1){
			dos.write(buffer,0,count);
		}
		fis.close();
		dos.writeBytes("\r\n");
		dos.writeBytes("--******--\r\n");
		dos.flush();
		
		//获取上传后的信息
		BufferedReader br=new BufferedReader(new InputStreamReader(httpURLConn.getInputStream()));
		String result=br.readLine();
		dos.close();
		httpURLConn.getInputStream().close();
		br.close();
		return result;
	}
}

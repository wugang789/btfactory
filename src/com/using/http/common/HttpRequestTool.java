package com.using.http.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequestTool {


	/**
	 * 请求页面
	 * @param strUrl
	 * @param strPostRequest
	 * @param maxLength
	 * @param code
	 * @return
	 */
	public static String getPageContent(String strUrl, String strPostRequest, int maxLength, String code) {
		// 读取结果网页
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(strUrl);
			// 打开url连接
			connection = (HttpURLConnection) url.openConnection();
			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod(strPostRequest);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), code));
			int ch;
			for (int length = 0; (ch = in.read()) > -1 && (maxLength <= 0 || length < maxLength); length++) {
				buffer.append((char) ch);
				//System.out.print((char) ch);
			}
			in.close();
			connection.disconnect();
			return buffer.toString().trim();
		} catch (Exception e) {
			if(connection != null) {
				connection.disconnect();
			}
			System.out.println(strUrl + "，" + e.getMessage());
			return null;
		}
	}
}

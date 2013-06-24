package com.using.http.btfactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeList;

import com.using.http.common.HttpRequestTool;
import com.using.http.common.PageListBean;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 下载从BT工场收集到的种子文件
 * @author using
 */
public class DownLoadTorrent {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		startDownLoadThread();
	}

	/**
	 * 处理线程数据并启动种子文件下载线程
	 */
	public static void startDownLoadThread() {
		List<String> listDownUrl = ExcelTools.getDownUrl(IConfig.EXCEL_OUT_PATH);
		int listCount = listDownUrl.size();
		int threadCount = IConfig.OPEN_THREAD_COUNT;
		int pageCount = listCount / threadCount;
		int lastCount = listCount % threadCount;
		System.out.println("文件下载开始。【共开启总线程数：" + threadCount + "】" + "\r\n种子文件存放在：【" + IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_TORRENT_FOLDER + "】");
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 1; i <= threadCount; i++) {
			PageListBean bean = new PageListBean();
			bean.setCurrentPage(i);
			List pagerList = null;
			if(i == threadCount && lastCount != 0) {
				pagerList = bean.getPaper(listDownUrl, pageCount);
				for (int j = pageCount * threadCount; j < listDownUrl.size(); j++) {
					pagerList.add(listDownUrl.get(j));
				}
			} else {
				pagerList = bean.getPaper(listDownUrl, pageCount);
			}
			pool.submit(new DownLoadThread(pagerList, i + ""));
		}
		
	}

	/**
	 * 执行种子文件下载
	 * 
	 * @param listDownUrl
	 *            下载地址
	 * @param index
	 *            线程标志
	 */
	public static void startDownloadFile(List<String> listDownUrl, String index) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		List<LinkBeans> listLinkBeanIndex = new ArrayList<LinkBeans>(0);
		for (int i = 0; i < listDownUrl.size(); i++) {
			String url = listDownUrl.get(i);
			String htmlData = HttpRequestTool.getPageContent(url, "GET", 100500, "UTF-8");
			if(htmlData != null) {
				LinkBeans linkBeans = extractText(htmlData, url);
				if (linkBeans.getId() != null && !"".equals(linkBeans.getId())) {
					String downUrl = url.substring(0, url.indexOf("file.php")) + "down.php";
					String linkNameStr = linkBeans.getName().replaceAll(" ", "");

					
					File fileFolder = new File(IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_TORRENT_FOLDER + "//" + linkNameStr);
					if(!fileFolder.exists()) {
						fileFolder.mkdirs();
					}
					
					
					String filePath = IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_TORRENT_FOLDER + "//" + linkNameStr + "//" + linkNameStr + "." + linkBeans.getType();
					File file = new File(filePath);
					
					String params = "type=" + linkBeans.getType() + "&id=" + linkBeans.getId() + "&name=" + linkNameStr;
					
					System.out.println(index+ ",开始下载:"+ ( i + 1) + "/"  + listDownUrl.size() + "：" + url + "  " + sdf.format(new Date()));
					downFile(downUrl, params, url, file);
					
					
					
					/** ---- 生成种子配图HTML文件 begin-------- */
					String imgKey = url.substring(url.indexOf("file.php") + 9);
					
					linkBeans.setLinkUrl(url);
					
					String context = getTxtContext(imgKey.replace(".html", ""));
					linkBeans.setContext(context);
					
					String title = getContextTitle(context);
					if("".equals(title)) {
						linkBeans.setTitle(linkNameStr);
					} else {
						linkBeans.setTitle(title);
					}
					
					createImgHtmlFile(linkBeans, IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_TORRENT_FOLDER + "//" + linkNameStr + "//种子配图.html");
					
					linkBeans.setIndexLinkUrl(IConfig.CONTEXT_TORRENT_FOLDER + "//" + linkNameStr + "//种子配图.html");
					
					listLinkBeanIndex.add(linkBeans);
					/** ---- 生成种子配图HTML文件 end-------- */
					
				}
			}
		}
		
		// 生成预览首页
		LinkBeans linkIndex = new LinkBeans();
		linkIndex.setListLinkBeanIndex(listLinkBeanIndex);
		createIndexHtml(linkIndex, IConfig.TORRENT_OUT_PATH + "index_" + index + ".html");
	}

	/**
	 * 提取表单参数信息
	 * @param inputHtml
	 * @return
	 */
	private static LinkBeans extractText(String inputHtml, String url) {
		LinkBeans listLinks = new LinkBeans();
		try {
			// Parser parser = new Parser(inputHtml);
			Parser parser = createParser(inputHtml);
			NodeFilter filter = new TagNameFilter("form");
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					FormTag formTag = (FormTag) nodes.elementAt(i);
					// 表单元素
					NodeList nodeInputs = formTag.getFormInputs();
					for (int j = 0; j < nodeInputs.size(); j++) {
						InputTag inputTag = (InputTag) nodeInputs.elementAt(j);
						if ("hidden".equals(inputTag.getAttribute("type"))) {
							if ("type".equals(inputTag.getAttribute("name"))) {
								listLinks.setType(inputTag.getAttribute("value"));
							}
							if ("name".equals(inputTag.getAttribute("name"))) {
								listLinks.setName(inputTag.getAttribute("value"));
							}
							if ("id".equals(inputTag.getAttribute("name"))) {
								listLinks.setId(inputTag.getAttribute("value"));
							}
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println(url + "，提取下载参数失败！");
		}
		return listLinks;
	}

	/**
	 * 描述：sendUrl 发送URL的post请求
	 * 
	 * @param urlStr
	 * @param params
	 */
	private static void downFile(String urlStr, String params, String referer, File file) {
		try {
			URL realUrl = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			// 请求方式
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Accept-Charset", "*/*");
			// conn.setRequestProperty("Referer",
			// "http://www3.97down.info/qb/file.php/MHG0YFS.html");
			// 来源地址
			conn.setRequestProperty("Referer", referer);
			// 连接超时
			conn.setConnectTimeout(2000);
			// 读取超时 --服务器响应比较慢，增大时间
			conn.setReadTimeout(35000);
			conn.connect();
			// 获取URLConnection对象对应的输出流
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			// 发送请求参数
			out.write(params);
			out.flush();
			out.close();

			/* ----------- 生成文件 ------------ */
			InputStream in = conn.getInputStream();
			FileOutputStream os = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int read;
			while ((read = in.read(buffer)) > 0) {
				os.write(buffer, 0, read);
			}
			os.flush();
			os.close();
			in.close();

			if (conn != null) {
				// 关闭连接
				conn.disconnect();
			}
		} catch (Exception e) {
			//System.out.println(referer + "   " + e.getMessage() + "，文件写入出错！");
		}
	}

	/**
	 * 生成预览图html文件
	 * @param imgBeans
	 * @param outPutPath
	 */
	private static void createImgHtmlFile(LinkBeans imgBeans, String outPutPath) {
		Configuration configuration = new Configuration();
		Writer out = null;
		try {
			Template temp = configuration.getTemplate("src//com//using//http//btfactory//imgHtml.ftl");
			temp.setEncoding("UTF-8");
			out = new FileWriter(new File(outPutPath));
			temp.process(imgBeans, out);
			out.flush();
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}
	
	/**
	 * 生成首页
	 * @param listBeans
	 * @param outPutPath
	 */
	private static void createIndexHtml(LinkBeans beans, String outPutPath) {
		Configuration configuration = new Configuration();
		Writer out = null;
		try {
			Template temp = configuration.getTemplate("src//com//using//http//btfactory//indexHtml.ftl");
			temp.setEncoding("UTF-8");
			out = new FileWriter(new File(outPutPath));
			temp.process(beans, out);
			out.flush();
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}

	/**
	 * 解析字符串
	 * 
	 * @param inputHTML
	 *            String
	 * @return Parser
	 */
	private static Parser createParser(String inputHTML) {
		Lexer mLexer = new Lexer(new Page(inputHTML));
		return new Parser(mLexer, new DefaultParserFeedback(DefaultParserFeedback.QUIET));
	}

	
	/**
	 * 获取描述内容
	 * @param fileName
	 * @return
	 */
	public static String getTxtContext(String fileName) {
		StringBuffer sbStr = new StringBuffer("");
		try {
			File file = new File(IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_FOLDER_NAME + "//" + fileName + ".txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String r = br.readLine();
			while (r != null) {
				sbStr.append(r);
				r = br.readLine();
			}
			
		} catch (Exception e) {
			return "";
		}
		return sbStr.toString();
	}
	
	
	/**
	 * 根据内容获取标题
	 * @param context
	 * @return
	 */
	public static String getContextTitle(String context) {
		String res = "";
		try {
			String temp = context.toString().substring(0,12);
			if("<br /><br />".equals(temp)) {
				String tempContext = context.toString().substring(12);
				res = tempContext.substring(0,tempContext.indexOf("<br />"));
				//System.out.println(tempContext.substring(0,tempContext.indexOf("<br />")));
			}
		} catch (Exception e) {
			return "";
		}
		return res;
	}
}

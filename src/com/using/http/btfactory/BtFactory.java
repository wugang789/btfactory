package com.using.http.btfactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import sun.management.counter.Variability;

import com.using.http.common.HttpRequestTool;

/**
 * BT工场请求收集种子下载页面地址
 * @author using
 */
public class BtFactory {
	
	public static void main(String[] args) {
		getBtFactoryTorrentDownLoadUrl();
	}
	
	/**
	 * 提取BT工场的种子文件下载地址
	 */
	public static void getBtFactoryTorrentDownLoadUrl() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		List<LinkBeans> saveLinks = new ArrayList<LinkBeans>(0);
		outputVersionInfo();
		System.out.println("------->开始提取种子下载地址！at time：" + sdf.format(new Date()));
		String urlTitle = HttpRequestTool.getPageContent(IConfig.BT_BASE_PATH + IConfig.BT_CONTEXTURL, "GET", 100500,"GBK");
		List<LinkBeans> listLinks = extractText(urlTitle);
		System.out.println("------->列表页面请求并解析完成！at time：" + sdf.format(new Date()));
		for (int i = 0; i < listLinks.size(); i++) {
			LinkBeans link = listLinks.get(i);
			String urlRes = HttpRequestTool.getPageContent(IConfig.BT_BASE_PATH + link.getLinkUrl(), "GET", 100500,"GBK");
			
			List<LinkBeans> listLinkConText = extractDownurl(urlRes);
			for (int j = 0; j < listLinkConText.size(); j++) {
				LinkBeans linkDown = listLinkConText.get(j);
				if (linkDown.getLinkUrl().indexOf("/file.php/") != -1) {
					saveLinks.add(linkDown);
				} 
			}
			System.out.println(link.getLinkText() + " ------>collect the end time at " + sdf.format(new Date()));
		}
		System.out.println("------->下载地址提取完成！at time：" + sdf.format(new Date()));
		ExcelTools.saveDownUrl(saveLinks,IConfig.EXCEL_OUT_PATH);
		System.out.println("------->提取完成，种子文件数:(" + saveLinks.size() + "个)");
		outputMsg();
	}
	/**
	 * 提取列表页链接
	 * @param inputHtml
	 * @return
	 */
	private static List<LinkBeans> extractText(String inputHtml) {
		List<LinkBeans> listLinks = new ArrayList<LinkBeans>();
		try {
			Parser parser = new Parser(inputHtml);
			NodeFilter filter = new TagNameFilter("a");
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					LinkTag link = (LinkTag) nodes.elementAt(i);
					
					if(link.getLink().indexOf("/p2p/") != -1) {
						LinkBeans liskBean = new LinkBeans();
						liskBean.setLinkUrl(link.getLink());
						liskBean.setLinkText(link.getLinkText());
						if(!"无".equals(IConfig.LIMITED_CODE)) {
							if(link.getLinkText().indexOf(IConfig.LIMITED_CODE) != -1) {
								listLinks.add(liskBean);
							}
						} else {
							listLinks.add(liskBean);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("列表页面解析失败！\n" + e.getMessage());
		}
		
		return listLinks;
	}
	
	
	/**
	 * 抽取内容页的下载地址
	 * @param inputHtml
	 * @return
	 */
	private static List<LinkBeans> extractDownurl(String inputHtml) {
		List<LinkBeans> listLinks = new ArrayList<LinkBeans>();
		try {
			Parser parser = new Parser(inputHtml);
			NodeFilter[] filters = new TagNameFilter[]{new TagNameFilter("a"),new TagNameFilter("img")};
			OrFilter orFilter = new OrFilter(filters);
			NodeList nodes = parser.extractAllNodesThatMatch(orFilter);
			int beginIndex = inputHtml.indexOf("<div id=\"content\">") + 18;
			int curIndex = 0;
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node noderes = nodes.elementAt(i);
					if (noderes instanceof LinkTag) {
						LinkTag link = (LinkTag) noderes;
						if (link.getLink().indexOf("/file.php/") != -1) {
							LinkBeans liskBean = new LinkBeans();
							liskBean.setLinkUrl(link.getLink());
							liskBean.setLinkText(link.getLinkText());
							listLinks.add(liskBean);
							
							String keys = link.getLink().substring(link.getLink().indexOf("file.php") + 9);
							curIndex = inputHtml.lastIndexOf(keys) + 16;
							if(beginIndex != -1 && beginIndex < curIndex) {
								createTxtFile(keys, inputHtml.substring(beginIndex,curIndex));
								if((i + 1) < nodes.size()) {
									Node noderesNext = nodes.elementAt(i + 1);
									if (noderesNext instanceof LinkTag) {
										LinkTag linkNext = (LinkTag) noderesNext;
										if (linkNext.getLink().indexOf("/file.php/") == -1) {
											beginIndex = curIndex;
										} 
									} else {
										beginIndex = curIndex;
									}
								} else {
									beginIndex = curIndex;
								}
							}
						}
						
					}
					
					// 提取图片  --- 废除代码，且不删除
					if (noderes instanceof ImageTag) {
						ImageTag imageTag = (ImageTag) noderes;
						LinkBeans liskBean = new LinkBeans();
						liskBean.setLinkUrl(imageTag.getImageURL());
						listLinks.add(liskBean);
					}
					
				}
			}

		} catch (Exception e) {
			System.out.println("内容页面解析失败！\n" + e.getMessage());
			e.printStackTrace();
		}
		
		return listLinks;
	}
	
	
	
	/**
	 * 创建内容临时存放文件
	 * @param fileName
	 */
	private static void createTxtFile(String fileName, String context) {
		try {
			File file = new File(IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_FOLDER_NAME + "//" + fileName.replace(".html", "") + ".txt");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(context);
			bw.close();
		} catch (Exception e) {
			System.out.println("文件：" + fileName + "，内容写入失败！");
		}
	}
	
	
	/**
	 * 输出下载提示信息
	 */
	private static void outputMsg() {
		System.out.println("即将下载种子文件？y是,n否");
		// 接受输入
		Scanner scannerInput = new Scanner(System.in);
		String inNum = scannerInput.next();
		if("y".equals(inNum)) {
			DownLoadTorrent.startDownLoadThread();
		} else {
			System.out.println("如需下载上一次提取的种子文件,请手动 Run As -> DownLoadTorrent.java");
		}
	}
	
	/**
	 * 版本信息和提示信息
	 */
	private static void outputVersionInfo() {
		System.out.println("|---------------------------------------------------------------------|");
		System.out.println("|******************** 【BT工场种子下载程序V3.0】 ***********************|");
		System.out.println("|******************* 本程序仅供学习参考,禁止传播。**********************|");
		System.out.println("|****************** 如涉及到任何法律责任与作者无关**********************|");
		System.out.println("|---------------------------------------------------------------------|");
		System.out.println("");
		
		/* 创建内容txt存放目录 */
		File file = new File(IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_FOLDER_NAME + "//");
		if(!file.exists()) {
			file.mkdir();
		}
		
		/* 创建内容存放文件夹 */
		File fileContext = new File(IConfig.TORRENT_OUT_PATH + IConfig.CONTEXT_TORRENT_FOLDER + "//");
		if(!fileContext.exists()) {
			fileContext.mkdir();
		} 
	}
}

package com.using.http.btfactory;

/**
 * 常量存放
 * @author using
 */
public class IConfig {
	// 种子下载地址存放的excel，请勿修改
	public static final String EXCEL_OUT_PATH = "src//torrent.xls";
	
	// 线程数量
	public static final int OPEN_THREAD_COUNT = 8;
	
	// 下载种子存放主目录
	public static final String TORRENT_OUT_PATH = "F://torrent//";
	
	// 临时内容存放的文件夹名称
	public static final String CONTEXT_FOLDER_NAME = "[临时内容存放]";
	
	// 种子存放子目录
	public static final String CONTEXT_TORRENT_FOLDER = "context_torrent";
	
	// 内容过滤模式：【无,此块过滤是用indexOf判断关键字，例：美女】
	public static final String LIMITED_CODE = "无";
	
	// BT工场地址  -- 屏蔽此地址，需要的自己挖掘
	public final static String BT_BASE_PATH = "http://xxxxx.com";
	
	// BT工场的列表内容地址 xx.html代表月份
	public final static String BT_CONTEXTURL = "/00/04.html";
	
}

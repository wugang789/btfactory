package com.using.http.btfactory;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 文件下载线程类
 * @author using
 */
public class DownLoadThread implements Callable<Object>{

	private List<String> listDownUrl;
	private String index;
	
	public Object call() throws Exception {
		DownLoadTorrent.startDownloadFile(listDownUrl, index);
		System.out.println("-----------------------------------------------> 第" + index + "个线程完成操作！ <-----------------------------------------------");
		return null;
	}
	
	public DownLoadThread(List<String> listDownUrl,String index) {
		this.listDownUrl = listDownUrl;
		this.index = index;
	}

}

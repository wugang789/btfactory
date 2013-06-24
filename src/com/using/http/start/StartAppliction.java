package com.using.http.start;

import com.using.http.btfactory.BtFactory;
/**
 * BT工场种子下载程序
 * @author using
 */
public class StartAppliction {

	public static void main(String[] args) {
		/*
		 * 运行前请配置好 IConfig.java 中的参数
		 * 默认种子存放地址(需要手动创建文件夹)：F://torrent//
		 * run as BtFactory是获取种子下载地址和配图地址，torrent.xls中
		 * 如果torrent.xls中有数据，可以直接run as DownLoadTorrent开始种子地址下载
		 * 本程序仅供学习参考,禁止传播。
		 * 如涉及到任何法律责任于作者无关
		 */
		BtFactory.getBtFactoryTorrentDownLoadUrl();
	}

}

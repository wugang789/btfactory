package com.using.http.btfactory;

import java.util.List;

/**
 * HTML解析对象
 * @author using
 */
public class LinkBeans {
	private String linkUrl; // 下载地址
	private String linkText; // 文字标题
	
	/* 种子文件下载参数 */
	private String name; // 参数名称
	private String type; // 参数类型
	private String id; // 参数ID
	
	private String title;
	private String context;
	
	private List<LinkBeans> listLinkBeanIndex;
	private String indexLinkUrl;
	
	public String getIndexLinkUrl() {
		return indexLinkUrl;
	}

	public void setIndexLinkUrl(String indexLinkUrl) {
		this.indexLinkUrl = indexLinkUrl;
	}

	public List<LinkBeans> getListLinkBeanIndex() {
		return listLinkBeanIndex;
	}

	public void setListLinkBeanIndex(List<LinkBeans> listLinkBeanIndex) {
		this.listLinkBeanIndex = listLinkBeanIndex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
}

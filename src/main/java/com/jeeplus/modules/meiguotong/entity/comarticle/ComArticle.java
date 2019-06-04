/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.meiguotong.entity.comarticle;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 文章管理Entity
 * @author cdq
 * @version 2018-07-30
 */
public class ComArticle extends DataEntity<ComArticle> {
	
	private static final long serialVersionUID = 1L;
	private String title;		// 标题
	private String link;		// 链接地址
	private String delDate;		// 删除时间
	private String delBy;		// 删除人
	private Integer languageId;		// 语言id
	private String imgUrl;		// 封面
	private String content;		// content
	private Integer type;		// 1  网站文章  2 资讯
	private  Integer status;  // 1 启用2 禁用
	
	private  Integer flag;  //   1添加编辑
	private String ids;   //模块选择的ID
	
	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public ComArticle() {
		super();
		this.setIdType(IDTYPE_AUTO);
	}

	public ComArticle(String id){
		super(id);
	}

	@ExcelField(title="标题", align=2, sort=1)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@ExcelField(title="链接地址", align=2, sort=2)
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	@ExcelField(title="删除时间", align=2, sort=8)
	public String getDelDate() {
		return delDate;
	}

	public void setDelDate(String delDate) {
		this.delDate = delDate;
	}
	
	@ExcelField(title="删除人", align=2, sort=9)
	public String getDelBy() {
		return delBy;
	}

	public void setDelBy(String delBy) {
		this.delBy = delBy;
	}
	
	@ExcelField(title="语言id", align=2, sort=10)
	public Integer getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Integer languageId) {
		this.languageId = languageId;
	}
	
	@ExcelField(title="封面", align=2, sort=11)
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	@ExcelField(title="content", align=2, sort=12)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@ExcelField(title="1  网站文章  2 资讯", align=2, sort=13)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
}
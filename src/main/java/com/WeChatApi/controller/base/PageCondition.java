package com.WeChatApi.controller.base;

/**
 * 分页条件参数对象
 * @author Gang Sun
 *
 */
public abstract class PageCondition {
	
	/**
	 * 当前起始记录
	 */
	private Integer pageNo;
	
	/**
	 * 每页显示条数
	 */
	private Integer pageSize;
	
	/**
	 * 排序字段
	 */
	private String sortColumn;

	/**
	 * 排序类型: asc:正序,desc:倒序
	 */
	private String sortType;
	
	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
	
/*************************************兼容旧版分页*************************************
	/**
	 * 当前起始记录
	 */
	private Integer page ;
	
	private Integer limit;
	
	/**
	 * 每页显示条数
	 */
	private Integer rows = 20;
	
	/**
	 * 排序字段
	 */
	private String sort;

	/**
	 * 排序类型: asc:正序,desc:倒序
	 */
	private String order;

	public void setPage(Integer page) {
		this.pageNo = page;
	}

	public void setRows(Integer rows) {
		this.pageSize = rows;
	}

	public void setSort(String sort) {
		this.sortColumn = sort;
	}

	public void setOrder(String order) {
		this.sortType = order;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getPage() {
		return page;
	}
}
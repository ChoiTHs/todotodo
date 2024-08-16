package model;

import java.sql.Date;

public class TodoDTO {
	private int todoIdx;
	private String title;
	private Date createDate;
	private Date updateDate;
	private Character status;
	private int writer;
	private int categoryIdx;
	
	public TodoDTO(int todoIdx, String title, Date createDate, Date updateDate, Character status, int categoryIdx,
			int writer) {
		super();
		this.todoIdx = todoIdx;
		this.title = title;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.status = status;
		this.categoryIdx = categoryIdx;
		this.writer = writer;
	}
	public int getTodoIdx() {
		return todoIdx;
	}
	public void setTodoIdx(int todoIdx) {
		this.todoIdx = todoIdx;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Character getStatus() {
		return status;
	}
	public void setStatus(Character status) {
		this.status = status;
	}
	public  int getCategoryIdx() {
		return categoryIdx;
	}
	public void setCategoryIdx(int categoryIdx) {
		this.categoryIdx = categoryIdx;
	}
	public int getWriter() {
		return writer;
	}
	public void setWriter(int writer) {
		this.writer = writer;
	}
	
	
	
	
}

package model;

import java.sql.Date;

public class CategoryDTO {
	private int categoryIdx;
	private String name;
	private Date creatDate;
	
	
	public CategoryDTO(int categoryIdx, String name, Date creatDate) {
		super();
		this.categoryIdx = categoryIdx;
		this.name = name;
		this.creatDate = creatDate;
	}
	
	
	public int getCategoryIdx() {
		return categoryIdx;
	}
	public void setCategoryIdx(int categoryIdx) {
		this.categoryIdx = categoryIdx;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreatDate() {
		return creatDate;
	}
	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
	}
	

}

package model;

import java.sql.Date;

public class WeeklyGoalDTO {
	private int weeklygoalIdx;
	private String content;
	private Character status;
	private Date createDate;
	private Date updateDate;
	private int writer;
	public WeeklyGoalDTO(int weeklygoalIdx, String content, Character status, Date createDate, Date updateDate,
			int writer) {
		super();
		this.weeklygoalIdx = weeklygoalIdx;
		this.content = content;
		this.status = status;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.writer = writer;
	}
	public int getWeeklygoalIdx() {
		return weeklygoalIdx;
	}
	public void setWeeklygoalIdx(int weeklygoalIdx) {
		this.weeklygoalIdx = weeklygoalIdx;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Character getStatus() {
		return status;
	}
	public void setStatus(Character status) {
		this.status = status;
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
	public int getWriter() {
		return writer;
	}
	public void setWriter(int writer) {
		this.writer = writer;
	}
	
	
	

}

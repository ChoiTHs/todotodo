package model;

import java.sql.Date;

public class FriendsDTO { 
	private int followIdx;
	private int toUser;
	private int fromUser;
	private Date CreateDate;
	private Date UpdateDate;
	private Character status;
	public FriendsDTO(int followIdx, int toUser, int fromUser, Date createDate, Date updateDate, Character status) {
		super();
		this.followIdx = followIdx;
		this.toUser = toUser;
		this.fromUser = fromUser;
		CreateDate = createDate;
		UpdateDate = updateDate;
		this.status = status;
	}
	public int getFollowIdx() {
		return followIdx;
	}
	public void setFollowIdx(int followIdx) {
		this.followIdx = followIdx;
	}
	public int getToUser() {
		return toUser;
	}
	public void setToUser(int toUser) {
		this.toUser = toUser;
	}
	public int getFromUser() {
		return fromUser;
	}
	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}
	public Date getCreateDate() {
		return CreateDate;
	}
	public void setCreateDate(Date createDate) {
		CreateDate = createDate;
	}
	public Date getUpdateDate() {
		return UpdateDate;
	}
	public void setUpdateDate(Date updateDate) {
		UpdateDate = updateDate;
	}
	public Character getStatus() {
		return status;
	}
	public void setStatus(Character status) {
		this.status = status;
	}
	
	
	

}

package model;

import java.sql.Date;
import java.sql.Timestamp;

public class UserDTO {
	
	private int userIdx;
	private String nickName;
	private Timestamp createDate;
	private Date updateDate;
	private String pwd;
	private Character status;
	
	public UserDTO() {}
	public UserDTO(int userIdx, String nickName, Timestamp createDate, Date updateDate, String pwd, Character status) {
		super();
		this.userIdx = userIdx;
		this.nickName = nickName;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.pwd = pwd;
		this.status = status;
	}

	public int getUserIdx() {
		return userIdx;
	}
	public void setUserIdx(int userIdx) {
		this.userIdx = userIdx;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Character getStatus() {
		return status;
	}
	public void setStatus(Character status) {
		this.status = status;
	}
	
	
	
	
	

}

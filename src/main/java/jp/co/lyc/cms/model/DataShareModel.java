package jp.co.lyc.cms.model;

import org.springframework.web.multipart.MultipartFile;

public class DataShareModel {

	String fileNo;
	String employeeNo;
	String employeeName;
	String filePath;
	String shareUser;
	String updateUser;
	String updateTime;
	String shareStatus;
	MultipartFile dataShareFile;

	public String getFileNo() {
		return fileNo;
	}

	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getShareUser() {
		return shareUser;
	}

	public void setShareUser(String shareUser) {
		this.shareUser = shareUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getShareStatus() {
		return shareStatus;
	}

	public void setShareStatus(String shareStatus) {
		this.shareStatus = shareStatus;
	}

	public MultipartFile getDataShareFile() {
		return dataShareFile;
	}

	public void setDataShareFile(MultipartFile dataShareFile) {
		this.dataShareFile = dataShareFile;
	}

}

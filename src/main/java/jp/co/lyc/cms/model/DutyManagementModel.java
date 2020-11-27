package jp.co.lyc.cms.model;

public class DutyManagementModel {

	int rowNo;
	String employeeNo;
	String employeeName;
	String customerName;
	String stationName;
	String payOffRange;
	Integer workTime;
	String  overTimePay;
	String checkSection;
	String updateTime;
	String updateUser;
	String approvalStatus;
	
	public int getRowNo() {
		return rowNo;
	}
	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
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
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getPayOffRange() {
		return payOffRange;
	}
	public void setPayOffRange(String payOffRange) {
		this.payOffRange = payOffRange;
	}
	public Integer getWorkTime() {
		return workTime;
	}
	public void setWorkTime(Integer workTime) {
		this.workTime = workTime;
	}
	public String getOverTimePay() {
		return overTimePay;
	}
	public void setOverTimePay(String overTimePay) {
		this.overTimePay = overTimePay;
	}
	public String getCheckSection() {
		return checkSection;
	}
	public void setCheckSection(String checkSection) {
		this.checkSection = checkSection;
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
	public String getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	
}

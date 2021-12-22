package jp.co.lyc.cms.model;

import java.util.List;

public class DutyManagementModel {

	int rowNo;
	String employeeNo;
	String employeeName;
	String customerName;
	String stationName;
	String payOffRange;
	String workTime;
	String unitPrice;
	String deductionsAndOvertimePay;
	String deductionsAndOvertimePayOfUnitPrice;
	String checkSection;
	String updateTime;
	String updateUser;
	String approvalStatus;
	String approvalUser;
	String workingTimeReport;
	String cost;
	List<CostRegistrationModel> costRegistrationModel;

	public String getApprovalUser() {
		return approvalUser;
	}

	public void setApprovalUser(String approvalUser) {
		this.approvalUser = approvalUser;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public List<CostRegistrationModel> getCostRegistrationModel() {
		return costRegistrationModel;
	}

	public void setCostRegistrationModel(List<CostRegistrationModel> costRegistrationModel) {
		this.costRegistrationModel = costRegistrationModel;
	}

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

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public String getDeductionsAndOvertimePay() {
		return deductionsAndOvertimePay;
	}

	public void setDeductionsAndOvertimePay(String deductionsAndOvertimePay) {
		this.deductionsAndOvertimePay = deductionsAndOvertimePay;
	}

	public String getCheckSection() {
		return checkSection;
	}

	public void setCheckSection(String checkSection) {
		this.checkSection = checkSection;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getDeductionsAndOvertimePayOfUnitPrice() {
		return deductionsAndOvertimePayOfUnitPrice;
	}

	public void setDeductionsAndOvertimePayOfUnitPrice(String deductionsAndOvertimePayOfUnitPrice) {
		this.deductionsAndOvertimePayOfUnitPrice = deductionsAndOvertimePayOfUnitPrice;
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

	public String getWorkingTimeReport() {
		return workingTimeReport;
	}

	public void setWorkingTimeReport(String workingTimeReport) {
		this.workingTimeReport = workingTimeReport;
	}

}

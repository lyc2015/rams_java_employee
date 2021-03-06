package jp.co.lyc.cms.model;

import java.io.Serializable;

public class SituationChangesModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String startYandM;
	String endYandM;
	String classification;
	String employeeNo;
	String employeeName;
	String status;
	String employeeFormName;
	String salary;
	String waitingCost;
	String socialInsurance;
	String remark;
	String reflectYearAndMonth;
	String socialInsuranceFlag;
	String scheduleOfBonusAmount;
	String employeeFormCode;
	String intoCompanyYearAndMonth;
	String retirementYearAndMonth;
	String salaryFlag;
	String bonusFlag;
	String lastTimeBonusAmount;
	String nextBonusMonth;
	int rowNo;

	public String getWaitingCost() {
		return waitingCost;
	}

	public void setWaitingCost(String waitingCost) {
		this.waitingCost = waitingCost;
	}

	public String getNextBonusMonth() {
		return nextBonusMonth;
	}

	public void setNextBonusMonth(String nextBonusMonth) {
		this.nextBonusMonth = nextBonusMonth;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getStartYandM() {
		return startYandM;
	}

	public void setStartYandM(String startYandM) {
		this.startYandM = startYandM;
	}

	public String getEndYandM() {
		return endYandM;
	}

	public void setEndYandM(String endYandM) {
		this.endYandM = endYandM;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmployeeFormName() {
		return employeeFormName;
	}

	public void setEmployeeFormName(String employeeFormName) {
		this.employeeFormName = employeeFormName;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getSocialInsurance() {
		return socialInsurance;
	}

	public void setSocialInsurance(String socialInsurance) {
		this.socialInsurance = socialInsurance;
	}

	public String getReflectYearAndMonth() {
		return reflectYearAndMonth;
	}

	public void setReflectYearAndMonth(String reflectYearAndMonth) {
		this.reflectYearAndMonth = reflectYearAndMonth;
	}

	public String getSocialInsuranceFlag() {
		return socialInsuranceFlag;
	}

	public void setSocialInsuranceFlag(String socialInsuranceFlag) {
		this.socialInsuranceFlag = socialInsuranceFlag;
	}

	public String getScheduleOfBonusAmount() {
		return scheduleOfBonusAmount;
	}

	public void setScheduleOfBonusAmount(String scheduleOfBonusAmount) {
		this.scheduleOfBonusAmount = scheduleOfBonusAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEmployeeFormCode() {
		return employeeFormCode;
	}

	public void setEmployeeFormCode(String employeeFormCode) {
		this.employeeFormCode = employeeFormCode;
	}

	public String getIntoCompanyYearAndMonth() {
		return intoCompanyYearAndMonth;
	}

	public void setIntoCompanyYearAndMonth(String intoCompanyYearAndMonth) {
		this.intoCompanyYearAndMonth = intoCompanyYearAndMonth;
	}

	public String getSalaryFlag() {
		return salaryFlag;
	}

	public void setSalaryFlag(String salaryFlag) {
		this.salaryFlag = salaryFlag;
	}

	public String getBonusFlag() {
		return bonusFlag;
	}

	public void setBonusFlag(String bonusFlag) {
		this.bonusFlag = bonusFlag;
	}

	public String getRetirementYearAndMonth() {
		return retirementYearAndMonth;
	}

	public void setRetirementYearAndMonth(String retirementYearAndMonth) {
		this.retirementYearAndMonth = retirementYearAndMonth;
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public String getLastTimeBonusAmount() {
		return lastTimeBonusAmount;
	}

	public void setLastTimeBonusAmount(String lastTimeBonusAmount) {
		this.lastTimeBonusAmount = lastTimeBonusAmount;
	}

}

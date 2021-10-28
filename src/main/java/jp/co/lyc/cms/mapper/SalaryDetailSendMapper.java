package jp.co.lyc.cms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.EmployeeInformationModel;

@Mapper
public interface SalaryDetailSendMapper {

	public List<EmployeeInformationModel> getEmployee();

	public List<EmployeeInformationModel> getEmployeeSameFile();
}

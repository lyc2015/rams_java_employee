package jp.co.lyc.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.SalaryDetailSendMapper;
import jp.co.lyc.cms.model.EmployeeInformationModel;

@Component
public class SalaryDetailSendService {

	@Autowired
	SalaryDetailSendMapper salaryDetailSendMapper;

	public List<EmployeeInformationModel> getEmployee() {
		return salaryDetailSendMapper.getEmployee();
	}
}

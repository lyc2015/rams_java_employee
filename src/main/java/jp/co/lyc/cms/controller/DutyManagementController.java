package jp.co.lyc.cms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.mail.handlers.image_gif;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.DutyManagementModel;
import jp.co.lyc.cms.service.DutyManagementService;

@Controller
@RequestMapping(value = "/dutyManagement")
public class DutyManagementController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	DutyManagementService dutyManagementService;

	/**
	 * 登録ボタン
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectDutyManagement", method = RequestMethod.POST)
	@ResponseBody
	public List<DutyManagementModel> selectDutyManagement(@RequestBody HashMap<String, String> dutyManagementModel) {
		logger.info("DutyManagementController.selectDutyManagement:" + "検索開始");
		logger.info(dutyManagementModel.toString());
		List<DutyManagementModel> checkMod = dutyManagementService.selectDutyManagement(dutyManagementModel);
		List<DutyManagementModel> workTimeList = dutyManagementService.selectWorkTime(dutyManagementModel);
		List<CostRegistrationModel> costRegistrationModelList = dutyManagementService
				.selectCostRegistration(dutyManagementModel);
		for (int i = 0; i < checkMod.size(); i++) {
			for (int j = 0; j < workTimeList.size(); j++) {
				if (checkMod.get(i).getEmployeeNo().equals(workTimeList.get(j).getEmployeeNo())) {
					if (checkMod.get(i).getWorkTime() == null || checkMod.get(i).getWorkTime().equals("")) {
						String workTime = workTimeList.get(j).getWorkTime();
						if (workTime != null)
							checkMod.get(i).setWorkTime(workTime.replace(".0", ""));
					}
				}
			}
		}

		for (int i = 0; i < checkMod.size(); i++) {
			if (checkMod.get(i).getDeductionsAndOvertimePay() != null) {
				checkMod.get(i)
						.setDeductionsAndOvertimePay(checkMod.get(i).getDeductionsAndOvertimePay().replace(".0", ""));
			}
			if (checkMod.get(i).getDeductionsAndOvertimePayOfUnitPrice() != null) {
				checkMod.get(i).setDeductionsAndOvertimePayOfUnitPrice(
						checkMod.get(i).getDeductionsAndOvertimePayOfUnitPrice().replace(".0", ""));
			}
			checkMod.get(i).setRowNo(i + 1);
			List<CostRegistrationModel> newCostRegistrationModelList = new ArrayList<CostRegistrationModel>();
			for (int j = 0; j < costRegistrationModelList.size(); j++) {
				if (checkMod.get(i).getEmployeeNo().equals(costRegistrationModelList.get(j).getEmployeeNo())) {
					CostRegistrationModel costRegistrationModel = new CostRegistrationModel();
					costRegistrationModel.setRowNo(j);
					costRegistrationModel.setHappendDate(costRegistrationModelList.get(j).getHappendDate());
					costRegistrationModel.setDueDate(costRegistrationModelList.get(j).getDueDate());
					costRegistrationModel
							.setCostClassificationCode(costRegistrationModelList.get(j).getCostClassificationCode());
					costRegistrationModel.setRegularStatus(costRegistrationModelList.get(j).getRegularStatus());
					costRegistrationModel
							.setDetailedNameOrLine(costRegistrationModelList.get(j).getDetailedNameOrLine() == null ? ""
									: costRegistrationModelList.get(j).getDetailedNameOrLine());
					costRegistrationModel.setCost(costRegistrationModelList.get(j).getCost());
					costRegistrationModel.setRemark(costRegistrationModelList.get(j).getRemark() == null ? ""
							: costRegistrationModelList.get(j).getRemark());
					costRegistrationModel.setCostFile(costRegistrationModelList.get(j).getCostFile());
					newCostRegistrationModelList.add(costRegistrationModel);
				}
			}
			int cost = 0;
			int costTotal = 0;
			for (int j = 0; j < newCostRegistrationModelList.size(); j++) {
				cost += Integer.parseInt(newCostRegistrationModelList.get(j).getCost());
				if (!newCostRegistrationModelList.get(j).getCostClassificationCode().equals("0")) {
					costTotal += Integer.parseInt(newCostRegistrationModelList.get(j).getCost());
				}
				if (j == newCostRegistrationModelList.size() - 1) {
					newCostRegistrationModelList.get(j).setCostTotal(String.valueOf(costTotal));
				}
			}
			checkMod.get(i).setCostRegistrationModel(newCostRegistrationModelList);
			checkMod.get(i).setCost(String.valueOf(cost));
		}

		// ソート
		List<DutyManagementModel> returnMod = new ArrayList<DutyManagementModel>();
		// 承認済み
		for (int i = 0; i < checkMod.size(); i++) {
			if (checkMod.get(i).getApprovalStatus() != null && checkMod.get(i).getApprovalStatus().equals("1")) {
				returnMod.add(checkMod.get(i));
				checkMod.remove(i);
				i--;
			}
		}
		// 報告書あり
		for (int i = 0; i < checkMod.size(); i++) {
			if (checkMod.get(i).getWorkTime() != null) {
				returnMod.add(checkMod.get(i));
				checkMod.remove(i);
				i--;
			}
		}
		// 他
		for (int i = 0; i < checkMod.size(); i++) {
			returnMod.add(checkMod.get(i));
		}
		// 番号つけ
		for (int i = 0; i < returnMod.size(); i++) {
			returnMod.get(i).setRowNo(i + 1);
		}

		logger.info("DutyManagementController.selectDutyManagement:" + "検索終了");
		return returnMod;
	}

	/**
	 * アップデート
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateDutyManagement", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateDutyManagement(@RequestBody HashMap<String, String> dutyManagementModel) {
		logger.info("DutyManagementController.updateDutyManagement:" + "アップデート開始");
		boolean result = false;
		HashMap<String, String> sendMap = dutyManagementModel;
		String employeeFirstName = dutyManagementService
				.getFirstName(getSession().getAttribute("employeeNo").toString());
		sendMap.put("approvalUser", employeeFirstName);

		result = dutyManagementService.updateDutyManagement(sendMap);
		logger.info("DutyManagementController.updateDutyManagement:" + "アップデート終了");
		return result;
	}
}

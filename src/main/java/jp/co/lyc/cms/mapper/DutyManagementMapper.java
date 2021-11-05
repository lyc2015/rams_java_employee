package jp.co.lyc.cms.mapper;

import java.util.HashMap;
import java.util.List;

import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.DutyManagementModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DutyManagementMapper {
	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public List<DutyManagementModel> selectDutyManagement(HashMap<String, String> sendMap);

	/**
	 * 費用詳細情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public List<CostRegistrationModel> selectCostRegistration(HashMap<String, String> sendMap);

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */
	public void updateDutyManagement(HashMap<String, String> sendMap);

	public List<DutyManagementModel> selectWorkTime(HashMap<String, String> dutyManagementModel);
}

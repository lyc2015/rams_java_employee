package jp.co.lyc.cms.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.BreakTimeModel;
import jp.co.lyc.cms.model.DutyRegistrationModel;
import jp.co.lyc.cms.model.EmployeeWorkTimeModel;

@Mapper
public interface DutyRegistrationMapper {

	/**
	 * インサート
	 * 
	 * @param sendMap
	 */
	public void insertDutyRegistration(HashMap<String, Object> sendMap);

	/**
	 * インサート
	 * 
	 * @param sendMap
	 */
	public void insertDuty(Map<String, Object> sendMap);

	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public BreakTimeModel selectDutyRegistration(Map<String, Object> sendMap);

	/**
	 * 
	 * @param sendMap
	 * @return
	 */
	public EmployeeWorkTimeModel[] selectDuty(Map<String, Object> sendMap);

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */
	public void updateDutyRegistration(HashMap<String, Object> sendMap);

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */
	public void updateDuty(Map<String, Object> sendMap);

	/**
	 * 上位お客様削除
	 * 
	 * @param customerNo
	 */
	public void deleteDutyRegistration(String dutyRegistrationNo);

	/**
	 * 初期化する
	 * 
	 * @param customerNo
	 */
	public void clearData(Map<String, Object> sendMap);

	/**
	 * 判断承認済み
	 * 
	 * @param sendMap
	 * @return
	 */
	public String getFlag(Map<String, Object> sendMap);

	public String selectWorkRepot(Map<String, Object> sendMap);
	public String getApprovalStatus(Map<String, Object> sendMap);
}

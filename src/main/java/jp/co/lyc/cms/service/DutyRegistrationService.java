package jp.co.lyc.cms.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.DutyRegistrationMapper;
import jp.co.lyc.cms.model.BreakTimeModel;
import jp.co.lyc.cms.model.DutyRegistrationModel;
import jp.co.lyc.cms.model.EmployeeWorkTimeModel;

@Component
public class DutyRegistrationService {

	@Autowired
	DutyRegistrationMapper dutyRegistrationMapper;
		
	/**
	 * 画面情報検索
	 * @param TopCustomerNo
	 * @return
	 */
	public BreakTimeModel selectDutyRegistration(Map<String, Object> sendMap) {
		BreakTimeModel resultMod = dutyRegistrationMapper.selectDutyRegistration(sendMap);
		return resultMod;
	}
	/**
	 * 
	 * @param sendMap
	 * @return
	 */
	public EmployeeWorkTimeModel[] selectDuty(Map<String, Object> sendMap) {
		EmployeeWorkTimeModel[] resultMod = dutyRegistrationMapper.selectDuty(sendMap);
		return resultMod;
	}
	
	/**
	 * 
	 * @param sendMap
	 * @return
	 */
	public BreakTimeModel selectBreakTime(Map<String, Object> sendMap) {
		BreakTimeModel resultMod = dutyRegistrationMapper.selectDutyRegistration(sendMap);
		return resultMod;
	}
	
	/**
	 * インサート
	 * @param sendMap
	 */
	public boolean insertDutyRegistration(HashMap<String, Object> sendMap) {
		boolean result = true;
		try {
			dutyRegistrationMapper.insertDutyRegistration(sendMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}
	
	/**
	 * インサート
	 * @param sendMap
	 */
	public boolean insertDuty(Map<String, Object> sendMap) {
		boolean result = true;
		try {
			dutyRegistrationMapper.insertDuty(sendMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * アップデート
	 * @param sendMap
	 */
	public boolean updateDutyRegistration(HashMap<String, Object> sendMap) {
		boolean result = true;
		try {
			dutyRegistrationMapper.updateDutyRegistration(sendMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}
	
	/**
	 * アップデート
	 * @param sendMap
	 */
	public boolean updateDuty(Map<String, Object> sendMap) {
		boolean result = true;
		try {
			dutyRegistrationMapper.updateDuty(sendMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}
	
	/**
	 * 
	 * @param 
	 */
	
	public boolean deleteDutyRegistration(String dutyRegistrationNo) {
		boolean result = true;
		try {
			dutyRegistrationMapper.deleteDutyRegistration(dutyRegistrationNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}
	
	/**
	 * 初期化する
	 * @param sendMap
	 * @return
	 */
	public void clearData(Map<String, Object> sendMap) {
		dutyRegistrationMapper.clearData(sendMap);		
	}
	
	/**
	 * 判断承認済み
	 * @param sendMap
	 * @return
	 */
	public String getFlag(Map<String, Object> sendMap) {
		return 	dutyRegistrationMapper.getFlag(sendMap);		
	}
	public String selectWorkRepot(Map<String, Object> sendMap) {
		return dutyRegistrationMapper.selectWorkRepot(sendMap);	
	}
}

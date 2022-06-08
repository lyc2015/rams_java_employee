package jp.co.lyc.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.WorkRepotMapper;
import jp.co.lyc.cms.model.WorkRepotModel;

@Component
public class WorkRepotService {

	@Autowired
	WorkRepotMapper workRepotMapper;

	/**
	 * 画面情報検索 本月
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public void selectCheckWorkRepot(WorkRepotModel workRepotModel) {
		workRepotMapper.selectCheckWorkRepot(workRepotModel);
	}

	/**
	 * 画面情報検索 本月
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public void insertWorkRepotByYearAndMonth(WorkRepotModel workRepotModel) {
		workRepotMapper.insertWorkRepotByYearAndMonth(workRepotModel);
	}
	
	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<WorkRepotModel> selectWorkRepot(WorkRepotModel workRepotModel) {
		List<WorkRepotModel> resultMod = workRepotMapper.selectWorkRepot(workRepotModel);
		return resultMod;
	}

	/**
	 * ファイルをクリア
	 * 
	 * @param sendMap
	 */

	public boolean clearworkRepot(WorkRepotModel workRepotModel) {
		boolean result = true;
		try {
			workRepotMapper.clearworkRepot(workRepotModel);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */

	public boolean updateWorkRepot(WorkRepotModel workRepotModel) {
		boolean result = true;
		try {
			workRepotMapper.updateWorkRepot(workRepotModel);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * ファイル名入力
	 * 
	 * @param sendMap
	 */

	public boolean updateWorkRepotFile(WorkRepotModel workRepotModel) {
		boolean result = true;
		try {
			workRepotMapper.updateWorkRepotFile(workRepotModel);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * 勤務時間入力有り無し判断
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public String selectWorkTime(WorkRepotModel workRepotModel) {
		return workRepotMapper.selectWorkTime(workRepotModel);
	}
}

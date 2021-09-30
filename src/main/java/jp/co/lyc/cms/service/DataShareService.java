package jp.co.lyc.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.mapper.DataShareMapper;
import jp.co.lyc.cms.model.DataShareModel;
import jp.co.lyc.cms.model.WorkRepotModel;

@Component
public class DataShareService {

	@Autowired
	DataShareMapper dataShareMapper;

	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<DataShareModel> selectDataShareFile() {
		List<DataShareModel> resultMod = dataShareMapper.selectDataShareFile();
		return resultMod;
	}

	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public List<DataShareModel> selectDataShareFileOnly() {
		List<DataShareModel> resultMod = dataShareMapper.selectDataShareFileOnly();
		return resultMod;
	}

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */

	public boolean updateDataShare(DataShareModel dataShareModel) {
		boolean result = true;
		try {
			dataShareMapper.updateDataShare(dataShareModel);
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

	public boolean updateDataShareFile(DataShareModel dataShareModel) {
		boolean result = true;
		try {
			dataShareMapper.updateDataShareFile(dataShareModel);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	/**
	 * 削除
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public boolean deleteDataShare(String fileNo) {
		boolean result = true;
		try {
			dataShareMapper.deleteDataShare(fileNo);
			dataShareMapper.updateDataShareAfterDelete(fileNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}
}

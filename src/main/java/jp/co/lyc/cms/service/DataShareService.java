package jp.co.lyc.cms.service;

import java.util.ArrayList;
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
	 * @param string
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<DataShareModel> selectDataShareFile(DataShareModel dataShareModel) {
		List<DataShareModel> resultMod = dataShareModel.getDataStatus().equals("0")
				? dataShareMapper.selectDataShareFile(dataShareModel)
				: dataShareMapper.selectDataShareFileUpload(dataShareModel);
		return resultMod;
	}

	/**
	 * 画面情報検索
	 * 
	 * @param dataShareModel
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public List<DataShareModel> selectDataShareFileOnly(DataShareModel dataShareModel) {
		List<DataShareModel> resultMod = dataShareMapper.selectDataShareFileOnly(dataShareModel);
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

	public boolean updateDataSharesTo2(ArrayList<String> fileNoList) {
		boolean result = true;
		try {
			dataShareMapper.updateDataSharesTo2(fileNoList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	public boolean updateDataSharesTo3(ArrayList<String> fileNoList) {
		boolean result = true;
		try {
			dataShareMapper.updateDataSharesTo3(fileNoList);
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

	/**
	 * return maxFileNo + 1
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public DataShareModel getMaxFileNo() {
		return dataShareMapper.getMaxFileNo();
	}

	/**
	 * 削除
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public boolean deleteDataShares(ArrayList<String> fileNoList) {
		boolean result = true;
		try {
			dataShareMapper.deleteDataShares(fileNoList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return result = false;
		}
		return result;
	}

	public boolean updateFileName(DataShareModel dataShareModel) {
		return dataShareMapper.updateFileName(dataShareModel);
	}
}

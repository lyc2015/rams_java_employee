package jp.co.lyc.cms.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.lyc.cms.model.DataShareModel;
import jp.co.lyc.cms.model.WorkRepotModel;

@Mapper
public interface DataShareMapper {
	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */
	public List<DataShareModel> selectDataShareFile(DataShareModel dataShareModel);

	public List<DataShareModel> selectDataShareFileUpload(DataShareModel dataShareModel);

	public List<DataShareModel> selectDataShareFileOnly(DataShareModel dataShareModel);

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */
	public void updateDataShare(DataShareModel dataShareModel);

	public void updateDataSharesTo2(ArrayList<String> fileNoList);

	public void updateDataSharesTo3(ArrayList<String> fileNoList);

	/**
	 * ファイル名入力
	 * 
	 * @param sendMap
	 */
	public void updateDataShareFile(DataShareModel dataShareModel);

	/**
	 * 削除
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public void deleteDataShare(String fileNo);

	/**
	 * 削除後更新
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public void updateDataShareAfterDelete(String fileNo);

	/**
	 * return maxFileNo + 1
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public DataShareModel getMaxFileNo();

	/**
	 * 削除
	 * 
	 * @param topCustomerMod
	 * @return
	 */
	public void deleteDataShares(ArrayList<String> fileNoList);

	public boolean updateFileName(DataShareModel dataShareModel);

}

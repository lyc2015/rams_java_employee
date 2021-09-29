package jp.co.lyc.cms.mapper;

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
	public void selectCheckWorkRepot(WorkRepotModel workRepotModel);

	public List<DataShareModel> selectDataShareFile();

	/**
	 * アップデート
	 * 
	 * @param sendMap
	 */
	public void updateDataShare(String fileNo);

	/**
	 * ファイル名入力
	 * 
	 * @param sendMap
	 */
	public void updateDataShareFile(DataShareModel dataShareModel);
}

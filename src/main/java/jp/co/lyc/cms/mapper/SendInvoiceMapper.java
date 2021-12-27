package jp.co.lyc.cms.mapper;

import java.util.HashMap;
import java.util.List;

import jp.co.lyc.cms.model.AccountInfoModel;
import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.SendInvoiceModel;
import jp.co.lyc.cms.model.SendInvoiceWorkTimeModel;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SendInvoiceMapper {
	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<SendInvoiceModel> selectSendInvoice(HashMap<String, String> dutyManagementModel);

	public List<CostRegistrationModel> selectCostRegistration(HashMap<String, String> dutyManagementModel);

	public List<SendInvoiceWorkTimeModel> selectSendInvoiceByCustomerNo(HashMap<String, String> dutyManagementModel);

	public void insertInvoiceData(HashMap<String, String> model);

	public void updateInvoiceData(HashMap<String, String> model);

	public void insertNewInvoiceData(HashMap<String, String> model);

	public void deleteInvoiceData(HashMap<String, String> model);

	public void updateAllInvoiceData(HashMap<String, String> model);

	public List<ModelClass> selectBankAccountInfo();

	public AccountInfoModel getAccountInfo(String bankCode);

	public void updateSendLetter(HashMap<String, String> model);

	public void deleteInvoiceDataAll(HashMap<String, String> model);
}

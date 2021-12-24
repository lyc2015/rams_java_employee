package jp.co.lyc.cms.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.lyc.cms.model.AccountInfoModel;
import jp.co.lyc.cms.model.CostRegistrationModel;
import jp.co.lyc.cms.model.ModelClass;
import jp.co.lyc.cms.model.SendInvoiceModel;
import jp.co.lyc.cms.model.SendInvoiceWorkTimeModel;
import jp.co.lyc.cms.mapper.SendInvoiceMapper;

@Component
public class SendInvoiceService {

	@Autowired
	SendInvoiceMapper sendInvoiceMapper;

	/**
	 * 画面情報検索
	 * 
	 * @param TopCustomerNo
	 * @return
	 */

	public List<SendInvoiceModel> selectSendInvoice(HashMap<String, String> dutyManagementModel) {
		List<SendInvoiceModel> resultMod = sendInvoiceMapper.selectSendInvoice(dutyManagementModel);
		return resultMod;
	}

	public List<CostRegistrationModel> selectCostRegistration(HashMap<String, String> dutyManagementModel) {
		List<CostRegistrationModel> resultMod = sendInvoiceMapper.selectCostRegistration(dutyManagementModel);
		return resultMod;
	}

	public List<SendInvoiceWorkTimeModel> selectSendInvoiceByCustomerNo(HashMap<String, String> dutyManagementModel) {
		List<SendInvoiceWorkTimeModel> resultMod = sendInvoiceMapper.selectSendInvoiceByCustomerNo(dutyManagementModel);
		return resultMod;
	}

	public void insertInvoiceData(HashMap<String, String> model) {
		sendInvoiceMapper.insertInvoiceData(model);
	}

	public void updateInvoiceData(HashMap<String, String> model) {
		sendInvoiceMapper.updateInvoiceData(model);
	}

	public void insertNewInvoiceData(HashMap<String, String> model) {
		sendInvoiceMapper.insertNewInvoiceData(model);
	}

	public void deleteInvoiceData(HashMap<String, String> model) {
		sendInvoiceMapper.deleteInvoiceData(model);
	}

	public void updateAllInvoiceData(HashMap<String, String> model) {
		sendInvoiceMapper.updateAllInvoiceData(model);
	}

	public List<ModelClass> selectBankAccountInfo() {
		return sendInvoiceMapper.selectBankAccountInfo();
	}

	public AccountInfoModel getAccountInfo(String bankCode) {
		return sendInvoiceMapper.getAccountInfo(bankCode);
	}

	public void updateSendLetter(HashMap<String, String> model) {
		sendInvoiceMapper.updateSendLetter(model);
	}
}

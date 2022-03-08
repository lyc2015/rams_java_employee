package jp.co.lyc.cms.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import jp.co.lyc.cms.mapper.AccountInfoMapper;
import jp.co.lyc.cms.model.AccountInfoModel;

@Component
public class AccountInfoService {

	@Autowired
	AccountInfoMapper bankMapper;

	/**
	 * 口座情報の検索
	 * 
	 * @param employeeOrCustomerNo
	 * @param accountBelongsStatus
	 * @return
	 */

	public List<AccountInfoModel> selectAccountInfo(String employeeNo) {
		List<AccountInfoModel> resultMod = bankMapper.selectAccountInfo(employeeNo);
		return resultMod;
	}

	/**
	 * アープデート
	 * 
	 * @param sendMap
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean update(HashMap<String, String> sendMap) {
		try {
			int count = bankMapper.getAccountCount(sendMap).getAccountNumber();
			if (count > 0) {
				bankMapper.updateAccount(sendMap);
			} else {
				bankMapper.insertAccount(sendMap);
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 削除
	 * 
	 * @param sendMap
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean delete(HashMap<String, String> sendMap) {
		try {
			bankMapper.deleteAccount(sendMap);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * インサートとアップデートの値を設定
	 * 
	 * @param bankCol
	 * @return
	 */
	public HashMap<String, String> setSendMap(AccountInfoModel bankCol) {
		HashMap<String, String> sendMap = new HashMap<>();
		sendMap.put("accountBelongsStatus", bankCol.getAccountBelongsStatus());
		sendMap.put("bankCode", bankCol.getBankCode());
		sendMap.put("accountName", bankCol.getAccountName());
		sendMap.put("accountNo", bankCol.getAccountNo());
		sendMap.put("bankBranchCode", bankCol.getBankBranchCode());
		sendMap.put("accountTypeStatus", bankCol.getAccountTypeStatus());
		sendMap.put("updateUser", bankCol.getUpdateUser());
		sendMap.put("employeeOrCustomerNo", bankCol.getEmployeeOrCustomerNo());
		return sendMap;
	}
}

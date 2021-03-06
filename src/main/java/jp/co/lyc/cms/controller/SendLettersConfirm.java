package jp.co.lyc.cms.controller;

import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.AllEmployName;
import jp.co.lyc.cms.model.EmailModel;
import jp.co.lyc.cms.model.EmployeeModel;
import jp.co.lyc.cms.model.SalesSituationModel;
import jp.co.lyc.cms.model.SendLettersConfirmModel;
import jp.co.lyc.cms.service.CustomerInfoService;
import jp.co.lyc.cms.service.CustomerTopInsertService;
import jp.co.lyc.cms.service.SendLettersConfirmService;
import jp.co.lyc.cms.util.UtilsCheckMethod;
import jp.co.lyc.cms.util.UtilsController;

@Controller
@RequestMapping(value = "/sendLettersConfirm")
public class SendLettersConfirm extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SendLettersConfirmService sendLettersConfirmService;

	@Autowired
	UtilsController utils;

	@Autowired
	CustomerInfoService customerInfoService;

	/**
	 * データを取得
	 * 
	 * @param model
	 * @return List
	 */

	@RequestMapping(value = "/getSalesEmps", method = RequestMethod.POST)
	@ResponseBody
	public List<SendLettersConfirmModel> getSalesEmps(@RequestBody SendLettersConfirmModel model) {

		logger.info("getSalesEmps:" + "検索開始");
		List<SendLettersConfirmModel> sendLettersConfirmModelList = new ArrayList<SendLettersConfirmModel>();
		try {
			sendLettersConfirmModelList = sendLettersConfirmService.getSalesEmps(model.getEmployeeNos());
			int i = 1;
			for (SendLettersConfirmModel sendletter : sendLettersConfirmModelList) {
				sendletter.setIndex(i++);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int j = 0; j < sendLettersConfirmModelList.size(); j++) {
			ArrayList<String> resumeInfoTemp = new ArrayList<String>();
			/*
			 * if (!(sendLettersConfirmModelList.get(j).getResumeInfo1() == null ||
			 * sendLettersConfirmModelList.get(j).getResumeInfo1().equals(""))) {
			 * resumeInfoTemp.add(sendLettersConfirmModelList.get(j).getResumeInfo1()
			 * .split("/")[sendLettersConfirmModelList.get(j).getResumeInfo1().split("/").
			 * length - 1]); } if (!(sendLettersConfirmModelList.get(j).getResumeInfo2() ==
			 * null || sendLettersConfirmModelList.get(j).getResumeInfo2().equals(""))) {
			 * resumeInfoTemp.add(sendLettersConfirmModelList.get(j).getResumeInfo2()
			 * .split("/")[sendLettersConfirmModelList.get(j).getResumeInfo2().split("/").
			 * length - 1]); }
			 */
			if (!(sendLettersConfirmModelList.get(j).getResumeName1() == null
					|| sendLettersConfirmModelList.get(j).getResumeName1().equals(""))) {
				resumeInfoTemp.add(sendLettersConfirmModelList.get(j).getResumeName1());
			}
			if (!(sendLettersConfirmModelList.get(j).getResumeName2() == null
					|| sendLettersConfirmModelList.get(j).getResumeName2().equals(""))) {
				resumeInfoTemp.add(sendLettersConfirmModelList.get(j).getResumeName2());
			}
			sendLettersConfirmModelList.get(j).setResumeInfoList(resumeInfoTemp);
			if (resumeInfoTemp.size() > 0) {
				sendLettersConfirmModelList.get(j).setResumeInfoName(resumeInfoTemp.get(0));
			} else {
				sendLettersConfirmModelList.get(j).setResumeInfoName("");
			}

			if (sendLettersConfirmModelList.get(j).getUnitPrice() != null
					&& !sendLettersConfirmModelList.get(j).getUnitPrice().equals("")) {
				sendLettersConfirmModelList.get(j).setUnitPrice(
						String.valueOf(Integer.parseInt(sendLettersConfirmModelList.get(j).getUnitPrice()) / 10000));
			}
		}
		logger.info("getSalesEmps" + "検索結束");
		return sendLettersConfirmModelList;
	}

	@RequestMapping(value = "/updateSalesSentence", method = RequestMethod.POST)
	@ResponseBody
	public void updateSalesSentence(@RequestBody SendLettersConfirmModel model) {

		logger.info("updateSalesSentence:" + "更新開始");
		if (model.getUnitPrice() != null && !model.getUnitPrice().equals(""))
			model.setUnitPrice(String.valueOf(Integer.parseInt(model.getUnitPrice()) * 10000));
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		sendLettersConfirmService.updateSalesSentence(model);
		logger.info("updateSalesSentence" + "更新結束");
	}

	@RequestMapping(value = "/getAllEmpsWithResume", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getAllEmpsWithResume() {

		logger.info("getSalesEmps:" + "検索開始");
		List<SalesSituationModel> sendLettersConfirmModelList = new ArrayList<SalesSituationModel>();
		try {
			sendLettersConfirmModelList = sendLettersConfirmService.getAllEmpsWithResume();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesEmps" + "検索結束");
		return sendLettersConfirmModelList;
	}

	@RequestMapping(value = "/getLoginUserInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<EmployeeModel> getLoginUserInfo() {

		logger.info("getSalesEmps:" + "検索開始");
		String lobinUserNo = getSession().getAttribute("employeeNo").toString();
		List<EmployeeModel> sendLettersConfirmModelList = new ArrayList<EmployeeModel>();
		try {
			sendLettersConfirmModelList = sendLettersConfirmService.getLoginUserInfo(lobinUserNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sendLettersConfirmModelList.size() > 0 && sendLettersConfirmModelList.get(0).getPhoneNo() != null
				&& !sendLettersConfirmModelList.get(0).getPhoneNo().equals("")) {
			sendLettersConfirmModelList.get(0)
					.setPhoneNo(sendLettersConfirmModelList.get(0).getPhoneNo().substring(0, 3) + "-"
							+ sendLettersConfirmModelList.get(0).getPhoneNo().substring(3, 7) + "-"
							+ sendLettersConfirmModelList.get(0).getPhoneNo().substring(7, 11));
		}
		logger.info("getSalesEmps" + "検索結束");
		return sendLettersConfirmModelList;
	}

	@RequestMapping(value = "/getMail", method = RequestMethod.POST)
	@ResponseBody
	public List<EmployeeModel> getMail() {

		logger.info("getSalesEmps:" + "検索開始");

		List<EmployeeModel> sendLettersConfirmModelList = new ArrayList<EmployeeModel>();
		try {
			sendLettersConfirmModelList = sendLettersConfirmService.getMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesEmps" + "検索結束");
		return sendLettersConfirmModelList;
	}

	@RequestMapping(value = "/sendMailWithFile", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> sendMailWithFile(@RequestBody EmailModel emailModel) {
		String errorsMessage = "";
		Map<String, Object> resulterr = new HashMap<>();
		if (emailModel.getMailFrom() == null || emailModel.getMailFrom().equals("")) {
			errorsMessage = "登録者メールアドレス入力されてない、確認してください。";

			resulterr.put("errorsMessage", errorsMessage);// エラーメッセージ
			return resulterr;
		}

		logger.info("sendMailWithFile:" + "送信開始");

		// EmailModel emailModel = new EmailModel();
		// String mail = es.getEmployeeCompanyMail(loginModel.getEmployeeNo());
		// 受信人のメール
		emailModel.setUserName(getSession().getAttribute("employeeName").toString());
		emailModel.setPassword("Lyc2020-0908-");
		// emailModel.setFromAddress(model.getMailFrom());
		emailModel.setContextType("text/html;charset=utf-8");
		if (emailModel.getPaths() != null) {
			for (int i = 0; i < emailModel.getPaths().length; i++) {
				if (emailModel.getPaths()[i].equals(" ")) {
					errorsMessage = "添付ファイルが存在していない。";

					resulterr.put("errorsMessage", errorsMessage);// エラーメッセージ
					return resulterr;
				}
			}
		}
		try {
			// checkEmail(emailModel.getSelectedmail());
			if (emailModel.getPaths() == null || emailModel.getPaths().length == 0)
				utils.EmailSend(emailModel);
			else
				utils.sendMailWithFile(emailModel);
		} catch (Exception e) {
			errorsMessage = "送信エラー発生しました。";

			resulterr.put("errorsMessage", errorsMessage);// エラーメッセージ
			return resulterr;
		}

		customerInfoService.updateCustomerNo(emailModel.getSelectedCustomer());
		logger.info("sendMailWithFile" + "送信結束");
		return resulterr;
	}

	// 要員追加機能の新規 20201216 張棟 START
	/**
	 * 名前と所属を取る<br/>
	 * <br/>
	 * 要員送信確認画面初期化する時、全て社内要員とBP社員の名前と所属を取る<br/>
	 */
	@RequestMapping(value = "/getAllEmployInfoName", method = RequestMethod.POST)
	@ResponseBody
	public List<AllEmployName> getAllEmployInfoName() {
		List<AllEmployName> EmployInfo = new ArrayList<AllEmployName>();
		EmployInfo = sendLettersConfirmService.getAllEmployInfoName();
		return EmployInfo;
	}
	// 要員追加機能の新規 20201216 張棟 END

	public static boolean checkEmail(String email) {
		String soapRequestData = "" + "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">"
				+ "  <soap:Body>" + "   <IsValidEmail xmlns=\"http://www.webservicex.net\">" + "    <Email>" + email
				+ "</Email>" + "   </IsValidEmail>" + "  </soap:Body>" + "</soap:Envelope>";

		try {
			URL u = new URL("http://www.webservicex.net/ValidateEmail.asmx?op=IsValidEmail");
			URLConnection uc = u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
			pw.println(soapRequestData);
			pw.close();

			DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = bf.newDocumentBuilder();
			Document document = db.parse(uc.getInputStream());

			String res = document.getElementsByTagName("IsValidEmailResult").item(0).getTextContent();

			return Boolean.parseBoolean(res);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

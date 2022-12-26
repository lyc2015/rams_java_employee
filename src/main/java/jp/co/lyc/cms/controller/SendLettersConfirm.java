package jp.co.lyc.cms.controller;

import java.io.File;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.AllEmployName;
import jp.co.lyc.cms.model.EmailModel;
import jp.co.lyc.cms.model.EmployeeModel;
import jp.co.lyc.cms.model.ResultModel;
import jp.co.lyc.cms.model.S3Model;
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
	
	@Autowired
	S3Controller s3Controller;

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
        // 统一向前端传円，而不是万円
				// sendLettersConfirmModelList.get(j).setUnitPrice(
				// 		String.valueOf(Double.parseDouble(sendLettersConfirmModelList.get(j).getUnitPrice()) / 10000));
			}
		}
		logger.info("getSalesEmps" + "検索結束");
		return sendLettersConfirmModelList;
	}

	@RequestMapping(value = "/updateSalesSentence", method = RequestMethod.POST)
	@ResponseBody
	public void updateSalesSentence(@RequestBody SendLettersConfirmModel model) {

		logger.info("updateSalesSentence:" + "更新開始");
//		if (model.getUnitPrice() != null && !model.getUnitPrice().equals(""))
//			model.setUnitPrice(String.valueOf(Double.parseDouble(model.getUnitPrice()) * 10000));
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
	public ResultModel sendMailWithFile(@RequestParam(value = "emailModel") String JSONEmailModel,@RequestParam(value = "myfiles") MultipartFile[] files) {
		ResultModel resulterr = new ResultModel();
		EmailModel emailModel = JSON.parseObject(JSONEmailModel, new TypeReference<EmailModel>() {
		});

		
		if (emailModel.getMailFrom() == null || emailModel.getMailFrom().equals("")) {
			resulterr.setErrMsg("登録者メールアドレス入力されてない、確認してください。");
			return resulterr;
		}

		logger.info("sendMailWithFile:" + "送信開始");

		// EmailModel emailModel = new EmailModel();
		// String mail = es.getEmployeeCompanyMail(loginModel.getEmployeeNo());
		// 受信人のメール
		emailModel.setUserName(getSession().getAttribute("employeeName").toString());
		emailModel.setPassword("Lyc2020-0908-");
		emailModel.setFiles(files);
		// emailModel.setFromAddress(model.getMailFrom());
		emailModel.setContextType("text/html;charset=utf-8");
		if (emailModel.getPaths() != null) {
			for (int i = 0; i < emailModel.getPaths().length; i++) {
				if (emailModel.getPaths()[i].equals(" ")) {
					resulterr.setErrMsg("添付ファイルが存在していない。");
					return resulterr;
				}
			}
		}
		// checkEmail(emailModel.getSelectedmail());
		if ((emailModel.getPaths() == null || emailModel.getPaths().length == 0)&&(files == null || files.length == 0))
			resulterr = utils.EmailSend(emailModel);
		else  {
			resulterr = utils.sendMailWithFile(emailModel);
		}
		if(resulterr.getResult() == false) {
			return resulterr;
		}

		customerInfoService.updateCustomerNo(emailModel.getSelectedCustomer());
		logger.info("sendMailWithFile" + "送信結束");
    resulterr.setSuccess();
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
	
	/**
	 * 履歴書アップロード
	 * 
	 * @param
	 * @return boolean
---	 */
	@RequestMapping(value = "/uploadTempFile", method = RequestMethod.POST)
	@ResponseBody
	public String uploadTempFile(@RequestParam(value = "dataShareFile", required = false) MultipartFile dataShareFile) throws Exception {
		String getFilename;
		try {
			getFilename=upload(dataShareFile);
		} catch (Exception e) {
			return null;
		}
		try {
			S3Model s3Model = new S3Model();
			String filePath = getFilename.replaceAll("\\\\", "/");
			String fileKey = filePath.split("file/")[1];
			s3Model.setFileKey(fileKey);
			s3Model.setFilePath(filePath);
			s3Controller.uploadFile(s3Model);
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public final static String UPLOAD_PATH_PREFIX = "C:"+File.separator+"file"+File.separator;
	public String upload(MultipartFile workRepotFile) {
		if (workRepotFile== null) {
			return "";
		}
		String realPath = new String(UPLOAD_PATH_PREFIX + "履歴書/Tmp_File");
		File file = new File(realPath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		String fileName =workRepotFile.getOriginalFilename();
		try {
			File newFile = new File(file.getAbsolutePath() + File.separator + fileName);
			workRepotFile.transferTo(newFile);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return realPath + "/" + fileName;
	}
}

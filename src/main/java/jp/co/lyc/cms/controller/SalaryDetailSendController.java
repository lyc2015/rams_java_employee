package jp.co.lyc.cms.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.EmailModel;
import jp.co.lyc.cms.model.EmployeeInformationModel;
import jp.co.lyc.cms.service.SalaryDetailSendService;
import jp.co.lyc.cms.util.UtilsController;

@Controller
@RequestMapping(value = "/SalaryDetailSend")
public class SalaryDetailSendController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	String errorsMessage = "";
	@Autowired
	SalaryDetailSendService salaryDetailSendService;
	@Autowired
	UtilsController utils;

	public final static String UPLOAD_PATH_PREFIX = "C:" + File.separator + "file" + File.separator
			+ "salaryDetailSend";

	@RequestMapping(value = "/getEmployee", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getEmployee() {
		logger.info("SalaryDetailSendController.getEmployee:" + "検索開始");

		List<EmployeeInformationModel> employeeList = new ArrayList<EmployeeInformationModel>();
		employeeList = salaryDetailSendService.getEmployee();
		for (int i = 0; i < employeeList.size(); i++) {
			employeeList.get(i).setRowNo(i + 1);
		}
		Map<String, Object> result = new HashMap<>();
		result.put("data", employeeList);
		logger.info("SalaryDetailSendController.getEmployee:" + "検索結束");
		return result;
	}

	@RequestMapping(value = "/updatePDF", method = RequestMethod.POST)
	@ResponseBody
	public void updatePDF(@RequestParam(value = "pdfUpdate", required = false) MultipartFile pdfUpdate)
			throws Exception {
		logger.info("SalaryDetailSendController.updatePDF:" + "上传開始");
		if (pdfUpdate != null) {
			upload(pdfUpdate);
		}
		logger.info("SalaryDetailSendController.updatePDF:" + "上传終了");
	}

	public String upload(MultipartFile workRepotFile) {
		if (workRepotFile == null) {
			return "";
		}
		String realPath = new String(UPLOAD_PATH_PREFIX);
		File file = new File(realPath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		String fileName = workRepotFile.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		String newName = fileName.substring(0, fileName.lastIndexOf(".")) + "." + suffix;
		try {
			File newFile = new File(file.getAbsolutePath() + File.separator + newName);
			workRepotFile.transferTo(newFile);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return realPath + "/" + newName;
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

		// 受信人のメール
		emailModel.setUserName(getSession().getAttribute("employeeName").toString());
		emailModel.setPassword("Lyc2020-0908-");
		emailModel.setContextType("text/html;charset=utf-8");
		String[] names = { emailModel.getResumePath() };
		String[] paths = { "c:/file/salaryDetailSend/" + emailModel.getResumePath() };
		emailModel.setNames(names);
		emailModel.setPaths(paths);
		try {
			// 送信
			utils.sendMailWithFile(emailModel);

			// 送信後削除
			File file = new File("c:/file/salaryDetailSend/" + emailModel.getResumePath());
			file.delete();

		} catch (Exception e) {
			errorsMessage = "送信エラー発生しました。";

			resulterr.put("errorsMessage", errorsMessage);// エラーメッセージ
			return resulterr;
		}

		logger.info("sendMailWithFile" + "送信結束");
		return resulterr;
	}
}

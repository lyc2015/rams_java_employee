package jp.co.lyc.cms.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.ListUtils;
import org.castor.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.internal.FIFOCache;
import com.amazonaws.util.StringUtils;

import ch.qos.logback.core.joran.conditional.IfAction;
import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.SalesSituationModel;
import jp.co.lyc.cms.model.SalesContent;
import jp.co.lyc.cms.service.SalesSituationService;
import jp.co.lyc.cms.util.StatusCodeToMsgMap;
import jp.co.lyc.cms.validation.SalesSituationValidation;
import software.amazon.ion.impl.PrivateScalarConversions.ValueVariant;

@Controller
@RequestMapping(value = "/salesSituation")
public class SalesSituationController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SalesSituationService salesSituationService;

	// 12月
	public static final String DECEMBER = "12";
	// 1月
	public static final String JANUARY = "01";

	/**
	 * データを取得 ffff
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/getSalesSituation", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getSalesSituation(@RequestBody SalesSituationModel model) {

		logger.info("getSalesSituation:" + "検索開始");
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> developLanguageList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> T010SalesSituationList = new ArrayList<SalesSituationModel>();
		try {
			// 現在の日付を取得
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);
			// 社員営業され日付
			String salesDate = getSalesDate(model.getSalesYearAndMonth());
//			String salesDate = String.valueOf(Integer.valueOf(model.getSalesYearAndMonth()) + 1);
			salesSituationList = salesSituationService.getSalesSituationModel(model.getSalesYearAndMonth(), curDate,
					salesDate);
			developLanguageList = salesSituationService.getDevelopLanguage();
			T010SalesSituationList = salesSituationService.getT010SalesSituation(model.getSalesYearAndMonth(), curDate,
					salesDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesSituation" + "検索結束");

		for (int i = 0; i < salesSituationList.size(); i++) {
			// 番号
			salesSituationList.get(i).setRowNo(i + 1);

			// 社員名
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(BP)");
			}

			// お客様
			salesSituationList.get(i).setCustomer("");

			// 開発言語
			String developLanguage = "";
			for (int j = 0; j < developLanguageList.size(); j++) {
				if (salesSituationList.get(i).getDevelopLanguage1() != null && salesSituationList.get(i)
						.getDevelopLanguage1().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage2() != null && salesSituationList.get(i)
						.getDevelopLanguage2().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage3() != null && salesSituationList.get(i)
						.getDevelopLanguage3().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage4() != null && salesSituationList.get(i)
						.getDevelopLanguage4().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage5() != null && salesSituationList.get(i)
						.getDevelopLanguage5().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
			}

			if (developLanguage.length() > 0)
				developLanguage = developLanguage.substring(0, developLanguage.length() - 1);
			salesSituationList.get(i).setDevelopLanguage(developLanguage);

			// T010
			for (int j = 0; j < T010SalesSituationList.size(); j++) {
				if (salesSituationList.get(i).getEmployeeNo().equals(T010SalesSituationList.get(j).getEmployeeNo())) {
					salesSituationList.get(i)
							.setSalesProgressCode(T010SalesSituationList.get(j).getSalesProgressCode());
					salesSituationList.get(i).setSalesDateUpdate(T010SalesSituationList.get(j).getSalesYearAndMonth());
					salesSituationList.get(i).setInterviewDate1(T010SalesSituationList.get(j).getInterviewDate1());
					salesSituationList.get(i).setStationCode1(T010SalesSituationList.get(j).getStationCode1());
					salesSituationList.get(i)
							.setInterviewCustomer1(T010SalesSituationList.get(j).getInterviewCustomer1());
					salesSituationList.get(i).setInterviewDate2(T010SalesSituationList.get(j).getInterviewDate2());
					salesSituationList.get(i).setStationCode2(T010SalesSituationList.get(j).getStationCode2());
					salesSituationList.get(i)
							.setInterviewCustomer2(T010SalesSituationList.get(j).getInterviewCustomer2());
					salesSituationList.get(i).setHopeLowestPrice(T010SalesSituationList.get(j).getHopeLowestPrice());
					salesSituationList.get(i).setHopeHighestPrice(T010SalesSituationList.get(j).getHopeHighestPrice());
					salesSituationList.get(i)
							.setCustomerContractStatus(T010SalesSituationList.get(j).getCustomerContractStatus());
					salesSituationList.get(i).setRemark(T010SalesSituationList.get(j).getRemark());
					salesSituationList.get(i).setSalesStaff(T010SalesSituationList.get(j).getSalesStaff());
					salesSituationList.get(i)
							.setSalesPriorityStatus(T010SalesSituationList.get(j).getSalesPriorityStatus());
					salesSituationList.get(i).setCustomer(T010SalesSituationList.get(j).getConfirmCustomer());
					salesSituationList.get(i).setPrice(T010SalesSituationList.get(j).getConfirmPrice());
				}
			}
		}
		return salesSituationList;
	}

	// 社員営業され日付
	private String getSalesDate(String getSalesYearAndMonth) {
		String salesDate = "";
//		if(getSalesYearAndMonth.substring(4) == DECEMBER) {
		if (DECEMBER.equals(getSalesYearAndMonth.substring(4))) {
			salesDate = String.valueOf(Integer.valueOf(getSalesYearAndMonth.substring(0, 4)) + 1) + JANUARY;
		} else {
			salesDate = String.valueOf(Integer.valueOf(getSalesYearAndMonth) + 1);
		}
		return salesDate;
	}

	@RequestMapping(value = "/updateSalesSituation", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateSalesSituation(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("updateSalesSituation:" + "検索開始");
		String[] errorsMessage = new String[] { "" };
		DataBinder binder = new DataBinder(model);
		binder.setValidator(new SalesSituationValidation());
		binder.validate();
		BindingResult results = binder.getBindingResult();
		Map<String, Object> result = new HashMap<>();
		if (results.hasErrors()) {
			results.getAllErrors().forEach((o) -> {
				FieldError error = (FieldError) o;
				errorsMessage[0] += error.getDefaultMessage();// エラーメッセージ
			});
			result.put("errorsMessage", errorsMessage[0]);// エラーメッセージ
			return result;
		}
		int index = 0;
		try {
			String salesDate = getSalesDate(model.getSalesYearAndMonth()).trim().toString();
			model.setSalesDateUpdate(salesDate);
			index = salesSituationService.insertSalesSituation(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("updateREcords", index);
		logger.info("updateSalesSituation" + "検索結束");
		return result;
	}

	/**
	 * データを取得
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/updateEmployeeSiteInfo", method = RequestMethod.POST)
	@ResponseBody
	public int updateEmployeeSiteInfo(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("updateSalesSituation:" + "検索開始");
		int index = 0;
		int index1 = 0;
		try {
			index = salesSituationService.updateEmployeeSiteInfo(model);
			index1 = salesSituationService.updateSalesSituation(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "検索結束");
		return index + index1;
	}

	/**
	 * 画面初期化のデータを取得
	 * 
	 * @param emp
	 * @return List
	 */
	@RequestMapping(value = "/getPersonalSalesInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getPersonalSalesInfo(@RequestBody SalesSituationModel model) {

		logger.info("getPersonalSalesInfo:" + "検索開始");
		int count = salesSituationService.getCount(model.getEmployeeNo());
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		if (count == 0) {
			try {
				salesSituationList = salesSituationService.getPersonalSalesInfo(model.getEmployeeNo());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				salesSituationList = salesSituationService.getPersonalSalesInfoFromT019(model.getEmployeeNo());
				/* 2020/12/09 STRAT 張棟 */
				// 時間のフォーマットを変更する。例えば「202009->2020/09」
				if (salesSituationList != null && salesSituationList.size() != 0
						&& !StringUtils.isNullOrEmpty(salesSituationList.get(0).getTheMonthOfStartWork())) {
					salesSituationList.get(0)
							.setTheMonthOfStartWork(DateFormat(salesSituationList.get(0).getTheMonthOfStartWork()));
				} else {
					// 取るデータがnullの時

				}
				/* 2020/12/09 END 張棟 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		logger.info("updateSalesSituation" + "検索結束");
		return salesSituationList;
	}

	/**
	 * データを取得
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/updateEmployeeAddressInfo", method = RequestMethod.POST)
	@ResponseBody
	public int updateEmployeeAddressInfo(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("getPersonalSalesInfo:" + "検索開始");
		int index = 0;
		try {
			index = salesSituationService.updateEmployeeAddressInfo(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "検索結束");
		return index;
	}

	/**
	 * 子画面の更新ボタンを押下する
	 **/
	@RequestMapping(value = "/updateSalesSentence", method = RequestMethod.POST)
	@ResponseBody
	public int updateSalesSentence(@RequestBody SalesContent model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("getPersonalSalesInfo:" + "検索開始");
		int index = 0;
		model.setBeginMonth(model.getTempDate());
		try {
			index = salesSituationService.updateSalesSentence(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "検索結束");
		return index;
	}

	/**
	 * 画面の可変項目変更する
	 * 
	 * @param model
	 * @return Map
	 * @throws ParseException
	 */
	@RequestMapping(value = "/changeDataStatus", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changeDataStatus(@RequestBody SalesSituationModel model) throws ParseException {

		Map<String, Object> result = new HashMap<>();
		HttpSession session = getSession();
		model.setUpdateUser(session.getAttribute("employeeName").toString());

		logger.info("changeDataStatus:" + "チェック開始");
		String errorsMessage = "";
		if (model.getSalesProgressCode() != null && (model.getSalesProgressCode().equals("4")
				|| model.getSalesProgressCode().equals("5") || model.getSalesProgressCode().equals("6"))) {
			if (model.getCustomerContractStatus() == null || model.getCustomerContractStatus().equals("")) {
				errorsMessage += "契約区分 ";
			}
			if (model.getCustomer() == null || model.getCustomer().equals("")) {
				errorsMessage += "確定客様 ";
			}
			if (model.getPrice() == null || model.getPrice().equals("")) {
				errorsMessage += "確定単価  ";
			}

			if (!errorsMessage.equals("")) {
				errorsMessage += "を入力してください。";
				result.put("errorsMessage", errorsMessage);
				return result;
			} else {
				String nextAdmission = salesSituationService.getEmpNextAdmission(model.getEmployeeNo());
				if (nextAdmission != null
						&& (Integer.parseInt(nextAdmission) > Integer.parseInt(model.getAdmissionEndDate()))) {
					errorsMessage += "次の現場存在しています、データをチェックしてください。";
					result.put("errorsMessage", errorsMessage);
					return result;
				} else {
					salesSituationService.insertEmpNextAdmission(model);
				}
			}
		}

		logger.info("changeDataStatus:" + "更新開始");
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		int updateCount = 0;
		try {
			// 現在の日付を取得
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);

			// 社員営業され日付
			String salesDate = getSalesDate(model.getAdmissionEndDate());
			if (DECEMBER.equals(model.getAdmissionEndDate().substring(4, 6))) {
				salesDate = String.valueOf(Integer.valueOf(model.getAdmissionEndDate().substring(0, 4)) + 1) + JANUARY;
			} else {
				salesDate = String.valueOf(Integer.valueOf(model.getAdmissionEndDate().substring(0, 6)) + 1);
			}

			model.setSalesYearAndMonth(salesDate);

			if (salesSituationService.checkEmpNoAndYM(model).size() == 0) {
				salesSituationService.insertDataStatus(model);
			}

			// テーブルT010SalesSituation項目を変更する
			updateCount = salesSituationService.updateDataStatus(model);

			// テーブルT006EmployeeSiteInfo項目を変更する
			updateCount = salesSituationService.updateEMPInfo(model);

			// テーブルT011BpInfoSupplement項目を変更する
			updateCount = salesSituationService.updateBPEMPInfo(model);

			// 日付に基づいて一覧を取得
			salesSituationList = salesSituationService
					.getSalesSituationModel(model.getAdmissionEndDate().substring(0, 6), curDate, salesDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("changeDataStatus" + "更新結束");
		result.put("result", salesSituationList);
		return result;
	}

	/**
	 * 営業フォルダー
	 * 
	 * @return Map
	 * @throws ParseException
	 * @throws Exception
	 */
	@RequestMapping(value = "/makeDirectory", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> makeDirectory(@RequestBody SalesSituationModel model) throws ParseException, Exception {

		Map<String, Object> result = new HashMap<>();
		ArrayList<String> employeeNoList = model.getEmployeeNoList();
		ArrayList<String> resumeInfo1List = model.getResumeInfo1List();
		ArrayList<String> resumeInfo2List = model.getResumeInfo2List();

		for (int i = 0; i < employeeNoList.size(); i++) {
			String mkDirectoryPath = "c:\\file\\営業フォルダー\\" + model.getSalesYearAndMonth() + "\\"
					+ employeeNoList.get(i);
			if (mkDirectory(mkDirectoryPath)) {
				// System.out.println(mkDirectoryPath + "建立完毕");
				if (resumeInfo1List.get(i) != null)
					fileChannelCopy(resumeInfo1List.get(i), mkDirectoryPath);
				if (resumeInfo2List.get(i) != null)
					fileChannelCopy(resumeInfo2List.get(i), mkDirectoryPath);
			} else {
				// System.out.println(mkDirectoryPath + "建立失败！此目录或许已经存在！");
			}
		}
		//cmd指令打开对应文件夹
		//Runtime.getRuntime().exec("cmd /c start explorer c:\\file\\営業フォルダー\\" + model.getSalesYearAndMonth());

		return result;
	}

	/*
	 * 
	 * */
	public static boolean mkDirectory(String path) {
		File file = null;
		try {
			file = new File(path);
			if (!file.exists()) {
				return file.mkdirs();
			} else {
				return false;
			}
		} catch (Exception e) {
		} finally {
			file = null;
		}
		return false;
	}

	public void fileChannelCopy(String sFile, String tFile) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		File s = new File(sFile);
		String fileName = sFile.substring(sFile.lastIndexOf("/") + 1);
		File t = new File(tFile + "//" + fileName);
		if (s.exists() && s.isFile()) {
			try {
				fi = new FileInputStream(s);
				fo = new FileOutputStream(t);
				in = fi.getChannel();// 得到对应的文件通道
				out = fo.getChannel();// 得到对应的文件通道
				in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fi.close();
					in.close();
					fo.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文字列の実装
	 */
	private String DateFormat(String date) {
		String dateStr = date.substring(0, 4) + "/" + date.substring(4, 6);
		return dateStr;
	}

}

package jp.co.lyc.cms.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.ArrayUtils;
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
import jp.co.lyc.cms.model.BpInfoModel;
import jp.co.lyc.cms.model.MasterModel;
import jp.co.lyc.cms.model.S3Model;
import jp.co.lyc.cms.model.SalesContent;
import jp.co.lyc.cms.service.SalesSituationService;
import jp.co.lyc.cms.service.UtilsService;
import jp.co.lyc.cms.util.StatusCodeToMsgMap;
import jp.co.lyc.cms.validation.SalesSituationValidation;
import software.amazon.ion.impl.PrivateScalarConversions.ValueVariant;

@Controller
@RequestMapping(value = "/salesSituation")
public class SalesSituationController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SalesSituationService salesSituationService;

	@Autowired
	S3Controller s3Controller;

	@Autowired
	UtilsService utilsService;

	// 12???
	public static final String DECEMBER = "12";
	// 1???
	public static final String JANUARY = "01";

	/**
	 * ?????????????????? ffff
	 * 
	 * @param emp
	 * @return List
	 * @throws ParseException
	 */

	@RequestMapping(value = "/getSalesSituationNew", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getSalesSituationNew(@RequestBody SalesSituationModel model)
			throws ParseException {
		List<String> employeeNoList = new ArrayList<String>();
		List<String> BpNoList = new ArrayList<String>();

		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> bpSalesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> developLanguageList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> T010SalesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> siteRoleCodeList = new ArrayList<SalesSituationModel>();
		List<BpInfoModel> T011BpInfoSupplementList = new ArrayList<BpInfoModel>();
		try {
			// ????????????????????????
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);

			// ????????????????????????
			String salesDate = getSalesDate(model.getSalesYearAndMonth());
			if (Integer.parseInt(model.getSalesYearAndMonth()) <= Integer.parseInt(curDate)) {
				employeeNoList = salesSituationService.getEmployeeNoListBefore(salesDate);
				T010SalesSituationList = salesSituationService.getT010SalesSituationBefore(model.getSalesYearAndMonth(),
						curDate, salesDate);
			} else {
				employeeNoList = salesSituationService.getEmployeeNoList(model.getSalesYearAndMonth(), salesDate);
				BpNoList = salesSituationService.getBpNoList(model.getSalesYearAndMonth(), salesDate);
				T010SalesSituationList = salesSituationService.getT010SalesSituation(model.getSalesYearAndMonth(),
						curDate, salesDate);
			}
			if (employeeNoList.size() > 0)
				salesSituationList = salesSituationService.getSalesSituationList(employeeNoList);
			if (BpNoList.size() > 0)
				bpSalesSituationList = salesSituationService.getBpSalesSituationList(BpNoList);
			developLanguageList = salesSituationService.getDevelopLanguage();
			T011BpInfoSupplementList = salesSituationService.getT011BpInfoSupplement();
			siteRoleCodeList = salesSituationService.getSiteRoleCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesSituation" + "????????????");

		for (int i = 0; i < bpSalesSituationList.size(); i++) {
			salesSituationList.add(bpSalesSituationList.get(i));
		}

		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSiteRoleCode() == null
					|| salesSituationList.get(i).getSiteRoleCode().equals("")) {
				for (int j = 0; j < siteRoleCodeList.size(); j++) {
					if (salesSituationList.get(i).getEmployeeNo().equals(siteRoleCodeList.get(j).getEmployeeNo())) {
						salesSituationList.get(i).setSiteRoleCode(siteRoleCodeList.get(j).getSiteRoleCode() == null ? ""
								: siteRoleCodeList.get(j).getSiteRoleCode());
					}
				}
			}
		}

		for (int i = 0; i < salesSituationList.size(); i++) {
			// ?????????
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 3).equals("BPR")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(BPR)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(BP)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SP)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SC)");
			}

			// ???????????????
			if (salesSituationList.get(i).getResumeInfo1() != null
					&& !salesSituationList.get(i).getResumeInfo1().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName1();
				String resumetemp = salesSituationList.get(i).getResumeInfo1();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName1(resumeName);
			}

			if (salesSituationList.get(i).getResumeInfo2() != null
					&& !salesSituationList.get(i).getResumeInfo2().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName2();
				String resumetemp = salesSituationList.get(i).getResumeInfo2();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName2(resumeName);
			}

			// ?????????
			salesSituationList.get(i).setCustomer("");

			// ????????????
			for (int j = 0; j < developLanguageList.size(); j++) {
				if (salesSituationList.get(i).getDevelopLanguage1() != null && salesSituationList.get(i)
						.getDevelopLanguage1().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage1(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage2() != null && salesSituationList.get(i)
						.getDevelopLanguage2().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage2(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage3() != null && salesSituationList.get(i)
						.getDevelopLanguage3().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage3(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
				if (salesSituationList.get(i).getDevelopLanguage4() != null && salesSituationList.get(i)
						.getDevelopLanguage4().equals(developLanguageList.get(j).getDevelopLanguageCode()))
					salesSituationList.get(i)
							.setDevelopLanguage4(developLanguageList.get(j).getDevelopLanguageName() + ",");
				// developLanguage += developLanguageList.get(j).getDevelopLanguageName() + ",";
			}
			String developLanguage = (salesSituationList.get(i).getDevelopLanguage1().equals(",") ? ""
					: salesSituationList.get(i).getDevelopLanguage1())
					+ (salesSituationList.get(i).getDevelopLanguage2().equals(",") ? ""
							: salesSituationList.get(i).getDevelopLanguage2())
					+ (salesSituationList.get(i).getDevelopLanguage3().equals(",") ? ""
							: salesSituationList.get(i).getDevelopLanguage3())
					+ (salesSituationList.get(i).getDevelopLanguage4().equals(",") ? ""
							: salesSituationList.get(i).getDevelopLanguage4());

			if (developLanguage.length() > 0)
				developLanguage = developLanguage.substring(0, developLanguage.length() - 1);
			salesSituationList.get(i).setDevelopLanguage(developLanguage);

			// T010
			for (int j = 0; j < T010SalesSituationList.size(); j++) {
				if (salesSituationList.get(i).getEmployeeNo().equals(T010SalesSituationList.get(j).getEmployeeNo())) {
					if (salesSituationList.get(i).getAdmissionEndDate() == null
							&& salesSituationList.get(i).getScheduledEndDate() != null) {
						salesSituationList.get(i).setAdmissionEndDate(salesSituationList.get(i).getScheduledEndDate());
					}

					// ????????????????????????
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					String curDate = sdf.format(date);
					if (Integer.parseInt(model.getSalesYearAndMonth()) <= Integer.parseInt(curDate)) {
						salesSituationList.get(i)
								.setSalesProgressCode(T010SalesSituationList.get(j).getSalesProgressCode());
						salesSituationList.get(i)
								.setSalesDateUpdate(T010SalesSituationList.get(j).getSalesYearAndMonth());
						salesSituationList.get(i).setInterviewDate1(T010SalesSituationList.get(j).getInterviewDate1());
						salesSituationList.get(i).setStationCode1(T010SalesSituationList.get(j).getStationCode1());
						salesSituationList.get(i)
								.setInterviewCustomer1(T010SalesSituationList.get(j).getInterviewCustomer1());
						salesSituationList.get(i).setInterviewDate2(T010SalesSituationList.get(j).getInterviewDate2());
						salesSituationList.get(i).setStationCode2(T010SalesSituationList.get(j).getStationCode2());
						salesSituationList.get(i)
								.setInterviewCustomer2(T010SalesSituationList.get(j).getInterviewCustomer2());
						salesSituationList.get(i)
								.setHopeLowestPrice(T010SalesSituationList.get(j).getHopeLowestPrice());
						salesSituationList.get(i)
								.setHopeHighestPrice(T010SalesSituationList.get(j).getHopeHighestPrice());
						salesSituationList.get(i)
								.setCustomerContractStatus(T010SalesSituationList.get(j).getCustomerContractStatus());
						salesSituationList.get(i).setRemark1(T010SalesSituationList.get(j).getRemark1());
						salesSituationList.get(i).setRemark2(T010SalesSituationList.get(j).getRemark2());
						salesSituationList.get(i).setSalesStaff(T010SalesSituationList.get(j).getSalesStaff());
						salesSituationList.get(i)
								.setSalesPriorityStatus(T010SalesSituationList.get(j).getSalesPriorityStatus());
						salesSituationList.get(i).setCustomer(T010SalesSituationList.get(j).getConfirmCustomer());
						salesSituationList.get(i).setPrice(T010SalesSituationList.get(j).getConfirmPrice());
					} else {
						if (!(salesSituationList.get(i).getAdmissionEndDate() != null && Integer
								.parseInt(salesSituationList.get(i).getAdmissionEndDate().substring(0, 6)) >= Integer
										.parseInt(T010SalesSituationList.get(j).getSalesYearAndMonth()))
								|| (salesSituationList.get(i).getAdmissionEndDate() != null
										&& Integer.parseInt(salesSituationList.get(i).getAdmissionEndDate().substring(0,
												6)) == Integer.parseInt(model.getSalesYearAndMonth()))) {
							salesSituationList.get(i)
									.setSalesProgressCode(T010SalesSituationList.get(j).getSalesProgressCode());
							salesSituationList.get(i)
									.setSalesDateUpdate(T010SalesSituationList.get(j).getSalesYearAndMonth());
							salesSituationList.get(i)
									.setInterviewDate1(T010SalesSituationList.get(j).getInterviewDate1());
							salesSituationList.get(i).setStationCode1(T010SalesSituationList.get(j).getStationCode1());
							salesSituationList.get(i)
									.setInterviewCustomer1(T010SalesSituationList.get(j).getInterviewCustomer1());
							salesSituationList.get(i)
									.setInterviewDate2(T010SalesSituationList.get(j).getInterviewDate2());
							salesSituationList.get(i).setStationCode2(T010SalesSituationList.get(j).getStationCode2());
							salesSituationList.get(i)
									.setInterviewCustomer2(T010SalesSituationList.get(j).getInterviewCustomer2());
							salesSituationList.get(i)
									.setHopeLowestPrice(T010SalesSituationList.get(j).getHopeLowestPrice());
							salesSituationList.get(i)
									.setHopeHighestPrice(T010SalesSituationList.get(j).getHopeHighestPrice());
							salesSituationList.get(i).setCustomerContractStatus(
									T010SalesSituationList.get(j).getCustomerContractStatus());
							salesSituationList.get(i).setRemark1(T010SalesSituationList.get(j).getRemark1());
							salesSituationList.get(i).setRemark2(T010SalesSituationList.get(j).getRemark2());
							salesSituationList.get(i).setSalesStaff(T010SalesSituationList.get(j).getSalesStaff());
							salesSituationList.get(i)
									.setSalesPriorityStatus(T010SalesSituationList.get(j).getSalesPriorityStatus());
							salesSituationList.get(i).setCustomer(T010SalesSituationList.get(j).getConfirmCustomer());
							salesSituationList.get(i).setPrice(T010SalesSituationList.get(j).getConfirmPrice());
						}
					}
				}
			}
		}

		// ??????????????????
		List<SalesSituationModel> salesSituationListTemp = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> salesProgressCodeListTemp = new ArrayList<SalesSituationModel>();

		// ??????????????????
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesProgressCode() != null
					&& !(salesSituationList.get(i).getSalesProgressCode().equals("1")
							|| salesSituationList.get(i).getSalesProgressCode().equals("0"))
					&& (salesSituationList.get(i).getSalesPriorityStatus() != null
							&& salesSituationList.get(i).getSalesPriorityStatus().equals("1"))) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesProgressCode() != null
					&& !(salesSituationList.get(i).getSalesProgressCode().equals("1")
							|| salesSituationList.get(i).getSalesProgressCode().equals("0"))
					&& (salesSituationList.get(i).getSalesPriorityStatus() != null
							&& salesSituationList.get(i).getSalesPriorityStatus().equals("2"))) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}

		// ?????????????????????
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (!salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
			}
		}

		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				for (int j = 0; j < T011BpInfoSupplementList.size(); j++) {

					if (salesSituationListTemp.get(i).getEmployeeNo()
							.equals(T011BpInfoSupplementList.get(j).getBpEmployeeNo())) {

						salesSituationListTemp.remove(i);
						i--;
						break;
					}
				}
			}
		}

		// ???????????????
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getSalesProgressCode() != null) {
				if (salesSituationListTemp.get(i).getSalesProgressCode().equals("1")
						|| salesSituationListTemp.get(i).getSalesProgressCode().equals("0")) {
					salesProgressCodeListTemp.add(salesSituationListTemp.get(i));
					salesSituationListTemp.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < salesProgressCodeListTemp.size(); i++) {
			salesSituationListTemp.add(salesProgressCodeListTemp.get(i));
		}

		// ???????????????
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			salesSituationListTemp.get(i).setRowNo(i + 1);

			// ????????????
			SimpleDateFormat sdf = new SimpleDateFormat();// ???????????????
			sdf.applyPattern("yyyyMMdd");// a???am/pm?????????
			Date date = new Date();// ??????????????????
			String time = sdf.format(date);
			if (salesSituationListTemp.get(i).getInterviewDate1() != null
					&& salesSituationListTemp.get(i).getInterviewDate1().length() > 8) {
				if (Integer.parseInt(salesSituationListTemp.get(i).getInterviewDate1().substring(0, 8)) < Integer
						.parseInt(time)) {
					salesSituationListTemp.get(i).setInterviewDate1("");
					salesSituationListTemp.get(i).setInterviewCustomer1("");
					salesSituationListTemp.get(i).setStationCode1("");
				}
			}
			if (salesSituationListTemp.get(i).getInterviewDate2() != null
					&& salesSituationListTemp.get(i).getInterviewDate2().length() > 8) {
				if (Integer.parseInt(salesSituationListTemp.get(i).getInterviewDate2().substring(0, 8)) < Integer
						.parseInt(time)) {
					salesSituationListTemp.get(i).setInterviewDate2("");
					salesSituationListTemp.get(i).setInterviewCustomer2("");
					salesSituationListTemp.get(i).setStationCode2("");
				}
			}

			// ?????????????????? ??????->????????????
			if (salesSituationListTemp.get(i).getAdmissionEndDate() == null
					|| salesSituationListTemp.get(i).getAdmissionEndDate().equals("")) {
				if (salesSituationListTemp.get(i).getSalesProgressCode() == null
						|| salesSituationListTemp.get(i).getSalesProgressCode().equals("")) {
					salesSituationListTemp.get(i).setSalesProgressCode("2");
				}
			}
		}
		return salesSituationListTemp;
	}

	/**
	 * ?????????????????? ffff
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/getSalesSituation", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getSalesSituation(@RequestBody SalesSituationModel model) {

		logger.info("getSalesSituation:" + "????????????");
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> developLanguageList = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> T010SalesSituationList = new ArrayList<SalesSituationModel>();
		List<BpInfoModel> T011BpInfoSupplementList = new ArrayList<BpInfoModel>();

		try {
			// ????????????????????????
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);
			// ????????????????????????
			String salesDate = getSalesDate(model.getSalesYearAndMonth());
//			String salesDate = String.valueOf(Integer.valueOf(model.getSalesYearAndMonth()) + 1);
			salesSituationList = salesSituationService.getSalesSituationModel(model.getSalesYearAndMonth(), curDate,
					salesDate);
			developLanguageList = salesSituationService.getDevelopLanguage();
			T010SalesSituationList = salesSituationService.getT010SalesSituation(model.getSalesYearAndMonth(), curDate,
					salesDate);
			T011BpInfoSupplementList = salesSituationService.getT011BpInfoSupplement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getSalesSituation" + "????????????");

		for (int i = 0; i < salesSituationList.size(); i++) {
			// ?????????
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(BP)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SP)");
			} else if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationList.get(i).setEmployeeName(salesSituationList.get(i).getEmployeeName() + "(SC)");
			}

			// ???????????????
			if (salesSituationList.get(i).getResumeInfo1() != null
					&& !salesSituationList.get(i).getResumeInfo1().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName1();
				String resumetemp = salesSituationList.get(i).getResumeInfo1();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName1(resumeName);
			}

			if (salesSituationList.get(i).getResumeInfo2() != null
					&& !salesSituationList.get(i).getResumeInfo2().equals("")) {
				String resumeName = salesSituationList.get(i).getResumeName2();
				String resumetemp = salesSituationList.get(i).getResumeInfo2();
				resumeName = resumetemp.split("/")[resumetemp.split("/").length - 1].split("_")[0] + "_" + resumeName
						+ "." + resumetemp.split("/")[resumetemp.split("/").length - 1].split(
								"\\.")[resumetemp.split("/")[resumetemp.split("/").length - 1].split("\\.").length - 1];
				salesSituationList.get(i).setResumeName2(resumeName);
			}

			// ?????????
			salesSituationList.get(i).setCustomer("");

			// ????????????
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
					salesSituationList.get(i).setRemark1(T010SalesSituationList.get(j).getRemark1());
					salesSituationList.get(i).setRemark2(T010SalesSituationList.get(j).getRemark2());
					salesSituationList.get(i).setSalesStaff(T010SalesSituationList.get(j).getSalesStaff());
					salesSituationList.get(i)
							.setSalesPriorityStatus(T010SalesSituationList.get(j).getSalesPriorityStatus());
					salesSituationList.get(i).setCustomer(T010SalesSituationList.get(j).getConfirmCustomer());
					salesSituationList.get(i).setPrice(T010SalesSituationList.get(j).getConfirmPrice());
				}
			}
		}

		// ??????????????????
		List<SalesSituationModel> salesSituationListTemp = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> salesProgressCodeListTemp = new ArrayList<SalesSituationModel>();

		// ??????????????????
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesPriorityStatus() != null
					&& salesSituationList.get(i).getSalesPriorityStatus().equals("1")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getSalesPriorityStatus() != null
					&& salesSituationList.get(i).getSalesPriorityStatus().equals("2")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}

		// ?????????????????????
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (!salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")
					&& !salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SC")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("SP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
				salesSituationList.remove(i);
				i--;
			}
		}
		for (int i = 0; i < salesSituationList.size(); i++) {
			if (salesSituationList.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				salesSituationListTemp.add(salesSituationList.get(i));
			}
		}

		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getEmployeeNo().substring(0, 2).equals("BP")) {
				for (int j = 0; j < T011BpInfoSupplementList.size(); j++) {
					if (salesSituationListTemp.get(i).getEmployeeNo()
							.equals(T011BpInfoSupplementList.get(j).getBpEmployeeNo())
							&& Integer.parseInt(model.getSalesYearAndMonth()) > Integer
									.parseInt(T011BpInfoSupplementList.get(j).getBpOtherCompanyAdmissionEndDate())) {
						salesSituationListTemp.remove(i);
						i--;
						break;
					}
				}
			}
		}

		// ???????????????
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			if (salesSituationListTemp.get(i).getSalesProgressCode() != null) {
				if ((salesSituationListTemp.get(i).getSalesProgressCode().equals("1")
						|| salesSituationListTemp.get(i).getSalesProgressCode().equals("0"))
						&& !(salesSituationListTemp.get(i).getSalesPriorityStatus() != null
								&& (salesSituationListTemp.get(i).getSalesPriorityStatus().equals("1")
										|| salesSituationListTemp.get(i).getSalesPriorityStatus().equals("2")))) {
					salesProgressCodeListTemp.add(salesSituationListTemp.get(i));
					salesSituationListTemp.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < salesProgressCodeListTemp.size(); i++) {
			salesSituationListTemp.add(salesProgressCodeListTemp.get(i));
		}

		// ???????????????
		for (int i = 0; i < salesSituationListTemp.size(); i++) {
			salesSituationListTemp.get(i).setRowNo(i + 1);
		}
		return salesSituationListTemp;
	}

	// ????????????????????????
	private String getSalesDate(String getSalesYearAndMonth) {
		String salesDate = getSalesYearAndMonth;

		// ????????????
		/*
		 * if (DECEMBER.equals(getSalesYearAndMonth.substring(4))) { salesDate =
		 * String.valueOf(Integer.valueOf(getSalesYearAndMonth.substring(0, 4)) + 1) +
		 * JANUARY; } else { salesDate =
		 * String.valueOf(Integer.valueOf(getSalesYearAndMonth) + 1); }
		 */
		return salesDate;
	}

	@RequestMapping(value = "/updateSalesSituation", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateSalesSituation(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("updateSalesSituation:" + "????????????");
		String[] errorsMessage = new String[] { "" };
		DataBinder binder = new DataBinder(model);
		binder.setValidator(new SalesSituationValidation());
		binder.validate();
		BindingResult results = binder.getBindingResult();
		Map<String, Object> result = new HashMap<>();
		if (results.hasErrors()) {
			results.getAllErrors().forEach((o) -> {
				FieldError error = (FieldError) o;
				errorsMessage[0] += error.getDefaultMessage();// ????????????????????????
			});
			result.put("errorsMessage", errorsMessage[0]);// ????????????????????????
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
		logger.info("updateSalesSituation" + "????????????");
		return result;
	}

	/**
	 * ??????????????????
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/updateEmployeeSiteInfo", method = RequestMethod.POST)
	@ResponseBody
	public int updateEmployeeSiteInfo(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("updateSalesSituation:" + "????????????");
		int index = 0;
		int index1 = 0;
		try {
			index = salesSituationService.updateEmployeeSiteInfo(model);
			index1 = salesSituationService.updateSalesSituation(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "????????????");
		return index + index1;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param emp
	 * @return List
	 * @throws Exception
	 */
	@RequestMapping(value = "/getPersonalSalesInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getPersonalSalesInfo(@RequestBody SalesSituationModel model) throws Exception {

		logger.info("getPersonalSalesInfo:" + "????????????");
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
				/* 2020/12/09 STRAT ?????? */
				// ?????????????????????????????????????????????????????????202009->2020/09???
				if (salesSituationList != null && salesSituationList.size() != 0
						&& !StringUtils.isNullOrEmpty(salesSituationList.get(0).getTheMonthOfStartWork())) {
					salesSituationList.get(0)
							.setTheMonthOfStartWork(DateFormat(salesSituationList.get(0).getTheMonthOfStartWork()));
				} else {
					// ??????????????????null??????

				}
				/* 2020/12/09 END ?????? */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (salesSituationList.size() > 0) {
			ArrayList<String> resumeInfoTemp = new ArrayList<String>();

			if (!(salesSituationList.get(0).getResumeName1() == null
					|| salesSituationList.get(0).getResumeName1().equals(""))) {
				resumeInfoTemp.add(salesSituationList.get(0).getResumeName1());
			}
			if (!(salesSituationList.get(0).getResumeName2() == null
					|| salesSituationList.get(0).getResumeName2().equals(""))) {
				resumeInfoTemp.add(salesSituationList.get(0).getResumeName2());
			}
			salesSituationList.get(0).setResumeInfoList(resumeInfoTemp);

			if (salesSituationList.get(0).getYearsOfExperience() != null
					&& salesSituationList.get(0).getYearsOfExperience().length() >= 4) {
				Calendar date = Calendar.getInstance();
				String year = String.valueOf(date.get(Calendar.YEAR));
				int tempYear = Integer.parseInt(year)
						- Integer.parseInt(salesSituationList.get(0).getYearsOfExperience().substring(0, 4)) + 1;
				if (tempYear < 0)
					tempYear = 0;
				salesSituationList.get(0).setYearsOfExperience(String.valueOf(tempYear));
			}

			if (salesSituationList.get(0).getAge() != null && salesSituationList.get(0).getAge().equals("")) {
				if (salesSituationList.get(0).getBirthday() != null
						&& !salesSituationList.get(0).getBirthday().equals(""))
					salesSituationList.get(0).setAge(String.valueOf(getAge(salesSituationList.get(0).getBirthday())));
			}
		}

		salesSituationList.get(0)
				.setSalesProgressCode(salesSituationList.get(salesSituationList.size() - 1).getSalesProgressCode());

		if (salesSituationList.get(salesSituationList.size() - 1).getRemark() == null
				|| salesSituationList.get(salesSituationList.size() - 1).getRemark().equals(" ")) {
			salesSituationList.get(0)
					.setRemark((salesSituationList.get(salesSituationList.size() - 1).getRemark1() == null ? ""
							: salesSituationList.get(salesSituationList.size() - 1).getRemark1())
							+ (salesSituationList.get(salesSituationList.size() - 1).getRemark2() == null ? ""
									: salesSituationList.get(salesSituationList.size() - 1).getRemark2()));
		} else {
			salesSituationList.get(0).setRemark(salesSituationList.get(salesSituationList.size() - 1).getRemark());
		}

		logger.info("updateSalesSituation" + "????????????");
		return salesSituationList;
	}

	@RequestMapping(value = "/getInterviewLists", method = RequestMethod.POST)
	@ResponseBody
	public List<SalesSituationModel> getInterviewLists(@RequestBody List<String> employeeNoList) {
		List<SalesSituationModel> interviewLists = new ArrayList<SalesSituationModel>();
		List<SalesSituationModel> interviewListsTemp = salesSituationService.getInterviewLists(employeeNoList);

		// ?????????
		for (int i = 0; i < employeeNoList.size(); i++) {
			for (int j = 0; j < interviewListsTemp.size(); j++) {
				if (employeeNoList.get(i).equals(interviewListsTemp.get(j).getEmployeeNo())) {
					if (interviewListsTemp.get(j).getEmployeeNo().substring(0, 2).equals("SC")
							|| interviewListsTemp.get(j).getEmployeeNo().substring(0, 2).equals("SP")
							|| interviewListsTemp.get(j).getEmployeeNo().substring(0, 2).equals("BP")) {
						if (interviewListsTemp.get(j).getEmployeeNo().substring(0, 3).equals("BPR"))
							interviewListsTemp.get(j).setEmployeeName(interviewListsTemp.get(j).getEmployeeName() + "("
									+ interviewListsTemp.get(j).getEmployeeNo().substring(0, 3) + ")");
						else
							interviewListsTemp.get(j).setEmployeeName(interviewListsTemp.get(j).getEmployeeName() + "("
									+ interviewListsTemp.get(j).getEmployeeNo().substring(0, 2) + ")");
					}

					interviewLists.add(interviewListsTemp.get(j));
					break;
				}
			}
		}

		for (int i = 0; i < interviewLists.size(); i++) {
			if (interviewLists.get(i).getInterviewDate1() == null
					|| interviewLists.get(i).getInterviewDate1().equals("")) {
				interviewLists.get(i)
						.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode2());
				interviewLists.get(i).setInterviewDate1(interviewLists.get(i).getInterviewDate2());
				interviewLists.get(i).setStationCode1(interviewLists.get(i).getStationCode2());
				interviewLists.get(i).setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer2());
				interviewLists.get(i).setInterviewInfo1(interviewLists.get(i).getInterviewInfo2());
				interviewLists.get(i).setInterviewUrl1(interviewLists.get(i).getInterviewUrl2());

				interviewLists.get(i).setInterviewClassificationCode2(null);
				interviewLists.get(i).setInterviewDate2(null);
				interviewLists.get(i).setStationCode2(null);
				interviewLists.get(i).setInterviewCustomer2(null);
				interviewLists.get(i).setInterviewInfo2(null);
				interviewLists.get(i).setInterviewUrl2(null);
			}
		}

		for (int i = 0; i < interviewLists.size(); i++) {
			if (!(interviewLists.get(i).getInterviewDate1() == null
					|| interviewLists.get(i).getInterviewDate1().equals(""))
					&& !(interviewLists.get(i).getInterviewDate2() == null
							|| interviewLists.get(i).getInterviewDate2().equals(""))) {
				if (Long.parseLong(interviewLists.get(i).getInterviewDate1()) > Long
						.parseLong(interviewLists.get(i).getInterviewDate2())) {
					SalesSituationModel temp = new SalesSituationModel();
					temp.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode1());
					temp.setInterviewDate1(interviewLists.get(i).getInterviewDate1());
					temp.setStationCode1(interviewLists.get(i).getStationCode1());
					temp.setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer1());
					temp.setInterviewInfo1(interviewLists.get(i).getInterviewInfo1());
					temp.setInterviewUrl1(interviewLists.get(i).getInterviewUrl1());

					interviewLists.get(i)
							.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode2());
					interviewLists.get(i).setInterviewDate1(interviewLists.get(i).getInterviewDate2());
					interviewLists.get(i).setStationCode1(interviewLists.get(i).getStationCode2());
					interviewLists.get(i).setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer2());
					interviewLists.get(i).setInterviewInfo1(interviewLists.get(i).getInterviewInfo2());
					interviewLists.get(i).setInterviewUrl1(interviewLists.get(i).getInterviewUrl2());

					interviewLists.get(i).setInterviewClassificationCode2(temp.getInterviewClassificationCode1());
					interviewLists.get(i).setInterviewDate2(temp.getInterviewDate1());
					interviewLists.get(i).setStationCode2(temp.getStationCode1());
					interviewLists.get(i).setInterviewCustomer2(temp.getInterviewCustomer1());
					interviewLists.get(i).setInterviewInfo2(temp.getInterviewInfo1());
					interviewLists.get(i).setInterviewUrl2(temp.getInterviewUrl1());
				}
			}
		}

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		String now = dateFormat.format(date);
		for (int i = 0; i < interviewLists.size(); i++) {
			if (!(interviewLists.get(i).getInterviewDate1() == null
					|| interviewLists.get(i).getInterviewDate1().equals(""))) {
				if (Long.parseLong(now) > Long.parseLong(interviewLists.get(i).getInterviewDate1())) {

					interviewLists.get(i)
							.setInterviewClassificationCode1(interviewLists.get(i).getInterviewClassificationCode2());
					interviewLists.get(i).setInterviewDate1(interviewLists.get(i).getInterviewDate2());
					interviewLists.get(i).setStationCode1(interviewLists.get(i).getStationCode2());
					interviewLists.get(i).setInterviewCustomer1(interviewLists.get(i).getInterviewCustomer2());
					interviewLists.get(i).setInterviewInfo1(interviewLists.get(i).getInterviewInfo2());
					interviewLists.get(i).setInterviewUrl1(interviewLists.get(i).getInterviewUrl2());

					interviewLists.get(i).setInterviewClassificationCode2(null);
					interviewLists.get(i).setInterviewDate2(null);
					interviewLists.get(i).setStationCode2(null);
					interviewLists.get(i).setInterviewCustomer2(null);
					interviewLists.get(i).setInterviewInfo2(null);
					interviewLists.get(i).setInterviewUrl2(null);

					i--;
				}
			}
		}

		return interviewLists;
	}

	@RequestMapping(value = "/deleteInterviewLists", method = RequestMethod.POST)
	@ResponseBody
	public void deleteInterviewLists(@RequestBody SalesSituationModel model) {
		logger.info("deleteInterviewLists:" + "????????????");
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		String salesDate = getSalesDate(model.getSalesYearAndMonth()).trim().toString();
		model.setSalesYearAndMonth(salesDate);
		try {
			salesSituationService.updateInterviewLists(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("deleteInterviewLists" + "????????????");
	}

	@RequestMapping(value = "/updateInterviewLists", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateInterviewLists(@RequestBody SalesSituationModel model) {
		Map<String, Object> result = new HashMap<>();
		String errorsMessage = "";
		if (!model.getInterviewClassificationCode1().equals("")) {
			if (model.getInterviewDate1() == null || model.getInterviewDate1().equals("")) {
				errorsMessage += "?????? ";
			}
			if (model.getInterviewCustomer1() == null || model.getInterviewCustomer1().equals("")) {
				errorsMessage += "????????? ";
			}
		}
		if (!model.getInterviewClassificationCode2().equals("")) {
			if (model.getInterviewDate2() == null || model.getInterviewDate2().equals("")) {
				errorsMessage += "?????? ";
			}
			if (model.getInterviewCustomer2() == null || model.getInterviewCustomer2().equals("")) {
				errorsMessage += "????????? ";
			}
		}

		if (!errorsMessage.equals("")) {
			errorsMessage += "??????????????????????????????";
			result.put("errorsMessage", errorsMessage);
			return result;
		}
		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		String salesDate = getSalesDate(model.getSalesYearAndMonth()).trim().toString();
		model.setSalesYearAndMonth(salesDate);
		logger.info("updateInterviewLists:" + "????????????");
		try {
			salesSituationService.updateInterviewLists(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateInterviewLists" + "????????????");
		return result;
	}

	/**
	 * ??????????????????
	 * 
	 * @param emp
	 * @return List
	 */

	@RequestMapping(value = "/updateEmployeeAddressInfo", method = RequestMethod.POST)
	@ResponseBody
	public int updateEmployeeAddressInfo(@RequestBody SalesSituationModel model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		logger.info("getPersonalSalesInfo:" + "????????????");
		int index = 0;
		try {
			index = salesSituationService.updateEmployeeAddressInfo(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "????????????");
		return index;
	}

	/**
	 * ??????????????????????????????????????????
	 **/
	@RequestMapping(value = "/updateSalesSentence", method = RequestMethod.POST)
	@ResponseBody
	public int updateSalesSentence(@RequestBody SalesContent model) {

		model.setUpdateUser(getSession().getAttribute("employeeName").toString());
		if (model.getJapaneaseConversationLevel() != null && model.getJapaneaseConversationLevel().equals("")) {
			model.setJapaneaseConversationLevel(null);
		}
		if (model.getEnglishConversationLevel() != null && model.getEnglishConversationLevel().equals("")) {
			model.setEnglishConversationLevel(null);
		}
		logger.info("getPersonalSalesInfo:" + "????????????");
		int index = 0;
		model.setBeginMonth(model.getTempDate());
		try {
			index = salesSituationService.updateSalesSentence(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("updateSalesSituation" + "????????????");
		return index;
	}

	/**
	 * ?????????????????????????????????
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

		logger.info("changeDataStatus:" + "??????????????????");
		String errorsMessage = "";
		if (model.getSalesProgressCode() != null
				&& (model.getSalesProgressCode().equals("1") || model.getSalesProgressCode().equals("0"))) {
			if (model.getCustomer() == null || model.getCustomer().equals("")) {
				errorsMessage += "???????????? ";
			}
			if (model.getPrice() == null || model.getPrice().equals("")) {
				errorsMessage += "????????????  ";
			}

			if (!errorsMessage.equals("")) {
				errorsMessage += "??????????????????????????????";
				result.put("errorsMessage", errorsMessage);
				return result;
			} else {
				if (model.getSalesProgressCode().equals("1")) {
					String nextAdmission = salesSituationService.getEmpNextAdmission(model.getEmployeeNo());
					if (nextAdmission != null && nextAdmission.equals("0")) {
						errorsMessage += "?????????????????????????????????????????????????????????????????????????????????????????????";
						result.put("errorsMessage", errorsMessage);
						return result;
					} else {
						salesSituationService.insertEmpNextAdmission(model);
					}
				} else if (model.getSalesProgressCode().equals("0")) {
					salesSituationService.updateEmpNextAdmission(model);
				}
			}
		}

		if (model.getSalesProgressCode() != null && (model.getSalesProgressCode().equals("2"))) {
			if (model.getCustomer() == null || model.getCustomer().equals("")) {
				errorsMessage += "????????????????????? ";
			}
			if (!errorsMessage.equals("")) {
				errorsMessage += "??????????????????????????????";
				result.put("errorsMessage", errorsMessage);
				return result;
			}
		}

		logger.info("changeDataStatus:" + "????????????");
		List<SalesSituationModel> salesSituationList = new ArrayList<SalesSituationModel>();
		int updateCount = 0;
		try {
			// ????????????????????????
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String curDate = sdf.format(date);

			// ????????????????????????
			String salesDate = getSalesDate(model.getAdmissionEndDate());
			/*
			 * if (DECEMBER.equals(model.getAdmissionEndDate().substring(4, 6))) { salesDate
			 * = String.valueOf(Integer.valueOf(model.getAdmissionEndDate().substring(0, 4))
			 * + 1) + JANUARY; } else { salesDate =
			 * String.valueOf(Integer.valueOf(model.getAdmissionEndDate().substring(0, 6)) +
			 * 1); }
			 */

			model.setSalesYearAndMonth(salesDate);

			if (salesSituationService.checkEmpNoAndYM(model).size() == 0) {
				salesSituationService.insertDataStatus(model);
			}

			// ????????????T010SalesSituation?????????????????????
			updateCount = salesSituationService.updateDataStatus(model);

			// ????????????T006EmployeeSiteInfo?????????????????????
			updateCount = salesSituationService.updateEMPInfo(model);

			// ????????????T011BpInfoSupplement?????????????????????
			if (model.getEmployeeNo().substring(0, 2).equals("BP")) {
				updateCount = salesSituationService.updateBPEMPInfo(model);
			}

			if (model.getEmployeeNo().substring(0, 3).equals("BPR")
					&& (model.getSalesProgressCode().equals("1") || model.getSalesProgressCode().equals("4"))) {
				Map<String, String> sendMap = new HashMap<String, String>();
				String newBpNo = utilsService.getNoBP(sendMap);
				if (newBpNo != null) {
					newBpNo = "BP" + String.format("%0" + 3 + "d", Integer.parseInt(newBpNo) + 1);
				} else {
					newBpNo = "BP001";
				}
				model.setEmployeeName(newBpNo);
				salesSituationService.updateBPR(model);
			}

			// ????????????????????????????????????
			/*
			 * salesSituationList = salesSituationService
			 * .getSalesSituationModel(model.getAdmissionEndDate().substring(0, 6), curDate,
			 * salesDate);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("changeDataStatus" + "????????????");
		result.put("result", salesSituationList);
		return result;
	}

	/**
	 * ??????????????????
	 * 
	 * @return Map
	 * @throws ParseException
	 * @throws Exception
	 */
	@RequestMapping(value = "/checkDirectory", method = RequestMethod.POST)
	@ResponseBody
	public boolean checkDirectory(@RequestBody SalesSituationModel model) throws ParseException, Exception {

		String mkDirectoryPath = "c:\\file\\??????????????????\\" + model.getSalesYearAndMonth();
		File folder = new File(mkDirectoryPath);
		if (!folder.exists() && !folder.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ??????????????????
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
			String mkDirectoryPath = "c:\\file\\??????????????????\\" + model.getSalesYearAndMonth() + "\\" + employeeNoList.get(i);
			if (mkDirectory(mkDirectoryPath)) {
				// System.out.println(mkDirectoryPath + "????????????");
				if (resumeInfo1List.get(i) != null)
					fileChannelCopy(resumeInfo1List.get(i), mkDirectoryPath);
				if (resumeInfo2List.get(i) != null)
					fileChannelCopy(resumeInfo2List.get(i), mkDirectoryPath);
			} else {
				// System.out.println(mkDirectoryPath + "?????????????????????????????????????????????");
				deleteFile(new File(mkDirectoryPath));
				if (resumeInfo1List.get(i) != null)
					fileChannelCopy(resumeInfo1List.get(i), mkDirectoryPath);
				if (resumeInfo2List.get(i) != null)
					fileChannelCopy(resumeInfo2List.get(i), mkDirectoryPath);
			}
		}
		String dir = "c:\\file\\??????????????????\\" + model.getSalesYearAndMonth();
		creatTxtFile(dir, "????????????", model.getText());
		mkDirectory("c:\\file\\salesFolder\\");
		String rar = "c:\\file\\salesFolder\\" + model.getSalesYearAndMonth() + ".rar";
		zip(dir, rar, true);
		// cmd???????????????????????????
		// Runtime.getRuntime().exec("cmd /c start explorer c:\\file\\??????????????????\\" +
		// model.getSalesYearAndMonth());

		S3Model s3Model = new S3Model();
		s3Model.setFilePath("c:/file/salesFolder/" + model.getSalesYearAndMonth() + ".rar");
		s3Model.setFileKey("??????????????????/" + model.getSalesYearAndMonth() + ".rar");
		s3Controller.uploadFile(s3Model);

		return result;
	}

	private static void deleteFile(File file) {
		if (file.isFile()) {// ???????????????????????????????????????
			file.delete();
		} else {// ??????????????????????????????
			String[] childFilePath = file.list();// ??????????????????????????????????????????
			for (String path : childFilePath) {
				File childFile = new File(file.getAbsoluteFile() + "/" + path);
				deleteFile(childFile);// ?????????????????????????????????
			}
		}
	}

	/**
	 * ??????
	 *
	 * @param dir            ??????????????????
	 * @param zipFile        ????????????????????????
	 * @param includeBaseDir ???????????????????????????
	 * @throws Exception
	 */
	public static void zip(String dir, String zipFile, boolean includeBaseDir) throws Exception {
		if (zipFile.startsWith(dir)) {
			throw new RuntimeException("?????????????????????????????????????????????");
		}
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
			File fileDir = new File(dir);
			String baseDir = "";
			if (includeBaseDir) {
				baseDir = fileDir.getName();
			}
			compress(out, fileDir, baseDir);
		}
	}

	public static void compress(ZipOutputStream out, File sourceFile, String base) throws Exception {

		if (sourceFile.isDirectory()) {
			base = base.length() == 0 ? "" : base + File.separator;
			File[] files = sourceFile.listFiles();
			if (ArrayUtils.isEmpty(files)) {
				// todo ???????????????
				// out.putNextEntry(new ZipEntry(base));
				return;
			}
			for (File file : files) {
				compress(out, file, base + file.getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			try (FileInputStream in = new FileInputStream(sourceFile)) {
				IOUtils.copy(in, out);
			} catch (Exception e) {
				throw new RuntimeException("????????????: " + e.getMessage());
			}
		}
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
				in = fi.getChannel();// ???????????????????????????
				out = fo.getChannel();// ???????????????????????????
				in.transferTo(0, in.size(), out);// ??????????????????????????????in???????????????????????????out??????
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
	 * ??????????????????
	 */
	private String DateFormat(String date) {
		String dateStr = date.substring(0, 4) + "/" + date.substring(4, 6);
		return dateStr;
	}

	/**
	 * ????????????txt??????
	 * 
	 * @throws IOException
	 */
	public static void creatTxtFile(String path, String name, String text) throws IOException {
		File file = new File(path + "\\" + name + ".txt");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(text.getBytes());
		fos.close();
	}

	public static int getMonthNum(String date1, String date2) throws java.text.ParseException {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		int month = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12;
		return result == 0 ? month : (month + result);
	}

	public static Integer getAge(String birthday) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date birth = df.parse(birthday);
		Calendar now = Calendar.getInstance();
		Calendar born = Calendar.getInstance();

		now.setTime(new Date());
		born.setTime(birth);

		if (born.after(now)) {
			return 0;
		}

		int age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
		if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
			age -= 1;
		}
		return age;
	}
}

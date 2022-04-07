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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import jp.co.lyc.cms.common.BaseController;
import jp.co.lyc.cms.model.DutyManagementModel;
import jp.co.lyc.cms.model.S3Model;
import jp.co.lyc.cms.model.WorkRepotModel;
import jp.co.lyc.cms.service.DutyManagementService;
import jp.co.lyc.cms.service.WorkRepotService;

@Controller
@RequestMapping(value = "/workRepot")
public class WorkRepotController extends BaseController { 

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	WorkRepotService workRepotService;
	@Autowired
	S3Controller s3Controller;
	@Autowired
	DutyManagementService dutyManagementService;
	
	/**
	 * 勤務時間入力有り無し判断
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectWorkTime", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> selectWorkTime(@RequestBody WorkRepotModel workRepotModel) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(workRepotModel.getEmployeeNo() == null) {
			workRepotModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		}
		logger.info("WorkRepotController.selectWorkTime:" + "検索開始");
		String count = workRepotService.selectWorkTime(workRepotModel);
		if(Integer.parseInt(count) > 0)
			result.put("nowMonth", true);
		else
			result.put("nowMonth", false);

		String lastMonth = workRepotModel.getAttendanceYearAndMonth();
		if(Integer.parseInt(lastMonth.substring(4, 6)) - 1 == 0) {
			lastMonth = (Integer.parseInt(lastMonth.substring(0, 4)) - 1) + "12";
		}else {
			int month = Integer.parseInt(lastMonth.substring(4, 6)) - 1;
			if(month < 10) 
				lastMonth = lastMonth.substring(0, 4) + "0" + String.valueOf(month);
			else
				lastMonth = lastMonth.substring(0, 4) + String.valueOf(month);
		}
		workRepotModel.setAttendanceYearAndMonth(lastMonth);
		count = workRepotService.selectWorkTime(workRepotModel);
		if(Integer.parseInt(count) > 0)
			result.put("lastMonth", true);
		else
			result.put("lastMonth", false);
		logger.info("WorkRepotController.selectWorkTime:" + "検索終了");

		return result;
	}
	
	/**
	 * 登録ボタン
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectWorkRepot", method = RequestMethod.POST)
	@ResponseBody
	public List<WorkRepotModel> selectWorkRepot(@RequestBody WorkRepotModel workRepotModel) {
		if(workRepotModel.getEmployeeNo() == null) {
			workRepotModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
			workRepotModel.setEmployeeName(getSession().getAttribute("employeeName").toString()); 
		}
		workRepotModel.setUpdateUser(getSession().getAttribute("employeeName").toString()); 
		logger.info("WorkRepotController.selectCheckWorkRepot:" + "検索開始");
		workRepotService.selectCheckWorkRepot(workRepotModel);
		logger.info("WorkRepotController.selectCheckWorkRepot:" + "検索終了");
		logger.info("WorkRepotController.selectWorkRepot:" + "検索開始");
		List<WorkRepotModel> checkMod = workRepotService.selectWorkRepot(workRepotModel);
		if(checkMod.size() == 1){
			WorkRepotModel tempModel = new WorkRepotModel();
			tempModel.setAttendanceYearAndMonth(String.valueOf(Integer.parseInt(checkMod.get(0).getAttendanceYearAndMonth()) - 1));
			checkMod.add(tempModel);
		}
		for(int i = 0;i < checkMod.size();i++) {
			checkMod.get(i).setId(i);
		}
		logger.info("WorkRepotController.selectWorkRepot:" + "検索終了");
		for(int i = 0;i < checkMod.size();i++) {
			if(checkMod.get(i).getSumWorkTime()== null ||checkMod.get(i).getSumWorkTime().equals("")) {
				HashMap<String, String> dutyManagementModel = new HashMap<String, String>();
				dutyManagementModel.put("yearAndMonth", checkMod.get(i).getAttendanceYearAndMonth());
				List<DutyManagementModel> workTimeList = dutyManagementService.selectWorkTime(dutyManagementModel);
				for(int j = 0;j < workTimeList.size();j++) {
					if(workTimeList.get(j).getEmployeeNo().equals(workRepotModel.getEmployeeNo())){
						checkMod.get(i).setSumWorkTime(workTimeList.get(j).getWorkTime().replace(".0", ""));
						checkMod.get(i).setUpdateTime(workTimeList.get(j).getUpdateTime());
						checkMod.get(i).setUpdateUser(workTimeList.get(j).getUpdateUser());
						break;
					}
				}
			}
		}

		int sumMonth = getMonths(checkMod.get(0).getAttendanceYearAndMonth(), checkMod.get(checkMod.size() - 1).getAttendanceYearAndMonth());
		if(checkMod.size() != sumMonth) {
			List<WorkRepotModel> returnMod = new ArrayList<WorkRepotModel>();
			String yearAndMonth = checkMod.get(checkMod.size() - 1).getAttendanceYearAndMonth();
			for(int i = 0; i < sumMonth; i++) {
				int year = Integer.parseInt(yearAndMonth.substring(0,4));
				int month = Integer.parseInt(yearAndMonth.substring(4,6)) + i;
				String attendanceYearAndMonth = (month > 12 ? String.valueOf(year + 1) + (month - 12 < 10 ? ("0" + String.valueOf(month - 12)) : String.valueOf(month - 12)) : String.valueOf(year) + (month < 10 ? ("0" + String.valueOf(month)) : String.valueOf(month)));
				WorkRepotModel tempModel = new WorkRepotModel();
				tempModel.setAttendanceYearAndMonth(attendanceYearAndMonth);
				boolean flag = false;
				for(int j = 0;j < checkMod.size(); j++) {
					if(checkMod.get(j).getAttendanceYearAndMonth().equals(attendanceYearAndMonth)) {
						returnMod.add(checkMod.get(j));
						flag = true;
						break;
					}
				}
				if(!flag) {
					returnMod.add(tempModel);
				}
			}
			checkMod = new ArrayList<WorkRepotModel>();
			for(int i = 0; i < returnMod.size(); i++) {
				checkMod.add(returnMod.get(returnMod.size()- i - 1));
			}
		}
		return checkMod;
	}
	
	public int getMonths(String bigDate,String smallDate) {
		int bigYear = Integer.parseInt(bigDate.substring(0,4));
		int smallYear = Integer.parseInt(smallDate.substring(0,4));
		int bigMonth = Integer.parseInt(bigDate.substring(4,6));
		int smallMonth = Integer.parseInt(smallDate.substring(4,6));
		int month = 0;
		if(bigYear > smallYear) {
			month = 12 + (bigMonth - smallMonth) + 1;
		}else if(bigYear == smallYear){
			month = (bigMonth - smallMonth) + 1;
		}
		
		return month;
	}

	/**
	 * アップデート
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateworkRepot", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateWorkRepotModel(@RequestBody WorkRepotModel emp){
		if(emp.getEmployeeNo() == null) {
			emp.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
			emp.setEmployeeName(getSession().getAttribute("employeeName").toString()); 	
		}
		emp.setUpdateUser(getSession().getAttribute("employeeName").toString()); 
		logger.info("DutyManagementController.updateworkRepot:" + "アップデート開始");
		boolean result = false;	
		result  = workRepotService.updateWorkRepot(emp);
		logger.info("DutyManagementController.updateworkRepot:" + "アップデート終了");
		return result;	
	}
	
	/**
	 * ファイルをクリア
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/clearworkRepot", method = RequestMethod.POST)
	@ResponseBody
	public boolean clearWorkRepotModel(@RequestBody WorkRepotModel emp){
		if(emp.getEmployeeNo() == null) {
			emp.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		}
		logger.info("DutyManagementController.updateworkRepot:" + "クリア開始");
		boolean result = false;	
		result  = workRepotService.clearworkRepot(emp);
		logger.info("DutyManagementController.updateworkRepot:" + "クリア終了");
		return result;	
	}
	/**
	 * 作業報告書アップロード
	 * 
	 * @param
	 * @return boolean
---	 */
	@RequestMapping(value = "/updateWorkRepotFile", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateWorkRepotFile(@RequestParam(value = "emp", required = false) String JSONEmp,
			@RequestParam(value = "workRepotFile", required = false) MultipartFile workRepotFile) throws Exception {
		logger.info("WorkRepotController.insertWorkRepot:" + "追加開始");
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		WorkRepotModel workRepotModel = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<WorkRepotModel>() {
		});
		if(workRepotModel.getEmployeeNo() == null) {
			workRepotModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
			workRepotModel.setEmployeeName(getSession().getAttribute("employeeName").toString()); 	
		}
		workRepotModel.setUpdateUser(getSession().getAttribute("employeeName").toString()); 	
		String getFilename;
		try {
			getFilename=upload(workRepotModel,workRepotFile);
		} catch (Exception e) {
			return false;
		}
		try {
			S3Model s3Model = new S3Model();
			String filePath = getFilename.replaceAll("\\\\", "/");
			String fileKey = filePath.split("file/")[1];
			s3Model.setFileKey(fileKey);
			s3Model.setFilePath(filePath);
			s3Controller.uploadFile(s3Model);
		} catch (Exception e) {
			return false;
		}
		workRepotModel.setWorkingTimeReport(getFilename);
		boolean result  = workRepotService.updateWorkRepotFile(workRepotModel);
		logger.info("WorkRepotController.insertWorkRepot:" + "追加結束");
		return result;
	}
	
	public final static String UPLOAD_PATH_PREFIX = "C:"+File.separator+"file"+File.separator;
	public String upload(WorkRepotModel workRepotModel,MultipartFile workRepotFile) {
		if (workRepotFile== null) {
			return "";
		}
		String realPath = new String(UPLOAD_PATH_PREFIX + "作業報告書フォルダ"+ File.separator+workRepotModel.getAttendanceYearAndMonth().substring(0,4) + File.separator+workRepotModel.getAttendanceYearAndMonth().substring(4));
		File file = new File(realPath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		String fileName =workRepotFile.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		String newName = workRepotModel.getEmployeeNo() + "_"+workRepotModel.getAttendanceYearAndMonth()+workRepotModel.getEmployeeName()+ "_作業報告書"+ "." + suffix;
		try {
			File newFile = new File(file.getAbsolutePath() + File.separator + newName);
			workRepotFile.transferTo(newFile);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return realPath+"/"+newName;
	}
}

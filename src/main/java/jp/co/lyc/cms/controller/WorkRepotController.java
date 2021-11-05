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
import jp.co.lyc.cms.model.WorkRepotModel;
import jp.co.lyc.cms.service.WorkRepotService;

@Controller
@RequestMapping(value = "/workRepot")
public class WorkRepotController extends BaseController { 

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	WorkRepotService workRepotService;
	
	/**
	 * 勤務時間入力有り無し判断
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectWorkTime", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> selectWorkTime(@RequestBody WorkRepotModel workRepotModel) {
		Map<String, Object> result = new HashMap<String, Object>();
		workRepotModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
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
	public List<WorkRepotModel> selectWorkRepot(WorkRepotModel workRepotModel) {
		workRepotModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		workRepotModel.setEmployeeName(getSession().getAttribute("employeeName").toString()); 	
		logger.info("WorkRepotController.selectCheckWorkRepot:" + "検索開始");
		workRepotService.selectCheckWorkRepot(workRepotModel);
		logger.info("WorkRepotController.selectCheckWorkRepot:" + "検索終了");
		logger.info("WorkRepotController.selectWorkRepot:" + "検索開始");
		List<WorkRepotModel> checkMod = workRepotService.selectWorkRepot(workRepotModel);
		for(int i = 0;i < checkMod.size();i++) {
			checkMod.get(i).setId(i);
		}
		logger.info("WorkRepotController.selectWorkRepot:" + "検索終了");
		return checkMod;
	}

	/**
	 * アップデート
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateworkRepot", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateWorkRepotModel(@RequestBody WorkRepotModel emp){
		emp.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		emp.setEmployeeName(getSession().getAttribute("employeeName").toString()); 	
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
		emp.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
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
		workRepotModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		workRepotModel.setEmployeeName(getSession().getAttribute("employeeName").toString()); 
		String getFilename;
		try {
			getFilename=upload(workRepotModel,workRepotFile);
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

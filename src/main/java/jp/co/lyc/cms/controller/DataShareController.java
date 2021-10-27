package jp.co.lyc.cms.controller;

import java.io.File;
import java.util.List;
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
import jp.co.lyc.cms.model.DataShareModel;
import jp.co.lyc.cms.model.WorkRepotModel;
import jp.co.lyc.cms.service.DataShareService;

@Controller
@RequestMapping(value = "/dataShare")
public class DataShareController extends BaseController { 

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	DataShareService dataShareService;
	/**
	 * 検索
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectDataShareFile", method = RequestMethod.POST)
	@ResponseBody
	public List<DataShareModel> selectDataShareFile() {
		logger.info("WorkRepotController.selectWorkRepot:" + "検索開始");
		List<DataShareModel> checkMod = dataShareService.selectDataShareFile();
		for(int i = 0;i < checkMod.size();i++) {
			checkMod.get(i).setRowNo(String.valueOf(i + 1));
		}
		logger.info("WorkRepotController.selectWorkRepot:" + "検索終了");
		return checkMod;
	}
	
	/**
	 * 検索(共有のみ)
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectDataShareFileOnly", method = RequestMethod.POST)
	@ResponseBody
	public List<DataShareModel> selectDataShareFileOnly(@RequestParam(value = "emp", required = false) String JSONEmp) {
		logger.info("WorkRepotController.selectWorkRepot:" + "検索開始");
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		DataShareModel dataShareModel = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<DataShareModel>() {
		});
		dataShareModel.setShareUser(getSession().getAttribute("employeeName").toString()); 
		List<DataShareModel> checkMod = dataShareService.selectDataShareFileOnly(dataShareModel);
		logger.info("WorkRepotController.selectWorkRepot:" + "検索終了");
		return checkMod;
	}

	/**
	 * アップデート
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateDataShare", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateDataShare(@RequestBody DataShareModel dataShareModel){
		logger.info("DutyManagementController.updateworkRepot:" + "アップデート開始");
		boolean result = false;	
		result  = dataShareService.updateDataShare(dataShareModel);
		logger.info("DutyManagementController.updateworkRepot:" + "アップデート終了");
		return result;	
	}
	/**
	 * 資料アップロード
	 * 
	 * @param
	 * @return boolean
---	 */
	@RequestMapping(value = "/updateDataShareFile", method = RequestMethod.POST)
	@ResponseBody
	public String updateDataShareFile(@RequestParam(value = "emp", required = false) String JSONEmp,
			@RequestParam(value = "dataShareFile", required = false) MultipartFile dataShareFile,
			@RequestParam(value = "fileNo", required = false) String fileNo,
			@RequestParam(value = "shareStatus", required = false) String shareStatus) throws Exception {
		logger.info("WorkRepotController.insertWorkRepot:" + "追加開始");
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		DataShareModel dataShareModel = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<DataShareModel>() {
		});
		dataShareModel.setEmployeeNo(getSession().getAttribute("employeeNo").toString());
		dataShareModel.setEmployeeName(getSession().getAttribute("employeeName").toString()); 
		dataShareModel.setShareUser(getSession().getAttribute("employeeName").toString()); 
		String getFilename;
		try {
			getFilename=upload(dataShareFile);
		} catch (Exception e) {
			return null;
		}
		if(fileNo == null || fileNo.equals("")) {
			fileNo = dataShareService.getMaxFileNo().getFileNo();
		}
		dataShareModel.setFileNo(fileNo);
		dataShareModel.setShareStatus(shareStatus);
		dataShareModel.setFilePath(getFilename);
		dataShareService.updateDataShareFile(dataShareModel);

		logger.info("WorkRepotController.insertWorkRepot:" + "追加結束");
		return fileNo;
	}
	
	public final static String UPLOAD_PATH_PREFIX = "C:"+File.separator+"file"+File.separator;
	public String upload(MultipartFile workRepotFile) {
		if (workRepotFile== null) {
			return "";
		}
		String realPath = new String(UPLOAD_PATH_PREFIX + "資料共有");
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
	
	/**
	 * 削除
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/deleteDataShare", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteDataShare(@RequestBody DataShareModel dataShareModel){
		logger.info("DutyManagementController.updateworkRepot:" + "アップデート開始");
		boolean result = false;	
		result  = dataShareService.deleteDataShare(dataShareModel.getFileNo());
		logger.info("DutyManagementController.updateworkRepot:" + "アップデート終了");
		return result;	
	}
}

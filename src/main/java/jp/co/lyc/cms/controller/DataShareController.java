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
import jp.co.lyc.cms.model.DataShareModel;
import jp.co.lyc.cms.model.S3Model;
import jp.co.lyc.cms.model.WorkRepotModel;
import jp.co.lyc.cms.service.DataShareService;

@Controller
@RequestMapping(value = "/dataShare")
public class DataShareController extends BaseController { 

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	DataShareService dataShareService;
	@Autowired
	S3Controller s3Controller;
	/**
	 * 検索
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectDataShareFile", method = RequestMethod.POST)
	@ResponseBody
	public List<DataShareModel> selectDataShareFile(@RequestBody DataShareModel dataShareModel) {
		logger.info("dataShare.selectDataShareFile:" + "検索開始");
		List<DataShareModel> checkMod = dataShareService.selectDataShareFile(dataShareModel);
		for(int i = 0;i < checkMod.size();i++) {
			String rowNo = (i + 1) < 10 ? ("0" + (i + 1)) : String.valueOf(i + 1);
			checkMod.get(i).setRowNo(rowNo);
		}
		logger.info("dataShare.selectDataShareFile:" + "検索終了");
		return checkMod;
	}
	
	/**
	 * 検索(共有のみ)
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/selectDataShareFileOnly", method = RequestMethod.POST)
	@ResponseBody
	public List<DataShareModel> selectDataShareFileOnly(@RequestParam(value = "emp", required = false) String JSONEmp,
														@RequestParam(value = "dataStatus", required = false) String dataStatus) {
		logger.info("dataShare.selectDataShareFileOnly:" + "検索開始");
		JSONObject jsonObject = JSON.parseObject(JSONEmp);
		DataShareModel dataShareModel = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<DataShareModel>() {
		});
		dataShareModel.setShareUser(getSession().getAttribute("employeeName").toString()); 
		dataShareModel.setDataStatus(dataStatus);
		List<DataShareModel> checkMod = dataShareService.selectDataShareFileOnly(dataShareModel);
		logger.info("dataShare.selectDataShareFileOnly:" + "検索終了");
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
		logger.info("dataShare.updateDataShare:" + "アップデート開始");
		boolean result = false;	
		result  = dataShareService.updateDataShare(dataShareModel);
		logger.info("dataShare.updateDataShare:" + "アップデート終了");
		return result;	
	}
	
	/**
	 * アップデート
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateDataSharesTo2", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateDataSharesTo2(@RequestBody ArrayList<String> fileNoList){
		logger.info("dataShare.updateDataSharesTo2:" + "アップデート開始");
		boolean result = false;	
		result  = dataShareService.updateDataSharesTo2(fileNoList);
		logger.info("dataShare.updateDataSharesTo2:" + "アップデート終了");
		return result;	
	}
	
	/**
	 * アップデート
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateDataSharesTo3", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateDataSharesTo3(@RequestBody ArrayList<String> fileNoList){
		logger.info("dataShare.updateDataSharesTo3:" + "アップデート開始");
		boolean result = false;	
		result  = dataShareService.updateDataSharesTo3(fileNoList);
		logger.info("dataShare.updateDataSharesTo3:" + "アップデート終了");
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
		logger.info("dataShare.updateDataShareFile:" + "追加開始");
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
		if(fileNo == null || fileNo.equals("")) {
			DataShareModel maxFileNo = dataShareService.getMaxFileNo();
			if(maxFileNo == null)
				fileNo = "1";
			else
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
		logger.info("dataShare.deleteDataShare:" + "削除開始");
		boolean result = false;	
		result  = dataShareService.deleteDataShare(dataShareModel.getFileNo());
		logger.info("dataShare.deleteDataShare:" + "削除終了");
		return result;	
	}
	
	/**
	 * 削除
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/deleteDataShares", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteDataShares(@RequestBody ArrayList<String> fileNoList){
		logger.info("dataShare.deleteDataShares:" + "削除開始");
		boolean result = false;	
		result  = dataShareService.deleteDataShares(fileNoList);
		logger.info("dataShare.deleteDataShares:" + "削除終了");
		return result;	
	}
	
	/**
	 * ファイル名更新
	 * @param topCustomerMod
	 * @return
	 */
	@RequestMapping(value = "/updateFileName", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateFileName(@RequestBody DataShareModel dataShareModel){
		logger.info("dataShare.updateFileName:" + "ファイル名更新開始");
		Map<String, Object> returnMap = new HashMap<String, Object>();
		boolean result = false;	
		
		String filePath = dataShareModel.getFilePath().substring(0,dataShareModel.getFilePath().lastIndexOf("/") + 1);
		String oldFileName = dataShareModel.getFilePath().substring(dataShareModel.getFilePath().lastIndexOf("/") + 1,dataShareModel.getFilePath().length());
		String newFileName = dataShareModel.getFileName();

		File oldFile = new File(filePath + oldFileName);
		File newFile = new File(filePath + newFileName);
		if(oldFile.exists() && oldFile.isFile()) {
			oldFile.renameTo(newFile);
			dataShareModel.setFilePath(filePath + newFileName);
			result = dataShareService.updateFileName(dataShareModel);
		}
		returnMap.put("flag", result);
		returnMap.put("newFilePath", filePath + newFileName);
		logger.info("dataShare.updateFileName:" + "ファイル名更新終了");
		return returnMap;	
	}
}

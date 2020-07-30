package jp.co.lyc.cms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import jp.co.lyc.cms.model.CustomerInfoModel;
import jp.co.lyc.cms.service.CustomerInfoSearchService;

@Controller
@CrossOrigin(origins = "http://127.0.0.1:3000")
@RequestMapping(value = "/customerInfoSearch")
public class CustomerInfoSearchController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CustomerInfoSearchService customerInfoSearchService;
	/**
	 * データの検索
	 * @param customerInfoMod
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	@ResponseBody
	public ArrayList<CustomerInfoModel> seach(@RequestBody CustomerInfoModel customerInfoMod) {
		logger.info("CustomerInfoController.onloadPage:" + "查询開始");
		ArrayList<CustomerInfoModel> resultList = new ArrayList<>();
		ArrayList<CustomerInfoModel> databeseList = new ArrayList<>();
		databeseList = SelectCustomerInfo(customerInfoMod);
		int rowNo = 1;
		for(int i = 0 ; i < databeseList.size() ; i++) {
			if(i != 0) {
				if(!databeseList.get(i).getCustomerNo().equals(databeseList.get(i-1).getCustomerNo())) {
					//如果客户番号不同，则全部数据写入结果数组
					databeseList.get(i).setRowNo(Integer.toString(rowNo));
					if(databeseList.get(i).getEmployeeName() != null) {
						ArrayList<String> employeeNameList = new ArrayList<>();
						employeeNameList.add(databeseList.get(i).getEmployeeName());
						databeseList.get(i).setEmployeeNameList(employeeNameList);
					}
					if(databeseList.get(i).getLocation() != null) {
						ArrayList<String> locationList = new ArrayList<>();
						locationList.add(databeseList.get(i).getLocation());
						databeseList.get(i).setLocationList(locationList);
					}
					if(databeseList.get(i).getSiteManager() != null) {
						ArrayList<String> siteManagerList = new ArrayList<>();
						siteManagerList.add(databeseList.get(i).getSiteManager());
						databeseList.get(i).setSiteManagerList(siteManagerList);
					}
					if(databeseList.get(i).getUnitPrice() != null) {
						ArrayList<String> unitPriceList = new ArrayList<>();
						unitPriceList.add(databeseList.get(i).getUnitPrice());
						databeseList.get(i).setUnitPriceList(unitPriceList);
					}
					resultList.add(databeseList.get(i));
					rowNo ++;
				}else if(databeseList.get(i).getCustomerNo().equals(databeseList.get(i-1).getCustomerNo()) && 
						databeseList.get(i).getCustomerName().equals(databeseList.get(i-1).getCustomerName())) {
					//与上一条客户番号相同的数据进行去头操作
					CustomerInfoModel dataChange = resultList.get(rowNo-2);
					ArrayList<String> employeeNameList = (dataChange.getEmployeeNameList() == null ? new ArrayList<>() :
						dataChange.getEmployeeNameList());
					employeeNameList.add(databeseList.get(i).getEmployeeName());
					dataChange.setEmployeeNameList(employeeNameList);
					
					ArrayList<String> locationList = (dataChange.getLocationList() == null ? new ArrayList<>() :
						dataChange.getLocationList());
					locationList.add(databeseList.get(i).getLocation());
					dataChange.setLocationList(locationList);
					
					ArrayList<String> siteManagerList = (dataChange.getSiteManagerList() == null ? new ArrayList<>() :
						dataChange.getSiteManagerList());
					siteManagerList.add(databeseList.get(i).getSiteManager());
					dataChange.setSiteManagerList(siteManagerList);
					
					ArrayList<String> unitPriceList = (dataChange.getUnitPriceList() == null ? new ArrayList<>() :
						dataChange.getUnitPriceList());
					unitPriceList.add(databeseList.get(i).getUnitPrice());
					dataChange.setUnitPriceList(unitPriceList);
					
					resultList.set((rowNo - 2), dataChange);
				}
			}else {
				databeseList.get(i).setRowNo(Integer.toString(rowNo));
				if(databeseList.get(i).getEmployeeName() != null) {
					ArrayList<String> employeeNameList = new ArrayList<>();
					employeeNameList.add(databeseList.get(i).getEmployeeName());
					databeseList.get(i).setEmployeeNameList(employeeNameList);
				}
				if(databeseList.get(i).getLocation() != null) {
					ArrayList<String> locationList = new ArrayList<>();
					locationList.add(databeseList.get(i).getLocation());
					databeseList.get(i).setLocationList(locationList);
				}
				if(databeseList.get(i).getSiteManager() != null) {
					ArrayList<String> siteManagerList = new ArrayList<>();
					siteManagerList.add(databeseList.get(i).getSiteManager());
					databeseList.get(i).setSiteManagerList(siteManagerList);
				}
				if(databeseList.get(i).getUnitPrice() != null) {
					ArrayList<String> unitPriceList = new ArrayList<>();
					unitPriceList.add(databeseList.get(i).getUnitPrice());
					databeseList.get(i).setUnitPriceList(unitPriceList);
				}
				resultList.add(databeseList.get(i));
				rowNo ++;
			}
		}
		return resultList;
	}
	/**
	 * 削除ボタン
	 * @param customerNo
	 * @return
	 */
	@RequestMapping(value = "/delect", method = RequestMethod.POST)
	@ResponseBody
	public boolean delect( @RequestBody CustomerInfoModel customerInfoMod) {
		return customerInfoSearchService.delect(customerInfoMod.getCustomerNo());
	}
	/**
	 * データの検索
	 * @param customerInfoMod
	 * @return
	 */
	public ArrayList<CustomerInfoModel> SelectCustomerInfo(CustomerInfoModel customerInfoMod) {
		
		HashMap<String, String> sendMap = new HashMap<>();
		if(!isNullOrEmpty(customerInfoMod.getCustomerNo())) {
			sendMap.put("customerNo", customerInfoMod.getCustomerNo());
		}
		if(!isNullOrEmpty(customerInfoMod.getCustomerName())) {
			sendMap.put("customerName", customerInfoMod.getCustomerName());
		}
		if(!isNullOrEmpty(customerInfoMod.getHeadOffice())) {
			sendMap.put("headOffice", customerInfoMod.getHeadOffice());
		}
		if(!isNullOrEmpty(customerInfoMod.getCustomerRankingCode())) {
			sendMap.put("customerRankingCode", customerInfoMod.getCustomerRankingCode());
		}
		if(!isNullOrEmpty(customerInfoMod.getCompanyNatureCode())) {
			sendMap.put("companyNatureCode", customerInfoMod.getCompanyNatureCode());
		}
		if(!isNullOrEmpty(customerInfoMod.getTopCustomerName())) {
			sendMap.put("topCustomerName", customerInfoMod.getTopCustomerName());
		}
		return customerInfoSearchService.SelectCustomerInfo(sendMap);
	}
	/**
	 * nullと空の判断
	 * @param aString
	 * @return
	 */
	public boolean isNullOrEmpty(String aString) {
		boolean result = true;
		if (aString == null || aString.isEmpty()) {
			return result;
		} else {
			return result = false;
		}
	}
}

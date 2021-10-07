package jp.co.lyc.cms.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import jp.co.lyc.cms.model.EmployeeInformationModel;
import jp.co.lyc.cms.model.EmployeeModel;
import jp.co.lyc.cms.model.S3Model;
import jp.co.lyc.cms.model.WorkRepotModel;
import jp.co.lyc.cms.service.SalaryDetailSendService;

@Controller
@RequestMapping(value = "/SalaryDetailSend")
public class SalaryDetailSendController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	String errorsMessage = "";
	@Autowired
	SalaryDetailSendService salaryDetailSendService;

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
		String employeeNo = "";
		String descDir = UPLOAD_PATH_PREFIX + File.separator;
		if (pdfUpdate != null) {
			String filename = upload(pdfUpdate);
			employeeNo = filename.substring(filename.lastIndexOf("_") + 1);
			// boolean flag = decompressZip(filename, descDir);
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

	/**
	 * 解压文件
	 * 
	 * @param zipPath 要解压的目标文件
	 * @param descDir 指定解压目录
	 * @return 解压结果：成功，失败
	 */
	@SuppressWarnings("rawtypes")
	public boolean decompressZip(String zipPath, String descDir) {
		File zipFile = new File(zipPath);
		boolean flag = false;
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile, Charset.forName("gbk"));// 防止中文目录，乱码
			for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String zipEntryName = entry.getName();
				InputStream in = zip.getInputStream(entry);
				// 指定解压后的文件夹+当前zip文件的名称
				String outPath = (descDir + zipEntryName).replace("/", File.separator);
				// 判断路径是否存在,不存在则创建文件路径
				File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
				if (!file.exists()) {
					file.mkdirs();
				}
				// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
				if (new File(outPath).isDirectory()) {
					continue;
				}
				// 保存文件路径信息（可利用md5.zip名称的唯一性，来判断是否已经解压）
				// System.err.println("当前zip解压之后的路径为：" + outPath);
				OutputStream out = new FileOutputStream(outPath);
				byte[] buf1 = new byte[2048];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
				in.close();
				out.close();
			}
			flag = true;
			// 必须关闭，要不然这个zip文件一直被占用着，要删删不掉，改名也不可以，移动也不行，整多了，系统还崩了。
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
}

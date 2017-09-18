package com.nms.ui.ptn.upgradeManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.table.TableModel;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.nms.db.bean.equipment.manager.Upgrade;
import com.nms.db.bean.equipment.manager.UpgradeManage;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.drive.service.impl.CoderUtils;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.upgradeManage.view.UpgradeVersionsDialog;

public class UpgradeManageUtil {
	/**
	 * zip里面的压缩文件不能有中文名称，并且该zip文件目录不能包含中文
	 * 
	 * @return
	 */
	public static ZipFile loadZip(List<ZipEntry> zipEntries, String path) {
		FileInputStream inputStream = null;
		ZipFile zipFile = null;
		try {
			System.out.println(path);
			inputStream = new FileInputStream(path);
			File file = new File(path);
			zipFile = new ZipFile(file);
			Enumeration emu = zipFile.getEntries();
			while (emu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) emu.nextElement();
				if (zipEntry.getName().contains("/")) {
					if (zipEntry.getName().lastIndexOf("/") != zipEntry.getName().length() - 1) {
						String zipName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1, zipEntry.getName().length() - 1);
						if (zipName.length() > 1) {
							zipEntries.add(zipEntry);
						}
					}
				} else {
					zipEntries.add(zipEntry);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UpgradeManageUtil.class);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					ExceptionManage.dispose(e, UpgradeManageUtil.class);
				}
			}

		}
		return zipFile;
	}

	/**
	 * 根据查询的软件摘要，过滤不需要升级的软件包,并下发摘要给设备
	 * @param zipEntries 升级Entry
	 * @param zipFile 升级文件
	 * @param siteInst 网元
	 * @param upgradeManages 摘要信息
	 * @param entriesNeed 需要升级的文件
	 * @param isNode 单站升级还是批量升级
	 * @param isForce 是否强制升级
	 * @return
	 */
	public static String getSummary(List<UpgradeManage> nowupgradeManages,List<ZipEntry> zipEntries, ZipFile zipFile, SiteInst siteInst, List<UpgradeManage> upgradeManages,List<ZipEntry> entriesNeed,int isNode,boolean isForce) {
		byte[] summary;
		InputStream inputStream = null;
		String str = "";
		try {

			for (ZipEntry zipEntry : zipEntries) {
				String zipName = "";
				if (zipEntry.getName().contains("/")) {
					if (zipEntry.getName().lastIndexOf("/") != zipEntry.getName().length() - 1) {
						zipName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1, zipEntry.getName().length() - 1);
					}
				} else {
					zipName = zipEntry.getName();
				}
				if ("summary".equals(zipName)) {
					inputStream = zipFile.getInputStream(zipEntry);
					byte[] readBytes = new byte[40 * 1024 - (40 * 1024 / 1450 + 1) * 12 - 15 - 16 - 21];
					int readLength = inputStream.read(readBytes);
					summary = new byte[readLength];// 需下发摘要
					System.arraycopy(readBytes, 0, summary, 0, summary.length);
					// 关闭相关对象
					str = filterZipEntry(nowupgradeManages,summary, upgradeManages, zipEntries, entriesNeed,isNode,isForce);// 过滤布需要升级的文件
					siteInst.setBs(summary);
					siteInst.setAllFileName(str);
					break;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UpgradeManageUtil.class);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					ExceptionManage.dispose(e, UpgradeManageUtil.class);
				} finally {
					inputStream = null;
				}
			}
		}
		return str;
	}

	/**
	 * 过滤不需要升级的文件
	 * 
	 * @param summary
	 * @throws Exception
	 */
	private static String filterZipEntry(List<UpgradeManage> nowupgradeManages,byte[] summary, List<UpgradeManage> upgradeManages, List<ZipEntry> zipEntries, List<ZipEntry> entriesNeed,int isNode,boolean isForce) {
		String[] summarys = CoderUtils.byteToStrings(summary);
		int count = Integer.parseInt(summarys[124]);
		summary[24] = CoderUtils.intToByte(isNode)[3];
		Map<Integer, UpgradeManage> manages = new HashMap<Integer, UpgradeManage>();
		Map<Integer, UpgradeManage> manageMap = new HashMap<Integer, UpgradeManage>();
		Map<Integer, byte[]> byteMap = new HashMap<Integer, byte[]>();
		List<ZipEntry> entriesRemove = new ArrayList<ZipEntry>();
		for (int i = 0; i < count; i++) {
			String[] strings = new String[256];
			System.arraycopy(summarys, 125 + 256 * i, strings, 0, strings.length);
			byte[] fiel = new byte[256];
			System.arraycopy(summary, 125 + 256 * i, fiel, 0, fiel.length);
			UpgradeManage upgradeManage = new UpgradeManage();
			upgradeManage.setSoftwareType(Integer.parseInt(strings[90]));
			upgradeManage.setCrc(Integer.parseInt(strings[100], 16) * 16777216 + Integer.parseInt(strings[101], 16) * 65536 + Integer.parseInt(strings[102], 16) * 256 + Integer.parseInt(strings[103], 16));
			StringBuffer stringBuffer = new StringBuffer();
			for(int j = 144;j<160;j++){
				stringBuffer.append((char) (Integer.parseInt(strings[j], 16)));
			}
			upgradeManage.setTime(stringBuffer.toString());
			if(upgradeManage.getSoftwareType() != 2){
				nowupgradeManages.add(upgradeManage);
			}
			manages.put(upgradeManage.getSoftwareType(), upgradeManage);
			byteMap.put(Integer.parseInt(strings[90]), fiel);
		} 
		for (UpgradeManage upgradeManage : upgradeManages) {
			manageMap.put(upgradeManage.getSoftwareType(), upgradeManage);
		}
		for (ZipEntry zipEntry : zipEntries) {
			entriesNeed.add(zipEntry);
			String zipName = "";
			if (zipEntry.getName().contains("/")) {
				if (zipEntry.getName().lastIndexOf("/") != zipEntry.getName().length() - 1) {
					zipName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1, zipEntry.getName().length() - 1);
				}
			} else {
				zipName = zipEntry.getName();
			}

			if (zipName.length() > 1) {
				if(!isForce){
					if ("data_fpga.bin".equals(zipName)) {
						UpgradeManage upgradeManage = manages.get(3);// 查询设备的摘要
						UpgradeManage upgradeManage2 = manageMap.get(3);// 升级包中带的摘要
						if (upgradeManage != null && upgradeManage2 != null && upgradeManage.getCrc() == upgradeManage2.getCrc() && upgradeManage.getTime().equals(upgradeManage2.getTime())) {
							entriesRemove.add(zipEntry);
							byteMap.remove(3);
						}
					} else if ("data_rom.bin".equals(zipName)) {
						UpgradeManage upgradeManage = manages.get(1);// 查询设备的摘要
						UpgradeManage upgradeManage2 = manageMap.get(1);// 升级包中带的摘要
						if (upgradeManage != null && upgradeManage2 != null && upgradeManage.getCrc() == upgradeManage2.getCrc() && upgradeManage.getTime().equals(upgradeManage2.getTime())) {
							entriesRemove.add(zipEntry);
							byteMap.remove(1);
						}
					} else if ("data_boot.bin".equals(zipName)) {
						UpgradeManage upgradeManage = manages.get(2);// 查询设备的摘要
						UpgradeManage upgradeManage2 = manageMap.get(2);// 升级包中带的摘要
						if (upgradeManage != null && upgradeManage2 != null && upgradeManage.getCrc() == upgradeManage2.getCrc() && upgradeManage.getTime().equals(upgradeManage2.getTime())) {
							entriesRemove.add(zipEntry);
							byteMap.remove(2);
						}
					} else if ("e1_xilinx.xsvf".equals(zipName)) {
						UpgradeManage upgradeManage = manages.get(4);// 查询设备的摘要
						UpgradeManage upgradeManage2 = manageMap.get(4);// 升级包中带的摘要
						if (upgradeManage != null && upgradeManage2 != null && upgradeManage.getCrc() == upgradeManage2.getCrc() && upgradeManage.getTime().equals(upgradeManage2.getTime())) {
							entriesRemove.add(zipEntry);
							byteMap.remove(4);
						}
					} 
				}
				if ("summary".equals(zipEntry.getName())) {
					entriesRemove.add(zipEntry);
				}
			}	
		}
		entriesNeed.removeAll(entriesRemove);
		summary[124] = (byte) entriesNeed.size();
		byte[] sendSummary = new byte[125 + 256 * byteMap.size()];
		System.arraycopy(summary, 0, sendSummary, 0, 125);
		int i = 0;
		for (Integer integer : byteMap.keySet()) {
			System.arraycopy(byteMap.get(integer), 0, sendSummary, 125 + i * 256, 256);
			i++;
		}
		summary = sendSummary;
		String str = "";
		for (ZipEntry zipEntry : entriesNeed) {
			str += zipEntry.getName() + ";";
		}
		return str;
	}
	
	/**
	 * 升级开始
	 * @param zipEntries
	 * @param textField
	 * @param siteInst
	 * @param type
	 * @param view
	 * @param zipFile
	 * @return
	 */
	public static String upgradeProcedure(List<ZipEntry> zipEntries,JTextField textField,SiteInst siteInst,int type,UpgradeVersionsDialog view,ZipFile zipFile,TableModel tableModel,int index){
		String reslut = "";
		try {
			boolean b = true;//持续查询摘要过程
			boolean hasSummary = false;//设备是否返回摘要
			int number = 0;//查询摘要次数，没有回应时，每次持续时间30秒
			boolean isContinue = true;//判断是否继续本次升级
			if(zipEntries.size() == 0){
				return ResourceUtil.srcStr(StringKeysLbl.FILEERROR);
			}
			for (int i = 0; i < zipEntries.size(); i++) {
				if(tableModel != null){
					tableModel.setValueAt(zipEntries.get(i).getName(), index, 3);
				}
				
				if(i>0){
					while(b){
						hasSummary = querySummary(type,siteInst);
						number++;
						if(hasSummary){//设备有摘要信息返回，开始升级
							break;
						}
						if(number>5){
							isContinue = false;
							return ResourceUtil.srcStr(StringKeysLbl.LBL_UPDATE_ING_SUMMAR_FAIL);
						}
					}
				}
				if(isContinue){
					siteInst.setFileName(zipEntries.get(i).getName());
					textField.setText(zipEntries.get(i).getName());
					Upgrade upgrade = new Upgrade();
					upgrade.setNeAddress(siteInst.getFieldID() * 256 + Integer.valueOf(siteInst.getSite_Hum_Id()));
					upgrade.setCardType(1);
					upgrade.setCardNumber(1);
					upgrade.setCardAddress(type);
					upgrade.setSiteId(siteInst.getSite_Inst_Id());
					siteInst.setSchedule(0);
					reslut = assignUpgrade(siteInst,upgrade,zipEntries.get(i),view,type,zipFile,tableModel,index);
					if(reslut.equals(ResourceUtil.srcStr(StringKeysLbl.LBL_ERROR_FILE))){
						return zipEntries.get(i).getName()+ResourceUtil.srcStr(StringKeysLbl.LBL_ERROR_FILE);
					}
					if(!ResultString.CONFIG_SUCCESS.equals(reslut)){
						return zipEntries.get(i).getName()+ResourceUtil.srcStr(StringKeysTip.TIP_UPGRADE_FAIL);
					}
					b = true;
					if(view != null){
						view.current = 0;
					}
					
				}else{
					return zipEntries.get(i).getName()+ResourceUtil.srcStr(StringKeysTip.TIP_UPGRADE_FAIL);
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, UpgradeManageUtil.class);
		} finally {
		}
		return reslut;
	}
	
	/**
	 * 升级文件下发设备
	 * @param upgrade
	 * @param zipEntry
	 * @param view
	 * @param type
	 * @param zipFile
	 * @return
	 * @throws Exception
	 */
	private static String assignUpgrade(SiteInst siteInst,Upgrade upgrade,ZipEntry zipEntry,UpgradeVersionsDialog view,int type,ZipFile zipFile,TableModel tableModel,int index) throws Exception {
		String result = "";
		try {
			AnalysisFileToByte analysisFileToByte = new AnalysisFileToByte();
			List<byte[]> bytesList = analysisFileToByte.readFile(zipEntry,zipFile);
			int filLength = 0;
			for (byte[] bytes : bytesList) {
				filLength = filLength + bytes.length;
			}
			String[] strings = CoderUtils.byteToStrings(bytesList.get(0));
			if(!isReasonable(strings,type)){
				return ResourceUtil.srcStr(StringKeysLbl.LBL_ERROR_FILE);
			}
			
			upgrade.setFileLength(filLength);
			upgrade.setDataList(bytesList);
			int a = 0;
			if(view != null){
				view.total = bytesList.size();
			}
			DispatchUtil dispatch = new DispatchUtil(RmiKeys.RMI_SOFTWAREUPDATE);
			for (int i = 1; i < upgrade.getDataList().size() + 1; i++) {// 按数据包的个数进行下发数据
				if(tableModel != null){
					tableModel.setValueAt(i*100/(upgrade.getDataList().size()), index, 4);
				}
				
				if(!ConstantUtil.isCancleRun){
					if(view != null){
						view.current = i;
					}
					if (i != upgrade.getDataList().size()) {
						/*每个数据包的数据内容 */
						byte[] dataEach = new byte[upgrade.getDataList().get(i - 1).length];
						System.arraycopy(upgrade.getDataList().get(i - 1), 0, dataEach, 0, dataEach.length);
						upgrade.setDataEach(dataEach);// 每个数据包的内容
						upgrade.setDataLong(dataEach.length);// 每个数据包的长度
						upgrade.setExcursion(a);// 偏移量
						a = a + dataEach.length;
					} else {
						/* 最后一个数据包的数据内容 */
						byte[] dataEach = new byte[upgrade.getDataList().get(i - 1).length];
						System.arraycopy(upgrade.getDataList().get(i - 1), 0, dataEach, 0, dataEach.length);
						upgrade.setDataEach(dataEach);// 每个数据包的内容
						upgrade.setExcursion(a);// 偏移量
						upgrade.setDataLong(dataEach.length);// 每个数据包的长度
					}
					result = dispatch.excuteUpdate(upgrade);
					System.out.println(siteInst.getCellDescribe()+"升级过程设备返回信息 ：     "+result);
					if (!ResultString.CONFIG_SUCCESS.equals(result)) {
						return ResultString.CONFIG_TIMEOUT;
					}
				}else{
					return ResultString.CONFIG_TIMEOUT;
				}
				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,UpgradeManageUtil.class);
			throw e;
		}
		return result;
	}
	
	/**
	 * 判断文件标志是否正确，文件类型是否合法
	 * @param strings
	 * @return
	 */
	private static boolean isReasonable(String[] strings,int type){
		boolean b = false;
		if(strings.length>100){
			if(type == 1){
				if(strings[0].equals("44") && strings[1].equals("41") && strings[2].equals("54") && strings[3].equals("41") &&//文件标志DATA
						(strings[90].equals("01") || strings[90].equals("02") || strings[90].equals("03") || strings[90].equals("04") || //文件类型1-6合法
								strings[90].equals("05") || strings[90].equals("06"))){
							b = true;
				}
			}else{
				if(strings[0].equals("44") && strings[1].equals("41") && strings[2].equals("54") && strings[3].equals("41")){
					b = true;
				}
			}
		}
		return b;
	}
	
	/**
	 * 升级过程中一直查询设备摘要
	 * @param type
	 * @return
	 */
	private static boolean querySummary(int type,SiteInst siteInst){
		List<UpgradeManage> upgradeManages = null;
		try {
			siteInst.setCardNumber(type+"");
			DispatchUtil siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			siteInst.setBs(null);
			upgradeManages = siteDispatch.softwareSummary(siteInst);
		} catch (Exception e) {
			ExceptionManage.dispose(e, UpgradeManageUtil.class);
		}
		if(upgradeManages != null){
			System.out.println("摘要数据大小:  "+upgradeManages.size());
			return true;
		}else{
			return false;
		}
	}

}

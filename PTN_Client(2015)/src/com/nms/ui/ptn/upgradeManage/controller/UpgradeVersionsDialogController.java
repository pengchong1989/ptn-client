﻿package com.nms.ui.ptn.upgradeManage.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import com.nms.db.bean.equipment.manager.UpgradeManage;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.systemconfig.dialog.bsUpdate.UpdateLog;
import com.nms.ui.ptn.upgradeManage.UpgradeManageUtil;
import com.nms.ui.ptn.upgradeManage.view.UpgradeVersionsDialog;
public class UpgradeVersionsDialogController implements ActionListener {

	public UpgradeVersionsDialog view;
	private ZipFile zipFile;
	private List<ZipEntry> zipEntries = new ArrayList<ZipEntry>();;
	private List<UpgradeManage> upgradeManages;
	private String str = "";
	private SiteInst siteInst;
	private Thread progressBarThread ;
	private Thread upgradeSoftVersions;
	private List<ZipEntry> entriesNeed = new ArrayList<ZipEntry>();
	private UpdateLog nowupdateLog;
	public UpgradeVersionsDialogController(UpgradeVersionsDialog view) {
		nowupdateLog = new UpdateLog();
		this.upgradeManages = view.getUpgradeManages();
		this.view = view;
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInst = siteService.select(ConstantUtil.siteId);
			siteInst.setCardNumber(view.getUpgradeManagePanel().getType()+"");
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
		}
		
		addListener();
	}

	private void addListener() {
		view.getConfirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {	
					if(view.getUserNameTextField().getText().trim().equals("")||view.getUserNameTextField().getText().trim() == null){
						DialogBoxUtil.succeedDialog(null,  ResourceUtil.srcStr(StringKeysLbl.FILEERROR));
						return ;
					}
					//并将打开按钮变灰
					view.getConfirm().setEnabled(false);
					view.getOpen().setEnabled(false);
					view.getIsForce().setEnabled(false);
					//升级单独线程
					upgradeSoftVersions = new Thread(new Runnable() {
						@Override
						public void run() {
						try {
							DispatchUtil siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
							String resultSummary = siteDispatch.sendSummary(siteInst);
							if(!resultSummary.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
								DialogBoxUtil.errorDialog(null, resultSummary);
								view.getConfirm().setEnabled(true);
								view.getOpen().setEnabled(true);
								view.getIsForce().setEnabled(true);
								return;
							}
							//进度条的线程
							progressBarThread = new Thread(view);
							progressBarThread.start();
							ConstantUtil.isCancleRun = false;
							String result = UpgradeManageUtil.upgradeProcedure(entriesNeed, view.getPasswordField(), siteInst, view.getUpgradeManagePanel().getType(), view, zipFile,null,0);
							progressBarThread.stop();
							progressBarThread = null;
							UpdateLog updateLog = new UpdateLog();
							updateLog.setUpgradeManages(upgradeManages);
							AddOperateLog.insertOperLog(view.getConfirm(), EOperationLogType.EQUIMENT.getValue(), result, 
									updateLog, nowupdateLog, ConstantUtil.siteId, ResourceUtil.srcStr(StringKeysTitle.TIT_VERSIONS_UPGRADE), "updateLog");
							if(ResultString.CONFIG_SUCCESS.equals(result)){
								DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_UPDATE_SUCCESSED_REBOOT));
							}else{
								DialogBoxUtil.errorDialog(null, result);
							}
							if(zipFile != null){
								zipFile.close();
								zipFile = null;
							}
							view.dispose();
							} catch (Exception e) {
								
					     	if(progressBarThread != null && !progressBarThread.isInterrupted()){
							  progressBarThread.stop();
							  progressBarThread = null;
							 }
							if(!upgradeSoftVersions.isInterrupted()){
								upgradeSoftVersions.stop();
								upgradeSoftVersions = null;
							}
							view.dispose();
						 }
						}
					});
					upgradeSoftVersions.start();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		
		
		
		
		view.getCancel().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(upgradeSoftVersions != null){
					    if(progressBarThread != null && !progressBarThread.isInterrupted()){
						   progressBarThread.stop();
						   progressBarThread = null;
					    }
						if(!upgradeSoftVersions.isInterrupted()){
							upgradeSoftVersions.stop();
							upgradeSoftVersions = null;
						}
						DialogBoxUtil.succeedDialog(null,cancelSofeWare());
						view.dispose();
				    }else{
					  view.dispose();
					}
					if(zipFile != null){
						zipFile.close();
						zipFile = null;
					}
					
				} catch (Exception e2) {
					ExceptionManage.dispose(e2,this.getClass());
				}
			}
		});
		view.getIsForce().addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(zipFile != null){
					nowupdateLog.getUpgradeManages().clear();
					entriesNeed.clear();
					if(e.getStateChange() == ItemEvent.SELECTED){
						str = UpgradeManageUtil.getSummary(nowupdateLog.getUpgradeManages(),zipEntries, zipFile, siteInst, upgradeManages,entriesNeed,0,true);
						view.getUserNameTextField().setText(str);
					}else if(e.getStateChange() == ItemEvent.DESELECTED){
						str = UpgradeManageUtil.getSummary(nowupdateLog.getUpgradeManages(),zipEntries, zipFile, siteInst, upgradeManages,entriesNeed,0,false);
						view.getUserNameTextField().setText(str);
					}
				}
			}
		});
	}
	
	private String cancelSofeWare(){
		String result = "";
		DispatchUtil dispatch = null;
		try {
			dispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			result = dispatch.executeQueryCancelSoftWare(this.siteInst);
			this.insertOpeLog(EOperationLogType.UPGRADCANCLE.getValue(), result, null,null);	
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		}
		return result;
		
	}
	
	 private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
			SiteService_MB service = null;
			try {
				service = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				String siteName=service.getSiteName(ConstantUtil.siteId);
		        AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac,ConstantUtil.siteId,siteName,"");	
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(service);
			}
		}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(ResourceUtil.srcStr(StringKeysBtn.BTN_OPEN))) {
			File file = null;
			view.getFileChooser().setApproveButtonText(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			view.getFileChooser();
			view.getFileChooser().setFileFilter(new FileNameExtensionFilter("ZIP", "zip"));
			int result = view.getFileChooser().showOpenDialog(view);
			if (result == JFileChooser.APPROVE_OPTION) {
				file = view.getFileChooser().getSelectedFile();
				view.getChooseFileTextField().setText(file.getAbsolutePath());
				if(zipFile != null){
					try {
						zipFile.close();
						zipEntries.clear();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					zipFile = null;
				}
				zipFile = UpgradeManageUtil.loadZip(zipEntries, view.getChooseFileTextField().getText());//解析压缩包
				entriesNeed.clear();
				str = UpgradeManageUtil.getSummary(nowupdateLog.getUpgradeManages(),zipEntries, zipFile, siteInst, upgradeManages,entriesNeed,0,view.getIsForce().isSelected());
				view.getUserNameTextField().setText(str);
			} else if (result == JFileChooser.CANCEL_OPTION) {
				// view.getChooseFileTextField().setText("");
			}
		} else if (command.equals(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL))) {

			view.dispose();
		}
	}
	
}
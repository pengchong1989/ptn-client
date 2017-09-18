package com.nms.ui.ptn.safety.controller;


import java.awt.event.ActionEvent;
import java.util.List;
import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.util.ExportExcel;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.LoginLogPanel;
import com.nms.ui.ptn.safety.dialog.LogChooseTime;
import com.nms.ui.ptn.safety.dialog.LoginLogFilterDialog;
/**
 * 登陆日志
 * 显示处理
 * @author sy
 *
 */
public class LogPanelController extends AbstractController{
	
	private  LoginLogPanel view;

	/**
	 * 过滤条件查询
	 *   根据log是否为空     
	 *   null  查询全部
	 *    ！null 条件查询
	 */
	private LoginLog log=null;	
	
	public LogPanelController(LoginLogPanel loginLagPanel) {
		this.view = loginLagPanel;
	}
	
	/**
	 * 刷新按钮事件	
	 */
	@Override
	public void refresh() {
		LoginLogServiece_Mb loginlogServiece=null;
		List<LoginLog> loginLogList = null;
		try {
			loginlogServiece=(LoginLogServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			if (log==null) {			
				// 若过滤条件为空，则显示所有信息
				log=new LoginLog();																			
			}
			loginLogList =loginlogServiece.select(log);		
			this.view.clear();
			this.view.initData(loginLogList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(loginlogServiece);
		}
	}
	// 打开设置过滤条件对话框
	
		@Override
		public void openFilterDialog()throws Exception {
			final LoginLogFilterDialog filterDialog = new LoginLogFilterDialog();
			filterDialog.getConfirm().addActionListener(new MyActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent evt) {
						try {
							LogPanelController.this.setFilter(filterDialog);
						} catch (Exception e) {
							
							ExceptionManage.dispose(e,this.getClass());
						}
				}
				@Override
				public boolean checking() {
					
					return true;
				}
			});

			UiUtil.showWindow(filterDialog, 390, 250);
		}

		/*
		 * 设置过滤条件，并显示查询结果
		 */
		private void setFilter(LoginLogFilterDialog dialog)throws Exception {
			
			try {
				
			log = new LoginLog();
			// 选择  模糊查询 复选框时
			if(dialog.getChbLiekSelect().isSelected()){
				log.setSelect(true);
			}
			log.setUser_name(dialog.userField.getText());
			//新增几种条件
			log.setLoginBeginTime(dialog.getLoginBeginTime().getText());
			log.setLoginEndTime(dialog.getLoginEndTime().getText());
			log.setLeaveBeginTime(dialog.getLeaveBeginTime().getText());
			log.setLeaveEndTime(dialog.getLeaveEndTime().getText());
			log.setLoginIp(dialog.getLoginIp().getText());
			if(dialog.getChbLiekSelect2().isSelected()){
				log.setIpSelect(true);
			}
			int operationType = dialog.getOperatingCmb().getSelectedIndex();
			if(operationType == 0){
				log.setLoginState(2);
			}else if(operationType == 1){
				log.setLoginState(1);
			}else if(operationType == 2){
				log.setLogoutState(2);
			}else if(operationType == 3){
				log.setLogoutState(1);
			}
			this.refresh();	
		    this.insertOpeLog(EOperationLogType.LOGSELECT.getValue(), ResultString.CONFIG_SUCCESS, null, null);
			} catch (Exception e) {				
				ExceptionManage.dispose(e,this.getClass());
			} finally {
				dialog.dispose();
			}
		}
	@Override
	public void clearFilter()throws Exception{
		log=null;
		this.refresh();
		this.insertOpeLog(EOperationLogType.LOGCLEARSELECT.getValue(), ResultString.CONFIG_SUCCESS, null, null);
	}
	
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TT_SELECT_LOG),"");		
	}
	/**
	 * 移除 按钮事件
	 */
	public void removeAction(){
		new LogChooseTime(1);
		this.refresh();
		this.insertOpeLog(EOperationLogType.LOGREMOVE.getValue(), ResultString.CONFIG_SUCCESS, null, null);
	}
	//导出
	@Override
	public void export()throws Exception{
		List<LoginLog> infos = null;
		String result;
		ExportExcel export=null;
		// 得到页面信息
		try {
			infos =  this.view.getTable().getAllElement();
			export=new ExportExcel();
			//得到bean的集合转为  String[]的List
			List<String[]> beanData=export.tranListString(infos,"loginLogTable");
			//导出页面的信息-Excel
			result=export.exportExcel(beanData, "loginLogTable");
			//添加操作日志记录
			this.insertOpeLog(EOperationLogType.EXPORTLOGINLOG.getValue(),result, null, null);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			infos = null;
			result=null;
			export=null;
		}
	}

}

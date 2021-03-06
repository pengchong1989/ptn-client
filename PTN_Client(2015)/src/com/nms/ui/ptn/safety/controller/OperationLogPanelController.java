package com.nms.ui.ptn.safety.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.system.OperationLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.ExportExcel;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.OperationLogPanel;
import com.nms.ui.ptn.safety.dialog.LogChooseTime;
import com.nms.ui.ptn.safety.dialog.OperationLogFilterDialog;
/**
 * 处理操作日志
 * @author sy
 *
 */
public class OperationLogPanelController extends AbstractController{
	
	private OperationLogPanel view;
	private OperationLog log=null;
	private String startTime;//起始时间
	private String overTime;//截止时间
	private int total;
	private int now = 1;
	private List<OperationLog> infos = null;
	
	public OperationLogPanelController(OperationLogPanel operationLagPanel) {
		this.view = operationLagPanel;
	}
	public OperationLogPanelController(){
		
	}
	
	/**
	 * 刷新按钮事件	
	 */
	@Override
	public void refresh() {
		OperationLogService_MB operationService=null;
		List<OperationLog> needs = new ArrayList<OperationLog>();
		try {
			operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
			if (log==null) {				
				// 若过滤条件为空，则显示所有信息
				log=new OperationLog();	
				log.setSiteId(-2);
			}
			infos =operationService.select(log);
			if(infos.size() ==0){
				now = 0;
				view.getNextPageBtn().setEnabled(false);
				view.getGoToJButton().setEnabled(false);
			}else{
				now =1;
				if (infos.size() % ConstantUtil.flipNumber == 0) {
					total = infos.size() / ConstantUtil.flipNumber;
				} else {
					total = infos.size() / ConstantUtil.flipNumber + 1;
				}
				if (total == 1) {
					view.getNextPageBtn().setEnabled(false);
					view.getGoToJButton().setEnabled(false);
				}else{
					view.getNextPageBtn().setEnabled(true);
					view.getGoToJButton().setEnabled(true);
				}
				if (infos.size() - (now - 1) * ConstantUtil.flipNumber > ConstantUtil.flipNumber) {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, ConstantUtil.flipNumber);
				} else {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size() - (now - 1) * ConstantUtil.flipNumber);
				}
			}
			operationService.setdata(needs);
			view.getCurrPageLabel().setText(now+"");
			view.getTotalPageLabel().setText(total + "");
			view.getPrevPageBtn().setEnabled(false);
			this.view.clear();
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(operationService);
		}
	}
	// 打开设置过滤条件对话框
	
	@Override
	public void openFilterDialog()throws Exception {
		final OperationLogFilterDialog filterDialog = new OperationLogFilterDialog();
		filterDialog.getConfirm().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {

				try {
					OperationLogPanelController.this.setFilter(filterDialog);
					this.insertOpeLog(EOperationLogType.OPERAIONLOGSELECT.getValue(),ResultString.CONFIG_SUCCESS, null, null);			
				} catch (Exception e) {
					ExceptionManage.dispose(e, OperationLogPanelController.class);
				}
				
			}

			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
				AddOperateLog.insertOperLog(filterDialog.getConfirm(), operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TT_OPERATION_SELECT_LOG),"");		
			}
		});

		UiUtil.showWindow(filterDialog, 390, 270);
	}


	/*
	 * 设置过滤条件，并显示查询结果
	 */
	private void setFilter(OperationLogFilterDialog dialog)throws Exception {
		
		try {			
			startTime=this.setUsTime(dialog.getStartChooseTime().getText());
			overTime=this.setUsTime(dialog.getOverChooseTime().getText());
			log=new OperationLog();
			log.setUserName(dialog.userField.getText());
			// 模糊查询是否为选中
			if(dialog.getChbLikeSelect().isSelected()){
				log.setSelect(true);
			}
			log.setOperationType(EOperationLogType.from(dialog.getOperationLogTypeField().getSelectedItem()+""));
			if(startTime!=null&&!this.startTime.equals("")){
				log.setStartTime(this.startTime);
			}
			if(overTime!=null&&!this.overTime.equals("")){
				log.setOverTime(this.overTime);
			}			
			int result=0;
			if(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_ISUCCESS).equals(dialog.getOperationResultBox().getSelectedItem())){
				result=1;
			}else if(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_FALSE).equals(dialog.getOperationResultBox().getSelectedItem())){
				result=2;
			}
			log.setOperationResult(result);
			if(dialog.getSiteCombox().getSelectedItem().equals(ResourceUtil.srcStr(StringKeysOperaType.BTN_ALL))){
				log.setSiteId(-2);
			}else{
				SiteInst f = new SiteInst();
				ControlKeyValue controlKeyValue = (ControlKeyValue) dialog.getSiteCombox().getSelectedItem();
				f = (SiteInst) controlKeyValue.getObject();
				log.setSiteId(f.getSite_Inst_Id());
			}
			
			this.refresh();
		
			dialog.dispose();
		} catch (Exception e) {
			dialog.dispose();
			throw e;
		}
	}
	//清除过滤
	@Override
	public void clearFilter()throws Exception{
		log=null;
		this.refresh();
		this.insertOpeLog(EOperationLogType.OPERAIONLOGCLEARSELECT.getValue(),ResultString.CONFIG_SUCCESS, null, null);			
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TT_OPERATION_SELECT_LOG),"");		
	}
	/**
	 * 移除 按钮事件
	 */
	public void removeAction(){
		new LogChooseTime(2);
		this.refresh();
		this.insertOpeLogRemove(EOperationLogType.OPERATIONLOGCLEAR.getValue(),ResultString.CONFIG_SUCCESS, null, null);		
	}
	
	private void insertOpeLogRemove(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TT_OPERATION_SELECT_LOG),"");		
	}
	//导出
	@Override
	public void export()throws Exception{
		List<OperationLog> infos = null;
		String result;
		ExportExcel export=null;
		// 得到页面信息
		try {
			infos =  this.view.getTable().getAllElement();
			export=new ExportExcel();
			//得到bean的集合转为  String[]的List
			List<String[]> beanData=export.tranListString(infos,"operationLogTable");
			//导出页面的信息-Excel
			result=export.exportExcel(beanData, "operationLogTable");
			//添加操作日志记录
			this.insertOpeLog(EOperationLogType.EXPORTOPERATELOG.getValue(),result, null, null);	
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			infos = null;
			result=null;
			export=null;
		}
	}
	
	/**
	 * 将MM-dd-yyyy转为 中文 yyyy-MM-dd
	 * 
	 * @param enTime
	 * 
	 *            sy
	 * @return
	 */
	private String setUsTime(String enTime) {
		if (enTime == null || "".equals(enTime)) {
			return null;
		}
		if (ResourceUtil.language.equals("zh_CN")) {
			return enTime;
		}
		String newTime = null;
		StringBuffer dd = null;
		StringBuffer MM = null;
		StringBuffer yyyy = null;
		try {
			dd = new StringBuffer();
			MM = new StringBuffer();
			yyyy = new StringBuffer();
			/**
			 * 取 前10个字节 即 MM-dd-yyyy （01-23-2014）
			 */
			for (int i = 0; i < enTime.length(); i++) {
				if (i < 2) {
					MM.append(enTime.charAt(i) + "");
				}
				if (i < 5 && i > 2) {
					dd.append(enTime.charAt(i) + "");
				}
				if (i < 10 && i > 5) {
					yyyy.append(enTime.charAt(i) + "");
				}
			}
			newTime = yyyy.append("-").append(MM).append("-").append(dd).toString();
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			yyyy = null;
			MM = null;
			dd = null;
		}
		return newTime;
	}
	
	@Override
    public void prevPage()throws Exception{
    	now = now-1;
    	if(now == 1){
    		view.getPrevPageBtn().setEnabled(false);
    	}
    	view.getNextPageBtn().setEnabled(true);
    	
    	flipRefresh();
    }
	
	private void flipRefresh() {
		view.getCurrPageLabel().setText(now + "");
		List<OperationLog> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		OperationLogService_MB operationService=null;
		try {
			operationService = (OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
			operationService.setdata(needs);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(operationService);
		}
		this.view.clear();
		this.view.initData(needs);
		this.view.updateUI();
	}
	
	@Override
	public void goToAction() throws Exception {
		if (CheckingUtil.checking(view.getGoToTextField().getText(), CheckingUtil.NUM1_9)) {// 判断填写是否为数字
			Integer goi = Integer.parseInt(view.getGoToTextField().getText());
			if(goi>= total){
				goi = total;
				view.getNextPageBtn().setEnabled(false);
			}
			if(goi == 1){
				view.getPrevPageBtn().setEnabled(false);
			}
			if(goi > 1){
				view.getPrevPageBtn().setEnabled(true);
			}
			if(goi<total){
				view.getNextPageBtn().setEnabled(true);
			}
			now = goi;
			flipRefresh();
		}else{
			DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.MESSAGE_NUMBER));
		}
		
	}
	
	@Override
	public void nextPage() throws Exception {
		now = now+1;
		if(now == total){
			view.getNextPageBtn().setEnabled(false);
		}
		view.getPrevPageBtn().setEnabled(true);
		flipRefresh();
	}
	
}

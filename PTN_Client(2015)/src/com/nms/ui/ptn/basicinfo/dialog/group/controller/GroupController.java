package com.nms.ui.ptn.basicinfo.dialog.group.controller;

import java.util.List;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.FieldService_MB;
import com.nms.model.system.SubnetService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.basicinfo.dialog.group.view.AddGroupDialog;
import com.nms.ui.ptn.basicinfo.dialog.group.view.GroupTablePanel;

public class GroupController extends AbstractController {
	private GroupTablePanel view;


	public GroupTablePanel getView() {
		return view;
	}

	public void setView(GroupTablePanel view) {
		this.view = view;
	}

	public GroupController(GroupTablePanel groupTablePanel) {
		this.setView(groupTablePanel);

	}

	@Override
	public void refresh() throws Exception {
		searchAndrefreshdata();

	}

	/**
	 * 刷新、查询
	 */
	private void searchAndrefreshdata() {
		FieldService_MB service = null;
		List<Field> groupList = null;
		NetWork netWork = new NetWork();
		try {
		service = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
		ControlKeyValue controlKeyValue = (ControlKeyValue) this.view.getComboBox().getSelectedItem();
		netWork = (NetWork) controlKeyValue.getObject();
		if (netWork.getNetWorkId() > 0) {
			groupList = service.queryByNetWorkid(netWork.getNetWorkId());
		}else{
			groupList = service.selectField();
		}
			this.view.clear();
			this.view.initData(groupList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	/**
	 * 新建
	 */
	@Override
	public void openCreateDialog() {
		new AddGroupDialog(this.getView(), true);
	}

	/**
	 * 修改
	 */
	@Override
	public void openUpdateDialog() throws Exception {
		if (this.getView().getAllSelect().size() == 0) {
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else {
			Field field = this.getView().getAllSelect().get(0);
			new AddGroupDialog(this.getView(), field,true);
		}
	}

	/**
	 * /删除
	 */
	@Override
	public void delete() {
		List<Field> fieldList = null;
		SubnetService_MB service = null;
//		boolean flag = true;
		boolean delresult = true;
		try {
			service = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
			fieldList = this.getView().getAllSelect();

			for (Field field : fieldList) {
//				boolean Single;
//				Single = service.isSingle(field);
//				if (Single) {
//					flag = false;
//					break;
//				} else {
					delresult = service.delete(field);
//				}
					String config;
					if(delresult){
						config=ResultString.CONFIG_SUCCESS;
					}else{
						config=ResultString.CONFIG_FAILED;
					}
					this.insertOpeLog(EOperationLogType.DELETEGROUPID.getValue(), config, null, field);			
			}
//			if (!flag) {
//				DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SUBNET_DELETE_NODE));
//				return;
//			}
			//添加日志记录
			PtnButton deleteButton=this.view.getDeleteButton();
			deleteButton.setOperateKey(EOperationLogType.DELETEGROUPID.getValue());
			int operationResult=0;
			if(delresult){
				operationResult=1;
			}else{
				operationResult=2;
			}
			deleteButton.setResult(operationResult);
			this.view.getRefreshButton().doClick();
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		Field field=(Field)newMac;
		String fieldName=field.getFieldName();		
		newMac=null;
		AddOperateLog.insertOperLog(view.getDeleteButton(), operationType, result, oldMac, newMac, 0,fieldName,"");		
	}
	/**
	 * 
	 * 删除之前
	 */
	@Override
	public boolean deleteChecking() throws Exception {
		boolean flag = false;
		List<Field> fieldList = null;
		SubnetService_MB service = null;
		try {
			service = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
			fieldList = this.getView().getAllSelect();

			for (Field field : fieldList) {
				boolean Single;
				Single = service.isSingle(field);
				if (Single) {					
					flag = true;
					break;
				} 
			}
			if (flag) {
				DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SUBNET_DELETE_NODE1));
				return false;
			}else{
				for (Field field : fieldList) {
					List<Field> fields=service.getMapper().selectByParentId(field.getId());
					if(fields !=null && fields.size()>0){
						flag = true;
						break;
					}
					
				}												
			}
			
			if (flag) {
				DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SUB_DELETE_NODE));
				return false;
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(service);
		}
		return true;
	}
}

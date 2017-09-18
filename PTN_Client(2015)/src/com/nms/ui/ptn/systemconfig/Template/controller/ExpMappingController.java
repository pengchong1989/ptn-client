package com.nms.ui.ptn.systemconfig.Template.controller;

import java.util.List;

import com.nms.db.bean.ptn.qos.QosMappingAttr;
import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.model.ptn.qos.QosMappingTemplateService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.util.TypeAndActionUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.systemconfig.Template.view.ExpMappingPanel;
import com.nms.ui.ptn.systemconfig.Template.view.dialog.EditeExpMappingDialog;

/**
 * EXP映射控制类
 * 
 * @author dzy
 * 
 */
public class ExpMappingController extends AbstractController {

	private ExpMappingPanel expMappingPanel = null; //EXP映射主界面面板

	/**
	 * 创建一个新的实例
	 * 
	 * @param EXP映射面面板
	 */
	public ExpMappingController(ExpMappingPanel expMappingPanel) {
		this.expMappingPanel = expMappingPanel;
	}

	/**
	 * 刷新界面数据
	 */
	@Override 
	public void refresh() throws Exception {
		
		QosMappingTemplateService_MB qosMappingTemplateService = null;
		List<QosMappingMode> qosMappingList=null;
		QosMappingAttr qosMappingAttr;
		try {
			qosMappingAttr = new QosMappingAttr();
			qosMappingAttr.setMappingType(3);
			qosMappingTemplateService = (QosMappingTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSMAPPINGTEMPLATESERVICE);
			qosMappingList=qosMappingTemplateService.refresh(qosMappingAttr);
			this.expMappingPanel.clear();
			this.expMappingPanel.initData(qosMappingList);
			this.expMappingPanel.updateUI();

		} catch (Exception e) {
 			throw e;
		} finally {
			UiUtil.closeService_MB(qosMappingTemplateService);
		}
	}

	/**
	 * 打开新建窗口
	 */
	@Override
	public void openCreateDialog() {
		try {
			new EditeExpMappingDialog(null,this.expMappingPanel,TypeAndActionUtil.ACTION_INSERT,UiUtil.getCodeByValue("EXPTYPE", "0").getId());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 打开修改窗口
	 */
	@Override
	public void openUpdateDialog() {
		try {
			new EditeExpMappingDialog(this.expMappingPanel.getSelect(),this.expMappingPanel,TypeAndActionUtil.ACTION_UPDATE, UiUtil.getCodeById(this.expMappingPanel.getSelect().getQosMappingAttrList().get(0).getModel()).getId());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 删除数据
	 */
	@Override
	public void delete() {
		int result = 0;
		List<QosMappingMode> qosMappingModeList = null; 
		QosMappingTemplateService_MB qosMappingTemplateService = null;
		try {
			qosMappingModeList = this.expMappingPanel.getAllSelect();
			qosMappingTemplateService = (QosMappingTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSMAPPINGTEMPLATESERVICE);
			for(QosMappingMode qosMappingMode:qosMappingModeList){
				result = qosMappingTemplateService.delete(qosMappingMode.getQosMappingAttrList().get(0).getGroupid());
			}
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(qosMappingTemplateService);
		}
	}
}

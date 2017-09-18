package com.nms.ui.ptn.systemconfig.Template.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.qos.CosToVlanpri;
import com.nms.db.bean.ptn.qos.QosMappingAttr;
import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.model.ptn.qos.QosMappingTemplateService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.systemconfig.Template.view.PriorityToVlanpriPanel;
import com.nms.ui.ptn.systemconfig.Template.view.dialog.EditePriorityToVlanpriDialog;

/**
 * 优先业务到VLANPRI映射控制类
 * 
 * @author dzy
 * 
 */
public class PriorityToVlanpriController extends AbstractController {

	private PriorityToVlanpriPanel priorityToVlanpriPanel = null; // msp主界面面板

	/**
	 * 创建一个新的实例
	 * 
	 * @param msp主界面面板
	 */
	public PriorityToVlanpriController(PriorityToVlanpriPanel priorityToVlanpriPanel) {
		this.priorityToVlanpriPanel = priorityToVlanpriPanel;
	}

	/**
	 * 刷新界面数据
	 */
	@Override
	public void refresh() throws Exception {
		QosMappingTemplateService_MB qosMappingTemplateService = null;
		List<QosMappingMode> qosMappingList=null;
		QosMappingAttr qosMappingAttrSel=null;
		List<CosToVlanpri> CosToVlanpriList=null;
		try {
			qosMappingAttrSel = new QosMappingAttr();
			qosMappingAttrSel.setMappingType(5);
			qosMappingTemplateService = (QosMappingTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSMAPPINGTEMPLATESERVICE);
			qosMappingList=qosMappingTemplateService.refresh(qosMappingAttrSel);
			CosToVlanpriList = getCosToVlanpriList(qosMappingList);
			this.priorityToVlanpriPanel.clear();
			this.priorityToVlanpriPanel.initData(CosToVlanpriList);
			this.priorityToVlanpriPanel.updateUI();

		} catch (Exception e) {
 			throw e;
		} finally {
			UiUtil.closeService_MB(qosMappingTemplateService);
		}
	
	}

	/**
	 * 转换成列表数据
	 * @param qosMappingList
	 * @return
	 */
	private List<CosToVlanpri> getCosToVlanpriList(
			List<QosMappingMode> qosMappingList) {
		CosToVlanpri cosToVlanpri ;
		List<CosToVlanpri> cosToVlanpriList = new ArrayList<CosToVlanpri>();
		if(null!=qosMappingList&&qosMappingList.size()>0){
			for(QosMappingMode qosMappingMode :qosMappingList){
				cosToVlanpri = new CosToVlanpri();
				for(QosMappingAttr qosMappingAttr:qosMappingMode.getQosMappingAttrList()){
					if(0==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setBE(qosMappingAttr.getValue());
					}else if(1==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setAF1(qosMappingAttr.getValue());
					}else if(2==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setAF2(qosMappingAttr.getValue());
					}else if(3==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setAF3(qosMappingAttr.getValue());
					}else if(4==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setAF4(qosMappingAttr.getValue());
					}else if(5==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setEF(qosMappingAttr.getValue());
					}else if(6==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setCS6(qosMappingAttr.getValue());
					}else if(7==Integer.parseInt(qosMappingAttr.getGrade())){
						cosToVlanpri.setCS7(qosMappingAttr.getValue());
					}
				}
				cosToVlanpri.setName(qosMappingMode.getName());
				cosToVlanpri.setQosMappingMode(qosMappingMode);
				cosToVlanpriList.add(cosToVlanpri);
				
			}
		}
		cosToVlanpri = null;
		return cosToVlanpriList;
	}

	/**
	 * 打开新建窗口
	 */
	@Override
	public void openCreateDialog() {
		try {
			new EditePriorityToVlanpriDialog(null,this.priorityToVlanpriPanel,"");
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
			new EditePriorityToVlanpriDialog(this.priorityToVlanpriPanel.getSelect().getQosMappingMode(),this.priorityToVlanpriPanel,"");
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
		List<CosToVlanpri> cosToVlanpriList=null;
		QosMappingTemplateService_MB qosMappingTemplateService = null;
		try {
			cosToVlanpriList = this.priorityToVlanpriPanel.getAllSelect();
			qosMappingTemplateService = (QosMappingTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QOSMAPPINGTEMPLATESERVICE);
			for(CosToVlanpri cosToVlanpri:cosToVlanpriList){
				result = qosMappingTemplateService.delete(cosToVlanpri.getQosMappingMode().getQosMappingAttrList().get(0).getGroupid());
			}
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(qosMappingTemplateService);
		}
	}

}

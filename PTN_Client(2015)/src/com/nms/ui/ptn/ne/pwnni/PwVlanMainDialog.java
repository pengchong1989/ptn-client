﻿package com.nms.ui.ptn.ne.pwnni;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.pw.PwNniInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.SiteUtil;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.pwnni.view.PwVlanCXDialog;
import com.nms.ui.ptn.ne.pwnni.view.PwVlanWHDialog;

/**
 * 
 * 项目名称：WuHanPTN2012 类名称：PortMainDialog 类描述： 修改端口端口部分主界面 创建人：kk 创建时间：2013-7-15 上午11:51:35 修改人：kk 修改时间：2013-7-15 上午11:51:35 修改备注：
 * 
 *   修改 
 *     --- sy
 *    此类作为pw  的vlan界面显示（所有vlan，包括武汉，陈晓网元）
 *        
 * @version
 * 
 */
public class PwVlanMainDialog extends PtnDialog {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2494021567865027645L;
	private PtnButton btnSave; // 确定按钮
	private JButton btnCanel; // 取消按钮
	private JPanel panelBtn; // 按钮布局
	private PwVlanCXDialog cxAPanel;//A端 陈晓 加载此 Panel
	private PwVlanWHDialog wuAPanel;//A端 武汉载此 Panel
	private PwVlanCXDialog cxZPanel;//Z端 陈晓 加载此 Panel
	private PwVlanWHDialog wuZPanel;//Z端武汉 加载此 Panel
	private PwNniInfo pwAinfo;//A 端 提取数据，使用此对象
	private PwNniInfo pwZinfo;//Z 端提取数据
	private PwInfo info=null;// 选取的 pw对象
	 boolean flag=false;//区分网络层与单网元，默认false：单网元
	 private JLabel lblMessage;
	/**
	 * 构造方法
	 * 
	 * @param flag 区分网络层与单网元
	 *        true  :  网络层
	 *        false : 单网元   
	 * @param info
	 *            选取pw的对象
	 */
	public PwVlanMainDialog(boolean flag, PwInfo info) {
		try {
			this.flag=flag;
			this.info=info;
			this.setModal(true);
			super.setTitle(ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE));
			this.initComponent();
			this.setLayout();
			this.addListener();
			
			if(flag){
				if(ResourceUtil.language.equals("zh_CN")){
					UiUtil.showWindow(this, 800, 300);
				}else{
					UiUtil.showWindow(this, 980, 300);
				}
			}else{
				if(ResourceUtil.language.equals("zh_CN")){
					UiUtil.showWindow(this, 430, 260);
				}else{
					UiUtil.showWindow(this, 530, 260);
				}
			}			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 * 添加监听
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void addListener() {
		//取消
		this.btnCanel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispose();
			}
		});
		// 保存按钮，事件
		this.btnSave.addActionListener(new MyActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					confirmActionPerformed();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				
				return true;
			}
		});
	}
	
	/**
	 * 处理陈晓VLAN 
	 * @param cxPanel
	 * @param node
	 * false A 端
	 * true Z 端
	 * @return
	 */
	private String cxAtion(PwVlanCXDialog cxPanel,boolean node){
		ControlKeyValue exitRule =null;
		ControlKeyValue tpId=null;
		String result="";
			
		try{
			exitRule = (ControlKeyValue) cxPanel.getCmbExitRule().getSelectedItem();
			tpId = (ControlKeyValue) cxPanel.getCmbTpID().getSelectedItem();
			
			/**
			 * node  false A端 
			 * 
			 */
			if(!node){
				pwAinfo=new PwNniInfo();
				pwAinfo.setPwStatus(this.info.getPwStatus());
				pwAinfo.setSiteId(this.info.getASiteId());
				pwAinfo.setPwBusinessId(this.info.getApwServiceId());
				pwAinfo.setPwId(this.info.getPwId());
				pwAinfo.setSvlan(cxPanel.getTxtSVlan().getText());
				pwAinfo.setVlanpri(cxPanel.getTxtVlanPri().getText());
				pwAinfo.setExitRule(Integer.parseInt(exitRule.getId()));
				pwAinfo.setTpid(Integer.parseInt(tpId.getId()));				
			//	result = pwBufferOperationImpl.excuteUpdate(pwAinfo);
			}
			/**
			 * Z 端的处理
			 */
			else{
				pwZinfo=new PwNniInfo();
				pwZinfo.setPwStatus(this.info.getPwStatus());
				pwZinfo.setSiteId(this.info.getZSiteId());
				pwZinfo.setPwBusinessId(this.info.getZpwServiceId());
				pwZinfo.setPwId(this.info.getPwId());
				pwZinfo.setSvlan(cxPanel.getTxtSVlan().getText());
				pwZinfo.setVlanpri(cxPanel.getTxtVlanPri().getText());
				pwZinfo.setExitRule(Integer.parseInt(exitRule.getId()));
				pwZinfo.setTpid(Integer.parseInt(tpId.getId()));				
			//	result = pwBufferOperationImpl.excuteUpdate(pwZinfo);
			}
			
		}catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			exitRule =null;
			tpId=null;
		}		
		return result;
	}
	/**
	 * 处理武汉 网元
	 *   完成提取数据
	 * @param wuPanel
	 * @param node
	 *  false   A  端
	 *  true    Z 端
	 * @return   result
	 *   返回 操作结果
	 *  配置成功－－操作成功
	 */
	private String wuAction(PwVlanWHDialog wuPanel,boolean node){
		String result="";
		try{
			
			/**
			 * true  ;赋值A 端 网元ID，pw名
			 */
			if(!node){
				pwAinfo=new PwNniInfo();				
				pwAinfo = wuPanel.getPwNniInfo(pwAinfo);
				pwAinfo.setPwStatus(this.info.getPwStatus());
				pwAinfo.setSiteId(this.info.getASiteId());
				pwAinfo.setPwBusinessId(this.info.getApwServiceId());							
			///	result = pwBufferOperationImpl.excuteUpdate(pwAinfo);
			}else{// Z端
				pwZinfo=new PwNniInfo();
				pwZinfo = wuPanel.getPwNniInfo(pwZinfo);
				pwZinfo.setPwStatus(this.info.getPwStatus());
				pwZinfo.setSiteId(this.info.getZSiteId());
				pwZinfo.setPwBusinessId(this.info.getZpwServiceId());
				//result = pwBufferOperationImpl.excuteUpdate(pwZinfo);
			}
			
			
		}catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			
		}
		return result;
	}
	/**
	 * 
	 * @throws Exception
	 */
	protected void confirmActionPerformed() throws Exception{
		DispatchUtil pwBufferOperationImpl;
		int operate=0;//操作日志添加，0 失败，1 成功
		List<PwNniInfo> pwinfoList=null;
		String result="";
		SiteService_MB siteService = null;
		PwNniInfoService_MB pwNNIService = null;
		try {
			pwNNIService = (PwNniInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			pwinfoList=new ArrayList<PwNniInfo>();
			pwBufferOperationImpl = new DispatchUtil(RmiKeys.RMI_PWBUFFER);
			/**
			 * 先判断A 端 网元id,
			 *   >0 : 可能是单网元（新建tunnel的类型为入口），也可能是网络层 ，正常提取数据
			 *   不大于0 ：（即==0），则必然为单网元，（新建tunnel的类型为出口）
			 */
			if(this.info.getASiteId()>0){
				if (siteService.getManufacturer(this.info.getASiteId()) == EManufacturer.WUHAN.getValue()) {
					// 收集武汉数据
					wuAction(this.wuAPanel,false);
				}else{
					//陈晓
					cxAtion(this.cxAPanel,false);
				}
				//判断  A  端  操作是否成功（不是武汉就是陈晓，即只有一个 ：所以  用||）
			}else{// A端网元id为0，则为单网元，并且，网元ID 在Z端
				if (siteService.getManufacturer(this.info.getZSiteId()) == EManufacturer.WUHAN.getValue()) {
					// 收集武汉数据
					wuAction(this.wuZPanel,true);
				}else{
					//陈晓
					cxAtion(this.cxZPanel,true);
				}
			}			
			/**
			 * 网络层 需 处理Z端，
			 */
			if(flag){
				if (siteService.getManufacturer(this.info.getZSiteId()) == EManufacturer.WUHAN.getValue()) {
					// 收集武汉数据
					wuAction(this.wuZPanel,true);
				}else{
					//陈晓
					cxAtion(this.cxZPanel,true);
				}
			}
			PwNniInfo aOldPwNniInfo = null;
			if(this.pwAinfo!=null){
				pwinfoList.add(this.pwAinfo);
				aOldPwNniInfo = pwNNIService.select(this.pwAinfo).get(0);
				this.pwAinfo.setPwName(this.info.getPwName());
				aOldPwNniInfo.setPwName(this.info.getPwName());
			}
			PwNniInfo zOldPwNniInfo = null;
			if(this.pwZinfo!=null){
				pwinfoList.add(this.pwZinfo);
				zOldPwNniInfo = pwNNIService.select(this.pwZinfo).get(0);
				this.pwZinfo.setPwName(this.info.getPwName());
				zOldPwNniInfo.setPwName(this.info.getPwName());
			}
			
			result=pwBufferOperationImpl.excuteUpdate(pwinfoList);
			if(result!=null&&result.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
				operate=1;
			}else{
				operate=0;
			}
			this.btnSave.setResult(operate);
			this.btnSave.setOperateKey(EOperationLogType.PWVLAN.getValue());
			if(this.pwAinfo != null){
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.PWVLAN.getValue(), result, aOldPwNniInfo, 
						this.pwAinfo, this.pwAinfo.getSiteId(), this.pwAinfo.getPwName(), "pwNNIInfo");
			}
			if(this.pwZinfo!=null){
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.PWVLAN.getValue(), result, zOldPwNniInfo, 
						this.pwZinfo, this.pwZinfo.getSiteId(), this.pwZinfo.getPwName(), "pwNNIInfo");
			}
		//	PwPanel.getPwPanel().getController().refresh();
			DialogBoxUtil.succeedDialog(null, result);
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {//结束操作时，清空所有属性
			this.wuAPanel = null;
			this.wuZPanel = null;
			this.cxAPanel = null;
			this.cxZPanel = null;
			this.pwAinfo = null;
			this.pwZinfo = null;
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(pwNNIService);
		}
	}
	/**
	 * 初始化控件
	 */
	private void initComponent() throws Exception {
		lblMessage=new JLabel();
		this.mainPanel = new JPanel();
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
		SiteUtil siteUtil = new SiteUtil();
		if(info.getASiteId()>0){
			if(EManufacturer.WUHAN.getValue() == siteUtil.getManufacturer(info.getASiteId())){
				this.wuAPanel =new PwVlanWHDialog(this.info,true,this);
			}else if(EManufacturer.CHENXIAO.getValue() == siteUtil.getManufacturer(info.getASiteId())){
				this.cxAPanel=new PwVlanCXDialog(this.info,true,this);
			}
		}
		if(info.getZSiteId()>0){
			if(EManufacturer.WUHAN.getValue() == siteUtil.getManufacturer(info.getZSiteId())){
				this.wuZPanel=new PwVlanWHDialog(this.info,false,this);
			}else if(EManufacturer.CHENXIAO.getValue() == siteUtil.getManufacturer(info.getZSiteId())){
				this.cxZPanel=new PwVlanCXDialog(this.info,false,this);
			}
		}
		this.panelBtn = new JPanel();
		/**
		 * 网络层 设置 内部 窗体名
		 */
		if(flag){
			if(cxAPanel != null){
				cxAPanel.setBorder(
						BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_A_PORT)+ " "+ (ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE))));
			}
			if(wuAPanel != null){
				wuAPanel.setBorder(
						BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_A_PORT)+ " "+ (ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE))));
			}
		}
	}
	/**
	 * 向主面板中添加Panel
	 * 根据 A,Z 端网元id，确认 是 武汉Panel还是陈晓Panel
	 * @param c
	 *    承载主界面布局，添加组件
	 */
	private void setPanelLayout(GridBagConstraints c) {
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			/**
			 * 先判断A 端 网元id,
			 *   >0 : 可能是单网元（新建tunnel的类型为入口），也可能是网络层 ，正常提取数据
			 *   不大于0 ：（即==0），则必然为单网元，（新建tunnel的类型为出口）
			 */
			if(this.info.getASiteId()>0){
				/**
				 * 判断A 端网元Id，添加陈晓panel
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteService.getManufacturer(this.info.getASiteId())) {
					addComponent(this.mainPanel, this.cxAPanel, 0, 1, 0.2, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				} else {
					/*
					 * 判断A端网元id,添加武汉Panel
					 */
					addComponent(this.mainPanel, this.wuAPanel, 0, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				}
			}else{// a端网元id为0，则为单网元，并且，网元ID 在Z端
				/**
				 * 判断Z 端网元Id，添加陈晓panel
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteService.getManufacturer(this.info.getZSiteId())) {
					addComponent(this.mainPanel, this.cxZPanel, 0, 1, 0.2, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				} else {
					/*
					 * 判断A端网元id,添加武汉Panel
					 */
					addComponent(this.mainPanel, this.wuZPanel, 0, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				}
			}
			
			//网络层，需要添加Z端倒换界面
			if(flag){				
				/**
				 * 判断Z 端网元是武汉还是陈晓，并且重新命名子窗体名称
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteService.getManufacturer(this.info.getZSiteId())) {
					cxZPanel.setBorder(
									BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_PORT)+ " "+ (ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE))));
					addComponent(this.mainPanel, this.cxZPanel, 1, 1, 0, 0, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				} else {
					//添加Z端武汉倒换界面，并且重新命名子窗体名称
					wuZPanel.setBorder(
									BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_PORT)+ " "+ ResourceUtil
													.srcStr(StringKeysPanel.PANEL_PW_PORT_CONFIGE)));
					addComponent(this.mainPanel, this.wuZPanel, 1, 1, 0, 0, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 设置主界面布局	
	 */
	private void setLayout() {
		this.add(this.mainPanel);
		this.setButtonLayout();
		GridBagLayout componentLayout = new GridBagLayout();
		if(flag){
			//网络层布局 ： 分A 端和 Z 端
			componentLayout.columnWidths = new int[] { 200,200 };
			componentLayout.columnWeights = new double[] { 0.1,0.1};
		}else{
			//单网元布局
			componentLayout.columnWidths = new int[] { 200 };
			componentLayout.columnWeights = new double[] { 0.1};
		}	
		componentLayout.rowHeights = new int[] { 30,160, 40 };
		componentLayout.rowWeights = new double[] { 0, 0.1 ,0};
		this.mainPanel.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		addComponent(this.mainPanel, panelBtn, 0, 2, 0, 0, 2, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponent(this.mainPanel, this.lblMessage, 0, 0, 0, 0, 2, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		setPanelLayout(c);
	}

	/**
	 * 设置按钮布局
	 */
	private void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 150, 25,25 };
		componentLayout.columnWeights = new double[] { 0.1, 0 ,0};
		componentLayout.rowHeights = new int[] { 30 };
		componentLayout.rowWeights = new double[] { 0 };
		this.panelBtn.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.btnSave, c);
		this.panelBtn.add(this.btnSave);

		c.gridx = 2;
		c.anchor = GridBagConstraints.CENTER;
		componentLayout.setConstraints(this.btnCanel, c);
		this.panelBtn.add(this.btnCanel);

	}
	private JPanel mainPanel;//主界面 的Panel
	public PtnButton getBtnSave() {
		return btnSave;
	}
	public void setBtnSave(PtnButton btnSave) {
		this.btnSave = btnSave;
	}
	public JLabel getLblMessage() {
		return lblMessage;
	}
	public void setLblMessage(JLabel lblMessage) {
		this.lblMessage = lblMessage;
	}
	
}

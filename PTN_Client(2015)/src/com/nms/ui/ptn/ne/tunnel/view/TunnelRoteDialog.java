﻿package com.nms.ui.ptn.ne.tunnel.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.nms.db.bean.ptn.SiteRoate;
import com.nms.db.bean.ptn.path.protect.LoopProRotate;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
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
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 *  tunnel倒换对话框（网络层，包括 单网元）
 * @author sy
 *
 */
public class TunnelRoteDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *   false 单网元
	 *   true 网络层
	 */
	private boolean flag;
	private Tunnel tunnel;// 选中的 tunnel对象
	private WURoatePanel whAPanel;//武汉 tunnel  A倒换
	private CXRoatePanel cxAPanel;//陈晓 tunnel  A倒换
	private WURoatePanel whZPanel;//武汉 tunnel  Z倒换
	private CXRoatePanel cxZPanel;//陈晓 tunnel Z 倒换
	private SiteRoate aRoate;//A端 倒换对象
	private SiteRoate zRoate;//Z端 倒换对象
	/**
	 *  新建一个新的tunnel倒换对话框
	 * @param flag
	 *   判断是网络层还是单网元
	 *     true  网络层 tunnel倒换
	 *     false  单网元 tunnel倒换
	 * @param tunnel
	 *     tunnel 对象
	 */
	public TunnelRoteDialog(boolean flag,Tunnel tunnel){
		this.tunnel=tunnel;
		this.flag=flag;
		init();
	}
	/*
	 * 初始化
	 */
	private void init(){
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_TUNNEL_PROTECT_ROTATE));
		initComponent();
		setBussinessLayout();
		addListener();
		if(flag){
		if(ResourceUtil.language.equals("zh_CN")){
			UiUtil.showWindow(this, 600, 300);
		}else{
			UiUtil.showWindow(this, 860, 300);
		}
		}else {
			UiUtil.showWindow(this, 450, 300);
		}		
	}
	/**
	 * 实例化组件
	 */
	private void initComponent(){
		this.mainPanel=new JPanel();
		this.panelButton=new javax.swing.JPanel();
		this.whAPanel=new WURoatePanel(this.tunnel,true);		
		this.cxAPanel=new CXRoatePanel(this.tunnel, true);	
		this.whZPanel=new WURoatePanel(this.tunnel,false);		
		this.cxZPanel=new CXRoatePanel(this.tunnel, false);
		/**
		 * 修改tunnel 倒换
		 *  网络层tunnel 时
		 *   需要重新命名A端Panel()的内窗体名
		 */
		if(this.flag){
			this.whAPanel.getContentPanel().setBorder(
					BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_A_PORT)+ " "+ ResourceUtil
									.srcStr(StringKeysTitle.TIT_EXTERNAL_ORDER)));
			this.cxAPanel.getPanelOrder().setBorder(
					BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_A_PORT)+ " "+ 
							(ResourceUtil.srcStr(StringKeysTitle.TIT_EXTERNAL_ORDER))));
		}
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false,RootFactory.CORE_MANAGE);
		this.btnSave.setActionCommand(this.flag+"");
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
	}
	/**
	 *  主界面布局（分网络层和单网元）
	 */
	private void setBussinessLayout(){
		this.add(this.mainPanel);
		GridBagLayout componentLayout = new GridBagLayout();
		if(this.flag){
			//网络层布局加载2个Panel
			componentLayout.columnWidths = new int[] { 200,200};
			componentLayout.columnWeights = new double[] { 0.1 ,0.1};
		}else{
			//单网元布局，只加载1 个panle
			componentLayout.columnWidths = new int[] { 120};
			componentLayout.columnWeights = new double[] { 0.1 };
		}		
		componentLayout.rowHeights = new int[] { 10,110, 10};
		componentLayout.rowWeights = new double[] {0, 0.2, 0.0 };
		this.mainPanel.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		addComponent(this.mainPanel, this.panelButton, 0, 2, 0, 0, 2, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComPanel(c);
		setLayoutButton();
	}
	/***
	 * 按钮布局
	 */
	private void setLayoutButton() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 320, 40,40 };
		componentLayout.columnWeights = new double[] { 0.1, 0, 0 };
		componentLayout.rowHeights = new int[] { 20 };
		componentLayout.rowWeights = new double[] {0};
		this.panelButton.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();		
		addComponent(this.panelButton, btnSave, 1, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
		addComponent(this.panelButton, btnCanel, 2, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), GridBagConstraints.CENTER, c);
	}
	/**
	 * 向主面板中添加Panel
	 * 根据 A,Z 端网元id，确认 是 武汉Panel还是陈晓Panel
	 * @param c
	 *    承载主界面布局，添加组件
	 */
	private void addComPanel(GridBagConstraints c){
		SiteService_MB siteServiceMB = null;
		try {
			siteServiceMB=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			/**
			 * 先判断A 端 网元id,
			 *   >0 : 可能是单网元（新建tunnel的类型为入口），也可能是网络层 ，正常提取数据
			 *   不大于0 ：（即==0），则必然为单网元，（新建tunnel的类型为出口）
			 */
			if(this.tunnel.getASiteId() == ConstantUtil.siteId || flag){
				/**
				 * 判断A 端网元Id，添加陈晓panel
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteServiceMB.getManufacturer(this.tunnel.getASiteId())) {
					addComponent(this.mainPanel, this.cxAPanel, 0, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				} else {
					/*
					 * 判断A端网元id,添加武汉Panel
					 */
					addComponent(this.mainPanel, this.whAPanel, 0, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				}
			}else{// a端网元id为0，则为单网元，并且，网元ID 在Z端
				/**
				 * 判断A 端网元Id，添加陈晓panel
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteServiceMB.getManufacturer(this.tunnel.getZSiteId())) {
					addComponent(this.mainPanel, this.cxZPanel, 0, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				} else {
					/*
					 * 判断A端网元id,添加武汉Panel
					 */
					addComponent(this.mainPanel, this.whZPanel, 0, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				}
			}
			
			//网络层，需要添加Z端倒换界面
			if(flag){				
				/**
				 * 判断Z 端网元是武汉还是陈晓，并且重新命名子窗体名称
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteServiceMB.getManufacturer(this.tunnel.getZSiteId())) {
					cxZPanel.getPanelOrder().setBorder(
									BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_PORT)+ " "+ (ResourceUtil.srcStr(StringKeysTitle.TIT_EXTERNAL_ORDER))));
					addComponent(this.mainPanel, this.cxZPanel, 1, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				} else {
					//添加Z端武汉倒换界面，并且重新命名子窗体名称
					whZPanel.getContentPanel().setBorder(
									BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_PORT)+ " "+ ResourceUtil
													.srcStr(StringKeysTitle.TIT_EXTERNAL_ORDER)));
					addComponent(this.mainPanel, this.whZPanel, 1, 1, 0.1, 0.1, 1,1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),GridBagConstraints.CENTER, c);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteServiceMB);
		}
	}
	/**
	 * 添加事件
	 */
	private void addListener(){
		/*
		 * 保存事件
		 */
		this.btnSave.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction(e.getActionCommand());
				TunnelRoteDialog.this.dispose();			
			}
			
			@Override
			public boolean checking() {
				return false;
			}
		});			
		//取消事件	
		this.btnCanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TunnelRoteDialog.this.dispose();
				
			}
		});
	}

	/**
	 * 保存按钮
	 *   提取数据
	 */
	private void saveAction(String command){
		String result="";
		DispatchUtil protectRotateDispatch = null;
		List<SiteRoate> siteRoateList=null;
		SiteService_MB siteServiceMB = null;
		try {
			siteServiceMB=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			protectRotateDispatch = new DispatchUtil(RmiKeys.RMI_PROTECTROTATE);
			siteRoateList=new ArrayList<SiteRoate>();
			/**
			 * 先判断A 端 网元id,
			 *   >0 : 可能是单网元（新建tunnel的类型为入口），也可能是网络层 ，正常提取数据
			 *   不大于0 ：（即==0），则必然为单网元，（新建tunnel的类型为出口）
			 */
			if((this.tunnel.getASiteId()==ConstantUtil.siteId) || command.equals("true")){
				/**
				 * 判断A 端网元是武汉还是陈晓
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteServiceMB.getManufacturer(this.tunnel.getASiteId())) {
					cxAction(this.cxAPanel,false);
				} else {
					whAction(this.whAPanel,false);
					
				}
			}else{// a端网元id为0，则为单网元，并且，网元ID 在Z端
				/**
				 * 判断Z 端网元是武汉还是陈晓
				 */
				if (EManufacturer.CHENXIAO.getValue() == siteServiceMB.getManufacturer(this.tunnel.getZSiteId())) {
					cxAction(this.cxZPanel,true);
				} else {
					whAction(this.whZPanel,true);
				}
			}
			
			/**
			 * 判断Z 端网元是武汉还是陈晓
			 */
			
			if(flag){
				if (EManufacturer.CHENXIAO.getValue() == siteServiceMB.getManufacturer(this.tunnel.getZSiteId())) {
					cxAction(this.cxZPanel,true);
				} else {
					whAction(this.whZPanel,true);
				}
			}
			if(this.aRoate!=null){
				siteRoateList.add(this.aRoate);
			}
			if(this.zRoate!=null){
				siteRoateList.add(this.zRoate);
			}
			result=protectRotateDispatch.excuteUpdate(siteRoateList);
			LoopProRotate log = new LoopProRotate();
			if(this.tunnel.getaSiteId() > 0 && EManufacturer.WUHAN.getValue() == siteServiceMB.getManufacturer(this.tunnel.getaSiteId())){
				log.setActionType(aRoate.getRoate());
				log.setAllRotateLog(aRoate.getSiteRoate()==1?true:false);
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.TUNNELROTATE.getValue(), result, 
						null, log, this.tunnel.getaSiteId(), this.tunnel.getTunnelName(), "tunnelRotate");
			}
			if(this.tunnel.getzSiteId() > 0 && EManufacturer.WUHAN.getValue() == siteServiceMB.getManufacturer(this.tunnel.getzSiteId())){
				log.setActionType(zRoate.getRoate());
				log.setAllRotateLog(zRoate.getSiteRoate()==1?true:false);
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.TUNNELROTATE.getValue(), result, 
						null, log, this.tunnel.getzSiteId(), this.tunnel.getTunnelName(), "tunnelRotate");
			}
			DialogBoxUtil.succeedDialog(null, result);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteServiceMB);
		}
	}
	/**
	 *  陈晓   倒换 ，提取数据
	 * @author kk
	 *    修改  sy
	 * @param   cxPanel
	 *   陈晓  Panel
	 * @param node
	 *    false   A端
	 *   true   z端
	 * @return  result
	 * 陈晓倒换操作的 结果
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private String cxAction(CXRoatePanel cxPanel,boolean node) {
		
		Enumeration<AbstractButton> elements = null;
		JRadioButton radioButton = null;
		String result = null;
		try {
			// 遍历所有radiobutton 获取选中的button
			elements = cxPanel.getButtonGroup().getElements();
			while (elements.hasMoreElements()) {
				radioButton = (JRadioButton) elements.nextElement();
				if (radioButton.isSelected()) {
					break;
				}
			}
			int ratate=0;
			if (null != radioButton) {
				//倒换界面有选中倒换命令
				ratate = Integer.parseInt(radioButton.getName());
				//网络层			
				if(node){
					//Z 端
					this.zRoate=new SiteRoate();
					this.zRoate.setRoate(ratate);
					this.zRoate.setType("tunnel");
					this.zRoate.setTypeId(this.tunnel.getProtectTunnelId());
					this.zRoate.setSiteId(this.tunnel.getZSiteId());
					this.zRoate.setTunnel(this.tunnel);
				}else{
					// A端
					this.aRoate=new SiteRoate();
					this.aRoate.setRoate(ratate);
					this.aRoate.setType("tunnel");
					this.aRoate.setTypeId(this.tunnel.getProtectTunnelId());
					this.aRoate.setSiteId(this.tunnel.getASiteId());
					this.aRoate.setTunnel(this.tunnel);
					
				}					
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			elements = null;
			radioButton = null;
		}
		return result;

	}
	/**
	 * 武汉倒换：提取数据
	 * @param whPanel
	 * @param node
	 *   false   A端
	 *   true Z 端
	 * @return result
	 *  返回 ，武汉倒换的操作结果
	 */
	private String whAction(WURoatePanel whPanel,boolean node) {		
		Enumeration<AbstractButton> elements = null;
		JRadioButton radioButton = null;
		String result = "";
		try {
			// 遍历所有radiobutton 获取选中的button
			elements = whPanel.getButtonGroup().getElements();
			while (elements.hasMoreElements()) {
				radioButton = (JRadioButton) elements.nextElement();
				if (radioButton.isSelected()) {
					break;
				}
			}
			int ratate=0;
			if (null != radioButton) {
				//A  端
				ratate = Integer.parseInt(radioButton.getName());
				//网络层
				
				//   Z  端
				if(node){
					this.zRoate=new SiteRoate();
					this.zRoate.setRoate(ratate);
					this.zRoate.setType("tunnel");
					this.zRoate.setSiteRoate(whPanel.getSiteRorate().isSelected()==true?1:0);
					this.zRoate.setTypeId(this.tunnel.getProtectTunnelId());
					this.zRoate.setSiteId(this.tunnel.getZSiteId());
					this.zRoate.setTunnel(this.tunnel);
				}else{//A 端
					this.aRoate=new SiteRoate();
					this.aRoate.setRoate(ratate);
					this.aRoate.setType("tunnel");
					this.aRoate.setSiteRoate(whPanel.getSiteRorate().isSelected()==true?1:0);
					this.aRoate.setTypeId(this.tunnel.getProtectTunnelId());
					this.aRoate.setSiteId(this.tunnel.getASiteId());
					this.aRoate.setTunnel(this.tunnel);
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			elements = null;
			radioButton = null;
		}
		return result;
	}
	private JPanel mainPanel;//主界面面板
	private JPanel panelButton;// 按钮 面板
	private PtnButton btnSave;//确认
	private JButton btnCanel;//取消
}

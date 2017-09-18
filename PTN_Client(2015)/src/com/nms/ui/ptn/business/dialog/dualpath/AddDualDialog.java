﻿/*
 * AddPWDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.business.dialog.dualpath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.PopupMenuGenerator;
import twaver.TUIManager;
import twaver.TView;
import twaver.TWaverConst;
import twaver.network.TNetwork;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.VplsInfo;
import com.nms.db.bean.ptn.path.protect.PwProtect;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.AutoNamingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.TopoAttachment;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.dialog.tunnel.TunnelTopoPanel;
import com.nms.ui.ptn.business.dual.DualBusinessPanel;
import com.nms.ui.ptn.ne.ac.view.AcListDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 
 * @author pc
 */
public class AddDualDialog extends PtnDialog {

	/**
	 * 
	 * @since Ver 1.1
	 */

	private static final long serialVersionUID = -4639610687746114209L;
	int rootId = 0;
	private DualBusinessPanel dualBusinessPanel;
	// List<Tunnel> rootTunnelList = new ArrayList<Tunnel>();
	List<Node> selBranchNodeList = new ArrayList<Node>();
	ControlKeyValue rootAcAndSiteInst = null;
	private ViewDataTable<AcPortInfo> protectAcTable; // 选择的辅节点actable
	private ViewDataTable<PwInfo> protectPwInfoTable; // 选择的辅节点pwtable
	private ViewDataTable<AcPortInfo> mainAcTable; // 选择的主节点actable
	private ViewDataTable<PwInfo> mainPwInfoTable; // 选择的主节点pwtable
	private final String PROTECTACTABLENAME = "selectAcList";
	private final String PROTECTPWTABLENAME = "selectPwList";
	private JScrollPane jscrollPane_ac;
	private JScrollPane jscrollPane_pw;
	private JScrollPane jscrollPane_protectAc;
	private JScrollPane jscrollPane_protectPw;
	private TNetwork network = null;
	private TunnelTopoPanel tunnelTopoPanel=null;
	/** Creates new form AddPWDialog */
	public AddDualDialog(DualBusinessPanel jPanel1, boolean modal) {

		try {
			this.setModal(modal);
			this.dualBusinessPanel = jPanel1;
			initComponents();
			setLayout();
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_DUAL));
			this.tunnelTopoPanel=new TunnelTopoPanel();
			jSplitPane1.setRightComponent(tunnelTopoPanel);
			network= tunnelTopoPanel.getNetWork();
			setBtnListeners();
			clientComboxData(this.clientComboBox);
			showTunnel();
			UiUtil.showWindow(this, 1200, 700);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setBtnListeners() {
		/**
		 * 自动命名事件
		 */
		jButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonActionPerformed(evt);
			}
		});
		
		oKButton.addActionListener(new MyActionListener() {

			@Override
			public boolean checking() {
				return true;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				oKButtonActionPerformed(e);
				
			}
		});
		
		network.addElementClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Element element = (Element) e.getSource();
				if(element!=null&&element instanceof Link){
					if(element.getUserObject()!=null&&element.getBusinessObject()==null){
						TUIManager.registerAttachment("SegmenttopoTitle", TopoAttachment.class,1, (int) element.getX(), (int) element.getY());
						PwInfo pwinfo =  (PwInfo)element.getUserObject();
						element.setBusinessObject(pwinfo.getPwName());
						element.addAttachment("SegmenttopoTitle");
					}else{
						element.removeAttachment("SegmenttopoTitle");
						element.setBusinessObject(null);
					}    
				}
			}
		});
	}

	public void setAText(String text) {
		this.rootTextField.setText(text);
	}

	/**
	 * 加载拓扑图时 获取pw集合
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<PwInfo> getPwList() throws Exception {
		List<PwInfo> pwinfoList = null;
		PwInfo pwInfo = null;
		PwInfoService_MB pwServiceMB = null;
		List<PwInfo> pwinfoList_result = null;
		SiteService_MB siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
		try {
			pwinfoList_result=new ArrayList<PwInfo>();
			pwServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwInfo = new PwInfo();
			pwInfo.setType(EPwType.ETH);
			pwinfoList = pwServiceMB.select(pwInfo); // 查询所有pw
			for (PwInfo info : pwinfoList) {
				// pw不被使用,且不是多段pw(协议暂不支持带多段的双归)
				if (info.getRelatedServiceId() == 0 && info.getPwStatus() == 1 && info.getTunnelId()>0) {
					//如果AZ两个网元都为武汉设备。才加载到拓扑中
					if(siteServiceMB.getManufacturer(info.getASiteId()) == EManufacturer.WUHAN.getValue() && siteServiceMB.getManufacturer(info.getZSiteId()) == EManufacturer.WUHAN.getValue()){
						pwinfoList_result.add(info);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteServiceMB);
			UiUtil.closeService_MB(pwServiceMB);
		}
		return pwinfoList_result;

	}

//	/**
//	 * 验证pwInfo所在tunnel是否有1:1保护
//	 * @param pwInfo
//	 * @return
//	 */
//	private boolean tunnelHasprotect(PwInfo pwInfo){
//		TunnelService_MB tunnelServiceMB = null;
//		Tunnel tunnel = null;
//		try {
//			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
//			tunnel = new Tunnel();
//			tunnel.setTunnelId(pwInfo.getTunnelId());
//			List<Tunnel> tunnels = tunnelServiceMB.select(tunnel);
//			if( tunnels != null && tunnels.size()>0){
//				tunnel = tunnels.get(0);
//				if(tunnel.getProtectTunnelId()>0){
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		}finally{
//			tunnel=null;
//			UiUtil.closeService_MB(tunnelServiceMB);
//		}
//		return false;
//		
//	}
	/**
	 * 将有pw的隧道显示在拓扑图中
	 * 
	 * @param jComboBox
	 * @param defaultValue
	 * @throws Exception
	 */
	public void showTunnel() throws Exception {

		// 加载拓扑
		final List<PwInfo> pwInfoList = this.getPwList();
		tunnelTopoPanel.boxDataByPws(pwInfoList);
		network =tunnelTopoPanel.getNetWork();
		// 设置拓扑的自动布局
		network.doLayout(TWaverConst.LAYOUT_CIRCULAR);
		this.setMenu();
	}

	/**
	 * 加载etree的菜单
	 */
	private void setMenu() {
		TNetwork network = tunnelTopoPanel.getNetWork();

		// 设置拓扑的右键菜单
		network.setPopupMenuGenerator(new PopupMenuGenerator() {
			@Override
			public JPopupMenu generate(TView tview, MouseEvent mouseEvent) {
				JPopupMenu menu = new JPopupMenu();
				AddDualMenu addEtreeMenu = new AddDualMenu(AddDualDialog.this, tview);
				Link link = null;
				try {
					if (!tview.getDataBox().getSelectionModel().isEmpty()) {
						final Element element = tview.getDataBox().getLastSelectedElement();
						if (element instanceof Node) {
							// 如果此node没有设置根或者叶子，加载菜单为 设置根节点、设置叶子节点
							if (element.getBusinessObject() == null || "".equals(element.getBusinessObject().toString())) {
								if(AddDualDialog.this.getProtectAcTable().getAllElement().size()==0){
									menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_TARGET_PROTECT, element));
								}
								if(AddDualDialog.this.getMainAcTable().getAllElement() .size()==0){
									menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_TARGET_MAIN, element));
								}
								if("".equals(AddDualDialog.this.getRootTextField().getText())){
									menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_SOURCE, element));
								}
								
							} else { // 否则加载取消设置、选择端口菜单
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_CANEL_CONFIG, element));
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_PORT, element));
							}

						} else if (element instanceof Link) {
							// 如果是link 并且颜色是绿色 说明没有被选中，加载 设置路径菜单
							link = (Link) element;
							if (link.getLinkColor() == Color.GREEN && canSetPath(link)) {
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_PATH, element));
							} else {
								// 否则加载取消设置菜单
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_CANEL_CONFIG, element));
							}
						}

					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
				return menu;
			}

		});
	}

	/**
	 * 检测link能否有设置路径的右键菜单
	 * @param link
	 * @return
	 */
	private boolean canSetPath(Link link){
		Node nodeFrom = link.getFrom();
		Node nodeTo = link.getTo();
		int root = 0;
		if("".equals(nodeFrom.getBusinessObject()) || "".equals(nodeTo.getBusinessObject())){
			return false;
		}
		if("root".equals(nodeFrom.getBusinessObject()+"") || "root".equals(nodeTo.getBusinessObject()+"")){
			root = 1;
		}
		if(root == 1){
			return true;
		}
		return false;
	}
	protected void clearFields() {
		rootTextField.setText("");
		rootAcAndSiteInst = null;
		selBranchNodeList.removeAll(selBranchNodeList);

	}

	protected void setport(AcListDialog acListDialog, Element siteElement) throws Exception {
		AcPortInfo acPortInfo = null;
		SiteInst siteInst = null;
		List<AcPortInfo> acPortList = null;
		try {
			acPortInfo = acListDialog.getAcPortInfo();

			if (((Node) siteElement).getBusinessObject().equals("root")) {
				siteInst = (SiteInst) siteElement.getUserObject();
				if (null != acPortInfo) {
					rootAcAndSiteInst = new ControlKeyValue(acPortInfo.getId() + "", "", siteInst);
					rootTextField.setText(acPortInfo.getName());
				}
			} else if(((Node) siteElement).getBusinessObject().equals("protect")){
				if (null != acPortInfo) {
					// 如果有同网元的端口，清除掉。添加新的
					acPortList = new ArrayList<AcPortInfo>();
					for (AcPortInfo acPortInfo_table : this.protectAcTable.getAllElement()) {
						if (acPortInfo.getSiteId() != acPortInfo_table.getSiteId()) {
							acPortList.add(acPortInfo_table);
						}
					}
					acPortList.add(acPortInfo);
					this.protectAcTable.clear();
					this.protectAcTable.initData(acPortList);
				}
			}else if(((Node) siteElement).getBusinessObject().equals("main")){
				if (null != acPortInfo) {
					// 如果有同网元的端口，清除掉。添加新的
					acPortList = new ArrayList<AcPortInfo>();
					for (AcPortInfo acPortInfo_table : this.mainAcTable.getAllElement()) {
						if (acPortInfo.getSiteId() != acPortInfo_table.getSiteId()) {
							acPortList.add(acPortInfo_table);
						}
					}
					acPortList.add(acPortInfo);
					this.mainAcTable.clear();
					this.mainAcTable.initData(acPortList);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			acPortInfo = null;
			siteInst = null;
			acPortList = null;
		}

	}

	// 自动命名事件
	private void jButtonActionPerformed(java.awt.event.ActionEvent evt) {

		try {
			DualInfo dualInfo = new DualInfo();
			dualInfo.setIsSingle(0);
			dualInfo.setaSiteId(ConstantUtil.siteId);
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			String autoNaming = (String) autoNamingUtil.autoNaming(dualInfo, null, null);
			etreeNameTextField.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void oKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		DualInfo dualInfoProtect = null;
		DualInfo dualInfoMain = null;
		List<DualInfo> dualInfoList = null;
		DispatchUtil dualDisPatch = null;
		List<PwInfo> protectPwInfoList = null;
		List<AcPortInfo> protectAcList = null;
		List<PwInfo> mainPwInfoList = null;
		List<AcPortInfo> mainAcList = null;
		TunnelService_MB tunnelServiceMB = null;
		String beforeName = null;
		SiteService_MB siteService = null;
		AcPortInfoService_MB acService = null;
		try {
			protectPwInfoList = this.protectPwInfoTable.getAllElement();
			protectAcList = this.protectAcTable.getAllElement();
			mainPwInfoList = this.mainPwInfoTable.getAllElement();
			mainAcList = this.mainAcTable.getAllElement();
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			// 验证名字是否重复
			VerifyNameUtil verifyNameUtil = new VerifyNameUtil();
			if (verifyNameUtil.verifyName(EServiceType.DUAL.getValue(), this.etreeNameTextField.getText().trim(), beforeName)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return;
			}
			// 验证名字是否选择了根节点
			if (this.rootAcAndSiteInst == null) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_ROOT_PORT));
				return;
			}

			// 验证是否选择了目的辅节点
			if (!(protectAcList.size() == 1)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_STAND_PROTECT));
				return;
			}
			
			// 验证是否选择了目的主节点
			if (!(mainAcList.size() == 1)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_STAND_MAIN));
				return;
			}

			// 验证叶子ac端口和pw的个数是否相同
			if (!(protectPwInfoList.size() == 1 && mainPwInfoList.size()==1)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PW_ERROR));
				return;
			}

			int rootAcId = Integer.valueOf(this.rootAcAndSiteInst.getId());
			SiteInst rootSiteInst = (SiteInst) this.rootAcAndSiteInst.getObject();
			//获取客户名称
			ControlKeyValue clientCKV = (ControlKeyValue) this.clientComboBox.getSelectedItem();
			
			//pw保护信息
			PwProtect pwProtect = new PwProtect();
			pwProtect.setSiteId(rootSiteInst.getSite_Inst_Id());
			pwProtect.setBackType(isBackJCheckBox.isSelected()?0:1);
			pwProtect.setDelayTime(Integer.parseInt(txtDelayTime.getText()));
			pwProtect.setMainPwId(mainPwInfoList.get(0).getPwId());
			pwProtect.setMainTunnelId(mainPwInfoList.get(0).getTunnelId());
			pwProtect.setStandPwId(protectPwInfoList.get(0).getPwId());
			pwProtect.setStandTunnelId(protectPwInfoList.get(0).getTunnelId());
			dualInfoList = new ArrayList<DualInfo>();
			
			dualInfoMain = new DualInfo();
			dualInfoMain.setaAcId(rootAcId);
			dualInfoMain.setPwId(mainPwInfoList.get(0).getPwId());
			dualInfoMain.setActiveStatus(activeCheckBox.isSelected() ? 1 : 0);
			dualInfoMain.setRootSite(rootSiteInst.getSite_Inst_Id());
			dualInfoMain.setzAcId(mainAcList.get(0).getId());
			dualInfoMain.setBranchMainSite(mainAcList.get(0).getSiteId());
			dualInfoMain.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
			dualInfoMain.setCreateUser(ConstantUtil.user.getUser_Name());
			dualInfoMain.setServiceType(EServiceType.DUAL.getValue());
			dualInfoMain.setName(etreeNameTextField.getText());
			if (!"".equals(clientCKV.getId())) {
				dualInfoMain.setClientId(Integer.parseInt(clientCKV.getId()));
			}
			dualInfoMain.setPwProtect(pwProtect);
			dualInfoList.add(dualInfoMain);
			
			dualInfoProtect = new DualInfo();
			dualInfoProtect.setaAcId(rootAcId);
			dualInfoProtect.setPwId(protectPwInfoList.get(0).getPwId());
			dualInfoProtect.setActiveStatus(activeCheckBox.isSelected() ? 1 : 0);
			dualInfoProtect.setRootSite(rootSiteInst.getSite_Inst_Id());
			dualInfoProtect.setzAcId(protectAcList.get(0).getId());
			dualInfoProtect.setBranchProtectSite(protectAcList.get(0).getSiteId());
			dualInfoProtect.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
			dualInfoProtect.setCreateUser(ConstantUtil.user.getUser_Name());
			dualInfoProtect.setServiceType(EServiceType.DUAL.getValue());
			dualInfoProtect.setName(etreeNameTextField.getText());
			if (!"".equals(clientCKV.getId())) {
				dualInfoProtect.setClientId(Integer.parseInt(clientCKV.getId()));
			}
			dualInfoList.add(dualInfoProtect);
			dualDisPatch = new DispatchUtil(RmiKeys.RMI_DUAL);
			String message = dualDisPatch.excuteInsert(dualInfoList);
			//添加日志记录
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			VplsInfo vpls_Log = new VplsInfo();
			vpls_Log.setVplsName(etreeNameTextField.getText());
			if (!"".equals(clientCKV.getId())) {
				dualInfoProtect.setClientId(Integer.parseInt(clientCKV.getId()));
				vpls_Log.setClientName(clientCKV.getName());
			}
			vpls_Log.setActiveStatus(dualInfoMain.getActiveStatus());
			DualInfo dual_Log = new DualInfo();
			vpls_Log.setDualInfo(dual_Log);
			PwProtect pwPro_Log = new PwProtect();
			dual_Log.setPwProtect(pwPro_Log);
			pwPro_Log.setDelayTime(pwProtect.getDelayTime());
			pwPro_Log.setBackType(isBackJCheckBox.isSelected()?1:0);
			dual_Log.setNode(null);
			dual_Log.setRootName(rootSiteInst.getCellId());
			dual_Log.setRootAcName(acService.selectById(rootAcId).getName());
			dual_Log.setBranchMainName(siteService.getSiteName(mainAcList.get(0).getSiteId()));
			dual_Log.setBranchMainAcName(mainAcList.get(0).getName());
			dual_Log.setBranchMainPwName(mainPwInfoList.get(0).getPwName());
			dual_Log.setBranchProtectName(siteService.getSiteName(protectAcList.get(0).getSiteId()));
			dual_Log.setBranchProtectAcName(protectAcList.get(0).getName());
			dual_Log.setBranchProtectPwName(protectPwInfoList.get(0).getPwName());
			//源节点
			AddOperateLog.insertOperLog(oKButton, EOperationLogType.DUALPROTECTINSERT.getValue(), message, 
					null, vpls_Log, rootSiteInst.getSite_Inst_Id(), vpls_Log.getVplsName(), "dual");
			//目的主节点
			AddOperateLog.insertOperLog(oKButton, EOperationLogType.DUALPROTECTINSERT.getValue(), message, 
					null, vpls_Log, mainAcList.get(0).getSiteId(), vpls_Log.getVplsName(), "dual");
			//目的辅节点
			AddOperateLog.insertOperLog(oKButton, EOperationLogType.DUALPROTECTINSERT.getValue(), message, 
					null, vpls_Log, protectAcList.get(0).getSiteId(), vpls_Log.getVplsName(), "dual");
			DialogBoxUtil.succeedDialog(this, message);
			this.dispose();
			this.dualBusinessPanel.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(acService);
		}
	}

	private void initComponents() throws Exception {

		this.lblMessage = new JLabel();
		oKButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.COREMODU,this);
		jButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel3 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		branchLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAIN_AC_LIST));
		rootLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_AC));
		rootTextField = new PtnTextField();
		rootTextField.setEditable(false);
		jLabel5 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		activeCheckBox = new javax.swing.JCheckBox();
		activeCheckBox.setSelected(true);
		pwlist = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAIN_PW_LIST));
		client = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_NAME));
		clientComboBox = new javax.swing.JComboBox();
		etreeNameTextField = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.oKButton, this);
		protectAcJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT_AC_LIST));
		protectPwJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT_PW_LIST));
		// pw和ac叶子端口的tabel
		this.protectAcTable = new ViewDataTable<AcPortInfo>(this.PROTECTACTABLENAME);
		this.protectPwInfoTable = new ViewDataTable<PwInfo>(this.PROTECTPWTABLENAME);

		this.protectAcTable.getTableHeader().setResizingAllowed(true);
		this.protectAcTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.protectAcTable.setTableHeaderPopupMenuFactory(null);
		this.protectAcTable.setTableBodyPopupMenuFactory(null);

		this.protectPwInfoTable.getTableHeader().setResizingAllowed(true);
		this.protectPwInfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.protectPwInfoTable.setTableHeaderPopupMenuFactory(null);
		this.protectPwInfoTable.setTableBodyPopupMenuFactory(null);
		
		this.mainAcTable = new ViewDataTable<AcPortInfo>(this.PROTECTACTABLENAME);
		this.mainPwInfoTable = new ViewDataTable<PwInfo>(this.PROTECTPWTABLENAME);

		this.mainAcTable.getTableHeader().setResizingAllowed(true);
		this.mainAcTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.mainAcTable.setTableHeaderPopupMenuFactory(null);
		this.mainAcTable.setTableBodyPopupMenuFactory(null);

		this.mainPwInfoTable.getTableHeader().setResizingAllowed(true);
		this.mainPwInfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.mainPwInfoTable.setTableHeaderPopupMenuFactory(null);
		this.mainPwInfoTable.setTableBodyPopupMenuFactory(null);

		jscrollPane_ac = new JScrollPane();
		jscrollPane_pw = new JScrollPane();
		jscrollPane_protectAc = new JScrollPane();
		jscrollPane_protectPw = new JScrollPane();
		jscrollPane_protectAc.setViewportView(this.protectAcTable);
		jscrollPane_protectPw.setViewportView(this.protectPwInfoTable);
		jscrollPane_ac.setViewportView(this.mainAcTable);
		jscrollPane_pw.setViewportView(this.mainPwInfoTable);
		
		lbldelayTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME));
		txtDelayTime = new PtnTextField(true, PtnTextField.TYPE_INT, 255, this.lblMessage,oKButton , this);
		txtDelayTime.setCheckingMaxValue(true);
		txtDelayTime.setCheckingMinValue(true);
		txtDelayTime.setMaxValue(255);
		txtDelayTime.setMinValue(0);
		txtDelayTime.setText("0");
		isBackJCheckBox = new JCheckBox();
		isBackJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BACK));
	}

	private void setLayout() {
		this.add(this.jSplitPane1);
		this.jPanel3.setPreferredSize(new Dimension(260, 700));
		this.jSplitPane1.setLeftComponent(this.jPanel3);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 160, 50 };
		layout.columnWeights = new double[] { 0, 0.1, 0 };
		layout.rowHeights = new int[] { 25, 30, 30, 30, 80, 80, 80, 80,30, 15, 30, 30, 10 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.jPanel3.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 80, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		this.jPanel3.add(this.lblMessage);

		/** 第一行 名称 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(jLabel2, c);
		this.jPanel3.add(jLabel2);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(etreeNameTextField, c);
		this.jPanel3.add(etreeNameTextField);
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.jButton, c);
		this.jPanel3.add(this.jButton);
		/** 第二行 根端口 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.rootLabel, c);
		this.jPanel3.add(this.rootLabel);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.rootTextField, c);
		this.jPanel3.add(this.rootTextField);

		/** 第三行 客户 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.client, c);
		this.jPanel3.add(this.client);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.clientComboBox, c);
		this.jPanel3.add(this.clientComboBox);

		/** 第4行 ac列表 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.branchLabel, c);
		this.jPanel3.add(this.branchLabel);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		layout.addLayoutComponent(this.jscrollPane_ac, c);
		this.jPanel3.add(this.jscrollPane_ac);
		/** 第5行 pw列表 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(this.pwlist, c);
		this.jPanel3.add(this.pwlist);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.jscrollPane_pw, c);
		this.jPanel3.add(this.jscrollPane_pw);
		
		/** 第6行 ac列表 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.protectAcJLabel, c);
		this.jPanel3.add(this.protectAcJLabel);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		layout.addLayoutComponent(this.jscrollPane_protectAc, c);
		this.jPanel3.add(this.jscrollPane_protectAc);
		/** 第7行 pw列表 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(this.protectPwJLabel, c);
		this.jPanel3.add(this.protectPwJLabel);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.jscrollPane_protectPw, c);
		this.jPanel3.add(this.jscrollPane_protectPw);
		

		/** 第8行 */
		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.isBackJLabel, c);
		this.jPanel3.add(this.isBackJLabel);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.isBackJCheckBox, c);
		this.jPanel3.add(this.isBackJCheckBox);
		
		/** 第9行 */
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.lbldelayTime, c);
		this.jPanel3.add(this.lbldelayTime);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.txtDelayTime, c);
		this.jPanel3.add(this.txtDelayTime);

		/** 第10行 确定按钮 空出一行 */
		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.jLabel5, c);
		this.jPanel3.add(this.jLabel5);
		c.gridx = 1;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.activeCheckBox, c);
		this.jPanel3.add(this.activeCheckBox);
		
		
		/** 第11行 确定按钮 空出一行 */
		c.gridx = 2;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 5, 5);
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.oKButton, c);
		this.jPanel3.add(this.oKButton);
	}

	/**
	 * 客户信息下拉列表
	 * 
	 * @param jComboBox1
	 */
	public void clientComboxData(JComboBox jComboBox1) {

		ClientService_MB service = null;
		List<Client> clientList = null;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) clientComboBox.getModel();
		try {
			service = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			clientList = service.refresh();
			defaultComboBoxModel.addElement(new ControlKeyValue("", "", null));
			for (Client client : clientList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(client.getId() + "", client.getName(), client));
			}
			clientComboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			clientList = null;

		}
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JCheckBox activeCheckBox;
	private javax.swing.JLabel branchLabel;
	private javax.swing.JButton jButton;// 自动命名
	private javax.swing.JTextField etreeNameTextField;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JSplitPane jSplitPane1;
	private PtnButton oKButton;
	private javax.swing.JLabel rootLabel;
	private javax.swing.JTextField rootTextField;
	private javax.swing.JLabel pwlist;
	private JLabel protectAcJLabel;
	private JLabel protectPwJLabel;
	// End of variables declaration//GEN-END:variables
	private JLabel lblMessage;
	private JLabel client;
	private JComboBox clientComboBox;

	private JCheckBox isBackJCheckBox;
	private JLabel isBackJLabel;
	private JLabel lbldelayTime; 
	private PtnTextField txtDelayTime;
	
	public JCheckBox getIsBackJCheckBox() {
		return isBackJCheckBox;
	}

	public void setIsBackJCheckBox(JCheckBox isBackJCheckBox) {
		this.isBackJCheckBox = isBackJCheckBox;
	}

	public JLabel getLbldelayTime() {
		return lbldelayTime;
	}

	public void setLbldelayTime(JLabel lbldelayTime) {
		this.lbldelayTime = lbldelayTime;
	}


	public PtnTextField getTxtDelayTime() {
		return txtDelayTime;
	}

	public void setTxtDelayTime(PtnTextField txtDelayTime) {
		this.txtDelayTime = txtDelayTime;
	}

	public JLabel getIsBackJLabel() {
		return isBackJLabel;
	}

	public void setIsBackJLabel(JLabel isBackJLabel) {
		this.isBackJLabel = isBackJLabel;
	}

	public int getRootId() {
		return rootId;
	}

	public void setRootId(int rootId) {
		this.rootId = rootId;
	}

	public ViewDataTable<AcPortInfo> getProtectAcTable() {
		return protectAcTable;
	}

	public void setProtectAcTable(ViewDataTable<AcPortInfo> protectAcTable) {
		this.protectAcTable = protectAcTable;
	}

	public ViewDataTable<PwInfo> getProtectPwInfoTable() {
		return protectPwInfoTable;
	}

	public void setProtectPwInfoTable(ViewDataTable<PwInfo> protectPwInfoTable) {
		this.protectPwInfoTable = protectPwInfoTable;
	}

	public ViewDataTable<AcPortInfo> getMainAcTable() {
		return mainAcTable;
	}

	public void setMainAcTable(ViewDataTable<AcPortInfo> mainAcTable) {
		this.mainAcTable = mainAcTable;
	}

	public ViewDataTable<PwInfo> getMainPwInfoTable() {
		return mainPwInfoTable;
	}

	public void setMainPwInfoTable(ViewDataTable<PwInfo> mainPwInfoTable) {
		this.mainPwInfoTable = mainPwInfoTable;
	}

	public List<Node> getSelBranchNodeList() {
		return selBranchNodeList;
	}

	public void setSelBranchNodeList(List<Node> selBranchNodeList) {
		this.selBranchNodeList = selBranchNodeList;
	}

	public ControlKeyValue getRootAcAndSiteInst() {
		return rootAcAndSiteInst;
	}

	public void setRootAcAndSiteInst(ControlKeyValue rootAcAndSiteInst) {
		this.rootAcAndSiteInst = rootAcAndSiteInst;
	}

	public javax.swing.JTextField getRootTextField() {
		return rootTextField;
	}

	public void setRootTextField(javax.swing.JTextField rootTextField) {
		this.rootTextField = rootTextField;
	}

}
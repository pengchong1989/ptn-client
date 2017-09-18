package com.nms.ui.ptn.ne.dual.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.path.protect.DualProtect;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.DualProtectService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 编辑双规保护信息对话框
 * 
 * @author dzy
 * 
 */
public class EditDualProtectDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8448797222173552873L;
	List<Tunnel> selectableTunnelList;  //可选隧道集合
	List<Tunnel> selectedTunnelList ;//已选隧道集合
	
	/**
	 * 创建一个新的实例
	 * @param dualProtect  
	 * @param dualProtectPanel
	 */
	public EditDualProtectDialog(DualProtect dualProtect,DualProtectPanel dualProtectPanel){
		try {
			this.setModal(true);
			this.dualProtect = dualProtect;
			this.dualProtectPanel=dualProtectPanel;
			initTunnelDate();
			initComponents();
			setLayout();
			initDate();
			this.addListener();
			UiUtil.showWindow(this, 650, 500);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 初始化控件
	 * @throws Exception 
	 */
	private void initComponents() throws Exception {
		if (null != dualProtect) {
			this.setTitle(ResourceUtil.srcStr(StringKeysOperaType.BTN_DUAL_UPDATE));
		} else {
			this.setTitle(ResourceUtil.srcStr(StringKeysOperaType.BTN_DUAL_INSERT));
		}
		mainPanel = new JPanel();
		basicPanel = new JPanel();
		chooseTunnelPanel = new JPanel();
		buttonPanel = new JPanel();
		jpanel = new JPanel(new BorderLayout());
		allTunnelPanel = new AllTunnelPanel(this.selectableTunnelList); //可选隧道面板
		selectedTunnelPanel = new SelectedTunnelPanel(this.selectedTunnelList); //已选隧道面板
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT_TYPE)); // 保护类型label
		this.lblModel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RECOVERY_MODE)); // 恢复模式label
		this.lblAps = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE)); // 使能APSlabel
		this.lblRole = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROLE)); // 角色label
		this.lblRotateWay = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATEWAY)); // 倒换方式label
		this.lblWaittime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME)); // 恢复等待时间label
		this.spinnerWaittime = new PtnSpinner(PtnSpinner.TYPE_WAITTIME);// 恢复等待时间控件
		this.cmbType = new JComboBox();// 保护类型下拉列表
		this.cmbModel = new JComboBox();// 恢复模式下拉列表
		this.chkAps = new JCheckBox();// 使能APS复选框
		this.cmbRotateWay = new JComboBox();
		this.cmbRole = new JComboBox();// 角色下拉列表
		this.lblLag = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LAGBEARING)); // lag端口label
		this.cmbLag = new JComboBox();// lag端口下拉列表
		chkBreakOver = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_BREAKOVER));
		chkRelevance = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_RELEVANCE));
		buttonGroup = new ButtonGroup();
		buttonGroup.add(chkBreakOver);
		buttonGroup.add(chkRelevance);
		chkBreakOver.setSelected(true);
		btnAdd = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD));
		btnRemove = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REMOVE));
		btnConfirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		btnCancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		if(null!=this.dualProtect){
			this.chkBreakOver.setEnabled(false);
			this.chkRelevance.setSelected(true);
		}
	}
	/**
	 * 初始化数据
	 */
	private void initDate() throws Exception {
		
		super.getComboBoxDataUtil().comboBoxData(this.cmbType, "MSPPROTECTTYPE");
		super.getComboBoxDataUtil().comboBoxData(this.cmbModel, "RECOVERYMODE");
		super.getComboBoxDataUtil().comboBoxData(this.cmbRole, "ROLE");
		super.getComboBoxDataUtil().comboBoxData(this.cmbRotateWay, "ROTATEWAY");
		this.initLagData(this.cmbLag);
		if(null!=dualProtect){
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbType,  dualProtect.getProtectType()+"");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbModel,  dualProtect.getRegainModel()+"");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbRole,  dualProtect.getRole()+"");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbRotateWay,  dualProtect.getRotateWay()+"");
			if(this.dualProtect.getApsEnable()==1){
				this.chkAps.setSelected(true);
			}else{
				this.chkAps.setSelected(false);
			}
			this.spinnerWaittime.setValue(this.dualProtect.getWaitTime());
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbLag,  dualProtect.getLagId()+"");
		}else{
			dualProtect = new DualProtect();
			this.dualProtect.setActiveStatus(EActiveStatus.ACTIVITY.getValue());
		}
	}
	/**
	 * 主布局
	 * @throws Exception
	 */
	private void setLayout() throws  Exception {
		//基本参数
		basicPanelSetLayOut();
		//tunnel 面板
		chooseTunnelPanelSetLayOut();
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 620};
		layout.columnWeights = new double[] { 0, 0 };
		layout.rowHeights = new int[] { 210,250 };
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1,0.1,0.1,0.6};
		mainPanel.setLayout(layout);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.anchor =GridBagConstraints.NORTHWEST;
		super.addComponent(this.mainPanel,basicPanel,0,0,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.mainPanel,chooseTunnelPanel,0,1,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(fl);
		buttonPanel.add(btnConfirm);
		buttonPanel.add(btnCancel);
		buttonPanel.setPreferredSize(new Dimension(600, 40));
		jpanel.add(mainPanel, BorderLayout.CENTER);
		jpanel.add(buttonPanel, BorderLayout.SOUTH);
		this.add(jpanel);

	}
	/**
	 * 基本参数面板 布局
	 */
	private void basicPanelSetLayOut() {
		this.basicPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_BASICPARAM)));
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 100,200,100,200 };
		layout.columnWeights = new double[] { 0, 0 };
		layout.rowHeights = new int[] { 30,30,30,30,20};
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1,0.1,0.1,0.6};
		basicPanel.setLayout(layout);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.anchor =GridBagConstraints.NORTHWEST;
		super.addComponent(this.basicPanel,this.lblType,0,0,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.cmbType,1,0,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.lblWaittime,2,0,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.spinnerWaittime,3,0,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		
		super.addComponent(this.basicPanel,this.lblModel,0,1,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.cmbModel,1,1,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.lblLag,2,1,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.cmbLag,3,1,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);

		super.addComponent(this.basicPanel,this.lblAps,0,2,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.chkAps,1,2,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.lblRotateWay,2,2,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.cmbRotateWay,3,2,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		
		super.addComponent(this.basicPanel,this.lblRole,0,3,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.basicPanel,this.cmbRole,1,3,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		
	}
	/**
	 * 选择tunnel面板 布局
	 */
	private void chooseTunnelPanelSetLayOut() {
		this.chooseTunnelPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_SELECT_BUSITUNNEL)));
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 240,70,240 };
		layout.columnWeights = new double[] { 0, 0 };
		layout.rowHeights = new int[] { 40,40,40,40,40,40 };
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1,0.1,0.1,0.6};
		chooseTunnelPanel.setLayout(layout);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.anchor =GridBagConstraints.NORTHWEST;
		//可选隧道
		super.addComponent(this.chooseTunnelPanel,this.allTunnelPanel,0,0,0,0,1,6,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		//隧道间的 按钮等
		super.addComponent(this.chooseTunnelPanel,this.chkBreakOver,1,1,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.chooseTunnelPanel,this.chkRelevance,1,2,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.chooseTunnelPanel,this.btnAdd,1,3,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		super.addComponent(this.chooseTunnelPanel,this.btnRemove,1,4,0,0,1,1,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		//已选隧道
		super.addComponent(this.chooseTunnelPanel,this.selectedTunnelPanel,2,0,0,0,1,6,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),GridBagConstraints.NORTHWEST,c);
		
	
	}
	
	/**
	 * 添加事件监听
	 */
	private void addListener() {
		this.btnConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnConfirmActionPerformed(e);
			}
		});
		this.btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancelActionPerformed(e);
			}
		});
		this.cmbModel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				regainModelActionPerformed(e);
			}
			
		});
		this.btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					btnAddActionPerformed(e);
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				}
			}
			
		});
		this.btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					btnRemoveActionPerformed(e);
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				}
			}
		});
	}
	/**
	 * 保存按钮事件
	 * @param e
	 */
	private void btnConfirmActionPerformed(ActionEvent e) {
		String result = null;
		DispatchUtil dualDispatch = null;
		try {
			//验证数据
			if(!btnConfirmChecking()){
				return;
			}
			// 给对象赋值
			setDualProtect();
			
			dualDispatch=new DispatchUtil(RmiKeys.RMI_DUALPROTECT);
			
			if(0==this.dualProtect.getId()){
				result = dualDispatch.excuteInsert(this.dualProtect);
				
			}else{
				result = dualDispatch.excuteUpdate(this.dualProtect);
			}
			DialogBoxUtil.succeedDialog(this.dualProtectPanel, result);
			this.dualProtectPanel.getController().refresh();
			this.dispose();
			//添加日志记录
			if(0==this.dualProtect.getId()){
				this.btnConfirm.setOperateKey(EOperationLogType.DUALPROTECTINSERT.getValue());
			}else{
				this.btnConfirm.setOperateKey(EOperationLogType.DUALPROTECTUPDATE.getValue());
			}
			if(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS).equals(result)){
				btnConfirm.setResult(1);
			}else{
				btnConfirm.setResult(0);
			}
		} catch (Exception e1) {
			ExceptionManage.dispose(e1,this.getClass());
		}
	}
	/**
	 * 给对象赋值
	 * @throws Exception
	 */
	private void setDualProtect() throws Exception {
		this.cmbType.getSelectedItem();
		this.dualProtect.setProtectType(Integer.parseInt(((ControlKeyValue)this.cmbType.getSelectedItem()).getId()));
		this.dualProtect.setRegainModel(Integer.parseInt(((ControlKeyValue)this.cmbModel.getSelectedItem()).getId()));
		this.dualProtect.setApsEnable(this.chkAps.isSelected()?1:0);
		this.dualProtect.setRole(Integer.parseInt(((ControlKeyValue)this.cmbRole.getSelectedItem()).getId()));
		this.dualProtect.setWaitTime(Integer.parseInt(this.spinnerWaittime.getValue().toString()));
		this.dualProtect.setLagId(Integer.parseInt(((ControlKeyValue)this.cmbLag.getSelectedItem()).getId()));
		this.dualProtect.setRotateWay(Integer.parseInt(((ControlKeyValue)this.cmbRotateWay.getSelectedItem()).getId()));
		this.dualProtect.setSiteId(ConstantUtil.siteId);
	}
	/**
	 * 验证
	 * @throws Exception
	 */
	private boolean btnConfirmChecking() throws Exception {
		boolean result = true;
		WrappingProtectService_MB  wrappingProtectService = null;
		List<LoopProtectInfo> loopProtectList ;
		LoopProtectInfo loopProtectInfoSel;
		try {
			loopProtectInfoSel = new LoopProtectInfo();
			loopProtectInfoSel.setSiteId(ConstantUtil.siteId );
			wrappingProtectService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			loopProtectList = wrappingProtectService.select(loopProtectInfoSel);
			if(null!=loopProtectList&&loopProtectList.size()>0){
				for(LoopProtectInfo loopProtectInfo:loopProtectList){
					if(this.dualProtect.getBreakoverTunnel().getAPortId()==loopProtectInfo.getEastPort()||
							this.dualProtect.getBreakoverTunnel().getAPortId()==loopProtectInfo.getWestPort()||
							this.dualProtect.getBreakoverTunnel().getZPortId()==loopProtectInfo.getEastPort()||
							this.dualProtect.getBreakoverTunnel().getZPortId()==loopProtectInfo.getWestPort()){
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_BEUSEDBYWRAP));
						result = false;
					}
				}
			}
			if(null==this.dualProtect.getBreakoverTunnel()||this.dualProtect.getBreakoverTunnel().getTunnelId()==0){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNELSELECT_FORDUAL));
				result = false;
				
			}
			if(null==(ControlKeyValue)this.cmbLag.getSelectedItem()){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSELAG));
				result = false;
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(wrappingProtectService);
		}
		return result;
	}

	/**
	 * 取消按钮时间
	 * @param e
	 */
	private void btnCancelActionPerformed(ActionEvent e) {
		try {
			this.dispose(); 
			this.dualProtectPanel.getController().refresh();
		} catch (Exception e1) {
			ExceptionManage.dispose(e1,this.getClass());
		}
		
	}
	/**
	 * 恢复模式时间监听
	 * @param e
	 */
	private void regainModelActionPerformed(ActionEvent e) {
		ControlKeyValue regainModel = (ControlKeyValue) this.cmbModel.getSelectedItem();
		Code code = (Code) regainModel.getObject();
		if("2".equals(code.getCodeValue())){
			this.spinnerWaittime.setEnabled(false);
		}else{
			this.spinnerWaittime.setEnabled(true);
		}
	}
	/**
	 * 初始化 lag的下拉列表
	 * @param comboBox
	 * @throws Exception
	 */
	private void initLagData(JComboBox comboBox) throws Exception {
		PortLagService_MB portLagServiceMB = null;
		DualProtectService_MB dualProtectServiceMB = null;
		List<PortLagInfo> portLagList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		PortLagInfo portLagInfo;
		List<DualProtect> list;
		DualProtect dualProtect;
		try {
			dualProtect = new DualProtect();
			dualProtect.setSiteId(ConstantUtil.siteId);
			list = new ArrayList<DualProtect>();
			defaultComboBoxModel = new DefaultComboBoxModel();
			portLagServiceMB = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
			dualProtectServiceMB = (DualProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALPROTECTSERVICE);
			portLagInfo = new PortLagInfo();
			portLagInfo.setSiteId(ConstantUtil.siteId);
			portLagInfo.setRole("UNI");
			portLagList = portLagServiceMB.selectByCondition(portLagInfo);
			list = dualProtectServiceMB.queryByCondition(dualProtect);
			for (PortLagInfo lagInfo : portLagList) {
				if(list.size()>0){
					for(DualProtect dualProtectForDual:list){
						if(lagInfo.getLagID()!=dualProtectForDual.getLagId()){
							defaultComboBoxModel.addElement(new ControlKeyValue(lagInfo.getId() + "", "" + "lag/" + lagInfo.getLagID(), lagInfo));
						}
					}
				}else{
					defaultComboBoxModel.addElement(new ControlKeyValue(lagInfo.getId() + "", "" + "lag/" + lagInfo.getLagID(), lagInfo));
				}
			}
			comboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portLagServiceMB);
			UiUtil.closeService_MB(dualProtectServiceMB);
		}
	}

	/**
	 * 添加按钮的监听事件
	 * @param e
	 * @throws Exception 
	 */
	protected void btnAddActionPerformed(ActionEvent e) throws Exception {
		if(null!=this.allTunnelPanel.getTable().getSelect()){
			//穿通
			if(this.chkBreakOver.isSelected()){
				this.dualProtect.setBreakoverTunnel(this.allTunnelPanel.getTable().getSelect());
				this.chkBreakOver.setEnabled(false);
				this.chkRelevance.setSelected(true);
			}
			//关联
			else{
				this.dualProtect.getRelevanceTunnelList().add(this.allTunnelPanel.getTable().getSelect());
				if(Integer.parseInt(this.allTunnelPanel.getTable().getSelect().getTunnelType())==UiUtil.getCodeByValue("PROTECTTYPE", "2").getId()){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_RELEVANCE_TUNNEL));
					return;
				}
			}
			this.selectableTunnelList.remove(this.allTunnelPanel.getTable().getSelect());
			this.selectedTunnelList.add(this.allTunnelPanel.getTable().getSelect());
			this.allTunnelPanel.clear();
			this.allTunnelPanel.initData(this.selectableTunnelList);
			this.selectedTunnelPanel.clear();
			this.selectedTunnelPanel.initData(this.selectedTunnelList);
		}
	}
	
	/**
	 * 移除按钮的监听事件
	 * @param e
	 * @throws Exception 
	 */
	protected void btnRemoveActionPerformed(ActionEvent e) throws Exception {
		if(null!= this.selectedTunnelPanel.getTable().getSelect()){
			//穿通
			if(this.dualProtect.getBreakoverTunnel().getTunnelId() == this.selectedTunnelPanel.getTable().getSelect().getTunnelId()){
				this.dualProtect.setBreakoverTunnel(null);
				this.chkBreakOver.setEnabled(true);
			}else{//关联
				this.dualProtect.getRelevanceTunnelList().remove(this.selectedTunnelPanel.getTable().getSelect());
			}
			this.selectableTunnelList.add(this.selectedTunnelPanel.getTable().getSelect());
			this.selectedTunnelList.remove(this.selectedTunnelPanel.getTable().getSelect());
			this.allTunnelPanel.clear();
			this.allTunnelPanel.initData(this.selectableTunnelList);
			this.selectedTunnelPanel.clear();
			this.selectedTunnelPanel.initData(this.selectedTunnelList);
		}
	}
	/**
	 * 初始化可选隧道的tunnel数据
	 * @throws Exception
	 */
	private void initTunnelDate() throws Exception {
		selectableTunnelList = new ArrayList<Tunnel>();  //可选隧道集合
		selectedTunnelList = new ArrayList<Tunnel>();//已选隧道集合
		//初始化可选tunnel面板数据
		List<PwInfo> pwList;
		List<Tunnel> tunnelList;
		TunnelService_MB tunnelServiceMB = null;
		PwInfoService_MB pwInfoServiceMB = null;
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			pwInfoServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			tunnelList = tunnelServiceMB.selectNodesBySiteId(ConstantUtil.siteId);
			pwList = pwInfoServiceMB.queryBySiteId(ConstantUtil.siteId);
			Map<Integer,PwInfo> map = new HashMap<Integer,PwInfo>();
			Map<Integer,Tunnel> relevanceTunnelMap = new HashMap<Integer,Tunnel>();
			for (PwInfo pwInfo : pwList) {
				map.put(pwInfo.getTunnelId(), pwInfo);
			}
			if(null!=this.dualProtect&&this.dualProtect.getRelevanceTunnelList().size()>0){
				for(Tunnel relevanceTunnel:this.dualProtect.getRelevanceTunnelList()){
					relevanceTunnelMap.put(relevanceTunnel.getTunnelId(), relevanceTunnel);
				}
			}
			for(Tunnel tunnel:tunnelList){
				if(null==map.get(tunnel.getTunnelId())){
					if(null!=this.dualProtect){
						if(tunnel.getTunnelId()!=this.dualProtect.getBreakoverTunnel().getTunnelId()){
							if(null==relevanceTunnelMap.get(tunnel.getTunnelId())){
								this.selectableTunnelList.add(tunnel);
							}
						}
					}else{
						this.selectableTunnelList.add(tunnel);
					}
					
				}
			}
			
			//初始化已选tunnel面板数据
			if(null!=this.dualProtect){
				for(Tunnel tunnel:this.dualProtect.getRelevanceTunnelList()){
					this.selectedTunnelList.add(tunnel);
				}
				selectedTunnelList.add(this.dualProtect.getBreakoverTunnel());
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(tunnelServiceMB);
			UiUtil.closeService_MB(pwInfoServiceMB);
		}
	}
	private JPanel mainPanel; //主面板
	private JPanel basicPanel;  //基本面板
	private JPanel chooseTunnelPanel;	//选择tunnl面板
	private JPanel buttonPanel; //按钮面板
	private JPanel jpanel ; //底层面板
	private JLabel lblType; // 保护类型label
	private JComboBox cmbType;// 保护类型下拉列表
	private JLabel lblModel; // 恢复模式label
	private JComboBox cmbModel;// 恢复模式下拉列表
	private JLabel lblAps; // 使能APSlabel
	private JCheckBox chkAps;// 使能APS复选框
	private JLabel lblRole; // 角色label
	private JComboBox cmbRole;// 角色下拉列表
	private JLabel lblWaittime; // 恢复等待时间label
	private PtnSpinner spinnerWaittime;// 恢复等待时间控件
	private JLabel lblRotateWay; // 延迟时间label
	private JComboBox cmbRotateWay;// 延迟时间控件
	private JLabel lblLag; // lag端口label
	private JComboBox cmbLag;// lag端口下拉列表
	private JCheckBox chkBreakOver; //穿通 复选框
	private JCheckBox chkRelevance; //关联 复选框
	private ButtonGroup buttonGroup; //按钮组
	private PtnButton btnConfirm; //确认按钮
	private JButton btnCancel;	//取消按钮
	private JButton btnAdd; //添加按钮
	private JButton btnRemove; //移除按钮
	private DualProtect dualProtect; //dualProtect对象
	private DualProtectPanel dualProtectPanel; //双规保护panel
	private AllTunnelPanel allTunnelPanel; //可选隧道面板
	private SelectedTunnelPanel selectedTunnelPanel; //已选隧道面板
}

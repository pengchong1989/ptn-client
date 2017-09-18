﻿package com.nms.ui.ptn.ne.protect.loopProtect;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.Businessid;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.BusinessidService_MB;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
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
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;

/**
 * 添加单网元环保护
 * @author dzy
 *
 */
public class AddLoopNodeDialog extends PtnDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6308102424902925740L;
	private LoopProtectPanelSingle loopProtectPanel = null;
	private JLabel lblMessage;
	private PtnButton confirmBtn;
	private JButton canelBtn;
	private JLabel loopNodeId; //环节点 ID
	private JLabel apsenable;//aps使能PtnButton
	private JLabel waittime;//等待恢复时间
	private JLabel delaytime;//拖延时间
	private JLabel westPort;//西端口
	private JLabel eastPort;//东端口
	private JLabel westPortOppo;//西向对端节点ID
	private JLabel eastPortOppo;//东向对端节点ID
	private JLabel activeStatus;//激活状态
	private JComboBox westPortCombo;//西端口
	private JComboBox eastPortCombo;//东端口
	private JTextField loopNodeIdTxt;
	private PtnSpinner waittimeTxt;
	private PtnSpinner delaytimeTxt;
	private JTextField westPortOppoTxt;
	private JTextField eastPortOppoTxt;
	private JCheckBox apsenableTxt;
	private JCheckBox activeStatusCheck;
	private JPanel jPanel;
	private LoopProtectInfo loopProtectInfo;
	
	public AddLoopNodeDialog(boolean modal, LoopProtectPanelSingle loopProtectPanel) {

		try {
			this.setModal(modal);
			this.loopProtectPanel = loopProtectPanel;
			initComponents();
			setLayout();
			initData();
			addListener();
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_LOOPNODE));
			this.showWindow();
			apsenableTxt.setEnabled(false);
			if (null != loopProtectPanel) {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_LOOPNODE));
				this.loopProtectInfo = loopProtectPanel.getSelect();
				if (loopProtectInfo != null) {
					super.getComboBoxDataUtil().comboBoxSelect(this.westPortCombo, this.loopProtectInfo.getWestPort()+"");
					super.getComboBoxDataUtil().comboBoxSelect(this.eastPortCombo, this.loopProtectInfo.getEastPort()+"");
					this.westPortCombo.setEnabled(false);
					this.eastPortCombo.setEnabled(false);
					this.apsenableTxt.setEnabled(false);
					
					this.loopNodeIdTxt.setText(this.loopProtectInfo.getNodeId()+"");
					this.waittimeTxt.setValue(this.loopProtectInfo.getWaittime());
					this.delaytimeTxt.setValue(this.loopProtectInfo.getDelaytime());
					this.westPortOppoTxt.setText(this.loopProtectInfo.getWestNodeId()+"");
					this.eastPortOppoTxt.setText(this.loopProtectInfo.getEastNodeId()+"");
					if (this.loopProtectInfo.getApsenable() == 1) {
						this.apsenableTxt.setSelected(true);
					}
				}
			} else {
				this.loopProtectPanel = new LoopProtectPanelSingle();
				loopProtectInfo = new LoopProtectInfo();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	//初始化数据
	private void initData() {
		initWestPortCombo(this.westPortCombo);
		initEastPortCombo(this.eastPortCombo);
		
	}
	

	private void showWindow() {
		Dimension dimension = new Dimension(400, 490);
		this.setSize(dimension);
		this.setMinimumSize(dimension);
	}

	private void initComponents() throws Exception {

		lblMessage = new JLabel();
		jPanel = new JPanel();
		confirmBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		canelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.loopNodeId = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOOPNODEID));
		this.loopNodeIdTxt = new PtnTextField(true, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH,this.lblMessage ,this.confirmBtn, this);
		this.apsenable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE));
		this.apsenableTxt = new JCheckBox();
		this.delaytime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME));
		this.delaytimeTxt = new PtnSpinner(PtnSpinner.TYPE_DELAYTIME);
		this.waittime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
		this.waittimeTxt = new PtnSpinner(PtnSpinner.TYPE_WAITTIME);
		this.eastPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_EAST));
		this.eastPortCombo = new JComboBox();
		this.westPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_WEST));
		this.westPortCombo =  new JComboBox();
		this.westPortOppo = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WESTPORTOPPO));
		this.westPortOppoTxt = new PtnTextField(true, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH,this.lblMessage ,this.confirmBtn, this);
		this.eastPortOppo = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_EASTPORTOPPO));
		this.eastPortOppoTxt = new PtnTextField(true, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH,this.lblMessage ,this.confirmBtn, this);
		this.activeStatus = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IS_ACTIVATED));
		this.activeStatusCheck = new JCheckBox();
		this.activeStatusCheck.setSelected(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void addListener() {

		confirmBtn.addActionListener(new MyActionListener() {
			@Override
			public boolean checking() {
				
				return true;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				confirmBtnActionPerformed(e);
				
			}
		});

		canelBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				canelBtnActionPerformed(evt);
			}
		});
	}

	private void setLayout() {

		// setButtonLayout();

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 10, 80, 50 };
		layout.columnWeights = new double[] { 0, 0.1, 0 };
		layout.rowHeights = new int[] { 25, 40, 40, 40, 40, 40, 40, 40, 15, 15, 5, 30 };
		layout.rowWeights = new double[] { 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.1 };
		this.jPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		/** 第0行 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(lblMessage, c);
		this.jPanel.add(lblMessage);
		
		
		/** 第一行 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 15, 10);
		layout.setConstraints(this.loopNodeId, c);
		this.jPanel.add(this.loopNodeId);
		c.gridx = 1;
		c.gridwidth=2;
		layout.addLayoutComponent(loopNodeIdTxt, c);
		this.jPanel.add(loopNodeIdTxt);
		
	
		/** 第2行 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		layout.setConstraints(this.delaytime, c);
		this.jPanel.add(this.delaytime);
		c.gridx = 1;
		c.gridwidth=2;
		layout.addLayoutComponent(this.delaytimeTxt, c);
		this.jPanel.add(this.delaytimeTxt);
		
		/** 第3行 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		this.jPanel.add(waittime);
		layout.addLayoutComponent(this.waittime, c);
		c.gridx =1;
		c.gridwidth=2;
		layout.addLayoutComponent(waittimeTxt, c);
		this.jPanel.add(waittimeTxt);
		
		/** 第4行 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.westPort, c);
		this.jPanel.add(this.westPort);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.westPortCombo, c);
		this.jPanel.add(this.westPortCombo);
		
		/** 第6行 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.westPortOppo, c);
		this.jPanel.add(this.westPortOppo);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.westPortOppoTxt, c);
		this.jPanel.add(this.westPortOppoTxt);
	
		
		/** 第5行 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.eastPort, c);
		this.jPanel.add(this.eastPort);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.eastPortCombo, c);
		this.jPanel.add(this.eastPortCombo);
		
	
		
		/** 第7行 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(eastPortOppo, c);
		this.jPanel.add(eastPortOppo);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(eastPortOppoTxt, c);
		this.jPanel.add(eastPortOppoTxt);
	
		/** 第8行 */
		c.gridx = 0;
		c.gridy = 8;
		layout.setConstraints(this.apsenable, c);
		this.jPanel.add(this.apsenable);
		c.gridx = 1;
		layout.addLayoutComponent(this.apsenableTxt, c);
		this.jPanel.add(apsenableTxt);
		
		/** 第9行 */
		c.gridx = 0;
		c.gridy = 9;
		layout.setConstraints(this.activeStatus, c);
		this.jPanel.add(this.activeStatus);
		c.gridx = 1;
		layout.addLayoutComponent(this.activeStatusCheck, c);
		this.jPanel.add(activeStatusCheck);
		
		c.fill = GridBagConstraints.NONE;
		/** 第11行 */
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(confirmBtn, c);
		this.jPanel.add(confirmBtn);
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		// c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(canelBtn, c);
		this.jPanel.add(canelBtn);

		this.add(jPanel);
	}
	private void canelBtnActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void confirmBtnActionPerformed(java.awt.event.ActionEvent evt) {
		String result;
		ControlKeyValue westPortSelect = null;
		ControlKeyValue eastPortSelect = null;
		
		List<LoopProtectInfo> loopList ;
		try {
			
			loopList = new ArrayList();
			westPortSelect = (ControlKeyValue) this.westPortCombo.getSelectedItem();
			eastPortSelect = (ControlKeyValue) this.eastPortCombo.getSelectedItem();			
			//东西端口不相同
			if (westPortSelect.getName().equals(eastPortSelect.getName())) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORT_MEMBERS_SAME));
				return;
			}
			//西端口可用
			if(!(((PortInst)westPortSelect.getObject()).getPortId()+"").equals(loopProtectInfo.getWestPort()+"")){
				if (isNenameExist(((PortInst)westPortSelect.getObject()).getPortId()+"", "port")){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORT_OCCUPY));
					return;
				}
			}
			
			//东端口可用
			if(!(((PortInst)eastPortSelect.getObject()).getPortId()+"").equals(loopProtectInfo.getEastPort()+""))
			if (isNenameExist(((PortInst)eastPortSelect.getObject()).getPortId()+"", "port")){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORT_OCCUPY));
				return;
			}
			//3个指向ID不相同
			if(this.loopNodeIdTxt.getText().equals(this.westPortOppoTxt.getText())||this.loopNodeIdTxt.getText().equals(this.eastPortOppoTxt.getText())||
					this.eastPortOppoTxt.getText().equals(this.westPortOppoTxt.getText())){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NODEID_DIFFERENT));
				return;
			}
			//本节点id可以用
			if(!loopNodeIdTxt.getText().trim().equals(loopProtectInfo.getNodeId()+""))
			if (isNenameExist(loopNodeIdTxt.getText().trim(), "id")) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NODEID_EXIT));
				return;
			}
			//西向id可以用
			if(!this.westPortOppoTxt.getText().trim().equals(loopProtectInfo.getWestNodeId()+""))
			if (isNenameExist(this.westPortOppoTxt.getText().trim(), "id")) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NODEID_EXIT));
				return;
			}
			//东向id可以用
			if(!this.eastPortOppoTxt.getText().equals(loopProtectInfo.getEastNodeId()+"")){
				if (isNenameExist(this.eastPortOppoTxt.getText().trim(), "id")) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NODEID_EXIT));
					return;
				}
			}
			loopProtectInfo.setNodeId(Integer.parseInt(this.loopNodeIdTxt.getText()));
			loopProtectInfo.setDelaytime(Integer.parseInt(this.delaytimeTxt.getValue()+""));
			loopProtectInfo.setWaittime(Integer.parseInt(this.waittimeTxt.getValue()+""));
			loopProtectInfo.setWestPort(((PortInst)westPortSelect.getObject()).getPortId());
			loopProtectInfo.setWestNodeId(Integer.parseInt(this.westPortOppoTxt.getText()));
			loopProtectInfo.setEastPort(((PortInst)eastPortSelect.getObject()).getPortId());
			loopProtectInfo.setEastNodeId(Integer.parseInt(this.eastPortOppoTxt.getText()));
			loopProtectInfo.setApsenable(this.apsenableTxt.isSelected()?1:0);
			loopProtectInfo.setSiteId(ConstantUtil.siteId);
			loopProtectInfo.setCreateUser(ConstantUtil.user.getUser_Name());
			loopProtectInfo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd hh:MM:ss").format( new Date()));
			loopProtectInfo.setIsSingle(1);
			loopProtectInfo.setActiveStatus(this.activeStatusCheck.isSelected()?EActiveStatus.ACTIVITY.getValue():EActiveStatus.UNACTIVITY.getValue());;
			loopList.add(loopProtectInfo);
			
			DispatchUtil wrappingDispatch = new DispatchUtil(RmiKeys.RMI_WRAPPING);
			if(0==this.loopProtectInfo.getLoopId()){
				result = wrappingDispatch.excuteInsert(loopList);
			}else{
				result = wrappingDispatch.excuteUpdate(loopList);
			}
			DialogBoxUtil.succeedDialog(this, result);
			this.dispose();
			this.loopProtectPanel.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		
		}
	}

	private boolean isNenameExist(String text, String type) {
		BusinessidService_MB businessidservice = null;
		Businessid businessid = null; 
		WrappingProtectService_MB wrappingProtectService = null;
		try {
			if("id".equals(type)){
				businessidservice = (BusinessidService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BUSINESSID);
				businessid = businessidservice.select(Integer.parseInt(text),ConstantUtil.siteId,"ringNode");
				if (null != businessid  && 0 != businessid.getIdStatus()) {
					return true;
				}
			}
			
			if("port".equals(type)){
				LoopProtectInfo loopProtectInfo = new LoopProtectInfo();
				List<LoopProtectInfo> list;
				loopProtectInfo.setSiteId(ConstantUtil.siteId);
				loopProtectInfo.setEastPort(Integer.parseInt(text));
				wrappingProtectService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
				list = wrappingProtectService.select(loopProtectInfo);
				if (null != list  && list.size() > 0) {
					return true;
				}
				loopProtectInfo.setEastPort(0);
				loopProtectInfo.setWestPort(Integer.parseInt(text));
				list = wrappingProtectService.select(loopProtectInfo);
				if (null != list  && list.size() > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(wrappingProtectService);
			UiUtil.closeService_MB(businessidservice);
		}
		return false;
	}
	//东向端口数据初始化
	private void initEastPortCombo(JComboBox eastPortTxt) {
		PortService_MB service = null;
		List<PortInst> portList = null;
		PortInst portInst;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) eastPortTxt.getModel();
		try {
			portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setPortType("NNI");
			service = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portList = service.select(portInst);
			for (PortInst portInst1 : portList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(portInst1.getPortId() + "", portInst1.getPortName(), portInst1));
			}
			eastPortTxt.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			portInst = null;
			UiUtil.closeService_MB(service);
			portList = null;
		}
	}
	//西向端口数据初始化
	private void initWestPortCombo(JComboBox westPortTxt) {
		PortService_MB service = null;
		List<PortInst> portList = null;
		PortInst portInst;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) westPortTxt.getModel();
		try {
			portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setPortType("NNI");
			service = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portList = service.select(portInst);
			for (PortInst portInst1 : portList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(portInst1.getPortId() + "", portInst1.getPortName(), portInst1));
			}
			westPortTxt.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			portInst = null;
			UiUtil.closeService_MB(service);
			portList = null;
		}
		
	}
	public LoopProtectPanelSingle getLoopProtectPanel() {
		return loopProtectPanel;
	}

	public void setLoopProtectPanel(LoopProtectPanelSingle loopProtectPanel) {
		this.loopProtectPanel = loopProtectPanel;
	}

	public PtnSpinner getWaittimeTxt() {
		return waittimeTxt;
	}

	public void setWaittimeTxt(PtnSpinner waittimeTxt) {
		this.waittimeTxt = waittimeTxt;
	}

	public PtnSpinner getDelaytimeTxt() {
		return delaytimeTxt;
	}

	public void setDelaytimeTxt(PtnSpinner delaytimeTxt) {
		this.delaytimeTxt = delaytimeTxt;
	}
}

package com.nms.ui.ptn.clock.view.cx.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.clock.PortConfigInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
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
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.clock.view.cx.time.MyItemListener;
import com.nms.ui.ptn.clock.view.cx.time.TabPanelTwoTCX;
import com.nms.ui.ptn.clock.view.cx.time.TextFiledKeyListener;
import com.nms.ui.ptn.clock.view.cx.time.TextfieldFocusListener;

public class TCCreateDialog extends PtnDialog {

	private static final long serialVersionUID = 3126078401125826631L;

	private JLabel port = null;

	private JComboBox port_ = null;

	private JLabel portEnable = null;

	private JCheckBox portEnable_ = null;

	private JLabel interfaceType = null;

	private JComboBox interfaceType_ = null;

	private JLabel timeStampMode = null;

	private JComboBox timeStampMode_ = null;

	private JLabel vlanID = null;

	private JTextField vlanID_ = null;

	private JLabel delayMechanism = null;

	private JComboBox delayMechanism_ = null;

	private JLabel operationMode = null;

	private JComboBox operationMode_ = null;

	private JLabel anncPacketsCycle = null;

	private JComboBox anncPacketsCycle_ = null;

	private JLabel anncTimeoutSetting = null;

	private JTextField anncTimeoutSetting_ = null;

	private JLabel syncPacketsCycle = null;

	private JComboBox syncPacketsCycle_ = null;

	private JLabel delayReqPacketsCycle = null;

	private JComboBox delayReqPacketsCycle_ = null;

	private JLabel pdelReqPacketsCycle = null;

	private JComboBox pdelReqPacketsCycle_ = null;

	private JLabel lineDelayCompensation = null;

	private JTextField lineDelayCompensation_ = null;

	private PtnButton confirm = null;

	private JButton cancel = null;

	private GridBagLayout gridBagLayout = null;

	private JPanel buttonPanel = null;
	
    private TabPanelTwoTCX tabPanelTwoTCX = null;
    private JDialog jDialog=null;
    private PortConfigInfo portConfigInfo=null;
    
	public TCCreateDialog(TabPanelTwoTCX tabPanelTwoTCX,PortConfigInfo portConfigInfo) throws Exception {

		try {
			this.portConfigInfo=portConfigInfo;
			this.tabPanelTwoTCX=tabPanelTwoTCX;
			init();
		} catch (Exception e) {

			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void init() throws Exception {

		try {
			gridBagLayout = new GridBagLayout();
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_PTPPORT));
			port = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT));
			port_ = new JComboBox();/* 下拉框所加数据暂不实现 */
			this.intalPortComboBox(port_);
			portEnable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_ENABLED));
			portEnable_ = new JCheckBox();
			interfaceType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INTERFACE_TYPE));
			interfaceType_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.interfaceType_, "interfaceType");
			interfaceType_.setSelectedIndex(1);
			timeStampMode = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIME_STAMP_MODE));
			timeStampMode_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.timeStampMode_, "timeStampMode");
			vlanID = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_ID));
			vlanID_ = new JTextField();
			vlanID_.setText("1");
			delayMechanism = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_MECHANISM));
			delayMechanism_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.delayMechanism_, "delayMechanism");
			operationMode = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OPERATION_MODE));
			operationMode_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.operationMode_, "operationModeT");
			anncPacketsCycle = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ANNC_PACKETS_CYCLE));
			anncPacketsCycle_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.anncPacketsCycle_, "PTPTimePort");
			anncTimeoutSetting = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ANNC_TIMEOUT_SETTING));
			anncTimeoutSetting_ = new JTextField();
			anncTimeoutSetting_.setText("4");
			syncPacketsCycle = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SYNC_PACKETS_CYCLE));
			syncPacketsCycle_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.syncPacketsCycle_, "SyncPtpPort");
			delayReqPacketsCycle = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_REQ_PACKETS_CYCLE));
			delayReqPacketsCycle_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.delayReqPacketsCycle_, "PTPTimePort");
			pdelReqPacketsCycle = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PDEL_REQ_PACKETS_CYCLE));
			pdelReqPacketsCycle_ = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.pdelReqPacketsCycle_, "PTPTimePort");
			pdelReqPacketsCycle_.setEnabled(false);
			lineDelayCompensation = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LINE_DELAY_COMPENSATION));
			lineDelayCompensation_ = new JTextField();
			lineDelayCompensation_.setText("0");
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonPanel = new JPanel();

			setButtonLayout();/* 按钮所在panel布局 */
			buttonPanel.add(confirm);
			buttonPanel.add(cancel);

			setGridBagLayout();/* 主窗口布局 */
			this.setLayout(gridBagLayout);
			this.add(port);
			this.add(port_);
			this.add(portEnable);
			this.add(portEnable_);
			this.add(interfaceType);
			this.add(interfaceType_);
			this.add(timeStampMode);
			this.add(timeStampMode_);
			this.add(vlanID);
			this.add(vlanID_);
			this.add(delayMechanism);
			this.add(delayMechanism_);
			this.add(operationMode);
			this.add(operationMode_);
			this.add(anncPacketsCycle);
			this.add(anncPacketsCycle_);
			this.add(anncTimeoutSetting);
			this.add(anncTimeoutSetting_);
			this.add(syncPacketsCycle);
			this.add(syncPacketsCycle_);
			this.add(delayReqPacketsCycle);
			this.add(delayReqPacketsCycle_);
			this.add(pdelReqPacketsCycle);
			this.add(pdelReqPacketsCycle_);
			this.add(lineDelayCompensation);
			this.add(lineDelayCompensation_);
			this.add(buttonPanel);
			
			if(null!=portConfigInfo){
				setValueForDialog();/*为对话框里的每项赋值*/
			}
			addItemListenerForCombox();/* 添加事件，如果选择，其它组件或者激活或者非激活 */
			addKeyListenerForTextfield();/* textfield添加键盘事件监听，只允许数字输入（或者‘-’，当为‘线路延时补偿’框） */
			addFocusListenerForTextfield();/*textfield聚焦事件监听，当离开此textfield判断值是否在指定范围内*/
			addButtonListener();/*为按钮添加事件;‘确定’和‘取消’*/
			UiUtil.showWindow(this, 700, 500);
		} catch (Exception e) {

			throw e;
		}
	}
	/**
	 * *为对话框里的每项赋值*
	 * @throws Exception
	 */
	private void setValueForDialog()throws Exception{
		try {
				//端口
			super.getComboBoxDataUtil().comboBoxSelect( this.port_, String.valueOf(portConfigInfo.getPort()));
//				this.comboBoxSelectByValue( this.port_,String.valueOf(portConfigInfo.getPort()));
				this.port.setEnabled(false);
				//端口使能
				this.portEnable_.setSelected(portConfigInfo.getPortEnable()==1?true:false);
				//接口类型
				super.getComboBoxDataUtil().comboBoxSelect( this.interfaceType_, String.valueOf(portConfigInfo.getInterfaceType()));
				//时间戳模式
				super.getComboBoxDataUtil().comboBoxSelect( this.timeStampMode_, String.valueOf(portConfigInfo.getTimeStampMode()));
				//Vlan ID 
				this.vlanID_.setText(portConfigInfo.getVlanID());
				//延时机制  delayMechanism_
				super.getComboBoxDataUtil().comboBoxSelect( this.delayMechanism_, String.valueOf(portConfigInfo.getDelayMechanism()));
				//操作模式 operationMode_
				super.getComboBoxDataUtil().comboBoxSelect( this.operationMode_, String.valueOf(portConfigInfo.getOperationMode()));
				//Annc报文周期
				super.getComboBoxDataUtil().comboBoxSelect( this.anncPacketsCycle_, String.valueOf(portConfigInfo.getAnncPacketsInterval()));
				//Annc超时设置
				this.anncTimeoutSetting_.setText(portConfigInfo.getAnncTimeoutSetting());
				//Sync报文周期 syncPacketsCycle_
				super.getComboBoxDataUtil().comboBoxSelect( this.syncPacketsCycle_,String.valueOf(portConfigInfo.getSyncPacketsInterval()));
				//线路延时补偿
				this.lineDelayCompensation_.setText(portConfigInfo.getLineDelayCompensation());
				//Delay_Req报文间隔
				super.getComboBoxDataUtil().comboBoxSelect(  this.delayReqPacketsCycle_, String.valueOf(portConfigInfo.getDelay_ReqPacketsInterval()));
				//Pdel_Req报文间隔
				super.getComboBoxDataUtil().comboBoxSelect( this.pdelReqPacketsCycle_, String.valueOf(portConfigInfo.getPdel_ReqPacketsInterval()));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 * <p>
	 * 按钮所在panel布局
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void setButtonLayout() throws Exception {

		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout = null;
		try {

			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 20, 20 };
			gridBagLayout.columnWeights = new double[] { 0, 0 };
			gridBagLayout.rowHeights = new int[] { 21 };
			gridBagLayout.rowWeights = new double[] { 0 };
			gridBagConstraints.insets = new Insets(5, 10, 5, 0);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(confirm, gridBagConstraints);

			gridBagConstraints.insets = new Insets(5, 25, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(cancel, gridBagConstraints);

			buttonPanel.setLayout(gridBagLayout);
		} catch (Exception e) {

			throw e;
		}
	}

	/**
	 * <p>
	 * 主窗口布局
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void setGridBagLayout() throws Exception {

		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 50, 100, 50, 100 };
			gridBagLayout.columnWeights = new double[] { 0, 1, 0, 1 };
			gridBagLayout.rowHeights = new int[] { 50, 50, 50, 50, 50, 50, 50, 50, 50 };
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 10, 0, 0);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(port, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(portEnable, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(interfaceType, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeStampMode, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(anncPacketsCycle, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(anncTimeoutSetting, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(syncPacketsCycle, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 7;
			gridBagLayout.setConstraints(lineDelayCompensation, gridBagConstraints);

			/** ******************************************************************** */

			gridBagConstraints.insets = new Insets(5, 5, 0, 0);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(port_, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(portEnable_, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(interfaceType_, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(timeStampMode_, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(anncPacketsCycle_, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(anncTimeoutSetting_, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(syncPacketsCycle_, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 7;
			gridBagLayout.setConstraints(lineDelayCompensation_, gridBagConstraints);

			/** ************************************************************************* */

			gridBagConstraints.insets = new Insets(5, 30, 0, 0);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(vlanID, gridBagConstraints);

			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(delayMechanism, gridBagConstraints);

			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(operationMode, gridBagConstraints);

			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(delayReqPacketsCycle, gridBagConstraints);

			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(pdelReqPacketsCycle, gridBagConstraints);

			/** ************************************************************************ */
			gridBagConstraints.insets = new Insets(5, 5, 0, 10);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(vlanID_, gridBagConstraints);

			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(delayMechanism_, gridBagConstraints);

			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(operationMode_, gridBagConstraints);

			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(delayReqPacketsCycle_, gridBagConstraints);

			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(pdelReqPacketsCycle_, gridBagConstraints);

			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 8;
			gridBagLayout.setConstraints(buttonPanel, gridBagConstraints);
		} catch (Exception e) {

			throw e;
		}
	}

	/**
	 * <p>
	 * 添加事件，如果选择，其它组件或者激活或者非激活
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void addItemListenerForCombox() throws Exception {

		MyItemListener myItemListener = null;
		try {

			/* 接口类型添加监听；1为‘接口类型’ */
			myItemListener = new MyItemListener(this, 1);
			interfaceType_.addItemListener(myItemListener);
			/* 接口类型添加监听；2为‘延时机制’ */
			myItemListener = new MyItemListener(this, 2);
			delayMechanism_.addItemListener(myItemListener);

		} catch (Exception e) {

			throw e;
		}
	}

	/**
	 * <p>
	 * textfield添加监听，只允许输入数字
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void addKeyListenerForTextfield() throws Exception {

		TextFiledKeyListener textFIledKeyListener = null;
		try {
			/* 为1：只接受数字 */
			textFIledKeyListener = new TextFiledKeyListener(1);
			anncTimeoutSetting_.addKeyListener(textFIledKeyListener);
			vlanID_.addKeyListener(textFIledKeyListener);
			/* 为2：接受数字和“-” */
			textFIledKeyListener = new TextFiledKeyListener(2);
			lineDelayCompensation_.addKeyListener(textFIledKeyListener);
		} catch (Exception e) {

			throw e;
		}
	}
	
	/**
	 * <p>
	 * 判断输入数值是否在指定区间内
	 * </p>
	 * @throws Exception
	 */
	private void addFocusListenerForTextfield()throws Exception{
		
		TextfieldFocusListener textfieldFocusListener=null;
		String whichTextTield=null;
		try{
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_ID);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,4,vlanID_);
			vlanID_.addFocusListener(textfieldFocusListener);
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_ANNC_TIMEOUT_SETTING);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,5,anncTimeoutSetting_);
			anncTimeoutSetting_.addFocusListener(textfieldFocusListener);
			
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_LINE_DELAY_COMPENSATION);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,6,lineDelayCompensation_);
			lineDelayCompensation_.addFocusListener(textfieldFocusListener);
		}catch(Exception e){
			
			throw e;
		}
	}
	
	/**
	 * <p>
	 * 按钮添加监听
	 * </p>
	 * 
	 * @throws Exception
	 */
	private void addButtonListener() throws Exception {
		jDialog=this;
		try {
			this.confirm.addActionListener(new MyActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					btnSaveListener();
				}

				@Override
				public boolean checking() {
					
					return true;
				}
			});
			
			this.cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					jDialog.dispose();
				}
			});
		} catch (Exception e) {

			throw e;
		}
	}
/**
 * 处理新建事件或修改事件
 */
	private void btnSaveListener(){
		DispatchUtil timePortConfigDispatch;
		try {
			timePortConfigDispatch = new DispatchUtil(RmiKeys.RMI_TIMEPORTCONFIG);
			if(null!=portConfigInfo){
				if(null==this.port_.getSelectedItem()){
				 DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
			}else{
				//端口
				ControlKeyValue portkey_broad = (ControlKeyValue) this.port_.getSelectedItem();
				portConfigInfo.setPort(Integer.parseInt(portkey_broad.getId()));
//			    portConfigInfo.setPort(1);
				//端口使能
			    portConfigInfo.setPortEnable(this.portEnable_.isSelected()?1:0);
				//接口类型
				ControlKeyValue interfaceTypekey_broad = (ControlKeyValue) this.interfaceType_.getSelectedItem();
				Code codeInterType=(Code) interfaceTypekey_broad.getObject();
				portConfigInfo.setInterfaceType(Integer.parseInt(codeInterType.getCodeValue()));
				
				//时间戳模式
				ControlKeyValue timeStampModekey_broad = (ControlKeyValue) this.timeStampMode_.getSelectedItem();
				Code codeStampMode=(Code) timeStampModekey_broad.getObject();
				portConfigInfo.setTimeStampMode(Integer.parseInt(codeStampMode.getCodeValue()));
				//Vlan ID 
				portConfigInfo.setVlanID(this.vlanID_.getText());
				//延时机制  delayMechanism_
				ControlKeyValue delayMechanismkey_broad = (ControlKeyValue) this.delayMechanism_.getSelectedItem();
				portConfigInfo.setDelayMechanism(Integer.parseInt(delayMechanismkey_broad.getId()));
				//操作模式 operationMode_
				ControlKeyValue operationModekey_broad = (ControlKeyValue) this.operationMode_.getSelectedItem();
				portConfigInfo.setOperationMode(Integer.parseInt(operationModekey_broad.getId()));
				//Annc报文周期
				ControlKeyValue anncPacketsCyclekey_broad = (ControlKeyValue) this.anncPacketsCycle_.getSelectedItem();
				Code codeAnnc=(Code) anncPacketsCyclekey_broad.getObject();
				portConfigInfo.setAnncPacketsInterval(Integer.parseInt(codeAnnc.getCodeValue()));
				//Annc超时设置
				portConfigInfo.setAnncTimeoutSetting(this.anncTimeoutSetting_.getText());
			    //Sync报文周期 syncPacketsCycle_
				ControlKeyValue syncPacketsCyclekey_broad = (ControlKeyValue) this.syncPacketsCycle_.getSelectedItem();
				Code codeSync=(Code) syncPacketsCyclekey_broad.getObject();
				portConfigInfo.setSyncPacketsInterval(Integer.parseInt(codeSync.getCodeValue()));
			   //线路延时补偿
				portConfigInfo.setLineDelayCompensation(this.lineDelayCompensation_.getText());
				//Delay_Req报文间隔
				ControlKeyValue delayReqPacketsCyclekey_broad = (ControlKeyValue) this.delayReqPacketsCycle_.getSelectedItem();
				Code codeDelay=(Code) delayReqPacketsCyclekey_broad.getObject();
				portConfigInfo.setDelay_ReqPacketsInterval(Integer.parseInt(codeDelay.getCodeValue()));
				//Pdel_Req报文间隔
				ControlKeyValue pdelReqPacketsCyclekey_broad = (ControlKeyValue) this.pdelReqPacketsCycle_.getSelectedItem();
				Code codePdel=(Code) pdelReqPacketsCyclekey_broad.getObject();
				portConfigInfo.setPdel_ReqPacketsInterval(Integer.parseInt(codePdel.getCodeValue()));
				
				//操作数据库和下发设备 portConfigInfo为空就就创建/否则就执行跟新操作
				String result=timePortConfigDispatch.excuteUpdate(portConfigInfo);
				if(result.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
					confirm.setResult(1);
				}else{
					confirm.setResult(2);
				}
				this.confirm.setOperateKey(EOperationLogType.PORTUPDATE.getValue());
				DialogBoxUtil.succeedDialog(this, result);
			}
			}
			else{
				if(null==this.port_.getSelectedItem()){
					 DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
				}else{
					//端口
					portConfigInfo=new PortConfigInfo();
					ControlKeyValue portkey_broad = (ControlKeyValue) this.port_.getSelectedItem();
					portConfigInfo.setPort(Integer.parseInt(portkey_broad.getId()));
					//端口使能
				    portConfigInfo.setPortEnable(this.portEnable_.isSelected()?1:0);
					//接口类型
					ControlKeyValue interfaceTypekey_broad = (ControlKeyValue) this.interfaceType_.getSelectedItem();
					Code codeInterType=(Code)interfaceTypekey_broad.getObject();
					portConfigInfo.setInterfaceType(Integer.parseInt(codeInterType.getCodeValue()));
					
					//时间戳模式
					ControlKeyValue timeStampModekey_broad = (ControlKeyValue) this.timeStampMode_.getSelectedItem();
					Code codeStampMode=(Code)timeStampModekey_broad.getObject();
					portConfigInfo.setTimeStampMode(Integer.parseInt(codeStampMode.getCodeValue()));
					//Vlan ID 
					portConfigInfo.setVlanID(this.vlanID_.getText());
					//延时机制  delayMechanism_
					ControlKeyValue delayMechanismkey_broad = (ControlKeyValue) this.delayMechanism_.getSelectedItem();
					portConfigInfo.setDelayMechanism(Integer.parseInt(delayMechanismkey_broad.getId()));
					//操作模式 operationMode_
					ControlKeyValue operationModekey_broad = (ControlKeyValue) this.operationMode_.getSelectedItem();
					portConfigInfo.setOperationMode(Integer.parseInt(operationModekey_broad.getId()));
					//Annc报文周期
					ControlKeyValue anncPacketsCyclekey_broad = (ControlKeyValue) this.anncPacketsCycle_.getSelectedItem();
					Code codeAnnc=(Code) anncPacketsCyclekey_broad.getObject();
					portConfigInfo.setAnncPacketsInterval(Integer.parseInt(codeAnnc.getCodeValue()));
					//Annc超时设置
					portConfigInfo.setAnncTimeoutSetting(this.anncTimeoutSetting_.getText());
				    //Sync报文周期 syncPacketsCycle_
					ControlKeyValue syncPacketsCyclekey_broad = (ControlKeyValue) this.syncPacketsCycle_.getSelectedItem();
					Code codeSync=(Code) syncPacketsCyclekey_broad.getObject();
					portConfigInfo.setSyncPacketsInterval(Integer.parseInt(codeSync.getCodeValue()));
				   //线路延时补偿
					portConfigInfo.setLineDelayCompensation(this.lineDelayCompensation_.getText());
					//Delay_Req报文间隔
					ControlKeyValue delayReqPacketsCyclekey_broad = (ControlKeyValue) this.delayReqPacketsCycle_.getSelectedItem();
					Code codeDelay=(Code) delayReqPacketsCyclekey_broad.getObject();
					portConfigInfo.setDelay_ReqPacketsInterval(Integer.parseInt(codeDelay.getCodeValue()));
					//Pdel_Req报文间隔
					ControlKeyValue pdelReqPacketsCyclekey_broad = (ControlKeyValue) this.pdelReqPacketsCycle_.getSelectedItem();
					Code codePdel=(Code) pdelReqPacketsCyclekey_broad.getObject();
					portConfigInfo.setPdel_ReqPacketsInterval(Integer.parseInt(codePdel.getCodeValue()));
					
					portConfigInfo.setSiteId(ConstantUtil.siteId);
					//操作数据库 和下发设备portConfigInfo为空就就创建/否则就执行跟新操作
					String result=timePortConfigDispatch.excuteInsert(portConfigInfo);
					if(result.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
						confirm.setResult(1);
					}else{
						confirm.setResult(2);
					}
					this.confirm.setOperateKey(EOperationLogType.PORTINSERT.getValue());
					DialogBoxUtil.succeedDialog(this, result);
					
					
			}
			}
			
			  //影藏创建界面
			  jDialog.dispose();
			  //刷新组界面
			  this.tabPanelTwoTCX.controller.refresh();
//			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			portConfigInfo=null;
			timePortConfigDispatch=null;
		}
		
	}
	public JComboBox getDelayMechanism_() {
		return delayMechanism_;
	}

	public void setDelayMechanism_(JComboBox delayMechanism_) {
		this.delayMechanism_ = delayMechanism_;
	}

	public JComboBox getDelayReqPacketsCycle_() {
		return delayReqPacketsCycle_;
	}

	public void setDelayReqPacketsCycle_(JComboBox delayReqPacketsCycle_) {
		this.delayReqPacketsCycle_ = delayReqPacketsCycle_;
	}

	public JComboBox getOperationMode_() {
		return operationMode_;
	}

	public void setOperationMode_(JComboBox operationMode_) {
		this.operationMode_ = operationMode_;
	}

	public JComboBox getPdelReqPacketsCycle_() {
		return pdelReqPacketsCycle_;
	}

	public void setPdelReqPacketsCycle_(JComboBox pdelReqPacketsCycle_) {
		this.pdelReqPacketsCycle_ = pdelReqPacketsCycle_;
	}
	private void intalPortComboBox(JComboBox jComboBox) throws Exception {
		PortService_MB portService = null;
		List<PortInst> portInstList = null;
		DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setPortType("NNI");

			portInstList = portService.select(portInst);
			for (PortInst inst : portInstList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(inst.getPortId() + "", inst.getPortName(), inst));
			}
			jComboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			portInstList = null;
		}

	}
	
}

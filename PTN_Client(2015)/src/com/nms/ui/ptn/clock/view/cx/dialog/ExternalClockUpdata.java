﻿package com.nms.ui.ptn.clock.view.cx.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.ptn.clock.ExternalClockInterface;
import com.nms.rmi.ui.util.RmiKeys;
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
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.clock.view.cx.clockinterface.TabPanelOneTICX;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
/**
 * function:时钟管理中--时钟接口--外接口时钟接口--中修改 ，类型为：extck
 * @author zhangkun
 *
 */
public class ExternalClockUpdata extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7926185985471260691L;

	private JLabel interfaceType;
	private JTextField interfaceType_;
	private JLabel manageState;
	private JComboBox manageState_;
	private JLabel jobState;
	private JTextField jobState_;
	private JLabel interfaceModel;
	private JComboBox interfaceModel_;
	private JLabel inputImpedance;
	private JComboBox inputImpedance_;
	private JLabel SANByte;
	private JComboBox SANByte_;
	private GridBagLayout gridBagLayout=null;
	private PtnButton confirm=null;
	private JButton cancel=null;
    private JDialog jDialog=null;
    private JPanel buttonJpan=null;
    private ExternalClockInterface externalClockInterface=null;
    private TabPanelOneTICX tabPanelOneTICX=null;
    
	public ExternalClockUpdata(TabPanelOneTICX tabPanelOneTICX,ExternalClockInterface externalClockInterface) {
		this.tabPanelOneTICX=tabPanelOneTICX;
		this.externalClockInterface=externalClockInterface;
		init();
	}

	/**
	 *初始化界面
	 */
	private void init() {
		try {
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_EXTERNALCLOCK));
			gridBagLayout = new GridBagLayout();
			interfaceType=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INTERFACE_TYPE));
			interfaceType_=new JTextField();
			interfaceType_.setEditable(false);
			manageState=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MANAGE_STATUS)); 
			manageState_=new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.manageState_, "ENABLEDSTATUE");
			
			jobState=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_JOB_STATUS));
			jobState_=new JTextField();
			jobState_.setEditable(false);
			
			interfaceModel=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INTERFACE_MODEL));
			interfaceModel_=new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.interfaceModel_, "intefaceModelTwo");
			
			inputImpedance=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INPURT_IMPEDANCE));
			inputImpedance_=new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.inputImpedance_, "inputImpedance");
			
			SANByte=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SANBYtE)); 
			SANByte_=new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.SANByte_, "SANByte");
			
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false,RootFactory.CORE_MANAGE);
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL)); 
			
			buttonJpan=new JPanel();
			setLayout();
			buttonJpan.add(confirm);
			buttonJpan.add(cancel);
			
			setGridBagLayout();/* 主窗口布局 */
			this.setLayout(gridBagLayout);
			this.add(buttonJpan);
			this.add(interfaceType);
			this.add(interfaceType_);
			this.add(manageState);
			this.add(manageState_);
			this.add(jobState);
			this.add(jobState_);
			this.add(interfaceModel);
			this.add(interfaceModel_);
			this.add(inputImpedance);
			this.add(inputImpedance_);
			this.add(SANByte);
			this.add(SANByte_);
			initdata();
			addButtonListener();
			UiUtil.showWindow(this, 400, 400);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	/**
	 *初始化数据 
	 */
	private void initdata() {
		try {
			
			this.interfaceType_.setText(externalClockInterface.getInterfaceName());
			super.getComboBoxDataUtil().comboBoxSelect(this.manageState_,String.valueOf(externalClockInterface.getManagingStatus()));
			this.jobState_.setText(externalClockInterface.getJobStatus(externalClockInterface.getWorkingStatus()));
			super.getComboBoxDataUtil().comboBoxSelect(this.interfaceModel_,String.valueOf(externalClockInterface.getInterfaceMode()));
			super.getComboBoxDataUtil().comboBoxSelect(this.inputImpedance_,String.valueOf(externalClockInterface.getInputImpedance()));
			super.getComboBoxDataUtil().comboBoxSelect(this.SANByte_,String.valueOf(externalClockInterface.getSanBits()));
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 
	 * 创建按钮的布局
	 */
     private void setLayout() {
    	 GridBagConstraints gridBagConstraints=null;
 		 GridBagLayout gridBagLayout = null;
 		try {
 			gridBagLayout = new GridBagLayout();
 			gridBagConstraints = new GridBagConstraints();
 			gridBagLayout.columnWidths=new int[]{20,20};
 			gridBagLayout.columnWeights=new double[]{0,0};
 			gridBagLayout.rowHeights=new int[]{21};
 			gridBagLayout.rowWeights=new double[]{0};
 			
 			gridBagConstraints.insets=new Insets(5,10,5,0);
 			gridBagConstraints= new GridBagConstraints();
 			gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
 			gridBagConstraints.gridx = 0;
 			gridBagConstraints.gridy = 0;
 			gridBagLayout.setConstraints(confirm, gridBagConstraints);
 			
 			gridBagConstraints.insets = new Insets(5, 25, 5, 5);
 			gridBagConstraints.gridx = 1;
 			gridBagConstraints.gridy = 0;
 			gridBagLayout.setConstraints(cancel, gridBagConstraints);
 			
 			buttonJpan.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}	
	}

/**
 * 处理各个单击事件处理
 * 
 */
	private void addButtonListener() {
		try {
			jDialog=this;
			this.confirm.addActionListener(new MyActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					btnSaveData();					
				}				
				@Override
				public boolean checking() {
					return false;
				}
			});
			this.cancel.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {				
					jDialog.dispose();
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
	}
	private void btnSaveData() {
		DispatchUtil externalClockDispatch;
		try {
		externalClockInterface.setInterfaceName(this.interfaceType_.getText());
		 //管理状态
	    ControlKeyValue manageStatekey_broad = (ControlKeyValue) this.manageState_.getSelectedItem();
	    externalClockInterface.setManagingStatus(Integer.parseInt(manageStatekey_broad.getId()));
	    externalClockInterface.setWorkingStatus(this.jobState_.getText());
	    //接口模式
	    ControlKeyValue interfaceModelkey_broad = (ControlKeyValue) this.interfaceModel_.getSelectedItem();
	    externalClockInterface.setInterfaceMode(Integer.parseInt(interfaceModelkey_broad.getId()));
	    //输入阻抗
	    ControlKeyValue inputImpedancekey_broad = (ControlKeyValue) this.inputImpedance_.getSelectedItem();
	    externalClockInterface.setInputImpedance(Integer.parseInt(inputImpedancekey_broad.getId()));
	    //SAN比特
	    ControlKeyValue SANBytekey_broad = (ControlKeyValue) this.SANByte_.getSelectedItem();
	    externalClockInterface.setSanBits(Integer.parseInt(SANBytekey_broad.getId()));
		
	    //操作数据库/刷新组界面/影藏界面
	   // externalClockInterfaceService.update(externalClockInterface);
	    externalClockDispatch = new DispatchUtil(RmiKeys.RMI_EXTERNALCLOCK);
		  //  ExternalClockDispatch e=new ExternalClockDispatch();
	    String result=externalClockDispatch.excuteUpdate(externalClockInterface);
//	    AddOperateLog.insertOperLog(confirm, EOperationLogType.REMOVELOGINLOG.getValue(), result);
	    //externalClockInterfaceService.update(externalClockInterface);
	    DialogBoxUtil.succeedDialog(null, result);
	    this.tabPanelOneTICX.controller.refresh();
		jDialog.dispose();
		
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			externalClockDispatch=null;
		}
		
	}
/**
 * 设置布局
 */
	private void setGridBagLayout() {
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagConstraints=new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 30,200};
			gridBagLayout.columnWeights = new double[] {0,0,0};
			gridBagLayout.rowHeights = new int[] {35, 35, 35, 35, 35, 35, 35, 35};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0};
			gridBagConstraints.insets = new Insets(5, 10, 0, 0); 
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(interfaceType, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(manageState, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(jobState, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(interfaceModel, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(inputImpedance, gridBagConstraints);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(SANByte, gridBagConstraints);
//			
//			
//			
//    //******************************************************************		
////			gridBagConstraints.insets = new Insets(5, 30, 0, 0);
//			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(interfaceType_, gridBagConstraints);
//			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(manageState_, gridBagConstraints);
//			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(jobState_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(interfaceModel_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagLayout.setConstraints(inputImpedance_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagLayout.setConstraints(SANByte_, gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 6;
			gridBagLayout.setConstraints(buttonJpan, gridBagConstraints);
			
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

}

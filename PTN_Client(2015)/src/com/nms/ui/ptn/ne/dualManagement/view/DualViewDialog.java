package com.nms.ui.ptn.ne.dualManagement.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.VplsInfo;
import com.nms.db.bean.ptn.path.protect.PwProtect;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
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
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.dualManagement.controller.DualContorller;

/**
 * <p>文件名称:DualViewDialog.java</p>
 * <p>文件描述:单站侧Dual新建界面</p>
 * <p>版权所有:版权所有(c)2013-2015</p>
 * <p>公司:北京建博信通软件技术有限公司</p>
 * <p>内容摘要:</p>
 * <p>其他说明:</p>
 * <p>完成时间:2015-3-5</p>
 * <p>修改记录:</p>
 * <pre>
 *   修改日期:
 *   版本号:
 *   修改人:
 *   修改内容:
 * </pre>
 * @version 1.0
 * @author zhangkun
 *
 */

public class DualViewDialog extends PtnDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6414679362902415977L;

	/**************代码块  常量定义****************************/
	private List<DualInfo> dualInfoList;
	private DualContorller dualContorller = null;
	private JLabel lblName;
	private JTextField txtName;
	private JLabel lblMessage;
	private JButton autoNameButton;
	private JLabel mainPortLabel;
	private JComboBox mainPortBox;
	private JLabel mainPwLabel;
	private JComboBox mainPwBox;
	private JLabel standPortLabel;
	private JComboBox standPortBox;
	private JLabel standPwLabel;
	private JComboBox standPwBox;
	private JLabel acPortLabel;
	private JComboBox acportBox;
	private JLabel acLabel;
	private JComboBox acBox;
	private JLabel activeLabel;
	private JCheckBox chbActivate;
	private JLabel lbldelayTime;
	private PtnTextField txtDelayTime;
	private JLabel isBackLabel;
	private JCheckBox isBackJhechBox;
	private PtnButton btnSave;
	private PtnButton btnCanel;
	private JPanel buttonPanel;
	
	/**************代码块  构造器****************************/
	public DualViewDialog(List<DualInfo> dualInfoList,DualContorller dualContorller )
	{
		this.dualInfoList =  dualInfoList;
		if(this.dualInfoList == null)
		{
			this.dualInfoList = new ArrayList<DualInfo>();
			DualInfo duaInfo = new DualInfo();
			PwProtect pwProtect = new PwProtect();
			duaInfo.setPwProtect(pwProtect);
			this.dualInfoList.add(duaInfo);
			this.dualInfoList.add(new DualInfo());
		}
		this.dualContorller = dualContorller;
		init();
	}

	private void init() 
	{
		try {
			/**********初始化数据***************/
			initComponents();
			/***********设置界面布局************/
			setLayout();
			
			/***********给界面初始值************/
			initData();
			
			/********增加相应的监听事件********/
			addListeners();
			/********显示界面***************/
			UiUtil.showWindow(this, 600, 350);
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}

	private void initData() 
	{
		try 
		{
			/**********初始化PW***********/
			initPwBox(mainPwBox,0);
			initPwBox(standPwBox,1);
			
			/**********初始化端口***********/
			initPortBox(mainPortBox,mainPwBox);
			initPortBox(standPortBox,standPwBox);
			/*****************AC端口*****************/
			 super.getComboBoxDataUtil().initPortData(acportBox);
			/***************************/
			super.getComboBoxDataUtil().initAcData(this.acportBox, this.acBox);
		} catch (Exception e) 
		{
			 ExceptionManage.dispose(e, getClass());
		}
	}

	private void initPwBox(JComboBox mainPwBox2, int label) 
	{
		PwInfoService_MB pwInfoServiceMB = null;
		List<PwInfo> pwInfoList = null;
		DefaultComboBoxModel boxModel = null;
		ControlKeyValue keyValue = null;
		int count = 0;
		try 
		{
			pwInfoServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwInfoList = pwInfoServiceMB.getAvailable(ConstantUtil.siteId,EPwType.ETH);
			boxModel = (DefaultComboBoxModel) mainPwBox2.getModel();
			if(pwInfoList != null && pwInfoList.size()>0)
			{
				for(PwInfo pwInst : pwInfoList)
				{
					if(pwInst.getRelatedServiceId() == 0 && pwInst.getPwStatus() == 1 && pwInst.getTunnelId() >0)
					{
						keyValue = new ControlKeyValue(pwInst.getPwId()+"", pwInst.getPwName(), pwInst);
						boxModel.addElement(keyValue);	
						count++;
					}
				}
				if(label == 1 && count> 1)
				{
					mainPwBox2.setSelectedIndex(1);
				}
			}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			pwInfoList = null;
			UiUtil.closeService_MB(pwInfoServiceMB);
			boxModel = null;
			keyValue = null;
		}
	}

	/*************初始化端口**********************/
	private void initPortBox(JComboBox mainPortBox, JComboBox mainPwBox2) 
	{
		PortService_MB portService = null;
		List<PortInst> portList = null;
		ControlKeyValue keyValue = null;
	    DefaultComboBoxModel boxModel = null;
	    PwInfo pwInfo = null;
	    TunnelService_MB tunnelServiceMB = null;
	    Tunnel tunnelInfo = null;
	    List<Tunnel> tunnelInfoList = null;
	    List<Integer> portIdList = null;
	   try 
	   {
		   portList = new ArrayList<PortInst>();
		   portIdList = new ArrayList<Integer>();
		   tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
		   portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
		   boxModel = (DefaultComboBoxModel) mainPortBox.getModel();
		   if(mainPwBox2.getSelectedItem() != null)
		   {
			   pwInfo = (PwInfo) ((ControlKeyValue)mainPwBox2.getSelectedItem()).getObject();
			   tunnelInfo = new Tunnel();
			   tunnelInfo.setTunnelId(pwInfo.getTunnelId());
			   tunnelInfoList = tunnelServiceMB.selectNodeByTunnelId(tunnelInfo);
			   if(tunnelInfoList != null && tunnelInfoList.size() ==1)
			   {
				   tunnelInfo = tunnelInfoList.get(0);
				   if(tunnelInfo.getaPortId() >0)
				   {
					   portIdList.add(tunnelInfo.getaPortId());
				   }
				   if(tunnelInfo.getzPortId() >0)
				   {
					   portIdList.add(tunnelInfo.getzPortId());
				   }
				   
				   /*****************NNI端口*****************/
				   portList =  portService.getAllPortByIdsAndSiteId(portIdList,ConstantUtil.siteId);
				   if(portList != null && portList.size() >0){
					   boxModel.removeAllElements();
					   for(PortInst portInst : portList)
					   {
						   keyValue = new ControlKeyValue(portInst.getPortId()+"", portInst.getPortName(), portInst);
						   boxModel.addElement(keyValue);
					   }
				   }
			   }
		   }
		
	   } catch (Exception e)
       {
		   ExceptionManage.dispose(e, getClass());
	   }finally
	   {
		   boxModel = null;
		   portList = null;
		   keyValue = null;
		   UiUtil.closeService_MB(portService);
		   UiUtil.closeService_MB(tunnelServiceMB);
		   boxModel = null;
		   pwInfo = null;
		   tunnelInfo = null;
		   tunnelInfoList = null;
		   portIdList = null;
	   }
	}

	
	private void addListeners() {
		
		/************取消事件***************/
		btnCanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelAciont();
			}
		});
		
		autoNameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				autoNameAction();
			}
		});
		
		acportBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1)
				{
					try 
					{
						getComboBoxDataUtil().initAcData(acportBox, acBox);
						
					} catch (Exception e1) 
					{
						ExceptionManage.dispose(e1,this.getClass());
					}
				}
			}
		});
		
		btnSave.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}

			@Override
			public boolean checking() {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		
		standPwBox.addItemListener( new ItemListener() 
		{
			
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
             if(e.getStateChange() == 1)
             {
            	 initPortBox(standPortBox,standPwBox); 
             }
			}
		});
		
		mainPwBox.addItemListener( new ItemListener() 
		{
			
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
             if(e.getStateChange() == 1)
             {
            	 initPortBox(mainPortBox,mainPwBox); 
             }
			}
		});
	}
	

	private void cancelAciont() 
	{
       this.dispose();		
	}

	
	private void autoNameAction() 
	{
		DualInfo dualInfo = null;
		AutoNamingUtil autoNaming = null;
		String autoName = "";
		try 
		{
			dualInfo = new DualInfo();
			dualInfo.setIsSingle(1);
			dualInfo.setaSiteId(ConstantUtil.siteId);
			autoNaming = new AutoNamingUtil();
			autoName = (String) autoNaming.autoNaming(dualInfo, null, null);
			txtName.setText(autoName);
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			 dualInfo = null;
			 autoNaming = null;
			 autoName = "";
		}
	}
	
    /**********保存时间*************/
	private void saveAction() 
	{
		PwInfo pwInfoMain = null;
		PwInfo pwInfoStand = null;
		AcPortInfo acportInfo = null;
		DispatchUtil  dispatchUtil  = null;
		String result = "" ;
		SiteService_MB siteService = null;
		try 
		{
			//验证PW的正确性
			if(!checkPw())
			{
				return ;
			}
			//验证名称的可用性
			if(!isExistSameName()){
				return ;
			}
			//验证AC是否选择
			if(!isSelectAc()){
				return ;
			}
			for( DualInfo dualInfo: dualInfoList)
			{
				pwInfoMain = (PwInfo) ((ControlKeyValue)mainPwBox.getSelectedItem()).getObject();
				pwInfoStand = (PwInfo) ((ControlKeyValue)standPwBox.getSelectedItem()).getObject();
				if(dualInfo.getPwProtect() != null)
				{
					dualInfo.getPwProtect().setSiteId(ConstantUtil.siteId);
					dualInfo.getPwProtect().setBackType(isBackJhechBox.isSelected() ? 0:1);
					dualInfo.getPwProtect().setDelayTime(Integer.parseInt(txtDelayTime.getText().trim()));
					dualInfo.getPwProtect().setMainPwId(pwInfoMain.getPwId());
					dualInfo.getPwProtect().setMainTunnelId(pwInfoMain.getTunnelId());
					dualInfo.getPwProtect().setStandPwId(pwInfoStand.getPwId());
					dualInfo.getPwProtect().setStandTunnelId(pwInfoStand.getTunnelId());
					dualInfo.setPwId(pwInfoMain.getPwId());
				}else
				{
					dualInfo.setPwId(pwInfoStand.getPwId());
				}
				acportInfo = (AcPortInfo) ((ControlKeyValue)acBox.getSelectedItem()).getObject();
				dualInfo.setaAcId(acportInfo.getId());
				dualInfo.setActiveStatus(chbActivate.isSelected() ? 1:0 );
				dualInfo.setRootSite(ConstantUtil.siteId);
				dualInfo.setName(this.txtName.getText().trim());
				dualInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
				dualInfo.setCreateUser(ConstantUtil.user.getUser_Name());
				dualInfo.setServiceType(EServiceType.DUAL.getValue());
				dualInfo.setIsSingle(1);
			}
			dispatchUtil = new DispatchUtil(RmiKeys.RMI_DUAL);
			result = dispatchUtil.excuteInsert(dualInfoList);
			DialogBoxUtil.succeedDialog(this, result);
			//添加日志记录
			VplsInfo vpls_Log = new VplsInfo();
			vpls_Log.setVplsName(this.txtName.getText().trim());
			vpls_Log.setActiveStatus(chbActivate.isSelected() ? 1:0);
			DualInfo dual_Log = new DualInfo();
			vpls_Log.setDualInfo(dual_Log);
			PwProtect pwPro_Log = new PwProtect();
			dual_Log.setPwProtect(pwPro_Log);
			pwPro_Log.setDelayTime(Integer.parseInt(txtDelayTime.getText().trim()));
			pwPro_Log.setBackType(isBackJhechBox.isSelected() ? 1:0);
			dual_Log.setNode(null);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			dual_Log.setRootName(siteService.getSiteName(ConstantUtil.siteId));
			dual_Log.setRootAcName(((AcPortInfo)((ControlKeyValue)acBox.getSelectedItem()).getObject()).getName());
			dual_Log.setBranchMainPwName(((PwInfo)((ControlKeyValue)mainPwBox.getSelectedItem()).getObject()).getPwName());
			dual_Log.setBranchProtectPwName(((PwInfo)((ControlKeyValue)standPwBox.getSelectedItem()).getObject()).getPwName());
			//源节点
			AddOperateLog.insertOperLog(btnSave, EOperationLogType.DUALPROTECTROTATEINSERT.getValue(), result, 
					null, vpls_Log, ConstantUtil.siteId, vpls_Log.getVplsName(), "dual");
			this.dispose();
			dualContorller.refresh();
		} catch (Exception e) 
		{
//			e.printStackTrace();
           ExceptionManage.dispose(e, getClass());
		}
		finally
		{
			UiUtil.closeService_MB(siteService);
		}
	}
	
	
	private boolean isSelectAc()
	{
		boolean flag = false;
	 try 
	 {
		 if(acBox.getSelectedItem() == null)
		 {
			 DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_DUALACERROR));
			 return false;
		 }
		 flag = true;
		
	} catch (Exception e) 
	{
		ExceptionManage.dispose(e, getClass());
	}	
	return flag;
	}
	
	
	
	private boolean isExistSameName()
	{
		String beforeName = "";
		boolean flag = false;
		VerifyNameUtil verifyNameUtil = null;
		try 
		{
			if(dualInfoList != null && dualInfoList.size() >0 && dualInfoList.get(0).getId() != 0)
			{
				beforeName = dualInfoList.get(0).getName();
			}
			verifyNameUtil = new VerifyNameUtil();
			if(verifyNameUtil.verifyName(EServiceType.DUAL.getValue(), this.txtName.getText().trim(), beforeName))
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return false;
			}
			flag = true;
		} catch (Exception e)
		{
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	
	/**
	 * 验证PW的正确性
	 * @return
	 */
	private boolean checkPw() 
	{
		boolean flag = false;
		try {
			if(mainPortBox.getSelectedItem() == null || standPortBox.getSelectedItem() == null )
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_DUALPWERROR1));
				return false;
			}else
			{
				if(Integer.parseInt(((ControlKeyValue)mainPortBox.getSelectedItem()).getId()) == Integer.parseInt(((ControlKeyValue)standPortBox.getSelectedItem()).getId())){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_DUALPWERROR2));
					return false;
				}
				else if(Integer.parseInt(((ControlKeyValue)mainPwBox.getSelectedItem()).getId()) == Integer.parseInt(((ControlKeyValue)standPwBox.getSelectedItem()).getId())){
					DialogBoxUtil.errorDialog(this,ResourceUtil.srcStr(StringKeysLbl.LBL_DUALPWERROR3));
					return false;
				}
				flag = true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	  return flag;
    }
	
	
	private void setLayout() 
	{
		/**************设置按钮的布局***********************/
		super.setButtonLayout(btnSave, btnCanel, buttonPanel, this);
		
		GridBagLayout componetLayout = new GridBagLayout();
		componetLayout.columnWidths = new int[]{10,150,10,150};
		componetLayout.columnWeights = new double[]{0,0,0,0};
		componetLayout.rowHeights = new int[]{30,30,30,30,30,30,30,30};
		componetLayout.rowWeights = new double[]{0,0,0,0};
		this.setLayout(componetLayout);
		GridBagConstraints c  = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(1, 5, 5, 5);
		componetLayout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 5, 10);
		componetLayout.setConstraints(lblName, c);
		this.add(lblName);
		c.gridx = 1;
		c.gridwidth = 1;
		componetLayout.setConstraints(txtName, c);
		this.add(this.txtName);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(autoNameButton, c); 
		this.add(this.autoNameButton);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 5, 10);
		componetLayout.setConstraints(mainPortLabel, c);
		this.add(mainPortLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(mainPortBox, c);
		this.add(this.mainPortBox);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(mainPwLabel, c);
		this.add(this.mainPwLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(mainPwBox, c);
		this.add(this.mainPwBox);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 5, 10);
		componetLayout.setConstraints(standPortLabel, c);
		this.add(standPortLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(standPortBox, c);
		this.add(this.standPortBox);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(standPwLabel, c);
		this.add(this.standPwLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(standPwBox, c);
		this.add(this.standPwBox);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 5, 10);
		componetLayout.setConstraints(acPortLabel, c);
		this.add(acPortLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(acportBox, c);
		this.add(this.acportBox);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(acLabel, c);
		this.add(this.acLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(acBox, c);
		this.add(this.acBox);
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 5, 10);
		componetLayout.setConstraints(lbldelayTime, c);
		this.add(lbldelayTime);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(txtDelayTime, c);
		this.add(this.txtDelayTime);
		c.gridx = 2;
		c.gridwidth =1;
		componetLayout.setConstraints(isBackLabel, c);
		this.add(this.isBackLabel);
		c.gridx = 3;
		c.gridwidth =1;
		componetLayout.setConstraints(isBackJhechBox, c);
		this.add(this.isBackJhechBox);
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 5, 10);
		componetLayout.setConstraints(activeLabel, c);
		this.add(activeLabel);
		c.gridx = 1;
		c.gridwidth =1;
		componetLayout.setConstraints(chbActivate, c);
		this.add(this.chbActivate);
		
		c.fill =GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(20, 0, 5, 5);
		c.gridx = 3;
		c.gridy = 7;
		c.gridwidth=3;
		componetLayout.setConstraints(this.buttonPanel, c);
		this.add(this.buttonPanel);
		
	}

	private void initComponents() throws Exception {
		lblName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
		this.btnCanel = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		lblMessage = new JLabel();
		this.txtName = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
		this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_DUAL));
		autoNameButton = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		mainPortLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DUALMAINPORT));
		mainPortBox = new JComboBox();
		mainPortBox.setEnabled(false);
		mainPortBox.setPreferredSize(new Dimension(180,5));
		mainPwLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DUALMAINPW));
		mainPwBox = new JComboBox();
		mainPwBox.setPreferredSize(new Dimension(180,5));
		standPortLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DUALSTANDPORT));
		standPortBox = new JComboBox();
		standPortBox.setEnabled(false);
		standPortBox.setPreferredSize(new Dimension(180,5));
		standPwLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DUALSTANDPW));
		standPwBox = new JComboBox();
		standPwBox.setPreferredSize(new Dimension(180,5));
		acPortLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DUALPORT));
		acportBox = new JComboBox();
		acportBox.setPreferredSize(new Dimension(180,5));
		acLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_AC));
		acBox = new JComboBox();
		acBox.setPreferredSize(new Dimension(180,5));
		activeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		chbActivate = new JCheckBox();
		lbldelayTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME));;
//		txtDelayTime = new PtnSpinner(PtnSpinner.TYPE_DELAYTIME);
		txtDelayTime = new PtnTextField(true, PtnTextField.TYPE_INT, 255, this.lblMessage,btnSave , this);
		txtDelayTime.setCheckingMaxValue(true);
		txtDelayTime.setCheckingMinValue(true);
		txtDelayTime.setMaxValue(255);
		txtDelayTime.setMinValue(0);
		txtDelayTime.setText("0");
		
		isBackLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BACK));;
		isBackJhechBox = new JCheckBox();
		buttonPanel = new JPanel();
	}
	
}

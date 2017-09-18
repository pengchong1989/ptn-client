package com.nms.ui.ptn.ne.CCCManagement.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import com.nms.db.bean.ptn.CccInfo;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.ptn.CccService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
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
import com.nms.ui.manager.keys.StringKeysTitle;

public class CccEditDialog extends PtnDialog {
	private static final long serialVersionUID = -6800612781484665905L;
	private CccInfo cccInfo;
	private CccPanel cccPanel;
	
	public CccEditDialog(CccInfo cccInfo, CccPanel cccPanel) {

		try {
			this.cccInfo = cccInfo;
			this.cccPanel = cccPanel;
			this.initComponent();
			this.setLayout();
			this.listAcData();
			this.addListener();
			this.initData();
			if(ResourceUtil.language.equals("zh_CN")){
				UiUtil.showWindow(this, 510, 340);
			}else{
				UiUtil.showWindow(this, 550, 450);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 初始化表单数据
	 * @throws Exception 
	 */
	private void initData() throws Exception {
		try {
			//初始化ac
			if (null == this.cccInfo) {
				this.cccInfo = new CccInfo();
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_CCC));
			}else{
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_CCC));
				this.txtname.setText(this.cccInfo.getName());
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 添加监听
	 */
	private void addListener() {
		this.btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CccEditDialog.this.dispose();
			}
		});

		this.btnSave.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {	
					saveInfo();
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				}
			}
			@Override
			public boolean checking() {
				
				return true;
			}
		});
		// 自动命名事件
		jButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonActionPerformed(evt);
			}
		});
		
		
		this.btnAcRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					btnRightAction(listAC,listSelectAC);
				} catch (Exception e) {
					e.printStackTrace();
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		
		this.btnAcLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					btnRightAction(listSelectAC,listAC);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
	}

	/**
	 * 向右选择pw按钮事件
	 * 
	 * @throws Exception
	 */
	private void btnRightAction(JList sourceList,JList purposeList) throws Exception {
		int index = 0;
		DefaultListModel defaultListModel = null;
		DefaultListModel defaultListModel_select = null;
		try {
			index = sourceList.getSelectedIndex();
			if (index >= 0) {
				defaultListModel_select = (DefaultListModel) purposeList.getModel();
				defaultListModel = (DefaultListModel) sourceList.getModel();
				defaultListModel_select.addElement(defaultListModel.getElementAt(index));
				defaultListModel.removeElementAt(index);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			defaultListModel = null;
			defaultListModel_select = null;
		}
	}

	

	private void listAcData() 
	{
		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acportInfoList = null;
		DefaultListModel defaultListModel = null;
		DefaultListModel defaultListModelSel = null;
		List<CccInfo> cccInfoList = null;
		CccService_MB cccService = null;
		List<Integer> acIdList = null;
		Set<Integer> acSet = null;
		UiUtil uiUtil = null;
		try 
		{
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acportInfoList = acInfoService.selectBySiteId(ConstantUtil.siteId);
			defaultListModel = new DefaultListModel();
			if (acportInfoList != null && acportInfoList.size() > 0) {
				for (AcPortInfo acPortInfo : acportInfoList) {
					if (acPortInfo.getIsUser() == 0) {
						defaultListModel.addElement(new ControlKeyValue(acPortInfo.getId() + "", acPortInfo.getName(), acPortInfo));
					}
				}
			}
			this.listAC.setModel(defaultListModel);
			//选中的AC
			if(null!=this.cccInfo){
				uiUtil = new UiUtil();
				cccService = (CccService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CCCMANAGEMENT);
				cccInfoList =  cccService.selectByServiceId(this.cccInfo.getServiceId());
				defaultListModelSel = new DefaultListModel();
				acSet = new HashSet<Integer>();
				acIdList = new ArrayList<Integer>();
				for (CccInfo cccInfo : cccInfoList) {
					acSet.addAll(uiUtil.getAcIdSets(cccInfo.getAmostAcId()));					
				}
				acIdList = new ArrayList<Integer>(acSet);
				acportInfoList = acInfoService.select(acIdList);
				if (acportInfoList != null && acportInfoList.size() > 0) {
					for (AcPortInfo acPortInfo : acportInfoList) {
						defaultListModelSel.addElement(new ControlKeyValue(acPortInfo.getId() + "", acPortInfo.getName(), acPortInfo));
					}
				}				
				this.listSelectAC.setModel(defaultListModelSel);
			}else{
				this.listSelectAC.setModel(new DefaultListModel());
			}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(cccService);
			acportInfoList = null;
			defaultListModel = null;
			acportInfoList = null;
			defaultListModelSel = null;
			cccInfoList = null;
			acIdList = null;
		}
	}
	
	// 自动命名事件
	private void jButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {

			cccInfo.setaSiteId(ConstantUtil.siteId);
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			String autoNaming = (String) autoNamingUtil.autoNaming(cccInfo, null, null);
			txtname.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 保存方法
	 * 
	 * @throws Exception
	 */
	private void saveInfo() throws Exception {
		List<Integer> acIdList = null;
		DefaultListModel defaultListModelAc = null;
		CccInfo cccInfo1 = null;		
		DispatchUtil cccDispatch = null;
		String resultStr = null;
		String beforeName = null;
		List<AcPortInfo> useAcPortList = null;
		AcPortInfoService_MB acInfoService = null;
		try {
			if (!this.isFull()) {
				return;
			}

			// 验证名称是否存在
			if (this.cccInfo.getId() != 0) {
				beforeName = this.cccInfo.getName();
			}

			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyNameBySingle(EServiceType.CCC.getValue(), this.txtname.getText().trim(), beforeName, ConstantUtil.siteId)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return;
			}

						
			defaultListModelAc  = (DefaultListModel) this.listSelectAC.getModel();
			if(defaultListModelAc.getSize()>10)
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_EXCEEDACNUMBER));
				return;
			}
			
			acIdList = new ArrayList<Integer>();
			useAcPortList = new ArrayList<AcPortInfo>();
			getAllAcId(acIdList,useAcPortList);
									
				cccInfo1 = new CccInfo();
				cccInfo1.setName(this.txtname.getText().trim());
				cccInfo1.setaSiteId(ConstantUtil.siteId);
				cccInfo1.setServiceType(EServiceType.CCC.getValue());
				cccInfo1.setActiveStatus(this.chkactivate.isSelected() ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
				cccInfo1.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
				cccInfo1.setCreateUser(ConstantUtil.user.getUser_Name());
				cccInfo1.setAmostAcId(acIdList.toString().subSequence(1, acIdList.toString().length() -1).toString());				
			
			cccDispatch = new DispatchUtil(RmiKeys.RMI_CCC);
			//日志记录
			List<CommonBean> acNameList = new ArrayList<CommonBean>();
			for (AcPortInfo acPortInfo : useAcPortList) {
				CommonBean acName = new CommonBean();
				acName.setAcName(acPortInfo.getName());
				acNameList.add(acName);
			}
			cccInfo1.setAcNameList(acNameList);
			cccInfo1.setNode(null);
			if(0==this.cccInfo.getId()){
				resultStr = cccDispatch.excuteInsert(cccInfo1);
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.INSERTCCC.getValue(), resultStr,
						null, cccInfo1, ConstantUtil.siteId, cccInfo1.getName(), "ccc");
			}else{
				List<Integer> acIdLists = new ArrayList<Integer>();
				String acIds=this.cccInfo.getAmostAcId();											 
			    String []macs= acIds.split("\\, ");							     
				for(int j=0;j<macs.length;j++){							    	 
					acIdLists.add(Integer.parseInt(macs[j]));
	            }
				if(acIdLists.size()==acIdList.size()){
					if(acIdLists.containsAll(acIdList) ){
						cccInfo1.setAction(0);
					}else{
						cccInfo1.setAction(1);
					}
					
				}else{
					  cccInfo1.setAction(1);
				}
				acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
				cccInfo1.setBeforeAAcList(acInfoService.select(acIdLists));
				cccInfo1.setServiceId(this.cccInfo.getServiceId());
				cccInfo1.setId(this.cccInfo.getId());
				cccInfo1.setBeforeActiveStatus(this.cccInfo.getActiveStatus());
				cccInfo1.setaXcId(this.cccInfo.getaXcId());
				resultStr = cccDispatch.excuteUpdate(cccInfo1);
				CccInfo cccBefore = new CccInfo();
				cccBefore.setName(this.cccInfo.getName());
				cccBefore.setActiveStatus(this.cccInfo.getActiveStatus());
				List<CommonBean> acNameList_before = new ArrayList<CommonBean>();
				for (AcPortInfo acPortInfo : cccInfo1.getBeforeAAcList()) {
					CommonBean acName = new CommonBean();
					acName.setAcName(acPortInfo.getName());
					acNameList_before.add(acName);
				}
				cccBefore.setNode(null);
				cccBefore.setAcNameList(acNameList_before);
				cccInfo1.setBeforeAAcList(null);
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.UPDATECCC.getValue(), resultStr,
						cccBefore, cccInfo1, ConstantUtil.siteId, cccInfo1.getName(), "ccc");
			}
			DialogBoxUtil.succeedDialog(this, resultStr);
			//添加日志记录
			this.cccPanel.getController().refresh();
			this.dispose();

		} catch (Exception e) {
			throw e;
		} finally {
			cccInfo1 = null;
			cccDispatch = null;
			defaultListModelAc = null;
			resultStr = null;
			UiUtil.closeService_MB(acInfoService);
		}

	}
	
	private void getAllAcId(List<Integer> acIdList,List<AcPortInfo> useAcPortList) throws Exception 
	{
		DefaultListModel defaultListModel = null;
		try 
		{
			defaultListModel = (DefaultListModel) this.listSelectAC.getModel();
			for (int i = 0; i < defaultListModel.getSize(); i++) 
			{
				acIdList.add(Integer.parseInt(((ControlKeyValue) defaultListModel.getElementAt(i)).getId()));
				useAcPortList.add((AcPortInfo)((ControlKeyValue) defaultListModel.getElementAt(i)).getObject());
			}
		} catch (Exception e) 
		{
			throw e;
		}finally
		{
			defaultListModel = null;
		}
	}
	
	
	
	
			
	
	/**
	 * 验证是否填写完整
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isFull() throws Exception {

		boolean flag = true;
		DefaultListModel defaultListacModel = null;
		try {
			defaultListacModel = (DefaultListModel) this.listSelectAC.getModel();
			if(defaultListacModel.size() == 0)
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_AC));
				flag = false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			defaultListacModel= null;
		}

		return flag;
	}

	
	/**
	 * 设置布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 150, 50, 100, 50 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 25, 40, 40, 40, 40, 40, 15 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0, 0, 0.2 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 6;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 15, 5);
		componentLayout.setConstraints(this.lblname, c);
		this.add(this.lblname);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 15, 5);
		componentLayout.setConstraints(this.txtname, c);
		this.add(this.txtname);
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.jButton, c);
		this.add(this.jButton);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 4;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblAC, c);
		this.add(this.lblAC);
		c.gridx = 1;
		componentLayout.setConstraints(this.slpAC, c);
		this.add(this.slpAC);
		c.gridx = 3;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.slpSelectAC, c);
		this.add(this.slpSelectAC);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.btnAcRight, c);
		this.add(this.btnAcRight);
		c.gridy = 4;
		componentLayout.setConstraints(this.btnAcLeft, c);
		this.add(this.btnAcLeft);
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblactivate, c);
		this.add(this.lblactivate);
		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		componentLayout.setConstraints(this.chkactivate, c);
		this.add(this.chkactivate);

		c.anchor = GridBagConstraints.EAST;
		c.gridx = 3;
		c.gridy = 7;
		componentLayout.setConstraints(this.btnSave, c);
		this.add(this.btnSave);
		c.gridx = 4;
		componentLayout.setConstraints(this.btnCanel, c);
		this.add(this.btnCanel);

	}

	/**
	 * 初始化控件
	 * 
	 * @throws Exception
	 */
	private void initComponent() throws Exception {
		this.lblMessage = new JLabel();
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.lblname = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		jButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		this.txtname = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
		this.lblactivate = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		this.chkactivate = new JCheckBox();		
		/*********添加多AC端口 2015-3-10 张坤***********/
		lblAC = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_AC));
		listAC = new JList();
		slpAC = new JScrollPane();
		slpAC.setViewportView(listAC); 
		listSelectAC = new JList();
		slpSelectAC = new JScrollPane();
		slpSelectAC.setViewportView(listSelectAC);	
		btnAcLeft = new JButton("<<");
		btnAcRight = new JButton(">>");
		if(null!=this.cccInfo){
			this.chkactivate.setSelected(this.cccInfo.getActiveStatus()==1?true:false);
		}
	}

	
	
	private JLabel lblname;
	private JTextField txtname;
	private JButton jButton;
	private JLabel lblactivate;
	private JCheckBox chkactivate;
	private PtnButton btnSave;
	private JButton btnCanel;	
	private JLabel lblMessage;
	/***********多AC**************/
	private JLabel lblAC;
	private JScrollPane slpAC;
	private JList listAC;
	private JScrollPane slpSelectAC;
	private JList listSelectAC;
	private JButton btnAcLeft;
	private JButton btnAcRight;
}

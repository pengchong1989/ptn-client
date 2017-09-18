package com.nms.ui.ptn.ne.arp.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.ptn.ARPInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.protect.PwProtect;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.ptn.ARPInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.protect.PwProtectService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.AutoNamingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
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
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class ARPAddDialog extends PtnDialog {
	private static final long serialVersionUID = 1L;
	private ARPInfo arpInfo = null;
	private ARPPanel panel = null;
	private ARPInfo arpInfoBefore = null;//log记录需要
	public ARPAddDialog(ARPInfo arpInfo, ARPPanel view) {
		try {
			this.arpInfo = arpInfo;
			this.panel = view;
			this.initComponent();
			this.setLayout();
			this.addListener();
			this.comboBoxData();
			this.initData();
			UiUtil.showWindow(this, 480, 450);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void initComponent() throws Exception {
		this.messageLbl = new JLabel();
		this.btnPanel = new JPanel();
		this.saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE), true, RootFactory.COREMODU, this);
		this.cancelBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.nameLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		this.nameTxt = new PtnTextField(true,PtnTextField.STRING_MAXLENGTH, this.messageLbl, this.saveBtn, this);
		this.autoNamingBtn = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		this.dualNameLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DUAL_NAME));
		this.dualNameCmb = new JComboBox();
		this.sourceMacLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_MAC));
		this.sourceMacTxt = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, messageLbl, saveBtn, this);
		this.vlanEnabledLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_ENABLED));
		this.vlanEnabledCmb = new JComboBox();
		this.vlanIdLbl = new JLabel("vlan Id");
		this.vlanIdTxt = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, messageLbl, saveBtn, this);
		this.vlanPriLbl = new JLabel("vlan pri");
		this.vlanPriTxt = new PtnTextField(true,PtnTextField.TYPE_INT,PtnTextField.INT_MAXLENGTH, messageLbl, saveBtn, this);
		this.sourceIpLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_IP));
		this.sourceIpTxt = new PtnTextField(true, PtnTextField.TYPE_IP, PtnTextField.IP_MAXLENGTH, messageLbl, saveBtn, this);
		this.targetIpLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_END_IP));
		this.targetIpTxt = new PtnTextField(true, PtnTextField.TYPE_IP, PtnTextField.IP_MAXLENGTH, messageLbl, saveBtn, this);
		this.enabledLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ENABLED_STATUS));
		this.enabledCmb = new JComboBox();
	}

	private void setLayout() {
		this.setButtonLayout();
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 70, 270, 80 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 25, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,30 };
		componentLayout.rowWeights = new double[] { 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		int i = 0;
		// 第一行 错误提示
		c.gridx = 0;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.messageLbl, c);
		this.add(this.messageLbl);
		
		// 第一行 名称
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		componentLayout.setConstraints(this.nameLbl, c);
		this.add(this.nameLbl);
		c.gridx = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.nameTxt, c);
		this.add(this.nameTxt);
		
		c.gridx = 2;
		c.gridy = i++;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.autoNamingBtn, c);
		this.add(this.autoNamingBtn);
		

		// 第二行 dual名称
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.dualNameLbl, c);
		this.add(this.dualNameLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.dualNameCmb, c);
		this.add(this.dualNameCmb);

		
		// 第三行 源mac
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.sourceMacLbl, c);
		this.add(this.sourceMacLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.sourceMacTxt, c);
		this.add(this.sourceMacTxt);
		
		
		// 第四行  vlan使能
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.vlanEnabledLbl, c);
		this.add(this.vlanEnabledLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.vlanEnabledCmb, c);
		this.add(this.vlanEnabledCmb);

		// 第五行 vlanid
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.vlanIdLbl, c);
		this.add(this.vlanIdLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.vlanIdTxt, c);
		this.add(this.vlanIdTxt);

		// 第六行 vlanpri
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.vlanPriLbl, c);
		this.add(this.vlanPriLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.vlanPriTxt, c);
		this.add(this.vlanPriTxt);

		// 第七行 源ip
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.sourceIpLbl, c);
		this.add(this.sourceIpLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.sourceIpTxt, c);
		this.add(this.sourceIpTxt);

		// 第八行 目的ip
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.targetIpLbl, c);
		this.add(this.targetIpLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.targetIpTxt, c);
		this.add(this.targetIpTxt);

		// 第九行 使能
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.enabledLbl, c);
		this.add(this.enabledLbl);
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.enabledCmb, c);
		this.add(this.enabledCmb);
		
		// 第十行 按钮
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.gridy = i;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = i++;
		c.gridwidth=3;
		componentLayout.setConstraints(this.btnPanel, c);
		this.add(this.btnPanel);
	}

	private void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 140,30,30 };
		componentLayout.columnWeights = new double[] { 0.1,0, 0};
		componentLayout.rowHeights = new int[] {  30 };
		componentLayout.rowWeights = new double[] { 0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.saveBtn, c);
		btnPanel.add(this.saveBtn);
		c.gridx = 2;
		componentLayout.setConstraints(this.cancelBtn, c);
		btnPanel.add(this.cancelBtn);
	}

	private void comboBoxData() throws Exception {
		PwProtectService_MB service = null;
		DualInfoService_MB dualService = null;
		ARPInfoService_MB arpService = null;
		try {
			service = (PwProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PWPROTECT);
			PwProtect pwProtect = new PwProtect();
			pwProtect.setSiteId(ConstantUtil.siteId);
			List<PwProtect> pwProtectList = service.select(pwProtect);
			if(pwProtectList != null && pwProtectList.size() > 0){
				arpService = (ARPInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ARP);
				List<Integer> usedDualList = new ArrayList<Integer>();
				List<ARPInfo> arpList = arpService.queryBySiteId(ConstantUtil.siteId);
				for (ARPInfo arpInfo : arpList) {
					usedDualList.add(arpInfo.getPwProtectId());
				}
				dualService = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
				DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) this.dualNameCmb.getModel();
				PwProtect pwPro_update = null;
				for (PwProtect pwPro : pwProtectList) {
					if(this.arpInfo != null && this.arpInfo.getPwProtectId() == pwPro.getBusinessId()){
						pwPro_update = pwPro;
					}
					if(!usedDualList.contains(pwPro.getBusinessId())){
						DualInfo dual = dualService.queryById(pwPro.getServiceId());
						if(dual != null){
							defaultComboBoxModel.addElement(new ControlKeyValue(pwPro.getBusinessId() + "", dual.getName(), pwPro));
						}
					}
				}
				if(pwPro_update != null){
					DualInfo dual = dualService.queryById(pwPro_update.getServiceId());
					if(dual != null){
						defaultComboBoxModel.addElement(new ControlKeyValue(pwPro_update.getBusinessId() + "", dual.getName(), pwPro_update));
					}
				}
				this.dualNameCmb.setModel(defaultComboBoxModel);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(arpService);
			UiUtil.closeService_MB(dualService);
			UiUtil.closeService_MB(service);
		}
		this.sourceMacTxt.setText("00-00-00-00-00-01");
		super.getComboBoxDataUtil().comboBoxData(this.vlanEnabledCmb, "ENABLEDSTATUE");
		this.vlanIdTxt.setText("2");
		this.vlanIdTxt.setMaxValue(4095);
		this.vlanIdTxt.setMinValue(2);
		this.vlanIdTxt.setCheckingMaxValue(true);
		this.vlanIdTxt.setCheckingMinValue(true);
		this.vlanPriTxt.setText("0");
		this.vlanPriTxt.setMaxValue(7);
		this.vlanPriTxt.setMinValue(0);
		this.vlanPriTxt.setCheckingMaxValue(true);
		this.vlanPriTxt.setCheckingMinValue(true);
		this.sourceIpTxt.setText("10.18.2.1");
		this.targetIpTxt.setText("10.18.3.2");
		super.getComboBoxDataUtil().comboBoxData(this.enabledCmb, "ENABLEDSTATUE");
	}
	
	private void initData() {
		if(this.arpInfo == null){
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_ARP));
		}else{
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_ARP));
			this.dualNameCmb.setEnabled(false);
			this.nameTxt.setText(this.arpInfo.getName());
			super.getComboBoxDataUtil().comboBoxSelect(this.dualNameCmb, this.arpInfo.getPwProtectId() + "");
			this.sourceMacTxt.setText(this.arpInfo.getSourceMac());
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.vlanEnabledCmb, this.arpInfo.getVlanEnabled() + "");
			this.vlanIdTxt.setText(this.arpInfo.getVlanId()+"");
			this.vlanPriTxt.setText(this.arpInfo.getVlanPri()+"");
			this.sourceIpTxt.setText(this.arpInfo.getSourceIp());
			this.targetIpTxt.setText(this.arpInfo.getTargetIp());
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.enabledCmb, this.arpInfo.getEnabled() + "");
			this.arpInfoBefore = new ARPInfo();
			this.arpInfoBefore.setName(this.arpInfo.getName());
			this.arpInfoBefore.setSourceMac(this.arpInfo.getSourceMac());
			this.arpInfoBefore.setVlanEnabled(this.arpInfo.getVlanEnabled());
			this.arpInfoBefore.setVlanId(this.arpInfo.getVlanId());
			this.arpInfoBefore.setVlanPri(this.arpInfo.getVlanPri());
			this.arpInfoBefore.setSourceIp(this.arpInfo.getSourceIp());
			this.arpInfoBefore.setTargetIp(this.arpInfo.getTargetIp());
			this.arpInfoBefore.setEnabled(this.arpInfo.getEnabled());
		}
	}

	private void addListener() {
		this.autoNamingBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				autoNamingActionPerformed();
			}
		});
		
		this.vlanEnabledCmb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Code codePwType;
				try {
					codePwType = (Code) ((ControlKeyValue) vlanEnabledCmb.getSelectedItem()).getObject();
					if("1".equals(codePwType.getCodeValue())){
						vlanIdTxt.setEnabled(true);
						vlanPriTxt.setEnabled(true);
					}else{
						vlanIdTxt.setEnabled(false);
						vlanPriTxt.setEnabled(false);
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		
		this.saveBtn.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAction();
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		
		this.cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}
	
	/**
	 * 自动命名
	 */
	private void autoNamingActionPerformed() {
		try {
			AutoNamingUtil autoNamingUtil = new AutoNamingUtil();
			ARPInfo info = new ARPInfo();
			info.setSiteId(ConstantUtil.siteId);
			String autoNaming = (String) autoNamingUtil.autoNaming(info, null, null);
			this.nameTxt.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void saveAction(){
		VerifyNameUtil verifyNameUtil = new VerifyNameUtil();
		String beforeName = null;
		if(this.arpInfo != null){
			beforeName = this.arpInfo.getName();
		}
		if(verifyNameUtil.verifyNameBySingle(EServiceType.ARP.getValue(), this.nameTxt.getText().trim(), beforeName, ConstantUtil.siteId)){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
			return;
		}
		if(null == this.dualNameCmb.getSelectedItem()){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_ADD_DUAL_AND_IS_SOURCE));
			return;
		}
		if(this.arpInfo == null){
			this.arpInfo = new ARPInfo();
		}
		this.arpInfo.setName(this.nameTxt.getText().trim());
		this.arpInfo.setPwProtectId(Integer.parseInt(((ControlKeyValue)this.dualNameCmb.getSelectedItem()).getId()));
		this.arpInfo.setDualName(((ControlKeyValue)this.dualNameCmb.getSelectedItem()).getName());
		this.arpInfo.setSourceMac(this.sourceMacTxt.getText().trim());
		this.arpInfo.setVlanEnabled(Integer.parseInt(((Code)((ControlKeyValue)this.vlanEnabledCmb.getSelectedItem()).getObject()).getCodeValue()));
		this.arpInfo.setVlanId(Integer.parseInt(this.vlanIdTxt.getText().trim()));
		this.arpInfo.setVlanPri(Integer.parseInt(this.vlanPriTxt.getText().trim()));
		this.arpInfo.setSourceIp(this.sourceIpTxt.getText().trim());
		this.arpInfo.setTargetIp(this.targetIpTxt.getText().trim());
		this.arpInfo.setEnabled(Integer.parseInt(((Code)((ControlKeyValue)this.enabledCmb.getSelectedItem()).getObject()).getCodeValue()));
		this.arpInfo.setSiteId(ConstantUtil.siteId);
		String result = null;
		try {
			DispatchUtil arpDispatch = new DispatchUtil(RmiKeys.RMI_ARP);
			if(this.arpInfo.getId()  == 0){
				result = arpDispatch.excuteInsert(this.arpInfo);
				//添加日志记录
				AddOperateLog.insertOperLog(saveBtn, EOperationLogType.ADDARP.getValue(), result,
						null, this.arpInfo, ConstantUtil.siteId, this.arpInfo.getName(), "arp");
			}else{
				result = arpDispatch.excuteUpdate(this.arpInfo);
				//添加日志记录
				this.arpInfo.setDualName(null);
				AddOperateLog.insertOperLog(saveBtn, EOperationLogType.UPDATEARP.getValue(), result,
						this.arpInfoBefore, this.arpInfo, ConstantUtil.siteId, this.arpInfo.getName(), "arp");
			}
			DialogBoxUtil.succeedDialog(this, result);
			this.panel.getController().refresh();
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private JLabel messageLbl;//提示信息
	private JLabel nameLbl;//名称
	private JTextField nameTxt;
	private JButton autoNamingBtn;//自动命名
	private JLabel dualNameLbl;//双规保护名称
	private JComboBox dualNameCmb;
	private JLabel sourceMacLbl;//源mac
	private PtnTextField sourceMacTxt;
	private JLabel vlanEnabledLbl;//vlan使能
	private JComboBox vlanEnabledCmb;
	private JLabel vlanIdLbl;//vlanid
	private PtnTextField vlanIdTxt;
	private JLabel vlanPriLbl;//vlanpri
	private PtnTextField vlanPriTxt;
	private JLabel sourceIpLbl;//源ip
	private PtnTextField sourceIpTxt;
	private JLabel targetIpLbl;//目的ip
	private PtnTextField targetIpTxt;
	private JLabel enabledLbl;//arp使能
	private JComboBox enabledCmb;
	private JPanel btnPanel;
	private PtnButton saveBtn;//保存
	private JButton cancelBtn;//取消
}

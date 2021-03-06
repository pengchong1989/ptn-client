package com.nms.ui.ptn.systemconfig.dialog.siteInitialise;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.system.Field;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;

public class SNConfigDialog extends PtnDialog{
	private SiteInst siteInst;
	private JLabel snJLabel;
	private PtnTextField snptnTextField;
	private JLabel newSNJLabel;
	private PtnTextField newSNPtnTextField;
	private JLabel lblMessage;
	private PtnButton ptnButton;
	private PtnButton canceButton;
	private SNConfigDialog ipConfigDialog;
	private JLabel groupJLabel;
	private JComboBox groupComboBox;
	private JLabel groupIDJLabel;
	private PtnTextField groupIdPtnTextField;
	private JLabel siteNameJLabel;
	private PtnTextField siteNamePtnTextField;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8299442855746820523L;

	public SNConfigDialog(SiteInst siteInst) {
		try {
			ipConfigDialog = this;
			this.siteInst = siteInst;
			initComponentss();
			this.setLayout();
			this.addListener();
			this.initData();
			initGroupCombobox(groupComboBox);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		
	}

	private void initComponentss() throws Exception {
		this.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_SET_IP));
		lblMessage = new JLabel();
		ptnButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE), false);
		canceButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL), false);
		snJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OLD_SN));
		snptnTextField = new PtnTextField(false, PtnTextField.TYPE_STRING,12, this.lblMessage, this.ptnButton, this);
		snptnTextField.setEditable(false);
		newSNJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NEW_SN));
		newSNPtnTextField = new PtnTextField(true, PtnTextField.TYPE_STRING,12, this.lblMessage, this.ptnButton, this);
		groupJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_GROUP_BELONG));
		groupComboBox = new JComboBox();
		groupComboBox.setEditable(false);
		groupIDJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_ID));
		groupIdPtnTextField = new PtnTextField(true, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH, this.lblMessage, this.ptnButton, this);
		groupIdPtnTextField.setCheckingMaxValue(true);
		groupIdPtnTextField.setCheckingMinValue(true);
		groupIdPtnTextField.setMaxValue(254);
		groupIdPtnTextField.setMinValue(1);
		groupIdPtnTextField.setEditable(false);
		siteNameJLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_SITE_NAME));
		siteNamePtnTextField = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.ptnButton, this);
		siteNamePtnTextField.setEditable(false);
	}
	
	private void initGroupCombobox(JComboBox groupJComboBox) {

		FieldService_MB service = null;
		List<Field> fieldList = null ;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) groupJComboBox.getModel();
		try {
			service = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fieldList = service.selectField();
			for (Field field : fieldList) {
					defaultComboBoxModel.addElement(new ControlKeyValue(field.getId() + "", field.getFieldName(), field));
			}
			groupJComboBox.setModel(defaultComboBoxModel);
		
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	
	}
	
	private void setLayout() {

		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 200, 50 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 40, 40, 40, 40,40 ,40,40};
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0,0,0,0,0};
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(15, 5, 5, 5);
		componentLayout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.groupJLabel, c);
		this.add(this.groupJLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.groupComboBox, c);
		this.add(this.groupComboBox);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.groupIDJLabel, c);
		this.add(this.groupIDJLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.groupIdPtnTextField, c);
		this.add(this.groupIdPtnTextField);
		
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.siteNameJLabel, c);
		this.add(this.siteNameJLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.siteNamePtnTextField, c);
		this.add(this.siteNamePtnTextField);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 5, 5, 5);
		componentLayout.setConstraints(this.snJLabel, c);
		this.add(this.snJLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.snptnTextField, c);
		this.add(this.snptnTextField);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.newSNJLabel, c);
		this.add(this.newSNJLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.newSNPtnTextField, c);
		this.add(this.newSNPtnTextField);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.ptnButton, c);
		this.add(this.ptnButton);
		c.gridx = 2;
		componentLayout.setConstraints(this.canceButton, c);
		this.add(this.canceButton);
	}

	private void initData() {
		snptnTextField.setText(siteInst.getSn());
		newSNPtnTextField.setText("111111111111");
		siteNamePtnTextField.setText(siteInst.getCellId());
		FieldService_MB fieldService = null;
		List<Field> fields = null;
		try {
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fields = fieldService.selectByFieldId(siteInst.getFieldID());
			if(fields.size()>0){
				super.getComboBoxDataUtil().comboBoxSelect(groupComboBox, this.siteInst.getFieldID()+"");
				groupIdPtnTextField.setText(siteInst.getSite_Hum_Id());
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(fieldService);
		}
		
		
	}

	private void addListener() {
		ptnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveActionPerformed();
			}
		});
		canceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ipConfigDialog.dispose();
			}
		});
	}
	
	private void saveActionPerformed(){
		SiteService_MB siteService = null;
		DispatchUtil dispatchUtil = null;
		String result = "";
		SiteInst beforesiteInst = null;
		try {
			if(newSNPtnTextField.getText() == null || newSNPtnTextField.getText().length() != 12){
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NEW_SN_LENGTH));
				return;
			}
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			beforesiteInst = siteService.selectById(siteInst.getSite_Inst_Id());
			siteInst.setSn(newSNPtnTextField.getText());
			dispatchUtil = new DispatchUtil(RmiKeys.RMI_SITE);
			result = dispatchUtil.excuteUpdate(siteInst);
			AddOperateLog.insertOperLog(ptnButton, EOperationLogType.SITEUPDATE.getValue(), result, 
					beforesiteInst, siteInst, siteInst.getSite_Inst_Id(), siteInst.getCellId(), "siteInst");
			DialogBoxUtil.succeedDialog(this, result);
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}
	
}

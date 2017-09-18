package com.nms.ui.ptn.basicinfo.dialog.subnet.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import com.nms.db.bean.system.Field;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.system.SubnetService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
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
import com.nms.ui.topology.NetworkElementPanel;

/**
 * 添加或修改子网
 * @author Dzy
 *
 */
public class AddSubnetDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4419811354083681235L;
	private Field field = new Field();
	private JLabel lblField;
	private JLabel lblSubnet;	
	private JTextField txtSubnet;
	private JComboBox combo;
	private PtnButton btnSave;
	private JButton btnCanel;
	private JLabel lblMessage;
	private SubnetTablePanel subnetTablePanel;
	private Field subnet;

	public AddSubnetDialog(SubnetTablePanel SubnetTablePanel, boolean modal, Field subnet) {
		this.setModal(modal);
		this.subnetTablePanel = SubnetTablePanel;
		this.subnet = subnet;
		try {
			initComponentss();
			this.setLayout();
			this.addListener();
			this.initData(subnet);
			UiUtil.showWindow(this, 380, 240);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void initData(Field subnet) throws Exception {
		initCombobox(this.combo);
		super.getComboBoxDataUtil().comboBoxSelect(this.combo, ConstantUtil.fieldId+"");
		try {
			if (null != subnet) {
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_SUBNET));
				this.getfield(subnet);
				this.txtSubnet.setText(subnet.getFieldName() + "");
				super.getComboBoxDataUtil().comboBoxSelect(this.combo, subnet.getParentId() + "");			
			} else {
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_SUBNET));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void addListener() {
		this.btnSave.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);

			}

			@Override
			public boolean checking() {
			
				return true;
			}
		});
		this.btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton2ActionPerformed(e);
			}
		});
	}

	private void initComponentss() throws Exception {
		try {
			this.lblMessage = new JLabel();
			this.lblField = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_GROUP_BELONG) + "");
			this.lblSubnet = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SUBNET_NAME) + "");
			this.combo = new JComboBox();
			this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
			this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.txtSubnet = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 200, 50 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 25, 40, 40, 40, 15, 40, 15 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0, 0, 0.2 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(15, 5, 5, 5);
		componentLayout.setConstraints(this.lblField, c);
		this.add(this.lblField);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.combo, c);
		this.add(this.combo);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblSubnet, c);
		this.add(this.lblSubnet);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.txtSubnet, c);
		this.add(this.txtSubnet);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.btnSave, c);
		this.add(this.btnSave);
		c.gridx = 2;
		componentLayout.setConstraints(this.btnCanel, c);
		this.add(this.btnCanel);
	}

	private void getfield(Field subnet) throws Exception {
		List<Field> subnetList = null;
		SubnetService_MB subnetService = null;
		try {
			subnetService = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
			subnetList = subnetService.searchAndrefreshdata(subnet);
			if (null != subnetList && subnetList.size() > 0) {
				field = subnetList.get(0);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(subnetService);
		}
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		String beforeName = null;
		Field field1 = new Field();
		ControlKeyValue c = (ControlKeyValue) this.combo.getSelectedItem();
		field1 = (Field) c.getObject();
		SubnetService_MB subnetService = null;
		Field fd=null;
		try {
			subnetService = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
			this.field.setFieldName(this.txtSubnet.getText());
			if (null != subnet) {
				this.field.setId(subnet.getId());
				this.field.setParentId(field1.getId());
			}
			Field f = new Field();
			ControlKeyValue controlKeyValue = (ControlKeyValue) this.combo.getSelectedItem();
			f = (Field) controlKeyValue.getObject();
			this.field.setParentId(f.getId());
			

			// 验证名称是否存在
			if (null != subnet) {
				beforeName = subnet.getFieldName();
			}
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyNameBySingle(EServiceType.SUBNET.getValue(), this.txtSubnet.getText().trim(), beforeName, field.getParentId())) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return;
			}
			if(field.getId()>0){
				this.btnSave.setOperateKey(EOperationLogType.SUBNETUPDATE.getValue());
				fd=subnetService.selectById(field.getId());
				fd.setParentName(subnetService.selectById(fd.getParentId()).getFieldName());
				field.setParentName(subnetService.selectById(field.getParentId()).getFieldName());
			}else{
				this.btnSave.setOperateKey(EOperationLogType.SUBNETINSERT.getValue());
			}
			subnetService.saveOrUpdate(field);
			int operateKey=this.btnSave.getOperateKey();
			if(operateKey == EOperationLogType.SUBNETUPDATE.getValue()){						                    	
             	this.insertOpeLog(EOperationLogType.SUBNETUPDATE.getValue(), ResultString.CONFIG_SUCCESS, fd, field);
            }else{
            	field.setParentName(subnetService.selectById(field.getParentId()).getFieldName());
            	this.insertOpeLog(EOperationLogType.SUBNETINSERT.getValue(), ResultString.CONFIG_SUCCESS, null, field);
            }
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED) + "");
			
			btnSave.setResult(1);
			this.dispose();

			// 如果是null 说明是在拓扑界面弹出的。 不用刷新子网列表
			if (null != this.subnetTablePanel) {
				this.subnetTablePanel.getController().refresh();
			}
			NetworkElementPanel.getNetworkElementPanel().showTopo(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(subnetService);
		}

	}

	private void insertOpeLog(int operationType, String result, Field oldMac, Field newMac){
		AddOperateLog.insertOperLog(btnSave, operationType, result, oldMac, newMac, 0,newMac.getFieldName(),"SubNet");		
	}
	
	public void initCombobox(JComboBox comboBox) {
		SubnetService_MB service = null;
		List<Field> fieldList = null;
		Field f = new Field();
		f.setFieldName("ALL");
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		try {
			service = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
			fieldList = (service.comboboxList());
			for (Field field : fieldList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(field.getId() + "", field.getFieldName(), field));
			}
			comboBox.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

}
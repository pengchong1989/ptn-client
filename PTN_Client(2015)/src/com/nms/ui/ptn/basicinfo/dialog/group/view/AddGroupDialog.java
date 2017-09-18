package com.nms.ui.ptn.basicinfo.dialog.group.view;

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
import javax.swing.JTextField;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.FieldService_MB;
import com.nms.model.system.NetService_MB;
import com.nms.model.system.SubnetService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
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
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.topology.NetworkElementPanel;

public class AddGroupDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4419811354083681235L;
	private JLabel lblField;
	private JLabel lblSubnet;
	private JTextField txtSubnet;
	private JComboBox combo;
	private PtnButton btnSave;
	private JButton btnCanel;
	private JLabel lblMessage;
	private GroupTablePanel groupTablePanel;
	private Field field;
	private JLabel groupIdJLabel;
	private PtnTextField groupIdJTextField;
	private List<Integer> allIds = new ArrayList<Integer>();

	public AddGroupDialog(GroupTablePanel groupTablePanel, boolean modal) {
		this.setModal(modal);
		this.groupTablePanel = groupTablePanel;
		try {
			initComponentss();
			this.setLayout();
			this.addListener();
			this.initData();
			UiUtil.showWindow(this, 380, 240);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	public AddGroupDialog(GroupTablePanel view, Field field, boolean modal) {
		this.setModal(modal);
		this.groupTablePanel = view;
		this.field = field;
		try {
			initComponentss();
			this.setLayout();
			this.addListener();
			this.initData();
			UiUtil.showWindow(this, 380, 240);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void initData() throws Exception {
		initCombobox(this.combo);
		initGroupId();
		super.getComboBoxDataUtil().comboBoxSelect(this.combo, ConstantUtil.fieldId + "");
		try {
			if (field != null) {
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_GROUP));
				this.txtSubnet.setText(field.getFieldName() + "");
				this.groupIdJTextField.setText(field.getGroupId() + "");
				super.getComboBoxDataUtil().comboBoxSelect(this.combo, field.getNetWorkId() + "");
			} else {
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_GROUP));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	/**
	 * 初始化可用groupid
	 */
	private void initGroupId() {
		FieldService_MB fieldService = null;
		List<Field> fields = null;
		List<Integer> selectIds = new ArrayList<Integer>();
		try {
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fields = new ArrayList<Field>();
			fields = fieldService.selectField();
			for (int i = 1; i < 255; i++) {
				allIds.add(i);
			}
			for (Field field : fields) {
				selectIds.add(field.getGroupId());
			}
			allIds.removeAll(selectIds);
			if (allIds.size() > 0) {
				this.groupIdJTextField.setText(allIds.get(0) + "");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(fieldService);
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
			this.lblField = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_FIELD_BELONG) + "");
			this.lblSubnet = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_GROUP_NAME) + "");
			this.combo = new JComboBox();
			this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE), false);
			this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.txtSubnet = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
			groupIdJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_GROUP_ID));
			this.groupIdJTextField = new PtnTextField(true, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH, this.lblMessage, this.btnSave, this);
			this.groupIdJTextField.setCheckingMaxValue(true);
			this.groupIdJTextField.setCheckingMinValue(true);
			this.groupIdJTextField.setMaxValue(254);
			this.groupIdJTextField.setMinValue(1);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
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

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.groupIdJLabel, c);
		this.add(this.groupIdJLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.groupIdJTextField, c);
		this.add(this.groupIdJTextField);

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

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		
		NetWork netWork = new NetWork();
		ControlKeyValue c = (ControlKeyValue) this.combo.getSelectedItem();
		netWork = (NetWork) c.getObject();
		FieldService_MB fieldService = null;
		String beforeName = "";
		Integer beforeId = 0;
		DispatchUtil adminDispatch = null;
		String result = ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED);
		boolean canused = false;
		NetService_MB service = null;
		Field fd=null;
		SubnetService_MB subnetService = null;
		try {
			service = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			subnetService = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
			if (allIds.size() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NO_GROUP));
				return;
			}
			if (field != null) {
				beforeName = field.getFieldName();
				beforeId = field.getGroupId();
				if (!beforeName.equals(this.txtSubnet.getText())) {// 修改前后网元名称不一样
					if (fieldService.checkNameExist(this.txtSubnet.getText())) {
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
						return;
					}
				}
				
				if (beforeId != Integer.parseInt(groupIdJTextField.getText())) {
					for (Integer integer : allIds) {
						if (integer == Integer.parseInt(groupIdJTextField.getText())) {
							canused = true;
							break;							
						}
					}
					if(!canused){
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_GROUP_EXIST));
						return;
					}
				}
				
				field.setGroupId(Integer.parseInt(groupIdJTextField.getText()));
				field.setFieldName(this.txtSubnet.getText());
				field.setNetWorkId(netWork.getNetWorkId());
				
				if (beforeId == Integer.parseInt(groupIdJTextField.getText())) {
					DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
				} else {
					if(field.getWorkIP() != null&&field.getWorkIP().size() > 0)
					{
						adminDispatch = new DispatchUtil(RmiKeys.RMI_ADMINISTRATECONFIG);
						result = adminDispatch.excuteInsert(field);
					}
					DialogBoxUtil.succeedDialog(this, result);
				}
				//记录修改前
				fd=subnetService.selectById(field.getId());
				NetWork net=new NetWork();
				net.setNetWorkId(fd.getNetWorkId());
				fd.setNetWorkName(service.select(net).get(0).getNetWorkName());
				NetWork net1=new NetWork();
				net1.setNetWorkId(netWork.getNetWorkId());
				field.setNetWorkName(service.select(net1).get(0).getNetWorkName());
				
				fieldService.saveOrUpdate(field);
				this.insertOpeLog(EOperationLogType.UPDATEGROUPID.getValue(), ResultString.CONFIG_SUCCESS, fd, field);
			} else {
				if (fieldService.checkNameExist(this.txtSubnet.getText())) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
					return;
				}
				field = new Field();
				field.setGroupId(Integer.parseInt(groupIdJTextField.getText()));
				field.setFieldName(this.txtSubnet.getText());
				field.setNetWorkId(netWork.getNetWorkId());
				NetWork net=new NetWork();
				net.setNetWorkId(netWork.getNetWorkId());
				field.setNetWorkName(service.select(net).get(0).getNetWorkName());
				if(!allIds.contains(Integer.parseInt(groupIdJTextField.getText()))){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_GROUP_EXIST));
					field = null;
					return;
				}
				fieldService.saveOrUpdate(field);
				this.insertOpeLog(EOperationLogType.CREATEGROUP.getValue(), ResultString.CONFIG_SUCCESS, null, field);
				DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
			}

			btnSave.setResult(1);
			this.dispose();

			// 如果是null 说明是在拓扑界面弹出的。 不用刷新子网列表
			if (null != this.groupTablePanel) {
				this.groupTablePanel.getController().refresh();
			}
			NetworkElementPanel.getNetworkElementPanel().showTopo(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(fieldService);
			UiUtil.closeService_MB(service);
			UiUtil.closeService_MB(subnetService);
		}

	}

	
	private void insertOpeLog(int operationType, String result, Field oldMac, Field newMac){
		AddOperateLog.insertOperLog(btnSave, operationType, result, oldMac, newMac, 0,newMac.getFieldName(),"Group");		
	}
	
	public void initCombobox(JComboBox comboBox) {
		NetService_MB service = null;
		List<NetWork> netWorks = null;
		Field f = new Field();
		f.setFieldName("ALL");
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		try {
			service = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
			netWorks = new ArrayList<NetWork>();
			netWorks = service.select();
			for (NetWork netWork : netWorks) {
				defaultComboBoxModel.addElement(new ControlKeyValue(netWork.getNetWorkId() + "", netWork.getNetWorkName(), netWork));
			}
			comboBox.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

}
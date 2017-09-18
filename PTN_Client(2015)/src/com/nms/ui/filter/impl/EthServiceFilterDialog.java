package com.nms.ui.filter.impl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.path.ServiceInfo;
import com.nms.db.enums.EServiceType;
import com.nms.ui.filter.FilterDialog;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnComboBox;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;

/**
 * 以太网业务过滤界面
 * 
 * @author kk
 * 
 */
public class EthServiceFilterDialog extends FilterDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EthServiceFilterDialog(Object object) {
		super(object);
	}

	@Override
	protected int getPanelChildHeight() {
		return 150;
	}

	@Override
	protected void setLayoutChild(JPanel panelChild) {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 80, 220 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 40, 40, 40 };
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1 };

		panelChild.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.NORTH;

		// 网元名称
		c.gridx = 0;
		c.gridy = 0;
		layout.setConstraints(this.lblNEName, c);
		panelChild.add(this.lblNEName);

		c.gridx = 1;
		layout.setConstraints(this.cmbNEName, c);
		panelChild.add(this.cmbNEName);
		
		// 端口名称
		c.gridx = 0;
		c.gridy = 1;
		layout.setConstraints(this.lblPort, c);
		panelChild.add(this.lblPort);

		c.gridx = 1;
		layout.setConstraints(this.cmbPort, c);
		panelChild.add(this.cmbPort);

		// pw名称
		c.gridx = 0;
		c.gridy = 2;
		layout.setConstraints(this.lblPwName, c);
		panelChild.add(this.lblPwName);

		c.gridx = 1;
		layout.setConstraints(this.cmbPwName, c);
		panelChild.add(this.cmbPwName);

		// 激活状态
		c.gridx = 0;
		c.gridy = 3;
		layout.setConstraints(this.lblActivatedState, c);
		panelChild.add(this.lblActivatedState);

		c.gridx = 1;
		layout.setConstraints(this.cmbActivatedState, c);
		panelChild.add(this.cmbActivatedState);
	}

	@Override
	protected void addListenerChild() {
		this.cmbNEName.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ControlKeyValue controlKeyValue = (ControlKeyValue) e.getItem();
					getComboBoxDataUtil().initUNIPortData(cmbPort, Integer.parseInt(controlKeyValue.getId()),EServiceType.SITE);
				}
			}
		});
	}
	
	@Override
	protected void initComponent() {
		this.lblPwName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW_NAME));
		this.cmbPwName = new PtnComboBox();
		this.lblNEName = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_SITE_NAME));
		this.cmbNEName = new PtnComboBox();
		this.lblPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		this.cmbPort = new PtnComboBox();
		this.lblActivatedState = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		this.cmbActivatedState = new PtnComboBox();
	}

	@Override
	protected void btnConfirmListener() {

		Object object = super.getObject();
		try {
			ServiceInfo serviceInfo = (ServiceInfo) object;
			serviceInfo.setName(super.getTxtName().getText());

			if (this.cmbNEName.getSelectedIndex() > -1) {
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbNEName.getSelectedItem();
				serviceInfo.setaSiteId(Integer.parseInt(controlKeyValue.getId()));
			}
			
			if (this.cmbPort.getSelectedIndex() > -1) {
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbPort.getSelectedItem();
				serviceInfo.setAportId(Integer.parseInt(controlKeyValue.getId()));
			}else{
				serviceInfo.setAportId(0);
			}

			if (this.cmbPwName.getSelectedIndex() > -1) {
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbPwName.getSelectedItem();
				serviceInfo.setPwId(Integer.parseInt(controlKeyValue.getId()));
			}
			
			if(this.cmbActivatedState.getSelectedIndex()>-1){
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbActivatedState.getSelectedItem();
				serviceInfo.setActiveStatus(Integer.parseInt(controlKeyValue.getId()));
			}

			super.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	@Override
	protected void loadData() {

		try {
			super.getComboBoxDataUtil().initNEData(this.cmbNEName);
			super.getComboBoxDataUtil().initActivatedData(this.cmbActivatedState);
			super.getComboBoxDataUtil().initPWData(this.cmbPwName,0,true);
			this.loadFormData();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	/**
	 * 绑定表单数据
	 */
	protected void loadFormData(){
		Object object=super.getObject();
		if(null!=object){
			ServiceInfo serviceInfo=(ServiceInfo) object;
			super.getTxtName().setText(serviceInfo.getName());
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbNEName, serviceInfo.getaSiteId()+"");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbPort, serviceInfo.getAportId()+"");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbActivatedState, serviceInfo.getActiveStatus()+"");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbPwName, serviceInfo.getPwId()+"");
		}
	}

	protected JLabel lblPwName;
	protected PtnComboBox cmbPwName;
	protected JLabel lblNEName;
	protected PtnComboBox cmbNEName;
	protected JLabel lblActivatedState;
	protected PtnComboBox cmbActivatedState;
	protected JLabel lblPort;
	protected PtnComboBox cmbPort;
}

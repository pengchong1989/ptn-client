package com.nms.ui.filter.impl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EServiceType;
import com.nms.ui.filter.FilterDialog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnComboBox;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;

public class TunnelNEFilterDialog extends FilterDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TunnelNEFilterDialog(Object object) {
		super(object);
	}

	@Override
	protected int getPanelChildHeight() {
		return 230;
	}

	@Override
	protected void setLayoutChild(JPanel panelChild) {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 80, 220 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 40, 40, 40, 40, 40 };
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1 };

		panelChild.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.NORTH;

		// 板卡名称
		c.gridx = 0;
		c.gridy = 0;
		layout.setConstraints(this.lblCard, c);
		panelChild.add(this.lblCard);

		c.gridx = 1;
		layout.setConstraints(this.cmbCard, c);
		panelChild.add(this.cmbCard);

		// 端口名称
		c.gridx = 0;
		c.gridy = 1;
		layout.setConstraints(this.lblPort, c);
		panelChild.add(this.lblPort);

		c.gridx = 1;
		layout.setConstraints(this.cmbPort, c);
		panelChild.add(this.cmbPort);

		// 类型
		c.gridx = 0;
		c.gridy = 2;
		layout.setConstraints(this.lblType, c);
		panelChild.add(this.lblType);

		c.gridx = 1;
		layout.setConstraints(this.cmbType, c);
		panelChild.add(this.cmbType);

		// 激活状态
		c.gridx = 0;
		c.gridy = 3;
		layout.setConstraints(this.lblActivatedState, c);
		panelChild.add(this.lblActivatedState);

		c.gridx = 1;
		layout.setConstraints(this.cmbActivatedState, c);
		panelChild.add(this.cmbActivatedState);

		// 角色
		c.gridx = 0;
		c.gridy = 4;
		layout.setConstraints(this.lblRole, c);
		panelChild.add(this.lblRole);

		c.gridx = 1;
		layout.setConstraints(this.cmbRole, c);
		panelChild.add(this.cmbRole);
	}

	@Override
	protected void addListenerChild() {
		this.cmbCard.addItemListener(new java.awt.event.ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ControlKeyValue controlKeyValue = (ControlKeyValue) e.getItem();
					getComboBoxDataUtil().initNNIPortData(cmbPort, Integer.parseInt(controlKeyValue.getId()), EServiceType.CARD);
				}
			}
		});
	}

	@Override
	protected void initComponent() {
		this.lblCard = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CARD_NAME));
		this.cmbCard = new PtnComboBox();
		this.lblPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		this.cmbPort = new PtnComboBox();
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		this.cmbType = new PtnComboBox();
		this.lblActivatedState = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		this.cmbActivatedState = new PtnComboBox();
		this.lblRole = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROLE));
		this.cmbRole = new PtnComboBox();
	}

	@Override
	protected void btnConfirmListener() {

		Object object = super.getObject();
		ControlKeyValue controlKeyValue = null;
		if (null != object) {
			Tunnel tunnel = (Tunnel) object;
			tunnel.setTunnelName(super.getTxtName().getText());

			if (this.cmbCard.getSelectedIndex() > -1) {
				controlKeyValue = (ControlKeyValue) this.cmbCard.getSelectedItem();
				tunnel.setCardId(Integer.parseInt(controlKeyValue.getId()));
			}

			if (this.cmbPort.getSelectedIndex() > -1) {
				controlKeyValue = (ControlKeyValue) this.cmbPort.getSelectedItem();
				tunnel.setAPortId(Integer.parseInt(controlKeyValue.getId()));
			}

			if (this.cmbType.getSelectedIndex() > -1) {
				controlKeyValue = (ControlKeyValue) this.cmbType.getSelectedItem();
				tunnel.setTunnelType(controlKeyValue.getId());
			}

			if (this.cmbRole.getSelectedIndex() > 0) {
				controlKeyValue = (ControlKeyValue) this.cmbRole.getSelectedItem();
				Code code = (Code) controlKeyValue.getObject();
				tunnel.setRole(code.getCodeValue());
			}

			if (this.cmbActivatedState.getSelectedIndex() > -1) {
				controlKeyValue = (ControlKeyValue) this.cmbActivatedState.getSelectedItem();
				tunnel.setTunnelStatus(Integer.parseInt(controlKeyValue.getId()));
			}
		}
		super.dispose();
	}

	@Override
	protected void loadData() {
		try {
			super.getComboBoxDataUtil().initActivatedData(this.cmbActivatedState);
			super.getComboBoxDataUtil().initCardData(this.cmbCard, ConstantUtil.siteId);
			super.getComboBoxDataUtil().comboBoxData(this.cmbType, "PROTECTTYPE");
			this.cmbRole.addItem(new ControlKeyValue("0", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), null));
			super.getComboBoxDataUtil().comboBoxData(this.cmbRole, "TUNNELROLE");
			this.loadFormData();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 绑定表单数据
	 */
	private void loadFormData() {
		Object object = super.getObject();
		if (null != object) {
			Tunnel tunnel = (Tunnel) object;
			super.getTxtName().setText(tunnel.getTunnelName());
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbCard, tunnel.getCardId() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbActivatedState, tunnel.getTunnelStatus() + "");
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, tunnel.getTunnelType());
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbPort, tunnel.getaPortId() + "");
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.cmbRole, tunnel.getRole());
		}
	}

	private JLabel lblCard;
	private PtnComboBox cmbCard;
	private JLabel lblPort;
	private PtnComboBox cmbPort;
	private JLabel lblType;
	private PtnComboBox cmbType;
	private JLabel lblActivatedState;
	private PtnComboBox cmbActivatedState;
	private JLabel lblRole;
	private PtnComboBox cmbRole;

}

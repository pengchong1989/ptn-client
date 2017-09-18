﻿package com.nms.ui.ptn.ne.eline.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EManufacturer;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.UiUtil;

public class AcElinePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4442083213455101239L;
	private JScrollPane scrollPane_ac;
	private ViewDataTable<AcPortInfo> table_ac;
	private final String TABLENAME_AC = "elineAcTable";

	public AcElinePanel() {
		initComponents();
		setLayout();
	}

	public void clear() {
		table_ac.clear();
	}

	public void initData(List<AcPortInfo> acPortInfoList) throws Exception {
		PortLagService_MB portLagServiceMB = null;
		PortService_MB portServiceMB = null;
		SiteService_MB siteServiceMB = null;
		if (acPortInfoList != null && acPortInfoList.size() > 0) {
			try {
				portLagServiceMB = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
				portServiceMB = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);

				for (AcPortInfo acPortInfo : acPortInfoList) {
					acPortInfo.putClientProperty("id", acPortInfo.getId());
					acPortInfo.putClientProperty("acname", acPortInfo.getName());
					if (acPortInfo.getPortId() > 0) {
						acPortInfo.putClientProperty("portname", portServiceMB.getPortname(acPortInfo.getPortId()));
					} else {
						acPortInfo.putClientProperty("portname", portLagServiceMB.getLagName(acPortInfo.getLagId()));
					}
					acPortInfo.putClientProperty("model", UiUtil.getCodeById(acPortInfo.getPortModel()).getCodeName());
					if (siteServiceMB.getManufacturer(acPortInfo.getSiteId()) == EManufacturer.WUHAN.getValue()) {
						acPortInfo.putClientProperty("vlanid", acPortInfo.getBufferList().get(0).getVlanId());
					} else {
						acPortInfo.putClientProperty("vlanid", acPortInfo.getVlanId());
					}
					acPortInfo.putClientProperty("vlanpri", acPortInfo.getVlanpri());
				}
				table_ac.initData(acPortInfoList);
			} catch (Exception e) {
				throw e;
			} finally {
				UiUtil.closeService_MB(portLagServiceMB);
				UiUtil.closeService_MB(siteServiceMB);
				UiUtil.closeService_MB(portServiceMB);
			}
		}
	}

	private void initComponents() {
		table_ac = new ViewDataTable<AcPortInfo>(TABLENAME_AC);
		table_ac.getTableHeader().setResizingAllowed(true);
		table_ac.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table_ac.setTableHeaderPopupMenuFactory(null);
		table_ac.setTableBodyPopupMenuFactory(null);
		scrollPane_ac = new JScrollPane();
		scrollPane_ac.setViewportView(table_ac);
		scrollPane_ac.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_ac.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	private void setLayout() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		GridBagLayout qosGridBagLayout = new GridBagLayout();
		this.setLayout(qosGridBagLayout);
		addComponent(this, scrollPane_ac, 0, 0, 0.5, 1.0, 0, 1, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), GridBagConstraints.NORTHWEST, gridBagConstraints);
	}

	private void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
	}
}

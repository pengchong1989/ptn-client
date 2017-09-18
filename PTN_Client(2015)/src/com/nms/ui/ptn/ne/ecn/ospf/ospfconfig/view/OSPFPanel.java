﻿package com.nms.ui.ptn.ne.ecn.ospf.ospfconfig.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.nms.db.bean.ptn.ecn.OSPFAREAInfo;
import com.nms.db.bean.ptn.ecn.OSPFInfo;
import com.nms.model.ptn.ecn.OSPFAREAService_MB;
import com.nms.model.ptn.ecn.OSPFInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.ne.ecn.ospf.areaconfig.view.AreaConfigPanel;
import com.nms.ui.ptn.ne.ecn.ospf.redistribution.view.DistributePanel;

/**
 * OSPF管理
 * @author sy
 *
 */
public class OSPFPanel extends PtnPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// TXC
	private String ospfId = "";
	public OSPFPanel() {
		initComponents();
		this.setMainLayout();
		this.setLayout();
		this.addListener();
		initValue();
		query();
	}

	private void addListener() {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 下发
				OSPFPanel.this.save();
			}
		});

		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				DispatchUtil ospfINFODispatch = null;
				try {
					ospfINFODispatch = new DispatchUtil(RmiKeys.RMI_OSPFINFO);
					ospfINFODispatch.synchro(ConstantUtil.siteId);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				} finally {
					ospfINFODispatch = null;
				}
				
				// 查询
				OSPFPanel.this.query();
			}
		});

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				OSPFAREAService_MB oSPFAREAService = null;
				try {
					oSPFAREAService = (OSPFAREAService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OSPFAREA);
					ospf.removeAllItems();
					List<OSPFAREAInfo> oSPFAREAInfoList = oSPFAREAService.queryByNeID(ConstantUtil.siteId);
					for (int i = 0; i < oSPFAREAInfoList.size(); i++) {
						ospf.addItem(oSPFAREAInfoList.get(i).getArea_range());// 默认值
					}
				} catch (Exception e1) {
				} finally {
					UiUtil.closeService_MB(oSPFAREAService);
				}
				if (ospf.getItemCount() == 0) {
					ospf.addItem("0");
				}
			}
		});
	}

	private void save() {
		try {
			OSPFInfo oSPFInfo = new OSPFInfo();
			oSPFInfo.setDefmetric(metrictext.getText());// 默认Metric值
			oSPFInfo.setRefbandwidth(bandwidthtext.getText());// 带宽计算加权值
			oSPFInfo.setDistance(routetext.getText());// 路由距离值
			oSPFInfo.setVersion(ospfversiontext.getText());// OSPF软件版本
			oSPFInfo.setSpf_holdtime(counttext.getText());// 路由计算计时器
			oSPFInfo.setSpf_maxholdtime(maxtimetext.getText());// 最大计时时间
			oSPFInfo.setSpf_delay(gaptext.getText());// 延时间隔
			oSPFInfo.setRefresh_time(lsatext.getText());// LSA刷新间隔
			oSPFInfo.setId(ospfInfoId);

			if (site.isSelected() == true) {// 网元OSPF使能
				oSPFInfo.setEnabled("1");
			} else {
				oSPFInfo.setEnabled("0");
			}

			if (rfccom.isSelected() == true) {// RFC1583兼容
				oSPFInfo.setCompatiblerfc1583("1");
			} else {
				oSPFInfo.setCompatiblerfc1583("0");
			}
			oSPFInfo.setAbr(typecom.getSelectedItem().toString());
			oSPFInfo.setRt_id_area(ospf.getSelectedItem().toString());
			oSPFInfo.setNeId(ConstantUtil.siteId + "");

			DispatchUtil OSPFINFODispatch = new DispatchUtil(RmiKeys.RMI_OSPFINFO);

			if (oSPFInfo.getEnabled() != null && "1".equals(oSPFInfo.getEnabled())) {
				if ("".equals(ospfInfoId)) {
					OSPFINFODispatch.excuteInsert(oSPFInfo);
					OSPFAREA();
				} else {
					oSPFInfo.setId(ospfInfoId);
					OSPFINFODispatch.excuteUpdate(oSPFInfo);
				}
			} else {
				if (ospfInfoId != null && !"".equals(ospfInfoId)) {
					oSPFInfo.setId(ospfInfoId);
					if (areaConfigPanel != null) {
						if (areaConfigPanel.getTable().getRowCount() > 0) {
							DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_AREA_INFORMATION));
							site.setSelected(true);
							return;
						}
					}
					OSPFINFODispatch.excuteDelete(oSPFInfo);
					initValue();
					ospfInfoId = "";
				}
			}

			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			OSPFINFODispatch = null;

		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this, e.getMessage());
		}
	}

	private void OSPFAREA() throws Exception {
		try {
			OSPFAREAInfo OSPFAREAInfo = new OSPFAREAInfo();
			OSPFAREAInfo.setNeId(ConstantUtil.siteId + "");
			OSPFAREAInfo.setType("NONE");
			String area_range = "0";
			OSPFAREAInfo.setArea_range(area_range);

			DispatchUtil OSPFAREADispatch = new DispatchUtil(RmiKeys.RMI_OSPFAREA);

			OSPFAREADispatch.excuteInsert(OSPFAREAInfo);
			if (areaConfigPanel != null) {
				areaConfigPanel.initData();
			}
			OSPFAREADispatch = null;
		} catch (Exception e) {
			throw e;
		}

	}

	private void query() {
		// System.out.println("查询");
		OSPFInfoService_MB oSPFInfoService = null;
		try {
			oSPFInfoService = (OSPFInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OSPFInfo);
			OSPFInfo oSPFInfo = oSPFInfoService.queryByNeID(ConstantUtil.siteId);

			if (oSPFInfo != null) {
				metrictext.setText(oSPFInfo.getDefmetric());// 默认Metric值
				bandwidthtext.setText(oSPFInfo.getRefbandwidth());// 带宽计算加权值
				routetext.setText(oSPFInfo.getDistance());// 路由距离值
				ospfversiontext.setText(oSPFInfo.getVersion());// OSPF软件版本
				counttext.setText(oSPFInfo.getSpf_holdtime());// 路由计算计时器
				maxtimetext.setText(oSPFInfo.getSpf_maxholdtime());// 最大计时时间
				gaptext.setText(oSPFInfo.getSpf_delay());// 延时间隔
				lsatext.setText(oSPFInfo.getRefresh_time());// LSA刷新间隔
				ospfInfoId = oSPFInfo.getId();
				// UiUtil.comboBoxSelectByValue(ospf, oSPFInfo.getRt_id_area());
				super.getComboBoxDataUtil().comboBoxSelectByValue(typecom, oSPFInfo.getAbr());

				int count = ospf.getItemCount();
				String value = "";
				for (int i = 0; i < count; i++) {
					value = (String) ospf.getItemAt(i);
					if (value != null && value.equals(oSPFInfo.getRt_id_area())) {
						ospf.setSelectedIndex(i);
						break;
					}
				}

				if ("1".equals(oSPFInfo.getEnabled())) {
					site.setSelected(true);
				}
				if ("1".equals(oSPFInfo.getCompatiblerfc1583())) {
					rfccom.setSelected(true);
				}

				// DialogBoxUtil.succeedDialog(this, "查询成功");
//				oSPFInfoService = null;
			} else {
				// DialogBoxUtil.succeedDialog(this, "请先初始化OSPF配置");
			}

		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this, e.toString());
		} finally {
			UiUtil.closeService_MB(oSPFInfoService);
		}
	}

	private void initValue() {
		OSPFAREAService_MB oSPFAREAService = null;
		try {
			oSPFAREAService = (OSPFAREAService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OSPFAREA);
			ospf.removeAllItems();
			List<OSPFAREAInfo> oSPFAREAInfoList = oSPFAREAService.queryByNeID(ConstantUtil.siteId);
			for (int i = 0; i < oSPFAREAInfoList.size(); i++) {
				ospf.addItem(oSPFAREAInfoList.get(i).getArea_range());// 默认值
			}
		} catch (Exception e1) {
		} finally {
			UiUtil.closeService_MB(oSPFAREAService);
		}

		if (ospf.getItemCount() == 0) {
			ospf.addItem("0");
		}

		try {
			super.getComboBoxDataUtil().comboBoxData(typecom, "OSPF边界路由器兼容类型");
		} catch (Exception e) {
		}
		metrictext.setText("-1");// 默认值
		bandwidthtext.setText("100");// 默认值
		routetext.setText("110");// 默认值
		ospfversiontext.setText("0.0.0");// 默认值
		counttext.setText("1000");// 默认值
		maxtimetext.setText("10000");// 默认值
		gaptext.setText("200");// 默认值
		lsatext.setText("10");// 默认值
		site.setSelected(false);
		rfccom.setSelected(false);

	}

	private void initComponents() {
		tabbedPane = new JTabbedPane();
		ospfpanel = new JPanel();
		areapanel = new JPanel();
		chongfenfapanel = new JPanel();
		ospflinstatuspanel = new JPanel();
		ospfinterfacepanel = new JPanel();
		siteospf = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OSPF_UP));
		site = new JCheckBox();
		site.setBackground(Color.WHITE);
		ospfarea = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OSPF_AREA));
		ospf = new JComboBox();
		type = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROUTE_TYPE));
		typecom = new JComboBox();
		metric = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DEFAULT_METRIC_VALUE));
		metrictext = new JTextField();
		bandwidth = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BANDWIDTH_CALCULATION));
		bandwidthtext = new JTextField();
		route = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROUTE_DISTANCE));
		routetext = new JTextField();
		ospfversion = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OSPF_VERSION));
		ospfversiontext = new JTextField();
		ospfversiontext.setEnabled(false);
		count = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROUTE_COUNT));
		counttext = new JTextField();
		maxtime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAX_TIMING));
		maxtimetext = new JTextField();
		gap = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_INTERVAL));
		gaptext = new JTextField();
		lsa = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LSA_INTERVAL));
		lsatext = new JTextField();
		rfc = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RFC1583));
		rfccom = new JCheckBox();
		rfccom.setBackground(Color.WHITE);
		button = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ISSUED));
		jButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SELECT));
		ospfrunpanel = new JPanel();
	}

	AreaConfigPanel areaConfigPanel = null;

	private void setMainLayout() {

		this.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_OSPF_MANAGE)));
		this.tabbedPane.addTab(ResourceUtil.srcStr(StringKeysTitle.TIT_OSPF_MANAGE), ospfpanel);
		areaConfigPanel = new AreaConfigPanel();
		this.tabbedPane.addTab(ResourceUtil.srcStr(StringKeysPanel.PANEL_AREA_CONFIG), areaConfigPanel);
		this.tabbedPane.addTab(ResourceUtil.srcStr(StringKeysPanel.PANEL_REDISTRIBUTION_CONFIG), new DistributePanel());
		// 暂时不实现
		// this.tabbedPane.addTab("OSPF邻居状态", new OSPFStatusPanel());
		// this.tabbedPane.addTab("OSPF接口报文统计", new OspfInterfacesMessagePanel());
		// this.tabbedPane.addTab("OSPF接口运行状态", new OspfinterfacesRunstatusPanel());

		GridBagLayout contentLayout = new GridBagLayout();
		this.setLayout(contentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0, 10, 0, 10);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(this.tabbedPane, c);
		this.add(this.tabbedPane);
	}

	private void setLayout() {
		this.ospfpanel.setBackground(Color.WHITE);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 150, 210, 40, 40 };
		layout.columnWeights = new double[] { 0, 0, 0, 0.2 };
		layout.rowHeights = new int[] { 10, 35, 35, 35, 35, 35, 35, 35, 35, 35, 20, 35, 35, 35, 35 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.ospfpanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();

		/** 第一行 网元OSPF使能 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.siteospf, c);
		this.ospfpanel.add(this.siteospf);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.site, c);
		this.ospfpanel.add(this.site);

		/** 第二行 OSPF区域 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.ospfarea, c);
		this.ospfpanel.add(this.ospfarea);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.ospf, c);
		this.ospfpanel.add(this.ospf);

		/** 第三行 边界路由器兼容类型 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.type, c);
		this.ospfpanel.add(this.type);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.typecom, c);
		this.ospfpanel.add(this.typecom);

		/** 第四行 默认Metric值 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.metric, c);
		this.ospfpanel.add(this.metric);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.metrictext, c);
		this.ospfpanel.add(this.metrictext);

		/** 第五行 带宽计算加权值 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.bandwidth, c);
		this.ospfpanel.add(this.bandwidth);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.bandwidthtext, c);
		this.ospfpanel.add(this.bandwidthtext);

		/** 第六行 路由距离值 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.route, c);
		this.ospfpanel.add(this.route);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.routetext, c);
		this.ospfpanel.add(this.routetext);

		/** 第七行 OSPF软件版本 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.ospfversion, c);
		this.ospfpanel.add(this.ospfversion);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.ospfversiontext, c);
		this.ospfpanel.add(this.ospfversiontext);

		/** 第八行 路由计算计时器 */
		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.count, c);
		this.ospfpanel.add(this.count);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.counttext, c);
		this.ospfpanel.add(this.counttext);

		/** 第九行 最大计时时间 */
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.maxtime, c);
		this.ospfpanel.add(this.maxtime);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.maxtimetext, c);
		this.ospfpanel.add(this.maxtimetext);

		/** 第十行 延时间隔 */
		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.gap, c);
		this.ospfpanel.add(this.gap);
		c.gridx = 1;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.gaptext, c);
		this.ospfpanel.add(this.gaptext);

		/** 第十一行 LSA刷新间隔 */
		c.gridx = 0;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.lsa, c);
		this.ospfpanel.add(this.lsa);
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.lsatext, c);
		this.ospfpanel.add(this.lsatext);

		/** 第十二行 RFC1583兼容 */
		c.gridx = 0;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 20, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.rfc, c);
		this.ospfpanel.add(this.rfc);
		c.gridx = 1;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.rfccom, c);
		this.ospfpanel.add(this.rfccom);

		/** 第十四行 按钮 */
		c.gridx = 1;
		c.gridy = 14;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(this.button, c);
		this.ospfpanel.add(this.button);
		c.gridx = 2;
		c.gridy = 14;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.jButton, c);
		this.ospfpanel.add(this.jButton);
	}

	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private JPanel ospfpanel;
	private JPanel areapanel;
	private JPanel chongfenfapanel;
	private JPanel ospflinstatuspanel;
	private JPanel ospfinterfacepanel;
	private JPanel ospfrunpanel;
	private JLabel siteospf;
	private JCheckBox site;
	private JLabel ospfarea;
	private JComboBox ospf;
	private JLabel type;
	private JComboBox typecom;
	private JLabel metric;
	private JTextField metrictext;
	private JLabel bandwidth;
	private JTextField bandwidthtext;
	private JLabel route;
	private JTextField routetext;
	private JLabel ospfversion;
	private JTextField ospfversiontext;
	private JLabel count;
	private JTextField counttext;
	private JLabel maxtime;
	private JTextField maxtimetext;
	private JLabel gap;
	private JTextField gaptext;
	private JLabel lsa;
	private JTextField lsatext;
	private JLabel rfc;
	private JCheckBox rfccom;
	private JButton button;
	private JButton jButton;
	private JTable jTable;
	private String ospfInfoId = "";
}

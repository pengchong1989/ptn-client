﻿/*
 * AddSegment.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.basicinfo.dialog.segment;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import twaver.Element;
import twaver.Node;
import twaver.PopupMenuGenerator;
import twaver.TView;
import twaver.TWaverUtil;
import twaver.network.TNetwork;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.ptn.qos.QosQueueService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
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
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.basicinfo.SegmentPanel;
import com.nms.ui.ptn.basicinfo.dialog.site.SelectSiteDialog;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosConfigController;

/**
 * 新建 段
 * @author __USER__
 */
public class AddSegment extends PtnDialog {

	
	private static final long serialVersionUID = 1869039459713539167L;
	private Segment segment = null;
	private SegmentPanel segmentpanel;
	private PortInst portInst_a = null;
	private PortInst portInst_z = null;
	private static int ID = 0;
	private List<OamInfo> oamList = new ArrayList<OamInfo>();
	private List<OamInfo> oldoamList = new ArrayList<OamInfo>();
	private Map<Integer, List<QosQueue>> qosMap = new HashMap<Integer, List<QosQueue>>();

	private Map<Integer, Integer> aCosMap = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> zCosMap = new HashMap<Integer, Integer>();
	private SegmentTopology segementTopology=null;
	private Map<Integer, List<QosQueue>> oldqosMap = new HashMap<Integer, List<QosQueue>>();
	public AddSegment(SegmentPanel jPanel1, boolean modal, int id) {

		try {
			 jPanel1.setDialog(this);
			this.setModal(modal);
			initComponents();
			setLayout();
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_SEGMENT));
			ID = id;
			segementTopology=new SegmentTopology();
			jSplitPane1.setRightComponent(segementTopology);
			this.PoTo();
			segmentpanel =  jPanel1;
			if (0 != id && !"".equals(id)) {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_SEGMENT));
				this.getID(id);
				jTextField2.setEditable(false);
				jTextField3.setEditable(false);
				int asum = 0;
				int zsum = 0;

				if (segment.getAqosqueue().size() != 0) {
					for (int i = 0; i < segment.getAqosqueue().size(); i++) {
						asum += segment.getAqosqueue().get(i).getCir();
					}
				}

				if (segment.getZqosqueue().size() != 0) {
					for (int i = 0; i < segment.getZqosqueue().size(); i++) {
						zsum += segment.getZqosqueue().get(i).getCir();
					}
				}

				int sum = asum + zsum;
				qosTextfield.setText("totalCir=" + sum);

				setNameToOamTxt();

				if (segment != null) {
					this.jTextField1.setText(this.segment.getNAME());
					this.jTextField2.setText(this.getPortname(segment.getAPORTID()));
					this.jTextField3.setText(this.getPortname(segment.getZPORTID()));
				}
			} else {
				qosConfigButton.setEnabled(true);
				oamConfigButton.setEnabled(true);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 给oam名称赋值
	 */
	private void setNameToOamTxt() {
//		OamMepInfo oamMepA;
//		OamMepInfo oamMepZ;
//		OamInfo oamA;
//		OamInfo oamZ;
//		OamInfoService oamInfoService = null;
		try {
			/*oamMepA = new OamMepInfo();
			oamMepA.setObjType("SECTION");
			oamMepA.setServiceId(segment.getId());
			oamMepA.setSiteId(segment.getASITEID());
			oamMepA.setObjId(segment.getAPORTID());

			oamMepZ = new OamMepInfo();
			oamMepZ.setObjType("SECTION");
			oamMepZ.setServiceId(segment.getId());
			oamMepZ.setSiteId(segment.getZSITEID());
			oamMepZ.setObjId(segment.getZPORTID());

			oamA = new OamInfo();
			oamA.setOamMep(oamMepA);
			oamInfoService = (OamInfoService) ConstantUtil.serviceFactory.newService(Services.OamInfo);
			oamA = oamInfoService.queryByCondition(oamA, OamTypeEnum.AMEP);
			oamZ = new OamInfo();
			oamZ.setOamMep(oamMepZ);
			oamZ = oamInfoService.queryByCondition(oamZ, OamTypeEnum.ZMEP);
			if (oamA.getOamMep() != null) {
				segment.getOamList().add(oamA);
				segment.getOamList().add(oamZ);
			}*/
			if (segment.getOamList().size() != 0) {
				for (OamInfo oamInfo : segment.getOamList()) {
					if(null!=oamInfo.getOamMep().getMegIcc()&&!"".equals(oamInfo.getOamMep().getMegIcc())){
						oamTextfield.setText("megid=" + oamInfo.getOamMep().getMegIcc() + oamInfo.getOamMep().getMegUmc());
					}else{
						oamTextfield.setText("megid=" + oamInfo.getOamMep().getMegId());
					}
				}
			} else {
				oamTextfield.setText("megid=0");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
//			oamMepA = null;
//			oamMepZ = null;
//			oamA = null;
//			oamZ = null;
//			UiUtil.closeService(oamInfoService);
		}
	}

	/**
	 * 根据端口主键查询端口名称
	 * 
	 * @param portId
	 * @return
	 * @throws Exception
	 */
	public String getPortname(int portId) throws Exception {

		PortService_MB portService = null;
		List<PortInst> portInstList = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);

			PortInst portInst = new PortInst();
			portInst.setPortId(portId);
			portInstList = portService.select(portInst);

			if (portInstList != null && portInstList.size() == 1) {
				return portInstList.get(0).getPortName();
			} else {
				throw new Exception("查询portInst失败");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			portInstList = null;
		}
	}

	private void PoTo() {
		TNetwork network = null;
		try {
			if (ID != 0) {
				segementTopology.boxData(ID);
				segementTopology.getNetWork();
			} else {
				segementTopology.boxDate();
				network=segementTopology.getNetWork();
//				network.doLayout(TWaverConst.LAYOUT_CIRCULAR);//自动布局
				network.setPopupMenuGenerator(new PopupMenuGenerator() {
					@Override
					public JPopupMenu generate(TView tview, MouseEvent mouseEvent) {
						JPopupMenu menu = new JPopupMenu();
						if (!tview.getDataBox().getSelectionModel().isEmpty()) {
							final Element element = tview.getDataBox().getLastSelectedElement();
							if (element instanceof Node) {
								JMenuItem jMenuItemA = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SELECT_A_PORT));
								//选中A 端  端口事件   
								jMenuItemA.addActionListener(new java.awt.event.ActionListener() {
									@Override
									public void actionPerformed(java.awt.event.ActionEvent evt) {
										segementTopology.showSegmentDialog((SiteInst) element.getUserObject(), "A", element,AddSegment.this);
										TWaverUtil.clearImageIconCache();//关闭TWaver
										
									}
								});
								menu.add(jMenuItemA);

								JMenuItem jMenuItemZ = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SELECT_Z_PORT));
								//选中Z 端  端口事件   
								jMenuItemZ.addActionListener(new java.awt.event.ActionListener() {
									@Override
									public void actionPerformed(java.awt.event.ActionEvent evt) {
										segementTopology.showSegmentDialog((SiteInst) element.getUserObject(), "Z", element,AddSegment.this);
										TWaverUtil.clearImageIconCache();//关闭TWaver
									}
								});
								menu.add(jMenuItemZ);
							}
						}else{
							JMenuItem selectSite = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SELECTSITE));
							//选中A 端  端口事件   
							selectSite.addActionListener(new java.awt.event.ActionListener() {
								@Override
								public void actionPerformed(java.awt.event.ActionEvent evt) {
									SelectSiteDialog selectsitedialog = new SelectSiteDialog(segementTopology.getNetWork(), true);
									selectsitedialog.setLocation(UiUtil.getWindowWidth(selectsitedialog.getWidth()), UiUtil.getWindowHeight(selectsitedialog.getHeight()));
									selectsitedialog.setVisible(true);
									
								}
							});
							menu.add(selectSite);
						}
						return menu;
					}
				});

			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			network = null;
		}

	}

	public void setAText(String text) {
		this.jTextField2.setText(text);
	}

	public void setZText(String text) {
		this.jTextField3.setText(text);
	}

	public PortInst getPortInst_a() {
		return portInst_a;
	}

	public void setPortInst_a(PortInst portInst_a) {
		this.portInst_a = portInst_a;
	}

	public PortInst getPortInst_z() {
		return portInst_z;
	}

	public void setPortInst_z(PortInst portInst_z) {
		this.portInst_z = portInst_z;
	}

	/**
	 * 获取段ID
	 * 
	 * @param ID
	 * @throws Exception
	 */
	private void getID(int ID) throws Exception {

		SegmentService_MB segmentservice = null;
		QosQueueService_MB qosQueueService = null;
		List<Segment> segmentList = null;
		QosQueue aqosQueue = null;
		QosQueue zqosQueue = null;
		List<QosQueue> aqosQueues = null;
		List<QosQueue> zqosQueues = null;
		OamInfoService_MB infoService = null;
		OamInfo oamInfo = null;
		List<OamInfo> oamInfos = null;
		try {
			this.segment = new Segment();
			this.segment.setId(ID);
			infoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			segmentservice = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			qosQueueService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			segmentList = segmentservice.select(this.segment);
			if (segmentList != null && segmentList.size() > 0) {
				this.segment = segmentList.get(0);
			}
			oamInfo = new OamInfo();
			OamMepInfo oamMepInfo = new OamMepInfo();
			oamMepInfo.setServiceId(this.segment.getId());
			oamMepInfo.setObjType("SECTION");
			oamInfo.setOamMep(oamMepInfo);
			oamInfos = infoService.queryByServiceId(oamInfo);
			this.segment.setOamList(oamInfos);
	
			aqosQueue = new QosQueue();
			aqosQueue.setObjId(this.segment.getAPORTID());
			aqosQueue.setObjType("SECTION");
			aqosQueues = qosQueueService.queryByCondition(aqosQueue);
			this.segment.setAqosqueue(aqosQueues);
			zqosQueue = new QosQueue();
			zqosQueue.setObjId(this.segment.getZPORTID());
			zqosQueue.setObjType("SECTION");
			zqosQueues = qosQueueService.queryByCondition(zqosQueue);
			this.segment.setZqosqueue(zqosQueues);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(segmentservice);
			UiUtil.closeService_MB(qosQueueService);
			UiUtil.closeService_MB(infoService);
		}
	}

	/**
	 * 下拉列表选中
	 * 
	 * @param jComboBox
	 *            下拉列表对象
	 * @param selectId
	 *            选中的ID
	 */
	public  void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			if (((ControlKeyValue) jComboBox.getItemAt(i)).getId().equals(selectId)) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 * 
	 * @throws Exception
	 */
	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() throws Exception {
		Dimension dimension = new Dimension(1200, 700);
		this.setSize(dimension);
		this.setMinimumSize(dimension);

		this.lblMessage = new JLabel();
		jPanel2 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		jSplitPane1 = new javax.swing.JSplitPane();
		jLabel4 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_A_SIDE_PORT));
		jTextField2 = new PtnTextField();
		jTextField2.setEnabled(false);
		jLabel5 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_SIDE_PORT));
		jTextField3 = new PtnTextField();
		jTextField3.setEnabled(false);
		jButton1 = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.COREMODU,this);
		qosConfigButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		oamConfigButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		qosLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_QOS));
		oamLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM));
		qosTextfield = new PtnTextField();
		qosTextfield.setEnabled(false);
		oamTextfield = new PtnTextField();
		oamTextfield.setEnabled(false);
		autoNamingButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		jTextField1 = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.jButton1, this);
		qosConfigButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				qosConfigButtonActionPerformed(e);
				List<QosQueue> qosQueues = new ArrayList<QosQueue>();
				int sum = 0;

				if (qosMap.size() != 0) {
					for (List<QosQueue> qosQueue : qosMap.values()) {
						if (qosQueue != null && qosQueue.size() > 0)
							qosQueues.addAll(qosQueue);
						for (int i = 0; i < qosQueue.size(); i++) {
							sum += qosQueue.get(i).getCir();
						}
					}
					qosTextfield.setText("totalCir=" + sum);
				} else {
					qosTextfield.setText("totalCir=" + 0);
				}
			}
		});

		oamConfigButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				oamConfigButtonActionPerformed(e);

				if (oamList.size() != 0) {
					for (OamInfo oamInfo : oamList) {
						if(null!=oamInfo.getOamMep().getMegIcc()&&!"".equals(oamInfo.getOamMep().getMegIcc())){
							oamTextfield.setText("megid=" + oamInfo.getOamMep().getMegIcc() + oamInfo.getOamMep().getMegUmc());
						}else{
							oamTextfield.setText("megid=" + oamInfo.getOamMep().getMegId());
						}
					}
				} else {
					oamTextfield.setText("megid=0");
				}
			}
		});

		jButton1.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);
			}

			@Override
			public boolean checking() {
				return checkValue();
			}
		});
		autoNamingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				autoNamingActionPerformed(e);
			}
		});

	}

	private void setLayout() {
		this.add(this.jSplitPane1);
		this.jSplitPane1.setLeftComponent(this.jPanel2);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 150, 50 };
		layout.columnWeights = new double[] { 0, 0, 0 };
		layout.rowHeights = new int[] { 25, 30, 30, 30, 30, 30, 15, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.jPanel2.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 55, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		this.jPanel2.add(this.lblMessage);

		/** 第一行 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(jLabel2, c);
		this.jPanel2.add(jLabel2);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(jTextField1, c);
		this.jPanel2.add(jTextField1);
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 5, 5);
		layout.addLayoutComponent(this.autoNamingButton, c);
		this.jPanel2.add(this.autoNamingButton);
		/** 第二行 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.jLabel4, c);
		this.jPanel2.add(this.jLabel4);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.jTextField2, c);
		this.jPanel2.add(this.jTextField2);

		/** 第三行 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.jLabel5, c);
		this.jPanel2.add(this.jLabel5);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.jTextField3, c);
		this.jPanel2.add(this.jTextField3);
		/** 第四行 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.qosLable, c);
		this.jPanel2.add(this.qosLable);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.qosTextfield, c);
		this.jPanel2.add(this.qosTextfield);
		c.gridx = 2;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.qosConfigButton, c);
		this.jPanel2.add(this.qosConfigButton);
		/** 第五行 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.oamLable, c);
		this.jPanel2.add(this.oamLable);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.oamTextfield, c);
		this.jPanel2.add(this.oamTextfield);
		c.gridx = 2;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.oamConfigButton, c);
		this.jPanel2.add(this.oamConfigButton);
		/** 第7行 确定按钮 中间空出一行 */
		c.gridx = 2;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.jButton1, c);
		this.jPanel2.add(this.jButton1);
	}

	private void oamConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if(null == segment){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
			this.insertOpeLog(EOperationLogType.ADDSEGMENT1.getValue(),ResultString.CONFIG_FAILED, null, null);	
			return;
		}
		//配置oam之前先查询oammepInfo表
		this.setOam2Segment();
		new OamInfoDialog(segment, EServiceType.SECTION.toString(), 0, true, this);
	}

	/**
	 * 配置oam之前先查询oammepInfo表
	 * 在新建段时或者修改之前没有配oam,有可能在单站侧配置了端口的oam
	 */
	private void setOam2Segment() {
		try {
//			if(this.segment.getId() == 0 || (this.segment.getId() > 0 && this.segment.getOamList().size() == 0)){
			this.segment.getOamList().clear();
			this.getOamInfoCondition("a");
			this.getOamInfoCondition("z");
//			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void getOamInfoCondition(String direction) throws Exception {
		OamInfo oamInfo = new OamInfo();
		OamMepInfo oamMepInfo = new OamMepInfo();
		OamInfoService_MB oamService = null;
		try {
			if(direction.equals("a")){
				oamMepInfo.setObjId(this.segment.getAPORTID());
				oamMepInfo.setSiteId(this.segment.getASITEID());
			}else{
				oamMepInfo.setObjId(this.segment.getZPORTID());
				oamMepInfo.setSiteId(this.segment.getZSITEID());
			}
			oamMepInfo.setObjType("SECTION");
//			oamMepInfo.setServiceId(this.segment.getId());
			oamInfo.setOamMep(oamMepInfo); 
			oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			OamMepInfo oamMepInfo2 = oamService.queryMep(oamMepInfo);
			if(oamMepInfo2!=null && oamMepInfo2.getId() > 0){
				oamInfo.setOamMep(oamMepInfo2);
				oamInfo.setId(oamMepInfo2.getId());
				this.segment.getOamList().add(oamInfo);
			}else{
				this.segment.getOamList().add(oamInfo);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(oamService);
		}
	}

	private void qosConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			qosConfig();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/*
	 * qos配置
	 */
	private void qosConfig() throws Exception {
		if (!checkPortHasConfig()) {
			return;
		}
		try {
			ConstantUtil.QOS_SEGMENT_A = UiUtil.getMaxCir(this.jTextField2.getText());
			ConstantUtil.QOS_SEGMENT_Z = UiUtil.getMaxCir(this.jTextField3.getText());
			// System.out.println(ConstantUtil.QOS_SEGMENT_A+"\t"+ConstantUtil.QOS_SEGMENT_Z);
			QosConfigController controller = new QosConfigController();
			controller.openQosConfig(controller, "SECTION", segment, null,this);
		} catch (Exception e) {
			throw e;
		}

	}

	private boolean checkPortHasConfig() {
		if ("".equals(jTextField2.getText()) || "".equals(jTextField3.getText())) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
			UiUtil.insertOperationLog(EOperationLogType.ADDSEGMENT2.getValue());
			return false;
		}
		if (segment == null) {
			segment = new Segment();
			segment.setNAME(jTextField1.getText());
			segment.setASITEID(this.getPortInst_a().getSiteId());
			segment.setAPORTID(this.getPortInst_a().getPortId());
			segment.setZSITEID(this.getPortInst_z().getSiteId());
			segment.setZPORTID(this.getPortInst_z().getPortId());
		}
		return true;
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		SegmentService_MB segmentservice = null;
		DispatchUtil segmentImpl = null;
		String result = null;
		SiteService_MB siteService = null;
		try {
			if(this.checkSegmentQos()){
				siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				segmentservice = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
				// 段qos验证
//				if (null == segment) {
//					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
//					return;
//				}

//				if (!jTextField1.getText().trim().equals(segment.getNAME())) {
//					Segment seg = new Segment();
//					seg.setNAME(jTextField1.getText().trim());
//					List<Segment> ret = segmentservice.select(seg);
//					if (ret != null && ret.size() > 0) {
//						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
//						// jTextField1.setBorder(new LineBorder(Color.RED));
//						return;
//					}
//				}
//				this.segment.setNAME(jTextField1.getText().trim());
//				if (segment.getId() == 0) {
//					if (this.portInst_a != null && this.portInst_z != null) {
//						if (!segmentservice.comparePortSpeed(this.portInst_a, this.portInst_z)) {
//							DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CREATE_SEGMENT_SPEED));
//							return;
//						}
//						//如果是武汉网元，赋值工作模式   晨晓网元赋值实际速率 
//						if (siteService.getManufacturer(this.portInst_a.getSiteId()) == EManufacturer.WUHAN.getValue()) {
//							this.segment.setSpeedSegment(this.portInst_a.getPortAttr().getWorkModel()+"");
//						}else{
//							this.segment.setSpeedSegment(this.portInst_a.getPortAttr().getPortSpeed()+"");
//						}
	//
//						this.segment.setAPORTID(this.portInst_a.getPortId());
//						this.segment.setASITEID(this.portInst_a.getSiteId());
//						this.segment.setaSlotNumber(this.portInst_a.getSlotNumber());
//						this.segment.setZPORTID(this.portInst_z.getPortId());
//						this.segment.setZSITEID(this.portInst_z.getSiteId());
//						this.segment.setzSlotNumber(this.portInst_a.getSlotNumber());
//					} else {
//						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORTNULL));
//						return;
//					}
//					if (this.qosMap.isEmpty()) {
//						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
//						return;
//					}
//				}
				
				segmentImpl = new DispatchUtil(RmiKeys.RMI_SEGMENT);
				segment.setOamList(oamList);
				segment.setQosMap(qosMap);
				segment.setCREATUSER(ConstantUtil.user.getUser_Name());
				if (segment.getId() == 0) {
					segment.setShowSiteAname(siteService.select(segment.getASITEID()).getCellId());
					segment.setShowSiteZname(siteService.select(segment.getZSITEID()).getCellId());
					segment.setShowPortAname(this.getPortname(segment.getAPORTID()));
					segment.setShowPortZname(this.getPortname(segment.getZPORTID()));				
					for(int i=0;i<segment.getQosMap().get(segment.getASITEID()).size();i++){
						segment.getQosMap().get(segment.getASITEID()).get(i).setSiteName(segment.getShowSiteAname());
					}
					for(int i=0;i<segment.getQosMap().get(segment.getZSITEID()).size();i++){
						segment.getQosMap().get(segment.getZSITEID()).get(i).setSiteName(segment.getShowSiteZname());
					}											
					result = segmentImpl.excuteInsert(segment);
					this.insertOpeLog(EOperationLogType.SEGEMENTINSERT.getValue(), result, null, segment);					
				} else {
			
					segment.setShowSiteAname(siteService.select(segment.getASITEID()).getCellId());
					segment.setShowSiteZname(siteService.select(segment.getZSITEID()).getCellId());
					segment.setShowPortAname(this.getPortname(segment.getAPORTID()));
					segment.setShowPortZname(this.getPortname(segment.getZPORTID()));	
					if(!segment.getQosMap().isEmpty()){
						for(int i=0;i<segment.getQosMap().get(segment.getASITEID()).size();i++){
							segment.getQosMap().get(segment.getASITEID()).get(i).setSiteName(segment.getShowSiteAname());
						}
						for(int i=0;i<segment.getQosMap().get(segment.getZSITEID()).size();i++){
							segment.getQosMap().get(segment.getZSITEID()).get(i).setSiteName(segment.getShowSiteZname());
						}
					}
					Segment seg = this.getSegForLog();
					result = segmentImpl.excuteUpdate(segment);
					this.insertOpeLog(EOperationLogType.SEGEMENTUPDATE.getValue(), result, seg, segment);	
				}
				this.dispose();
				TWaverUtil.clearImageIconCache();
				if (null != segmentpanel) {
					segmentpanel.getController().refresh();
				}
				DialogBoxUtil.succeedDialog(segmentpanel, result);
			}else{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORT_QOS_ALARM));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(segmentservice);
			UiUtil.closeService_MB(siteService);
		}
	}
   
	private Segment getSegForLog() {
		SiteService_MB siteService = null;
		SegmentService_MB service = null;
		Segment seg = null;
		QosQueueService_MB qosQueueService=null;
		Map<Integer, List<QosQueue>> oldqosMap = null;
		List<QosQueue> infoList = null;
		try {
			qosQueueService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			Segment condition = new Segment();
			condition.setId(this.segment.getId());
			seg = service.select(condition).get(0);
			QosQueue zqosQueue = new QosQueue();
			zqosQueue.setObjId(seg.getZPORTID());
			zqosQueue.setObjType("SECTION");
			List<QosQueue> z=qosQueueService.queryByCondition(zqosQueue);
			QosQueue aqosQueue = new QosQueue();
			aqosQueue.setObjId(seg.getAPORTID());
			aqosQueue.setObjType("SECTION");
			List<QosQueue> a=qosQueueService.queryByCondition(aqosQueue);		
			oldqosMap = new HashMap<Integer, List<QosQueue>>();
			for (QosQueue qosQueue : a) {
				if (oldqosMap.get(qosQueue.getSiteId()) == null) {
					infoList = new ArrayList<QosQueue>();
					for (QosQueue info : a) {
						if (info.getSiteId() == qosQueue.getSiteId()) {
							info.setSiteName(siteService.select(seg.getASITEID()).getCellId());
							infoList.add(info);
						}

					}
					oldqosMap.put(qosQueue.getSiteId(), infoList);
				}
			}
			for (QosQueue qosQueue : z) {
				if (oldqosMap.get(qosQueue.getSiteId()) == null) {
					infoList = new ArrayList<QosQueue>();
					for (QosQueue info : z) {
						if (info.getSiteId() == qosQueue.getSiteId()) {
							info.setSiteName(siteService.select(seg.getZSITEID()).getCellId());
							infoList.add(info);
						}

					}
					oldqosMap.put(qosQueue.getSiteId(), infoList);
				}
			}
			seg.setQosMap(oldqosMap);
			if(this.segment.getOamList().isEmpty()){
			   seg.setOamList(null);
			}
			if(this.segment.getQosMap().isEmpty()){
				   seg.setQosMap(null);
				}
			seg.setShowSiteAname(siteService.select(seg.getASITEID()).getCellId());
			seg.setShowSiteZname(siteService.select(seg.getZSITEID()).getCellId());
			seg.setShowPortAname(this.getPortname(seg.getAPORTID()));
			seg.setShowPortZname(this.getPortname(seg.getZPORTID()));		
			this.getOamSiteName(seg);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(qosQueueService);
		}
		return seg;
	}
	
	
	
	private void insertOpeLog(int operationType, String result, Segment oldSeg, Segment newSeg){
		this.getOamSiteName(newSeg);
		newSeg.setAqosqueue(null);
		newSeg.setZqosqueue(null);
		AddOperateLog.insertOperLog(jButton1, operationType, result, oldSeg, newSeg, newSeg.getASITEID(), newSeg.getNAME(), "segment");
		AddOperateLog.insertOperLog(jButton1, operationType, result, oldSeg, newSeg, newSeg.getZSITEID(), newSeg.getNAME(), "segment");
		
	}
	
	private void getOamSiteName(Segment seg){
		List<OamInfo> oamList = seg.getOamList();
		if(oamList != null && oamList.size() > 0){
			for (OamInfo oamInfo : oamList) {
				if(oamInfo.getOamMep().getSiteId() == seg.getASITEID()){
					oamInfo.getOamMep().setSiteName(seg.getShowSiteAname());
				}else if(oamInfo.getOamMep().getSiteId() == seg.getZSITEID()){
					oamInfo.getOamMep().setSiteName(seg.getShowSiteZname());
				}
			}
		}
	}
   /**
	 * 修改时验证段的qos
	 * @param segmentDialog 
	 */
	private boolean checkSegmentQos() {
		Map<Integer, List<QosQueue>> qosMap = this.getQosMap();
		for (int siteId : qosMap.keySet()) {
			if(segment.getASITEID() == siteId){ 
				boolean result = this.checkQosCir(siteId, segment.getAPORTID(), qosMap.get(siteId), segment.getAqosqueue());
				if(!result){
					return false;
				}
			}else{
				boolean result = this.checkQosCir(siteId, segment.getZPORTID(), qosMap.get(siteId), segment.getZqosqueue());
				if(!result){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 修改段的qos队列的cir值时验证带宽是否满足tunnel使用
	 * @param portId 
	 * @param qosQueueList 
	 * @param selectPortId 
	 * 通过 返回true
	 * 不通过 返回false
	 */
	private boolean checkQosCir(int siteId, int portId, List<QosQueue> qosQueueList_after,
		List<QosQueue> qosQueueList) {
		TunnelService_MB tunnelService = null;
		QosInfoService_MB qosService = null;
		boolean result = true;
		try {
			//验证带宽之前,先判断端口带宽是否改变,如果不变就不必验证,如果改变并且改之后的带宽小于改之前的带宽就需要验证
			Map<Integer, Integer> qosMap_after = new HashMap<Integer, Integer>();
			for (QosQueue qos : qosQueueList) {
				for (QosQueue qosAfter : qosQueueList_after){
					if(qos.getCos() == qosAfter.getCos() && 
							qos.getCir() > qosAfter.getCir()){
						qosMap_after.put(qosAfter.getCos(), qosAfter.getCir());
					}
				}
			}
			//有需要验证的带宽
			if(qosMap_after.size() > 0){
				qosService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo);
				tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				List<Tunnel> tunnelList = tunnelService.selectByPortIdAndSiteId(siteId, portId);
				List<Integer> tunnelIdList = new ArrayList<Integer>();
				for (Tunnel tunnel : tunnelList) {
					tunnelIdList.add(tunnel.getTunnelId());
				}
				List<QosInfo> qosList = new ArrayList<QosInfo>();
				qosList = qosService.selectByCondition("TUNNEL", tunnelIdList);
				//以tunnel已用的qos等级和带宽为准,验证修改的端口的qos带宽是否满足要求
				if(qosList.size() > 0){
					//验证前向
					result = this.checkCirByDirection(1, qosList, qosMap_after, siteId);
					if(result == true){
						//验证后向
						result = this.checkCirByDirection(2, qosList, qosMap_after, siteId);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(qosService);
			UiUtil.closeService_MB(tunnelService);
		}
		return result;
	}

	/**
	 * 根据前后向去验证带宽
	 * 通过 返回true
	 * 不通过 返回false
	 * @param siteId 
	 */
	private boolean checkCirByDirection(int direction, List<QosInfo> qosList, 
			Map<Integer, Integer> qosMap_after, int siteId) {
		Map<Integer, Integer> cos_cirMap = new HashMap<Integer, Integer>();
		for (QosInfo usedQos : qosList) {
			if(Integer.parseInt(usedQos.getDirection()) == direction && usedQos.getSiteId() == siteId){
				if(cos_cirMap.get(usedQos.getCos()) == null){
					cos_cirMap.put(usedQos.getCos(), 0+usedQos.getCir());
				}else{
					cos_cirMap.put(usedQos.getCos(), cos_cirMap.get(usedQos.getCos())+usedQos.getCir());
				}
			}
		}
		for(int key : qosMap_after.keySet()){
			if(cos_cirMap.get(key) != null){
				if(qosMap_after.get(key) < cos_cirMap.get(key)){
					//端口带宽小于端口所承载的tunnel带宽,则不满足
					return false;
				}
			}
		}
		return true;
	}

//验证数据	
	private boolean checkValue() {
		boolean flag = false;
		SiteService_MB siteService = null;
		SegmentService_MB segmentservice = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			segmentservice = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			// 段qos验证
			if (null == segment) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
				this.insertOpeLog(EOperationLogType.ADDSEGMENT1.getValue(), ResultString.CONFIG_FAILED, null, null);
				return false;
			}
			//验证名称
			//Segment
			//修改
			if(segment.getId()>0){
				if (!jTextField1.getText().trim().equals(segment.getNAME())) {
					if(!isSameName(segmentservice)){
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
						this.insertOpeLog(EOperationLogType.ADDSEGMENT5.getValue(), ResultString.CONFIG_FAILED, null, null);
						return false;
					}
//					Segment seg = new Segment();
//					seg.setNAME(jTextField1.getText().trim());
//					List<Segment> ret = segmentservice.select(seg);
//					if (ret != null && ret.size() > 0) {
//						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
//						// jTextField1.setBorder(new LineBorder(Color.RED));
//						return false;
//					}
				}
			}else{
				if(!isSameName(segmentservice)){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
					this.insertOpeLog(EOperationLogType.ADDSEGMENT5.getValue(), ResultString.CONFIG_FAILED, null, null);
					return false;
				}
			}
			this.segment.setNAME(jTextField1.getText().trim());
			//验证端口的速率
			if (segment.getId() == 0) {
				if (this.portInst_a != null && this.portInst_z != null) {
					if (!segmentservice.comparePortSpeed(this.portInst_a, this.portInst_z)) {
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CREATE_SEGMENT_SPEED));
						this.insertOpeLog(EOperationLogType.ADDSEGMENT6.getValue(), ResultString.CONFIG_FAILED, null, null);
						return false;
					}
					//如果是武汉网元，赋值工作模式   晨晓网元赋值实际速率 
					if (siteService.getManufacturer(this.portInst_a.getSiteId()) == EManufacturer.WUHAN.getValue()) {
						this.segment.setSpeedSegment(this.portInst_a.getPortAttr().getWorkModel()+"");
					}else{
						this.segment.setSpeedSegment(this.portInst_a.getPortAttr().getPortSpeed()+"");
					}
					this.segment.setAPORTID(this.portInst_a.getPortId());
					this.segment.setASITEID(this.portInst_a.getSiteId());
					this.segment.setaSlotNumber(this.portInst_a.getSlotNumber());
					this.segment.setZPORTID(this.portInst_z.getPortId());
					this.segment.setZSITEID(this.portInst_z.getSiteId());
					this.segment.setzSlotNumber(this.portInst_a.getSlotNumber());
				} else {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORTNULL));
					UiUtil.insertOperationLog(EOperationLogType.ADDSEGMENT6.getValue());
					return false;
				}
				if (this.qosMap.isEmpty()) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
					this.insertOpeLog(EOperationLogType.ADDSEGMENT4.getValue(), ResultString.CONFIG_FAILED, null, null);
					return false;
				}
			}
			
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(segmentservice);
			UiUtil.closeService_MB(siteService);
		}
		return flag;
	}

	
	private boolean isSameName(SegmentService_MB segmentservice){
		boolean flag = false;
		try {
			Segment seg = new Segment();
			seg.setNAME(jTextField1.getText().trim());
			List<Segment> ret = segmentservice.select(seg);
			if (ret != null && ret.size() > 0) {
				return false;
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	/**
	 * 自动命名
	 * 
	 * @param e
	 */
	private void autoNamingActionPerformed(ActionEvent e) {
		try {
			if (null != segment && 0 != segment.getId()) {
				portInst_a = new PortInst();
				portInst_z = new PortInst();
				portInst_a.setSiteId(segment.getASITEID());
				portInst_z.setSiteId(segment.getZSITEID());
				portInst_a.setPortName(segment.getShowPortAname());
				portInst_z.setPortName(segment.getShowPortZname());
			}
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			String autoNaming = (String) autoNamingUtil.autoNaming(new Segment(), portInst_a, portInst_z);
			if (autoNaming.equals(ResourceUtil.srcStr(StringKeysTip.TIP_PORTNULL))) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORTNULL));
				this.insertOpeLog(EOperationLogType.ADDSEGMENT8.getValue(), ResultString.CONFIG_FAILED, null, null);
				return;
			}
			jTextField1.setText(autoNaming);
		} catch (Exception e1) {
			ExceptionManage.dispose(e1, this.getClass());
		}
	}
	public List<OamInfo> getOamList() {
		return oamList;
	}

	public void setOamList(List<OamInfo> oamList) {
		this.oamList = oamList;
	}

	public Map<Integer, List<QosQueue>> getQosMap() {
		return qosMap;
	}

	public void setQosMap(Map<Integer, List<QosQueue>> qosMap) {
		this.qosMap = qosMap;
	}

	public Map<Integer, Integer> getaCosMap() {
		return aCosMap;
	}

	public void setaCosMap(Map<Integer, Integer> aCosMap) {
		this.aCosMap = aCosMap;
	}

	public Map<Integer, Integer> getzCosMap() {
		return zCosMap;
	}

	public void setzCosMap(Map<Integer, Integer> zCosMap) {
		this.zCosMap = zCosMap;
	}

	public SegmentTopology getSegementTopology() {
		return segementTopology;
	}

	public void setSegementTopology(SegmentTopology segementTopology) {
		this.segementTopology = segementTopology;
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private PtnButton jButton1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JTextField jTextField3;
	private javax.swing.JButton oamConfigButton;
	private javax.swing.JButton qosConfigButton;
	private javax.swing.JLabel qosLable;
	private javax.swing.JLabel oamLable;
	private javax.swing.JTextField qosTextfield;
	private javax.swing.JTextField oamTextfield;
	private JLabel lblMessage;
	private javax.swing.JButton autoNamingButton;
	// End of variables declaration//GEN-END:variables

}
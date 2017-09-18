package com.nms.ui.ptn.business.dialog.cespath.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;

import twaver.DataBoxSelectionAdapter;
import twaver.DataBoxSelectionEvent;
import twaver.Element;
import twaver.Node;
import twaver.Port;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.port.PortStmTimeslot;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EManagerStatus;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.port.PortStmTimeslotService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.business.dialog.cespath.ChooseTimeSlotDialog;
import com.nms.ui.ptn.business.dialog.cespath.modal.CesPortInfo;

public class ChooseTimeSlotController {
	private final ChooseTimeSlotDialog dialog;
	private final SiteInst siteInst;
	private Map<JCheckBox, PortStmTimeslot> jcbAndPortStmTimeSlotMap = null;
	private List<JCheckBox> jcbList = null;

	public ChooseTimeSlotController(ChooseTimeSlotDialog dialog, SiteInst siteInst) {
		this.dialog = dialog;
		this.siteInst = siteInst;
		addListeners();
	}
	/**
	 * 处理事件
	 */
	private void addListeners() {
		this.dialog.getTreebox().getSelectionModel().addDataBoxSelectionListener(new DataBoxSelectionAdapter() {
			@Override
			public void selectionChanged(DataBoxSelectionEvent e) {

				Element element = dialog.getTreebox().getLastSelectedElement();
				PortStmTimeslotService_MB portStmTimeService = null;
				PortInst portInst = null;
				List<PortStmTimeslot> portStmTimeSlotList = null;
				if (element instanceof Port) {
					try {
						portInst = (PortInst) element.getUserObject();
						portStmTimeService = (PortStmTimeslotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTSTMTIMESLOT);
						portStmTimeSlotList = portStmTimeService.selectbyportId(portInst.getPortId()); // 按portId查询时隙
						initStmTimeSlot(portStmTimeSlotList, portInst);
					} catch (Exception exception) {
						exception.printStackTrace();
					} finally {
						UiUtil.closeService_MB(portStmTimeService);
						portInst = null;
						portStmTimeSlotList = null;
					}
				}
			}
		});
		this.dialog.getOkBtn().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<PortStmTimeslot> StmTimeslotList = new ArrayList<PortStmTimeslot>();
				for (JCheckBox jcb : jcbList) {
					if (jcb.isSelected()) {
						StmTimeslotList.add(jcbAndPortStmTimeSlotMap.get(jcb));
					}
				}

				List<CesPortInfo> cesPortInfoList = new ArrayList<CesPortInfo>();
				CesPortInfo cesPort = null;
				for (PortStmTimeslot portStmTimeSlot : StmTimeslotList) {
					cesPort = new CesPortInfo();
					cesPort.setPortStmTimeSlot(portStmTimeSlot);
					cesPortInfoList.add(cesPort);
				}
				// 拿出端口对应的网元
				int siteId = 0;
				if (!StmTimeslotList.isEmpty()) {
					siteId = StmTimeslotList.get(0).getSiteId();
				}

				if (dialog.getType().equalsIgnoreCase("A")) {
					// 将选择的端口放到A端表中
					dialog.getAddCesDialog().loadPortTable_a(cesPortInfoList, siteId);

				} else if (dialog.getType().equalsIgnoreCase("Z")) {
					// 将选择的端口放到Z端表中
					dialog.getAddCesDialog().loadPortTable_z(cesPortInfoList, siteId);
				}
				if (dialog.getAddCesDialog().getPortTable_a().getRowCount() != 0 && dialog.getAddCesDialog().getPortTable_z().getRowCount() != 0) {
					loadPW();
				}
				dialog.dispose();
			}
		});
		this.dialog.getCancelBtn().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});

		/*
		 * dialog.getNetwork().setPopupMenuGenerator(new PopupMenuGenerator() {
		 * 
		 * @Override public JPopupMenu generate(TView tview, MouseEvent mouseEvent) { JPopupMenu menu = new JPopupMenu(); final Element portStmElement = tview.getDataBox() .getLastSelectedElement(); if (portStmElement instanceof Port) { final PortStmTimeslot portStmInst = (PortStmTimeslot) portStmElement .getUserObject(); JMenuItem jMenuItemSelect = new JMenuItem("选中"); jMenuItemSelect.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent arg0) { doChoosePort(portStmInst, dialog); } }); menu.add(jMenuItemSelect); } return menu; } });
		 */
	}

	/*
	 * private void doChoosePort(PortStmTimeslot portStmInst, JDialog dialog) { try { if (dialog instanceof ChooseTimeSlotDialog) { this.setPortInfo(this.siteInst, portStmInst, ((ChooseTimeSlotDialog) dialog).getType()); } dialog.dispose();
	 * 
	 * } catch (Exception e) { ExceptionManage.dispose(e,this.getClass()); } }
	 */

	/**
	 * 给tunnel面板上的控件赋值
	 * 
	 * @param siteInst
	 * @param portInst
	 * @param type
	 */
	/*
	 * private void setPortInfo(SiteInst siteInst, PortStmTimeslot portStmInst, String type) { Field field = null; FieldService fieldService = null; List<Field> fieldList = null; String text = null; try { field = new Field(); field.setId(siteInst.getFieldID()); fieldService = (FieldService) ConstantUtil.serviceFactory .newService(Services.Field); fieldList = fieldService.select(field); if (fieldList == null || fieldList.size() != 1) { throw new Exception("查询field出错"); } text = fieldList.get(0).getFieldName() + "/" + siteInst.getCellId() + "/" + portStmInst.getTimeslotnumber(); if (type.equals("A")) { this.dialog.getAddCesDialog().setPortStmTime_a(portStmInst); this.dialog.getAddCesDialog().setPortInst_a(null); this.dialog.getAddCesDialog().setSiteId_a( portStmInst.getSiteId()); this.dialog.getAddCesDialog().setAText(text); } else if (type.equals("Z")) { this.dialog.getAddCesDialog().setPortStmTime_z(portStmInst); this.dialog.getAddCesDialog().setPortInst_z(null); this.dialog.getAddCesDialog().setSiteId_z( portStmInst.getSiteId()); this.dialog.getAddCesDialog().setZText(text); }
	 * 
	 * if (!this.dialog.getAddCesDialog().getTfAPort().getText() .equals("") && !this.dialog.getAddCesDialog().getTfZPort().getText() .equals("")) { // 加载可以的pw loadPW(); } } catch (Exception e) { ExceptionManage.dispose(e,this.getClass()); } finally { field = null; fieldService = null; fieldList = null; text = null; } }
	 */

	private void loadPW() {
		List<PwInfo> filterPwInfoList = null;
		List<Integer> siteIdList = null;
		Map<Integer, PwInfo> pwIdAndPwInfoMap = null;
		ControlKeyValue kc = null;
		Code code=null;
		try {
			filterPwInfoList = new ArrayList<PwInfo>();
			siteIdList = new ArrayList<Integer>();
			siteIdList.add(this.dialog.getAddCesDialog().getSiteId_a());
			siteIdList.add(this.dialog.getAddCesDialog().getSiteId_z());
			pwIdAndPwInfoMap = getPwIdAndPwInfoMap(siteIdList);

			for (PwInfo pw : pwIdAndPwInfoMap.values()) {
				if (pw.getASiteId() == this.dialog.getAddCesDialog().getSiteId_a() && pw.getZSiteId() == this.dialog.getAddCesDialog().getSiteId_z() || pw.getZSiteId() == this.dialog.getAddCesDialog().getSiteId_a() && pw.getASiteId() == this.dialog.getAddCesDialog().getSiteId_z()) {
					if (pw.getRelatedServiceId() == 0) { // 没有被用过的pw
						kc = (ControlKeyValue) this.dialog.getAddCesDialog().getServiceTypeCbox().getSelectedItem();
						code=(Code) kc.getObject();
						if (code.getCodeValue().equals(pw.getType().getValue()+"")) {
							filterPwInfoList.add(pw);
						}

					}
				}
			}
			this.dialog.getAddCesDialog().loadPw(filterPwInfoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			filterPwInfoList = null;
			pwIdAndPwInfoMap = null;
			siteIdList=null;
			kc=null;
			code=null;
		}
	}

	protected void initStmTimeSlot(List<PortStmTimeslot> portStmTimeSlotList, PortInst portInst) {

		JCheckBox jcb = null;
		if (jcbAndPortStmTimeSlotMap != null) {
			jcbAndPortStmTimeSlotMap.clear();
		}
		if (jcbList != null) {
			jcbList.clear();
		}
		if (portStmTimeSlotList != null && !portStmTimeSlotList.isEmpty()) {
			jcbAndPortStmTimeSlotMap = new HashMap<JCheckBox, PortStmTimeslot>();
			jcbList = new ArrayList<JCheckBox>();
			for (int i = 0; i < portStmTimeSlotList.size(); i++) {
				jcb = new JCheckBox(i + 1 + "");
				jcbList.add(jcb);
				jcbAndPortStmTimeSlotMap.put(jcb, portStmTimeSlotList.get(i));
			}
			// 把创建的时隙放到对话框的面板里
			placeStmTimeSlotCheck();

			/*
			 * double x = this.dialog.getCenterPanel().getLocation().getX() - 120; double y = this.dialog.getCenterPanel().getLocation().getY() + 5; int index = 0;
			 */
			/*
			 * for (int i = 0; i < 8; i++) { for (int j = 0; j < 8; j++) { if (index < 63) { PortStmTimeslot port = portStmTimeSlotList.get(index); index++;
			 * 
			 * this.dialog.getBox().addElement( createPort(port, x + i * 75, y + j * 60 , portInst)); } } }
			 */

		}
	}

	private void placeStmTimeSlotCheck() {
		dialog.getJcheJPanel().removeAll();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 50, 50, 50, 50, 50, 50, 50 };
		layout.columnWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		layout.rowHeights = new int[] { 30, 30, 30, 30, 30, 30, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		dialog.getJcheJPanel().setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 5, 5);
		int index = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				c.gridx = i;
				c.gridy = j;
				if (index < 63) {
					layout.setConstraints(jcbList.get(index), c);
					dialog.getJcheJPanel().add(jcbList.get(index));
					index++;
				}
			}
		}
		setCheckBoxIsEnable();
		this.dialog.getCenterPanel().updateUI();
		this.dialog.getJcheJPanel().updateUI();

	}

	// 如果该时隙被用了或者是未使能状态，不能选择
	private void setCheckBoxIsEnable() {
		PortStmTimeslot portStmTimeslot = null;
		try {
			for (JCheckBox jcb : jcbList) {
				jcb.setEnabled(false);
				portStmTimeslot = jcbAndPortStmTimeSlotMap.get(jcb);
				if (portStmTimeslot.getManagerStatus() == EManagerStatus.ENABLED.getValue() && portStmTimeslot.getIsUsed() == 0) {
					jcb.setEnabled(true);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			portStmTimeslot = null;
		}

	}

	/*	*//**
	 * 创建端口
	 * 
	 * @param portInst
	 * @param card
	 * @return
	 */
	/*
	 * private Port createPort(PortStmTimeslot portStmTimeInst, double x, double x2 , PortInst portInst) { Port port = new Port(); port.setImage(portInst.getImagePath()); port.setLocation(x, x2); port.setUserObject(portStmTimeInst); port.setDisplayName(portStmTimeInst.getTimeslotnumber()); port.setSelected(false); return port; }
	 */

	public void initData() {
		PortService_MB portService = null;
		List<PortInst> allPortInsts = null;
		List<PortInst> stmPortInsts = null;
		PortInst portInst = null;
		try {
			portInst = new PortInst();
			portInst.setSiteId(siteInst.getSite_Inst_Id());
			stmPortInsts = new ArrayList<PortInst>();
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			allPortInsts = portService.select(portInst);
			for (PortInst info : allPortInsts) {
				if (info.getPortType().equalsIgnoreCase("stm1")) {
					stmPortInsts.add(info);
				}
			}
			initTreeData(stmPortInsts);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			allPortInsts=null;
			stmPortInsts=null;
			portInst=null;
		}
	}

	private void initTreeData(List<PortInst> stmPortInst) {
		Node nodeParent = new Node();
		this.dialog.getTreebox().addElement(nodeParent);
		nodeParent.setName("NE:" + siteInst.getCellId());
		Port port = null;

		for (PortInst portInst : stmPortInst) {
			port = new Port(portInst.getPortId());
			port.setParent(nodeParent);
			port.setName(portInst.getPortName());
			port.setUserObject(portInst);
			this.dialog.getTreebox().addElement(port);
		}
	}

	public Map<Integer, PwInfo> getPwIdAndPwInfoMap(List<Integer> siteIdList) {
		PwInfoService_MB pwInfoService = null;
		List<PwInfo> pwInfoList = null;
		Map<Integer, PwInfo> pwIdAndPwInfoMap = null;
		try {
			pwIdAndPwInfoMap = new HashMap<Integer, PwInfo>();
			pwInfoList = new ArrayList<PwInfo>();
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for (Integer siteId : siteIdList) {
				pwInfoList.addAll(pwInfoService.select(siteId));
			}

			for (PwInfo pwInfo : pwInfoList) {
				if (pwIdAndPwInfoMap.get(pwInfo.getPwId()) == null) {
					pwIdAndPwInfoMap.put(pwInfo.getPwId(), pwInfo);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(pwInfoService);
			pwInfoList = null;
		}
		return pwIdAndPwInfoMap;
	}
}

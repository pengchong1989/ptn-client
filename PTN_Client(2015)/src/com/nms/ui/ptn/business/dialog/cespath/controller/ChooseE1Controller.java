package com.nms.ui.ptn.business.dialog.cespath.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;

import twaver.Card;
import twaver.DataBoxSelectionAdapter;
import twaver.DataBoxSelectionEvent;
import twaver.Element;
import twaver.Node;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EManagerStatus;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.business.dialog.cespath.AddCesDialog;
import com.nms.ui.ptn.business.dialog.cespath.ChooseE1Dialog;
import com.nms.ui.ptn.business.dialog.cespath.modal.CesPortInfo;

public class ChooseE1Controller {
	private final ChooseE1Dialog dialog;
	private final SiteInst siteInst;

	private Map<JCheckBox, PortInst> jcbAndE1PortSlotMap = null;
	private List<JCheckBox> jcbList= null;
	private List<JCheckBox> jcbListUpdate = null;
	private final AddCesDialog addCesDialog;

	public ChooseE1Controller(ChooseE1Dialog dialog, SiteInst siteInst, AddCesDialog addCesDialog) {
		this.addCesDialog = addCesDialog;
		this.dialog = dialog;
		this.siteInst = siteInst;
		addListeners();
	}
	/**
	 * 处理事件
	 */
	private void addListeners() {

		this.dialog.getTreebox().getSelectionModel().addDataBoxSelectionListener(new DataBoxSelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(DataBoxSelectionEvent e) {
				Element element = dialog.getTreebox().getLastSelectedElement();
				List<PortInst> portInstList = null;
				if (element instanceof Card) {
					try {
						portInstList = (List<PortInst>) element.getUserObject();
						initE1Port(portInstList);
					} catch (Exception exception) {
						exception.printStackTrace();
					} finally {

					}
				}
			}
		});

		this.dialog.getOkBtn().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<PortInst> portInstList = new ArrayList<PortInst>();
				
				if(dialog.getAddCesDialog().getCesInfo()!=null){
					for (JCheckBox jcb : jcbListUpdate) {
						if (jcb.isSelected()) {
							portInstList.add(jcbAndE1PortSlotMap.get(jcb));
						}
					}
				}else{
					if(jcbList != null){
						for (JCheckBox jcb : jcbList) {
							if (jcb.isSelected()) {
								portInstList.add(jcbAndE1PortSlotMap.get(jcb));
							}
						}
					}
				}

				List<CesPortInfo> cesPortInfoList = new ArrayList<CesPortInfo>();
				CesPortInfo cesPort = null;
				for (PortInst e1 : portInstList) {
					cesPort = new CesPortInfo();
					cesPort.setE1PortInst(e1);
					cesPortInfoList.add(cesPort);
				}
				// 拿出端口对应的网元
				int siteId = 0;
				if (!portInstList.isEmpty()) {
					siteId = portInstList.get(0).getSiteId();
				}
				
				if (dialog.getType().equalsIgnoreCase("A")) {
					if(addCesDialog.getSiteId_z() == siteId){
						addCesDialog.getPortTable_z().clear();
						addCesDialog.getSelpwVector().removeAllElements();
					}
					// 将选择的端口放到A端表中
					dialog.getAddCesDialog().loadPortTable_a(cesPortInfoList, siteId);
					
				} else if (dialog.getType().equalsIgnoreCase("Z")) {
					if(addCesDialog.getSiteId_a() == siteId){
						addCesDialog.getPortTable_a().clear();
						addCesDialog.getSelpwVector().removeAllElements();
					}
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
		 * @Override public JPopupMenu generate(TView tview, MouseEvent mouseEvent) { JPopupMenu menu = new JPopupMenu(); final Element portElement = tview.getDataBox() .getLastSelectedElement(); if (portElement instanceof Port) { final PortInst portInst = (PortInst) portElement .getUserObject(); JMenuItem jMenuItemSelect = new JMenuItem("选中"); jMenuItemSelect.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent arg0) { if(portInst.getIsEnabled_code() == 0) { DialogBoxUtil.errorDialog(dialog, TipMessageUtil.E1PORTISNOTENABLE); return; } doChoosePort(portInst, dialog); } }); menu.add(jMenuItemSelect); } return menu; } });
		 */
	}

	protected void initE1Port(List<PortInst> portInstList) {
		JCheckBox jcb = null;
		if (jcbAndE1PortSlotMap != null) {
			jcbAndE1PortSlotMap.clear();
		}
		if(dialog.getAddCesDialog().getCesInfo()!=null){
			if (jcbListUpdate != null) {
				jcbListUpdate.clear();
			}
			if (portInstList != null && !portInstList.isEmpty()) {
				ButtonGroup buttonGroup=new ButtonGroup();
				jcbAndE1PortSlotMap = new HashMap<JCheckBox, PortInst>();
				jcbListUpdate = new ArrayList<JCheckBox>();
				for (int i = 0; i < portInstList.size(); i++) {
					jcb = new JCheckBox(portInstList.get(i).getPortName());
					buttonGroup.add(jcb);
					jcbListUpdate.add(jcb);
					jcbAndE1PortSlotMap.put(jcb, portInstList.get(i));
				}
				// 把创建的E1放到对话框的面板里
				placeE1JCheck();
			}
		}else{
			if (jcbList != null) {
				jcbList.clear();
			}
			if (portInstList != null && !portInstList.isEmpty()) {
				jcbAndE1PortSlotMap = new HashMap<JCheckBox, PortInst>();
				jcbList = new ArrayList<JCheckBox>();
				for (int i = 0; i < portInstList.size(); i++) {
					jcb = new JCheckBox(portInstList.get(i).getPortName());
					jcbList.add(jcb);
					jcbAndE1PortSlotMap.put(jcb, portInstList.get(i));
				}
				// 把创建的E1放到对话框的面板里
				placeE1JCheck();
			}
		}
		
	}

	private void placeE1JCheck() {
		dialog.getJcheJPanel().removeAll();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 50, 50, 50 };
		layout.columnWeights = new double[] { 0, 0, 0, 0 };
		layout.rowHeights = new int[] { 30, 30, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0 };
		this.dialog.getJcheJPanel().setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 5, 5);
		int index = 0;
		int row = 0;
		int length=0;
		if(dialog.getAddCesDialog().getCesInfo()!=null){
			length = jcbListUpdate.size();
			
			if (length % 4 == 0) {
				row = length / 4;
			} else {
				row = length / 4 + 1;
			}
			for (int i = 0; i < row; i++) {
				for (int col = 0; col < 4; col++) {
					c.gridx = col;
					c.gridy = i;
					layout.setConstraints(jcbListUpdate.get(index), c);
					this.dialog.getJcheJPanel().add(jcbListUpdate.get(index));
					index++;
				}
			}
			setCheckBoxIsEnable();
		}else{
			length = jcbList.size();
			
			if (length % 4 == 0) {
				row = length / 4;
			} else {
				row = length / 4 + 1;
			}
			for (int i = 0; i < row; i++) {
				for (int col = 0; col < 4; col++) {
					c.gridx = col;
					c.gridy = i;
					layout.setConstraints(jcbList.get(index), c);
					this.dialog.getJcheJPanel().add(jcbList.get(index));
					index++;
				}
			}
			setCheckBoxIsEnable();
		}
		this.dialog.getCenterPanel().updateUI();
		this.dialog.getJcheJPanel().updateUI();

	}

	// 如果该e1被用了或者是未使能状态，不能选择
	private void setCheckBoxIsEnable() {
		PortInst e1Port = null;
		try {
			if(dialog.getAddCesDialog().getCesInfo()!=null){
				CesInfo ces = dialog.getAddCesDialog().getCesInfo();
				int e1PortId = 0;
				if(this.siteInst.getSite_Inst_Id() == ces.getaSiteId()){
					e1PortId = ces.getaAcId();
				}else{
					e1PortId = ces.getzAcId();
				}
				for (JCheckBox jcb : jcbListUpdate) {
					jcb.setEnabled(false);
					e1Port = jcbAndE1PortSlotMap.get(jcb);
					if (e1Port.getIsEnabled_code() == EManagerStatus.ENABLED.getValue() && e1Port.getIsOccupy()!=0) {
						if(e1Port.getPortId() == e1PortId){
							jcb.setEnabled(true);
							jcb.setSelected(true);
						}
					}
					if (e1Port.getIsEnabled_code() == EManagerStatus.ENABLED.getValue() && e1Port.getIsOccupy() == 0) {
						jcb.setEnabled(true);
					}
				}
			}else{
				for (JCheckBox jcb : jcbList) {
					jcb.setEnabled(false);
					e1Port = jcbAndE1PortSlotMap.get(jcb);
					if (e1Port.getIsEnabled_code() == EManagerStatus.ENABLED.getValue() && e1Port.getIsOccupy() == 0) {
						jcb.setEnabled(true);
					}
					
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			e1Port = null;
		}

	}

	/*
	 * private void doChoosePort(PortInst portInst, JDialog dialog) { try { if (dialog instanceof ChooseE1Dialog) { this.setPortInfo(this.siteInst, portInst, ((ChooseE1Dialog) dialog).getType()); } dialog.dispose();
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
	 * private void setPortInfo(SiteInst siteInst, PortInst portInst, String type) { Field field = null; FieldService fieldService = null; List<Field> fieldList = null; String text = null; try { field = new Field(); field.setId(siteInst.getFieldID()); fieldService = (FieldService) ConstantUtil.serviceFactory .newService(Services.Field); fieldList = fieldService.select(field); if (fieldList == null || fieldList.size() != 1) { throw new Exception("查询field出错"); } text = fieldList.get(0).getFieldName() + "/" + siteInst.getCellId() + "/" + portInst.getPortName(); if (type.equals("A")) { this.dialog.getAddCesDialog().setPortInst_a(portInst); this.dialog.getAddCesDialog().setPortStmTime_a(null); this.dialog.getAddCesDialog().setSiteId_a(portInst.getSiteId()); this.dialog.getAddCesDialog().setAText(text); } else if (type.equals("Z")) { this.dialog.getAddCesDialog().setPortInst_z(portInst); this.dialog.getAddCesDialog().setPortStmTime_z(null); this.dialog.getAddCesDialog().setSiteId_z(portInst.getSiteId()); this.dialog.getAddCesDialog().setZText(text); }
	 * 
	 * if (!this.dialog.getAddCesDialog().getTfAPort().getText() .equals("") && !this.dialog.getAddCesDialog().getTfZPort().getText() .equals("")) { // 加载可以的pw loadPW(); }
	 * 
	 * } catch (Exception e) { ExceptionManage.dispose(e,this.getClass()); } finally { field = null; fieldService = null; fieldList = null; text = null; } }
	 */

	private void loadPW() {
		List<PwInfo> filterPwInfoList = null;
		List<Integer> siteIdList = null;
		Map<Integer, PwInfo> pwIdAndPwInfoMap = null;
		ControlKeyValue kc = null;
		Code code = null;
		int pwId = 0;
		try {
			filterPwInfoList = new ArrayList<PwInfo>();
			siteIdList = new ArrayList<Integer>();
			siteIdList.add(this.dialog.getAddCesDialog().getSiteId_a());
			siteIdList.add(this.dialog.getAddCesDialog().getSiteId_z());
			pwIdAndPwInfoMap = getPwIdAndPwInfoMap(siteIdList);
			if(this.dialog.getAddCesDialog().getCesInfo() != null){
				pwId = this.dialog.getAddCesDialog().getCesInfo().getPwId();
			}
			for (PwInfo pw : pwIdAndPwInfoMap.values()) {
				if ((pw.getASiteId() == this.dialog.getAddCesDialog().getSiteId_a() && pw.getZSiteId() == this.dialog.getAddCesDialog().getSiteId_z()) || (pw.getZSiteId() == this.dialog.getAddCesDialog().getSiteId_a() && pw.getASiteId() == this.dialog.getAddCesDialog().getSiteId_z())) {
					if (pw.getRelatedServiceId() == 0) {
						kc = (ControlKeyValue) this.dialog.getAddCesDialog().getServiceTypeCbox().getSelectedItem();
						code = (Code) kc.getObject();
						if (code.getCodeValue().equals(pw.getType().getValue() + "")) {
							filterPwInfoList.add(pw);
						}
					}else{
						//修改ces业务时，当前的pw需要加载到可用列表中
						if(pw.getPwId() == pwId){
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
			code = null;
			siteIdList = null;
			kc = null;
		}
	}

	public void initData() {
		PortService_MB portService = null;
		PortInst portinst = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portinst = new PortInst();
			portinst.setPortType("e1");
			portinst.setSiteId(this.siteInst.getSite_Inst_Id());
			List<PortInst> portList = portService.select(portinst);
			initTreeData(portList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			portinst=null;
			UiUtil.closeService_MB(portService);
		}

	}

	private void initTreeData(List<PortInst> e1ortInst) {
		Map<Integer, List<PortInst>> cardIdAndPortListMap = new HashMap<Integer, List<PortInst>>();
		Set<Integer> cardIdSet = new HashSet<Integer>();
		List<PortInst> portInstList = null;
		
		for (PortInst portInst : e1ortInst) {
			cardIdSet.add(portInst.getCardId());
			if (cardIdAndPortListMap.get(portInst.getCardId()) == null) {
				portInstList = new ArrayList<PortInst>();
				cardIdAndPortListMap.put(portInst.getCardId(), portInstList);
			}
			portInstList = cardIdAndPortListMap.get(portInst.getCardId());
			portInstList.add(portInst);
			cardIdAndPortListMap.put(portInst.getCardId(), portInstList);
		}
		Node nodeParent = new Node();
		this.dialog.getTreebox().addElement(nodeParent);
		nodeParent.setName("NE:" + siteInst.getCellId());
		Card card = null;

		for (Integer cardId : cardIdSet) {
			card = new Card(cardId);
			card.setParent(nodeParent);
			card.setName("card:" + cardId);
			card.setUserObject(cardIdAndPortListMap.get(cardId));
			this.dialog.getTreebox().addElement(card);
		}
	}

	private Map<Integer, PwInfo> getPwIdAndPwInfoMap(List<Integer> siteIdList) {
		PwInfoService_MB pwInfoServiceMB = null;
		List<PwInfo> pwInfoList = null;
		Map<Integer, PwInfo> pwIdAndPwInfoMap = null;
		try {
			pwIdAndPwInfoMap = new HashMap<Integer, PwInfo>();
			pwInfoList = new ArrayList<PwInfo>();
			pwInfoServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for (Integer siteId : siteIdList) {
				List<PwInfo> pwList = pwInfoServiceMB.select(siteId);
				for (PwInfo pwInfo : pwList) {
					if(pwInfo.getPwStatus() == EActiveStatus.ACTIVITY.getValue()){
						pwInfoList.add(pwInfo);
					}
				}
			}

			for (PwInfo pwInfo : pwInfoList) {
				if (pwIdAndPwInfoMap.get(pwInfo.getPwId()) == null) {
					pwIdAndPwInfoMap.put(pwInfo.getPwId(), pwInfo);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(pwInfoServiceMB);
			pwInfoList = null;
		}
		return pwIdAndPwInfoMap;
	}

	/**
	 * 创建端口
	 * 
	 * @param portInst
	 * @param card
	 * @return
	 */
	/*
	 * private Port createPort(PortInst portInst, double x, double x2) { Port port = new Port(); port.setImage(portInst.getImagePath()); port.setLocation(x, x2); port.setUserObject(portInst); port.setDisplayName(portInst.getPortName()); port.setSelected(false); return port; }
	 */

}
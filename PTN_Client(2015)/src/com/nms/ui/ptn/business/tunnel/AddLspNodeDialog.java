package com.nms.ui.ptn.business.tunnel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.Businessid;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.dao.ptn.LabelInfoMapper;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.BusinessidService_MB;
import com.nms.model.ptn.LabelInfoService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
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
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
/**
 * 扩容
 * @author guoqc
 */
public class AddLspNodeDialog extends PtnDialog {
	private static final long serialVersionUID = 4967434076278415176L;
	private Tunnel tunnel;//需要扩容的tunnel
	private TopoPanel topoPanel;//拓扑图
	private JPanel leftPanel;
	private JSplitPane jSplitPanel;
	private JLabel addNodeLspLbl;//选择扩容路径
	private JComboBox addNodeLspJcb;
	private JLabel addNodeLbl;//选择扩容网元
	private JComboBox addNodeJcb;
	private JLabel aPortLbl;//和A端网元相连端口
	private JComboBox aPortJcb;
	private JLabel zPortLbl;//和Z端网元相连端口
	private JComboBox zPortJcb;
	private PtnButton saveBtn;
	private Map<Integer, List<PortInst>> siteIdPortListMap = new HashMap<Integer, List<PortInst>>();
	private Segment oldSegment;//需要拆分的段
//	private boolean backUpFlag = true;
	
	public AddLspNodeDialog(Tunnel tunnel) {
		this.setModal(true);
		this.tunnel = tunnel;
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_ADD_LSP_NODE));
		this.initComponent();
		this.setLayout();
		this.addListener();
		this.initData();
		UiUtil.showWindow(this, 850, 600);
	}

	private void initComponent() {
		this.topoPanel = new TopoPanel();
		this.leftPanel = new JPanel();
		this.jSplitPanel = new JSplitPane();
		this.addNodeLspLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_ADD_LSP));
		this.addNodeLspJcb = new JComboBox();
		this.addNodeLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_ADD_NODE));
		this.addNodeJcb = new JComboBox();
		this.aPortLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WITH)+ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_WITH_A));
		this.aPortJcb = new JComboBox();
		this.zPortLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WITH)+ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_WITH_Z));
		this.zPortJcb = new JComboBox();
		this.saveBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE), true, RootFactory.CORE_MANAGE, this);
	}

	private void setLayout() {
		this.add(this.jSplitPanel);
		this.jSplitPanel.setLeftComponent(this.leftPanel);
		this.jSplitPanel.setRightComponent(this.topoPanel);
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.rowHeights = new int[] {30, 30, 30, 30, 30, 30, 30, 30};
		componentLayout.columnWidths = new int[] { 50, 70, 80};
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0.1};
		this.leftPanel.setLayout(componentLayout);
	
		GridBagConstraints c = new GridBagConstraints();
		//第一行
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(50, 10, 15, 10);
		c.fill = GridBagConstraints.NONE;
		componentLayout.setConstraints(this.addNodeLspLbl, c);
		this.leftPanel.add(this.addNodeLspLbl);
		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.addNodeLspJcb, c);
		this.leftPanel.add(this.addNodeLspJcb);
		// 第二行
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.NONE;
		componentLayout.setConstraints(this.addNodeLbl, c);
		this.leftPanel.add(this.addNodeLbl);
		c.gridx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.addNodeJcb, c);
		this.leftPanel.add(this.addNodeJcb);
		//第三行
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.aPortLbl, c);
		this.leftPanel.add(this.aPortLbl);
		c.gridx = 2;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.aPortJcb, c);
		this.leftPanel.add(this.aPortJcb);
		//第四行
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.zPortLbl, c);
		this.leftPanel.add(this.zPortLbl);
		c.gridx = 2;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.zPortJcb, c);
		this.leftPanel.add(this.zPortJcb);
		//第六行
		c.gridx = 2;
		c.gridy = 5;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		componentLayout.setConstraints(this.saveBtn, c);
		this.leftPanel.add(this.saveBtn);
	}
	
	private void addListener() {
		this.saveBtn.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}

			@Override
			public boolean checking() {
				return true;
			}
		});			
		
		this.addNodeJcb.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					initPortJcb();
				}
			}
		});
		
		this.addNodeLspJcb.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				SiteService_MB siteService = null;
				try {
					siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					Lsp addNodeLsp = (Lsp) ((ControlKeyValue)addNodeLspJcb.getSelectedItem()).getObject();
					aPortLbl.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_WITH)+siteService.getSiteName(addNodeLsp.getASiteId())+
							ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_CONNECT_NODE));
					zPortLbl.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_WITH)+siteService.getSiteName(addNodeLsp.getZSiteId())+
							ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_CONNECT_NODE));
				} catch (Exception e1) {
					ExceptionManage.dispose(e1, this.getClass());
				} finally {
					UiUtil.closeService_MB(siteService);
				}
			}
		});
	}

	private void initData() {
		this.topoPanel.initData(this.tunnel);
		this.initAddNodeLsp();
		this.initAddNodeSite();
	}

	/**
	 * 初始化可扩容的路径
	 */
	private void initAddNodeLsp() {
		List<Lsp> mainLspList = this.tunnel.getLspParticularList();
		List<Lsp> reserveLspList = this.tunnel.getProtectTunnel().getLspParticularList();
		List<Lsp> usableLspList = new ArrayList<Lsp>();
		List<Integer> repeatedIdList = new ArrayList<Integer>();
		for (Lsp mainLsp : mainLspList) {
//			int aSiteId = mainLsp.getASiteId();
//			int zSiteId = mainLsp.getZSiteId();
//			for (Lsp reserveLsp : reserveLspList) {
//				if((reserveLsp.getASiteId() == aSiteId && reserveLsp.getZSiteId() == zSiteId) ||
//						reserveLsp.getASiteId() == zSiteId && reserveLsp.getZSiteId() == aSiteId){
//					repeatedIdList.add(mainLsp.getId());
//					repeatedIdList.add(reserveLsp.getId());
//					break;
//				}
//			}
			for (Lsp reserveLsp : reserveLspList) {
				if(reserveLsp.getSegmentId() == mainLsp.getSegmentId()){
					repeatedIdList.add(mainLsp.getId());
					repeatedIdList.add(reserveLsp.getId());
					break;
				}
			}
		}
		for (Lsp mainLsp : mainLspList) {
			if(!repeatedIdList.contains(mainLsp.getId())){
				usableLspList.add(mainLsp);
			}
		}
		for (Lsp reserveLsp : reserveLspList) {
			if(!repeatedIdList.contains(reserveLsp.getId())){
				usableLspList.add(reserveLsp);
			}
		}
		SegmentService_MB segmentService = null;
		try {
			segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			for (Lsp lsp : usableLspList) {
				try {
					ControlKeyValue ckv = new ControlKeyValue(lsp.getId()+"", this.getLspName(lsp, segmentService), lsp);
					this.addNodeLspJcb.addItem(ckv);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(segmentService);
		}
	}
	
	private String getLspName(Lsp lsp, SegmentService_MB segmentService) throws Exception {
		Segment s = new Segment();
		s.setId(lsp.getSegmentId());
		return segmentService.select(s).get(0).getNAME();
	}

	/**
	 * 初始化扩容的网元
	 */
	private void initAddNodeSite() {
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			List<SiteInst> siteList = siteService.select();
			if(siteList != null){
				for (SiteInst siteInst : siteList) {
					PortInst port = new PortInst();
					port.setSiteId(siteInst.getSite_Inst_Id());
					port.setPortType("NNI");
					List<PortInst> portList = portService.select(port);
					if(portList != null && portList.size() > 1){
						boolean flag = true;
						for (PortInst portInst : portList) {
							if(portInst.getIsOccupy() == 1){
								flag = false;
								break;
							}
						}
						if(flag){
							ControlKeyValue ckv = new ControlKeyValue(siteInst.getSite_Inst_Id()+"", siteInst.getCellId(), siteInst);
							this.siteIdPortListMap.put(siteInst.getSite_Inst_Id(), portList);
							this.addNodeJcb.addItem(ckv);
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
		}
	}
	
	private void initPortJcb() {
		this.aPortJcb.removeAllItems();
		this.zPortJcb.removeAllItems();
		int siteId = Integer.parseInt(((ControlKeyValue)addNodeJcb.getSelectedItem()).getId());
		List<PortInst> portList = siteIdPortListMap.get(siteId);
		if(portList != null){
			for (PortInst portInst : portList) {
				ControlKeyValue ckv = new ControlKeyValue(portInst.getPortId()+"", portInst.getPortName(), portInst);
				this.aPortJcb.addItem(ckv);
				this.zPortJcb.addItem(ckv);
			}
		}
	}
	
	private void saveAction(){
		Lsp addNodeLsp = (Lsp) ((ControlKeyValue)this.addNodeLspJcb.getSelectedItem()).getObject();
		if(this.addNodeJcb.getSelectedItem() == null){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SITE_IS_NOT_NULL));
			return;
		}
		SiteInst addNodeSite = (SiteInst) ((ControlKeyValue)this.addNodeJcb.getSelectedItem()).getObject();
		if(this.aPortJcb.getSelectedItem() == null){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORT_IS_NOT_NULL));
			return;
		}
		PortInst aPort = (PortInst) ((ControlKeyValue)this.aPortJcb.getSelectedItem()).getObject();
		if(this.zPortJcb.getSelectedItem() == null){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORT_IS_NOT_NULL));
			return;
		}
		PortInst zPort = (PortInst) ((ControlKeyValue)this.zPortJcb.getSelectedItem()).getObject();
		if(aPort.getPortId() == zPort.getPortId()){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_APORT_NOT_LIKE_ZPORT));
			return;
		}
		//操作之前提示用户手动备份数据
//		if(this.backUpFlag){
			int result = DialogBoxUtil.confirmDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_BACK_UP_DATA));
			if(result == JOptionPane.NO_OPTION){
				return;
			}
//			this.backUpFlag = false;
//			return;
//		}
		boolean flag = false;
		try {
			//删除之前的段，新建两条段
			flag = this.updateSegment(addNodeLsp, addNodeSite, aPort, zPort);
			if(flag){
				//重组lsp(除了正在修改的tunnel外，还包括其他使用相同段的tunnel)
				flag = this.updateLsp(addNodeLsp, addNodeSite, aPort, zPort);
				//		//修改环网保护(如果有)
				//		this.updateLoop(addNodeLsp);
				//修改之前段的OAM(如果有,解除serviceId绑定)
				this.releaseOAM(addNodeLsp);
				//lsp的业务id有变化，需要将pw重新下发
				this.udpatePw(addNodeLsp);
			}
			if(flag){
				DialogBoxUtil.succeedDialog(this, ResultString.CONFIG_SUCCESS);
			}else{
				DialogBoxUtil.errorDialog(this, ResultString.CONFIG_FAILED+","+ResourceUtil.srcStr(StringKeysTip.TIP_DATA_IS_ROLLBABK));
			}
		} catch (Exception e) {
			//提示用户手动恢复数据
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_ROLLBACK_FAIL_RECOVER_DATA));
		}
		//添加日志记录
		CommonBean log = new CommonBean();
		log.setAddLspName(((ControlKeyValue)this.addNodeLspJcb.getSelectedItem()).getName());
		log.setAddSiteName(addNodeSite.getCellId());
		log.setPortNameWithA(aPort.getPortName());
		log.setPortNameWithZ(zPort.getPortName());
		AddOperateLog.insertOperLog(saveBtn, EOperationLogType.ADDLSPNODE.getValue(), flag?ResultString.CONFIG_SUCCESS:ResultString.CONFIG_FAILED,
				null, log, addNodeSite.getSite_Inst_Id(), ResourceUtil.srcStr(StringKeysTitle.TIT_ADD_LSP_NODE), "addLspNode");
		this.dispose();
	}

	/**
	 * 删除之前的段，新建两条段
	 */
	private boolean updateSegment(Lsp addNodeLsp, SiteInst addNodeSite, PortInst aPort, PortInst zPort)throws Exception {
		SegmentService_MB service = null;
		PortService_MB portService = null;
		LspInfoService_MB lspService = null;
		try {
			lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			Segment condition = new Segment();
			condition.setId(addNodeLsp.getSegmentId());
			List<Segment> segmentList = service.queryByCondition(condition);
			if(segmentList != null && segmentList.size() == 1){
				this.oldSegment = segmentList.get(0);
				this.getSegmentQosQueue();
				DispatchUtil segmentDispatch = new DispatchUtil(RmiKeys.RMI_SEGMENT);
				String result = segmentDispatch.excuteDelete(segmentList);
				if((ResultString.CONFIG_SUCCESS).equals(result)){
					Segment segment = this.addSegment(addNodeLsp, aPort, portService, this.oldSegment.getAqosqueue(), 1);
					if(segment != null){
						result = segmentDispatch.excuteInsert(segment);
						if((ResultString.CONFIG_SUCCESS).equals(result)){
							segment = this.addSegment(addNodeLsp, zPort, portService, this.oldSegment.getZqosqueue(), 2);
							if(segment != null){
								result = segmentDispatch.excuteInsert(segment);
								if((ResultString.CONFIG_SUCCESS).equals(result)){
									return true;
								}
							}
						}
					}
				}
				//数据恢复
//				this.recoverSegment(segmentDispatch, service, addNodeLsp, aPort, zPort, lspService);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(lspService);
			UiUtil.closeService_MB(service);
			UiUtil.closeService_MB(portService);
		}
		return false;
	}
	
	private void getSegmentQosQueue() throws Exception {
		QosQueueService_MB qosService = null;
		try {
			qosService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			QosQueue qosQueue = new QosQueue();
			qosQueue.setSiteId(this.oldSegment.getASITEID());
			qosQueue.setObjType("SECTION");		
			qosQueue.setObjId(this.oldSegment.getAPORTID());
			List<QosQueue> qosQueueList = qosService.queryByCondition(qosQueue);
			this.oldSegment.setAqosqueue(qosQueueList);
			qosQueue.setSiteId(this.oldSegment.getZSITEID());
			qosQueue.setObjId(this.oldSegment.getZPORTID());
			qosQueueList = qosService.queryByCondition(qosQueue);
			this.oldSegment.setZqosqueue(qosQueueList);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(qosService);
		}
	}

	private void recoverSegment(DispatchUtil segmentDispatch, SegmentService_MB service, Lsp addNodeLsp, PortInst aPort, PortInst zPort, LspInfoService_MB lspService) throws RemoteException, Exception{
		//数据恢复
		this.oldSegment.setId(0);
		segmentDispatch.excuteInsert(this.oldSegment);
		List<Segment> segmentList = service.select_search(addNodeLsp.getAPortId(), aPort.getPortId());
		if(segmentList != null && !segmentList.isEmpty()){
			segmentDispatch.excuteDelete(segmentList);
		}
		segmentList = service.select_search(zPort.getPortId(), addNodeLsp.getZPortId());
		if(segmentList != null && !segmentList.isEmpty()){
			segmentDispatch.excuteDelete(segmentList);
		}
		segmentList = service.select_search(addNodeLsp.getAPortId(), addNodeLsp.getZPortId());
		if(segmentList != null && !segmentList.isEmpty()){
			int segmentId = segmentList.get(0).getId();
			List<Lsp> lspList = lspService.selectBySegmentId(addNodeLsp.getSegmentId());
			if(lspList != null){
				for (Lsp lsp : lspList) {
					lsp.setSegmentId(segmentId);
					lspService.saveOrUpdate(lsp);
				}
			}
		}
	}

	private Segment addSegment(Lsp addNodeLsp, PortInst aPort, PortService_MB portService, List<QosQueue> qosList, int type) throws Exception {
		try{
			Segment segment = new Segment();
			PortInst portInst = new PortInst();
			if(type == 1){
				portInst.setSiteId(addNodeLsp.getASiteId());
				portInst.setPortId(addNodeLsp.getAPortId());
				portInst = portService.selectPortybyid(addNodeLsp.getAPortId());
				segment.setNAME(""+new AutoNamingUtil().autoNaming(segment, portInst, aPort));
				segment.setASITEID(addNodeLsp.getASiteId());
				segment.setAPORTID(addNodeLsp.getAPortId());
				segment.setZSITEID(aPort.getSiteId());
				segment.setZPORTID(aPort.getPortId());
				segment.setaSlotNumber(portInst.getSlotNumber());
				segment.setzSlotNumber(aPort.getSlotNumber());
			}else{
				portInst.setSiteId(addNodeLsp.getZSiteId());
				portInst.setPortId(addNodeLsp.getZPortId());
				portInst = portService.selectPortybyid(addNodeLsp.getZPortId());
				segment.setNAME(""+new AutoNamingUtil().autoNaming(segment, aPort, portInst));
				segment.setASITEID(aPort.getSiteId());
				segment.setAPORTID(aPort.getPortId());
				segment.setZSITEID(addNodeLsp.getZSiteId());
				segment.setZPORTID(addNodeLsp.getZPortId());
				segment.setaSlotNumber(aPort.getSlotNumber());
				segment.setzSlotNumber(portInst.getSlotNumber());
			}
			List<QosQueue> qosQueue = new ArrayList<QosQueue>();
			qosQueue.addAll(qosList);
			for (int i = 0; i < qosQueue.size(); i++) {
				QosQueue qos = qosQueue.get(i);
				qos.setId(0);
				if(type == 1){
					qos.setSiteId(segment.getZSITEID());
					qos.setObjId(segment.getZPORTID());
				}else{
					qos.setSiteId(segment.getASITEID());
					qos.setObjId(segment.getAPORTID());
				}
			}	
			segment.getQosMap().put(1, qosQueue);
			segment.setSpeedSegment(aPort.getPortAttr().getWorkModel()+"");
			segment.setCREATUSER(ConstantUtil.user == null ? "":ConstantUtil.user.getUser_Name());
			return segment;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 重组lsp(除了正在修改的tunnel外，还包括其他使用相同段的tunnel)
	 * @throws Exception 
	 */
	private boolean updateLsp(Lsp addNodeLsp, SiteInst addNodeSite, PortInst aPort, PortInst zPort) throws Exception {
		TunnelService_MB tunnelServiceMB = null;
		LspInfoService_MB lspService = null;
		SegmentService_MB segmentService = null;
		LabelInfoService_MB labelService = null;
		BusinessidService_MB businessidServiceMB = null;
		SiteService_MB siteService = null;
		LabelInfoMapper labelMapper = null;
		Segment segmentA = null;
		Segment segmentZ = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			businessidServiceMB = (BusinessidService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BUSINESSID);
			labelService = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO);
			segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
			labelMapper = labelService.getSqlSession().getMapper(LabelInfoMapper.class);
			List<Segment> segmentList = segmentService.select_search(addNodeLsp.getAPortId(), aPort.getPortId());
			if(segmentList != null && !segmentList.isEmpty()){
				segmentA = segmentList.get(0);
			}
			segmentList = segmentService.select_search(zPort.getPortId(), addNodeLsp.getZPortId());
			if(segmentList != null && !segmentList.isEmpty()){
				segmentZ = segmentList.get(0);
			}
			List<Lsp> lspList = lspService.selectBySegmentId(addNodeLsp.getSegmentId());
			List<Lsp> rollBackLspList = lspService.selectBySegmentId(addNodeLsp.getSegmentId());//数据备份,需要恢复的数据
			List<Lsp> deleteLspList = new ArrayList<Lsp>();//需要删除的数据
			for (Lsp lsp : lspList) {
				int frontLabel = lsp.getFrontLabelValue();
				int backLabel = lsp.getBackLabelValue();
				int aSiteId = lsp.getASiteId();
				int zSiteId = lsp.getZSiteId();
				int zPortId = lsp.getZPortId();
				int aBusinessId = lsp.getAtunnelbusinessid();
				int zBusinessId = lsp.getZtunnelbusinessid();
				String zIp = lsp.getZoppositeId();
				//第一条
				lsp.setZSiteId(addNodeSite.getSite_Inst_Id());
				lsp.setFrontLabelValue(0);
				lsp.setBackLabelValue(0);
				lsp.setZoppositeId(addNodeSite.getCellDescribe());
				lsp.setAtunnelbusinessid(0);
				lsp.setZtunnelbusinessid(0);
				//第二条新建
				Lsp newLsp = new Lsp();
				newLsp.setTunnelId(lsp.getTunnelId());
				newLsp.setPathStatus(lsp.getPathStatus());
				newLsp.setASiteId(addNodeSite.getSite_Inst_Id());
				newLsp.setZSiteId(zSiteId);
				newLsp.setZPortId(zPortId);
				newLsp.setAoppositeId(addNodeSite.getCellDescribe());
				newLsp.setZoppositeId(zIp);
				newLsp.setSourceMac("00-00-00-00-00-00");
				newLsp.setTargetMac("00-00-00-00-00-00");
				if(lsp.getAPortId() == addNodeLsp.getAPortId()){
					newLsp.setAPortId(zPort.getPortId());
					lsp.setZPortId(aPort.getPortId());
					lsp.setSegmentId(segmentA.getId());
					newLsp.setSegmentId(segmentZ.getId());
				}else{
					newLsp.setAPortId(aPort.getPortId());
					lsp.setZPortId(zPort.getPortId());
					lsp.setSegmentId(segmentZ.getId());
					newLsp.setSegmentId(segmentA.getId());
				}
				//释放标签
				this.releaseLabel(frontLabel, backLabel, aSiteId, zSiteId, labelService);
				//重新分配标签
				lsp.setFrontLabelValue(0);
				lsp.setBackLabelValue(0);
				tunnelServiceMB.getLabel(lsp, 0, siteService, labelMapper, labelService);
				tunnelServiceMB.getLabel(newLsp, 0, siteService, labelMapper, labelService);
				//释放业务id
				this.releaseBusinessId(aBusinessId, zBusinessId, aSiteId, zSiteId, businessidServiceMB);
				//分配业务id
				Map<Integer, Integer> siteServiceMap = new HashMap<Integer, Integer>();
				this.getBusinessId(lsp, siteServiceMap, tunnelServiceMB, businessidServiceMB);
				newLsp.setAtunnelbusinessid(lsp.getZtunnelbusinessid());
				this.getBusinessId(newLsp, siteServiceMap, tunnelServiceMB, businessidServiceMB);
				lspService.saveOrUpdate(lsp);
				int id = lspService.saveOrUpdate(newLsp);
				newLsp.setId(id);
				deleteLspList.add(newLsp);//备份数据
			}
			Tunnel tunnel = tunnelServiceMB.selectId(addNodeLsp.getTunnelId());
			DispatchUtil tunnelDispatch = new DispatchUtil(RmiKeys.RMI_TUNNEL);
			String result = tunnelDispatch.excuteUpdate(tunnel);
			if((ResultString.CONFIG_SUCCESS).equals(result)){
				return true;
			}else{
				//数据恢复
				DispatchUtil segmentDispatch = new DispatchUtil(RmiKeys.RMI_SEGMENT);
				List<Segment> delteSegmentList = new ArrayList<Segment>();
				delteSegmentList.add(segmentA);
				delteSegmentList.add(segmentZ);
				segmentDispatch.excuteDelete(delteSegmentList);
				this.oldSegment.setId(0);
				segmentDispatch.excuteInsert(this.oldSegment);
				segmentList = segmentService.select_search(this.oldSegment.getAPORTID(), this.oldSegment.getZPORTID());
				int segmentId = segmentList.get(0).getId();
				for (int i = 0; i < rollBackLspList.size(); i++) {
					Lsp lsp = rollBackLspList.get(i);
					Lsp deleteLsp = deleteLspList.get(i);
					Lsp updateLsp = lspList.get(i);
					//释放标签
					this.releaseLabel(updateLsp.getFrontLabelValue(), updateLsp.getBackLabelValue(), updateLsp.getASiteId(),
							updateLsp.getZSiteId(), labelService);
					this.releaseLabel(deleteLsp.getFrontLabelValue(), deleteLsp.getBackLabelValue(), deleteLsp.getASiteId(),
							deleteLsp.getZSiteId(), labelService);
					//释放业务id
					this.releaseBusinessId(updateLsp.getAtunnelbusinessid(), updateLsp.getZtunnelbusinessid(), updateLsp.getASiteId(), 
							updateLsp.getZSiteId(), businessidServiceMB);
					this.releaseBusinessId(deleteLsp.getAtunnelbusinessid(), deleteLsp.getZtunnelbusinessid(), deleteLsp.getASiteId(), 
							deleteLsp.getZSiteId(), businessidServiceMB);
					lspService.delete(deleteLsp.getId());
					lsp.setSegmentId(segmentId);
					lspService.saveOrUpdate(lsp);
				}
				tunnel = tunnelServiceMB.selectId(addNodeLsp.getTunnelId());
				result = tunnelDispatch.excuteUpdate(tunnel);
				if(!(ResultString.CONFIG_SUCCESS).equals(result)){
					throw new Exception("tunnel rollback failed");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(businessidServiceMB);
			UiUtil.closeService_MB(labelService);
			UiUtil.closeService_MB(segmentService);
			UiUtil.closeService_MB(tunnelServiceMB);
			UiUtil.closeService_MB(lspService);
			UiUtil.closeService_MB(siteService);
		}
		return false;
	}
	
	private void getBusinessId(Lsp lsp, Map<Integer, Integer> siteServiceMap, TunnelService_MB tunnelServiceMB, BusinessidService_MB businessidServiceMB) throws Exception {
		tunnelServiceMB.setbusinessId(lsp, siteServiceMap, businessidServiceMB, "a");
		tunnelServiceMB.setbusinessId(lsp, siteServiceMap, businessidServiceMB, "z");
	}

	/**
	 * 释放标签
	 */
	private void releaseLabel(int frontLabel, int backLabel, int aSiteId, int zSiteId, LabelInfoService_MB labelService) throws Exception{
		labelService.updateBatch(frontLabel, zSiteId, 1, "TUNNEL");
		labelService.updateBatch(backLabel, aSiteId, 1, "TUNNEL");
	}
	
	/**
	 * 释放业务id
	 * @throws Exception 
	 */
	private void releaseBusinessId(int aBusinessId, int zBusinessId, int aSiteId, int zSiteId, BusinessidService_MB businessidServiceMB) throws Exception{
		Businessid businessId = new Businessid();
		businessId.setIdStatus(0);
		businessId.setIdValue(aBusinessId);
		businessId.setType("tunnel");
		businessId.setSiteId(aSiteId);
		businessidServiceMB.updateBusinessid(businessId);
		businessId.setIdValue(zBusinessId);
		businessId.setSiteId(zSiteId);
		businessidServiceMB.updateBusinessid(businessId);
	}

	/**
	 * 修改环网保护(如果有)
	 */
//	private void updateLoop(Lsp addNodeLsp) {
//		WrappingProtectService service = null;
//		try {
//			
//		} catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		} finally {
//			UiUtil.closeService(service);
//		}
//	}

	/**
	 * 修改之前段的OAM(如果有,解除serviceId绑定)
	 * @throws Exception 
	 */
	private void releaseOAM(Lsp addNodeLsp) throws Exception {
		OamInfoService_MB oamService = null;
		try {
			oamService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			List<OamInfo> oamInfoList = this.oldSegment.getOamList();
			if(oamInfoList.size() == 2){
				for (OamInfo oamInfo : oamInfoList) {
					OamMepInfo mep = oamInfo.getOamMep();
					oamService.deleteById(oamInfo);
					mep.setServiceId(0);
					mep.setId(0);
					oamService.saveOrUpdate(oamInfo);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamService);
		}
	}
	
	/**
	 * 需要将pw重新下发一遍
	 * @param addNodeLsp
	 * @throws Exception 
	 */
	private void udpatePw(Lsp addNodeLsp) throws Exception {
		PwInfoService_MB service = null;
		try {
			service = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			List<PwInfo> pwList = service.selectByTunnelId(addNodeLsp.getTunnelId());
			if(pwList.size() > 0){
				DispatchUtil dispatch = new DispatchUtil(RmiKeys.RMI_PW);
				dispatch.excuteUpdate(pwList.get(0));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
}

package com.nms.ui.ptn.business.tunnel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
 * 缩容
 * @author guoqc
 */
public class DeleteLspNodeDialog extends PtnDialog {
	private static final long serialVersionUID = 4383901509678399606L;

	private Tunnel tunnel;//需要缩容的tunnel
	private TopoPanel topoPanel;//拓扑图
	private JPanel leftPanel;
	private JSplitPane jSplitPanel;
	private JLabel deleteNodeLbl;//选择缩容网元
	private JComboBox deleteNodeJcb;
	private JLabel aPortLbl;//A端网元相连端口
	private JComboBox aPortJcb;
	private JLabel zPortLbl;//Z端网元相连端口
	private JComboBox zPortJcb;
	private PtnButton saveBtn;
	private List<Integer> segmentIdList = new ArrayList<Integer>();
	private int tunnelId = 0;
//	private boolean backUpFlag = true;
	
	public DeleteLspNodeDialog(Tunnel tunnel) {
		this.setModal(true);
		this.tunnel = tunnel;
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_DELETE_LSP_NODE));
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
		this.deleteNodeLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_DELETE_NODE));
		this.deleteNodeJcb = new JComboBox();
		this.aPortLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_WITH_A));
		this.aPortJcb = new JComboBox();
		this.zPortLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_WITH_Z));
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
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.NONE;
		componentLayout.setConstraints(this.deleteNodeLbl, c);
		this.leftPanel.add(this.deleteNodeLbl);
		c.gridx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 15, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		componentLayout.setConstraints(this.deleteNodeJcb, c);
		this.leftPanel.add(this.deleteNodeJcb);
		//第二行
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
		//第三行
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
		//第五行
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
		
		this.deleteNodeJcb.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					initPortJcb();
				}
			}
		});
	}

	private void initData() {
		this.topoPanel.initData(this.tunnel);
		this.initDeleteNodeSite();
	}

	/**
	 * 初始化缩容的网元
	 */
	private void initDeleteNodeSite() {
		SiteService_MB siteService = null;
		TunnelService_MB tunnelServiceMB = null;
		SegmentService_MB segmentService = null;
		try {
			int aSiteId = this.tunnel.getaSiteId();
			int zSiteId = this.tunnel.getzSiteId();
			List<Lsp> mainLspList = this.tunnel.getLspParticularList();
			List<Lsp> reserveLspList = this.tunnel.getProtectTunnel().getLspParticularList();
			List<Integer> repeatedIdList = new ArrayList<Integer>();
			this.filterDeleteSiteId(mainLspList, aSiteId, zSiteId, repeatedIdList);
			this.filterDeleteSiteId(reserveLspList, aSiteId, zSiteId, repeatedIdList);
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			for (Integer siteId : repeatedIdList) {
				List<Tunnel> tunnelList = tunnelServiceMB.queryTunnelBySiteId(siteId);
				if(tunnelList == null || tunnelList.isEmpty()){
					List<Segment> segmentList = segmentService.selectBySite(siteId);
					if(segmentList != null && segmentList.size() == 2){
						SiteInst siteInst = siteService.select(siteId);
						if(siteInst.getSite_Inst_Id() > 0 && siteInst.getIsGateway() == 0){
							ControlKeyValue ckv = new ControlKeyValue(siteInst.getSite_Inst_Id()+"", siteInst.getCellId(), siteInst);
							this.deleteNodeJcb.addItem(ckv);
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(segmentService);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(tunnelServiceMB);
		}
	}
	
	private void filterDeleteSiteId(List<Lsp> lspList, int aSiteId, int zSiteId, List<Integer> repeatedIdList) {
		for (Lsp lsp : lspList) {
			if(lsp.getASiteId() != aSiteId && lsp.getASiteId() != zSiteId){
				if(!repeatedIdList.contains(lsp.getASiteId())){
					repeatedIdList.add(lsp.getASiteId());
				}
			}
			if(lsp.getZSiteId() != aSiteId && lsp.getZSiteId() != zSiteId){
				if(!repeatedIdList.contains(lsp.getZSiteId())){
					repeatedIdList.add(lsp.getZSiteId());
				}
			}
		}
	}

	/**
	 * 初始化端口列表
	 */
	private void initPortJcb() {
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			int siteId = Integer.parseInt(((ControlKeyValue)deleteNodeJcb.getSelectedItem()).getId());
			List<Lsp> mainLspList = this.tunnel.getLspParticularList();
			Lsp lsp = new Lsp();
			boolean flag = this.initPortText(mainLspList, siteId, siteService, lsp);
			if(flag){
				List<Lsp> reserveLspList = this.tunnel.getProtectTunnel().getLspParticularList();
				this.initPortText(reserveLspList, siteId, siteService, lsp);
			}
			this.aPortJcb.removeAllItems();
			this.zPortJcb.removeAllItems();
			List<PortInst> portList = portService.selectNniPortname(lsp.getASiteId());
			this.initPortData(portList, lsp.getAPortId(), aPortJcb);
			portList = portService.selectNniPortname(lsp.getZSiteId());
			this.initPortData(portList, lsp.getZPortId(), zPortJcb);
		} catch (Exception e1) {
			ExceptionManage.dispose(e1, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
		}
	}
	
	private boolean initPortText(List<Lsp> lspList, int siteId, SiteService_MB siteService, Lsp lspInfo) throws Exception {
		boolean flag = true;
		int aSiteId = 0;
		int zSiteId = 0;
		int aPortId = 0;
		int zPortId = 0;
		for (Lsp lsp : lspList) {
			if(lsp.getASiteId() == siteId){
				zPortLbl.setText(siteService.getSiteName(lsp.getZSiteId())+
				ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_CONNECT_NODE));
				zSiteId = lsp.getZSiteId();
				zPortId = lsp.getZPortId();
				flag = false;
			}
			if(lsp.getZSiteId() == siteId){
				aPortLbl.setText(siteService.getSiteName(lsp.getASiteId())+
				ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_CONNECT_NODE));
				aSiteId = lsp.getASiteId();
				aPortId = lsp.getAPortId();
			}
			if(aSiteId != 0 && zSiteId != 0){
				lspInfo.setASiteId(aSiteId);
				lspInfo.setAPortId(aPortId);
				lspInfo.setZSiteId(zSiteId);
				lspInfo.setZPortId(zPortId);
				break;
			}
		}
		return flag;
	}

	private void initPortData(List<PortInst> portList, int portId, JComboBox portJcb) {
		if(portList != null && !portList.isEmpty()){
			for (PortInst portInst : portList) {
//				if(portInst.getPortId() != portId && portInst.getIsOccupy() == 0){
//					ControlKeyValue ckv = new ControlKeyValue(portInst.getPortId()+"", portInst.getPortName(), portInst);
//					portJcb.addItem(ckv);
//				}
				if(portInst.getPortId() == portId){
					ControlKeyValue ckv = new ControlKeyValue(portInst.getPortId()+"", portInst.getPortName(), portInst);
					portJcb.addItem(ckv);
				}
			}
		}
	}

	private void saveAction(){
		if(this.deleteNodeJcb.getSelectedItem() == null){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SITE_IS_NOT_NULL));
			return;
		}
		SiteInst deleteNodeSite = (SiteInst) ((ControlKeyValue)this.deleteNodeJcb.getSelectedItem()).getObject();
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
		//操作之前提示用户手动备份数据
//		if(this.backUpFlag){
//			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_BACK_UP_DATA));
//			this.backUpFlag = false;
//			return;
//		}
		int result = DialogBoxUtil.confirmDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_BACK_UP_DATA));
		if(result == JOptionPane.NO_OPTION){
			return;
		}
		boolean flag = false;
		try {
			//将需要删除的网元所在的两条lsp合并为一条(除了正在修改的tunnel外，还包括其他相同网元的tunnel)
			flag = this.updateLsp(deleteNodeSite, aPort, zPort);
			if(flag){
				//将需要删除的网元所在的两条段合并为一条
				flag = this.updateSegment(aPort, zPort);
				//lsp的业务id有变化，需要将pw重新下发
				this.udpatePw();
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
		log.setAddSiteName(deleteNodeSite.getCellId());
		log.setPortNameWithA(aPort.getPortName());
		log.setPortNameWithZ(zPort.getPortName());
		AddOperateLog.insertOperLog(saveBtn, EOperationLogType.DELETELSPNODE.getValue(), flag?ResultString.CONFIG_SUCCESS:ResultString.CONFIG_FAILED,
				null, log, deleteNodeSite.getSite_Inst_Id(), ResourceUtil.srcStr(StringKeysTitle.TIT_DELETE_LSP_NODE), "deleteLspNode");
		this.dispose();
	}

	/**
	 * 将需要删除的网元所在的两条lsp合并为一条(除了正在修改的tunnel外，还包括其他相同网元的tunnel)
	 */
	private boolean updateLsp(SiteInst deleteNodeSite, PortInst aPort, PortInst zPort)throws Exception {
		LspInfoService_MB lspService = null;
		LabelInfoService_MB labelService = null;
		BusinessidService_MB businessidServiceMB = null;
		TunnelService_MB tunnelServiceMB = null;
		OamInfoService_MB oamInfoService = null;
		SiteService_MB siteService = null;
		LabelInfoMapper labelMapper = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			businessidServiceMB = (BusinessidService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BUSINESSID);
			lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
			labelService = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO);
			labelMapper = labelService.getSqlSession().getMapper(LabelInfoMapper.class);
			int deleteId = deleteNodeSite.getSite_Inst_Id();
			int aPortId = aPort.getPortId();
			int zPortId = zPort.getPortId();
			List<Lsp> lspList = lspService.selectBySiteId(deleteId);
			List<Lsp> rollBackLspList = lspService.selectBySiteId(deleteId);//需要恢复的数据
			List<Lsp> deleteLspList = new ArrayList<Lsp>();//需要删除的数据
			List<Integer> tunnelIdList = new ArrayList<Integer>();
			for (Lsp lspLeft : lspList) {
				if(lspLeft.getZSiteId() == deleteId){
					for (Lsp lspRight : lspList) {
						if(lspRight.getTunnelId() == lspLeft.getTunnelId()){
							if(lspRight.getASiteId() == deleteId){
								//释放标签，释放中间网元业务id
								this.releaseLabel(lspLeft.getFrontLabelValue(), lspLeft.getBackLabelValue(), lspLeft.getASiteId(),
										lspLeft.getZSiteId(), labelService);
								this.releaseLabel(lspRight.getFrontLabelValue(), lspRight.getBackLabelValue(), lspRight.getASiteId(),
										lspRight.getZSiteId(), labelService);
								this.releaseBusinessId(lspLeft.getZtunnelbusinessid(), deleteId, businessidServiceMB);
								lspLeft.setZSiteId(lspRight.getZSiteId());
								if(lspLeft.getTunnelId() == this.tunnel.getTunnelId()){
									lspLeft.setAPortId(aPortId);
									lspLeft.setZPortId(zPortId);
								}else{
									lspLeft.setZPortId(lspRight.getZPortId());
								}
								lspLeft.setZtunnelbusinessid(lspRight.getZtunnelbusinessid());
								lspLeft.setZoppositeId(lspRight.getZoppositeId());
								//重新分配标签
								lspLeft.setFrontLabelValue(0);
								lspLeft.setBackLabelValue(0);
								tunnelServiceMB.getLabel(lspLeft, 0, siteService, labelMapper, labelService);
								lspService.saveOrUpdate(lspLeft);
								lspService.delete(lspRight.getId());
								tunnelIdList.add(lspLeft.getTunnelId());
								deleteLspList.add(lspLeft);
								if(!this.segmentIdList.contains(lspLeft.getSegmentId())){
									this.segmentIdList.add(lspLeft.getSegmentId());
								}
								if(!this.segmentIdList.contains(lspRight.getSegmentId())){
									this.segmentIdList.add(lspRight.getSegmentId());
								}
								break;
							}
						}
					}
				}
			}
			//先删除被删网元的OAM
			DispatchUtil tunnelDispatch = new DispatchUtil(RmiKeys.RMI_TUNNEL);
			boolean flag = false;
			this.tunnelId = tunnelIdList.get(0);
			for (Integer tunnelId : tunnelIdList) {
				Tunnel tunnel = tunnelServiceMB.selectId(tunnelId);
				List<OamInfo> mipList = tunnel.getOamList();
				if(mipList != null && !mipList.isEmpty()){
					for (OamInfo oamInfo : mipList) {
						if(oamInfo.getOamMip() != null && "TUNNEL".equals(oamInfo.getOamMip().getObjType())){
							if(oamInfo.getOamMip().getSiteId() == deleteId){
								oamInfoService.deleteById(oamInfo);
							}
						}
					}
				}
				String result = tunnelDispatch.excuteUpdate(tunnel);
				if(!ResultString.CONFIG_SUCCESS.equals(result)){
					flag = true;
					break;
				}
			}
			if(flag){
				//数据回滚
				for (Lsp lsp : deleteLspList) {
					this.releaseLabel(lsp.getFrontLabelValue(), lsp.getBackLabelValue(), lsp.getASiteId(),
							lsp.getZSiteId(), labelService);
				}
				Map<Integer, Integer> siteServiceMap = new HashMap<Integer, Integer>();
				for (Lsp lspRight : rollBackLspList) {
					if(lspRight.getASiteId() == deleteId){
						lspRight.setId(0);
						tunnelServiceMB.setbusinessId(lspRight, siteServiceMap, businessidServiceMB, "a");
						for (Lsp lspLeft : rollBackLspList) {
							if(lspRight.getTunnelId() == lspLeft.getTunnelId()){
								if(lspLeft.getZSiteId() == deleteId){
									lspLeft.setZtunnelbusinessid(lspRight.getAtunnelbusinessid());
									lspService.saveOrUpdate(lspRight);
									lspService.saveOrUpdate(lspLeft);
									break;
								}
							}
						}
					}
				}
				for (Integer tunnelId : tunnelIdList) {
					Tunnel tunnel = tunnelServiceMB.selectId(tunnelId);
					String result = tunnelDispatch.excuteUpdate(tunnel);
					if(!ResultString.CONFIG_SUCCESS.equals(result)){
						throw new Exception("tunnel rollback failed");
					}
				}
			}else{
				return true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			UiUtil.closeService_MB(lspService);
			UiUtil.closeService_MB(labelService);
			UiUtil.closeService_MB(businessidServiceMB);
			UiUtil.closeService_MB(tunnelServiceMB);
			UiUtil.closeService_MB(siteService);
		}
		return false;
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
	private void releaseBusinessId(int businessId, int siteId, BusinessidService_MB businessidServiceMB) throws Exception{
		Businessid business = new Businessid();
		business.setIdStatus(0);
		business.setIdValue(businessId);
		business.setType("tunnel");
		business.setSiteId(siteId);
		businessidServiceMB.updateBusinessid(business);
	}

	private boolean updateSegment(PortInst aPort, PortInst zPort) throws Exception {
		SegmentService_MB segmentService = null;
		try {
			segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			Segment segment = new Segment();
			segment.setId(this.segmentIdList.get(0));
			List<Segment> segmentList = segmentService.queryByCondition(segment);
			Segment segmentA = segmentList.get(0);
			int aPortId = segmentA.getAPORTID();
			int zPortId = segmentA.getZPORTID();
			segment.setId(this.segmentIdList.get(1));
			segmentList = segmentService.queryByCondition(segment);
			Segment segmentZ = segmentList.get(0);
			//将两条段合并成一条段
			this.getSegmentQosQueue(segmentA);
			this.getSegmentQosQueue(segmentZ);
			if(segmentA.getZSITEID() == segmentZ.getZSITEID()){
				segmentA.setZSITEID(segmentZ.getASITEID());
				segmentA.setZqosqueue(segmentZ.getAqosqueue());
			}else if(segmentA.getASITEID() == segmentZ.getZSITEID()){
				segmentA.setASITEID(segmentZ.getASITEID());
				segmentA.setAqosqueue(segmentZ.getAqosqueue());
			}else if(segmentA.getZSITEID() == segmentZ.getASITEID()){
				segmentA.setZSITEID(segmentZ.getZSITEID());
				segmentA.setZqosqueue(segmentZ.getZqosqueue());
			}else if(segmentA.getASITEID() == segmentZ.getASITEID()){
				segmentA.setASITEID(segmentZ.getZSITEID());
				segmentA.setAqosqueue(segmentZ.getZqosqueue());
			}
			if(aPort.getSiteId() == segmentA.getASITEID()){
				segmentA.setAPORTID(aPort.getPortId());
				segmentA.setZPORTID(zPort.getPortId());
				segmentA.setaSlotNumber(aPort.getSlotNumber());
				segmentA.setzSlotNumber(zPort.getSlotNumber());
			}else if(zPort.getSiteId() == segmentA.getASITEID()){
				segmentA.setAPORTID(zPort.getPortId());
				segmentA.setZPORTID(aPort.getPortId());
				segmentA.setaSlotNumber(zPort.getSlotNumber());
				segmentA.setzSlotNumber(aPort.getSlotNumber());
			}
			for (QosQueue qos : segmentA.getAqosqueue()) {
				qos.setObjId(segmentA.getAPORTID());
			}
			for (QosQueue qos : segmentA.getZqosqueue()) {
				qos.setObjId(segmentA.getZPORTID());
			}
			segmentA.getAqosqueue().addAll(segmentA.getZqosqueue());
			segmentService.delete(segmentZ.getId());
			segment.setAPORTID(aPortId);
			segment.setZPORTID(zPortId);
			segmentService.updatePortOccupy(segment, 0);
			segmentService.updatePortOccupy(segmentA, 1);
			segmentService.saveOrUpdate(segmentA, segmentA.getAqosqueue(), null);
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			 UiUtil.closeService_MB(segmentService);
		}
	}
	
	private void getSegmentQosQueue(Segment segment) throws Exception {
		QosQueueService_MB qosService = null;
		try {
			qosService = (QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			QosQueue qosQueue = new QosQueue();
			qosQueue.setSiteId(segment.getASITEID());
			qosQueue.setObjType("SECTION");		
			qosQueue.setObjId(segment.getAPORTID());
			List<QosQueue> qosQueueList = qosService.queryByCondition(qosQueue);
			segment.setAqosqueue(qosQueueList);
			qosQueue.setSiteId(segment.getZSITEID());
			qosQueue.setObjId(segment.getZPORTID());
			qosQueueList = qosService.queryByCondition(qosQueue);
			segment.setZqosqueue(qosQueueList);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(qosService);
		}
	}
	
	/**
	 * 需要将pw重新下发一遍
	 * @param addNodeLsp
	 * @throws Exception 
	 */
	private void udpatePw() throws Exception {
		PwInfoService_MB service = null;
		try {
			service = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			List<PwInfo> pwList = service.selectByTunnelId(this.tunnelId);
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

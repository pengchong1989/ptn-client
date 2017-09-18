package com.nms.ui.ptn.alarm.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import twaver.DataBoxSelectionAdapter;
import twaver.DataBoxSelectionEvent;
import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.tree.TTree;

import com.nms.db.bean.alarm.AlarmShieldInfo;
import com.nms.db.bean.alarm.AlarmShieldInfo_t;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.ptn.alarm.model.AlarmLineInfo;
/**
 *告警线路面板图 
 * @author Administrator
 *
 */
public class AlarmLineJpanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TDataBox tDataBox = null; // 数据对象
	private TTree tree = null; // 线路对象
	private TDataBox alarmDataBox = null;
	private List<AlarmLineInfo> portList = null;//端口告警码
	private List<AlarmLineInfo> ringList = null;//端口告警码
	private List<AlarmLineInfo> TMCList = null;//TMC告警码
	private List<AlarmLineInfo> tmpList = null;//TMP告警码
	private List<AlarmLineInfo> vpwsList = null;//vpws告警码
	private List<AlarmLineInfo> e1List = null;//e1告警码
	private List<AlarmLineInfo> List1713 = null;//vpws告警码
	private List<AlarmLineInfo> ptpList = null;
	private List<AlarmLineInfo> tMSList = null;//tms告警码
	private List<AlarmLineInfo> fanList = null;//风扇告警码 
	private List<AlarmLineInfo> equipmentList = null;//设备温度
	private List<AlarmLineInfo> equipmentUPList = null;//设备上电
	private List<AlarmLineInfo> equipmentDropList = null;//设备下电
	List<AlarmLineInfo> powerList = null;
	private TTree alarmTree;//告警码 对象
	private int label =0;//用于标记 是0 :“告警码来确定线路” 还是 1:"线路“来确定”告警码“
	
	private NeTreePanel neTreePanel = null;

	/**
	 * 创建一个新的实例
	 * 
	 */
	public AlarmLineJpanel(int label) {
		try {
			this.label = label;
			this.initComponent();
			this.setLayout();
			this.initData(tDataBox);
			this.addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	public AlarmLineJpanel(int label,NeTreePanel netreePanel) {
		try {
			this.label = label;
			this.neTreePanel = netreePanel;
			this.initComponent();
			this.setLayout();
//			this.initData(tDataBox);
			this.addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void addListener() {
		
		tree.getDataBox().getSelectionModel().addDataBoxSelectionListener(new DataBoxSelectionAdapter(){
			public void selectionChanged(DataBoxSelectionEvent e) {
				if(label ==1 ){
					analysisLineListend();
				}else if(label == 2){
					try {
						if(null != neTreePanel && neTreePanel.getSelectSiteInst().size() >= 0){
							analysisAlarmCodeListend(neTreePanel.getSelectSiteInst());
						}
						else{
							analysisAlarmCodeListend();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}	
				}				
			}
			
		});
		
	}
	/**
	 * 初始化对象
	 */
	public void initComponent() {
		alarmCode();
		this.tDataBox = new TDataBox(ResourceUtil.srcStr(StringKeysObj.ALARM_LINE));
		this.tree = new TTree(tDataBox);
		this.tree.setTTreeSelectionMode(TTree.CHECK_DESCENDANT_ANCESTOR_SELECTION);
		
		this.alarmDataBox = new TDataBox(ResourceUtil.srcStr(StringKeysObj.ALARM_ALARMCODE));
		this.alarmTree = new TTree(alarmDataBox);
		this.alarmTree.setTTreeSelectionMode(TTree.CHECK_DESCENDANT_ANCESTOR_SELECTION);
		
	}
	/**
	 * 布局
	 */
	private void setLayout() {
		JScrollPane treeJscollPane = new JScrollPane(this.tree);
		JScrollPane alarmCodeJscollPane = new JScrollPane(this.alarmTree);
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 40, 40, 40, 40,40,80 };
		buttonLayout.columnWeights = new double[] { 0, 0, 0, 0, 0,0.4,0 };
		buttonLayout.rowHeights = new int[] { 40 };
		buttonLayout.rowWeights = new double[] { 0 };
		GridBagLayout contentLayout = new GridBagLayout();
		this.setLayout(contentLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(treeJscollPane, c);
		this.add(treeJscollPane);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.4;
		contentLayout.setConstraints(alarmCodeJscollPane, c);
		this.add(alarmCodeJscollPane);
	}


	/**
	 * 加载数据
	 * 
	 * @throws Exception
	 */
	private void initData(TDataBox tDataBox2) throws Exception {
		PortService_MB portService = null;
		TunnelService_MB tunnelServiceMB = null;
		PwInfoService_MB pwService = null;
		PortInst portInst = null;
		List<PortInst> allportInstList = null;
		List<PortInst> allportInstList1 = null;
		List<PortInst> e1PortInstList1 = null;
		List<Tunnel> lspInfoList = null;
		List<Tunnel> tunnelInfoList = null;
		List<PwInfo> pwInfoList = null;
		List<ElineInfo> elineInfoList = null;
		List<CesInfo> cesInfoList = null;
		ElineInfoService_MB elineService = null;
		CesInfoService_MB cesService = null;
		ElanInfoService_MB elanService = null;
		List<ElanInfo> elanInfoList = null;
		Map<Integer, List<ElanInfo>> elanMap = null;
		EtreeInfoService_MB etreeService = null;
		List<EtreeInfo> etreeInfoList = null;
		Map<Integer, List<EtreeInfo>> etreeMap = null;
		int siteId = 0;
		if(neTreePanel != null && neTreePanel.getSelectSiteInst().size() >= 0){
			List<SiteInst> siteInsts = neTreePanel.getSelectSiteInst();
			try {
				addNodeLine(getAlarmLine("1.0V",1,"power"),tDataBox2);
				addNodeLine(getAlarmLine("1.2V",2,"power"),tDataBox2);
				addNodeLine(getAlarmLine("2.5V",3,"power"),tDataBox2);
				addNodeLine(getAlarmLine("3.3V",4,"power"),tDataBox2);
				addNodeLine(getAlarmLine("12.0V",5,"power"),tDataBox2);
				addNodeLine(getAlarmLine("设备温度",8,"equipment"),tDataBox2);
				addNodeLine(getAlarmLine("远端设备上电",9,"up"),tDataBox2);
				addNodeLine(getAlarmLine("远端设备掉电",10,"drop"),tDataBox2);
				createParentNode(getLine("风扇-",16,3,"fan"),"风扇-1-风扇-3",tDataBox2);
					
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
				cesService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
				elanService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				//端口
				Node portNode = new Node();
				portNode.setName("端口线路");
				tDataBox2.addElement(portNode);
				//E1
				Node e1Node = new Node();
				e1Node.setName("E1支路通道");
				tDataBox2.addElement(e1Node);
				//LSP1:1
				Node lspNode = new Node();
				lspNode.setName("LSP1:1_1");
				tDataBox2.addElement(lspNode);
				//TMP
				Node tmpNode = new Node();
				tmpNode.setName("TMP通路");
				tDataBox2.addElement(tmpNode);
				//TMC
				Node tmcNode = new Node();
				tmcNode.setName("TMC通路");
				tDataBox2.addElement(tmcNode);
				//VPWS
				Node vpwsNode = new Node();
				vpwsNode.setName("TVPWS");
				tDataBox2.addElement(vpwsNode);
				//vpls
				Node vplsNode = new Node();
				vplsNode.setName("TVPLS");
				tDataBox2.addElement(vplsNode);
				
				if(null != siteInsts && siteInsts.size() > 0){
				for(SiteInst siteInst : siteInsts){
					portInst = new PortInst();
					portInst.setSiteId(siteInst.getSite_Inst_Id());
					allportInstList=portService.select(portInst);
					allportInstList1 = new ArrayList<PortInst>();
					e1PortInstList1 = new ArrayList<PortInst>();
					for(int i=0;i<allportInstList.size();i++){
						if(allportInstList.get(i).getPortType().equals("NNI")||allportInstList.get(i).getPortType().equals("UNI")
							||allportInstList.get(i).getPortType().equals("NONE")){
							allportInstList1.add(allportInstList.get(i));
							
						}
						if(allportInstList.get(i).getPortType().equals("e1")){
							e1PortInstList1.add(allportInstList.get(i));
								
							}
					}	
					//端口
					Node PortSiteNode = setParentNode(tDataBox2, siteInst.getCellId(), portNode);
					createParentNode(getLines(allportInstList1,512,"port"),PortSiteNode,tDataBox2);
					//E1
					Node e1SiteNode = setParentNode(tDataBox2, siteInst.getCellId(), e1Node);
					createParentNode(getLines(e1PortInstList1,32768,"E1"),e1SiteNode,tDataBox2);
					
					//LSP1:1
					Node lspSiteNode = setParentNode(tDataBox2, siteInst.getCellId(), lspNode);			
					lspInfoList = tunnelServiceMB.queryTunnelBySiteId(siteInst.getSite_Inst_Id());	
					createParentNode(getTunnelLines(lspInfoList,8192,"wrappingLsp",siteInst.getSite_Inst_Id()),lspSiteNode,tDataBox2);
					
					//TMP
					Node tmpSiteNode = setParentNode(tDataBox2, siteInst.getCellId(), tmpNode);	
					tunnelInfoList=tunnelServiceMB.selectNodesBySiteId(siteInst.getSite_Inst_Id());
					createParentNode(getTunnelLines(tunnelInfoList,12288,"tmp",siteInst.getSite_Inst_Id()),tmpSiteNode,tDataBox2);
					
					 //TMC
					Node tmcSiteNode = setParentNode(tDataBox2, siteInst.getCellId(), tmcNode);	
					pwInfoList = pwService.selectNodeBySiteid(siteInst.getSite_Inst_Id());		
					createParentNode(getPwLine(pwInfoList,16384,"tmc",siteInst.getSite_Inst_Id()),tmcSiteNode,tDataBox2);
					
					//VPWS
					elineInfoList = elineService.selectNodeBySite(siteInst.getSite_Inst_Id());
					cesInfoList = cesService.selectNodeBySite(siteInst.getSite_Inst_Id());
					if(cesInfoList !=null && cesInfoList.size()!=0){
						for(int i=0;i<cesInfoList.size();i++){
							ElineInfo eline =new ElineInfo();
							eline.setName(cesInfoList.get(i).getName());
							eline.setaXcId(cesInfoList.get(i).getAxcId());
							eline.setzXcId(cesInfoList.get(i).getZxcId());
							eline.setaSiteId(cesInfoList.get(i).getaSiteId());
							eline.setzSiteId(cesInfoList.get(i).getzSiteId());
							elineInfoList.add(eline);
						}									
					}
					Node vpwsSiteNode = setParentNode(tDataBox2, siteInst.getCellId(), vpwsNode);
					createParentNode(getVpwsLine(elineInfoList,12288,"VP",siteInst.getSite_Inst_Id()),vpwsSiteNode,tDataBox2);
					
					//vpls
					
					elanMap = elanService.selectBySiteId(siteInst.getSite_Inst_Id());
					elanInfoList = new ArrayList<ElanInfo>();
					if (null != elanMap && elanMap.size() > 0) {
						Iterator iter = elanMap.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry2 = (Map.Entry) iter.next();
							elanInfoList.addAll((Collection<? extends ElanInfo>) entry2.getValue());
						}
					}

					EtreeInfo etree = new EtreeInfo();			
					etree.setaSiteId(siteInst.getSite_Inst_Id());
					etreeMap = etreeService.filterSelect(etree);
					etreeInfoList = new ArrayList<EtreeInfo>();
					if (null != etreeMap && etreeMap.size() > 0) {
						Iterator iter1 = etreeMap.entrySet().iterator();
						while (iter1.hasNext()) {
							Map.Entry entry = (Map.Entry) iter1.next();
							etreeInfoList.addAll((Collection<? extends EtreeInfo>) entry.getValue());
						}
					}
					if(etreeInfoList !=null && etreeInfoList.size()!=0){
						for(int i=0;i<etreeInfoList.size();i++){
							ElanInfo elan =new ElanInfo();
							elan.setName(etreeInfoList.get(i).getName());
							elan.setAxcId(etreeInfoList.get(i).getaXcId());
							elan.setZxcId(etreeInfoList.get(i).getzXcId());
							elan.setaSiteId(etreeInfoList.get(i).getaSiteId());
							elan.setzSiteId(etreeInfoList.get(i).getzSiteId());
							elanInfoList.add(elan);
						}									
					}
					Node vplsSiteNode = setParentNode(tDataBox2, siteInst.getCellId(), vplsNode);
					createParentNode(getVplsLine(elanInfoList,24576,"VP",siteInst.getSite_Inst_Id()),vplsSiteNode,tDataBox2);
				
				}
				}
				
				createParentNode(getLine("TMS-",768,4,"tms"),"TMS-1-TMS-4",tDataBox2);
				addNode(getAlarmLine("PTP模块",1024,"ptp"),tDataBox2);
				addNode(getAlarmLine("TOD端口",1025,"ptp"),tDataBox2);
				createParentNode(getLine("PTP端口-",1026,26,"ptp"),"PTP端口-1-PTP端口-26",tDataBox2);
	
				//Wrapping
				createParentNode(getLine("环 Ring Wrapping West-",1280,2,"wrappingLsp"),"环 Ring Wrapping West-1- 环 Ring Wrapping West-2",tDataBox2);
				createParentNode(getLine("环 Ring Wrapping east-",1282,2,"wrappingLsp"),"环 Ring Wrapping east-1- 环 Ring Wrapping east-2",tDataBox2);
				createParentNode(getLine("环 Ring Wrapping-",1284,2,"wrappingLsp"),"环 Ring Wrapping-1- 环 Ring Wrapping-2",tDataBox2);										
				createParentNode(getLine("客户业务-",28672,1023,"service"),"客户业务-1-客户业务-1024",tDataBox2);
			} catch (Exception e) {
				throw e;
			} finally {
				UiUtil.closeService_MB(portService);
				UiUtil.closeService_MB(tunnelServiceMB);	
				UiUtil.closeService_MB(pwService);	
				UiUtil.closeService_MB(elineService);	
				UiUtil.closeService_MB(cesService);	
				UiUtil.closeService_MB(elanService);	
				UiUtil.closeService_MB(etreeService);
				portInst=null;
				allportInstList=null;
				allportInstList1=null;
				e1PortInstList1=null;
				lspInfoList = null;
				tunnelInfoList = null;
				pwInfoList = null;
				elineInfoList = null;
				cesInfoList = null;
				elanInfoList = null;
				elanMap = null;
				etreeInfoList = null;
				etreeMap = null;
			}
		}
		else{
			siteId = ConstantUtil.siteId;
			initData(tDataBox2,siteId);
		}
		
	}
	
	/**
	 * 给结点增加网元子节点
	 * 
	 * @throws Exception
	 */
	private Node setParentNode(TDataBox tDataBox2,String CellId,Node node) throws Exception {
		Node siteNode = new Node();
		siteNode.setName(CellId);
		tDataBox2.addElement(siteNode);
		siteNode.setParent(node);
		return siteNode;		
	}
	
	/**
	 * 单网元加载数据
	 * 
	 * @throws Exception
	 */
	private void initData(TDataBox tDataBox2,int siteId) throws Exception {
		PortService_MB portService = null;
		TunnelService_MB tunnelServiceMB = null;
		PwInfoService_MB pwService = null;
		PortInst portInst = null;
		List<PortInst> allportInstList = null;
		List<PortInst> allportInstList1 = null;
		List<PortInst> e1PortInstList1 = null;
		List<Tunnel> lspInfoList = null;
		List<Tunnel> tunnelInfoList = null;
		List<PwInfo> pwInfoList = null;
		List<ElineInfo> elineInfoList = null;
		List<CesInfo> cesInfoList = null;
		ElineInfoService_MB elineService = null;
		CesInfoService_MB cesService = null;
		ElanInfoService_MB elanService = null;
		List<ElanInfo> elanInfoList = null;
		Map<Integer, List<ElanInfo>> elanMap = null;
		EtreeInfoService_MB etreeService = null;
		List<EtreeInfo> etreeInfoList = null;
		Map<Integer, List<EtreeInfo>> etreeMap = null;
		try {
			addNodeLine(getAlarmLine("1.0V",1,"power"),tDataBox2);
			addNodeLine(getAlarmLine("1.2V",2,"power"),tDataBox2);
			addNodeLine(getAlarmLine("2.5V",3,"power"),tDataBox2);
			addNodeLine(getAlarmLine("3.3V",4,"power"),tDataBox2);
			addNodeLine(getAlarmLine("12.0V",5,"power"),tDataBox2);
			addNodeLine(getAlarmLine("设备温度",8,"equipment"),tDataBox2);
			addNodeLine(getAlarmLine("远端设备上电",9,"up"),tDataBox2);
			addNodeLine(getAlarmLine("远端设备掉电",10,"drop"),tDataBox2);
			createParentNode(getLine("风扇-",16,3,"fan"),"风扇-1-风扇-3",tDataBox2);
			
			//端口	
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setSiteId(siteId);
			allportInstList=portService.select(portInst);
			allportInstList1 = new ArrayList<PortInst>();
			e1PortInstList1 = new ArrayList<PortInst>();
			for(int i=0;i<allportInstList.size();i++){
				if(allportInstList.get(i).getPortType().equals("NNI")||allportInstList.get(i).getPortType().equals("UNI")
					||allportInstList.get(i).getPortType().equals("NONE")){
					allportInstList1.add(allportInstList.get(i));
					
				}
				if(allportInstList.get(i).getPortType().equals("e1")){
					e1PortInstList1.add(allportInstList.get(i));
						
					}
			}	
			createParentNode(getLines(allportInstList1,512,"port"),"端口线路",tDataBox2);
			
			//E1
			createParentNode(getLines(e1PortInstList1,32768,"E1"),"E1支路通道",tDataBox2);
			
			//LSP1:1
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);			
			lspInfoList = tunnelServiceMB.queryTunnelBySiteId(siteId);			
			createParentNode(getTunnelLines(lspInfoList,8192,"wrappingLsp",siteId),"LSP1:1_1",tDataBox2);
						
			//TMP
			tunnelInfoList=tunnelServiceMB.selectNodesBySiteId(siteId);			
			createParentNode(getTunnelLines(tunnelInfoList,12288,"tmp",siteId),"TMP通路",tDataBox2);
			
            //TMC
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwInfoList = pwService.selectNodeBySiteid(siteId);								
			createParentNode(getPwLine(pwInfoList,16384,"tmc",siteId),"TMC通路",tDataBox2);
			
			//VPWS
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			elineInfoList = elineService.selectNodeBySite(siteId);
			cesService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
			cesInfoList = cesService.selectNodeBySite(siteId);
			if(cesInfoList !=null && cesInfoList.size()!=0){
				for(int i=0;i<cesInfoList.size();i++){
					ElineInfo eline =new ElineInfo();
					eline.setName(cesInfoList.get(i).getName());
					eline.setaXcId(cesInfoList.get(i).getAxcId());
					eline.setzXcId(cesInfoList.get(i).getZxcId());
					eline.setaSiteId(cesInfoList.get(i).getaSiteId());
					eline.setzSiteId(cesInfoList.get(i).getzSiteId());
					elineInfoList.add(eline);
				}									
			}
			createParentNode(getVpwsLine(elineInfoList,12288,"VP",siteId),"TVPWS",tDataBox2);
			
			//vpls
			elanService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			elanMap = elanService.selectBySiteId(siteId);
			elanInfoList = new ArrayList<ElanInfo>();
			if (null != elanMap && elanMap.size() > 0) {
				Iterator iter = elanMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry2 = (Map.Entry) iter.next();
					elanInfoList.addAll((Collection<? extends ElanInfo>) entry2.getValue());
				}
			}
			etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			EtreeInfo etree = new EtreeInfo();			
			etree.setaSiteId(siteId);
			etreeMap = etreeService.filterSelect(etree);
			etreeInfoList = new ArrayList<EtreeInfo>();
			if (null != etreeMap && etreeMap.size() > 0) {
				Iterator iter1 = etreeMap.entrySet().iterator();
				while (iter1.hasNext()) {
					Map.Entry entry = (Map.Entry) iter1.next();
					etreeInfoList.addAll((Collection<? extends EtreeInfo>) entry.getValue());
				}
			}
			if(etreeInfoList !=null && etreeInfoList.size()!=0){
				for(int i=0;i<etreeInfoList.size();i++){
					ElanInfo elan =new ElanInfo();
					elan.setName(etreeInfoList.get(i).getName());
					elan.setAxcId(etreeInfoList.get(i).getaXcId());
					elan.setZxcId(etreeInfoList.get(i).getzXcId());
					elan.setaSiteId(etreeInfoList.get(i).getaSiteId());
					elan.setzSiteId(etreeInfoList.get(i).getzSiteId());
					elanInfoList.add(elan);
				}									
			}
			createParentNode(getVplsLine(elanInfoList,24576,"VP",siteId),"TVPLS",tDataBox2);

			createParentNode(getLine("TMS-",768,4,"tms"),"TMS-1-TMS-4",tDataBox2);
			addNode(getAlarmLine("PTP模块",1024,"ptp"),tDataBox2);
			addNode(getAlarmLine("TOD端口",1025,"ptp"),tDataBox2);
			createParentNode(getLine("PTP端口-",1026,26,"ptp"),"PTP端口-1-PTP端口-26",tDataBox2);
			//Wrapping
			createParentNode(getLine("环 Ring Wrapping West-",1280,2,"wrappingLsp"),"环 Ring Wrapping West-1- 环 Ring Wrapping West-2",tDataBox2);
			createParentNode(getLine("环 Ring Wrapping east-",1282,2,"wrappingLsp"),"环 Ring Wrapping east-1- 环 Ring Wrapping east-2",tDataBox2);
			createParentNode(getLine("环 Ring Wrapping-",1284,2,"wrappingLsp"),"环 Ring Wrapping-1- 环 Ring Wrapping-2",tDataBox2);										
			createParentNode(getLine("客户业务-",28672,1023,"service"),"客户业务-1-客户业务-1024",tDataBox2);
//			createParentNode(getLine("LSP1+1_",4096,256,"wrappingLsp"),"LSP1+1_1-LSP1+1_257",tDataBox2);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(tunnelServiceMB);	
			UiUtil.closeService_MB(pwService);	
			UiUtil.closeService_MB(elineService);	
			UiUtil.closeService_MB(cesService);	
			UiUtil.closeService_MB(elanService);	
			UiUtil.closeService_MB(etreeService);
			portInst=null;
			allportInstList=null;
			allportInstList1=null;
			e1PortInstList1=null;
			lspInfoList = null;
			tunnelInfoList = null;
			pwInfoList = null;
			elineInfoList = null;
			cesInfoList = null;
			elanInfoList = null;
			elanMap = null;
			etreeInfoList = null;
			etreeMap = null;
		}
	}
	
	
	
	/**
	 * 端口线路
	 */
	private List<AlarmLineInfo> getLines(List<PortInst> portInstList,int number,String type){
		List<AlarmLineInfo>	portAlarmLine = new ArrayList<AlarmLineInfo>();
		if(portInstList!=null && portInstList.size()!=0){
		   for(int i=0;i<portInstList.size();i++){
				AlarmLineInfo alarmLineInfo = new AlarmLineInfo();
				alarmLineInfo.setLineName(portInstList.get(i).getPortName());
				alarmLineInfo.setLineNumber(portInstList.get(i).getNumber()+number-1);						 		
				alarmLineInfo.setType(type);
				portAlarmLine.add(alarmLineInfo);
		   }
		 }
		 return portAlarmLine;
	}
	
	/**
	 * TUNNEL/LSP线路
	 */
	private List<AlarmLineInfo> getTunnelLines(List<Tunnel> tunnelList,int number,String type,int siteId){
		List<AlarmLineInfo>	tunnelAlarmLine = new ArrayList<AlarmLineInfo>();
		if(tunnelList !=null && tunnelList.size()!=0){
		 for(int i=0;i<tunnelList.size();i++){		 
			 String tunneltype = tunnelList.get(i).getTunnelType();
			 if(tunneltype.equals("0") && type.equals("wrappingLsp")){
				 AlarmLineInfo alarmLineInfo = new AlarmLineInfo();
				 alarmLineInfo.setLineName("LSP-"+tunnelList.get(i).getTunnelName());
				 if(siteId == tunnelList.get(i).getaSiteId()){
					    alarmLineInfo.setLineNumber(tunnelList.get(i).getAprotectId()+number-1);
					 }else{
						alarmLineInfo.setLineNumber(tunnelList.get(i).getZprotectId()+number-1);
					 }
				 alarmLineInfo.setType(type);
				 tunnelAlarmLine.add(alarmLineInfo);
			  }else if(!tunneltype.equals("0") && type.equals("tmp")){
				   AlarmLineInfo alarmLineInfo = new AlarmLineInfo();
				   alarmLineInfo.setLineName("TMP通路-"+tunnelList.get(i).getTunnelName());
				  if(siteId == tunnelList.get(i).getaSiteId()){
					  for(int j=0;j<tunnelList.get(i).getLspParticularList().size();j++){
						  if(siteId == tunnelList.get(i).getLspParticularList().get(j).getASiteId()){
							  alarmLineInfo.setLineNumber(tunnelList.get(i).getLspParticularList().get(j).getAtunnelbusinessid()+number-1);
						      break;
						  }
					  }
					  							  
				  }else if(siteId == tunnelList.get(i).getzSiteId()){
					  for(int j=0;j<tunnelList.get(i).getLspParticularList().size();j++){
						  if(siteId == tunnelList.get(i).getLspParticularList().get(j).getZSiteId()){
							  alarmLineInfo.setLineNumber(tunnelList.get(i).getLspParticularList().get(j).getZtunnelbusinessid()+number-1);
						      break;
						  }
					  }			
				  }
				  alarmLineInfo.setType(type);
				  tunnelAlarmLine.add(alarmLineInfo);
			  }		 		 		 									
		 	}
		}
		 return tunnelAlarmLine;
	}

	/**
	 * pw线路
	 */
	private List<AlarmLineInfo> getPwLine(List<PwInfo> pwInstList,int number,String type,int siteId){
		 List<AlarmLineInfo>	pwAlarmLine = new ArrayList<AlarmLineInfo>();
		 if(pwInstList!=null && pwInstList.size()!=0){
			 for(int i=0;i<pwInstList.size();i++){
				 AlarmLineInfo alarmLineInfo = new AlarmLineInfo();
				 alarmLineInfo.setLineName("TMC通路-"+pwInstList.get(i).getPwName());
				 if(pwInstList.get(i).getASiteId() == siteId){
				    alarmLineInfo.setLineNumber(pwInstList.get(i).getApwServiceId()+number-1);
				 }else if(pwInstList.get(i).getZSiteId() == siteId){
					 alarmLineInfo.setLineNumber(pwInstList.get(i).getZpwServiceId()+number-1); 
				 }			 		
				 alarmLineInfo.setType(type);
				 pwAlarmLine.add(alarmLineInfo);
			 }
		 }
		 return pwAlarmLine;
	}

	
	/**
	 * VPWS线路
	 */
	private List<AlarmLineInfo> getVpwsLine(List<ElineInfo> vpwsInstList,int number,String type,int siteId){
		 List<AlarmLineInfo>	vpwsAlarmLine = new ArrayList<AlarmLineInfo>();
		 if(vpwsInstList !=null && vpwsInstList.size()!=0){
			 for(int i=0;i<vpwsInstList.size();i++){
				 AlarmLineInfo alarmLineInfo = new AlarmLineInfo();
				 alarmLineInfo.setLineName("VPWS-"+vpwsInstList.get(i).getName());
				 if(siteId == vpwsInstList.get(i).getaSiteId()){
					alarmLineInfo.setLineNumber(vpwsInstList.get(i).getaXcId()+number-1);
				 }else if(siteId == vpwsInstList.get(i).getzSiteId()){
					 alarmLineInfo.setLineNumber(vpwsInstList.get(i).getzXcId()+number-1); 
				 }		 					 		
				 alarmLineInfo.setType(type);
				 vpwsAlarmLine.add(alarmLineInfo);
			 }
		 }
		 return vpwsAlarmLine;
	}
	
	/**
	 * VPLS线路
	 */
	private List<AlarmLineInfo> getVplsLine(List<ElanInfo> vplsInstList,int number,String type,int siteId){
		 List<AlarmLineInfo>	vplsAlarmLine = new ArrayList<AlarmLineInfo>();
		 if(vplsInstList!=null && vplsInstList.size()!=0){
			 for(int i=0;i<vplsInstList.size();i++){
				 AlarmLineInfo alarmLineInfo = new AlarmLineInfo();
				 alarmLineInfo.setLineName("VPLS-"+vplsInstList.get(i).getName());
				 if(siteId == vplsInstList.get(i).getaSiteId()){
					alarmLineInfo.setLineNumber(vplsInstList.get(i).getAxcId()+number-1);
				 }else if(siteId == vplsInstList.get(i).getzSiteId()){
					 alarmLineInfo.setLineNumber(vplsInstList.get(i).getZxcId()+number-1); 
				 }		 					 		
				 alarmLineInfo.setType(type);
				 vplsAlarmLine.add(alarmLineInfo);
				 //去掉重复的
				 for(int j=0;j<vplsAlarmLine.size();j++){
				    	for(int k=vplsAlarmLine.size()-1;k>j;k--){
				    		if(vplsAlarmLine.get(k).getLineName().equals(vplsAlarmLine.get(j).getLineName())){
				    			vplsAlarmLine.remove(k);
				    		}
				    	}
				    	
				    }		
			 }
		 }
		 return vplsAlarmLine;
	}
	
	/**
	 * 端口线路 LAN1-LAN22  WAN1-WAN4
	 */
	private List<AlarmLineInfo> getLine(String name,int LineNumber,int count,String type){
	List<AlarmLineInfo>	portAlarmLine = new ArrayList<AlarmLineInfo>();
	 for(int i=0;i<count;i++){
		 AlarmLineInfo alarmLineInfo = new AlarmLineInfo();
		 alarmLineInfo.setLineName(name+(i+1));
		 alarmLineInfo.setLineNumber(LineNumber+i);
		 alarmLineInfo.setType(type);
		 portAlarmLine.add(alarmLineInfo);
	 }
	 return portAlarmLine;
	}
	
	/**
	 * 
	 * @param alarmLineInfoList
	 * @param parentName
	 */
	private void createParentNode(List<AlarmLineInfo> alarmLineInfoList,String parentName,TDataBox dataBox){
		AlarmLineInfo alarmLine = new AlarmLineInfo();
		Node nodeParent = new Node();
		nodeParent.setName(parentName);	
		dataBox.addElement(nodeParent);
		if(alarmLineInfoList != null && alarmLineInfoList.size() !=0){						
			alarmLine.setType(alarmLineInfoList.get(0).getType());
			nodeParent.setUserObject(alarmLine);
			for(AlarmLineInfo alarmLineInfo : alarmLineInfoList){
				Node node = new Node();
				node.setName(alarmLineInfo.getCodeName());
				node.setUserObject(alarmLineInfo);
				if(!dataBox.contains(node)){
					dataBox.addElement(node);
					node.setParent(nodeParent);
				}			
			}
		}
	  }
	
	private void createParentNode(List<AlarmLineInfo> alarmLineInfoList,Node nodeParent,TDataBox dataBox){
		AlarmLineInfo alarmLine = new AlarmLineInfo();
		if(alarmLineInfoList != null && alarmLineInfoList.size() !=0){
			if(null != nodeParent.getParent()){
				alarmLine.setType(alarmLineInfoList.get(0).getType());
				nodeParent.getParent().setUserObject(alarmLine);
			}
			alarmLine.setType(alarmLineInfoList.get(0).getType());
			nodeParent.setUserObject(alarmLine);
			for(AlarmLineInfo alarmLineInfo : alarmLineInfoList){
				Node node = new Node();
				node.setName(alarmLineInfo.getLineName());
				node.setUserObject(alarmLineInfo);
				if(!dataBox.contains(node)){
					node.setParent(nodeParent);
					dataBox.addElement(node);
				}
			}
		}
	  }
	
	private void alarmCode(){
		portList = new ArrayList<AlarmLineInfo>();
		portList.add(getAlarmLineInfo("丢包率过限",12));
		portList.add(getAlarmLineInfo("CRC校验错",121));
		portList.add(getAlarmLineInfo("收坏包过限",122));
		portList.add(getAlarmLineInfo("对齐错误过限",123));
		portList.add(getAlarmLineInfo("设备环回",190));
		portList.add(getAlarmLineInfo("线路环回",191));
		portList.add(getAlarmLineInfo("连接信号丢失",11));
		portList.add(getAlarmLineInfo("端口镜像",192));
		portList.add(getAlarmLineInfo("端口关断",193));
		portList.add(getAlarmLineInfo("光模块不在位",16));
		portList.add(getAlarmLineInfo("光模块无光",17));
		portList.add(getAlarmLineInfo("光模块故障",18));
		portList.add(getAlarmLineInfo("对端错误符号门限事件",68));
		portList.add(getAlarmLineInfo("对端错误帧门限事件",69));
		portList.add(getAlarmLineInfo("对端错误帧周期门限事件",70));
		portList.add(getAlarmLineInfo("对端错误帧秒门限事件",71));
		portList.add(getAlarmLineInfo("对端dying gasp事件",35));
		portList.add(getAlarmLineInfo("对端link fault 事件",36));
		portList.add(getAlarmLineInfo("对端critical事件",37));
		portList.add(getAlarmLineInfo("OAM 发现对端",211));
		portList.add(getAlarmLineInfo("OAM环回超时",212));
		portList.add(getAlarmLineInfo("以太网发现环路",72));
		
		ringList = new ArrayList<AlarmLineInfo>();
		ringList.add(getAlarmLineInfo("倒收",97));
		ringList.add(getAlarmLineInfo("桥接",98));
		ringList.add(getAlarmLineInfo("等待恢复",99));
		ringList.add(getAlarmLineInfo("APS失配",3));
		ringList.add(getAlarmLineInfo("倒换失败",4));
		
		TMCList = new ArrayList<AlarmLineInfo>();
		TMCList.add(getAlarmLineInfo("通路连接确认信号丢失",6));
		TMCList.add(getAlarmLineInfo("通路告警指示信号",109));
		TMCList.add(getAlarmLineInfo("通路远端故障指示",107));
		TMCList.add(getAlarmLineInfo("通路不期望的维护实体组",111));
		TMCList.add(getAlarmLineInfo("通路不期望的维护实体组端点",113));
		TMCList.add(getAlarmLineInfo("通路时间间隔失配",105));
		TMCList.add(getAlarmLineInfo("通路锁定",184));
		TMCList.add(getAlarmLineInfo("通路客户信号失效",114));
		TMCList.add(getAlarmLineInfo("通路环回信号超时",188));
		
		tmpList = new ArrayList<AlarmLineInfo>();
		tmpList.add(getAlarmLineInfo("通道连接确认信号丢失",7));
		tmpList.add(getAlarmLineInfo("通道服务层信号失效",9));
		tmpList.add(getAlarmLineInfo("通道时间间隔失配",104));
		tmpList.add(getAlarmLineInfo("通道远端故障指示",106));
		tmpList.add(getAlarmLineInfo("通道告警指示信号",108));
		tmpList.add(getAlarmLineInfo("通道不期望的维护实体组",110));
		tmpList.add(getAlarmLineInfo("通道不期望的维护实体组端点",112));
		tmpList.add(getAlarmLineInfo("通道锁定",183));
		tmpList.add(getAlarmLineInfo("通道环回信号超时",187));
		tmpList.add(getAlarmLineInfo("配置失配",115));
		
		vpwsList = new ArrayList<AlarmLineInfo>();
		vpwsList.add(getAlarmLineInfo("配置失配",115));
		
		e1List = new ArrayList<AlarmLineInfo>();
		e1List.add(getAlarmLineInfo("2M告警指示信号",125));
		e1List.add(getAlarmLineInfo("2M CRC校验错",126));
		e1List.add(getAlarmLineInfo("2M帧失步告警",19));
		e1List.add(getAlarmLineInfo("2M复帧失步告警",20));
		e1List.add(getAlarmLineInfo("2M远端缺陷指示",127));
		e1List.add(getAlarmLineInfo("E1 MRDI",128));
		e1List.add(getAlarmLineInfo("E1线路环回",195));
		e1List.add(getAlarmLineInfo("E1设备环回",196));
		
		List1713 = new ArrayList<AlarmLineInfo>();
		List1713.add(getAlarmLineInfo("以太网连接确认信号丢失",8));
		List1713.add(getAlarmLineInfo("以太网不期望的维护实体组",100));
		List1713.add(getAlarmLineInfo("以太网不期望的维护实体组端点",101));
		List1713.add(getAlarmLineInfo("以太网时间间隔失配",102));
		List1713.add(getAlarmLineInfo("以太网远端故障指示",103));
		List1713.add(getAlarmLineInfo("以太网环回信号超时",197));
		List1713.add(getAlarmLineInfo("以太网锁定",186));
		List1713.add(getAlarmLineInfo("以太网维护域交叉连接错误",130));
		List1713.add(getAlarmLineInfo("以太网维护域时间间隔错误",129));
		List1713.add(getAlarmLineInfo("以太网远端信号丢失",21));
		List1713.add(getAlarmLineInfo("以太网维护端点端口错误",198));
		List1713.add(getAlarmLineInfo("以太网远端错误指示",199));
		
		ptpList = new ArrayList<AlarmLineInfo>();
		ptpList.add(getAlarmLineInfo("精确时间同步丢失",5));
		ptpList.add(getAlarmLineInfo("PTP端口异常告警",65));
	
		tMSList = new ArrayList<AlarmLineInfo>();
		tMSList.add(getAlarmLineInfo("段锁定",185));
		tMSList.add(getAlarmLineInfo("段环回信号超时",189));
		tMSList.add(getAlarmLineInfo("段连接确认信号丢失",10));
		tMSList.add(getAlarmLineInfo("段时间间隔失配",116));
		tMSList.add(getAlarmLineInfo("段远端故障指示",117));
		tMSList.add(getAlarmLineInfo("段告警指示信号",118));
		tMSList.add(getAlarmLineInfo("段不期望的维护实体组",119));
		tMSList.add(getAlarmLineInfo("段不期望的维护实体组端点",120));
		tMSList.add(getAlarmLineInfo("段信号劣化",66));
		tMSList.add(getAlarmLineInfo("段信号失效",67));
		
		fanList = new ArrayList<AlarmLineInfo>();
		fanList.add(getAlarmLineInfo("风扇故障或不在位",48));
		fanList.add(getAlarmLineInfo("风扇转速过缓",49));
		
		equipmentList = new ArrayList<AlarmLineInfo>();
		equipmentList.add(getAlarmLineInfo("设备温度过高告警",33));
		equipmentList.add(getAlarmLineInfo("设备温度过低告警",34));
		equipmentList.add(getAlarmLineInfo("设备温度过高门限越限",38));
		equipmentList.add(getAlarmLineInfo("设备温度过低门限越限",39));
		
		equipmentUPList = new ArrayList<AlarmLineInfo>();//设备上电
		equipmentUPList.add(getAlarmLineInfo("远端设备上电",213));
		
		equipmentDropList = new ArrayList<AlarmLineInfo>();//设备下电
		equipmentDropList.add(getAlarmLineInfo("远端设备掉电",214));
		
		
		powerList = new ArrayList<AlarmLineInfo>();
		powerList.add(getAlarmLineInfo("电源故障",2));
		powerList.add(getAlarmLineInfo("设备电压过高(>14v)",40));
		powerList.add(getAlarmLineInfo("设备电压过低(<9v)",41));
		
	}
	
	
	/**
	 * 根据告警简称和告警码来创建对象
	 * @param lineName
	 * @param lineNumber
	 * @return
	 */
	private AlarmLineInfo getAlarmLineInfo(String lineName,int lineNumber){
		AlarmLineInfo alarmLineInfo = null;
		try {
			alarmLineInfo = new AlarmLineInfo();
			alarmLineInfo.setCodeName(lineName);
			alarmLineInfo.setLineNumber(lineNumber);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return alarmLineInfo;
	}
	
	private AlarmLineInfo getAlarmLine(String lineName,int lineNumber,String type){
		AlarmLineInfo alarmLineInfo = null;
		try {
			alarmLineInfo = new AlarmLineInfo();
			alarmLineInfo.setLineName(lineName);
			alarmLineInfo.setLineNumber(lineNumber);
			alarmLineInfo.setType(type);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return alarmLineInfo;
	}
	
	/**
	 * 根据alarmLineInfo 对象创建Node
	 * @param alarmLineInfo
	 * @param tDataBox
	 */
	private void addNode(AlarmLineInfo alarmLineInfo,TDataBox tDataBox){
		try {
			Node node = new Node();
			node.setName(alarmLineInfo.getCodeName());
			node.setUserObject(alarmLineInfo);
			tDataBox.addElement(node);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 根据alarmLineInfo 对象创建Node
	 * @param alarmLineInfo
	 * @param tDataBox
	 */
	private void addNodeLine(AlarmLineInfo alarmLineInfo,TDataBox tDataBox){
		try {
			Node node = new Node();
			node.setName(alarmLineInfo.getLineName());
			node.setUserObject(alarmLineInfo);
			tDataBox.addElement(node);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 根据alarmLineInfo 对象创建Node
	 * @param alarmLineInfo
	 * @param tDataBox
	 */
	private void addNodeList(List<AlarmLineInfo> alarmCodeList,TDataBox tDataBox){
		try {
			for(AlarmLineInfo alarmLineInfo : alarmCodeList){
				Node node = new Node();
				node.setName(alarmLineInfo.getCodeName());
				node.setUserObject(alarmLineInfo);
				tDataBox.addElement(node);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 判断线路是否是同类型
	 * @param elementList
	 * @return
	 */
	private boolean isSameType(List<Element> elementList){
		boolean fale = true;
		try {
			if(elementList != null && elementList.size()>0){
				for(int i = 0; i< elementList.size(); i++){
					if(elementList.get(i).getUserObject() != null){
						Element elementInfo = elementList.get(i);
						for(int j = 0; j< elementList.size(); j++){
							if(elementList.get(j).getUserObject()!= null && i != j){
								if(!(((AlarmLineInfo)elementInfo.getUserObject()).getType()).equals(((AlarmLineInfo)elementList.get(j).getUserObject()).getType())){
								 return false;
								}
							}
						}
					}else{
						fale = false;
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return fale;
	}
	/**
	 * 获取相应的需要屏蔽的线路号和告警代码
	 * @return
	 */
	public AlarmShieldInfo getAlarmShieldInfo(){
		AlarmShieldInfo alarmShieldInfo = new AlarmShieldInfo();
		if(label ==1 ){
			getLineCode(alarmShieldInfo,null);
		}else if(label == 2){
			getAlarmCode(alarmShieldInfo,null);
		}else if(label == 3){
			getLine(alarmShieldInfo,null);
		}
		return alarmShieldInfo;
	}
	
	public AlarmShieldInfo getAlarmShieldInfo(SiteInst siteInst,String type){
		AlarmShieldInfo alarmShieldInfo = new AlarmShieldInfo();
		if(label ==1 ){
			type = ResourceUtil.srcStr(StringKeysObj.ALARM_SHILED_LINE_MODEL);
			getLineCode(alarmShieldInfo,siteInst);
		}else if(label == 2){
			type = ResourceUtil.srcStr(StringKeysObj.ALARM_SHILED_ALARM_MODEL);
			getAlarmCode(alarmShieldInfo,siteInst);
		}else if(label == 3){
			type = ResourceUtil.srcStr(StringKeysObj.ALARM_SHILEDLINE_MODEL);
			getLine(alarmShieldInfo,siteInst);
		}
		return alarmShieldInfo;
	}

	  //通过线路告警和告警代码来屏蔽告警
    	private void getLineCode(AlarmShieldInfo alarmShieldInfo,SiteInst siteInst){
		
		    List<AlarmShieldInfo_t> alarmShieldTypeList = new ArrayList<AlarmShieldInfo_t>();
			List<Integer> lineCountList = new ArrayList<Integer>();
			List<Integer> shieldAlarmCodeCountList = new ArrayList<Integer>();
			AlarmShieldInfo_t alarmShieldInfo_t = new AlarmShieldInfo_t();
			try {
			//线路号
			List<Element> elementList = tree.getDataBox().getSelectionModel().getAllSelectedElement();
			if(null != siteInst){
				elementList.removeAll(getNeedRemoveList(elementList, siteInst));
			}
			getAlarmLineOrCode(elementList,lineCountList,alarmShieldInfo_t.getLineList());
			alarmShieldInfo_t.setLineCountList(lineCountList);
			//告警代码
			List<Element> elementAlarmCodeList = alarmDataBox.getSelectionModel().getAllSelectedElement();
			getAlarmLineOrCode(elementAlarmCodeList,shieldAlarmCodeCountList,alarmShieldInfo_t.getAlarmCodeList());
			alarmShieldInfo_t.setShieldAlarmCodeCountList(shieldAlarmCodeCountList);
			alarmShieldTypeList.add(alarmShieldInfo_t);
			
           //如果只有告警线路没有只有告警代码则,线路全屏蔽标志 为 1 （代表后面的线路号配置不处理）否则为0
			if(lineCountList !=null && lineCountList.size()>0 ){
				if(shieldAlarmCodeCountList.isEmpty()){
					 alarmShieldInfo_t.setShieldAlarmCode(1);
				}
              
			}
			
			alarmShieldInfo.setAlarmShieldTypeList(alarmShieldTypeList);
			alarmShieldInfo.setLineOrAlarmCode(1);
			alarmShieldInfo.setShieldType(1);
			
			
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			}finally{
				lineCountList = null;
				shieldAlarmCodeCountList = null;
			}
	}
	    //通过告警代码和告警线路来屏蔽告警
	    private void getAlarmCode(AlarmShieldInfo alarmShieldInfo,SiteInst siteInst){
	  	
		    List<AlarmShieldInfo_t> alarmShieldTypeList = new ArrayList<AlarmShieldInfo_t>();
			List<Integer> lineCountList = new ArrayList<Integer>();
			List<Integer> shieldAlarmCodeCountList = new ArrayList<Integer>();
			AlarmShieldInfo_t alarmShieldInfo_t = new AlarmShieldInfo_t();
			try {
			//告警代码
			List<Element> elementList = tree.getDataBox().getSelectionModel().getAllSelectedElement();
			getAlarmLineOrCode(elementList,shieldAlarmCodeCountList,alarmShieldInfo_t.getAlarmCodeList());
			alarmShieldInfo_t.setShieldAlarmCodeCountList(shieldAlarmCodeCountList);
			//线路号
			List<Element> elementAlarmCodeList = alarmDataBox.getSelectionModel().getAllSelectedElement();
			if(null != siteInst){
				elementAlarmCodeList.removeAll(getNeedRemoveList(elementAlarmCodeList, siteInst));
			}
			getAlarmLineOrCode(elementAlarmCodeList,lineCountList,alarmShieldInfo_t.getLineList());
			alarmShieldInfo_t.setLineCountList(lineCountList);
			//如果线路号没有只有告警代码则，代码全屏蔽标志 为 1 （代表后面的线路号配置不处理）否则为0
			if(shieldAlarmCodeCountList !=null && shieldAlarmCodeCountList.size()>0){
			   if(lineCountList.isEmpty() ){
			     	alarmShieldInfo_t.setShieldAlarmCode(1);
			   }
			}
			alarmShieldTypeList.add(alarmShieldInfo_t);
			
			alarmShieldInfo.setAlarmShieldTypeList(alarmShieldTypeList);
			alarmShieldInfo.setLineOrAlarmCode(1);
			alarmShieldInfo.setShieldType(2);
			
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			}finally{
				alarmShieldTypeList = null;
				lineCountList = null;
				shieldAlarmCodeCountList = null;
				alarmShieldInfo_t = null;
			}
	}
	    
	//线路屏蔽
	private void getLine(AlarmShieldInfo alarmShieldInfo,SiteInst siteInst) {
		
		List<Integer> lineCountList = new ArrayList<Integer>();
		List<AlarmShieldInfo_t> alarmShieldTypeList = new ArrayList<AlarmShieldInfo_t>();
		AlarmShieldInfo_t alarmShieldInfo_t = new AlarmShieldInfo_t();
		try {
		//线路号
		List<Element> elementList = tree.getDataBox().getSelectionModel().getAllSelectedElement();
		if(null != siteInst){
			elementList.removeAll(getNeedRemoveList(elementList, siteInst));
		}
		getAlarmLineOrCode(elementList,lineCountList,alarmShieldInfo_t.getLineList());
		alarmShieldInfo_t.setLineCountList(lineCountList);
		alarmShieldTypeList.add(alarmShieldInfo_t);
		alarmShieldInfo.setAlarmShieldTypeList(alarmShieldTypeList);
		alarmShieldInfo.setLineOrAlarmCode(2);
		alarmShieldInfo.setShieldType(1);
		
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			lineCountList = null;
			alarmShieldTypeList = null;
			alarmShieldInfo_t = null;
		}
	}
	
	//移除与该网元不相关的结点
	private List<Element> getNeedRemoveList(List<Element> elementList,SiteInst siteInst){
		List<Element> elementList1 = new ArrayList<Element>();
		List<Element> elementList2 = new ArrayList<Element>();
		for(Element element : elementList){
			if(element.getName().equalsIgnoreCase("端口线路")){
				elementList1 = element.getChildren();
				for(Element element1 : elementList1){
					if(element1.getName().equalsIgnoreCase(siteInst.getCellId())){
						if(elementList.contains(element1)){
							elementList2.add(element1);
						}else{
							elementList2.add(element);
						}
					}else{
						elementList2.add(element1);
						elementList2.addAll(element1.getChildren());
					}
				}
			}
			if(element.getName().equalsIgnoreCase("E1支路通道")){
				elementList1 = element.getChildren();
				for(Element element1 : elementList1){
					if(element1.getName().equalsIgnoreCase(siteInst.getCellId())){
						if(elementList.contains(element1)){
							elementList2.add(element1);
						}else{
							elementList2.add(element);
						}
					}else{
						elementList2.add(element1);
						elementList2.addAll(element1.getChildren());
					}
				}
			}
			if(element.getName().equalsIgnoreCase("LSP1:1_1")){
				elementList1 = element.getChildren();//11 22
				for(Element element1 : elementList1){
					if(element1.getName().equalsIgnoreCase(siteInst.getCellId())){					
						if(elementList.contains(element1)){
							elementList2.add(element1);
						}else{
							elementList2.add(element);
						}
					}else{
						elementList2.add(element1);
						elementList2.addAll(element1.getChildren());
					}
				}
			}
			if(element.getName().equalsIgnoreCase("TMP通路")){
				elementList1 = element.getChildren();
				for(Element element1 : elementList1){
					if(element1.getName().equalsIgnoreCase(siteInst.getCellId())){
						if(elementList.contains(element1)){
							elementList2.add(element1);
						}else{
							elementList2.add(element);
						}
					}else{
						elementList2.add(element1);
						elementList2.addAll(element1.getChildren());
					}
				}
			}
			if(element.getName().equalsIgnoreCase("TMC通路")){
				elementList1 = element.getChildren();
				for(Element element1 : elementList1){
					if(element1.getName().equalsIgnoreCase(siteInst.getCellId())){
						if(elementList.contains(element1)){
							elementList2.add(element1);
						}else{
							elementList2.add(element);
						}
					}else{
						elementList2.add(element1);
						elementList2.addAll(element1.getChildren());
					}
				}
			}
			if(element.getName().equalsIgnoreCase("TVPWS")){
				elementList1 = element.getChildren();
				for(Element element1 : elementList1){
					if(element1.getName().equalsIgnoreCase(siteInst.getCellId())){
						if(elementList.contains(element1)){
							elementList2.add(element1);
						}else{
							elementList2.add(element);
						}
					}else{
						elementList2.add(element1);
						elementList2.addAll(element1.getChildren());
					}
				}
			}
			if(element.getName().equalsIgnoreCase("TVPLS")){
				elementList1 = element.getChildren();
				for(Element element1 : elementList1){
					if(element1.getName().equalsIgnoreCase(siteInst.getCellId())){
						if(elementList.contains(element1)){
							elementList2.add(element1);
						}else{
							elementList2.add(element);
						}
					}else{
						elementList2.add(element1);
						elementList2.addAll(element1.getChildren());
					}
				}
			}
		}	
		return elementList2;
	}
	
	private void getAlarmLineOrCode(List<Element> elementList,List<Integer> alarmCodeList,List<AlarmLineInfo> strings){
		try {
			if(elementList != null && elementList.size()>0){
				for(Element element : elementList){
					if(element.getUserObject() != null && ((AlarmLineInfo)element.getUserObject()).getLineNumber() > 0){
						alarmCodeList.add(((AlarmLineInfo)element.getUserObject()).getLineNumber());
						strings.add(((AlarmLineInfo)element.getUserObject()));
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		
	}
	//屏蔽线路指定的告警
	public void newAlarmLevel(int lable){
		try {
			this.label  = lable;
			alarmCode();
			tDataBox.clear();
			alarmDataBox.clear();
			tDataBox.setName(ResourceUtil.srcStr(StringKeysObj.ALARM_LINE));
			alarmDataBox.setName(ResourceUtil.srcStr(StringKeysObj.ALARM_ALARMCODE));
			initData(tDataBox);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
   //告警屏蔽
	public void newAlarmCodeLevel(int lable){
		this.label  = lable;
		tDataBox.clear();
		alarmDataBox.clear();
		tDataBox.setName(ResourceUtil.srcStr(StringKeysObj.ALARM_ALARMCODE));
		alarmDataBox.setName(ResourceUtil.srcStr(StringKeysObj.ALARM_LINE));
		setAlarmCode(tDataBox);
	}
	
	 //告警屏蔽
	public void newLineCodeLevel(int lable){
		try {
			this.label  = lable;
			tDataBox.clear();
			alarmDataBox.clear();
			alarmDataBox.setName("");
			tDataBox.setName(ResourceUtil.srcStr(StringKeysObj.ALARM_LINE));
			initData(tDataBox);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	//设置所有的告警码
	private void setAlarmCode(TDataBox tDataBox2) {
		
		addNode(getAlarmLine("电源故障",2,"equipment"),tDataBox2);
		addNode(getAlarmLine("设备电压过高(>14v)",40,"equipment"),tDataBox2);
		addNode(getAlarmLine("设备电压过低(<9v)",41,"equipment"),tDataBox2);
		addNode(getAlarmLine("设备温度过高告警",33,"ciHign"),tDataBox2);
		addNode(getAlarmLine("设备温度过低告警",34,"ciHign"),tDataBox2);
		addNode(getAlarmLine("设备温度过高门限越限",38,"ciHign"),tDataBox2);
		addNode(getAlarmLine("设备温度过低门限越限",39,"ciHign"),tDataBox2);
		addNode(getAlarmLine("远端设备上电",213,"remoteon"),tDataBox2);
		addNode(getAlarmLine("远端设备掉电",214,"remoteUp"),tDataBox2);
		addNode(getAlarmLine("风扇故障或不在位",48,"fanFault"),tDataBox2);
		addNode(getAlarmLine("风扇转速过缓",49,"fanFault"),tDataBox2);
		
		
		addNode(getAlarmLine("丢包率过限",12,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("CRC校验错",121,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("收坏包过限",122,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对齐错误过限",123,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("设备环回",190,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("线路环回",191,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("连接信号丢失",11,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("端口镜像",192,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("端口关断",193,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("光模块不在位",16,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("光模块无光",17,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("光模块故障",18,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对端错误符号门限事件",68,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对端错误帧门限事件",69,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对端错误帧周期门限事件",70,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对端错误帧秒门限事件",71,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对端dying gasp事件",35,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对端link fault 事件",36,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("对端critical事件",37,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("OAM 发现对端",211,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("OAM环回超时",212,"portAlarmCode"),tDataBox2);
		addNode(getAlarmLine("以太网发现环路",72,"portAlarmCode"),tDataBox2);
		
		addNode(getAlarmLine("段锁定",185,"segment"),tDataBox2);
		addNode(getAlarmLine("段环回信号超时",189,"segment"),tDataBox2);
		addNode(getAlarmLine("段连接确认信号丢失",10,"segment"),tDataBox2);
		addNode(getAlarmLine("段时间间隔失配",116,"segment"),tDataBox2);
		addNode(getAlarmLine("段远端故障指示",117,"segment"),tDataBox2);
		addNode(getAlarmLine("段告警指示信号",118,"segment"),tDataBox2);
		addNode(getAlarmLine("段不期望的维护实体组",119,"segment"),tDataBox2);
		addNode(getAlarmLine("段不期望的维护实体组端点",120,"segment"),tDataBox2);
		addNode(getAlarmLine("段信号劣化",66,"segment"),tDataBox2);
		addNode(getAlarmLine("段信号失效",67,"segment"),tDataBox2);
		
		addNode(getAlarmLine("精确时间同步丢失",5,"time"),tDataBox2);
		addNode(getAlarmLine("PTP端口异常告警",65,"time"),tDataBox2);
		
		addNode(getAlarmLine("倒收",97,"ring"),tDataBox2);
		addNode(getAlarmLine("桥接",98,"ring"),tDataBox2);
		addNode(getAlarmLine("等待恢复",99,"ring"),tDataBox2);
		addNode(getAlarmLine("APS失配",3,"ring"),tDataBox2);
		addNode(getAlarmLine("倒换失败",4,"ring"),tDataBox2);
		
		addNode(getAlarmLine("通道连接确认信号丢失",7,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道服务层信号失效",9,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道时间间隔失配",104,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道远端故障指示",106,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道告警指示信号",108,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道不期望的维护实体组",110,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道不期望的维护实体组端点",112,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道锁定",183,"tmp"),tDataBox2);
		addNode(getAlarmLine("通道环回信号超时",187,"tmp"),tDataBox2);
		
		//配置失败包括 VPWS/VPLS/TMP通道
		addNode(getAlarmLine("配置失配",115,"tmpConfig"),tDataBox2);
		
		addNode(getAlarmLine("通路连接确认信号丢失",6,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路告警指示信号",109,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路远端故障指示",107,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路不期望的维护实体组",111,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路不期望的维护实体组端点",113,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路时间间隔失配",105,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路锁定",184,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路客户信号失效",114,"tmc"),tDataBox2);
		addNode(getAlarmLine("通路环回信号超时",188,"tmc"),tDataBox2);
		
		addNode(getAlarmLine("以太网连接确认信号丢失",8,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网不期望的维护实体组",100,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网不期望的维护实体组端点",101,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网时间间隔失配",102,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网远端故障指示",103,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网环回信号超时",197,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网锁定",186,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网维护域交叉连接错误",130,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网维护域时间间隔错误",129,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网远端信号丢失",21,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网维护端点端口错误",198,"1713"),tDataBox2);
		addNode(getAlarmLine("以太网远端错误指示",199,"1713"),tDataBox2);
		
		addNode(getAlarmLine("2M告警指示信号",125,"e1"),tDataBox2);
		addNode(getAlarmLine("2M CRC校验错",126,"e1"),tDataBox2);
		addNode(getAlarmLine("2M帧失步告警",19,"e1"),tDataBox2);
		addNode(getAlarmLine("2M复帧失步告警",20,"e1"),tDataBox2);
		addNode(getAlarmLine("2M远端缺陷指示",127,"e1"),tDataBox2);
		addNode(getAlarmLine("E1 MRDI",128,"e1"),tDataBox2);
		addNode(getAlarmLine("E1线路环回",195,"e1"),tDataBox2);
		addNode(getAlarmLine("E1设备环回",196,"e1"),tDataBox2);
		
  }
	
	/**
	 * 这是 线路选择告警码是 处理的监听事件
	 * 
	 */
	private void analysisAlarmCodeListend(){
		//先判断选择的类型是否一致
		List<Element> elementList = tree.getDataBox().getSelectionModel().getAllSelectedElement();
		if(!isSameType(elementList)){
			alarmDataBox.clear();
			return ;
		}
		//先判断选择的类型是否一致		
		Element element = tree.getDataBox().getLastSelectedElement();
		if(element != null){
			//equipment
			if(((AlarmLineInfo)element.getUserObject()).getType().equals("equipment")){
				alarmDataBox.clear();
				addNode(getAlarmLine("1.0V",1,"power"),alarmDataBox);
				addNode(getAlarmLine("1.2V",2,"power"),alarmDataBox);
				addNode(getAlarmLine("2.5V",3,"power"),alarmDataBox);
				addNode(getAlarmLine("3.3V",4,"power"),alarmDataBox);
				addNode(getAlarmLine("12.0V",5,"power"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("ciHign")){
				alarmDataBox.clear();
				addNode(getAlarmLine("设备温度",8,"equipment"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("remoteon")){
				alarmDataBox.clear();
				addNode(getAlarmLine("远端设备上电",9,"up"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("remoteUp")){
				alarmDataBox.clear();
				addNode(getAlarmLine("远端设备掉电",10,"drop"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("remoteUp")){
				alarmDataBox.clear();
				addNodeList(vpwsList,alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("fanFault")){
				alarmDataBox.clear();
				createParentNode(getLine("风扇-",16,3,"fan"),"风扇-1-风扇-3",alarmDataBox);
				addNode(getAlarmLine("PTP模块",1024,"ptp"),alarmDataBox);
				addNode(getAlarmLine("TOD端口",1025,"ptp"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("portAlarmCode")){
			    alarmDataBox.clear();
			    PortService_MB portService = null;
				PortInst portInst = null;
				List<PortInst> allportInstList = null;
				try {
					portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
					portInst = new PortInst();
					portInst.setSiteId(ConstantUtil.siteId);
					allportInstList=portService.select(portInst);
					List<PortInst> allportInstList1 = new ArrayList<PortInst>();
					for(int i=0;i<allportInstList.size();i++){
						if(allportInstList.get(i).getPortType().equals("NNI")||allportInstList.get(i).getPortType().equals("UNI")
							||allportInstList.get(i).getPortType().equals("NONE")){
							allportInstList1.add(allportInstList.get(i));
							
						}
					}
				   createParentNode(getLines(allportInstList1,512,"port"),"端口线路",alarmDataBox);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(portService);
					portInst=null;
					allportInstList=null;
				}
			
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("segment")){
			   alarmDataBox.clear();
			   createParentNode(getLine("TMS-",768,4,"tms"),"TMS-1-TMS-4",alarmDataBox);
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("time")){
			   alarmDataBox.clear();
			   createParentNode(getLine("PTP端口-",1026,26,"ptp"),"PTP端口-1-PTP端口-26",alarmDataBox);
			   
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("ring")){
			   alarmDataBox.clear();
			   createParentNode(getLine("环 Ring Wrapping West-",1280,2,"wrappingLsp"),"环 Ring Wrapping West-1- 环 Ring Wrapping West-2",alarmDataBox);
			   createParentNode(getLine("环 Ring Wrapping east-",1282,2,"wrappingLsp"),"环 Ring Wrapping east-1- 环 Ring Wrapping east-2",alarmDataBox);
			   createParentNode(getLine("环 Ring Wrapping-",1284,2,"wrappingLsp"),"环 Ring Wrapping-1- 环 Ring Wrapping-2",alarmDataBox);			 
			  //LSP
			   TunnelService_MB tunnelServiceMB = null;
			   List<Tunnel> lspInfoList = null;
			   try {
				   tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);			
				   lspInfoList = tunnelServiceMB.queryTunnelBySiteId(ConstantUtil.siteId);		
				   createParentNode(getTunnelLines(lspInfoList,8192,"wrappingLsp",ConstantUtil.siteId),"LSP1:1_1",alarmDataBox);
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(tunnelServiceMB);
					lspInfoList=null;
				}
			  //  createParentNode(getLine("LSP1+1_",4096,256,"wrappingLsp"),"LSP1+1_1-LSP1+1_257",alarmDataBox);

			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("tmp")){
			   alarmDataBox.clear();
			   TunnelService_MB tunnelService = null;
			   List<Tunnel> tunnelInfoList = null;
			   try {
			       tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);			
			       tunnelInfoList=tunnelService.selectNodesBySiteId(ConstantUtil.siteId);			
				   createParentNode(getTunnelLines(tunnelInfoList,12288,"tmp",ConstantUtil.siteId),"TMP通路",alarmDataBox);
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(tunnelService);
					tunnelInfoList=null;
				}			  
			
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("tmpConfig")){
			   alarmDataBox.clear();
			   //TMP
			   TunnelService_MB tunnelService = null;
			   List<Tunnel> tunnelInfoList = null;
			   try {
			       tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);			
			       tunnelInfoList=tunnelService.selectNodesBySiteId(ConstantUtil.siteId);			
				   createParentNode(getTunnelLines(tunnelInfoList,12288,"tmp",ConstantUtil.siteId),"TMP通路",alarmDataBox);
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(tunnelService);
					tunnelInfoList=null;
				}		
		
			   //vpws
			   ElineInfoService_MB elineService = null;
			   List<ElineInfo> elineInfoList = null;
			   CesInfoService_MB cesService = null;
			   List<CesInfo> cesInfoList = null;
			   try {
				elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
				elineInfoList = elineService.selectNodeBySite(ConstantUtil.siteId);
				cesService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
				cesInfoList = cesService.selectNodeBySite(ConstantUtil.siteId);
				if(cesInfoList !=null && cesInfoList.size()!=0){
					for(int i=0;i<cesInfoList.size();i++){
						ElineInfo eline =new ElineInfo();
						eline.setName(cesInfoList.get(i).getName());
						eline.setaXcId(cesInfoList.get(i).getAxcId());
						eline.setzXcId(cesInfoList.get(i).getZxcId());
						eline.setaSiteId(cesInfoList.get(i).getaSiteId());
						eline.setzSiteId(cesInfoList.get(i).getzSiteId());
						elineInfoList.add(eline);
					}									
				}
				createParentNode(getVpwsLine(elineInfoList,12288,"VP",ConstantUtil.siteId),"TVPWS",alarmDataBox);
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(elineService);
					elineInfoList=null;
					UiUtil.closeService_MB(cesService);
					cesInfoList=null;
				}	
				
				
                //VPLS
				ElanInfoService_MB elanService = null;
				List<ElanInfo> elanInfoList = null;
				Map<Integer, List<ElanInfo>> elanMap = null;
				EtreeInfoService_MB etreeService = null;
				List<EtreeInfo> etreeInfoList = null;
				Map<Integer, List<EtreeInfo>> etreeMap = null;
				try {
					elanInfoList = new ArrayList<ElanInfo>();
					etreeInfoList = new ArrayList<EtreeInfo>();
					elanService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
					elanMap = elanService.selectBySiteId(ConstantUtil.siteId);					
					if (null != elanMap && elanMap.size() > 0) {
						Iterator iter = elanMap.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry2 = (Map.Entry) iter.next();
							elanInfoList.addAll((Collection<? extends ElanInfo>) entry2.getValue());
						}
					}
					etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
					EtreeInfo etree = new EtreeInfo();			
					etree.setaSiteId(ConstantUtil.siteId);
					etreeMap = etreeService.filterSelect(etree);				
					if (null != etreeMap && etreeMap.size() > 0) {
						Iterator iter1 = etreeMap.entrySet().iterator();
						while (iter1.hasNext()) {
							Map.Entry entry = (Map.Entry) iter1.next();
							etreeInfoList.addAll((Collection<? extends EtreeInfo>) entry.getValue());
						}
					}
					if(etreeInfoList !=null && etreeInfoList.size()!=0){
						for(int i=0;i<etreeInfoList.size();i++){
							ElanInfo elan =new ElanInfo();
							elan.setName(etreeInfoList.get(i).getName());
							elan.setAxcId(etreeInfoList.get(i).getaXcId());
							elan.setZxcId(etreeInfoList.get(i).getzXcId());
							elan.setaSiteId(etreeInfoList.get(i).getaSiteId());
							elan.setzSiteId(etreeInfoList.get(i).getzSiteId());
							elanInfoList.add(elan);
						}									
					}
					createParentNode(getVplsLine(elanInfoList,24576,"VP",ConstantUtil.siteId),"TVPLS",alarmDataBox);
				  } catch (Exception e) {
						e.printStackTrace();
				  }finally{
						UiUtil.closeService_MB(elanService);				
						UiUtil.closeService_MB(etreeService);
						elanInfoList = null;
						elanMap = null;
						etreeInfoList = null;
						etreeMap = null;
					}
				
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("tmc")){
			   alarmDataBox.clear();
			   PwInfoService_MB pwService = null;
			   List<PwInfo> pwInfoList = null;
			   try {
				   pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				   pwInfoList = pwService.selectNodeBySiteid(ConstantUtil.siteId);								
					createParentNode(getPwLine(pwInfoList,16384,"tmc",ConstantUtil.siteId),"TMC通路",alarmDataBox);
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(pwService);
					pwInfoList=null;
				}				   
			   //createParentNode(getLine("TMC通路-",16384,2047,"tmc"),"TMC通路-1-TMC通路-2048",alarmDataBox);
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("1713")){
			   alarmDataBox.clear();
			   createParentNode(getLine("客户业务-",28672,1023,"service"),"客户业务-1-客户业务-1024",alarmDataBox);
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("e1")){
			   alarmDataBox.clear();
			   PortService_MB portService = null;
			   PortInst portInst = null;
			   List<PortInst> allportInstList = null;
			   try {
					portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
					portInst = new PortInst();
					portInst.setSiteId(ConstantUtil.siteId);
					allportInstList=portService.select(portInst);
					List<PortInst> allportInstList1 = new ArrayList<PortInst>();
					for(int i=0;i<allportInstList.size();i++){
						if(allportInstList.get(i).getPortType().equals("e1")){
							allportInstList1.add(allportInstList.get(i));
							
						}
					}
					  createParentNode(getLines(allportInstList1,32768,"E1"),"E1支路通道",alarmDataBox);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(portService);
					portInst=null;
					allportInstList=null;
				}
			 
			  // createParentNode(getLine("E1支路通道-",32768,3,"E1"),"E1支路通道-1-E1支路通道-4",alarmDataBox);
			}
		}else{
			alarmDataBox.clear();
		}
	}
	
	private void analysisAlarmCodeListend(List<SiteInst> siteInsts){
		//先判断选择的类型是否一致
		List<Element> elementList = tree.getDataBox().getSelectionModel().getAllSelectedElement();
		if(!isSameType(elementList)){
			alarmDataBox.clear();
			return ;
		}
		//先判断选择的类型是否一致		
		Element element = tree.getDataBox().getLastSelectedElement();
		if(element != null){
			//equipment
			if(((AlarmLineInfo)element.getUserObject()).getType().equals("equipment")){
				alarmDataBox.clear();
				addNode(getAlarmLine("1.0V",1,"power"),alarmDataBox);
				addNode(getAlarmLine("1.2V",2,"power"),alarmDataBox);
				addNode(getAlarmLine("2.5V",3,"power"),alarmDataBox);
				addNode(getAlarmLine("3.3V",4,"power"),alarmDataBox);
				addNode(getAlarmLine("12.0V",5,"power"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("ciHign")){
				alarmDataBox.clear();
				addNode(getAlarmLine("设备温度",8,"equipment"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("remoteon")){
				alarmDataBox.clear();
				addNode(getAlarmLine("远端设备上电",9,"up"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("remoteUp")){
				alarmDataBox.clear();
				addNode(getAlarmLine("远端设备掉电",10,"drop"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("remoteUp")){
				alarmDataBox.clear();
				addNodeList(vpwsList,alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("fanFault")){
				alarmDataBox.clear();
				createParentNode(getLine("风扇-",16,3,"fan"),"风扇-1-风扇-3",alarmDataBox);
				addNode(getAlarmLine("PTP模块",1024,"ptp"),alarmDataBox);
				addNode(getAlarmLine("TOD端口",1025,"ptp"),alarmDataBox);
			}else if(((AlarmLineInfo)element.getUserObject()).getType().equals("portAlarmCode")){
			    alarmDataBox.clear();
			    PortService_MB portService = null;
				PortInst portInst = null;
				List<PortInst> allportInstList = null;
				try {
					//端口
					Node portNode = new Node();
					portNode.setName("端口线路");
					alarmDataBox.addElement(portNode);
					portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
					if(null != siteInsts && siteInsts.size() > 0){
						for(SiteInst siteInst : siteInsts){
							portInst = new PortInst();
							portInst.setSiteId(siteInst.getSite_Inst_Id());
							allportInstList=portService.select(portInst);
							List<PortInst> allportInstList1 = new ArrayList<PortInst>();
							for(int i=0;i<allportInstList.size();i++){
								if(allportInstList.get(i).getPortType().equals("NNI")||allportInstList.get(i).getPortType().equals("UNI")
									||allportInstList.get(i).getPortType().equals("NONE")){
									allportInstList1.add(allportInstList.get(i));
									
								}
							}
							Node PortSiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), portNode);
						    createParentNode(getLines(allportInstList1,512,"port"),PortSiteNode,alarmDataBox);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(portService);
					portInst=null;
					allportInstList=null;
				}
			
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("segment")){
			   alarmDataBox.clear();
			   createParentNode(getLine("TMS-",768,4,"tms"),"TMS-1-TMS-4",alarmDataBox);
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("time")){
			   alarmDataBox.clear();
			   createParentNode(getLine("PTP端口-",1026,26,"ptp"),"PTP端口-1-PTP端口-26",alarmDataBox);
			   
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("ring")){
			   alarmDataBox.clear();
			   createParentNode(getLine("环 Ring Wrapping West-",1280,2,"wrappingLsp"),"环 Ring Wrapping West-1- 环 Ring Wrapping West-2",alarmDataBox);
			   createParentNode(getLine("环 Ring Wrapping east-",1282,2,"wrappingLsp"),"环 Ring Wrapping east-1- 环 Ring Wrapping east-2",alarmDataBox);
			   createParentNode(getLine("环 Ring Wrapping-",1284,2,"wrappingLsp"),"环 Ring Wrapping-1- 环 Ring Wrapping-2",alarmDataBox);			 
			  //LSP
			   TunnelService_MB tunnelServiceMB = null;
			   List<Tunnel> lspInfoList = null;
			   try {
				 //LSP1:1
				   Node lspNode = new Node();
				   lspNode.setName("LSP1:1_1");
				   alarmDataBox.addElement(lspNode);
				   tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);	
				   if(null != siteInsts && siteInsts.size() > 0){
						for(SiteInst siteInst : siteInsts){
						   lspInfoList = tunnelServiceMB.queryTunnelBySiteId(siteInst.getSite_Inst_Id());	
						   Node LspSiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), lspNode);
						   createParentNode(getTunnelLines(lspInfoList,8192,"wrappingLsp",siteInst.getSite_Inst_Id()),LspSiteNode,alarmDataBox);
						}
				   }
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(tunnelServiceMB);
					lspInfoList=null;
				}
			  //  createParentNode(getLine("LSP1+1_",4096,256,"wrappingLsp"),"LSP1+1_1-LSP1+1_257",alarmDataBox);

			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("tmp")){
			   alarmDataBox.clear();
			   TunnelService_MB tunnelService = null;
			   List<Tunnel> tunnelInfoList = null;
			   try {
				 //TMP
					Node tmpNode = new Node();
					tmpNode.setName("TMP通路");
					alarmDataBox.addElement(tmpNode);
			       tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);	
			       if(null != siteInsts && siteInsts.size() > 0){
						for(SiteInst siteInst : siteInsts){
					       tunnelInfoList=tunnelService.selectNodesBySiteId(siteInst.getSite_Inst_Id());
					       Node TmpSiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), tmpNode);
						   createParentNode(getTunnelLines(tunnelInfoList,12288,"tmp",siteInst.getSite_Inst_Id()),TmpSiteNode,alarmDataBox);
						}
			       }
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(tunnelService);
					tunnelInfoList=null;
				}			  
			
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("tmpConfig")){
			   alarmDataBox.clear();
			   //TMP
			   TunnelService_MB tunnelService = null;
			   List<Tunnel> tunnelInfoList = null;
			   try {
				 //TMP
					Node tmpNode = new Node();
					tmpNode.setName("TMP通路");
					alarmDataBox.addElement(tmpNode);
			       tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);	
			       if(null != siteInsts && siteInsts.size() > 0){
						for(SiteInst siteInst : siteInsts){
					       tunnelInfoList=tunnelService.selectNodesBySiteId(siteInst.getSite_Inst_Id());
					       Node TmpSiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), tmpNode);
						   createParentNode(getTunnelLines(tunnelInfoList,12288,"tmp",siteInst.getSite_Inst_Id()),TmpSiteNode,alarmDataBox);
						}
			       }
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(tunnelService);
					tunnelInfoList=null;
				}		
		
			   //vpws
			   ElineInfoService_MB elineService = null;
			   List<ElineInfo> elineInfoList = null;
			   CesInfoService_MB cesService = null;
			   List<CesInfo> cesInfoList = null;
			   try {
				 //VPWS
				Node vpwsNode = new Node();
				vpwsNode.setName("TVPWS");
				alarmDataBox.addElement(vpwsNode);
				elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
				cesService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
				if(null != siteInsts && siteInsts.size() > 0){
					for(SiteInst siteInst : siteInsts){
						elineInfoList = elineService.selectNodeBySite(siteInst.getSite_Inst_Id());
						cesInfoList = cesService.selectNodeBySite(siteInst.getSite_Inst_Id());
						if(cesInfoList !=null && cesInfoList.size()!=0){
							for(int i=0;i<cesInfoList.size();i++){
								ElineInfo eline =new ElineInfo();
								eline.setName(cesInfoList.get(i).getName());
								eline.setaXcId(cesInfoList.get(i).getAxcId());
								eline.setzXcId(cesInfoList.get(i).getZxcId());
								eline.setaSiteId(cesInfoList.get(i).getaSiteId());
								eline.setzSiteId(cesInfoList.get(i).getzSiteId());
								elineInfoList.add(eline);
							}									
						}
						Node VpwsSiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), vpwsNode);
						createParentNode(getVpwsLine(elineInfoList,12288,"VP",siteInst.getSite_Inst_Id()),VpwsSiteNode,alarmDataBox);
				       
					}
		       }
				
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(elineService);
					elineInfoList=null;
					UiUtil.closeService_MB(cesService);
					cesInfoList=null;
				}	
				
				
                //VPLS
				ElanInfoService_MB elanService = null;
				List<ElanInfo> elanInfoList = null;
				Map<Integer, List<ElanInfo>> elanMap = null;
				EtreeInfoService_MB etreeService = null;
				List<EtreeInfo> etreeInfoList = null;
				Map<Integer, List<EtreeInfo>> etreeMap = null;
				try {
					//vpls
					Node vplsNode = new Node();
					vplsNode.setName("TVPLS");
					alarmDataBox.addElement(vplsNode);
					elanService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
					etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
					if(null != siteInsts && siteInsts.size() > 0){
						for(SiteInst siteInst : siteInsts){
							elanInfoList = new ArrayList<ElanInfo>();
							etreeInfoList = new ArrayList<EtreeInfo>();				
							elanMap = elanService.selectBySiteId(siteInst.getSite_Inst_Id());					
							if (null != elanMap && elanMap.size() > 0) {
								Iterator iter = elanMap.entrySet().iterator();
								while (iter.hasNext()) {
									Map.Entry entry2 = (Map.Entry) iter.next();
									elanInfoList.addAll((Collection<? extends ElanInfo>) entry2.getValue());
								}
							}
							EtreeInfo etree = new EtreeInfo();			
							etree.setaSiteId(siteInst.getSite_Inst_Id());
							etreeMap = etreeService.filterSelect(etree);				
							if (null != etreeMap && etreeMap.size() > 0) {
								Iterator iter1 = etreeMap.entrySet().iterator();
								while (iter1.hasNext()) {
									Map.Entry entry = (Map.Entry) iter1.next();
									etreeInfoList.addAll((Collection<? extends EtreeInfo>) entry.getValue());
								}
							}
							if(etreeInfoList !=null && etreeInfoList.size()!=0){
								for(int i=0;i<etreeInfoList.size();i++){
									ElanInfo elan =new ElanInfo();
									elan.setName(etreeInfoList.get(i).getName());
									elan.setAxcId(etreeInfoList.get(i).getaXcId());
									elan.setZxcId(etreeInfoList.get(i).getzXcId());
									elan.setaSiteId(etreeInfoList.get(i).getaSiteId());
									elan.setzSiteId(etreeInfoList.get(i).getzSiteId());
									elanInfoList.add(elan);
								}									
							}
							Node VplsSiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), vplsNode);
							createParentNode(getVplsLine(elanInfoList,24576,"VP",siteInst.getSite_Inst_Id()),VplsSiteNode,alarmDataBox);
						}
					}
				  } catch (Exception e) {
						e.printStackTrace();
				  }finally{
						UiUtil.closeService_MB(elanService);				
						UiUtil.closeService_MB(etreeService);
						elanInfoList = null;
						elanMap = null;
						etreeInfoList = null;
						etreeMap = null;
					}
				
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("tmc")){
			   alarmDataBox.clear();
			   PwInfoService_MB pwService = null;
			   List<PwInfo> pwInfoList = null;
			   try {
				 //TMC
					Node tmcNode = new Node();
					tmcNode.setName("TMC通路");
					alarmDataBox.addElement(tmcNode);
				   pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				   if(null != siteInsts && siteInsts.size() > 0){
						for(SiteInst siteInst : siteInsts){
						   pwInfoList = pwService.selectNodeBySiteid(siteInst.getSite_Inst_Id());	
						   Node TmcSiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), tmcNode);
						   createParentNode(getPwLine(pwInfoList,16384,"tmc",siteInst.getSite_Inst_Id()),TmcSiteNode,alarmDataBox);
						}
				   }
			   } catch (Exception e) {
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(pwService);
					pwInfoList=null;
				}				   
			   //createParentNode(getLine("TMC通路-",16384,2047,"tmc"),"TMC通路-1-TMC通路-2048",alarmDataBox);
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("1713")){
			   alarmDataBox.clear();
			   createParentNode(getLine("客户业务-",28672,1023,"service"),"客户业务-1-客户业务-1024",alarmDataBox);
			 }else if(((AlarmLineInfo)element.getUserObject()).getType().equals("e1")){
			   alarmDataBox.clear();
			   PortService_MB portService = null;
			   PortInst portInst = null;
			   List<PortInst> allportInstList = null;
			   try {
					//E1
					Node e1Node = new Node();
					e1Node.setName("E1支路通道");
					alarmDataBox.addElement(e1Node);
					portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
					 if(null != siteInsts && siteInsts.size() > 0){
							for(SiteInst siteInst : siteInsts){
								portInst = new PortInst();
								portInst.setSiteId(siteInst.getSite_Inst_Id());
								allportInstList=portService.select(portInst);
								List<PortInst> allportInstList1 = new ArrayList<PortInst>();
								for(int i=0;i<allportInstList.size();i++){
									if(allportInstList.get(i).getPortType().equals("e1")){
										allportInstList1.add(allportInstList.get(i));
										
									}
								}
								Node E1SiteNode = setParentNode(alarmDataBox, siteInst.getCellId(), e1Node);
								createParentNode(getLines(allportInstList1,32768,"E1"),E1SiteNode,alarmDataBox);
							}
					 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					UiUtil.closeService_MB(portService);
					portInst=null;
					allportInstList=null;
				}
			 
			  // createParentNode(getLine("E1支路通道-",32768,3,"E1"),"E1支路通道-1-E1支路通道-4",alarmDataBox);
			}
		}else{
			alarmDataBox.clear();
		}
	}
	/**
	 * 这是 告警码来确定告警线路是的监听事件
	 * 
	 */
	private void analysisLineListend() {
		try {
			//先判断选择的类型是否一致
			List<Element> elementList = tree.getDataBox().getSelectionModel().getAllSelectedElement();
			if(!isSameType(elementList)){
				alarmDataBox.clear();
				return ;
			}
			//先判断选择的类型是否一致
			Element element = tree.getDataBox().getLastSelectedElement();
			if(element != null){
				//if(element.getName().contains("LAN")||element.getName().contains("WAN")){
				if(element.getName().contains("ge") || element.getName().contains("fe") || element.getName().contains("端口线路")){
					alarmDataBox.clear();
					addNodeList(portList,alarmDataBox);
				}else if(element.getName().contains("环 Ring Wrapping")||element.getName().contains("LSP")){
					alarmDataBox.clear();
					addNodeList(ringList,alarmDataBox);
				}else if(element.getName().contains("TMC通路")){
					alarmDataBox.clear();
					addNodeList(TMCList,alarmDataBox);
				}else if(element.getName().contains("TMP通路")){										
					alarmDataBox.clear();
					addNodeList(tmpList,alarmDataBox);
				}else if(element.getName().contains("VPWS")||element.getName().contains("VPLS")){
					alarmDataBox.clear();
					addNodeList(vpwsList,alarmDataBox);
				}else if(element.getName().contains("e1") || element.getName().contains("E1支路通道")){
					alarmDataBox.clear();
					addNodeList(e1List,alarmDataBox);
				}else if(element.getName().contains("客户业务")){
				   alarmDataBox.clear();
				   addNodeList(List1713,alarmDataBox);
			     }else if(element.getName().contains("PTP模块")
			    		 ||element.getName().contains("TOD端口")
			    		 ||element.getName().contains("PTP端口")){
				   alarmDataBox.clear();
				   addNodeList(ptpList,alarmDataBox);
			     }else if(element.getName().contains("TMS")){
				   alarmDataBox.clear();
				   addNodeList(tMSList,alarmDataBox);
			     }else if(element.getName().contains("风扇")){
			       addNodeList(fanList,alarmDataBox);
				  }else if(element.getName().contains("设备温度")){
				   alarmDataBox.clear();
				   addNodeList(equipmentList,alarmDataBox);
				  }else if(element.getName().contains("远端设备上电")){
					alarmDataBox.clear();
				    addNodeList(equipmentUPList,alarmDataBox);
				 }else if(element.getName().contains("远端设备掉电")){
					alarmDataBox.clear();
				    addNodeList(equipmentDropList,alarmDataBox);
				 }else if(element.getName().contains("1.0V")||
						  element.getName().contains("1.2V")||
						  element.getName().contains("2.5V")||
						  element.getName().contains("3.3V")||
						  element.getName().contains("12.0V")
				       ){
						alarmDataBox.clear();
						addNodeList(powerList,alarmDataBox);
					 }
			}else{
				alarmDataBox.clear();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, AlarmLineJpanel.class);
		}
	}
	
	public TDataBox gettDataBox() {
		return tDataBox;
	}

	public void settDataBox(TDataBox tDataBox) {
		this.tDataBox = tDataBox;
	}

	public TDataBox getAlarmDataBox() {
		return alarmDataBox;
	}

	public void setAlarmDataBox(TDataBox alarmDataBox) {
		this.alarmDataBox = alarmDataBox;
	}
}

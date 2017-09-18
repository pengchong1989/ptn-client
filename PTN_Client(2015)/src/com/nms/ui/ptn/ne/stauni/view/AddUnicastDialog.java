﻿package com.nms.ui.ptn.ne.stauni.view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.ptn.path.StaticUnicastInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.model.ptn.path.SingleSpreadService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.pw.PwNniInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;

public class AddUnicastDialog extends PtnDialog{

	private static final long serialVersionUID = 87465204576134581L;
	
	private JLabel vpls_vs;
	private JComboBox vplsComboBox;
	private JLabel portChoice;
	private JComboBox portComboBox;
	private JLabel macAddress;
	private JTextField macText;
	
	private PtnButton okButton;
	private JButton cancelButton;
	private JPanel contentPanel;
	private JPanel buttonPanel;
	private String type;
	private Map<Integer, String> portMap=null;
	private Map<Integer, String>VplsMap =null;
	private String acNameLogBefore;//log日志用到，记录修改前的ac名称
	
	public String getAcNameLogBefore() {
		return acNameLogBefore;
	}

	public void setAcNameLogBefore(String acNameLogBefore) {
		this.acNameLogBefore = acNameLogBefore;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AddUnicastDialog(JPanel panel, String type) {
		this.setModal(true);
		this.setType(type);
		init();
		initVpls();		
		//当打开新建界面时，给portComboBox赋初始值
		if(this.getVplsComboBox().getSelectedItem()!=null){
		  initPort();
		}
	}
	
	public AddUnicastDialog(StaticUniPanel panel, StaticUnicastInfo info, String type) {
		this.setModal(true);
		this.setType(type);
		init();
		initVpls();
		initPort();
		initdata(info);
		this.vplsComboBox.setEnabled(false);
	}
	
	private String getVplsName(int vsId, int siteId)
	{
		String serviceName="";
		SingleSpreadService_MB service= null;
		try {
			service = (SingleSpreadService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SINGELSPREAD);
			serviceName = service.selectNameByXcId(vsId, siteId);
			
		}catch (Exception e) {
			UiUtil.closeService_MB(service);
		}
		return serviceName;
	}

	private void initdata(StaticUnicastInfo info) {
		this.vplsComboBox.setSelectedIndex(getVplsIndex(getVplsName(info.getVplsVs(), info.getSiteId())));	    		
		this.portComboBox.setSelectedIndex(getPortIndex(info.getPortChoice()));	    
		this.getMacText().setText(info.getMacAddress());
	}

	private void init() {
		initComponents();
		setLayout();
		this.addListener();//监听事件
	}
	
	private void addListener()
	{
		vplsComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					Object obj = ((ControlKeyValue)(vplsComboBox.getSelectedItem())).getObject();
					getport(obj);					
				}
			}
			
		});
	}
	
	public void getport(Object obj)
	{
		int siteId = ConstantUtil.siteId;
		AcPortInfoService_MB acService = null;
		List<ElanInfo> elanInfoList = null;
		ElanInfoService_MB elanInfoService = null;
		Set<Integer> acIdSet = null;
		List<Integer> pwIdList = null;
		List<PwInfo> pwIdList1 = null;
		List<Integer> acList = null;
		List<AcPortInfo> acInfoList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		PwNniInfoService_MB pwNniBufferService = null;
		PwInfoService_MB pwInfoService = null;
		AcPortInfo acInfo = null;
		List<PwNniInfo>	pwNNIInfoList = null;
		UiUtil uiUtil = null;	
		try {
			portMap = new LinkedHashMap<Integer, String>();
			acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);			
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);		
			pwNniBufferService =(PwNniInfoService_MB)ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			pwInfoService = (PwInfoService_MB)ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			defaultComboBoxModel = new DefaultComboBoxModel();
			if(obj instanceof ElanInfo)
			{
				ElanInfo elan =(ElanInfo)obj;				
				elanInfoList = elanInfoService.selectElan(elan);				
				uiUtil = new UiUtil();
				acIdSet =  new HashSet<Integer>();
				pwIdList = new ArrayList<Integer>();
				for(int i=0;i<elanInfoList.size();i++){
					if(elanInfoList.get(i).getaSiteId() == siteId){
						acIdSet.addAll(uiUtil.getAcIdSets(elanInfoList.get(i).getAmostAcId()));
						pwIdList.add(elanInfoList.get(i).getPwId());
					}else if(elanInfoList.get(i).getzSiteId() == siteId){
						acIdSet.addAll(uiUtil.getAcIdSets(elanInfoList.get(i).getZmostAcId()));
						pwIdList.add(elanInfoList.get(i).getPwId());
					}
										
				}
				acList= new ArrayList<Integer>();
				for (Integer acId : acIdSet) {
					 acList.add(acId);					  
				}
				acInfoList = acService.select(acList);
				PwNniInfo pwNNIInfo= null;
				pwIdList1 = new ArrayList<PwInfo>();
				pwNNIInfoList = new ArrayList<PwNniInfo>();
			    for (Integer pwId : pwIdList) {
					 pwNNIInfo = new PwNniInfo();
					 pwNNIInfo.setSiteId(siteId);
					 pwNNIInfo.setPwId(pwId);
					 pwNNIInfoList.addAll(pwNniBufferService.select(pwNNIInfo));	
					 PwInfo pw= new PwInfo();
					 pw.setPwId(pwId);
					 pwIdList1.add(pwInfoService.selectBypwid_notjoin(pw));
				}
			    for(int i=0;i<pwIdList1.size();i++){
			    	for(int j=0;j<pwNNIInfoList.size();j++){
			    		if(pwIdList1.get(i).getPwId()==pwNNIInfoList.get(j).getPwId()){
			    			acInfo = new AcPortInfo();
			    			acInfo.setName(pwIdList1.get(i).getPwName());
			    			acInfo.setLanId(pwNNIInfoList.get(j).getLanId());
			    			acInfoList.add(acInfo);
			    			
			    		}
			    	}
			    }
			    for(AcPortInfo info : acInfoList){					
				    defaultComboBoxModel.addElement(new ControlKeyValue(info.getLanId() + "", info.getName(), info));					
				}
			    for(int i= 0; i < defaultComboBoxModel.getSize(); i++)
				{
			    	portMap.put(i, defaultComboBoxModel.getElementAt(i).toString());
				}
				this.portComboBox.setModel(defaultComboBoxModel);				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(pwNniBufferService);
			UiUtil.closeService_MB(acService);
			UiUtil.closeService_MB(pwInfoService);
			UiUtil.closeService_MB(elanInfoService);
			elanInfoList = null;
			acIdSet = null;
			pwIdList = null;
			pwIdList1 = null;
			acList = null;
			acInfoList = null;
			acInfo = null;
			pwNNIInfoList = null;
			uiUtil = null;
			
		}
		
		
	}
	
	private void initComponents() {
		vpls_vs = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VPLS_SELECT));
		vplsComboBox = new JComboBox();
		portChoice = new JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_PORT_SELECT));
		portComboBox = new JComboBox();
//		portMap = new LinkedHashMap<Integer, String>();
//		portMap.put(1, "LAN1");
//		portMap.put(2, "LAN2");
//		portMap.put(3, "LAN3");
//		portMap.put(4, "LAN4");
//		portMap.put(5, "LAN5");
//		portMap.put(6, "LAN6");
//		portMap.put(7, "LAN7");
//		portMap.put(8, "LAN8");
//		portMap.put(9, "LAN9");
//		portMap.put(10, "LAN10");
//		portMap.put(11, "eLAN1");
//		portMap.put(12, "eLAN2");
//		portMap.put(13, "eLAN3");
//		portMap.put(14, "eLAN4");
//		portMap.put(15, "eLAN5");
//		portMap.put(16, "eLAN6");
//		portMap.put(17, "eLAN7");
//		portMap.put(18, "eLAN8");
//		portMap.put(19, "eLAN9");
//		portMap.put(20, "eLAN10");
//		portMap.put(21, "eLAN11");
//		portMap.put(22, "eLAN12");
//		portMap.put(23, "eLAN13");
//		portMap.put(24, "eLAN14");
//		portMap.put(25, "eLAN15");
//		portMap.put(26, "eLAN16");
//		portMap.put(27, "eLAN17");
//		portMap.put(28, "eLAN18");
//		portMap.put(29, "eLAN19");
//		portMap.put(30, "eLAN20");
//		portMap.put(31, "eLAN21");
//		portMap.put(32, "eLAN22");
//		portMap.put(33, "eLAN23");
//		portMap.put(34, "eLAN24");
//		portMap.put(35, "eLAN25");
//		portMap.put(36, "eLAN26");
//		portMap.put(37, "eLAN27");
//		portMap.put(38, "eLAN28");
//		portMap.put(39, "eLAN29");
//		portMap.put(40, "eLAN30");
//		portMap.put(41, "eLAN31");
//		portMap.put(42, "eLAN32");
//		portMap.put(43, "eLAN33");
//		portMap.put(44, "eLAN34");
//		portMap.put(45, "eLAN35");
//		portMap.put(46, "eLAN36");
//		portMap.put(47, "eLAN37");
//		portMap.put(48, "eLAN38");
//		portMap.put(49, "eLAN39");
//		portMap.put(50, "eLAN40");
//		portMap.put(51, "eLAN41");
//		portMap.put(52, "eLAN42");
//		portMap.put(53, "eLAN43");
//		portMap.put(54, "eLAN44");
//		portMap.put(55, "eLAN45");
//		portMap.put(56, "eLAN46");
//		portMap.put(57, "eLAN47");
//		portMap.put(58, "eLAN48");
//		portMap.put(59, "eLAN49");
//		portMap.put(60, "eLAN50");
//		portMap.put(61, "eLAN51");
//		portMap.put(62, "eLAN52");
//		portMap.put(63, "eLAN53");
//		portMap.put(64, "eLAN54");
//		portMap.put(65, "eLAN55");
//		portMap.put(66, "eLAN56");
//		portMap.put(67, "eLAN57");
//		portMap.put(68, "eLAN58");
//		portMap.put(69, "eLAN59");
//		portMap.put(70, "eLAN60");
//		portMap.put(71, "eLAN61");
//		portMap.put(72, "eLAN62");
//		portMap.put(73, "eLAN63");
//		portMap.put(74, "eLAN64");
//		
//		setModal(portComboBox, portMap);
		
		macAddress = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAC_ADDRESS));
		macText = new JTextField("00-00-00-00-00-00");
		okButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
		cancelButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		contentPanel = new JPanel();
		buttonPanel = new JPanel();
	}
	
	private int getVplsIndex(String value)
	{
		Set<Integer> mapSet =  VplsMap.keySet();	//获取所有的key值 为set的集合
		Iterator<Integer> itor =  mapSet.iterator();//获取key的Iterator便利
		while(itor.hasNext()){//存在下一个值
			int key = itor.next();//当前key值
		if(VplsMap.get(key).equals(value)){//获取value 与 所知道的value比较
			return key;
			}
		}
		
		return 0;
	}
	
	private int getPortIndex(int value)
	{
		Set<Integer> mapSet =  portMap.keySet();	//获取所有的key值 为set的集合
		Iterator<Integer> itor =  mapSet.iterator();//获取key的Iterator便利
		while(itor.hasNext()){//存在下一个值
			int key = itor.next();//当前key值			
			this.getPortComboBox().setSelectedIndex(key);
			AcPortInfo acInfo=new AcPortInfo();
			acInfo =(AcPortInfo)((ControlKeyValue) (this.getPortComboBox().getSelectedItem())).getObject();
		    if(acInfo.getLanId()== value){//获取value 与 所知道的value比较
		       acNameLogBefore = acInfo.getName();
			   return key;
			}
		}
		
		return 0;
	}
	public void initVpls(){
		EtreeInfoService_MB etreeService = null;
		ElanInfoService_MB elanInfoService = null;
		Map<String, List<EtreeInfo>> ServiceIdAndEtreeListMap = null;
		Map<String, List<ElanInfo>> ServiceIdAndElanListMap = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		List<EtreeInfo> etreeInfos = null;
		ElanInfo elanInfo = null;
		try {
			VplsMap = new HashMap<Integer, String>();
			etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			ServiceIdAndEtreeListMap = etreeService.selectNodeBySite(ConstantUtil.siteId);
			ServiceIdAndElanListMap = elanInfoService.selectSiteNodeBy(ConstantUtil.siteId);
			defaultComboBoxModel = new DefaultComboBoxModel();
			for(String str : ServiceIdAndEtreeListMap.keySet()){
				etreeInfos = ServiceIdAndEtreeListMap.get(str);
				List<ElanInfo> elanInfos = new ArrayList<ElanInfo>();
				for(EtreeInfo etreeInfo : etreeInfos){
					elanInfo = new ElanInfo();
					elanInfo.setId(etreeInfo.getId());
					if(etreeInfo.getRootSite() == ConstantUtil.siteId){
						elanInfo.setAxcId(etreeInfo.getaXcId());
						elanInfo.setZxcId(etreeInfo.getzXcId());
						elanInfo.setActiveStatus(etreeInfo.getActiveStatus());
						elanInfo.setaAcId(etreeInfo.getaAcId());
						elanInfo.setzAcId(etreeInfo.getzAcId());
						elanInfo.setaSiteId(etreeInfo.getRootSite());
						elanInfo.setzSiteId(etreeInfo.getBranchSite());
						elanInfo.setAmostAcId(etreeInfo.getAmostAcId());
						elanInfo.setZmostAcId(etreeInfo.getZmostAcId());
						elanInfo.setPwId(etreeInfo.getPwId());
					}else{
						elanInfo.setAxcId(etreeInfo.getzXcId());
						elanInfo.setZxcId(etreeInfo.getaXcId());
						elanInfo.setActiveStatus(etreeInfo.getActiveStatus());
						elanInfo.setaAcId(etreeInfo.getzAcId());
						elanInfo.setzAcId(etreeInfo.getaAcId());
						elanInfo.setaSiteId(etreeInfo.getBranchSite());
						elanInfo.setzSiteId(etreeInfo.getRootSite());
						elanInfo.setAmostAcId(etreeInfo.getAmostAcId());
						elanInfo.setZmostAcId(etreeInfo.getZmostAcId());
						elanInfo.setPwId(etreeInfo.getPwId());
					}
					
					elanInfo.setServiceId(etreeInfo.getServiceId());
					elanInfo.setPwId(etreeInfo.getPwId());
					elanInfo.setServiceType(etreeInfo.getServiceType());
					elanInfo.setName(etreeInfo.getName());
					
					elanInfo.setIsSingle(etreeInfo.getIsSingle());
					elanInfo.setJobStatus(etreeInfo.getJobStatus());
					elanInfos.add(elanInfo);
				}
				ServiceIdAndElanListMap.put(elanInfos.get(0).getServiceId()+"/"+elanInfos.get(0).getServiceType(), elanInfos);
			}
			
			for(String str : ServiceIdAndElanListMap.keySet()){
				List<ElanInfo> elanInfoList = ServiceIdAndElanListMap.get(str);
				for(ElanInfo info : elanInfoList){
					if (info.getaSiteId() == ConstantUtil.siteId) {
						defaultComboBoxModel.addElement(new ControlKeyValue(info.getName() + "", info.getName(), info));
						break;
					}else{
						defaultComboBoxModel.addElement(new ControlKeyValue(info.getName() + "", info.getName(), info));
						break;
					}
				}
			}
			for(int i= 0; i < defaultComboBoxModel.getSize(); i++)
			{
				VplsMap.put(i, defaultComboBoxModel.getElementAt(i).toString());
			}
			this.vplsComboBox.setModel(defaultComboBoxModel);
			if(this.vplsComboBox.getModel().getSize()>0){
			   this.vplsComboBox.setSelectedIndex(0);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(elanInfoService);
			UiUtil.closeService_MB(etreeService);
		}
	}
	
	public void initPort(){
		int siteId = ConstantUtil.siteId;
		AcPortInfoService_MB acService = null;
		List<ElanInfo> elanInfoList = null;
		ElanInfoService_MB elanInfoService = null;
		Set<Integer> acIdSet = null;
		List<Integer> pwIdList = null;
		List<PwInfo> pwIdList1 = null;
		List<Integer> acList = null;
		List<AcPortInfo> acInfoList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		PwNniInfoService_MB pwNniBufferService = null;
		PwInfoService_MB pwInfoService = null;
		AcPortInfo acInfo = null;
		List<PwNniInfo>	pwNNIInfoList = null;
		UiUtil uiUtil = null;	
		try {
			portMap = new LinkedHashMap<Integer, String>();
			acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);			
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);		
			pwNniBufferService =(PwNniInfoService_MB)ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			pwInfoService = (PwInfoService_MB)ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			defaultComboBoxModel = new DefaultComboBoxModel();				
			ElanInfo elan = (ElanInfo)((ControlKeyValue) (this.getVplsComboBox().getSelectedItem())).getObject();
			elanInfoList = elanInfoService.selectElan(elan);				
			uiUtil = new UiUtil();
			acIdSet =  new HashSet<Integer>();
			pwIdList = new ArrayList<Integer>();
			for(int i=0;i<elanInfoList.size();i++){
				if(elanInfoList.get(i).getaSiteId() == siteId){
					acIdSet.addAll(uiUtil.getAcIdSets(elanInfoList.get(i).getAmostAcId()));
					pwIdList.add(elanInfoList.get(i).getPwId());
				}else if(elanInfoList.get(i).getzSiteId() == siteId){
					acIdSet.addAll(uiUtil.getAcIdSets(elanInfoList.get(i).getZmostAcId()));
					pwIdList.add(elanInfoList.get(i).getPwId());
				}
									
			}
			acList= new ArrayList<Integer>();
			for (Integer acId : acIdSet) {
				 acList.add(acId);					  
			}
			acInfoList = acService.select(acList);
			PwNniInfo pwNNIInfo= null;
			pwIdList1 = new ArrayList<PwInfo>();
			pwNNIInfoList = new ArrayList<PwNniInfo>();
		    for (Integer pwId : pwIdList) {
				 pwNNIInfo = new PwNniInfo();
				 pwNNIInfo.setSiteId(siteId);
				 pwNNIInfo.setPwId(pwId);
				 pwNNIInfoList.addAll(pwNniBufferService.select(pwNNIInfo));	
				 PwInfo pwInfo=new PwInfo();
				 pwInfo.setPwId(pwId);
				 pwIdList1.add(pwInfoService.selectBypwid_notjoin(pwInfo));
			}
		    for(int i=0;i<pwIdList1.size();i++){
		    	for(int j=0;j<pwNNIInfoList.size();j++){
		    		if(pwIdList1.get(i).getPwId()==pwNNIInfoList.get(j).getPwId()){
		    			acInfo = new AcPortInfo();
		    			acInfo.setName(pwIdList1.get(i).getPwName());
		    			acInfo.setLanId(pwNNIInfoList.get(j).getLanId());
		    			acInfoList.add(acInfo);
		    			
		    		}
		    	}
		    }
		    for(AcPortInfo info : acInfoList){					
			    defaultComboBoxModel.addElement(new ControlKeyValue(info.getLanId() + "", info.getName(), info));					
			}
		    for(int i= 0; i < defaultComboBoxModel.getSize(); i++)
			{
		    	portMap.put(i, defaultComboBoxModel.getElementAt(i).toString());
			}
			this.portComboBox.setModel(defaultComboBoxModel);				
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(elanInfoService);
			UiUtil.closeService_MB(acService);
			UiUtil.closeService_MB(pwNniBufferService);
			UiUtil.closeService_MB(pwInfoService);

		}
	}
	
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {80, 20};
		layout.columnWeights = new double[] {0, 0};
		layout.rowHeights = new int[] {30, 30, 30, 30};
		layout.rowWeights = new double[] {0, 0, 0, 0};
		
		GridBagConstraints c = new GridBagConstraints();
		contentPanel.setLayout(layout);
		
		addComponent(contentPanel, vpls_vs, 0, 1, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, vplsComboBox, 1, 1, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);
		
		addComponent(contentPanel, portChoice, 0, 2, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, portComboBox, 1, 2, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);
		
		addComponent(contentPanel, macAddress, 0, 3, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 50, 10, 5), GridBagConstraints.NORTHWEST, c);
		addComponent(contentPanel, macText, 1, 3, 1.0, 0.001, 1, 1,
				GridBagConstraints.BOTH, new Insets(15, 10, 10, 20), GridBagConstraints.NORTHWEST, c);
		
		addComponent(contentPanel, buttonPanel, 1, 4, 1.0, 0.1, 4, 1, 
				GridBagConstraints.BOTH, new Insets(15, 5, 10, 50), GridBagConstraints.NORTHWEST, c);
		
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		this.add(contentPanel);
	}

	private int getSuId(StaticUnicastInfo unicastInfo)
	{
		List<Integer> suIds = null;
		SingleSpreadService_MB singleSpreadService = null;
		try {
			singleSpreadService = (SingleSpreadService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SINGELSPREAD);
			// 根据siteId查询数据库
			suIds = singleSpreadService.querySuId(unicastInfo.getSiteId());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(singleSpreadService);
		}
	
		if(suIds.size() == 0)
		{
			return 1;
		}

		for(int i = 1; i <= suIds.size(); i++)
		{
			if(suIds.contains(i))
			{
				continue;
			}
			else
			{
				return i;
			}
		}
		
		return suIds.size()+1;
	}
	
	//收集页面数据
	public StaticUnicastInfo pageSetValue(StaticUnicastInfo unicastInfo) {
		unicastInfo.setSiteId(ConstantUtil.siteId);
		if(this.getType().equals("modify"))
		{
			unicastInfo.setSuId(unicastInfo.getSuId());
		}
		else if(this.getType().equals("create"))
		{
			unicastInfo.setSuId(getSuId(unicastInfo));
		}
		ElanInfo elanInfo = (ElanInfo)((ControlKeyValue) (this.getVplsComboBox().getSelectedItem())).getObject();
		if(elanInfo.getaSiteId() == ConstantUtil.siteId){
			unicastInfo.setVplsVs(elanInfo.getAxcId()); 
		}else{
			unicastInfo.setVplsVs(elanInfo.getZxcId()); 
		}
		unicastInfo.setVplsNameLog(elanInfo.getName());
		AcPortInfo acInfo = (AcPortInfo)((ControlKeyValue) (this.getPortComboBox().getSelectedItem())).getObject();
		unicastInfo.setPortChoice(acInfo.getLanId()); 
		unicastInfo.setAcPortNameLog(acInfo.getName());
		unicastInfo.setMacAddress(this.getMacText().getText());
		return unicastInfo;

	}
	
		
	/**
	 * 判断是否选择了VPLS
	 * @return
	 */
	public boolean vplsExist(){
		if((ControlKeyValue) (this.getVplsComboBox().getSelectedItem()) == null){
			return false;
		}
		return true;
	}
	
	public JLabel getVpls_vs() {
		return vpls_vs;
	}

	public void setVpls_vs(JLabel vplsVs) {
		vpls_vs = vplsVs;
	}

	public JComboBox getVplsComboBox() {
		return vplsComboBox;
	}

	public void setVplsComboBox(JComboBox vplsComboBox) {
		this.vplsComboBox = vplsComboBox;
	}

	public JLabel getPortChoice() {
		return portChoice;
	}

	public void setPortChoice(JLabel portChoice) {
		this.portChoice = portChoice;
	}

	public JComboBox getPortComboBox() {
		return portComboBox;
	}

	public void setPortComboBox(JComboBox portComboBox) {
		this.portComboBox = portComboBox;
	}

	public JLabel getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(JLabel macAddress) {
		this.macAddress = macAddress;
	}

	public JTextField getMacText() {
		return macText;
	}

	public void setMacText(JTextField macText) {
		this.macText = macText;
	}

	public PtnButton getOkButton() {
		return okButton;
	}

	public void setOkButton(PtnButton okButton) {
		this.okButton = okButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public void setButtonPanel(JPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}
}

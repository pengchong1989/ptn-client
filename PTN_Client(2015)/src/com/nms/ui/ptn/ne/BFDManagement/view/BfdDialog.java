package com.nms.ui.ptn.ne.BFDManagement.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.ptn.BfdInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.BfdInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
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
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.ne.BFDManagement.controller.BfdController;




public class BfdDialog extends PtnDialog {
	private static final long serialVersionUID = -1830839435398260186L;
	private JLabel bfdEnabelLabel;
	private JComboBox bfdEnabelComboBox;//BFD会话使能 0/1=关/开
	private JLabel testModeLabel;
	private JComboBox testModeComboBox;//检测方式 0/1=单跳检测/多跳检测
	private JLabel bfdMessageSendTypeLabel;
	private JComboBox bfdMessageSendTypeComboBox;//BFD报文发帧类型：比特7+比特6:00/01=BFD for IP MPLS/ BFD for MPLS-TP比特0~比特5：0/1/2= IP/LSP/PW+LSP
	private JLabel vlanPriorityLabel;
	private JComboBox vlanPriorityComboBox;//vlan优先级1-7
	private JLabel vlanIdLabel;
	private JTextField vlanIdField;//vlanId 1-2-4095
	private JLabel serviceTypeLabel;
	private JComboBox serviceTypeComboBox;//服务类型
	private JLabel localIpLabel;
	private JTextField localIpField;//本地IP地址
	private JLabel peerIpLabel;
	private JTextField peerIpField;//远端IP地址
	private JLabel udpPortLabel;
	private JTextField udpPortField;//UDP源端口
	private JLabel mySidLabel;
	private JTextField mySidField;//本地回话标识符
	private JLabel peerSidLabel;
	private JTextField peerSidField;//对端会话标识符
	private JLabel peerStudyEnabelLabel;
	private JComboBox peerStudyEnabelComboBox;//对端会话标识符自学习使能：0/1=不使能/使能
	private JLabel dmtiLabel;
	private JComboBox dmtiComboBox;//发送最小周期
	private JLabel rmriLabel;
	private JComboBox rmriComboBox;//接收最小周期
	private JLabel dectMulLabel;
	private JTextField dectMulField;//检测倍数DECT_MUL：1-3-255
	private JLabel pwBfdStyleLabel;
	private JComboBox pwBfdStyleComboBox;//BFD FOR PW时BFD封装方式：0/1/2/3=PW-ACH封装/IPv4-UDP封装/IPv6-UDP封装（暂不支持ipv6）/CCTYPE2封装（三层标签）
	private JLabel pwTtlLabel;
	private JTextField pwTtlField;//PW TTL值 0-255
	private JLabel tLayelPortMarkLabel;
	private JComboBox tLayelPortMarkComboBox;//二层端口BFD标识：0表示不是二层端口BFD，1表示是二层端口BFD
	private JLabel lspLabel;
	private JComboBox lspComboBox;//LSP
	private JLabel pwLabel;
	private JComboBox pwComboBox;//pw
	private JLabel vertifyLabel;
	private JPanel componentPanel;
	private JPanel buttonPanel;
	private PtnButton confirm;
	private JButton cancel;
	private BfdInfo info;
	private BfdController controller;
	private BfdDialog dialog ;
	
	public BfdDialog(BfdPanel panel, BfdInfo info,BfdController controller) {
		this.setModal(true);
		this.initComponent();
		this.setComponentLayout();	
		if (info == null ) {
           this.initLspNameAndPwNameCombox(1);
		}
        this.addListener();
        this.controller = controller;
        dialog = this;
		if (info != null ) {
			this.info = info;
			this.setValue(info);
		}
	}
	

    private void initLspNameAndPwNameCombox(int type) {
    	List<Tunnel> tunnelList = null;
		TunnelService_MB tunnelService = null;	
		BfdInfoService_MB bfdService = null;
		List<PwInfo> pwInfoList =null;
		try {
			lspComboBox.removeAllItems();	
			pwComboBox.removeAllItems();
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			tunnelList = tunnelService.select(ConstantUtil.siteId);
			DefaultComboBoxModel boxModel = (DefaultComboBoxModel) lspComboBox.getModel();
			lspComboBox.setPreferredSize(new Dimension(180,5));
			//去掉中间网元
			for(int i=tunnelList.size()-1;i>=0;i--){
				if(tunnelList.get(i).getLspParticularList().get(0).getASiteId() == ConstantUtil.siteId 
						&& ConstantUtil.siteId!=tunnelList.get(i).getaSiteId()){
					tunnelList.remove(i);
					continue;					
				}
				if(tunnelList.get(i).getLspParticularList().get(0).getZSiteId() == ConstantUtil.siteId 
						&& ConstantUtil.siteId!=tunnelList.get(i).getzSiteId()){
					tunnelList.remove(i);
					continue;					
				}				
			}
			//将已建的去掉
			//type为1时对lsp的处理
			if(type ==1){
			   List<Integer> lspIds=bfdService.queryLspIds(ConstantUtil.siteId, type);
			   if(this.info!=null && this.info.getBfdMessageSendType()==type ){
					for(int i=lspIds.size()-1;i>=0;i--){
						if(lspIds.get(i)==info.getLspId()){
							lspIds.remove(i);
						}
					}
				}
			   
				for(int i=tunnelList.size()-1;i>=0;i--){
					for(int j=0;j<lspIds.size();j++){
						if(tunnelList.get(i).getLspParticularList().get(0).getASiteId() == ConstantUtil.siteId 
							&& lspIds.get(j)==tunnelList.get(i).getLspParticularList().get(0).getAtunnelbusinessid()){
							tunnelList.remove(i);
							continue;
						}
						
						
						if(tunnelList.get(i).getLspParticularList().get(0).getZSiteId() == ConstantUtil.siteId 
								&& lspIds.get(j)==tunnelList.get(i).getLspParticularList().get(0).getZtunnelbusinessid()){
								tunnelList.remove(i);
								continue;
							}
					}
				}
			}
						
			for (Tunnel inst : tunnelList) {
				
				  boxModel.addElement(new ControlKeyValue(inst.getTunnelId() + "", inst.getTunnelName(), inst));
			}
			if(info!=null){				
				lspComboBox.setSelectedIndex(0);
			}
			if(tunnelList!=null && tunnelList.size()!=0){
			   int tunnelId=Integer.parseInt(((ControlKeyValue) (lspComboBox.getSelectedItem())).getId());
			    pwInfoList=getpwName(tunnelId);
			}else{
			    pwInfoList=new ArrayList<PwInfo>();
			}
			
			//type为2时对PW的处理
			if(type==2){
			    List<Integer> pwIds=bfdService.queryPwIds(ConstantUtil.siteId, type);
			    if(this.info!=null && this.info.getBfdMessageSendType()==type ){
					for(int i=pwIds.size()-1;i>=0;i--){
						if(pwIds.get(i)==info.getPwId()){
							pwIds.remove(i);
						}
					}
				}

			    for(int i=pwInfoList.size()-1;i>=0;i--){
					for(int j=0;j<pwIds.size();j++){
						if(pwInfoList.get(i).getASiteId() == ConstantUtil.siteId 
							&& pwIds.get(j)==pwInfoList.get(i).getApwServiceId()){
							pwInfoList.remove(i);
							continue;
						}
						
						if(pwInfoList.get(i).getZSiteId() == ConstantUtil.siteId 
								&& pwIds.get(j)==pwInfoList.get(i).getZpwServiceId()){
								pwInfoList.remove(i);
								continue;
							}
					}
				}
			}

			DefaultComboBoxModel boxModel1 = (DefaultComboBoxModel) pwComboBox.getModel();
			pwComboBox.setPreferredSize(new Dimension(180,5));
			if(pwInfoList !=null && pwInfoList.size()>0){
			   for(PwInfo pwinfo:pwInfoList){				
			       boxModel1.addElement(new ControlKeyValue( pwinfo.getPwId() + "", pwinfo.getPwName(), pwinfo));			   
			  }
			}
			pwComboBox.setEnabled(false);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(bfdService);
			tunnelList = null;
		}
	}
    
    
    
    
    private void setValue(BfdInfo bfdInfo) {	
    	super.getComboBoxDataUtil().comboBoxSelectByValue(bfdEnabelComboBox, bfdInfo.getBfdEnabel()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(testModeComboBox, bfdInfo.getTestMode()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(bfdMessageSendTypeComboBox, bfdInfo.getBfdMessageSendType()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(vlanPriorityComboBox, bfdInfo.getVlanPriority()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(serviceTypeComboBox, bfdInfo.getServiceType()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(peerStudyEnabelComboBox, bfdInfo.getPeerStudyEnabel()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(dmtiComboBox, bfdInfo.getDmti()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(rmriComboBox, bfdInfo.getRmri()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(pwBfdStyleComboBox, bfdInfo.getPwBfdStyle()+"");
    	super.getComboBoxDataUtil().comboBoxSelectByValue(tLayelPortMarkComboBox, bfdInfo.gettLayelPortMark()+"");
		this.vlanIdField.setText(bfdInfo.getVlanId()+"");
		this.localIpField.setText(bfdInfo.getLocalIp());
		this.peerIpField.setText(bfdInfo.getPeerIp());
		this.udpPortField.setText(bfdInfo.getUdpPort()+"");
		this.mySidField.setText(bfdInfo.getMySid()+"");
		this.peerSidField.setText(bfdInfo.getPeerSid()+"");
	    this.dectMulField.setText(bfdInfo.getDectMul()+"");		
		this.pwTtlField.setText(bfdInfo.getPwTtl()+"");
		this.initLspNameAndPwNameCombox(bfdInfo.getBfdMessageSendType());	
		super.getComboBoxDataUtil().comboBoxSelect(this.lspComboBox, getLspComboxBoxByLspId(bfdInfo.getLspId(),bfdInfo.getSiteId()).getTunnelId() + "");
	//	this.lspComboBox.getModel().setSelectedItem(getLspComboxBoxByLspId(bfdInfo.getLspId(),bfdInfo.getSiteId()));
	//	this.lspComboBox.setSelectedItem(getLspComboxBoxByLspId(bfdInfo.getLspId(),bfdInfo.getSiteId()));
		if(bfdInfo.getPwId()==0){
			this.pwComboBox.setEnabled(false);
		}else{
			//this.pwComboBox.setSelectedItem(getPwComboxBoxByPwServiceId(bfdInfo.getPwId(),bfdInfo.getSiteId()));
			super.getComboBoxDataUtil().comboBoxSelect(this.pwComboBox, getPwComboxBoxByPwServiceId(bfdInfo.getPwId(),bfdInfo.getSiteId()).getPwId() + "");
			this.pwComboBox.setEnabled(true);
		}		
	}

    private Tunnel getLspComboxBoxByLspId(int lspServiceId,int siteId) {
		TunnelService_MB tunnelService = null;
	//	String tunnelName =null;
		Tunnel tunnel=new Tunnel();
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnel= tunnelService.selectBySiteIdAndServiceId(siteId, lspServiceId);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		finally
		{
			UiUtil.closeService_MB(tunnelService);
		}
		return tunnel;
	}




	private PwInfo getPwComboxBoxByPwServiceId(int pwServiceId,int siteId) {
		PwInfoService_MB pwService = null;
		//String pwName=null;
		PwInfo pw=new PwInfo();
		try {			
			   pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			   pw= pwService.selectBysiteIdAndServiceId(siteId, pwServiceId).get(0);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		finally
		{
			UiUtil.closeService_MB(pwService);
		}
		return pw;
	}
    

	private void initComponent() {
		vertifyLabel = new JLabel();
		vertifyLabel.setForeground(Color.red);
		this.setTitle(ResourceUtil.srcStr(StringKeysPanel.PANEL_BFDINFO));
		componentPanel = new JPanel();
		bfdEnabelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BFD_ENABEL));
		bfdEnabelComboBox = new JComboBox();		
		testModeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TEST_MODEL));
		testModeComboBox = new JComboBox();
		bfdMessageSendTypeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BFDMESSAGE_SENDTYPE));
		bfdMessageSendTypeComboBox = new JComboBox();
		vlanPriorityLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_VLAN_PRIORITY));
		vlanPriorityComboBox = new JComboBox();
		vlanIdLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BFD_VLAN_ID));
		vlanIdField = new JTextField();
		vlanIdField.setText("2");
		serviceTypeLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BFD_SERVICE_TYPE));
		serviceTypeComboBox = new JComboBox();
		localIpLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOCAL_IP));
		localIpField = new JTextField();
		localIpField.setText("0.0.0.0");
		peerIpLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PEER_IP));
		peerIpField = new JTextField();
		peerIpField.setText("0.0.0.0");
		udpPortLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_UDP_PORT));
		udpPortField = new JTextField();
		udpPortField.setText("49152");
		mySidLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MY_SID));
		mySidField = new JTextField();
		int mySid=this.getMySid();
		mySidField.setText(mySid+"");
		peerSidLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PEER_SID));
		peerSidField = new JTextField();
		int peerSid=this.getPeerSid();
		peerSidField.setText(peerSid+"");
		peerStudyEnabelLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PEERSTUDY_TYPE));
		peerStudyEnabelComboBox = new JComboBox();
		dmtiLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DMTI));
		dmtiComboBox = new JComboBox();
		rmriLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMRI));
		rmriComboBox = new JComboBox();
		
		dectMulLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DECT_MUL));
		dectMulField = new JTextField();
		dectMulField.setText("3");
		pwBfdStyleLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PWBFD_STYLE));
		pwBfdStyleComboBox = new JComboBox();
		pwTtlLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW_TTL));
		pwTtlField = new JTextField();
		pwTtlField.setText("255");
		tLayelPortMarkLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TLAYEL_PORTMARK));
		tLayelPortMarkComboBox = new JComboBox();
		lspLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LSP));
		lspComboBox = new JComboBox();
		pwLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW));
		pwComboBox = new JComboBox();
		
		buttonPanel = new JPanel();
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		try {
			super.getComboBoxDataUtil().comboBoxData(bfdEnabelComboBox, "bfdEnabel");
			bfdEnabelComboBox.setSelectedIndex(0);
			super.getComboBoxDataUtil().comboBoxData(testModeComboBox, "testMode");
			testModeComboBox.setSelectedIndex(0);
			super.getComboBoxDataUtil().comboBoxData(bfdMessageSendTypeComboBox, "bfdMessageSendType");
			bfdMessageSendTypeComboBox.setSelectedIndex(1);
			super.getComboBoxDataUtil().comboBoxData(vlanPriorityComboBox, "vlanPriority");
			super.getComboBoxDataUtil().comboBoxData(serviceTypeComboBox, "serviceType");
			serviceTypeComboBox.setSelectedIndex(0);
			super.getComboBoxDataUtil().comboBoxData(peerStudyEnabelComboBox, "peerStudyEnabel");
			peerStudyEnabelComboBox.setSelectedIndex(0);
			super.getComboBoxDataUtil().comboBoxData(dmtiComboBox, "DMTI");
			dmtiComboBox.setSelectedIndex(1);
			super.getComboBoxDataUtil().comboBoxData(rmriComboBox, "RMRI");
			rmriComboBox.setSelectedIndex(1);
			super.getComboBoxDataUtil().comboBoxData(pwBfdStyleComboBox, "pwBfdStyle");
			pwBfdStyleComboBox.setSelectedIndex(0);
			super.getComboBoxDataUtil().comboBoxData(tLayelPortMarkComboBox, "tLayelPortMark");			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	
	}
    //获取bfdId
	private int getBfdId(){
		List<Integer> bfdIds = null;
		BfdInfoService_MB bfdService = null;
		try {
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			// 根据siteId查询数据库
			bfdIds = bfdService.queryBfdId(ConstantUtil.siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e, null);
		} finally {
			UiUtil.closeService_MB(bfdService);
		}	
		if(bfdIds.size() == 0){
			return 1;
		}

		for(int i = 1; i <= bfdIds.size(); i++){
			if(bfdIds.contains(i)){
				continue;
			}else{
				return i;
			}
		}		
		return bfdIds.size()+1;		
	}
	//获取mysid
	private int getMySid(){
		List<Integer> mySids = null;
		BfdInfoService_MB bfdService = null;
		try {
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			// 根据siteId查询数据库
			mySids = bfdService.queryMySid(ConstantUtil.siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(bfdService);
		}
	
		if(mySids.size() == 0){
			return 1;
		}

		for(int i = 1; i <= mySids.size(); i++){
			if(mySids.contains(i)){
				continue;
			}else{				
				return i;
			}
		}		
		return mySids.size()+1;		
	}
	
	//判断mysid有么有重复
	private boolean getMySidStatus(int mySid,BfdInfo bfdinfo){
		Boolean flag = false;
		List<Integer> mySids = null;
		BfdInfoService_MB bfdService = null;
		try {
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			// 根据siteId查询数据库
			mySids = bfdService.queryMySid(ConstantUtil.siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(bfdService);
		}
	
		if(mySids.size() == 0){
			flag = true;
		}
        if(bfdinfo == null){
		   if(mySids.contains(mySid)){
			  flag = false;
		   }else{				
			  flag = true;
		   }
        }
        if(bfdinfo!=null){
        	for(int i = mySids.size()-1; i >= 0; i--){
        		if(mySids.get(i)== bfdinfo.getMySid()){
        			mySids.remove(i);
        		}
        	}
				if(mySids.contains(mySids)){
					flag = false;
				}else{
					flag = true;
				}       	
        }
		return flag;		
	}
	//判断peerSid有没有重复
	private boolean getPeerSidStatus(int peerSid,BfdInfo bfdinfo){
		Boolean flag = false;
    	List<Integer> peerSids = null;
		BfdInfoService_MB bfdService = null;
		try {
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			// 根据siteId查询数据库
			peerSids = bfdService.queryPeerSid(ConstantUtil.siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(bfdService);
		}
	   
		if(peerSids.size() == 0){
			flag = true;
		}
        if(bfdinfo ==null){
			if(peerSids.contains(peerSid)){
				flag = false;
			}else{
				flag = true;
			}
        }
        if(bfdinfo!=null){
        	for(int i = peerSids.size()-1; i >= 0; i--){
        		if(peerSids.get(i)== bfdinfo.getPeerSid()){
        			peerSids.remove(i);
        		}
        	}
				if(peerSids.contains(peerSid)){
					flag = false;
				}else{
					flag = true;
				}       	
        }
		return flag;			
	}
	
	//获取peerSid
    private int getPeerSid(){
    	List<Integer> peerSids = null;
		BfdInfoService_MB bfdService = null;
		try {
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			// 根据siteId查询数据库
			peerSids = bfdService.queryPeerSid(ConstantUtil.siteId);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(bfdService);
		}
	
		if(peerSids.size() == 0){
			return 1;
		}

		for(int i = 1; i <= peerSids.size(); i++){
			if(peerSids.contains(i)){
				continue;
			}else{
				return i;
			}
		}		
		return peerSids.size()+1;			
	}
      	
	private void setComponentLayout() {
		setOamInfoLayout();
		setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[] { 100, 20 };
		layout.rowWeights = new double[] { 1.0, 0.0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		layout.setConstraints(componentPanel, c);
		this.add(componentPanel);
		c.gridy = 1;
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);
	}

	private void setOamInfoLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 80, 150, 80, 150 };
		componentLayout.columnWeights = new double[] { 1.0, 1.0 };
		componentLayout.rowHeights = new int[] { 10, 10, 10, 10, 10, 10, 10,10, 10, 10};
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0 };
		componentPanel.setLayout(componentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(bfdEnabelLabel, c);
		componentPanel.add(bfdEnabelLabel);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(bfdEnabelComboBox, c);
		componentPanel.add(bfdEnabelComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(testModeLabel, c);
		componentPanel.add(testModeLabel);
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(testModeComboBox, c);
		componentPanel.add(testModeComboBox);
					
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(vlanPriorityLabel, c);
		componentPanel.add(vlanPriorityLabel);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(vlanPriorityComboBox, c);
		componentPanel.add(vlanPriorityComboBox);
	
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(vlanIdLabel, c);
		componentPanel.add(vlanIdLabel);
		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(vlanIdField, c);
		componentPanel.add(vlanIdField);
				
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(bfdMessageSendTypeLabel, c);
		componentPanel.add(bfdMessageSendTypeLabel);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(bfdMessageSendTypeComboBox, c);
		componentPanel.add(bfdMessageSendTypeComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(serviceTypeLabel, c);
		componentPanel.add(serviceTypeLabel);
		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(serviceTypeComboBox, c);
		componentPanel.add(serviceTypeComboBox);

		
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(localIpLabel, c);
		componentPanel.add(localIpLabel);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(localIpField, c);
		componentPanel.add(localIpField);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(peerIpLabel, c);
		componentPanel.add(peerIpLabel);
		c.gridx = 3;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(peerIpField, c);
		componentPanel.add(peerIpField);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(mySidLabel, c);
		componentPanel.add(mySidLabel);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(mySidField, c);
		componentPanel.add(mySidField);	
		c.gridx = 2;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(peerSidLabel, c);
		componentPanel.add(peerSidLabel);
		c.gridx = 3;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(peerSidField, c);
		componentPanel.add(peerSidField);
		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(peerStudyEnabelLabel, c);
		componentPanel.add(peerStudyEnabelLabel);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(peerStudyEnabelComboBox, c);
		componentPanel.add(peerStudyEnabelComboBox);
		c.gridx = 2;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(udpPortLabel, c);
		componentPanel.add(udpPortLabel);
		c.gridx = 3;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(udpPortField, c);
		componentPanel.add(udpPortField);
				
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmtiLabel, c);
		componentPanel.add(dmtiLabel);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dmtiComboBox, c);
		componentPanel.add(dmtiComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(rmriLabel, c);
		componentPanel.add(rmriLabel);
		c.gridx = 3;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(rmriComboBox, c);
		componentPanel.add(rmriComboBox);

		
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dectMulLabel, c);
		componentPanel.add(dectMulLabel);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(dectMulField, c);
		componentPanel.add(dectMulField);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(pwBfdStyleLabel, c);
		componentPanel.add(pwBfdStyleLabel);
		c.gridx = 3;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(pwBfdStyleComboBox, c);
		componentPanel.add(pwBfdStyleComboBox);

		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(pwTtlLabel, c);
		componentPanel.add(pwTtlLabel);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(pwTtlField, c);
		componentPanel.add(pwTtlField);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(tLayelPortMarkLabel, c);
		componentPanel.add(tLayelPortMarkLabel);
		c.gridx = 3;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(tLayelPortMarkComboBox, c);
		componentPanel.add(tLayelPortMarkComboBox);
	
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lspLabel, c);
		componentPanel.add(lspLabel);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(lspComboBox, c);
		componentPanel.add(lspComboBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 30, 5, 5);
		componentLayout.setConstraints(pwLabel, c);
		componentPanel.add(pwLabel);
		c.gridx = 3;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(pwComboBox, c);
		componentPanel.add(pwComboBox);

		
	}

	private void setButtonLayout() {
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(vertifyLabel);
		buttonPanel.add(confirm);
		buttonPanel.add(cancel);
	}

	private void addListener() {
		bfdMessageSendTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if(evt.getStateChange() == 1){					
					if (bfdMessageSendTypeComboBox.getSelectedIndex()==1) {	
						initLspNameAndPwNameCombox(1);
						pwComboBox.setEnabled(false);

					}
					if (bfdMessageSendTypeComboBox.getSelectedIndex()!=1) {
						initLspNameAndPwNameCombox(2);			
						pwComboBox.setEnabled(true);
					}
				}
				
			}
		});
		
		lspComboBox.addItemListener(new java.awt.event.ItemListener() {
			
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if(evt.getStateChange() == 1 ){	
					BfdInfoService_MB bfdService = null;
					try {
						bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
						int tunnelId=Integer.parseInt(((ControlKeyValue) (lspComboBox.getSelectedItem())).getId());
						pwComboBox.removeAllItems();
						List<PwInfo> pwInfoList=getpwName(tunnelId);
						
						if(bfdMessageSendTypeComboBox.getSelectedIndex()==2){
						    List<Integer> pwIds=bfdService.queryPwIds(ConstantUtil.siteId, 2);
						    if(info!=null && info.getBfdMessageSendType()==2 ){
								for(int i=pwIds.size()-1;i>=0;i--){
									if(pwIds.get(i)==info.getPwId()){
										pwIds.remove(i);
									}
								}
							}
						    for(int i=pwInfoList.size()-1;i>=0;i--){
								for(int j=0;j<pwIds.size();j++){
									if(pwInfoList.get(i).getASiteId() == ConstantUtil.siteId 
										&& pwIds.get(j)==pwInfoList.get(i).getApwServiceId()){
										pwInfoList.remove(i);
										continue;
									}
									
									if(pwInfoList.get(i).getZSiteId() == ConstantUtil.siteId 
											&& pwIds.get(j)==pwInfoList.get(i).getZpwServiceId()){
											pwInfoList.remove(i);
											continue;
										}
								}
							}
						}
						DefaultComboBoxModel boxModel = (DefaultComboBoxModel) pwComboBox.getModel();
						pwComboBox.setPreferredSize(new Dimension(180,5));
						if(pwInfoList !=null && pwInfoList.size()>0){
						   for(PwInfo pwinfo:pwInfoList){				
						       boxModel.addElement(new ControlKeyValue( pwinfo.getPwId() + "", pwinfo.getPwName(), pwinfo));			   
						  }
						}
					}catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}finally{
						UiUtil.closeService_MB(bfdService);
					}
					
				}
				
			}
		});
			
		confirm.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					int LspId =0;
					int PwId =0;
					if(info == null && getBfdId()>100){
					    vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_EMPTY));
						return;
					}
					if(bfdMessageSendTypeComboBox.getSelectedIndex()==1 && lspComboBox.getSelectedItem()== null){
						vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_NOT_EMPTY_LSP));
						return;
					}
					if(bfdMessageSendTypeComboBox.getSelectedIndex()==0 && (lspComboBox.getSelectedItem()== null || pwComboBox.getSelectedItem()==null)){
						vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
						return;
					}
					if(bfdMessageSendTypeComboBox.getSelectedIndex()==2 && (lspComboBox.getSelectedItem()== null ||pwComboBox.getSelectedItem()==null)){
						vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_FULL_DATA));
						return;
					}
				    //vlan id
		            if(!vlanIdField.getText().matches(CheckingUtil.NUMBER_NUM)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_VLANID_NUMBER));
						return;
		            }
		            if(Integer.parseInt(vlanIdField.getText())>4095 || Integer.parseInt(vlanIdField.getText())<1){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_VLANID));
						return;
		            }
		            //udp port
		            if(!udpPortField.getText().matches(CheckingUtil.NUMBER_NUM)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_UDPPORT_NUMBER));
						return;
		            }
		            if(udpPortField.getText().length()>5 && Integer.parseInt(udpPortField.getText())>65535 || Integer.parseInt(udpPortField.getText())<49152){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_UDPPORT));
						return;
		            }
		            //mysid
		            if(!mySidField.getText().matches(CheckingUtil.NUMBER_NUM)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_MYSID_NUMBER));
						return;
		            }
		            if((Integer.parseInt(mySidField.getText())/65535)>65537 || Integer.parseInt(mySidField.getText())<1){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_MYSID));
						return;
		            }
		            if(!getMySidStatus(Integer.parseInt(mySidField.getText()),info)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_MYSID_HAVE));
						return;		            	
		            }
		            //PEERSID
		            if(!peerSidField.getText().matches(CheckingUtil.NUMBER_NUM)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_PEERSID_NUMBER));
						return;
		            }
		            if((Integer.parseInt(peerSidField.getText())/65535)>65537 || Integer.parseInt(peerSidField.getText())<1){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_PEERSID));
						return;
		            }
		         
		            if(!getPeerSidStatus(Integer.parseInt(peerSidField.getText()),info)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_PEERSID_HAVE));
						return;		            	
		             }
		           
		            //DECTMUI
		            if(!dectMulField.getText().matches(CheckingUtil.NUMBER_NUM)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_DECTMUI_NUMBER));
						return;
		            }
		            if(Integer.parseInt(dectMulField.getText())>255 || Integer.parseInt(dectMulField.getText())<1){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_DECTMUI));
						return;
		            }
		            //pwttl
		            if(!pwTtlField.getText().matches(CheckingUtil.NUMBER_NUM)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_PWTLL_NUMBER));
						return;
		            }
		            if(Integer.parseInt(pwTtlField.getText())>255 || Integer.parseInt(pwTtlField.getText())<0){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_PWTLL));
						return;
		            }      
		            //localIp
		            if(!localIpField.getText().matches(CheckingUtil.IP_REGULAR)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_LOCALIPMATCH));
						return;
		            }
		            //PEERiP
		            if(!peerIpField.getText().matches(CheckingUtil.IP_REGULAR)){
		            	vertifyLabel.setText(ResourceUtil.srcStr(StringKeysTip.TIP_BFD_PEERIPMATCH));
						return;
		            }
		            
		            Tunnel tunnel=((Tunnel) ((ControlKeyValue) (lspComboBox.getSelectedItem())).getObject());
                    if(ConstantUtil.siteId == tunnel.getLspParticularList().get(0).getASiteId()){
                    	LspId=tunnel.getLspParticularList().get(0).getAtunnelbusinessid();                   	
                    }else{
                    	LspId=tunnel.getLspParticularList().get(0).getZtunnelbusinessid();                   	
                    }
                    String pwName = null;                   
                    if(bfdMessageSendTypeComboBox.getSelectedIndex()==1){
                    	PwId =0;                    	                	
                    }else{
                    	PwInfo pwInfo=((PwInfo) ((ControlKeyValue) (pwComboBox.getSelectedItem())).getObject());
                    	if(ConstantUtil.siteId == pwInfo.getASiteId()){
                    		PwId=pwInfo.getApwServiceId();                    	   
                    	}else{
                    		PwId=pwInfo.getZpwServiceId();                   	  
                    	}
                    	pwName = pwInfo.getPwName();
                    }
                  
                    if(info == null){
		            	info = new BfdInfo();
		            	info.setBfdId(getBfdId());
		            }
                    info.setLspName(tunnel.getTunnelName());
                    info.setPwName(pwName);
                    info.setLspId(LspId);
                    info.setPwId(PwId);
		            info.setBfdEnabel((Integer.parseInt(((Code) ((ControlKeyValue) bfdEnabelComboBox.getSelectedItem()).getObject()).getCodeValue())));
		            info.setTestMode((Integer.parseInt(((Code)((ControlKeyValue) testModeComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setBfdMessageSendType((Integer.parseInt(((Code)((ControlKeyValue) bfdMessageSendTypeComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setPwBfdStyle((Integer.parseInt(((Code)((ControlKeyValue) pwBfdStyleComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setVlanPriority((Integer.parseInt(((Code)((ControlKeyValue) vlanPriorityComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setVlanId(Integer.parseInt(vlanIdField.getText()));
                    info.setServiceType((Integer.parseInt(((Code)((ControlKeyValue) serviceTypeComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setLocalIp(localIpField.getText());
                    info.setPeerIp(peerIpField.getText()); 
                    info.setUdpPort(Integer.parseInt(udpPortField.getText())); 
                    info.setMySid(Integer.parseInt(mySidField.getText()));
                    info.setPeerSid(Integer.parseInt(peerSidField.getText()));
                    info.setPeerStudyEnabel((Integer.parseInt(((Code)((ControlKeyValue) peerStudyEnabelComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setDmti((Integer.parseInt(((Code)((ControlKeyValue) dmtiComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setRmri((Integer.parseInt(((Code)((ControlKeyValue) rmriComboBox.getSelectedItem()).getObject()).getCodeValue())));
  		            info.setDectMul(Integer.parseInt(dectMulField.getText()));
                    info.setPwTtl(Integer.parseInt(pwTtlField.getText()));
                    info.settLayelPortMark((Integer.parseInt(((Code)((ControlKeyValue) tLayelPortMarkComboBox.getSelectedItem()).getObject()).getCodeValue())));
                    info.setSiteId(ConstantUtil.siteId);
                    String result = null;
                    if(info.getId() == 0){
                    	try {
            				DispatchUtil bfdDispatch = new DispatchUtil(RmiKeys.RMI_BFD);	
            				result = bfdDispatch.excuteInsert(info);	
            				//添加日志记录
            				AddOperateLog.insertOperLog(confirm, EOperationLogType.INSERTBFD.getValue(), result, 
            						null, info, ConstantUtil.siteId, "bfd_ipv4", "bfd");
            			} catch (Exception e) {
            				ExceptionManage.dispose(e,this.getClass());
            			}
                    }else{
                    	BfdInfoService_MB service = null;
                    	TunnelService_MB tunnelService = null;
                    	PwInfoService_MB pwService = null;
                    	try {
                    		service = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
                    		tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
                    		pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
                    		BfdInfo bfdBefore = new BfdInfo();
                    		bfdBefore.setId(info.getId());
                    		bfdBefore.setSiteId(ConstantUtil.siteId);
                    		bfdBefore = service.selectByCondition(bfdBefore).get(0);
                    		if(bfdBefore.getLspId() > 0){
                    			bfdBefore.setLspName(getLspComboxBoxByLspId(bfdBefore.getLspId(), ConstantUtil.siteId).getTunnelName());
                    		}
                    		if(bfdBefore.getPwId() > 0){
                    			bfdBefore.setPwName(getPwComboxBoxByPwServiceId(bfdBefore.getPwId(), ConstantUtil.siteId).getPwName());
                    		}
            				DispatchUtil bfdDispatch = new DispatchUtil(RmiKeys.RMI_BFD);	
            				result = bfdDispatch.excuteUpdate(info);	
            				AddOperateLog.insertOperLog(confirm, EOperationLogType.UPDATEBFD.getValue(), result, 
            						bfdBefore, info, ConstantUtil.siteId, "bfd_ipv4", "bfd");
            			} catch (Exception e) {
            				ExceptionManage.dispose(e,this.getClass());
            			} finally {
            				UiUtil.closeService_MB(service);
            				UiUtil.closeService_MB(tunnelService);
            				UiUtil.closeService_MB(pwService);
            			}
                    }

                    DialogBoxUtil.succeedDialog(dialog, result);	
            		// 跟新界面
            		controller.refresh();	
            		// 隐藏界面
            		dialog.dispose();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
					dispose();
				}
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}

	/**
	 * 通过tunnelId获取pwName
	 * @param mepInfo
	 * @return
	 */
	private List<PwInfo> getpwName(int tunnelId) {
		PwInfoService_MB service = null;
		List<Integer> tunnelIds =null;
		List<PwInfo> pwInfoList=null;
		try {
			service = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			tunnelIds = new ArrayList<Integer>();
			tunnelIds.add(tunnelId);
			pwInfoList = service.selectPwInfoByTunnelId(tunnelIds);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			tunnelIds= null;
		}
		return pwInfoList;
	}


	public PtnButton getConfirm() {
		return confirm;
	}

	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}



	
}
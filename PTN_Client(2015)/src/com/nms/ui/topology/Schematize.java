/**   
 * 文件名：Schematize.java   
 * 创建人：kk   
 * 创建时间：2013-8-7 下午02:34:23 
 *   
 */
package com.nms.ui.topology;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import twaver.Element;
import twaver.Link;
import twaver.MovableFilter;
import twaver.Node;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.Text;
import twaver.network.TNetwork;
import com.nms.db.bean.ptn.path.ServiceInfo;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;

/**
 * 
 * 项目名称：WuHanPTN2012 类名称：Schematize 类描述：图形化显示拓扑 创建人：kk 创建时间：2013-8-7 下午02:34:23 修改人：kk 修改时间：2013-8-7 下午02:34:23 修改备注：
 * 
 * @version
 * 
 */
public class Schematize extends JPanel {

	/**
	 * @since Ver 1.1
	 */
	private static final long serialVersionUID = -3252803182449635905L;

	private TDataBox box = new TDataBox();
	private TNetwork network = new TNetwork(box);
	/**
	 * 文本字体
	 */
	private final Font TEXT_FONT = new Font(null, Font.BOLD, 12);
	/**
	 * 线的颜色
	 */
	private final Color COLOR_LINK = Color.BLACK;
	/**
	 * 业务类型
	 */
	private int serviceType = 0;
	/**
	 * 每层高度
	 */
	private final int HEIGHT = 50;
	/**
	 * 默认距离顶部高度
	 */
	private final int HEIGHTGAP = 30;
	/**
	 * 距离拓扑图左部距离
	 */
	private final int WIDTH = 200;
	/**
	 * 一条线路的长度
	 */
	private final int LINKWIDTH = 250;
	/**
	 * 两段link的间距
	 */
	private final int LINKGAP = 150;
	/**
	 * 网元的图片名称
	 */
	private final String SITEIMAGENAME = "bigne.png";
	/**
	 * 网元图标宽度
	 */
	private final int IMAGEWIDTH = 48;
	/**
	 * 网元图标高度
	 */
	private final int IMAGEHEIGHT = 44;
	/**
	 * 线路两端node图片名称
	 */
	private final String NODEIMAGENAME = "terminalPoint_image.png";

	/**
	 * 网元层计数。 用来计算层高的
	 */
	private int siteCount = 0;

	/**
	 * 计数器。记录有几层
	 */
	private int count = 1;

	/**
	 * 服务层
	 */
	private final String STRING_SERVICE = ResourceUtil.srcStr(StringKeysLbl.LBL_SERVICE_LEVEL);
	/**
	 * pw层
	 */
	private final String STRING_PW = ResourceUtil.srcStr(StringKeysLbl.LBL_PW_LEVEL);
	/**
	 * LSP层
	 */
	private final String STRING_LSP = ResourceUtil.srcStr(StringKeysLbl.LBL_LSP_LEVEL);
	/**
	 * 段层
	 */
	private final String STRING_SEGMENT = ResourceUtil.srcStr(StringKeysLbl.LBL_SEGMENT_LEVEL);
	/**
	 * LSP类型 工作
	 */
	private final String STRING_LSP_WORK = ResourceUtil.srcStr(StringKeysObj.LSP_TYPE_JOB);
	/**
	 * LSP类型 保护
	 */
	private final String STRING_LSP_PRO = ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT);

	/**
	 * 
	 * 创建一个新的实例 Schematize.
	 * 
	 */
	public Schematize() {
		this.initNetwork();
		this.setLayout();
	}

	/**
	 * 初始化NETWORK
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void initNetwork() {
		// 设置network中元素不可拖动
		network.addMovableFilter(new MovableFilter() {
			@Override
			public boolean isMovable(Element element) {
				return false;
			}
		});
	}

	/**
	 * 布局
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		layout.addLayoutComponent(this.network, c);
		this.add(this.network);

	}

	/**
	 * 绑定数据
	 * 
	 * @author kk
	 * 
	 * @param object
	 *            业务对象 tunnel pwinfo等
	 * 
	 * @Exception 异常对象
	 */
	public void initData(Object object) {

		List<PwInfo> pwinfoList = null;
		try {
			// 给业务类型赋值
			this.setServiceType(object);

			// 如果是业务 则需要在拓扑上画服务层
			if (this.serviceType == EServiceType.ELINE.getValue() || this.serviceType == EServiceType.CES.getValue() || this.serviceType == EServiceType.ETREE.getValue() || this.serviceType == EServiceType.ELAN.getValue() || this.serviceType == EServiceType.DUAL.getValue()) {
				this.drawingService(object);
			} else if (this.serviceType == EServiceType.PW.getValue()) {
				// 画PW层
				pwinfoList = new ArrayList<PwInfo>();
				pwinfoList.add((PwInfo) object);
				this.drawingPw(pwinfoList, true);
			} else if (this.serviceType == EServiceType.TUNNEL.getValue()) {
				// 画tunnel层
				this.drawingTunnel((Tunnel) object, true, this.STRING_LSP_WORK);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 画服务层
	 * 
	 * @author kk
	 * 
	 * @param object
	 *            业务对象 tunnel pwinfo等
	 * 
	 * @Exception 异常对象
	 */
	private void drawingService(Object object) throws Exception {

		List<Integer> pwIdList = new ArrayList<Integer>();
		ElineInfo elineInfo = null;
		CesInfo cesInfo = null;
		boolean flag = false; // 画pw层时。是否画网元层
		List<PwInfo> pwInfoList = null;
		try {
			// 根据业务类型不同。获取不同的pwid集合
			if (this.serviceType == EServiceType.ELINE.getValue()) {
				elineInfo = (ElineInfo) object;
				pwIdList.add(elineInfo.getPwId());
			} else if (this.serviceType == EServiceType.CES.getValue()) {
				cesInfo = (CesInfo) object;
				pwIdList.add(cesInfo.getPwId());
			} else if (this.serviceType == EServiceType.ETREE.getValue()) {
				pwIdList = this.getPwIdByEtree((EtreeInfo) object);
				flag = true;
			} else if (this.serviceType == EServiceType.ELAN.getValue()) {
				pwIdList = this.getPwIdByElan((ElanInfo) object);
				flag = true;
			}else if (this.serviceType == EServiceType.DUAL.getValue()) {
				pwIdList = this.getPwIdByDual((DualInfo) object);
				flag = true;
			}
			pwInfoList = this.getPwInfoList(pwIdList);
			// 如果pwidList的size为1 说明业务类型是ces或者eline 就先画网元层
			if (pwIdList.size() == 1) {
				this.drawingSiteByPw(pwInfoList);
			}
			// 根据业务画服务层
			this.createServcieLink(object);

			// // 转换成pwinfoList对象 然后开始画pw层
			this.drawingPw(pwInfoList, flag);

		} catch (Exception e) {
			throw e;
		} finally {
			pwIdList = null;
			elineInfo = null;
			cesInfo = null;
			pwInfoList = null;
		}
	}

	/**
	 * 创建服务层的线路
	 * 
	 * @author kk
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void createServcieLink(Object object) throws Exception {
		ElineInfo elineInfo = null;
		CesInfo cesInfo = null;
		String nodeName_a = null;
		String nodeName_z = null;
		List<Tunnel> tunnels = null;
		List<ServiceInfo> serviceInfoList = null;
		List<ElanInfo> elanInfoList = null;
		List<EtreeInfo> etreeInfoList = null;
		List<DualInfo> dualInfoList = null;
		CesInfoService_MB cesInfoService=null;
		SiteService_MB siteService = null;
		UiUtil uiUtil = new UiUtil();
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			cesInfoService=(CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
			// 如果是eline或者ces 画一条直线 否则画环形或者tree
			if (this.serviceType == EServiceType.ELINE.getValue() || this.serviceType == EServiceType.CES.getValue()) {
				if (this.serviceType == EServiceType.ELINE.getValue()) {
					elineInfo = (ElineInfo) object;
					tunnels = this.getTunnelByPwId(elineInfo.getPwId());
					nodeName_a = siteService.getSiteName(elineInfo.getaSiteId()) + "/" + this.getPortAndAcName(elineInfo.getaAcId());
					nodeName_z = siteService.getSiteName(elineInfo.getzSiteId()) + "/" + this.getPortAndAcName(elineInfo.getzAcId());
				} else {
					cesInfo = (CesInfo) object;
					tunnels = this.getTunnelByPwId(cesInfo.getPwId());
					nodeName_a = siteService.getSiteName(cesInfo.getaSiteId()) + "/" + cesInfoService.getCesPortName(cesInfo, "a");
					nodeName_z = siteService.getSiteName(cesInfo.getzSiteId()) + "/" + cesInfoService.getCesPortName(cesInfo, "z");
				}

				this.createLongLink(tunnels.get(0), nodeName_a, nodeName_z, this.STRING_SERVICE);
			} else {
				serviceInfoList = new ArrayList<ServiceInfo>();
				// 画环形的或者etree的link
				if (this.serviceType == EServiceType.ELAN.getValue()) {
					elanInfoList = this.getElanInfoList((ElanInfo) object);
					for (ElanInfo elanInfo : elanInfoList) {
						serviceInfoList.add(elanInfo);
					}
				} else if(this.serviceType == EServiceType.ETREE.getValue()){
					etreeInfoList = this.getEtreeInfoList((EtreeInfo) object);
					for (EtreeInfo etreeInfo : etreeInfoList) {
						serviceInfoList.add(etreeInfo);
					}
				}else if(this.serviceType == EServiceType.DUAL.getValue()){
					dualInfoList = this.getDualInfoList((DualInfo) object);
					for (DualInfo dualInfo : dualInfoList) {
						serviceInfoList.add(dualInfo);
					}
				}
				this.createLoopLink(serviceInfoList);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(cesInfoService);
			UiUtil.closeService_MB(siteService);
			uiUtil = null;
		}
	}

	/**
	 * 创建环的link
	 * 
	 * @author kk
	 * @throws Exception
	 * @Exception 异常对象
	 */
	private void createLoopLink(List<ServiceInfo> serviceInfoList) throws Exception {
		int y = 0;
		Map<Integer, Node> siteIdAndNodeMap = null;
		Node node_from = null;
		Node node_to = null;
		EtreeInfo etreeInfo = null;
		ElanInfo elanInfo = null;
		DualInfo dualInfo = null;
		try {
			y = this.getTopoY();

			// 创建文本
			this.createText(y, this.STRING_SERVICE);
			// 获取以网元ID和node的map集合
			siteIdAndNodeMap = this.getNodeMap(serviceInfoList);

			for (ServiceInfo serviceInfo : serviceInfoList) {
				if (serviceInfo instanceof EtreeInfo) {
					etreeInfo = (EtreeInfo) serviceInfo;
					node_from = siteIdAndNodeMap.get(etreeInfo.getRootSite());
					node_to = siteIdAndNodeMap.get(etreeInfo.getBranchSite());
				} else if(serviceInfo instanceof ElanInfo){
					elanInfo = (ElanInfo) serviceInfo;
					node_from = siteIdAndNodeMap.get(elanInfo.getaSiteId());
					node_to = siteIdAndNodeMap.get(elanInfo.getzSiteId());
				}else if(serviceInfo instanceof DualInfo){
					dualInfo = (DualInfo) serviceInfo;
					node_from = siteIdAndNodeMap.get(dualInfo.getRootSite());
					if(dualInfo.getBranchMainSite()>0){
						node_to = siteIdAndNodeMap.get(dualInfo.getBranchMainSite());
					}else if(dualInfo.getBranchProtectSite()>0){
						node_to = siteIdAndNodeMap.get(dualInfo.getBranchProtectSite());
					}
					
				}
				if(null != node_from && null != node_to){
					this.createLink(node_from, node_to,true);
				}
			}

			// 服务层占了一个高度 圆的直径占了4个高度。
			this.count += 5;
		} catch (Exception e) {
			throw e;
		} finally{
			siteIdAndNodeMap = null;
			node_from = null;
			node_to = null;
			etreeInfo = null;
			elanInfo = null;
		}

	}

	/**
	 * 遍历elan或etree 根据网元ID算出各个node的xy坐标。显示到拓扑中
	 * 
	 * @author kk
	 * @return 以网元ID为KYE node为value的map集合、画link用
	 * @throws Exception
	 * @Exception 异常对象
	 */
	private Map<Integer, Node> getNodeMap(List<ServiceInfo> serviceInfoList) throws Exception {
		EtreeInfo etreeInfo = null;
		ElanInfo elanInfo = null;
		DualInfo dualInfo = null;
//		Map<Integer, Integer> siteAndAcMap = new HashMap<Integer, Integer>();
		Map<Integer, Set<Integer>> siteAndAcMap = new HashMap<Integer, Set<Integer>>();
		Set<Integer> acIdSet = null;
		UiUtil uiUtil = new UiUtil();
		try {
			for (ServiceInfo serviceInfo : serviceInfoList) {
				if (serviceInfo instanceof EtreeInfo) {
					etreeInfo = (EtreeInfo) serviceInfo;
					// 把根网元和叶子网元去重后添加到集合中
					if (null == siteAndAcMap.get(etreeInfo.getRootSite())) {
//						siteAndAcMap.put(etreeInfo.getRootSite(), etreeInfo.getaAcId());
						siteAndAcMap.put(etreeInfo.getRootSite(), uiUtil.getAcIdSets(etreeInfo.getAmostAcId()));
					}
					if (null == siteAndAcMap.get(etreeInfo.getBranchSite())) {
//						siteAndAcMap.put(etreeInfo.getBranchSite(), etreeInfo.getzAcId());
						siteAndAcMap.put(etreeInfo.getBranchSite(), uiUtil.getAcIdSets(etreeInfo.getZmostAcId()));
					}
				} else if(serviceInfo instanceof ElanInfo){
					elanInfo = (ElanInfo) serviceInfo;
					// 把根A和Z网元去重后添加到集合中
					if (null == siteAndAcMap.get(elanInfo.getaSiteId())) {
//						siteAndAcMap.put(elanInfo.getaSiteId(), elanInfo.getaAcId());
						siteAndAcMap.put(elanInfo.getaSiteId(), uiUtil.getAcIdSets(elanInfo.getAmostAcId()));
					}
					if (null == siteAndAcMap.get(elanInfo.getzSiteId())) {
//						siteAndAcMap.put(elanInfo.getzSiteId(), elanInfo.getzAcId());
						siteAndAcMap.put(elanInfo.getzSiteId(), uiUtil.getAcIdSets(elanInfo.getZmostAcId()));
					}
				}else if(serviceInfo instanceof DualInfo){
					dualInfo = (DualInfo) serviceInfo;
					// 把根A和Z网元去重后添加到集合中
					if (null == siteAndAcMap.get(dualInfo.getRootSite())) {
						acIdSet = new HashSet<Integer>();
						acIdSet.add(dualInfo.getaAcId());
//						siteAndAcMap.put(dualInfo.getRootSite(), dualInfo.getaAcId());
						siteAndAcMap.put(dualInfo.getRootSite(), acIdSet);
					}
					if(dualInfo.getBranchMainSite()>0){
						if (null == siteAndAcMap.get(dualInfo.getBranchMainSite())) {
							acIdSet = new HashSet<Integer>();
							acIdSet.add(dualInfo.getzAcId());
//							siteAndAcMap.put(dualInfo.getBranchMainSite(), dualInfo.getzAcId());
							siteAndAcMap.put(dualInfo.getBranchMainSite(), acIdSet);
						}
					}
					if(dualInfo.getBranchProtectSite()>0){
						acIdSet = new HashSet<Integer>();
						acIdSet.add(dualInfo.getzAcId());
						if (null == siteAndAcMap.get(dualInfo.getBranchProtectSite())) {
//							siteAndAcMap.put(dualInfo.getBranchProtectSite(), dualInfo.getzAcId());
							siteAndAcMap.put(dualInfo.getBranchProtectSite(), acIdSet);
						}
					}
				}
			}
			return this.getSiteNodeMap(siteAndAcMap);
		} catch (Exception e) {
			throw e;
		}finally
		{
			acIdSet = null;
			uiUtil = null;
			etreeInfo = null;
			elanInfo = null;
			dualInfo = null;
			siteAndAcMap = null;
		}

	}

	/**
	 * 根据ACID获取port名称加ac名称。 显示用
	 * 
	 * @author kk
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private String getPortAndAcName(int acId) throws Exception {
		AcPortInfoService_MB acInfoService = null;
		AcPortInfo acPortInfo = new AcPortInfo();
		List<AcPortInfo> acPortInfoList = null;
		String result = "";
		PortLagService_MB portLagService = null;
		PortService_MB portService = null;
		try {
			// 根据主键从数据库中查询AC对象
			portLagService=(PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
			portService=(PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acPortInfo.setId(acId);
			acPortInfoList = acInfoService.selectByCondition(acPortInfo);

			if (acPortInfoList.size() == 1) {
				acPortInfo = acPortInfoList.get(0);
				// 如果portID有值。 说明此AC是端口下。 否则是lag端口下
				if (acPortInfo.getPortId() > 0) {
					result = portService.getPortname(acPortInfo.getPortId()) + "/" + acPortInfo.getName();
				} else {
					result = portLagService.getLagName(acPortInfo.getLagId()) + "/" + acPortInfo.getName();
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(portLagService);
		}
		return result;
	}

	/**
	 * 根据ACID获取port名称加ac名称。 显示用
	 * 
	 * @author kk
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private String getPortAndAcName(Set<Integer> acIdSet) throws Exception {
		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acPortInfoList = null;
		String result = "";
		PortLagService_MB portLagService = null;
		PortService_MB portService = null;
		try {
			if(null != acIdSet && !acIdSet.isEmpty())
			{
				// 根据主键从数据库中查询AC对象
				portLagService=(PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
				portService=(PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
				acPortInfoList = acInfoService.select(new ArrayList<Integer>(acIdSet));

				if (null != acPortInfoList && !acPortInfoList.isEmpty()) 
				{
					for(AcPortInfo acPortInfo : acPortInfoList)
					{
						// 如果portID有值。 说明此AC是端口下。 否则是lag端口下
						if (acPortInfo.getPortId() > 0) {
							result += portService.getPortname(acPortInfo.getPortId()) + "/" + acPortInfo.getName()+";";
						} else {
							result += portLagService.getLagName(acPortInfo.getLagId()) + "/" + acPortInfo.getName()+";";
						}	
					}
				}	
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(portLagService);
		}
		return result;
	}
	
	/**
	 * 根据pw主键查询tunnel对象
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void drawingSite(int pwId) throws Exception {
		List<Tunnel> tunnels = null;
		try {
			tunnels = this.getTunnelByPwId(pwId);
			for(Tunnel tunnel: tunnels){
				this.createSite(tunnel);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			tunnels = null;
		}
	}

	/**
	 * 根据PWID查询tunnel对象
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private List<Tunnel> getTunnelByPwId(int pwId) throws Exception {

		PwInfoService_MB pwInfoService = null;
		PwInfo pwinfo = new PwInfo();
		TunnelService_MB tunnelService = null;
		Tunnel tunnel = new Tunnel();
		List<Tunnel> tunnelList = new ArrayList<Tunnel>();

		try {
			// 通过pwID查询pw对象
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwinfo.setPwId(pwId);
			pwinfo = pwInfoService.selectBypwid_notjoin(pwinfo);

			// 通过pw中的tunnelID查询tunnel对象
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			if(pwinfo.getTunnelId() != 0){
				tunnel.setTunnelId(pwinfo.getTunnelId());
				tunnelList = tunnelService.select_nojoin(tunnel);
			}else{
				for(MsPwInfo msPwInfo : pwinfo.getMsPwInfos()){
					tunnel.setTunnelId(msPwInfo.getFrontTunnelId());
					List<Tunnel> listFront = tunnelService.select_nojoin(tunnel);
					if(!tunnelList.containsAll(listFront)){
						tunnelList.addAll(listFront);
					}
					tunnel.setTunnelId(msPwInfo.getBackTunnelId());
					List<Tunnel> listBack = tunnelService.select_nojoin(tunnel);
					if(!tunnelList.containsAll(listBack)){
						tunnelList.addAll(listBack);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(pwInfoService);
			pwinfo = null;
		}
		return tunnelList;
	}

	/**
	 * 根据tunnel对象 把网元画到拓扑中
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void createSite(Tunnel tunnel) throws Exception {
		SiteService_MB siteService = null;
		List<Lsp> lspList = null;
		int x = 0;
		int y = 0;
		try {
		    siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			y = this.getTopoY();

			lspList = tunnel.getLspParticularList();

			for (int i = 0; i < lspList.size(); i++) {
				x = this.getTopoX(i, true);
				this.createNode(x, y, siteService.getSiteName(lspList.get(i).getASiteId()), this.SITEIMAGENAME);
				this.createNode(x + this.LINKWIDTH, y, siteService.getSiteName(lspList.get(i).getZSiteId()), this.SITEIMAGENAME);
			}

			this.count++;
			this.siteCount++;
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 在拓扑图上创建一个node
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private Node createNode(int x, int y, String name, String imageName) throws Exception {
		Node node = null;
		try {
			node = new Node();
			if (x != 0 && y != 0) {
				node.setLocation(x, y);
			}
			node.setName(name);
			node.setImage(getClass().getResource("/com/nms/ui/images/topo/" + imageName).toString());
			this.box.addElement(node);
		} catch (Exception e) {
			throw e;
		} finally {
		}
		return node;
	}

	/**
	 * 画pw层
	 * 
	 * @author kk
	 * 
	 * @param pwinfoList
	 *            pw对象集合 可批量画pw
	 * @param isDrawingSite
	 *            画pw层前 是否画pw下所有网元
	 * @Exception 异常对象
	 */
	private void drawingPw(List<PwInfo> pwinfoList, boolean isDrawingSite) throws Exception {

		try {

			for (PwInfo pwInfo : pwinfoList) {

				// 如果为true 要先画网元 再画pw线路
				if (isDrawingSite) {
					List<PwInfo> pwList = new ArrayList<PwInfo>();
					pwList.add(pwInfo);
					this.drawingSiteByPw(pwList);
				}

				// 画pw线路
				this.createPwLink(pwInfo);
			}

		} catch (Exception e) {
			throw e;
		}

	}

	private void drawingSiteByPw(List<PwInfo> pwInfoList) throws Exception {
		SiteService_MB siteService = null;
		int x = 0;
		int y = 0;
		try {
		    siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			y = this.getTopoY();
			for (int i = 0; i < pwInfoList.size(); i++) {
				x = this.getTopoX(i, true);
				this.createNode(x, y, siteService.getSiteName(pwInfoList.get(i).getASiteId()), this.SITEIMAGENAME);
				this.createNode(x + this.LINKWIDTH, y, siteService.getSiteName(pwInfoList.get(i).getZSiteId()), this.SITEIMAGENAME);
			}
			this.count++;
			this.siteCount++;
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}
	

	/**
	 * 画tunnel层
	 * 
	 * @author kk
	 * 
	 * @param tunnel
	 *            tunnel对象
	 * @param isDrawingSite
	 *            画tunnel层前 是否画tunnel下所有网元
	 * @Exception 异常对象
	 */
	private void drawingTunnel(Tunnel tunnel, boolean isDrawingSite, String type) throws Exception {

		try {

			// 如果为true 先画此tunnel下的所有网元
			if (isDrawingSite) {
				this.createSite(tunnel);
			}
			// 画此tunnel下所有线路
			this.createTunnelLink(tunnel, type);

			// 如果此tunnel有保护tunnel 调用本方法 把保护tunnel的 所有线路画出来
			if (!"0".equals(tunnel.getTunnelType()) && ("2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue()) || "3".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue()))) {
				this.drawingTunnel(tunnel.getProtectTunnel(), true, this.STRING_LSP_PRO);
			}

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 创建tunnellink type表示工作还是保护
	 * 
	 * @author kk
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void createTunnelLink(Tunnel tunnel, String type) throws Exception {
		int y = 0;
		int x = 0;
		Lsp lsp = null;
		Node node_from = null;
		Node node_to = null;
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			// 计算高度
			y = this.getTopoY();

			// 创建文本
			this.createText(y, this.STRING_LSP + "(" + type + ")");

			// 遍历lsp 创建lsp层路径
			for (int i = 0; i < tunnel.getLspParticularList().size(); i++) {
				lsp = tunnel.getLspParticularList().get(i);
				x = this.getTopoX(i, false);
				// 创建node
				node_from = this.createNode(x, y, siteService.getSiteName(lsp.getASiteId()) + "/tunnel/" + lsp.getAtunnelbusinessid() + "/" + lsp.getFrontLabelValue(), this.NODEIMAGENAME);
				node_to = this.createNode(x + this.LINKWIDTH, y, siteService.getSiteName(lsp.getZSiteId()) + "/tunnel/" + lsp.getZtunnelbusinessid() + "/" + lsp.getBackLabelValue(), this.NODEIMAGENAME);
				// 连接两node之间的link
				this.createLink(node_from, node_to,false);
			}

			this.count++;

			// 画段层
			this.createSegmentLink(tunnel);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}

	}

	/**
	 * 创建段层link
	 * 
	 * @author kk
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void createSegmentLink(Tunnel tunnel) throws Exception {
		int y = 0;
		int x = 0;
		Lsp lsp = null;
		Node node_from = null;
		Node node_to = null;
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			// 计算高度
			y = this.getTopoY();

			// 创建文本
			this.createText(y, this.STRING_SEGMENT);

			// 遍历lsp 创建lsp层路径
			for (int i = 0; i < tunnel.getLspParticularList().size(); i++) {
				lsp = tunnel.getLspParticularList().get(i);
				x = this.getTopoX(i, false);
				// 创建node
				node_from = this.createNode(x, y, siteService.getSiteName(lsp.getASiteId()) + "/" + portService.getPortname(lsp.getAPortId()), this.NODEIMAGENAME);
				node_to = this.createNode(x + this.LINKWIDTH, y, siteService.getSiteName(lsp.getZSiteId()) + "/" + portService.getPortname(lsp.getZPortId()), this.NODEIMAGENAME);
				// 连接两node之间的link
				this.createLink(node_from, node_to,false);
			}

			this.count += 2;

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(siteService);
			lsp = null;
			node_from = null;
			node_to = null;
		}

	}

	/**
	 * 创建link
	 * 
	 * @author kk
	 * @param isDotted 是否是虚线
	 * @Exception 异常对象
	 */
	private void createLink(Node node_from, Node node_to,boolean isDotted) {
		Link link = new Link();
		link.setFrom(node_from);
		link.setTo(node_to);
		link.putLinkColor(this.COLOR_LINK);
		link.putLinkWidth(2);
		if(isDotted){
			link.putClientProperty(TWaverConst.PROPERTYNAME_LINK_STYLE, TWaverConst.LINK_STYLE_DASH_DOT);
		}
		this.box.addElement(link);
	}

	/**
	 * 在拓扑图上创建文本显示控件
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void createText(int y, String textStr) throws Exception {
		Text text = null;
		try {
			text = new Text();
			text.setName(textStr);
			text.putLabelFont(this.TEXT_FONT);
			text.setLocation(80, y);
			this.box.addElement(text);
		} catch (Exception e) {
			throw e;
		} finally {
			text = null;
		}
	}

	/**
	 * 根据pw对象 创建pw的线路图
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void createPwLink(PwInfo pwinfo) throws Exception {
		List<Tunnel> tunnels = null;
		String nodeName_a = null;
		String nodeName_z = null;
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			// 查询此pw下的tunnel对象
			tunnels = this.getTunnelByPwId(pwinfo.getPwId());
			nodeName_a = siteService.getSiteName(pwinfo.getASiteId()) + "/" + pwinfo.getType().toString() + "/" + pwinfo.getApwServiceId() + "/" + pwinfo.getOutlabelValue();
			nodeName_z = siteService.getSiteName(pwinfo.getZSiteId()) + "/" + pwinfo.getType().toString() + "/" + pwinfo.getZpwServiceId() + "/" + pwinfo.getInlabelValue();

			this.createLongLink(tunnels.get(0), nodeName_a, nodeName_z, this.STRING_PW);

			// 画tunnel线路
			for(Tunnel tunnel : tunnels){
				this.drawingTunnel(tunnel, false, this.STRING_LSP_WORK);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}

	}

	/**
	 * 创建服务层和pw层的长线
	 * 
	 * @author kk
	 * @Exception 异常对象
	 */
	private void createLongLink(Tunnel tunnel, String nodeName_a, String nodeName_z, String textName) throws Exception {
		Node node_from = null;
		Node node_to = null;
		int y = 0;
		int x_from = 0;
		int x_to = 0;
		try {
			// 计算高度
			y = this.getTopoY();
			// 计算A网元的x坐标
			x_from = this.getTopoX(0, false);
			// 计算Z网元的x坐标
			x_to = this.getTopoX(tunnel.getLspParticularList().size(), false) - this.LINKGAP;

			// 创建文本
			this.createText(this.getTopoY(), textName);

			// 创建node及link
			node_from = this.createNode(x_from, y, nodeName_a, this.NODEIMAGENAME);
			node_to = this.createNode(x_to, y, nodeName_z, this.NODEIMAGENAME);
			this.createLink(node_from, node_to,false);

			// 加计数器
			this.count++;
		} catch (Exception e) {
			throw e;
		} finally {
			node_from = null;
			node_to = null;
		}

	}

	/**
	 * 根据pwID集合查询出所有pw对象
	 * 
	 * @author kk
	 * @Exception 异常对象
	 */
	private List<PwInfo> getPwInfoList(List<Integer> pwIdList) throws Exception {
		PwInfoService_MB pwInfoService = null;
		PwInfo pwInfo = null;
		List<PwInfo> pwInfoList = new ArrayList<PwInfo>();
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);

			// 遍历pwID集合。查询pw对象
			for (int pwId : pwIdList) {
				pwInfo = new PwInfo();
				pwInfo.setPwId(pwId);

				pwInfoList.add(pwInfoService.selectBypwid_notjoin(pwInfo));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(pwInfoService);
		}

		return pwInfoList;
	}

	/**
	 * 根据etree对象获取此etree下的所有pwid
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private List<Integer> getPwIdByEtree(EtreeInfo etreeInfo) throws Exception {

		List<EtreeInfo> etreeInfoList = null;
		List<Integer> pwIdList = new ArrayList<Integer>();
		try {
			etreeInfoList = this.getEtreeInfoList(etreeInfo);

			for (EtreeInfo etreeInfoSelect : etreeInfoList) {
				pwIdList.add(etreeInfoSelect.getPwId());
			}

		} catch (Exception e) {
			throw e;
		} finally {
			etreeInfoList = null;
		}

		return pwIdList;
	}

	/**
	 * 从数据库查询etree集合
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private List<EtreeInfo> getEtreeInfoList(EtreeInfo etreeInfo) throws Exception {

		EtreeInfoService_MB etreeService = null;

		try {
			etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			return etreeService.select(etreeInfo);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(etreeService);
		}
	}

	/**
	 * 根据elan对象获取此elan下的所有pwid
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private List<Integer> getPwIdByElan(ElanInfo elanInfo) throws Exception {

		List<ElanInfo> elanInfoList = null;
		List<Integer> pwIdList = new ArrayList<Integer>();
		try {
			elanInfoList = this.getElanInfoList(elanInfo);

			for (ElanInfo elanInfo_select : elanInfoList) {
				pwIdList.add(elanInfo_select.getPwId());
			}

		} catch (Exception e) {
			throw e;
		} finally {
			elanInfoList = null;
		}

		return pwIdList;
	}

	/**
	 * 从数据库查询elan集合
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private List<ElanInfo> getElanInfoList(ElanInfo elanInfo) throws Exception {

		ElanInfoService_MB elanInfoService = null;

		try {
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			return elanInfoService.select(elanInfo);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(elanInfoService);
		}
	}

	/**
	 * 给业务类型赋值
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void setServiceType(Object object) {
		if (object instanceof Tunnel) {
			this.serviceType = EServiceType.TUNNEL.getValue();
		} else if (object instanceof PwInfo) {
			this.serviceType = EServiceType.PW.getValue();
		} else if (object instanceof ElineInfo) {
			this.serviceType = EServiceType.ELINE.getValue();
		} else if (object instanceof CesInfo) {
			this.serviceType = EServiceType.CES.getValue();
		} else if (object instanceof EtreeInfo) {
			this.serviceType = EServiceType.ETREE.getValue();
		} else if (object instanceof ElanInfo) {
			this.serviceType = EServiceType.ELAN.getValue();
		} else if (object instanceof DualInfo) {
			this.serviceType = EServiceType.DUAL.getValue();
		}
	}

	/**
	 * 计算y轴坐标
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private int getTopoY() {

		if (this.count == 1) {
			return this.HEIGHTGAP;
		} else {
			return this.HEIGHTGAP + this.IMAGEHEIGHT * this.siteCount + this.HEIGHT * (this.count - 1);
		}

	}

	/**
	 * 计算X轴坐标
	 * 
	 * @author kk
	 * @param isSite
	 *            是否是网元层。 如果不是。 就把图片的宽度加上。 使线路起点和终点显示在网元中间
	 * @Exception 异常对象
	 */
	private int getTopoX(int i, boolean isSite) {
		if (isSite) {
			return this.WIDTH + (this.LINKWIDTH + this.LINKGAP) * i;
		} else {
			return this.WIDTH + this.IMAGEWIDTH / 2 + (this.LINKWIDTH + this.LINKGAP) * i;
		}
	}

	/**
	 * 清空数据
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	public void clear() {
		this.box.clear();
		this.count = 1;
		this.siteCount = 0;
	}

	/**
	 * 获取以siteid为key node为value的map对象。并且把site创建在拓扑中
	 * 
	 * @author kk
	 * @Exception 异常对象
	 */
	private Map<Integer, Node> getSiteNodeMap(Map<Integer, Set<Integer>> siteAndAcMap) throws Exception {
		int startx = 0; // 起始X坐标
		int starty = 0; // 起始Y坐标
		int radius = this.HEIGHT * 2; // 圆形半径 等于每层的高度。 也就是占用了两个高度。count需要+2
		int node_x = 0; // node的X坐标
		int node_y = 0; // node的Y坐标
		int siteSize = 0;
		Map<Integer, Node> siteAndNodeMap = new HashMap<Integer, Node>(); // 返回值
		int siteId = 0;
		Node node = null;
		String nodeName = null; // node显示名称
		SiteService_MB siteService = null;
		try {
		    siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			startx = (this.getTopoX(1, false)) / 2;
			starty = this.getTopoY();
			siteSize = siteAndAcMap.keySet().size(); // 网元个数

			for (int i = 0; i < siteSize; i++) {
				// 如果i=0 node的坐标从起始点开始。 也就是x和y变量
				if (i == 0) {
					siteId = Integer.parseInt(siteAndAcMap.keySet().toArray()[0].toString());
					node_x = startx;
					node_y = starty;
				} else {
					siteId = Integer.parseInt(siteAndAcMap.keySet().toArray()[i].toString());
					node_x = (int) (startx - radius * Math.sin(((2 * Math.PI) / siteSize) * i));
					node_y = (int) (starty + radius - radius * Math.cos(((2 * Math.PI) / siteSize) * i));
				}
				// 取node名称
				nodeName = siteService.getSiteName(siteId) + "/" + this.getPortAndAcName(siteAndAcMap.get(siteId));
				node = this.createNode(node_x, node_y, nodeName, this.NODEIMAGENAME);
				siteAndNodeMap.put(siteId, node);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}
		return siteAndNodeMap;
	}
	
	/**
	 * 根据dual对象获取此dual下的所有pwid
	 * 
	 * 
	 * @Exception 异常对象
	 */
	private List<Integer> getPwIdByDual(DualInfo dualInfo) throws Exception {

		List<DualInfo> dualInfoList = null;
		List<Integer> pwIdList = new ArrayList<Integer>();
		try {
			dualInfoList = this.getDualInfoList(dualInfo);
			for (DualInfo dualInfo_select : dualInfoList) {
				pwIdList.add(dualInfo_select.getPwId());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dualInfoList = null;
		}

		return pwIdList;
	}
	
	/**
	 * 从数据库查询dual集合
	 * 
	 * 
	 * @Exception 异常对象
	 */
	private List<DualInfo> getDualInfoList(DualInfo dualInfo) throws Exception {

		DualInfoService_MB dualInfoService = null;

		try {
			dualInfoService = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
			return dualInfoService.select(dualInfo);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(dualInfoService);
		}
	}
	
}
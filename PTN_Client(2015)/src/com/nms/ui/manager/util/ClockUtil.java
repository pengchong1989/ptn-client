﻿package com.nms.ui.manager.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;

/**
 * 时钟模块辅助类
 * @author kk
 *
 */
public class ClockUtil {

	/**
	 * 优先级排列
	 */
	public Map<String, String> getClockPRIList(int siteId) {
		Map<String, String> clockPRIMap = new LinkedHashMap<String, String>();
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			siteService =(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setSiteId(siteId);
			List<PortInst> portInsts = portService.select(portInst);
			String siteType = siteService.getSiteType(siteId);
			clockPRIMap.put("0", ResourceUtil.srcStr(StringKeysLbl.OUTER_CLOCK));
			if (portInsts.size() > 0) {
				for (PortInst inst : portInsts) {
					if (inst.getNumber() > 0 && (inst.getPortName().contains("ge") || inst.getPortName().contains("xg"))) {
						clockPRIMap.put(inst.getNumber() + "", inst.getPortName());
					}
					if("710A".equals(siteType) || "710".equals(siteType)){
						if(inst.getNumber() > 0 && inst.getPortName().contains("e1")){
							clockPRIMap.put(inst.getNumber() + "", inst.getPortName().substring(0, 6));
						}
					}
					if("703-5".equals(siteType) || "703-6".equals(siteType)){
						if(inst.getNumber() > 0 && (inst.getPortName().contains("fe.1.1") || 
								inst.getPortName().contains("fe.1.2"))){
							clockPRIMap.put(inst.getNumber() + "", inst.getPortName());
						}
					}
					if("703-7".equals(siteType)){
						if(inst.getNumber() > 0 && (inst.getPortName().contains("fe.1.1"))){
							clockPRIMap.put(inst.getNumber() + "", inst.getPortName());
						}
					}
				}
			}
			if ("703A".equals(siteType)) {
				clockPRIMap.put(5 + "", "E1");
//			} else if ("703B".equals(siteType) || "703B2".equals(siteType) || "703-2A".equals(siteType)
//					 || "703-1A".equals(siteType) || "703-4A".equals(siteType)) {
//				if (portInsts.size() > 0) {
//					for (PortInst inst : portInsts) {
//						if (inst.getNumber() > 0 && inst.getPortName().contains("fe")) {
//							clockPRIMap.put(inst.getNumber() + "", inst.getPortName());
//						}
//					}
//				}
			}else if("703-2".equals(siteType)){
				clockPRIMap.put(3 + "", "E1");
			}else if("703-4".equals(siteType)){
				clockPRIMap.put(2 + "", "E1");
			}else if("703-6".equals(siteType)){
				clockPRIMap.put(3 + "", "E1");
			}else if("703B".equals(siteType) || "703B2".equals(siteType) || "703-2A".equals(siteType)){
				clockPRIMap.put(5 + "", "E1");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
		}
		return clockPRIMap;
	}

	/**
	 * 输出时钟选择
	 */
	public Map<String, String> getOutSelectList(int siteId) {
		Map<String, String> outSelectMap = new LinkedHashMap<String, String>();
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setSiteId(siteId);
			List<PortInst> portInsts = portService.select(portInst);
			String siteType = siteService.getSiteType(siteId);
			outSelectMap.put("0", ResourceUtil.srcStr(StringKeysLbl.LBL_PLL_LOCKED));
			outSelectMap.put("1", ResourceUtil.srcStr(StringKeysLbl.OUTER_CLOCK));
			if (portInsts.size() > 0) {
				for (PortInst inst : portInsts) {
					if (inst.getNumber() > 0 && (inst.getPortName().contains("ge") || inst.getPortName().contains("xg"))) {
						outSelectMap.put((inst.getNumber()+1) + "", inst.getPortName());
					}
					if("710A".equals(siteType) || "710".equals(siteType)){
						if(inst.getNumber() > 0 && inst.getPortName().contains("e1")){
							outSelectMap.put((inst.getNumber()+1) + "", inst.getPortName().substring(0, 6));
						}
					}
					if("703-5".equals(siteType) || "703-6".equals(siteType)){
						if(inst.getNumber() > 0 && (inst.getPortName().contains("fe.1.1") || 
								inst.getPortName().contains("fe.1.2"))){
							outSelectMap.put((inst.getNumber()+1) + "", inst.getPortName());
						}
					}
					if("703-7".equals(siteType)){
						if(inst.getNumber() > 0 && (inst.getPortName().contains("fe.1.1"))){
							outSelectMap.put((inst.getNumber()+1) + "", inst.getPortName());
						}
					}
				}
			}
			if ("703A".equals(siteType)) {
				outSelectMap.put(6 + "", "E1");
//			} else if ("703B".equals(siteType) || "703B2".equals(siteType) || "703-2A".equals(siteType)
//					 	|| "703-1A".equals(siteType) || "703-4A".equals(siteType)) {
//				if (portInsts.size() > 0) {
//					for (PortInst inst : portInsts) {
//						if (inst.getNumber() > 0 && inst.getPortName().contains("fe")) {
//							outSelectMap.put((inst.getNumber()+1) + "", inst.getPortName());
//						}
//					}
//				}
			}else if("703-2".equals(siteType)){
				outSelectMap.put(4 + "", "E1");
			}else if("703-4".equals(siteType)){
				outSelectMap.put(3 + "", "E1");
			}else if("703-6".equals(siteType)){
				outSelectMap.put(4 + "", "E1");
			}else if("703B".equals(siteType) || "703B2".equals(siteType) || "703-2A".equals(siteType) ||
					"703-2".equals(siteType) || "703-4".equals(siteType) || "703-6".equals(siteType) ){
				outSelectMap.put(6 + "", "E1");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
		}
		return outSelectMap;
	}
	
	/**
	 * 获取时钟等待恢复时间端口
	 * 
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public List<PortInst> getFrequencyPorts(int siteId) {
		List<PortInst> addPortInsts = new ArrayList<PortInst>();
		PortService_MB portService = null;
		SiteService_MB siteService = null;
		String siteType = null;
		List<Integer> portNumber = new ArrayList<Integer>();
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteType = siteService.getSiteType(siteId);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setSiteId(siteId);
			List<PortInst> portInsts = portService.select(portInst);
			if (portInsts != null && portInsts.size() > 0) {
				for (PortInst inst : portInsts) {
					if (inst.getNumber() > 0 && (inst.getPortName().contains("ge") || inst.getPortName().contains("xg"))) {
						addPortInsts.add(inst);
					}
					if("710A".equals(siteType) || "710".equals(siteType)){
						if(inst.getNumber() > 0 && inst.getPortName().contains("e1")){
							if(!portNumber.contains(inst.getNumber())){
								portNumber.add(inst.getNumber());
								inst.setPortName(inst.getPortName().substring(0, 6));
								addPortInsts.add(inst);
							}
						}
					}else if("710B".equals(siteType)){
						if(inst.getNumber() > 0 && inst.getNumber() != 30 && inst.getPortName().contains("e1")){
							if(!portNumber.contains(inst.getNumber())){
								portNumber.add(inst.getNumber());
								inst.setPortName(inst.getPortName().substring(0, 6));
								addPortInsts.add(inst);
							}
						}
					}else{
						if(inst.getNumber() > 0 && inst.getPortName().contains("e1.1.1.1")){
							if(!portNumber.contains(inst.getNumber())){
								portNumber.add(inst.getNumber());
								inst.setPortName("E1");
								addPortInsts.add(inst);
							}
						}
					}
					
					if("703-5".equals(siteType) || "703-6".equals(siteType)){
						if(inst.getNumber() > 0 && (inst.getPortName().contains("fe.1.1") || 
								inst.getPortName().contains("fe.1.2"))){
							addPortInsts.add(inst);
						}
					}
					if("703-7".equals(siteType)){
						if(inst.getNumber() > 0 && (inst.getPortName().contains("fe.1.1"))){
							addPortInsts.add(inst);
						}
					}
//					if("703-2".equals(siteType) || "703-4".equals(siteType)
//							|| "703-6".equals(siteType)){
//						if(inst.getNumber() > 0 && inst.getPortName().contains("e1.1.1.1")){
//							if(!portNumber.contains(inst.getNumber())){
//								portNumber.add(inst.getNumber());
//								inst.setPortName("E1");
//								addPortInsts.add(inst);
//							}
//						}
//					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(siteService);
		}
		return addPortInsts;
	}
}

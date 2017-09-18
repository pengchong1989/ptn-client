package com.nms.ui.ptn.statistics.sement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.ptn.qos.QosQueue;
import com.nms.db.bean.report.PathStatisticsWidthRate;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EQosDirection;
import com.nms.db.enums.EServiceType;
import com.nms.db.enums.QosCosLevelEnum;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.ptn.qos.QosQueueService_MB;
import com.nms.model.util.ExportExcel;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 物理连接信息
 * @author sy
 *
 */
public class SegmentStatisticsWidthController extends AbstractController {
	private SegmentStatisticsWidthPanel view;
	private  JTable table;
	private  List<PathStatisticsWidthRate> dataList = new ArrayList<PathStatisticsWidthRate>();

	public SegmentStatisticsWidthController(SegmentStatisticsWidthPanel segmentStatisticsWidthPanel) {
		this.setView(segmentStatisticsWidthPanel);
		this.table = this.view.getWidthRightPane().getTable();
		addListener();
	}

	private void addListener() {
		this.view.getBtnBack().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 1; i < 18; i++) {
					setColumnWidth(table, i, 0);
				}
				for (int i = 1; i < 18; i++) {
					if (i < 2 || i >= 10) {
						setColumnWidth(table, i, 133);
					}
					if (i >= 2 && i < 10) {
						setColumnWidth(table, i, 0);
					}
				}
			}
		});

		this.view.getBtnForward().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 1; i < 18; i++) {
					setColumnWidth(table, i, 0);
				}
				for (int i = 1; i < 18; i++) {
					if (i < 10) {
						setColumnWidth(table, i, 133);
					}
					if (i >= 10) {
						setColumnWidth(table, i, 0);
					}
				}
			}
		});

		this.view.getBtnAll().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 1; i < 18; i++) {
					setColumnWidth(table, i, 0);
				}
				for (int i = 1; i < 18; i++) {
					setColumnWidth(table, i, 74);
				}
			}
		});

		this.view.getLeftPane().getBtnImport().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					dataList.clear();
					calculateWidth();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});

		this.view.getBtnExport().addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					export();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				
				return true;
			}
		});
	}

	/**
	 * 通过网元id查找对应的qos信息，计算带宽利用率
	 * 
	 * @param siteIds
	 * @throws Exception
	 */
	private void calculateWidth() throws Exception {
		List<Integer> siteIds = this.view.getLeftPane().getNeTreePanel().getPrimaryKeyList("site");
		if (siteIds.size() == 0) {
			DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_SITE));
		} else {
			SegmentService_MB segmentService = null;
			Map<Integer, Segment> map = new HashMap<Integer, Segment>();
			TunnelService_MB tunnelService = null;
			List<Tunnel> tunnelList = null;
			try {
				segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
				tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);

				// 将所有网元下的段放入map中，过滤掉重复的值
				for (int i = 0; i < siteIds.size(); i++) {
					int siteId = siteIds.get(i);
					List<Segment> segmentList = segmentService.selectBySite(siteId);
					for (Segment segment : segmentList) {
						map.put(segment.getId(), segment);
					}
				}
				// 遍历map，即遍历所有的段
				Set<Map.Entry<Integer, Segment>> set = map.entrySet();
				for (Iterator<Map.Entry<Integer, Segment>> its = set.iterator(); its.hasNext();) {
					Map.Entry<Integer, Segment> entry = its.next();
					// 以段为单位，统计该条段下关联的所有tunnel的cir，计算带宽

					tunnelList = tunnelService.selectByPort(entry.getValue().getAPORTID(), entry.getValue().getZPORTID());

					// 统计带宽利用率
					caculateRate(entry.getValue(), tunnelList);
				}

				this.view.getWidthRightPane().clear();
				this.view.getWidthRightPane().initData(dataList);
				this.view.getWidthRightPane().updateUI();
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			} finally {
				UiUtil.closeService_MB(segmentService);
				UiUtil.closeService_MB(tunnelService);
			}
		}
	}

	/**
	 * 统计带宽利用率
	 * 
	 * @param segment
	 *            段对象
	 * @param tunnelList
	 *            此段上承载的tunnel集合
	 */
	private void caculateRate(Segment segment, List<Tunnel> tunnelList) throws Exception {
		int cos = -1;
		int segment_forWard_cir = -1;
		int segment_back_cir = -1;
		int tunnel_forWard_cir_BE = 0;
		int tunnel_back_cir_BE = 0;
		int tunnel_forWard_cir_AF1 = 0;
		int tunnel_back_cir_AF1 = 0;
		int tunnel_forWard_cir_AF2 = 0;
		int tunnel_back_cir_AF2 = 0;
		int tunnel_forWard_cir_AF3 = 0;
		int tunnel_back_cir_AF3 = 0;
		int tunnel_forWard_cir_AF4 = 0;
		int tunnel_back_cir_AF4 = 0;
		int tunnel_forWard_cir_EF = 0;
		int tunnel_back_cir_EF = 0;
		int tunnel_forWard_cir_CS6 = 0;
		int tunnel_back_cir_CS6 = 0;
		int tunnel_forWard_cir_CS7 = 0;
		int tunnel_back_cir_CS7 = 0;
		Map<Integer, String> cirMap = new HashMap<Integer, String>();
		Map<Integer, Integer> forWardMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> backWardMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> tunnel_forWardMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> tunnel_backWardMap = new HashMap<Integer, Integer>();
		
		QosQueueService_MB qosQueueService=null;
		List<QosQueue> qosQueueList=null;
		QosQueue qosQueue_select=null;
		List<QosInfo> qosInfoList=null;
		QosInfoService_MB qosInfoService=null;
		try {
			//分别查询AZ端的qos
			qosQueueList=new ArrayList<QosQueue>();
			qosQueueService=(QosQueueService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosQueue);
			qosQueue_select=new QosQueue();
			qosQueue_select.setObjId(segment.getAPORTID());
			qosQueue_select.setObjType(EServiceType.SECTION.toString());
			qosQueueList.addAll(qosQueueService.queryByCondition(qosQueue_select));
			qosQueue_select.setObjId(segment.getZPORTID());
			qosQueueList.addAll(qosQueueService.queryByCondition(qosQueue_select));
			
			//遍历两个端口的qos 计算前向后向的qos
			for(QosQueue qosQueue : qosQueueList){
				if (qosQueue.getSiteId() == segment.getASITEID()) {
					// 说明是前向
					cos = qosQueue.getCos();// 0,1,2,3,4,5,6,7中的一种
					segment_forWard_cir = qosQueue.getCir();// 最低带宽
					forWardMap.put(cos, segment_forWard_cir);
				} else {
					// 说明是后向
					cos = qosQueue.getCos();// 0,1,2,3,4,5,6,7中的一种
					segment_back_cir = qosQueue.getCir();// 最低带宽
					backWardMap.put(cos, segment_back_cir);
				}
			}
			
			qosInfoService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo);
			for(Tunnel tunnel : tunnelList){
				
				qosInfoList = qosInfoService.getQosByObj(EServiceType.TUNNEL.toString(), tunnel.getTunnelId());
				if(qosInfoList!=null&&qosInfoList.size()>0){
					for(QosInfo qosInfo : qosInfoList){
						if(qosInfo.getCos() == QosCosLevelEnum.BE.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_BE += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_back_cir_BE += qosInfo.getCir();// 最低带宽
							}
						} else if(qosInfo.getCos() == QosCosLevelEnum.AF1.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_AF1 += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_back_cir_AF1 += qosInfo.getCir();// 最低带宽
							}
						}else if(qosInfo.getCos() == QosCosLevelEnum.AF2.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_AF2 += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_back_cir_AF2 += qosInfo.getCir();// 最低带宽
							}
						}else if(qosInfo.getCos() == QosCosLevelEnum.AF3.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_AF3 += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_back_cir_AF3 += qosInfo.getCir();// 最低带宽
							}
						}else if(qosInfo.getCos() == QosCosLevelEnum.AF4.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_AF4 += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_back_cir_AF4 += qosInfo.getCir();// 最低带宽
							}
						}else if(qosInfo.getCos() == QosCosLevelEnum.EF.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_EF += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_forWard_cir_EF += qosInfo.getCir();// 最低带宽
							}
						}else if(qosInfo.getCos() == QosCosLevelEnum.CS6.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_CS6 += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_forWard_cir_CS6 += qosInfo.getCir();// 最低带宽
							}
						}else if(qosInfo.getCos() == QosCosLevelEnum.CS7.getValue()){
							if(Integer.parseInt(qosInfo.getDirection()) == EQosDirection.FORWARD.getValue()){
								tunnel_forWard_cir_CS7 += qosInfo.getCir();// 最低带宽
							}else{
								tunnel_forWard_cir_CS7 += qosInfo.getCir();// 最低带宽
							}
						}
						
					}
				}
		
			}
			
			// 将结果放入map中
			tunnel_forWardMap.put(0, tunnel_forWard_cir_BE);
			tunnel_forWardMap.put(1, tunnel_forWard_cir_AF1);
			tunnel_forWardMap.put(2, tunnel_forWard_cir_AF2);
			tunnel_forWardMap.put(3, tunnel_forWard_cir_AF3);
			tunnel_forWardMap.put(4, tunnel_forWard_cir_AF4);
			tunnel_forWardMap.put(5, tunnel_forWard_cir_EF);
			tunnel_forWardMap.put(6, tunnel_forWard_cir_CS6);
			tunnel_forWardMap.put(7, tunnel_forWard_cir_CS7);
			tunnel_backWardMap.put(0, tunnel_back_cir_BE);
			tunnel_backWardMap.put(1, tunnel_back_cir_AF1);
			tunnel_backWardMap.put(2, tunnel_back_cir_AF2);
			tunnel_backWardMap.put(3, tunnel_back_cir_AF3);
			tunnel_backWardMap.put(4, tunnel_back_cir_AF4);
			tunnel_backWardMap.put(5, tunnel_back_cir_EF);
			tunnel_backWardMap.put(6, tunnel_back_cir_CS6);
			tunnel_backWardMap.put(7, tunnel_back_cir_CS7);

			String rate_cir_forWard = null;
			String rate_cir_back = null;
			for (int i = 0; i < 8; i++) {
				rate_cir_forWard = caculateRate(tunnel_forWardMap.get(i), forWardMap.get(i));
				rate_cir_back = caculateRate(tunnel_backWardMap.get(i), backWardMap.get(i));
				cirMap.put(i, rate_cir_forWard + ":" + rate_cir_back);
			}
			collectData(segment.getNAME(), cirMap);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(qosQueueService);
			UiUtil.closeService_MB(qosInfoService);
		}
	}

	/**
	 * 获取数据，封装到对象中
	 * 
	 * @param tunnelName
	 * @param cirMap
	 * @param eirMap
	 * @param qosType
	 */
	private void collectData(String segmentName, Map<Integer, String> cirMap) {
		PathStatisticsWidthRate rate_Cir = new PathStatisticsWidthRate();
		rate_Cir.setName(segmentName);
		rate_Cir.setRateName(segmentName);
		rate_Cir.setForWard_BE(cirMap.get(0).split(":")[0]);
		rate_Cir.setBackWard_BE(cirMap.get(0).split(":")[1]);
		rate_Cir.setForWard_AF1(cirMap.get(1).split(":")[0]);
		rate_Cir.setBackWard_AF1(cirMap.get(1).split(":")[1]);
		rate_Cir.setForWard_AF2(cirMap.get(2).split(":")[0]);
		rate_Cir.setBackWard_AF2(cirMap.get(2).split(":")[1]);
		rate_Cir.setForWard_AF3(cirMap.get(3).split(":")[0]);
		rate_Cir.setBackWard_AF3(cirMap.get(3).split(":")[1]);
		rate_Cir.setForWard_AF4(cirMap.get(4).split(":")[0]);
		rate_Cir.setBackWard_AF4(cirMap.get(4).split(":")[1]);
		rate_Cir.setForWard_EF(cirMap.get(5).split(":")[0]);
		rate_Cir.setBackWard_EF(cirMap.get(5).split(":")[1]);
		rate_Cir.setForWard_CS6(cirMap.get(6).split(":")[0]);
		rate_Cir.setBackWard_CS6(cirMap.get(6).split(":")[1]);
		rate_Cir.setForWard_CS7(cirMap.get(7).split(":")[0]);
		rate_Cir.setBackWard_CS7(cirMap.get(7).split(":")[1]);
		// 放入集合中
		dataList.add(rate_Cir);
	}

	/**
	 * 计算剩余率
	 * 
	 * @param tunnel_ForWard_Cir
	 * @param segment_ForWard_Cir
	 * @return
	 */
	private String caculateRate(int tunnel_ForWard_Cir, int segment_ForWard_Cir) {
		double rate = 0.0d;
		String rate_temp = "";
		if (segment_ForWard_Cir > 0) {
			Format format = new DecimalFormat("0.00");
			rate_temp = format.format((double) (segment_ForWard_Cir - tunnel_ForWard_Cir) / segment_ForWard_Cir);
			rate = (Double.parseDouble(rate_temp)) * 100;
		} else {
			rate = 0.0d;
		}
		String rate_cir_forWard = (segment_ForWard_Cir - tunnel_ForWard_Cir) + "/" + segment_ForWard_Cir + " (" + rate + "%)";
		return rate_cir_forWard;
	}

	/**
	 * 设置列宽
	 * 
	 * @param table
	 * @param count
	 * @param width
	 */
	private void setColumnWidth(JTable table, int count, int width) {
		TableColumn tc = table.getTableHeader().getColumnModel().getColumn(count);
		if (width == 0) {
			tc.setPreferredWidth(width);
			tc.setMinWidth(width);
			tc.setMaxWidth(width);
		} else {
			tc.setPreferredWidth(width);
			tc.setMinWidth(width - 50);
			tc.setMaxWidth(width + 50);
		}
	}

	/**
	 * 导出统计数据保存到excel
	 */
	@Override
	public void export() throws Exception {
		ExportExcel export=null;
		String result;
		// 得到页面信息
		try {
			export=new ExportExcel();
			//得到bean的集合转为  String[]的List
			List<String[]> beanData=export.tranListString(this.dataList,"segmentWidthTable");
			//导出页面的信息-Excel
			result=export.exportExcel(beanData, "segmentWidthTable");
			//添加操作日志记录
			this.insertOpeLog(EOperationLogType.SITESTATISTICSEXPORT.getValue(),ResultString.CONFIG_SUCCESS, null, null);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			result=null;
			export=null;
		}
	}

	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENTNFOWIDTH),"");		
	}
	public SegmentStatisticsWidthPanel getView() {
		return view;
	}

	public void setView(SegmentStatisticsWidthPanel view) {
		this.view = view;
	}

	@Override
	public void refresh() throws Exception {
	}

}
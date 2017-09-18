package com.nms.util;
import java.awt.Component;

import com.nms.rmi.ui.util.ServerConstant;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.ptn.alarm.view.CurrentAlarmPanel;
import com.nms.ui.ptn.alarm.view.HisAlarmPanel;
import com.nms.ui.ptn.basicinfo.NetWorkInfoPanel;
import com.nms.ui.ptn.basicinfo.SegmentPanel;
import com.nms.ui.ptn.basicinfo.SetNameRulePanel;
import com.nms.ui.ptn.business.ces.CesBusinessPanel;
import com.nms.ui.ptn.business.dual.DualBusinessPanel;
import com.nms.ui.ptn.business.elan.ElanBusinessPanel;
import com.nms.ui.ptn.business.eline.ElineBusinessPanel;
import com.nms.ui.ptn.business.etree.EtreeBusinessPanel;
import com.nms.ui.ptn.business.loopProtect.LoopProtectPanel;
import com.nms.ui.ptn.business.pw.PwBusinessPanel;
import com.nms.ui.ptn.business.tunnel.TunnelBusinessPanel;
import com.nms.ui.ptn.client.view.ClientManagerPanel;
import com.nms.ui.ptn.configinfo.AlarmDescPanel;
import com.nms.ui.ptn.event.oamEvent.view.OamEventPanel;
import com.nms.ui.ptn.performance.view.CurrPerformCountPanel;
import com.nms.ui.ptn.performance.view.CurrentPerformancePanel;
import com.nms.ui.ptn.performance.view.HisPerformancePanel;
import com.nms.ui.ptn.performance.view.PathPerformCountPanel;
import com.nms.ui.ptn.performance.view.PerformanceDescPanel;
import com.nms.ui.ptn.performance.view.PerformanceTaskPanel;
import com.nms.ui.ptn.safety.LoginLogPanel;
import com.nms.ui.ptn.safety.OperationLogPanel;
import com.nms.ui.ptn.safety.UserInfoPanel;
import com.nms.ui.ptn.safety.UserOnLinePanel;
import com.nms.ui.ptn.safety.roleManage.RoleManagePanel;
import com.nms.ui.ptn.statistics.alarm.AlarmInfoPanel;
import com.nms.ui.ptn.statistics.business.ProfessPanel;
import com.nms.ui.ptn.statistics.card.CardInfoPanel;
import com.nms.ui.ptn.statistics.lable.LableInfoPanel;
import com.nms.ui.ptn.statistics.layerRate.LayerRateCountPanel;
import com.nms.ui.ptn.statistics.nepathstatics.NePathNumStatisticsPanel;
import com.nms.ui.ptn.statistics.path.PathStatisticsWidthPanel;
import com.nms.ui.ptn.statistics.pathstatics.PathNumStatisticsPanel;
import com.nms.ui.ptn.statistics.performance.PerformanceInfoPanel;
import com.nms.ui.ptn.statistics.prot.LspPortPanel;
import com.nms.ui.ptn.statistics.prot.PortInfoPanel;
import com.nms.ui.ptn.statistics.sement.SegmentStatisticsPanel;
import com.nms.ui.ptn.statistics.sement.SegmentStatisticsWidthPanel;
import com.nms.ui.ptn.statistics.site.SiteCountStatisticsPanel;
import com.nms.ui.ptn.statistics.site.SiteStatisticsPanel;
import com.nms.ui.ptn.statistics.slot.SlotStatisticsPanel;
import com.nms.ui.ptn.systemManage.monitor.SystemMontorConfigPanel;
import com.nms.ui.ptn.systemManage.view.UnLoadingPanel;
import com.nms.ui.ptn.systemconfig.NeConfigView;
import com.nms.ui.ptn.systemconfig.QosTemplatePanel;
import com.nms.ui.ptn.systemconfig.UploadDownloadConfigurePanel;
import com.nms.ui.ptn.systemconfig.dialog.siteInitialise.SiteInitialiseConfig;
import com.nms.ui.topology.NetworkElementPanel;

/**
 *根据目录地址来打开相应的目录 
 * @author zk
 */
public class OpenHelpManual {
	/**
	 * @param contentsPath 目录地址 eg:273.html
	 * 通过调用系统自带的命令来打开相应的网管PTN手册.chm目录
	 */
   private void openHelp(String contentsPath){
	   
	   StringBuffer cmd = new StringBuffer();
		try{
		//如果参数为空或没有填值默认打开第一页
		if(contentsPath == null || contentsPath.trim().equals("")){
			contentsPath = "7.html";
		}
	     // 通过调用系统自带的命令“hh.exe mk:@MSITStore:”为Dos命令。
		//调用windows hh.exe程序,“inquire.mht”为*.chm格式文件中的所要打指定的索引页
		cmd.append("hh.exe mk:@MSITStore:").append(ServerConstant.HELPMANUALADDRESS).append("::").append("/z/").append(contentsPath);
		Runtime.getRuntime().exec(cmd.toString());
		
		}catch(Exception e){
			ExceptionManage.dispose(e, getClass());
		}finally{
			cmd = null;
		}
   }
   
   public void openHelp(Component comp){
	   try {
		   //获取帮助地址
		   String helpPath = getHelpPath(comp);
		   //打开相应的帮助位置
		   openHelp(helpPath);
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
   }
   
   //根据不同的界面来显示对应的帮助文档
   private String getHelpPath(Component comp) throws Exception{
	   
	String helpPath = "";
	try {
		if(comp instanceof NetworkElementPanel){
			helpPath = "51.html";//拓扑管理对应的地址
		}else if(comp instanceof SegmentPanel){
			helpPath = "82.html";//段对应的地址
		}else if(comp instanceof TunnelBusinessPanel){
			helpPath = "208.html";//tunnel对应的地址
		}else if(comp instanceof PwBusinessPanel){
			helpPath = "222.html";//PW对应的地址
		}else if(comp instanceof ElineBusinessPanel){
			helpPath = "242.html";//Eline业务对应的地址
		}else if(comp instanceof EtreeBusinessPanel){
			helpPath = "263.html";//etree业务对应的地址
		}else if(comp instanceof ElanBusinessPanel){
			helpPath = "253.html";//Elan业务对应的地址
		}else if(comp instanceof CesBusinessPanel){
			helpPath = "234.html";//CES业务对应的地址
		}else if(comp instanceof LoopProtectPanel){
			helpPath = "98.html";//环业务对应的地址
		}else if(comp instanceof CurrentAlarmPanel){
			helpPath = "274.html";//当前告警对应的地址
		}else if(comp instanceof HisAlarmPanel){
			helpPath = "275.html";//历史告警对应的地址
		}else if(comp instanceof AlarmDescPanel){
			helpPath = "AlarmDes.html";//告警描述对应的地址
		}else if(comp instanceof CurrentPerformancePanel){
			helpPath = "298.html";//当前性能对应的地址
		}else if(comp instanceof HisPerformancePanel){
			helpPath = "299.html";//历史性能对应的地址
		}else if(comp instanceof PerformanceDescPanel){
			helpPath = "306.html";//性能描述对应的地址
		}else if(comp instanceof PerformanceTaskPanel){
			helpPath = "301.html";//性能长期任务对应的地址
		}else if(comp instanceof UserInfoPanel){
			helpPath = "325.html";//用户管理对应的地址
		}else if(comp instanceof SiteStatisticsPanel){
			helpPath = "337.html";//网元配置信息对应的地址
		}else if(comp instanceof SegmentStatisticsPanel){
			helpPath = "338.html";//网元物理连接信息信息对应的地址
		}else if(comp instanceof SegmentStatisticsWidthPanel){
			helpPath = "339.html";//网元物理连接带宽信息对应的地址
		}else if(comp instanceof SlotStatisticsPanel){
			helpPath = "341.html";//槽位信息统计对应的地址
		}else if(comp instanceof CardInfoPanel){
			helpPath = "342.html";//单板信息统计对应的地址
		}else if(comp instanceof PortInfoPanel){
			helpPath = "dk.htm";//端口信息统计对应的地址
		}else if(comp instanceof LableInfoPanel){
			helpPath = "bq.htm";//标签信息统计对应的地址
		}else if(comp instanceof AlarmInfoPanel){
			helpPath = "352.html";//告警信息统计对应的地址
		}else if(comp instanceof NetWorkInfoPanel){
			helpPath = "106.html";//网元管理统计对应的地址
		}else if(comp instanceof UnLoadingPanel){
			helpPath = "1112.html";//数据转储对应的地址
		}else if(comp instanceof SetNameRulePanel){
			helpPath = "rule.htm";//命名规则对应的地址
		}else if(comp instanceof QosTemplatePanel){
			helpPath = "1011.html";//QOS模板对应的地址
		}else if(comp instanceof NeConfigView){
			helpPath = "1012.html";//网元管理对应的地址
		}else if(comp instanceof ClientManagerPanel){
			helpPath = "1013.html";//客户信息对应的地址
		}else if(comp instanceof UploadDownloadConfigurePanel){
			helpPath = "1015.html";//上下载对应的地址
		}else if(comp instanceof SiteInitialiseConfig){
			helpPath = "ip.htm";//上下载对应的地址
		}else if(comp instanceof DualBusinessPanel){
			helpPath = "Dual.htm";//dual对应的地址
		}else if(comp instanceof OamEventPanel){
			helpPath = "oamshijian.html";//OAM事件的地址
		}else if(comp instanceof SystemMontorConfigPanel){
			helpPath = "xtjh.html";//OAM事件的地址
		}else if(comp instanceof CurrPerformCountPanel){
			helpPath = "ss299.html";//实时性能统计的地址
		}else if(comp instanceof PerformanceInfoPanel){
			helpPath = "353.html";//历史性能统计的地址
		}else if(comp instanceof PathPerformCountPanel){
			helpPath = "dd299.html";//端到端性能统计的地址
		}else if(comp instanceof RoleManagePanel){
			helpPath = "js.htm";//角色管理的地址
		}else if(comp instanceof LoginLogPanel){
			helpPath = "831.html";//登录日志的地址
		}else if(comp instanceof UserOnLinePanel){
			helpPath = "841.html";//在线用户的地址
		}else if(comp instanceof OperationLogPanel){
			helpPath = "861.html";//操作日志的地址
		}else if(comp instanceof SiteCountStatisticsPanel){
			helpPath = "shul.html";//网元数量统计的地址
		}else if(comp instanceof PathStatisticsWidthPanel){
			helpPath = "9422.htm";//服务路径带宽统计的地址
		}else if(comp instanceof LspPortPanel){
			helpPath = "LL.html";//链路带宽统计的地址
		}else if(comp instanceof ProfessPanel){
			helpPath = "942.htm";//业务信息的地址
		}else if(comp instanceof NePathNumStatisticsPanel){
			helpPath = "wy.htm";//网元路径数量统计的地址
		}else if(comp instanceof PathNumStatisticsPanel){
			helpPath = "dd.htm";//端到端路径数量统计的地址
		}else if(comp instanceof LayerRateCountPanel){
			helpPath = "cs.htm";//层速率统计的地址
		}
		
	} catch (Exception e) {
		throw e;
	}
	return helpPath;
   }
   
   public static void main(String[] args) {
	   OpenHelpManual openHelpManual = new OpenHelpManual();
	   openHelpManual.openHelp("260.html");
  }
}

package com.nms.ui.ptn.systemManage.monitor;

import com.nms.db.bean.ptn.DbInfoTask;
import com.nms.db.bean.system.DataBaseInfo;
import com.nms.model.system.DataBaseService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.alarm.AlarmTools;
import com.nms.ui.ptn.systemManage.monitor.view.DataBaseRightPanel;

/**
 * <p>文件名称:MointorCPUThread.java</p>
 * <p>文件描述:监控数据库内存</p>
 * <p>版权所有: 版权所有(C)2013-2015</p>
 * <p>公    司: 北京建博信通软件技术有限公司</p>
 * <p>内容摘要:</p>
 * <p>其他说明: </p>
 * <p>完成日期: 2015年2月28日</p>
 * <p>修改记录1:</p>
 * <pre>
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 * <p>修改记录2：</p>
 * @version 1.0
 * @author zhangkun
 */
public class MointorCPUThread implements Runnable{
    /**************用于标记是监控CPU和内存 ==1 磁盘 ==2********************/
	private int label;
	/**************监控周期********************/
	long millis = 0l;
	
	private String threadName;
	//****** 代码段: 构造方法 *********************************/
	public MointorCPUThread(int label,String threadName)
	{
		this.label = label;
		this.threadName = threadName;
	}
	@Override
	public void run() {
		analysisCpuThread();
	}
	
	
	
	private synchronized void analysisCpuThread()
	{
		/**************是否需要监控********************/
		boolean flag = true;
		while(flag)
		{
			DataBaseRightPanel dataBaseRightPanel = new DataBaseRightPanel();
			DataBaseService_MB dataBaseService  =  null;
			DbInfoTask dbInfoTask = null;
			try 
			{
				dataBaseService = (DataBaseService_MB)ConstantUtil.serviceFactory.newService_MB(Services.DATABASEINFO);
				dbInfoTask = dataBaseService.selectMoinTableInfo("ptn","2");
				dataBaseRightPanel.disdatchDbInfoTask(dbInfoTask);
				//设置监控任务
				setRunTask(dbInfoTask);
			} catch (Exception e) 
			{
				flag = false;
				ExceptionManage.dispose(e, getClass());
			}finally
			{
				dataBaseRightPanel = null;
				UiUtil.closeService_MB(dataBaseService);
				dbInfoTask = null;
				if(flag){
						try {
						Thread thread = ConstantUtil.threadMap.get(threadName);
						if(thread == null)
						{
							flag = false;
						}
						else {
							if(thread.isInterrupted())
							{
								flag = false;
							}
							Thread.sleep(millis);
						}
						} catch (Exception e2) {
							flag = false;
					}
				}
			}
		}
	}
	/**
	 * 设置任务
	 * 
	 */
	private void setRunTask(DbInfoTask dbInfoTask) throws Exception{
		AlarmTools  alarmTools = new AlarmTools(1);
		try 
		{
				//监控CPU
				if(label ==1 && dbInfoTask.isMointorTotal()&& dbInfoTask.getDaTableList() != null && dbInfoTask.getDaTableList().size() >0)
				{
					if(dbInfoTask.isMointorTotal()&& dbInfoTask.getDaTableList() != null && dbInfoTask.getDaTableList().size() >0){
						
						for(DataBaseInfo dataBaseInfo : dbInfoTask.getDaTableList()){
							if(dataBaseInfo.getMointorLevel() == 2 && dataBaseInfo.getName().equals("CPU"))
							{
								if(!dataBaseInfo.getCriticalRate().equals("")&&(dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getCriticalRate()))){
									alarmTools.createMointorClintAndService(1057, 5, dataBaseInfo.getName(),0);
								} 
								 if(!dataBaseInfo.getMajorRate().equals("")&&(dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getMajorRate())))
								{
									alarmTools.createMointorClintAndService(1058, 4, dataBaseInfo.getName(),0);
								}
								 if(!dataBaseInfo.getMinorRate().equals("")&& (dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getMinorRate())))
								{
									alarmTools.createMointorClintAndService(1059, 3, dataBaseInfo.getName(),0);
								}
								 if(!dataBaseInfo.getWarningRate().equals("")&& dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getWarningRate()))
								{
									alarmTools.createMointorClintAndService(1060, 2, dataBaseInfo.getName(),0);
								}
							}else if(dataBaseInfo.getMointorLevel() == 4 && dataBaseInfo.getName().equals(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_MEMORY)))
							{
								if(!dataBaseInfo.getCriticalRate().equals("")&&(dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getCriticalRate()))){
									alarmTools.createMointorClintAndService(1061, 5, dataBaseInfo.getName(),0);
								} 
								if(!dataBaseInfo.getMajorRate().equals("")&&(dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getMajorRate())))
								{
									alarmTools.createMointorClintAndService(1062, 4, dataBaseInfo.getName(),0);
								}
								if(!dataBaseInfo.getMinorRate().equals("")&& (dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getMinorRate())))
								{
									alarmTools.createMointorClintAndService(1063, 3, dataBaseInfo.getName(),0);
								}
								if(!dataBaseInfo.getWarningRate().equals("")&& dataBaseInfo.getCountSize() > Double.parseDouble(dataBaseInfo.getWarningRate()))
								{
									alarmTools.createMointorClintAndService(1064, 2, dataBaseInfo.getName(),0);
								}
							}
						}
						millis = dbInfoTask.getTotalDbSpace()*1000;
					}
				}
				if(label ==2 && dbInfoTask.isMointorTypeDb()&& dbInfoTask.getDaTableList() != null && dbInfoTask.getDaTableList().size() >0)
				{
					for(DataBaseInfo dataBaseInfo : dbInfoTask.getDaTableList())
					{
						if(dataBaseInfo.getMointorLevel() == 3 && dataBaseInfo.getMointorType() == 1)
						{
							 String transitionV1= String.valueOf(dataBaseInfo.getUseMemory());
							 String transitionV2= String.valueOf(dataBaseInfo.getCountMemory());
							 double transitiond1 = Double.parseDouble(transitionV1);
							 double transitiond2 = Double.parseDouble(transitionV2);
							 double count = (transitiond1/transitiond2)*100;
							if(!dataBaseInfo.getCriticalRate().equals("")&&(count > Double.parseDouble(dataBaseInfo.getCriticalRate()))){
								alarmTools.createMointorClintAndService(1065, 5, dataBaseInfo.getName().substring(0, 2),1);
							} 
							 if(!dataBaseInfo.getMajorRate().equals("")&&(count > Double.parseDouble(dataBaseInfo.getMajorRate())))
							{
								alarmTools.createMointorClintAndService(1066, 4, dataBaseInfo.getName().substring(0, 2),1);
							}
							 if(!dataBaseInfo.getMinorRate().equals("")&& (count > Double.parseDouble(dataBaseInfo.getMinorRate())))
							{
								alarmTools.createMointorClintAndService(1067, 3, dataBaseInfo.getName().substring(0, 2),1);
							}
							 if(!dataBaseInfo.getWarningRate().equals("")&& count > Double.parseDouble(dataBaseInfo.getWarningRate()))
							{
								alarmTools.createMointorClintAndService(1068, 2, dataBaseInfo.getName().substring(0, 2),1);
							}
						}
					}
					millis = dbInfoTask.getMiintorCycle()*1000;
				}
		} catch (Exception e) 
		{
			throw e;
		}
	}

}

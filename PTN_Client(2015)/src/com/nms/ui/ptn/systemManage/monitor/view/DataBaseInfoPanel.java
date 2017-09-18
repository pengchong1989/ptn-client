package com.nms.ui.ptn.systemManage.monitor.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.nms.db.bean.system.DataBaseInfo;
import com.nms.model.system.DataBaseService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 
 * @author zk
 *数据库信息
 */
public class DataBaseInfoPanel extends DataBasePanel{
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 8268823431418301351L;

   public DataBaseInfoPanel(){
	   initDataBse();
	   setValue();
    }
    private void initDataBse() {
    	
        super.table.setModel(new DefaultTableModel(new Object[][]{},new String[]{ResourceUtil.srcStr(StringKeysTip.COURSEINFOTABLENUMBER)
        		,ResourceUtil.srcStr(StringKeysTip.COURSEINFOTABLTABLE)
        		,ResourceUtil.srcStr(StringKeysTip.DATABASESIZE)
        		,ResourceUtil.srcStr(StringKeysTip.FREEDPACE)
        		,ResourceUtil.srcStr(StringKeysTip.DATASPACE1)
        		,ResourceUtil.srcStr(StringKeysTip.INDEXSPACE1)
        }){
		@SuppressWarnings("rawtypes")
		Class[] types = new Class[]{java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class};
		@Override
		public Class getColumnClass(int columnIndex){
			return types[columnIndex];
		}
		@Override
		public boolean isCellEditable(int rowIndex,int columnIndex){
			if(rowIndex == 1){
				return false;
			}
			return true;
		}
	});
 }
    //为表格赋值
    public void setValue(){
    	
    	DataBaseService_MB dataBaseService  =  null;
    	List<DataBaseInfo> dataBaseInfoList = null;
    	
    	try {
    		
    		dataBaseInfoList = new ArrayList<DataBaseInfo>();
    		dataBaseService = (DataBaseService_MB)ConstantUtil.serviceFactory.newService_MB(Services.DATABASEINFO);
    		dataBaseInfoList.add(dataBaseService.selectTableInfo("ptn",1));
    		super.refresh(dataBaseInfoList,1);
    		
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(dataBaseService);
			dataBaseInfoList = null;
		}
    }
    
	public JTable getTable() {
		return super.table;
	}
    
}

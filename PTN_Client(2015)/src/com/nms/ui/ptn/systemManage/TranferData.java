package com.nms.ui.ptn.systemManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.system.UnLoading;
import com.nms.ui.manager.ExceptionManage;
import com.nms.util.Mybatis_DBManager;


public class TranferData {
	public static void main(String []args){
		TranferData tranData=new TranferData();
		try {
			@SuppressWarnings("static-access")
			UnLoading unload=new UnLoading();
			unload.setUnloadType(3);
			List<String> sqlList=getDataStr(unload, 4);
			for(String sql:sqlList){
				System.out.println(sql);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,TranferData.class);
		}
	}
	/**
	 * 根据table名称。 获取table语句
	 * 
	 * @param tableName
	 *            表名
	 * @throws SQLException
	 */
	private static List<String> getTableBeans(String tableName) throws Exception {

		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		List<String> typeList = null;
		String sql = null;
		try {
			sql = "desc `" + tableName + "`";
			typeList = new ArrayList<String>();
			preparedStatement = Mybatis_DBManager.getInstance().getConnection().prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				typeList.add(resultSet.getString("type"));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
					ExceptionManage.dispose(e,TranferData.class);
				}finally{
					resultSet = null;
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (Exception e) {
					ExceptionManage.dispose(e,TranferData.class);
				}finally{
					preparedStatement = null;
				}
			}
			sql = null;
		}
		return typeList;
	}
	/**
	 * 根据table 把table数据转成sql语句
	 * 
	 * @param tableName
	 *            表名
	 * @param tableBeanList
	 *            列集合 有多少个列就循环多少此
	 * @return
	 * @throws Exception
	 */
	private static List<String> getDataStr(UnLoading unload,int count) throws Exception {
		ResultSet resultSet = null;
		StringBuffer stringBuffer = new StringBuffer();
		PreparedStatement preparedStatement = null;
		String sql = null;
		String label = "";
		String tableName=null;
		String byTime=null;
		List<String> sqlList=null;
		List<String> typeList=null;
		Connection conn = null;
		try {
			conn = Mybatis_DBManager.getInstance().getConnection();
			if(1==unload.getUnloadType()){
				tableName="history_alarm";
				byTime="happenedtime";
			}else if(2==unload.getUnloadType()){
				tableName="history_performance";
				byTime="performancetime";
			}else if(3==unload.getUnloadType()){
				tableName="operation_log";
				byTime="startTime";
			}else{
				return sqlList;
			}
			sqlList=new ArrayList<String>();
			sql = "select * from " + tableName + " order by "+byTime+" limit 0,?";
			typeList=getTableBeans(tableName);
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, count);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				stringBuffer=new StringBuffer();
				stringBuffer.append("insert into " + tableName + " values (");
				// 遍历列集合，取索引
				for (int i = 1; i <= typeList.size(); i++) {
					// 如果是第一次循环。前方不加","
					if (i == 1) {
						label = "";
					} else {
						label = ",";
					}
					// 如果值为null 直接存入NULL 而不是'null'
					if (null != resultSet.getObject(i)) {
						/**
						 * 判断 类型
						 * 	varchar  需要加''
						 */
						if(typeList.get(i-1).contains("int")){
							stringBuffer.append(label+ resultSet.getObject(i));
						}else if(typeList.get(i-1).contains("varchar")){
							stringBuffer.append(label + "'" + resultSet.getObject(i) + "'");
						}
					} else {
						stringBuffer.append(label + "null");
					}
				}
				stringBuffer.append(");\n");
				sqlList.add(stringBuffer.toString());
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
					ExceptionManage.dispose(e,TranferData.class);
				}finally{
					resultSet = null;
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (Exception e) {
					ExceptionManage.dispose(e,TranferData.class);
				}finally{
					preparedStatement = null;
				}
			}
			sql = null;
			tableName=null;
			byTime=null;
		}
		return sqlList;
	}
}

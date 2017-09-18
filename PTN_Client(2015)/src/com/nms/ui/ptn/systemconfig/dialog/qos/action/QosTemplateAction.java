package com.nms.ui.ptn.systemconfig.dialog.qos.action;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.qos.QosTemplateInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EQosDirection;
import com.nms.db.enums.QosCosLevelEnum;
import com.nms.db.enums.QosTemplateTypeEnum;
import com.nms.model.ptn.qos.QosTemplateService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.systemconfig.QosTemplatePanel;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosTemplateController;
import com.nms.ui.ptn.systemconfig.dialog.qos.dialog.AddQosTemplateDialog;

public class QosTemplateAction {

	private  String oldName = "";

	private  String oldQosType = "";
	
	Vector datas = null;
	
	public void setTableDatas(AddQosTemplateDialog addQosTemplateDialog) {
		addQosTemplateDialog.getQosTableModel().setRowCount(0);
		Object data[] = new Object[] {};
		int rowCount = 0;
		String direction = "";
		if (addQosTemplateDialog.getQosTypeComboBox().getSelectedItem().toString().equals(QosTemplateTypeEnum.LLSP.toString())) {
			rowCount = 0;
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					direction = EQosDirection.FORWARD.toString();
				} else {
					direction = EQosDirection.BACKWARD.toString();
				}
				data = new Object[] { ++rowCount, "EF", direction, 0, 1, 0, 1, 0 };
				addQosTemplateDialog.getQosTableModel().addRow(data);
			}
		} else if (addQosTemplateDialog.getQosTypeComboBox().getSelectedItem().toString().equals(QosTemplateTypeEnum.ELSP.toString())) {
			rowCount = 0;
			for (QosCosLevelEnum level : QosCosLevelEnum.values()) {
				for (int i = 0; i < 2; i++) {
					if (i == 0) {
						direction = EQosDirection.FORWARD.toString();
					} else {
						direction = EQosDirection.BACKWARD.toString();
					}
					data = new Object[] { ++rowCount, level.toString(), direction, 0, 1, 0, 1, 0 };
					addQosTemplateDialog.getQosTableModel().addRow(data);
				}
			}
		}
		setDatas(addQosTemplateDialog.getQosTableModel().getDataVector());
	}

	/*
	 * 增加qos模板
	 */
	@SuppressWarnings("rawtypes")
	public void addQosTemplate(AddQosTemplateDialog addQosTemplateDialog) throws Exception {
		QosTemplateService_MB templateService = null;
		try {
			String qosType = addQosTemplateDialog.getQosTypeComboBox().getSelectedItem().toString();
			String qosTemplateName = addQosTemplateDialog.getTempNameField().getText();
			List<QosTemplateInfo> qosTemplateInfoList = new ArrayList<QosTemplateInfo>();
			DefaultTableModel tableModel = addQosTemplateDialog.getQosTableModel();
			Vector dataVector = tableModel.getDataVector();
			Iterator dataIterator = dataVector.iterator();
			while (dataIterator.hasNext()) {
				Vector obj = (Vector) dataIterator.next();
				QosTemplateInfo templateInfo = new QosTemplateInfo();
				templateInfo.setTemplateName(qosTemplateName);
				templateInfo.setQosType(qosType);
				templateInfo.setCos(QosCosLevelEnum.from(obj.get(1).toString()));
				if(obj.get(2).toString().equals(EQosDirection.FORWARD.toString())){
					templateInfo.setDirection(EQosDirection.FORWARD.toString());
				}else{
					templateInfo.setDirection(EQosDirection.BACKWARD.toString());
				}
				templateInfo.setCir(new Integer(obj.get(3).toString()));
				templateInfo.setCbs(new Integer(obj.get(4).toString()));
				templateInfo.setEir(new Integer(obj.get(5).toString()));
				templateInfo.setEbs(new Integer(obj.get(6).toString()));
				templateInfo.setPir(new Integer(obj.get(7).toString()));
				qosTemplateInfoList.add(templateInfo);
			}
			/*
			 * 修改模板时
			 */
			CommonBean qosTempBefore = null;
			 templateService = (QosTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosTemplate);
			if (getOldName() != null && getOldQosType() != null) {
				/*start 将修改前的数据保留，便于日志记录*****/
				List<QosTemplateInfo> qosTempListBefore = templateService.queryByCondition(oldName);
				qosTempBefore = new CommonBean();
				qosTempBefore.setQosTemplateName(oldName);
				qosTempBefore.setQosTemplateList(qosTempListBefore);
				/*end*************************************/
				templateService.delete(getOldName(), getOldQosType());
			}
			templateService.saveOrUpdate(qosTemplateInfoList);
			DialogBoxUtil.succeedDialog(addQosTemplateDialog, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
			//添加日志记录
			CommonBean qosTemp = new CommonBean();
			qosTemp.setQosTemplateName(qosTemplateName);
			qosTemp.setQosTemplateList(qosTemplateInfoList);
			if (getOldName() != null && getOldQosType() != null && oldQosType.equals(qosType)) {
				AddOperateLog.insertOperLog(addQosTemplateDialog.getSaveButton(), EOperationLogType.QOSMODEUPDATE.getValue(), 
						null, qosTempBefore, qosTemp, 0, ResourceUtil.srcStr(StringKeysObj.QOS_TEMPLATE_CONFIG), "qosTemplate");
			}else{
				AddOperateLog.insertOperLog(addQosTemplateDialog.getSaveButton(), EOperationLogType.QOSMODEINSERT.getValue(), 
						null, null, qosTemp, 0, ResourceUtil.srcStr(StringKeysObj.QOS_TEMPLATE_CONFIG), "qosTemplate");
			}
			
			
			//恢复默认值
			oldName = "";
			oldQosType = "";
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(templateService);
		}
	}

	/*
	 * 初始时加载到界面中的数据
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> initData() {
		Map<String, List> qosTemplateMap = null;
		QosTemplateService_MB templateService = null;
		try {
			templateService = (QosTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosTemplate);
			qosTemplateMap = templateService.selectAll();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(templateService);
		}
		return qosTemplateMap;
	}

	/*
	 * 删除模板
	 */
	public void deleteTemplate(QosTemplatePanel qosTemplatePanel) throws Exception {
		QosTemplateService_MB templateService = null;
		try {
			if (qosTemplatePanel.getQosResultTable().getSelectedRowCount() == 0) {
				DialogBoxUtil.errorDialog(qosTemplatePanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_MORE));
			} else {
				templateService = (QosTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosTemplate);
				int result = DialogBoxUtil.confirmDialog(qosTemplatePanel, ResourceUtil.srcStr(StringKeysTip.TIP_IS_DELETE));
				if (result == 0) {
					for (int i = 0; i < qosTemplatePanel.getQosResultTable().getSelectedRows().length; i++) {
						String name = qosTemplatePanel.getQosResultTable().getValueAt(qosTemplatePanel.getQosResultTable().getSelectedRows()[i], 1).toString();
						String qosType = qosTemplatePanel.getQosResultTable().getValueAt(qosTemplatePanel.getQosResultTable().getSelectedRows()[i], 2).toString();
						templateService.delete(name, qosType);
					}
				}
				//添加日志记录
				qosTemplatePanel.getDeleteButton().setOperateKey(EOperationLogType.QOSMODEDELETE.getValue());
				qosTemplatePanel.getDeleteButton().setResult(1);
				DialogBoxUtil.succeedDialog(qosTemplatePanel, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(templateService);
		}
	}

	/*
	 * 修改qos模板
	 */
	public void setUpdateTemplateInfo(AddQosTemplateDialog addQosTemplateDialog, String name, String qosType) {
		addQosTemplateDialog.getTempNameField().setText(name);
		addQosTemplateDialog.getQosTypeComboBox().setSelectedItem(qosType);
		List<QosTemplateInfo> templateInfoList = null;
		addQosTemplateDialog.getQosTableModel().setRowCount(0);
		QosTemplateService_MB templateService = null;
		try {
			templateService = (QosTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosTemplate);
			templateInfoList = templateService.queryByCondition(name);
			int i = 0;
			for (QosTemplateInfo info : templateInfoList) {
				Object[] data = new Object[] { ++i, QosCosLevelEnum.from(info.getCos()), info.getDirection(), info.getCir(), info.getCbs(), info.getEir(), info.getEbs(), info.getPir() };
				addQosTemplateDialog.getQosTableModel().addRow(data);
				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(templateService);
		}
	}

	/*
	 * 打开添加qos模块窗口
	 */
	public void openAddQosTemplate(QosTemplatePanel qosTemplatePanel) {
		AddQosTemplateDialog addQosTemplateDialog = new AddQosTemplateDialog(qosTemplatePanel.getUpPanel(), true, ResourceUtil.srcStr(StringKeysObj.ADD_QOS_TEMPLATE), "add");
		@SuppressWarnings("unused")
		QosTemplateController controller = new QosTemplateController(addQosTemplateDialog, this);
		addQosTemplateDialog.setSize(550, 450);
		addQosTemplateDialog.setLocation(UiUtil.getWindowWidth(addQosTemplateDialog.getWidth()), UiUtil.getWindowHeight(addQosTemplateDialog.getHeight()));
		addQosTemplateDialog.setVisible(true);
	}

	/*
	 * 打开修改qos模块窗口
	 */
	public void openUpdateQosTemplate(QosTemplatePanel qosTemplatePanel) {
		String name = null;
		String qosType = null;
		try {
			if (qosTemplatePanel.getQosResultTable().getSelectedRowCount() != 1) {
				DialogBoxUtil.errorDialog(qosTemplatePanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			} else {
				name = qosTemplatePanel.getQosResultTable().getValueAt(qosTemplatePanel.getQosResultTable().getSelectedRows()[0], 1).toString();
				qosType = qosTemplatePanel.getQosResultTable().getValueAt(qosTemplatePanel.getQosResultTable().getSelectedRows()[0], 2).toString();
				this.setOldName(name);
				this.setOldQosType(qosType);
				AddQosTemplateDialog addQosTemplateDialog = new AddQosTemplateDialog(qosTemplatePanel.getUpPanel(), true, ResourceUtil.srcStr(StringKeysObj.UPDATE_QOS_TEMPLATE), "modify");
				QosTemplateController controller = new QosTemplateController(addQosTemplateDialog, this);
				controller.setUpdateTemplateInfo(addQosTemplateDialog, name, qosType);
				addQosTemplateDialog.getSaveButton().setOperateKey(EOperationLogType.QOSMODEUPDATE.getValue());
				addQosTemplateDialog.setSize(550, 450);
				addQosTemplateDialog.setLocation(UiUtil.getWindowWidth(addQosTemplateDialog.getWidth()), UiUtil.getWindowHeight(addQosTemplateDialog.getHeight()));
				addQosTemplateDialog.setVisible(true);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/*
	 * 获取模板的详细信息
	 */
	public void showDetailQosInfo(QosTemplatePanel qosTemplatePanel) throws Exception {
		QosTemplateService_MB templateService = null;
		try {
			if (qosTemplatePanel.getQosResultTable().getSelectedRows().length > 0) {
				templateService = (QosTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosTemplate);
				String name = (String) qosTemplatePanel.getQosResultTable().getValueAt(qosTemplatePanel.getQosResultTable().getSelectedRows()[0], 1);
				@SuppressWarnings("unused")
				String qosType = (String) qosTemplatePanel.getQosResultTable().getValueAt(qosTemplatePanel.getQosResultTable().getSelectedRows()[0], 2);
				qosTemplatePanel.getDetailTableModel().getDataVector().clear();
				qosTemplatePanel.getDetailTableModel().fireTableDataChanged();
				List<QosTemplateInfo> infoList = templateService.queryByCondition(name);
				if (infoList != null && infoList.size() != 0) {
//				if(CodeConfigItem.getInstance().getWuhan() == 1 || CodeConfigItem.getInstance().getFiberhome() == 1){
//					//武汉设备或者晨晓的qos做处理,显示的时候要把Mbps换算成kbps
//					for (QosTemplateInfo qosInfo : infoList) {
//						qosInfo.setCir(qosInfo.getCir()*1000);
//						qosInfo.setEir(qosInfo.getEir()*1000);
//						qosInfo.setPir(qosInfo.getPir()*1000);
//					}
//				}
					int i = 0;
					for (QosTemplateInfo info : infoList) {
						Object[] data = new Object[] { ++i, QosCosLevelEnum.from(info.getCos()), info.getDirection(), info.getCir(), info.getCbs(), info.getEir(), info.getEbs(), info.getPir() };
						qosTemplatePanel.getDetailTableModel().addRow(data);
					}
					
//				if(CodeConfigItem.getInstance().getWuhan() == 1 || CodeConfigItem.getInstance().getFiberhome() == 1){
//					//武汉设备或者晨晓的qos做处理,显示完之后再还原
//					for (QosTemplateInfo qosInfo : infoList) {
//						qosInfo.setCir(qosInfo.getCir()/1000);
//						qosInfo.setEir(qosInfo.getEir()/1000);
//						qosInfo.setPir(qosInfo.getPir()/1000);
//					}
//				}
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(templateService);
		}

	}

	/*
	 * 判断模板名是否为空
	 */
	public boolean checkNameIsEmpty(AddQosTemplateDialog addQosTemplateDialog) {
		if ("".equals(addQosTemplateDialog.getTempNameField().getText())) {
			return true;
		}
		addQosTemplateDialog.getTempNameField().setBorder(new LineBorder(Color.BLACK));
		addQosTemplateDialog.getSaveButton().setEnabled(true);
		addQosTemplateDialog.getCancelButton().setEnabled(true);
		return false;
	}

	/*
	 * 判断模板名称是否冲突
	 */
	public boolean checkTempNameIsExsit(AddQosTemplateDialog addQosTemplateDialog) {
		String qosType = addQosTemplateDialog.getQosTypeComboBox().getSelectedItem().toString();
		String qosTemplateName = addQosTemplateDialog.getTempNameField().getText();
		QosTemplateService_MB templateService = null;
		try {
			if(this.oldName.equals(qosTemplateName)){
				return false;
			}
			templateService = (QosTemplateService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosTemplate);
			if (templateService.checkTemplateName(qosTemplateName, qosType)) {
				return true;
			}
			addQosTemplateDialog.getSaveButton().setEnabled(true);
			addQosTemplateDialog.getCancelButton().setEnabled(true);
			addQosTemplateDialog.getTempNameField().setBorder(new LineBorder(Color.BLACK));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(templateService);
		}
		return false;
	}

	/*
	 * 若是EELP类型，则cos固定
	 */
	public void qosIsELSP(AddQosTemplateDialog addQosTemplateDialog) {
		if (addQosTemplateDialog.getQosTypeComboBox().getSelectedItem().equals(QosTemplateTypeEnum.ELSP.toString())) {
			setNewTableModel(addQosTemplateDialog);
			TableColumn eirColumn = null;
			TableColumn pirColumn = null;
//			if(CodeConfigItem.getInstance().getWuhan()==1 || CodeConfigItem.getInstance().getFiberhome()==1){
//				eirColumn = addQosTemplateDialog.getTempTable().getColumn("EIR(Mbps)");
//				pirColumn = addQosTemplateDialog.getTempTable().getColumn("PIR(Mbps)");
//			}else{
				eirColumn = addQosTemplateDialog.getTempTable().getColumn("EIR(kbps)");
				pirColumn = addQosTemplateDialog.getTempTable().getColumn("PIR(kbps)");
//			}
			
			TableColumn ebsColumn = addQosTemplateDialog.getTempTable().getColumn(ResourceUtil.srcStr(StringKeysObj.EBS_BYTE));
			for (int i = 0; i < addQosTemplateDialog.getTempTable().getRowCount(); i++) {
				Object cosvalue = addQosTemplateDialog.getTempTable().getValueAt(i, 1);
				if (cosvalue.toString().equals(QosCosLevelEnum.EF.toString()) || cosvalue.toString().equals(QosCosLevelEnum.CS6.toString()) || cosvalue.toString().equals(QosCosLevelEnum.CS7.toString())) {
					JTextField eirField = new JTextField("-1");
					eirField.setEditable(false);
					eirField.setEnabled(false);
					eirColumn.setCellEditor(new DefaultCellEditor(eirField));
					JTextField ebsField = new JTextField("0");
					ebsField.setEditable(false);
					ebsField.setEnabled(false);
					ebsColumn.setCellEditor(new DefaultCellEditor(ebsField));
					JTextField pirJTextField = (JTextField) pirColumn.getCellEditor().getTableCellEditorComponent(addQosTemplateDialog.getTempTable(), 0, false, i, 7);
					pirJTextField.setEnabled(false);
					pirJTextField.setEditable(false);
				} else {
					JSpinner eirSpinner = (JSpinner) eirColumn.getCellEditor().getTableCellEditorComponent(addQosTemplateDialog.getTempTable(), 0, false, i, 5);
					eirSpinner.setEnabled(true);
					JSpinner ebsSpinner = (JSpinner) ebsColumn.getCellEditor().getTableCellEditorComponent(addQosTemplateDialog.getTempTable(), -1, false, i, 6);
					ebsSpinner.setEnabled(true);
					JTextField pirJTextField = (JTextField) pirColumn.getCellEditor().getTableCellEditorComponent(addQosTemplateDialog.getTempTable(), 0, false, i, 7);
					pirJTextField.setEnabled(true);
					pirJTextField.setEditable(true);
				}
			}
		} else {
			setNewTableModel(addQosTemplateDialog);
			TableColumn cosColumn = addQosTemplateDialog.getTempTable().getColumn("COS");
			for (int j = 0; j < addQosTemplateDialog.getTempTable().getRowCount(); j++) {
				JComboBox comboBox = (JComboBox) cosColumn.getCellEditor().getTableCellEditorComponent(addQosTemplateDialog.getTempTable(), cosColumn.getCellEditor().getCellEditorValue().toString(), false, j, 1);
				comboBox.setEnabled(true);
			}
		}

	}

	@SuppressWarnings("serial")
	private void setNewTableModel(AddQosTemplateDialog addQosTemplateDialog) {
		DefaultTableModel tableModel = null;
		if (addQosTemplateDialog.getQosTypeComboBox().getSelectedItem().equals(QosTemplateTypeEnum.ELSP.toString())) {
			tableModel = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(int row, int column) {
					if (column == 0 || column == 2 || column == 1) {
						return false;
					}
					return true;
				}
			};
		} else {
			tableModel = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(int row, int column) {
					if (column == 0 || column == 2) {
						return false;
					}
					return true;
				}
			};
		}
		tableModel.setDataVector(this.getDatas(), addQosTemplateDialog.getColumnName());
		addQosTemplateDialog.setQosTableModel(tableModel);
		addQosTemplateDialog.getTempTable().setModel(tableModel);
		addQosTemplateDialog.configCosComboBox();
		addQosTemplateDialog.configSpinner();
	}

	/*
	 * 使表格数据瞬间变化
	 */
	public void commitTable(AddQosTemplateDialog addQosTemplateDialog) {
		int selectR = -1;
		int selectC = -1;
		int oldValue = 0;
		int newValue = 0;
		JSpinner spinner = null;
		try {
			if (addQosTemplateDialog.getTempTable().getEditorComponent() != null) {
				if (addQosTemplateDialog.getTempTable().getEditorComponent() instanceof JSpinner) {
					spinner = (JSpinner) addQosTemplateDialog.getTempTable().getEditorComponent();
					selectR = addQosTemplateDialog.getTempTable().getSelectedRow();
					selectC = addQosTemplateDialog.getTempTable().getSelectedColumn();
					if (selectR >= 0 && selectC >= 0) {
						oldValue = Integer.valueOf(addQosTemplateDialog.getTempTable().getValueAt(selectR, selectC) + "");
					}
					JTextField ff = ((JSpinner.NumberEditor) (spinner.getComponents()[2])).getTextField();
					String value = ff.getText();
					((DefaultEditor) spinner.getEditor()).getTextField().setText(value);
					for (char di : value.replace(",", "").toCharArray()) {
						if (!Character.isDigit(di)) {
							return;
						}
					}
					if ("".equals(value.replace(",", ""))) {
						newValue = 0;
					} else if (Long.parseLong(value.replace(",", "")) >= 
//						(CodeConfigItem.getInstance().getWuhan() == 1 ? 
//								ConstantUtil.QOS_CIR_MAX_10G/1000 : 
									ConstantUtil.QOS_CIR_MAX_10G) {
//						if (CodeConfigItem.getInstance().getWuhan() == 1) {
//							newValue = ConstantUtil.QOS_CIR_MAX_10G/1000;
//						}else{
							newValue = ConstantUtil.QOS_CIR_MAX_10G;
//						}
					} else if (Long.parseLong(value.replace(",", "")) <= 0) {
						newValue = 0;
					} else {
						newValue = Integer.parseInt(value.replace(",", ""));
					}
					if (selectC != 4 && selectC != 6) {
//						if (CodeConfigItem.getInstance().getWuhan() == 1) {
//							spinner.setModel(new SpinnerNumberModel(newValue, 0, ConstantUtil.QOS_CIR_MAX_10G/1000, 1));
//						}else{
							spinner.setModel(new SpinnerNumberModel(newValue, 0, ConstantUtil.QOS_CIR_MAX_10G, 64));
//						}
					}

					spinner.commitEdit();
					if (addQosTemplateDialog.getTempTable().isEditing()) {
						addQosTemplateDialog.getTempTable().getCellEditor().stopCellEditing();
					}
					if (selectC == 4 || selectC == 6) {
						if(newValue < 0 || newValue > ConstantUtil.CBS_MAXVALUE){
							addQosTemplateDialog.getTempTable().setValueAt(1, selectR, selectC);
						}else{
							spinner.setModel(new SpinnerNumberModel(newValue, 0, ConstantUtil.CBS_MAXVALUE, 1));
						}
//						if (oldValue == -1) {
//							if (newValue > oldValue && newValue < 4096) {
//								addQosTemplateDialog.getTempTable().setValueAt(4096, selectR, selectC);
//							} else if (newValue >= 4096) {
//								addQosTemplateDialog.getTempTable().setValueAt(newValue, selectR, selectC);
//							} else if (newValue <= -1) {
//								addQosTemplateDialog.getTempTable().setValueAt(-1, selectR, selectC);
//							}
//						} else if (oldValue >= 4096) {
//							if (newValue < 4096) {
//								addQosTemplateDialog.getTempTable().setValueAt(-1, selectR, selectC);
//							} else if (newValue >= 4096) {
//								addQosTemplateDialog.getTempTable().setValueAt(newValue, selectR, selectC);
//							}
//						}
					} else {
//						if (CodeConfigItem.getInstance().getWuhan() == 1) {
//							if (newValue % 1 != 0) {
//								if (newValue > 1) {
//									newValue = ((newValue / 1)) * 1;
//								} else {
//									newValue = 1;
//								}
//								addQosTemplateDialog.getTempTable().setValueAt(newValue, selectR, selectC);
//							}
//						}else{
							if (newValue % 64 != 0) {
								if (newValue > 64) {
									newValue = ((newValue / 64)) * 64;
								} else {
									newValue = 64;
								}
								addQosTemplateDialog.getTempTable().setValueAt(newValue, selectR, selectC);
							}
//						}
					}
				}
			}
		} catch (Exception e) {
			((DefaultEditor) spinner.getEditor()).getTextField().setText(spinner.getValue() + "");
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/*
	 * 当表格数据变化时，使得数据变化一致
	 */
	public void setDataIsConsistent(AddQosTemplateDialog addQosTemplateDialog) {
		// pir设的初始值应该== cir+eir
		int selectR = addQosTemplateDialog.getTempTable().getSelectedRow();
//		int selectC = addQosTemplateDialog.getTempTable().getSelectedColumn();
		Object cirvalue = addQosTemplateDialog.getTempTable().getValueAt(selectR, 3);
		Object eirvalue = addQosTemplateDialog.getTempTable().getValueAt(selectR, 5);
		Object a = Integer.parseInt(cirvalue.toString())+Integer.parseInt(eirvalue.toString());
		addQosTemplateDialog.getQosTableModel().setValueAt(a, addQosTemplateDialog.getTempTable().getSelectedRow(), 7);
	}

	/*
	 * 保证cos的一致性
	 */
	public void keepCosConsistent(AddQosTemplateDialog addQosTemplateDialog) {
		if (addQosTemplateDialog.getQosTypeComboBox().getSelectedItem().equals(QosTemplateTypeEnum.LLSP.toString())) {
			if (addQosTemplateDialog.getTempTable().getSelectedColumn() == 1) {
				TableColumn cosColumn = addQosTemplateDialog.getTempTable().getColumn("COS");

				Object cosvalue = cosColumn.getCellEditor().getCellEditorValue();
				for (int i = 0; i < addQosTemplateDialog.getTempTable().getRowCount(); i++) {
					addQosTemplateDialog.getQosTableModel().setValueAt(cosvalue, i, 1);
				}
			}
		}
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public  String getOldQosType() {
		return oldQosType;
	}

	public  void setOldQosType(String oldQosType) {
		this.oldQosType = oldQosType;
	}

	@SuppressWarnings("rawtypes")
	public Vector getDatas() {
		return datas;
	}

	@SuppressWarnings("rawtypes")
	public void setDatas(Vector datas) {
		this.datas = datas;
	}

}

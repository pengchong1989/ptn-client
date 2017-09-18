package com.nms.ui.ptn.systemconfig.dialog.qos.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.systemconfig.dialog.qos.action.QosTemplateAction;
import com.nms.ui.ptn.systemconfig.dialog.qos.dialog.AddQosTemplateDialog;

public class QosTemplateController {

	private AddQosTemplateDialog addQosTemplateDialog;

	QosTemplateAction qosTemplateAction;

	public QosTemplateController(AddQosTemplateDialog addQosTemplateDialog, QosTemplateAction qosAction) {
		this.qosTemplateAction = qosAction;
		qosTemplateAction.setTableDatas(addQosTemplateDialog);
		this.addQosTemplateDialog = addQosTemplateDialog;
		addListeners();
	}

	public void addListeners() {
		this.addQosTemplateDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				addQosTemplateDialog.dispose();
			}
		});
		this.addQosTemplateDialog.getQosTypeComboBox().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				qosTemplateAction.setTableDatas(getAddQosTemplateDialog());
				qosTemplateAction.qosIsELSP(getAddQosTemplateDialog());
			}
		});
		this.addQosTemplateDialog.getSaveButton().addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					if (qosTemplateAction.checkNameIsEmpty(addQosTemplateDialog)) {
						DialogBoxUtil.errorDialog(getAddQosTemplateDialog(), ResourceUtil.srcStr(StringKeysTip.TIP_QOS_TEMPLATENAME_NOTEMPTY));
						return;
					}
					if (qosTemplateAction.checkTempNameIsExsit(addQosTemplateDialog)) {
						DialogBoxUtil.errorDialog(getAddQosTemplateDialog(), ResourceUtil.srcStr(StringKeysTip.TIP_QOS_TEMPLATENAME_ISEXSIT));
						return;
					}	
					qosTemplateAction.addQosTemplate(getAddQosTemplateDialog());
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
				getAddQosTemplateDialog().dispose();
			}

			@Override
			public boolean checking() {

				return true;
			}

		});

		this.addQosTemplateDialog.getCancelButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getAddQosTemplateDialog().dispose();
			}
		});
		this.addQosTemplateDialog.getTempTable().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				qosTemplateAction.keepCosConsistent(getAddQosTemplateDialog());
			}
		});
		this.addQosTemplateDialog.getTableScrollPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent et) {
				if (getAddQosTemplateDialog().getTempTable().getEditorComponent() == null) {
					return;
				}
				if (getAddQosTemplateDialog().getTempTable().getSelectedColumn() > 1) {
					qosTemplateAction.commitTable(getAddQosTemplateDialog());
				}
				if (getAddQosTemplateDialog().getTempTable().getSelectedColumn() == 3) {
					qosTemplateAction.setDataIsConsistent(getAddQosTemplateDialog());
				}
				if (getAddQosTemplateDialog().getTempTable().getSelectedColumn() == 5) {
					qosTemplateAction.setDataIsConsistent(getAddQosTemplateDialog());
				}
			}
		});
		this.addQosTemplateDialog.getTempTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				qosTemplateAction.keepCosConsistent(getAddQosTemplateDialog());
			}

			@Override
			public void mouseReleased(MouseEvent et) {
				qosTemplateAction.keepCosConsistent(getAddQosTemplateDialog());
			}

			@Override
			public void mouseEntered(MouseEvent et) {
				if (getAddQosTemplateDialog().getTempTable().getEditorComponent() == null) {
					return;
				}
				if (getAddQosTemplateDialog().getTempTable().getSelectedColumn() > 1) {
					qosTemplateAction.commitTable(getAddQosTemplateDialog());
				}
				if (getAddQosTemplateDialog().getTempTable().getSelectedColumn() == 3) {
					qosTemplateAction.setDataIsConsistent(getAddQosTemplateDialog());
				}
				if (getAddQosTemplateDialog().getTempTable().getSelectedColumn() == 5) {
					qosTemplateAction.setDataIsConsistent(getAddQosTemplateDialog());
				}
			}
		});
	}

	public void setUpdateTemplateInfo(AddQosTemplateDialog addQosTemplateDialog, String name, String qosType) {
		qosTemplateAction.setUpdateTemplateInfo(addQosTemplateDialog, name, qosType);
	}

	public AddQosTemplateDialog getAddQosTemplateDialog() {
		return addQosTemplateDialog;
	}

	public void setAddQosTemplateDialog(AddQosTemplateDialog addQosTemplateDialog) {
		this.addQosTemplateDialog = addQosTemplateDialog;
	}

}

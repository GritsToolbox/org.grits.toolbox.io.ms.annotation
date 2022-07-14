package org.grits.toolbox.io.ms.annotation.process.export;

import org.apache.log4j.Logger;
import org.grits.toolbox.widgets.processDialog.ProgressDialog;

import org.grits.toolbox.io.ms.annotation.listener.ExcelListener;

public class MSAnnotationExcelListener implements ExcelListener {

	//log4J Logger
	private static final Logger logger = Logger.getLogger(MSAnnotationExcelListener.class);
	private ProgressDialog progressBarDialog = null;
	
	public MSAnnotationExcelListener(ProgressDialog a_progressBarDialog) {
		super();
		this.progressBarDialog = a_progressBarDialog;
	}
	
	@Override
	public void message(String a_message) {
		progressBarDialog.setDescriptionText(a_message);
		logger.error(a_message);
	}

}

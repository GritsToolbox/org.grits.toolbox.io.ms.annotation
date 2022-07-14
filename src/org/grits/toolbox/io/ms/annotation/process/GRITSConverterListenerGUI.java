package org.grits.toolbox.io.ms.annotation.process;

import org.apache.log4j.Logger;
import org.grits.toolbox.widgets.processDialog.ProgressDialog;

public class GRITSConverterListenerGUI implements GRITSConverterListener {

	//log4J Logger
	private static final Logger logger = Logger.getLogger(GRITSConverterListenerGUI.class);
	private int countScan = 1;
	private ProgressDialog progressBarDialog = null;
	
	public GRITSConverterListenerGUI(ProgressDialog a_progressBarDialog) {
		super();
		this.progressBarDialog = a_progressBarDialog;
	}
	
	@Override
	public void error(String a_message, Integer a_scan) {
		progressBarDialog.setDescriptionText(a_message + " at scan: " + a_scan);
		logger.error(a_message + " at scan: " + a_scan);
	}

	@Override
	public void error(String a_message, Exception a_exception, Integer a_scan, String a_glycanId) {
		progressBarDialog.setDescriptionText(a_message + ", glycanId: " + a_glycanId + " at scan: " + a_scan);
		logger.error(a_message + ", glycanId: " + a_glycanId + " at scan: " + a_scan, a_exception);
	}

	@Override
	public void processScan(final Integer arg0) {
		//will inform you scan id need to count
		progressBarDialog.updateProgresBar("Process scan " + countScan++);
	}

	@Override
	public void error(String arg0) {
		progressBarDialog.setDescriptionText(arg0);
		logger.error(arg0);
	}

	@Override
	public void error(String a_message, Exception a_exception, Integer a_scan) {
		progressBarDialog.setDescriptionText(a_message + " at scan: " + a_scan);
		logger.error(a_message + " at scan: " + a_scan, a_exception);
	}

}

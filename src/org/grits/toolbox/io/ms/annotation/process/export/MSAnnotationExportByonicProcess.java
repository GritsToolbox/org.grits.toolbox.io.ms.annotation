package org.grits.toolbox.io.ms.annotation.process.export;

import org.apache.log4j.Logger;
import org.grits.toolbox.datamodel.ms.annotation.preference.MSAnnotationViewerPreference;
import org.grits.toolbox.datamodel.ms.annotation.tablemodel.MSAnnotationTableDataObject;
import org.grits.toolbox.widgets.progress.CancelableThread;
import org.grits.toolbox.widgets.progress.IProgressThreadHandler;

public class MSAnnotationExportByonicProcess extends CancelableThread {

	//log4J Logger
	private static final Logger logger = Logger.getLogger(MSAnnotationExportByonicProcess.class);

	protected MSAnnotationTableDataObject tableDataObject = null;
	private int iMasterParentScan = -1; // if set, use this when determining if a row is hidden
	private int m_lastVisibleColInx = -1;
	private String sOutputFile = null;
	
	/**
	 *  there is nothing to export by default
	 *  need to be implemented for the glycan annotations
	 */ 
	@Override
	public boolean threadStart(IProgressThreadHandler a_progressThreadHandler) throws Exception{
		
		
		return true;
	}

	protected boolean hideUnAnnotatedRows() {
		MSAnnotationViewerPreference settings = (MSAnnotationViewerPreference) getTableDataObject().getTablePreferences();
		return settings.isHideUnannotatedPeaks();
	}

	public MSAnnotationTableDataObject getTableDataObject() {
		return tableDataObject;
	}

	public void setTableDataObject(MSAnnotationTableDataObject tableDataObject) {
		this.tableDataObject = tableDataObject;
	}

	public int getLastVisibleColInx() {
		return m_lastVisibleColInx;
	}

	public void setLastVisibleColInx(int m_lastVisibleColInx) {
		this.m_lastVisibleColInx = m_lastVisibleColInx;
	}


	public String getOutputFile() {
		return sOutputFile;
	}

	public void setOutputFile(String _sOutputFile) {
		this.sOutputFile = _sOutputFile;
	}
	
	public int getMasterParentScan() {
		return iMasterParentScan;
	}
	
	public void setMasterParentScan(int iMasterParentScan) {
		this.iMasterParentScan = iMasterParentScan;
	}
}

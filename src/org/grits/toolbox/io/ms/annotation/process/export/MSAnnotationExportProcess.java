package org.grits.toolbox.io.ms.annotation.process.export;

import org.apache.log4j.Logger;
import org.grits.toolbox.datamodel.ms.annotation.preference.MSAnnotationViewerPreference;
import org.grits.toolbox.datamodel.ms.annotation.tablemodel.MSAnnotationTableDataObject;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.widgets.processDialog.ProgressDialog;
import org.grits.toolbox.widgets.progress.CancelableThread;
import org.grits.toolbox.widgets.progress.IProgressThreadHandler;

public class MSAnnotationExportProcess extends CancelableThread {

	//log4J Logger
	private static final Logger logger = Logger.getLogger(MSAnnotationExportProcess.class);

	protected MSAnnotationTableDataObject tableDataObject = null;
	private int iMasterParentScan = -1; // if set, use this when determining if a row is hidden
	private int m_lastVisibleColInx = -1;
	private String sOutputFile = null;

	protected MSAnnotationWriterExcel getNewMSAnnotationWriterExcel() {
		return new MSAnnotationWriterExcel();
	}
	
	@Override
	public boolean threadStart(IProgressThreadHandler a_progressThreadHandler) throws Exception{
		try{
			// write values to Excel
			MSAnnotationWriterExcel t_writerExcel = getNewMSAnnotationWriterExcel();

			t_writerExcel.createNewFile(getOutputFile(), getTableDataObject(), getLastVisibleColInx(),
					new MSAnnotationExcelListener((ProgressDialog) a_progressThreadHandler));	        
//			t_writerExcel.writeHeadline();	        
			((ProgressDialog) a_progressThreadHandler).setMax(this.tableDataObject.getTableData().size());
			((ProgressDialog) a_progressThreadHandler).setProcessMessageLabel("Exporting data");
			for( int i = 0; i < getTableDataObject().getTableData().size(); i++ )  {
				if(isCanceled()) {
					t_writerExcel.close();
					return false;
				}
				
				int iParentScanIdCol = -1;
				if( getTableDataObject().getParentNoCol() != null && ! getTableDataObject().getParentNoCol().isEmpty() ) {
					iParentScanIdCol = getTableDataObject().getParentNoCol().get(0);
				}
				int iPeakIdCol = getTableDataObject().getPeakIdCols().get(0);
				int iFeatureIdCol = getTableDataObject().getFeatureIdCols().get(0);
				Integer iParentScanNum = null;
				if( getMasterParentScan() != -1 ) {
					iParentScanNum = getMasterParentScan();
				} else if ( iParentScanIdCol != -1 && getTableDataObject().getTableData().get(i).getDataRow().get(iParentScanIdCol) != null ) {
					iParentScanNum = (Integer) getTableDataObject().getTableData().get(i).getDataRow().get(iParentScanIdCol);
				}
				Integer iPeakId = (Integer) getTableDataObject().getTableData().get(i).getDataRow().get(iPeakIdCol);
				String sFeatureId = (String) getTableDataObject().getTableData().get(i).getDataRow().get(iFeatureIdCol);
				if( sFeatureId == null && hideUnAnnotatedRows() ) {
					continue;
				}
				Integer iScanNum = null;
				if( getTableDataObject().getScanNoCols() != null && ! getTableDataObject().getScanNoCols().isEmpty() ) {
					iScanNum = getTableDataObject().getScanNoCols().get(0);
				}				
				String iRowId = Feature.getRowId(iPeakId, iScanNum, ((MSAnnotationTableDataObject) getTableDataObject()).getUsesComplexRowId() );
				if(  iPeakId != null && sFeatureId != null && iParentScanNum != null &&
						getTableDataObject().isHiddenRow(iParentScanNum, iRowId, sFeatureId) ) 
					continue;
				
				boolean bInvisible = false;
				if( iPeakId != null && iParentScanNum != null && getTableDataObject().isInvisibleRow(iParentScanNum, iRowId) )
					bInvisible = true;
								
				//write scan
				t_writerExcel.writeRow(i, bInvisible);
				//show in dialog
				((ProgressDialog) a_progressThreadHandler).updateProgresBar("Scan: "+ (i+1));
			}
			t_writerExcel.close();
		}catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			throw e;
		}
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

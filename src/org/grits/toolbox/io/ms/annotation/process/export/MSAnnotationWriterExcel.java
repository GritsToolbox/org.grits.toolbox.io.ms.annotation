package org.grits.toolbox.io.ms.annotation.process.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.grits.toolbox.datamodel.ms.annotation.tablemodel.MSAnnotationTableDataObject;
import org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader;
import org.grits.toolbox.display.control.table.datamodel.GRITSListDataRow;
import org.grits.toolbox.io.ms.annotation.listener.ExcelListener;

public class MSAnnotationWriterExcel
{
	protected Workbook m_objWorkbook = null;
	protected Sheet m_objSheet = null;
	protected int m_iRowCounter = 0;
	protected String m_file = null;
	protected ExcelListener m_listener = null;
	protected MSAnnotationTableDataObject m_msAnnotationDataObject = null;
	protected int m_lastVisibleColInx = -1;
	protected Map<Integer, Integer> dataToPrefColumn = null;
	protected int iSheetCount = 1;
	
	public final static int EXCEL_DEFAULT_COLUMN_WIDTH = 3500;
	public final static int EXCEL_MAX_NUM_ROWS = 20000;

	public void createNewFile(String a_sOutputFile, 
			MSAnnotationTableDataObject a_msAnnotationDataObject, int m_lastVisibleColInx,
			ExcelListener a_listener )
	{
		this.m_objWorkbook = new HSSFWorkbook();
		this.m_msAnnotationDataObject = a_msAnnotationDataObject;
		this.m_listener = a_listener;
		this.m_file = a_sOutputFile;
		this.m_lastVisibleColInx = m_lastVisibleColInx;
//	createSheet();
		//        this.writeFileName();
	}

	public void createSheet() {
		this.m_objSheet = this.m_objWorkbook.createSheet( 
				this.m_msAnnotationDataObject.getAnalysisName() != null ? 
						this.m_msAnnotationDataObject.getAnalysisName() + "_" + this.iSheetCount : "MS Annotation Results" + "_" + this.iSheetCount);    
		if( this.iSheetCount == 1 ) {
			determineCollapsedColumnPositions();
		}
		this.m_iRowCounter = 0;
		writeHeadline();
		this.iSheetCount++;
	}

	public void close() throws IOException
	{
		FileOutputStream t_fos = new FileOutputStream(this.m_file);
		this.m_objWorkbook.write(t_fos);
		t_fos.close();
	}

	public void writeEmptyLine()
	{
		this.m_iRowCounter++;
	}

	public MSAnnotationTableDataObject getAnnotationDataObject() {
		return m_msAnnotationDataObject;
	}

	protected void determineCollapsedColumnPositions() {
		List<Integer> alPrefColNums = new ArrayList<>();
		ArrayList<GRITSColumnHeader> headerRow = (ArrayList<GRITSColumnHeader>) getAnnotationDataObject().getLastHeader();       
		for (int i = 0; i < headerRow.size(); i++) {
			GRITSColumnHeader header = headerRow.get(i);
			int iColNum = getAnnotationDataObject().getTablePreferences().getPreferenceSettings().getColumnPosition(header);
			if (iColNum == -1) {
				continue;
			}
			alPrefColNums.add(iColNum);
		}

		Collections.sort(alPrefColNums);
		this.dataToPrefColumn = new HashMap<>();
		for (int i = 0; i < headerRow.size(); i++) {
			GRITSColumnHeader header = headerRow.get(i);
			int iColNum = getAnnotationDataObject().getTablePreferences().getPreferenceSettings().getColumnPosition(header);
			if ( iColNum == -1 ) {
				continue;
			}
			int iCollapsedColNum = alPrefColNums.indexOf(iColNum);
			dataToPrefColumn.put(i, iCollapsedColNum);
		}
	}

	public void writeHeadline()
	{
//		determineCollapsedColumnPositions();

		Row t_row = this.m_objSheet.createRow(this.m_iRowCounter);
		ArrayList<GRITSColumnHeader> headerRow = (ArrayList<GRITSColumnHeader>) getAnnotationDataObject().getLastHeader();       
		for (int i = 0; i < headerRow.size(); i++) {
			GRITSColumnHeader header = headerRow.get(i);
			//				int iColNum = getAnnotationDataObject().getTablePreferences().getPreferenceSettings().getColumnPosition(header);
			int iColNum = getPreferredCellNumber(i);
			if( iColNum == -1 ) {
				continue;
			}
			Cell t_cell = t_row.createCell(iColNum);
			t_cell.setCellValue(header.getLabel());
			t_cell.setCellType(CellType.STRING);
			this.m_objSheet.setColumnWidth( iColNum, EXCEL_DEFAULT_COLUMN_WIDTH);
		}
		writeEmptyLine();
	}

	public void writeFileName()
	{
		Row t_row = this.m_objSheet.createRow(this.m_iRowCounter);
		Cell t_cell = t_row.createCell(0);
		t_cell.setCellValue("Project file");
		t_cell.setCellType(CellType.STRING);
		t_cell = t_row.createCell(2);
		t_cell.setCellValue(this.m_msAnnotationDataObject.getAnalysisName());
		t_cell.setCellType(CellType.STRING);
		this.m_iRowCounter++;
	}
	
	protected void performPreWriteInits() {
		if( this.m_objSheet == null || this.m_iRowCounter == (EXCEL_MAX_NUM_ROWS-1) ) {
			createSheet();
		}
	}

	public void writeRow( int _iRow, boolean bInvisible ) {
		performPreWriteInits();
		GRITSListDataRow alRow = getAnnotationDataObject().getTableData().get(_iRow);
		Row excelRow = this.m_objSheet.createRow(this.m_iRowCounter);
		for ( int iColNum = 0; iColNum < alRow.getDataRow().size(); iColNum++ ) {
			writeCell(excelRow, alRow.getDataRow(), iColNum, getPreferredCellNumber( iColNum ), bInvisible);
		}
		this.writeEmptyLine();    	
	}

	protected int getPreferredCellNumber(int _iColNum ) {
		//		int iPrefColNum = getAnnotationDataObject().getTablePreferences().getPreferenceSettings().getColumnPosition(header);
		if( dataToPrefColumn.containsKey(_iColNum) ) {
			return dataToPrefColumn.get(_iColNum);
		}
		return -1;
	}

	protected void writeCell( Row _excelRow, ArrayList<Object> _tableRow, int _iDataColNum, int _iPrefColNum, boolean bInvisible ) {
		if( _iPrefColNum < 0 ) 
			return;
		if ( bInvisible && _iDataColNum > m_lastVisibleColInx )
			return;    		
		Object oVal = _tableRow.get(_iDataColNum);
		if ( oVal == null ) 
			return;
		Cell t_cell = _excelRow.createCell(_iPrefColNum);
		//		t_cell.setCellValue( oVal.toString() );
		if (oVal instanceof Number ) {
			if ( oVal instanceof Integer )
				t_cell.setCellValue((Integer) oVal);
			else
				t_cell.setCellValue(new Double(oVal.toString()));
			t_cell.setCellType(CellType.NUMERIC);    				
		} else if ( oVal instanceof Boolean ) {
			//					t_cell.setCellValue( (Boolean) alRow.get(iColNum));   
			t_cell.setCellValue((Boolean) oVal);
			t_cell.setCellType(CellType.BOOLEAN);    				
		} else {
			t_cell.setCellValue(oVal.toString());
			t_cell.setCellType(CellType.STRING);    	
		}
		this.m_objSheet.setColumnWidth( _iPrefColNum, EXCEL_DEFAULT_COLUMN_WIDTH);

	}

	protected void errorMessage(String a_message)
	{
		if ( this.m_listener != null )
		{
			this.m_listener.message(a_message);
		}
	}

}
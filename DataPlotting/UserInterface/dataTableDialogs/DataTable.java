/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 7, 2021
 * Version: 2022.0
 */
package dataTableDialogs;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTable;

import ultilInputOutput.FileChoiceUtil;

/**A specialized JTable in which the top row
 * contains headers. Row sorting leaves headers alone
   and headers are colored differently. Also responds to copy, cut, paste
   and select all keyboard shortcuts in the expected way*/
public class DataTable extends JTable implements TableReader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataTable(Object[][] objects, Object[] objects2) {
			super(objects, objects2);
			onConstruction();
	}

	public DataTable(int i, int j) {
		super(i, j);
		onConstruction();
	}
	
	private void onConstruction() {
		addKeyListener(new  DialogKeyListener());
		createSmartRowSorter();
		setColumnSelectionAllowed(true);
		setRowSelectionAllowed(true);
		setCellSelectionEnabled(true);
		setAutoResizeMode(DataTable.AUTO_RESIZE_OFF);
		
		//area.setMinimumSize(new Dimension());
		for(int col=0; col<getColumnCount(); col++) {
			getColumnModel().getColumn(col).setPreferredWidth(120);
		}
	}
	
	private void createSmartRowSorter() {
		setRowSorter(new SmartRowSorter(getModel(), this));
	}
	

	
	
	
	
	/**Assuming the clipboard has a tab delimited text, pastes the text
	  into the cells of the table*/
	public void pasteFromClipboard(Clipboard c) {
		try {
			int row=getSelectedRow();
			int col=getSelectedColumn();
			
			pasteToLocation(c, row, col);
			
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**Assuming the clipboard has a tab delimited text, pastes the text
	  into the cells of the table*/
	protected void pasteToLocation(Clipboard c, int row, int col2) throws UnsupportedFlavorException, IOException {
		String s=c.getData(DataFlavor.stringFlavor).toString();
		putStringIntoTable(row, col2, s);
	}

	/**When given a tab delimited text file with the given rows and columns,
	  pastes it into the table with the upper left of the new data in Row row and
	  Column col2*/
	public void putStringIntoTable(int row, int col2, String s) {
		String[] line = s.split(""+'\n');
		for(String l: line) try {
			String[] parts = l.split(""+'\t');
			
			int col=col2;
			for(String p: parts) try {
				setValueAt(p, row, col);
				col++;
			} catch (Throwable t) {}
			row++;
		}catch (Throwable t) {}
	}
	
	/**Takes the content of a tab delimted Text And put it into the table*/
	public void putFileIntoTable(File f) throws FileNotFoundException {
		String s = FileChoiceUtil.readStringFrom(new FileInputStream(f));
		putStringIntoTable(0,0,s);
		shiftToTopLeft();
	}
	
	/**shifts all the data upward or to the left to fill in missing rows
	 and columns.*/
	public void shiftToTopLeft() {
		shiftMissingRowsUp();
		shiftMissingColsLeft();
	}
	
	/**shifts all the data upward to fill in missing rows.
	  Precondition: the highest row with content must have column headers*/
	private void shiftMissingRowsUp() {
		int i=0;
		for(int row=0; row<getRowCount(); row++) {
			if (isRowEmpty(row)) i++;
			else break;
		}
		if (i==0) return;
		
		for(int row=i; row<getRowCount(); row++) {
			for(int col=0; col<getColumnCount(); col++) {
				Object v = getValueAt(row, col);
				setValueAt(v, row-i, col);
				setValueAt(null, row, col);
			}
		}
		
	}
	
	/**Moved the contents to ensure that the far left column is not empty*/
	private void shiftMissingColsLeft() {
		int i=0;
		for(int row=0; row<getColumnCount(); row++) {
			if (isColEmpty(row)) i++;
			else break;
		}
		if (i==0) return;
		
		for(int row=0; row<getRowCount(); row++) {
			for(int col=i; col<getColumnCount(); col++) {
				Object v = getValueAt(row, col);
				setValueAt(v, row, col-i);
				setValueAt(null, row, col);
			}
		}
		
	}


/**when given two column indices, looks for null values in the 
 * column to be filled. If the values have data in the reference column
 * refCol, they are filled to match the value located one row above.*/
	public void fillMissingValuesFor(int refCol, int colToBeFilled) {
		for(int row=2; row<=this.lastNonEmptyRow(); row++) {
			if (getValueAt(row, refCol)!=null &&
				getValueAt(row, colToBeFilled)==null) {
				Object newVal = getValueAt(row-1, colToBeFilled);
				setValueAt(newVal, row, colToBeFilled);
			}
		}
	}

	/**Sets the contents of all the cells to null*/
	public void deleteAllCells() {
		int nrows = getRowCount();
		int ncols=getColumnCount();
		for(int r=0; r<nrows; r++) {
			for(int c=0; c<ncols; c++) {
				setValueAt(null, r,c);
			}
		}
	}

	
	protected int lastNonEmptyRow() {
		int last=1;
		for(int row=0; row<getRowCount(); row++) {
			if (!isRowEmpty(row)) last=row;
		}
		return last;
	}
	
	/**Returns true if the given row is empty*/
	protected boolean isRowEmpty(int row) {
		for(int col=0; col<getColumnCount(); col++) {
			Object v = getValueAt(row, col);
			if (v instanceof String && "".equals(v)) continue;
			if (v!=null) return false;
		}
		return true;
	}
	
	protected int lastNonEmptyColumn() {
		int last=1;
		for(int col=0; col<getColumnCount(); col++) {
			if (!isColEmpty(col)) last=col;
		}
		return last;
	}
	
	/**Returns true if the given col is empty*/
	protected boolean isColEmpty(int row) {
		for(int col=0; col<getColumnCount(); col++) {
			Object v = getValueAt(row, col);
			if (v instanceof String && "".equals(v)) continue;
			if (v!=null) return false;
		}
		return true;
	}
	
	/**Writes a tab delimited text file to the path*/
	public void writeTableToFile(File f) {
		if (f==null) return;
		try {
			StringBuilder output=new StringBuilder();
			for(int row=0; row<=lastNonEmptyRow(); row++) {
				for(int col=0; col<=lastNonEmptyColumn(); col++) {
					if(col>0) output.append('\t');
					Object valueAdded = getValueAt(row, col);
					if (valueAdded!=null)output.append(valueAdded+"");
				}
				output.append('\n');
			}
			BufferedWriter writer = new BufferedWriter( new FileWriter(f.getAbsolutePath()));
			writer.write(output.toString());
			writer.flush();
			writer.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**Handles key events and copies to the clipboard*/
	public class DialogKeyListener implements KeyListener, Transferable {

		private String copiedString;

		@Override
		public void keyPressed(KeyEvent arg0) {
			
			if (arg0.getKeyCode()==KeyEvent.VK_V) {
				if (arg0.isControlDown()||arg0.isMetaDown()) {
					Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
					
					pasteFromClipboard(c);
					arg0.consume();
				}
			}
			
			if (arg0.getKeyCode()==KeyEvent.VK_A) {
				if (arg0.isControlDown()||arg0.isMetaDown()) {
					selectAll();
				}
			}
			
			if (arg0.getKeyCode()==KeyEvent.VK_C) {
				if (arg0.isControlDown()||arg0.isMetaDown()) {
					Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
					
					copyToClipboard(c);
					arg0.consume();
				}
			}
			
			if (arg0.getKeyCode()==KeyEvent.VK_X) {
				if (arg0.isControlDown()||arg0.isMetaDown()) {
					Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
					
					copyToClipboard(c);
					deleteSelectedCells();
					arg0.consume();
				}
			}
			
			if (arg0.getKeyCode()==KeyEvent.VK_BACK_SPACE||arg0.getKeyCode()==KeyEvent.VK_DELETE) {
				
					deleteSelectedCells();
					arg0.consume();
			}
		}

		private void deleteSelectedCells() {
			int[] rows = getSelectedRows();
			int[] cols=getSelectedColumns();
			
			
			for(int r: rows) {
				for(int c: cols) {
					setValueAt(null, r,c);
				}
			}
		}
		
		

		private void copyToClipboard(Clipboard clip) {
			int[] rows = getSelectedRows();
			int[] cols= getSelectedColumns();
			
			String st="";
			
			for(int r: rows) {
				for(int c: cols) {
					Object value = getValueAt(r, c);
					if (value!=null)
					st+=value.toString()+'\t';
					else st+=" "+'\t';
				}
				st+='\n';
			}
			this.copiedString=st;
			clip.setContents(this, null);
			
			System.out.println(st);
		}

		

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
			if (arg0.equals(DataFlavor.stringFlavor))
			return this.copiedString;
			return null;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			// TODO Auto-generated method stub
			return new DataFlavor[] {DataFlavor.stringFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor arg0) {
			if (arg0.equals(DataFlavor.stringFlavor)) return true;
			return false;
		}
		
	}

}

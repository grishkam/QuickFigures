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
 * Date Created: Mar 26, 2022
 * Date Modified: May 9, 2023
 * Version: 2023.2
 */
package plateDisplay;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import dataTableDialogs.DataTable;
import dataTableDialogs.SmartDataInputDialog;
import dataTableDialogs.TableReader;
import fileread.PlotType;
import logging.IssueLog;
import plates.BasicCellAddress;
import plates.Plate;
import plates.PlateCell;

/**
 
 * 
 */
public class ShowPlate {

	/**formulas that reference another sheet are not automatically understood to be formulas by libre office nad excel
	 * this feature is not yet implemented*/
	boolean useFormula=true;
	
	public void showVisiblePlate(Plate p) {
		SmartDataInputDialog dialog =  new SmartDataInputDialog(new DataTable(300, 15), PlotType.COLUMN_PLOT_TYPE);
		showPlate(dialog.getDataTable(), p);
		dialog.showDialog();;
	}
	
	/**Adds cells to the table based on the current plate*/
	public void showPlate(TableReader table, Plate plate) {
		
		/**for(int i=0; i<plate.getNRows(); i++) {
			table.setValueAt(Plate.getCharForIndex(i), i+1, 0);
		}*/
		/**Sets the title*/
		table.setValueAt(plate.getPlateName(), 0, 0);
		
		
		/**Creates the hues*/
		if(plate.getHueList()!=null) {
			ArrayList<Color> c=new ArrayList<Color>();
			c.addAll(plate.getHueList());
			table.setupColorMap( c);
		} else {
			IssueLog.log("group hues not setup yet");
		}
		
		/**sets the column names*/
		for(int i=0; i<plate.getNCol(); i++) {
			int i3 = i+1;
			if(plate.addressMod!=null)
				i3+=plate.addressMod.getColShift();
			table.setValueAt(""+i3,0, i+1);
			table.setCellBorder(1,0, i+1);
			
			
		}
		
		
		/**Sets the row names*/
		for(int i=0; i<plate.getNRow(); i++) {
			int i2 = i;
			if(plate.addressMod!=null)
				i2+=plate.addressMod.getRowShift();
			table.setValueAt(""+BasicCellAddress.getCharForIndex(i2), i+1, 0);
			table.setCellBorder(0,  i+1, 0);
		}
		
		
		for(PlateCell cell: plate.getPlateCells()) try {
			BasicCellAddress index = cell.getAddress();
			int rowR = index.getRow()+1;
			int ColC = index.getCol()+1;
			String shortLabel = cell.getShortLabel();
			
			if(this.useFormula&&cell.getSpreadSheetRow()!=null) {
				//shortLabel="="+cell.getFormulaForShortName();//+cell.getSourceSheetName()+"!"+".A"+cell.getSpreadSheetRow();
				
			}
				
			table.setValueAt(shortLabel, rowR, ColC);
			
			
			
			if(plate.colorTheText) {
				table.setRichText(new Color[] {cell.getColor().darker()}, new String[] {shortLabel}, rowR, ColC);
			} else 
				table.setCellColor(cell.getColor(), rowR, ColC);
			table.setWrapTextAt(rowR, ColC);
			
			boolean needsRowHeightAdjust = shortLabel.contains(""+'\n') && !plate.horizontalOrientation();
			
			
			if(needsRowHeightAdjust) {
			
				int len1= shortLabel.split(""+'\n').length;
				int base=300;
				if(len1*base>300 && table.getRowHeight(rowR)< len1*base) 
					
					table.setRowHeight(rowR, len1*base);
			
				}
			
		} catch (Throwable t) {
			IssueLog.log("something went wrong  "+cell.getAddress());
			t.printStackTrace();
		}
		
		
		
		table.mergeIdenticalCells(!plate.horizontalOrientation(), new int[] {1,12,1,8});
		
		if(useFormula)for(PlateCell cell: plate.getPlateCells()) try {
			
			BasicCellAddress index = cell.getAddress();
			int rowR = index.getRow()+1;
			int ColC = index.getCol()+1;
			if(this.useFormula&&cell.getSpreadSheetRow()!=null) {
				String formulaLabel="="+cell.getFormulaForShortName();//+cell.getSourceSheetName()+"!"+".A"+cell.getSpreadSheetRow();
				table.setValueAt(formulaLabel, rowR, ColC);
			}
				
		
		}
		catch (Throwable t) {
			IssueLog.log("something went wrong while setting formula "+cell.getAddress());
			t.printStackTrace();
		}
	}
	
	/**Colors the cells based on the content of the label*/
	public static ArrayList<Color> colorCellsBasedOnShortLabels(ArrayList<PlateCell>  cells) {
		ArrayList<String> row1=new ArrayList<String>();//row 1 similarity will determine the hues
		ArrayList<String> row2=new ArrayList<String>();//row 2 similarity will determine the brightness
		HashMap< String, Integer> rowmap1=new HashMap<String, Integer>();//row 1 similarity will determine the hues
		HashMap< String, Integer> rowmap2=new HashMap< String, Integer>();//row 2 similarity will determine the brightness
		String regex = ""+'\n';
		ArrayList<Color> colors = new ArrayList<Color>();
		
		int i=0;
		int j=0;
		for(PlateCell cell: cells) {
			if(cell.getCellText()==null)
				continue;
			String label = cell.getShortLabel();
			
			String[] splitPart = label.split(regex);
			
			String row1Name = splitPart[0].trim();
			
			if(!rowmap1.containsKey(row1Name) )	{
					row1.add(row1Name);
					rowmap1.put(row1Name, i);
					i++;
					
				}
			
			if(splitPart.length>1){
				String row2Name = splitPart[1];
				if(!rowmap2.containsKey(row2Name) )	{
					row2.add(row2Name);
					rowmap2.put(row2Name, j);
					j++;
					
				}
				
			}
			
		}
		
	
		
		for(PlateCell cell: cells) {
			if(cell.getCellText()==null)
				continue;
			String label = cell.getShortLabel();
			
			String[] splitPart = label.split(regex);
			String row1Name = splitPart[0].trim();
		
			
			int section = row1.indexOf(row1Name);
			int block = 1;
			if(splitPart.length>1){
				String row2Name = splitPart[1];
				block =row2.indexOf(row2Name);
			}
			Color color = Plate.determineHueForCell(row1.size()+1, section, row2.size()+1, block, true);
			cell.setColor(color);
			//IssueLog.log("Color for "+row1Name+" and "+row2Name+" is "+color);
			//IssueLog.log("Color for "+section+"/"+row1.size()+" and "+block+"/"+row2.size()+" is "+color);
			colors.add(color);
		}
		
		return colors;
	}
	
	
	public static void main(String[] args) {
		new ShowPlate().showVisiblePlate(new Plate());
	}
}

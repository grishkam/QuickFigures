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
 * Date Modified: May 26, 2022
 * Version: 2022.1
 */
package plates;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import logging.IssueLog;


/**
 
 * 
 */
public class Plate {
	
	/**
	 * 
	 */
	
	String formatName="Generic Plate";
	int nCol=12;
	int nRow=8;
	private int blockWidth;
	private int blockHeight;
	
	/**List of all the plate cells*/
	ArrayList<PlateCell> cellList=new ArrayList<PlateCell>();
	
	/**List of all the plate cells*/
	ArrayList<PlateCell> availableCellList=new ArrayList<PlateCell>();
	
	PlateOrientation oritenation=PlateOrientation.STANDARD;
	private String plateName="";
	
	/**determines whether the group are ordered up/down or left/right*/
	private boolean flipGroups=false;
	private ArrayList<Integer> usedSections=new ArrayList<Integer>();
	public HashMap<Integer, Color> hues;//a map with the hues for each section
	private boolean hueMismash=true;
	private int lastB;
	
	
	
	public Plate() {
		availableCellList=createPlaceCells();
	}
	public Plate(int row, int col, PlateOrientation orient, int blockWidth, int blockHeight, boolean flipGroups) {
		this.nCol=col;
		this.nRow=row;
		this.oritenation=orient;
		this.blockWidth=blockWidth;
		this.blockHeight=blockHeight;
		this.flipGroups=flipGroups;
		availableCellList=createPlaceCells();
		Collections.sort(availableCellList, new PlateCellComparotor());
		Collections.sort(cellList, new PlateCellComparotor());
		assignHuesToSections( usedSections.size());
	}
	
	public Plate createSimilar() {
		return new Plate(nRow, nCol, oritenation, blockWidth, blockHeight, flipGroups);
	}
	
	/**returns true if the cell of the given address is available*/
	public boolean isCellAvailable(BasicCellAddress b) {
		for(PlateCell i: availableCellList) {
			if(i.getAddress().matches(b))
				return true;
		}
		
		return false;
	}
	
	/**returns the row/col address of the index. Depending on the system used to iterate through the plate
	public BasicCellAddress getIndexAddress(int index) {
		if(index*(1+skipRows)>=nRow*nCol) {
			IssueLog.log( "no valid address");
			return null;
			}
		CellAddress address = new CellAddress(0,0);
		
		for(int i=1; i<=index; i++) address.moveToNextCell();
		return address; */
		/**if(oritenation==PlateOrientation.FLIP) {
			int rowIndex = index%nRow;
			int colIndex = index/nRow;
			
			if(skipRows>0)
				colIndex*=1+this.skipRows;//if there are gap rows meant as spacers or replicates
			
			rowIndex=this.nRow-rowIndex-1;
			
			char letter=(char)(A_Index+rowIndex);
			String addressText = ""+letter+(colIndex+1);
			
			return addressText;
		}
		
		int colIndex = index%nCol;
		int rowIndex = index/nCol;
		if(skipRows>0)
			rowIndex*=1+this.skipRows;//if there are gap rows meant as spacers or replicates
		
		char letter=(char)(A_Index+rowIndex);
		return ""+letter+(colIndex+1);
	}*/
	
	
	/**creates a list of plate cells
	 * @return */
	public ArrayList<PlateCell> createPlaceCells() {
		ArrayList<PlateCell> names=new ArrayList<PlateCell>();
		
		for(int i=0; i<nRow; i++) {
			for(int j=0; j<nCol; j++) {
			BasicCellAddress a1 = new BasicCellAddress(i,j);
			PlateCell cell = new PlateCell(a1);
			cellList.add(cell);
			names.add(cell);
			}
		}
		return names;
	}
	
	/**returns a list of plate cells*/
	public ArrayList<PlateCell> getPlateCells() {
		return this.cellList;
	}
	/**
	 * @return the number of rows
	 */
	public int getNRow() {
		return nRow;
	}
	
	/**
	 * @return the number of columns
	 */
	public int getNCol() {
		return nCol;
	}
	
	
	/**A cell address*/
	public class CellAddress extends BasicCellAddress {
		
		
		
		/**
		 * @param row
		 * @param col
		 */
		public CellAddress(int row, int col) {
			super(row, col);
			// TODO Auto-generated constructor stub
		}

		
		
		/**advances to the next cell
		public void moveToNextCell() {
			int nextRow=getRow()+oritenation.yFlow;
			int nextCol=getCol()+oritenation.xFlow;
			*/
			/**if moving accross a row and reached the end of a block*/
			/**boolean endOfColLock = oritenation.xFlow>0&&blockSize!=0&&getCol()>0&&(getCol()+1)%blockSize==0;
			if (endOfColLock) {
				
				nextCol-=blockSize;
				
				nextRow++;
				nextRow+=skipRows;
				
				if(nextRow>=nRow) {
					nextCol+=blockSize;
					nextRow=0;
				}
				
			}else 
			if(nextRow>=nRow)
				{
				nextRow=0;
				nextCol++;
				nextCol+=skipRows;
				
				
				}
				else 
					if(nextCol>=nCol)
					{
				nextCol=0;
				nextRow++;
				nextRow+=skipRows;
					}
				
			
			row=nextRow;
			col=nextCol;
			
		}
		*/
	}

	

	/**
	 * @param address
	 * @return
	 */
	public int getSection(BasicCellAddress address) {
		int blockWidth = getBlockWidth();
		int blockHeight = getBlockHeight();
		
		int u = getUAxisLocation(address)/blockWidth;
		int v = getVAxisLocation(address)/blockHeight;
				
		int blocksPersection = this.getUAxisWidth()/blockWidth;
		
		int section = v*blocksPersection+u;
		if(!flipGroups) {
			int b_perSection = getUAxisWidth()/blockHeight;
			section = u*b_perSection+v;
				{
					//IssueLog.log(" factor will be multiplied "+b_perSection+" for "+address.toString());
				//	IssueLog.log(" u= "+u +" v="+v+ " section="+section);
				}
			
		}
		
		
		if(!usedSections.contains(section))
			usedSections.add(section);
		return section;
	}
	/**
	 * @return
	 */
	public int getBlockWidth() {
		int blockWidth=this.blockWidth;
		if(this.blockWidth==0)
			blockWidth= getUAxisWidth();
		return blockWidth;
	}
	/**
	 * @return
	 */
	public int getBlockHeight() {
		int blockHeight=this.blockHeight;
		if(this.blockHeight==0)
			blockHeight=getVAxisWidth();
		return blockHeight;
	}
	/**
	 * @return
	 */
	private int getUAxisLocation(BasicCellAddress address) {
		if(horizontalOrientation())
			return address.getRow();
		return address.getCol();
	}
	private int getVAxisLocation(BasicCellAddress address) {
		int possibleV = address.getRow();
		if(horizontalOrientation())
			possibleV = address.getCol();
		if(invertV())
			possibleV =getVAxisWidth()-possibleV-1;
		return possibleV;
	}
	
	
	/**returns true if the v axis is flipped
	 * @return
	 */
	private boolean invertV() {
		if(horizontalOrientation())
			return false;
		return true;
	}
	/**
	 * @return
	 */
	public boolean horizontalOrientation() {
		return PlateOrientation.STANDARD==oritenation;
	}
	private int getUAxisWidth() {
		if(horizontalOrientation())
			this.getNRow();
		return this.getNCol();
		};
	private int getVAxisWidth() {
		if(horizontalOrientation())
			this.getNCol();
		return this.getNRow();
		};


	class PlateCellComparotor implements Comparator<PlateCell> {

		@Override
		public int compare(PlateCell o1, PlateCell o2) {
			int s1 = getSection(o1.address);
			int s2 = getSection(o2.address);
			if(s1>s2)
				return 1;
			if(s2>s1)
				return -1;
			
			/***/
			int v1 = getVAxisLocation(o1.address);
			int v2 = getVAxisLocation(o2.address);
			if(v1>v2)
				return 1;
			if(v2>v1)
				return -1;
			
			
			/***/
			int u1 = getUAxisLocation(o1.address);
			int u2 = getUAxisLocation(o2.address);
			if(u1>u2)
				return 1;
			if(u2>u1)
				return -1;
			
			
			
			return 0;
		}}


	/**
	 * @param string
	 */
	public void setPlateName(String string) {
		plateName=string;
		
	}
	public String getPlateName() {return plateName;}
	
	public void assignHuesToSections(double size) {
		HashMap<Integer, Color> hudes=new HashMap<Integer, Color>();
		
		
		for(int i=0; i<this.cellList.size();i++) {
			PlateCell cell= this.cellList.get(i);
			int section = this.getSection(cell.getAddress());
			int repeat = (i/(getBlockWidth()));
			
			 
			float theHue = (float)(1.0*(section/size))*0.9f;
			if(hueMismash) {
				int h=(int) (theHue*10000);
				if(section%2==0)
					h=h/2;
				else {
					h=(h-1)/2+5000;
				}
				theHue=h/10000.0f;
			}
			
			float theBrightness = (float) (1f*(repeat%getBlockHeight())/getBlockHeight()+0.2)/2f;//number should always be a max of 1 or hues will not match
			Color c = Color.getHSBColor(theHue, theBrightness, 1f);
			
			hudes.put(i, c);
			//Color h = hudes.get(this.getSection(cell.getAddress()));
			cell.setColor(c);
		}
		this.hues=hudes;
	}
}

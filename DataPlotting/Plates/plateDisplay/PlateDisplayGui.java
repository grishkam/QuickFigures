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
 * Date Created: May 16, 2022
 * Date Modified: Dec 10, 2022
 * Version: 2023.2
 */
package plateDisplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.layoutHandles.AddLabelHandle;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import plates.BasicCellAddress;
import plates.Plate;
import plates.PlateCell;
import undo.CombinedEdit;
import undo.Edit;

public class PlateDisplayGui extends GraphicLayerPane {

	private Plate plate;
	private int width;
	private int height;
	int font =5;
	Color[] bColors=new Color[] {Color.red, Color.green,  Color.blue, Color.cyan, Color.magenta, Color.yellow};
	Color[] colors=new Color[] {Color.red, Color.green,  Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.orange, Color.pink,  Color.lightGray, Color.yellow.darker(), Color.BLACK, Color.red.darker(), Color.green.darker(), Color.blue.darker(),  Color.cyan.darker(), Color.magenta.darker(), Color.yellow.darker()};
	ArrayList<Color> moreColors=new ArrayList<Color>();
	
	private boolean showSampleNames=false;
	private boolean trimIdenticalSections=false;
	private HashMap<PlateCell, RectangularGraphic> cellMap=new HashMap<PlateCell, RectangularGraphic>();
	
	/**
	 * @param name
	 */
	public PlateDisplayGui(String name, Plate plate) {
		super(name);
		this.plate=plate;
		width=600;
		height=400;
	
		empty();
		for(int i=0; i<20; i++)
		for(Color c: bColors) {
			for(int j=0 ;j<i; j++)
				c=c.darker();
			moreColors.add(c);
		}
	}

	/**
	 * 
	 */
	public void empty() {
		for(ZoomableGraphic g: this.getAllGraphics()) {
			this.remove(g);
		}
		cellMap.clear();
	}
	
	/**draws the layer*/
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		//this.updatePlateDisplay();
		super.draw(graphics, cords);
	}
	
	/**updates the plate display based on new settings to the plate configuration*/
	public void updatePlateDisplay() {
		font=5;
		empty() ;
		int cellWidth = width/getPlate().getNCol();
		int cellHeeght = height/getPlate().getNRow();
		BasicLayout layout = new BasicLayout(getPlate().getNCol(), getPlate().getNRow(), cellWidth, cellHeeght, 4,4, true);
		layout.setLabelSpaces(50, 0, 50, 0);
		layout.resetPtsPanels();
		DefaultLayoutGraphic layoutGraphic = new DefaultLayoutGraphic(layout);
		this.add(layoutGraphic);
		
		//new AddLabelHandle(layoutGraphic, AddLabelHandle.ROWS, 1, false).performAllLabelAddition(null, new CombinedEdit(), );;
		for(int n=0; n<layout.nPanels()&&n<getPlate().getPlateCells().size(); n++) try {
			
			PlateCell plateCell = getPlate().getPlateCells().get(n);
			addPlateCellForLayout(layout, plateCell, false);
		} catch (Throwable t) {
			IssueLog.log(t);
		}
		
		for(PlateCell c: getPlate().getBannedCell()) {
			addPlateCellForLayout(layout, c, true);
		}
		
		
		 if(font>20)
			 font=20;
		String[] rows = new String[] {"A", "B", "C","D", "E", "F", "G", "H"};
		rows=BasicCellAddress.namesOfAxisRows(layout.nRows(), getPlate().addressMod);
		for(int n=0; n<layout.nRows(); n++) try {
			BasicLayout l2 = layout.makeAltered(LayoutSpaces.ROWS);
			
			TextGraphic t=new TextGraphic(rows[n]+"");
			Rectangle2D r= l2.getPanelAtPosition(n+1, 1);
			t.setFontSize(font);
			t.setLocation(RectangleEdges.getLocation(RectangleEdges.LEFT, r.getBounds()));
			this.add(t);
			
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		
		rows=BasicCellAddress.namesOfAxisCols(layout.nColumns(), getPlate().addressMod);
		for(int n=0; n<layout.nColumns(); n++) try {
			BasicLayout l2 = layout.makeAltered(LayoutSpaces.COLS);
			TextGraphic t=new TextGraphic(rows[n]+"");
			Rectangle2D r= l2.getPanelAtPosition(1,n+1);
			t.setFontSize(font);
			t.setLocation(RectangleEdges.getLocation(RectangleEdges.TOP, r.getBounds()));
			t.moveLocation(0, 20);
			this.add(t);
			
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}

	/**adds a plate cell graphic to depict the given plate cell
	 * @param layout
	 * @param plateCell
	 */
	public void addPlateCellForLayout(BasicLayout layout, PlateCell plateCell, boolean banned) {
		
		Rectangle2D panel = layout.getPanelAtPosition(plateCell.getAddress().getRow()+1, plateCell.getAddress().getCol()+1);
		
		RectangularGraphic r = RectangularGraphic.blankRect(panel.getBounds(),plate.colorTheText?Color.black: plateCell.getColor());
		
		cellMap.put(plateCell, r);
		if(Plate.BY_ADDRESS.contentEquals(""+plateCell.getTagMap().get("Specified"))) {
			r.setStrokeWidth(6);
			r.setDashes(new float[] {5,5});
			
		}
		
		this.add(r);
		r.getTagHashMap().put("Cell", plateCell);
		if(banned)
			r.setFillColor(Color.black);
		else 
			createCellLabel(plateCell, panel, r);
	}

	/**
	 * @param plateCell
	 * @param panel
	 * @param r
	 */
	public void createCellLabel(PlateCell plateCell, Rectangle2D panel, RectangularGraphic r) {
		
		TextGraphic t = createCellLabel(plateCell);
		
		if(t==null)
			return;
		font = (int) (panel.getHeight()/2);
		if(t instanceof ComplexTextGraphic) {
			ComplexTextGraphic c=(ComplexTextGraphic) t;
			if(c.getParagraph().size()>1||c.getParagraph().getLastLine().getText().length()>4)
				font = (int) (panel.getHeight()/3);//for multiline labels
			if(c.getParagraph().getLastLine().getText().length()>5)
				font = (int) (panel.getHeight()/5);//for multiline labels
		}
		t.setFontSize(font);
		t.setLocation(RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, r.getBounds()));
		t.moveLocation(1,font);
		if(plate.colorTheText) {
			t.setTextColor(plateCell.getColor().darker());
		}
		
		t.getTagHashMap().put("Cell", plateCell);
		this.add(t);
	}
	
	/**
	 * @param cellPress
	 * @param cellDrag
	 * @return 
	 */
	public ArrayList<PlateCell> selectCell(PlateCell cellPress, PlateCell cellDrag) {
		ArrayList<PlateCell> cells=new ArrayList<PlateCell>();
		if(cellPress==null&&cellDrag==null)
			return cells;
		if(cellPress==null&&cellDrag!=null) {
			cells.add(cellDrag);
			return cells;
			}
		if(cellPress!=null&&cellDrag==null) {
			cells.add(cellPress);
			return cells;
			}
		int minCol = Math.min(cellPress.getAddress().getCol(), cellDrag.getAddress().getCol());
		int minRow = Math.min(cellPress.getAddress().getRow(), cellDrag.getAddress().getRow());
		int maxCol = Math.max(cellPress.getAddress().getCol(), cellDrag.getAddress().getCol());
		int maxRow = Math.max(cellPress.getAddress().getRow(), cellDrag.getAddress().getRow());
		for(PlateCell cell: cellMap.keySet()) {
			int c = cell.getAddress().getCol();
			int r = cell.getAddress().getRow();
			if(c>=minCol&&c<=maxCol&r>=minRow&&r<=maxRow) {
				selectCell(cell, false);
				cells.add(cell);
			}
			else selectCell(cell, true);
			
		}
		
		return cells;
	}
	
	public ArrayList<PlateCell> getSelectedCells() {
		ArrayList<PlateCell> cells=new ArrayList<PlateCell>();
		
		for(PlateCell cell: cellMap.keySet()) {
			int c = cell.getAddress().getCol();
			int r = cell.getAddress().getRow();
			if(cellMap.get(cell).getTag("Cell_selected")==Boolean.TRUE) {
				
				cells.add(cell);
			}
			
			
		}
		return cells;
	}
	
	public void selectCell(PlateCell cell1, boolean deselect) {
		RectangularGraphic r = cellMap.get(cell1);
		if(r==null)
				{IssueLog.log("could not find cell ");}
		
		r.setFillColor(Color.lightGray);
		boolean banned = plate.getBannedCell().contains(cell1);
		if(banned)
			r.setFillColor(Color.DARK_GRAY);
		
		if(deselect) {
			r.setFillColor(Color.white);
			if(banned)
				r.setFillColor(Color.black);
		}
		r.getTagHashMap().put("Cell_selected", !deselect);
	}

	/**returns a label for the cell
	 * @param plateCell
	 * @return
	 */
	public TextGraphic createCellLabel(PlateCell plateCell) {
		Integer spreadSheetRow = plateCell.getSpreadSheetRow();
		if(!showSampleNames&&spreadSheetRow ==null)
			return null;
		if(!showSampleNames) {
				TextGraphic t=new TextGraphic(spreadSheetRow+"");
				return t;
		}
		else {
			String label = plateCell.getShortLabel();
			
			String[] labels = label.split(""+'\n');
			int trimLength=0;
			if(labels.length==2 &&trimIdenticalSections) {
				for(int i=0; i<labels[0].length()&&i<labels[1].length(); i++) {
					String l0 = labels[0];
					String l1 = labels[1];
					if(l0.charAt(i)!=l1.charAt(i))
						break;
					else
						{trimLength++;};
				}
				
				if(trimLength>0)
					{
					labels[0] = labels[0].substring(trimLength);
					labels[1] = labels[1].substring(trimLength);
					}
			}
			ComplexTextGraphic t = new ComplexTextGraphic(labels);
			return t;
			
		}
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param createPlate
	 */
	public void setPlate(Plate createPlate) {
		this.plate=createPlate;
		
	}
	
	/**
	 * @param buildPlate
	 * @param displayPlate
	 */
	public void setPlate(ArrayList<Plate> buildPlate,int displayPlate) {
		if(buildPlate.size()>displayPlate)
			setPlate(buildPlate.get(displayPlate));
		else {
			setPlate(buildPlate.get(0));
			IssueLog.log("used asked for preview "+displayPlate+ " but plate list length was too short "+buildPlate.size());
		}
	}

	public boolean isShowSampleNames() {
		return showSampleNames;
	}

	public void setShowSampleNames(boolean showSampleNames) {
		this.showSampleNames = showSampleNames;
	}

	public Plate getPlate() {
		return plate;
	}

	

	

}

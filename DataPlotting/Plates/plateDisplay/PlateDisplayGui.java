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
 * Date Modified: May 26, 2022
 * Version: 2022.1
 */
package plateDisplay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
	Color[] bColors=new Color[] {Color.red, Color.green,  Color.blue, Color.cyan, Color.magenta, Color.yellow};
	Color[] colors=new Color[] {Color.red, Color.green,  Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.orange, Color.pink,  Color.lightGray, Color.yellow.darker(), Color.BLACK, Color.red.darker(), Color.green.darker(), Color.blue.darker(),  Color.cyan.darker(), Color.magenta.darker(), Color.yellow.darker()};
	ArrayList<Color> moreColors=new ArrayList<Color>();
	
	private boolean showSampleNames=false;
	private boolean trimIdenticalSections=false;
	
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
	}
	
	/**draws the layer*/
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		this.updatePlateDisplay();
		super.draw(graphics, cords);
	}
	
	public void updatePlateDisplay() {
		empty() ;
		int cellWidth = width/plate.getNCol();
		int cellHeeght = height/plate.getNRow();
		BasicLayout layout = new BasicLayout(plate.getNCol(), plate.getNRow(), cellWidth, cellHeeght, 4,4, true);
		layout.setLabelSpaces(50, 0, 50, 0);
		layout.resetPtsPanels();
		DefaultLayoutGraphic layoutGraphic = new DefaultLayoutGraphic(layout);
		this.add(layoutGraphic);
		int font =5;
		//new AddLabelHandle(layoutGraphic, AddLabelHandle.ROWS, 1, false).performAllLabelAddition(null, new CombinedEdit(), );;
		for(int n=0; n<layout.nPanels(); n++) try {
			PlateCell plateCell = plate.getPlateCells().get(n);
			Rectangle2D panel = layout.getPanelAtPosition(plateCell.getAddress().getRow()+1, plateCell.getAddress().getCol()+1);
			//Rectangle2D panel = layout.getPanel(n+1);
			//int section = plate.getSection(plateCell.getAddress());
			//Color color1 = moreColors.get(section);
			RectangularGraphic r = RectangularGraphic.blankRect(panel.getBounds(),plateCell.getColor());
			//r.setFillColor();
			this.add(r);
			//IssueLog.log("Drawing plate cell "+plateCell.getAddress().getAddress());
			TextGraphic t = createCellLabel(plateCell);
			if(t==null)
				continue;
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
			
			this.add(t);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		 if(font>20)
			 font=20;
		String[] rows = new String[] {"A", "B", "C","D", "E", "F", "G", "H"};
		rows=BasicCellAddress.namesOfAxisRows(layout.nRows(), plate.addressMod);
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
		
		
		rows=BasicCellAddress.namesOfAxisCols(layout.nColumns(), plate.addressMod);
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

	public boolean isShowSampleNames() {
		return showSampleNames;
	}

	public void setShowSampleNames(boolean showSampleNames) {
		this.showSampleNames = showSampleNames;
	}

}

/**
 * Author: Greg Mazo
 * Date Modified: May 16, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
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
			Integer spreadSheetRow = plateCell.getSpreadSheetRow();
			if(spreadSheetRow ==null)
				continue;
			TextGraphic t=new TextGraphic(spreadSheetRow+"");
			 font = (int) (panel.getHeight()/2);
			
			t.setFontSize(font);
			t.setLocation(RectangleEdges.getLocation(RectangleEdges.LOWER_LEFT, r.getBounds()));
			
			this.add(t);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		 if(font>20)
			 font=20;
		String[] rows = new String[] {"A", "B", "C","D", "E", "F", "G", "H"};
		rows=BasicCellAddress.namesOfAxis(layout.nRows());
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
		
		
		rows=BasicCellAddress.namesOfAxisCols(layout.nColumns());
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

}

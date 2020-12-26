/**
 * Author: Greg Mazo
 * Date Modified: Dec 19, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package selectedItemMenus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.UndoableEdit;

import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import logging.IssueLog;
import standardDialog.colors.ColorInputEvent;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.ColorEditUndo;
import undo.CombinedEdit;

/**
 Changes the frame color if all the selected panels
 */
public class FrameColorButton extends BasicMultiSelectionOperator implements  ColorMultiSelectionOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color color;// the target color
	private RectangularGraphic colorObject;//an object that shows the user the current color
	private ImagePanelGraphic image;//the main image, its frame color defines the icon color

	public  FrameColorButton(ImagePanelGraphic image) {
		this.image=image;
	}
	
	@Override
	public void run() {
		ArrayList<ZoomableGraphic> list = this.getSelector().getSelecteditems();
		CombinedEdit ce=new CombinedEdit();
		for(ZoomableGraphic l: list) {
			if (l instanceof ImagePanelGraphic)
			ce.addEditToList(editFrameColor((ImagePanelGraphic)l));
		}
		
		/**Adds the edit to the undo manager*/
		if (selector!=null&&selector.getGraphicDisplayContainer()!=null)
			{
				selector.getGraphicDisplayContainer().getUndoManager().addEdit(ce);
					}
		else {IssueLog.log("failed to add undo to udo manager "+this);}
	}

	/**changes the frame color of one panel and returns an undo
	 * @param l
	 * @return
	 */
	private UndoableEdit editFrameColor(ImagePanelGraphic l) {
		ColorEditUndo out = new ColorEditUndo(l);
		l.setFrameColor(color);
		return out;
	}

	@Override
	public String getMenuCommand() {
		return "Color frames";
	}

	@Override
	public void onColorInput(ColorInputEvent fie) {
		color=fie.getColor();
		run();
		if (colorObject!=null)
			colorObject.setFillColor(color);
	}

	@Override
	public boolean doesStroke() {
		return true;
	}
	
	public Icon getIcon() {
		return  getItemIcon(true, true);
	}
	
	/**creates an icon*/
	public GraphicDisplayComponent getItemIcon(boolean selected, boolean stroke) {
		GraphicGroup gg=new GraphicGroup();
		gg.getTheLayer().add(RectangularGraphic.blankRect(new Rectangle(0,0,25,25), new Color(0,0,0,0)));
		
		
		if (getTheColor()!=null) {
					Rectangle r=new Rectangle(3,3, 10,10);
					
					RectangularGraphic rect = RectangularGraphic.blankRect(r, getTheColor());
					 rect.setDashes(null);
					 {
						 rect.setStrokeWidth(4);
						 
						 rect.setStrokeColor(getTheColor());
						
						 rect.setFilled(false);
						 colorObject=rect;
						 rect.setStrokeJoin(BasicStroke.JOIN_MITER);
						 rect.setDashes(new float[] {});
						 rect.deselect();
					
						 Rectangle r2=new Rectangle(4,4, 7,7); 
						 RectangularGraphic rect2 = new RectangularGraphic(r2); 
						 rect2.setFillColor(Color.black);
						 rect2.setStrokeWidth(1); rect2.setStrokeColor(Color.black);rect2.makeNearlyDashLess();
						rect2.setStrokeCap(BasicStroke.CAP_SQUARE);
						 
						 Rectangle r3=new Rectangle(0,0, 15,15); 
						 RectangularGraphic rect3 = new RectangularGraphic(r3); 
						 rect3.setFillColor(new Color(0,0,0,0));
						 rect3.setStrokeWidth(1); rect3.setStrokeColor(Color.black);rect3.makeNearlyDashLess();
						
						 gg.getTheLayer().add(rect3);
						 gg.getTheLayer().add(rect);
						 gg.getTheLayer().add(rect2);
						
					 }
					
		}
		
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		 gg.moveLocation(5, 5);
		
		 return output;
	}

	/**
	 * @return
	 */
	private Color getTheColor() {
		if (image!=null) return image.getFrameColor();
		return color;
	}

}

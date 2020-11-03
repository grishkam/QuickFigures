package graphicTools;


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import applicationAdapters.ImageWrapper;
import externalToolBar.TreeIconWrappingToolIcon;
import genericMontageKit.SelectionManager;
import graphicalObjects_BasicShapes.RectangularGraphic;
import utilityClassesForObjects.LocatedObject2D;

public class RectGraphicTool extends GraphicTool {
	
	boolean started=false;
	RectangularGraphic model= new RectangularGraphic(0,0,0,0);
	RectangularGraphic lastSetObject=null;
	{
		model.setStrokeColor(Color.black);
	{super.set=TreeIconWrappingToolIcon.createIconSet(model); super.temporaryTool=true;}}
	
/**	{createIconSet("icons2/RectangleIcon.jpg","icons2/RectangleIconPress.jpg","icons2/RectangleIcon.jpg");
	this.getIconSet().setIcon(0, new GraphicRectToolIcon(0));
	this.getIconSet().setIcon(1, new GraphicRectToolIcon(1));
	this.getIconSet().setIcon(2, new GraphicRectToolIcon(2));
	}
	;*/
	
	
	
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		//if (!(roi2 instanceof RectangularGraphic)) return;
		handleRectangularObject(gmp, roi2);
		
		
	}
	
	
	
	protected RectangularGraphic createNewRect(Rectangle r) {
		return new RectangularGraphic(r);
	}
	
	public void handleRectangularObject(ImageWrapper gmp, Object roi2) {
				
		RectangularGraphic textob;
		establishMovedIntoOrClickedHandle();
		if (roi2 instanceof RectangularGraphic||getPressedHandle()>-1) {
			textob=(RectangularGraphic) roi2;
			
		} else
			{
			started=true;
			textob= createNewRect(new Rectangle(getClickedCordinateX(), getClickedCordinateY(), 15,15));
			textob.copyColorsFrom(model);
			textob.copyAttributesFrom(model);
			//textob.setStrokeColor(getForeGroundColor());
			
		
			gmp.getGraphicLayerSet().getSelectedContainer().add(textob);
			addUndoerForAddItem(gmp, gmp.getGraphicLayerSet().getSelectedContainer(), textob);
			setSelectedHandleNum(2);
			
		//textob.showOptionsDialog();
		}
		setSelectedObject(textob);
		
		
	}
	
	public void mouseReleased() {
		started=false;
		super.mouseReleased();
	}
	
	
	
	private boolean invalid(RectangularGraphic lastSetObject2) {
		if (lastSetObject2.getBounds().getWidth()<5) return true;
		if (lastSetObject2.getBounds().getHeight()<5) return true;
		
		return false;
	}


	public void mouseDragged() {
		if (started) {
			Rectangle2D r = SelectionManager.createRectangleFrom2Points(this.clickedCord(), this.draggedCord());
			if (this.getSelectedObject() instanceof RectangularGraphic) {
				RectangularGraphic rr=(RectangularGraphic) getSelectedObject();
				rr.setRectangle(r);
				lastSetObject=rr;
			}
		}
		else super.mouseDragged();
		
	}
	
	@Override
	public void showOptionsDialog() {
		model.showOptionsDialog();
	}
	
	
	
	@Override
	public String getToolTip() {
			
			return "Draw a "+getShapeName();
		}
	@Override
	public String getToolName() {
		return "Draw "+getShapeName();
	}



	public String getShapeName() {
		return model.getShapeName();
	}



	
	
}

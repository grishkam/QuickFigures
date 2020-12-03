package uiForAnimations;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;

import javax.swing.Icon;
import animations.KeyFrameCompatible;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.BasicShapeGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import standardDialog.GraphicDisplayComponent;

public class KeyFrameAssignRemove extends BasicTimeLineOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean all=false;
	public KeyFrameAssignRemove(boolean a) {
		all=a;
	}

	@Override
	public void run() {
		for(ZoomableGraphic item: array) {
			if (item==null) continue;
			actioinOnSelected(item);
			
		}
		
	}
	
	@Override
	public String getMenuCommand() {
		if (all) return "Remove All Key Frames";
		return "Remove Key Frame";
	}
	
	/**removes the selected item, period. it the item is mortal,
	   it will call its kill() method which should let some listeners know
	   of its demise*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		
		
		
		
		
		if (selectedItem instanceof KeyFrameCompatible ) {
			KeyFrameCompatible  m=(KeyFrameCompatible ) selectedItem;
			int frame = new CurrentFigureSet().getCurrentlyActiveDisplay().getCurrentFrame();
			if (m.getAnimation()==null) return;
			m.getOrCreateAnimation().removeKeyFrame(frame);
			if (all) for(int i=0; i<display.getEndFrame(); i++) m.getOrCreateAnimation().removeKeyFrame(i);
		}
		
		
		
		
	}
	

	static ShapeGraphic createCartoonX(boolean selected) {
		Point p1=new Point(5,0);
		Point p2=new Point(17,24);
		
			ArrowGraphic ag1 =ArrowGraphic.createDefaltOutlineArrow(Color.red.darker(), Color.black);
			ag1.setStrokeWidth(4);
			if (selected) ag1.getBackGroundShape().setFillColor(Color.red);
			ag1.setPoints(p1, p2);
			ag1.setNHeads(0);
			
			ArrowGraphic ag2 = ag1.copy();
			p1=new Point(17,0);
			p2=new Point(5,24);
			ag2.setPoints(p1, p2);
			
			Area s= ag2.getOutline();
			s.add(ag1.getOutline());
			BasicShapeGraphic output = new BasicShapeGraphic(s);
			output.copyAttributesFrom(ag2);
			output.setStrokeColor(Color.black);
			output.setFillColor(Color.red);
			output.setStrokeWidth(2);
			output.setFilled(true);
			output.setAntialize(true);
			return output;
	}
	
	public GraphicDisplayComponent getDeleteIcon(boolean selected) {
		 GraphicDisplayComponent output = new GraphicDisplayComponent(createCartoonX( selected));
		 
		 output.setRelocatedForIcon(false);
		 //output.setSelected(selected);
		 return output;
	}
	
	
	public Icon getIcon() {
		return  getDeleteIcon(true);
	}

}

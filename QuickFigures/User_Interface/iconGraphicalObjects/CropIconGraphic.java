package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RhombusGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import utilityClassesForObjects.DefaultPaintProvider;
import utilityClassesForObjects.PaintProvider;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.SnappingPosition;

/**A class for creation of a folder icon object*/
public class CropIconGraphic extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	boolean open;
	private RhombusGraphic spacefilled;

	
	ArrayList<ShapeGraphic> spokeDots=new ArrayList<ShapeGraphic>();
	
	public  CropIconGraphic() {
	
		createItems() ;
		setItemColors();
		
		addItems();
	}
	
	
	
	
	public void createItems() {
		spacefilled = new RhombusGraphic();
		spacefilled.setRectangle(new Rectangle(0,2,14,10));
		spacefilled.setAntialize(true);
		Rectangle rectsize = new Rectangle(4,4,10,10);
		
		
	
		Rectangle fullRect=new Rectangle(3,3, 12, 12);
		for(int i=0; i<8; i++) {
			int place = i%4;
			Rectangle partRect=new Rectangle(0,0, 2, 15);
			if(i<4) partRect=new Rectangle(0,0, 15, 2);
			if(place==1||place==3) continue;
			SnappingPosition p=new SnappingPosition();
			p.setSnapLocationTypeInternal(place);
			p.setSnapType(SnappingPosition.INTERNAL);
			p.snapRects(partRect, fullRect);
			
			ShapeGraphic bar = RectangularGraphic.filledRect(partRect);
			
			bar.setFillColor(Color.black);
			
			spokeDots.add(bar);
		}
		
		 setItemColors() ;
	}
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getR1rect() {
		 {
			return new Rectangle(0,2,14,10);
		}
	}
	
	public double getR1angle() {
		 {
			return 0;
		}
	}
	
	/**alters r1's properties depending on the condition of the folder.
	   open or closed*/
	public void setR1Dims() {
		spacefilled.setRectangle(getR1rect());
		spacefilled.setAngleBend(getR1angle());
	}
	
	public void setOpen(boolean o) {
		this.open=o;
		this.setR1Dims();
		this.setItemColors();
	}
	
	public void setColor(Color c) {
		setItemColors();
	}
	public void setItemColors() {
		setR1Dims();
		
		
		//	r.setFilled(true);
			PaintProvider pp = spacefilled.getFillPaintProvider();
			
			pp.setColor(getIconColor().brighter());
			pp.setColor(1, getIconColor().darker());
			spacefilled.setStrokeColor(getIconColor().darker());
			
			if (open) {
				pp.setColor(getIconColor().darker());
				//r.setRectangle(new Rectangle(0,4,14,8));
				spacefilled.setStrokeColor(getIconColor().darker().darker());
		}
			 
		
			
			
			
	}
	
	public void addItems() {
		
		for(ShapeGraphic d:spokeDots) {	getTheLayer().add(d);}
	}



	public Color getIconColor() {
		if (iconColor==null) {
			iconColor=Color.black;
		}
		return iconColor;
	}



	
}
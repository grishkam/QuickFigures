package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import standardDialog.GraphicObjectDisplayBasic;
import utilityClassesForObjects.AttachmentPosition;

/**A class for rendering of a cropping icon*/
public class CropIconGraphic extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	boolean open;
	private RectangularGraphic spacefilled;

	
	ArrayList<ShapeGraphic> iconParts=new ArrayList<ShapeGraphic>();
	
	public  CropIconGraphic() {
	
		createItems() ;
		
		addItems();
	}
	
	
	
	/**creates the graphical objects that will compose the shapes in the icon */
	public void createItems() {
		spacefilled = new RectangularGraphic();
		spacefilled.setRectangle(getR1rect());
		spacefilled.setAntialize(true);
		
		
	
		Rectangle fullRect=new Rectangle(3,3, 12, 12);
		for(int i=0; i<8; i++) {
			int place = i%4;
			Rectangle partRect=new Rectangle(0,0, 2, 15);
			if(i<4) partRect=new Rectangle(0,0, 15, 2);
			if(place==1||place==3) continue;
			AttachmentPosition p=new AttachmentPosition();
			p.setLocationTypeInternal(place);
			p.setLocationType(AttachmentPosition.INTERNAL);
			p.snapRects(partRect, fullRect);
			
			ShapeGraphic bar = RectangularGraphic.filledRect(partRect);
			
			bar.setFillColor(iconColor);
			
			iconParts.add(bar);
		}
		
		
	}
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getR1rect() {
		 {
			return new Rectangle(0,2,14,10);
		}
	}

	
	public void addItems() {
		
		for(ShapeGraphic d:iconParts) {	getTheLayer().add(d);}
	}



	public Color getIconColor() {
		if (iconColor==null) {
			iconColor=Color.black;
		}
		return iconColor;
	}

	/**
	 * @return
	 */
	public static Icon createsCropIcon() {
		return new GraphicObjectDisplayBasic<CropIconGraphic>(new 	CropIconGraphic());
	}

	
}
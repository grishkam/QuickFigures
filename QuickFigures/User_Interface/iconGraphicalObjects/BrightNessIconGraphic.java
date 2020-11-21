package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import utilityClassesForObjects.RectangleEdgePosisions;

/**A class for rendering of a brightness icon
  */
public class BrightNessIconGraphic extends GraphicGroup  implements  RectangleEdgePosisions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	int form;
	private CircularGraphic blackPart;
	private CircularGraphic whiteHalf;
	
	ArrayList<ShapeGraphic> spokeDots=new ArrayList<ShapeGraphic>();
	
	public BrightNessIconGraphic(int open) {
		this.form=open;
		createItems() ;
		setItemColors();
		
		addItems();
	}
	
	
	
	
	public void createItems() {
		Rectangle rectsize = new Rectangle(4,5,10,10);
		
		
		blackPart = CircularGraphic.filledCircle(rectsize);
		whiteHalf= CircularGraphic.halfCircle(rectsize);
		whiteHalf.setFillColor(Color.white);
		blackPart.setStrokeColor(Color.black);
		blackPart.setStrokeWidth(2);
	
		/**creates a ring of dots around the black/white circle*/
		for(int i=0; i<8; i++) {
			ShapeGraphic dot = CircularGraphic.filledCircle(new Rectangle(0,0,2,2));
			if(form==1) dot = RectangularGraphic.filledRect(new Rectangle(-2,0,4,2));
			dot.setLocation(8+8*Math.cos(Math.PI/4*i), 9+8*Math.sin(Math.PI/4*i));
			dot.setFillColor(Color.black);
			dot.setAngle(-Math.PI/4*i);
			spokeDots.add(dot);
		}
		
		 setItemColors() ;
	}
	

	public void setOpen(int o) {
		this.form=o;
		this.setItemColors();
	}
	
	public void setColor(Color c) {
		setItemColors();
	}
	public void setItemColors() {
		 blackPart.setFillColor(getIconColor());
		whiteHalf.setFillColor(Color.white);
	}
	
	/**adds the graphic items for this icon to the layer*/
	public void addItems() {
		getTheLayer().add(blackPart);
		getTheLayer().add(whiteHalf);
		for(ShapeGraphic d:spokeDots) {	getTheLayer().add(d);}
	}


	public Color getIconColor() {
		if (iconColor==null) {
			iconColor=Color.black;
		}
		return iconColor;
	}



	
	
}
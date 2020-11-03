package standardDialog;

import java.awt.Dimension;

import graphicalObjects.BasicCordinateConverter;
import graphicalObjects.ZoomableGraphic;
import utilityClassesForObjects.LocatedObject2D;

public class GraphicSampleComponent extends GraphicComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ZoomableGraphic z;
	int x=0;
	int y=0;
	
	public GraphicSampleComponent(ZoomableGraphic z) {
		super.getGraphicLayers().add(z);
		this.z=z;
	}
	
	public LocatedObject2D zLoc() {
		if (z instanceof LocatedObject2D) {
			return (LocatedObject2D) z;
		}
		return null;
	}
	
	public BasicCordinateConverter getCord() {
	//	if (cords==null) {
		BasicCordinateConverter bcc = new BasicCordinateConverter();
		bcc.setMagnification(1);
		if (zLoc()!=null) {
			bcc.setX(zLoc().getLocationUpperLeft().getX());
			bcc.setY(zLoc().getLocationUpperLeft().getY());
		}
		
		cords=bcc;
		//}
		return cords;
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (zLoc()!=null)  {
			return new Dimension(zLoc().getBounds().width*2, zLoc().getBounds().height*2);
		}
        return new Dimension(100,100);
    }
	
	
	
}

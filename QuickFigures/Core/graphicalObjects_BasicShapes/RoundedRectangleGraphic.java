package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import graphicalObjectHandles.RectangleEdgeHandle;
import graphicalObjectHandles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import objectDialogs.RoundRectGraphicOptionsDialog;

public class RoundedRectangleGraphic extends RectangularGraphic {
	{name="Rounded Rect";}
	/**
	 * 
	 */

	private RectangleEdgeParameter arcw=new RectangleEdgeParameter(this, 40, UPPER_RIGHT, UPPER_LEFT);

	private RectangleEdgeParameter arch=new RectangleEdgeParameter(this, 40,  UPPER_RIGHT, LOWER_RIGHT);;
	
	private static final long serialVersionUID = 1L;
	
	
	public static RoundedRectangleGraphic blankShape(Rectangle r, Color c) {
		RoundedRectangleGraphic r1 = new RoundedRectangleGraphic(r);
		
		r1.setDashes(new float[]{100000,1});
		r1.setStrokeWidth(4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	

	public RoundedRectangleGraphic copy() {
		RoundedRectangleGraphic output = new RoundedRectangleGraphic(this);
		output.arch=arch.copy(output);
		output.arcw=arcw.copy(output);
		return output;
	}
	
	public RoundedRectangleGraphic(Rectangle rectangle) {
		super(rectangle);
	}
	
	public RoundedRectangleGraphic(RectangularGraphic r) {
		super(r);
	}

	public Shape getShape() {
		return new RoundRectangle2D.Double(x,y,getObjectWidth(),getObjectHeight(), getArcw(), getArch());
	}
	
	
	RectangularGraphic rectForIcon() {
		RoundedRectangleGraphic iconshape = blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
		iconshape.setArcw(6);
		iconshape.setArch(6);
		return iconshape;
	}





	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}

	
	
	@Override
	public void showOptionsDialog() {
		new RoundRectGraphicOptionsDialog(this).showDialog();
	}



	public double getArcw() {
		return arcw.getLength();
	}



	public void setArcw(double arcw) {
		this.arcw.setLength(arcw);;
	}



	public double getArch() {
		return arch.getLength();
	}



	public void setArch(double arch) {
		this.arch.setLength( arch);
	}
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		 {
			list.add(0,new RectangleEdgeHandle(this, arch, Color.cyan, 18,0, 0.05));
			list.add(0,new RectangleEdgeHandle(this, arcw, Color.orange, 20,0, -0.05));
		}
		return list;
	}
	
	
	public String getShapeName() {
		return "Rounded Rectangle";
	}
}

package pathGraphicToolFamily;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.Icon;

import applicationAdapters.ImageWrapper;
import externalToolBar.IconWrappingToolIcon;
import graphicTools.GraphicTool;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import standardDialog.GraphicDisplayComponent;
import undo.PathEditUndo;
import utilityClassesForObjects.LocatedObject2D;

public class PathReflactTool extends GraphicTool {
	
	
	private boolean pathChosen;
	private PathGraphic chosenGraphic;
	private PathGraphic pathGraphic;
	private boolean scale=false;
	private boolean rotate=false;
	boolean reflect=false;
	private boolean move;
	private boolean scaleuniform;

	
	private boolean scale() {return scale||scaleuniform;}
	
	public PathReflactTool(int scale) {
		reflect=scale==0;
		this.scale=scale==1;
		this.rotate=scale==2;
		this.move=scale==3;
		this.scaleuniform=scale==4;
		this.set= IconWrappingToolIcon.createIconSet(getDefaultIcon()) ;
	}
	
	public void mousePressed() {
		
		super.mousePressed();
		;
		
		
		
		if (this.getSelectedObject()instanceof PathGraphic) {
			this.pathGraphic=(PathGraphic) getSelectedObject();
			
			//handleSmart = pathGraphic.getSmartHandleList().getHandleNumber(this.getPressedHandle());
		}
		
		onPathPress();
		}

	@Override
	public String getToolName() {
		if (scale()) return "Scale Path";
		if (rotate) return "Rotate Path";
		if (move) return "Shift Path";
		return "Reflect Path";
	}
	
	@Override
	public void mouseReleased() {
		onRelease(this.getImageWrapperClick(),getSelectedObject());
		getImageWrapperClick().getOverlaySelectionManagger().setSelectionstoNull();
	}
	
	public void mouseDragged() {
		ShapeGraphic rect =ArrowGraphic.createLine(Color.BLACK, Color.black, new Point2D.Double(getClickedCordinateX(), this.getClickedCordinateY()), this.draggedCord());
		if (scale()&&chosenGraphic!=null) {
			
			PathGraphic rect2 = new PathGraphic(new Point(0,0)); rect2.setPathToShape(chosenGraphic.getBounds());
			rect2.setFilled(false); rect2.setStrokeColor(Color.BLACK);
			if (scaleuniform) {
				rect2.scaleAbout(this.clickedCord(), scaleLevel()[0]);
			} else 	rect2.scaleAbout(this.clickedCord(), scaleLevel()[0], scaleLevel()[1]);
			rect=rect2;
		}
		getImageWrapperClick().getOverlaySelectionManagger().setSelection(rect, 0);
		getImageWrapperClick().getOverlaySelectionManagger().setSelection(createMirrorGraphic() , 1);
	}
	
	
	public void onPathPress() {
		if (!pathChosen &&pathGraphic!=null) {
			 pathChosen = true;
				this.chosenGraphic=pathGraphic;
		}
	}
	
public void onRelease(ImageWrapper imageWrapper, LocatedObject2D roi2) {
	
			
		getImageWrapperClick().getOverlaySelectionManagger().setSelectionstoNull();
		PathGraphic path2 = createMirrorGraphic();
		if (path2==null) return;
		
		GraphicLayer layer = this.getImageWrapperClick().getGraphicLayerSet();
		if (!this.shiftDown()) {
		
			
			layer.add(path2);
			addUndoerForAddItem( imageWrapper,layer, path2);
		
		
		} else {
			PathEditUndo undo = new PathEditUndo(chosenGraphic);
			if (reflect||scale) path2.setPoints(path2.getPoints(). getOrderFlippedCopy());
			chosenGraphic.getPoints().addAll(path2.getPoints());
			chosenGraphic.updatePathFromPoints();
			undo.saveFinalPositions();
			 
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		}
		
		getImageDisplayWrapperClick().updateDisplay();
		pathChosen = false;
		
		
	}

double[] scaleLevel() {
	
	double dx = draggedCord().getX()-clickedCord(). getX();
	double dy = draggedCord().getY()-clickedCord(). getY() ;
	return new double[] {2*dx/chosenGraphic.getBounds().getWidth(), 2*dy/chosenGraphic.getBounds().getHeight()};
}

PathGraphic createMirrorGraphic() {
	
	PathGraphic p = (PathGraphic) chosenGraphic;
	if (p==null) return null;
	
	double dx = draggedCord().getX()-clickedCord(). getX();
	double dy = draggedCord().getY()-clickedCord(). getY() ;
	
	PathGraphic out = p.copy();
	if (reflect)out.reflectPathAboutLine(this.clickedCord(),this.draggedCord() );
	else if (scale) out.scaleAbout(clickedCord(),scaleLevel()[0], scaleLevel()[1]);
	else if (scaleuniform) out.scaleAbout(clickedCord(),scaleLevel()[0]);
	
	else if (rotate)  {out.rotateAbout(clickedCord(), -BasicGraphicalObject.distanceFromCenterOfRotationtoAngle(this.clickedCord(), this.draggedCord())/**-Math.PI*/);}
	else if (move)  {
		
		out.getPoints().applyAffine(AffineTransform.getTranslateInstance(dx, dy));
		out.updatePathFromPoints();
		}
	
	
	return out;
	
}



	
	public void mouseExited() {
		getImageWrapperClick().getOverlaySelectionManagger().setSelectionstoNull();
		
		
	}
	
	public Icon getDefaultIcon() {
		return new GraphicDisplayComponent(createIcon() );
	}
	
	GraphicGroup createIcon() {
		GraphicGroup out = new GraphicGroup(); ;//RectangularGraphic.blankRect(new Rectangle(0,0,14,12), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
	
		PathGraphic p = new PathGraphic(new Point(2,2));
			p.setStrokeWidth(2);
			p.setFilled(false);
			p.addPoint(new Point(2,2));
			
			p.addPoint(new Point(9,5));
			p.addPoint(new Point(4,3));
			p.addPoint(new Point(9,15));
			p.moveLocation(-2, 0);
		
			p.makeDashLess();
			p.updatePathFromPoints();
			out.getTheLayer().add(p);
			PathGraphic p2 =p.copy();  
			if (scale) {p2.scaleAbout(new Point(14,3), .5);} 
				else if (rotate) {p2.rotateAbout(new Point(7,7), Math.PI);} 
				else if (move) {p2.moveLocation(7, 1);} 
				else p2. reflectPathAboutLine( new Point(10,0), new Point(10,10));
			
			out.getTheLayer().add(p2);
			p.setStrokeColor(Color.green);
			p2.setStrokeColor(Color.red);
			
		;
		//out.setAngle(this.getAngle());
		return out;
	}	
	
	@Override
	public String getToolSubMenuName() {
		return "Transform Path";
	}

}

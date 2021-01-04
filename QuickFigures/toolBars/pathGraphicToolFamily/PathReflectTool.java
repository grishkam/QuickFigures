/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package pathGraphicToolFamily;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.Icon;

import applicationAdapters.ImageWorkSheet;
import graphicTools.GraphicTool;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import icons.IconWrappingToolIcon;
import locatedObject.LocatedObject2D;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.PathEditUndo;

/**a tool that modifies the clicked pathgraphic*/
public class PathReflectTool extends GraphicTool {
	
	public static final int 	REFLECT=0, SCALE=1, ROTATE=2, MOVE=3, UNIFORM_SCALE=4;
	
	
	private boolean pathChosen;
	private PathGraphic chosenGraphic;
	private PathGraphic pathGraphic;
	private boolean scale=false, rotate=false, reflect=false, move=false, scaleuniform=false;

	
	private boolean scale() {return scale||scaleuniform;}
	
	public PathReflectTool(int toolType) {
		reflect=toolType==REFLECT;
		this.scale=toolType==SCALE;
		this.rotate=toolType==ROTATE;
		this.move=toolType==MOVE;
		this.scaleuniform=toolType==UNIFORM_SCALE;
		this.iconSet= IconWrappingToolIcon.createIconSet(getDefaultIcon()) ;
	}
	
	public void mousePressed() {
		
		super.mousePressed();
		;
		
		
		
		if (this.getPrimarySelectedObject()instanceof PathGraphic) {
			this.pathGraphic=(PathGraphic) getPrimarySelectedObject();
			
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
		onRelease(this.getImageClicked(),getPrimarySelectedObject());
		getImageClicked().getOverlaySelectionManagger().setSelectionstoNull();
	}
	
	public void mouseDragged() {
		ShapeGraphic rect =ArrowGraphic.createLine(Color.BLACK, Color.black, new Point2D.Double(getClickedCordinateX(), this.getClickedCordinateY()), this.draggedCord());
		if (scale()&&chosenGraphic!=null) {
			
			PathGraphic rect2 = new PathGraphic(new Point(0,0)); rect2.setAnchorPointsTo(chosenGraphic.getBounds());
			rect2.setFilled(false); rect2.setStrokeColor(Color.BLACK);
			if (scaleuniform) {
				rect2.scaleAbout(this.clickedCord(), scaleLevel()[0]);
			} else 	rect2.scaleAbout(this.clickedCord(), scaleLevel()[0], scaleLevel()[1]);
			rect=rect2;
		}
		getImageClicked().getOverlaySelectionManagger().setSelection(rect, 0);
		getImageClicked().getOverlaySelectionManagger().setSelection(createMirrorGraphic() , 1);
	}
	
	
	public void onPathPress() {
		if (!pathChosen &&pathGraphic!=null) {
			 pathChosen = true;
				this.chosenGraphic=pathGraphic;
		}
	}
	
public void onRelease(ImageWorkSheet imageWrapper, LocatedObject2D roi2) {
	
			
		getImageClicked().getOverlaySelectionManagger().setSelectionstoNull();
		PathGraphic path2 = createMirrorGraphic();
		if (path2==null) return;
		
		GraphicLayer layer = this.getImageClicked().getTopLevelLayer();
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
		getImageClicked().getOverlaySelectionManagger().setSelectionstoNull();
		
		
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
		
			p.makeNearlyDashLess();
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

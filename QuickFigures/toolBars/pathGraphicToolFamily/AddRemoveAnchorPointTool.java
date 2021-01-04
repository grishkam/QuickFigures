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
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import iconGraphicalObjects.IconTraits;
import icons.IconWrappingToolIcon;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import standardDialog.graphics.GraphicDisplayComponent;
import storedValueDialog.ReflectingFieldSettingDialog;

/**A tool for adding and removing points from a PathGraphic. @see PathGraphic*/
public class AddRemoveAnchorPointTool extends PathAnchorPointTool {
	
	
	private boolean remove;
	public boolean oldRemovalFormula;
	

	public AddRemoveAnchorPointTool(boolean remove) {
		this.remove=remove;
		this.iconSet= IconWrappingToolIcon.createIconSet(getDefaultIcon()) ;
		
	}
	

	void performActionOnPath(PathGraphic p,  boolean valid, int anchorPointIndex) {
		if (anchorPointIndex>1000) return;
		
		boolean remove1=remove;
		if(this.shiftDown())remove1=!remove;
		
		Point clickedCord = this.clickedCord();
		
		
		
		
		addOrRemovePointAtLocation(p, remove1, clickedCord);
		
		
	
		
		/**
		pathPoint pnew = new pathPoint(this.getClickedCordinateX()-p.getLocation().getX(), this.getClickedCordinateY()-p.getLocation().getY());
		if (samecurveControlAsLast)	pnew.setCurveControl(p.getPoints().get(i).getCurveControl1());
		if (i<p.getPoints().size())p.getPoints().add(i,pnew);
		else p.getPoints().add(pnew);
		*/
			
		
		
		
	
	
	
		
	}


	public void addOrRemovePointAtLocation(PathGraphic p, boolean remove1, Point2D clickedCord) {
		Point2D locationClickInPathsCords=p.convertPointToInternalCrdinates(clickedCord);//new Point2D.Double(getClickedCordinateX()-p.getLocation().getX(), this.getClickedCordinateY()-p.getLocation().getY());
		
		PathGraphic broken = p.break10(50);
		PathPoint nearest = broken.getPoints().getNearest(locationClickInPathsCords.getX(), locationClickInPathsCords.getY());
		int indexSeg=broken.getPoints().indexOf(nearest);
		int indexPath=indexSeg/50;
		double dt=indexSeg%50; dt/=50;//calculates the t for the clicked point
		
		this.setSelectedHandleNumber(indexPath);
		boolean withInRange = nearest.getAnchor().distance(locationClickInPathsCords)<10;
	
		if (!remove1) {
			if (indexPath< p.getPoints().size()&&withInRange ) {
				p.getPoints().splitPath(indexPath, dt);
				setSelectedHandleNumber(indexPath+1);
			}
					else {
						Double pnew = new Point2D.Double(locationClickInPathsCords.getX(), locationClickInPathsCords.getY());
						 //p.getPoints().add(pnew);
						 p.addPoint(pnew);
						 setSelectedHandleNumber(p.getPoints().size()-1);
					}
			
}
		
		if (remove1) {
			indexPath = removePoint(p,  locationClickInPathsCords);
		}
		
		p.updatePathFromPoints();
	}


	public static int removePoint(PathGraphic p,  Point2D locationClickInPathsCords) {
		PathPoint nearest;
		int indexPath;
		boolean adjectCurvature=true;
		PathPointList pts = p.getPoints();
		nearest =pts .getNearest(locationClickInPathsCords);
		indexPath=pts .indexOf(nearest);
		
		if (!adjectCurvature||indexPath==0||indexPath==pts.size()-1) {
				pts.remove(indexPath);
				if (pts.size()==0){ p.getParentLayer().remove(p); p=null;}//when you delete the last point you no longer need the path
			
			}
		else {
			
			
			//if (oldRemovalFormula) p.getPoints().reverseSplitPath(indexPath, 0.5); else
				p.getPoints().cullPointAndAdjustCurvature(nearest);
		}
			p.updatePathFromPoints();
			p.updateDisplay();
		return indexPath;
	}
	
	
	
	public Icon getDefaultIcon() {
		return new GraphicDisplayComponent(createIcon() );
		//return createImageIcon();
	}
	
	GraphicGroup createIcon() {
		GraphicGroup out = new GraphicGroup(); ;//RectangularGraphic.blankRect(new Rectangle(0,0,14,12), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
			Point2D p1=new Point2D.Double(7, 2);
			Point2D p2=new Point2D.Double(7, 12);
			Point2D p3=new Point2D.Double(2, 7);
			Point2D p4=new Point2D.Double(12,7);
			ArrowGraphic line = ArrowGraphic.createLine(Color.black, null, p1, p2);
			line.setStrokeWidth(2);
			if (!remove)out.getTheLayer().add(line);
			line=ArrowGraphic.createLine(Color.black, null, p3, p4);
			line.setStrokeWidth(2);
			out.getTheLayer().add(line);
			
			PathGraphic p = new PathGraphic(new Point(0,0));
			p.select();
			p.getPoints().get(0).setAnchorPoint(new Point(IconTraits.TREE_ICON_WIDTH, IconTraits.TREE_ICON_HEIGHT));
			out.getTheLayer().add(p);
		;
		//out.setAngle(this.getAngle());
		return out;
	}	
	
	
	@Override
	public String getToolName() {
		if (remove) return "Remove Point Tool";
		return "Add Point Tool";
	}
	
	@Override
	public String getToolTip() {
		if (remove) return "Remove Anchor Tool";
			return "Add Anchor Points";
		}
	
	@Override 
	public void showOptionsDialog() {
		new  ReflectingFieldSettingDialog(this, "oldRemovalFormula").showDialog();;
	}
	@Override
	public String getToolSubMenuName() {
		return "Edit Points";
	}

	
}

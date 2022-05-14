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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2022.1
 */
package objectCartoon;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import locatedObject.PathPoint;
import locatedObject.PathPointList;

public class CiliaryPocketPathCreator extends BasicShapeMaker{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public double pocket_Width=14;
	public double pocket_Height=12.5;
	public double cilia_Height=40;
	public double tip_Top_flat_Width=1;
	
	private double cell_Body_Height=120;
	private double cell_Body_Length=70;
	
	
	
	PathPointList createPoints() {
		PathPointList output = new PathPointList();
		
		
		PathPoint bottomOfCell = createPoint(0,-getCell_Body_Height());
		output.add(bottomOfCell);
		PathPoint sideOfCell = createPoint(0,pocket_Height-getCell_Body_Height()/5);
		sideOfCell.setCurveControl(new Point2D.Double(-cell_Body_Length*1/3,pocket_Height- getCell_Body_Height()/2));
		
		
		bottomOfCell.setCurveControl2(new Point2D.Double(sideOfCell.getCurveControl1().getX() , sideOfCell.getCurveControl1().getY()-getCell_Body_Height()/5-pocket_Height));
		
		bottomOfCell.setCurveControl(new Point2D.Double(cell_Body_Length/2, bottomOfCell.getAnchor().getY()-getCell_Body_Height()/2.3));
		output.add(sideOfCell);
		sideOfCell.makePointAlongLine(true);
		

		/**side of pocket*/
		double x=cell_Body_Length;
		double y=pocket_Height;
		PathPoint sideOfPocket=createPoint(x,y);
		
		
		
		
		sideOfPocket.setCurveControl(new Point2D.Double(x-cell_Body_Length/2, y+getCell_Body_Height()/20));
		output.add(sideOfPocket);
		
		
		
		x+=pocket_Width/8;
		y=0;
		PathPoint pocketBase = createPoint(x,y);
		
		output.add(pocketBase);
		
		
		/**The cilia tip point*/
		x+=pocket_Width*3/8;
		pocketBase.getCurveControl2().setLocation(x, pocketBase.getCurveControl2().getY());
		y=cilia_Height;
		PathPoint pp = createPoint(x,y);
		pp.setCurveControl2((Double) pp.getAnchor().clone());
		output.add(pp);
		
		
		addHorizontalFlippedIn(output, x+tip_Top_flat_Width/2);
		/**
		x+=pocketWidth*3/8;
		y=0;
		output.add(createPoint(x,y));	
		
		x+=pocketWidth/8;
		 y=pocketHeight;
		output.add(createPoint(x,y));	
		
		
		output.add(createPoint(x+planeLength,pocketHeight));
		output.add(createPoint(x+planeLength,0));
		*/
		return output;
	}
	
	
	
	public PathPoint createPoint(double x, double y) {
		PathPoint pp = new PathPoint(x,y);
		
		pp.setCurveControl1(new Point2D.Double(x-pocket_Width/2, y));
		pp.setCurveControl2(new Point2D.Double(x+pocket_Width/2, y));
		return pp;
	}
	
	public static void main(String[] args) {
		
		
		
		
		CiliaryPocketPathCreator cpc = new CiliaryPocketPathCreator();
		
		showShape(cpc);
		
	}
	

	
	@Override
	public PathPointList getPathPointList() {
		PathPointList pts = createPoints();
		pts.applyAffine(AffineTransform.getRotateInstance(Math.PI, cilia_Height, cilia_Height));
		 moveAllIntoView(pts);
		
		return pts;
	}



	public double getCell_Body_Height() {
		
		return cell_Body_Height-pocket_Height;
	}



	

}

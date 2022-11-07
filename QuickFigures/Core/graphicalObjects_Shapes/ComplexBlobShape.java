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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;

import applicationAdapters.ToolbarTester;
import handles.SmartHandleList;
import imageDisplayApp.ImageWindowAndDisplaySet;
import locatedObject.PathPoint;
import locatedObject.PathPointList;

/**A blob shape with a number of distinct lobes that can be edited individually by dragging handles*/
public class ComplexBlobShape extends BlobShape {
	{super.setNvertex(12);}
	
	/**Additional blob parameters beyond the one used by the superclass*/
	BlobCurveParameterGroup[] parameters2=new BlobCurveParameterGroup[] {createParameters2(5, 200023, 0.25), createParameters2(6, 243023, 0.25), createParameters2(7, 9324023, 0.25)};
	private SmartHandleList blobList2;
	{innitialSetup2() ;}
	
	public ComplexBlobShape(RectangularGraphic r) {
		super(r);
		
	}



	public ComplexBlobShape(Rectangle r, int i, double d) {
		super(r,i, d);
	}
	
	/**Sets the positions of the parameter groups within 
	 *  the blob with manually determined parameters for one style of blob*/
	void innitialSetup() {
		double d0=0.25;
		double baseRadia1 = 0.9;
		double angleshift1=-0.25;
		getParameter(1).setPositions(baseRadia1, -d0, -0.25, angleshift1);
		getParameter(2).setPositions(baseRadia1, d0, 0.250,angleshift1);
		
		double d = -.15;
		double baseRadia = .55;
		double angleShift2 = 0.1;
		double bAngle=-1.1;
		getParameter(3).setPositions(baseRadia, -d, bAngle,angleShift2);
		getParameter(0).setPositions(baseRadia, d, -bAngle,angleShift2);
	}
	
	/**Sets the positions of the parameter groups within 
	 * the blob with manually determined parameters for the second style of complex blob*/
	void innitialSetup2() {
		
		for(int i=0; i<this.getNHandleGroups(); i++) { 
				if(i%2==0) {
					double d0=0;
					double baseRadia0 = 0.7;
					double angleshift0=-0.65;
					getParameter(i).setPositions(baseRadia0, -d0, 0, angleshift0);
					getParameter(i).moveCurveControlOutward(0.02);
				}
				if (i%2==1) {
					double d1=0;
					double baseRadia1 = 0.85;
					double angleshift1=-0.65;
					getParameter(i).setPositions(baseRadia1, d1, 0,angleshift1);
					getParameter(i).moveCurveControlOutward(0.015);
				}
		
		}
	
		this.getParameter(3).movePointOutward(0.15);
		this.getParameter(0).movePointOutward(-0.1);
		
	}

	/**returns the i-th parameter group that determines the curve control points for the */
	protected BlobCurveParameterGroup getParameter( int i) {
		if(i==0) return parameters;
		if(i<=this.getNHandleGroups())
			return parameters2[i-1];
		return null;
	}

	/**Generates a parameter group 
	 * @param a the angle ratio used to shift the curve control points away from the anchor
	 * @param point the index of the point in the blob
	 * @param base unique code for each parameter group made*/
	public BlobCurveParameterGroup createParameters2(int point, int base, double a) {
		AngleParameter ccParameter1=createCCAngleParameter2(this); {ccParameter1.setRatioToStandardAngle(-a); }
		AngleParameter ccParameter2=createCCAngleParameter2(this);{ccParameter2.setRatioToStandardAngle(a);}
		AngleParameter anchorParameter2=createCCAngleParameter2(this);
		
		BlobCurveParameterGroup blobCurveParameterGroup = new BlobCurveParameterGroup(anchorParameter2, ccParameter1, ccParameter2);
		blobCurveParameterGroup.point=point;
		blobCurveParameterGroup.handleIDBase=base;
		return blobCurveParameterGroup;
	}
	
	/**returns the number of handle groups used*/
	int getNHandleGroups() {
		return 1+parameters2.length;
	}
	
	/**creates a group of angle parameters for this blob shape*/
	public BlobCurveParameterGroup createParameters() {
		AngleParameter ccParameter1=createCCAngleParameter(this); {ccParameter1.setRatioToStandardAngle(-0.25);}
		AngleParameter ccParameter2=createCCAngleParameter(this);{ccParameter2.setRatioToStandardAngle(0.25);}
		AngleParameter anchorParameter2=createCCAngleParameter(this);
		
		BlobCurveParameterGroup blobCurveParameterGroup = new BlobCurveParameterGroup(anchorParameter2, ccParameter1, ccParameter2);
		
		blobCurveParameterGroup.point=4;
		return blobCurveParameterGroup;
	}
	
	/**creates an angle parameter for this blob shape*/
	protected AngleParameter createCCAngleParameter2(BlobShape blobShape) {
		AngleParameter n = new AngleParameter(this);
		n.setType(AngleParameter.ANGLE_RATIO_AND_RAD_TYPE);
		n.setRatioToMaxRadius(0.5);
		
		return n;
	}
	

	
	public RegularPolygonGraphic copy() {
		ComplexBlobShape output = new ComplexBlobShape(this);
		output.setNvertex(this.getNvertex());
		this.giveStarTraitsToo(output);
		output.parameters=parameters.copy();
		return output;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**implements a formular to produce a regular polygon with a certain number of vertices*/
	@Override
	public Shape getShape() {
		PathPointList path=new PathPointList();
		
		
		
		for(int i=0; i<getNvertex()*2;i++) {
			
			PathPoint p2 = getPathPointAtAngularLocation(i, 0);

			path.add(p2);
		}
		
		this.setClosedShape(true);
		
		
		return path.createPath(true);
		
	}

	

	public PathPoint getPathPointAtAngularLocation(int i, double aa) {
		
		BlobCurveParameterGroup parameters3 = parameters;
		int index = i%getNHandleGroups();
		if (index>0) parameters3=parameters2[index-1];
		Point2D anchor = getPointForPosition(i, parameters3.anchor.getRatioToStandardAngle()+aa,parameters3.anchor.getRatioToMaxRadius());
		Point2D cc1 = getPointForPosition(i, parameters3.curve1.getRatioToStandardAngle()+aa,parameters3.curve1.getRatioToMaxRadius());
		Point2D cc2 = getPointForPosition(i,parameters3.curve2.getRatioToStandardAngle()+aa,parameters3.curve2.getRatioToMaxRadius());
			
		PathPoint p2 =new PathPoint(anchor);
		p2.setCurveControl1(cc1);
		p2.setCurveControl2(cc2);
		return p2;
	}
	
	
	public String getPolygonType() {return "Fancy Blob";}

	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		
		return list;
	}
	
	
	/**override the superclass*/
	@Override
	protected void addStarHandlesToList(SmartHandleList list) {

		}

	

	/**override the superclass*/
	@Override
	protected void updateStarHandles() {
			}
	
	@Override
	public SmartHandleList getSmartHandleList() {
		SmartHandleList list1 = super.getSmartHandleList();
		
	
		if (blobList2==null) {
			blobList2=new SmartHandleList();
			for(BlobCurveParameterGroup para: parameters2) {
				blobList2.addAll(para.createHandlesList(this));
			}
		}
		
		return SmartHandleList.combindLists(list1, blobList2);
	}
	
	public static void main(String[] args) {
		ImageWindowAndDisplaySet ex = ToolbarTester.showExample(true);
		ComplexBlobShape z = new ComplexBlobShape(new Rectangle(20,20,200,200), 5, 0);
		z.setStrokeColor(Color.BLACK);
		ex.getImageAsWorksheet().getTopLevelLayer().add(z);
		ex.updateDisplay();
	}
}

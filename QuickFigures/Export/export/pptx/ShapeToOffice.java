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
 * Date Modified: Nov 28, 2021
 * Version: 2021.2
 */
package export.pptx;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import org.apache.poi.sl.usermodel.StrokeStyle.LineDash;
import org.apache.poi.sl.usermodel.StrokeStyle.LineCap;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFFreeformShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;

import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.PathPointList;
import logging.IssueLog;

/**subclass of OfficeObjectMaker that creates a shape in powerpoint*/
public class ShapeToOffice  implements OfficeObjectMaker{


ShapeGraphic shape=null;
	
public ShapeToOffice(ShapeGraphic p1) {
	this.shape=p1;
}
	
	public ShapeGraphic getShape() {
		return shape;
	}
	

	/**sets the fill color dashes and other traits of the powerpoint shape to match the shape graphic*/
	public void matchShapeTraits(org.apache.poi.xslf.usermodel.XSLFSimpleShape shape, ShapeGraphic pathGraphic) {
		
		
		 shape.setFillColor(pathGraphic.getFillColor());
		 
		 if (!pathGraphic.isFilled()||pathGraphic.getFillColor().getAlpha()==0) shape.setFillColor(null); 
		
		 setupDashes(shape, pathGraphic);
		
	       shape.setLineColor(pathGraphic.getStrokeColor());
	       if (pathGraphic.getStrokeColor().getAlpha()==0)  shape.setLineColor(null);
	        shape.setLineWidth(pathGraphic.getStrokeWidth());
	        shape.setRotation(pathGraphic.getAngle()*(-180/Math.PI));
	        
	        setupStrokeCap(shape, pathGraphic);
	        
	        
	}

	/**sets the line cap for the exported shape
	 * @param shape
	 * @param pathGraphic
	 */
	public void setupStrokeCap(org.apache.poi.xslf.usermodel.XSLFSimpleShape shape, ShapeGraphic pathGraphic) {
		if (pathGraphic.getStrokeCap()==BasicStroke.CAP_SQUARE) shape.setLineCap(LineCap.SQUARE);
		if (pathGraphic.getStrokeCap()==BasicStroke.CAP_ROUND) shape.setLineCap(LineCap.ROUND);
	}

	/**
	 * @param shape
	 * @param pathGraphic
	 */
	public void setupDashes(org.apache.poi.xslf.usermodel.XSLFSimpleShape shape, ShapeGraphic pathGraphic) {
		if (pathGraphic.getDashes()!=null &&pathGraphic.getDashes().length>1) {
			
			 float[] dash = pathGraphic.getDashes();
			 if (dash[0]==dash[1]) {
				if (dash[0]<3) shape.setLineDash(LineDash.DOT);
				else if (dash[0]<5) shape.setLineDash(LineDash.DASH);
				else if 	(dash[0]>4) shape.setLineDash(LineDash.LG_DASH);
				;
			 } else {
				 shape.setLineDash(LineDash.LG_DASH_DOT);
			 }
		 }
	}
	
	/**Creates a shape in the powerpoint slide*/
	@Override
	public XSLFFreeformShape addObjectToSlide(XMLSlideShow ppt, XSLFShapeContainer slide) {
		
		
		XSLFFreeformShape shape =  slide.createFreeform();
		createOnFreeForm(shape);
        
     return shape;
		
	}
	
	/**makes the given powerpoint object matche the target shape*/
	public void createOnFreeForm(XSLFFreeformShape freeshape) {
		
			
			//path2.append(path, true);//true
			
			
			
			
				Shape path = this.shape.getShape();
				GeneralPath path2 = new GeneralPath();
				
				path2.append(path.getPathIterator(new AffineTransform()), true);//true
				
				if (shape instanceof PathGraphic) {
					IssueLog.log("with some shapes, powerpoint will need to repair the exported file");

				}
				
				if (path instanceof Line2D) {
					//path2.append(path, true);
					//path2.closePath();

					IssueLog.log("may have issues with strait lines");

				}
			try {// paths with quad curves may cause exceptions
				freeshape.setPath(path2);
			
			} catch (Exception e) {
				Path2D it2 = PathPointList.convertToCubicShape(path2);
				freeshape.setPath(it2);
			}
				matchShapeTraits(freeshape, this.shape);
	}

	

	/**created when trying to determine why some paths did not export. fixed bug but did not delete this code 
	 * @param path2
	 */
	public void reportOnPath(GeneralPath path2) {
		if(path2 instanceof GeneralPath) {
			GeneralPath p2=(GeneralPath)path2;
			PathIterator iterator = p2.getPathIterator(AffineTransform.getTranslateInstance(0, 0));
			while(!iterator.isDone()) {
				double[] points = new double[6];
				int type = iterator.currentSegment(points);
				if(type==PathIterator.SEG_QUADTO)
					IssueLog.log("Path type"+getTypeName(type)+" x1="+points[0]+"  y1="+points[1]+" x2="+points[2]+"  y2="+points[3]+" x3="+points[4]+"  y3="+points[4]);
				iterator.next();
			}
		}
	}

	/**created when trying to determine why some paths did not export. fixed bug but did not delete this code 
	 * @param type
	 * @return
	 */
	private String getTypeName(int type) {
		switch(type) {
		case PathIterator.SEG_MOVETO:
			return "Move to";
		case PathIterator.SEG_CLOSE:
			return "Close to";	
		case PathIterator.SEG_LINETO:
			return "Line to";
		case PathIterator.SEG_QUADTO:
			return "Quad to";
		case PathIterator.SEG_CUBICTO:
			return "Cube to";	
		}
		return null;
	}

	
	public static boolean hasQuads(Shape p2) {
		PathIterator iterator = p2.getPathIterator(AffineTransform.getTranslateInstance(0, 0));
		while(!iterator.isDone()) {
			double[] points = new double[6];
			int type = iterator.currentSegment(points);
			if(type==PathIterator.SEG_QUADTO)
				return true;
			iterator.next();
		}
		
		return false;
	}
	

}

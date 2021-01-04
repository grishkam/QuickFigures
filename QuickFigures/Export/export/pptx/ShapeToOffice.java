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
package export.pptx;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import org.apache.poi.xslf.usermodel.LineDash;
import org.apache.poi.xslf.usermodel.LineCap;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFFreeformShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;

import graphicalObjects_Shapes.ShapeGraphic;
import logging.IssueLog;

public class ShapeToOffice  implements OfficeObjectMaker{


	//protected ShapeGraphic pathGraphic;
ShapeGraphic shape=null;
	
public ShapeToOffice(ShapeGraphic p1) {
	this.shape=p1;
}
	
	public ShapeGraphic getShape() {
		return shape;
	}
	

	
	public void matchShapeTraits(org.apache.poi.xslf.usermodel.XSLFSimpleShape shape, ShapeGraphic pathGraphic) {
		
		
		 shape.setFillColor(pathGraphic.getFillColor());
		 
		 if (!pathGraphic.isFilled()||pathGraphic.getFillColor().getAlpha()==0) shape.setFillColor(null); 
		
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
		
	       shape.setLineColor(pathGraphic.getStrokeColor());
	       if (pathGraphic.getStrokeColor().getAlpha()==0)  shape.setLineColor(null);
	        shape.setLineWidth(pathGraphic.getStrokeWidth());
	        shape.setRotation(pathGraphic.getAngle()*(-180/Math.PI));
	        
	        if (pathGraphic.getStrokeCap()==BasicStroke.CAP_SQUARE) shape.setLineCap(LineCap.SQUARE);
	        if (pathGraphic.getStrokeCap()==BasicStroke.CAP_ROUND) shape.setLineCap(LineCap.ROUND);
	        
	        
	}
	
	@Override
	public XSLFFreeformShape addObjectToSlide(XMLSlideShow ppt, XSLFShapeContainer slide) {
		
		
		XSLFFreeformShape shape =  slide.createFreeform();
		createOnFreeForm(shape);
        
     return shape;
		
	}
	
	public void createOnFreeForm(XSLFFreeformShape freeshape) {
		Shape path = this.shape.getShape();
	
		
			GeneralPath path2 = new GeneralPath();
			path2.append(path.getPathIterator(new AffineTransform()), true);//true
			
			//path2.append(path, true);//true
			
			
			freeshape.setPath(path2);
			
			
			if (path instanceof Line2D) {
				//path2.append(path, true);
				//path2.closePath();
				
				IssueLog.log("may have issues with strait lines");
				//freeshape.set
				}
		//}
        matchShapeTraits(freeshape, this.shape);
	}

	
	
	

}

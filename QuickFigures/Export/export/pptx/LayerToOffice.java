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

import java.awt.geom.Rectangle2D;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;

public class LayerToOffice implements OfficeObjectMaker {

	private GraphicGroup layer;

	public LayerToOffice(GraphicGroup p1) {
		layer=p1;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public XSLFGroupShape addObjectToSlide(XMLSlideShow ppt, XSLFShapeContainer slide) {
		XSLFGroupShape gg = slide.createGroup();
	
	
		
		for(ZoomableGraphic l : layer.getTheLayer().getAllGraphics()) {
			if (l instanceof OfficeObjectConvertable) {
				OfficeObjectConvertable o=(OfficeObjectConvertable) l;
				o.getObjectMaker().addObjectToSlide(ppt, gg);
			}
		}
		
		Rectangle2D anchor = layer.getOutline().getBounds2D();
		gg.setAnchor(anchor);
		gg.setInteriorAnchor(anchor);
		
		
		
		return gg;
	}

}

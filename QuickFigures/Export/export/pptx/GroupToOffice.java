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
 * Date Modified: Nov 27, 2021
 * Version: 2022.1
 */
package export.pptx;

import java.awt.geom.Rectangle2D;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.ClosedGroup;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import logging.IssueLog;

/**subclass of OfficeObjectMaker that creates a group in powerpoint*/
public class GroupToOffice implements OfficeObjectMaker {

	private GraphicGroup layer;
	private GraphicLayerPane theInternalLayer;
	private Rectangle2D bounds2d;
	private ClosedGroup layer2;

	public GroupToOffice(GraphicGroup p1) {
		layer=p1;
		 theInternalLayer = layer.getTheInternalLayer();
		 bounds2d = layer.getOutline().getBounds2D();
	}
	
	public GroupToOffice(ClosedGroup p1) {
		layer2=p1;
		 theInternalLayer = layer2.getTheInternalLayer();
		 bounds2d = layer2.getOutline().getBounds2D();
	}
	
	@Override
	public XSLFGroupShape addObjectToSlide(XMLSlideShow ppt, XSLFShapeContainer slide) {
		

		
		
		return officeExport(ppt, slide, theInternalLayer, bounds2d);
	}

	/**
	 * @param ppt
	 * @param slide
	 * @param theInternalLayer
	 * @param bounds2d
	 * @return
	 */
	public XSLFGroupShape officeExport(XMLSlideShow ppt, XSLFShapeContainer slide, GraphicLayerPane theInternalLayer,
			Rectangle2D bounds2d) {
		XSLFGroupShape gg = slide.createGroup();
		for(ZoomableGraphic l : theInternalLayer.getAllGraphics()) try {
			if (l instanceof OfficeObjectConvertable) {
				OfficeObjectConvertable o=(OfficeObjectConvertable) l;
				o.getObjectMaker().addObjectToSlide(ppt, gg);
			}
		} catch (Throwable t) {
			IssueLog.log("an issue occured when trying to export "+l);
			IssueLog.logT(t);
		}
		
		
		Rectangle2D anchor = bounds2d;
		gg.setAnchor(anchor);
		gg.setInteriorAnchor(anchor);
		
		
		
		return gg;
	}

}

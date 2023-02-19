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
 * Date Modified: Jan 6, 2021
 * Version: 2023.1
 */
package export.pptx;

import java.awt.geom.Rectangle2D;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFFreeformShape;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;

import graphicalObjects_SpecialObjects.BarGraphic;

/**subclass of ShapeToOffice that creates a scale bar in powerpoint*/
public class BarGraphicToOffice extends ShapeToOffice {

	private BarGraphic bar;

	public BarGraphicToOffice(BarGraphic p1) {
		super(p1);
		
		bar=p1;
		
	}
	
	/**Adds both scale bar and the bars label text to the slide*/
	@Override
	public XSLFFreeformShape addObjectToSlide(XMLSlideShow ppt, XSLFShapeContainer slide) {
		XSLFGroupShape gg = slide.createGroup();
	
		
		XSLFFreeformShape thebar = super.addObjectToSlide(ppt, gg);
		thebar.setLineWidth(0);
		thebar.setLineColor(null);
		bar.getBarText().getBarTextObjectMaker().addObjectToSlide(ppt, gg);
		
		Rectangle2D anchor = bar.getOutline().getBounds2D();
		gg.setAnchor(anchor);
		gg.setInteriorAnchor(anchor);
		return thebar;
	}

}

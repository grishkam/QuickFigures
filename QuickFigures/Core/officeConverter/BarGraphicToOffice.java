package officeConverter;

import java.awt.geom.Rectangle2D;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFFreeformShape;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;

import graphicalObjects_BasicShapes.BarGraphic;

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

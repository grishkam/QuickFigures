package officeConverter;

import java.awt.geom.Rectangle2D;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFSlide;

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

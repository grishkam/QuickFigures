package officeConverter;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;

public interface OfficeObjectMaker {
	
	public Object addObjectToSlide( XMLSlideShow ppt, XSLFShapeContainer slide);

}

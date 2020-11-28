package export.pptx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;

import graphicalObjects.ImagePanelGraphic;
import illustratorScripts.PlacedItemRef;

public class ImagePanelImmitator implements OfficeObjectMaker {
	
	private ImagePanelGraphic imagepanel;

	public ImagePanelImmitator(ImagePanelGraphic ipg) {
		this.imagepanel=ipg;
		
	}

	@Override
	public Object addObjectToSlide(XMLSlideShow ppt, XSLFShapeContainer slide) {
		ImagePanelGraphic ipg=imagepanel;
		try {
		;
		File f = PlacedItemRef.prepareFile(ipg.getProcessedImageForDisplay(), ipg.getName());
		byte[] pictureData;
		
			pictureData = IOUtils.toByteArray(new FileInputStream(f.getAbsolutePath()));
		

        int idx = ppt.addPicture(pictureData, XSLFPictureData.PICTURE_TYPE_PNG);
       
        XSLFPictureShape pic = slide.createPicture(idx);
     
      
        pic.setAnchor(ipg.getBounds());//TODO Check if using bounds2d instead of bounds has an effect
       f.deleteOnExit();
       return pic;
       
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}

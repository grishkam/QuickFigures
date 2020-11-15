package applicationAdaptersForImageJ1;

import ij.ImagePlus;
import infoStorage.StringBasedMetaWrapper;
import infoStorage.MetaInfoWrapper;

/**This class is crucial for retrieving certain information from an image's metadata
 * For example, real channel names are obtained this way. See superclass for detail
  */
public class ImagePlusMetaDataWrapper extends StringBasedMetaWrapper  implements MetaInfoWrapper{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImagePlus image;

	public ImagePlusMetaDataWrapper(ImagePlus imp) {
		image=imp;
	}


	public String getProperty() {
		ImagePlus img = image;
		return (String) img.getProperty("Info");
	}
	
	public void setProperty(String newProp) {
		ImagePlus img = image;
		img.setProperty("Info", newProp);
	}
	
	
}

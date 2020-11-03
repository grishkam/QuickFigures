package applicationAdaptersForImageJ1;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import logging.IssueLog;
import applicationAdapters.ImageWrapper;
import channelMerging.MultiChannelOpener;
import channelMerging.MultiChannelWrapper;


public class ImagePlusOpener implements MultiChannelOpener<ImagePlus> {

	@Override
	public ImagePlus openFile(String path) {
		// TODO Auto-generated method stub
		return IJ.openImage(path);
	}

	@Override
	public MultiChannelWrapper openMultiChannel(String path) {
		// TODO Auto-generated method stub
		return wrap(openFile(path));
	}

	@Override
	public MultiChannelWrapper wrap(ImagePlus ob) {
		if (ob==null) return null;
		return new ImagePlusWrapper(ob);
	}

	@Override
	public MultiChannelWrapper getActiveWrapper() {
		if (getActiveImage()!=null)
		return wrap(getActiveImage());
		return null;
	}

	@Override
	public ImagePlus getActiveImage() {
		// TODO Auto-generated method stub
		return IJ.getImage();
	}
	
	public String[] getImageChoices() {
		 String[] titles = getAvailableImageNamesID() ;
		    if (titles.length==0) return new String[]{};
		String[] newtitles= new String[titles.length+1];
		newtitles[0]="none";
		for (int l=0; l<titles.length; l++) {newtitles[l+1]=titles[l];}
		return newtitles;
	}
	
	public int[] getAvailableImageID() {
		return WindowManager.getIDList();
	}
	
	public String[] getAvailableImageNamesID() {
		 int[] wList = WindowManager.getIDList();
		return listOfOpenImageNames(wList);
	}
	
	public ImagePlus getImageFromChoiceIndex(	int i) {
		 int[] wList=getAvailableImageID() ;
		if (i==0) return null;
		return imageID(wList[i-1]);
	}
	

	

	public ImagePlus imageID(int imageID) {
		return WindowManager.getImage(imageID);
	}

	@Override
	public ImageWrapper makeImageWrapper(ImagePlus ot) {
		if (ot==null) {
			IssueLog.log("ImagePlus opener tried to make a wrapper from a null image ");
			return null;
		}
		return new ImagePlusWrapper(ot);
	}

	@Override
	public ImagePlus unwrap(ImageWrapper montage) {
		if (montage instanceof ImagePlusWrapper) {
			ImagePlusWrapper imp=(ImagePlusWrapper) montage;
			
			return imp.getImagePlus();
		}
		return null;
	}
	
	/**when given a list of integers with each images id,
	  returns a string array with the names of each */
	public static String[] listOfOpenImageNames(int[] wList) {
		   if (wList==null) {
		        
		        return new String[]{};
		    }

		    String[] titles = new String[wList.length];
		    for (int i=0; i<wList.length; i++) {
		        ImagePlus imp = WindowManager.getImage(wList[i]);
		        titles[i] = imp!=null?imp.getTitle():"";
		    }
		    return titles;
		    
	}
	/**when given a list of integers with each images id,
	  returns a string array with the names of each */
	public static String[] listOfOpenImageNames() {
		  return listOfOpenImageNames(WindowManager.getIDList());    
	}
	

}

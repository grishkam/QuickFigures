package addObjectMenus;

import ultilInputOutput.FileChoiceUtil;
import ultilInputOutput.ForDragAndDrop;

import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import graphicalObjects.BufferedImageGraphic;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;

public class FileImageAdder extends BasicGraphicAdder{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean rg=false;
	boolean isImageMale=true;

	public FileImageAdder(boolean RGBint) {
		this.rg=RGBint;
	}
	
	public ImagePanelGraphic getImage() {
		return getImage(FileChoiceUtil.getOpenFile());
	}
	
	
	public ImagePanelGraphic getImage(File f) {
		ImagePanelGraphic ag;
		Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(ForDragAndDrop.getExtension(f).toUpperCase());
		if(!readers.hasNext()) {
			IssueLog.log("May have touble reading file"+'\n'+f);
			return null;
		}
		if (rg) ag=new BufferedImageGraphic(f);
		else  ag = new ImagePanelGraphic(f);
		isImageMale=ag.isFilefound();
		return ag;
	}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		ImagePanelGraphic ag= getImage();
		
		if(isImageMale) {
		ag.setEmbed(true);
		gc.add(ag);;
		ag.setLocationUpperLeft(0, 0);
		return  ag;
		} else 
			return null;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "FileImage"+rg;
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		if (rg)return "Add RGB Image";
		else return "Add Image File";
	}
	
}
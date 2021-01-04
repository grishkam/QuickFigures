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
package addObjectMenus;

import ultilInputOutput.FileChoiceUtil;
import ultilInputOutput.ForDragAndDrop;

import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.BufferedImageGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import messages.ShowMessage;

/**Adds a simple image panel by opening a file with ImageIO*/
public class FileImageAdder extends BasicGraphicAdder{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean bufferedImageGraphic=false;
	boolean isImageMade=true;

	public FileImageAdder(boolean RGBint) {
		this.bufferedImageGraphic=RGBint;
	}
	
	public ImagePanelGraphic getImage() {
		return getImage(FileChoiceUtil.getOpenFile());
	}
	
	
	public ImagePanelGraphic getImage(File f) {
		ImagePanelGraphic ag;
		Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(ForDragAndDrop.getExtension(f).toUpperCase());
		if(!readers.hasNext()) {
			ShowMessage.showMessages("This kind of file cannot be used with this menu option",""+f);
			return null;
		}
		if (bufferedImageGraphic) ag=new BufferedImageGraphic(f);
		else  ag = new ImagePanelGraphic(f);
		isImageMade=ag.isFilefound();
		return ag;
	}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		ImagePanelGraphic ag= getImage();
		
		if(isImageMade&&ag!=null) {
			ag.setEmbed(true);
			gc.add(ag);;
			ag.setLocationUpperLeft(0, 0);
			return  ag;
		} else 
			return null;
	}

	@Override
	public String getCommand() {
		return "FileImage"+bufferedImageGraphic;
	}

	@Override
	public String getMenuCommand() {
		if (bufferedImageGraphic)return "Add RGB Image";
		else return "Add Image File";
	}
	
}
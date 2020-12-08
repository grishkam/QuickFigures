/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package objectDialogs;

import graphicalObjects.BufferedImageGraphic;
import graphicalObjects.ImagePanelGraphic;
import standardDialog.booleans.BooleanArrayInputPanel;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ComboBoxPanel;
import standardDialog.colors.ColorCheckbox;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.InfoDisplayPanel;
import undo.Edit;


public class ImageGraphicOptionsDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImagePanelGraphic   image;
	
	public ImageGraphicOptionsDialog() {}
	public ImageGraphicOptionsDialog(BufferedImageGraphic bg) {
		image=bg;
		addOptionsToDialog() ;
		super.undoableEdit=Edit.createGenericEditForItem(bg);
	}
	
	public ImageGraphicOptionsDialog(ImagePanelGraphic bg) {
		image=bg;
		addOptionsToDialog() ;
		super.undoableEdit=Edit.createGenericEditForItem(bg);
	}
	
	
	
	protected void addOptionsToDialog() {
		super.addNameField(image);
	
		super.addFixedEdgeToDialog(image);
		
		addCommonOptionsToDialog();
		this.addScaleInfoToDialog(image.getScaleInfo());
		
			this.add("embed", new BooleanInputPanel("Embed ", image.isEmbed()));
			if (image.isFilederived()) {
				this.moveGrid(2, -1);
				this.add("fileLoad", new BooleanInputPanel("Always Load From File", image.isLoadFromFile()));
				this.moveGrid(-2, 0);
			}
			
			this.add("Dimensions", new InfoDisplayPanel("Dimensions", image.getDimensionString()));
			this.add("Dimensions2", new InfoDisplayPanel("", image.getRealDimensionString()));
			
			this.add("PPI", 
					new InfoDisplayPanel("PPI ", image.getIllustratorPPI() /**+" ("+image.getScreenPPI()+" on screen)"*/));
			//this.add("PPI-ink", 
			//		new InfoDisplayPanel("PPI (Inkscape-like)", image.getInkscapePPI() /**+" ("+image.getScreenPPI()+" on screen)"*/));
			
			
		if (image instanceof BufferedImageGraphic) {
			BufferedImageGraphic image2=(BufferedImageGraphic) image;
	
		
		
		this.add("ExcludedChannels", new BooleanArrayInputPanel("Include Colors ", image2.getRemovedChannels(), ColorCheckbox.get4Channel()));
		this.add("Force Gray channel", new ComboBoxPanel("Force Gray channel ", new String[]{"None", "Red", "Green", "Blue"}, image2.getForceGrayChannel()));
		}
		
		AngleInputPanel aip = new AngleInputPanel("Angle", image.getAngle(), true);
		this.add("Angle", aip);
		
		this.add("locked in place", new BooleanInputPanel("Protect from mouse drags ", image.isUserLocked()==1));

		this.addSnappingBehviourToDialog(image);
	}
	
	public void addCommonOptionsToDialog() {
		this.add("scale", new NumberInputPanel("Relative Scale", image.getScale(), 2) );
		this.add("frame", new NumberInputPanel("Frame Width", image.getFrameWidthH(), 3) );
		this.add("frameC", new ColorComboboxPanel("Frame Color ", null, image.getFrameColor()));
	}
	
	protected void setItemsToDiaog() {
			setNameFieldToDialog(image);
			//IssueLog.log("setting image to options");
			
		//image.setName(this.getNextString());
		//image.setLocationType(this.getNextChoiceIndex());
				setFixedEdgeToDialog(image);
				setCommonOptionsToDialog(image);
				
				image.setEmbed(this.getBoolean("embed"));
				image.setUserLocked(this.getBoolean("locked in place")?1:0);
				if (image.isFilederived()) {
					
					image.setLoadFromFile(this.getBoolean("fileLoad"));
				}
				allStrings.get("Dimensions").setContentText(image.getRealDimensionString());
				allStrings.get("Dimensions2").setContentText(image.getDimensionString());
				allStrings.get("PPI").setContentText(image.getIllustratorPPI() /**+" "+image.getScreenPPI()*/);
				//Strings.get("PPI-ink").setContentText(image.getInkscapePPI() /**+" "+image.getScreenPPI()*/);
				
				
				if (image instanceof BufferedImageGraphic) {
					BufferedImageGraphic	image2=(BufferedImageGraphic) image;
					
				image2.setRemovedChannels(this.getBooleanArray("ExcludedChannels"));
				image2.setForceGrayChannel((int)this.getChoiceIndex("Force Gray channel"));
				}
					this.setScaleInfoToDialog(image.getScaleInfo());
					this.setObjectSnappingBehaviourToDialog(image);
					image.setAngle(this.getNumber("Angle"));
	}
	
	public void setCommonOptionsToDialog(ImagePanelGraphic   image) {
		image.setScale(this.getNumber("scale"));
		image.setFrameWidthH((float)this.getNumber("frame"));
		image.setFrameColor(this.getColor("frameC"));
		image.notifyListenersOfUserSizeChange();
	}
}

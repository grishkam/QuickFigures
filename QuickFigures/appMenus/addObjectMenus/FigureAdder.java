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
 * Date Modified: April 11, 2021
 * Version: 2021.1
 */
package addObjectMenus;

import ultilInputOutput.FileFinder;

import appContext.CurrentAppContext;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import figureEditDialogs.SubStackDialog;
import figureFormat.AutoFigureGenerationOptions;
import figureFormat.FigureTemplate;
import figureFormat.TemplateUserMenuAction;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import logging.IssueLog;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import selectedItemMenus.LayerSelectionSystem;

/**this class adds a figure containing a multidimensional images to a layer */
public class FigureAdder extends LayoutAdder {
	
	/**
	 a dialog will be shown to users if they attempt to add create with a very large image
	 */
	private static final int MAX_RECCOMENDED_SIZE_LIMIT = 25;

	private static final long serialVersionUID = 1L;

	public AutoFigureGenerationOptions autoFigureGenerationOptions=new AutoFigureGenerationOptions();
	
	public boolean openFile=false;//set to true if this opens a file to create a figure
	
	private boolean makeLabels=false;
	
	/**set to true if limiting the new figure to a single frame*/
	public boolean useSingleFrame=false;
	public boolean useSingleSlice=false;
	
	/**constants indicate what sort of figure, defaul indicates to folow the template*/
	public static final int MERGE_PANELS_ONLY=1, DEFAULT=0, SPLIT_CHANNELS_ONLY=2;
	public int mergeOnly=DEFAULT;
	
	private TemplateUserMenuAction templatesaver =null;
	

	private FigureOrganizingLayerPane currentFigureOrganizer;

	private boolean useOpen;

	/**creates a figure adder that relies on a file*/
	public FigureAdder(boolean fromFile) {
			openFile=fromFile;
		}
	
	public FigureAdder(boolean fromFile, boolean autogen) {
		openFile=fromFile;
		this.autoFigureGenerationOptions.autoGenerateFromModel=autogen;
		this.useOpen=autogen;
	}
	

	/**returns the template saver that is used to create objects*/
	public TemplateUserMenuAction getTemplatesaver() {
		
		if (templatesaver==null) {
			templatesaver = new TemplateUserMenuAction(TemplateUserMenuAction.APPLY_TEMPLATE, false);
		}
		return templatesaver;
	}

	/**sets the template saver that is used to determine which template to load*/
	public void setTemplatesaver(TemplateUserMenuAction templatesaver) {
		this.templatesaver = templatesaver;
	}
	
	
	

	/**when given a layer, will return a figure organizing layer pane for that layer
	  if the layer is inside of a figure organizing layer, returns that figure organizing layer
	  otherwise creates a new figure organizing layer.
	  , attempts to move up the layer tree to find a Figure organizing layer pane*/
	private static FigureOrganizingLayerPane getOrCreateUsedFigureOrganizingLayerPane(GraphicLayer gc) {
		
			FigureOrganizingLayerPane l22 = new FigureOrganizingLayerPane("Figure");
		
			/**What to do if the input layer is a multichannel display*/
			if (gc instanceof MultichannelDisplayLayer) {
				gc=gc.getParentLayer();
			}
			
			/**What to do if the input layer or one of its parents is already FigureOrganizingLayerPane. */
		if 	(gc instanceof FigureOrganizingLayerPane) 
					{l22=(FigureOrganizingLayerPane) gc;} 
			else
					{
					while (gc!=null&&!gc.canAccept(l22)) gc=gc.getParentLayer();
				
					if (gc==null) 
						return null;
					gc.add(l22);
						}
		
		
		
		return l22;
	}
	
	/**when given a file path, opens the Image and returns a the multichannel display layer
	  that contains the open image*/
	public MultichannelDisplayLayer createMultiChannel(String path) {
		MultichannelDisplayLayer output = getMultiChannelOpener().creatMultiChannelDisplayFromUserSelectedImage(openFile, path);
		if (output==null) {
			IssueLog.log("no image found");
			
		};
		return output;
	}
	
	/**Adds a figure to the layer*/
	@Override
	public FigureOrganizingLayerPane add(GraphicLayer gc) {
		return add(gc, null);
	}
	
	FileFinder getFileFinder() {
		return new FileFinder();	
	}
	
	/**shortens a long name into a shorter form*/
	public String shorten(String tooLong) {
		if (tooLong==null) return null;
		String output = tooLong.split(";")[0];
		if (output.length()>12) output=output.substring(0, 11);
		return output;
	}
	
	/**Adds a figure to the layer gc. If path is not null, the image from the save path is used
	  otherwise, uses the image that is already open*/
	public FigureOrganizingLayerPane add(GraphicLayer gc, String path) {
		return add(gc, path, null);
	}
	
	/**Adds a figure to the layer gc. If path is not null, the image from the save path is used
	  otherwise, uses the image that is already open. if the preprocess information given is not null
	  sets the cropping and scale based on it. If the Preprocess informatin is null, just uses the selected roi*/
	public FigureOrganizingLayerPane add(GraphicLayer targetLayer, String path, PreProcessInformation p) {
		
		/***/
		
		MultichannelDisplayLayer display=  createMultiChannel(path) ;
		if(display==null) return null;
		
		/**determines the crop for this image*/
		if (p==null)
			FigureOrganizingLayerPane.cropIfUserSelectionExists(display); 
		else display.getSlot().applyCropAndScale(p);
		
		
		boolean useSingleFrame2 = useSingleFrame;
		boolean useSingleSlice2 = useSingleSlice;
		
		
		if (useSingleFrame2) display.getPanelList().getChannelUseInstructions().limitStackUseToFrame(display.getMultiChannelImage().getSelectedFromDimension(MultiChannelImage.FRAME_DIMENSION));
		if (useSingleSlice2) display.getPanelList().getChannelUseInstructions().limitStackUseToSlice( display.getMultiChannelImage().getSelectedFromDimension(MultiChannelImage.SLICE_DIMENSION));
		
		int n = display.getPanelList().getChannelUseInstructions().estimageNPanels(display.getMultiChannelImage());
		if (n>MAX_RECCOMENDED_SIZE_LIMIT) {
			new SubStackDialog(display, true, n+" would be a lot of panels " +"please select substack").showDialog();
		}
		
		FigureOrganizingLayerPane figureOrganizerFor = addFigureOrganizerFor(targetLayer, display, p);
		
		
		return figureOrganizerFor;
		}

	/**when given a normal parent layer and a multidimensional image display layer,
	 places the multichannel display layer into a figure organizing layer (which is created if the ordinary layer is not inside of an existing figure organizer). 
	 that figure organizer will contain the multidimensional image inside of it  
	 * @param p */
	public FigureOrganizingLayerPane addFigureOrganizerFor(GraphicLayer ordinaryLayer, MultichannelDisplayLayer multiDimensionalImage, PreProcessInformation p) {

		/**Sets up which figure organizing layer pane should be used*/
		 currentFigureOrganizer = getOrCreateUsedFigureOrganizingLayerPane(ordinaryLayer);
		 
		 /**If the figure organizer is newly created, it will need a new name which depends on the multichannel*/
		if (currentFigureOrganizer!=null&&currentFigureOrganizer.getPrincipalMultiChannel()==null) {
			currentFigureOrganizer.setName("Figure For "+multiDimensionalImage.getName());
		}
		
		/**opens a figure template or creates one if one does not exist*/
		FigureTemplate temp = getUsedTemplate( multiDimensionalImage);
		/**If there is not a template*/
		if (temp==null){
				currentFigureOrganizer.addNovelMultiChannel(multiDimensionalImage, -1);
				showRecreateDialog();
				return currentFigureOrganizer;
			} 
			else 
				addImageToFigureUsingTemplate(currentFigureOrganizer, multiDimensionalImage, temp, p);
				
		return currentFigureOrganizer;
	}

	/**
	 Using the figure template given, adds the multichannel display layer to the figure organizer given
	 * @param p 
	 */
	protected void addImageToFigureUsingTemplate(FigureOrganizingLayerPane currentFigureOrganizer, MultichannelDisplayLayer multiDimensionalImage, FigureTemplate temp, PreProcessInformation p) {
		/**if the template scale is very large this fixes the issue*/
		boolean d = temp.getMultiChannelPicker()!=null&&temp.getMultiChannelPicker().isProprocessSuitable(multiDimensionalImage);
		
		/**if no preprocss information is given as an argument and the template's process is suitable, this sets*/
		if(p==null)
			temp.getMultiChannelPicker().doesPreprocess=d;
		else temp.getMultiChannelPicker().doesPreprocess=false;
		
		
		/**must apply before creating the layout so the minimum number of columns can be created
		  it appears that without a template the display will not be added to the figure. TODO: fix this */
		
			temp .addDisplayToFigure(currentFigureOrganizer, multiDimensionalImage);
			showRecreateDialog();


		try{
			
		if (temp!=null) 
			temp.applyTemplateToLayer(currentFigureOrganizer);
		
		
		if (isAutoGenerateFromModel()) {
				multiDimensionalImage.eliminateAndRecreate();
				DefaultLayoutGraphic layout = currentFigureOrganizer.getMontageLayoutGraphic();
				if (makeLabels) 
					temp .createDefaultLabelsObjectsFromTemplate( currentFigureOrganizer, multiDimensionalImage, layout);
				temp .createScaleBarOffTemplate( currentFigureOrganizer);
				layout.generateCurrentImageWrapper();
				layout.getEditor().fitLabelSpacesToContents(layout.getPanelLayout());
 
} 

	}catch (Throwable t) {
		IssueLog.logT(t);
	}
		
	}

	/**
	If the option to show the recreate panels dialog right away is choses. does so.
	 */
	protected void showRecreateDialog() {
		if (showPanelCreationOptions()) {
				currentFigureOrganizer.recreateFigurePanels();
			}
	}
	
	FigureTemplate getUsedTemplate() {
		return getUsedTemplate(null);
	}
		
	/**returns the figure template to be used by this adder. 
	  if no template is found, creates a new default template*/
	protected FigureTemplate getUsedTemplate(MultichannelDisplayLayer display) {
		FigureTemplate template = getTemplatesaver().loadDefaultTemplate();
		if (autoFigureGenerationOptions.ignoreSavedTemplate||(template==null&&display!=null)) {
			template=new FigureTemplate(display);
			if (!autoFigureGenerationOptions.ignoreSavedTemplate)getTemplatesaver().saveDefaultTemplate(template);
		}
		return template;
	}
	/**Applies a figure template to the layer*/
	void applyUsedTemplate(GraphicLayer g) {
			try{
				getUsedTemplate().applyTemplateToLayer(g);
				}catch (Throwable t) {
					IssueLog.logT(t);
					}
	}
	

	
	
	
	private boolean isAutoGenerateFromModel() {
		return 	autoFigureGenerationOptions.autoGenerateFromModel;
	}

	boolean showPanelCreationOptions() {
		if (isAutoGenerateFromModel()) return false;
		if (autoFigureGenerationOptions.showPanelDialog==false) return false;
		
		return true;
	}

	
	
	
	@Override
	public String getCommand() {
		return "addImageLayer"+openFile+isAutoGenerateFromModel();
	}

	@Override
	public String getMenuCommand() {
		if (useOpen) {
			return "Figure from active "+ nameType();
		}
		String output= "Figure from open "+ nameType();
		if (openFile)  output="Figure from saved "+nameType();
		return output;
	}
	
	@Override
	public boolean canUseObjects(LayerSelectionSystem graphicTreeUI) {
		return true;
	}


	public MultiChannelDisplayCreator getMultiChannelOpener() {
		if (CurrentAppContext.getMultichannelContext()==null) {
			IssueLog.log("no multichannel opening package is available ");
			return null;
		}
		return CurrentAppContext.getMultichannelContext().getMultichannelOpener() ;
	}
	
	private String nameType() {
		if (getMultiChannelOpener()!=null) return getMultiChannelOpener().imageTypeName();
			return "Image";
		
	}
	}
	
	

	

	

	

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
 * Date Modified: April 23, 2022
 * Version: 2022.2
 */
package addObjectMenus;

import ultilInputOutput.FileFinder;

import java.util.HashMap;

import appContext.CurrentAppContext;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import figureEditDialogs.ScaleLevelInputDialog;
import figureEditDialogs.SubStackDialog;
import figureFormat.AutoFigureGenerationOptions;
import figureFormat.FigureTemplate;
import figureFormat.TemplateUserMenuAction;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.FigureType;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import imageScaling.ScaleInformation;
import logging.IssueLog;
import messages.ShowMessage;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import selectedItemMenus.LayerSelectionSystem;

/**this class adds a figure containing a multidimensional images to a layer */
public class FigureAdder extends LayoutAdder {
	
	/**
	 a dialog will be shown to users if they attempt to add create with a very large image
	 */
	private static final int MAX_RECOMMENDED_SIZE_LIMIT_PANELS = 25;

	private static final long serialVersionUID = 1L;

	public AutoFigureGenerationOptions autoFigureGenerationOptions=new AutoFigureGenerationOptions();
	
	
	private boolean makeLabels=false;
	
	/**set to true if limiting the new figure to a single frame*/
	public boolean useSingleFrame=false;
	public boolean useSingleSlice=false;
	
	/**constants indicate what sort of figure, default indicates to folow the saved template*/
	public static final int MERGE_PANELS_ONLY=1, DEFAULT=0, SPLIT_CHANNELS_ONLY=2;
	public int mergeOnly=DEFAULT;
	
	private TemplateUserMenuAction templatesaver =null;
	

	private FigureOrganizingLayerPane currentFigureOrganizer;

	//public boolean openFile=false;//set to true if this opens a file to create a figure
	
	public static enum ImageSource{
		
		USE_ACTIVE_IMAGE("Figure from active "), SAVED_MULTICHANNEL("Figure from saved "), SAVED_SEPARATE("Figure from channels saved in separate files ");
		
		
		String description="";
		private ImageSource(String description) {
			this.description=description;
		}

		/**
		 * @return
		 */
		String getDescription() {
			return description;
		} 
		
		
		}
	
	
	private ImageSource useOpen=ImageSource.USE_ACTIVE_IMAGE;

	private FigureType figureType;

	/**creates a figure adder that relies on a file*/
	public FigureAdder(boolean fromFile) {
		setToUseOpenFile(fromFile);
			
		}
	
	/**creates a figure adder that relies on a file*/
	public FigureAdder(ImageSource useOpen) {
		this.useOpen=useOpen;
			
		}

	/**
	 * @param fromFile
	 */
	public void setToUseOpenFile(boolean fromFile) {
		if(!fromFile)
				useOpen=ImageSource.USE_ACTIVE_IMAGE;
			else 
				useOpen=ImageSource.SAVED_MULTICHANNEL;
	}
	
	/**private FigureAdder(boolean fromFile, boolean autogen) {
		openFile=fromFile;
		this.autoFigureGenerationOptions.autoGenerateFromModel=autogen;
		this.useOpen=autogen;
	}*/
	

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
		
		
		MultichannelDisplayLayer output;
		if(this.useOpen==ImageSource.SAVED_SEPARATE) {
			path= getMultiChannelOpener().createMultichannelFromImageSequence(null, null, null, false);
		}
		
		output= getMultiChannelOpener().creatMultiChannelDisplayFromUserSelectedImage(this.useOpen!=ImageSource.USE_ACTIVE_IMAGE, path);
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
	
	/**Adds a figure to the target layer. If path is not null, the image from the save path is used
	  otherwise, uses the image that is already open. if the preprocess information given is not null
	  sets the cropping and scale based on it. If the Preprocess informatin is null, just uses the selected roi*/
	public FigureOrganizingLayerPane add(GraphicLayer targetLayer, String path, PreProcessInformation p) {
		
		/***/
		
		MultichannelDisplayLayer display=  createMultiChannel(path) ;
		return addNewlyOpenedDisplayLayer(display, targetLayer, p);
		}

	/**Adds a figure to the target layer. If the preprocess information given is not null
	  sets the cropping and scale based on it. 
	  If the Preprocess informatin is null, just uses the selected roi*/
	public FigureOrganizingLayerPane addNewlyOpenedDisplayLayer(MultichannelDisplayLayer display,
			GraphicLayer targetLayer, PreProcessInformation p) {
		if(display==null) 
			return null;
		display.setFigureType(getFigureType());//sets the figure type before th
		/**determines the crop for this image*/
		if (p==null)
			FigureOrganizingLayerPane.cropIfUserSelectionExists(display); 
		else 
			display.getSlot().applyCropAndScale(p);
		
		
		boolean useSingleFrame2 = useSingleFrame;
		boolean useSingleSlice2 = useSingleSlice;
		
		
		if (useSingleFrame2) display.getPanelList().getChannelUseInstructions().limitStackUseToFrame(display.getMultiChannelImage().getSelectedFromDimension(MultiChannelImage.FRAME_DIMENSION));
		if (useSingleSlice2) display.getPanelList().getChannelUseInstructions().limitStackUseToSlice( display.getMultiChannelImage().getSelectedFromDimension(MultiChannelImage.SLICE_DIMENSION));
		
		int n = display.getPanelList().getChannelUseInstructions().estimageNPanels(display.getMultiChannelImage());
		if (n>MAX_RECOMMENDED_SIZE_LIMIT_PANELS) {
			new SubStackDialog(display, true, n+" would be a lot of panels " +"please select substack").showDialog();
		}
		p = promptForScaleDown(display, p);
		
		FigureOrganizingLayerPane figureOrganizerFor = addFigureOrganizerFor(targetLayer, display, p);
		
		
		return figureOrganizerFor;
	}

	/**If the image that is to be added is too large, prompts the user to input a scale factor
	 * @param display
	 * @param p
	 * @return
	 */
	public PreProcessInformation promptForScaleDown(MultichannelDisplayLayer display, PreProcessInformation p) {
		int h = display.getPanelList().getHeight();
		int w= display.getPanelList().getWidth();
		
		if(h>3000||w>3000) {
			if(p==null)
				p=new PreProcessInformation(1);
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("This image would produce very large panels", "Please input a scale factor above");
			hm.put("Note", "You can alays change the scale factor or crop area later");
			
			ScaleLevelInputDialog dialog = new ScaleLevelInputDialog(p.getScaleInformation(), hm, "This is a very large image");
			ScaleInformation newScale = dialog.showUserOption();
			display.getSlot().applyCropAndScale(new PreProcessInformation(p, newScale));
		
		}
		return p;
	}

	/**when given a normal parent layer and a multidimensional image display layer,
	 places the multichannel display layer into a figure organizing layer (which is created if the ordinary layer is not inside of an existing figure organizer). 
	 that figure organizer will contain the multidimensional image inside of it  
	 * @param p */
	public FigureOrganizingLayerPane addFigureOrganizerFor(GraphicLayer ordinaryLayer, MultichannelDisplayLayer multiDimensionalImage, PreProcessInformation p) {

		/**Sets up which figure organizing layer pane should be used*/
		 currentFigureOrganizer = getOrCreateUsedFigureOrganizingLayerPane(ordinaryLayer);
		 if(currentFigureOrganizer==null)
		 	{ 
			 IssueLog.log("Failed to find or create figure organizer for layer "+ordinaryLayer);
			 return null;
			 }
		 
		 /**If the figure organizer is newly created, it will need a new name which depends on the multichannel*/
		if (currentFigureOrganizer!=null&&currentFigureOrganizer.getPrincipalMultiChannel()==null) {
			currentFigureOrganizer.setName("Figure For "+multiDimensionalImage.getName());
			currentFigureOrganizer.setFigureType(this.getFigureType());
		}
		
		multiDimensionalImage.setFigureType(this.getFigureType());
		
		/**opens a figure template or creates one if one does not exist*/
		FigureTemplate temp = getUsedTemplate( multiDimensionalImage, getFigureType());
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
	 returns the figure type that will be used
	 */
	public FigureType getFigureType() {
		if (this.figureType!=null)
			return figureType;
		return FigureType.FLUORESCENT_CELLS;
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
				layout.setFigureType(getFigureType());
				if (getFigureType()==FigureType.WESTERN_BLOT) {
					layout.rowLabelsOnRight=true;//I prefer to label western blots on right
				}
				
				if (makeLabels&&getFigureType().needsLabels()) 
					temp .createDefaultLabelsObjectsFromTemplate( currentFigureOrganizer, multiDimensionalImage, layout);
				if (getFigureType()==FigureType.WESTERN_BLOT) {
					multiDimensionalImage.eliminateChanLabels();//do not need channel labels for a western blot?
				}
				if (getFigureType().needsScaleBar()) 
					temp .createScaleBarOffTemplate( currentFigureOrganizer);
				layout.generateCurrentImageWrapper();
				layout.getEditor().fitLabelSpacesToContents(layout.getPanelLayout());
				
				if (getFigureType()==FigureType.WESTERN_BLOT) {
					multiDimensionalImage.eliminateChanLabels();//do not need channel labels for a western blot?
				}
 
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
		return getUsedTemplate(null, null);
	}
		
	/**returns the figure template to be used by this adder. 
	  if no template is found, creates a new default template*/
	protected FigureTemplate getUsedTemplate(MultichannelDisplayLayer display, FigureType type) {
		;
		if(display!=null &&type==null) {
			type=display.getFigureType();
		}
		
		FigureTemplate template = getTemplatesaver().loadDefaultTemplate(type);
		if (autoFigureGenerationOptions.ignoreSavedTemplate||(template==null&&display!=null)) {
			template=new FigureTemplate(display);
			template.suggestedType=type;
			
			/**if the template is not to be ignored and had to be newly created, tries to save a new template for that figure type*/
			if (!autoFigureGenerationOptions.ignoreSavedTemplate)
				getTemplatesaver().saveDefaultTemplate(template);
		}
		return template;
	}
	
	/**Applies a figure template to the layer
	private void applyUsedTemplate(GraphicLayer g) {
			try{
				getUsedTemplate().applyTemplateToLayer(g);
				}catch (Throwable t) {
					IssueLog.logT(t);
					}
	}*/
	

	
	
	
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
		return "addImageLayer"+isAutoGenerateFromModel()+useOpen.getDescription();
	}

	@Override
	public String getMenuCommand() {
		String command = useOpen.getDescription()+ nameType();
		
		return command;
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

	public void setFigureType(FigureType figureType) {
		this.figureType = figureType;
	}
	
	
	
	
	}
	
	

	

	

	

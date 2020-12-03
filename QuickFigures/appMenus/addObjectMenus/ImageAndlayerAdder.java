package addObjectMenus;

import ultilInputOutput.FileFinder;

import appContext.CurrentAppContext;
import channelMerging.PreProcessInformation;
import figureFormat.AutoFigureGenerationOptions;
import figureFormat.FigureTemplate;
import figureFormat.TemplateUserMenuAction;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import multiChannelFigureUI.MultiChannelDisplayCreator;

/**this class adds a figure containing a multidimensional images to a layer */
public class ImageAndlayerAdder extends LayoutAdder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AutoFigureGenerationOptions autoFigureGenerationOptions=new AutoFigureGenerationOptions();
	
	public boolean openFile=false;//set to true if this opens a file to create a figure
	
	boolean makeLabels=false;
	
	/**set to true if limiting the new figure to a single frame*/
	public boolean useSingleFrame=false;
	public boolean useSingleSlice=false;
	
	private TemplateUserMenuAction templatesaver =null;
	

	private FigureOrganizingLayerPane currentFigureOrganizer;

	/**returns the template saver that is used to create objects*/
	public TemplateUserMenuAction getTemplatesaver() {
		
		if (templatesaver==null) {
			templatesaver = new TemplateUserMenuAction(false, false);
		}
		return templatesaver;
	}

	public void setTemplatesaver(TemplateUserMenuAction templatesaver) {
		this.templatesaver = templatesaver;
	}
	
	public ImageAndlayerAdder(boolean fromFile) {
		openFile=fromFile;
	}
	
	public ImageAndlayerAdder(boolean fromFile, boolean autogen) {
		openFile=fromFile;
		this.autoFigureGenerationOptions.autoGenerateFromModel=autogen;
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
				
					if (gc==null) return null;
					gc.add(l22);
						}
		
		
		
		return l22;
	}
	
	/**when given a file path, opens the Image and returns a the multichannel display layer
	  that contains the open image*/
	public MultichannelDisplayLayer createMultiChannel(String path) {
		return getMultiChannelOpener().creatMultiChannelDisplayFromUserSelectedImage(openFile, path);
	}
	
	/**Adds a figure to the layer*/
	@Override
	public FigureOrganizingLayerPane add(GraphicLayer gc) {
		return add(gc, null);
	}
	
	FileFinder getFileFinder() {
		return new FileFinder();	
	}
	
	
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
		
		if (useSingleFrame) display.getPanelList().getChannelUseInstructions().limitStackUseToFrame(display.getMultiChannelImage().getSelectedFromDimension(2));
		if (useSingleSlice) display.getPanelList().getChannelUseInstructions().limitStackUseToSlice( display.getMultiChannelImage().getSelectedFromDimension(1));
		
		return addFigureOrganizerFor(targetLayer, display);
		}

	/**when given a normal parent layer and a multidimensional image display layer,
	 places the multichannel display layer into a figure organizing layer (which is created if the ordinary layer is not inside of an existing figure organizer). 
	 that figure organizer will contain the multidimensional image inside of it  */
	public FigureOrganizingLayerPane addFigureOrganizerFor(GraphicLayer ordinaryLayer, MultichannelDisplayLayer multiDimensionalImage) {

		/**Sets up which figure organizing layer pane should be used*/
		 currentFigureOrganizer = getOrCreateUsedFigureOrganizingLayerPane(ordinaryLayer);
		 
		 /**If the figure organizer is newly created, it will need a new name which depends on the multichannel*/
		if (currentFigureOrganizer.getPrincipalMultiChannel()==null) {
			currentFigureOrganizer.setName("Figure For "+multiDimensionalImage.getName());
		}
		
		/**opens a figure template or creates one if one does not exist*/
		FigureTemplate temp = getUsedTemplate( multiDimensionalImage);
		/**If there is not template*/
		if (temp==null){
				currentFigureOrganizer.addNovelMultiChannel(multiDimensionalImage, -1);
				showRecreateDialog();
				return currentFigureOrganizer;
			} 
			else 
				addImageToFigureUsingTemplate(currentFigureOrganizer, multiDimensionalImage, temp);
				
		return currentFigureOrganizer;
	}

	/**
	 Using the figure template given, adds the multichannel display layer to the figure organizer given
	 */
	protected void addImageToFigureUsingTemplate(FigureOrganizingLayerPane currentFigureOrganizer, MultichannelDisplayLayer multiDimensionalImage, FigureTemplate temp) {
		/**if the template scale is very large this fixes the issue*/
		boolean d = temp.getMultiChannelPicker()!=null&&temp.getMultiChannelPicker().isProprocessSuitable(multiDimensionalImage);
		temp.getMultiChannelPicker().doesPreprocess=d;
		
		/**must apply before creating the layout so the minimum number of columns can be created
		  it appears that without a template the display will not be added to the figure. TODO: fit this */
		
			temp .addDisplayToFigure(currentFigureOrganizer, multiDimensionalImage);
			showRecreateDialog();


		try{
		if (temp!=null) temp.applyTemplateToLayer(currentFigureOrganizer);
		
		
		
		if (isAutoGenerateFromModel()) {
		multiDimensionalImage.eliminateAndRecreate();
		MontageLayoutGraphic layout = currentFigureOrganizer.getMontageLayoutGraphic();
		if (makeLabels) temp .createDefaultLabelsObjectsFromTemplate( currentFigureOrganizer, multiDimensionalImage, layout);
		temp .createScaleBarOffTemplate( currentFigureOrganizer);
		layout.generateCurrentImageWrapper();
		layout.getEditor().fitLabelSpacesToContents(layout.getPanelLayout());
 
} 

	}catch (Throwable t) {
	t.printStackTrace();
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
		if (template==null&&display!=null) {
			template=new FigureTemplate(display);
			getTemplatesaver().saveDefaultTemplate(template);
		}
		return template;
	}
	/**Applies a figure template to the layer*/
	void applyUsedTemplate(GraphicLayer g) {
			try{
				getUsedTemplate().applyTemplateToLayer(g);
				}catch (Throwable t) {
						t.printStackTrace();
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
		if (isAutoGenerateFromModel()) {
			return "Figure from active "+ nameType();
		}
		String output= "Figure from open "+ nameType();
		if (openFile)  output="Figure from saved "+nameType();
		return output;
	}


	public MultiChannelDisplayCreator getMultiChannelOpener() {
		if (CurrentAppContext.getMultichannelContext()==null) return null;
		return CurrentAppContext.getMultichannelContext().getMultichannelOpener() ;
	}
	
	private String nameType() {
		if (getMultiChannelOpener()!=null) return getMultiChannelOpener().imageTypeName();
			return "Image";
		
	}
	}
	
	

	

	

	

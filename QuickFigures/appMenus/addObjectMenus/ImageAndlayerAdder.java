package addObjectMenus;

import ultilInputOutput.FileFinder;

import appContext.CurrentAppContext;
import figureTemplates.AutoFigureGenerationOptions;
import figureTemplates.FigureTemplate;
import figureTemplates.TemplateSaver;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import multiChannelFigureUI.MultiChannelDisplayCreator;

public class ImageAndlayerAdder extends LayoutAdder {
	
	public AutoFigureGenerationOptions autoFigureGenerationOptions=new AutoFigureGenerationOptions();
	
	public boolean openFile=false;
	boolean firstSet=true;
	boolean makeLabels=false;

	private TemplateSaver templatesaver =null;
	
	//private multiChannelDisplayCreator multiChannelCreator=new IJ1MultiChannelCreator();
	
	//String imageTypeName="ImagePlus";
	//private MultiChannelOpener<?> opener=new ImagePlusOpener();

	private FigureOrganizingLayerPane currentFigureOrganizer;

	private boolean cropOnROI=true;
	
	public TemplateSaver getTemplatesaver() {
		
		if (templatesaver==null) {
			templatesaver = new TemplateSaver(false, false);
		}
		return templatesaver;
	}

	public void setTemplatesaver(TemplateSaver templatesaver) {
		this.templatesaver = templatesaver;
	}
	
	public ImageAndlayerAdder(boolean fromFile) {
		openFile=fromFile;
	}
	
	public ImageAndlayerAdder(boolean fromFile, boolean autogen) {
		openFile=fromFile;
		this.autoFigureGenerationOptions.autoGenerateFromModel=autogen;
	}
	

	
	
	

	
	
	public static FigureOrganizingLayerPane getUsedFigureOrganizingLayerPane(GraphicLayer gc) {
			FigureOrganizingLayerPane l22 = new FigureOrganizingLayerPane("Sub-Figure");
		
			/**What to do if the input layer is a multichannel display*/
			if (gc instanceof MultichannelDisplayLayer) {
				gc=gc.getParentLayer();
			}
			
			/**What to do if the input layer is a FigureOrganizingLayerPane. */
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
	
	public MultichannelDisplayLayer createMultiChannel(String path) {
		return getMultiChannelCreator().creatMultiChannelDisplayFromUserSelectedImage(openFile, path);
	}
	
	
	@Override
	public FigureOrganizingLayerPane add(GraphicLayer gc) {
		return add(gc, null);
	}
	
	FileFinder getFileFinder() {
		return new FileFinder();	
	}
	
	/**uses the information is the Subfigure Object to find the files need to 
	  create a figure
	 
	public void addFromSubFigureObject(GraphicLayer gc, SubFigure subfig) {
		if (subfig==null||subfig.getSourceStackCount()<1) return;
		 currentFigureOrganizer = getUsedFigureOrganizingLayerPane(gc);
		 currentFigureOrganizer.setDescription(subfig.getDescription());
		 FigureTemplate temp = getUsedTemplate();
		
		//add(gc, path);
		for(int i=0; i<subfig.getSourceStackCount(); i++) {
			SourceStackEntry sse = subfig.getSourceStack(i);
			String path = sse.getPath();
			MultichannelImageDisplay multichan = createMultiChannel(getFileFinder().findExistingFilePath(path));
			if (temp==null) getUsedTemplate(multichan);
			temp.addDisplayToFigure(currentFigureOrganizer,
					multichan 
							);
			if (sse.getDescription()==null ||sse.getDescription().equals("null"))
							{
				String s=multichan.getMultichanalWrapper().getTitle();
				sse.setDescription(shorten(s));
							}
			multichan .setDescription(sse.getDescription());
			
			
			currentFigureOrganizer.addRowLabel(sse.getDescription(), i+1);
		}
		temp.createScaleBarOffTemplate(currentFigureOrganizer);
		this.applyUsedTemplate(currentFigureOrganizer);
	} */
	
	public String shorten(String tooLong) {
		if (tooLong==null) return null;
		String output = tooLong.split(";")[0];
		if (output.length()>12) output=output.substring(0, 11);
		return output;
	}
	
	
	
	public FigureOrganizingLayerPane add(GraphicLayer gc, String path) {
		
		firstSet=true;
		/***/
		MultichannelDisplayLayer display=  createMultiChannel(path) ;
		if(display==null) return null;
		FigureOrganizingLayerPane.cropIfUserSelectionExists(display);
		
		return addFigureOrganizerFor(gc, display);
		}

	public FigureOrganizingLayerPane addFigureOrganizerFor(GraphicLayer gc, MultichannelDisplayLayer display) {

		
		
		/**Sets up which figure organizing layer pane should be used*/
		 currentFigureOrganizer = getUsedFigureOrganizingLayerPane(gc);
		if (currentFigureOrganizer.getPrincipalMultiChannel()==null) {
			currentFigureOrganizer.setName("Figure For "+display.getName());
		}
		
		FigureTemplate temp = getUsedTemplate( display);
		
	/**if the template scale is very large this fixes the issue*/
		boolean d = temp.getMultiChannelPicker().isProprocessSuitable(display);
		temp.getMultiChannelPicker().doesPreprocess=d;
		
		/**must apply before creating the layout so the minimum number of columns can be created*/
		if (temp!=null)
			temp .addDisplayToFigure(currentFigureOrganizer, display);
		
		if (showPanelCreationOptions()) {
			currentFigureOrganizer.recreateFigurePanels();
			//display.showOptionsThenRegeneratePanelGraphics();
			}

		
		try{
			if (temp!=null) temp.applyProperties(currentFigureOrganizer);
			
			
			
		if (isAutoGenerateFromModel()) {
			display.eliminateAndRecreate();
			MontageLayoutGraphic layout = currentFigureOrganizer.getMontageLayoutGraphic();
			if (makeLabels) temp .createDefaultLabelsObjectsFromTemplate( currentFigureOrganizer, display, layout);
			temp .createScaleBarOffTemplate( currentFigureOrganizer);
			layout.generateCurrentImageWrapper();
			layout.getEditor().fitLabelSpacesToContents(layout.getPanelLayout());
			 
		} 
		
		}catch (Throwable t) {
			t.printStackTrace();
		}
		return currentFigureOrganizer;
	}
	
	FigureTemplate getUsedTemplate() {
		return getUsedTemplate(null);
	}
		
	/**returns the figure template to be used by this adder. might return null*/
	protected FigureTemplate getUsedTemplate(MultichannelDisplayLayer display) {
		FigureTemplate template = getTemplatesaver().loadDefaultTemplate();
		if (template==null&&display!=null) {
			
			template=new FigureTemplate(display);
			getTemplatesaver().saveDefaultTemplate(template);
		}
		return template;
	}
	
	void applyUsedTemplate(GraphicLayer g) {
			try{
				getUsedTemplate().applyProperties(g);
				}catch (Throwable t) {
						t.printStackTrace();
					}
	}
	

	
	
	
	private boolean isAutoGenerateFromModel() {
		// TODO Auto-generated method stub
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
	public String getMessage() {
		if (isAutoGenerateFromModel()) {
			return "Figure from active "+ nameType();
		}
		String output= "Figure from open "+ nameType();
		if (openFile)  output="Figure from saved "+nameType();
		return output;
	}


	public MultiChannelDisplayCreator getMultiChannelCreator() {
		if (CurrentAppContext.getMultichannelContext()==null) return null;
		return CurrentAppContext.getMultichannelContext().getMultichannelOpener() ;
	}
	
	private String nameType() {
		if (getMultiChannelCreator()!=null) return getMultiChannelCreator().imageTypeName();
			return "Image";
		
	}
	}
	
	

	

	

	

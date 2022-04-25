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
 * Date Modified: Feb 1, 2022
 * Version: 2022.0
 */
package figureOrganizer.insetPanels;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelProperties;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import figureOrganizer.ChannelSubFigureOrganizer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_Shapes.FrameGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import iconGraphicalObjects.IconUtil;
import imageScaling.Interpolation;
import imageScaling.ScaleInformation;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.RectangleEdges;
import popupMenusForComplexObjects.InsetMenu;
import popupMenusForComplexObjects.MenuForMultiChannelDisplayLayer;
import popupMenusForComplexObjects.PanelMenuForMultiChannel;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoLayoutEdit;
import utilityClasses1.ArraySorter;

/**A special inset definer object that the user can use to draw insets with the inset tool.
 * This object is the region of interest that determines which area is displayed
  */
public class PanelGraphicInsetDefiner extends FrameGraphic implements LocationChangeListener, DependentSubFigure, ChannelSubFigureOrganizer{


	
	/**The source panel for the inset definer*/
	private ImagePanelGraphic sourcePanel;
	
	/**A list of the minor panels */
	public PanelList multiChannelStackofInsets;
	
	/**insetpanels and layout will be placed in this layer*/
	public InsetGraphicLayer personalLayer;
	
	/**inset panels will be placed within this layout*/
	public DefaultLayoutGraphic personalLayout;
	
	private ChannelLabelProperties channelLabelProp;//instructions on how this one uses channel labels
	private ChannelLabelManager channelLabelMan;
	
	
	public InsetLayout previosInsetLayout;
	
	/**the scale level of the inset relative to the parent panel. any interpolation method may be used. not just bilinear*/
	private double bilinearScale=2;
	
	/**can set to true to avoid applying a scale operation to the image
	 * in this case, images for panels will be created with the same number of pixels 
	  and panels will be set to a large panel size*/
	private boolean doNotScale=false;
	
	{this.setName("Inset Definer");}
	transient boolean setup=false;
	
	public PanelGraphicInsetDefiner(ImagePanelGraphic p, Rectangle r) {
		super(r);
		this.setFilled(false);
		this.setFillColor(new Color(0,0,0,0));
		this.setFillColor(null);
		setSourcePanel(p);
		updateImagePanels();
	}

	/**returns how many fold larger the inset panels should be compared to the region of interest on the parent panel*/
	public double getInsetScale() {
		return bilinearScale;
	}

	/**sets how many fold larger the inset panels should be*/
	public void setInsetScale(double bilinearScale) {
		this.bilinearScale = bilinearScale;
	}

	
	/**creates a copy*/
	public PanelGraphicInsetDefiner copy() {
		return new PanelGraphicInsetDefiner(getSourcePanel(), this.getBounds());
	}

	/**creates a special layer where panels and labels for an inset whould go*/
	public InsetGraphicLayer createPersonalLayer(String st) {
		return new InsetGraphicLayer(st);
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	
	/**Returns an indicator rectangle to be used by the cropping dialog on the original image,
	 * At the moment, those rectanges merely function to indicate the use of an inset*/
	public RectangularGraphic mapRectBackToUnprocessedVersion(PreProcessInformation p) {
		try{
			
			PreProcessInformation insetProcess = generateInsetPreprocess(p);
			RectangularGraphic r=new RectangularGraphic(insetProcess.getRectangle());
			r.setAngle(insetProcess.getAngle());
			
			r.setStrokeColor(Color.red);
			
			return r;
			} catch (Error e) {
				e.printStackTrace();
				return null;
			}
	}
	
	/**Applies the appropriate crop and scale and returns a version of the source image that is original*/
	public MultiChannelImage generatePreProcessedVersion() {
		
		MultiChannelImage unprocessed = this.getSourceDisplay().getSlot().getUnprocessedVersion(false);
		 MultiChannelImage cropped = unprocessed .cropAtAngle(generateInsetPreprocess(getSourcePreprocess()));
		
		 getSourceDisplay().getSlot().matchOrderAndLuts(cropped);//there was an issue reported on oct 20 2021 with slow performance for this. really the channel order and luts should be same
		 
		 return cropped;
	
	}

	/**returns the proprocess information from the source image
	 * @return
	 */
	protected PreProcessInformation getSourcePreprocess() {
		return getSourceDisplay().getSlot().getModifications();
	}
	
	/**When given the preprocess modifications done on the original image, 
	 * returns what preprocess would need to be used by the inset to create panels*/
	public PreProcessInformation generateInsetPreprocess(PreProcessInformation p) {
		AffineTransform inv = getSourcePanel().getAfflineTransformToCord();
		Rectangle2D b = inv.createTransformedShape(this.getBounds()).getBounds2D();
		if (p==null) 
			return new PreProcessInformation(b.getBounds(), this.getAngle(), createInsetScaleInformation(p));;
		
		double nx=b.getX()/p.getScale();
		double ny=b.getY()/p.getScale();
		double dw=b.getWidth()/p.getScale();
		double dh=b.getHeight()/p.getScale();
		
		Rectangle2D.Double outputRect = new Rectangle2D.Double(nx, ny, dw, dh);
		double angleOutput = getAngle();
		if (p.getRectangle()!=null) try
			{
			
			AffineTransform t = AffineTransform.getTranslateInstance(p.getRectangle().getX(), p.getRectangle().getY());
			RectangularGraphic rCrop1=new RectangularGraphic(p.getRectangle()); rCrop1.setAngle(p.getAngle());
			//t.concatenate(rCrop1.getRotationTransform().createInverse());
			AffineTransform t2 = RectangleEdges.getRotationAboutCenter(rCrop1.getBounds(), -rCrop1.getAngle());
			
			Point2D.Double Oldcenter = new Point2D.Double( outputRect.getCenterX(), outputRect.getCenterY());
			Point2D.Double newcenter = new Point2D.Double();
			Point2D.Double newcenter2 = new Point2D.Double();
			
			t.transform(Oldcenter, newcenter);
			newcenter2=newcenter;
			t2.transform(newcenter, newcenter2);
			RectangleEdges.setLocation(outputRect,RectangleEdges.CENTER, newcenter2.x, newcenter2.y);
			angleOutput+=p.getAngle();
		} catch (Throwable t) {}
		
		ScaleInformation scaleInfo = createInsetScaleInformation(p);
		return new PreProcessInformation(outputRect.getBounds(), angleOutput, scaleInfo);
		
	}

	/**returns the scale information that will be used to scale the image that will be displayed as an inset
	 * may return a scale of 1
	 * @param p
	 * @return
	 */
	protected ScaleInformation createInsetScaleInformation(PreProcessInformation p) {
		double scaleFactor =getInsetScale();
		Interpolation inter=Interpolation.BILINEAR;
		if(p!=null)
			 {
				scaleFactor =p.getScale()*getInsetScale();
				inter=p.getInterpolationType();
			 }
		if(this.isDoNotScale())
			scaleFactor=1;
		return new ScaleInformation(scaleFactor,  inter);
	}

	@Override
	public void userSizeChanged(LocatedObject2D object) {
		
		
	}
	
	
	/** Creates a panel list for this cropper. not used for important functions yet. work in progress*/
	private PanelList createCroppedInsetChannelDisplay(PanelList p) {
		PanelList output = p.createDouble();
	
		return output;
	}
	
	

	
	
	
	
	/**Creates inset panels  */
	public void createMultiChannelInsets() {
		
			MultichannelDisplayLayer d=getSourceDisplay();
			if (d==null) return;
			PanelList stack = createCroppedInsetChannelDisplay(d.getPanelList());
		
			this.multiChannelStackofInsets=stack;
			getPanelManager().generatePanelGraphicsFor(stack);
			updateDisplayPanelImages();		
	}
	
	/**returns the source multichannel*/
	public MultichannelDisplayLayer getSourceDisplay() {
		if (this.getParentLayer() instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer d=(MultichannelDisplayLayer) getParentLayer();
			return d;
			}
		return null;
	}
	
	/**any panels that are linked to the stack for the parent panel's multichannel image
	  are updated this way */
	public void updateDisplayPanelImages() {
		if (multiChannelStackofInsets!=null&&getSourceDisplay()!=null) {
			//multiChannelStackofInsets.setCropper(getproperCropping());
			//setUpListToMakeInset(this.multiChannelStackofInsets,this.getSourceDisplay().getPanelList());//Sets all the cropping and scale to fit
			multiChannelStackofInsets.updateAllPanelsWithImage(getSourceImageForUpdates());}
	}

	/**returns the image that will be used as a source for the update.
	  either the original or a version that has already been cropped might
	  be returned*/
	public MultiChannelImage getSourceImageForUpdates() {
		
			return this.generatePreProcessedVersion();
		
	}
	
	/**any panels that are linked to the stack for the multichannel image
	  are updated this way */
	public void updateDisplayPanelImagesWithChannelName(String name) {
		
		if (multiChannelStackofInsets!=null ) {
			
			multiChannelStackofInsets.updateAllPanelsWithImage(getSourceImageForUpdates(), name);
			
		}
		
		
	}

	
	
	/**updates the image panels that display the inset region*/
	public void updateImagePanels() {
		if (!isValid()) return;//returns if the inset definer is not inside of the source panel
		
		updateDisplayPanelImages() ;
		
	}
	
	/**returns true if the inset graphic is large enough and inside of the parent image panel*/
	public boolean isValid() {
		if (getBounds().getWidth()<2||this.getBounds().getHeight()<2) return false;
		return this.sourcePanel.getBounds().contains(this.getBounds());
	}

	/**returns the image panel that the inset in drawn onto*/
	public ImagePanelGraphic getSourcePanel() {
		return sourcePanel;
	}

	/**sets the panel that the inset is drawn onto*/
	public void setSourcePanel(ImagePanelGraphic sourcePanel) {
		this.sourcePanel = sourcePanel;
	}
	
	/**removes all of the image panels and returns an undoable edit*/
	public CombinedEdit removePanels() {
		CombinedEdit output = new CombinedEdit();
		
		if (sharesPersonalLayer()) {
			output.addEditToList(
					getChannelLabelManager().eliminateChanLabels());
			output.addEditToList(
					getPanelManager().removeDisplayObjectsForAll());
			
			return output;
		}
		
		
		
		
		output.addEditToList(new UndoAbleEditForRemoveItem(getParentLayer(),personalLayer ));
		getParentLayer().remove(this.personalLayer);
			
		output.addEditToList(new UndoAbleEditForRemoveItem(getParentLayer(),this.personalLayout));
		getParentLayer().remove(this.personalLayout);
		
		output.addEditToList(
				getChannelLabelManager().eliminateChanLabels());
		
		if(multiChannelStackofInsets!=null &&multiChannelStackofInsets.getPanels()!=null)
		for(PanelListElement p:multiChannelStackofInsets.getPanels()) {
			ImagePanelGraphic image=(ImagePanelGraphic) p.getImageDisplayObject();
			if (image==null) continue;
			
			output.addEditToList(new UndoAbleEditForRemoveItem(getParentLayer(),image));
			this.getParentLayer().remove(image);
			p.setImageDisplayObject(null);
			
			if (image.getScaleBar()==null) continue;
			output.addEditToList(new UndoAbleEditForRemoveItem(getParentLayer(),image));
			this.getParentLayer().remove(image.getScaleBar());
			
			
		}
		
		return output;
	}
	
	
	/**Overrides the parent. creates the inset pannel*/
	public void createImageInsetDisplay() {}

	public ChannelLabelProperties getChannelLabelProp() {
		if (channelLabelProp==null) channelLabelProp=new ChannelLabelProperties();
		return channelLabelProp;
	}
	
	/**returns a channel label manager, known to have problems*/
	public ChannelLabelManager getChannelLabelManager() {
		GraphicLayer usedLayer = this.getParentLayer();
		if(this.personalLayer!=null) usedLayer =personalLayer;
		if (getSourceDisplay()==null) return null;
		if (channelLabelMan==null) channelLabelMan=new ChannelLabelManager(this.getSourceDisplay(), multiChannelStackofInsets, usedLayer);
		channelLabelMan.setLayer(usedLayer);
		channelLabelMan.setStack(multiChannelStackofInsets);
		return channelLabelMan;
	}
	
	/**returns the channel label menu for the inset*/
	public MenuForMultiChannelDisplayLayer getChannelLabelMenu() {
		return new MenuForMultiChannelDisplayLayer("Channel Label", this.getSourceDisplay(), multiChannelStackofInsets, this.getChannelLabelManager());
		
	}
	
	/**retrns the popup menu that is used for this inset definers*/
	public InsetMenu getMenuSupplier(){
		InsetMenu ii = new  InsetMenu(this);
		
		ii.add( getChannelLabelMenu());
		
		PanelMenuForMultiChannel men3 = new PanelMenuForMultiChannel("Image Panels", getSourceDisplay(), multiChannelStackofInsets, getPanelManager() );
		ii.add(men3.recreateChannelUseMenuItem());
		return ii;
	}
	
	/**returns the inset panel manager*/
	public InsetPanelManager getPanelManager() {
		
		MultichannelDisplayLayer sourceDisplay = this.getSourceDisplay();
		InsetPanelManager panMan = new InsetPanelManager(sourceDisplay, this.multiChannelStackofInsets, this.personalLayer, this);
		panMan.setMultiChannelWrapper( this.generatePreProcessedVersion());
		return panMan;
	}
	
	public void afterUserScaleResize() {
		resizeLayoutPanels(CENTER);
	}

	public void resizeLayoutPanels() {
		resizeLayoutPanels(LOWER_RIGHT);
	}
	
	/**whan a certain handle is moved, resizes the layout panels
	 * @return */
	private AbstractUndoableEdit resizeLayoutPanels(int handlenum) {
		if (this.personalLayout!=null) {
			
			personalLayout.snapLockedItems();
			personalLayout.generateCurrentImageWrapper();
			
			//personalGraphic.getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(personalGraphic.getPanelLayout());
			if(handlenum==TOP||handlenum==BOTTOM) {
				personalLayout.getPanelLayout().getEditor().alterPanelHeightsToFitContents(personalLayout.getPanelLayout());
				personalLayout.getPanelLayout().getEditor().alterPanelHeightsToFitContents(personalLayout.getPanelLayout());//random glitch fix
;
				
			} else 
			if(handlenum==LEFT||handlenum==RIGHT) {
				personalLayout.getPanelLayout().getEditor().alterPanelWidthsToFitContents(personalLayout.getPanelLayout());
				
			} else {
				personalLayout.getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(personalLayout.getPanelLayout());
				
			}
			
			
			return expandParentLayout();
			
		}
		return null;
	}
	
	
	
	
	/**
	 expands the boundry of the parent layout to fit the inset layout
	 As long as the content of the inset layout is in a sublayer of the layer 
	 for the figure organizing layer of the source image, this will work
	 * @return 
	 */
	private CombinedEdit expandParentLayout() {
		CombinedEdit undo=new CombinedEdit();
		FigureOrganizingLayerPane org = FigureOrganizingLayerPane.findFigureOrganizer(sourcePanel);
		if(org!=null) {
		
			DefaultLayoutGraphic montageLayoutGraphic = org.getMontageLayoutGraphic();
			UndoLayoutEdit ule=new UndoLayoutEdit(montageLayoutGraphic);
			undo.addEditToList(ule);
			montageLayoutGraphic.getEditor().expandSpacesToInclude(montageLayoutGraphic.getPanelLayout(), personalLayout.getBounds());
			
			montageLayoutGraphic.getEditor().trimLabelSpacesToFitContents(montageLayoutGraphic.getPanelLayout());
			ule.establishFinalLocations();
			return undo;
		}
		return null;
	}

	/**returns true if another Insetdefiner shares the personal layout and layer with this one*/
	public boolean sharesPersonalLayer() {
		ArrayList<PanelGraphicInsetDefiner> list = getInsetDefinersFromLayer(this.getParentLayer());
		
		for(PanelGraphicInsetDefiner inset1: list) {
			if (inset1!=this && inset1.personalLayer==this.personalLayer) {return true;}
		}
		
		return false;
	}
	
	/**returns true if another Insetdefiner shares the personal layout and layer with this one*/
	public int totalThatSharesPersonalLayer() {
		ArrayList<PanelGraphicInsetDefiner> list = getInsetDefinersFromLayer(this.getParentLayer());
		int output=1;
		for(PanelGraphicInsetDefiner inset1: list) {
			if (inset1!=this && inset1.personalLayer==this.personalLayer) {output++;}
		}
		
		return output;
	}
	

	/**returns the inset definers within a specific layer*/
	public   ArrayList<PanelGraphicInsetDefiner> getInsetDefinersThatShareLayout() {
		GraphicLayer gl=this.getParentLayer();
		ArrayList<ZoomableGraphic> array1 = new ArraySorter<ZoomableGraphic>().getThoseOfClass(gl.getAllGraphics(), PanelGraphicInsetDefiner.class);
		ArrayList<PanelGraphicInsetDefiner> arrayout=new ArrayList<PanelGraphicInsetDefiner>();
		for(ZoomableGraphic ar: array1) {
			PanelGraphicInsetDefiner otherPanelDef = (PanelGraphicInsetDefiner) ar;
			if (otherPanelDef.personalLayer==this.personalLayer) arrayout.add(otherPanelDef);
		}
		return arrayout;
	}
	
	/**returns the inset definers that have the specified channel label*/
	public static PanelGraphicInsetDefiner findInsetWith(ChannelLabelTextGraphic z) {
		GraphicLayer layer = z.getParentLayer().getTopLevelParentLayer();
		ArrayList<PanelGraphicInsetDefiner> defs = getInsetDefinersFromLayer(layer);
		for(PanelGraphicInsetDefiner definer: defs) {
			if (definer.getChannelLabelManager().getChannelLabelProp()==z.getChannelLabelProperties())
				return definer;;
		}
		return null;
	}
	
	
	/**returns the inset definers that have the specified channel label*/
	public static PanelGraphicInsetDefiner findInsetWith(ImagePanelGraphic z) {
		GraphicLayer layer = z.getParentLayer().getTopLevelParentLayer();
		ArrayList<PanelGraphicInsetDefiner> defs = getInsetDefinersFromLayer(layer);
		for(PanelGraphicInsetDefiner definer: defs) {
			if (definer.getPanelManager().getListElementFor(z)!=null)
				return definer;
		}
		return null;
	}
	/**returns the inset definers within a specific layer*/
	public static ArrayList<PanelGraphicInsetDefiner> getInsetDefinersFromLayer(GraphicLayer gl) {
		ArrayList<ZoomableGraphic> array1 = new ArraySorter<ZoomableGraphic>().getThoseOfClass(gl.getAllGraphics(), PanelGraphicInsetDefiner.class);
		ArrayList<PanelGraphicInsetDefiner> arrayout=new ArrayList<PanelGraphicInsetDefiner>();
		for(ZoomableGraphic ar: array1) {
			PanelGraphicInsetDefiner otherPanelDef = (PanelGraphicInsetDefiner) ar;
			
			arrayout.add(otherPanelDef);
		}
		return arrayout;
		
	}
	
	/**if edit is requested */
	public AbstractUndoableEdit2 provideDragEdit() {
		
		return null;
		
	}
	
	/**a layer that is used to store objects that are part of the inset figure*/
	public class InsetGraphicLayer extends GraphicLayerPane {

		public InsetGraphicLayer(String name) {
			super(name);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		@Override
		public Icon getTreeIcon(boolean open) {
			
			return createDefaultTreeIcon2(open);
		
		}
	}
	
static Color  folderColor2= new Color(0,140, 0);
		public static Icon createDefaultTreeIcon2(boolean open) {
			return IconUtil.createFolderIcon(open, folderColor2);
		}
		
		
		
	/**A special panel manager designed for the inset panels*/
	public static class InsetPanelManager extends PanelManager {

		private PanelGraphicInsetDefiner inset;

		public InsetPanelManager(MultichannelDisplayLayer multichannelImageDisplay, PanelList stack,
				GraphicLayer multichannelImageDisplay2, PanelGraphicInsetDefiner panelGraphicInsetDef) {
			super(multichannelImageDisplay, stack, multichannelImageDisplay2);
			this.inset=panelGraphicInsetDef;
		}
		
		/**a working change ppi function. Alters the pixel density of the inset panels if the inset is set to scale its panels */
		@Override
		public CombinedEdit changePPI(double newppi) {
			if(getInset().isDoNotScale())
				return null;
			ImagePanelGraphic panel = getPanelList().getPanels().get(0).getPanelGraphic();
			double ppi = panel.getQuickfiguresPPI();
			double newPanelScale=panel.getRelativeScale()*ppi/newppi;
			double newScale=getInset().getInsetScale()*newppi/ppi;
			if (getInset().getSourceDisplay().getSlot().getModifications()!=null) newScale/=getInset().getSourceDisplay().getSlot().getModifications().getScale();
			
			for(PanelListElement panel2: getPanelList().getPanels()) {
				ImagePanelGraphic panelGraphic = panel2.getPanelGraphic();
				panelGraphic.setLocationType(RectangleEdges.UPPER_LEFT);
				panelGraphic.setRelativeScale(newPanelScale);
			}
			this.setPanelLevelScale(newPanelScale);
			getInset().setInsetScale(newScale);
			updatePanels();
			getInset().updateDisplayPanelImages();
			return null;
		}

		public PanelGraphicInsetDefiner getInset() {
			return inset;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}



	
	public void objectMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	
	public void objectSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	public void objectEliminated(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	
	public void userMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setLocation(double x, double y) {
		super.setLocation(x, y);
		updateImagePanels();
	}
	@Override
	public void moveLocation(double xmov, double ymov) {
		super.moveLocation(xmov, ymov);
		updateImagePanels();
	}
	

	

	/**Called after a rectangular graphics handle is moved
	 * overrides the superclass function
	 * @param handleNumber
	 * @param p1
	 * @param p2
	 */
	@Override
	public void afterHandleMove(int handleNumber, Point2D p1, Point2D p2) {
		updateImagePanels();
		resizeLayoutPanels(handleNumber);
		
	}
	
	
	public AbstractUndoableEdit removeInsetAndPanels() {
		
		getParentLayer().remove(this);
		
		return null;
	}
	
	/**checks if the setup function has run already, it not, runs that function*/
	protected void ensureSetup() {
		if (setup) return;
		onsetup();
		setup=true;
	}
	
	/**refreshers the image panels*/
	public void onsetup() {
		
		updateImagePanels();
		setup=true;
	}
	
	@Override 
	public void draw(Graphics2D g, CordinateConverter cords) {
		this.ensureSetup();
		super.draw(g, cords);
	}

	
	/**returns the resize applied to the panels*/
	public double getPanelSizeInflation() {
		if(this.isDoNotScale())
			return bilinearScale;
					
					return 1;
	}

	/**returns the relative size/scale that should be applied to inset panels
	 * @return
	 */
	public double getReccomendedPanelScale() {
		double output=sourcePanel.getRelativeScale();
		if (this.isDoNotScale())
			output=output*this.getInsetScale()*getSourcePreprocess().getScale();
		return output;
	}

	/**set to true if scaling should be avoided
	 * @param dontScale
	 */
	public void setDoNotScale(boolean dontScale) {
		this.doNotScale=dontScale;
		
	}

	/**updates the panel size to match the reccomended sise*/
	public void updateRelativeScaleOfPanels() {
			ArrayList<ImagePanelGraphic> newpanels = this.getPanelManager().getPanelList().getPanelGraphics();
		for(ImagePanelGraphic panel1:newpanels) {
			panel1.setRelativeScale(getReccomendedPanelScale());
		}
	}

	/**returns true if the inset should avoid creating a scaled version*/
	public boolean isDoNotScale() {
		return doNotScale;
	}

	/**Updates the panels with the given real channel name
	 * @param name
	 */
	@Override
	public void updateChannel(String name) {
		if(name==null) {
			getPanelManager().updatePanels();
			
		} else
		getPanelManager().updatePanelsWithChannel(name);
	}

	/**returns true if the panel is part of the figure
	 * @param image 
	 * @return
	 */
	@Override
	public boolean producesObject(Object image) {
		return getPanelManager().getPanelList().getPanelGraphics().contains(image);
	}
	

}

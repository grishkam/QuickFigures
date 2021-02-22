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
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package figureOrganizer.insetPanels;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelProperties;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
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
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.RectangleEdges;
import locatedObject.ScaleInfo;
import logging.IssueLog;
import popupMenusForComplexObjects.InsetMenu;
import popupMenusForComplexObjects.MenuForMultiChannelDisplayLayer;
import popupMenusForComplexObjects.PanelMenuForMultiChannel;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;
import utilityClasses1.ArraySorter;

/**A special inset definer object that the user can use to draw insets with the inset tool.
  */
public class PanelGraphicInsetDefiner extends FrameGraphic implements LocationChangeListener{


	public InsetGraphicLayer createPersonalLayer(String st) {
		return new InsetGraphicLayer(st);
	}
	
	/**The source panel for the inset definer*/
	
	private ImagePanelGraphic sourcePanel;
	public PanelList multiChannelStackofInsets;
	public InsetGraphicLayer personalLayer;
	public DefaultLayoutGraphic personalLayout;
	private ChannelLabelProperties channelLabelProp;//instructions on how this one uses channel labels
	private ChannelLabelManager channelLabelMan;
	public InsetLayout previosInsetLayout;
	private double bilinearScale=2;
	
	{this.setName("Inset Definer");}
	transient boolean setup=false;

	public double getBilinearScale() {
		return bilinearScale;
	}

	public void setBilinearScale(double bilinearScale) {
		this.bilinearScale = bilinearScale;
	}

public PanelGraphicInsetDefiner(ImagePanelGraphic p, Rectangle r) {
		super(r);
		setSourcePanel(p);
		updateImagePanels();
	}
	
	public PanelGraphicInsetDefiner copy() {
		return new PanelGraphicInsetDefiner(getSourcePanel(), this.getBounds());
	}

	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScaleInfo getScaleInfoForSourceImage() {
		
		return getSourcePanel().getScaleInfo();
	}

	/**returns the buffered image for the source panel*/
	public BufferedImage getBuffImage() {
		
		try {
		
		BufferedImage b=getSourcePanel().getBufferedImage();
		
		BufferedImage b2=new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = b2.getGraphics();
		g.setColor(Color.red);
		g.fillRect(0, 0, b.getWidth(), b.getHeight());
		g.drawImage(b,0,  0, b.getWidth(), b.getHeight(), 0, 0, b.getWidth(), b.getHeight(), null);
		
		return b2;
		} catch (Throwable t) {
			IssueLog.logT(t);
			return null;
		}
	}

	
	/**Returns an indicator rectangle to be used by the cropping dialog*/
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
		 MultiChannelImage cropped = unprocessed .cropAtAngle(generateInsetPreprocess(getSourceDisplay().getSlot().getModifications()));
		 getSourceDisplay().getSlot().matchOrderAndLuts(cropped);
		 return cropped;
	
	}
	
	/**When given the preprocess modifications done on the original image, 
	 * returns what preprocess would need to be used by the inset*/
	public PreProcessInformation generateInsetPreprocess(PreProcessInformation p) {
		AffineTransform inv = getSourcePanel().getAfflineTransformToCord();
		Rectangle2D b = inv.createTransformedShape(this.getBounds()).getBounds2D();
		if (p==null) 
			return new PreProcessInformation(b.getBounds(), this.getAngle(), getBilinearScale());;
		
		double nx=b.getX()/p.getScale();
		double ny=b.getY()/p.getScale();
		double dw=b.getWidth()/p.getScale();
		double dh=b.getHeight()/p.getScale();
		
		Rectangle2D.Double outputRect = new Rectangle2D.Double(nx, ny, dw, dh);
		double angleOutput = getAngle();
		if (p.getRectangle()!=null) try
		{
			//this part does not put the rect in the right place
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
		
		return new PreProcessInformation(outputRect.getBounds(), angleOutput, p.getScale()*getBilinearScale());
		
	}

	@Override
	public void userSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	/**does the bilinear scale and returns the scaled up version of the image
	@Deprecated
	public Image getImagePixelsScaledBilinear(double bilinearScale) {
		IssueLog.log("Called get scaled picture");
		PanelListElement potentialPanel1 = getSourcePanel().getSourcePanel();
		
		if (potentialPanel1!=null) {
			//this.setCropping();
			PixelWrapper outputpanel = potentialPanel1.getImageWrapped().copy(null);
			outputpanel.crop(getproperCropping().getBounds());
			outputpanel.resizeBilinear(outputpanel.width()*this.getBilinearScale(), outputpanel.height()*this.getBilinearScale());
			
			return outputpanel.image();
		}
		
		
		super.getImageInset().setCroppingRect(getproperCropping().getBounds());
		return getBuffImage();

	}*/
	
	
	/**super experimental. Creates a panel list for this cropper*/
	public PanelList createCroppedInsetChannelDisplay(PanelList p) {
		PanelList output = p.createDouble();
	//	setUpListToMakeInset(output, p);
		return output;
	}
	
	

	
	
	
	
	/**Creates inset versions of the multichannel insets*/
	public void createMultiChannelInsets() {
		//GraphicLayer layer = this.getParentLayer();
		
			MultichannelDisplayLayer d=getSourceDisplay();
			if (d==null) return;
			PanelList stack = createCroppedInsetChannelDisplay(d.getPanelList());
		
			this.multiChannelStackofInsets=stack;
			getPanelManager().generatePanelGraphicsFor(stack);
			updateDisplayPanelImages();		
	}
	
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

	/**
	 */
	@Deprecated
	protected void oldPanelUpdate(String name) {
		multiChannelStackofInsets.updateAllPanelsWithImage(getSourceImageForUpdates(), name);
	}
	
	
	public void updateImagePanels() {
		if (!isValid()) return;//returns if the inset definer is not inside of the source panel
		
		updateDisplayPanelImages() ;
	}
	
	/**returns true if the inset graphic is large enough and inside of the parent image panel*/
	public boolean isValid() {
		if (getBounds().getWidth()<2||this.getBounds().getHeight()<2) return false;
		return this.sourcePanel.getBounds().contains(this.getBounds());
	}


	public ImagePanelGraphic getSourcePanel() {
		return sourcePanel;
	}


	public void setSourcePanel(ImagePanelGraphic sourcePanel) {
		this.sourcePanel = sourcePanel;
	}
	
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
	
	public MenuForMultiChannelDisplayLayer getChannelLabelMenu() {
		return new MenuForMultiChannelDisplayLayer("Channel Label", this.getSourceDisplay(), multiChannelStackofInsets, this.getChannelLabelManager());
		
	}
	
	public InsetMenu getMenuSupplier(){
		InsetMenu ii = new  InsetMenu(this);
		
		ii.add( getChannelLabelMenu());
		
		PanelMenuForMultiChannel men3 = new PanelMenuForMultiChannel("Image Panels", getSourceDisplay(), multiChannelStackofInsets, getPanelManager() );
		ii.add(men3.recreateChannelUseMenuItem());
		return ii;
	}
	
	public PanelManager getPanelManager() {
		
		MultichannelDisplayLayer sourceDisplay = this.getSourceDisplay();
		PanelManager panMan = new InsetPanelManager(sourceDisplay, this.multiChannelStackofInsets, this.personalLayer, this);
		panMan.setMultiChannelWrapper( this.generatePreProcessedVersion());
		return panMan;
	}
	
	public void afterUserScaleResize() {
		resizeMontageLayoutPanels(CENTER);
		
	}

	public void resizeMontageLayoutPanels() {
		
		resizeMontageLayoutPanels(LOWER_RIGHT);
		
	}
	
	/**whan a certain handle is moved, resizes the panels*/
	public void resizeMontageLayoutPanels(int handlenum) {
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
			
			
		}
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
		
		
		
	public class InsetPanelManager extends PanelManager {

		private PanelGraphicInsetDefiner inset;

		public InsetPanelManager(MultichannelDisplayLayer multichannelImageDisplay, PanelList stack,
				GraphicLayer multichannelImageDisplay2, PanelGraphicInsetDefiner panelGraphicInsetDef) {
			super(multichannelImageDisplay, stack, multichannelImageDisplay2);
			this.inset=panelGraphicInsetDef;
		}
		
		public CombinedEdit changePPI(double newppi) {
			ImagePanelGraphic panel = getPanelList().getPanels().get(0).getPanelGraphic();
			double ppi = panel.getQuickfiguresPPI();
			double newPanelScale=panel.getScale()*ppi/newppi;
			double newScale=inset.getBilinearScale()*newppi/ppi;
			if (getSourceDisplay().getSlot().getModifications()!=null) newScale/=getSourceDisplay().getSlot().getModifications().getScale();
			
			for(PanelListElement panel2: getPanelList().getPanels()) {
				ImagePanelGraphic panelGraphic = panel2.getPanelGraphic();
				panelGraphic.setLocationType(RectangleEdges.UPPER_LEFT);
				panelGraphic.setRelativeScale(newPanelScale);
			}
			this.setPanelLevelScale(newPanelScale);
			inset.setBilinearScale(newScale);
			updatePanels();
			inset.updateDisplayPanelImages();
			return null;
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
	 * @param handleNumber
	 * @param p1
	 * @param p2
	 */
	@Override
	public void afterHandleMove(int handleNumber, Point2D p1, Point2D p2) {
		updateImagePanels();
		resizeMontageLayoutPanels(handleNumber);
		
	}
	
	
	public AbstractUndoableEdit removeInsetAndPanels() {
		
		getParentLayer().remove(this);
		
		return null;
	}
	
	
	protected void ensureSetup() {
		if (setup) return;
		onsetup();
		setup=true;
	}
	
	public void onsetup() {
		
		updateImagePanels();
		setup=true;
	}
	
	@Override 
	public void draw(Graphics2D g, CordinateConverter cords) {
		this.ensureSetup();
		super.draw(g, cords);
	}

}

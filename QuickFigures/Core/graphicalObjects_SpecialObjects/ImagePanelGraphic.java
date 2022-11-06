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
 * Date Modified: Nov 6, 2022
 * Version: 2022.1
 */
package graphicalObjects_SpecialObjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import popupMenusForComplexObjects.ImagePanelMenu;
import undo.AbstractUndoableEdit2;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.ProvidesDialogUndoableEdit;
import undo.UndoScalingAndRotation;
import utilityClasses1.ArraySorter;
import animations.KeyFrameAnimation;
import appContext.ImageDPIHandler;
import export.pptx.ImagePanelImmitator;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import export.svg.ImageSVGExporter;
import export.svg.SVGExportable;
import export.svg.SVGExporter;
import figureOrganizer.FigureType;
import figureOrganizer.PanelListElement;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import handles.HasSmartHandles;
import handles.ImagePanelHandleList;
import handles.AttachmentPositionHandle;
import handles.SmartHandleList;
import handles.miniToolbars.HasMiniToolBarHandles;
import handles.miniToolbars.ImagePanelActionHandleList;
import icons.TreeIconForImageGraphic;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.HasIllustratorOptions;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.PathItemRef;
import illustratorScripts.PlacedItemRef;
import imageDisplayApp.GraphicSetDisplayWindow;
import imageDisplayApp.ImageWindowAndDisplaySet;
import includedToolbars.StatusPanel;
import keyFrameAnimators.ImagePanelGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.AttachedItemList;
import locatedObject.PointsToFile;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;
import locatedObject.ScaleInfo;
import locatedObject.ScalededItem;
import locatedObject.Scales;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelSwapHandleList;
import objectDialogs.CroppingDialog;
import objectDialogs.ImageGraphicOptionsDialog;

/**an object that displays an image inside a frame at a specified size.
 * May also have an additional cropping operation*/
public class ImagePanelGraphic extends BasicGraphicalObject implements TakesAttachedItems, HasTreeLeafIcon,ScalededItem,HasIllustratorOptions ,Scales,IllustratorObjectConvertable, PointsToFile, RectangleEdgePositions, OfficeObjectConvertable,  SVGExportable, HasSmartHandles, HasMiniToolBarHandles, ProvidesDialogUndoableEdit{

	
	/**Images temporarily stored*/
	transient BufferedImage img;
	/**image stored long term as a byte array that can be serialized*/
	byte[] serializedIm=null;
	
	/**set to true while certain handles are being dragged*/
	public boolean dragOngoing=false;
	/**
	 * 
	 */
	/**The width of the panel object.*/
	private double width;
	/**The height of the panel object.*/
	private double height;

	 private Color frameColor=Color.white;
		private double frameWidthv=0;
		private double frameWidthh=0;
		private boolean uniformFrameWidth=true;
		
	protected static int imagePanelUserLocked=NOT_LOCKED;//determines if the user is allowed to move image panels by directly clicking and draging

	{setLocationType(UPPER_LEFT);
	name="Image Graphic";
	
	}
	
	/**this is the image used to draw*/
	private transient Image displayedImage;
	
	/**the scale relative to the points*/
	private double scale=1;
	boolean embed=false;
	 
	/**If the image is the product of opening a file, these fields store information on that*/
	protected File file=null;
	private boolean filederived=false;
	private boolean loadFromFile=false;
    boolean filefound=false;
    
    /**if set to true, will show overlay*/
    private boolean showOverlay=false;
	 
	
	private static final long serialVersionUID = 1L;
	  AttachedItemList lockedItems=new AttachedItemList(this);

	/**an options crop area that is limited to this specific panel*/
	 Rectangle croppingrect=null;
	

		
		public ImagePanelGraphic() {}
		
		/**Creats a new image panel from the image given*/
		public ImagePanelGraphic(BufferedImage bi) {
			setImage(bi);
		}
		
		/**loads a file*/
		public ImagePanelGraphic(File f) {
			file=f;
			filederived=true;
			img=loadFromFile(f);
		}
		 
		 /**Sets whether the object embeds the image or not*/
		 public void setEmbed(boolean em) {
			 embed=em;	
			 if (!em)  serializedIm=null;
		 }
		 
		 /**Since BufferedImages are not Serializable, image is stored in a byte array*/
		 private void saveEmbeded() {
			 try {
				serializedIm=imageToByteArray(getBufferedImage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
		
		 /**returns a byte array for a buffered image. used to embed the image*/
		public static byte[] imageToByteArray(BufferedImage image) throws IOException {
		  if (image==null) return null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ImageIO.write(image, "png", baos);
		    return baos.toByteArray();
		}

		/**Retrieves the image, if the image is null will try a few methods to obtain 
		 * the missing image. May attempt to open a file or to recreate the image based on a a stored byte array
		 or just create a new image*/
		public BufferedImage getBufferedImage() {
			if (img!=null) return img;
			
			if (img==null && embed==false && this.isLoadFromFile()) {
				img=loadFromFile(file);
				if (!this.isFilefound() && embed&&serializedIm!=null) {
					img=null;
				}
			}
			
			
			if (img==null && embed==false){ 
				img=new BufferedImage((int)(getObjectWidth()/getRelativeScale()), (int)(getObjectHeight()/getRelativeScale()),  BufferedImage.TYPE_INT_RGB);}
			
			
			if (img==null && embed==true) {
					try {
					img=getImageFromByteArray(serializedIm);
					
						} catch (Throwable t) {
								img=new BufferedImage((int)(getObjectWidth()/getRelativeScale()), (int)(getObjectHeight()/getRelativeScale()),  BufferedImage.TYPE_INT_RGB);
								}
			}
			
			return img;
			
			}

	 
	 public BufferedImage getImageFromByteArray(byte[] serializedIm) throws IOException {
		 ByteArrayInputStream baos = new ByteArrayInputStream(serializedIm);
			
				return ImageIO.read( baos);
		
	 }
	 
	 public boolean isEmbed() {
		 return embed;
	 }
	 
	/**copies the settings of the given image*/ 
	 public void copyAttributesFrom(ImagePanelGraphic bg) {
		this.setFrameWidthH(bg.getFrameWidthH());
		this.setFrameWidthV(bg.getFrameWidthV());
		this.setFrameColor(bg.getFrameColor());
		this.setEmbed(bg.isEmbed());
		this.setRelativeScale(bg.getRelativeScale());

	 }
	 
	 /**returns the width of the stored image (in pixels)*/
		public int getUnderlyingImageWidth() {return getBufferedImage().getWidth();}
	/**returns the height of the stored image (in pixels)*/
		public int getUnderlyingImageHeight() {return getBufferedImage().getHeight();}
		
		
		/**returns a cropped version of this image
		  this is a cropped image*/
		public BufferedImage getProcessedImageForDisplay() {
			if (getObjectWidth()==0||getObjectHeight()==0) computeWidths();
			if (this.isCroppintRectValid()) return getBufferedImage().getSubimage((int)this.getCroppingRect().getX(), (int)this.getCroppingRect().getY(), (int)this.getCroppingRect().getWidth(), (int) this.getCroppingRect().getHeight());
			return this.getBufferedImage();
		}
		
		/**returns a version that can be saved as a png and opened. Required for some export methods*/
		public BufferedImage getPNGExportImage() {
			return getProcessedImageForDisplay();
		}

	 
	 /**refreshes the scaled copy that is kept ready to draw*/
		protected void ensureDisplayedImage() {
			setDisplayedImage(getProcessedImageForDisplay());
		}
		
		
	
	@Override
	public boolean hasLockedItem(LocatedObject2D l) {
		return getLockedItems().contains(l);
	}
	

	public static Icon createImageIcon() {
		return new TreeIconForImageGraphic(Color.white);//return i.getIcon(0);
	}

	@Override
	public Icon getTreeIcon() {
		return new TreeIconForImageGraphic(frameColor);
	}

	
	public AttachedItemList getLockedItems() {
		if (lockedItems==null) lockedItems=new AttachedItemList(this);
		return lockedItems;
	}
	
	/**Adds an attached item to the panel*/
	public void addLockedItem(LocatedObject2D l) {
		if (l==null) return;
		if (l instanceof BarGraphic) {
			BarGraphic bar = (BarGraphic) l;
			this.setScaleBar(bar);
			}
		
		getLockedItems().add(l);
		addHandleForAttachedItem(l);
		this.snapLockedItems();
	}

	/**creates a handle for adjusting the location of an attached item
	 * @param l
	 */
	public void addHandleForAttachedItem(LocatedObject2D l) {
		SmartHandleList list = getPanelHandleList();
		getPanelHandleList().add(new AttachmentPositionHandle(this, l, list.size()));
	}
	
	public void removeLockedItem(LocatedObject2D l) {
		 removeScaleBar(l);
		getLockedItems().remove(l);
		getSmartHandleList().removeLockedItemHandle(l);
	}
	
	private void removeScaleBar(Object l) {
		if (l instanceof BarGraphic &&l==this.getScaleBar()) {this.setScaleBar(null);}
	}
	
	public BarGraphic getScaleBar() {
		return scaleBar;
	}

	private void setScaleBar(BarGraphic scaleBar) {
		this.scaleBar = scaleBar;
		if (scaleBar!=null) {
			if (scaleBar.getAttachmentPosition()==null)scaleBar.setAttachmentPosition(AttachmentPosition.defaultScaleBar());
			updateBarScale();
		}
	}
	
	/**updates the scale bar to ensure that it is drawn correctly. Also updates any additional scale bars
	  that may be attached*/
	public void updateBarScale() {
		BarGraphic scaleBar=this.scaleBar;
		updateScaleBar(scaleBar);
		for(LocatedObject2D i:this.getLockedItems()) {
			if (i instanceof BarGraphic) updateScaleBar((BarGraphic) i);
		}
	}

	/**
	updates the given scale bar
	 */
	private void updateScaleBar(BarGraphic scaleBar) {
		if (scaleBar!=null) {
			scaleBar.setScaleInfo(getDisplayScaleInfo());
			scaleBar.getAttachmentPosition().snapLocatedObjects(getScaleBar(), this);
			scaleBar.setUpBarRects();
		}
	}
	
	private BarGraphic scaleBar=null;
	
	/**returns a version of the scale information that can be used by the scale bar*/
	@Override
	public ScaleInfo getDisplayScaleInfo() {
		return info.getScaledCopyXY(getRelativeScale());
	}
	
	

	/**information about the pixel width and depth*/
	 ScaleInfo info=new ScaleInfo();


	 /**A panel list element that is associated with this image panel */
	private PanelListElement sourcePanel;


	private transient ImagePanelHandleList panelHandleList;
	private transient ImagePanelActionHandleList aHandleList;

	private transient ChannelSwapHandleList extraHandles;
	private FigureType figureType;
	private OverlayObjectList overlayObjects;
	


	
	 
	 	/**returns the scale factor that determines the size at which the image panel is displayed.*/
		public double getRelativeScale() {
			return scale;
		}
		/**sets the scale factor that determines the size at which the image panel is displayed.*/
		public void setRelativeScale(double scale) {
			if (scale==getRelativeScale()) return;
			if (scale<=0) return;
			Point2D pi=getLocation();
			this.scale = scale;
			computeWidths();
			setLocation(pi.getX(), pi.getY());
			ensureDisplayedImage();
			notifyListenersOfSizeChange();
		}

		

		/**returns the information used to calculate the scale bar's length*/
		@Override
		public ScaleInfo getScaleInfo() {
			return info;
		}
		/**sets the information used to calculate the scale bar's length*/
		@Override
		public void setScaleInfo(ScaleInfo s) {
			info=s;
			updateBarScale();
		}
		
		/**returns the rectangle used to draw the frame*/
		public Rectangle2D getFrameRect() {
			return new Rectangle2D.Double(x-getFrameWidthV(), y-getFrameWidthV(), getObjectWidth()+2*getFrameWidthV(), getObjectHeight()+2*getFrameWidthV());
		}

		
		/**returns the frame width in the vertical direction*/
		public double getFrameWidthV() {
			return frameWidthv;
		}

		
		
		/**returns the frame width in the horizontal direction*/
		public double getFrameWidthH() {
			
			if (uniformFrameWidth) return getFrameWidthV();
			return frameWidthh;
		}
		
		/**sets the frame width Sets both the horizontal and the vertical frame width*/
		public void setFrameWidth(double d) {
			boolean changed = false;
			if(frameWidthh !=d)
				changed=true;
			if(frameWidthv !=d)
				changed=true;
			this.frameWidthh = d;
			this.frameWidthv = d;
			if(changed)
				notifyListenersOfSizeChange();
		}

		/**sets the horizontal frame width*/
		public void setFrameWidthH(double d) {
			if(d<0) d=0;//negative numbers not allowed
			if (frameWidthh==d) return;
			if (uniformFrameWidth) this.frameWidthv=d;
			this.frameWidthh = d;
			notifyListenersOfSizeChange();
		}
		
		/**sets the vertical frame width*/
		public void setFrameWidthV(double size) {
			if(size<0) size=0;
			if (frameWidthv==size) return;
			if (uniformFrameWidth) this.frameWidthh=size;
			this.frameWidthv = size;
			notifyListenersOfSizeChange();
		}
		
		public boolean isFrameWidthUniform() {
			return uniformFrameWidth;
		}
		
		
		@Override
		public Rectangle getBounds() {
			return getBounds2D().getBounds();
		}

		public Double getBounds2D() {
			return new Rectangle2D.Double(x,y,getObjectWidth(), getObjectHeight() );
		}
		
		@Override
		public Rectangle getContainerForBounds(LocatedObject2D l) {
			return getBounds();
		}
		
		@Override
		public Rectangle getExtendedBounds() {
			return extendRect(getBounds(), getFrameWidthH(), getFrameWidthV());
		}
		
		public static Rectangle extendRect(Rectangle r, double d, double e) {
			Rectangle nr = new Rectangle(r);
			nr.x-=e;
			nr.y-=d;
			nr.width+=e*2;
			nr.height+=d*2;
			return nr;
		}
		
		 
		 /**Draws the image*/
		 @Override
			public void draw(Graphics2D graphics, CordinateConverter cords) {
			 
			
			 
				computeWidths();
				snapLockedItems() ;
				double x1 = cords.transformX(x);
				double x2 = cords.transformX((x+getObjectWidth()));
				double y1 =cords.transformY(y);
				double y2 = cords.transformY( (y+getObjectHeight()));
				
				int displaywidth=(int) (getObjectWidth()/this.getRelativeScale());
				int displayheight=(int) (getObjectHeight()/this.getRelativeScale());
				
				
				if (getDisplayedImage()==null) ensureDisplayedImage();

				Image image = getDisplayedImage();
				if (image!=null){
					
					
						double sx1 = cords.transformX(getCenterOfRotation().getX());
						double sy1 = cords.transformY(getCenterOfRotation().getY());
					   graphics.rotate(-angle, sx1, sy1);
					   
					  drawFrame(graphics, cords, x1, x2, y1, y2); 
					   
					   graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					   graphics.drawImage(image, (int) x1,  (int)y1, (int)x2, (int)y2, 0, 0, displaywidth, displayheight, null);
					  
					   graphics.rotate(angle, sx1, sy1);
						}
				
			
				  if (selected) {
					  getPanelHandleList().updateHandleLocs();
					  getSmartHandleList().draw(graphics, cords);
					
					   
				   }
				 
				if(isShowOverlay())  
					drawOverlayObjects(graphics, cords);
			
			}

		/**Draws any overlay objects that are completely inside the region of interest
		 * @param graphics
		 * @param cords
		 */
		public void drawOverlayObjects(Graphics2D graphics, CordinateConverter cords) {
			try {
				  if(getOverlay()==null) {
					  
				  } else {
					  Rectangle2D r = new Rectangle2D.Double(0,0, this.getObjectWidth()/scale, this.getObjectHeight()/scale);
					  
					  CordinateConverter cordsOverlay = cords.getCopyScaled(scale).getCopyTranslated((int)(-this.getLocationUpperLeft().getX()/scale), (int)(-this.getLocationUpperLeft().getY()/scale));
					
					  for(Object object: getOverlay().getOverlayObjects())  try {
						  if(object instanceof LocatedObject2D) {
							  boolean inside = r.contains(((LocatedObject2D) object).getBounds());
		
							  if(!inside)
								  continue;
						  }
						  if(object instanceof ZoomableGraphic) {
							  ZoomableGraphic z=(ZoomableGraphic) object;
							  z.draw(graphics, cordsOverlay);
						  }
					  }catch (Throwable t){
						  IssueLog.logT(t);
					  }
				  }
			  } catch (Throwable t){
				  IssueLog.logT(t);
			  }
		}

		/**
		 * Draws the Frame
		 */
		public void drawFrame(Graphics2D graphics, CordinateConverter cords, double x1, double x2, double y1,
				double y2) {
			if (getFrameWidthV()>0||getFrameWidthH()>0) {
				   graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);						
				   double scaleFrame=this.getFrameWidthV()*cords.getMagnification();
					double scaleFrame2=this.getFrameWidthH()*cords.getMagnification();
					
					Rectangle2D rect2 = new Rectangle2D.Double((x1-scaleFrame2 ), (y1-scaleFrame), (x2-x1+2*scaleFrame2), (y2-y1+2*scaleFrame));
					
				   graphics.setColor(getFrameColor());
				   graphics.fill(rect2);
			   }
		}

	

		private Image getDisplayedImage() {
			return displayedImage;
		}

		private void setDisplayedImage(Image displayedImage) {
			this.displayedImage = displayedImage;
			updateBarScale();
		}
		
		

		
		/**
		Notifies the listeners of a size change for this object
		*/
		public void notifyListenersNow() {
			getListenerList().notifyListenersOfUserSizeChange(this);
		}
		
		/**Returns the cropped size of the buffered image*/
		public Rectangle getCroppedImageSize() {
			if (!this.isCroppintRectValid())
			return new Rectangle(0,0, this.getUnderlyingImageWidth(), this.getUnderlyingImageHeight());
		
			return new Rectangle(0,0,this.getCroppingRect().width, this.getCroppingRect().height);
		}
		
		/**returns true if the current cropping rectangle can be used as a crop area for the image*/
		public boolean isCroppintRectValid() {
			if (getCroppingRect()==null) return false;
			return isItAValidCrop(getCroppingRect());
		}
		
		/**returns true if the given rectangle can be used as a crop area for the image
		 * @param cropAngle2 */
		public boolean isItAValidCrop(Rectangle2D r) {
			if (r==null) return false;
			Rectangle rectangle = new Rectangle(0,0, this.getUnderlyingImageWidth(), this.getUnderlyingImageHeight());
			
			return rectangle.contains(r);
		}
		
		/**returns the cropping rectangle*/
		public Rectangle getCroppingRect() {
			return croppingrect;
		}

		
		/**Sets a cropping rectangle if the given rectangle is appropriate for defining a crop area
		 * @param cropAngle */
		public void setCroppingRect(Rectangle croppingrect) {
			
			if (this.croppingrect!=null && this.croppingrect.equals(croppingrect)) return;
			if (!this.isItAValidCrop(croppingrect)&&croppingrect!=null) return;
		
			this.croppingrect = croppingrect;
			
			this.computeWidths();
			this.ensureDisplayedImage();
		}
		
		
		
		/**Based on the number of pixels in the image, the scale and the crop area,
		  calculates the width and height of this object. */
		private void computeWidths() {
			double newwidth=getRelativeScale()* getUnderlyingImageWidth();
			double newheight=getRelativeScale()* getUnderlyingImageHeight();
			
			this.setObjectWidth(newwidth);
			this.setObjectHeight(newheight);
			if (this.isCroppintRectValid()) {
				setObjectWidth(getRelativeScale()*croppingrect.getWidth());
				setObjectHeight(getRelativeScale()*croppingrect.getHeight());
			}
			updateBarScale();
			
		}
			
	
		
		/**transforms */
		public AffineTransform getAfflineTransformToCord() {
			AffineTransform af = new AffineTransform();
			if (this.isCroppintRectValid()) {
				af.translate(this.getCroppingRect().getX(), this.getCroppingRect().getY());
			}
			af.scale(1/scale, 1/scale);
			//af.translate(-transformX(0), -transformY(0));
			af.translate(-x, -y); 
			
			return af;
		}
		
		
static int typeIllsEx=1;
		

protected File prepareImageForExport(PlacedItemRef pir) {
	Image i= getPNGExportImage() ;
	if (i==null) {
		IssueLog.log("no image returned");
	}
	
	
	File f = pir.prepareImageForJavaScript(i, getName(), x,y, false);
	return f;
}

		@Override
		public Object toIllustrator(ArtLayerRef aref) {
			
		
			
			if (hasFrame()){
				PathItemRef pi=new PathItemRef();
				pi.createRectangle(aref, getFrameRect());
				pi.setFilled(true);
				pi.setFillColor(getFrameColor());
				pi.setName("frame");
				pi.setStoke(0);
				pi.setStrokeColor(getFrameColor());
			
		}
			
			PlacedItemRef pir = new PlacedItemRef();
			pir.createItem(aref);
			
			if (typeIllsEx==1) {
				prepareImageForExport(pir);
			pir.resize(100*this.getRelativeScale(), 100*getRelativeScale());
			if (getName()!=null) pir.setName(getName()); //Bug fix: a null name would previously case a problem with the position of the panel
	
			pir.setLeftandTop(x, y);
			} 
			
			else {
				Image i=this.getPNGExportImage() ;
				pir.prepareImageForJavaScript(i, getName(), x,y, false);
				pir.resize(100*this.getRelativeScale(), 100*getRelativeScale());
		
				pir.setLeftandTop(x, y);
			}
			
			
			
			pir.embed();
			
			if (this.isShowOverlay()){
				this.extractOverlay().toIllustrator(aref);
		}
			
			return pir;
		}

		/**returns true if this image has a visible frame around it
		 * @return
		 */
		public boolean hasFrame() {
			return this.getFrameWidthH()>0||this.getFrameWidthV()>0;
		}

		
		
		/**Returns a scaled version of the image, that may have the colors altered*/
		public Image createScaledAndProcessedImageForDisplay() {
			if (getObjectWidth()==0||getObjectHeight()==0) computeWidths();
			
			return  getProcessedImageForDisplay().getScaledInstance((int)getObjectWidth(), (int)getObjectHeight(), Image.SCALE_SMOOTH);
			} 
		
		
		public void snapLockedItems() {
			AttachedItemList items = getLockedItems();
			for(int i=0; i<items.size(); i++) {
				snapLockedItem(items.get(i));
			}
		}
		
		@Override
		public void snapLockedItem(LocatedObject2D o) {
			if (o==null) return;
			AttachmentPosition sb = o.getAttachmentPosition();
				if (sb==null) {
					o.setAttachmentPosition(AttachmentPosition.defaultInternal());
					sb=o.getAttachmentPosition();
					}
				
				sb.snapLocatedObjects(o, this);
		}
		
		
		public PopupMenuSupplier getMenuSupplier(){
			return new  ImagePanelMenu(this);
		}
		
		/**when given a source panel, sets the AWT image for this graphic
		  as that panels image. Does NOT change the stored awt image
		  for this ImageGraphic when the panel changes*/
	public void setSourcePanel(PanelListElement panel) {
		if (panel==null) return;//bugfix not sure why it is turning out null
			this.sourcePanel=panel;
			setImage((BufferedImage) panel.getAwtImage());
			setOverlayObjects(panel.getOverlayObjects());
			setScaleInfo(panel.getDisplayScaleInfo());
			this.updateBarScale();
			
		}
		
	/**Sets the overlay object list
		 * @param overlayObjects
		 */
		public void setOverlayObjects(OverlayObjectList overlayObjects) {
			if(this.overlayObjects==overlayObjects)
				return;
			 closeOverlayEditingWindow();
			this.overlayObjects=overlayObjects;
			
		}

	/**Sets the image*/
		public void setImage(BufferedImage img) {
			
			if (img==null) return;
			if (img.getWidth()==0) return;
			this.img = img;
			ensureDisplayedImage();
			computeWidths();
			notifyListenersOfSizeChange();
			updateBarScale();
		}
		
		@Override
		public void showOptionsDialog() {
			ImageGraphicOptionsDialog ig = new ImageGraphicOptionsDialog(this);
			ig.showDialog();
			//new BufferedImageGraphicDialog(this);
		}

		/**shows a cropping dialog specific to the individual panel. This is not compatible with overlays yet*/
		public void showCroppingDialog() {
			CroppingDialog cd = new CroppingDialog();
			cd.hideRotateHandle=true;
			try{cd.showDialog(this);} catch (Throwable t) {
				IssueLog.logT(t);
			}
			//IssueLog.log("The cropping rect is valid? "+this.isCroppintRectValid(), this.getCroppingrect().toString());
		}

	
		/**returns the color of the frame*/
		public Color getFrameColor() {
			return frameColor;
		}

		/**sets the color of the frame*/
		public void setFrameColor(Color frameColor) {
			this.frameColor = frameColor;
		}
		
		/**changes the image data to a serializable form before serialization of this object*/
		private void writeObject(java.io.ObjectOutputStream out)
			     throws IOException {

			if (this.isEmbed()) {
				saveEmbeded();
			}
			out.defaultWriteObject();
		}
		
		@Override
		public ImagePanelGraphic copy() {
			ImagePanelGraphic out = new ImagePanelGraphic(this.getBufferedImage());
			if (getCroppingRect()!=null) out.setCroppingRect(getCroppingRect().getBounds());
			out.copyAttributesFrom(this);
			out.setLocationUpperLeft(this.getLocationUpperLeft().getX(), this.getLocationUpperLeft().getY());
			out.setScaleInfo(getScaleInfo().copy());
			return out;
		}
		
		/**Creates a buffered image with the words "File not found" printeds*/
		 protected BufferedImage createFileNotFountImage(double width, double height) {
			 String text = "Image File"+'\n'+" Not Found";
			 int w = (int)(width/getRelativeScale());
			int h = (int)(height/getRelativeScale());
			return createImageWithText(text, w, h, 10);
		}

		/**
		An image with a message to the user
		 */
		public static BufferedImage createImageWithText(String text, int w, int h, int fontsize) {
			BufferedImage img=new BufferedImage(w, h,  BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics();
			g.setFont(new Font("Arial", Font.BOLD, fontsize));
			g.setColor(Color.RED);
			
			g.drawString(text, 0, (int) (1.5*fontsize));
			return img;
		}
		 
		 /**Reads an image from a file. returns null if there is 
		   any problem.*/
			public static BufferedImage readFromFile(File f) {
				if (f==null) return null;
				if (!f.exists()) return null;
				 BufferedImage img;
				try {
					img = ImageIO.read(f);
				} catch (Throwable e) {
					IssueLog.logT(e);
					return null;
				}
				 return img;
			}
			
			
		/**returns true if there is (or was) a file that stores the underlying image*/
		public boolean isFilederived() {
			if (this.file==null) return false;
			return filederived;
		}

		/**returns true if the method to find the image (if missing) is to load from the original source file*/
		public boolean isLoadFromFile() {
			if (!isFilederived()) return false;
			return loadFromFile;
		}

		
		/**returns the image saved in file f, if this fails, 
		  creates a file not found message image*/
		public BufferedImage loadFromFile(File imagefile) {
			BufferedImage img=readFromFile(imagefile);
			if (img!=null) filefound=true;else  {
			img=createFileNotFountImage(getObjectWidth(), getObjectHeight());
			 filefound=false;
			}
			return img;
		}

		public boolean isFilefound() {
			return filefound;
		}

		public void setLoadFromFile(boolean loadFromFile) {
			this.loadFromFile = loadFromFile;
		}

		@Override
		public void showOptionsForExport() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void scaleAbout(Point2D p, double mag) {
			Point2D p2 = this.getLocationUpperLeft();
			p2=scalePointAbout(p2, p,mag,mag);
			this.setRelativeScale(this.getRelativeScale()*mag);
			double nfh = this.getFrameWidthH()*mag;
			double nfv = this.getFrameWidthV()*mag;
			this.setFrameWidthH(nfh);
			this.setFrameWidthV(nfv);
			this.setLocationUpperLeft(p2);
			this.snapLockedItems();
		}
		
		public String getSummary() {
			return "PPI= "+getIllustratorPPI() +" ("+getDimensionString()+")"+"("+getRealDimensionString()+")";
		}
		
		public String getMinimalSummary() {
			return "PPI= "+getIllustratorPPI() +" ("+getRealDimensionString()+")";
		}

		
		public String getIllustratorPPI() {
			return  ""+(int)getQuickfiguresPPI();
		}

		public double getQuickfiguresPPI() {
			return ImageDPIHandler.getInchDefinition()/getRelativeScale();
		}
		
		public String getInkscapePPI() {
			return  ""+(int)(90/getRelativeScale());
		}
		
		public int getScreenPPI() {
			return (int)(Toolkit.getDefaultToolkit().getScreenResolution()/getRelativeScale());
		}

		
		public String getDimensionString() {
			return  (getBounds().getWidth()+" X "+this.getBounds().getHeight()+"");
		}
		
		public String getInchDimensionString() {
			return  (getBounds().getWidth()/getScreenPPI()+" X "+this.getBounds().getHeight()/getScreenPPI()+" inch");
		}
		
		public String getRealDimensionString() {
			return  (getBufferedImage().getWidth()+" X "+getBufferedImage().getHeight()+" pixels");
		}
		public String getTwoDimensions() {
			return getDimensionString()+'\n'+" ("+getRealDimensionString()+")";
		}

		public double getObjectHeight() {
			return height;
		}
		protected void setObjectHeight(double height) {
			this.height = height;
		}
		public double getObjectWidth() {
			return width;
		}
		protected void setObjectWidth(double width) {
			this.width = width;
		}
		
		public double[] getDimensionsInUnits() {
			return this.getScaleInfo().convertPixelsToUnits( new Dimension(this.getBufferedImage().getWidth(), this.getBufferedImage().getHeight()));
	}

		@Override
		public Point2D getLocationUpperLeft() {
			return new Point2D.Double(x,y);
		}

		@Override
		public void setLocationUpperLeft(double x, double y) {
			this.x=x;
			this.y=y;
			
		}

		@Override
		public Shape getOutline() {
			return new Rectangle2D.Double(x, y, width, height);
		}
		
		/**returns the location. the type of location may vary depending on the location type (@see RectangleEdgePosisions)*/
		@Override
		public Point2D getLocation() {
			Point2D out = RectangleEdges.getLocation(getLocationType(), getBounds());
			return new Point2D.Double(out.getX(), out.getY());
		}

		
		/**sets the location. the type of location may vary depending on the location type (@see RectangleEdgePosisions)*/
		@Override
		public void setLocation(double x, double y) {
			Rectangle2D.Double r=this.getBounds2D();
			RectangleEdges.setLocation(r,getLocationType(), x,y);
			this.setLocationUpperLeft(r.getX(), r.getY());
			super.notifyListenersOfMoveMent();
		}


		public Shape getShape() {
			return getBounds();
		}
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name=name;
		}

		@Override
		public OfficeObjectMaker getObjectMaker() {
			// TODO Auto-generated method stub
			return new ImagePanelImmitator(this);
		}

		@Override
		public SVGExporter getSVGEXporter() {
			return new ImageSVGExporter(this);
		}

		public PanelListElement getSourcePanel() {
			return sourcePanel;
		}

	
		public  KeyFrameAnimation getOrCreateAnimation() {
			if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
			animation=new ImagePanelGraphicKeyFrameAnimator(this);
			return (KeyFrameAnimation) animation;
		}

		/**If the image is derived from a specific saved image file, this returns the file*/
		@Override
		public File getFile() {
			if (filederived&&file!=null) return file;
			return null;
		}
		
		public Dimension getDimensions() {
			return new Dimension(getBounds().width, getBounds().height);
		}
		
		/**returns a list that contains both the image panels handles and the attached item handles*/
		protected ImagePanelHandleList getPanelHandleList() {
			if (panelHandleList==null) 
				{
						panelHandleList=new ImagePanelHandleList(this);
						for(LocatedObject2D l: this.getLockedItems()) {
							this.addHandleForAttachedItem(l);
						}
				}
				return panelHandleList;
		}
		
		/**
		returns the action handle list of the image panel
		 */
		protected ImagePanelActionHandleList getActionHandleList() {
			if (aHandleList==null) aHandleList=createActionHandleList();
				return aHandleList;
		}

		/**
		Creates an action handle list for the image panel
		 */
		public ImagePanelActionHandleList createActionHandleList() {
			return new ImagePanelActionHandleList(this);
		}

		@Override
		public SmartHandleList getSmartHandleList() {
			 {
				SmartHandleList out = new SmartHandleList();
				if (this.extraHandles!=null&&superSelected) 
					out.addAll(extraHandles);
				out.addAll(getPanelHandleList());
				
			if (!dragOngoing&&superSelected)	{
					getActionHandleList().updateLocation();
					out.addAll(this.getActionHandleList());
				}
				return out;
			}
			//return getPanelHandleList();
		}
		
		@Override
		public int handleNumber(double x, double y) {
			if (this.getSmartHandleList()!=null) {
				
				int output=this.getSmartHandleList().handleNumberForClickPoint(x, y);
				
				return output;
			}
			
			return NO_HANDLE;

		}

		public void setUserLocked(int j) {
			imagePanelUserLocked=j;
		}
		public int isUserLocked() {
			return imagePanelUserLocked;
		}
		
		/**Looks for items in the parent layer that may potentially be accepted as locked items but are not currently attached*/
		@Override
		public ArrayList<LocatedObject2D> getNonLockedItems() {
			TakesAttachedItems taker = this;
			Rectangle rect = extendRect(getExtendedBounds(), 4,4);//in order to consider items just outside the panel
			ArrayList<LocatedObject2D> potentialAttachments = this.getLockedItems().getEligibleNONLockedItems(taker, rect );
			ArraySorter.removeThoseOfClass(potentialAttachments, this.getClass());//in order not to consider other panels
			return  potentialAttachments;
		
		}

		public void setExtraHandles(ChannelSwapHandleList handles) {
			extraHandles=handles;
			
		}

		public ChannelSwapHandleList getExtraHandles() {
			return extraHandles;
		}

		

		@Override
		public void select() {
			selected=true;
			dragOngoing=false;
			try {
				StatusPanel.updateStatus(getMinimalSummary());
				
			} catch (Exception e) {
			}
		}
		
		public String[] getScaleWarning() {
			this.snapLockedItems();
			return new String[] {"Image Panel Pixel Density is now "+this.getIllustratorPPI()};
		}

		@Override
		public AbstractUndoableEdit2 provideUndoForDialog() {
			return new CombinedEdit(new UndoScalingAndRotation(this), new ColorEditUndo(this));
		}

		/**sets the figure type
		 * @param figureType
		 */
		public void setFigureType(FigureType figureType) {
			this.figureType=figureType;
			
		}
		
		/**
		 returns the figure type. never returns null
		 */
		public FigureType getFigureType() {
			if (this.figureType!=null)
				return figureType;
			return FigureType.FLUORESCENT_CELLS;
		}

		public boolean isShowOverlay() {
			return showOverlay;
		}

		public void setShowOverlay(boolean showOverlay) {
			this.showOverlay = showOverlay;
		}
		
		/**returns a scaled copy of the overlay*/
		public GraphicLayerPane extractOverlay() {
			GraphicLayerPane added = new GraphicLayerPane("Overlay Copy");
			try {
				  if(getOverlay()==null) {
					  IssueLog.log("no overlay objects detected");
				  } else {
					  Rectangle2D sizeOfImagePanel = new Rectangle2D.Double(-1/scale,-1/scale, (getObjectWidth()+getFrameWidthH()+2)/scale, (getObjectHeight()+getFrameWidthV()+2)/scale);
					 
					 
					  ArrayList<?> overlayObjectList = getOverlay().getOverlayObjects();
					  if(overlayObjectList.size()==0) {
						  IssueLog.log("There are no objects listed. cannot extract overlay ");
					  }
					  
					for(Object object: overlayObjectList)  try {
						 
						  if(object instanceof ShapeGraphic) {
							 
							  Rectangle objectbounds = ((ShapeGraphic) object).getBounds();
							  ShapeGraphic copy = ((ShapeGraphic) object).copy();
							 
							  if (copy instanceof BasicShapeGraphic) {
								  copy=copy.createPathCopy();
							  }
							  copy.scaleAbout(new Point2D.Double(0,0), scale);
							  
							  boolean inside = sizeOfImagePanel.contains(objectbounds);
							  boolean overlaps = sizeOfImagePanel.intersects(objectbounds);//might be used later
							  
							  copy.moveLocation((int)(this.getLocationUpperLeft().getX()), (int)(this.getLocationUpperLeft().getY()));
							 
								
							  if(!inside) { 
								
								    continue;
							  } 
							  else {
								  added.addItemToLayer(copy);
								
									
							  }
						  } else {
							  IssueLog.log("failed to extract item "+object);
						  }
						  
					  }catch (Throwable t){
						  IssueLog.logT(t);
					  }
				  }
			  } catch (Throwable t){
				  IssueLog.logT(t);
			  }
			if(added.getAllGraphics().size()==0) {
				IssueLog.log(" have not added any items ");
			}
			return added;
		}

		/**returns an overlay object list*/
		public OverlayObjectList getOverlay() {
			return overlayObjects;
		}
		
		/**The current overlay editing window*/
		transient GraphicSetDisplayWindow overlayEditingwindow;

		/**Sets the overlay editing window
		 * @param newwindow
		 */
		public void setOverlayEditingWindow(GraphicSetDisplayWindow graphicSetDisplayWindow) {
			closeOverlayEditingWindow();
			this.overlayEditingwindow=graphicSetDisplayWindow;
			
		}

		/**
		 * closes the overlay editing window if there is one
		 */
		private void closeOverlayEditingWindow() {
			if(overlayEditingwindow!=null)
				overlayEditingwindow.setVisible(false);
		}

		/**closes any overlay editing windows*/
		public void kill() {
			super.kill();
			closeOverlayEditingWindow();
		}

}

package graphicalObjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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
import javax.swing.undo.AbstractUndoableEdit;

import officeConverter.ImagePanelImmitator;
import officeConverter.OfficeObjectConvertable;
import officeConverter.OfficeObjectMaker;
import popupMenusForComplexObjects.ImagePanelMenu;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.ProvidesDialogUndoableEdit;
import undo.UndoScalingAndRotation;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LockedItemList;
import utilityClassesForObjects.PointsToFile;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.ScaleInfo;
import utilityClassesForObjects.ScalededItem;
import utilityClassesForObjects.Scales;
import utilityClassesForObjects.SnappingPosition;
import utilityClassesForObjects.TakesLockedItems;
import animations.KeyFrameAnimation;
import externalToolBar.IconSet;
import fieldReaderWritter.ImageSVGExporter;
import fieldReaderWritter.SVGExportable;
import fieldReaderWritter.SVGExporter;
import genericMontageKit.PanelListElement;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.ImagePanelActionHandleList;
import graphicalObjectHandles.ImagePanelHandleList;
import graphicalObjectHandles.LockedItemHandle;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.HasIllustratorOptions;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.PathItemRef;
import illustratorScripts.PlacedItemRef;
import includedToolbars.StatusPanel;
import keyFrameAnimators.ImagePanelGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelSwapHandleList;
import objectDialogs.CroppingDialog;
import objectDialogs.ImageGraphicOptionsDialog;

/**an object that displays an image inside a frame with a specified scale and cropping*/
public class ImagePanelGraphic extends BasicGraphicalObject implements TakesLockedItems, HasTreeLeafIcon,ScalededItem,HasIllustratorOptions ,Scales,IllustratorObjectConvertable, PointsToFile, RectangleEdgePosisions, OfficeObjectConvertable,  SVGExportable, HasSmartHandles, ProvidesDialogUndoableEdit{

	
	
	/**
	 * 
	 */
	private double width;
	private double height;
	
	protected static int imagePanelUserLocked=0;//determines if the user is allowed to move image panels by directly clicking and draging

	{setLocationType(BufferedImageGraphic.UPPER_LEFT);
	name="Image Graphic";
	
	}
	
	/**this is the image used to show the actual display*/
	private transient Image displayedImage;
	/**the scale*/
	double scale=1;
	boolean embed=false;
	 
	protected File file=null;
	private boolean filederived=false;
	private boolean loadFromFile=false;
    boolean filefound=false;
	 
	
	private static final long serialVersionUID = 1L;
	  LockedItemList lockedItems=new LockedItemList(this);
	  
	  
		 private Color frameColor=Color.white;
		private double frameWidthv=2;
		private double frameWidthh=2;
		private boolean uniformFrameWidth=true;
	
	
	 Rectangle croppingrect=null;



		/**Images temporarily stored*/
		transient BufferedImage img;
		/**image stored long term*/
		byte[] serializedIm=null;

		
		
		public ImagePanelGraphic() {}
		
		public ImagePanelGraphic(BufferedImage bi) {
			setImage(bi);
		}
		
		public ImagePanelGraphic(File f) {
			file=f;
			filederived=true;
			img=loadFromFile(f);
		}
		 
		 /**Sets whether the object embeds the image or not*/
		 public void setEmbed(boolean em) {
			 embed=em;
			// if (em)	 saveEmbeded();
			
			 if (!em)  serializedIm=null;
		 }
		 
		 private void saveEmbeded() {
			 try {
				serializedIm=imageToByteArray(getBufferedImage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
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

		 /**returns the dimensions of the stored image.
		   this must not be allowed to return null.
		   */
		/**Retrieves the image, if the image is null will try to recreate it based on the embedded image*/
		public BufferedImage getBufferedImage() {
			
			if (img==null && embed==false && this.isLoadFromFile()) {
				img=loadFromFile(file);
				if (!this.isFilefound() && embed&&serializedIm!=null) {
					img=null;
				}
			}
			if (img==null && embed==false){ img=new BufferedImage((int)(getObjectWidth()/getScale()), (int)(getObjectHeight()/getScale()),  BufferedImage.TYPE_INT_RGB);}
			if (img==null && embed==true) {
					try {
				
					img=getImageFromByteArray(serializedIm);
					
						} catch (Throwable t) {
								img=new BufferedImage((int)(getObjectWidth()/getScale()), (int)(getObjectHeight()/getScale()),  BufferedImage.TYPE_INT_RGB);
								}
			}
			//IssueLog.log("image code is "+img.toString());
			return img;
			
			}

	 
	 public BufferedImage getImageFromByteArray(byte[] serializedIm) throws IOException {
		 ByteArrayInputStream baos = new ByteArrayInputStream(serializedIm);
			
				return ImageIO.read( baos);
		
	 }
	 
	 public boolean isEmbed() {
		 return embed;
	 }
	 
	 /**Sets whether the object embeds the image or not*/

	 
	 public void copyAttributesFrom(ImagePanelGraphic bg) {
		//super.copyAttributesFrom(bg);
		this.setFrameWidthH(bg.getFrameWidthH());
		this.setFrameWidthV(bg.getFrameWidthV());
		this.setFrameColor(bg.getFrameColor());
		this.setEmbed(bg.isEmbed());
		this.setScale(bg.getScale());

	 }
	 
	 /**returns the dimensions of the stored image (in pixels)*/
		public int getUnderlyingImageWidth() {return getBufferedImage().getWidth();}
		public int getUnderlyingImageHeight() {return getBufferedImage().getHeight();}
		
		
		/**returns an image that is the postprocessed version of this image
		  this is a cropped image*/
		public BufferedImage getProcessedImageForDisplay() {
			if (getObjectWidth()==0||getObjectHeight()==0) computeWidths();
			if (this.isCroppintRectValid()) return getBufferedImage().getSubimage((int)this.getCroppingrect().getX(), (int)this.getCroppingrect().getY(), (int)this.getCroppingrect().getWidth(), (int) this.getCroppingrect().getHeight());
			return this.getBufferedImage();
		}
		
		/**returns a version that can be saved as a png and opened.*/
		public BufferedImage getPNGExportImage() {
			return getProcessedImageForDisplay();
		}

	 
	 /**refreshes the scaled copy*/
		protected void ensureDisplayedImage() {
			setDisplayedImage(getProcessedImageForDisplay());
		}
		
		
	
	@Override
	public boolean hasLockedItem(LocatedObject2D l) {
		return getLockedItems().contains(l);
	}
	

	transient static IconSet i;
	
	public static Icon createImageIcon() {
		if (i==null) i=new IconSet("iconsTree/TreeImageGraphicIcon.png");
		return i.getIcon(0);//new ImageIcon(i.getIcon(0));
	}

	@Override
	public Icon getTreeIcon() {
		return createImageIcon();
	}

	
	
	
	public LockedItemList getLockedItems() {
		if (lockedItems==null) lockedItems=new LockedItemList(this);
		return lockedItems;
	}
	
	public void addLockedItem(LocatedObject2D l) {
		if (l==null) return;
		if (l instanceof BarGraphic) {
			BarGraphic bar = (BarGraphic) l;
			this.setScaleBar(bar);
			}
		
		getLockedItems().add(l);
		SmartHandleList list = getPanelHandleList();
		getPanelHandleList().add(new LockedItemHandle(this, l, list.size()));
		this.snapLockedItems();
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
			if (scaleBar.getSnapPosition()==null)scaleBar.setSnappingBehaviour(SnappingPosition.defaultScaleBar());
			updateBarScale();
		}
	}
	
	public void updateBarScale() {
		if (scaleBar!=null) {
			scaleBar.setScaleInfo(getDisplayScaleInfo());
			this.snapLockedItems();
			scaleBar.getSnapPosition().snapLocatedObjects(getScaleBar(), this);
			scaleBar.setUpBarRects();
		}
		
	}
	
	private BarGraphic scaleBar=null;
	
	@Override
	public ScaleInfo getDisplayScaleInfo() {
		return info.getScaledCopyXY(getScale());
	}
	
	

	/**information about the pixel width and depth*/
	 ScaleInfo info=new ScaleInfo();


	private PanelListElement sourcePanel;


	private ImagePanelHandleList panelHandleList;
	private transient ImagePanelActionHandleList aHandleList;

	private ChannelSwapHandleList extraHandles;


	
	 
	 
		public double getScale() {
			return scale;
		}

		public void setScale(double scale) {
			if (scale==getScale()) return;
			if (scale<=0) return;
			Point2D pi=getLocation();
			this.scale = scale;
			computeWidths();
			setLocation(pi.getX(), pi.getY());
			ensureDisplayedImage();
			notifyListenersOfSizeChange();
		}

		

		
		@Override
		public ScaleInfo getScaleInfo() {
			return info;
		}

		@Override
		public void setScaleInfo(ScaleInfo s) {
			info=s;
			updateBarScale();
		}
		
		public Rectangle2D getFrameRect() {
			return new Rectangle2D.Double(x-getFrameWidthV(), y-getFrameWidthV(), getObjectWidth()+2*getFrameWidthV(), getObjectHeight()+2*getFrameWidthV());
		}

		

		public double getFrameWidthV() {
			return frameWidthv;
		}

		public void setFrameWidthV(double size) {
			if(size<0) size=0;
			if (frameWidthv==size) return;
			if (uniformFrameWidth) this.frameWidthh=size;
			this.frameWidthv = size;
			notifyListenersOfSizeChange();
		}
		
		
		public double getFrameWidthH() {
			
			if (uniformFrameWidth) return getFrameWidthV();
			return frameWidthh;
		}

		public void setFrameWidthH(double d) {
			if(d<0) d=0;//negative numbers not allowed
			if (frameWidthh==d) return;
			if (uniformFrameWidth) this.frameWidthv=d;
			this.frameWidthh = d;
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
		
		 
		 public void setImageScale(String units, double pwidth, double pheight) {
			 info=new  ScaleInfo(units, pwidth, pheight);
			 updateBarScale();
			 
		 }
		 
		 
		 @Override
			public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
			 
			
			 
				computeWidths();
				snapLockedItems() ;
				double x1 = cords.transformX(x);
				double x2 = cords.transformX((x+getObjectWidth()));
				double y1 =cords.transformY(y);
				double y2 = cords.transformY( (y+getObjectHeight()));
				double scaleFrame=this.getFrameWidthV()*cords.getMagnification();
				double scaleFrame2=this.getFrameWidthH()*cords.getMagnification();
				//Rectangle rect = new Rectangle(x1, y1, x2-x1, y2-y1);
				Rectangle rect2 = new Rectangle((int)(x1-scaleFrame2 ), (int)(y1-scaleFrame), (int)(x2-x1+2*scaleFrame2), (int)(y2-y1+2*scaleFrame));
				
				int displaywidth=(int) (getObjectWidth()/this.getScale());//scale is for new version. to be deleted in the event of bugs. 
				int displayheight=(int) (getObjectHeight()/this.getScale());
				graphics.setColor(getFrameColor());
				
				
				if (getDisplayedImage()==null) ensureDisplayedImage();

				Image image = getDisplayedImage();
				if (image!=null){
					
					
						double sx1 = cords.transformX(getCenterOfRotation().getX());
						double sy1 = cords.transformY(getCenterOfRotation().getY());
					   graphics.rotate(-angle, sx1, sy1);
					   
					   graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);						
					   graphics.fill(rect2);
					   graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
						
					   
					   graphics.drawImage(image, (int) x1,  (int)y1, (int)x2, (int)y2, 0, 0, displaywidth, displayheight, null);
					  
					   graphics.rotate(angle, sx1, sy1);
						}
				
			
				  if (selected) {
					  getPanelHandleList().updateHandleLocs();
					  getPanelHandleList().draw(graphics, cords);
					  // getGrahpicUtil().drawHandlesAtPoints(graphics, cords,  RectangleEdges.getLocationsForHandles(this.getBounds()));
					
						  // handleBoxes=getGrahpicUtil().lastHandles;
						   
						  // Point frameHandPoint = getFrameHandlePoint();
						   //HandleRect framehandle = super.getGrahpicUtil().drawHandlesAtPoint(graphics, cords,frameHandPoint );
					
					  // handleBoxes.add(framehandle);
					  // drawLocationAnchorHandle(graphics,cords);
					   
				   }
				 
				 // getTheLayer().draw(graphics, cords);
			}

	

		public Image getDisplayedImage() {
			return displayedImage;
		}

		public void setDisplayedImage(Image displayedImage) {
			this.displayedImage = displayedImage;
			updateBarScale();
		}
		
		

		/**Called whenever a handle is moved. Additional actions might be performed by smart handle objects*/
		@Override
		public void handleMove(int handlenum, Point p1, Point p2) {
			if (handlenum>50) {
				if (this.getPanelHandleList()==null) return;
				SmartHandle thehandle = this.getPanelHandleList().getHandleNumber(handlenum);
				if (thehandle!=null)thehandle.handleMove(p1, p2);
				
			}
			
			if (handlenum>50) return;
			//int rightside=x+width;
			//IssueLog.log("handle num is "+handlenum);
			//double distance=0;
			if (handlenum==10) {
				double bottom=y+getObjectHeight();
				double size=p2.y-bottom;
				setFrameWidthV(size);
				notifyListenersNow();
				
				return;
			}
			
			if (handlenum==8) {
				this.setLocationType(RectangleEdges.CENTER);
				this.setLocation(p2);
				return;
				}
			
			 setLocationType(RectangleEdges.oppositeSide(handlenum));
			 double dist1=RectangleEdges.distanceOppositeSide(handlenum, getCroppedImageSize());
			double dist2= RectangleEdges.getLocation(getLocationType(), getBounds()).distance(p2);
			 //Point2D lo = getLocation(handlenum, getBounds());
		
			//double l1 = getLocation(op, getBounds()).distance( getLocation(handlenum, getBounds()));		
			if (getScale()==dist2/dist1) return;
			setScale( dist2/dist1);		
			notifyListenersNow();
			
		}

		/**
		Notifies the listeners of a size change for this object
		*/
		public void notifyListenersNow() {
			getListenerList().notifyListenersOfUserSizeChange(this);
		}
		
		public Rectangle getCroppedImageSize() {
			if (!this.isCroppintRectValid())
			return new Rectangle(0,0, this.getUnderlyingImageWidth(), this.getUnderlyingImageHeight());
		
			return new Rectangle(0,0,this.getCroppingrect().width, this.getCroppingrect().height);
		}
		
		public boolean isCroppintRectValid() {
			if (getCroppingrect()==null) return false;
			return isItAValidCrop(getCroppingrect());
		}
		
		public boolean isItAValidCrop(Rectangle2D r) {
			if (r==null) return false;
			
			return new Rectangle(0,0, this.getUnderlyingImageWidth(), this.getUnderlyingImageHeight()).contains(r);
		}
		
		public Rectangle getCroppingrect() {
			return croppingrect;
		}

		public void setCroppingrect(Rectangle croppingrect) {
			
			if (this.croppingrect!=null && this.croppingrect.equals(croppingrect)) return;
			if (!this.isItAValidCrop(croppingrect)&&croppingrect!=null) return;
			this.croppingrect = croppingrect;
			this.computeWidths();
			this.ensureDisplayedImage();
		}
		
		
		
		
		protected void computeWidths() {
			double newwidth=getScale()* getUnderlyingImageWidth();
			double newheight=getScale()* getUnderlyingImageHeight();
			
			this.setObjectWidth(newwidth);
			this.setObjectHeight(newheight);
			if (this.isCroppintRectValid()) {
				setObjectWidth(getScale()*croppingrect.getWidth());
				setObjectHeight(getScale()*croppingrect.getHeight());
			}
			updateBarScale();
			
		}
			
		/**
		public BasicCordinateConverter getCord() {
			double xc=-x/this.getScale();
			double yc=-y/this.getScale();
			if (this.isCroppintRectValid()) {
				xc-=(this.getCroppingrect().x)*this.getScale();
				yc-=(this.getCroppingrect().y)*this.getScale();
			}
			
			return new BasicCordinateConverter(xc,yc, 1);
		}*/
		
		/**transforms */
		public AffineTransform getAfflineTransformToCord() {
			AffineTransform af = new AffineTransform();
			if (this.isCroppintRectValid()) {
				af.translate(this.getCroppingrect().getX(), this.getCroppingrect().getY());
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
			
		
			
			if (this.getFrameWidthH()>0||this.getFrameWidthV()>0){
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
			pir.resize(100*this.getScale(), 100*getScale());
			if (getName()!=null) pir.setName(getName()); //Bug fix: a null name would previously case a problem with the position of the panel
	//./this.
			pir.setLeftandTop(x, y);
			} 
			
			else {
				Image i=this.getPNGExportImage() ;
				pir.prepareImageForJavaScript(i, getName(), x,y, false);
				pir.resize(100*this.getScale(), 100*getScale());
		//./this.
				pir.setLeftandTop(x, y);
			}
		//	else 
			//pir.prepareImageForJavaScript(getScaledImage(), getName(), x,y, false);
			
			
			
			pir.embed();
			
			
			return pir;
		}

		
		
		/**Returns a scaled version of the image, that may have the colors altered*/
		public Image createScaledAndProcessedImageForDisplay() {
			if (getObjectWidth()==0||getObjectHeight()==0) computeWidths();
			
			return  getProcessedImageForDisplay().getScaledInstance((int)getObjectWidth(), (int)getObjectHeight(), Image.SCALE_SMOOTH);
			} 
		
		
		public void snapLockedItems() {
			for(LocatedObject2D o: getLockedItems()) {
				snapLockedItem(o);
			}
		}
		
		@Override
		public void snapLockedItem(LocatedObject2D o) {
			if (o==null) return;
			SnappingPosition sb = o.getSnapPosition();
				if (sb==null) {
					o.setSnappingBehaviour(SnappingPosition.defaultInternal());
					sb=o.getSnapPosition();
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
			setScaleInfo(panel.getDisplayScaleInfo());
			this.updateBarScale();
			
		}
		
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

		
		public void showCroppingDialog() {
			CroppingDialog cd = new CroppingDialog();
			cd.hideRotateHandle=true;
			try{cd.showDialog(this);} catch (Throwable t) {
				IssueLog.log(t);
			}
			//IssueLog.log("The cropping rect is valid? "+this.isCroppintRectValid(), this.getCroppingrect().toString());
		}

		public PanelGraphicInsetDef createInset() {
			try {
		//	IssueLog.log("Will attempt to create inset");
			PanelGraphicInsetDef inset = new PanelGraphicInsetDef(this, ShrinkedRect(this.getBounds(), 8));
			if (this.getParentLayer()==null) IssueLog.log("parent layer not found");
		//	IssueLog.log("inset will be added to layer"+this.getParentLayer());
			this.getParentLayer().add(inset);
		
			BufferedImageGraphic imageForin = inset.getImageInset();
			getParentLayer().add(imageForin);
			imageForin.setScale( getScale());
			this.addLockedItem(imageForin);
			return inset;
			
			} catch (Throwable t) {
				IssueLog.log(t);
			}
		return null;
		}
		
		
		
		static Rectangle ShrinkedRect(Rectangle r) {
			Rectangle r2 = r.getBounds();
			r2.width/=4;
			r2.height/=4;
			r2.x+=r2.width;
			r2.y+=r2.width;
			return r2;
		}
		
		static Rectangle ShrinkedRect(Rectangle r, double factor) {
			Rectangle r2 = r.getBounds();
			r2.width/=factor;
			r2.height/=factor;
			r2.x+=r2.width*factor/4;
			r2.y+=r2.width*factor/4;
			return r2;
		}

		public Color getFrameColor() {
			return frameColor;
		}

		public void setFrameColor(Color frameColor) {
			this.frameColor = frameColor;
		}
		
		private void writeObject(java.io.ObjectOutputStream out)
			     throws IOException {

			if (this.isEmbed()) {
				saveEmbeded();
			}
			out.defaultWriteObject();
		}
		
		@Override
		public ImagePanelGraphic copy() {
			ImagePanelGraphic out = new ImagePanelGraphic();
			if (getCroppingrect()!=null) out.setCroppingrect(getCroppingrect().getBounds());
			out.setImage(this.getBufferedImage());
			out.copyAttributesFrom(this);
			out.setLocationUpperLeft(this.getLocationUpperLeft().getX(), this.getLocationUpperLeft().getY());
			return out;
		}
		
		 protected BufferedImage createFileNotFountImage(double width, double height) {
			 BufferedImage img=new BufferedImage((int)(width/getScale()), (int)(height/getScale()),  BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics();
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.setColor(Color.RED);
			g.drawString("Image File"+'\n'+" Not Found", 0, 20);
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
					IssueLog.log(e);
					return null;
				}
				 return img;
			}
			
			

		public boolean isFilederived() {
			if (this.file==null) return false;
			return filederived;
		}


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
			p2=scaleAbout(p2, p,mag,mag);
			this.setScale(this.getScale()*mag);
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
			return 72/getScale();
		}
		
		public String getInkscapePPI() {
			return  ""+(int)(90/getScale());
		}
		
		public int getScreenPPI() {
			return (int)(Toolkit.getDefaultToolkit().getScreenResolution()/getScale());
		}

		public String getDimensionString() {
			// TODO Auto-generated method stub
			return  (getBounds().getWidth()+" X "+this.getBounds().getHeight()+" units");
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
		
		@Override
		public Point2D getLocation() {
			Point2D out = RectangleEdges.getLocation(getLocationType(), getBounds());
			return new Point2D.Double(out.getX(), out.getY());
		}

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
			// TODO Auto-generated method stub
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
		
		protected ImagePanelHandleList getPanelHandleList() {
			if (panelHandleList==null) panelHandleList=new ImagePanelHandleList(this);
				return panelHandleList;
		}
		
		protected ImagePanelActionHandleList getActionHandleList() {
			if (aHandleList==null) aHandleList=new ImagePanelActionHandleList(this);
				return aHandleList;
		}

		@Override
		public SmartHandleList getSmartHandleList() {
			if (this.extraHandles!=null) {
				SmartHandleList out = new SmartHandleList();
				out.addAll(extraHandles);
				out.addAll(getPanelHandleList());
				getActionHandleList().updateLocation();
				out.addAll(this.getActionHandleList());
				return out;
			}
			return getPanelHandleList();
		}
		
		@Override
		public int handleNumber(int x, int y) {
			if (this.getSmartHandleList()!=null) {
				
				int output=this.getSmartHandleList().handleNumberForClickPoint(x, y);
				
				return output;
			}
			
			return -1;

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
			TakesLockedItems taker = this;
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
			try {
				StatusPanel.updateStatus(getMinimalSummary());
			} catch (Exception e) {
			}
		}
		
		public String[] getScaleWarning() {
			this.snapLockedItems();
			return new String[] {"Image Panel PPI is now "+this.getIllustratorPPI()};
		}

		@Override
		public AbstractUndoableEdit provideUndoForDialog() {
			return new CombinedEdit(new UndoScalingAndRotation(this), new ColorEditUndo(this));
		}

	

}

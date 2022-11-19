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
 * Date Created: Jan 29, 2022
 * Date Modified:  Feb 1, 2022
 * Version: 2022.2
 */
package lineprofile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import dataSeries.DataReplacer;
import dataSeries.XYDataSeries;
import fLexibleUIKit.MenuItemMethod;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.insetPanels.DependentSubFigure;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import iconGraphicalObjects.IconUtil;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import imageScaling.Interpolation;
import imageScaling.ScaleInformation;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;
import undo.UndoLayoutEdit;
import xyPlots.XY_Plot;

/**work in progress.
 * A special object that the user can use to generate line profiles
 * This object is the region of interest that determines which area is displayed
  */
public class ProfileLine extends RectangularGraphic implements LocationChangeListener, DependentSubFigure{


	
	/**The source panel for the inset definer*/
	private ImagePanelGraphic sourcePanel;
	
	boolean asLine=false;


	
	{this.setName("Profile Line");}
	
	transient boolean setup=false;

	private XY_Plot plotLayout;
	String xLabelT = "Distance";
	String yLabelT = "Intensity";
	
	/**set to 1 if line profile should be percentage of max value*/
	private ProfileValueType profileValueType= ProfileValueType.RAW_VALUE;
	private ArrayList<ChannelEntry> chaneEntries=new ArrayList<ChannelEntry>();

	private ProfileDistanceType profileDistanceType=ProfileDistanceType.PIXELS;
	
	public ProfileLine(ImagePanelGraphic p) {
		if(asLine)this.setClosedShape(false);
		this.setFilled(false);
		this.setFillColor(new Color(255,255,255));
		this.setFillColor(null);
		this.setDashes(new float[] {9, 6});
		this.setStrokeColor(new Color(255,255,255));
		setSourcePanel(p);
		this.setObjectHeight(8);
		
	}

	

	
	/**creates a copy*/
	public ProfileLine copy() {
		return new ProfileLine(getSourcePanel());
	}

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	/**Returns an indicator line to be used by the cropping dialog on the original image,
	 * At the moment, those rectanges merely function to indicate the use of an inset*/
	public RectangularGraphic mapRectBackToUnprocessedVersion(PreProcessInformation p) {
		try{
			
			PreProcessInformation insetProcess = generateLineProfilePreprocess(p);
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
		
		MultichannelDisplayLayer sourceDisplay = this.getSourceDisplay();
		if(sourceDisplay==null)
			return null;
		MultiChannelImage unprocessed = getOriginal();
		 MultiChannelImage cropped = unprocessed .cropAtAngle(generateLineProfilePreprocess(getSourcePreprocess()));
		
		 getSourceDisplay().getSlot().matchOrderAndLuts(cropped);//there was an issue reported on oct 20 2021 with slow performance for this. really the channel order and luts should be same
		 
		 return cropped;
	
	}




	/**
	 * @param sourceDisplay
	 * @return
	 */
	public MultiChannelImage getOriginal() {
		return getSourceDisplay().getSlot().getUnprocessedVersion(false);
	}

	/**returns the proprocess information from the source image
	 * @return
	 */
	protected PreProcessInformation getSourcePreprocess() {
		return getSourceDisplay().getSlot().getModifications();
	}
	
	



	@Override
	public void userSizeChanged(LocatedObject2D object) {
		
		
	}
	
	



	
	/**returns the source multichannel*/
	public MultichannelDisplayLayer getSourceDisplay() {
		if (this.getParentLayer() instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer d=(MultichannelDisplayLayer) getParentLayer();
			return d;
			}
		return null;
	}
	

	/**returns the image that will be used as a source for the update.
	  either the original or a version that has already been cropped might
	  be returned*/
	public MultiChannelImage getSourceImageForUpdates() {
		
			return this.generatePreProcessedVersion();
		
	}
	


	
	

	
	/**returns true if the profile line is large enough to use and inside the parent image panel*/
	public boolean isValid() {
		if (getRectangle().getWidth()<2||getRectangle().getHeight()<2) return false;
		IssueLog.log("This source panel is ");
		return this.sourcePanel.getBounds().contains(getRectangle());
	}

	/**returns the image panel that the inset in drawn onto*/
	public ImagePanelGraphic getSourcePanel() {
		return sourcePanel;
	}

	/**sets the panel that the inset is drawn onto*/
	public void setSourcePanel(ImagePanelGraphic sourcePanel) {
		this.sourcePanel = sourcePanel;
	}
	
	

	
	
	/**
	 expands the boundry of the parent layout to fit the plot layout
	TODO: make this work
	 * @return 
	 */
	private CombinedEdit expandParentLayout() {
		CombinedEdit undo=new CombinedEdit();
		FigureOrganizingLayerPane org = FigureOrganizingLayerPane.findFigureOrganizer(sourcePanel);
		if(org!=null) {
		
			DefaultLayoutGraphic montageLayoutGraphic = org.getMontageLayoutGraphic();
			UndoLayoutEdit ule=new UndoLayoutEdit(montageLayoutGraphic);
			undo.addEditToList(ule);
			montageLayoutGraphic.getEditor().expandSpacesToInclude(montageLayoutGraphic.getPanelLayout(), plotLayout.getPlotArea());
			
			montageLayoutGraphic.getEditor().trimLabelSpacesToFitContents(montageLayoutGraphic.getPanelLayout());
			ule.establishFinalLocations();
			return undo;
		}
		return null;
	}


	
	

	
	/**if edit is requested */
	public AbstractUndoableEdit2 provideDragEdit() {
		
		return null;
		
	}
	

	
static Color  folderColor2= new Color(0,140, 0);
		public static Icon createDefaultTreeIcon2(boolean open) {
			return IconUtil.createFolderIcon(open, folderColor2);
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
		
	}
	@Override
	public void moveLocation(double xmov, double ymov) {
		super.moveLocation(xmov, ymov);
		
		updatePlot();
	}
	

	

	/**Called after a handle is moved
	 * TODO: make this function
	 * @param handleNumber
	 * @param p1
	 * @param p2
	 */
	public void afterHandleMove(int handleNumber, Point2D p1, Point2D p2) {
	
		updatePlot();
		
	}
	
	




	
	
	/**checks if the setup function has run already, it not, runs that function*/
	protected void ensureSetup() {
		if (setup) return;
		onsetup();
		setup=true;
	}
	
	/**refreshers the image panels*/
	public void onsetup() {
		
		updatePlot();
		setup=true;
	}
	
	@Override 
	public void draw(Graphics2D g, CordinateConverter cords) {
		this.ensureSetup();
		super.draw(g, cords);
	}

	/**When given the preprocess modifications done on the original image, 
	 * returns what preprocess would need to be used to create a cropped version
	 * with the axis matching the axis of this shape*/
	public PreProcessInformation generateLineProfilePreprocess(PreProcessInformation p) {
		AffineTransform inv = getSourcePanel().getAfflineTransformToCord();
		Rectangle2D b = inv.createTransformedShape(this.getProfileBounds()).getBounds2D();
		if (p==null) 
			return new PreProcessInformation(b.getBounds(), this.getAngle(), createInsetScaleInformation(p));
		
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






	/**returns the bounding rectangle that will be used to determine the line averages
	 * @return
	 */
	private Shape getProfileBounds() {
		return this.getRectangle();
	}




	/**returns the scale information that will be used to scale the image that will be used
	 * to calculate the line profile
	 * will return a scale of 1
	 * @param p
	 * @return
	 */
	protected ScaleInformation createInsetScaleInformation(PreProcessInformation p) {
		double scaleFactor =1;
		Interpolation inter=Interpolation.BILINEAR;
		return new ScaleInformation(scaleFactor,  inter);
	}
	
	/**creates an xyplot*/
	@MenuItemMethod(menuActionCommand = "Create new plot", subMenuName="Profile Line", menuText = "Create new plot",  orderRank=8)
	public XY_Plot createLineProfile() {
		MultiChannelImage image = generatePreProcessedVersion();
		if(image==null)
			return null;
		ArrayList<XYDataSeries> profiles = createProfileFor(image);
		XY_Plot plot = new XY_Plot("Line profiles", profiles);
		plot.lineOnlyPlot();
		
		plot.setAxesLabels(xLabelT, yLabelT);
		this.plotLayout=plot;
		this.getParentLayer().add(plot);
		double xNew = this.sourcePanel.getBounds().getMaxX();
		double yNew = this.sourcePanel.getBounds().getMinY();
		plot.moveEntirePlot(xNew, yNew);
		updatePlot();
		return plot;
	}




	/**
	 * @param image
	 * @return
	 */
	private ArrayList<XYDataSeries> createProfileFor(MultiChannelImage image) {
		return LineProfileBuilder.createProfiles(image, getChannelChoices(),  profileValueType, profileDistanceType, getOriginal() );
	}
	
	/**updates the plot area with the line profiles*/
	@MenuItemMethod(menuActionCommand = "Update plot", menuText = "Update Plot", subMenuName="Profile Line", orderRank=15)
	public void updatePlot() {
		if(plotLayout==null)
			return;
		MultiChannelImage image = generatePreProcessedVersion();
		if(image==null)
			return;
		ArrayList<XYDataSeries> profiles = createProfileFor(image);
		if(plotLayout!=null) {
			new DataReplacer<XYDataSeries>().replaceAllData(plotLayout, profiles);
			plotLayout.updateAxisRange();
			plotLayout.moveAxisLabelsOutOfWay();
			
			
			}
	}
	
	/**removes the line profile*/
	@MenuItemMethod( menuText = "Remove profile line and plot", subMenuName="Profile Line",  orderRank=12)
	public AbstractUndoableEdit removeLineAndPlot() {
		CombinedEdit c=new CombinedEdit();
		GraphicLayer parentLayer = getParentLayer();
		c.addEditToList(Edit.removeItem(parentLayer, this));
		c.addEditToList(Edit.removeItem(parentLayer, this.plotLayout));
		return c;
	}

	/**changes the value type*/
	@MenuItemMethod(menuText = "", subMenuName="Profile Line<Change Profile Type", iconMethod="getProfileValueType", orderRank=9)
	public AbstractUndoableEdit setProfileValueType(ProfileValueType v) {
		this.profileValueType=v;
		yLabelT=v.getAxisLabel();
		if(this.plotLayout!=null)plotLayout.setAxesLabels(xLabelT, yLabelT);
		this.updatePlot();
		return null;
	}
	
	/**changes the value type*/
	@MenuItemMethod(menuText = "", subMenuName="Profile Line<Distance As", iconMethod="getProfileDistanceType", orderRank=9)
	public AbstractUndoableEdit setProfileDistanceType(ProfileDistanceType v) {
		this.profileDistanceType=v;
		
		if(v==ProfileDistanceType.UNITS)
			xLabelT="Distance ("+this.getSourcePanel().getScaleInfo().getUnits()+")";
		if(v==ProfileDistanceType.PERCENT)
			xLabelT="Percent";
		if(v==ProfileDistanceType.PIXELS)
			xLabelT="Distance";
		if(this.plotLayout!=null)
			plotLayout.setAxesLabels(xLabelT, yLabelT);
		this.updatePlot();
		return null;
	}
	
	@MenuItemMethod(menuText = "Plot channels", subMenuName="Profile Line",  orderRank=10)
	public  ProfileLineChannelMenu getChannelChoiceMenu() {
		return new  ProfileLineChannelMenu(this);
	}
	
	public ProfileValueType getProfileValueType() {
		return this.profileValueType;
	}

	/**Sets which channels are to be used
	 * @param channelEntries
	 */
	public void setChannelChoices(ArrayList<ChannelEntry> channelEntries) {
		this.chaneEntries=channelEntries;
		
		
	}
	
	
	public String getShapeName() {return "Line Profile Shape";}

	/**implements a formula to create a rectangle that is missing one side*/
	@Override
	public Shape getShape() {
		if(!asLine)
			return super.getShape();
		Path2D.Double path=new Path2D.Double();
		Rectangle2D r = this.getRectangle();
		

		Point2D startPoint = RectangleEdges.getLocation(LEFT, r);
		path.moveTo( startPoint .getX(),startPoint .getY());
		

		Point2D p3 = RectangleEdges.getLocation(RIGHT, r);
		path.lineTo(p3.getX(), p3.getY());
		
		this.setClosedShape(false);
		
		return path;
		
	}


	
		
	/**returns a pathGraphic that looks just like this shape
	 * @see PathGraphic*/
	public PathGraphic createPathCopy() { 
		PathGraphic out = super.createPathCopy();
		out.setClosedShape(false);
		return out;
	}
	
	
	/**Creates the shape in adobe illustrator*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi, false);
	}
	
	public boolean isDrawClosePoint() {
		if(!asLine)
			return true;
		return false;
	}




	/**Sets the rectangle position based on two points
	 * such that the line forms a link between the two points
	 * @param p1
	 * @param p2
	 */
	public void setPoints(Point2D p1, Point2D p2) {
		double w = p1.distance(p2);
		Point2D center = super.midPoint(p1, p2);
		double a = ArrowGraphic.getAngleBetweenPoints(p1, p2);
		
		Double rect = new Rectangle2D.Double(0,0, w,this.getRectangle().getHeight());
		RectangleEdges.setLocation(rect, RectangleEdges.CENTER, center.getX(), center.getY());
		this.setAngle(-a);
		this.setRectangle(rect);
	}
	
	/**returns the outline*/
	@Override
	public Shape getOutline() {
		return new RectangularGraphic(this).getRotationTransformShape();
	}



	/**updates the plot*/
	@Override
	public void updateChannel(String name) {
		this.updatePlot();
		
	}


	@Override
	public boolean producesObject(Object image) {
		return false;
	}




	public ArrayList<Integer> getChannelChoices() {
		ArrayList<Integer> channelChoices = new ArrayList<Integer>(); 
		for(ChannelEntry entry:chaneEntries) {
			channelChoices.add(entry.getOriginalChannelIndex());
		}
		if(channelChoices.size()==0)
			channelChoices.add(1);
		return channelChoices;
	}




	/**
	 * @param entry
	 */
	public void addChannelToPlot(ChannelEntry entry) {
		for(ChannelEntry c: this.chaneEntries) {
			if(c.getOriginalChannelIndex()==entry.getOriginalChannelIndex()) {
				
				return;
				
			}
		}
		chaneEntries.add(entry);
	}




	/**
	 * @param originalChannelIndex
	 */
	public void removeChannelFromPlot(int originalChannelIndex) {
		for(ChannelEntry c: this.chaneEntries) {
			if(c.getOriginalChannelIndex()==originalChannelIndex) {
				chaneEntries.remove(c);
				return;
				
			}
		}
		
	}




	public ProfileDistanceType getProfileDistanceType() {
		return profileDistanceType;
	}

}

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
 * Date Modified: Dec 11, 2021
 * Version: 2021.2
 */
package objectDialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import applicationAdapters.PixelWrapper;
import channelMerging.CSFLocation;
import channelMerging.MultiChannelImage;
import channelMerging.MultiChannelSlot;
import channelMerging.PreProcessInformation;
import figureEditDialogs.ChannelSliceAndFrameSelectionDialog;
import figureOrganizer.FigureType;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import imageScaling.ScaleInformation;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import standardDialog.booleans.BooleanInputEvent;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.graphics.GraphicComponent;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.ButtonPanel;
import standardDialog.strings.CombindedInputPanel;
import standardDialog.strings.InfoDisplayPanel;

/**a dialog for setting the crop area for a multi dimensional image.
 * Can also be used to set a crop area for one or more image panels.*/
public class CroppingDialog extends GraphicItemOptionsDialog implements MouseListener, MouseMotionListener, ActionListener, MouseWheelListener{

	

	/**String key for the field that presents the user withan option to permit crop areas that extend beyond the image.
	 */
	private static final String ALLOW_OUT_KEY = "allow out";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**The component that will display a preview image and a crop area*/
	public GraphicComponent panel=new GraphicComponent();
	public JScrollPane scrollPane=new JScrollPane(panel); {
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}
	
	/**is set to true if the user presses the eliminate crop rectangle button*/
	public boolean wasEliminated=false;
	String instructions="Set Crop Area";
	
	/**A button that appears near the bottom of the dialog*/
	JButton eliminateButton=new JButton("Eliminate Cropping Rectangle"); {eliminateButton.addActionListener(new EliminageCropAreaListener());}
	
	/**A buttons that appears near the bottom of the dialog under certain circumstances.
	 * if the context is a sequence of crop dialogs, this will appear*/
	JButton yesAll=new JButton("OK for all"); {yesAll.addActionListener(new AllOKListener());}
	
	private ArrayList<ImagePanelGraphic> imagepanels=new ArrayList<ImagePanelGraphic>();
	
	/**Determines whether a rotation angle option is included*/
	boolean includeAngle=false;
	boolean includeScaleButton=false;
	
	/**the innitial crop angle of the dialog*/
	private double cropAngle=0;
	
	/**The rectangular graphic that a user modifies to set the crop area*/
	RectangularGraphic cropAreaRectangle;
	
	
	/**stores the handle that the user is clicking and dragging*/
	int handle=-1;
	Point2D press=new Point();
	
	
	
	public static final int CROP_FOR_IMAGE_PANEL=0, CROP_FOR_SLOT=1;
	private int dialogType= CROP_FOR_IMAGE_PANEL;

	/**the factor that determines what size the image within this dialog is shown at*/
	double displayMagnification=1;
	/**The image that is being cropped if 
	 *  @field dialogType is set to @field CROP_FOR_IMAGE_PANEL, that images crop area will be set*/
	ImagePanelGraphic image;
	
	private ArrayList<ZoomableGraphic> extraItems=new ArrayList<ZoomableGraphic>();
	public boolean hideRotateHandle;
	private MultiChannelImage multiChannelSource;
	private CSFLocation display=new CSFLocation();
	
	/**This object displays the full size uncropped image for the user to see*/
	private ImagePanelGraphic dialogDisplayImage;
	
	private CropDialogContext dialogContext;
	
	/**the stroke color for the rectangle*/
	private Color rectangleStrokeColor=new Color(200, 200, 250);
	
	private PreProcessInformation startingCrop;

	private Rectangle reccomendedRectangle;

	/**The scale factor that is a product of using the crop dialog scaler*/
	public double scaleFactorForCropArea=1;
	/**set to true if this dialog changes the scale as well*/
	public boolean changeScale=false;

	/**set to true if the user is not precented from setting an out of bounds crop area*/
	private boolean outofBoundsCrop=false;
	
	{this.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets=new Insets(10,10,10,10);
		gc.gridwidth=4;
		this.add(scrollPane, gc);
		this.moveGrid(0, 1);
		}
	
	
	public CroppingDialog() {
		
	}
	
	public void setArray(ArrayList<?> array2) {
		setImagepanels(new ArrayList<ImagePanelGraphic>());
		addGraphicsToArray(getImagepanels(), array2);
	}
	
	
	public void addGraphicsToArray(ArrayList<ImagePanelGraphic> array, ArrayList<?> zs) {
		for(Object z:zs) {
			
			if (z instanceof ImagePanelGraphic) {array.add(((ImagePanelGraphic) z));}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	/**Creats a crop dialog
	 * @param s the slot that contains the original uncropped image
	 * @param multichanalWrapper the image*/
	public CroppingDialog(MultiChannelSlot s, MultiChannelImage multichanalWrapper, PreProcessInformation preprocessRecord) {
		includeInsets(s);
		
		
		setImageToCrop(multichanalWrapper, display.channel, display.frame, display.slice);
		
		if(preprocessRecord!=null) 
			{
			this.cropAngle=preprocessRecord.getAngle();
			
			this.startingCrop=preprocessRecord;
			
			//image.setCroppingRect(preprocessRecord.getRectangle(), this.cropAngle);//TODO:delete this
			
			
			}
		this.setModal(true);
		this.setWindowCentered(true);
	}

	/**adds indicators regarding the position of insets to the crop dialog*/
	public void includeInsets(MultiChannelSlot s) {
		for(PanelGraphicInsetDefiner i: s.getDisplayLayer().getInsets()) {
			if(i==null) continue;
			RectangularGraphic r3 = i.mapRectBackToUnprocessedVersion(s.getModifications());
			this.addExtraItem(r3);
		}
	}
	
	private void addExtraItem(RectangularGraphic r3) {
		extraItems.add(r3);
		
	}

	/**shows the crip dialog*/
	public void showDialog() {
		this.showDialog(image);
	}
	
	/**creates a cropping dialog
	 * @param recAngle the rotation angle
	 * @param r the starting rectangle bounds*/
	public CroppingDialog(MultiChannelSlot s, MultiChannelImage multichanalWrapper, Rectangle r, double recAngle) {
		includeInsets(s);
		setImageToCrop(multichanalWrapper, display.channel, display.frame, display.slice);
		
		Rectangle rDefault = getRectForEntireImage();
		this.cropAngle=recAngle;
		
		if(r!=null) 
			{
				/**series of lines deal with specific ircumstances of an roi that is too large*/
				RectangularGraphic rotatedRactangle = new RectangularGraphic(r);
				rotatedRactangle.setAngle(recAngle);
				Rectangle rotatedBounds = rotatedRactangle.getRotationTransformShape().getBounds();
				if (!rDefault.contains(rotatedBounds)) {
					if (r.width>rDefault.width) {
						IssueLog.log("crop area width is larger than the bounds of the image "+rotatedBounds);
						r.width=rDefault.width;
					}
					if (r.height>rDefault.height) {
						IssueLog.log("crop area height is larger than the bounds of the image "+rotatedBounds);
						r.height=rDefault.height;
					}
				}
				
				image.setCroppingRect(r);//sets the crop rect
			
			}
		this.setModal(true);
		this.setWindowCentered(true);
		this.reccomendedRectangle=r;
		
	}


	/**sets the target multichannel image*/
	private void setImageToCrop(MultiChannelImage multichanalWrapper,int chan, int frame, int slice) {
		multiChannelSource= multichanalWrapper;
		this.setTitle("Crop: "+multichanalWrapper.getTitle());
		this.display.frame=frame;
		this.display.slice=slice;
		
		BufferedImage image3 = createDisplayImage(chan, frame, slice);
		this.image=new ImagePanelGraphic(image3);
		includeAngle=true;
		this.includeScaleButton=true;
		
		this.dialogType=CROP_FOR_SLOT;//makes sure the crop area that is used will be the one provided by the multichannel and not the image graphis
	
	}
	
	void updateDisplayImage() {
		if(dialogDisplayImage==null) return;
		dialogDisplayImage.setImage(createDisplayImage(this.display.channel, this.display.frame, this.display.slice));
		dialogDisplayImage.updateDisplay();
		panel.repaint();
	}

	
	/**creates an image of the entire source image to display as the background*/
	public BufferedImage createDisplayImage(int chan, int frame, int slice) {
		PanelListElement pList;
		PanelList panelList = new PanelList();
		if(chan==0 &&multiChannelSource.nChannels()>1)
			pList = panelList.createMergePanelEntry(multiChannelSource ,frame, slice);
		else if (chan==0&&multiChannelSource.nChannels()==1)
			pList =panelList.createChannelPanelEntry(multiChannelSource, 1, display.frame, display.slice);
		else 
			pList =panelList.createChannelPanelEntry(multiChannelSource, display.channel, display.frame, display.slice);
		
		PixelWrapper image2 =pList.getImageWrapped();// multiChannelSource.getChannelMerger().generateMergedRGB(pList, 0);
		
		BufferedImage image3 = (BufferedImage) image2.image();
		return image3;
	}
	
	
	/**the simplest form of crop dialog*/
	public CroppingDialog(ImagePanelGraphic image) {
		this.image=image;
		showDialog(image);
	}
	
	
	/**returns the magnification that will be used when displaying the given image in the crop dialog.
	 * This is selected to create a large dialog window that still fits on the screen*/
	public double getIdealDisplayScaleForImage(ImagePanelGraphic imagePanelGraphic) {
		
		int w = imagePanelGraphic.getUnderlyingImageWidth();
		int h=imagePanelGraphic.getUnderlyingImageHeight();
		if (h>w)
			w=h;
		if (w<200) {return 200/w;}
		if (w>5000){return 0.08;}
		if (w>4000){return 0.1;}
		if (w>3000){return 0.15;}
		
		if (w>2000){return 0.2;}
		
		if (w>1000){return 0.4;}
	
		
		return 1;
	}
	
	/**shows the dialog for the particular image*/
	public Rectangle showDialog(ImagePanelGraphic imagePanelGraphic) {
		Rectangle r=imagePanelGraphic.getCroppingRect();
		if (this.startingCrop!=null)
			r=this.startingCrop.getRectangle();//if the crop area comes from a set of instructions and not the image
		if(this.reccomendedRectangle!=null) {
			r=reccomendedRectangle;
		}
		setDialogImagePanel(imagePanelGraphic);
		
		double d = this.getIdealDisplayScaleForImage(imagePanelGraphic);
		setDisplayScale(d);
		
		
		if (r==null) {r=getRectForEntireImage(imagePanelGraphic);}
		
		setupCropAreaRectangle(r);
		
		for(ZoomableGraphic eItem:this.extraItems) {
			panel.getGraphicLayers().add(eItem);
		}
		
		{
			panel.addMouseListener(this); 
			panel.addMouseMotionListener(this);
			panel.addMouseWheelListener(this);
		}
		
		this.addButton(eliminateButton);
		if(dialogContext!=null &&dialogContext.nInseries>1) {
			this.addButton(yesAll);
		}
		
		this.add("ins", new InfoDisplayPanel("", instructions));
		this.add("x", new NumberInputPanel("x", cropAreaRectangle.getBounds().getX()));
		this.moveGrid(2, -1);
		this.add("y", new NumberInputPanel("y", cropAreaRectangle.getBounds().getY()));
		this.moveGrid(-2, 0);
		this.add("width", new NumberInputPanel("width", cropAreaRectangle.getBounds().getWidth()));
		this.moveGrid(2, -1);
		this.add("height", new NumberInputPanel("height", cropAreaRectangle.getBounds().getHeight()));
		if(includeAngle) {
				this.add("angle", new AngleInputPanel("angle", cropAreaRectangle.getAngle(), true));
		}
		if(this.includeScaleButton)
			this.add("scale", new ButtonPanel("  ", "Scale Crop Area", new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					getCropAreaScaler().showAsPopup(e);
					
				}}));
		
		if(showsFrameSlider()) {
			ChannelSliceAndFrameSelectionDialog.addFrameSelectionToDialog(this, multiChannelSource, display.frame);
		}
		if(showsSliceSlider()) {
			ChannelSliceAndFrameSelectionDialog.addSliceSelectionToDialog(this, multiChannelSource, display.slice);
		}
		if(showsChannelBox()) {
			ChannelSliceAndFrameSelectionDialog.addChannelSelectionToDialog(this, multiChannelSource, display.channel);
		}
	
		this.moveGrid(-2, 0);
		
		
		
		ButtonPanel in = new ButtonPanel("Zoom", "+", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				changeZoomLevel(2);
				
			}});
		
		ButtonPanel out = new ButtonPanel("Zoom", "-", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				changeZoomLevel(-2);
				
			}});
		CombindedInputPanel zoomLevelPanel = new CombindedInputPanel("Zoom", out, in);
		super.add("ZoomI", zoomLevelPanel);
		
		
		this.add(ALLOW_OUT_KEY, new BooleanInputPanel("Permit out of bounds crop", outofBoundsCrop));
		
		this.pack();
		boolean allOK = false;
		if (dialogContext!=null) allOK =dialogContext.okToAll;
		
		if(this.dialogContext!=null&&allOK) {
			this.setModal(false);
		}
		
		super.showDialog();
		
		if(this.dialogContext!=null) {
			dialogContext.current+=1;
		}
		
		/**if the user chose to ok an entire sequence of crop dialogs including this one*/
		if(this.dialogContext!=null&&allOK) {
			super.wasOKed=true;
			super.onOK();
			super.setVisible(false);
		}
		
		if(cropAreaRectangle==null)return null;
		return cropAreaRectangle.getBounds();
	}

	/**Creates the image panel that will be shown within the crop dialog window
	 * @param imagePanelGraphic
	 */
	protected void setDialogImagePanel(ImagePanelGraphic imagePanelGraphic) {
		this.image=imagePanelGraphic;
		ImagePanelGraphic b = new ImagePanelGraphic(imagePanelGraphic.getBufferedImage());
		panel.getGraphicLayers().add(b);
		b.setLocationUpperLeft(0, 0);
		dialogDisplayImage=b;
	}

	/**sets the scale at which the image and crop area are shown
	 * @param d
	 */
	protected void setDisplayScale(double d) {
		if(d<=0)
			d=0.1;
		displayMagnification=d;
		panel.setMagnification(displayMagnification);
		double width2 = image.getUnderlyingImageWidth()*displayMagnification;
		double height2 = image.getUnderlyingImageHeight()*displayMagnification;
		panel.setPrefferedSize(width2, height2);
		panel.setSize((int)width2, (int)height2);
	}

	/**Creates a rectangular graphic to display the crop area
	 * @param r
	 */
	public void setupCropAreaRectangle(Rectangle r) {
		cropAreaRectangle=new RectangularGraphic(r);
		cropAreaRectangle.hideStrokeHandle=true;
		cropAreaRectangle.handleSize=4;
		if(this.hideRotateHandle) {
			cropAreaRectangle.hideCenterAndRotationHandle=true;
		}
		cropAreaRectangle.setAngle(cropAngle);
		cropAreaRectangle.setStrokeColor(rectangleStrokeColor);
		
		cropAreaRectangle.select();
		
		
		panel.getGraphicLayers().add(cropAreaRectangle);
	}


	public boolean showsChannelBox() {
		return this.multiChannelSource!=null&&this.multiChannelSource.nChannels()>1;
		}
	public boolean showsSliceSlider() {
		return this.multiChannelSource!=null&&this.multiChannelSource.nSlices()>1;
	}


	public boolean showsFrameSlider() {
		return this.multiChannelSource!=null&&this.multiChannelSource.nFrames()>1;
	}

	/**returns a rectangle large engough to contain the entire image being cropped*/
	public Rectangle getRectForEntireImage() {
		return getRectForEntireImage(image);
	}
	public Rectangle getRectForEntireImage(ImagePanelGraphic imagePanelGraphic) {
		return new Rectangle(0,0, imagePanelGraphic.getUnderlyingImageWidth(), imagePanelGraphic.getUnderlyingImageHeight());
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		Point2D drag=panel.getCord().unTransformClickPoint(arg0);
		RectangularGraphic rect2 = cropAreaRectangle.copy();
		
		
		{cropAreaRectangle.handleMove(handle, new Point((int)press.getX(),(int) press.getY()), new Point((int)drag.getX(),(int) drag.getY()));
		if (handle==8||handle==-1) {
			cropAreaRectangle.setLocationType(RectangleEdges.CENTER);
			cropAreaRectangle.setLocation((int)drag.getX(), (int)drag.getY());
			}
		
		
		/**
		if(cropAreaRectangle.getBounds().getX()<0) {
			cropAreaRectangle.setLocationUpperLeft(0, cropAreaRectangle.getLocationUpperLeft().getY());
			
		}
		
		if(cropAreaRectangle.getBounds().getY()<0) {
			cropAreaRectangle.setLocationUpperLeft(cropAreaRectangle.getLocationUpperLeft().getX(), 0);
		
		} 
		*/
		boolean isNewRectValid = isCroppingRectangleValid();
		
		/**if the new rectangle location is outside the area, reverts the Rectangular Graphic*/
		if (!isNewRectValid) {
			setRectangleTo(rect2);
		}
		
		
		
		
		
		
		
		
		super.notifyAllListeners(null, null);
		}
		
		
		
		if (controlsCropAreaForImagePanel()) 
			setImageCropping();
		
		setFieldsToRect();
		
		this.onOK();
		panel.repaint();
	}

	/**returns true if this dialog controls the crop area for an image panel
	 * @return
	 */
	public boolean controlsCropAreaForImagePanel() {
		return this.dialogType==CROP_FOR_IMAGE_PANEL;
	}


	public void setRectangleTo(RectangularGraphic rect2) {
		cropAreaRectangle.setRectangle(rect2.getRectangle());
		cropAreaRectangle.setAngle(rect2.getAngle());
		cropAreaRectangle.setLocationType(rect2.getLocationType());
	}


	/**Returns true if the current crop rectangle is valid. If the rectangle is partly outside the image, this will return false*/
	public boolean isCroppingRectangleValid() {
		if(outofBoundsCrop)
			return true;
		RectangularGraphic testedRect = cropAreaRectangle;
		return isCropRectangleValid(testedRect);
	}

	/**method to check if a particular crop area fits inside the image
	 * @param testedRect
	 * @return
	 */
	public boolean isCropRectangleValid(RectangularGraphic testedRect) {
		Rectangle rmax = getRectForEntireImage();
		if (testedRect==null) return true;
		
		boolean isNewRectValid = rmax.contains(testedRect.getOutline().getBounds());
		
		return isNewRectValid;
	}
	
	/**Changes the stored rectangle to match the dialog fields*/
	public void setRectToDialog() {
		
		RectangularGraphic rect2 = cropAreaRectangle.copy();
		Rectangle r = new Rectangle(this.getNumberInt("x"), this.getNumberInt("y"),this.getNumberInt("width"), this.getNumberInt("height"));
		
		cropAreaRectangle.setRectangle(r);
		if(includeAngle) {
			double angle = this.getNumber("angle");
			cropAreaRectangle.setAngle(angle);
				}
		if (!this.isCroppingRectangleValid()) {
			this.setRectangleTo(rect2);
		}
	}
	
	/**Changes the values in the dialog field sto match the rectangle*/
	public void setFieldsToRect() {
		this.setNumber("x", cropAreaRectangle.getBounds().getX());
		this.setNumber("y", cropAreaRectangle.getBounds().getY());
		this.setNumber("width", cropAreaRectangle.getBounds().getWidth());
		this.setNumber("height", cropAreaRectangle.getBounds().getHeight());
		if(includeAngle) {
			this.setNumber("angle", cropAreaRectangle.getAngle());
		}
	}
	
	@Override
	public void numberChanged(NumberInputEvent ne) {
		setRectToDialog();
		panel.repaint();
		setImageCropping();
		
		super.numberChanged(ne);
		if(ne.getKey()==null) {return;}
		if(ne.getKey().equals("frame")) { display.frame=(int) ne.getNumber();updateDisplayImage();}
		if(ne.getKey().equals("slice")) {display.slice=(int) ne.getNumber();updateDisplayImage();}
		if(ne.getKey().equals("chan")) {display.channel=(int) ne.getNumber();updateDisplayImage();}
		
	}
	
	public void valueChanged(ChoiceInputEvent ne) {
		super.valueChanged(ne);
		
		if(ne.getKey()==null) {return;}
		if(ne.getKey().equals("chan")) {display.channel=(int) ne.getChoiceIndex();updateDisplayImage();}
	}

	@Override 
	public void booleanInput(BooleanInputEvent be) {
		this.outofBoundsCrop=this.getBoolean(ALLOW_OUT_KEY);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		press = panel.getCord().unTransformClickPoint(arg0);
		handle=cropAreaRectangle.handleNumber(arg0.getX(), arg0.getY());
		

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setVisible(boolean b) {
		
	//if (b==false)	setImageCropping();TODO: determines if this part of the code is obsolete and delete if it is
		super.setVisible(b);
	}
	
	/**sets the crop area for the image panel*/
	public void setImageCropping() {
		try{
		image.setCroppingRect(cropAreaRectangle.getBounds());

		for(ImagePanelGraphic image: getImagepanels()) {
			image.setCroppingRect(cropAreaRectangle.getBounds());
		}
		
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	/**removes the crop area from the image panels*/
	public void removeCroppingRectFromImagePanels() {
		image.setCroppingRect(null);
		for(ImagePanelGraphic image: getImagepanels()) {
			image.setCroppingRect(null);
		}
	}
	
	/**returns a list of image panels whose crop area may be set by this dialog*/
	public ArrayList<ImagePanelGraphic> getImagepanels() {
		return imagepanels;
	}


	/**sets a list of image panels whose crop area may be set by this dialog*/
	public void setImagepanels(ArrayList<ImagePanelGraphic> imagepanels) {
		this.imagepanels = imagepanels;
	}
	
	/**returns the crop rect*/
	public RectangularGraphic getRectangle() {
		return cropAreaRectangle;
	}
	

	/**A listener object that responds to the 'eliminate crop rectangle' button*/
	public class EliminageCropAreaListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (controlsCropAreaForImagePanel())
				removeCroppingRectFromImagePanels();
			cropAreaRectangle=null;
			wasEliminated=true;
			setVisible(false);
			if (CroppingDialog.getSetContainer()!=null) CroppingDialog.getSetContainer().updateDisplay();	
		}
		
	}
	
	/**A listener object that responds to the 'ok to all' button */
	public class AllOKListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			dialogContext.okToAll=true;
			wasOKed=true;
			setVisible(false);
			if (CroppingDialog.getSetContainer()!=null) CroppingDialog.getSetContainer().updateDisplay();	
		}
		
	}

	
	
	
	/**shows a recrop dialog
	 * @param context will determine what to do if this is the second in a series*/
	public static void showCropDialogOfSize(MultiChannelSlot slot, Dimension recommmendation,  CropDialogContext context) {
		if (recommmendation==null)
			{
			showCropDialog(slot, null, 0, context);
			return;
			}
		
		if (slot.getModifications()!=null &&slot.getModifications().getRectangle()!=null) {
			Rectangle rold = slot.getModifications().getRectangle();
			if(rold!=null)
				showCropDialog(slot, new Rectangle(rold.x, rold.y, recommmendation.width, recommmendation.height), slot.getModifications().getAngle(), context);
			else showCropDialog(slot, null, 0, context);
		}
		else if (recommmendation!=null)
			showCropDialog(slot, new Rectangle(0,0, recommmendation.width, recommmendation.height),0, context);
		
	
	}
	
	/**Shows a crop dialog based on a suggested rectangle*/
	public static CroppingDialog showCropDialog(MultiChannelSlot slot, Rectangle recommmendation, double recAngle) {
		return showCropDialog(slot, recommmendation, recAngle, null);
	}
	
	/**Shows a crop dialog based on a suggested rectangle*/
	public static CroppingDialog showCropDialog(MultiChannelSlot slot, Rectangle recommmendation, double recAngle, CropDialogContext context) {
		
		
		
		CroppingDialog crop;
		if(recommmendation==null)
			crop= new CroppingDialog(slot, slot.getUnprocessedVersion(true), slot.getModifications());
		else {
			crop = new CroppingDialog(slot, slot.getUnprocessedVersion(true), recommmendation, recAngle);
		}
		crop.setContext(context);
		
		if(slot.getDisplaySlice()!=null) 
			crop.setDisplaySlice(slot.getDisplaySlice());
		
		crop.showDialog();
		
		if(!crop.wasOKed()&&!crop.wasEliminated) return crop;
		
		if(!crop.isCroppingRectangleValid()) return crop;
		
		RectangularGraphic r = crop.getRectangle();
		ScaleInformation scaleInformation=new ScaleInformation();
		
		if (slot.getModifications()!=null) { //the former scale information
			scaleInformation=slot.getModifications().getScaleInformation();
		}
		
	
		
		/**If the user chose to edit the scale in the crop dialog, this changes the scale information*/
		if(crop.changeScale&&crop.scaleFactorForCropArea!=1) {
			
			scaleInformation=scaleInformation.multiplyBy(1/crop.scaleFactorForCropArea);
			
				/**stores the scale information in the context so can be used for the next crop dialog*/
					if(context!=null){
						context.userScaledCropArea=true;
						context.userRescale=crop.scaleFactorForCropArea;
						context.scaleInformation=scaleInformation;
						}
					
		} else if (context!=null&&context.scaleInformation!=null) {
			scaleInformation=context.scaleInformation;
		}
		
		PreProcessInformation process;
		if (!crop.wasEliminated)
			process = new PreProcessInformation(r.getRectangle().getBounds(), r.getAngle(), scaleInformation);
		else {
			process = new PreProcessInformation(null, 0, scaleInformation);
		}
		
		try {
			
			slot.setDisplaySlice(crop.display);
			slot.applyCropAndScale(process);
			
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		return crop;
		
	}

	
	/**sets the context for the crop dialog
	 * @param context
	 */
	private void setContext(CropDialogContext context) {
		this.dialogContext=context;
		if(context!=null ) {
			this.rectangleStrokeColor=context.getFigureType().getForeGroundDrawColor();
			
			if(this.cropAreaRectangle!=null)
				this.cropAreaRectangle.setStrokeColor(rectangleStrokeColor);
		}
	}

	/**Changes the channel, slice and frame that is shown*/
	public void setDisplaySlice(CSFLocation l) {
		display=l;
		this.updateDisplayImage();
	}
	
	
	/**An object that contains information about the circumstances that resulted in opening of a crop dialog*/
	public static class CropDialogContext {
		
		

		



		/**indicates the number of crop dialogs that will be shown in a sequence*/
		int nInseries=1;
		
		/**indicates the index of the current crop dialog*/
		public int current=1;
		
		/**set to true if the user clicks the 'OK for all option' in one of the dialogs*/
		boolean okToAll=false;
		
		/**set to true if the user has scaled a crop area*/
		public boolean userScaledCropArea;
		/**what scale factor the user selected for rescaling*/
		public double userRescale=1;
		
		/**Set to a nonnull value if a particular scale should be forced on each image in the sequence*/
		public ScaleInformation scaleInformation;
		
		
		/**What sort of figure is this*/
		FigureType type=FigureType.FLUORESCENT_CELLS;
		
		public CropDialogContext(int nImages, FigureType type) {
			this.nInseries=nImages;
			this.type=type;
			
		}

		/**
		returns the figure type
		 */
		public FigureType getFigureType() {
			return type;
		}
		
	}
	
	/**returns a crop area scaler object*/
	public CropAreaScaler getCropAreaScaler() {
		return new CropAreaScaler(this, null);
	}

	/**Changes the magnification of the image*/
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int turns=e.getWheelRotation();
		changeZoomLevel(turns);
		
	}

	/**
	 * @param turns
	 */
	public void changeZoomLevel(int turns) {
		this.setDisplayScale(displayMagnification+turns*0.02);
	
		this.repaint();
	}
	





	
	
}
	
	


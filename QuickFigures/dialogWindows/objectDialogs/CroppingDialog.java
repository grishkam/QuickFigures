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
 * Date Modified: April 18, 2021
 * Version: 2021.2
 */
package objectDialogs;

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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;

import applicationAdapters.PixelWrapper;
import channelMerging.CSFLocation;
import channelMerging.MultiChannelSlot;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import figureEditDialogs.ChannelSliceAndFrameSelectionDialog;
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
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.graphics.GraphicComponent;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.InfoDisplayPanel;

/**a dialog for setting the crop area for a multi dimensional image*/
public class CroppingDialog extends GraphicItemOptionsDialog implements MouseListener, MouseMotionListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GraphicComponent panel=new GraphicComponent();
	public boolean wasEliminated=false;
	String instructions="Set Crop Area";
	
	JButton eliminateButton=new JButton("Eliminate Cropping Rectangle"); {eliminateButton.addActionListener(new cropLis());}
	JButton yesAll=new JButton("OK for all"); {yesAll.addActionListener(new allOKLis());}
	
	private ArrayList<ImagePanelGraphic> imagepanels=new ArrayList<ImagePanelGraphic>();
	boolean includeAngle=false;
	

	
	RectangularGraphic rect;
	int handle=-1;
	Point2D press=new Point();
	double mag=1;
	ImagePanelGraphic image;
	private double cropAngle=0;
	private ArrayList<ZoomableGraphic> extraItems=new ArrayList<ZoomableGraphic>();
	public boolean hideRotateHandle;
	private MultiChannelImage multiChannelSource;
	private CSFLocation display=new CSFLocation();
	private ImagePanelGraphic dialogDisplayImage;
	private CropDialogContext dialogContext;
	
	{this.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets=new Insets(10,10,10,10);
		gc.gridwidth=4;
		this.add(panel, gc);
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
	
	public CroppingDialog(MultiChannelSlot s, MultiChannelImage multichanalWrapper, PreProcessInformation preprocessRecord) {
		includeInsets(s);
		
		
		setImageToCrop(multichanalWrapper, display.channel, display.frame, display.slice);
		
		if(preprocessRecord!=null) 
			{
			image.setCroppingRect(preprocessRecord.getRectangle());
			this.cropAngle=preprocessRecord.getAngle();
			
			}
		this.setModal(true);
		this.setWindowCentered(true);
	}


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
	
	public CroppingDialog(MultiChannelSlot s, MultiChannelImage multichanalWrapper, Rectangle r, double recAngle) {
		includeInsets(s);
		setImageToCrop(multichanalWrapper, display.channel, display.frame, display.slice);
		
		Rectangle rDefault = getRectForEntireImage();
		
		
		if(r!=null) 
			{
			
			if (r.width>rDefault.width) {
				r.width=rDefault.width;
			}
			if (r.height>rDefault.height) {
				r.height=rDefault.height;
			}
			this.cropAngle=recAngle;
			image.setCroppingRect(r);//sets the crop rect
			
			}
		this.setModal(true);
		this.setWindowCentered(true);
	}


	private void setImageToCrop(MultiChannelImage multichanalWrapper,int chan, int frame, int slice) {
		multiChannelSource= multichanalWrapper;
		this.setTitle("Crop: "+multichanalWrapper.getTitle());
		this.display.frame=frame;
		this.display.slice=slice;
		BufferedImage image3 = createDisplayImage(chan, frame, slice);
		this.image=new ImagePanelGraphic(image3);
		includeAngle=true;
		
	
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
	
	
	
	public CroppingDialog(ImagePanelGraphic image) {
		this.image=image;
		showDialog(image);
	}
	
	public double getDisplayScale(ImagePanelGraphic imagePanelGraphic) {
		
		int w = imagePanelGraphic.getUnderlyingImageWidth();
		int h=imagePanelGraphic.getUnderlyingImageHeight();
		if (h>w)
			w=h;
		if (w<200) {return 200/w;}
		
		if (w>4000){return 0.1;}
		if (w>3000){return 0.15;}
		
		if (w>2000){return 0.2;}
		
		if (w>1000){return 0.4;}
	
		
		return 1;
	}
	
	public Rectangle showDialog(ImagePanelGraphic imagePanelGraphic) {
		Rectangle r=imagePanelGraphic.getCroppingRect();
		
		mag=this.getDisplayScale(imagePanelGraphic);
		
		
		this.image=imagePanelGraphic;
		panel.setMagnification(mag);
	//	IssueLog.log("the magnification of the display will be "+mag);
		ImagePanelGraphic b = new ImagePanelGraphic(imagePanelGraphic.getBufferedImage());
		panel.getGraphicLayers().add(b);
		dialogDisplayImage=b;
		double width2 = imagePanelGraphic.getUnderlyingImageWidth()*mag;
		double height2 = imagePanelGraphic.getUnderlyingImageHeight()*mag;
		panel.setPrefferedSize(width2, height2);
		b.setLocationUpperLeft(0, 0);
		
		if (r==null) {r=getRectForEntireImage(imagePanelGraphic);}
		rect=new RectangularGraphic(r);
		rect.hideStrokeHandle=true;
		rect.handleSize=4;
		if(this.hideRotateHandle) {
			rect.hideCenterAndRotationHandle=true;
		}
		rect.setAngle(cropAngle);
		rect.select();
		
		panel.getGraphicLayers().add(rect);
		for(ZoomableGraphic eItem:this.extraItems) {
			panel.getGraphicLayers().add(eItem);
		}
		
		{panel.addMouseListener(this); panel.addMouseMotionListener(this);}
		
		this.addButton(eliminateButton);
		if(dialogContext!=null &&dialogContext.nInseries>1) {
			this.addButton(yesAll);
		}
		
		this.add("ins", new InfoDisplayPanel("", instructions));
		this.add("x", new NumberInputPanel("x", rect.getBounds().getX()));
		this.moveGrid(2, -1);
		this.add("y", new NumberInputPanel("y", rect.getBounds().getY()));
		this.moveGrid(-2, 0);
		this.add("width", new NumberInputPanel("width", rect.getBounds().getWidth()));
		this.moveGrid(2, -1);
		this.add("height", new NumberInputPanel("height", rect.getBounds().getHeight()));
		if(includeAngle) {
				this.add("angle", new AngleInputPanel("angle", rect.getAngle(), true));
		}
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
		
		if(rect==null)return null;
		return rect.getBounds();
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
		RectangularGraphic rect2 = rect.copy();
		
		
		{rect.handleMove(handle, new Point((int)press.getX(),(int) press.getY()), new Point((int)drag.getX(),(int) drag.getY()));
		if (handle==8||handle==-1) {
			rect.setLocationType(RectangleEdges.CENTER);
			rect.setLocation((int)drag.getX(), (int)drag.getY());
			}
		
		
		if(rect.getBounds().getX()<0) {
			rect.setLocationUpperLeft(0, rect.getLocationUpperLeft().getY());
			//rect.setWidth(rect.getBounds().getWidth()+rect.getBounds().getX());
		}
		
		if(rect.getBounds().getY()<0) {
			rect.setLocationUpperLeft(rect.getLocationUpperLeft().getX(), 0);
			//rect.setHeight(rect.getBounds().getHeight()+rect.getBounds().getY());
		} 
		
		boolean isNewRectValid = isCroppingRectangleValid();
		
		/**if the new rectangle location is outside the area, reverts the Rectangular Graphic*/
		if (!isNewRectValid) {
			setRectangleTo(rect2);
		}
		
		
		
		
		
		
		
		
		super.notifyAllListeners(null, null);
		}
		
		
		
		setImageCropping();
		setFieldsToRect();
		
		this.onOK();
		panel.repaint();
	}


	public void setRectangleTo(RectangularGraphic rect2) {
		rect.setRectangle(rect2.getRectangle());
		rect.setAngle(rect2.getAngle());
		rect.setLocationType(rect2.getLocationType());
	}


	public boolean isCroppingRectangleValid() {
		Rectangle rmax = getRectForEntireImage();
		if (rect==null) return true;
		boolean isNewRectValid = rmax.contains(rect.getOutline().getBounds());
		return isNewRectValid;
	}
	
	public void setRectToDialog() {
		RectangularGraphic rect2 = rect.copy();
		Rectangle r = new Rectangle(this.getNumberInt("x"), this.getNumberInt("y"),this.getNumberInt("width"), this.getNumberInt("height"));
		
		rect.setRectangle(r);
		if(includeAngle) {
			double angle = this.getNumber("angle");
			rect.setAngle(angle);
				}
		if (!this.isCroppingRectangleValid()) {
			this.setRectangleTo(rect2);
		}
	}
	
	public void setFieldsToRect() {
		this.setNumber("x", rect.getBounds().getX());
		this.setNumber("y", rect.getBounds().getY());
		this.setNumber("width", rect.getBounds().getWidth());
		this.setNumber("height", rect.getBounds().getHeight());
		if(includeAngle) {
			this.setNumber("angle", rect.getAngle());
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
		handle=rect.handleNumber(arg0.getX(), arg0.getY());
		

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setVisible(boolean b) {
		
	//if (b==false)	setImageCropping();
		super.setVisible(b);
	}
	
	public void setImageCropping() {
		try{
		image.setCroppingRect(rect.getBounds());

		for(ImagePanelGraphic image: getImagepanels()) {
			image.setCroppingRect(rect.getBounds());
		}
		
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	public void removeCroppingRect() {
		image.setCroppingRect(null);
		for(ImagePanelGraphic image: getImagepanels()) {
			image.setCroppingRect(null);
		}
	}
	
	public ArrayList<ImagePanelGraphic> getImagepanels() {
		return imagepanels;
	}


	public void setImagepanels(ArrayList<ImagePanelGraphic> imagepanels) {
		this.imagepanels = imagepanels;
	}
	
	/**returns the crop rect*/
	public RectangularGraphic getRectangle() {
		return rect;
	}
	

	/**A listener object that responds to the 'eliminate crop rectangle' button*/
	public class cropLis implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			removeCroppingRect();
			rect=null;
			wasEliminated=true;
			setVisible(false);
			if (CroppingDialog.getSetContainer()!=null) CroppingDialog.getSetContainer().updateDisplay();	
		}
		
	}
	
	/**A listener object that responds to the 'ok to all' button */
	public class allOKLis implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			dialogContext.okToAll=true;
			wasOKed=true;
			setVisible(false);
			if (CroppingDialog.getSetContainer()!=null) CroppingDialog.getSetContainer().updateDisplay();	
		}
		
	}

	
	
	
	/**shows a recrop dialog*/
	public static void showCropDialogOfSize(MultiChannelSlot slot, Dimension recommmendation,  CropDialogContext context) {
		if (recommmendation==null)
			{showCropDialog(slot, null, 0, context);
			return;
			}
		
		if (slot.getModifications()!=null &&slot.getModifications().getRectangle()!=null) {
			Rectangle rold = slot.getModifications().getRectangle();
			if(rold!=null)
				showCropDialog(slot, new Rectangle(rold.x, rold.y, recommmendation.width, recommmendation.height), slot.getModifications().getAngle());
			else showCropDialog(slot, null, 0, context);
		}
		else if (recommmendation!=null)
			showCropDialog(slot, new Rectangle(0,0, recommmendation.width, recommmendation.height),0, context);
		
	
	}
	
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
		ScaleInformation oldScale=new ScaleInformation();
		
		if (slot.getModifications()!=null) { 
			oldScale=slot.getModifications().getScaleInformation();
		}
		
		PreProcessInformation process;
		if (!crop.wasEliminated)
		process = new PreProcessInformation(r.getRectangle().getBounds(), r.getAngle(), oldScale);
		else {
			process = new PreProcessInformation(null, 0, oldScale);
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
		
	}

	/**Changes the channel, slice and frame that is shown*/
	public void setDisplaySlice(CSFLocation l) {
		display=l;
		this.updateDisplayImage();
	}
	
	public static class CropDialogContext {
		
		/**indicates the number of crop dialogs that will be shown in a sequence*/
		int nInseries=1;
		
		/**indicates the index of the current crop dialog*/
		public int current=1;
		
		/**set to true if the user clicks the 'OK for all option' */
		boolean okToAll=false;
		
		public CropDialogContext(int nImages) {
			this.nInseries=nImages;
		}
		
	}
	
	
}
	
	


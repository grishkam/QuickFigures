/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicalObjects_FigureSpecific;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects.BufferedImageGraphic;
import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.FrameGraphic;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.InsetMenu;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LocationChangeListener;
import utilityClassesForObjects.ScaleInfo;

public abstract class InsetDefiner extends FrameGraphic implements LocationChangeListener{
	/**The inset itself. it is a smaller imagepanel*/
	private BufferedImageGraphic mig=null;
	public MontageLayoutGraphic personalGraphic;
	protected ArrayList<BufferedImageGraphic> additionalGraphics=new ArrayList<BufferedImageGraphic>();
	Rectangle cropping=null;
	private double bilinearScale=2;
	
	boolean listenerlongterm=true;
	{this.setName("Inset Definer");}
	
	public InsetDefiner(Rectangle r) {
		super(r);
	}
	
	
	transient boolean setup=false;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected void ensureSetup() {
		if (setup) return;
		onsetup();
		setup=true;
	}
	
	public void createChannelInsets() {
		if (additionalGraphics==null)additionalGraphics=new ArrayList<BufferedImageGraphic>();
		BufferedImageGraphic added=new BufferedImageGraphic(this.getBilinealScaledBuffImage());
		added.setForceGrayChannel(1);
		additionalGraphics.add(added);
		added=new BufferedImageGraphic(this.getBilinealScaledBuffImage());
		added.setForceGrayChannel(2);
		additionalGraphics.add(added);
		added=new BufferedImageGraphic(this.getBilinealScaledBuffImage());
		added.setForceGrayChannel(3);
		additionalGraphics.add(added);
		for(BufferedImageGraphic g: additionalGraphics) {
			if (this.getParentLayer()!=null) this.getParentLayer().add(g);
		}
		updateImagePanels();
		
	}
	
	
	
	public BufferedImageGraphic getImageInset() {
		if (mig==null) {
			createImageInsetDisplay();
		}
		return mig;
	}
	
	/**creates the inset pannel*/
	public void createImageInsetDisplay() {
		
		 mig=new BufferedImageGraphic(this.getBilinealScaledBuffImage());
		if (this.getParentLayer()!=null) this.getParentLayer().add(mig);
		updateImagePanels();
		onsetup();
	}
	
	public void onsetup() {
		if (mig!=null) mig.addLocationChangeListener(this);
		updateImagePanels();
		setup=true;
	}
	
	
	
	public void updateImagePanels() {
		if (this.getBounds().getWidth()<=0||this.getBounds().getHeight()<=0) return;
		try  {
		updateImage(getImageInset());
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		//matchImage(getImageInset(), additionalGraphics);
		
		for(BufferedImageGraphic big: additionalGraphics)try  {
			if (big==null) continue;
			
			updateImage(big);
		} catch (Throwable r) {
			IssueLog.logT(r);
		}
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
	
	public void handleSmartMove(int handlenum, Point p1, Point p2){
		super.handleSmartMove(handlenum, p1, p2);
		updateImagePanels();
	}
	
	public void matchScaleFrame(BufferedImageGraphic mig, ArrayList<BufferedImageGraphic> additionalGraphics) {
		for(BufferedImageGraphic big: additionalGraphics) {
			if (big==null) continue;
			big.setScale(mig.getScale());
			big.setFrameWidthH(mig.getFrameWidthH());
			big.setFrameWidthV(mig.getFrameWidthV());
		}
	}

	@Override
	public void objectMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectSizeChanged(LocatedObject2D object) {
		
		
	}

	@Override
	public void objectEliminated(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	public ArrayList<BufferedImageGraphic> getImageInsets() {
		ArrayList<BufferedImageGraphic> output = new  ArrayList<BufferedImageGraphic>();
		output.addAll(additionalGraphics);
		output.add(mig);
		return output;
	}
	
	void updateImage(BufferedImageGraphic big) {
		//IssueLog.log("will update image "+big);
		if (big==null) return;
		try {
		big.setImage(getBilinealScaledBuffImage());
		big.setScaleInfo(getScaleInfoForSourceImage());
		if (cropping!=null) big.setCroppingrect(cropping);}
		catch (Throwable r) {
			IssueLog.logT(r);
		}
	}
	void matchImage(BufferedImageGraphic big, ArrayList<BufferedImageGraphic> images) {
		for(BufferedImageGraphic im:images) try {
			if (im==null) continue;
			//IssueLog.log("updating image "+im);
			im.setScaleInfo(big.getScaleInfo());
			im.setImage(big.getBufferedImage());
			im.setCroppingrect(big.getCroppingrect());
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	public PopupMenuSupplier getMenuSupplier(){
		return new  InsetMenu(this);
	}

	@Override
	public void userMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void userSizeChanged(LocatedObject2D object) {
		matchScaleFrame(mig,additionalGraphics);
		
	}
	
	public abstract ScaleInfo getScaleInfoForSourceImage() ;

	public abstract BufferedImage getBuffImage();
	
	/**returns either a cropped and scaled image for the inset
	 * or merely a cropped one*/
	protected BufferedImage getBilinealScaledBuffImage() {
		if (this.getBilinearScale()==1) return this.getBuffImage(); else
		{return (BufferedImage) this.getImagePixelsScaledBilinear(getBilinearScale()) ;}
		

		
	}
	//public abstract PixelWrapper<?> getImagePixels();
	
	@Override 
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		this.ensureSetup();
		super.draw(g, cords);
	}

	
	public abstract Image getImagePixelsScaledBilinear(double bilinearScale);

	public void createMultiChannelInsets() {
		// TODO Auto-generated method stub
		
	}
	
	public AbstractUndoableEdit removeInsetAndPanels() {
		//CompoundEdit2 output = new CompoundEdit2();
		removePanels() ;
		getParentLayer().remove(this);
		
		return null;
	}

	public AbstractUndoableEdit removePanels() {
		// TODO Auto-generated method stub
		this.getParentLayer().remove(getImageInset());
		for(BufferedImageGraphic big: additionalGraphics)  {
			if (big==null) continue;
			
			this.getParentLayer().remove(big);
		} 
		return null;
	}

	public double getBilinearScale() {
		return bilinearScale;
	}

	public void setBilinearScale(double bilinearScale) {
		this.bilinearScale = bilinearScale;
	}

	public void afterUserScaleResize() {
		// TODO Auto-generated method stub
		
	}

	public PanelManager getPanelManager() {
		// TODO Auto-generated method stub
		return null;
	}
	
	}

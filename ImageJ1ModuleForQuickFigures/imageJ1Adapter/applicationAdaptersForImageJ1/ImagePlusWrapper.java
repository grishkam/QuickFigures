package applicationAdaptersForImageJ1;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.Roi;
import ij.gui.Toolbar;
import ij.measure.Calibration;
import ij.plugin.CanvasResizer;
import ij.process.ImageProcessor;
import ij.process.LUT;
import infoStorage.BasicMetaDataHandler;
import infoStorage.MetaInfoWrapper;
import logging.IssueLog;
import multiChannelFigureUI.ChannelManipulations;
import undo.UndoManagerPlus;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ScaleInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;


import channelMerging.ChannelEntry;
import channelMerging.ChannelMerger;
import channelMerging.ChannelOrderAndColorWrap;
import channelMerging.MultiChannelWrapper;
import channelMerging.PreProcessInformation;
import channelMergingImageJ1.ChannelSwapListener;
import channelMergingImageJ1.CompositeImageMerger;
import channelMergingImageJ1.IJ1ChannelOrderWrap;
import genericMontageKit.SelectionManager;
import genericMontageKit.SubFigureOrganizer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import gridLayout.BasicMontageLayout;
import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ImageWrapper;
import applicationAdapters.ObjectCreator;
import applicationAdapters.PixelWrapper;

public class ImagePlusWrapper implements  ImageWrapper, MultiChannelWrapper, ChannelSwapListener {

	private SelectionManager selectionManagger;
	private  ObjectCreator  objectCreator=new  OverlayObjectCreator();
	private CompositeImageMerger merger=new CompositeImageMerger(this);
	
	ImagePlus imp;
	private ArrayList<String> channames;
	private ArrayList<String> chanexposures;
	
	public ImagePlusWrapper(ImagePlus imp) {
		this.imp=imp;
		/**if (imp instanceof GraphicalImagePlus) {
			selectionManagger=((GraphicalImagePlus)imp).getSelectionManagger();
		}
		else*/ {
			this.setSelectionManagger(new IJ1Selections(imp));
		}
		checkVoid(); 
		
		if(imp!=null)
		ChannelManipulations.innitializeDisplayRangetoMinMax(this);
		}
	
	public ImagePlus getImagePlus() {
		return imp;
	}
	
	public void checkVoid() {
	
	}
	
	@Override
	public void takeRoiFromImage(LocatedObject2D roi) {
		checkVoid();
		if (roi instanceof RoiWrapper) {
			((RoiWrapper)roi).takeFromImage(imp);
		} 
		/**
		if (imp instanceof GraphicalImagePlus&&roi instanceof ZoomableGraphic) {
			GraphicalImagePlus gi = (GraphicalImagePlus)imp;
			ZoomableGraphic z=(ZoomableGraphic) roi;
			gi.getGraphicLayerSet().remove(z);
		} */
	}

	@Override
	public void addRoiToImage(LocatedObject2D roi) {
		checkVoid();
		if (roi instanceof RoiWrapper) {
			((RoiWrapper)roi).addToImage(imp);
		}
		/**
		if (roi instanceof ZoomableGraphic && !(imp instanceof GraphicalImagePlus)) {
			imp=MakeGraphical.makeGraphicalIfPossible(imp);
		}
		
		if (imp instanceof GraphicalImagePlus&&roi instanceof ZoomableGraphic) {
			GraphicalImagePlus gi = (GraphicalImagePlus)imp;
			ZoomableGraphic z=(ZoomableGraphic) roi;
			gi.add(z);
		}*/
	}

	@Override
	public void addRoiToImageBack(LocatedObject2D roi) {
		addRoiToImage(roi);

	}

	@Override
	public ArrayList<LocatedObject2D> getLocatedObjects() {
		
		if (imp==null) {
			return new ArrayList<LocatedObject2D>() ;
			}
		ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D>();
		if (imp.getOverlay()!=null) {
		Roi[] rois=imp.getOverlay().toArray();
		for(Roi roi: rois) {if (roi!=null) output.add(new RoiWrapper(roi));}
		}
		
		/**if (imp instanceof GraphicalImagePlus) {
			GraphicalImagePlus gi = (GraphicalImagePlus)imp;
			ArrayList<ZoomableGraphic> graphics = gi.getGraphicLayerSet().getAllGraphics();
			for(ZoomableGraphic item :graphics) {
				if (item instanceof locatedObject) {
					output.add((locatedObject)item);
				}
			}
		}*/
		return output;
	}

	@Override
	public LocatedObject2D getSelectionObject() {
		checkVoid();
		if (imp.getRoi()==null) return null;
		return new RoiWrapper(imp.getRoi());
	}

	@Override
	public PixelWrapper getPixelWrapper() {
		checkVoid();
		return new ProcessorWrapper(imp.getProcessor());
	}

	@Override
	public void updateDisplay() {
		checkVoid();
		imp.updateAndDraw();
		
	}

	/**resizes the canvas of pixels but does not move the objects*/
	@Override
	public void CanvasResizePixelsOnly(int width, int height, int xOff, int yOff) {
		Prefs.set("resizer.zero", false); 
		Toolbar.setBackgroundColor(Color.white);
		ImageStack newStack=new CanvasResizer().expandStack(imp.getStack(), width, height, xOff, yOff);	
		imp.setStack(newStack);
	}

	@Override
	public String getTitle() {
		if (imp==null) return "";
		return imp.getTitle();
	}

	@Override
	/**Returns the path where the file is saved*/
	public String getPath() {
		if (imp==null) return null;
		String title=imp.getTitle(); 
		if (imp.getOriginalFileInfo()==null) {
			if (!title.endsWith(".tif")) title+=".tif"; 
			return title;
			}
		
		String folderDirectory=imp.getOriginalFileInfo().directory;
	
		if (folderDirectory==null||title==null) return null;
	
		String path=folderDirectory+title; 
		
		if (path!=null && !"".equals(path.trim()) ) return path;
		return path;
	}
	
	@Override
	public
	Window window() {
		checkVoid();
		if (imp==null) return null;
		return imp.getWindow();
	}
	public int width() {
		checkVoid();
		if (imp==null) return 0;
		return imp.getWidth();
		}
	public int height() {
		checkVoid();
		if (imp==null) return 0;
		return imp.getHeight();
		}
	
	public void show() {
		if (imp!=null)
		imp.show();
		 
	 }
	
	
	
	public SubFigureOrganizer getOrganizer() {
		//Montage_Updater mu=UpdaterManger.findUpdater(imp);
		//if (mu.images2().hasImage(imp)) return mu;
		return null;
	}

	
	
	@Override
	public ScaleInfo getScaleInfo() {
		if (imp!=null)
		return new IJ1ScaleInfo(imp);
		return null;
	}
	
	@Override
	public void setScaleInfo(ScaleInfo scaleInfo) {
		Calibration cal = imp.getCalibration();
		cal.setUnit(scaleInfo.getUnits());
		cal.pixelWidth=scaleInfo.getPixelWidth();
		cal.pixelHeight=scaleInfo.getPixelHeight();
		cal.pixelDepth=scaleInfo.getPixelDepth();
		/**
		FileInfo fi=imp.getFileInfo();
		fi.unit=scaleInfo.getUnits();
		fi.pixelWidth=scaleInfo.getPixelWidth();
		fi.pixelHeight=scaleInfo.getPixelHeight();
		
		imp.setFileInfo(fi);*/
		imp.setCalibration(cal);
		
		
	}

	
	public void saveLayout(BasicMontageLayout layout) {
		if (layout instanceof BasicMontageLayout) {
		//	(( BasicMontageLayout)layout).setMontageProperties(getMetadataWrapper());
		}
		
	}

	@Override
	public GraphicLayer getGraphicLayerSet() {
		/**if (imp instanceof GraphicalImagePlus) {
			GraphicalImagePlus gmp=(GraphicalImagePlus) imp;
			return gmp.getGraphicLayerSet();
		}else 
		if (!(imp instanceof CompositeImage)) {
			imp=MakeGraphical.makeGraphicalIfPossible(imp);
		}*/
		return null;
	}

	

	@Override
	public void onItemLoad(ZoomableGraphic z) {
		/**if (imp instanceof GraphicalImagePlus) {
			GraphicalImagePlus gmp=(GraphicalImagePlus) imp;
			gmp.onItemLoad(z);
		}*/
		
	}

	/**
	@Override
	public void setSelection(locatedObject l, int i) {
		if (imp instanceof GraphicalImagePlus) {
			GraphicalImagePlus g=(GraphicalImagePlus) imp;
			g.getSelectionManagger().setSelection(l, i);
		}
		
	}*/
	
	public String toString() {
		return ""+imp;
	}

	@Override
	public Dimension getCanvasDims() {
		// TODO Auto-generated method stub
		if (imp==null) return new Dimension();
		return new Dimension(imp.getWidth(), imp.getHeight());
	}

	@Override
	public ImageWrapper getAsWrapper() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public int nChannels() {
		// TODO Auto-generated method stub
		if(imp==null)  return 1;
		return imp.getNChannels();
	}

	@Override
	public int nFrames() {
		if(imp==null)  return 1;
		return imp.getNFrames();
	}

	@Override
	public int nSlices() {
		if(imp==null)  return 1;
		return imp.getNSlices();
	}

	
	/**retrives the pixels from the given channel slice frame and other dimensions*/
	@Override
	public PixelWrapper getPixelWrapperForSlice(int channel, int slice,
			int frame, int... dim) {
		checkVoid();
		if(imp==null) {
			IssueLog.log("Image has been set to null");
			return null;
			}
		/**checks for validaty and warns*/
		if (channel>nChannels()) {
			IssueLog.log("asked for impossible channel in "+this.getTitle());
		}
		if (slice>nSlices()) {
			IssueLog.log("asked for impossible slice in "+this.getTitle());
		}
		if (frame>nFrames()) {
			IssueLog.log("asked for impossible frame in "+this.getTitle());
		}
		int i=imp.getStackIndex(channel, slice, frame);
		if (imp.getStackSize()<i||imp.getStack()==null||imp.getStack().getSize()==0) {
			IssueLog.log("asked for impossible stack index "+this.getTitle());
		}
		ImageProcessor p = imp.getStack().getProcessor(i);
		return new ProcessorWrapper(p);
		
	}

	@Override
	public MetaInfoWrapper getMetadataWrapper() {
		return new ImagePlusMetaDataWrapper(imp);
	}

	public SelectionManager getSelectionManagger() {
			
		return selectionManagger;
	}

	public void setSelectionManagger(SelectionManager selectionManagger) {
		this.selectionManagger = selectionManagger;
	}

	@Override
	public ObjectCreator getDefaultObjectCreator() {
		return  objectCreator;
	}

	@Override
	public int getID() {
		if (imp!=null) return imp.getID();
		return 0;
	}

	@Override
	public int getStackIndex(int channel, int slice, int frame, int...dim) {
		return imp.getStackIndex(channel, slice, frame);
	}

	@Override
	public boolean containsImage() {
		if (imp==null||imp.getStack()==null||imp.getStack().getSize()<1) return false;
		
		return true;
	}

	@Override
	/**returns the name of the channel. channel indexes go from 1 onward*/
	public String channelName(int c) {
		int index=imp.getStackIndex(c, 1, 1);
		ImageStack stack=imp.getStack();
		if (stack.getSize()<index) return null;
		String name=stack.getSliceLabel(index);
		if (name==null) {
			setSliceLabelToChannel(c, index, stack);
		}
		return name;
		
	}

	private void setSliceLabelToChannel(int c, int index, ImageStack stack) {
		stack.setSliceLabel("c:"+c+"/"+imp.getNChannels(), index);
		if(imp.getNChannels()<stack.getSize())
			for( int f=1; f<=imp.getNFrames(); f++) {
				for(int s=1; s<=imp.getNSlices(); s++) {
					index=imp.getStackIndex(c, s, f);
					stack.setSliceLabel("c:"+c+"/"+imp.getNChannels(), index);
				}
			}
		}

	/**gets the channel lut as a color*/
	@Override
	public Color getChannelColor(int c) {
		// TODO Auto-generated method stub
		if(imp==null) return null;
		LUT[] eachChanLUT=imp.getLuts(); 
		if (eachChanLUT==null)
			return getColorFromLUT(imp.getProcessor().getLut(), 255);
		else if (c==0) {
			IssueLog.log("problem with lut arrays");
			return null;
		} else if (eachChanLUT.length==0){
			return Color.white;	
		}
		else {
			
			return getColorFromLUT(eachChanLUT[c-1], 255);
			}
		
	}
	
	
	/**These methods return a color from specified part of a lut defined by int in*/
	public static Color getColorFromLUT(LUT lut, int in){return new Color(lut.getRed(in), lut.getGreen(in), lut.getBlue(in));}

	
	@Override
	public String getSliceName(int channel, int slice, int frame, int... dim) {
		int i=this.getStackIndex(channel, slice, frame, dim);
		
		return imp.getStack().getSliceLabel(i);
	}

	@Override
	public boolean containsSplitedChannels() {
		return imp instanceof CompositeImage;
		//return false;
	}


	public Rectangle getSelectionRectangle( int i) {
		// TODO Auto-generated method stub
		try{
		if (i==0&&imp.getRoi()!=null) return imp.getRoi().getBounds();
		if (i==1&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[0].getBounds();
		if (i==2&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[1].getBounds(); 
		if (i==3&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[2].getBounds(); 
		if (i==4&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[3].getBounds(); 
		} catch (Exception e) {}
		return null;
	}
	
	public void eliminateSelection( int i) {
		// TODO Auto-generated method stub
		try{
		if (i==0&&imp.getRoi()!=null)imp.killRoi();;
		} catch (Exception e) {}
		
	}

	@Override
	public boolean isSameImage(Object o) {
		if (o==imp) return true;
		if (o instanceof ImagePlusWrapper) {
			ImagePlusWrapper o2=(ImagePlusWrapper) o;
			if (o2.getImagePlus()==imp) return true;
		}
		return false;
	}

	@Override
	public ChannelOrderAndColorWrap getChannelSwapper() {
		IJ1ChannelOrderWrap cs = new IJ1ChannelOrderWrap(imp, this);
		cs.addChannelSwapListener(this);
		this.channames=null;
		return cs;
	}

	@Override
	public int[] convertIndexToPosition(int i) {
		// TODO Auto-generated method stub
		return imp.convertIndexToPosition(i);
	}

	@Override
	public void setSliceName(String name, int i) {
		imp.getStack().setSliceLabel(name, i);
		
	}

	@Override
	public String getSliceName(int stackIndex) {
		// TODO Auto-generated method stub
		return imp.getStack().getSliceLabel(stackIndex);
	}

	@Override
	public double getChannalMax(int chan) {
		if (lutsNotAvailable(chan)) return 0;
		if(getLut(chan)==null) return 0;
		return getLut(chan).max;
	}

	public boolean lutsNotAvailable(int chan) {
		try {
			return imp==null||imp.getLuts()==null||imp.getLuts().length==0||imp.getLuts().length<chan;
		} catch (Exception e) {
			return true;
		}
		
		
	
	}

	public LUT getLut(int chan) {
		try {
			return imp.getLuts()[chan-1];
		} catch (Exception e) {
			IssueLog.log(e);
			
		}
		return null;
	}

	@Override
	public double getChannalMin(int chan) {
		if (lutsNotAvailable(chan)) return 0;
		if(getLut(chan)==null) return 0;
		return getLut(chan).min;
	}

	@Override
	public void setChannalMax(int chan, double max) {
		if (lutsNotAvailable(chan)) return ;
		imp.setSlice(chan);
		 double smin = getLut(chan).min;
		 imp.setDisplayRange(smin, max);
		 imp.updateImage();
		
	}

	@Override
	public void setChannalMin(int chan, double min) {
		if (lutsNotAvailable(chan)) return ;
		imp.setSlice(chan);
		double smax = getLut(chan).min;
		 imp.setDisplayRange(min, smax);
		 imp.updateImage();
	}
	
	


	@Override
	public void setTitle(String st) {
		imp.setTitle(st);
		
	}

	@Override
	public ChannelEntry getSliceChannelEntry(int channel, int slice, int frame,
			int... dim) {
		int index2=getStackIndex(channel, slice, frame,
				 dim);
		String title2=getSliceName(channel, slice, frame, dim);
		ChannelEntry ce = new ChannelEntry(title2,  this.getChannelColor(channel), channel);
		ce.setRealChannelName(this.getRealChannelName(channel));
		ce.setOriginalStackIndex(index2);
		return ce;
	}

	@Override
	public DisplayedImageWrapper getImageDisplay() {
		if (imp==null) return null;
		return new ImagePlusDisplayWrap(imp);
	}

	
	void setUpChanNames() {
		if (this.channames==null){
			ImagePlusMetaDataWrapper mcw = new ImagePlusMetaDataWrapper(imp);
			this.channames  =
				(new BasicMetaDataHandler()).channelNamesInOrder(mcw);
			if (channames.size()==0) 
				(new BasicMetaDataHandler()).createChanNamesFor(mcw, imp.getNChannels());//if there are no channel names, creates artificial ones
			this.chanexposures=(new BasicMetaDataHandler()).ZviChannelExposuresInOrder(mcw);
		}
	}
	
	/**returns the real channel name from a list field. first channel is 1, second is 2*/
	@Override
	public String getRealChannelName(int i) {
		 setUpChanNames() ;
		if (channames.size()>=i) {
			
			return channames.get(i-1);
			
		}
		return null;
	}
	
	@Override
	public String getRealChannelExposure(int i) {
		// TODO Auto-generated method stub
		 setUpChanNames() ;
		if (this.chanexposures.size()>=i) {
			
			return chanexposures.get(i-1);
			
		}
		return null;
	}

	/**returns the index of the channel with real channel name. example
	  if eGFP is in the second channel then this will return a 2
	  for an input of "eGFP" */
	@Override
	public int getIndexOfChannel(String realname) {
		if (realname==null) return 0;
		for(int c=1; c<=this.nChannels(); c++) {
			String rc = getRealChannelName(c);
			if (rc==null) continue;
			if ((rc.trim()).equals(realname.trim())) return c;
		}
		return 0;
	}

	@Override
	public ChannelMerger getChannelMerger() {
		return merger;
	}

	@Override
	public ArrayList<ChannelEntry> getChannelEntriesInOrder() {
		ArrayList<ChannelEntry> output = new  ArrayList<ChannelEntry>();
		// TODO Auto-generated method stub
		for(int c=1; c<=this.nChannels(); c++) {
			ChannelEntry entry= getSliceChannelEntry(c, 1,1);
			output.add(entry);
		}
		return output;
	}

	@Override
	public int getStackSize() {
		if (imp==null) return 0;
 		return imp.getStackSize();
	}

	@Override
	public UndoManagerPlus getUndoManager() {
		return new UndoManagerPlus();
	}

	@Override
	public void colorBasedOnRealChannelName() {
		for(int i=1; i<=this.nChannels(); i++) try {
		
			String name=this.getRealChannelName(i);
			if (name==null) {IssueLog.log("Cannot find real Channel name");
								continue;}
			
			//IssueLog.log("Will try to set channel color for "+name);//+ " of exposure "+this.getRealChannelExposure(i) +" ms");
			ChannelOrderAndColorWrap channelSwapper = this.getChannelSwapper();
			
			Color color1 = ChannelManipulations.setChannelColor(i, name, channelSwapper);
			
		} catch (Throwable t) {
			IssueLog.log("problem setting colors based on real channel names "+i);
			IssueLog.log("meta data may be incomplete or inconsistent "+i);
			IssueLog.log(t);
			
		}
		
	}

	
 
	@Override
	public void renameBasedOnRealChannelName() {
		for(int i=1; i<=this.nChannels(); i++) {
		
			String name=this.getRealChannelName(i)+"; "+this.getRealChannelExposure(i) +" ms";
			if (name==null) {IssueLog.log("Cannot find real Channel name");
								continue;}
			name=name+";";
			this.setSliceName(name+this.getSliceName(i), i);;
		}
		
	}

	/**not yet implemented. returns a scaled version of this*/
	@Override
	public MultiChannelWrapper scaleBilinear(double d) {

		return new ImagePlusWrapper(imp.duplicate());
	}

	public ImagePlusWrapper cropAtAngle(PreProcessInformation p) {
		if(p==null) {
			
			IssueLog.log("no preprocess given");
			return this;
		}
		return cropAtAngle(p.getRectangle(), p.getAngle(), p.getScale());
		
	}
	
	/** returns a scaled version of this*/
	@Override
	public ImagePlusWrapper cropAtAngle(Rectangle r, double angle, double scale) {
		imp.deleteRoi();
		ImagePlus d = imp.duplicate();
		ImageStack oldstack = d.getStack();

		ImageStack nstack = null;
		for(int i=1; i<=oldstack.getSize(); i++) {
			ProcessorWrapper p = new ProcessorWrapper(oldstack.getProcessor(i));
			
			if(r!=null)
				p.cropAtAngle(r, angle);
			
			if (scale!=1) {
				p.scaleBilinear(scale);
			}
			
			if (nstack == null)
				nstack=new ImageStack(p.getPixels().getWidth(), p.getPixels().getHeight());
			nstack.addSlice(oldstack.getSliceLabel(i), p.getPixels());
		}
		
		
		
		d.setStack(nstack);
		
		
		/**
		d.getOriginalFileInfo().pixelHeight=imp.getOriginalFileInfo().pixelHeight/scale;
				;
		d.getOriginalFileInfo().pixelWidth=imp.getOriginalFileInfo().pixelWidth/scale;
				
		
			d.getFileInfo().pixelHeight=imp.getFileInfo().pixelHeight/scale;
			d.getFileInfo().pixelWidth=imp.getFileInfo().pixelWidth/scale;
		*/
		
		ImagePlusWrapper imagePlusWrapper = new ImagePlusWrapper(d);
		
		ScaleInfo scaled = this.getScaleInfo().getScaledCopyXY(scale);
		imagePlusWrapper.setScaleInfo(scaled);
		
		return imagePlusWrapper;
		
	
	}

	@Override
	public Dimension getDimensions() {
		return new Dimension(imp.getWidth(), imp.getHeight());
	}

	@Override
	public void afterChanSwap() {
		channames=null;
		chanexposures=null;
	}

	@Override
	public Integer getSelectedFromDimension(int i) {
		if(i==0) return imp.getChannel();
		if(i==1) return imp.getSlice();
		if(i==2) return imp.getFrame();
		return 0;
	}

	@Override
	public double bitDepth() {
		if (imp==null) return 8;
		return imp.getBitDepth();
	}




	
	
}

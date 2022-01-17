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
 * Version: 2022.0
 */

package channelMergingImageJ1;
import java.awt.Color;
import applicationAdaptersForImageJ1.ImagePlusWrapper;
import genericTools.AbstractChannelHandler;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.process.ImageProcessor;
import ij.process.LUT;
import infoStorage.BasicMetaDataHandler;

public class IJ1ChannelSwapper extends AbstractChannelHandler<ImagePlus> {
	

	/**Switches two slice of the stack in the imagePlus. I know there are many simpler ways to write this program
	  but I adapted this from my swap channels method that already worked.*/
	public  static ImagePlus swapStackSlices(ImagePlus imp, int stackingIndex1, int stackIndex2){
		ImageStack oldstack=imp.getStack() ;
		ImageProcessor pa=oldstack.getProcessor(stackingIndex1).duplicate();
		ImageProcessor pb=oldstack.getProcessor(stackIndex2).duplicate();
		String sa=oldstack.getSliceLabel(stackingIndex1);
		String sb=oldstack.getSliceLabel(stackIndex2);
		oldstack.getProcessor(stackingIndex1).insert(pb, 0, 0);
		oldstack.getProcessor(stackIndex2).insert(pa, 0, 0);
		oldstack.setSliceLabel(sb, stackingIndex1);
		oldstack.setSliceLabel(sa, stackIndex2);
		imp.updateAndDraw();
		return new ImagePlus("", oldstack);
	}
	
	/**Given a color composite image and two channel numbers, this will return 
	   an identical color composite image with the channels swapped.*/
	private  static CompositeImage swapChannels(ImagePlus imp, int a, int b){
		if (imp==null) return null;
		if (!imp.isComposite()) return null;	
		while (a>imp.getNChannels()) a-=imp.getNChannels();
		while (b>imp.getNChannels()) b-=imp.getNChannels();
		
		if (a>imp.getNChannels() || b>imp.getNChannels() ) return null;
		if (a==-1 || b==-1 ) return null;
		
		CompositeImage ci4 = (CompositeImage) imp;
		CompositeImage imm=new CompositeImage(ci4, CompositeImage.	COLOR );
		imm.setLuts(ci4.getLuts());
		imm.setPosition(1, imm.getSlice(), imm.getFrame());
		ImageStack stack=new ImageStack(imm.getWidth(), imm.getHeight()) ;
		LUT[] oldluts=imm.getLuts();
		LUT[] newluts= new LUT[oldluts.length];
		
		int j=1;
		int i=0;
		
		if (!imp.isHyperStack()) {
		while (j<=imm.getStackSize()){
			imm.setPositionWithoutUpdate(j, imm.getSlice(), imm.getFrame());  	
			if ( j!=a && j!=b) {stack.addSlice(imp.getStack().getSliceLabel(j), imm.getChannelProcessor() ); newluts[i]=oldluts[j-1]; i++;}
			if ( j==a ) {imm.setPositionWithoutUpdate(b, imm.getSlice(), imm.getFrame()); stack.addSlice(imp.getStack().getSliceLabel(b), imm.getChannelProcessor() ); 
			newluts[i]=oldluts[b-1]; i++;}
			if ( j==b ) {imm.setPositionWithoutUpdate(a, imm.getSlice(), imm.getFrame()); stack.addSlice(imp.getStack().getSliceLabel(a), imm.getChannelProcessor() ); 
			newluts[i]=oldluts[a-1]; i++;}
			j++;
		}
		}
		
		if (imp.isHyperStack()){
			int nC = imp.getNChannels();
			
			while (j<=imm.getStackSize()){
				imm.setPosition(j);  	
				if ( (j-1)%nC!=a-1 && (j-1)%nC!=b-1) {stack.addSlice(imp.getStack().getSliceLabel(j), imm.getChannelProcessor() ); if (i<nC) newluts[i]=oldluts[j-1]; i++;}
				if ( (j-1)%nC==a-1 ) {imm.setPosition(j+(b-a)); stack.addSlice(imp.getStack().getSliceLabel(j+(b-a)), imm.getChannelProcessor() ); 
				if (i<nC) newluts[i]=oldluts[b-1]; i++; }
				if ( (j-1)%nC==b-1 ) {imm.setPosition(j+(a-b)); stack.addSlice(imp.getStack().getSliceLabel(j+(a-b)), imm.getChannelProcessor() ); 
				if (i<nC) newluts[i]=oldluts[a-1]; i++;}
				j++;
			}

	}
		
		 CompositeImage output=new CompositeImage(new ImagePlus("", stack));//some wort of erro goes on here it says that stack size is not a multiple of channels.
		 
		 if (imp.isHyperStack()) {	output.setDimensions(imp.getNChannels(), imp.getNSlices(), imp.getNFrames()) ;}
		 output.setLuts(newluts);
		 return output;
	}
	

/**given an image, it will swap the channels */
	@Override
	public  void swapChannelsOfImage(ImagePlus p, int a, int b){
		if (p==null) return;
		if (a==b) return;
		if (p.getNChannels()==1) return;
		if (a<1||b<1) return;//channel indices start from 1 so lower is invalid
		
		if (!p.isComposite()) {
			ImagePlus cpnew=swapStackSlices(p, a, b); 
			p.setStack(cpnew.getStack()); return;
			}
		
		
		CompositeImage cpnew=swapChannels(p, a, b);
		if(cpnew==null||cpnew.getStack()==null) return;
		p.setStack(cpnew.getStack());
		((CompositeImage) p).setLuts(cpnew.getLuts());
		p.setPosition(a, p.getSlice(), p.getFrame()) ;
		p.setPosition(b, p.getSlice(), p.getFrame()) ;
		
		
		/**meta data alteration. has flaws*/
		try {
			IJMetaDatause.switchMetaDataEntries(new ImagePlusWrapper(p).getMetadataWrapper(), BasicMetaDataHandler.myIndexCode+ (a-1)+" ", BasicMetaDataHandler.myIndexCode+ (b-1)+" ");
		} catch (NullPointerException np) {}	
		
		/**CZI derived meta data alteration. Has known flaws. not tested on a variety of channel orders. */
		try {
			/**this meta data swap will ruin the agreement between exposure time. Also it appears that this does not even work properly*/
			/**ArrayList<Integer> list = IJMetaDatause.getIntEntryListByNumber(new ImagePlusWrapper(p).getMetadataWrapper(), "Information|Image|Channel|Id #"," ", 1,6, new String[] {"Channel:"});
			int inda = list.indexOf((a-1));
			int indb = list.indexOf((b-1));
			IJMetaDatause.switchMetaDataEntries(new ImagePlusWrapper(p).getMetadataWrapper(), "Information|Image|Channel|Id #"+(inda+1)+ " " , "Information|Image|Channel|Id #"+ (indb+1)+" ");
			*/
		} catch (NullPointerException np) {}	
	}

	public void updateAndDraw(ImagePlus imp) {
		if (imp==null) return;
		imp.updateAndDraw();
	}
		
	public void removeConflictingListeners() {
		closeContrastAdjuster();
	}
	
	public static void closeContrastAdjuster() {
		for (Object f: WindowManager.getNonImageWindows()) {
				if (f instanceof ij.plugin.frame.ContrastAdjuster) ((ij.plugin.frame.ContrastAdjuster) f).close();
		}
		}

	public Boolean swapChannelLuts(ImagePlus imp, int a, int b) {
		if (imp==null) return false;
		if (!imp.isComposite()) return false;
		
		while (a>imp.getNChannels()) a-=imp.getNChannels();
		while (b>imp.getNChannels()) b-=imp.getNChannels();
		//IJ.log("swapped luts of two channels "+a+ " and "+b);
		if (a>imp.getNChannels() || b>imp.getNChannels() ) return false;
		CompositeImage ci4 = (CompositeImage) imp;
		
		LUT[] oldluts=ci4.getLuts().clone();
		LUT[] newluts=ci4.getLuts().clone();
		//IJ.showMessage("lut length is" + oldluts.length);
		LUT lb= newluts[b-1];
		LUT la= newluts[a-1];
		lb.min=oldluts[a-1].min;lb.max=oldluts[a-1].max;//these set the display ranges to be correct
		la.min=oldluts[b-1].min;la.max=oldluts[b-1].max;
		ci4.setChannelLut(lb, a);
		ci4.setChannelLut(la, b);
		return true;
	}
	
	@Override
	public void setChannelColor(ImagePlus imp, Color lut, int chan) {
		if (imp instanceof CompositeImage) setLutColorWithoutDisplayRangeEdit((CompositeImage)imp, LUT.createLutFromColor(lut), chan);
	}
	
	private void setLutColorWithoutDisplayRangeEdit(CompositeImage ci4, LUT lut, int a) {
		LUT[] oldluts=ci4.getLuts().clone();
		lut.min=oldluts[a-1].min;lut.max=oldluts[a-1].max;
		ci4.setChannelLut(lut, a);
	}
	
	static BasicMetaDataHandler IJMetaDatause=new BasicMetaDataHandler();

}

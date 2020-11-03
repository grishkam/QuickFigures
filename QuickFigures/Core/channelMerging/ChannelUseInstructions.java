package channelMerging;

import java.io.Serializable;
import java.util.ArrayList;

import gridLayout.BasicMontageLayout;
import logging.IssueLog;
import utilityClasses1.ArraySorter;

	public class ChannelUseInstructions implements Serializable {

		/**
		 Instructions on how to take that channels from a multichannel image
		 and make panels.
		 Used by 
		 */
		private static final long serialVersionUID = 1L;
			
			/**These options are important for deciding how composite stacks are converted into RGB*/
			public int ChannelsInGrayScale=1;
			
			/**Channels that are treated differently are noted by their numbers*/
		    public int eachMergeChannel=0;//merged with every other channel
		    public ArrayList<Integer> excludedChannelPanels=new ArrayList<Integer>(), 
		    		noMergeChannels=new ArrayList<Integer>();//excluded from the merge
		   
		    /**will skip channels after this one*/
		    public int ignoreAfterChannel=0;
		    
		    /**what to do with the Merge*/
		    public int MergeHandleing=0;
		    
		    /**what amount of columns are recommended*/
		    public int idealColNum=5;

			private ChannelPanelReorder reorder;
		 
		    /**returns a set of channel use instrcutions used when one wants to put
		     * channel panels as insets near or around a merged image*/
		    public static ChannelUseInstructions getChannelInstructionsForInsets() {
		    	ChannelUseInstructions out = new ChannelUseInstructions();
		    	out.ignoreAfterChannel=0;
		    	out.eachMergeChannel=0;
		    	out.ChannelsInGrayScale=0;
		    	out.excludedChannelPanels=new ArrayList<Integer>();
		    	out.noMergeChannels=new ArrayList<Integer>();
		    	return out;
		    }
		   
		    /**sets several values of fields*/
			public void setChannelHandleing(int ChannelsInGrayScale, int MergeFirst, int eachMergeChannel, ArrayList<Integer> excludedChannelPanels, ArrayList<Integer> noMergeChannels, int ignoreAfterChannel) {
				this.ChannelsInGrayScale=ChannelsInGrayScale;
				this.eachMergeChannel=eachMergeChannel;
				this.excludedChannelPanels=excludedChannelPanels;
				this.noMergeChannels=noMergeChannels;
				this.MergeHandleing=MergeFirst;
				this.ignoreAfterChannel=ignoreAfterChannel;
			}
			
			public static final int MERGE_LAST=0, MERGE_FIRST=1, ONLY_MERGE_PANELS=3, NO_MERGE_PANELS=4;
			
			/**true if merge panel goes last*/
			public boolean mergePanelLast() {
				return MergeHandleing==MERGE_LAST;
			}

			/**true if merge panel goes first*/
			public boolean mergePanelFirst() {
				return MergeHandleing==MERGE_FIRST;
			}
			
			/**returns true if ONLY the mergePanel should be included in the panel set*/
			public boolean onlyMergePanel() {
				return MergeHandleing==ONLY_MERGE_PANELS;
			}
			
			/**returns true if the mergePanel should be included*/
			public boolean addsMergePanel() {
				if (MergeHandleing==MERGE_LAST||MergeHandleing==ONLY_MERGE_PANELS||MergeHandleing==MERGE_FIRST) return true;
				
				return false;
			}

			/**returns true if the given channel should be excluded from the merge panel*/
			public boolean isMergeExcluded(int c) {
				for(Integer ex: noMergeChannels) {if (ex.intValue()==c) return true;}
				if (this.ignoreAfterChannel>0&&c>this.ignoreAfterChannel) return true;
				return false;
			}
			
			/**true is channel number c is to be excluded from the panel list. false otherwise*/
			public boolean isChanPanExcluded(int c) {
				for(Integer ex: excludedChannelPanels) {if (ex.intValue()==c) return true;}
				if (this.ignoreAfterChannel>0&&c>ignoreAfterChannel) return true;
				if (c==eachMergeChannel) return true;
				if (onlyMergePanel()) return true;
				return false;
			}
			
			
			
		
			
			public void makeMatching(ChannelUseInstructions other) {
				 makePartialMatching(other);
				other.reorder=new ChannelPanelReorder(this.getChanPanelReorder().currentOrder);
			} 
			
			public void makePartialMatching(ChannelUseInstructions other) {
				other.ChannelsInGrayScale=ChannelsInGrayScale;
				other.eachMergeChannel=eachMergeChannel;
				
				other.MergeHandleing=MergeHandleing;
				other.ignoreAfterChannel=ignoreAfterChannel;
				other.idealColNum=idealColNum;
				
				other.excludedChannelPanels=excludedChannelPanels;
				other.noMergeChannels=noMergeChannels;
					} 
			
			/**Generates a duplicate of this set of instructions but without the channel order*/
			public ChannelUseInstructions duplicate() {
				ChannelUseInstructions output = new ChannelUseInstructions();
				makePartialMatching(output);
				output.excludedChannelPanels=new ArrayList<Integer>();output.excludedChannelPanels.addAll(excludedChannelPanels);
				output.noMergeChannels=new ArrayList<Integer>();output.noMergeChannels.addAll(noMergeChannels);
				return output;
			}
			
			/**Matches the setting of the argument to this instance.
			public void giveSettingsTo(ChannelUseInstructions output) {
				output.ChannelsInGrayScale=this.ChannelsInGrayScale;
				output.eachMergeChannel=this.eachMergeChannel;
				output.excludedChannelPanels=this.excludedChannelPanels;
				output.MergeHandleing=this.MergeHandleing;
				output.noMergeChannels=this.noMergeChannels;
				output.scaleBilinear=this.scaleBilinear;
				output.ignoreAfterChannel=this.ignoreAfterChannel;
			}*/
			/**
			public int estimateNPanelsNeeded(MultiChannelWrapper imp) {
				if (imp==null) {
					IssueLog.log("no multichannel image available. cannot estimate number of panels needed");
					return 1;
				}
				
				int out=0;
				int fs=imp.nSlices()*imp.nFrames();
				out=fs*(imp.nChannels());
				
				if (addsMergePanel()) out+=fs;
				
				for(int c=1; c<=imp.nChannels(); c++) {
					if (isChanPanExcluded(c)) out-=fs;
				}
				
				
				return out;
			}
			*/
			
			/**Estimates the number of channel panels that will be in the processed stack*/
			public int estimateChanColsNeeded(MultiChannelWrapper imp) {
				if (this.onlyMergePanel()) return 1;
				int out=0;
				int fs=1;
				out=fs*(imp.nChannels());
				
				if (addsMergePanel()) out+=fs;
				
				for(int c=1; c<=imp.nChannels(); c++) {
					if (isChanPanExcluded(c)) out-=fs;
				}
				
				return out;
			}
			
			
			/**Estimates the number of rows and columns of a
			 * grid layout needed to display the panels for 
			 * the given image*/
			public int[] estimateBestMontageDims(MultiChannelWrapper image) {
				
				if (image==null) {
					IssueLog.log("No multichannel image available");
					return new int[]{1,1};
				}
				
				/**What is done if multiple channels are represented*/
				if (image.nChannels()>1 && !onlyMergePanel()) {
					int chanCols = estimateChanColsNeeded(image);
					int nSF=image.nFrames()*image.nSlices();
					
					/**what to do if the number of channel columns needed is above the desired total number of cols*/
					if(this.idealColNum<chanCols) {
						int rows = (chanCols*nSF)/idealColNum;
						int extra=(chanCols*nSF)%idealColNum;
						if(extra>0) rows++;
						return new int[] {rows, idealColNum};
					}
					
					return new int[] {nSF, chanCols}
					;}
				
				/**What to do if both frames and z slices are represented*/
				if ((image.nChannels()==1||this.onlyMergePanel())&&image.nSlices()>1&&image.nFrames()>1) {return new int[] {image.nFrames(),image.nSlices()};}
				
				if ((image.nChannels()==1||this.onlyMergePanel())&& image.nFrames()==1) {return gridFor(image.nSlices());};
				if ((image.nChannels()==1||this.onlyMergePanel())&& image.nSlices()==1) {return gridFor(image.nFrames());};
				
				
				return new int[]{1,1};
			}
			
			public int estimageNPanels(MultiChannelWrapper image) {
				int[] in = estimateBestMontageDims(image);
				return in[0]*in[1];
			}
			
			
			/**when given a number of panels containing slices or frames, determines a grid dimension for them*/
			public int[] gridFor(int i) {
				if (i<=idealColNum) return new int[] {1,i};
				
				return new int[] {1+i/idealColNum,idealColNum};
					
			}
			
			public int[] estimateBestMontageDims(ArrayList<MultiChannelWrapper> images) {
				
				
				
				if (images.size()==0) return new int[]{1,1};
				int[] output=estimateBestMontageDims(images.get(0));
				for(int i2=1; i2<images.size(); i2++) {
					int[] addition=estimateBestMontageDims(images.get(i2));
					if (this.onlyMergePanel()){output[1]=output[1]+addition[1];} else {output[0]=output[0]+addition[0];}
					if (addition[1]>output[1]) output[1]=addition[1];
					
					
				}
				
				
				if (this.onlyMergePanel()) {
					return this.gridFor(images.size());
					//return new int[] {1,};
				}
				return output;
			}
			
			
			
			/***/
			public void setDimensionForPanels(BasicMontageLayout p,ArrayList<MultiChannelWrapper> image) {
				int[] dims=estimateBestMontageDims(image);
				int col=dims[1];
				int row=dims[0];
				if(p.rowmajor) {
					p.setNColumns(col);
					p.setNRows(row);
				}
				else {
					p.setNColumns(row);
					p.setNRows(col);
				}
				
			}
			
			
			/**not yet implemented. swaps channels */
			public void onChannelSwap(int c1, int c2) {
				/**IssueLog.log("Swap for excluded and includeded chans");
				ArrayList<Integer> t = excludedChannelPanels;
				int i1 = t.indexOf(c1);
				int i2= t.indexOf(c1);
				if(i1<0&&i2<0) return;*/
				
				getChanPanelReorder().swap(c1, c2);
			}
			
			public ChannelPanelReorder getChanPanelReorder() {
				if (reorder==null) {
					reorder=new ChannelPanelReorder();
				}
				return reorder;
			}
			
			public static class ChannelPanelReorder implements Serializable {
				private static final long serialVersionUID = 1L;
				static int[] defaultOrder=new int[] {0,1,2,3,4,5,6,7,8,9,10};
				private ArrayList<Integer> currentOrder;
				
				public ChannelPanelReorder() {
					ArrayList<Integer> order = new ArrayList<Integer>();
					for(int i: defaultOrder) {order.add(i);}
					this.currentOrder=order;
				}
				public ChannelPanelReorder(ArrayList<Integer> order) {
					this.currentOrder = new ArrayList<Integer>();
					currentOrder.addAll(order);
				}
				void swap(int c1, int c2) {
					new ArraySorter<Integer>().swapObjectPositionsInArray(c1, c2, currentOrder);
				//IssueLog.log("Order is "+currentOrder);
				}
				public int index(int c) {
					return currentOrder.indexOf(c);
				}
				public void setOrder(ArrayList<Integer> order) {
					currentOrder = new ArrayList<Integer>();
					currentOrder.addAll(order);
					for(int i: defaultOrder) {
						if(!currentOrder.contains(i))
							currentOrder.add(i);
						}
					}
				public void setOrder(ChannelPanelReorder chanPanelReorder) {
					setOrder(chanPanelReorder.currentOrder);
				}
			}

			public void clearChannelReorder() {
				reorder=null;
			}

}

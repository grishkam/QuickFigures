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
 * Version: 2022.1
 */
package channelMerging;

import java.io.Serializable;
import java.util.ArrayList;

/**The class stores information about which frames and slices
  from a multidimensional image are used for the figure*/
public abstract class SubStackSelectionInstructions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final int ALL_=0, SINGLE_=1, MULTIPLE_SELECTED_=2;

	
	int method=ALL_;
	/**Stores a list of selected indices*/
	public ArrayList<Integer> selected=new ArrayList<Integer>();
	
	public SubStackSelectionInstructions(int... s) {
		setSelected(s);
	}
	
	
	/**creates an identical copy*/
	public abstract SubStackSelectionInstructions duplicate() ;
	
	
	/**creates a copy that is set up with the same method of selection as this one */
	public abstract SubStackSelectionInstructions createDouble();
	
	/**returns how many frames or slices of the image are to be used in the figure*/
	public abstract int estimateNUsed(MultiChannelImage image) ;
	
	/**returns true if the item at index t is excluded*/
	public boolean isExcluded(int t) {
		if(selectsAll()) return false;
		return !selectedIndices().contains(t);
	}


	
	/**returns the list of selected indices.
	 * Always returns at least one index*/
	public ArrayList<Integer> selectedIndices() {
		if (selected==null||selected.isEmpty()) {
			 selected=new ArrayList<Integer>();
			 selected.add(1);
		}
		return selected;
	}
	
	/**returns the list of selected indices as a String*/
	public String selectedString() {
		String output = selectedIndices().toString();
		if (this.selectsAll())return "all";	
		output=output.replace("[", "");
		output=output.replace("]", "");
		return output.trim();
	}
	
	/**Sets the selected slide numbers, if null, will use all of them*/
	public void setSelected(int... i) {
		if(i==null||i.length==0) {
			method=ALL_;
			selected=null;
			return;
		}
		if (i.length==1) method=SINGLE_; else method=MULTIPLE_SELECTED_;
		selected=new ArrayList<Integer>();
		for(int num:i) {selected.add(num);
			}
	}

	/**Sets the selected slide numbers, if null, will use all of them*/
	public void resetSelectedIndex(ArrayList<Integer>i) {
		if(isInvalid(i)) {
			method=ALL_;
			selected=null;
			return;
		}
		if (i.size()==1) method=SINGLE_; 
			else method=MULTIPLE_SELECTED_;
		selected=new ArrayList<Integer>();
		for(int num:i) {
			if (!selected.contains(num))
				selected.add(num);
			}
		
	}

	/**
	returns true if the given int array is not appropriate for identifying slices
	 */
	public boolean isInvalid(ArrayList<Integer> i) {
		
		if (i==null||i.size()==0) return true;
		if (allZero(i)) return true;
		return false;
	}
	
	/**
	returns true if all of the numbers in the list are 0 or below
	 */
	private boolean allZero(ArrayList<Integer> i) {
		for(Integer number:i) {
			if (number>0) return false;
		}
		return true;
	}


	/**
	returns the index of the first slice or frame to be used
	 */
	public int getFirstIndex() {
		if (this.selectsAll()) return 1;
		if(this.selectsSingle()) return this.selectedIndices().get(0);
		if (isInvalid(this.selectedIndices()))
			return 1;
		return findLowest(selectedIndices());
	}
	
	/**
	 returns the lowest number in the list
	 */
	private int findLowest(ArrayList<Integer> selectedIndices) {
		int output=Integer.MAX_VALUE;
		for(int i:selectedIndices) {
			if (i<output) output=i;
		}
		return output;
	}


	/**modifies the argument from another set of instructions to match this one
	  required in order for a second image added to a figure to be treated like the previous ones*/
	public void giveBasicTraitsTo(SubStackSelectionInstructions output) {
		if(selectsAll()) output.setSelected(null);
		if(selectsSingle()) output.setSelected(1);
	}
	
	/**modifies the argument to make it a near copy of this*/
	public void giveAllTraitsTo(SubStackSelectionInstructions output) {
		if(selectsAll()) output.setSelected(null); else
		if(selectsSingle()) output.setSelected(selectedIndices().get(0));
		else {
			output.setSelectedIndex(selectedIndices());
		}
	}

	/**sets the selected indices*/
	public void setSelectedIndex(ArrayList<Integer> selectedIndices) {
		if(selectedIndices==null) {
			this.setSelected(null);
			return;
		}
		selected=new ArrayList<Integer>();
		selected.addAll(selectedIndices);
	}
	
	
	
	/**returns true if this targets a single index only*/
	public boolean selectsSingle() {
		return method==SINGLE_;
	}
	
	/**returns true if the instructions indicate not to select a specific dimension index*/
	public boolean selectsAll() {
		return method==ALL_;
	}
	
	
	
	/**subclass for selecting frames*/
	public static class FrameUseInstructions extends SubStackSelectionInstructions {

		public FrameUseInstructions(int... s) {
			super(s);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public int estimateNUsed(MultiChannelImage image) {
			if(method==ALL_) {return image.nFrames();}
			int count=0;
			for(int i: selectedIndices()) {
				if(i>0&&i<=image.nFrames()) count++;
			}
			
			return count;
		}

		/**Creates an equivalent instructions that can be used for another image
		   returns a non-identical copy of this*/
		@Override
		public FrameUseInstructions createDouble() {
			FrameUseInstructions output = new FrameUseInstructions(1);
			giveBasicTraitsTo(output);
			return output;
		}
		
		
		/** returns an identical copy of this*/
		@Override
		public FrameUseInstructions duplicate() {
			FrameUseInstructions output = new FrameUseInstructions(null);
			giveAllTraitsTo(output);
			return output;
		}
		
		/**If this specifies a single frame, alters the CSF location to target that same frame*/
		@Override
		public void setupLocation(CSFLocation d) {
			if(this.selectsSingle()&&selected.size()>0) d.frame=selected.get(0);
			
		}

		/**replaces one location with another. returns false if cannot perform replace
		 If the initial index is not present in the list, replace is not possible
		 */
		public boolean replaceIndex(CSFLocation f1, CSFLocation f2) {
			if (!canReplace(f1, f2))return false;
			int i = selected.indexOf(f1.frame);
			selected.set(i, f2.frame);
			return true;
		}
		/**returns false if cannot perform replace requested
		 If the initial index is not present in the list, replace is not possible
		 */
		private boolean canReplace(CSFLocation f1, CSFLocation f2) {
			if(f1==null||f2==null||selected==null) return false;
			if(!selected.contains(f1.frame)) return false;
			if(selected.contains(f2.frame)) return false;
			return true;
		}

		
	}
	
	/**subclass for selecting z slices*/
	public static class SliceUseInstructions extends SubStackSelectionInstructions {

		public SliceUseInstructions(int... s) {
			super(s);
		}
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int estimateNUsed(MultiChannelImage image) {
			if(method==ALL_) {return image.nSlices();}
			int count=0;
			for(int i: selectedIndices()) {
				if(i>0&&i<=image.nSlices()) count++;
			}
			
			return count;
		}	
		
		/**Creates an equivalent instructions that can be used for another image
		   returns a non-identical copy of this*/
		@Override
		public SliceUseInstructions createDouble() {
			SliceUseInstructions output = new SliceUseInstructions(1);
			giveBasicTraitsTo(output);
			return output;
		}

		/**If this specifies a single slice, alters the CSF location to target that same slice*/
		@Override
		public void setupLocation(CSFLocation d) {
			if(this.selectsSingle()&&selected.size()>0) d.slice=selected.get(0);
			
		}
		
		/**replaces one location with another. returns false if cannot perform replace
		 If the initial index is not present in the list, replace is not possible
		 */
		public boolean replaceIndex(CSFLocation f1, CSFLocation f2) {
			if (!canReplace(f1, f2))return false;
			int i = selected.indexOf(f1.slice);
			selected.set(i, f2.slice);
			return true;
		}
		/**returns false if cannot perform replace requested
		 If the initial index is not present in the list, replace is not possible
		 */
		private boolean canReplace(CSFLocation f1, CSFLocation f2) {
			if(f1==null||f2==null||selected==null) return false;
			if(!selected.contains(f1.slice)) return false;
			if(selected.contains(f2.slice)) return false;
			return true;
		}
		
		/** returns an identical copy of this*/
		@Override
		public SliceUseInstructions duplicate() {
			SliceUseInstructions output = new SliceUseInstructions(null);
			giveAllTraitsTo(output);
			return output;
		}

		

		
		
	}

	/**Alters the given CSF location to match the target slice/frame, 
	 * so that it reflects stack index targeted by this selection instructions
	 * if the instructions target more than one slice/frame, does nothing */
	public abstract void setupLocation(CSFLocation d);


	/**
	 creates a new slice use instructions
	 */
	public static SliceUseInstructions createSliceUseInstructions(ArrayList<Integer> newSlices) {
		SliceUseInstructions n = new  SliceUseInstructions(null);
		n.resetSelectedIndex(newSlices);
		return n;
	}


	/**
	  creates a new frame use instructions
	 */
	public static FrameUseInstructions createFrameUseInstructions(ArrayList<Integer> newFrame) {
		FrameUseInstructions n = new  FrameUseInstructions(null);
		n.resetSelectedIndex(newFrame);
		return n;
	}

	
}

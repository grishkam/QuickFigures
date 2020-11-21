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


	
	/**returns the list of selected indices*/
	public ArrayList<Integer> selectedIndices() {
		if (selected==null||selected.isEmpty()) {
			 selected=new ArrayList<Integer>();
			 selected.add(1);
		}
		return selected;
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

	/**Alters the CSF location to target, so that is shows the stack index targeted */
	public abstract void setupLocation(CSFLocation d);

	
}

package channelMerging;

import java.io.Serializable;
import java.util.ArrayList;

import logging.IssueLog;

public abstract class SubStackSelectionInstructions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int ALL_=0, SINGLE_=1, MULTIPLE_SELECTED_=2;

	
	int method=ALL_;
	public ArrayList<Integer> selected=new ArrayList<Integer>();
	
	public SubStackSelectionInstructions(int... s) {
		setSelected(s);
	}
	
	public abstract SubStackSelectionInstructions createDouble();
	
	
	public boolean isExcluded(int t) {
		if(selectsAll()) return false;
		return !selectedIndices().contains(t);
	}

	public boolean selectsAll() {
		return method==ALL_;
	}
	
	
	
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
	
	public void giveTraitsTo(SubStackSelectionInstructions output) {
		if(selectsAll()) output.setSelected(null);
		if(selectsSingle()) output.setSelected(1);
	}

	public boolean selectsSingle() {
		return method==SINGLE_;
	}
	
	
	public int estimateNUsed(MultiChannelWrapper image) {
		return 1;
	}
	
	
	
	public static class FrameUseInstructions extends SubStackSelectionInstructions {

		public FrameUseInstructions(int... s) {
			super(s);
			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public int estimateNUsed(MultiChannelWrapper image) {
			if(method==ALL_) {return image.nFrames();}
			int count=0;
			for(int i: selectedIndices()) {
				if(i>0&&i<=image.nFrames()) count++;
			}
			
			return count;
		}

		@Override
		public FrameUseInstructions createDouble() {
			FrameUseInstructions output = new FrameUseInstructions(1);
			giveTraitsTo(output);
			return output;
		}
		
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
		private boolean canReplace(CSFLocation f1, CSFLocation f2) {
			if(f1==null||f2==null||selected==null) return false;
			if(!selected.contains(f1.frame)) return false;
			if(selected.contains(f2.frame)) return false;
			return true;
		}

		
	}
	
	public static class SliceUseInstructions extends SubStackSelectionInstructions {

		public SliceUseInstructions(int... s) {
			super(s);
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int estimateNUsed(MultiChannelWrapper image) {
			if(method==ALL_) {return image.nSlices();}
			int count=0;
			for(int i: selectedIndices()) {
				if(i>0&&i<=image.nSlices()) count++;
			}
			
			return count;
		}	
		
		@Override
		public SliceUseInstructions createDouble() {
			SliceUseInstructions output = new SliceUseInstructions(1);
			giveTraitsTo(output);
			return output;
		}

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
		private boolean canReplace(CSFLocation f1, CSFLocation f2) {
			if(f1==null||f2==null||selected==null) return false;
			if(!selected.contains(f1.slice)) return false;
			if(selected.contains(f2.slice)) return false;
			return true;
		}
	}

	public abstract void setupLocation(CSFLocation d);

	
}

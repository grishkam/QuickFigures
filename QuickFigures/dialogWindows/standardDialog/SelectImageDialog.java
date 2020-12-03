package standardDialog;

import java.util.ArrayList;

import appContext.CurrentAppContext;
import channelMerging.MultiChannelImage;
import logging.IssueLog;

/**A dialog for selecting one or more open multidimensional images*/
public class SelectImageDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**The choices*/
	private ArrayList<MultiChannelImage> optioins;



	private boolean includeChans=true;

	private int num=0;
	{this.setModal(true);}
	
	/**builds a dialog*/
	public SelectImageDialog( ArrayList<MultiChannelImage> ops, boolean includeChans, int numb) {
		this.setWindowCentered(true);
		this.num=numb;
		this.includeChans=includeChans;
		if (ops.size()==0) {
			IssueLog.log("not options");
			this.add("Info", new InfoDisplayPanel("Problem:","no image is open"));
			return;
		}
		String[] s=new 	String[ops.size()+1];
		this.optioins=ops;
		for(int i=0; i<ops.size(); i++) {
			//IssueLog.log("got title "+optioins.get(i).getTitle());
			s[i+1]=(this.optioins.get(i).getTitle());
			
		}
		s[0]="none";
		for(int i=1; i<=num; i++) {
			int innitial=i;
			if(i>2) innitial=0;
			this.add(""+i, new ComboBoxPanel("Image "+i, s, innitial));
			}
		
		if (this.includeChans)
			addChannelCheckBoxes(optioins.get(0));
	}
	
	
	
	
	public static void main(String[] arg) {
		new SelectImageDialog(new ArrayList<MultiChannelImage> (), true,4).showDialog();;
	}
	
	
	/**returns a list of the multichannel images that were selected*/
	public ArrayList<MultiChannelImage> getList() {
		ArrayList<MultiChannelImage> listoutput=new ArrayList<MultiChannelImage> ();
		
		for(int  i=1; i<=num; i++) try {
			 int index = this.getChoiceIndex(""+i);
			 
			 if (index>0) {
				 MultiChannelImage item = this.optioins.get(index-1);
				 if (!listoutput.contains(item)) 
					 listoutput.add(item);
				 }
			
			
		} catch (Throwable t) {
			t.printStackTrace();
			IssueLog.logT(t);
		}
		IssueLog.log("got list of "+listoutput.size()+" out of a possible " +num);
		return listoutput;
		
	}
	
	/**returns a list of the multichannel images that are open in the application*/
	protected static	ArrayList<MultiChannelImage>  getAvailableMultis() {
		return  CurrentAppContext.getMultichannelContext().getallVisibleMultichanal();
	}
	
	/**shows a dialog and returns it.*/
	public static SelectImageDialog getSelectedMultis(boolean includeChans, int num) {
		ArrayList<MultiChannelImage> multis =getAvailableMultis();
		
		SelectImageDialog sd = new SelectImageDialog(multis, includeChans,num);
		sd.showDialog();;
		return sd;
	}
}

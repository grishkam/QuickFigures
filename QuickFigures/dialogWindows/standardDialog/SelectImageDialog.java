package standardDialog;

import java.util.ArrayList;

import javax.swing.JLabel;

import appContext.CurrentAppContext;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelWrapper;
import logging.IssueLog;

public class SelectImageDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int rotated=0, scales=1, sheared=2, translate=3;

	private int type;

	private ArrayList<MultiChannelWrapper> optioins;

	private ArrayList<ChannelEntry> channelEnt;

	private boolean includeChans;

	private int num=0;
	{this.setModal(true);}
	
	public SelectImageDialog( ArrayList<MultiChannelWrapper> ops, boolean includeChans, int numb) {
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
		
		if (includeChans)addChannelCheckBoxes(optioins.get(0));
	}
	
	
	
	
	public static void main(String[] arg) {
		new SelectImageDialog(new ArrayList<MultiChannelWrapper> (), true,4).showDialog();;
	}
	
	
	public ArrayList<MultiChannelWrapper> getList() {
		ArrayList<MultiChannelWrapper> listoutput=new ArrayList<MultiChannelWrapper> ();
		
		for(int  i=1; i<=num; i++) try {
			 int index = this.getChoiceIndex(""+i);
			 
			 if (index>0) {
				 MultiChannelWrapper item = this.optioins.get(index-1);
				 if (!listoutput.contains(item)) listoutput.add(item);
				 }
			
			
		} catch (Throwable t) {
			t.printStackTrace();
			IssueLog.log(t);
		}
		IssueLog.log("got list of "+listoutput.size()+" out of a possible " +num);
		return listoutput;
		
	}
	
	protected static	ArrayList<MultiChannelWrapper>  getAvailableMultis() {
		return  CurrentAppContext.getMultichannelContext().getallVisibleMultichanal();
	}
	
	public static SelectImageDialog getSelectedMultis(boolean includeChans, int num) {
		ArrayList<MultiChannelWrapper> multis =getAvailableMultis();
		
		SelectImageDialog sd = new SelectImageDialog(multis, includeChans,num);
		sd.showDialog();;
		return sd;
	}
}

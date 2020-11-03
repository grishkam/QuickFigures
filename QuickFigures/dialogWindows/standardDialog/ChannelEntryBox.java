package standardDialog;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComboBox;
import channelMerging.ChannelEntry;


public class ChannelEntryBox extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//static Color[] segColors=new Color[] {Color.red, Color.green, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.GRAY};
	ArrayList<ChannelEntry> theChannelentries;
	
	String nullString="none";
	
	public ChannelEntryBox(ArrayList<ChannelEntry> cl) {
		this(cl, "none");
	}
	
	
	
	public ChannelEntryBox(ArrayList<ChannelEntry> cl, String zeroText) {
		super(namesOfEach(cl, zeroText));
		this.nullString=zeroText;
		this.theChannelentries=cl;
		ChannelColorCellRenerer cc = new ChannelColorCellRenerer(this);
		cc.setBox(this);
		cc.setFont(cc.getFont().deriveFont((float)20));
		this.setRenderer(cc);
	}
	
	public static String[] namesOfEach(ArrayList<ChannelEntry> theChannelentries, String zeroText) {
		String[] names = new String[theChannelentries.size()+1];
		names[0]=zeroText;
		for(int i=1;i<=names.length-1&&i-1<theChannelentries.size(); i++) {
			names[i]=getUsedChanName(theChannelentries.get(i-1), zeroText);//.getRealChannelName();
			
		}
		return names;
	}
	
	public static String getUsedChanName(ChannelEntry c1, String zeroText) {
		String st = zeroText; 
		if (c1!=null) {
			st=c1.getRealChannelName();
			if (st==null) st=c1.getLabel();
			if (st==null) st="Ch "+c1.getOriginalChannelIndex();
		}
		return st;
	}
	
	
	public ChannelEntryBox(int innitial, ArrayList<ChannelEntry> cl) {
		this(cl);
		
		this.setSelectedIndex(innitial);
	}
	
	//public static void indexOfValue(String value, ) {}
	
	
	public void drawRainbowString(Graphics g, ChannelEntry c1, int x, int y) {
		drawRainbowString(g,c1, x,y, nullString);
		
	}
	
public static void drawRainbowString(Graphics g, ChannelEntry c1, int x, int y, String nullString) {
		
		//ArrayList<String> stringarr = ComplexTextGraphic .splitStringBasedOnArray(st, ints);
		String st = nullString; 
		if (c1!=null) {
			st=c1.getRealChannelName();
			if (st==null) st=c1.getLabel();
			if (st==null) st="Ch "+c1.getOriginalChannelIndex();
		}
		
		 {
			
			FontMetrics fm = g.getFontMetrics();
			if (c1!=null) {
				g.setColor(c1.getColor());
				g.drawString(st, x, y);
			} else {
				g.setColor(Color.black);
				g.drawString(nullString, x, y);
			}
			
		}
		
	}
	
	
	/*
	public static void main(String[] arg) {
		JFrame jf = new JFrame();
		;
		ImagePlusWrapper wrap = new ImagePlusWrapper(IJ.openImage());
		
		jf.add(new ChannelEntryBox(2,wrap.getChannelEntriesInOrder()));
		jf.pack();jf.setVisible(true);
	}*/

}

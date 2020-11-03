package panelGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import standardDialog.ChannelEntryBox;

/**Renders the channel color cells in a channel selection JList*/
public class ChannelColorCellRenerer2 extends DefaultListCellRenderer {
	
	/**
	 * 
	 */
	
	/**
	 * 
	 */
	public int channelNumber=0;
	private ChannelListDisplay box;
	public int theindex=-1;
	
	private static final long serialVersionUID = 1L;
	
	public ChannelColorCellRenerer2(ChannelListDisplay channelEntryBox) {
		box = channelEntryBox;
	
	}


	public void paint(Graphics g) {
		if(theindex==-1) theindex=box.getSelectedIndex();
		int dim=theindex;
		if (dim==-1) {dim=theindex;}
		
		
		
		if (box.elements.size()>0&&dim>-1) {
			if(box.getSelectedIndex()==dim) {
			g.setColor(Color.blue);
			g.fillRect(2, 2, (int)g.getFontMetrics().getStringBounds(box.elements.get(dim).getRealChannelName(), g).getWidth(), g.getFont().getSize());
			}
			ChannelEntryBox.drawRainbowString(g, box.elements.get(dim), 1,this.getFont().getSize()+1, "none");
			
		}
		else {ChannelEntryBox.drawRainbowString(g, null, 1,this.getFont().getSize()+1, "none");}
		
	}
	
	public  Component	getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		theindex=index;
		Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (out instanceof ChannelColorCellRenerer2) {
			ChannelColorCellRenerer2 c=(ChannelColorCellRenerer2) out;
			c.channelNumber=theindex;
			//	{this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont((float)20.0));}
			if (cellHasFocus) {
				c.channelNumber=theindex;
	
				}
		}
	
		//Font font=new Font(out.getFont().getFamily(), index, out.getFont().getSize());
		//out.setFont(font);
		return out;
			}

	public ChannelListDisplay getBox() {
		return box;
	}

	public void setBox(ChannelListDisplay box) {
		this.box = box;
		this.channelNumber=box.getSelectedIndex();
	}
}
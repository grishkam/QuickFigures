package standardDialog;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;

import menuUtil.SmartPopupJMenu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class ColorComboBox extends JComboBox implements ColorListChoice{
	public int rainbowIndex=Integer.MAX_VALUE-2;
	{this.setRenderer(new ColorCellRenderer(this));}
	/**
	 * 
	 * 
	 */
	//{this.getJ}
	private Color selectedColor=Color.black;
	private static final long serialVersionUID = 1L;
	ArrayList<Color> colors=new ArrayList<Color> ();
	
	public ColorComboBox(ArrayList<Color> c) {
		
		for(Color ci:c) {addColor(ci);}
	}
	
public ColorComboBox(ArrayList<Color> c, int rainbowInd) {
		int i=0;
		rainbowIndex=rainbowInd;
		for(Color ci:c) {
			addColor(ci);
			i++;
			if (i==rainbowInd) addColor(new Color(3,3,3,3));
			}
		
		
		
	}
	
	public void addColor(Color ci) {
		this.addItem(ci);
		colors.add(ci);
		
	}
	
	public void addUserColor(Component c) {
		Color nc = JColorChooser.showDialog(c, "Color", this.getSelectedColor());
		if (nc==null) return;
		
		addColor(nc);
		selectedColor=nc;
		this.setSelectedIndex(colors.size()-1);
	}
	
    public void setSelectedIndex(int anIndex) {
        if (anIndex==this.getRainbow()) {
        	addUserColor(this);
        }
        else super.setSelectedIndex(anIndex);
    }


	
	public static void main(String[] args) {
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		JButton button = new JButton("button");
		ff.add(button);
		ArrayList<Color> ac = new ArrayList<Color> ();
		{
		ac.add(Color.blue);
		//ac.add(null);
		ac.add(new Color(20,20,20, 0));
		ac.add(Color.gray);
		ac.add(Color.red);
		//ac.add(null);
		for(Color c: ColorComboboxPanel.standardC) 
		ac.add(c);
		for(Color c: ColorComboboxPanel.standardC) 
		ac.add(c);
		
		}
		ColorComboBox sb = new ColorComboBox(ac, 4);
		
		ff.add(sb);
		
	
	/**JList jl = sb.createJList();
		jl.setCellRenderer(new colorCellRenerer(sb));
		menuWithJList menwl = new menuWithJList(jl);*/
		//button.addMouseListener(menwl);
		ff.pack();
		
		ff.setVisible(true);
	}
	
	public Color getSelectedColor() {
		if (this.getSelectedIndex()==-1) {
			this.setSelectedIndex(0);
			selectedColor=colors.get(0);
		} else
		 selectedColor=colors.get(this.getSelectedIndex());
		if (getSelectedIndex()==rainbowIndex) return null;
		return selectedColor;
	}

	public int getWidth() {
		return 60;
	}
	
	public int getHeight() {
		return 20;
	}
	public Dimension getPreferedSize() {
		
		return new Dimension(70,30);
		
	}
	
	@Override
	public void paint(Graphics g) {
		if (g instanceof Graphics2D) {
			
			Graphics2D g2 = (Graphics2D) g;
			Rectangle r=new Rectangle(0, 0, this.getWidth(),  this.getHeight());
			
			if (getSelectedColor()!=null)
		g2.setPaint(this.getSelectedColor());
			//else g2.setPaint(ColorCellRenderer.getRaindowGradient(r));
		
		
		
		g2.fill(r);
		if (getSelectedColor()==null||getSelectedColor().getAlpha()==0) {
			g2.setColor(Color.white);
			g2.fill(r);
			ColorCellRenderer.drawXAcrossREct(g2, r);
		}
		
		g2.setStroke(this.getBorderStroke());
		g2.setColor(Color.DARK_GRAY);
			g2.draw(r);
		}
	//	g.setColor(Color.darkGray);
		
		
	}

	private Stroke getBorderStroke() {
		BasicStroke st=new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		// TODO Auto-generated method stub
		return st;
	}

	@Override
	public List<Color> getColors() {
		// TODO Auto-generated method stub
		return colors;
	}
	
	
	public static class menuWithJList extends SmartPopupJMenu implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JList list1;
		public menuWithJList(JList list) {
			list1=list;
			add(list1);
			pack();
			
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			Component c = arg0.getComponent();
			show(c, arg0.getX(), arg0.getY());
			
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}


	@Override
	public int getRainbow() {
		return rainbowIndex;
	}

	
	
	}




	//public void 



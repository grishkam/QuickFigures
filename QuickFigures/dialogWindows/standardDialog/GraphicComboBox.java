package standardDialog;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_BasicShapes.SimpleGraphicalObject;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;

/**a combo box that lets the user choose various graphical objects*/
public class GraphicComboBox extends JComboBox implements UserSelectable{
	
	/**
	 * 
	 */
	
	{this.setRenderer(new colorCellRenerer());}
	private SimpleGraphicalObject selectedColor=null;
	private static final long serialVersionUID = 1L;
	ArrayList<SimpleGraphicalObject> colors=new ArrayList<SimpleGraphicalObject> ();
	
	public GraphicComboBox(ArrayList<? extends SimpleGraphicalObject> c) {
		addItem(getNullComponent() );
		for(SimpleGraphicalObject ci:c) {this.addItem(ci);colors.add(ci);}
	}
	
	public Object getSelectedItem() {
		if (this.getSelectedIndex()==0) return null;
		else return super.getSelectedItem();
	}
	


	public static void main(String[] args) {
		JFrame ff = new JFrame("frame");
		//ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		ArrayList<SimpleGraphicalObject> ac = new ArrayList<SimpleGraphicalObject> ();
		{ac.add(new TextGraphic(""));
		RectangularGraphic rect =RectangularGraphic.filledRect( new Rectangle(0,0,20,35));
		rect.setStrokeColor(Color.blue);
		rect.setStrokeWidth(10);
		
		ac.add(rect);
		ac.add(new TextGraphic("none"));}
		GraphicComboBox sb = new GraphicComboBox(ac);
		ff.add(sb);
		ff.pack();
		
		ff.setVisible(true);
		ff.pack();
	}
	
	public SimpleGraphicalObject getSelectedGraphicalObject() {
		int i=this.getSelectedIndex();
		if (i<1) return null;
		 selectedColor=colors.get(this.getSelectedIndex()-1);
		return selectedColor;
	}
	
	

	
	@Override
	public void paint(Graphics g) {
		//super.paintComponent(g);
		g.setColor(Color.white);
		//g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
	
	
		 getCurrentDisplaycomp() .paintComponent(g);
		//new GraphicObjectDisplayBasic<simpleGraphicalObject>();
	
		
		//simpleGraphicalObject zz = getSelectedColor();
		
		
		//if (zz!=null) zz.draw((Graphics2D) g,new BasicCordinateConverter(0,0,1));
		
		//if (getSelectedColor()!=null) this.getSelectedColor().draw((Graphics2D) g, new BasicCordinateConverter());
	}
	
	
	public int getWidth() {
		return  200;
	}
	
	public int getHeight() {
		return  80;
	}
	
	
	
	
	class colorCellRenerer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public Color currentColor=Color.black;

		public  Component	getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			
				try {
					
					/**simpleGraphicalObject item = colors.get(index);
					if (index==0) item=new TextGraphic("none");
					String s="none";
					if (item!=null) s=item.toString();
					
					
					GraphicDisplayComponent com = new GraphicDisplayComponent(s,item,  isSelected);
					*/
					return getDisplayedForIndex(index,isSelected);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return out;
				}
				
				//	out.setBackground(AbstractSavedDataServiceGM.getColor(value.toString()));
				//((BasicComboBoxRenderer) out).setText("    ");
				//out.setForeground(AbstractSavedDataServiceGM.getColor(value.toString()));
				//if (index<colors.size()&&index>-1)out.setForeground(colors.get(index));
			
			
				}
	
		
		boolean selected=false;
		
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.darkGray);
			if (selected) {
				g.fillRect(0, 0, 25, this.getHeight());
			}
			
			
			g.setColor(this.getForeground());
			g.fillRect(20, 0, this.getWidth()-20,  this.getHeight());
			//GraphicDisplayComponent com = new GraphicDisplayComponent(null,colors.get(getSelectionNumber()),  false);
			getSelectedDC().paintComponent(g);
			
			
		}
		
		
		
		 /**	public Dimension getPreferredSize() {
		 GraphicDisplayComponent dc = getSelectedDC();
			  if (dc==null)   return new Dimension(20,15);
			 return (getSelectedDC().getPreferredSize());
		
			 return new Dimension(30,25);

		    } */
	}
	
	private GraphicDisplayComponent getSelectedDC() {
		
		return new GraphicDisplayComponent(null,getSelectedGraphicalObject(),  false);
		
	}
/**
	@Override
	 public Dimension getPreferredSize() {
		 GraphicDisplayComponent dc = getSelectedDC();
		 Dimension output;
		 dc=null;
		  if (dc==null) output= new Dimension(30,25);
		  output=(getSelectedDC().getPreferredSize());
	 
		  return output;
	    }*/
	 
	 
	@Override
	public int getSelectionNumber() {
		// TODO Auto-generated method stub
		return super.getSelectedIndex();
	}
	
	@Override
	public void setSelectionNumber(int index) {
		super.setSelectedIndex(index);
		
	}
	
	boolean usename=false;
	
	public GraphicDisplayComponent  getCurrentDisplaycomp() {
		return getDisplayedForIndex(getSelectedIndex(), false);
	}
	
	
	private TextGraphic getNullComponent() {
		TextGraphic tg = new TextGraphic("no item selected");
	
		tg.setFont(tg.getFont().deriveFont((float)10));
		return tg;
	}
	
	public GraphicDisplayComponent getDisplayedForIndex(int index, boolean isSelected) {
		SimpleGraphicalObject item =null;
		if (index>0) item=colors.get(index-1);
		if (index<=0) {
			
		
			item=getNullComponent();
		}
		
		String s="none";
		
		if (item!=null) s=item.toString();
		if (!usename) s=null;
		
		GraphicDisplayComponent com = new GraphicDisplayComponent(s,item,  isSelected);
		com.setCurrentItemInsets(new Insets(6,6,6,6));
		return com;
	}

	
	
	
	//public void 

}
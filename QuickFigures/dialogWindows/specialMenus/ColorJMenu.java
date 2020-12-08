/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package specialMenus;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import menuUtil.SmartPopupJMenu;
import standardDialog.colors.ColorCellRenderer;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;
import standardDialog.colors.ColorListChoice;
import menuUtil.PopupCloser;

/**A menu that is used to pick colors. Color rectangles appear in the menu instead of normal menu items*/
public class ColorJMenu extends SmartPopupJMenu implements ColorListChoice,  MouseListener, ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Color> colors=new ArrayList<Color>(); 
	int i=0;

	
	ArrayList<ColorInputListener> listens=new ArrayList<ColorInputListener> ();
	JList<?> thelist;
	private int rainbowIndex=1000;//the index at which a special menu item that is drawn as a rainbow will appear
	
	
	public ColorJMenu(Color[] standard) {
		
		for(Color c:standard) {colors.add(c);}
		 {
			 thelist=createJList();
		 add(thelist);
		 }
	}
	
	
	
	
	
	
	public void addColorInputListener(ColorInputListener lsiten) {
		if(listens.contains(lsiten)) return;
		listens.add(lsiten);
	}
	
	public void notifyListens() {
		for(ColorInputListener listen: listens) {
			listen.ColorChanged(new ColorInputEvent(null, this, getSelectedColor()));
		}
	}
	
	

	
	@Override
	public List<Color> getColors() {
		// TODO Auto-generated method stub
		return colors;
	}

	@Override
	public Color getSelectedColor() {
		if (i<colors.size()) return colors.get(i);
		return Color.WHITE;
	}
	
	

	public JList<?> createJList() {
		
		Vector<Color> x = new Vector<Color> ();
		x.addAll(colors);
		JList<?> out = new JList<Color>(x);

		out.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		ColorCellRenderer cc = ColorCellRenderer.getPalleteRenderer(this);
		
		out.setCellRenderer(cc);
		
		int rows=out.getVisibleRowCount();
		int cols=1;
		if (rows>x.size()) rows=x.size();
		int maxRow=20;
		if (rows>maxRow) {
			
			cols=rows/maxRow;
			rows=maxRow;
		}
		
		
		
		out.setFixedCellWidth((cc.getPreferredSize().width+20)*cols);
		
		out.addListSelectionListener(this);
		
		return out;
	}
	
	
	
	
	public static void main(String[] args) {
		
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		JButton button = new JButton("button");
		ff.add(button);
		
		ColorJMenu cjm = new ColorJMenu(standardLutColors);//new Color[] {Color.red, Color.green, Color.blue});
		cjm.pack();
		button.addMouseListener(cjm);
		
		
		ff.pack();
		
		ff.setVisible(true);
		
		
		
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
		// TODO Auto-generated method stub
		new PopupCloser(this);
		show(arg0.getComponent(), arg0.getX(), arg0.getY());
		
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	Object lastValue=null;



	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		
		Object ob = thelist.getSelectedValue();
		if ((ob instanceof Color)&&!ob.equals(lastValue)) {
			i=colors.indexOf(ob);
			this.notifyListens();
			this.setVisible(false);
			lastValue=ob;//to avoid consecutively calling for the same color
		} else return;
		
	}
	
	
	 static Color[] standardLutColors=new Color[] {Color.red, Color.GREEN, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.white, Color.black};
		
		public static JPopupMenu getStandardColorJMenu(ColorInputListener t) {
			
			ColorJMenu colors= new ColorJMenu(standardLutColors); 
			
			colors.addColorInputListener(t);
			
			return colors;
		
			
			
		}






		@Override
		public int getRainbow() {
			return rainbowIndex;
		}
}

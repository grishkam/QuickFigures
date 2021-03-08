/**
 * Author: Greg Mazo
 * Date Modified: Jan 17, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package standardDialog.choices;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.junit.Test;

import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.SimpleGraphicalObject;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import standardDialog.StandardDialog;
import testing.VisualTest;

/**
 
 * 
 */
public class GraphicComboBoxTest extends VisualTest {

	/**displays a window with a combo box, user must click on it to confirm that appearance and function are normal*/
	@Test
	public void test() {
		
			JFrame ff = new JFrame("frame");
			ff.add(new JButton("button"));
			ArrayList<SimpleGraphicalObject> ac = new ArrayList<SimpleGraphicalObject> ();
			{ac.add(new TextGraphic("An obect"));
			
			RectangularGraphic rect =RectangularGraphic.filledRect( new Rectangle(0,0,20,35));
			rect.setStrokeColor(Color.blue);
			rect.setStrokeWidth(10);
			ac.add(rect);
			
			/**to confirm that the shape is seen in the icon even if the location is far from origin*/
			rect =CircularGraphic.blankOval(new Rectangle(120,100,40,35), Color.green, CircularGraphic.CHORD_ARC);
			rect.setStrokeWidth(5);
			ac.add(rect);
			
			ac.add(new BarGraphic());
			
			ac.add(new TextGraphic("this is a text item"));}
			
			GraphicComboBox sb = new GraphicComboBox(ac, Color.orange);
			ff.add(sb);
			ff.pack();
			
			ff.setVisible(true);
			StandardDialog.center(ff);
			ff.pack();
			
			comboBoxVisualTest(ff, sb);
			
			ff.setVisible(false);
	}

	

}

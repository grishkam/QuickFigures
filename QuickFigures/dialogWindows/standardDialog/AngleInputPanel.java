package standardDialog;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class AngleInputPanel extends NumberInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   AngleBox angle=new AngleBox(); {angle.addMouseMotionListener(this);field.setDecimalPlaces(3);field.setColumns(7);}
   
   {includeSlider=false; }
   
   public AngleInputPanel(String name, double angle, boolean includeField) {
	   this.label.setText(name);
	   this.setNumber(angle);
	   this.includeField=includeField;
		this.originalStatus=angle;//the angle should be in radians
   }
   
   @Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx=x0;
		gc.gridy=y0;
		gc.insets=firstInsets;
		gc.anchor = GridBagConstraints.EAST;
		jp.add(label, gc);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx++;
		
		
		
			gc.insets=middleInsets;
			jp.add(getPanelForContents(angle, field), gc);
			gc.gridx++;
		
		//gc.insets=lastInsets;
		//if (this.includeField)  jp.add(field, gc);
		
	}
   
  
   
   @Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getSource()==field) {
			//slider.setValue((int)field.getNumberFromField());
			number=field.getNumberFromField()/(180/Math.PI);
			angle.setAngle(number);
			angle.repaint();
			notifyListeners(new NumberInputEvent(this, field, number) );
			this.repaint();
		}
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (arg0.getSource()==angle) {
			if (field!=null)
			field.setNumber(angle.getAngle()*180/Math.PI);
			if (angle!=null)
			this.number=angle.getAngle();
			notifyListeners(new NumberInputEvent(this, slider, number) );
			this.repaint();
		}
		
	}

   
   
	public void setNumber(double d) {
		
		number=d;
		if (field!=null)field.setNumber(d*180/Math.PI);
		if (angle!=null)angle.setAngle(d);
	}
	public double getNumber() {
		return number;
	}
	
}

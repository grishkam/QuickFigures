package dataTableDialogs;

import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import dataSeries.Basic1DDataSeries;
import standardDialog.GriddedPanel;
import standardDialog.StandardDialog;

public class DataInputDialog extends StandardDialog{

	/**
	 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea area;
	
	public DataInputDialog(String starting) {
		super("Input Data", true);
		GriddedPanel panel = this.getMainPanel();
		
			this.setTabName("Data");
		
		area=new JTextArea(20, 60);
		area.setText(starting);
    	JScrollPane pane = new JScrollPane(area);
    
    	panel.add(pane, super.getCurrentConstraints());
    
	}
	
	public static Basic1DDataSeries getUserData(String starting) {
		DataInputDialog d = new DataInputDialog(starting);
		d.showDialog();
		
		if (d.wasOKed()) {
			ArrayList<Double> nums = d.getNumbers();
			ArrayList<String> text = d.getNonNumbers();
			String dataName="data";
			if (text.size()>0) dataName=text.get(0);
			return new Basic1DDataSeries(dataName, nums);
		} 
		return null;
	}
	
	ArrayList<Double> getNumbers() {
		ArrayList<Double> output=new ArrayList<Double>();
		String t = area.getText();
		String[] lines = t.split(""+'\n');
		for(String line: lines) try {
			output.add(Double.parseDouble(line));
		} catch (Throwable ex) {}
		
		return output;
	}
	
	ArrayList<String> getNonNumbers() {
		ArrayList<String> output=new ArrayList<String>();
		String t = area.getText();
		String[] lines = t.split(""+'\n');
		for(String line: lines) try {
			Double.parseDouble(line);
		} catch (Throwable ex) {
			if (!line.trim().equals("")) output.add(line);
		}
		
		return output;
	}
	
	public static void main(String[] args) {
		
	}
	
	

}

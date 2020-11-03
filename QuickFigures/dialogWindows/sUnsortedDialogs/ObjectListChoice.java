package sUnsortedDialogs;

import java.util.ArrayList;

import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;


public class ObjectListChoice<T> extends StandardDialog {

	public ObjectListChoice(String title) {
		super(title);
		this.setModal(true);
		this.setWindowCentered(true);
		super.setTabName("Pick from the list");
	}

	 ArrayList<T> array;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public T select(String prompt, ArrayList<T> oos) {
		if (oos.size()==1) return oos.get(0);
		if (oos.size()==0) return null;
		
		addChoice(prompt, oos, 0);
		
		this.showDialog();
		
		int ind=this.getChoiceIndex(prompt);
		return oos.get(ind);
		
	}
	
	public ArrayList<T> selectMany(String prompt, ArrayList<T> oos, int howMany) {
		
		if (oos.size()==0) return null;
		
		for(int i=0; i<howMany; i++)
			addChoice(prompt+" "+(1+i), oos, i);
		
		this.showDialog();
		
		ArrayList<T> output=new ArrayList<T>();
		for(int i=0; i<howMany; i++)
			{
			int ind=this.getChoiceIndex(prompt+" "+(1+i));
			if (ind==oos.size()) {
				output.add(null);
			} else
			if (ind==-1) output.add(null); else
			 output.add(oos.get(ind));
			 
			}
		
		return output;
		
	}
	
	void addChoice(String prompt, ArrayList<T> oos, int starting) {
		
		if (oos.size()==0) return ;
		this.array=oos;
		String[] Items=new String[oos.size()+1];
		for(int i=0; i<oos.size()+1; i++) {
			if (i>=oos.size()) {
				Items[i]="none";
				continue;
			}
			T t=oos.get(i);
			if (t!=null) Items[i]=t.toString(); else Items[i]="";
		}
		
		if (starting>oos.size()) starting=oos.size();
		this.add(prompt, new ComboBoxPanel(prompt, Items, starting));
		
	}
	
}

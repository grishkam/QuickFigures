package journalCriteria;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import selectedItemMenus.BasicMultiSelectionOperator;

public class JournalCriteriaMenuOption extends BasicMultiSelectionOperator {

	public JournalCriteriaMenuOption() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Impose Journal Limits";
	}
	

	@Override
	public void run() {
		ArrayList<ZoomableGraphic> arrayTaret = getAllArray();
		new FormatOptionsDialog(arrayTaret, new JournalCriteria()).showDialog();

	}

}

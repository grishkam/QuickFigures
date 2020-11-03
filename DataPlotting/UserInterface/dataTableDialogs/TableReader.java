package dataTableDialogs;

public interface TableReader {

	Object getValueAt(int rowNumber, int checkCol);

	int getRowCount();

}

package dataTableDialogs;


public interface RowFilter {
	
	public boolean isChosenRow(TableReader table, int rowNumber);
	

	class CombinedFilter implements RowFilter {

		private RowFilter[] filter;

		public CombinedFilter(RowFilter... filters ) {
			this.filter=filters;
		}
		
		@Override
		public boolean isChosenRow(TableReader table, int rowNumber) {
			for(RowFilter f:filter) {
				if (!f.isChosenRow(table, rowNumber)) return false;
			}
			
			return true;
		}
		
	}
	
	class HeaderExcludingFilter implements RowFilter {

		@Override
		public boolean isChosenRow(TableReader i, int rowNumber) {
			return rowNumber!=0;
		}}
	
	class ColEqualFilter implements RowFilter {

		private String checkForValue;
		private int checkCol;

		public ColEqualFilter(String name, int classColumn) {
			this.checkForValue=name;
			this.checkCol=classColumn;
		}

		@Override
		public boolean isChosenRow(TableReader table, int rowNumber) {
			Object value = table.getValueAt(rowNumber, checkCol);
			if (value==checkForValue) return true;
			if (value==null) return false;
			return (value+"").equals(checkForValue);
		}}
	
}
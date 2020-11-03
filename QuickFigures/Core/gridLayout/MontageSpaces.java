package  gridLayout;

public interface MontageSpaces {
	public static final int MONTAGE=-1, PANELS=0, COLS=1, ROWS=2, PANELS_WITH_BORDER=3, BORDER=4, POINTS=5, 
	TOP_SPACE=6, LEFT_SPACE=8, RIGHT_SPACE=9, BOTTOM_SPACE=7, ALL_MONTAGE_SPACE=10, NON_MONTAGE_SPACE=11, 
	PANEL_WITH_TOP_SPACE=12, PANEL_WITH_BOTTOM_SPACE=13, PANEL_WITH_LEFT_SPACE=14, PANEL_WITH_RIGHT_SPACE=15,  
	EXTENDED_TOP_SPACE=16, EXTENDED_LEFT_SPACE=18, EXTENDED_RIGHT_SPACE=19,EXTENDED_BOTTOM_SPACE=17, 
	ALL_BORDERS=20,
	EXTENDED_COL=21, EXTENDED_ROW=22, PANEL_WITH_SPACES=23, ALL_SPACES=24, 
	NON_MONTAGE_TOP=25, NON_MONTAGE_LEFT=27, NON_MONTAGE_BOT=26, NON_MONTAGE_RIGHT=28,
	LABEL_ALLOTED_TOP=29, LABEL_ALLOTED_BOT=30, LABEL_ALLOTED_LEFT=31, LABEL_ALLOTED_RIGHT=32,
	HORIZONTAL_SPACES=33, VERTICAL_SPACES=34, HORIZONTAL_BORDER=35, VERTICAL_BORDER=36,
	COLUMN_OF_PANELS=37, ROW_OF_PANELS=38, BLOCK_OF_PANELS=39,
	ENTIRE_IMAGE=40, TOP_3rd=41, BOTTOM_3rd=42, LEFT_3rd=43, RIGHT_3rd =44;
	public static final int ALL_OF_THE=100, THIS_COLS=200, THIS_ROWS=300, PAIR=400, TRIAD=500, QUAD=600, PENT=700;
	
	final String[] stringDescriptors=new String[] {"Panel", "Column", "Row", "Panel with border", "Border", "Points", 
			"Top Space", "Bottom Space", "Left Space", "Right Space", 
			"Entire Montage", "Non-Montage Space", 
			"Panel with top space", "Panel with bottom space", "Panel with left space", "Panel with right space",
			"Extended Top Space", "Extended Bottom Space", "Extended Left Space", "Extended Right Space", 
			"All Borders",
			"Extended Column", "Extended Row", 
			"Panel with spaces", "Spaces", 
			"Non-montage top", "Non-Montage bottom", "Non-montage left", "Non-montage right",
			"Label alloted top", "Label alloted bottom", "Label alloted left", "Label alloted right",
			"Left and Right space", "Top and Bottom Space", 
			"Horizontal Border", "Vertical Border",
			"Column without labelspace", "Row without labelspace", "All Panels without labelspace",
			"Entire Image",
			"Top 3rd of panel", "Bottom 3rd of panel", "Left 3rd of panel", "Right 3rd of panel"};
		final String[] stringDescriptorsOfModifyers=new String[] {"of the single panel ", "of the entire montage", "of this column", "of this row", "of pair", "of trio", "of quartet", "of pentet"};

}


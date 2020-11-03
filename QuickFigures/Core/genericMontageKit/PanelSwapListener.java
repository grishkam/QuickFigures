package genericMontageKit;

import gridLayout.BasicMontageLayout;

/**Listens for a panel swap event*/
public interface PanelSwapListener {
	void onSwapCol(BasicMontageLayout ml, int row1, int row2);
	void onSwapRow(BasicMontageLayout ml, int row1, int row2);
	void onSwapPanel(BasicMontageLayout ml, int row1, int row2);
	//public void setLayout(BasicMontageLayout ml);
}

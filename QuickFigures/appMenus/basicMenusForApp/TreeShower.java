package basicMenusForApp;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import applicationAdapters.DisplayedImageWrapper;
import layersGUI.GraphicTreeUI;

public class TreeShower  extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		GraphicTreeUI tree = new GraphicTreeUI(diw.getImageAsWrapper());
		tree.showTreeForLayerSet(diw.getImageAsWrapper()) ;
		treeWindowCloser closer = new treeWindowCloser(tree, diw.getWindow());
		
		diw.getWindow().addWindowListener(closer);
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "ShowMeTheTree";
	}

	@Override
	public String getNameText() {
		// TODO Auto-generated method stub
		return "Show Layers";
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "Image";
	}
	
	class treeWindowCloser implements WindowListener {
		GraphicTreeUI currentTree=null;
		public treeWindowCloser(GraphicTreeUI tree, Window w) {
			
			currentTree=tree;
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			
			currentTree.closeWindow();
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

}

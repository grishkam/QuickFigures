package selectedItemMenus;

import java.io.File;

import ultilInputOutput.FileChoiceUtil;
import graphicalObjects.GraphicEncoder;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class SaveItem extends BasicMultiSelectionOperator {

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Save Item";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (array.size()==0) return;
		ZoomableGraphic item = array.get(0);//getSelecteditem();
		if (item==null) return;
		File f=FileChoiceUtil.getSaveFile();
		GraphicLayer gl=null;
		if (item instanceof KnowsParentLayer) {
			KnowsParentLayer kn=(KnowsParentLayer) item;
			gl=kn.getParentLayer();
			kn.setParentLayer(null);
		}
		GraphicEncoder ge = new GraphicEncoder(item);
		ge.writeToFile(f.getAbsolutePath());
		if (item instanceof KnowsParentLayer) {
			KnowsParentLayer kn=(KnowsParentLayer) item;
			gl=kn.getParentLayer();
			kn.setParentLayer(gl);
		}
	}

}

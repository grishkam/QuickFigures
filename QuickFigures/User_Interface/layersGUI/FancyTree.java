package layersGUI;

public class FancyTree implements MiscTreeOptions {

	@Override
	public String getMenuText() {
		return "Change Tree Modes";
	}

	@Override
	public void run() {
		TreeMode.fancy=!TreeMode.fancy;

	}

}

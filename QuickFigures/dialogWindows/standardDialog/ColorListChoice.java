package standardDialog;

import java.awt.Color;
import java.util.List;

public interface ColorListChoice {
	public List<Color> getColors();
	public Color getSelectedColor();
	
	public int getRainbow();//the index of the unknown color choice
}

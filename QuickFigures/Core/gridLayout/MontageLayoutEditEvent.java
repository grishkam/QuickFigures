package gridLayout;

public class MontageLayoutEditEvent {
	BasicMontageLayout layout;
	int type;
	public MontageLayoutEditEvent(BasicMontageLayout ml, int kind) {
		layout=ml;
		type=kind;
	}
	
	
	
}

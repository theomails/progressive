package net.progressit.progressive;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import lombok.Data;
import net.progressit.progressive.PSimpleTextField.PSTFValueEvent;

public class PSimpleTextArea extends PComponent<String, String>{

	@Data
	public static class PSTAValueEvent{
		private final String value;
	}
	
	private TextArea textArea = new TextArea();
	private ScrollPane spTextArea = new ScrollPane(textArea);
	public PSimpleTextArea(PPlacers placers) {
		super(placers);
	}

	@Override
	protected PDataPeekers<String> getDataPeekers() {
		return new PAllToSelfDataPeekers<String>();
	}

	@Override
	protected PRenderers<String> getRenderers() {
		return new PRenderers<String>( ()-> spTextArea, (data)->{
			if(!textArea.getText().equals(data)) {
				textArea.setText(data);
			}
		}, (data)-> new PChildrenPlan());

	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				
				//Below gets fired even when we programmatically do setText on the UI field?
				textArea.textProperty().addListener( (observable, oldValue, newValue)->{
					//TODO: Does it stabilise and all the remove/insert events have fired?
					post(new PSTFValueEvent(newValue));
				});
			}
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

}

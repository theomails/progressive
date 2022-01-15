package net.progressit.progressive;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import lombok.Data;

public class PSimpleTextField extends PComponent<String, String>{
	@Data
	public static class PSTFActionEvent{
		private final ActionEvent event;
	}
	@Data
	public static class PSTFValueEvent{
		private final String value;
	}
	
	private TextField textField = new TextField();
	public PSimpleTextField(PPlacers placers) {
		super(placers);
	}

	@Override
	protected PDataPeekers<String> getDataPeekers() {
		return new PAllToSelfDataPeekers<String>();
	}

	@Override
	protected PRenderers<String> getRenderers() {
		return new PRenderers<String>( ()-> textField, (data)->{
			if(!textField.getText().equals(data)) {
				textField.setText(data);
			}
		}, (data)-> new PChildrenPlan());

	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				textField.setOnAction((e)->{
					post(new PSTFActionEvent(e));
				});
				
				//Below gets fired even when we programmatically do setText on the UI field?
				textField.textProperty().addListener( (observable, oldValue, newValue)->{
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

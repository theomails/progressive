package net.progressit.progressive.components;

import java.util.List;

import com.google.common.eventbus.EventBus;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import lombok.Data;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class PSimpleTextField extends PLeafComponent<String, String>{
	@Data
	public static class PSTFActionEvent{
		private final ActionEvent event;
	}
	@Data
	public static class PSTFValueEvent{
		private final String value;
	}
	
	private TextField textField = new TextField();
	
	private PLifecycleHandler lifecycleHandler = new PSimpleLifecycleHandler() {
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
	
	public PSimpleTextField(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return lifecycleHandler;
	}

	@Override
	protected Node getUiComponent() {
		return textField;
	}

	@Override
	protected void renderSelf(String data) {
		if(!textField.getText().equals(data)) {
			textField.setText(data);
		}
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of(PSTFActionEvent.class, PSTFValueEvent.class);
	}

}

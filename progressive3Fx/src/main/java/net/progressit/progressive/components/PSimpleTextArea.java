package net.progressit.progressive.components;

import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.EventBus;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import lombok.Data;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class PSimpleTextArea extends PLeafComponent<String, String>{

	@Data
	public static class ValueEvent{
		private final String value;
	}
	
	private TextArea textArea = new TextArea();
	private ScrollPane spTextArea = new ScrollPane(textArea);
	public PSimpleTextArea(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				
				//Below gets fired even when we programmatically do setText on the UI field?
				textArea.textProperty().addListener( (observable, oldValue, newValue)->{
					//TODO: Does it stabilise and all the remove/insert events have fired?
					post(new ValueEvent(newValue));
				});
			}
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

	@Override
	protected Node getUiComponent() {
		return spTextArea;
	}

	@Override
	protected void renderSelf(String data) {
		if(!textArea.getText().equals(data)) {
			textArea.setText(data);
		}
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return Arrays.asList(ValueEvent.class);
	}

}

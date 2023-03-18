package net.progressit.progressive.components;


import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.EventBus;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import lombok.Data;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class PSimpleButton extends PLeafComponent<String, String>{
	@Data
	public static class ButtonEvent{
		private final ActionEvent event;
	}
	
	private Button button = new Button();
	public PSimpleButton(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				button.setOnAction((e)->{
					post(new ButtonEvent(e));
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
		return button;
	}

	@Override
	protected void renderSelf(String data) {
		button.setText(data);
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return Arrays.asList(ButtonEvent.class);
	}

}

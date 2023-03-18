package net.progressit.progressive.components;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.google.common.eventbus.EventBus;

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
	
	private JButton button = new JButton();
	public PSimpleButton(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				button.addActionListener((e)->{
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
	protected JComponent getUiComponent() {
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

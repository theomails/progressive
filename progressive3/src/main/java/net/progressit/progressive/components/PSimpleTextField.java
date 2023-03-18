package net.progressit.progressive.components;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.eventbus.EventBus;

import lombok.Data;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class PSimpleTextField extends PLeafComponent<String, String>{
	@Data
	public static class TFActionEvent{
		private final ActionEvent event;
	}
	@Data
	public static class TFValueEvent{
		private final String value;
	}
	
	private JTextField textField = new JTextField();
	
	private PLifecycleHandler lifecycleHandler = new PSimpleLifecycleHandler() {
		@Override
		public void prePlacement() {
			textField.addActionListener((e)->{
				post(new TFActionEvent(e));
			});
			
			//Below gets fired even when we programmatically do setText on the UI field.
			textField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					postChange();
				}
				public void removeUpdate(DocumentEvent e) {
					postChange();
				}
				public void insertUpdate(DocumentEvent e) {
					postChange();
				}
				
				private void postChange() {
					//Needed because getText doesn't stabilise until all the remove/insert events have fired.
					SwingUtilities.invokeLater(()->{
						String text = textField.getText();
						post(new TFValueEvent(text));
					});
				}
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
	protected JComponent getUiComponent() {
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
		return Arrays.asList(TFActionEvent.class, TFValueEvent.class);
	}

}

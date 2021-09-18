package net.progressit.progressive;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
	
	private JTextField textField = new JTextField();
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
				textField.addActionListener((e)->{
					post(new PSTFActionEvent(e));
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
							post(new PSTFValueEvent(text));
						});
					}
				});
			}
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

}

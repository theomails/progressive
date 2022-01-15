package net.progressit.progressive;

import javafx.scene.control.Label;

public class PLabel extends PComponent<String, String>{
	
	private Label label = new Label();
	public PLabel(PPlacers placers) {
		super(placers);
	}

	@Override
	protected PDataPeekers<String> getDataPeekers() {
		return new PAllToSelfDataPeekers<String>();
	}

	@Override
	protected PRenderers<String> getRenderers() {
		return new PRenderers<String>( ()-> label, (data)->{
			label.setText(data);
		}, (data)-> new PChildrenPlan());
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

}

# Progressive Swing - Simple reactive component model for Swing inspired by Vue JS

A _progressive component_ takes inputs from its parent in terms of `props`. It has `data`, which can be richer than the `props` passed in, which 
represents its whole _state_. Whenever this data changes, the component should pass the updated data to the framework, which 
uses some hooks on the component to decide whether to update this component itself and/or its children and then calls the render hooks. 

The component is given an event bus internally so that the parent can register a `listener` and listen to the 
events `posted` by the component. 

The component is given lifecycle hooks so that it can manage its own lifecycle. A _progressive component_ 
can encapsulate one Swing `JComponent`. To support the rendering, the component defines its own render function which will update its `JComponent`. In addition to this, the component 
describes a list of child `PComponents`, if any, which should be rendered.  Each component is defined by sub-classing the base component `PComponent`. 

Some design ideas that have been used:
- Lambdas as used to define the hook functions wherever possible so that the component definition is concise
- Immutable data structures are used for the `props` and `data` so that the old state can be retained and compared efficiently with the new state.
- A light weight event bus is used, with custom data types for the events, and events being routed to the appropriate handler based on the handler method signature.

Scroll to the bottom to see a few `PComponent`s in action.

## PComponent

 `PComponent` is the Progressive Swing framework core class, which defines a Reactive Visual Component.
 
Any Progressive component is created as a sub-class of `PComponent` class, with two template type specifications. A `PComponent` is defined around a single Swing `JComponent`
 which it manages (however that component could be a container, and more nested `JComponent`s could be handled by the `PComponent` itself). Each `PComponent` could have 
 several `PComponents` as children. The framework takes care of rendering the children which are `PComponent`s.
 
The `PComponent` is reactive in the sense that the _when-to-redraw_ decision is taken automatically by the framework, whenever data is updated.
 Also, rendering of own Swing component(s) and rendering of child `PComponent`s are triggered by the framework.
 
`PComponent` provides several lifecycle hooks to enable the component to be well defined. Using these, the `PComponent` can manage the Swing component(s) that it wraps.
 
These are the steps of defining the `PComponent` tree to build an app:
 

 1. Sub-class `PComponent` to build each custom component which wraps one or a few Swing components.
 2. Define two data structures for each component - the format of the props passed from parent, and the format of the state/data maintained in the component
 3. Parent can pass in the whole data of the component, making the child completely in sync with the parent, or, the parent can just provide the context while the
 child component takes more ownership of the data as well as more responsibility in terms of functionality.
 4. Child should never edit the props passed in. However, child can manage its own data. Child should keep the framework informed about changes in its data. In fact, it is 
 recommended that the child not store any data in local variables, and rather just pass the data directly to `setData` method. Note that the props have to be merged
 into the data, and passed on to the framework. This it because the framework just hands over the props and does not monitor or react to the props.
 5. Parent has to provide placement logic through a `PPlacers` for each child, and the lambda function will be used by framework when the child's Swing component needs to be placed.
 6. `PComponent` subclass itself has to provide a `PDataPeekers` which will define which part of the data field affects its own Swing components
 vs which part of the data affects its `PComponent` children.
 7. `PComponent` also needs to provide lambdas via `PDataPeekers` to render the data into (1) its own Swing components and (2) to generate a render plan of `PComponent` children
 8. `PComponent` also needs to provide a `PSimpleLifecycleHandler` to hook into its own lifecycle.
 9. Having setup all the above, at the time of using the `PComponent`, a `PEventListener` has to be provided along with the props.
 10. Most of the `PComponent` tree of the application will be used inside `childrenPlanRenderer` lambda of the parent `PComponent`'s `PDataPeekers`
 11. However, the top-most one (or few) `PComponent`s can be placed manually by calling the `PComponent.place` static method. 
 12. Once all the lambdas and handlers are setup for a `PComponent`, the rendering only starts on the first `setData` call. However, `setData` is never called
 by framework. Framework only calls `setProps` to trigger the component. The component has to merge it with/pass it on as `setData`.
 
Some tips for defining the generic types for `PComponent`s:
 
 - `T` represents the type of the data, while `U` represents the type of the props.
 - If the component uses no props or data (highly unlikely to be useful), both T and U can be left as `Object` or some such class.
 - If the component uses no props and uses only data, U can be left as `Object`, etc.
 - If the component has a prop, usually the data will contain that prop's information and could have more internal state (as the framework reacts only to data not props).
 - In the above case, the prop can even be a scalar data class like String or Long. Or, if there are more props, it can be a custom type.
 - Likewise, the data can also be just a scalar type matching the prop, or it can be a bigger type to hold more internal state of the component.
 - The `data` has to be an immutable data structure. This is because we need the old data tree to be intact to compare it to the new tree. This is because
 mutating the data object will also change the "old" copy which is retained by the framework. Using some other mechanism to overcome this limitation, like cloning or serialization, would also be sub-optimal.
 - For derieving updated versions of the immutable data class, the recommended way is to use the `Lombok` `@Builder` annotation with the `toBuilder` flag enabled.
 This allows for converting an existing object back to a builder, and from there creating a derived copy, with just selected fields getting new values.
 - All listeners and hooks are called on Swing Event dispatcher threads. You may start other custom threads, but, get back on Swing event dispatcher thread to interact with the F/w.
 
### Examples

#### Eg. 1 - VFStatusPanel - An example of a component that an end user of the library would create.

```java

public class VFStatusPanel extends PComponent<VFStatusData, VFStatusData>{
	@Data
	@Builder(toBuilder = true)
	public static class VFStatusData{
		private final String info;
	}
	
	private JPanel panel = new JPanel(new MigLayout("insets 1","[grow, fill]","[]"));
	private PPlacers simplePlacers = new PSimpleContainerPlacers(panel);

	private PLabel lblStatus = new PLabel( simplePlacers );
	public VFStatusPanel(PPlacers placers) {
		super(placers);
	}

	@Override
	protected PDataPeekers<VFStatusData> getDataPeekers() {
		return new PAllToChildrenDataPeekers<VFStatusData>();
	}

	@Override
	protected PRenderers<VFStatusData> getRenderers() {
		return new PRenderers<VFStatusData>( ()-> panel, (data)->{}, (data)->{
			PChildrenPlan plans = new PChildrenPlan();
			
			PChildPlan plan = PChildPlan.builder().component(lblStatus).props(data.getInfo()).listener(Optional.empty()).build();
			plans.addChildPlan(plan);
			
			return plans;
		} );
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void postProps() {
				setData( getProps() );
			}
		};
	}

}

```

#### Eg. 2 - PLabel - An example of a simple component that is bundled with the library.

```java

public class PLabel extends PComponent<String, String>{
	@Data
	public static class PLActionEvent{
		private final ActionEvent event;
	}
	
	private JLabel label = new JLabel();
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

```

#### Eg. 3 - PSimpleTextField - An example of a component that is bundled with the library that reacts to Swing events

```java

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

```

#### Eg. 4 - VFStatusPanel - An example of a more meaty component that an end user of the library would create.

```java

public class VFScanSettingsPanel extends PComponent<String, String>{
	//EVENTS
	@Data
	public static class VFSSPPathChangedEvent{
		private final String path;
	}
	@Data
	public static class VFSSPScanClickedEvent{
	}
	
	
	private PDisplayWindow window;
	private JPanel panel = new JPanel(new MigLayout("insets 1","[]5[grow, fill]10[]5[]","[]"));
	private PPlacers simplePlacers = new PSimpleContainerPlacers(panel);

	private PLabel lblPath = new PLabel( simplePlacers );
	private PSimpleTextField txtPath = new PSimpleTextField( simplePlacers );
	private PSimpleButton btnBrowse = new PSimpleButton( simplePlacers );
	private PSimpleButton btnScan = new PSimpleButton( simplePlacers );

	public VFScanSettingsPanel(PPlacers placers, PDisplayWindow window) {
		super(placers);
		this.window = window;
	}

	@Override
	protected PDataPeekers<String> getDataPeekers() {
		return new PAllToChildrenDataPeekers<String>();
	}

	@Override
	protected PRenderers<String> getRenderers() {
		return new PRenderers<String>( ()-> panel, (data)->{}, (data)->{
			PChildrenPlan plans = new PChildrenPlan();
			
			PChildPlan plan = PChildPlan.builder().component(lblPath).props("Folder to Scan: ").listener(Optional.empty()).build();
			plans.addChildPlan(plan);
			
			plan = PChildPlan.builder().component(txtPath).props(data).listener( Optional.of( new PEventListener() {
				@Subscribe
				public void handle(PSTFValueEvent e) {
					post(new VFSSPPathChangedEvent(e.getValue()));
				}
			} )).build();
			plans.addChildPlan(plan);
			
			plan = PChildPlan.builder().component(btnBrowse).props("Browse...").listener(Optional.of( new PEventListener() {
				@Subscribe
				public void handle(PSBActionEvent e) {
					onBrowseClick();
				}
			} )).build();
			plans.addChildPlan(plan);
			
			plan = PChildPlan.builder().component(btnScan).props("Scan").listener(Optional.of( new PEventListener() {
				@Subscribe
				public void handle(PSBActionEvent e) {
					onScanClick();
				}
			} )).build();
			plans.addChildPlan(plan);
			
			return plans;
		} );
	}
	
	public void onBrowseClick() {
		JFileChooser chooser = new JFileChooser();
		System.out.println("Trying to open folder.." + getData());
		chooser.setCurrentDirectory(new File( getData() ));
		chooser.setDialogTitle("Choose Folder to Scan...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
			File result = chooser.getSelectedFile();
			if(!result.isDirectory()) {
				result = chooser.getCurrentDirectory();
			}
			String sPath = result.toPath().toString();
			System.out.println("SEND: " + sPath);
			post(new VFSSPPathChangedEvent(sPath));
		}
	}
	public void onScanClick() {
		post(new VFSSPScanClickedEvent());
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void postProps() {
				setData( getProps() );
			}
		};
	}

}

```

### Key Classes which are used to define the hooks for the component

```java

	//Marker interface. The library (with the help of Guava Event Bus) calls the appropriate handler based on the Event data type.
	public static interface PEventListener{}
	
	@Data
	public static class PPlacers{
		private final Consumer<JComponent> placer;
		private final Consumer<JComponent> remover;		
	}
	
	@Data
	public static class PDataPeekers <T>{
		private final Function<T, Set<Object>> selfDataGetter;
		private final Function<T, Set<Object>> childrenDataGetter;
	}
	
	@Data
	public static class PRenderers <T>{
		private final Supplier<JComponent> uiComponentMaker;
		private final Consumer<T> selfRenderer;
		private final Function<T, PChildrenPlan> childrenPlanRenderer;
	}
	
	public static interface PLifecycleHandler {
		public void prePlacement();
		public void postPlacement();
		public void preProps();
		public void postProps();
		public void preRemove();
		public void postRemove();
	}
	
	//Simple implementation which provides blank implementations for all the lifecycle methods
	public static class PSimpleLifecycleHandler implements PLifecycleHandler{
		@Override
		public void prePlacement() {
		}
		@Override
		public void postPlacement() {
		}
		@Override
		public void preProps() {
		}
		@Override
		public void postProps() {
		}
		@Override
		public void preRemove() {
		}
		@Override
		public void postRemove() {
		}
	}

```
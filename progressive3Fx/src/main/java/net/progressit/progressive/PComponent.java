package net.progressit.progressive;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import javafx.scene.Node;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * <code>PComponent</code> is the Progressive Swing framework core class, which defines a Reactive Visual Component.
 * <p>PComponent is intended to be part of a hierarchy of reactive visual components which render the UI</p>
 * <p>Each PComponent maps to a single Node, which could be either a simple component or a container</p>
 * <p>PComponent also holds plans for child PComponents.</p>
 * <p>A component is defined by the props it takes as input from the parent (or outside in the case of Root component), and the data which is its state.</p>
 * <p>The component provides data impact functions which define the impact of data/state changes.</p>
 * <p>The component takes props from the parent and decides how the props merge into the data/state.</p>
 * <p>The merged data is then applied to itself via the framework.</p>
 * 
 * 
 * 
 * @author theo
 *
 * @param <T>
 */
public abstract class PComponent<T,U> {
	private static final Logger LOGGER = LoggerFactory.getLogger( PComponent.class.getName() );
	
	/**
	 * The static <code>place</code> method which allows "outside" to place the <em>root</em> PComponent.
	 * 
	 * @param <V>
	 * @param newComponent
	 * @param listener
	 * @param props
	 */
	public static <V> void place(PComponent<?,V> newComponent, PEventListener listener, V props){
		LOGGER.info("Placing component");
		ensureEDT();
		
		Node uiComponent = newComponent.getUiComponent();
		//uiComponent.setBorder(BorderFactory.createLineBorder(Color.red));
		
		newComponent.getLifecycleHandler().prePlacement();
		newComponent.getPlacers().placer.accept(uiComponent);
		newComponent.setListener(listener);
		newComponent.getLifecycleHandler().postPlacement();
		
		newComponent.setProps(props);
	}
	
	/**
	 * The static <code>remove</code> method which allows "outside" to place the <em>root</em> PComponent.
	 * 
	 * @param <V>
	 * @param newComponent
	 */
	public static <V> void remove(PComponent<?,V> oldComponent){
		LOGGER.info("Removing component");
		ensureEDT();
		
		Node uiComponent = oldComponent.getUiComponent();
		//uiComponent.setBorder(BorderFactory.createLineBorder(Color.red));
		
		oldComponent.getLifecycleHandler().preRemove();
		oldComponent.getPlacers().remover.accept(uiComponent);
		oldComponent.clearListener();  
		oldComponent.getLifecycleHandler().postRemove();
	}
	
	/**
	 * Return a (sub)set of the data which impacts the self render.
	 * This is used to determine if the self-render has to be triggered again.
	 * 
	 * @param data
	 * @return
	 */
	protected abstract Set<Object> partitionDataForSelf(T data);
	
	/**
	 * Return a (sub)set of the data which impacts the children render.
	 * This is used to determine if the children-render has to be triggered again.
	 * 
	 * @param data
	 * @return
	 */	
	protected abstract Set<Object> partitionDataForChildren(T data);
	
	/**
	 * Get the static UI component, with nothing pre-rendered. 
	 * The rendering of data into the component is handled later via <code>renderSelf</code>
	 * 
	 * @return
	 */
	protected abstract Node getUiComponent();
	
	/**
	 * The rendering of data into the UI component provided via <code>getUiComponent</code>
	 * 
	 * @param data
	 */
	protected abstract void renderSelf(T data);
	
	/**
	 * A render plan of what children <code>PComponent</code>s would be needed based on the current data.
	 * 
	 * @param data
	 * @return
	 */
	protected abstract PChildrenPlan renderChildrenPlan(T data);
	
	/**
	 * A unified interface to handle all the lifecycle hooks.
	 * Keeping it as a separate interface allows lifecycle adapter(s) which can assist with boilerplate code for the trivial case.
	 * <p><b>Note:</b> Create a single instance of a lifecycle handler and return the same reference. 
	 * This method call is not cached and can be called several times whenever needed.
	 * If a new object is created each time, then at least the handler should be stateless.</p>   
	 * 
	 * @return
	 */
	protected abstract PLifecycleHandler getLifecycleHandler();
	
	protected abstract List<Class<?>> declareEmittedEvents();
	
	/**
	 * The bus of this component on which this component emits all events defined by this component.
	 * <p>Usually the parent PComponent listens to this bus, by providing a PEventListener, which the method signatures deciding how the events are delivered.
	 */
	private final EventBus bus = new EventBus();
	
	@Getter(value = AccessLevel.PROTECTED)
	private final PPlacers placers;
	
	@Getter(value = AccessLevel.PROTECTED)
	private final EventBus globalBus;
	
	/**
	 * The listener which is bound to the bus, usually provided by the parent PComponnet.
	 * This reference could be useful at least when this component is re-used for a different purpose.
	 */
	private PEventListener listener = null;
	
	/**
	 * Data is stored here after it is rendered. So, getData only works after rendered.
	 */
	private T renderedData = null;
	
	/**
	 * Props are stored here as soon as received. However, the pre hook is called before this variable is updated (so old props if any could be accessed)
	 */
	private U props = null;
	
	/**
	 * The self part of the data is saved for change detection.
	 */
	private Set<Object> renderedSelfData = null;
	
	/**
	 * The child part of the data is saved for change detection.
	 */
	private Set<Object> renderedChildrenData = null;
	
	/**
	 * Currently rendered children plan is kept, so that once the new plan is obtained, it can be diffed.
	 */
	private PChildrenPlan renderedPlan = new PChildrenPlan();
	
	/**
	 * Actual rendered PComponents are kept, with the hope of reusing some of them if the types are the same. 
	 */
	@SuppressWarnings("rawtypes")
	private final List<PComponent> renderedChildComponents = new ArrayList<>();
	
	
	public PComponent(PPlacers placers, EventBus globalBus) {
		LOGGER.info(string("Initializing"));
		this.placers = placers;
		this.globalBus = globalBus;
	}
	
	public void setProps(U props) {
		LOGGER.info(string("Setting props :: ", props));
		//LOGGER.info(string("Settings props", props));
		ensureEDT();
		
		this.getLifecycleHandler().preProps();
		this.props = props;
		this.getLifecycleHandler().postProps();
	}
	protected U getProps() {
		LOGGER.info(string("Getting props"));
		ensureEDT();
		
		return props;
	}
	public T getData() {
		LOGGER.info(string("Getting data"));
		ensureEDT();
		
		return renderedData;
	}
	/**
	 * Feel free to set data always. This component will check and re-render only if necessary.
	 * @param inData
	 */
	protected void setData(T inData) {
		LOGGER.info(string("Setting data :: ", inData));
		//LOGGER.info(string("Setting data", inData));
		ensureEDT();
		
		if((inData==null && renderedData==null) || inData.equals(renderedData)) {
			LOGGER.debug(string("No change in data"));
			return;
		}
		//Some change is there
		Set<Object> selfData = partitionDataForSelf(inData);
		Set<Object> childrenData = partitionDataForChildren(inData);
		
		if(!selfData.equals(renderedSelfData)) {
			LOGGER.debug(string("Self data has changed.. rendering"));
			LOGGER.debug("selfData " + selfData);
			LOGGER.debug("renderedSelfData " + renderedSelfData);
			renderSelf(inData);
			renderedSelfData = selfData;
		}
		renderedData = inData; //Set before going to children.
		if(!childrenData.equals(renderedChildrenData)) {
			LOGGER.debug(string("Children data has changed.. rendering"));
			PChildrenPlan childrenPlan = renderChildrenPlan(inData);
			renderedChildrenData = childrenData; //Data has been processed into plan
			diffAndRenderPlan(childrenPlan);
			//getUiComponent().invalidate();
			//getUiComponent().repaint();
			renderedPlan = childrenPlan; //Plan has been rendered
		}
	}
	
	/**
	 * Just to make sure that next time we don't register the same listener again during setListener.
	 */
	public void clearListener() {
		LOGGER.info(string("Clearing listener"));
		ensureEDT();
		
		if(listener!=null) {
			bus.unregister(listener);
			listener = null;
		}
	}
	/**
	 * Set listener has to be called each time when a component is placed (used), because, each time
	 * the same component could be used for a completely different purpose.
	 * @param listener
	 */
	public void setListener(PEventListener listener) {
		LOGGER.info(string("Setting listener"));
		ensureEDT();
		
		this.listener = listener;
		if(listener!=null) {
			bus.register(listener);
		}
	}
	
	protected void post(Object event) {
		LOGGER.info(string("Posting event to bus", event));
		ensureEDT();
		if(!declareEmittedEvents().contains(event.getClass())) {
			throw new RuntimeException("Undeclared event class: " + event.getClass());
		}
		
		bus.post(event);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void diffAndRenderPlan(PChildrenPlan childrenPlan) {
		LOGGER.info(string("Diffing and rendering children plan", "children size", childrenPlan.getChildrenPlan().size()));
		ensureEDT();
		
		int oldSize = renderedPlan.getChildrenPlan().size();
		int newSize = childrenPlan.getChildrenPlan().size();
		int commonSize = Math.min(oldSize, newSize);
		int matchedCount = 0;
		for(int i=0;i<commonSize;i++) {
			Class<?> oldType = renderedPlan.getChildrenPlan().get(i).getComponent().getClass();
			Class<?> newType = childrenPlan.getChildrenPlan().get(i).getComponent().getClass();
			if(oldType.equals(newType)) { //The user implemented sub class should not be parameterised.
				matchedCount++;
			}else {
				break; //Only consecutive matches are used currently.
			}
		}
		if(matchedCount>0) {
			LOGGER.debug(string("First #n components matched: ", matchedCount, " Re-using components"));
			for(int i=0;i<matchedCount;i++) {
				LOGGER.debug(string("#i", (i + 1)));
				//Swap out info, so that same component is re-used, possibly for different data/listener
				PChildPlan newPlan = childrenPlan.getChildrenPlan().get(i);
				PComponent<Object, Object> renderedComponent = renderedChildComponents.get(i);
				renderedComponent.clearListener();
				renderedComponent.setListener( newPlan.getListener().orElse(null) );
				renderedComponent.setProps(newPlan.getProps());
			}
		} 
		if(oldSize!=newSize) {
			if(oldSize > matchedCount) {
				LOGGER.debug(string("Removing #n excess components", (oldSize - matchedCount)));
				//Remove old comps
				for(int i=matchedCount;i<oldSize;i++) {
					LOGGER.debug(string("#i", (i - matchedCount + 1)));
					PComponent oldComponent = renderedChildComponents.get(renderedChildComponents.size()-1); //Get next available
					Node uiComponent = (Node) oldComponent.getUiComponent();
					oldComponent.getLifecycleHandler().preRemove();
					oldComponent.getPlacers().remover.accept(uiComponent);
					oldComponent.clearListener();  
					oldComponent.getLifecycleHandler().postRemove();
					renderedChildComponents.remove(renderedChildComponents.size()-1); //Remove the picked one
				}
			}
			if(newSize>matchedCount) {
				LOGGER.debug(string("Adding #n new components", (newSize - matchedCount)));
				//Add new comps
				for(int i=matchedCount;i<newSize;i++) {
					LOGGER.debug(string("#i", (i - matchedCount + 1)));
					PChildPlan newPlan = childrenPlan.getChildrenPlan().get(i);
					PComponent newComponent = newPlan.getComponent(); //Get next available
					Node uiComponent = (Node) newComponent.getUiComponent();
					renderedChildComponents.add(newComponent);
					
					newComponent.getLifecycleHandler().prePlacement();
					newComponent.getPlacers().placer.accept(uiComponent);
					newComponent.setListener(newPlan.getListener().orElse(null));
					newComponent.getLifecycleHandler().postPlacement();
					
					newComponent.setProps(newPlan.getProps());
				}
			}
		}
	}
	
	private String string(Object...objects) {
		StringBuilder builder = new StringBuilder(1000);
		builder.append(getClass().getSimpleName()).append(": ");
		for(Object o:objects) {
			builder.append(o==null?"*null*":o.toString()).append(" ");
		}
		return builder.toString();
	}
	
	private static void ensureEDT() {
		if(!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("All PComponent operations should be on EDT!");
		}
	}
}

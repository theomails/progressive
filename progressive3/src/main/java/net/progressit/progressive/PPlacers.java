package net.progressit.progressive;

import java.util.function.Consumer;

import javax.swing.JComponent;

import lombok.Data;

/**
 * Holds the UI placer and remover, usually provided by the parent to a PComponent.
 * PComponent just holds the reference to PPlacers, whereas the framework takes care of using the PPlacer.
 * Usually the child PComponent's JComponents are placed into the Parent PComponent's JComponent.
 * 
 * @author theodore.r
 *
 */
@Data
public class PPlacers{
	final Consumer<JComponent> placer;
	final Consumer<JComponent> remover;		
}
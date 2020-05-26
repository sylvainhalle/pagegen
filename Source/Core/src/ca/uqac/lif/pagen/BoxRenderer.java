package ca.uqac.lif.pagen;

import java.io.PrintStream;

public abstract class BoxRenderer
{
	/**
	 * Renders a box into a PrintStream
	 * @param ps The PrintStream into which the box contents are printed
	 * @param b The box to render
	 */
	public abstract void render(PrintStream ps, Box b);
}

package ca.uqac.lif.pagen;

import java.io.PrintStream;

public class TextRenderer extends BoxRenderer
{
	public TextRenderer()
	{
		super();
	}
	
	@Override
	public void render(PrintStream ps, Box b)
	{
		int id = b.getId();
		ps.print("x_" + id + "=" + b.getX());
		ps.print(",");
		ps.print("y_" + id + "=" + b.getY());
		ps.print(",");
		ps.print("w_" + id + "=" + b.getWidth());
		ps.print(",");
		ps.print("h_" + id + "=" + b.getHeight());
		ps.println();
		for (Box b_c : b.getChildren())
		{
			render(ps, b_c);
		}
	}
}

package ca.uqac.lif.pagen;

import java.io.PrintStream;

public class DotRenderer extends BoxRenderer
{
	public DotRenderer()
	{
		super();
	}
	
	@Override
	public void render(PrintStream ps, Box b)
	{
		ps.println("digraph G {");
		ps.println("node [shape=\"circle\",fillstyle=\"solid\"]");
		render(ps, b, -1);
		ps.println("}");
	}
	
	protected void render(PrintStream ps, Box b, int parent)
	{
		int id = b.getId();
		if (parent >= 0)
		{
			ps.println(parent + " -> " + id + ";");
		}
		for (Box b_c : b.getChildren())
		{
			render(ps, b_c, id);
		}
	}
}

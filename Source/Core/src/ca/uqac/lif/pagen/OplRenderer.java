package ca.uqac.lif.pagen;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.pagen.LayoutConstraint.Disjoint;
import ca.uqac.lif.pagen.LayoutConstraint.HorizontallyAligned;
import ca.uqac.lif.pagen.LayoutConstraint.VerticallyAligned;

public class OplRenderer extends BoxRenderer
{
	Set<LayoutConstraint> m_constraints;
	
	@SafeVarargs
	public OplRenderer(Set<LayoutConstraint> ... constraints)
	{
		super();
		m_constraints = new HashSet<LayoutConstraint>();
		for (Set<LayoutConstraint> set : constraints)
		{
			m_constraints.addAll(set);
		}
	}
	
	public OplRenderer addConstraints(Set<LayoutConstraint> constraints)
	{
		m_constraints.addAll(constraints);
		return this;
	}

	@Override
	public void render(PrintStream ps, Box b)
	{
		Map<Integer,Box> boxes = b.flatten();
		int size = boxes.size();
		int top_id = b.getId();
		ps.println("int nb_rectangles=" + size + ";");
		ps.print("{int} rectangles_id={");
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(i);
		}
		ps.println("};");
		ps.print("float Height[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(i).getHeight());
		}
		ps.println("];");
		
		ps.print("float Width[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(i).getWidth());
		}
		ps.println("];");
		ps.print("float ini_left[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(i).getX());
		}
		ps.println("];");
		ps.print("float ini_top[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(i).getY());
		}
		ps.println("];");	
		ps.println("dvar float left[rectangles_id];");
		ps.println("dvar float top[rectangles_id];");
		ps.println("minimize sum(i in rectangles_id)(abs(top[i]-ini_top[i])+abs(left[i]-ini_left[i]));");
		ps.println("subject to {");
		ps.println("left[" + top_id + "]==ini_left[" + top_id + "];");
		ps.println("top[" + top_id + "]==ini_top[" + top_id + "];");
		for (LayoutConstraint lc : m_constraints)
		{
			render(ps, lc);
		}
		ps.println("}");
	}

	protected static void render(PrintStream ps, LayoutConstraint c)
	{
		if (c instanceof VerticallyAligned)
		{
			renderVerticallyAligned(ps, (VerticallyAligned) c);
		}
		else if (c instanceof HorizontallyAligned)
		{
			renderHorizontallyAligned(ps, (HorizontallyAligned) c);
		}
		else if (c instanceof Disjoint)
		{
			renderDisjoint(ps, (Disjoint) c);
		}
	}
	
	protected static void renderVerticallyAligned(PrintStream ps, VerticallyAligned c)
	{
		Set<Box> boxes = new HashSet<Box>(c.m_boxes.size());
		boxes.addAll(c.m_boxes);
		Box first = null;
		for (Box b : boxes)
		{
			first = b;
			break;
		}
		if (first == null)
		{
			return;
		}
		boxes.remove(first);
		if (boxes.isEmpty())
		{
			return;
		}
		for (Box b : boxes)
		{
			ps.println("top[" + first.m_id + "]==top[" + b.m_id + "];");
		}
	}
	
	protected static void renderHorizontallyAligned(PrintStream ps, HorizontallyAligned c)
	{
		Set<Box> boxes = new HashSet<Box>(c.m_boxes.size());
		boxes.addAll(c.m_boxes);
		Box first = null;
		for (Box b : boxes)
		{
			first = b;
			break;
		}
		if (first == null)
		{
			return;
		}
		boxes.remove(first);
		if (boxes.isEmpty())
		{
			return;
		}
		for (Box b : boxes)
		{
			ps.println("left[" + first.m_id + "]==left[" + b.m_id + "];");
		}
	}
	
	protected static void renderDisjoint(PrintStream ps, Disjoint c)
	{
		
	}
}

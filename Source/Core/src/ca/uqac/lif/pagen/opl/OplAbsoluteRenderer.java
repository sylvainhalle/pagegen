/*
    A random DOM tree generator
    Copyright (C) 2020-2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.pagen.opl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.pagen.Box;
import ca.uqac.lif.pagen.LayoutConstraint;
import ca.uqac.lif.pagen.LayoutConstraint.Contained;
import ca.uqac.lif.pagen.LayoutConstraint.Disjoint;
import ca.uqac.lif.pagen.LayoutConstraint.HorizontallyAligned;
import ca.uqac.lif.pagen.LayoutConstraint.VerticallyAligned;

/**
 * Produces a file in the OPL format based on a tree of nested boxes.
 * @author Stéphane Jacquet
 */
public class OplAbsoluteRenderer extends OplRenderer
{
	/**
	 * The objective function used in the OPL model
	 */
	protected static final transient String s_objectiveFunction = "minimize sum(i in rectangles_id)(abs(top[i]-ini_top[i])+abs(left[i]-ini_left[i])+Height[i]-ini_Height[i]+Width[i]-ini_Width[i]);";

	@SafeVarargs
	public OplAbsoluteRenderer(Set<LayoutConstraint> ... constraints)
	{
		super(constraints);
	}

	@Override
	public void render(PrintStream ps, Box b)
	{
		Map<Integer,Box> boxes = b.flatten();
		int size = boxes.size();
		// Box IDs are put in a list so that we can always enumerate them
		// in the same order
		List<Integer> box_ids = new ArrayList<Integer>(size);
		box_ids.addAll(boxes.keySet());
		int top_id = b.getId();
		ps.println("/****************************************");
		ps.println(" * OPL 12.10.0.0 Model");
		ps.println(" * Tree size:             " + b.getSize());
		ps.println(" * Tree depth:            " + b.getDepth());
		ps.println("****************************************/");
		ps.println("int nb_rectangles=" + size + ";");
		ps.print("{int} rectangles_id={");
		for (int i = 0; i < size; i++)
		{
			int box_index = box_ids.get(i);
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(box_index);
		}
		ps.println("};");
		ps.print("float ini_Height[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			int box_index = box_ids.get(i);
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(box_index).getHeight());
		}
		ps.println("];");

		ps.print("float ini_Width[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			int box_index = box_ids.get(i);
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(box_index).getWidth());
		}
		ps.println("];");
		ps.print("float ini_left[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			int box_index = box_ids.get(i);
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(box_index).getX());
		}
		ps.println("];");
		ps.print("float ini_top[rectangles_id]=[");
		for (int i = 0; i < size; i++)
		{
			int box_index = box_ids.get(i);
			if (i > 0)
			{
				ps.print(", ");
			}
			ps.print(boxes.get(box_index).getY());
		}
		ps.println("];");	
		ps.println("dvar float Height[rectangles_id];");
		ps.println("dvar float Width[rectangles_id];");
		ps.println("dvar float left[rectangles_id];");
		ps.println("dvar float top[rectangles_id];");
		ps.println("execute");		
		ps.println("{");
		ps.println("cplex.tilim=1000;");	
		ps.println("cplex.epgap=0.2;");	
		ps.println("}");
		ps.println(s_objectiveFunction);
		ps.println("subject to {");
		ps.println("left[" + top_id + "]==ini_left[" + top_id + "];");
		ps.println("top[" + top_id + "]==ini_top[" + top_id + "];");
		for (LayoutConstraint lc : m_constraints)
		{
			render(ps, lc);
		}

		// Next is to force boxes to be at least a minimal size
		ps.println("forall(k in rectangles_id)");
		ps.println("Width[k]>=ini_Width[k]; ");
		ps.println("forall(l in rectangles_id)");
		ps.println("Height[l]>=ini_Height[l]; ");

		ps.println("}");

		// Next section with execute DISPLAY will write the outputs in a good form.
		ps.println("execute DISPLAY");
		ps.println("{");

		ps.println("write(\"var Top = [\");");
		ps.println("for(var i in rectangles_id)");
		ps.println("{");
		ps.println("if (i!=(nb_rectangles-1))");
		ps.println("write(top[i]+\", \");");
		ps.println("else");
		ps.println("write(top[i]+\"];\\n\");");
		ps.println("}");

		ps.println("write(\"var left = [\");");
		ps.println("for(var i in rectangles_id)");
		ps.println("{");
		ps.println("if (i!=(nb_rectangles-1))");
		ps.println("write(left[i]+\", \");");
		ps.println("else");
		ps.println("write(left[i]+\"];\\n\");");
		ps.println("}");

		ps.println("write(\"var height = [\");");
		ps.println("for(var i in rectangles_id)");
		ps.println("{");
		ps.println("if (i!=(nb_rectangles-1))");
		ps.println("write(Height[i]+\", \");");
		ps.println("else");
		ps.println("write(Height[i]+\"];\\n\");");
		ps.println("}");

		ps.println("write(\"var width = [\");");
		ps.println("for(var i in rectangles_id)");
		ps.println("{");
		ps.println("if (i!=(nb_rectangles-1))");
		ps.println("write(Width[i]+\", \");");
		ps.println("else");
		ps.println("write(Width[i]+\"];\\n\");");
		ps.println("}");

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
		else if (c instanceof Contained)
		{
			renderContained(ps, (Contained) c);
		}
	}

	protected static void renderVerticallyAligned(PrintStream ps, VerticallyAligned c)
	{
		Set<Box> boxes = new HashSet<Box>(c.getBoxes().size());
		boxes.addAll(c.getBoxes());
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
			ps.println("top[" + first.getId() + "]==top[" + b.getId() + "];");
		}
	}

	protected static void renderHorizontallyAligned(PrintStream ps, HorizontallyAligned c)
	{
		Set<Box> boxes = new HashSet<Box>(c.getBoxes().size());
		boxes.addAll(c.getBoxes());
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
			ps.println("left[" + first.getId() + "]==left[" + b.getId() + "];");
		}
	}

	protected static void renderDisjoint(PrintStream ps, Disjoint c)
	{
		int b1_id = c.getFirstBox().getId();
		int b2_id = c.getSecondBox().getId();
		ps.print("top[" + b1_id + "]+Height[" + b1_id + "]<= top[" + b2_id + "] || ");
		ps.print("top[" + b2_id + "]+Height[" + b2_id + "]<= top[" + b1_id + "] || ");
		ps.print("left[" + b1_id + "]+Width[" + b1_id + "]<= left[" + b2_id + "] || ");
		ps.print("left[" + b2_id + "]+Width[" + b2_id + "]<= left[" + b1_id + "];\n");
	}

	protected static void renderContained(PrintStream ps, Contained c)
	{
		int b1_id = c.getFirstBox().getId();
		int b2_id = c.getSecondBox().getId();
		ps.print("top[" + b1_id + "]<=top[" + b2_id + "];");
		ps.print("top[" + b1_id + "]+Height[" + b1_id + "]>= top[" + b2_id + "]+Height[" + b2_id + "];");
		ps.print("left[" + b1_id + "]<=left[" + b2_id + "];");
		ps.print("left[" + b1_id + "]+Width[" + b1_id + "]>= left[" + b2_id + "]+Width[" + b2_id + "];\n");
	}
}

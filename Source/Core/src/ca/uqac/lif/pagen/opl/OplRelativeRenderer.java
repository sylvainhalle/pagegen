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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.pagen.Box;
import ca.uqac.lif.pagen.BoxDependency;
import ca.uqac.lif.pagen.BoxDependencyGraph;
import ca.uqac.lif.pagen.BoxProperty;
import ca.uqac.lif.pagen.BoxProperty.Property;
import ca.uqac.lif.pagen.LayoutConstraint.Contained;
import ca.uqac.lif.pagen.LayoutConstraint.Disjoint;
import ca.uqac.lif.pagen.LayoutConstraint.SameX;
import ca.uqac.lif.pagen.LayoutConstraint.SameY;
import ca.uqac.lif.pagen.LayoutConstraint;

/**
 * Produces a file in the OPL format based on a tree of nested boxes.
 * @author Stéphane Jacquet
 */
public class OplRelativeRenderer extends OplRenderer
{
	/**
	 * The objective function used in the OPL model
	 */
	protected static final transient String s_objectiveFunction = "minimize sum(i in xdot_id)(abs(xdot[i]))+sum(i in ydot_id)(abs(ydot[i]))+sum(i in hdot_id)(abs(hdot[i]))+sum(i in wdot_id)(abs(wdot[i]));";

	/**
	 * The graph of dependencies between DOM nodes that the renderer uses to
	 * model the variables.
	 */
	protected BoxDependencyGraph m_graph = null;

	/**
	 * The set of boxes properties that are directly involved in a constraint
	 * violation.
	 */
	/*@ non_null @*/ protected Set<BoxProperty> m_faultyBoxes;

	/**
	 * The list of box properties referring to the x-shift.
	 */
	protected List<BoxProperty> m_xDots = null;

	/**
	 * The list of box properties referring to the y-shift.
	 */
	protected List<BoxProperty> m_yDots = null;

	/**
	 * The list of box properties referring to the h-shift.
	 */
	protected List<BoxProperty> m_hDots = null;

	/**
	 * The list of box properties referring to the w-shift.
	 */
	protected List<BoxProperty> m_wDots = null;

	/**
	 * The map containing the transitive closure of the
	 * dependency graph for each node.
	 */
	protected Map<BoxProperty,Set<BoxProperty>> m_closure = null;

	@SafeVarargs
	public OplRelativeRenderer(Set<LayoutConstraint> ... constraints)
	{
		super(constraints);
		m_faultyBoxes = new HashSet<BoxProperty>();
	}

	/**
	 * Adds one or more box properties to the set of boxes that are directly
	 * involved in a constraint violation.
	 * @param boxes The boxes to add
	 * @return This renderer
	 */
	public OplRelativeRenderer addFaultyBoxProperty(BoxProperty ... properties)
	{
		for (BoxProperty bp : properties)
		{
			m_faultyBoxes.add(bp);
		}
		return this;
	}

	protected final Set<LayoutConstraint> fillFaultyBoxes()
	{
		Map<BoxProperty,Set<LayoutConstraint>> constraint_index = LayoutConstraint.indexProperties(m_graph, m_constraints);
		Queue<BoxProperty> to_consider = new ArrayDeque<BoxProperty>();
		Set<LayoutConstraint> constraints_to_consider = new HashSet<LayoutConstraint>();
		for (LayoutConstraint c : m_constraints)
		{
			if (!c.getVerdict())
			{
				constraints_to_consider.add(c);
				Set<BoxProperty> bps = c.getBoxProperties(m_graph);
				to_consider.addAll(bps);
			}
		}
		// Loop through box properties
		while (!to_consider.isEmpty())
		{
			BoxProperty bp = to_consider.remove();
			if (m_faultyBoxes.contains(bp))
			{
				continue;
			}
			m_faultyBoxes.add(bp);
			if (!constraint_index.containsKey(bp))
			{
				continue;
			}
			Set<LayoutConstraint> involved_constraints = constraint_index.get(bp);
			for (LayoutConstraint c : involved_constraints)
			{
				Set<BoxProperty> new_properties = c.getBoxProperties(m_graph, bp);
				if (!new_properties.isEmpty())
				{
					constraints_to_consider.add(c);
					for (BoxProperty new_bp : new_properties)
					{
						if (!m_faultyBoxes.contains(new_bp) && !to_consider.contains(new_bp))
						{
							to_consider.add(new_bp);
						}
					}
				}
			}
			Set<BoxDependency> new_dependencies = m_graph.getInfluences(bp);
			for (BoxDependency bd : new_dependencies)
			{
				BoxProperty new_bp = bd.getProperty();
				if (!m_faultyBoxes.contains(new_bp) && !to_consider.contains(new_bp))
				{
					to_consider.add(new_bp);
				}
			}
		}
		return constraints_to_consider;
	}

	/**
	 * Sets the dependency graph to be used for the rendering.
	 * @param graph The graph
	 * @return This renderer
	 */
	public OplRelativeRenderer setDependencyGraph(BoxDependencyGraph graph)
	{
		m_graph = graph;
		return this;
	}

	@Override
	public void render(PrintStream ps, Box root)
	{
		Set<LayoutConstraint> constraints_to_model = fillFaultyBoxes();
		m_numConstraints = constraints_to_model.size();
		System.out.print(m_constraints.size() + " vs " + constraints_to_model.size() + "; ");
		ps.println("/****************************************");
		ps.println(" * OPL 12.10.0.0 Model");
		ps.println(" * Tree size:             " + root.getSize());
		ps.println(" * Tree depth:            " + root.getDepth());
		ps.println(" * Relative modeling");
		ps.println("****************************************/");
		m_closure = m_graph.getTransitiveClosure(m_faultyBoxes);
		m_xDots = filter(m_closure, Property.DX);
		m_yDots = filter(m_closure, Property.DY);
		m_wDots = filter(m_closure, Property.DW);
		m_hDots = filter(m_closure, Property.DH);
		m_numVariables = m_xDots.size() + m_yDots.size() + m_wDots.size() + m_hDots.size();
		System.out.println(root.getSize() * 4 + " vs " + m_numVariables);
		printArray(m_xDots, "xdot", ps);
		printArray(m_yDots, "ydot", ps);
		printArray(m_wDots, "wdot", ps);
		printArray(m_hDots, "hdot", ps);
		ps.println("execute");		
		ps.println("{");
		ps.println("cplex.tilim=1000;");	
		ps.println("cplex.epgap=0.2;");	
		ps.println("}");
		ps.println(s_objectiveFunction);
		ps.println("subject to {");
		for (LayoutConstraint lc : constraints_to_model)
		{
			render(ps, lc);
		}
		ps.println("}");
		ps.println("execute DISPLAY");
		ps.println("{");
		ps.println("}");
	}

	public static void printArray(List<BoxProperty> list, String name, PrintStream ps)
	{
		// Prints the box IDs
		ps.print("{int} " + name + "_id={");
		for (int i = 0; i < list.size(); i++)
		{
			if (i > 0)
			{
				ps.print(", ");
			}
			BoxProperty bp = list.get(i);
			ps.print(bp.getBox().getId());
		}
		ps.println("}");
		// Declares the delta array
		ps.println("dvar float " + name + "[" + name + "_id" + "];");
	}

	/**
	 * Produces a sorted list of all the box properties for a given property.
	 * @param closure The map containing the transitive closure of the
	 * dependency graph for each node
	 * @param p The property to look for
	 * @return The list of box properties
	 */
	/*@ non_null @*/ public static List<BoxProperty> filter(Map<BoxProperty,Set<BoxProperty>> closure, Property p)
	{
		Set<BoxProperty> out = new HashSet<BoxProperty>();
		for (Map.Entry<BoxProperty,Set<BoxProperty>> e : closure.entrySet())
		{
			if (e.getKey().getProperty() == p)
			{
				out.add(e.getKey());
			}
			for (BoxProperty bp : e.getValue())
			{
				if (bp.getProperty() == p)
				{
					out.add(bp);
				}
			}
		}
		List<BoxProperty> sorted_out = new ArrayList<BoxProperty>(out.size());
		sorted_out.addAll(out);
		Collections.sort(sorted_out);
		return sorted_out;
	}

	protected void printTerm(PrintStream ps, BoxProperty property)
	{
		Set<BoxProperty> terms = m_closure.get(property);
		ps.print("(");
		Box b = property.getBox();
		switch (property.getProperty())
		{
		case X:
			ps.print(b.getX());
			break;
		case Y:
			ps.print(b.getY());
			break;
		case W:
			ps.print(b.getWidth());
			break;
		case H:
			ps.print(b.getHeight());
			break;
		default:
			break;
		}
		if (terms != null)
		{
			for (BoxProperty bp : terms)
			{
				if (m_faultyBoxes.contains(bp.getAbsolute()))
				{
					ps.print("+");
					printProperty(ps, bp);
				}
			}
		}
		ps.print(")");
	}

	protected void printProperty(PrintStream ps, BoxProperty bp)
	{
		switch (bp.getProperty())
		{
		case DX:
		{
			int index = m_xDots.indexOf(bp);
			ps.print("xdot[" + index + "]");
			break;
		}
		case DY:
		{
			int index = m_yDots.indexOf(bp);
			ps.print("ydot[" + index + "]");
			break;
		}
		case DW:
		{
			int index = m_wDots.indexOf(bp);
			ps.print("wdot[" + index + "]");
			break;
		}
		case DH:
		{
			int index = m_hDots.indexOf(bp);
			ps.print("hdot[" + index + "]");
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void renderSameY(PrintStream ps, SameY c)
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
		BoxProperty first_property = BoxProperty.get(first, Property.Y);
		for (Box b : boxes)
		{
			BoxProperty other_property = BoxProperty.get(b, Property.Y);
			printTerm(ps, first_property);
			ps.print("==");
			printTerm(ps, other_property);
			ps.println(";");
			m_numConstraints++;
		}
	}

	@Override
	protected void renderSameX(PrintStream ps, SameX c)
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
		BoxProperty first_property = BoxProperty.get(first, Property.X);
		for (Box b : boxes)
		{
			BoxProperty other_property = BoxProperty.get(b, Property.X);
			printTerm(ps, first_property);
			ps.print("==");
			printTerm(ps, other_property);
			ps.println(";");
			m_numConstraints++;
		}
	}

	@Override
	protected void renderDisjoint(PrintStream ps, Disjoint c)
	{
		Box b1 = c.getFirstBox();
		Box b2 = c.getSecondBox();
		BoxProperty b1_x = BoxProperty.get(b1, Property.X);
		BoxProperty b1_y = BoxProperty.get(b1, Property.Y);
		BoxProperty b1_h = BoxProperty.get(b1, Property.H);
		BoxProperty b1_w = BoxProperty.get(b1, Property.W);
		BoxProperty b2_x = BoxProperty.get(b2, Property.X);
		BoxProperty b2_y = BoxProperty.get(b2, Property.Y);
		BoxProperty b2_h = BoxProperty.get(b2, Property.H);
		BoxProperty b2_w = BoxProperty.get(b2, Property.W);
		// b1_y + b1_h <= b2_y
		printTerm(ps, b1_y);
		ps.print("+");
		printTerm(ps, b1_h);
		ps.print(" <= ");
		printTerm(ps, b2_y);
		ps.print(" || ");
		// b2_y + b2_h <= b1_y
		printTerm(ps, b2_y);
		ps.print("+");
		printTerm(ps, b2_h);
		ps.print(" <= ");
		printTerm(ps, b1_y);
		ps.print(" || ");
		// b1_x + b1_w <= b2_x
		printTerm(ps, b1_x);
		ps.print("+");
		printTerm(ps, b1_w);
		ps.print(" <= ");
		printTerm(ps, b2_x);
		ps.print(" || ");
		// b2_x + b2_w <= b1_x
		printTerm(ps, b2_x);
		ps.print("+");
		printTerm(ps, b2_w);
		ps.print(" <= ");
		printTerm(ps, b1_x);
		ps.println(";");
		m_numConstraints++;
	}

	@Override
	protected void renderContained(PrintStream ps, Contained c)
	{
		Box b1 = c.getFirstBox();
		Box b2 = c.getSecondBox();
		BoxProperty b1_x = BoxProperty.get(b1, Property.X);
		BoxProperty b1_y = BoxProperty.get(b1, Property.Y);
		BoxProperty b1_h = BoxProperty.get(b1, Property.H);
		BoxProperty b1_w = BoxProperty.get(b1, Property.W);
		BoxProperty b2_x = BoxProperty.get(b2, Property.X);
		BoxProperty b2_y = BoxProperty.get(b2, Property.Y);
		BoxProperty b2_h = BoxProperty.get(b2, Property.H);
		BoxProperty b2_w = BoxProperty.get(b2, Property.W);
		// b1_y <= b2_y
		printTerm(ps, b1_y);
		ps.print(" <= ");
		printTerm(ps, b2_y);
		ps.println(";");
		// b1_y + b1_h >= b2_y + b2_h
		printTerm(ps, b1_y);
		ps.print("+");
		printTerm(ps, b1_h);
		ps.print(" >= ");
		printTerm(ps, b2_y);
		ps.print("+");
		printTerm(ps, b2_h);
		ps.println(";");
		// b1_x <= b2_x
		printTerm(ps, b1_x);
		ps.print(" <= ");
		printTerm(ps, b2_x);
		ps.println(";");
		// b1_x + b1_w >= b2_x + b2_w
		printTerm(ps, b1_x);
		ps.print("+");
		printTerm(ps, b1_w);
		ps.print(" >= ");
		printTerm(ps, b2_x);
		ps.print("+");
		printTerm(ps, b2_w);
		ps.println(";");
		m_numConstraints += 4;
	}
}

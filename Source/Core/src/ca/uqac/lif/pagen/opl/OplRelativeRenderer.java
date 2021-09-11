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
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.pagen.Box;
import ca.uqac.lif.pagen.BoxDependency;
import ca.uqac.lif.pagen.BoxDependencyGraph;
import ca.uqac.lif.pagen.BoxProperty;
import ca.uqac.lif.pagen.LayoutConstraint;

/**
 * Produces a file in the OPL format based on a tree of nested boxes.
 * @author Stéphane Jacquet
 */
public class OplRelativeRenderer extends OplRenderer
{
	/**
	 * The graph of dependencies between DOM nodes that the renderer uses to
	 * model the variables.
	 */
	protected BoxDependencyGraph m_graph = null;
	
	/**
	 * The set of boxes properties that are directly involved in a constraint
	 * violation.
	 */
	protected Set<BoxProperty> m_faultyBoxes;
	
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
		Set<BoxProperty> properties_to_model = getPropertiesToModel();
		for (BoxProperty bp : properties_to_model)
		{
			
		}
	}
	
	/**
	 * Fetches all the box properties that are impacted by the faulty boxes
	 * @return
	 */
	protected Set<BoxProperty> getPropertiesToModel()
	{
		Set<BoxProperty> properties_to_model = new HashSet<BoxProperty>();
		// Step 1: fetch all box properties that are impacted by the faulty boxes
		for (BoxProperty start : m_faultyBoxes)
		{
			properties_to_model.add(start);
			
		}
		return properties_to_model;
	}
}

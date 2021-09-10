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
import java.util.Set;

import ca.uqac.lif.pagen.Box;
import ca.uqac.lif.pagen.BoxDependencyGraph;
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
	protected BoxDependencyGraph m_graph;
	
	@SafeVarargs
	public OplRelativeRenderer(Set<LayoutConstraint> ... constraints)
	{
		super(constraints);
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
	public void render(PrintStream ps, Box b)
	{
		// TODO Auto-generated method stub
		
	}
	
}

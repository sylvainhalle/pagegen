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
package ca.uqac.lif.pagen;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BoxDependencyGraph
{
	/**
	 * A map that associates box properties to the downstream dependencies
	 * it is associated to.
	 */
	protected Map<BoxProperty,Set<BoxDependency>> m_dependencies;
	
	/**
	 * Creates a new empty box dependency graph.
	 */
	public BoxDependencyGraph()
	{
		super();
		m_dependencies = new HashMap<BoxProperty,Set<BoxDependency>>();
	}
	
	/**
	 * Adds a set of dependencies to the graph.
	 * @param dependencies The set of dependencies
	 * @return This graph
	 */
	public BoxDependencyGraph add(Set<BoxDependency> dependencies)
	{
		for (BoxDependency bd : dependencies)
		{
			add(bd.getProperty(), bd.getInfluencedBy());
		}
		return this;
	}
	
	/**
	 * Adds a dependency between two box properties to the graph. Conceptually,
	 * this corresponds to the creation of an edge between two nodes.
	 * @param bp1 The first box property
	 * @param bp2 The second box property
	 * @return This graph
	 */
	public BoxDependencyGraph add(BoxProperty bp1, BoxProperty bp2)
	{
		Set<BoxDependency> deps = null;
		if (m_dependencies.containsKey(bp1))
		{
			deps = m_dependencies.get(bp1);
		}
		else
		{
			deps = new HashSet<BoxDependency>();
			m_dependencies.put(bp1, deps);
		}
		deps.add(new BoxDependency(bp1, bp2));
		if (!m_dependencies.containsKey(bp2))
		{
			m_dependencies.put(bp2, new HashSet<BoxDependency>());
		}
		return this;
	}
	
	/**
	 * Adds a dependency between two box properties to the graph. Conceptually,
	 * this corresponds to the creation of an edge between two nodes.
	 * @param b1 The first box
	 * @param p1 The first property
	 * @param b2 The second box
	 * @param p2 The second property
	 * @return This graph
	 */
	public BoxDependencyGraph add(Box b1, BoxProperty.Property p1, Box b2, BoxProperty.Property p2)
	{
		return add(BoxProperty.get(b1, p1), BoxProperty.get(b2, p2));
	}
	
	/**
	 * Gets the set of downstream dependencies for a given box property.
	 * @param bp The box property
	 * @return The set of other box dependencies
	 */
	/*@ non_null @*/ public Set<BoxDependency> getDependencies(BoxProperty bp)
	{
		if (m_dependencies.containsKey(bp))
		{
			return m_dependencies.get(bp);
		}
		return new HashSet<BoxDependency>(0);
	}
	
	/*@ pure non_null @*/ public Map<BoxProperty,Set<BoxProperty>> getTransitiveClosure(Set<BoxProperty> start_list)
	{
		Map<BoxProperty,Set<BoxProperty>> mapping = new HashMap<BoxProperty,Set<BoxProperty>>();
		if (start_list.isEmpty())
		{
			start_list.addAll(m_dependencies.keySet());
		}
		for (BoxProperty start : start_list)
		{
			Set<BoxProperty> visited = new HashSet<BoxProperty>();
			Set<BoxProperty> deps = new HashSet<BoxProperty>();
			Queue<BoxProperty> to_explore = new ArrayDeque<BoxProperty>();
			to_explore.add(start);
			while (!to_explore.isEmpty())
			{
				BoxProperty bp = to_explore.remove();
				if (visited.contains(bp))
				{
					continue;
				}
				visited.add(bp);
				deps.add(bp.getDelta());
				Set<BoxDependency> children = getDependencies(bp);
				for (BoxDependency bd : children)
				{
					BoxProperty child_bp = bd.getInfluencedBy();
					if (!deps.contains(child_bp) && !to_explore.contains(child_bp) 
							&& !start.getBox().equals(child_bp.getBox()))
					{
						to_explore.add(child_bp);
					}
				}
			}
			mapping.put(start, deps);
		}
		return mapping;
	}
	
	/*@ pure non_null @*/ public Map<BoxProperty,Set<BoxProperty>> getTransitiveClosure(BoxProperty ... starting_points)
	{
		Set<BoxProperty> start_list = new HashSet<BoxProperty>();
		for (BoxProperty bp : starting_points)
		{
			start_list.add(bp);
		}
		return getTransitiveClosure(start_list);
	}
	
	/**
	 * Gets the set of downstream dependencies for a given box and a given
	 * property. 
	 * @param b The box
	 * @param p The property
	 * @return The set of other box dependencies
	 */
	/*@ pure non_null @*/ public Set<BoxDependency> getDependencies(Box b, BoxProperty.Property p)
	{
		BoxProperty bp1 = BoxProperty.get(b, p);
		if (m_dependencies.containsKey(bp1))
		{
			return m_dependencies.get(bp1);
		}
		return new HashSet<BoxDependency>(0);
	}
}

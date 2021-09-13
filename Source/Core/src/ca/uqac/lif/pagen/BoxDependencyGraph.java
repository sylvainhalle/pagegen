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

import ca.uqac.lif.pagen.BoxProperty.Property;

public class BoxDependencyGraph
{
	/**
	 * A map that associates box properties to the downstream dependencies
	 * it is associated to.
	 */
	protected Map<BoxProperty,Set<BoxDependency>> m_influencedBy;

	/**
	 * A map that associates box properties to the downstream dependencies
	 * it is associated to.
	 */
	protected Map<BoxProperty,Set<BoxDependency>> m_influences;

	/**
	 * Creates a new empty box dependency graph.
	 */
	public BoxDependencyGraph()
	{
		super();
		m_influencedBy = new HashMap<BoxProperty,Set<BoxDependency>>();
		m_influences = new HashMap<BoxProperty,Set<BoxDependency>>();
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
	 * Gets the set of all box properties represented in this graph.
	 * @return The set of properties
	 */
	public Set<BoxProperty> getNodes()
	{
		return m_influencedBy.keySet();
	}

	/**
	 * Adds a dependency between two box properties to the graph. Conceptually,
	 * this corresponds to the creation of an edge between two nodes,
	 * stating that the first property <em>is influenced by</em> the second.
	 * @param bp1 The first box property (the one that is influenced)
	 * @param bp2 The second box property (the one that influences)
	 * @return This graph
	 */
	public BoxDependencyGraph add(BoxProperty bp1, BoxProperty bp2)
	{
		BoxDependency bd = new BoxDependency(bp1, bp2);
		{
			Set<BoxDependency> deps = null;
			if (m_influencedBy.containsKey(bp1))
			{
				deps = m_influencedBy.get(bp1);
			}
			else
			{
				deps = new HashSet<BoxDependency>();
				m_influencedBy.put(bp1, deps);
			}
			deps.add(bd);
			if (!m_influencedBy.containsKey(bp2))
			{
				m_influencedBy.put(bp2, new HashSet<BoxDependency>());
			}
		}
		{
			Set<BoxDependency> deps = null;
			if (m_influences.containsKey(bp2))
			{
				deps = m_influences.get(bp2);
			}
			else
			{
				deps = new HashSet<BoxDependency>();
				m_influences.put(bp2, deps);
			}
			deps.add(bd);
			if (!m_influences.containsKey(bp1))
			{
				m_influences.put(bp1, new HashSet<BoxDependency>());
			}
		}
		return this;
	}

	/**
	 * Adds a dependency between two box properties to the graph. Conceptually,
	 * this corresponds to the creation of an edge between two nodes,
	 * stating that the first property <em>is influenced by</em> the second.
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

	/*@ pure non_null @*/ public Map<BoxProperty,Set<BoxProperty>> getTransitiveClosure(Set<BoxProperty> start_list)
	{
		Map<BoxProperty,Set<BoxProperty>> mapping = new HashMap<BoxProperty,Set<BoxProperty>>();
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
				//if (start_list.contains(bp))
				{
					deps.add(bp.getDelta());
				}
				Set<BoxDependency> children = getInfluencedBy(bp);
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
		if (starting_points.length == 0)
		{
			start_list.addAll(m_influencedBy.keySet());
		}
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
		return getInfluencedBy(BoxProperty.get(b, p));
	}
	
	/**
	 * Gets the set of dependencies that influence a given box property.
	 * @param b The box property
	 * @return The set of other box dependencies
	 */
	public Set<BoxDependency> getInfluencedBy(BoxProperty bp)
	{
		return getInfluencedBy(bp, false);
	}
	
	/**
	 * Determines if box b1 influences box b2
	 * @param b1 The first box
	 * @param b2 The second box
	 * @return {@code true} if b1 influences b2, {@code false} otherwise
	 */
	public boolean influences(Box b1, Box b2)
	{
		BoxProperty bp1 = BoxProperty.get(b1, Property.X);
		Set<BoxDependency> deps_x = getInfluences(bp1, true);
		for (BoxDependency bd : deps_x)
		{
			if (bd.getProperty().getBox().equals(b2))
			{
				return true;
			}
		}
		Set<BoxDependency> deps_y = getInfluences(bp1, true);
		for (BoxDependency bd : deps_y)
		{
			if (bd.getProperty().getBox().equals(b2))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the set of dependencies that influence a given box property.
	 * @param b The box property
	 * @param transitive Set to {@code true} to perform a transitive scan
	 * @return The set of other box properties
	 */
	public Set<BoxDependency> getInfluencedBy(BoxProperty bp, boolean transitive)
	{
		if (!transitive)
		{
			if (m_influencedBy.containsKey(bp))
			{
				return m_influencedBy.get(bp);
			}
			return new HashSet<BoxDependency>(0);
		}
		Set<BoxDependency> out = new HashSet<BoxDependency>();
		Queue<BoxDependency> to_visit = new ArrayDeque<BoxDependency>();
		Box start_box = bp.getBox();
		if (m_influencedBy.containsKey(bp))
		{
			to_visit.addAll(m_influencedBy.get(bp));
		}
		while (!to_visit.isEmpty())
		{
			BoxDependency bd_current = to_visit.remove();
			if (out.contains(bd_current))
			{
				continue;
			}
			out.add(bd_current);
			BoxProperty bp_target = bd_current.getInfluencedBy();
			if (!bp_target.getBox().equals(start_box) && m_influencedBy.containsKey(bp_target))
			{
				to_visit.addAll(m_influencedBy.get(bp_target));
			}
		}
		return out;
	}
	
	/**
	 * Gets the set of dependencies that are influenced by a given box
	 * property.
	 * @param b The box property
	 * @return The set of other box dependencies
	 */
	public Set<BoxDependency> getInfluences(BoxProperty bp)
	{
		return getInfluences(bp, false);
	}

	/**
	 * Gets the set of box properties that are influenced by a given box
	 * property.
	 * @param b The box property
	 * @param transitive Set to {@code true} to perform a transitive scan
	 * @return The set of other box properties
	 */
	public Set<BoxDependency> getInfluences(BoxProperty bp, boolean transitive)
	{
		if (!transitive)
		{
			if (m_influences.containsKey(bp))
			{
				return m_influences.get(bp);
			}
			return new HashSet<BoxDependency>(0);
		}
		Set<BoxDependency> out = new HashSet<BoxDependency>();
		Queue<BoxDependency> to_visit = new ArrayDeque<BoxDependency>();
		Box start_box = bp.getBox();
		if (m_influences.containsKey(bp))
		{
			to_visit.addAll(m_influences.get(bp));
		}
		while (!to_visit.isEmpty())
		{
			BoxDependency bd_current = to_visit.remove();
			if (out.contains(bd_current))
			{
				continue;
			}
			out.add(bd_current);
			BoxProperty bp_target = bd_current.getProperty();
			if (!bp_target.getBox().equals(start_box) && m_influences.containsKey(bp_target))
			{
				to_visit.addAll(m_influences.get(bp_target));
			}
		}
		return out;
	}
}

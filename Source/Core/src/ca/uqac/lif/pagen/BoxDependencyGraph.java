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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoxDependencyGraph
{
	/**
	 * A map that associates box properties to the downstream dependencies
	 * it is associated to.
	 */
	protected Map<BoxProperty,Set<BoxDependency>> m_dependencies;
	
	protected Map<BoxProperty,BoxProperty> m_propertyPool;
	
	public BoxDependencyGraph()
	{
		super();
		m_dependencies = new HashMap<BoxProperty,Set<BoxDependency>>();
		m_propertyPool = new HashMap<BoxProperty,BoxProperty>();
	}
	
	public BoxDependencyGraph add(Box b1, BoxProperty.Property p1, Box b2, BoxProperty.Property p2)
	{
		BoxProperty bp1 = new BoxProperty(b1, p1);
		if (m_propertyPool.containsKey(bp1))
		{
			bp1 = m_propertyPool.get(bp1);
		}
		BoxProperty bp2 = new BoxProperty(b2, p2);
		if (m_propertyPool.containsKey(bp2))
		{
			bp2 = m_propertyPool.get(bp2);
		}
		Set<BoxDependency> deps = null;
		if (m_dependencies.containsKey(bp1))
		{
			deps = m_dependencies.get(bp1);
		}
		else
		{
			deps = new HashSet<BoxDependency>();
		}
		deps.add(new BoxDependency(bp1, bp2));
		return this;
	}
	
	/*@ non_null @*/ public Set<BoxDependency> getDependencies(Box b, BoxProperty.Property p)
	{
		BoxProperty bp1 = new BoxProperty(b, p);
		if (m_propertyPool.containsKey(bp1))
		{
			bp1 = m_propertyPool.get(bp1);
		}
		if (m_dependencies.containsKey(bp1))
		{
			return m_dependencies.get(bp1);
		}
		return new HashSet<BoxDependency>(0);
	}
}

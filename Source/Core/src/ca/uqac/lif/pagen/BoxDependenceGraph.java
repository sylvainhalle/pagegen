package ca.uqac.lif.pagen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BoxDependenceGraph
{
	/**
	 * A map that associates box properties to the downstream dependences
	 * it is associated to.
	 */
	protected Map<BoxProperty,Set<BoxDependence>> m_dependencies;
	
	public BoxDependenceGraph()
	{
		super();
		m_dependencies = new HashMap<BoxProperty,Set<BoxDependence>>();
	}
}

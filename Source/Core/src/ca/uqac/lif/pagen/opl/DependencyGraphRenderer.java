/*
  A lab for web site hotfix benchmarking
  Copyright (C) 2020-2021 Xavier Chamberland-Thibeault,
  Stéphane Jacquet and Sylvain Hallé

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.pagen.opl;

import java.io.PrintStream;
import java.util.Set;

import ca.uqac.lif.pagen.Box;
import ca.uqac.lif.pagen.BoxDependency;
import ca.uqac.lif.pagen.BoxProperty;
import ca.uqac.lif.pagen.LayoutConstraint;
import ca.uqac.lif.pagen.ObjectIdentifier;

public class DependencyGraphRenderer extends OplRelativeRenderer
{
	@SafeVarargs
	public DependencyGraphRenderer(Set<LayoutConstraint> ... constraints)
	{
		super(constraints);
	}

	@Override
	public void render(PrintStream ps, Box root)
	{
		fillFaultyBoxes();
		ObjectIdentifier<BoxProperty> identifier = new ObjectIdentifier<BoxProperty>();
		ps.println("digraph G {");
		ps.println("node [shape=\"circle\",style=\"filled\",height=0.4,width=0.4,fixedsize=\"true\"];");
		for (BoxProperty bp : m_graph.getNodes())
		{
			if (!identifier.seenBefore(bp))
			{
				renderNode(ps, bp, identifier.getObjectId(bp));
			}
			Set<BoxDependency> deps = m_graph.getInfluencedBy(bp);
			for (BoxDependency bd : deps)
			{
				BoxProperty other = bd.getInfluencedBy();
				if (!identifier.seenBefore(other))
				{
					renderNode(ps, other, identifier.getObjectId(other));
				}
				ps.print(identifier.getObjectId(other) + " -> " + identifier.getObjectId(bp));
				if (!m_faultyBoxes.contains(bp) || !m_faultyBoxes.contains(other))
				{
					ps.print(" [color=\"gainsboro\"]");
				}
				else if (bp.getBox().getChildren().contains(other.getBox()) || 
						other.getBox().getChildren().contains(bp.getBox()))
				{
					ps.print(" [color=\"black:white:black\"]");
				}
				ps.println(";");
			}
		}
		ps.println("}");
	}

	protected void renderNode(PrintStream ps, BoxProperty node, int id)
	{
		String fillcolor = "white";
		String color = "black";
		String label = "<";
		if (m_faultyBoxes.contains(node))
		{
			switch (node.getProperty())
			{
			case X:
				fillcolor = "cyan3";
				label += "x";
				break;
			case Y:
				fillcolor = "deeppink";
				label += "y";
				break;
			case H:
				fillcolor = "darkorchid";
				label += "h";
				break;
			case W:
				fillcolor = "dodgerblue3";
				label += "w";
				break;
			default:
				break;
			}
		}
		else
		{
			color = "gainsboro";
			switch (node.getProperty())
			{
			case X:
				fillcolor = "lightcyan1";
				label += "x";
				break;
			case Y:
				fillcolor = "lightpink";
				label += "y";
				break;
			case H:
				fillcolor = "mediumorchid1";
				label += "h";
				break;
			case W:
				fillcolor = "lightskyblue3";
				label += "w";
				break;
			default:
				break;
			}
		}
		label += "<sub>" + node.getBox().getId() + "</sub>>";
		ps.println(id + " [fillcolor=\"" + fillcolor + "\",label=" + label + ",color=\"" + color + "\"];");
	}


}

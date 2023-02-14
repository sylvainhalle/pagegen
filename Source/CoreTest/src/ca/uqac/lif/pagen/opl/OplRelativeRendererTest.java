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

import static ca.uqac.lif.pagen.BoxProperty.Property.X;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ca.uqac.lif.pagen.Box;
import ca.uqac.lif.pagen.BoxDependencyGraph;
import ca.uqac.lif.pagen.LayoutConstraint;

import static ca.uqac.lif.pagen.LayoutConstraint.Disjoint;
import static ca.uqac.lif.pagen.LayoutConstraint.SameX;

public class OplRelativeRendererTest
{
	protected static final Box A = new Box(0, 0, 100, 80);
	protected static final Box B = new Box(10, 9, 30, 30);
	protected static final Box C = new Box(50, 8, 20, 20);
	protected static final Box D = new Box(0, 50, 15, 15);
	protected static final Box E = new Box(50, 30, 25, 15);
	
	@Test
	public void test1()
	{
		BoxDependencyGraph g = new BoxDependencyGraph();
		g.add(B, X, A, X);
		//g.add(B, Y, A, Y);
		g.add(C, X, A, X);
		g.add(E, X, C, X);
		g.add(D, X, B, X);
		g.add(E, X, B, X);
		Set<LayoutConstraint> constraints = new HashSet<LayoutConstraint>();
		LayoutConstraint va = new SameX().add(B).add(C);
		constraints.add(va);
		constraints.add(new Disjoint(C, E));
		OplRelativeRenderer renderer = new OplRelativeRenderer(constraints);
		renderer.setDependencyGraph(g);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		renderer.render(ps, A);
		System.out.println(baos.toString());
	}
}

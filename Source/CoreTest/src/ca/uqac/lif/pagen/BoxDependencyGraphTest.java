/*
    A random DOM tree generator
    Copyright (C) 2020 Sylvain Hallé

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

import org.junit.Test;

import ca.uqac.lif.pagen.Box;
import ca.uqac.lif.pagen.BoxDependencyGraph;
import ca.uqac.lif.pagen.BoxProperty;

import static ca.uqac.lif.pagen.BoxProperty.Property.DW;
import static ca.uqac.lif.pagen.BoxProperty.Property.DX;
import static ca.uqac.lif.pagen.BoxProperty.Property.W;
import static ca.uqac.lif.pagen.BoxProperty.Property.X;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

public class BoxDependencyGraphTest
{
	protected static final Box A = new Box(0, 0, 0, 0);
	protected static final Box B = new Box(0, 0, 0, 0);
	protected static final Box C = new Box(0, 0, 0, 0);
	protected static final Box D = new Box(0, 0, 0, 0);
	protected static final Box E = new Box(0, 0, 0, 0);

	@Test
	public void test1()
	{
		BoxDependencyGraph g = new BoxDependencyGraph();
		g.add(B, X, A, X);
		g.add(C, X, A, X);
		g.add(D, X, B, X);
		g.add(E, X, B, X);
		Map<BoxProperty,Set<BoxProperty>> closure = g.getTransitiveClosure();
		assertEquals(5, closure.size());
		{
			Set<BoxProperty> nodes = closure.get(BoxProperty.get(B, X));
			assertEquals(2, nodes.size());
			assertTrue(nodes.contains(BoxProperty.get(A, DX)));
			assertTrue(nodes.contains(BoxProperty.get(B, DX)));
		}
		{
			Set<BoxProperty> nodes = closure.get(BoxProperty.get(D, X));
			assertEquals(3, nodes.size());
			assertTrue(nodes.contains(BoxProperty.get(A, DX)));
			assertTrue(nodes.contains(BoxProperty.get(B, DX)));
			assertTrue(nodes.contains(BoxProperty.get(D, DX)));
		}
		{
			Set<BoxProperty> nodes = closure.get(BoxProperty.get(A, X));
			assertEquals(1, nodes.size());
			assertTrue(nodes.contains(BoxProperty.get(A, DX)));
		}
	}
	
	@Test
	public void test2()
	{
		BoxDependencyGraph g = new BoxDependencyGraph();
		g.add(B, X, A, X);
		g.add(A, X, C, W);
		g.add(C, W, B, W);
		g.add(C, W, D, X);
		g.add(B, W, E, W);
		Map<BoxProperty,Set<BoxProperty>> closure = g.getTransitiveClosure();
		assertEquals(6, closure.size());
		{
			Set<BoxProperty> nodes = closure.get(BoxProperty.get(B, X));
			assertEquals(4, nodes.size());
			assertTrue(nodes.contains(BoxProperty.get(B, DX)));
			assertTrue(nodes.contains(BoxProperty.get(A, DX)));
			assertTrue(nodes.contains(BoxProperty.get(C, DW)));
			assertTrue(nodes.contains(BoxProperty.get(D, DX)));
		}
	}
	
	@Test
	public void test3()
	{
		BoxDependencyGraph g = new BoxDependencyGraph();
		g.add(B, X, A, X);
		g.add(C, X, A, X);
		g.add(D, X, B, X);
		g.add(E, X, B, X);
		Map<BoxProperty,Set<BoxProperty>> closure = g.getTransitiveClosure(BoxProperty.get(B, X));
		assertEquals(1, closure.size());
		{
			Set<BoxProperty> nodes = closure.get(BoxProperty.get(B, X));
			assertEquals(2, nodes.size());
			assertTrue(nodes.contains(BoxProperty.get(A, DX)));
			assertTrue(nodes.contains(BoxProperty.get(B, DX)));
		}
		{
			assertFalse(closure.containsKey(BoxProperty.get(D, X)));
		}
	}
}

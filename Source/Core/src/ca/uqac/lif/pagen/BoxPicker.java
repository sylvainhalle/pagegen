package ca.uqac.lif.pagen;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Constant;

public class BoxPicker implements Picker<Box>
{
	protected Picker<Integer> m_numChildren;

	protected Picker<Integer> m_depth;

	protected Picker<Float> m_width;

	protected Picker<Float> m_height;

	protected Picker<LayoutManager> m_layout;

	public BoxPicker(Picker<Integer> children, Picker<Integer> depth, Picker<LayoutManager> layout, Picker<Float> width, Picker<Float> height)
	{
		super();
		m_numChildren = children;
		m_depth = depth;
		m_width = width;
		m_height = height;
		m_layout = layout;
	}

	@Override
	public Picker<Box> duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Box pick()
	{
		int num_children = m_numChildren.pick();
		List<Box> children = new ArrayList<Box>(num_children);
		for (int i = 0; i < num_children; i++)
		{
			Box b = null;
			int depth = m_depth.pick();
			if (depth == 0)
			{
				b = new Box(0f, 0f, m_width.pick(), m_height.pick());
				b.setPadding(2);
			}
			else
			{
				BoxPicker bp = new BoxPicker(m_numChildren, new Constant<Integer>(depth - 1), m_layout, m_width, m_height);
				b = bp.pick();
			}
			children.add(b);
		}
		Box parent = new Box(0, 0, 0, 0);
		parent.setPadding(2);
		LayoutManager layout = m_layout.pick();
		layout.arrange(parent, children);
		return parent;
	}

	@Override
	public void reset() 
	{
		m_numChildren.reset();
		m_depth.reset();
	}
}

package ca.uqac.lif.pagen;

import java.util.List;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Constant;

public class HorizontalFlowLayout extends FlowLayout
{
	/**
	 * Creates a new horizontal flow layout manager
	 * @param max_elements The maximum number of elements to 
	 * flow before moving to the next line. Set to 0 to flow all elements
	 * on the same line
	 */
	public HorizontalFlowLayout(Picker<Integer> max_elements)
	{
		super(max_elements);
	}
	
	/**
	 * Creates a new horizontal flow layout manager
	 */
	public HorizontalFlowLayout()
	{
		this(new Constant<Integer>(0));
	}
	
	@Override
	public void arrange(Box parent, List<Box> children)
	{
		if (children.isEmpty())
		{
			return;
		}
		int max_elements = m_maxElements.pick();
		float p_padding = parent.getPadding();
		float x = p_padding, y = p_padding, max_y = 0;
		float bounding_w = 0, bounding_h = 0;
		int n = 0;
		LayoutConstraint.VerticallyAligned const_align = new LayoutConstraint.VerticallyAligned();
		for (Box b : children)
		{
			parent.addChild(b);
			b.shiftX(x);
			b.shiftY(y);
			bounding_w = Math.max(bounding_w, x + b.getWidth());
			x += b.getWidth() + m_spacing;
			bounding_h = Math.max(bounding_h, y + b.getHeight());
			max_y = Math.max(max_y, b.getHeight());
			n++;
			const_align.add(b);
			if (max_elements > 0 && n == max_elements)
			{
				n = 0;
				x = p_padding;
				y += max_y + m_spacing;
				max_y = 0;
				if (const_align.isValid())
				{
					m_constraints.add(const_align);
				}
				const_align = new LayoutConstraint.VerticallyAligned();
			}
		}
		if (const_align.isValid())
		{
			m_constraints.add(const_align);
		}
		parent.setWidth(bounding_w + p_padding);
		parent.setHeight(bounding_h + p_padding);
	}
}

package ca.uqac.lif.pagen;

import java.util.List;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Constant;

public class VerticalFlowLayout extends FlowLayout
{
	/**
	 * Creates a new horizontal flow layout manager
	 * @param max_elements The maximum number of elements to 
	 * flow before moving to the next line. Set to 0 to flow all elements
	 * on the same line
	 */
	public VerticalFlowLayout(Picker<Integer> max_elements)
	{
		super(max_elements);
	}
	
	/**
	 * Creates a new horizontal flow layout manager
	 */
	public VerticalFlowLayout()
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
		float x = p_padding, y = p_padding, max_x = 0;
		float bounding_w = 0, bounding_h = 0;
		int n = 0;
		LayoutConstraint.HorizontallyAligned const_align = new LayoutConstraint.HorizontallyAligned();
		for (Box b : children)
		{
			parent.addChild(b);
			boolean toss = m_injectAlignementFault.pick();
			float x_shift = 0;
			if (toss)
			{
				m_misalignmentCount++;
				x_shift = m_shiftPicker.pick();
				b.alter();
			}
			b.shiftX(x + x_shift);
			b.shiftY(y);
			bounding_h = Math.max(bounding_h, y + b.getHeight());
			y += b.getHeight() + m_spacing;
			bounding_w = Math.max(bounding_w, x + b.getWidth() + x_shift);
			max_x = Math.max(max_x, b.getWidth());
			n++;
			const_align.add(b);
			if (max_elements > 0 && n == max_elements)
			{
				n = 0;
				y = p_padding;
				x += max_x + m_spacing;
				max_x = 0;
				if (const_align.isValid())
				{
					m_constraints.add(const_align);
				}
				const_align = new LayoutConstraint.HorizontallyAligned();
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

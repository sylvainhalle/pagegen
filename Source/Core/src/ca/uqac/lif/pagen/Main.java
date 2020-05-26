package ca.uqac.lif.pagen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomIntervalFloat;
import ca.uqac.lif.synthia.util.Constant;
import ca.uqac.lif.synthia.util.ElementPicker;
import ca.uqac.lif.synthia.random.PoissonInteger;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;

public class Main {

	public static void main(String[] args) throws FileNotFoundException 
	{
		Picker<Integer> depth = new RandomInteger(4, 5); // 6-22
		Picker<Integer> degree = new Constant<Integer>(2);//new PoissonInteger(2.3);
		Picker<Float> width = new RandomIntervalFloat(5, 20);
		Picker<Float> height = new RandomIntervalFloat(5, 10);
		Picker<Float> float_source = new RandomFloat();
		Picker<Integer> row_size = new RandomInteger(0, 10);
		Picker<Integer> column_size = new RandomInteger(0, 10);
		Picker<Boolean> misalignment = new RandomBoolean(0.1);
		Picker<Integer> misalignment_shift = new RandomInteger(2, 10);
		ElementPicker<LayoutManager> layout = new ElementPicker<LayoutManager>(float_source);
		Picker<String> color = new ColorPicker(new RandomInteger(128, 255));
		HorizontalFlowLayout hfl_1 = new HorizontalFlowLayout();
		hfl_1.setAlignmentFault(misalignment, misalignment_shift);
		HorizontalFlowLayout hfl_2 = new HorizontalFlowLayout(row_size);
		hfl_2.setAlignmentFault(misalignment, misalignment_shift);
		VerticalFlowLayout vfl_1 = new VerticalFlowLayout(column_size);
		vfl_1.setAlignmentFault(misalignment, misalignment_shift);
		layout.add(hfl_1, 0.2);
		layout.add(hfl_2, 0.4);
		layout.add(vfl_1, 0.4);
		RandomBoxPicker box_picker = new RandomBoxPicker(degree, depth, layout, width, height);
		Box b = box_picker.pick();
		renderToFile(b, new HtmlRenderer(color), "/tmp/out.html");
		renderToFile(b, new TextRenderer(), "/tmp/out.txt");
		//renderToFile(b, new DotRenderer(), "/tmp/out.dot");
		System.out.println(hfl_1.getConstraints());
		System.out.println(hfl_2.getConstraints());
		System.out.println(vfl_1.getConstraints());
		System.out.println(b.getSize());
		System.out.println(hfl_1.getMisalignmentCount());
		System.out.println(hfl_2.getMisalignmentCount());
		System.out.println(vfl_1.getMisalignmentCount());
	}
	
	public static void renderToFile(Box b, BoxRenderer r, String filename) throws FileNotFoundException
	{
		PrintStream ps = new PrintStream(new FileOutputStream(new File(filename)));
		r.render(ps, b);
		ps.close();
	}

}

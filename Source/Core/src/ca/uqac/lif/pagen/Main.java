package ca.uqac.lif.pagen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomIntervalFloat;
import ca.uqac.lif.synthia.util.ElementPicker;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;

public class Main {

	public static void main(String[] args) throws FileNotFoundException 
	{
		Picker<Integer> depth = new RandomInteger(6, 15); // 6-22
		Picker<Integer> degree = new RandomInteger(1, 10);
		Picker<Float> width = new RandomIntervalFloat(1, 30);
		Picker<Float> height = new RandomIntervalFloat(1, 20);
		Picker<Float> float_source = new RandomFloat();
		ElementPicker<LayoutManager> layout = new ElementPicker<LayoutManager>(float_source);
		Picker<String> color = new ColorPicker(new RandomInteger(0, 255));
		layout.add(new HorizontalFlowLayout(), 0.4);
		layout.add(new HorizontalFlowLayout(10), 0.35);
		layout.add(new VerticalFlowLayout(), 0.25);
		BoxPicker box_picker = new BoxPicker(degree, depth, layout, width, height);
		Box b = box_picker.pick();
		HtmlRenderer renderer = new HtmlRenderer(color);
		FileOutputStream fos = new FileOutputStream(new File("/tmp/out.html"));
		PrintStream ps = new PrintStream(fos);
		renderer.toHtml(ps, b);
		ps.close();
	}

}

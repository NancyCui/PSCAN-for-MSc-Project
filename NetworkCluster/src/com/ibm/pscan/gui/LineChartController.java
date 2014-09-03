package com.ibm.pscan.gui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;


public class LineChartController {
	
	public static void drawLineChart(final List<Double> finalSimi) throws Exception{


		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				createAndShowGUI(finalSimi);
			}
		});
	}
	
	protected static void createAndShowGUI(List<Double> finalSimi) {

		JFrame frame=new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.setMinimumSize(new Dimension(500,500));
		LineChartPanel LineChart=new LineChartPanel(finalSimi);
		frame.add(LineChart);
		frame.pack();
		Dimension size=LineChart.getSize();
		//the width of the panel
		double width=size.width; 
		//the height of the panel
		double height=size.height;
		LineChart.calulateScreenSpace(width,height);
		frame.setVisible(true);
		frame.setResizable(false);
	}
}

package com.ibm.pscan.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Draw the line chart for finding the input parameter
 * 
 * @author Ningxin
 *
 */

@SuppressWarnings("serial")
public class LineChartPanel extends JPanel{
	
	//color for background, judge line, line chart, axises
	int[][] color={{255,255,255},{192,192,192},{153,51,250},{0,0,0}};
	
	//set the label of the x-axis and y-axis
	private static int shareX=0;
	private static int shareY=0;
	//the height and width of the axis
	private static double height;
	private static double width;
	//the originial point
	private static double originX;
	private static double originY;
	//the unit between each label on the axis
	private static double unitX;
	private static double unitY;
	//the smallest and biggest label of y
	private static int smallestPoint;
	private static int biggestPoint;
	//the arraylist which is needed to draw
	List<Double> finalSimi=new ArrayList<Double>();
	
	public LineChartPanel(List<Double> finalSimi) {
		setBackground(new Color(color[0][0],color[0][1],color[0][2]));	
		smallestPoint=(int)(Math.floor(finalSimi.get(0)*10));
		biggestPoint=(int)(Math.ceil(finalSimi.get(finalSimi.size()-1)*10));
		shareX=10;
		shareY=biggestPoint-smallestPoint+1;		
		this.finalSimi=finalSimi;
	}
	
	public void paintComponent(Graphics g){
		//clear the screen
		super.paintComponent(g);
		Graphics2D g2d=(Graphics2D) g;
		drawLinechart(g2d);
	}

	private void drawLinechart(Graphics2D g2d) {
		drawCoordinate(g2d, width, height,originX,originY);
		unitX=(width-30)/shareX;
		unitY=(height-30)/shareY;
		drawLabels(g2d);
		drawJudgeLine(g2d);
		drawPoint(g2d);
	}

	/**
	 * Draw the related judge line of each label
	 */
	private void drawJudgeLine(Graphics2D g2d) {		

		Stroke strokePoint=new  BasicStroke(1,   BasicStroke.CAP_BUTT,   
                BasicStroke.JOIN_BEVEL,   0,   
                new   float[]{1,   1},   0);
		g2d.setStroke(strokePoint);
		g2d.setColor(new Color(color[1][0],color[1][1],color[1][2]));
		for(int i=0;i<shareX;i++){
			//draw the judge line of x-axis
			g2d.drawLine((int)(originX+unitX*(i+1)), (int)(originY), (int)(originX+unitX*(i+1)), (int)(originY-height));
			for(int j=1;j<5;j++){
				g2d.drawLine((int)(originX+unitX*(i)+0.5*j*unitX), (int)(originY), (int)(originX+unitX*(i)+0.5*j*unitX), (int)(originY-height));
			}
		}
		int k=0;
		for(int i=smallestPoint-1;i<biggestPoint;i++){
			//draw the judge line of y-axis			
			g2d.drawLine((int)(originX), (int)(originY-unitY*(k+1)), (int)(originX+width), (int)(originY-unitY*(k+1)));
			k++;
		}	
		
		g2d.setColor(Color.BLACK);
		
	}

	/**
	 * Draw the arraylist as a line chart in the panel
	 */
	private void drawPoint(Graphics2D g2d) {
		for(int i=0;i<finalSimi.size();i++){
			g2d.setColor(new Color(color[2][0]+i*6,color[2][1]+i*8,(color[2][2]-i*6)));
			Stroke strokePoint=new BasicStroke(4.0f);
			g2d.setStroke(strokePoint);
			double pointX=originX+unitX*(i+1);			
			double pointY=originY-(finalSimi.get(i)*10-smallestPoint+1)*unitY;
			g2d.drawLine((int)(pointX), (int)(pointY), (int)(pointX), (int)(pointY));
			g2d.drawString(Double.toString(Math.round(finalSimi.get(i)*100)/100.0), (int)(pointX), (int)(pointY+20));
						
			Stroke stroke=new BasicStroke(3.0f);
			g2d.setStroke(stroke);
			if(i!=(finalSimi.size()-1)){
				double pointX2=originX+unitX*(i+2);
				double pointY2=originY-(finalSimi.get(i+1)*10-smallestPoint+1)*unitY;
				g2d.setColor(new Color(230,230,250));
				Stroke strokePoint2=new BasicStroke(5.0f);
				g2d.setStroke(strokePoint2);
				g2d.drawLine((int)(pointX+2), (int)(pointY+2), (int)(pointX2+2), (int)(pointY2+2));
				g2d.setColor(new Color(color[2][0]+i*6,color[2][1]+i*8,(color[2][2]-i*6)));
				g2d.setStroke(strokePoint);
				g2d.drawLine((int)(pointX), (int)(pointY), (int)(pointX2), (int)(pointY2));
				
			}
			
		}
		
	}

	/**
	 * Draw the labels for x-axis and y-axis
	 */
	private void drawLabels(Graphics2D g2d) {
	
		//Draw the original point
		g2d.drawString("X", (int)(originX-3.2), (int)(originY+3.2));
		g2d.drawString("(0,0)", (int)(originX-10), (int)(originY+20));
		//Draw label x and y
		g2d.drawString("X", (int)(originX+width+10), (int)(originY+5));
		g2d.drawString("Y", (int)(originX-5), (int)(originY-height-10));
		//Draw labels for x-axis
		for(int i=0;i<shareX;i++){
			g2d.drawString(Integer.toString(i+1), (int)(originX+unitX*(i+1)), (int)(originY+20));
		}
		//Draw labels for y-axis
		int j=0;
		for(int i=smallestPoint-1;i<biggestPoint;i++){
			g2d.drawString(Double.toString(Math.round(0.1*(i+1)*100)/100.0), (int)(originX-25), (int)(originY-unitY*(j+1)));
			j++;
		}		
	}

	/**
	 * Set the position of original point and the length of x-axis and y-axis
	 */
	public void calulateScreenSpace(double panelWidth, double panelHeight) {
		originX=40;
		originY=panelHeight-40;
		height=panelHeight-40*2;
		width=panelWidth-40*2;				
	}	
	
	/**
	 * Draw x-axis and y-axis of the diagram
	 */
	private void drawCoordinate(Graphics2D g2d, double width, double height,
			double pointCenterX, double pointCenterY) {
		Stroke strokePoint=new BasicStroke(2.0f);
		g2d.setStroke(strokePoint);
		g2d.setColor(new Color(color[3][0],color[3][1],color[3][2]));
		//x-axis
		g2d.drawLine((int)(originX), (int)(originY),(int)(originX+width), (int)(originY));
		//y-axis
		g2d.drawLine((int)(originX), (int)(originY), (int)(originX), (int)(originY-height));
		
	}
	
}

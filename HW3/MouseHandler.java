package edu.ou.cs.cg.homework;

//******************************************************************************
//Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
//Last modified: Mon Feb 29 23:46:15 2016 by Chris Weaver
//******************************************************************************
//Major Modification History:
//
//20160225 [weaver]:	Original file.
//
//******************************************************************************
//Notes:
//
//******************************************************************************

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

//******************************************************************************

/**
* The <CODE>MouseHandler</CODE> class.<P>
*
* @author  Chris Weaver
* @version %I%, %G%
*/
public final class MouseHandler extends MouseAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View	view;
	private ArrayList<Point> list;
	private ArrayList<Point> newlist;
	int width;   //canvas width
	int height;  //canvas height

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public MouseHandler(View view)
	{
		this.view = view;
		list = new ArrayList<Point>();
		newlist = new ArrayList<Point>();
		
		Component	component = view.getComponent();
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
	}

	//**********************************************************************
	// Override Methods (MouseListener)
	//**********************************************************************

	public void		mouseClicked(MouseEvent e)
	{
		if (view.getStarIndex() >= 0) {
			Point cur = new Point(e.getX(), e.getY()); //get cursor position
			view.setCursor(cur);  //send cursor position to view for star movement
		}
		
	}

	public void		mouseEntered(MouseEvent e)
	{
	}

	public void		mouseExited(MouseEvent e)
	{
		view.setCursor(null);
	}
//if no star is selected and shift is not pressed, then goto kite mode
	public void		mousePressed(MouseEvent e)
	{
		if (!Utilities.isShiftDown(e) && (view.getStarIndex() == 0)) {
			int mode = 1;
				width = view.getWidth();
				height = view.getHeight();
				Point start = new Point(e.getX(), height - e.getY());
				int diffy = start.y - view.getkiteCetner().y;
				int diffx = start.x - view.getkiteCetner().x;
				if ((Math.abs(diffy) < 20) && (Math.abs(diffx)) < 20) {
					view.setIsDraw(mode);
				}
				view.clearkiteline();
				view.addPoint(start);
		}
		
	}

	public void		mouseReleased(MouseEvent e)
	{
		width = view.getWidth();
		height = view.getHeight();
		Point newCenter = new Point(e.getX(), e.getY());
		//If shift is pressed, then move all stars' center
		if (Utilities.isShiftDown(e)) {
			
			int x,y;
			Point old = calStarsCenter();	
			int j = 0;
			newlist.clear();
			newlist.add(new Point(0, 0));
			for(j = 1; j < list.size(); ++j){
				x = list.get(j).x + newCenter.x - old.x;
				y = list.get(j).y + height - newCenter.y - old.y;
				//make sure star position is above horizon
				if (x < 30)
					x = 30;
				if (x > 1250)
					x = 1250;
				if (y < 340)
					y = 340;
				if (y > 690)
					y = 690;
				newlist.add(new Point(x, y));
			}
			view.setStars(newlist);
		}
		else {      //Shift is not pressed, then this is kite center
			if (view.getStarIndex() == 0) {
				int mode = 2;
				view.setkiteCenter(new Point(newCenter.x, height - newCenter.y));
				view.setIsDraw(mode);
			}
			
		}
		
	}

	//**********************************************************************
	// Override Methods (MouseMotionListener)
	//**********************************************************************
	//if no star is selected and shift is not pressed, then goto kite mode
	public void		mouseDragged(MouseEvent e)
	{
		height = view.getHeight();
		if ((!Utilities.isShiftDown(e)) && (view.getStarIndex() == 0)) {
			Point current = new Point(e.getX(), height - e.getY());
			view.addPoint(current);
		}
		
	}

	public void		mouseMoved(MouseEvent e)
	{

	}

	//**********************************************************************
	// Override Methods (MouseWheelListener)
	//**********************************************************************

	public void		mouseWheelMoved(MouseWheelEvent e)
	{
	}

	//**********************************************************************
	// Private Methods
	//**********************************************************************
//Calculate stars' center
	private Point calStarsCenter() {
		int j, totalx, totaly;
		int xcenter, ycenter;
		totalx = 0;
		totaly = 0;
		list.clear();
		list.addAll(view.getStars());
		
		for(j = 1; j < list.size(); ++j){
			totalx = totalx + list.get(j).x;
			totaly = totaly + list.get(j).y;
		}
		
		xcenter = totalx/(list.size() - 1);
		ycenter = totaly/(list.size() - 1);
		
		return (new Point(xcenter, ycenter));		
	}
  
}



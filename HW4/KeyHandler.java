
//package homework04;
//******************************************************************************
//Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
//Last modified: Mon Feb 29 23:36:04 2016 by Chris Weaver
//******************************************************************************
//Major Modification History:
//
//20160225 [weaver]:	Original file.
//
//******************************************************************************
//Notes:
//
//******************************************************************************

package edu.ou.cs.cg.homework;
//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

//******************************************************************************

/**
* The <CODE>KeyHandler</CODE> class.<P>
*
* @author  Chris Weaver
* @version %I%, %G%
*/
public final class KeyHandler extends KeyAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	//private final View view;
	private final Homework04 hw;
	private double newspeed;
	private int out;
	private int in;
	private double radius;
	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(Homework04 hw)
	{
		this.hw =hw;

		Component	component = hw.getComponent();

		component.addKeyListener(this);
		newspeed = 0.0;
		out = 1;
		in = 6;
		radius = 0.0;
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************

	public void	keyPressed(KeyEvent e)
	{
		double	a = (Utilities.isShiftDown(e) ? 0.01 : 0.1);

		switch (e.getKeyCode())
		{  
		case KeyEvent.VK_LEFT:  //decrease speed
		{
			newspeed = 0.9*hw.getspeed();
			hw.setspeed(newspeed);
			break;
		}
		case KeyEvent.VK_RIGHT:   //increase speed
		{
			newspeed = 1.1*hw.getspeed();
			hw.setspeed(newspeed);
			break;
		}
		case KeyEvent.VK_UP:     //increase insider radius
		{
			radius = 1.1*hw.getInRadius();
			hw.setInRadius(radius);;
			break;
		}
		case KeyEvent.VK_DOWN:   //decrease insider radius
		{
			radius = 0.9*hw.getInRadius();
			hw.setInRadius(radius);;
			break;
		}
		case KeyEvent.VK_1:
		case KeyEvent.VK_NUMPAD1:   //Container is rectangle
		{
			out = 1;
			hw.resetStart();
			hw.resetIdx();
			hw.setOutMode(out);
			break;
		}
		case KeyEvent.VK_2:
		case KeyEvent.VK_NUMPAD2:   //Container is octagon
		{
			out = 2;
			hw.resetStart();
			hw.resetIdx();
			hw.setOutMode(out);
			break;
		}
		case KeyEvent.VK_3:
		case KeyEvent.VK_NUMPAD3:   //Container is circle
		{
			out = 3;
			hw.resetStart();
			hw.resetIdx();
			hw.setOutMode(out);
			break;
		}
		case KeyEvent.VK_4:
		case KeyEvent.VK_NUMPAD4:   //Container is non-regular polygon
		{
			out = 4;
			hw.resetStart();
			hw.resetIdx();
			hw.setOutMode(out);
			break;
		}
		case KeyEvent.VK_6:            //insider is a point
		case KeyEvent.VK_NUMPAD6:
		{
			in = 6;
			hw.resetStart();
			hw.resetIdx();
			hw.setInMode(in);
			break;
		}
		case KeyEvent.VK_7:            //insider is a square
		case KeyEvent.VK_NUMPAD7:
		{
			in = 7;
			hw.resetStart();
			hw.resetIdx();
			hw.setInMode(in);
			break;
		}
		case KeyEvent.VK_8:           //insider is a octagon
		case KeyEvent.VK_NUMPAD8:
		{
			in = 8;
			hw.resetStart();
			hw.resetIdx();
			hw.setInMode(in);
			break;
		}
		case KeyEvent.VK_9:           //insider is a non-regular polygon
		case KeyEvent.VK_NUMPAD9:
		{
			in = 9;
			hw.resetStart();
			hw.resetIdx();
			hw.setInMode(in);
			break;
		}
		default:
			return; 
			
		}

	}
}

//******************************************************************************

package edu.ou.cs.cg.homework;
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

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
//import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

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
	private final View	view;
	private int fh;  //hence height increment
	private int hh;  //hopscotch left or right movement
	private int hl;  //hopscotch up or down movement
	private int width;   //canvas width
	private int height;   //canvas height
	private int starIdx;  //star index
	private int isOpen;   //denote if window shade is open
	private int numFans;  //denote number of fan blades
	private int ran_x;      //random number for x
	private int ran_y;      //random number for y
	private Random rand;
	private ArrayList<Point> starlist;   //star list
	private ArrayList<Float> alphalist;  // alpha value foe each star

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(View view)
	{
		this.view = view;
		fh = 0;
		hh = 0;
		hl = 0;
		starIdx = -1;
		isOpen = 0;
		numFans = 0;
		rand = new Random();
		starlist = new ArrayList<Point>();
		alphalist = new ArrayList<Float>();
		Component component = view.getComponent();

		component.addKeyListener(this);
		component.setFocusTraversalKeysEnabled(false);
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************

	public void		keyPressed(KeyEvent e)
	{

		switch (e.getKeyCode())
		{
		    case KeyEvent.VK_PAGE_UP:{  //increase hence height
		    	fh += 2;
		    	view.changeFenceHeight(fh);
				break;
		    }	    	
		    case KeyEvent.VK_PAGE_DOWN:{   //decrease hence height
		    	fh -= 2;
		    	view.changeFenceHeight(fh);
				break;
		    }		
		    case KeyEvent.VK_UP: {   //move hopscotch up
		    	hh += 12;
		    	view.changeHopscothPosition(hh, hl);
		    	break;
		    }
		    case KeyEvent.VK_DOWN: {   //move hopscotch down
		    	hh -= 12;
		    	view.changeHopscothPosition(hh, hl);
		    	break;
		    }
		    case KeyEvent.VK_RIGHT: {  //move hopscotch right
			if (!Utilities.isShiftDown(e))
		    		hl += 8;
		    	else
		    		hl += 77;
		    	view.changeHopscothPosition(hh, hl);
		    	break;
		    }
		    case KeyEvent.VK_LEFT: {   //move hopscotch left
			if (!Utilities.isShiftDown(e))
		    		hl -= 8;
		    	else
		    		hl -= 77;

		    	view.changeHopscothPosition(hh, hl);
		    	break;
		    }
		    case KeyEvent.VK_TAB: {    //select star
		    	starlist.clear();
				starlist.addAll(view.getStars());
		    	starIdx = starIdx + 1;
		    	starIdx = starIdx % (starlist.size());
		    	view.changeStarColor(starIdx);
		    	break;
		    }
		    case KeyEvent.VK_W: {      //open or close window shade
		    	isOpen = isOpen + 1;
		    	isOpen = isOpen % 2;
		    	view.setShade(isOpen);
		    	break;
		    }
		    case KeyEvent.VK_1:
		    case KeyEvent.VK_NUMPAD1:{    //only 1 panel in each kite fan
				numFans = 1;
				view.setNumFans(numFans);
				break;
			}
		    case KeyEvent.VK_2:
		    case KeyEvent.VK_NUMPAD2:{   //2 panels in each kite fan
				numFans = 2;
				view.setNumFans(numFans);
				break;
			}
		    case KeyEvent.VK_3:
		    case KeyEvent.VK_NUMPAD3:{   //3 panels in each kite fan
				numFans = 3;
				view.setNumFans(numFans);
				break;
			}
		    case KeyEvent.VK_4:
		    case KeyEvent.VK_NUMPAD4:{   //4 panels in each kite fan
				numFans = 4;
				view.setNumFans(numFans);
				break;
			}
		    case KeyEvent.VK_5:
			case KeyEvent.VK_NUMPAD5:{    //5 panels in each kite fan
				numFans = 5;
				view.setNumFans(numFans);
				break;
			}
			case KeyEvent.VK_6:
			case KeyEvent.VK_NUMPAD6:{     //6 panels in each kite fan
				numFans = 6;
				view.setNumFans(numFans);
				break;
			}
			case KeyEvent.VK_7:
			case KeyEvent.VK_NUMPAD7:{     //7 panels in each kite fan
				numFans = 7;
				view.setNumFans(numFans);
				break;
			}
			case KeyEvent.VK_8:
			case KeyEvent.VK_NUMPAD8:{     //8 panels in each kite fan
				numFans = 8;
				view.setNumFans(numFans);
				break;
			}
			case KeyEvent.VK_9:
			case KeyEvent.VK_NUMPAD9:{      //9 panels in each kite fan
				numFans = 9;
				view.setNumFans(numFans);
				break;
			}
			case KeyEvent.VK_INSERT: {   //insert a star with random position

				alphalist.clear();
				alphalist.addAll(view.getAlphas());
				alphalist.add(1.0f);
				width = view.getWidth();
				height = view.getHeight();
				starlist.clear();
				starlist.addAll(view.getStars());
				ran_x = rand.nextInt(width - 50) + 1;
				ran_y = height - (rand.nextInt(340) + 1);  //make sure star is above horizon
				starlist.add(new Point(ran_x, ran_y));
				view.setAlphas(alphalist);
				view.setStars(starlist);
				
				break;
			}
			case KeyEvent.VK_DELETE: {   //delete selected star
				starlist.clear();
				starlist.addAll(view.getStars());
				alphalist.clear();
				alphalist.addAll(view.getAlphas());
				starlist.remove(starIdx);
				alphalist.remove(starIdx);
				view.setStars(starlist);
				view.setAlphas(alphalist);
				break;
				
			}
			default:
				return;
		}

	}
}

//******************************************************************************



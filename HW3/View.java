//package main.java.edu.ou.cs.cg.homework;
package edu.ou.cs.cg.homework;
import java.util.Random;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.GLCanvas;

//import javax.media.opengl.*;
//import javax.media.opengl.awt.GLCanvas;
//import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

public final class View
implements GLEventListener
{
	
public static final int				DEFAULT_FRAMES_PER_SECOND = 60;
//**********************************************************************
// Public Class Members
//**********************************************************************

public static final GLU		GLU = new GLU();
public static final GLUT	GLUT = new GLUT();
public static final Random	RANDOM = new Random();

//**********************************************************************
// Private Members
//**********************************************************************

//**********************************************************************
// Private Members
//**********************************************************************

// State (internal) variables
	private final GLJPanel			canvas;
	private int						w;				// Canvas width
	private int						h;				// Canvas height
	private int dh;   //use to change hence's height
	private int hl;   //use to change hopscoth's position
	private int hh; //use to change hopscoth's position
	private int starIdx;  //denote which star is selected
	private int isOpen;   //flag for window shade is open or closed
	private int count;
	private int numFans;  //denote the number of panels in each kite fan
	private int isDraw;   //denote mode for drawing kite

	private Point kiteCenter;  //denote as kite center
	private Point cursor;  //denote current cursor position
	private ArrayList<Point> stars;  //denote stars' position
	private ArrayList<Float> alphas;  //alpha list for drawing star;
	private ArrayList<Point> kiteline;  //denote nodes in the kite line
	private final FPSAnimator animator;
	private final KeyHandler keyHandler;   //key event handler
	private final MouseHandler mouseHandler;  //mouse event handler

//**********************************************************************
// Main
//**********************************************************************

public View(GLJPanel canvas)
{
	this.canvas = canvas;
	//Initilize rendering
	canvas.addGLEventListener(this);
	//Initialize model
	cursor = null;
	kiteCenter = new Point(956, 490);
//	isTab = 0;
	isDraw = 0;

	kiteline = new ArrayList<Point>();
	stars = new ArrayList<Point>();
	alphas = new ArrayList<Float>();
	// Initialize interaction
	keyHandler = new KeyHandler(this);
	mouseHandler = new MouseHandler(this);
	animator = new FPSAnimator(canvas, DEFAULT_FRAMES_PER_SECOND);	
	animator.start();
	
}

public Component getComponent()
{
	return (Component)canvas;
}


//**********************************************************************
// Override Methods (GLEventListener)
//**********************************************************************

public void		init(GLAutoDrawable drawable)
{
	w = drawable.getSurfaceWidth();
	h = drawable.getSurfaceHeight();
//	w = drawable.getWidth();
//	h = drawable.getHeight();
	dh = 0;
	hh = 0;
	hl = 0;
	starIdx = 0;
	isOpen = 0;
	
	numFans = 5;
	//Initialize kiteline
	kiteline.add(new Point( 964, 272));
	kiteline.add(new Point( 924, 364));
	kiteline.add(new Point( 928, 396));
	kiteline.add(new Point( 900, 428));
	kiteline.add(new Point( 912, 464));
	kiteline.add(new Point( 936, 472));
	kiteline.add(new Point( 956, 490));
	//Initialize star position and alpha value
	stars.add(new Point(0, 0));
	stars.add(new Point(921, 720-29));
	stars.add(new Point(1052, 720-61));
	stars.add(new Point(1177, 720-49));
	stars.add(new Point(1205, 720-153));
	stars.add(new Point(1146, 720-254));
	
	alphas.add(0.0f);
	alphas.add(1.0f);
	alphas.add(0.90f);
	alphas.add(0.95f);
	alphas.add(0.50f);
	alphas.add(0.30f);
	

}

public void		dispose(GLAutoDrawable drawable)
{
	kiteline.clear();
}

public void		display(GLAutoDrawable drawable)
{
	update();
	render(drawable);
}

public void		reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
{
	this.w = w;
	this.h = h;
}

//**********************************************************************
// Private Methods (Rendering)
//**********************************************************************

private void	update()
{
	count++;									// Counters are useful, right?
}

//Get width
public int getWidth() {
	return w;
}

//Get height
public int getHeight() {
	return h;
}

//set isDraw, true means drawing kiteline by dragging mouse, otherwise false means not
public void setIsDraw(int draw) {
	this.isDraw = draw;
	canvas.repaint();
}

//get isDraw value
public int getIsDraw() {
	return isDraw;
}

//set kitecenter
public void setkiteCenter(Point center) {
	kiteCenter = center;
	canvas.repaint();
}

//get kitecenter
public Point getkiteCetner() {
	return kiteCenter;
}

//Set star list
public void setStars(ArrayList<Point> list) {
		stars.clear();
		stars.addAll(list);
		canvas.repaint();
}

//Get current star list
public ArrayList<Point> getStars() {
	return stars;
}

//Set star list
public void setAlphas(ArrayList<Float> list) {
//	if (list.size() > 1) {
		alphas.clear();
		alphas.addAll(list);
		canvas.repaint();
//	}	
}

//Get current star list
public ArrayList<Float> getAlphas() {
	return alphas;
}

//clear kiteline
public void clearkiteline() {
	kiteline.clear();
	canvas.repaint();
}

private void	render(GLAutoDrawable drawable)
{
	GL2		gl = drawable.getGL().getGL2();

	gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer

	// Make the sky gradient easier by enabling alpha blending.
	// Note: OpenGL supports translucency very poorly!
	gl.glEnable(GL.GL_BLEND);
	gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

	setLorenzProjection(gl);
	drawLorenz(gl);

	setScreenProjection(gl);
	drawSky(gl);
	drawGround(gl);
	drawSidewalkBase(gl);

	drawStars(gl);
	drawMoon(gl);
	drawSidewalk(gl);
	drawHopscotch(gl);
	drawHouses(gl);
	drawFence(gl);
	drawKite(gl);
}

//**********************************************************************
// Private Methods (Coordinate System)
//**********************************************************************

private void	setLorenzProjection(GL2 gl)
{
	GLU		glu = new GLU();

	gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
	gl.glLoadIdentity();						// Set to identity matrix
	glu.gluOrtho2D(-1.0f, 1.0f, -1.45f, 1.0f);	// 2D translate and scale
}

private void	setScreenProjection(GL2 gl)
{
	GLU		glu = new GLU();

	gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
	gl.glLoadIdentity();						// Set to identity matrix
	glu.gluOrtho2D(0.0f, 1280.0f, 0.0f, 720.0f);// 2D translate and scale
}

//**********************************************************************
// Private Methods (Scene)
//**********************************************************************

// I faded the galaxy a bit to experiment with animation.
private void	drawLorenz(GL2 gl)
{
	gl.glBegin(GL.GL_POINTS);
	gl.glColor3f(1.0f, 1.0f, 1.0f);

	double		dt = 0.01;
	double		sigma = 10.0;
	double		beta = 8.0 / 3.0;
	double		rho = 28.0;
	double		lx = 0.1;
	double		ly = 0.0;
	double		lz = 0.0;

	for (int i=0; i<10000; i++)
	{
		double	llx = lx + dt * sigma * (ly - lx);
		double	lly = ly + dt * (lx * (rho - lz) - ly);
		double	llz = lz + dt * (lx * ly - beta * lz);

		lx = llx;
		ly = lly;
		lz = llz;
		//System.out.println(" " + lx + " " + ly + " " + lz);
		float	cc = (float)((lz + 30.0) / 60.0);

		if (Math.abs(count % 10000 - i) < 20)	// Window moves with animation
			setColor(gl, 255, 32, 32);		// Some dots red, cycling around
		else
			gl.glColor4f(cc, cc, cc, 0.25f);

		gl.glVertex2d(-lx / 30.0, ly / 30.0);
	}

	gl.glEnd();
}

private void	drawSky(GL2 gl)
{
	gl.glBegin(GL2.GL_QUADS);

	setColor(gl, 128, 112, 80);				// Opaque gold on horizon
	gl.glVertex2i(0, 312);
	gl.glVertex2i(1280, 312);
	setColor(gl, 32, 48, 96, 64);			// Translucent dark blue at top
	gl.glVertex2i(1280, 720);
	gl.glVertex2i(0, 720);

	gl.glEnd();
}

private void	drawGround(GL2 gl)
{
	gl.glBegin(GL2.GL_QUADS);

	setColor(gl, 64, 48, 48);				// Red-purple on horizon
	
	gl.glVertex2i(0, 312);
	gl.glVertex2i(1280, 312);
	setColor(gl, 80, 128, 64);				// Moss green by sidewalk
	gl.glVertex2i(1280, 129);
	gl.glVertex2i(0, 129);

	gl.glEnd();
}

private void	drawSidewalkBase(GL2 gl)
{
	gl.glBegin(GL2.GL_QUADS);

	setColor(gl, 255, 255, 255);			// White
	gl.glVertex2i(0, 0);
	gl.glVertex2i(1280, 0);
	gl.glVertex2i(1280, 129);
	gl.glVertex2i(0, 129);

	gl.glEnd();
}

private void	drawSidewalk(GL2 gl)
{
	for (int i=-1; i<16; i++)
		drawSidewalkSlab(gl, i * 79);
}

private void	drawSidewalkSlab(GL2 gl, int dx)
{
	gl.glBegin(GL2.GL_QUADS);

	setColor(gl, 128, 128, 128);			// Medium gray
	gl.glVertex2i(dx +  34,   2);
	gl.glVertex2i(dx +  57, 127);
	gl.glVertex2i(dx + 134, 127);
	gl.glVertex2i(dx + 111,   2);

	gl.glEnd();
}

//change hopscoth position
public void changeHopscothPosition(int hh, int hl) {
	this.hh = hh;
	this.hl = hl;
	canvas.repaint();
}

private void	drawHopscotch(GL2 gl)
{
	//make sure hopscotch is in the range
	if (hl <= -670){
		hl = -670;
	}
	if (hl >= 380){
		hl = 380;
	}
	
	if (hh >= 15){
		hh = 15;
	}
	
	if (hh <= -25){
		hh = -25;
	}
	drawHopscotchSquare(gl, 673 + hl, 720-622-25 + hh);
	drawHopscotchSquare(gl, 704 + hl, 720-622-25 + hh);
	drawHopscotchSquare(gl, 736 + hl, 720-622-25 + hh);

	drawHopscotchSquare(gl, 764 + hl, 720-634-25 + hh);
	drawHopscotchSquare(gl, 770 + hl, 720-608-25 + hh);

	drawHopscotchSquare(gl, 798 + hl, 720-620-25 + hh);

	drawHopscotchSquare(gl, 826 + hl, 720-631-25 + hh);
	drawHopscotchSquare(gl, 832 + hl, 720-606-25 + hh);

	drawHopscotchSquare(gl, 861 + hl, 720-620-25 + hh);
}

private void	drawHopscotchSquare(GL2 gl, int dx, int dy)
{
	setColor(gl, 255, 255, 192, 128);			// Taupe + alpha
	gl.glBegin(GL2.GL_POLYGON);
	doHopscotchLoop(gl, dx, dy);
	gl.glEnd();

	// This approach cuts off the corners
	setColor(gl, 229, 229, 229);				// Light gray
	gl.glLineWidth(3);
	gl.glBegin(GL2.GL_LINE_LOOP);
	doHopscotchLoop(gl, dx, dy);
	gl.glEnd();
	gl.glLineWidth(1);
}

private void	doHopscotchLoop(GL2 gl, int dx, int dy)
{
	gl.glVertex2i(dx +  0, dy +  0);
	gl.glVertex2i(dx +  5, dy + 25);
	gl.glVertex2i(dx + 35, dy + 25);
	gl.glVertex2i(dx + 30, dy +  0);
}

private void	drawFence(GL2 gl)
{
	drawFenceSlat(gl, false,    6, 132);
	drawFenceSlat(gl, false,   30, 132);
	drawFenceSlat(gl, false,   54, 132);
	drawFenceSlat(gl, false,   78, 132);

	drawFenceSlat(gl, false,  290, 132);
	drawFenceSlat(gl, false,  314, 132);
	drawFenceSlat(gl, false,  338, 132);
	drawFenceSlat(gl, false,  362, 132);

	drawFenceSlat(gl, false,  391, 132);
	drawFenceSlat(gl, false,  415, 132);
	drawFenceSlat(gl, false,  439, 132);
	drawFenceSlat(gl, false,  463, 132);

	drawFenceSlat(gl, false,  856, 132);
	drawFenceSlat(gl, true,   880, 132);
	drawFenceSlat(gl, false,  904, 132);
	drawFenceSlat(gl, true,   928, 132);
	drawFenceSlat(gl, false,  952, 132);
	drawFenceSlat(gl, true,   976, 132);
	drawFenceSlat(gl, false, 1000, 132);
	drawFenceSlat(gl, true,  1024, 132);

	drawFenceSlat(gl, false, 1224, 132);
	drawFenceSlat(gl, true,  1248, 132);
}
//Change fence height. If press page up key, increase it, else press page down key, decrease it
public void changeFenceHeight(int dh) {
	this.dh = dh;
	canvas.repaint();
}
// Draws a single fence slat with bottom left corner at dx, dy.
// If flip is true, the slat is higher on the left, else on the right.
private void	drawFenceSlat(GL2 gl, boolean flip, int dx, int dy)
{	
	//make sure fence's height is in the range (132, 312)
	if(dh <= -102){
		dh = -102;
	}
	if(dh >= 68) {
		dh = 68;
	}
	
		gl.glBegin(GL2.GL_POLYGON);					// Fill the slat, in...

		setColor(gl, 192, 192, 128);				// ...tan
		gl.glVertex2i(dx +  0, dy +   0);
		gl.glVertex2i(dx +  0, dy + (flip ? (112+dh) : (102+dh)));
		gl.glVertex2i(dx + 24, dy + (flip ? (102+dh) : (112+dh)));
		gl.glVertex2i(dx + 24, dy +   0);

		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_LOOP);				// Edge the slat, in...

		setColor(gl, 0, 0, 0);						// ...black
		gl.glVertex2i(dx +  0, dy +   0);
		gl.glVertex2i(dx +  0, dy + (flip ? (112+dh) : (102+dh)));
		gl.glVertex2i(dx + 24, dy + (flip ? (102+dh) : (112+dh)));
		gl.glVertex2i(dx + 24, dy +   0);

		gl.glEnd();
	
}
//set selected star index
public void changeStarColor(int starIdx) {
	this.starIdx = starIdx;
	canvas.repaint();
}

//get selected star index 
public int getStarIndex() {
	return starIdx;
}

//get cursor poistion from mousehanlder
public void setCursor(Point cur) {
		cursor = cur;
		canvas.repaint();
}
//Draw star at destinated position 
private void	drawStars(GL2 gl)
{	
	int i;
	for (i = 1; i < stars.size(); ++i) {
		drawStar(gl, i, cursor);
	}

}

//Draw selected star in orange color in mouse clicked position
//if no star is selected, draw in default position
private void drawStar(GL2 gl, int index, Point newPosition) {
	double	theta = 0.5 * Math.PI;
	int newy = 0;
	
	if (starIdx == index) {
		setColor(gl, 255, 120, 0, (int)(alphas.get(index) * 255)); //orange + alpha
	//	if ((newPosition != null) && isTab == 1) {
		if ((newPosition != null)) {
			newy = h - newPosition.y;
			
			if (newPosition.x < 30)
				newPosition.x = 30;
			if (newPosition.x > 1250)
				newPosition.x = 1250;
			
			if (newy < 340)
				newy = 340;
			if (newy > 690)
				newy = 690;
			
			stars.get(index).x = newPosition.x;
			stars.get(index).y = newy;
			//make sure star is drawn above horizon
	//		if (stars.get(index).y >= 340){
				
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glVertex2d(stars.get(index).x, stars.get(index).y);
			doStarVertices(gl, stars.get(index).x, stars.get(index).y, 8, 20.0, 8.0);
			gl.glVertex2d(stars.get(index).x + 15 * Math.cos(theta), stars.get(index).y + 15 * Math.sin(theta));
			gl.glEnd();
							
			//}
		}
		else {
			//If mouse doesn't click, draw star in default position
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glVertex2d(stars.get(index).x, stars.get(index).y);
			doStarVertices(gl, stars.get(index).x, stars.get(index).y, 8, 20.0, 8.0);
			gl.glVertex2d(stars.get(index).x + 15 * Math.cos(theta), stars.get(index).y + 15 * Math.sin(theta));
			gl.glEnd();
			
		}
	}
	else {
		setColor(gl, 255, 255, 0, (int)(alphas.get(index) * 255));
		// Yellow + alpha
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2d(stars.get(index).x, stars.get(index).y);
		doStarVertices(gl, stars.get(index).x, stars.get(index).y, 8, 20.0, 8.0);
		gl.glVertex2d(stars.get(index).x + 15 * Math.cos(theta), stars.get(index).y + 15 * Math.sin(theta));
		gl.glEnd();
	}
	
}

private static final int		SIDES_MOON = 18;
private static final double		ANGLE_MOON = 2.0 * Math.PI / SIDES_MOON;

private void	drawMoon(GL2 gl)
{
	double	theta = 0.20 * ANGLE_MOON;
	int		cx = 94;
	int		cy = 720 - 92;
	int		r = 59;

	// Fill the whole moon in white
	gl.glBegin(GL.GL_TRIANGLE_FAN);

	setColor(gl, 255, 255, 255);				// White
	gl.glVertex2d(cx, cy);

	for (int i=0; i<SIDES_MOON+1; i++)			// 18 sides
	{
		gl.glVertex2d(cx + r * Math.cos(theta), cy + r * Math.sin(theta));
		theta += ANGLE_MOON;
	}

	gl.glEnd();

	// Fill the outside shadow in dark bluish gray
	theta = -1.80 * ANGLE_MOON;

	gl.glBegin(GL.GL_TRIANGLE_FAN);

	setColor(gl, 64, 64, 80);
	gl.glVertex2d(cx, cy);

	for (int i=0; i<8; i++)						// 7 sides
	{
		gl.glVertex2d(cx + r * Math.cos(theta), cy + r * Math.sin(theta));
		theta += ANGLE_MOON;
	}

	gl.glEnd();

	// Fill the inside shadow in dark bluish gray
	theta = 1.50 * ANGLE_MOON;
	cx = 128;
	cy = 650;
	theta = 7.2 * ANGLE_MOON;

	gl.glBegin(GL.GL_TRIANGLE_FAN);

	setColor(gl, 64, 64, 80);
	gl.glVertex2d(cx, cy);

	for (int i=0; i<8; i++)						// 7 sides
	{
		gl.glVertex2d(cx + r * Math.cos(theta), cy + r * Math.sin(theta));
		theta += ANGLE_MOON;
	}

	gl.glEnd();
}
//isDraw is 0, default mode; isDraw is 1, kite is translucent; isDraw is 2, kite is not translucent
private void	drawKite(GL2 gl)
{ 
	if (isDraw == 0) {
		drawKiteFans(gl, false);
		drawKiteLine(gl);
	}
	else { 
		
		if(isDraw == 1) {
			drawKiteFans(gl, true);
			drawKiteLine(gl);
			}
		else {
			drawKiteLine(gl);
			drawKiteFans(gl, false);
		}
	}

}

//Add point to array list for kite line
public void addPoint(Point p) {
	kiteline.add(p);
	canvas.repaint();
}
// Draw kite line, each node is dependent mouse's position, except begin node
private void	drawKiteLine(GL2 gl)
{
	setColor(gl, 128, 128, 96);
	gl.glLineWidth(2);
		if (kiteline.size() > 0) {
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex2i(1024,  244);
			
				for (Point p : kiteline)
					gl.glVertex2i(p.x, p.y);
			
			gl.glVertex2i(kiteCenter.x, kiteCenter.y);
			gl.glEnd();
		}
	gl.glLineWidth(1);
}
//set number of fans
public void setNumFans(int numFans) {
	this.numFans = numFans;
	canvas.repaint();
}

//Draw fans at destinated number 
private void	drawKiteFans(GL2 gl, boolean trans)
{
	int		cx = kiteCenter.x;
	int		cy = kiteCenter.y;
	int		r = 80;

	// Flap those wings!
	int		ticks = 120;
	double	phase = ((count % (2 * ticks)) - ticks) / (double)ticks;
	double	variance = ANGLE_MOON * Math.cos(2 * Math.PI * phase);

	// The min and max angles of each wing, with variance over time
	double	amin =  4.0 * ANGLE_MOON - variance;
	double	amax =  9.0 * ANGLE_MOON + variance;
	double	bmin = 13.0 * ANGLE_MOON - variance;
	double	bmax = 18.0 * ANGLE_MOON + variance;

	double	astep = (amax - amin) / numFans;
	double	bstep = (bmax - bmin) / numFans;

	for (int i=0; i<numFans; i++)
	{
		double	a = amin + astep * i;
		double	b = bmin + bstep * i;

		drawKiteBlade(gl, cx, cy, r, a, a + astep, trans);		// Upper blade
		drawKiteBlade(gl, cx, cy, r, b, b + bstep, trans);		// Lower blade
	}
}

private void	drawKiteBlade(GL2 gl, int cx, int cy, int r,
							  double a1, double a2, boolean trans)
{
	// Fill in the blade
	if (trans == true) {
		setColor(gl, 48, 80, 224, 50);
	}
	else {
		setColor(gl, 48, 80, 224, 255);
	}
	//make sure kite is above horizon
	if (cx < 80)
		cx = 80;
	if (cx > 1200)
		cx = 1200;
	
	if (cy < 400)
		cy = 400;
	if (cy > 640)
		cy = 640;
	
	gl.glBegin(GL2.GL_POLYGON);
	gl.glVertex2d(cx, cy);
	gl.glVertex2d(cx + r * Math.cos(a1), cy + r * Math.sin(a1));
	gl.glVertex2d(cx + r * Math.cos(a2), cy + r * Math.sin(a2));
	gl.glEnd();

	// Draw the thin struts
	if (trans == true) {
		setColor(gl, 96, 96, 96, 50);
	}
	else {
		setColor(gl, 96, 96, 96, 255);
	}
	gl.glBegin(GL.GL_LINE_STRIP);
	gl.glVertex2d(cx, cy);
	gl.glVertex2d(cx + r * Math.cos(a1), cy + r * Math.sin(a1));
	gl.glVertex2d(cx + r * Math.cos(a2), cy + r * Math.sin(a2));
	gl.glVertex2d(cx, cy);
	gl.glEnd();

	// Draw the thick translucent edges
	if (trans == true) {
		setColor(gl, 128, 128, 128, 32);
	}
	else {
		setColor(gl, 128, 128, 128, 64);
	}

	gl.glLineWidth(6);
	gl.glBegin(GL.GL_LINE_STRIP);
	gl.glVertex2d(cx, cy);
	gl.glVertex2d(cx + r * Math.cos(a1), cy + r * Math.sin(a1));
	gl.glVertex2d(cx + r * Math.cos(a2), cy + r * Math.sin(a2));
	gl.glVertex2d(cx, cy);
	gl.glEnd();
	gl.glLineWidth(1);
}

private static final Point[]		HOUSE_OUTLINE = new Point[]
{
	new Point(0, 0),		// lower left corner
	new Point(0, 162),		// bottom left corner
	new Point(88, 250),		// apex
	new Point(176, 162),	// top right corner
	new Point(176, 0),		// bottom left corner
};

private static final Point[]		HOUSE_OUTLINE1 = new Point[]
{
	new Point(-1, -1),		// lower left corner
	new Point(-1, 162),		// bottom left corner
	new Point(88, 251),		// apex
	new Point(177, 162),	// top right corner
	new Point(177, -1),		// bottom left corner
};

//set if open or close window shade
public void setShade(int isOpen) {
	this.isOpen = isOpen;
	canvas.repaint();
}

// Too much variation to encapsulate house drawing in a drawHouse() method
private void	drawHouses(GL2 gl)
{
	int		tx = 108;
	int		ty = 132;

	drawChimney(gl, tx + 114, ty + 162, true);
	drawOutline(gl, tx, ty, 0, 1);
	drawRoof(gl, tx + 88, ty + 250);
	drawWindow(gl, tx + 127, ty + 127, true, isOpen);
	drawDoor(gl, tx + 39, ty);

	tx = 634;
	ty = 158;

	drawChimney(gl, tx + 30, ty + 162, false);
	drawOutline(gl, tx, ty, 1, 2);
	drawWindow(gl, tx + 98, ty + 64, false, isOpen);
	drawWindow(gl, tx + 144, ty + 64, false, isOpen);
	drawDoor(gl, tx + 7, ty);
	drawHouseStar(gl, tx + 88, ty + 200);

	tx = 1048;
	ty = 132;

	drawChimney(gl, tx + 30, ty + 162, false);
	drawOutline(gl, tx, ty, 2, 2);
	drawWindow(gl, tx + 98, ty + 64, false, isOpen);
	drawWindow(gl, tx + 144, ty + 64, false, isOpen);
	drawDoor(gl, tx + 7, ty);
	drawDoorWindow(gl, tx + 27, ty + 71);
}

private void	drawChimney(GL2 gl, int sx, int sy, boolean smoke)
{
	setColor(gl, 128, 0, 0);					// Firebrick red
	fillRect(gl, sx, sy, 30, 88);

	setColor(gl, 0, 0, 0);						// Black
	drawRect(gl, sx, sy, 30, 88);

	if (smoke)
		drawSmoke(gl, sx + 3, sy + 88);
}

private LinkedList<Point>	smoke = new LinkedList<Point>();

// The picture's quads are boring...let's have some fun with animation!
private void	drawSmoke(GL2 gl, int sx, int sy)
{
	// Random walk up to two pixels on each end of the previous smoke line
	// Each point in the list defines (xmin, xmax) for a smoke line
	Point	p = ((smoke.size() == 0) ?
				 new Point(3, 27) : smoke.getFirst());
	int		ql = Math.min(30, Math.max( 0, p.x + RANDOM.nextInt(5) - 2));
	int		qr = Math.max( 0, Math.min(30, p.y + RANDOM.nextInt(5) - 2));
	Point	q = ((ql < qr) ? new Point(ql, qr) : new Point(qr, ql));

	smoke.addFirst(q);			// Add the lowest line to beginning

	if (smoke.size() > 255)		// If it's long enough,
		smoke.removeLast();		// remove the highest (=transparent) line

	int		alpha = 0;			// For opaque line closest to the chimney

	for (Point a : smoke)		// Draw all the lines lowest to highest,
	{
		if (RANDOM.nextInt(1024) < alpha)	// simulate diffusion leftward
			a.x--;

		if (RANDOM.nextInt(1024) < alpha)	// and rightward
			a.y++;

		setColor(gl, 255, 255, 255, 255 - alpha++);	// fading along the way

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2i(sx + a.x, sy + alpha + 1);	// as height goes up
		gl.glVertex2i(sx + a.y, sy + alpha + 1);
		gl.glEnd();
	}
}

private void	drawOutline(GL2 gl, int sx, int sy, int shade, int thickness)
{
	if (shade == 0)
		setColor(gl, 64, 64, 0);				// Dark green
	else if (shade == 1)
		setColor(gl, 143, 82, 10);				// Medium brown
	else
		setColor(gl, 128, 64, 0);				// Medium brown

	fillPoly(gl, sx, sy, HOUSE_OUTLINE);

	setColor(gl, 0, 0, 0);						// Black
	gl.glLineWidth(thickness);
	drawPoly(gl, sx, sy, HOUSE_OUTLINE);
	gl.glLineWidth(1);
}

private void	drawRoof(GL2 gl, int cx, int cy)
{
	setColor(gl, 80, 64, 32);					// Dark brown

	gl.glBegin(GL.GL_TRIANGLE_FAN);
	gl.glVertex2i(cx, cy);
	gl.glVertex2i(cx - 88, cy - 88);
	gl.glVertex2i(cx - 56, cy - 88);
	gl.glVertex2i(cx - 24, cy - 88);
	gl.glVertex2i(cx + 24, cy - 88);
	gl.glVertex2i(cx + 56, cy - 88);
	gl.glVertex2i(cx + 88, cy - 88);
	gl.glEnd();

	setColor(gl, 0, 0, 0);						// Black

	gl.glBegin(GL.GL_LINE_STRIP);				// Leftmost board
	gl.glVertex2i(cx, cy);
	gl.glVertex2i(cx - 88, cy - 88);
	gl.glVertex2i(cx - 56, cy - 88);
	gl.glVertex2i(cx, cy);
	gl.glEnd();

	gl.glBegin(GL.GL_LINE_STRIP);				// Left-center board
	gl.glVertex2i(cx, cy);
	gl.glVertex2i(cx - 56, cy - 88);
	gl.glVertex2i(cx - 24, cy - 88);
	gl.glVertex2i(cx, cy);
	gl.glEnd();

	gl.glBegin(GL.GL_LINE_STRIP);				// Center board
	gl.glVertex2i(cx, cy);
	gl.glVertex2i(cx - 24, cy - 88);
	gl.glVertex2i(cx + 24, cy - 88);
	gl.glVertex2i(cx, cy);
	gl.glEnd();

	gl.glBegin(GL.GL_LINE_STRIP);				// Right-center board
	gl.glVertex2i(cx, cy);
	gl.glVertex2i(cx + 24, cy - 88);
	gl.glVertex2i(cx + 56, cy - 88);
	gl.glVertex2i(cx, cy);
	gl.glEnd();

	gl.glBegin(GL.GL_LINE_STRIP);				// Rightmost board
	gl.glVertex2i(cx, cy);
	gl.glVertex2i(cx + 56, cy - 88);
	gl.glVertex2i(cx + 88, cy - 88);
	gl.glVertex2i(cx, cy);
	gl.glEnd();
}

private void	drawDoor(GL2 gl, int cx, int cy)
{
	setColor(gl, 192, 128, 0);					// Light brown
	fillRect(gl, cx, cy, 40, 92);

	setColor(gl, 0, 0, 0);						// Black
	drawRect(gl, cx, cy, 40, 92);

	setColor(gl, 176, 192, 192);				// Light steel
	fillOval(gl, cx + 8, cy + 46, 4, 4);

	setColor(gl, 0, 0, 0);						// Black
	drawOval(gl, cx + 8, cy + 46, 4, 4);
}
//draw open or closed window, it depends on value of open
private void	drawWindow(GL2 gl, int cx, int cy, boolean shade, int open)
{
	int		dx = 20;
	int		dy = 20;

	if (shade)
		setColor(gl, 224, 224, 224);			// Light gray
	else
		setColor(gl, 224, 192, 224);			// Light pink
	
	fillRect(gl, cx - dx, cy - dy, 2 * dx, 2 * dy); //Window backgrounds fill
	
	if (open == 1) {                            //open is 1, open window shade, else close it
		setColor(gl, 255, 255, 128);				// Light yellow
		gl.glBegin(GL2.GL_POLYGON);					// Shade fill
		gl.glVertex2i(cx - dx, cy - dy);
		gl.glVertex2i(cx     , cy + dy);
		gl.glVertex2i(cx + dx, cy - dy);
		gl.glEnd();
	
	setColor(gl, 0, 0, 0);						// Black

	gl.glBegin(GL2.GL_LINE_LOOP);				// Shade edge
	gl.glVertex2i(cx - dx, cy - dy);
	gl.glVertex2i(cx     , cy + dy);
	gl.glVertex2i(cx + dx, cy - dy);
	gl.glEnd();
	
	}
	setColor(gl, 0, 0, 0);						// Black

	// Window frame: bottom, middle, top
	fillRect(gl, cx - dx - 1, cy - dy - 1, 2 * dx + 3, 3);
	fillRect(gl, cx - dx - 1, cy +  0 - 1, 2 * dx + 3, 3);
	fillRect(gl, cx - dx - 1, cy + dy - 1, 2 * dx + 3, 3);

	// Window frame: left, middle, right
	fillRect(gl, cx - dx - 1, cy - dy - 1, 3, 2 * dy + 3);
	fillRect(gl, cx +  0 - 1, cy - dy - 1, 3, 2 * dy + 3);
	fillRect(gl, cx + dx - 1, cy - dy - 1, 3, 2 * dy + 3);

	// Could use LINE_STRIP for the thick window frames instead
}

private void	drawHouseStar(GL2 gl, int cx, int cy)
{
	double	theta = 0.5 * Math.PI;

	setColor(gl, 255, 255, 0);
	gl.glBegin(GL.GL_TRIANGLE_FAN);
	gl.glVertex2d(cx, cy);
	doStarVertices(gl, cx, cy, 5, 20.0, 8.0);
	gl.glVertex2d(cx + 20 * Math.cos(theta), cy + 20 * Math.sin(theta));
	gl.glEnd();

	setColor(gl, 0, 0, 0);
	gl.glBegin(GL.GL_LINE_STRIP);
	doStarVertices(gl, cx, cy, 5, 20.0, 8.0);
	gl.glVertex2d(cx + 20 * Math.cos(theta), cy + 20 * Math.sin(theta));
	gl.glEnd();
}

private void	drawDoorWindow(GL2 gl, int cx, int cy)
{
	double	theta = 0.5 * Math.PI;

	setColor(gl, 255, 255, 128);
	gl.glBegin(GL.GL_TRIANGLE_FAN);
	gl.glVertex2d(cx, cy);
	doStarVertices(gl, cx, cy, 4, 15.0, 13.5);
	gl.glVertex2d(cx + 15 * Math.cos(theta), cy + 15 * Math.sin(theta));
	gl.glEnd();

	setColor(gl, 0, 0, 0);
	gl.glBegin(GL.GL_LINE_STRIP);
	doStarVertices(gl, cx, cy, 4, 15.0, 13.5);
	gl.glVertex2d(cx + 15 * Math.cos(theta), cy + 15 * Math.sin(theta));
	gl.glEnd();
}

//**********************************************************************
// Private Methods (Utility Functions)
//**********************************************************************

private void	setColor(GL2 gl, int r, int g, int b, int a)
{
	gl.glColor4f(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
}

private void	setColor(GL2 gl, int r, int g, int b)
{
	setColor(gl, r, g, b, 255);
}

private void	fillRect(GL2 gl, int x, int y, int w, int h)
{
	gl.glBegin(GL2.GL_POLYGON);
	gl.glVertex2i(x+0, y+0);
	gl.glVertex2i(x+0, y+h);
	gl.glVertex2i(x+w, y+h);
	gl.glVertex2i(x+w, y+0);
	gl.glEnd();
}

private void	drawRect(GL2 gl, int x, int y, int w, int h)
{
	gl.glBegin(GL.GL_LINE_LOOP);
	gl.glVertex2i(x+0, y+0);
	gl.glVertex2i(x+0, y+h);
	gl.glVertex2i(x+w, y+h);
	gl.glVertex2i(x+w, y+0);
	gl.glEnd();
}

private void	fillOval(GL2 gl, int cx, int cy, int w, int h)
{
	gl.glBegin(GL2.GL_POLYGON);

	for (int i=0; i<32; i++)
	{
		double	a = (2.0 * Math.PI) * (i / 32.0);

		gl.glVertex2d(cx + w * Math.cos(a), cy + h * Math.sin(a));
	}

	gl.glEnd();
}

private void	drawOval(GL2 gl, int cx, int cy, int w, int h)
{
	gl.glBegin(GL.GL_LINE_LOOP);

	for (int i=0; i<32; i++)
	{
		double	a = (2.0 * Math.PI) * (i / 32.0);

		gl.glVertex2d(cx + w * Math.cos(a), cy + h * Math.sin(a));
	}

	gl.glEnd();
}

private void	fillPoly(GL2 gl, int startx, int starty, Point[] offsets)
{
	gl.glBegin(GL2.GL_POLYGON);

	for (int i=0; i<offsets.length; i++)
		gl.glVertex2i(startx + offsets[i].x, starty + offsets[i].y);

	gl.glEnd();
}

private void	drawPoly(GL2 gl, int startx, int starty, Point[] offsets)
{
	gl.glBegin(GL2.GL_LINE_LOOP);

	for (int i=0; i<offsets.length; i++)
		gl.glVertex2i(startx + offsets[i].x, starty + offsets[i].y);

	gl.glEnd();
}

private void	doStarVertices(GL2 gl, int cx, int cy, int sides,
							   double r1, double r2)
{
	double	delta = Math.PI / sides;
	double	theta = 0.5 * Math.PI;

	for (int i=0; i<sides; i++)
	{
		gl.glVertex2d(cx + r1 * Math.cos(theta), cy + r1 * Math.sin(theta));
		theta += delta;

		gl.glVertex2d(cx + r2 * Math.cos(theta), cy + r2 * Math.sin(theta));
		theta += delta;
	}
}
}

//******************************************************************************

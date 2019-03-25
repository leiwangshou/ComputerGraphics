//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Feb  9 20:33:16 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160209 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

//package main.java.edu.ou.cs.cg.homework;
package edu.ou.cs.cg.homework;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
//import javax.media.opengl.*;
//import javax.media.opengl.awt.GLCanvas;
//import javax.media.opengl.glu.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;

//******************************************************************************

/**
 * The <CODE>Homework02</CODE> class.<P>
 *
 * @author  Lei Wang
 * @version %I%, %G%
 */
public final class Homework02
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final GLU		GLU = new GLU();
	public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();
	public static float PI = 3.1415926F;

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
//	private int				k = 0;		// Just an animation counter

	private int				w;			// Canvas width
	private int				h;			// Canvas height

	private float line_w;              //line width
	
//	private TextRenderer	renderer;

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		GLCanvas		canvas = new GLCanvas(capabilities);
		JFrame			frame = new JFrame("Homework02");

		canvas.setPreferredSize(new Dimension(1066, 600));
	//	canvas.setPreferredSize(new Dimension(750, 750));
		frame.setBounds(50, 50, 916, 450);
	//	frame.setBounds(50, 50, 600, 600);
		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

		canvas.addGLEventListener(new Homework02());
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
		w = drawable.getSurfaceWidth();
		h = drawable.getSurfaceHeight();
	}

	public void		dispose(GLAutoDrawable drawable)
	{
		//renderer = null;
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
		//k++;									// Counters are useful, right?
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer

		setProjection(gl);	// Use a coordinate system
		
		//Start to draw picture
		//Draw top background
		drawTopBackground(gl);
		//Draw top items
		drawTopPart(gl);
		//Draw middle background
		drawMiddleBackground(gl);
		//Draw middle items
		drawMiddlePart(gl);
		//Draw bottom background and items
		drawBottom(gl);						
	}

	//**********************************************************************
	// Private Methods (Coordinate System)
	//**********************************************************************

	private void	setProjection(GL2 gl)
	{
		GLU		glu = new GLU();

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(-1.0f, 1.0f, -1.0f, 1.0f);	// 2D translate and scale
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************
	
	//Draw top background with gradient effect
	private void drawTopBackground(GL2 gl) {
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0.57f, 0.51f, 0.39f);
		gl.glVertex2d(-1.0f, -0.14f);
		gl.glColor3f(0.05f, 0.07f, 0.12f);
		gl.glVertex2d(-1.0f, 1.0f);
		gl.glVertex2d(1.0f, 1.0f);
		gl.glColor3f(0.57f, 0.51f, 0.39f);
		gl.glVertex2d(1.0f, -0.14f);
		gl.glEnd();
		
	}
	
	private void drawTopPart(GL2 gl) {
		//Smooth all lines, polygons
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		float x, y, r;
						
		//Draw galaxy
		x = 0.04f;
		y = 0.04f;
		drawGalaxy(gl, x, y);
		
		//draw moon
		x = -0.85f;
	    y = 0.75f;
		r = 0.1f;
		drawMoon(gl, x, y, r);
		
		//draw five stars on top area
		gl.glColor3f(1.0f, 0.98f, 0.0f);
		x = 0.44f;
		y = 0.91f;
		r = 0.03f;	
        //Draw a circle	
		drawStar1(gl, x, y, r);
		gl.glColor3f(0.075f, 0.098f, 0.153f);
		//Draw small triangles with background color to cut circle, thus star forms
		drawStar2(gl, x, y, r);
		//Draw bounds for the star
		gl.glColor3f(1.0f, 0.98f, 0.0f);
		drawStar3(gl, x, y, r);
		
		gl.glColor3f(0.95f, 0.94f, 0.008f);
		x = 0.65f;
		y = 0.77f;
		r = 0.03f;		
		//Draw a circle	
		drawStar1(gl, x, y, r);
		gl.glColor3f(0.122f, 0.137f, 0.18f);
		//Draw small triangles with background color to cut circle, thus star forms
		drawStar2(gl, x, y, r);
		gl.glColor3f(0.95f, 0.94f, 0.008f);
		//Draw bounds for the star
		drawStar3(gl, x, y, r);
		
		gl.glColor3f(0.91f, 0.91f, 0.02f);
		x = 0.84f;
		y = 0.8f;
		r = 0.03f;		
		//Draw a circle	
		drawStar1(gl, x, y, r);
		gl.glColor3f(0.11f, 0.125f, 0.18f);
		//Draw small triangles with background color to cut circle, thus star forms
		drawStar2(gl, x, y, r);
		gl.glColor3f(0.91f, 0.91f, 0.02f);
		//Draw bounds for the star
		drawStar3(gl, x, y, r);
		
		gl.glColor3f(0.6f, 0.6f, 0.12f);
		x = 0.9f;
		y = 0.5f;
		r = 0.03f;		
		//Draw a circle	
		drawStar1(gl, x, y, r);
		gl.glColor3f(0.23f, 0.23f, 0.255f);
		//Draw small triangles with background color to cut circle, thus star forms
		drawStar2(gl, x, y, r);
		gl.glColor3f(0.6f, 0.6f, 0.12f);
		//Draw bounds for the star
		drawStar3(gl, x, y, r);
		
		gl.glColor3f(0.54f, 0.52f, 0.22f);
		x = 0.8f;
		y = 0.23f;
		r = 0.03f;		
		//Draw a circle	
		drawStar1(gl, x, y, r);
		gl.glColor3f(0.38f, 0.35f, 0.32f);
		//Draw small triangles with background color to cut circle, thus star forms
		drawStar2(gl, x, y, r);
		gl.glColor3f(0.54f, 0.52f, 0.22f);
		//Draw bounds for the star
		drawStar3(gl, x, y, r);
		
		gl.glDisable(GL2.GL_LINE_SMOOTH);
		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
	}

	//draw middle background with gradient effect
	private void drawMiddleBackground(GL2 gl) {
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0.38f, 0.55f, 0.31f);
		gl.glVertex2d(-1.0f, -0.65f);
		gl.glColor3f(0.32f, 0.26f, 0.25f);
		gl.glVertex2d(-1.0f, -0.14f);
		gl.glVertex2d(1.0f, -0.14f);
		gl.glColor3f(0.38f, 0.55f, 0.31f);
		gl.glVertex2d(1.0f, -0.65f);
		gl.glEnd();
		
	}
	
	//draw middle part
	private void drawMiddlePart(GL2 gl) {
		float x, y, ww, hh, r;
		int i;
		//Smooth all points, lines and polygons
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		//Draw chimneys
		ww = 0.05f;
		hh = 0.15f;
		for(i = 0; i < 3; ++i) {
			x = -0.65f + i * 0.65f;
			if (i == 1) {				
				y = 0.0f;
				//Middle chimney is little higher
				drawChimney(gl, x, y, ww, hh);
				
			}
			else {
				y = -0.09f;
				drawChimney(gl, x, y, ww, hh);
			}			
		}
		
		//Draw smoke
		x = -0.64f;
		y = 0.06f;
		ww = 0.04f;
		hh = 0.03f;
		drawSmoke(gl, x, y, ww, hh);
			
		//Draw two brown houses
		line_w = 1.5f;
		gl.glLineWidth(line_w);
		for (i = 0; i < 2; ++i)  {
			x = -0.05f + i * 0.65f;
			y = -0.55f - i * 0.09f;
			ww = 0.3f;
			hh = 0.47f;
			gl.glColor3f(0.56f, 0.32f, 0.04f);
			drawHouse(gl, x, y, ww, hh);
			
			//draw door
			x = -0.04f + i * 0.65f;
			y = -0.55f - i * 0.09f;
			ww = 0.07f;
			hh = 0.25f;
			drawDoor(gl, x, y, ww, hh);
			
			//draw door knob
			//draw a bigger points with black color
			gl.glPointSize(7.0f);
			gl.glBegin(GL2.GL_POINTS);
			gl.glColor3f(0.0f, 0.0f, 0.0f);		  
			gl.glVertex2d(x + 0.01f, y + 0.12f);
			gl.glEnd();
			//draw a smaller points on the bigger one to form knob
			gl.glPointSize(6.0f);
			gl.glBegin(GL2.GL_POINTS);
			gl.glColor3f(0.74f, 0.80f, 0.80f);		  
			gl.glVertex2d(x + 0.01f, y + 0.12f);
			gl.glEnd();
			
			//draw windows
			x = 0.08f + i * 0.65f;
			y = -0.43f - i * 0.09f;
			ww = 0.07f;
			hh = 0.11f;
			drawWindow(gl, x, y, ww, hh);
			x = 0.16f + i * 0.65f;
			drawWindow(gl, x, y, ww, hh);			
		}
		
		//Draw kite
		x = 0.5f;
		y = 0.35f;
		r = 0.1f;
		drawKite(gl, x, y, r);
		
		//Draw window on the door for right houses
		gl.glColor3f(1.0f, 0.99f, 0.57f);
		x = 0.645f;
		y = -0.45f;
		r = 0.025f;
		drawWindow2(gl, x, y, r);
		
		//Draw left house
		x = -0.85f;
		y = -0.64f;
		ww = 0.3f;
		hh = 0.47f;
		gl.glColor3f(0.32f, 0.31f, 0.0f);
		line_w = 0.9f;
		gl.glLineWidth(line_w);
		drawHouse(gl, x, y, ww, hh);
		
		//draw roof
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0.31f, 0.25f, 0.13f);
		gl.glVertex2d(x, y + hh);
		gl.glVertex2d(x + ww/2, y + hh + hh/2);
		gl.glVertex2d(x + ww, y + hh);
		gl.glEnd(); 
		//draw bounds for roof
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glVertex2d(x, y + hh);
		gl.glVertex2d(x + ww/2, y + hh + hh/2);
		gl.glVertex2d(x + ww, y + hh);
		gl.glEnd(); 
		
		//draw lines on roof
		for(i = 1; i < 6; ++i) {
			if (i != 3) {
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex2d(x + ww/2, y + hh + hh/2);
				gl.glVertex2d(x + i * 0.05f, y + hh);
				gl.glEnd();
			}
		}
		
		//draw door for left house
		x = -0.78f;
		y = -0.64f;
		ww = 0.07f;
		hh = 0.25f;
		drawDoor(gl, x, y, ww, hh);
		
		//draw door knob for left house
		//draw a bigger points with black color
		gl.glPointSize(7.0f);
		gl.glBegin(GL2.GL_POINTS);
		gl.glColor3f(0.0f, 0.0f, 0.0f);		  
		gl.glVertex2d(x + 0.01f, y + 0.12f);
		gl.glEnd();
		//draw a smaller points on the bigger one to form knob
		gl.glPointSize(6.0f);
		gl.glBegin(GL2.GL_POINTS);
		gl.glColor3f(0.74f, 0.80f, 0.80f);		  
		gl.glVertex2d(x + 0.01f, y + 0.12f);
		gl.glEnd();  
		
		//draw window
		x = -0.66f;
		y = -0.34f;
		ww = 0.07f;
		hh = 0.11f;
		drawWindow(gl, x, y, ww, hh);
		
		//draw fence for left two houses
		x = -0.984f;
		y = -0.64f;
		ww = 0.031f;
		hh = 0.26f;
		drawFence(gl, x, y, ww, hh);
		x = -0.54f;
		drawFence(gl, x, y, ww, hh);
		x = -0.406f;
		drawFence(gl, x, y, ww, hh);
		
		//draw fence at rightmost side
		x = 0.9f;
		ww = 0.065f;
		drawFence2(gl, x, y, ww, hh);
		
		for(i = 1; i < 5; ++i) {
			x = 0.6f - i * ww;
			drawFence2(gl, x, y, ww, hh);
		}
		
		//Draw five-pointed star
		x = 0.1f;
		y = 0.0f;
		r = 0.04f;
		drawFiveStar(gl, x, y, r);
				
		//Disbale smooth effect		
		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_LINE_SMOOTH);
		gl.glDisable(GL2.GL_POINT_SMOOTH);
		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
	}
	
	//Draw bottom part
	private void drawBottom(GL2 gl) {
		line_w = 1.3f;
		gl.glLineWidth(line_w);
		//draw background
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0.57f, 0.57f, 0.57f);
		gl.glVertex2d(-1.0f, -1.0f);
		gl.glVertex2d(-1.0f, -0.65f);
		gl.glVertex2d(1.0f, -0.65f);
		gl.glVertex2d(1.0f, -1.0f);
		gl.glEnd();  
		
		//Smooth all lines, polygons
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		
		//draw two white lines as bounds
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(-1.0f, -0.65f);
		gl.glVertex2d(1.0f, -0.65f);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(-1.0f, -0.997f);
		gl.glVertex2d(1.0f, -0.997f);
		gl.glEnd();  
		
		//Draw white lines on road
		int i;
		for(i = 0; i < 32; ++i) {
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(-0.95f + i * 0.1f, -1.0f);
			gl.glVertex2d(-0.89f + i * 0.1f, -0.65f);
			gl.glEnd();  
		}
		
		//draw a bigger horizontal polygon on the road
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4f(0.79f, 0.79f, 0.70f, 0.7f);
		gl.glVertex2d(0.06f, -0.85f);
		gl.glVertex2d(0.075f, -0.75f);
		gl.glVertex2d(0.425f, -0.75f);
		gl.glVertex2d(0.41f, -0.85f);
		gl.glEnd();  
		
		line_w = 2.5f;
		gl.glLineWidth(line_w);
		//draw bounds for polygon above
		gl.glColor4f(0.92f, 0.92f, 0.92f, 0.7f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(0.06f, -0.85f);
		gl.glVertex2d(0.075f, -0.75f);
		gl.glVertex2d(0.425f, -0.75f);
		gl.glVertex2d(0.41f, -0.85f);
		gl.glEnd();
		
		//Draw 6 lines to cut bigger polygon to 7 smaller polygons
		for (i = 1; i < 7; ++i) {
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(0.06f + i *0.05f, -0.85f);
			gl.glVertex2d(0.075f + i * 0.05f, -0.75f);
			gl.glEnd();
			
		}
		
		//draw vertical polygon
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4f(0.79f, 0.79f, 0.70f, 0.7f);
		gl.glVertex2d(0.20f, -0.90f);
		gl.glVertex2d(0.235f, -0.70f);
		gl.glVertex2d(0.285f, -0.70f);
		gl.glVertex2d(0.25f, -0.90f);
		gl.glEnd(); 
		
		//draw bounds for polygon above
		gl.glColor4f(0.92f, 0.92f, 0.92f, 0.7f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(0.20f, -0.90f);
		gl.glVertex2d(0.235f, -0.70f);
		gl.glVertex2d(0.285f, -0.70f);
		gl.glVertex2d(0.25f, -0.90f);
		gl.glEnd();
		
		//draw line to form 2 smaller vertical polygons
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(0.217f, -0.8f);
		gl.glVertex2d(0.267f, -0.8f);
		gl.glEnd();
		
		//draw vertical polygon
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4f(0.79f, 0.79f, 0.70f, 0.7f);
		gl.glVertex2d(0.30f, -0.90f);
		gl.glVertex2d(0.335f, -0.70f);
		gl.glVertex2d(0.385f, -0.70f);
		gl.glVertex2d(0.35f, -0.90f);
		gl.glEnd(); 
		
		//draw bounds for polygon above
		gl.glColor4f(0.92f, 0.92f, 0.92f, 0.7f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(0.30f, -0.90f);
		gl.glVertex2d(0.335f, -0.70f);
		gl.glVertex2d(0.385f, -0.70f);
		gl.glVertex2d(0.35f, -0.90f);
		gl.glEnd();
		//draw line to form 2 smaller vertical polygons
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(0.317f, -0.8f);
		gl.glVertex2d(0.367f, -0.8f);
		gl.glEnd();
		
		//Disbale smooth effect		
		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_LINE_SMOOTH);
		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
	
		
	}
	
	//Draw chimney
	private void drawChimney(GL2 gl, float x, float y, float w, float h) {
		//draw chimeny with rectangle
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0.58f, 0.067f, 0.0f);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd();
		//draw bounds for chimney above
		line_w = 0.9f;
		gl.glLineWidth(line_w);
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd();
	}
	
	//Draw house
	private void drawHouse(GL2 gl, float x, float y, float w, float h) {
		//draw a pentagon house
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w/2, y + h + h/2);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd(); 
			
		//draw bounds for house above
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w/2, y + h + h/2);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd();
	}
	
	//Draw door
	private void drawDoor(GL2 gl, float x, float y, float w, float h){
		//draw door with rectangle
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0.80f, 0.57f, 0.0f);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd(); 
				
		line_w = 0.9f;
		gl.glLineWidth(line_w);
		//draw bounds for door above
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd();
	}
	
	//Draw window
	private void drawWindow(GL2 gl, float x, float y, float w, float h) {
		
		//draw square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0.91f, 0.80f, 0.90f);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd(); 
		
		//draw triangle
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(1.0f, 0.99f, 0.57f);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x + w/2, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd();
		
		//draw triangle bounds
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		line_w = 0.9f;
		gl.glLineWidth(line_w);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x + w/2, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd();
		
		//draw square bounds
		line_w = 2.2f;
		gl.glLineWidth(line_w);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2d(x, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + w, y);
		gl.glEnd();
		//draw two cross lines
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(x + w/2, y + h);
		gl.glVertex2d(x + w/2, y);
		gl.glEnd();
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(x, y + h/2);
		gl.glVertex2d(x + w, y + h/2);
		gl.glEnd();

	}
	
	//Draw window on the door
	private void drawWindow2(GL2 gl, float x, float y, float r) {
		int theta;
		//Draw 8 polygons to form it
		gl.glBegin(GL2.GL_POLYGON);
		for(theta = 0; theta < 8; ++theta) {
			gl.glVertex2d(r * Math.cos(2 * PI * theta/8) + x, 
					1.6 * r * Math.sin(2 * PI * theta/8) + y);
		}
		gl.glEnd();
		//Draw bounds for this window
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		line_w = 0.8f;
		gl.glLineWidth(line_w);
		gl.glBegin(GL.GL_LINE_LOOP);
		for(theta = 0; theta < 8; ++theta) {
			gl.glVertex2d(r * Math.cos(2 * PI * theta/8) + x, 
					1.6 * r * Math.sin(2 * PI * theta/8) + y);
		}
		gl.glEnd();
	}
	
	//Draw smoke
	private void drawSmoke(GL2 gl, float x, float y, float w, float h) {
	//	int j;
		gl.glColor3f(0.71f, 0.69f, 0.67f);
		
		//Draw quad strip to form smoke
		gl.glBegin(GL2.GL_QUAD_STRIP);
		
		gl.glVertex2d(x, y);
		gl.glVertex2d(x + w, y);
		gl.glVertex2d(x, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + 0.2 * w, y + h);
		gl.glVertex2d(x + w, y + h);
		gl.glVertex2d(x + 0.3 * w, y + 2.3 * h);
		gl.glVertex2d(x + 1.1 * w, y + 2.3 * h);
		gl.glVertex2d(x + 0.3 * w, y + 3.5 * h);
		gl.glVertex2d(x + 1.0 * w, y + 3.5 * h);
		gl.glVertex2d(x, y + 4.8 * h);
		gl.glVertex2d(x + w, y + 4.8 * h);
		gl.glVertex2d(x, y + 5.8 * h);
		gl.glVertex2d(x + 0.6 * w, y + 5.8 * h);
		gl.glVertex2d(x + 0.2 * w, y + 7 * h);
		gl.glVertex2d(x + 0.9 * w, y + 7 * h);
		gl.glVertex2d(x + 0.4 * w, y + 8 * h);
		gl.glVertex2d(x + 0.7 * w, y + 8 * h);
		
		gl.glEnd();  
	}
	
	//Draw Kite
	private void drawKite(GL2 gl, float x, float y, float r) {
		int j;
		float x1, y1;
		gl.glColor3f(0.24f,  0.42f,  0.91f);
		//Draw triangle fan to form half kite
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glVertex2d(x, y);
		for(j = 0; j < 6; ++j) {
			x1 = (float) (r * Math.cos((360 - j*20) * 2 * PI/360) + x);
			y1 = (float) (1.5 * r * Math.sin((360 - j*20) * 2 * PI/360) + y);
			gl.glVertex2d(x1, y1);
		}
		gl.glEnd();
		
		//Draw triangle fan to form half kite
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glVertex2d(x, y);
		for(j = 0; j < 6; ++j) {
			x1 = (float) (r * Math.cos((180 - j*20) * 2 * PI/360) + x);
			y1 = (float) (1.5 *r * Math.sin((180 - j*20) * 2 * PI/360) + y);
			gl.glVertex2d(x1, y1);
		}
		gl.glEnd();
				
		gl.glColor3f(0.52f,  0.52f,  0.45f);
		line_w = 0.9f;
		gl.glLineWidth(line_w);
		gl.glBegin(GL2.GL_LINES);
		//draw half kite's bounds
		for(j = 0; j < 6; ++j) {
			gl.glVertex2d(x, y);
			x1 = (float) (r * Math.cos((360 - j*20) * 2 * PI/360) + x);
			y1 = (float) (1.5 * r * Math.sin((360 - j*20) * 2 * PI/360) + y);
			gl.glVertex2d(x1, y1);
			
			if (j <= 4) {
				gl.glVertex2d(x1, y1);
				gl.glVertex2d((float) (r * Math.cos((360 - (j+1)*20) * 2 * PI/360) + x), 
						(float) (1.5 * r * Math.sin((360 - (j+1)*20) * 2 * PI/360) + y));				
			}			
		}
		//draw half kite's bounds
		for(j = 0; j < 6; ++j) {
			gl.glVertex2d(x, y);
			x1 = (float) (r * Math.cos((180 - j*20) * 2 * PI/360) + x);
			y1 = (float) (1.5 * r * Math.sin((180 - j*20) * 2 * PI/360) + y);
			gl.glVertex2d(x1, y1);
			
			if (j <= 4) {
				gl.glVertex2d(x1, y1);
				gl.glVertex2d((float) (r * Math.cos((180 - (j+1)*20) * 2 * PI/360) + x), 
						(float) (1.5 * r * Math.sin((180 - (j+1)*20) * 2 * PI/360) + y));				
			}			
		}
		gl.glEnd();	
		
		line_w = 1.4f;
		gl.glLineWidth(line_w);
		//draw kite rope
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex2d(0.57f, -0.34f);
		gl.glVertex2d(0.5f, -0.24f);
		gl.glVertex2d(0.45f, -0.04f);
		gl.glVertex2d(0.46f, 0.1f);
		gl.glVertex2d(0.41f, 0.2f);
		gl.glVertex2d(0.45f, 0.3f);
		gl.glVertex2d(0.48f, 0.32f);
		gl.glVertex2d(0.5f, 0.35f);
		gl.glEnd();
		
	}
	
	//Draw fence
	private void drawFence(GL2 gl, float x, float y, float w, float h) {
		int j;
		
		for(j = 0; j < 4; ++j) {
			gl.glColor3f(0.8f, 0.79f, 0.57f);
			//draw quads to form fence
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(x + w * j, y);
			gl.glVertex2d(x + w * j, y + h);
			gl.glVertex2d(x + w * (j + 1), y + h + 0.04f);
			gl.glVertex2d(x + w * (j + 1), y);
			gl.glEnd();
			
			//draw bounds
			line_w = 0.9f;
			gl.glLineWidth(line_w);
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(x + w * j, y);
			gl.glVertex2d(x + w * j, y + h);
			gl.glVertex2d(x + w * (j + 1), y + h + 0.04f);
			gl.glVertex2d(x + w * (j + 1), y);
			gl.glEnd();
		}
			
	}
		
		//Draw fence at right side
		private void drawFence2(GL2 gl, float x, float y, float w, float h) {
			gl.glColor3f(0.8f, 0.79f, 0.57f);
			//draw pategon for fence unit
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x, y + h);
			gl.glVertex2d(x + w/2, y + h + 0.04f);
			gl.glVertex2d(x + w, y + h);
			gl.glVertex2d(x + w, y);
			gl.glEnd();
			
			//Draw bounds
			line_w = 0.9f;
			gl.glLineWidth(line_w);
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x, y + h);
			gl.glVertex2d(x + w/2, y + h + 0.04f);
			gl.glVertex2d(x + w, y + h);
			gl.glVertex2d(x + w, y);
			gl.glEnd();
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(x + w/2, y + h + 0.04f);
			gl.glVertex2d(x + w/2, y);
			gl.glEnd();
				
	}
		
	//draw five-pointed star
	private void drawFiveStar(GL2 gl, float x, float y, float r) {
		int j;
		//fill pentagon
		gl.glColor3f(1.0f, 0.98f, 0.0f);
		gl.glBegin(GL2.GL_POLYGON);
		for(j = 0; j < 5; ++j) {
			gl.glVertex2d((r * Math.cos((306 - j * 72) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((306 - j * 72) * 2 * PI/360)) + y);
			gl.glVertex2d((r * Math.cos((306 - (j+1) * 72) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((306 - (j + 1) * 72) * 2 * PI/360)) + y);
		}
		gl.glEnd();
		
		//fill five triangles with background color to form five-pointed star
		gl.glColor3f(0.56f, 0.32f, 0.04f);
		for(j = 0; j < 5; ++j) {
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d((r * Math.cos((306 - j * 72) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((306 - j * 72) * 2 * PI/360)) + y);
			gl.glVertex2d((0.4 * r * Math.cos((306 - j * 72 - 36) * 2 * PI/360)) + x, 
					(0.5 * r * Math.sin((306 - j * 72 - 36) * 2 * PI/360)) + y);
			gl.glVertex2d((r * Math.cos((306 - (j+1) * 72) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((306 - (j + 1) * 72) * 2 * PI/360)) + y);
			gl.glEnd();
		}
		
		//draw bounds for star
		line_w = 0.9f;
		gl.glLineWidth(line_w);
		gl.glColor3f(0.0f,  0.0f, 0.0f);
		gl.glBegin(GL.GL_LINE_LOOP);
		for(j = 0; j < 5; ++j) {
			gl.glVertex2d((r * Math.cos((306 - j * 72) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((306 - j * 72) * 2 * PI/360)) + y);
			gl.glVertex2d((0.4 * r * Math.cos((306 - j * 72 - 36) * 2 * PI/360)) + x, 
					(0.5 * r * Math.sin((306 - j * 72 - 36) * 2 * PI/360)) + y);
			gl.glVertex2d((r * Math.cos((306 - (j+1) * 72) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((306 - (j + 1) * 72) * 2 * PI/360)) + y);
		}
		gl.glEnd();
	}
	
	//Draw star
	private void drawStar1(GL2 gl, float x, float y, float r) {
		int j;
		//fill circle with 8 triangles
		gl.glColor3f(1.0f, 0.98f, 0.0f);
		gl.glBegin(GL2.GL_POLYGON);
		for(j = 0; j < 8; ++j) {
			gl.glVertex2d((r * Math.cos((360 - j * 45) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((360 - j * 45) * 2 * PI/360)) + y);
			gl.glVertex2d((r * Math.cos((360 - (j+1) * 45) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((360 - (j + 1) * 45) * 2 * PI/360)) + y);
		}
		gl.glEnd();
	}
	
	//Draw star
	private void drawStar2(GL2 gl, float x, float y, float r) {
		int j;
		//fill eight triangles with background color to form star
		for(j = 0; j < 8; ++j) {
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d((r * Math.cos((360 - j * 45) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((360 - j * 45) * 2 * PI/360)) + y);
			gl.glVertex2d((0.4 * r * Math.cos((360 - j * 45 - 22) * 2 * PI/360)) + x, 
					(0.5 * r * Math.sin((360 - j * 45 - 22) * 2 * PI/360)) + y);
			gl.glVertex2d((r * Math.cos((360 - (j+1) * 45) * 2 * PI/360)) + x, 
					(1.5 * r * Math.sin((360 - (j + 1) * 45) * 2 * PI/360)) + y);
			gl.glEnd();
		}		
	}
	
	//Draw star's bounds
		private void drawStar3(GL2 gl, float x, float y, float r) {
			int j;
			line_w = 0.9f;
			gl.glLineWidth(line_w);
			gl.glBegin(GL.GL_LINE_LOOP);
			for(j = 0; j < 8; ++j) {
				gl.glVertex2d((r * Math.cos((360 - j * 45) * 2 * PI/360)) + x, 
						(1.5 * r * Math.sin((360 - j * 45) * 2 * PI/360)) + y);
				gl.glVertex2d((0.4 * r * Math.cos((360 - j * 45 - 22) * 2 * PI/360)) + x, 
						(0.5 * r * Math.sin((360 - j * 45 - 22) * 2 * PI/360)) + y);
				gl.glVertex2d((r * Math.cos((360 - (j+1) * 45) * 2 * PI/360)) + x, 
						(1.5 * r * Math.sin((360 - (j + 1) * 45) * 2 * PI/360)) + y);
			}
			gl.glEnd();	
		}
		
	//Draw Full Moon
		private void drawMoon(GL2 gl, float x, float y, float r) {
			int j;
			//draw full moon
			//fill circle with 18 triangles
			gl.glColor3f(1.0f, 1.0f, 1.0f);			
			gl.glBegin(GL2.GL_POLYGON);
			for(j = 0; j < 18; ++j) {
				gl.glVertex2d((r * Math.cos((350 - j * 20) * 2 * PI/360)) + x, 
						(1.6 * r * Math.sin((350 - j * 20) * 2 * PI/360)) + y);
				gl.glVertex2d((r * Math.cos((350 - (j+1) * 20) * 2 * PI/360)) + x, 
						(1.6 * r * Math.sin((350 - (j + 1) * 20) * 2 * PI/360)) + y);
			}
			gl.glEnd();
			
			//draw bounds 
			line_w = 0.9f;
			gl.glLineWidth(line_w);
			gl.glBegin(GL.GL_LINE_LOOP);
			for(j = 0; j < 18; ++j) {
				gl.glVertex2d((r * Math.cos((350 - j * 20) * 2 * PI/360)) + x, 
						(1.6 * r * Math.sin((350 - j * 20) * 2 * PI/360)) + y);
				gl.glVertex2d((r * Math.cos((350 - (j+1) * 20) * 2 * PI/360)) + x, 
						(1.6 * r * Math.sin((350 - (j + 1) * 20) * 2 * PI/360)) + y);
			}
			gl.glEnd();	
			
			//draw polygon for half moon
			/*start from 310 degree to 90 degree at clockwise way, calculate the other side 
			vertice's positions by rotating 20 degree*/
			gl.glColor3f(0.32f, 0.32f, 0.39f);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d((r * Math.cos(310 * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin(310 * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos(330 * 2 * PI/360)) + x - Math.cos(PI/9) * 0.07f, 
					(1.6 * r * Math.sin(330 * 2 * PI/360)) + y - Math.sin(PI/9) * 0.07f);

			
			gl.glVertex2d((r * Math.cos(350 * 2 * PI/360)) + x - Math.cos(PI/9) * 0.12f, 
					(1.6 * r * Math.sin(350 * 2 * PI/360)) + y - Math.sin(PI/9) * 0.12f);
			
			gl.glVertex2d((r * Math.cos((10) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.14f, 
					(1.6 * r * Math.sin((10) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.14f);

			
			gl.glVertex2d((r * Math.cos((30) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.14f, 
					(1.6 * r * Math.sin((30) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.14f);
			
			
			gl.glVertex2d((r * Math.cos((50) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.12f, 
					(1.6 * r * Math.sin((50) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.12f);

			
			gl.glVertex2d((r * Math.cos((70) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.07f, 
					(1.6 * r * Math.sin((70) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.07f);
			
			gl.glVertex2d((r * Math.cos(90 * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin(90 * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((70) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((70) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((50) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((50) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((30) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((30) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((10) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((10) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((310 + 2 * 20) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((310 + 2 * 20) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((310 + 1 * 20) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((310 + 1 * 20) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos(310* 2 * PI/360)) + x, 
					(1.6 * r * Math.sin(310 * 2 * PI/360)) + y);
			
			gl.glEnd(); 
			
			//draw bounds for half moon
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d((r * Math.cos(310 * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin(310 * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos(330 * 2 * PI/360)) + x - Math.cos(PI/9) * 0.07f, 
					(1.6 * r * Math.sin(330 * 2 * PI/360)) + y - Math.sin(PI/9) * 0.07f);
			
			gl.glVertex2d((r * Math.cos(350 * 2 * PI/360)) + x - Math.cos(PI/9) * 0.12f, 
					(1.6 * r * Math.sin(350 * 2 * PI/360)) + y - Math.sin(PI/9) * 0.12f);
			
			gl.glVertex2d((r * Math.cos((10) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.14f, 
					(1.6 * r * Math.sin((10) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.14f);

			
			gl.glVertex2d((r * Math.cos((30) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.14f, 
					(1.6 * r * Math.sin((30) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.14f);
			
			
			gl.glVertex2d((r * Math.cos((50) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.12f, 
					(1.6 * r * Math.sin((50) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.12f);

			
			gl.glVertex2d((r * Math.cos((70) * 2 * PI/360)) + x - Math.cos(PI/9) * 0.07f, 
					(1.6 * r * Math.sin((70) * 2 * PI/360)) + y - Math.sin(PI/9) * 0.07f);
			
			gl.glVertex2d((r * Math.cos(90 * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin(90 * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((70) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((70) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((50) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((50) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((30) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((30) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((10) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((10) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((310 + 2 * 20) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((310 + 2 * 20) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos((310 + 1 * 20) * 2 * PI/360)) + x, 
					(1.6 * r * Math.sin((310 + 1 * 20) * 2 * PI/360)) + y);
			
			gl.glVertex2d((r * Math.cos(310* 2 * PI/360)) + x, 
					(1.6 * r * Math.sin(310 * 2 * PI/360)) + y);
			
			gl.glEnd(); 
		}
		
		//Generate galaxy with lorenz equations, draw with only x and y values
		private void drawGalaxy(GL2 gl, float x, float y) {
			int j;
			float xnew, ynew, znew, xold, yold, zold;
			float xpot, ypot;
			float dt = 0.01f;
			xold = x;
			yold = y;
			zold = 0.0f;
			xpot = 0.0f;
			ypot = 0.0f;
			gl.glPointSize(1.0f);
			gl.glColor4f(0.57f, 0.57f, 0.57f, 0.5f);
			gl.glBegin(GL2.GL_POINTS);
			
			gl.glVertex2d(x, y);
			for(j = 0; j < 15000; ++j) {
				xnew = (float) (xold - 10 * (xold - yold) * dt);
				ynew = (float) (yold + dt * (28 * xold - yold - xold * zold));
				znew = (float) (zold + dt * (xold * yold - 8 * zold/3));
				
				//make sure generated point in range
				xpot = (xnew/30);
				ypot = (ynew/30) - 0.15f;
				
				xold = xnew;
				yold = ynew;
				zold = znew;
				//rotate 90 degree to show
				gl.glVertex2d(xpot, -ypot);

			}			
			gl.glEnd();
			
		}
}

//******************************************************************************

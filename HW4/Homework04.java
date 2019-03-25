
//******************************************************************************
// Implementation Description
//******************************************************************************
// 1. Implementation of second, third and fourth parts
// a. Draw a rectangle/hexagon/regular 32-gon circle/non-regular polygon container 
//    with 10% off window border
// b. Draw a point/square/octagon/non-regular polygon inside to collision and 
//    bounce off above container.
// c. Generate a random number within 360 (denoting as startangle) to define initial
//    direction vector (cos(startangle), sin(startangle)), set center position as 
//    the point initial position, then calculate initial speed such that it can 
//    cross above container in 2s. Since initial position is screen center, and 
//    speed 5 is enough to cross in 2s, thus set DEFAULT_SPEED as 5.
// d. Store this container’s side and its normal vectors (in anti-clockwise), center 
//    point for each side. Calculate nearest out-ward hit point and related side index 
//    for the insider shape’s current position, moving direction, and speed.
// e. Update point’s position by adding speed*direction to current position. Calculate 
//    distance square (square root needs more time, and square is simpler) between this 
//    insider point and hit point. When this distance is less than a bound, calculate 
//    new moving direction via r = v – 2*(v*n)*n.
// 2. Implementation of bonus 1, 2 and 3
// a. When draw container polygon, set one color for all odd-index side, set another 
//    color for all even-index side. And modify insider shape color when it bounces off 
//    container, the bounced index can be get in step 1d.
// b. Same to above step, set a factor for all odd-index side, another factor for all 
//    even-index side, then multiply it with current speed when the insider shape bounces 
//    off container.
// c. Define a variable to count times of bouncing. After each bouncing, draw the insider 
//    shape in fade color way (set alpha parameter in glColor4f). And, when the number is 20,
//    draw it in total transparent way.
// 
// Notes: all vectors above are normalized.
//******************************************************************************
// Date:2018-04-05
// Author:Lei Wang
//******************************************************************************

//package homework04;
package edu.ou.cs.cg.homework;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

//******************************************************************************

/**
 * The <CODE>Homework04</CODE> class.<P>
 *
 * @author  Lei Wang
 * @version %I%, %G%
 */
public final class Homework04
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final GLU		GLU = new GLU();
	public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();
	
	public static final double DEFAULT_SPEED = 5.0;
	public static final double DEFAULT_X = 640.0;
	public static final double DEFAULT_Y = 360.0;
	
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private int				k = 0;		// Just an animation counter
	private int idx = -1;				//Index of container side
	private int lastIdx = -1;			//Index of last touched side
	private int outMode;				//Container shape
	private int inMode;					//Insider shape
	private int isTouched;				//Current touch times, start from 0
	private int lastTouched;			//Last touch times
	private boolean isSpeed;			//Flag if changes speed
	private int				w;			// Canvas width
	private int				h;			// Canvas height
	private double speed = DEFAULT_SPEED;  //Default speed
	private double px = DEFAULT_X;      //Default x-axis position
	private double py = DEFAULT_Y;		//Default x-axis position
	private double startangle;			//Start angle
	private double inradius;            //Radius of insider shape
	Point2D start;						//Initial position
	private KeyHandler keyhandler;      //Handler for key activity
	static GLJPanel canvas;
	private Vector direction;           //Insider shape direction
	ArrayList<Vector> sides;			//Store side vector of container 
	ArrayList<Vector> normals;			//Store normal vector of each side
	ArrayList<Point2D> centers;			//Store center point of each side
	
	
	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		canvas = new GLJPanel(capabilities);
		JFrame			frame = new JFrame("Homework04");

		canvas.setPreferredSize(new Dimension(1280, 720));

		frame.setBounds(50, 50, 600, 600);
		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

		canvas.addGLEventListener(new Homework04());

		FPSAnimator		animator = new FPSAnimator(canvas, 60);

		animator.start();
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
		w = drawable.getWidth();
		h = drawable.getHeight();
	//	w = drawable.getSurfaceWidth();
		//h = drawable.getSurfaceHeight();
		outMode = 1;
		inMode = 6;
		inradius = 20.0;
		//generate random redirection
		startangle = (double)(RANDOM.nextInt(360));    
		isTouched = -1;
		lastTouched = 0;
		isSpeed = false;
		start = new Point2D.Double(px, py);  
		sides = new ArrayList<Vector>();
		normals = new ArrayList<Vector>();
		centers = new ArrayList<Point2D>();
		direction = new Vector(Math.cos(Math.toRadians(startangle)), Math.sin(Math.toRadians(startangle)));

		keyhandler = new KeyHandler(this);
	}

	public void		dispose(GLAutoDrawable drawable)
	{
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
	
	public Component	getComponent()
	{
		return (Component)canvas;
	}

	//Set speed
	public void setspeed(double speed) {
		this.speed = speed;
	}
	
	//Get speed
	public double getspeed() {
		return speed;
	}
	
	//Set insider radius
	public void setInRadius(double inradius) {
		this.inradius = inradius;
	}
		
	//Get insider radius
	public double getInRadius() {
		return inradius;
	}
	
	//Set container shape
	public void setOutMode(int out) {
		outMode = out;
	}
	
	//Get container shape
	public void setInMode(int in) {
		inMode = in;
	}
	
	//Reset index
	public void resetIdx() {
		idx = -1;
	}
	
	//reset variables
	public void resetStart() {
		Point2D reset = new Point2D.Double(DEFAULT_X, DEFAULT_Y);
		start = reset;
		px = DEFAULT_X;
		py = DEFAULT_Y;
		speed = DEFAULT_SPEED;
		lastIdx = -1;
		isTouched = -1;
		lastTouched = 0;
		isSpeed = false;
	}
	
	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	private void	update()
	{
		k++;									// Counters are useful, right?
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer
		gl.glEnable(GL.GL_BLEND);				//Enable alpha setting
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		setScreenProjection(gl);	
		
		if (outMode == 1) {
			bounceInRect(gl);
		} else if (outMode == 2) {
			bounceInHexagon(gl);
		} else if (outMode == 3) {
			bounceInCircle(gl);
		} else if (outMode == 4) {
			bounceInPoly(gl);
		}
		
	}

	//**********************************************************************
	// Private Methods (Coordinate System)
	//**********************************************************************
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
	
	//bounce in rectangle box
	private void bounceInRect(GL2 gl) {
		sides.clear();
		normals.clear();
		centers.clear();
		drawRectBox(gl);
		
		Vector[] sideArr = new Vector[sides.size()];
		Vector[] normArr = new Vector[normals.size()];
		Point2D[] centerArr = new Point2D[centers.size()];
		//Store container side vectors, normal vectors and centers
		sides.toArray(sideArr);
		normals.toArray(normArr);
		centers.toArray(centerArr);
		
		idx = -1;
		//Find nearest hit point
		Point2D hit = findNearestHitPoint(direction, normArr, centerArr, start);
		double dist = 0.0;
		if (isTouched == -1) {
			gl.glColor3f(1.0f, 1.0f, 1.0f);
		}
		//Change speed, color
		else if (isTouched > lastTouched){
			if (lastIdx % 2 == 0) {
				if(isSpeed == true) {
					speed = speed * 1.2;   //Increase speed when bouncing off side with even index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(1.0f, 0.0f, 0.0f);
				}
				else {
					double alpha = 0.0;
					//If times of bounced is over 20, disappear
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(1.0f, 0.0f, 0.0f, (float) alpha);
				}
				
			}
			else {
				if(isSpeed == true) {
					speed = speed * 0.8;   //Decrease speed when bouncing off side with odd index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(0.0f, 0.0f, 1.0f);
				}
				else {
					double alpha = 0.0;
					//If times of bounced is over 20, disappear
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(0.0f, 0.0f, 1.0f, (float)alpha);
					
				}
			}
		}
		if (inMode == 6) {
			//Draw a point 
			gl.glPointSize(3);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex2d(px, py);
			gl.glEnd();
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
		} else if (inMode == 7) {
			//Draw a square
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			double a = (2.0 * Math.PI) * (1.0/8.0);
			double off = (2.0 * Math.PI) * (1.0/4.0);
			for (int i = 0; i < 4; i++) {
				double vx = px + inradius * Math.cos(a + off * i);
				double vy = py + inradius * Math.sin(a + off * i);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}

			drawPoly(gl, vertices);
			//Calculate distance square instead of square-root, to save calculation time
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if (inMode == 8) {
			//Draw a regular octagon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 8; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if(inMode == 9) {
			//Draw a non regular polygon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 5; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			
			for (int i = 1; i < 3; i++) {
				double a = (2.0 * Math.PI) * (0.5 + 0.25 * i);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		}
		
		double factor = 0.0;
		//Set different factor to calculate distance bound based on insider shape and speed
		if (inMode == 6) {
			factor = 4.0;
		} else if (inMode == 7) {
			factor = inradius * 2.0;
		}
		else {
			factor = inradius * 10.0;
		}
		
		if (dist > speed * factor) {
			px = px + speed * direction.x;
			py = py + speed * direction.y;
		} else {
			lastTouched = isTouched;
			isTouched = isTouched + 1;
			lastIdx = idx;
			isSpeed = true;
			//Calculate new direction after bounce off container
			Vector newdirection = Vector.subVector(direction, Vector.multiVector(normArr[idx], 
					2.0 * Vector.dotProduct(direction, normArr[idx])));

			direction.setX(newdirection.x);
			direction.setY(newdirection.y);

			px = px + speed * direction.getX();
			py = py + speed * direction.getY();
			Point2D newStart = new Point2D.Double(px, py);
			start = newStart;
		}	
		
	}
	
	//bounce in regular hexagon
	private void bounceInHexagon(GL2 gl) {
		sides.clear();
		normals.clear();
		centers.clear();
		drawHexagonBox(gl);
		Vector[] sideArr = new Vector[sides.size()];
		Vector[] normArr = new Vector[normals.size()];
		Point2D[] centerArr = new Point2D[centers.size()];
		//Store container side vectors, normal vectors and centers
		sides.toArray(sideArr);
		normals.toArray(normArr);
		centers.toArray(centerArr);
	
		idx = -1;
		//Find nearest hit point
		Point2D hit = findNearestHitPoint(direction, normArr, centerArr, start);
		double dist = 0.0;
		if (isTouched == -1) {
			gl.glColor3f(1.0f, 1.0f, 1.0f);
		}
		//Change speed, color
		else if (isTouched > lastTouched){
			if (lastIdx % 2 == 0) {
				if(isSpeed == true) {
					speed = speed * 1.2;     //Increase speed after bouncing off side with even index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(1.0f, 0.0f, 0.0f);
				}
				else {
					double alpha = 0.0;
					//Disappear after bouncing off 20 times
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(1.0f, 0.0f, 0.0f, (float) alpha);
				}
				
			}
			else {
				if(isSpeed == true) {
					speed = speed * 0.8;   //Decrease speed after bouncing off side with odd index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(0.0f, 0.0f, 1.0f);
				}
				else {
					double alpha = 0.0;
					//Disappear after bouncing off 20 times
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(0.0f, 0.0f, 1.0f, (float)alpha);
					
				}
			}
		}
		if (inMode == 6) {
			//Draw a point in center
			gl.glPointSize(3);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex2d(px, py);
			gl.glEnd();
			//Calculate distance square
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
		} else if (inMode == 7) {
			//Draw a square
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			double a = (2.0 * Math.PI) * (1.0/8.0);
			double off = (2.0 * Math.PI) * (1.0/4.0);
			for (int i = 0; i < 4; i++) {
				double vx = px + inradius * Math.cos(a + off * i);
				double vy = py + inradius * Math.sin(a + off * i);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if (inMode == 8) {
			//Draw a regular octagon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 8; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if(inMode == 9) {
			//Draw a non regular polygon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 5; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			
			for (int i = 1; i < 3; i++) {
				double a = (2.0 * Math.PI) * (0.5 + 0.25 * i);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			
			drawPoly(gl, vertices);
			//Calculate distance square
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		}
		
		//Set different factor to calculate distance bound based on insider shape and speed
		double factor = 0.0;
		if (inMode == 6) {
			factor = 4.0;
		} else if (inMode == 7) {
			factor = inradius * 2.0;
		}
		else {
			factor = inradius * 10.0;
		}
		
		if (dist > speed * factor) {
			px = px + speed * direction.x;
			py = py + speed * direction.y;
		} else {
			lastTouched = isTouched;
			isTouched = isTouched + 1;
			lastIdx = idx;
			isSpeed = true;
			//Calculate new direction after bouncing off 
			Vector newdirection = Vector.subVector(direction, Vector.multiVector(normArr[idx], 
					2.0 * Vector.dotProduct(direction, normArr[idx])));
			direction.setX(newdirection.x);
			direction.setY(newdirection.y);
			px = px + speed * direction.getX();
			py = py + speed * direction.getY();
			Point2D newStart = new Point2D.Double(px, py);
			start = newStart;
		}	
	
	}
	
	//bounce in circle
	private void bounceInCircle(GL2 gl) {
		sides.clear();
		normals.clear();
		centers.clear();
		
		drawCircleBox(gl);
		
		Vector[] sideArr = new Vector[sides.size()];
		Vector[] normArr = new Vector[normals.size()];
		Point2D[] centerArr = new Point2D[centers.size()];
		//Store container side vectors, normal vectors and centers
		sides.toArray(sideArr);
		normals.toArray(normArr);
		centers.toArray(centerArr);
	
		idx = -1;
		//Find the nearest hit position and side index
		Point2D hit = findNearestHitPoint(direction, normArr, centerArr, start);
		double dist = 0.0;
		if (isTouched == -1) {
			gl.glColor3f(1.0f, 1.0f, 1.0f);
		}
		//Change speed and color based on index of bounced side
		else if (isTouched > lastTouched){
			if (lastIdx % 2 == 0) {
				if(isSpeed == true) {
					speed = speed * 1.2;   //Increase speed when bouncing off side with even index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(1.0f, 0.0f, 0.0f);
				}
				else {
					double alpha = 0.0;
					//Disappear when bouncing over 20 times
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(1.0f, 0.0f, 0.0f, (float) alpha);
				}
				
			}
			else {
				if(isSpeed == true) {
					speed = speed * 0.8;	//Decrease speed when bouncing off side with odd index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(0.0f, 0.0f, 1.0f);
				}
				else {
					double alpha = 0.0;
					//Disappear when bouncing over 20 times
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(0.0f, 0.0f, 1.0f, (float)alpha);
					
				}
			}
		}
		if (inMode == 6) {
			//Draw a point in center
			gl.glPointSize(3);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex2d(px, py);
			gl.glEnd();
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
		} else if (inMode == 7) {
			//Draw a square
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			double a = (2.0 * Math.PI) * (1.0/8.0);
			double off = (2.0 * Math.PI) * (1.0/4.0);
			for (int i = 0; i < 4; i++) {
				double vx = px + inradius * Math.cos(a + off * i);
				double vy = py + inradius * Math.sin(a + off * i);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			drawPoly(gl, vertices);
			//Calculate distance square
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if (inMode == 8) {
			//Draw a regular octagon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 8; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if(inMode == 9) {
			//Draw a non regular polygon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 5; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			
			for (int i = 1; i < 3; i++) {
				double a = (2.0 * Math.PI) * (0.5 + 0.25 * i);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		}
		//Set different factor based on insider shape
		double factor = 0.0;
		if (inMode == 6) {
			factor = 4.0;
		} else if (inMode == 7) {
			factor = inradius * 2.0;
		}
		else {
			factor = inradius * 6.0;
		}
		//Calculate distance bound based
		if (dist > speed * factor) {
			px = px + speed * direction.x;
			py = py + speed * direction.y;
		} else {

			lastTouched = isTouched;
			isTouched = isTouched + 1;
			lastIdx = idx;
			isSpeed = true;
			//Calculate new direction when bouncing off side
			Vector newdirection = Vector.subVector(direction, Vector.multiVector(normArr[idx], 
					2.0 * Vector.dotProduct(direction, normArr[idx])));
			direction.setX(newdirection.x);
			direction.setY(newdirection.y);
			px = px + speed * direction.getX();
			py = py + speed * direction.getY();
			Point2D newStart = new Point2D.Double(px, py);
			start = newStart;
		}
		
	}
	
	//Bounce in non regular polygon
	private void bounceInPoly(GL2 gl) {
		sides.clear();
		normals.clear();
		centers.clear();
		
		drawNonRegBox(gl);
		
		Vector[] sideArr = new Vector[sides.size()];
		Vector[] normArr = new Vector[normals.size()];
		Point2D[] centerArr = new Point2D[centers.size()];
		//Store container side vectors, normal vectors and centers
		sides.toArray(sideArr);
		normals.toArray(normArr);
		centers.toArray(centerArr);
		
		idx = -1;
		//Find nearest hit position and side index
		Point2D hit = findNearestHitPoint(direction, normArr, centerArr, start);
			
		double dist = 0.0;
		if (isTouched == -1) {
			gl.glColor3f(1.0f, 1.0f, 1.0f);
		}
		//Change speed and color based on index of bounced side
		else if(isTouched > lastTouched) {
			if (lastIdx % 2 == 0) {
				if(isSpeed == true) {
					speed = speed * 1.2;	//Increase speed when bouncing off side with even index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(1.0f, 0.0f, 0.0f);
				}
				else {
					double alpha = 0.0;
					//Disappear over bouncing off 20 times
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(1.0f, 0.0f, 0.0f, (float) alpha);
				}
				
			}
			else {
				if(isSpeed == true) {
					speed = speed * 0.8;	//Decrease speed when bouncing off side with odd index
					isSpeed = false;
				}
				if (inMode == 6) {
					gl.glColor3f(0.0f, 0.0f, 1.0f);
				}
				else {
					double alpha = 0.0;
					//Disappear when bouncing off over 20 times
					if (isTouched <= 19) {
						alpha = 1.0 - ((isTouched + 1.0)/20.0);
					} else {
						alpha = 0.0;
					}
					gl.glColor4f(0.0f, 0.0f, 1.0f, (float)alpha);
					
				}
			}
		}
		if (inMode == 6) {
			//Draw a point in center
			gl.glPointSize(3);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex2d(px, py);
			gl.glEnd();
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
		} else if (inMode == 7) {
			//Draw a square
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			double a = (2.0 * Math.PI) * (1.0/8.0);
			double off = (2.0 * Math.PI) * (1.0/4.0);
			for (int i = 0; i < 4; i++) {
				double vx = px + inradius * Math.cos(a + off * i);
				double vy = py + inradius * Math.sin(a + off * i);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if (inMode == 8) {
			//Draw a regular octagon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 8; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		} else if(inMode == 9) {
			//Draw a non regular polygon
			ArrayList<Point2D> vertices = new ArrayList<Point2D>();
			
			for (int i = 0; i < 5; i++) {
				double a = (2.0 * Math.PI) * (i/8.0);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}
			
			for (int i = 1; i < 3; i++) {
				double a = (2.0 * Math.PI) * (0.5 + 0.25 * i);
				double vx = px + inradius * Math.cos(a);
				double vy = py + inradius * Math.sin(a);
				Point2D p = new Point2D.Double(vx, vy);
				vertices.add(p);
			}

			drawPoly(gl, vertices);
			dist = calDistSquare(hit.getX(), hit.getY(), px, py);
			dist = dist - inradius * inradius;
		}
		//Set different factor to calculate distance bound based on insider shape
		double factor = 0.0;
		if (inMode == 6) {
			factor = 4.0;
		} else if (inMode == 7) {
			factor = inradius * 2.0;
		}
		else {
			factor = inradius * 6.0;
		}
		
		if (dist > speed * factor) {
			px = px + speed * direction.x;
			py = py + speed * direction.y;
		} else {
			lastTouched = isTouched;
			isTouched = isTouched + 1;
			lastIdx = idx;
			isSpeed = true;
			//Calculate new direction when bounced off side
			Vector newdirection = Vector.subVector(direction, Vector.multiVector(normArr[idx], 
					2.0 * Vector.dotProduct(direction, normArr[idx])));
			direction.setX(newdirection.x);
			direction.setY(newdirection.y);
			px = px + speed * direction.getX();
			py = py + speed * direction.getY();
			Point2D newStart = new Point2D.Double(px, py);
			start = newStart;
		}
		
	}
	//find the nearest hit point
	private Point2D findNearestHitPoint(Vector dir, Vector[] norms, Point2D[] centers, Point2D start) {
		double tempt = 0;
		double tmin = 3000;
		for(int i = 0 ; i < norms.length; i++) {
			if (Vector.dotProduct(dir, norms[i]) < 0) {
				tempt = Vector.dotProduct(norms[i], new Vector(start, centers[i]))/Vector.dotProduct(norms[i], dir);
				if (tempt < tmin) {
					tmin = tempt;
					idx = i;
				}
			}
		}
		
		double hitx = start.getX() + dir.x * tmin;
		double hity = start.getY() + dir.y * tmin;
		Point2D hitPoint = new Point2D.Double(hitx, hity);
		return hitPoint;
	}
	
	
	//Calculate distance square 
	private double calDistSquare(double ax, double ay, double bx, double by) {
		double distSq = 0.0;
		
		distSq = Math.pow((ax - bx), 2) + Math.pow((ay - by), 2);
		
		return distSq;
	}
	
	//draw a rectangle box to bounce
	private void drawRectBox(GL2 gl) {
		double leftX = 0.1 * w;
		double bottomY = 0.1 * h;
		double rightX = 0.9 * w;
		double upY = 0.9 * h;
		Point2D leftBottom = new Point2D.Double(leftX, bottomY);
		Point2D rightBottom = new Point2D.Double(rightX, bottomY);
		Point2D rightUp = new Point2D.Double(rightX, upY);
		Point2D leftUp = new Point2D.Double(leftX, upY);
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		points.add(leftBottom);
		points.add(rightBottom);
		points.add(rightUp);
		points.add(leftUp);
		sides.add(new Vector(1, 0));
		sides.add(new Vector(0, 1));
		sides.add(new Vector(-1, 0));
		sides.add(new Vector(0, -1));
		normals.add(new Vector(0, 1));
		normals.add(new Vector(-1, 0));
		normals.add(new Vector(0, -1));
		normals.add(new Vector(1, 0));
		centers.add(new Point2D.Double(0.5*w, 0.1*h));
		centers.add(new Point2D.Double(0.9*w, 0.5*h));
		centers.add(new Point2D.Double(0.5*w, 0.9*h));
		centers.add(new Point2D.Double(0.1*w, 0.5*h));
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		//draw polygon, even index side is in one color, odd index side is in other color
		drawPolyWithColor(gl, points);
	}
	
	//draw a hexagon box for to bounce
	private void drawHexagonBox(GL2 gl) {
		double cx = DEFAULT_X;
		double cy = DEFAULT_Y;
		double radius = 0.4 * h;
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0 ; i < 6; i++) {
			double a = (2.0 * Math.PI) * (i/6.0);
			double vx = cx + radius * Math.cos(a);
			double vy = cy + radius * Math.sin(a);
			Point2D p = new Point2D.Double(vx, vy);
			points.add(p);
		}
		
		Point2D[] pArr = new Point2D[points.size()];
		points.toArray(pArr);
		for(int i = 0; i < 5; i++) {
			Vector side = new Vector(pArr[i], pArr[i+1]);
			side.norMalize();
			sides.add(side);
			Vector norm = Vector.calNormal(side);
			normals.add(norm);
			Point2D center = new Point2D.Double((pArr[i].getX() + pArr[i+1].getX())/2.0, (pArr[i].getY()+pArr[i+1].getY())/2.0);
			centers.add(center);
		}
		Vector last = new Vector(pArr[5], pArr[0]);
		last.norMalize();
		sides.add(last);
		Vector lastNorm = Vector.calNormal(last);
		normals.add(lastNorm);
		Point2D lastcenter = new Point2D.Double((pArr[0].getX() + pArr[5].getX())/2.0, (pArr[0].getY()+pArr[5].getY())/2.0);
		centers.add(lastcenter);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		//draw polygon, even index side is in one color, odd index side is in other color
		drawPolyWithColor(gl, points);
	}
	
	//Draw circle box to bounce
	private void drawCircleBox(GL2 gl) {
		double cx = DEFAULT_X;
		double cy = DEFAULT_Y;
		double radius = 0.4 * h;
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0 ; i < 32; i++) {
			double a = (2.0 * Math.PI) * (i/32.0);
			double vx = cx + radius * Math.cos(a);
			double vy = cy + radius * Math.sin(a);
			Point2D p = new Point2D.Double(vx, vy);
			points.add(p);
		}
		
		Point2D[] pArr = new Point2D[points.size()];
		points.toArray(pArr);
		for(int i = 0; i < 31; i++) {
			Vector side = new Vector(pArr[i], pArr[i+1]);
			side.norMalize();
			sides.add(side);
			Vector norm = Vector.calNormal(side);
			normals.add(norm);
			Point2D center = new Point2D.Double((pArr[i].getX() + pArr[i+1].getX())/2.0, (pArr[i].getY()+pArr[i+1].getY())/2.0);
			centers.add(center);
		}
		Vector last = new Vector(pArr[31], pArr[0]);
		last.norMalize();
		sides.add(last);
		Vector lastNorm = Vector.calNormal(last);
		normals.add(lastNorm);
		Point2D lastcenter = new Point2D.Double((pArr[0].getX() + pArr[31].getX())/2.0, (pArr[0].getY()+pArr[31].getY())/2.0);
		centers.add(lastcenter);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		//draw polygon, even index side is in one color, odd index side is in other color
		drawPolyWithColor(gl, points);
		
	}
	
	//draw non regular polygon
	private void drawNonRegBox(GL2 gl) {
		double cx = DEFAULT_X;
		double cy = DEFAULT_Y;
		double radius = 0.4 * h;
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0 ; i < 2; i++) {
			double a = (2.0 * Math.PI) * (i/6.0);
			double vx = cx + radius * Math.cos(a);
			double vy = cy + radius * Math.sin(a);
			Point2D p = new Point2D.Double(vx, vy);
			points.add(p);
		}
		double offset = (2.0 * Math.PI) * (1.0/6.0);
		for(int i = 1; i < 7; i++) {			
			double a = (2.0 * Math.PI) * (i/12.0);
			double vx = cx + radius * Math.cos(a+offset);
			double vy = cy + radius * Math.sin(a+offset);
			Point2D p = new Point2D.Double(vx, vy);
			points.add(p);
		}
		offset = (2.0 * Math.PI) * (2.0/3.0);
		double b = (2.0 * Math.PI) * (1.0/6.0);
		double vx = cx + radius * Math.cos(b+offset);
		double vy = cy + radius * Math.sin(b+offset);
		Point2D p = new Point2D.Double(vx, vy);
		points.add(p);
		offset = (2.0 * Math.PI) * (5.0/6.0);			
			b = (2.0 * Math.PI) * (1.0/12.0);
			vx = cx + radius * Math.cos(b+offset);
			vy = cy + radius * Math.sin(b+offset);
			Point2D q = new Point2D.Double(vx, vy);
			points.add(q);
		
		Point2D[] pArr = new Point2D[points.size()];
		points.toArray(pArr);
		for(int i = 0; i < (points.size() - 1); i++) {
			Vector side = new Vector(pArr[i], pArr[i+1]);
			side.norMalize();
			sides.add(side);
			Vector norm = Vector.calNormal(side);
			normals.add(norm);
			Point2D center = new Point2D.Double((pArr[i].getX() + pArr[i+1].getX())/2.0, (pArr[i].getY()+pArr[i+1].getY())/2.0);
			centers.add(center);
		}
		Vector last = new Vector(pArr[points.size() - 1], pArr[0]);
		last.norMalize();
		sides.add(last);
		Vector lastNorm = Vector.calNormal(last);
		normals.add(lastNorm);
		Point2D lastcenter = new Point2D.Double((pArr[0].getX() + pArr[points.size() - 1].getX())/2.0, (pArr[0].getY()+pArr[points.size() - 1].getY())/2.0);
		centers.add(lastcenter);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		//draw polygon, even index side is in one color, odd index side is in other color
		drawPolyWithColor(gl, points);		
	}
	
	//draw polygon
	private void drawPoly(GL2 gl, ArrayList<Point2D> points)
	{
		gl.glLineWidth(2);
		gl.glBegin(GL2.GL_LINE_LOOP);
		for(Point2D p: points) {
			gl.glVertex2d(p.getX(), p.getY());
		}
			
		gl.glEnd();
	}
	
	//draw polygon, even index side is in one color, odd index side is in other color
	private void drawPolyWithColor(GL2 gl, ArrayList<Point2D> points)
	{
			int j = 0;
			gl.glLineWidth(2);
			gl.glBegin(GL2.GL_LINES);
			Point2D[] pArray = new Point2D[points.size()];
			points.toArray(pArray);
			for (j = 0; j < pArray.length - 1; j++) {
				if (j % 2 == 0) {
					gl.glColor3f(1.0f, 0.0f, 0.0f);
				} else {
					gl.glColor3f(0.0f, 0.0f, 1.0f);
				}
				
				gl.glVertex2d(pArray[j].getX(), pArray[j].getY());
				gl.glVertex2d(pArray[j+1].getX(), pArray[j+1].getY());
			}
			if (j % 2 == 0) {
				gl.glColor3f(1.0f, 0.0f, 0.0f);
			} else {
				gl.glColor3f(0.0f, 0.0f, 1.0f);
			}
			gl.glVertex2d(pArray[j].getX(), pArray[j].getY());
			gl.glVertex2d(pArray[0].getX(), pArray[0].getY());
				
			gl.glEnd();
	}

}


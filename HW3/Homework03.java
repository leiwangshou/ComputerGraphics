//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Mar  1 23:28:19 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160228 [Lei Wang]
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.homework;

//import java.lang.*;
import java.awt.*;
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

//******************************************************************************

/**
 * The <CODE>Homework03</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Homework03
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final GLU		GLU = new GLU();
	public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private int				k = 0;		// Just an animation counter

	private int				w;			// Canvas width
	private int				h;			// Canvas height

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		GLJPanel		canvas = new GLJPanel(capabilities);
		JFrame			frame = new JFrame("Homework03");

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
		
		View view = new View(canvas);

	}
}

//******************************************************************************

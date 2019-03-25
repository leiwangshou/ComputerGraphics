//package homework04;

package edu.ou.cs.cg.homework;

import java.awt.geom.Point2D;

//class Vector

public class Vector {
	double x, y;
	
	//Default constructor
	public Vector() {
		x = 1;
		y = 0;
	}
	
	//Override constructor
	public Vector(Point2D p, Point2D q){
		x = q.getX() - p.getX();
		y = q.getY() - p.getY();

	}
	
	//Override constructor
	public Vector(double x, double y){
		this.x = x;
		this.y = y;
		}
	
	//set x
	public void setX(double x){
		this.x = x;
	}
	
	//set y
	public void setY(double y){
		this.y = y;
	}
				
	//get x
	public double getX(){
		return x;
	}
		
	//get y
	public double getY(){
		return y;
	}
					
	//calculate length
	public double calLength() {
		double length = Math.sqrt(x*x + y*y);
		return length;
	}
	
	//Normalize vector
	void norMalize() {
		double length = calLength();
		x = x /length;
		y = y /length;
		
	}
	
	public static Vector calNormal(Vector a) {
		return new Vector((-1) *(a.y), a.x);
	}
	
	//Add two vectors
	public static Vector addVector(Vector a, Vector b) {
		
		return new Vector(a.x + b.x, a.y + b.y);
	}
	
	//Substract two vectors
	public static Vector subVector(Vector a, Vector b) {
			
		return new Vector(a.x - b.x, a.y - b.y);
	}
	
	//Calculate dot product of two vectors
	public static double dotProduct(Vector a, Vector b) {
		double dotproduct;
		dotproduct = a.x * b.x + a.y*b.y;
		return dotproduct;
	}
	
	//Calculate dot product of two vectors
	public static Vector multiVector(Vector a, double b) {

		return new Vector(b*(a.x), b*(a.y));
		}

	
}

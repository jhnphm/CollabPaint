/*
 * DrawingPrimitives.java
 *
 * Created on May 5, 2007, 1:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cx.ath.feck.jchat.paint;

import cx.ath.feck.jchat.paint.tools.brush.BrushTip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;


/**
 *
 * @author john
 */
public class DrawingPrimitives {
	private DrawingPrimitives() {
	}
	
	public static void drawLine(Point p0, Point p1, Color color,
			BufferedImage img) {
		Rectangle imgBounds = new Rectangle(img.getWidth(), img.getHeight());
		boolean isSteep = false;
		int x0;
		int x1;
		int y0;
		int y1;
		int dx;
		int dy;
		int err;
		int y;
		int ystep = 1;
		
		x0 = p0.x;
		y0 = p0.y;
		x1 = p1.x;
		y1 = p1.y;
		
		//Swap x/y if steep
		if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
			int tmp;
			isSteep = true;
			tmp = x0;
			x0 = y0;
			y0 = tmp;
			tmp = x1;
			x1 = y1;
			y1 = tmp;
		}
		
		//Swap points 0,1 if point 1 is located before point 2 on x axis
		if (x0 > x1) {
			int tmp;
			tmp = x0;
			x0 = x1;
			x1 = tmp;
			tmp = y0;
			y0 = y1;
			y1 = tmp;
		}
		
		//Use bresenham's algorithm for line drawing
		dx = x1 - x0;
		dy = Math.abs(y1 - y0);
		err = -dx / 2;
		
		if (y0 > y1) {
			ystep = -1;
		}
		
		y = y0;
		
		for (int x = x0; x <= x1; x++) {
			if (isSteep) {
				//Check if point is actually on canvas before drawing
				if (imgBounds.contains(y, x)) {
					img.setRGB(y, x, color.getRGB());
				}else{
					return;
				}
			} else {
				//Check if point is actually on canvas before drawing
				if (imgBounds.contains(x, y)) {
					img.setRGB(x, y, color.getRGB());
				}else{
					return;
				}
			}
			
			err = err + dy;
			
			if (err > 0) {
				y = y + ystep;
				err = err - dx;
			}
		}
	}
	public static void drawLineWithBrush(Point p0, Point p1, BrushTip brush,
			BufferedImage img, boolean skipFirst) {
		Rectangle imgBounds = new Rectangle(img.getWidth(), img.getHeight());
		boolean steep = false;
		boolean swapped = false;
		Graphics2D g = img.createGraphics();
		int x0;
		int x1;
		int y0;
		int y1;
		int dx;
		int dy;
		int err;
		int y;
		int ystep = 1;
		int skip = brush.getThickness()/8;
		skip = (skip > 0)?skip:1;
		
		int radius = brush.getThickness() /2;
		x0 = p0.x;
		y0 = p0.y;
		x1 = p1.x;
		y1 = p1.y;
		
		//Swap x/y if steep
		if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
			int tmp;
			steep = true;
			tmp = x0;
			x0 = y0;
			y0 = tmp;
			tmp = x1;
			x1 = y1;
			y1 = tmp;
		}
		
		//Swap points 0,1 if point 1 is located before point 2 on x axis
		if (x0 > x1) {
			int tmp;
			tmp = x0;
			x0 = x1;
			x1 = tmp;
			tmp = y0;
			y0 = y1;
			y1 = tmp;
			swapped = true;
		}
		
		//Use bresenham's algorithm for line drawing
		dx = x1 - x0;
		dy = Math.abs(y1 - y0);
		err = -dx / 2;
		
		if (y0 > y1) {
			ystep = -1;
		}
		
		y = y0;
		
		for (int x = x0; x <= x1; x++) {
			boolean noDraw = false;
			if(skipFirst){
					if(swapped){
						if(x == x1){
							noDraw = true;
						}
					}else{
						if(x == x0){
							noDraw = true;
						}
					}
			}
						
			if(!noDraw){
				if (steep) {
					g.drawImage(brush.getBrushPixmap(),y-radius,x-radius,null);
				} else {
					g.drawImage(brush.getBrushPixmap(),x-radius,y-radius,null);
				}
			}
			
			err = err + dy;
			
			if (err > 0) {
				y = y + ystep;
				err = err - dx;
			}
		}
		g.dispose();
	}
	
	public static void drawCircle(Point p, int radius, Color color,
			BufferedImage img) {
		Rectangle imgBounds = new Rectangle(img.getWidth(), img.getHeight());
		if(radius == 0){		//If 1px wide circle, skip algorithm and just draw dot.
			if (imgBounds.contains(p.x,p.y)) {
				img.setRGB( p.x,p.y, color.getRGB());
			}
			return;
		}
		
		int r2 = radius * radius;
		int x = radius;
		int y = 0;
		int tx;
		int ty;
		int error = radius;
		

		while (y < x) {
			int dy = (y * 2) + 1;
			y = y + 1;
			error = error - dy;
			
			if (error < 0) {
				int dx = 1 - (x * 2);
				x = x - 1;
				error = error - dx;
			}
			tx = p.x+x;
			ty = p.y+y;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}
			tx = p.x+x;
			ty = p.y-y;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}
			tx = p.x-x;
			ty = p.y+y;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}
			tx = p.x-x;
			ty = p.y-y;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}
			tx = p.x+y;
			ty = p.y+x;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}
			tx = p.x+y;
			ty = p.y-x;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}
			tx = p.x-y;
			ty = p.y+x;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}
			tx = p.x-y;
			ty = p.y-x;
			if (imgBounds.contains(tx,ty)) {
				img.setRGB( tx, ty, color.getRGB());
			}

		}
		//Fill missing points at top/right/bottom/left
		tx = p.x+radius;
		ty = p.y;
		if (imgBounds.contains(tx,ty)) {
			img.setRGB( tx, ty, color.getRGB());
		}
		tx = p.x-radius;
		if (imgBounds.contains(tx,ty)) {
			img.setRGB( tx, ty, color.getRGB());
		}
		tx = p.x;
		ty = p.y+radius;
		if (imgBounds.contains(tx,ty)) {
			img.setRGB( tx, ty, color.getRGB());
		}
		tx = p.x;
		ty = p.y-radius;
		if (imgBounds.contains(tx,ty)) {
			img.setRGB( tx, ty, color.getRGB());
		}
	}
	
	public static void drawFilledCircle(Point p, int radius, Color color,
		BufferedImage img){
		drawCircle(p,radius,color,img);
		fill(p,color,img);
	}
	
	public static void drawGradientCircle(Point p, int radius, Color color, BufferedImage img){
		int alphaincrement = 255/(radius +1);
		for(int i=radius; i >= 0; i--){
			drawFilledCircle(p, i, new Color(color.getRed(),color.getGreen(),color.getBlue(),
					(radius -i)*alphaincrement),img);
		}
	}

	
	public static void drawAALine(Point p0, Point p1, Color color,
			BufferedImage img) {
		Rectangle imgBounds = new Rectangle(img.getWidth(), img.getHeight());
		boolean isSteep = false;
		int x0;
		int x1;
		int y0;
		int y1;
		int ox0;
		int ox1;
		int oy0;
		int oy1;
		int dx;
		int dy;
		int err;
		int y;
		int ystep = 1;
		
		x0 = p0.x;
		y0 = p0.y;
		x1 = p1.x;
		y1 = p1.y;
		
		//Swap x/y if steep
		if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
			int tmp;
			isSteep = true;
			tmp = x0;
			x0 = y0;
			y0 = tmp;
			tmp = x1;
			x1 = y1;
			y1 = tmp;
		}
		
		//Swap points 0,1 if point 1 is located before point 2 on x axis
		if (x0 > x1) {
			int tmp;
			tmp = x0;
			x0 = x1;
			x1 = tmp;
			tmp = y0;
			y0 = y1;
			y1 = tmp;
		}
		
		//Use bresenham's algorithm for line drawing
		dx = x1 - x0;
		dy = Math.abs(y1 - y0);
		err = -dx / 2;
		
		if (y0 > y1) {
			ystep = -1;
		}
		
		y = y0;
		
		for (int x = x0; x <= x1; x++) {
			if (isSteep) {
				//Check if point is actually on canvas before drawing
				if (imgBounds.contains(y, x)) {
					img.setRGB(y, x, color.getRGB());
				}
			} else {
				//Check if point is actually on canvas before drawing
				if (imgBounds.contains(x, y)) {
					img.setRGB(x, y, color.getRGB());
				}
			}
			
			err = err + dy;
			
			if (err > 0) {
				y = y + ystep;
				err = err - dx;
			}
		}
	}
	
	public static Rectangle getLineBounds(Point p0, Point p1) {
		return getLineBounds(p0, p1, 1);
	}
	
	public static Rectangle getLineBounds(Point p0, Point p1, int size) {
		Rectangle r =  new Rectangle(p0.x-size,p0.y-size,2*size+1,2*size+1) ;
		r.add(new Rectangle(p1.x-size,p1.y-size,2*size+1,2*size+1));
		return r;
	}
	/**
	 * Fills contiguous area of image 
	 */
	public static void fill(Point p0, Color color, BufferedImage img){
		LinkedList<Point> stack = new LinkedList<Point>();
		Rectangle bounds = new Rectangle(img.getWidth(),img.getHeight());
		if(!bounds.contains(p0)){
			return;
		}
		int oldcolor = img.getRGB(p0.x,p0.y);
		if(oldcolor == color.getRGB()){
			return;
		}
		
		Point pptr;
		stack.push(p0);
		while(!stack.isEmpty()){
			pptr = stack.pop();
			if(img.getRGB(pptr.x,pptr.y) == oldcolor){
				img.setRGB(pptr.x,pptr.y,color.getRGB());
				Point pp2 = new Point(pptr.x-1,pptr.y);
				if(bounds.contains(pp2)){
					stack.push(pp2);
				}
				pp2 = new Point(pptr.x+1,pptr.y);
				if(bounds.contains(pp2)){
					stack.push(pp2);
				}
				pp2 = new Point(pptr.x,pptr.y-1);
				if(bounds.contains(pp2)){
					stack.push(pp2);
				}			
				pp2 = new Point(pptr.x,pptr.y+1);
				if(bounds.contains(pp2)){
					stack.push(pp2);
				}			
			}
		}		
	}
	
	public static Rectangle getSegmentBounds(Point p0, Point p1, int size) {
		int radius = (int) java.lang.Math.ceil(size / 2);
		Rectangle r =  new Rectangle(p0.x-radius,p0.y-radius,size,size) ;
		r.add(new Rectangle(p1.x-radius,p1.y-radius,size,size));
		return r;
	}
}

/*
 * Brush.java
 *
 * Created on April 28, 2007, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cx.ath.feck.jchat.paint.tools.brush;

import cx.ath.feck.jchat.paint.tools.Tool;
import cx.ath.feck.jchat.paint.DrawingPrimitives;
import cx.ath.feck.jchat.paint.Layer;
import cx.ath.feck.jchat.paint.ToolPoint;
import cx.ath.feck.jchat.paint.tools.ToolData;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasAntialiasing;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasColor;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasIncremential;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasThickness;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author John Pham
 */
public class GaussianBrush extends Brush {
	protected transient LinkedList<ToolPoint> toolPoints;
		
	protected Rectangle totalBounds;
	protected Rectangle segmentBounds;

	//Subclasses draw to these.
	protected Layer targetLayer;
	protected Layer displayLayer;
	protected Layer scratchLayer;
	
	protected int thickness;
	
	protected Color color;

	protected transient BufferedImage brushPixmap;
	
	public GaussianBrush(){
		
	}

	private Rectangle drawSegment(ToolPoint p0, ToolPoint p1, boolean isFirst){
		Graphics2D g;
		BufferedImage lineBuffer;
		Rectangle bounds = DrawingPrimitives.getSegmentBounds(p0.getPoint(),p1.getPoint(),brushTip.getThickness());
		bounds = bounds.intersection(new Rectangle(targetLayer.getSize()));
		if(bounds.width <= 0 || bounds.height<= 0){	//Deal w/ garbage if line drawn on edge
			return new Rectangle(0,0,1,1);
		}
		//We draw stuff into a temporary buffer for easy compositing later. 
		//Perhaps later put whole line on temp invisible activeLayer, then merge down for uniformity
		//TODO: Use manual compositing w/ setPixel();  Or perhaps not.
		lineBuffer = new BufferedImage(bounds.width,bounds.height,BufferedImage.TYPE_INT_ARGB);
		g = lineBuffer.createGraphics();
		Point p0t= new Point(p0.getX()-bounds.x,p0.getY()-bounds.y);	//Use these adjusted coordinates to draw into temporary line buffer
		Point p1t= new Point(p1.getX()-bounds.x,p1.getY()-bounds.y);
		//DrawingPrimitives.drawLine(p0t,p1t,color,lineBuffer);
		DrawingPrimitives.drawLineWithBrush(p0t,p1t,brushTip,lineBuffer,!isFirst);
		g.dispose();//Get rid of graphics object used to draw on linebuffer.
//		toolLayer.setComposite(AlphaComposite.SrcOut);
		scratchLayer.drawFrom(lineBuffer,bounds.x,bounds.y);
//		toolLayer.setComposite(AlphaComposite.DstOut);
//		toolLayer.drawFrom(lineBuffer,bounds.x,bounds.y);
		//activeLayer.setRGB(p0.x,p0.y,color.getRGB());
		return bounds;
	}
	
	public static void fillSegment(Point p0, Point p1, BrushTip brush,BufferedImage buffer,boolean isFirst) {
		Rectangle imgBounds = new Rectangle(buffer.getWidth(),buffer.getHeight());
		boolean steep = false;
		boolean swapped = false;
		Graphics2D g = buffer.createGraphics();
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
			if(isFirst){
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
	
    public void start(ToolPoint p) {
		toolPoints = new LinkedList<ToolPoint>();
		brushTip.refresh();
		brushData = new BrushData(toolPoints,brushTip);
		totalBounds = new Rectangle(p.getX(),p.getY(),1,1);
		//toolPoints.add(p);
		this.addPoint(p); //Add toolPoint twice so we don't have to use a special case for addPoint's drawing
    }

    public void addPoint(ToolPoint p) {
		Rectangle bounds;
		if(!toolPoints.isEmpty()){
			if(p.equalLocations(toolPoints.getLast())){
				return;
			}
			bounds = 	drawSegment(toolPoints.getLast(),p, false);
		}else{
			bounds = 	drawSegment(p,p, true);	
		}
		totalBounds.add(bounds);
		segmentBounds = bounds;
		toolPoints.add(p);
    }

    public void end(ToolPoint p) {
        if(!p.equalLocations(toolPoints.getLast())){	//Do this so we don't draw over same point twice, since screws up alpha
			toolPoints.add(p);
		}
		targetLayer.drawFrom(scratchLayer);
    }

    public List<Rectangle> getSegmentBounds() {
		ArrayList<Rectangle> a = new ArrayList<Rectangle>();
		a.add(segmentBounds);
		return a;
    }

    public Rectangle getTotalBounds() {
        return totalBounds;
    }

    public void setTargetLayer(Layer layer) {
        this.targetLayer = layer;
    }

    public void setScratchLayer(Layer layer) {
        this.scratchLayer = layer;
    }

    public void setDisplayLayer(Layer layer) {
        this.displayLayer = layer;
    }

    public ToolData getToolData() {
		return brushData;
    }

    public Color getColor() {
        return brushTip.getColor();
    }

    public void setColor(Color color) {
		this.color = color;
    }

    public void setIncremential(boolean isIncremential) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isIncremential() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getThickness() {
      return this.thickness;
    }

    public void setAntiAliased(boolean isAntiAliased) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isAntiAliased() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void drawToolData(Object toolData, Layer layer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}

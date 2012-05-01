/*
 * Tool.java
 *
 * Created on April 28, 2007, 8:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cx.ath.feck.jchat.paint.tools;

import cx.ath.feck.jchat.paint.Layer;
import cx.ath.feck.jchat.paint.ToolPoint;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author john
 */
public interface Tool {
	
    public void start(ToolPoint p);

   public  void addPoint(ToolPoint p);

   public  void end(ToolPoint p);

    public List<Rectangle> getSegmentBounds();

    public Rectangle getTotalBounds();

    public void setTargetLayer(Layer layer);
	public void setScratchLayer(Layer layer);
	public void setDisplayLayer(Layer layer);
	public Object getToolData();
	public void drawToolData(Object toolData, Layer layer);
}

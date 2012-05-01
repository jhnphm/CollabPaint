/*
 * BrushData.java
 * 
 * Created on May 16, 2007, 7:43:30 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cx.ath.feck.jchat.paint.tools.brush;

import cx.ath.feck.jchat.paint.Layer;
import cx.ath.feck.jchat.paint.ToolPoint;
import cx.ath.feck.jchat.paint.tools.ToolData;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author john
 */
public class BrushData implements ToolData{
	
	final LinkedList toolPoints;
	final GaussianBrushTip tip;
	protected BrushData(LinkedList<ToolPoint> toolPoints,GaussianBrushTip tip){
		this.toolPoints = toolPoints;
		this.tip = tip;
	}
	public Rectangle draw(Layer target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

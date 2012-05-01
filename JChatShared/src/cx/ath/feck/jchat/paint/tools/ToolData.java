/*
 * ToolData.java
 * 
 * Created on May 22, 2007, 10:19:33 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cx.ath.feck.jchat.paint.tools;

import cx.ath.feck.jchat.paint.Layer;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 *
 * @author john
 */
public interface  ToolData extends Serializable{
	public Rectangle draw(Layer target);
}

/*
 * NewClass.java
 * 
 * Created on May 26, 2007, 6:41:54 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cx.ath.feck.jchat.paint.tools.brush;

import cx.ath.feck.jchat.paint.tools.Tool;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasAntialiasing;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasColor;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasIncremential;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasThickness;
import java.io.Serializable;

/**
 *
 * @author john
 */
public abstract class Brush implements HasAntialiasing, HasColor, HasIncremential, HasThickness, Tool,Serializable {

    public Brush() {
    }

}

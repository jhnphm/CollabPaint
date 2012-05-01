/*
 * BrushTip.java
 * 
 * Created on May 18, 2007, 7:48:55 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cx.ath.feck.jchat.paint.tools.brush;

import cx.ath.feck.jchat.shared.Math2;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasColor;
import cx.ath.feck.jchat.paint.tools.toolinterfaces.HasThickness;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author john
 */
public class GaussianBrushTip implements HasColor,BrushTip {
	private transient BufferedImage cache;
    private int thickness;
	private Color color;
	
	public GaussianBrushTip() {
		color = Color.BLACK;
		thickness = 1;
    }

	public Color getColor(){
		return color;
	}

    public void setColor(final Color color) {
        this.color = color;
    }

    public void setThickness(final int thickness) {
		this.thickness = thickness;
    }

    public int getThickness() {
       return thickness;
    }
	
	public int getSpacing(){
		return thickness/4;
	}
	public void refresh(){
		cache = new BufferedImage(thickness,thickness,BufferedImage.TYPE_INT_ARGB);
		float stddev = thickness/6;
		float gaussHeight = 1;
		int radius = thickness/2;
		boolean even = thickness %2 == 0;
		
		for(int i = 0; i < thickness; i++){
			for(int j = 0; j < thickness; j++){
				float intensity;
				int colorRGB;
				int colorAlpha;
				if(even){
					intensity=(float)Math2.gaussian((i-radius < 0)?(i-radius):(i-radius+1),	//Add 1 if even to compare distance from point between pixels
							(j-radius < 0)?(j-radius):(j-radius+1),
							stddev,stddev,0,gaussHeight);
				}else{
					intensity = (float)Math2.gaussian(i-radius, j-radius, stddev,stddev,0,gaussHeight);
				}
				Color color2 = new Color(color.getRed(),color.getGreen(),color.getBlue(),(int)(color.getAlpha()*intensity));
				cache.setRGB(i, j, color2.getRGB());
			}
		}
	}
	public BufferedImage getBrushPixmap(){
		if(cache == null){
			this.refresh();
		}
		return cache;
	}
	public int getPixmapSize(){
		return thickness*2;
	}
}

/*
 * Scale.java
 * 
 * Created on May 12, 2007, 4:52:22 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cx.ath.feck.jchat.paint;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author john
 */
public class Scale {
	int n;
	int d;

	public int getNumerator() {
        return n;
    }

    public int getDenominator() {
        return d;
    }
	
	public Scale(int numerator, int denominator){
		this.n = numerator;
		this.d = denominator;
	}
	public int scale(int num){
		return num*this.n/this.d;
	}
	public int scaleInverse(int num){
		return num*this.d/this.n;
	}
	public Rectangle scale(Rectangle r){
		return new Rectangle(scale(r.x),scale(r.y),scale(r.width),scale(r.height));
	}
	public Rectangle scaleInverse(Rectangle r){
		return new Rectangle(scaleInverse(r.x),scaleInverse(r.y),scaleInverse(r.width),scaleInverse(r.height));
	}
	public Rectangle scaleAndPad(Rectangle r){
		Rectangle r2 = new Rectangle();
			r2.width = r.width * n / d
					+(r.width * n % d);
			r2.height = r.height * n / d
					+(r.height * n % d);
			
			int remainder = r.x * n % d;
			r2.x = r.x * n / d  - remainder;
			r2.width += remainder;
			
			remainder = r.y * n % d;
			r2.y = r.y * n / d  - remainder;
			r2.height += remainder;
			
			r2.width += (r.width * n % d);
			r2.height += (r.height * n % d);
		return r2;
	}
	public Rectangle scaleInverseAndPad(Rectangle r){
		Rectangle r2 = new Rectangle();
			r2.width = r.width * d / n
					+(r.width * d % n);
			r2.height = r.height * d / n
					+(r.height * d % n);
			
			int remainder = r.x * d % n;
			r2.x = r.x * d / n  - remainder;
			r2.width += remainder;
			
			remainder = r.y * d % n;
			r2.y = r.y * d / n  - remainder;
			r2.height += remainder;
			
			r2.width += (r.width * d % n);
			r2.height += (r.height * d % n);
		return r2;
	}
	public int getRatio(){
		return n*100/d;
	}
	/**
	 * Used scaling methods from the java.net article warning against getScaledImage()
	 * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html.
	 * Uses multistep bilinear for ratios less than .50, 1 step for ratios greater than .5 
	 * but less than than 1, and nearest neighbor for greater than 1.
	 */
	public BufferedImage scaleImage(BufferedImage img){
		if(getRatio() == 100){
			return img;
		}
		//long time = (new Date()).getTime();
		//BufferedImage tmp,		//long time = (new Date()).getTime();
		BufferedImage tmp,tmp2;
		tmp2 = img;
		//Rectangle targetRect = scaleAndPad(new Rectangle(img.getWidth(),
		//		img.getHeight()),false).intersection(new Rectangle(Singletons.getCanvasSize()));
		Rectangle  targetRect = new Rectangle(scale(img.getWidth()),scale(img.getHeight()));
		int tw = targetRect.width;
		int th = targetRect.height;
		int ratio = this.getRatio(); //times 10
		if( ratio < 50){	//If less that ~.5
			Graphics2D gt;
			
			int w = img.getWidth();
			int h = img.getHeight();
			do {
				if ( w > tw) {
					w /= 2;
					if (w < tw) {
						w = tw;
					}
				}
				if (h > th) {
					h /= 2;
					if (h < th) {
						h = th;
					}
				}
				tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				gt = tmp.createGraphics();
				gt.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				gt.drawImage(tmp2, 0, 0, w, h, null);
				gt.dispose();
				tmp2 = tmp;
				//System.out.println(w + " " + h);
			} while (w != tw || h != th);
			
			
		}else if (ratio < 100){
			tmp = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = tmp.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(img,0,0,tw,th,null);
			g.dispose();
		}else{
			tmp = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
			Graphics g = tmp.getGraphics();
			g.drawImage(img,0,0,tw,th,null);
			g.dispose();
		}
		return tmp;
	}
	public BufferedImage scaleAndPadImage(BufferedImage img){
		if(getRatio() == 100){
			return img;
		}
		//long time = (new Date()).getTime();
		//BufferedImage tmp,		//long time = (new Date()).getTime();
		BufferedImage tmp,tmp2;
		tmp2 = img;
		Rectangle targetRect = scaleAndPad(new Rectangle(img.getWidth(),
				img.getHeight()));
		//Rectangle  targetRect = new Rectangle(scale(img.getWidth()),scale(img.getHeight()));
		int tw = targetRect.width;
		int th = targetRect.height;
		int ratio = this.getRatio(); //times 10
		if( ratio < 50){	//If less that ~.5
			Graphics2D gt;
			
			int w = img.getWidth();
			int h = img.getHeight();
			do {
				if ( w > tw) {
					w /= 2;
					if (w < tw) {
						w = tw;
					}
				}
				if (h > th) {
					h /= 2;
					if (h < th) {
						h = th;
					}
				}
				tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				gt = tmp.createGraphics();
				gt.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				gt.drawImage(tmp2, 0, 0, w, h, null);
				gt.dispose();
				tmp2 = tmp;
				//System.out.println(w + " " + h);
			} while (w != tw || h != th);
			
			
		}else if (ratio < 100){
			tmp = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = tmp.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(img,0,0,tw,th,null);
			g.dispose();
		}else{
			tmp = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
			Graphics g = tmp.getGraphics();
			g.drawImage(img,0,0,tw,th,null);
			g.dispose();
		}
		return tmp;
	}
}

/*
 * Layer.java
 *
 * Created on May 6, 2007, 7:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cx.ath.feck.jchat.paint;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

//TODO: Implement tile compression
/**
 *
 * @author john
 */
public class Layer {
	final private int tileSize=256;
	private BufferedImage[][] tiles;
	private Graphics2D g;
	private boolean incremential = false;

	private Color tileColor;
	private Composite composite;
	private Scale scale;
	final private Dimension size;

	final private boolean debugTiles = false;
	final private boolean debugDrawTo = false;
	
/** Creates a new instance of Layer 
  * @param dim Size of layer
  */
	public Layer(Dimension dim) {
		this.size = dim;
		flush();
		tileColor = new Color(0,0,0,0);
		composite = AlphaComposite.SrcOver;
		scale = new Scale(1,1);

	}
	
	//TODO: Implement, we need for faster scaling and network
	public BufferedImage getSubImage(Rectangle bounds){
		Rectangle clippedBounds = 
				bounds.intersection(new Rectangle(size));
		BufferedImage img = 
				new BufferedImage(clippedBounds.width,clippedBounds.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		Point p0 = getTileLocationByPixel(clippedBounds.x,clippedBounds.y);
		Point p1 = getTileLocationByPixel(clippedBounds.width+clippedBounds.x-1,
				clippedBounds.height+clippedBounds.y-1);
		int xoffset = clippedBounds.x % tileSize;
		int yoffset = clippedBounds.y % tileSize;
		boolean clear = true;
		if(!tileColor.equals(new Color(0,0,0,0))){
			g.setColor(tileColor);		
			g.fillRect(0,0,img.getWidth(),img.getHeight());
		}

		for(int i = p0.x; i<=p1.x; i++){
			for(int j = p0.y; j <= p1.y; j++){	
				//System.out.println(i + " " + j);
				if(getTile(i,j) == null){
					continue;
				}		
				
				int srcX,srcY,srcW,srcH,dstX,dstY;
	
				srcX = (i == p0.x) ? xoffset : 0;
				srcY = (j == p0.y) ? yoffset : 0;
				
				if(p0.x == p1.x){
					srcW = img.getWidth();
				}else{
					srcW = (i  == p1.x)? ((clippedBounds.x+clippedBounds.width) % tileSize) : 
							(i == p0.x)? tileSize - xoffset: tileSize;
					srcW = (srcW != 0) ? srcW : tileSize;	//if width+x happens to be divisible by tilesize, make width = to tilesize.
				}
				if(p0.y == p1.y){
					srcH = img.getHeight();
				}else{
					srcH = (j  == p1.y)?( (clippedBounds.y+clippedBounds.height) % tileSize) : 
							(j == p0.y)? tileSize - yoffset: tileSize;
					srcH = (srcH != 0) ? srcH : tileSize;
				}

				dstX = (i == p0.x) ? 0 : ((i-p0.x)*tileSize -xoffset);
				dstY = (j == p0.y) ? 0 : ((j-p0.y)*tileSize -yoffset);
				g = img.createGraphics();
				BufferedImage tile = getTile(i, j);
				g.drawImage(tile.getSubimage(srcX, srcY, srcW, srcH),dstX,dstY,null);				

			}
		}
		g.dispose();
		return img;
	}
	
	public void drawFrom(BufferedImage img, int x, int y){
		Graphics2D g;
		Point p0 = getTileLocationByPixel(x,y);
		Point p1 = getTileLocationByPixel(img.getWidth()+x-1,img.getHeight()+y-1);
		//Dimension d = new Dimension(p1.x-p0.x,p1.y-p0.y);
		int xoffset = x % tileSize;
		int yoffset = y % tileSize;
		for(int i = p0.x; i<=p1.x; i++){
			for(int j = p0.y; j <= p1.y; j++){
				int srcX,srcY,srcW,srcH,dstX,dstY;
				if(getTile(i,j) == null){
					createTile(i,j);
				}
				g = getTile(i,j).createGraphics();
				
				g.setComposite(composite);
	
				
				srcX = (i == p0.x) ? 0 : ((i - p0.x)*tileSize -xoffset);
				srcY = (j == p0.y) ? 0 : ((j - p0.y)*tileSize -yoffset);
				
				if(p0.x == p1.x){
					srcW = img.getWidth();
				}else{
					srcW = (i  == p1.x)? (x+img.getWidth()) % tileSize : 
							(i == p0.x)? tileSize - xoffset: tileSize;
					srcW = (srcW == 0) ? tileSize:srcW;
				}
				if(p0.y == p1.y){
					srcH = img.getHeight();
				}else{
					srcH = (j  == p1.y)? (y+img.getHeight()) % tileSize : 
							(j == p0.y)? tileSize - yoffset: tileSize;
					srcH = (srcH == 0) ? tileSize : srcH;
				}

				dstX = i == p0.x ? xoffset : 0;
				dstY = j == p0.y ?  yoffset: 0;

				BufferedImage subImage =
				img.getSubimage(srcX,srcY,srcW,srcH);
				g.drawImage(subImage,dstX,dstY,null);
				g.dispose();
			}
		}
	}
	
	public void drawFrom(Layer layer){
		if(!(layer.getTileGridSize().equals(this.getTileGridSize()) && layer.getSize().equals(this.getSize()))){
			throw new UnsupportedOperationException("Copy between two layers of different size or tileSizes not supported");
		}
		for(int i = 0; i < getTileGridSize().width; i++){
			for(int j = 0; j < getTileGridSize().height; j++){
				if(layer.getTile(i, j) != null){
					if(this.getTile(i, j) == null){
						this.createTile(i,j);
					}
					Graphics2D g = this.getTile(i, j).createGraphics();
					g.drawImage(layer.getTile(i, j),0,0,null);
					g.dispose();
				}
			}
		}
	}
	
	public void drawTo(BufferedImage img){
		drawTo(img,new Rectangle(size));
	}
		
	public void drawTo(BufferedImage img, Rectangle bounds){
		Graphics2D g = img.createGraphics();
		g.setClip(bounds);
		drawTo(g);
		g.dispose();
	}

	public void drawTo(Graphics2D g){
			Rectangle bounds = g.getClipBounds();
			if(scale.getRatio() >100){	//Pad bound box to kill rounding errors.
				bounds = new Rectangle(bounds.x, bounds.y,
						bounds.width+4, bounds.height+4);//Add 2 to avoid tearing artifacts
				//bounds = bounds.intersection(new Rectangle(this.layerSize));
			}
			bounds = scale.scaleInverseAndPad(bounds);
			BufferedImage subImage = getSubImage(bounds);
			subImage = scale.scaleAndPadImage(subImage);
			Rectangle outputBounds = scale.scaleAndPad(bounds);		
			//Rectangle outputBounds = g.getClipBounds();		
			if(debugDrawTo){
				g.setColor(Color.red);
				g.drawRect(g.getClipBounds().x, g.getClipBounds().y, g.getClipBounds().width-1, g.getClipBounds().height-1);
				g.setColor(Color.blue);
				g.drawRect(outputBounds.x,outputBounds.y, outputBounds.width-1,outputBounds.height-1);
			}
			g.drawImage(subImage, outputBounds.x,  outputBounds.y,null);
//		}

		
	}
	
	public void flush(){
		int tileGridWidth = size.width % tileSize == 0 ?
			size.width/tileSize:
			size.width/tileSize+1;
		
		int tileGridHeight = size.height % tileSize == 0 ?
			size.height/tileSize:
			size.height/tileSize+1;
		tiles = new BufferedImage[tileGridWidth][tileGridHeight];
	}
			
	private BufferedImage createTile(int x, int y){
		Dimension d = getTileSize(x, y);
		tiles[x][y] = new BufferedImage(d.width,d.height,
				BufferedImage.TYPE_INT_ARGB);
		if(debugTiles){
			Graphics2D g = tiles[x][y].createGraphics();
			g.setColor(Color.GRAY);
			g.drawRect(0,0, d.width-1,d.height-1);
			g.dispose();
		}
		return tiles[x][y];
	}
	
	public Dimension getTileSize(int x, int y){
		int xdim=tileSize,ydim=tileSize;
		if(x == tiles.length-1){
			xdim = size.width%tileSize==0
					?tileSize:size.width%tileSize;
		}else{
			xdim=tileSize;
		}
		if(y==tiles[0].length-1){
			ydim = size.height%tileSize==0
					?tileSize:size.height%tileSize;
		}else{
			ydim=tileSize;
		}
		return new Dimension(xdim,ydim);
	}
	
	private BufferedImage createTileByPixel(int x, int y){
		return createTile(x/tileSize,y/tileSize);
	}
	
	public BufferedImage getTile(int x, int y){
		return tiles[x][y];
	}
	
	public Dimension getTileGridSize(){
		return new Dimension(tiles.length,tiles[0].length);
	}
	
	private BufferedImage getTileByPixel(int x, int y){
		return tiles[x/tileSize][y/tileSize];
	}
	
	private Point getTileLocationByPixel(int x, int y){
		return new Point(x/tileSize,y/tileSize);
	}
		
	public boolean isIncremential() {
		return incremential;
	}
	
	public void setIncremential(boolean incremential) {
		this.incremential = incremential;
	}
	
	public void setRGB(int x, int y, int rgb){
		BufferedImage tile = getTileByPixel(x,y);
		if(tile == null){
			tile = createTileByPixel(x,y);
		}
		tile.setRGB(x%tileSize,y%tileSize,rgb);
	}
	
	public void setColor(Color color){
		this.tileColor = color;
	}
	
	public void setComposite(Composite composite) {
        this.composite = composite;
    }
	
	public Composite getComposite() {
        return composite;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public Dimension getSize() {
        return size;
    }
	
}



package net.bioclipse.cdk.jchempaint.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Math.round;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.openscience.cdk.renderer.IRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingVisitor;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;


public class SWTRenderer implements IRenderingVisitor, IRenderer{

    GC gc;
    double scaleX = 1;
    double scaleY = 1;
    Renderer2DModel model;
    public AffineTransform transform;
    // scale a lite more and translate the differense to center it
    // dosen't handle zoom
    public SWTRenderer(GC graphics, Renderer2DModel model, double[] scale) {
        scaleX = Math.min( scale[0], scale[1] );
        scaleY = scaleX;
        this.model = model;
        gc = graphics;
        
    }
    
    private Map<java.awt.Color, Color> cleanUp;
    
    public Renderer2DModel getModel() {
        return model;
    }
    
    private int scaleX(double x) {
        double[] result = new double[2];
        transform.transform( new double[]{x,0}, 0, result, 0, 1 );
        return (int)result[0];
    }
    
    private int scaleY(double y) {
        double[] result = new double[2];
        transform.transform( new double[]{0,y}, 0, result, 0, 1 );
        return (int)result[1];
    }
    
    private int scale(double v,double f) {
        double[] result = new double[2];
        transform.transform( new double[]{v,v}, 0, result, 0, 1 );
        return (int)result[0];
        //return (int)(v*f+.5);
    }
    
    public void render(ElementGroup renderingModel) {
        for(IRenderingElement re:renderingModel ) {
           re.accept( this );
        }
    }
    

    public void visitOval( OvalElement element ) {
        Color colorOld = gc.getBackground();
//        int radius = (int) (scaleX(element.getRadius())+.5);
//        int radius_2 = (int) (scaleX(element.getRadius())/2.0+.5);
        int radius = (int)round( element.radius);
        int radius_2 = (int)round( element.radius/2d );
        if(element.fill) {
        gc.setBackground( toSWTColor( gc, element.color ) );
        
        gc.fillOval( scaleX(element.x)-radius_2, 
                     scaleY(element.y)-radius_2, 
                     radius,
                     radius );
        } else {
            gc.setForeground(  toSWTColor( gc, element.color ) );
            
            gc.drawOval( scaleX(element.x)-radius_2, 
                         scaleY(element.y)-radius_2, 
                         radius,
                         radius );
        }
        gc.setBackground( colorOld);
    }

    public void visitLine( LineElement element ) {
        Color colorOld = gc.getBackground();
        // init recursion with background to get the first draw with foreground
        gc.setForeground( getBackgroundColor() ); 
        drawLineX( element, element.type.count() );
            
        gc.setBackground( colorOld);
    }
    
    public void visitWedge( WedgeLineElement element) {
        Color colorOld = gc.getBackground();
        gc.setForeground( getForgroundColor() );
        gc.setBackground( getForgroundColor() );
        //drawWedge( element);
        drawWedge( element );
        gc.setBackground( colorOld );
    }
    
    private void drawWedge(WedgeLineElement element) {
        double width = element.width;
        Point2d p1 = new Point2d( scaleX(element.x1),
                                  scaleY(element.y2));
        Point2d p2 = new Point2d( scaleX(element.x2),
                                  scaleY(element.y2));
        Vector2d p12 = new Vector2d(p2);p12.sub( p1 );
        Vector2d v12n = new Vector2d(p12.y,-p12.x); // normal for p12
        v12n.normalize();
        //   wedge thickness is based on line width probably better to be based
        //  on text size
        double l = width*4/2; 
        Vector2d pa = new Vector2d(v12n);pa.scale( l );
        Vector2d pb = new Vector2d(v12n);pb.scale(-l);
        v12n.scale( l);
        
        Point2d p1a = new Point2d();p1a.add( p1, v12n );
        Point2d p1b = new Point2d();p1b.sub( p1, v12n );
        
        gc.setLineWidth( (int) width );
        if(element.isDashed)
            drawDashedWedge( p1a, p1b, p2, width );
        else
            drawFilledWedge( p1a, p1b, p2);
        
    }
    private void drawFilledWedge( Point2d p1a, Point2d p1b, 
                                  Point2d p2) {
        Path path = new Path(gc.getDevice());
        path.moveTo( (float)p2.x,(float) p2.y );
        path.lineTo( (float)(p1a.x), (float)(p1a.y) );
        path.lineTo( (float)(p1b.x), (float)(p1b.y) );
        path.close();        
        
        gc.fillPath( path );
        
        path.dispose();
    }
    private void drawDashedWedge( Point2d p1a, Point2d p1b, 
                                  Point2d p2, double d) {

        double s = (d*2);
        double t = s / p1a.distance( p2 );
        double t2 = p1a.distance( p2 )/ s;
        Path dashes = new Path(gc.getDevice());
        for(int i=0 ;i<t2;i++) {
            Point2d w = new Point2d();
            w.interpolate( p1a, p2, t*i);
            Point2d u = new Point2d();
            u.interpolate( p1b,p2, t*i );
            dashes.moveTo( (float) (w.x), (float) (w.y) );
            dashes.lineTo( (float) (u.x), (float) (u.y) );
        }

        gc.drawPath( dashes );
        dashes.dispose();
    }
    

    private Color getForgroundColor() {
        return toSWTColor( gc, getModel().getForeColor() );
    }
    
    private Color getBackgroundColor() {
        return toSWTColor( gc, getModel().getBackColor() );
    }

    private void drawLine(LineElement element) {
        gc.drawLine( scaleX(element.x1),
                     scaleY(element.y1),
                     scaleX(element.x2),
                     scaleY(element.y2));
    }
    
    private void drawLineX(LineElement element, int val) {
        if(val <= 0) return; // end recursion if less than 1
        int width = (int) (element.width*val+element.gap*(val-1)+.5);
        // switch foreground and background
        if(!gc.getForeground().equals( getBackgroundColor() ))
            gc.setForeground( getBackgroundColor() );
        else
            gc.setForeground( toSWTColor( gc, element.color ) );
        gc.setLineWidth( width );
        drawLine(element);
        
        drawLineX(element, val-1);
    }
    
    public void visitModel( ElementGroup model ) {

        for(IRenderingElement element:model) {
            element.accept( this );
        }
    }
    
    public void visitText( TextElement element ) {

        int x = scaleX(element.x);
        int y = scaleY(element.y);
        String text = element.text;
        int fontSize = (int) (scaleX*.4);
        fontSize = (fontSize<12?12:fontSize);
        fontSize = (fontSize>100?100:fontSize);
        Font font= new Font(gc.getDevice(),"Arial",fontSize,SWT.NORMAL);
        gc.setFont(font);
        Point textSize = gc.textExtent( text );
        x = x - textSize.x/2;
        y = y - textSize.y/2;
        gc.setForeground( toSWTColor( gc, element.color ) );
        gc.setBackground(  getBackgroundColor() );
        gc.setAdvanced( true );
        gc.drawText( text, x, y, true );
        //drawTextUpsideDown( gc, text, x, y, textSize.x, textSize.y );
    }

    private ImageData drawText(Device device, String text, Rectangle rect) {
    	    Image image = new Image(device,rect);
    	    
    	    GC imageGC = new GC(image);
    	        imageGC.setFont( gc.getFont() );
    	        imageGC.setForeground( device.getSystemColor( SWT.COLOR_WHITE ) );
    	        imageGC.setBackground( device.getSystemColor( SWT.COLOR_BLACK ) );
    	        imageGC.setAdvanced( true );
    	        imageGC.setInterpolation(SWT.HIGH);
    	        imageGC.setAntialias( SWT.OFF);
    	        
    	        imageGC.fillRectangle( rect);
//    	        layout.draw( imageGC, 0, 0 );
    	        imageGC.drawText( text, 0, 0 );
          imageGC.dispose();
    	    ImageData imageData = image.getImageData();
    	    image.dispose();
//    	    font.dispose();
    	    return imageData;
    	}

    private void drawTextUpsideDown( GC graphics, String text, 
                                     int x, int y,
                                     int width, int height){
    	    
    	    Rectangle bounds = new Rectangle(x,y,width,height);
    	    Rectangle doubleBounds = new Rectangle(
                                                   bounds.x,
                                                   bounds.y,
                                                   bounds.width,
                                                   bounds.height);
    	    if(false) {//rendererModel.getIsCompact()) {
    	        int size = Math.max( bounds.width, bounds.height );
    	        graphics.setBackground( graphics.getForeground() );
    	        graphics.fillOval( x, y, size,size);
    	    } else {
    	        ImageData alphaMask =
                        drawText( graphics.getDevice(), text, doubleBounds );
                alphaMask = superFlip( alphaMask, true, graphics.getForeground() );
                graphics.setInterpolation( SWT.HIGH );
                Image image = new Image( graphics.getDevice(), alphaMask );
                graphics.drawImage( image, 0, 0, doubleBounds.width,
                                    doubleBounds.height, x, y, bounds.width,
                                    bounds.height );
    
                image.dispose();
    	    }
    	}

    static ImageData superFlip(ImageData srcData, boolean vertical,Color color) {
    
      ImageData newImageData = (ImageData) srcData.clone();
    
      for (int srcY = 0; srcY < srcData.height; srcY++) {
        for (int srcX = 0; srcX < srcData.width; srcX++) {
          int destX = 0, destY = 0;
//          if (vertical) {
//            destX = srcX;
//            destY = srcData.height - srcY - 1;
//          } else {
//            destX = srcData.width - srcX - 1;
//            destY = srcY;
//          }       
          destX =srcX;
          destY = srcY;
          int red = srcData.palette.getRGB( srcData.getPixel( srcX, srcY)).red;
          newImageData.setAlpha( destX, destY, red );
          newImageData.setPixel( destX, destY, srcData.palette.getPixel(
                                                             color.getRGB() ) );          
        }
      }
    
      return newImageData;
    }
    
    public  Color toSWTColor(GC graphics,java.awt.Color color) {
        if(cleanUp == null) 
            cleanUp = new HashMap<java.awt.Color,Color>();
        if(color == null) return graphics.getDevice().getSystemColor( SWT.COLOR_DARK_MAGENTA );
        assert(color != null);
        Color otherColor=cleanUp.get(color);
        if(otherColor==null){
            otherColor = new Color(graphics.getDevice(),
                                   color.getRed(),
                                   color.getGreen(),
                                   color.getBlue());
            cleanUp.put(color,otherColor);
        }
        return otherColor;
    }
    
    public void dispose() {
        for(Color c:cleanUp.values())
            c.dispose();
          cleanUp.clear();
    }

    public void visitElementGroup( ElementGroup elementGroup ) {

        for(IRenderingElement element:elementGroup) {
            element.accept( this );
        }
        
    }
    public void setScale(double scale) {
        scaleX = scale;
        scaleY = scale;
    }
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    public void render() {

        // TODO Auto-generated method stub
        
    }
}

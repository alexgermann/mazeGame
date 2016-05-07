/**
 * @author Group L
 * @author Matt Grant, Adam Coggeshall, Jared Frank 
 * @author Alex Germann, Auston Larson
 * COSC 3011
 * VisualTileHolder.java
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;


/**
 * This is a generic representation of a front-end tile container.
 * It is responsible for drawing itself and the tiles it contains.
 * It should also translate mouse coordinates to slot IDs... -AC
 */

public class VisualTileHolder {
  
  // Screen position of the holder (Upper left corner!) -AC
  private int posX;
  private int posY;
  
  // Size of the container in tiles. -AC
  private int width;
  private int height;
  
  // Our sub-classes generally require a reference to a messenger. -AC
  private Messenger messenger;
  
  // We need to know what side we are so we can query the messenger for the
  // correct information. -AC
  private BoardSide side;
  
  protected VisualTileHolder(
      Messenger msgr,
      BoardSide side,
      int x, int y,
      int w, int h
  ) {
    messenger = msgr;
    posX = x;
    posY = y;
    width = w;
    height = h;
    this.side = side;
  }
  
  /**
   * Returns current messenger -MG
   * @return the current messenger
   * @see Messenger
   */
  protected Messenger getMessenger() {
    return messenger;
  }
  
  /**
   * Draws all tiles to screen. -MG
   * @param g Graphics2D object
   */
  public void draw(Graphics2D g) {
    
    int size = TileDrawer.TILE_SIZE;
    
    // Currently we draw the empty holder with
    // a grey checkerboard pattern. -AC
    for (int x=0; x<width; x++) {
      for (int y=0;y<height; y++) {
        Point loc = new Point(x, y);
        Image tileImg = getMessenger().getTile(side, loc.x, loc.y);
        int tileRot = getMessenger().getTileRotation(side, loc.x, loc.y);
        
        // We only draw the tile if it exists and is not being dragged. -AC
        if (tileImg != null && messenger.getDraggedTileImage() != tileImg)
          TileDrawer.drawTile(g, posX+x*size, posY+y*size, tileImg, tileRot);
        else {
          if ((x+y)%2==0)
            g.setColor(new Color(100,100,100));
          else
            g.setColor(new Color(200,200,200));
          g.fillRect(posX+x*size, posY+y*size, size, size);
        }
      }
    }
  }
  
  /**
   * Get the slot ID from a mouse click. We need the slot ID to perform
   * swaps between slots when drags are successful. -AC
   */
  public int getSlotFromClick(MouseEvent e) {
    Point position = getLocationFromClick(e);
    if (position != null) {
      switch (side) {
      case CENTER:
        return 16+position.x+position.y*4;
      case LEFT:
        return position.y;
      case RIGHT:
        return position.y+8;
      }
    }
    return -1;
  }
  
  /**
   * Returns the image from tile at location specified by MouseEvent -MG
   * @param e MouseEvent
   * @return Image of tile at location in e or null if no tile present
   */
  public Image getTileImageFromClick(MouseEvent e) {
    Point position = getLocationFromClick(e);
    if (position != null)
      return getMessenger().getTile(side, position.x, position.y);
    return null;
  }
  
  /**
   * Returns the rotation of tile at location specified by MouseEvent -MG
   * @param e MouseEvent
   * @return Rotation of tile represented as number of 90 degree clockwise rotations or -1 if no tile present
   */
  public int getRotationFromClick(MouseEvent e) {
    Point position = getLocationFromClick(e);
    if (position != null) {
      return getMessenger().getTileRotation(side, position.x, position.y);
    }
    return -1;
  }
  
  /**
   * Rotates tile 90 degrees clockwise -MG
   * @param e MouseEvent
   */
  public void rotateTileFromClick(MouseEvent e) {
    Point position = getLocationFromClick(e);
    if (position != null)
      getMessenger().doRotate(side, position.x, position.y);
  }
  
  /**
   * Gets the x/y location of a slot in the container from a mouse click. -MG
   * @return Point object containing location of tile or null if no tile present
   */
  private Point getLocationFromClick(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    
    if (x < posX || 
        y < posY ||
        x>posX+TileDrawer.TILE_SIZE*width ||
        y>posY+TileDrawer.TILE_SIZE*height ) {
      // We can bail out if the coordinates are outside our bounding box -AC
      return null;
    }
    
    int tileX = (x-posX)/TileDrawer.TILE_SIZE;
    int tileY = (y-posY)/TileDrawer.TILE_SIZE;
    
    return new Point(tileX,tileY);
  }
}
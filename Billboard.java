import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
public class Billboard {
  private int[] img = new int[0]; //The actual pixel data
  private int width = 0; //The width of the original image
  private int height = 0; //The height of the original image
  private float[] pos = {0, 0, 0}; //The position of the billboard
  private float[] scale = {1, 1}; //The scale of the billboard
  private byte tint = -1; //The tint applied to the billboard
  private short brightness = 0;
  private byte flags = 8; //bit 0 = attached to camera, bit 1 = draw in front, bit 2 = outline, bit 3 = inside pixels, bit 4 = removal enable
  private int removalColour = 0xFF00FF; //Pixels of this colour will be skipped
  private int strokeColour = 0xFF000000; //The colour of the outline of the image
  public Billboard(){
    img = new int[0];
    width = 0;
    height = 0;
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    tint = -1;
    brightness = 0;
    flags = 8;
    removalColour = 0xFF00FF;
    strokeColour = 0xFF000000;
  }

  public Billboard(String imagePath){
    File file = new File(imagePath);
    try{
      BufferedImage sprite = ImageIO.read(file);
      width = sprite.getWidth();
      height = sprite.getHeight();
      img = new int[width*height];
      for(int i = 0; i < width; i++)
        for(int j = 0; j < height; j++)
          img[j*width+i] = sprite.getRGB(i, j);
    }
    catch(Exception e){
      System.out.println("ERROR: FILE "+imagePath+" NOT FOUND OR IS INVALID");
      System.exit(1);
    }
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    tint = -1;
    brightness = 0;
    flags = 8;
    removalColour = 0xFF00FF;
    strokeColour = 0xFF000000;

  }
  public Billboard(String imagePath, byte newTint){
    tint = newTint;
    File file = new File(imagePath);
    try{
      BufferedImage sprite = ImageIO.read(file);
      width = sprite.getWidth();
      height = sprite.getHeight();
      img = new int[width*height];
      for(int i = 0; i < width; i++)
        for(int j = 0; j < height; j++)
          img[j*width+i] = sprite.getRGB(i, j);
    }
    catch(Exception e){
      System.out.println("ERROR: FILE "+imagePath+" NOT FOUND OR IS INVALID");
      System.exit(1);
    }
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    brightness = 0;
    flags = 8;
    removalColour = 0xFF00FF;
    strokeColour = 0xFF000000;
  }
  public Billboard(int[] newImage, int newWidth, int newHeight){
    if(newWidth*newHeight != newImage.length){
        System.out.println("ERROR: DIMENSIONS DO NOT MATCH");
        System.exit(1);
    }
    img = new int[newImage.length];
    width = newWidth;
    height = newHeight;
    for(int i = 0; i < newImage.length; i++)
      img[i] = newImage[i];
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    tint = -1;
    brightness = 0;
    flags = 8;
    removalColour = 0xFF00FF;
    strokeColour = 0xFF000000;
  }
  public Billboard(int[] newImage, int newWidth, int newHeight, byte newTint){
    tint = newTint;
    if(newWidth*newHeight != newImage.length){
        System.out.println("ERROR: DIMENSIONS DO NOT MATCH");
        System.exit(1);
    }
    img = new int[newImage.length];
    width = newWidth;
    height = newHeight;
    for(int i = 0; i < newImage.length; i++)
      img[i] = newImage[i];
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    brightness = 0;
    flags = 8;
    removalColour = 0xFF00FF;
    strokeColour = 0xFF000000;
  }
  public void setImage(String imagePath){
    File file = new File(imagePath);
    try{
      BufferedImage sprite = ImageIO.read(file);
      width = sprite.getWidth();
      height = sprite.getHeight();
      img = new int[width*height];
      for(int i = 0; i < width; i++)
        for(int j = 0; j < height; j++)
          img[j*width+i] = sprite.getRGB(i, j);
    }
    catch(Exception e){
      System.out.println("ERROR: FILE "+imagePath+" NOT FOUND OR IS INVALID");
      System.exit(1);
    }
  }
  public void setImage(int[] newImage, int newWidth, int newHeight){
    if(newWidth*newHeight != newImage.length){
        System.out.println("ERROR: DIMENSIONS DO NOT MATCH");
        System.exit(1);
    }
    img = new int[newImage.length];
    width = newWidth;
    height = newHeight;
    for(int i = 0; i < newImage.length; i++)
      img[i] = newImage[i];
  }
  public void setTint(byte newTint){
    tint = newTint;
  }
  public void setBrightness(short newBrightness){
    brightness = (short)(newBrightness&0x00FF);
    if(newBrightness < 0)
      brightness|=0xFF00;
  }
  public void setDrawDisable(boolean drawDisable){
    if(drawDisable)
      flags|=2;
    else
      flags&=-3;
  }
  public void setAttachedToCamera(boolean isAttached){
    if(isAttached)
      flags|=1;
    else
      flags&=-2;
  }
  public void setInvisColour(int invisColour){
    invisColour&=0xFFFFFF;
    if(invisColour > 0xFF)
      removalColour = invisColour;
    else
      removalColour = (invisColour << 16) | (invisColour << 8) | invisColour;
  }
  public void setInvisColour(byte r, byte g, byte b){
    removalColour = ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
  }
  public void setPosition(float x, float y, float z){
    pos[0] = x;
    pos[1] = y;
    pos[2] = z;
  }
  public void setPosition(float[] newPos){
    pos[0] = newPos[0];
    pos[1] = newPos[1];
    pos[2] = newPos[2];
  }
  public void setScale(float xScale, float yScale){
    scale[0] = xScale;
    scale[1] = yScale;
  }
  public void setScale(float newScale){
    scale[0] = newScale;
    scale[1] = newScale;
  }
  public void setScale(float[] newScale){
    scale[0] = newScale[0];
    scale[1] = newScale[1];
  }
  
  public void stroke(int stroke){
    int alpha = stroke >>> 24;
    if((stroke & 0xFFFFFF) <= 0xFF)
      stroke = ((stroke & 0xFF) << 16)|((stroke & 0xFF) << 8)|(stroke & 0xFF);
      
    if(alpha > 0)
      stroke = (alpha << 24)|(stroke & 0xFFFFFF);
    else
      stroke = 0xFF000000|stroke;
    strokeColour = stroke;
    
  }
  public void stroke(int stroke, int a){
    if((stroke & 0xFFFFFF) <= 0xFF)
      stroke = ((stroke & 0xFF) << 16)|((stroke & 0xFF) << 8)|(stroke & 0xFF);
    a = (a & 0xFF) << 24;
    strokeColour = a|stroke;
  }
  public void stroke(int r, int b, int g){
    r = (r & 0xFF) << 16;
    g = (g & 0xFF) << 8;
    b&=0xFF;
    strokeColour = 0xFF000000|r|g|b;
  }
  
  public void stroke(int r, int b, int g, int a){
    a = (a & 0xFF) << 24;
    r = (r & 0xFF) << 16;
    g = (g & 0xFF) << 8;
    b&=0xFF;
    strokeColour = a|r|g|b;
  }
  
  
  public void setOutline(boolean outlineEnable){
    if(outlineEnable)
      flags|=4;
    else
      flags&=-5;
  }
  public void setInside(boolean insideEnable){
    if(insideEnable)
      flags|=8;
    else
      flags&=-9;
  }
  
  public void setRemoval(boolean removalEnable){
    if(removalEnable)
      flags|=16;
    else
      flags&=-17;
  }
  
  public int returnWidth(){
    return width;
  }
  public int returnHeight(){
    return height;
  }
  public float[] returnPosition(){
    return pos;
  }
  public float[] returnScale(){
    return scale;
  }
  public boolean noDraw(){
    return (flags & 2) == 2;
  }
  public boolean isAttachedToCamera(){
    return (flags & 1) == 1;
  }
  public int[] returnPixels(){
    return img;
  }
  public byte returnTint(){
    return tint;
  }
  public int returnInvisColour(){
    return removalColour;
  }
  public int returnStroke(){
    return strokeColour;
  }
  public short returnBrightness(){
    return brightness;
  }
  public boolean hasOutline(){
    return (flags & 4) == 4;
  }
  public boolean hasImage(){
    return (flags & 8) == 8;
  }
  public boolean hasRemoval(){
    return (flags & 16) == 16;
  }
  public float[][] boundingBox(){
    float[][] boundingBox = new float[8][3];
    boundingBox[0][0] = -(width >>> 1);
    boundingBox[0][1] = -(height >>> 1);
    boundingBox[0][2] = 0;
    boundingBox[1][0] = (width >>> 1);
    boundingBox[1][1] = -(height >>> 1);
    boundingBox[1][2] = 0;
    boundingBox[2][0] = (width >>> 1);
    boundingBox[2][1] = (height >>> 1);
    boundingBox[2][2] = 0;
    boundingBox[3][0] = -(width >>> 1);
    boundingBox[3][1] = (height >>> 1);
    boundingBox[3][2] = 0;
    boundingBox[4][0] = boundingBox[0][0];
    boundingBox[4][1] = boundingBox[0][1];
    boundingBox[4][2] = boundingBox[0][2];
    boundingBox[5][0] = boundingBox[1][0];
    boundingBox[5][1] = boundingBox[1][1];
    boundingBox[5][2] = boundingBox[1][2];
    boundingBox[6][0] = boundingBox[6][0];
    boundingBox[6][1] = boundingBox[6][1];
    boundingBox[6][2] = boundingBox[6][2];
    boundingBox[7][0] = boundingBox[7][0];
    boundingBox[7][1] = boundingBox[7][1];
    boundingBox[7][2] = boundingBox[7][2];
    return boundingBox;
  }
  public void copy(Billboard b){
    scale[0] = b.scale[0];
    scale[1] = b.scale[1];
    width = b.width;
    height = b.height;
    pos[0] = b.pos[0];
    pos[1] = b.pos[1];
    pos[2] = b.pos[2];
    tint = b.tint;
    brightness = b.brightness;
    removalColour = b.removalColour;
    strokeColour = b.strokeColour;
    flags = b.flags;
    img = new int[b.img.length];
    for(int i = 0; i < img.length; i++)
      img[i] = b.img[i];
  }
}

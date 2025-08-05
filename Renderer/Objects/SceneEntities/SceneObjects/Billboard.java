package Renderer.Objects.SceneEntities.SceneObjects;
import Renderer.ModelDataHandler.Graphic;
import Renderer.Objects.Parents.*;
//Class for abstracting away billboarded sprite object data
public class Billboard extends ShadedObject{
  private Graphic image;
  private int fill = 0xFFFFFFFF;
  private int stroke = 0xFF000000;
  //Flag bits:
  //bit 6 = hasRemoved colour
  //bit 7 = has image

  public Billboard(){
    super((byte)-120);
    image = new Graphic();
    fill = 0xFFFFFFFF;
    stroke = 0xFF000000;
  }

  public Billboard(String imagePath){
    super((byte)-120);
    image = new Graphic(imagePath);
    stroke = 0xFF000000;
    fill = 0xFFFFFFFF;
  }
  public Billboard(String imagePath, int newFill){
    super((byte)-120);
    image = new Graphic(imagePath);
    fill(newFill);
    stroke = 0xFF000000;
  }
  public Billboard(int[] newImage, int newWidth, int newHeight){
    super((byte)-120);
    image = new Graphic(newImage, newWidth, newHeight);
    fill = 0xFFFFFFFF;
    stroke = 0xFF000000;

  }
  public Billboard(int[] newImage, int newWidth, int newHeight, byte newTint){
    super((byte)-120);
    image = new Graphic(newImage, newWidth, newHeight);
    fill = 0xFFFFFFFF;
    stroke = 0xFF000000;
  }

  public Billboard(Graphic img){
    super((byte)-120);
    image = img;
    fill = 0xFFFFFFFF;
    stroke = 0xFF000000;
  }
  public Billboard(Graphic img, int newFill){
    super((byte)-120);
    image = img;
    fill(newFill); 
    stroke = 0xFF000000;

  }

  public void setScale(float xScale, float yScale){
    scale[0] = xScale;
    scale[1] = yScale;
    scale[2] = 0;
  }

  
  public int returnWidth(){
    return image.returnWidth();
  }
  public int returnHeight(){
    return image.returnHeight();
  }

  public void setImage(String imagePath){
    image.setImage(imagePath);
  }
  public void setImage(int[] newImage, int newWidth, int newHeight){
    image.setImage(newImage, newWidth, newHeight);
  }

  //Setting the fill colour of the triangles
  public void fill(short r, short g, short b){
    r&=0xFF;
    g&=0xFF;
    b&=0xFF;
    fill = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public void fill(short r, short g, short b, short a){
    r&=0xFF;
    g&=0xFF;
    b&=0xFF;
    a&=0xFF;
    fill = (a << 24)|(r << 16)|(g << 8)|b;
  }
  public void fill(int colour){
    if((colour >>> 24) == 0){
      if(colour <= 0xFF)
          colour = 0xFF000000 | (colour << 16) | (colour << 8) | colour;
      else if(colour <= 0xFFFF)
          colour = ((colour & 0xFF00) << 16) | ((colour & 0xFF) << 16) | ((colour & 0xFF) << 8) | (colour & 0xFF);
      else
          colour = 0xFF000000 | colour;
    }
    fill = colour;
  }
  public void fill(int colour, short alpha){
    alpha&=0xFF;
    colour = (colour & 0xFFFFFF);
    if(colour > 0xFF)
      fill = (alpha << 24)|colour;
    else
      fill = (alpha << 24)|(colour << 16)|(colour << 8)|colour;
  }
  public void stroke(int colour){
    if((colour >>> 24) == 0){
      if(colour <= 0xFF)
        colour = 0xFF000000 | (colour << 16) | (colour << 8) | colour;
      else if(colour <= 0xFFFF)
        colour = ((colour & 0xFF00) << 16) | ((colour & 0xFF) << 16) | ((colour & 0xFF) << 8) | (colour & 0xFF);
      else
        colour = 0xFF000000 | colour;
    }
    stroke = colour;
  }
  public void stroke(int colour, int a){
    if((colour & 0xFFFFFF) <= 0xFF)
      colour = ((colour & 0xFF) << 16)|((colour & 0xFF) << 8)|(colour & 0xFF);
    a = (a & 0xFF) << 24;
    stroke = a|(colour & 0xFFFFFF);
  }
  public void stroke(int r, int b, int g){
    r = (r & 0xFF) << 16;
    g = (g & 0xFF) << 8;
    b&=0xFF;
    stroke = 0xFF000000|r|g|b;
  }
  
  public void stroke(int r, int b, int g, int a){
    a = (a & 0xFF) << 24;
    r = (r & 0xFF) << 16;
    g = (g & 0xFF) << 8;
    b&=0xFF;
    stroke = a|r|g|b;
  }

  
  public void setRemoval(boolean removalEnable){
    if(removalEnable)
      flags|=64;
    else
      flags&=-65;
  }

  public void setHasImage(boolean hasImage){
    if(hasImage)
      flags|=-128;
    else
      flags&=127;
  }
  

  public int[] returnPixels(){
    return image.returnPixels();
  }

  public int returnInvisColour(){
    return image.returnInvisColour((byte)0);
  }
  public boolean shouldDrawPixel(int pixelIndex){
    return image.shouldDrawPixel(pixelIndex);
  }
  public int returnStroke(){
    return stroke;
  }
  public int returnFill(){
    return fill;
  }

  public boolean hasImage(){
    return (flags & -120) == -120 && image != null && image.returnWidth() != 0 && image.returnHeight() != 0;
  }
  public boolean hasRemoval(){
    return (flags & 64) == 64;
  }

  public void copy(Object o){
    if(o instanceof Billboard){
      Billboard b = (Billboard)o;
      super.copy(b);
      image = b.image;
      stroke = b.stroke;
      fill = b.fill;

    }
  }
  public void copy(Billboard b){
    super.copy(b);
    image = b.image;
    stroke = b.stroke;
    fill = b.fill;
  }

  public boolean equals(Object o){
    if(o instanceof Billboard){
      Billboard b = (Billboard)o;
      boolean isEqual = super.equals(b);
      isEqual&=image.equals(b.image);
      isEqual&=(stroke == b.stroke);
      isEqual&=(fill == b.fill);
      return isEqual;
    }
    else
      return false;
  }
  public boolean equals(Billboard b){
    boolean isEqual = super.equals(b);
    isEqual&=image.equals(b.image);
    isEqual&=(stroke == b.stroke);
    isEqual&=(fill == b.fill);
    return isEqual;
  }
}
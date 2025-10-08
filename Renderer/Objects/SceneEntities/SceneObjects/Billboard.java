package Renderer.Objects.SceneEntities.SceneObjects;
import Renderer.ModelDataHandler.Graphic;
import Renderer.Objects.Parents.*;
//Class for abstracting away billboarded sprite object data
public class Billboard extends ShadedObject{
  private static final float INV_255 = 0.003921569f;
  private Graphic sprite;
  private int stroke = 0xFF000000;
  private float[] fill = {1, 1, 1, 1};
  //Flag bits:
  //bit 6 = hasRemoved colour
  //bit 7 = has image

  public Billboard(){
    super((byte)-120);
    sprite = new Graphic();
    fill[0] = 1;
    fill[1] = 1;
    fill[2] = 1;
    fill[3] = 1;
    stroke = 0xFF000000;
  }

  public Billboard(String imagePath){
    super((byte)-120);
    sprite = new Graphic(imagePath);
    fill[0] = 1;
    fill[1] = 1;
    fill[2] = 1;
    fill[3] = 1;
    stroke = 0xFF000000;
  }
  public Billboard(String imagePath, int newFill){
    super((byte)-120);
    sprite = new Graphic(imagePath);
    fill(newFill);
    stroke = 0xFF000000;
  }
  public Billboard(int[] newImage, int newWidth, int newHeight){
    super((byte)-120);
    sprite = new Graphic(newImage, newWidth, newHeight);
    fill[0] = 1;
    fill[1] = 1;
    fill[2] = 1;
    fill[3] = 1;
    stroke = 0xFF000000;
  }
  
  public Billboard(Graphic img){
    super((byte)-120);
    sprite = img;
    fill[0] = 1;
    fill[1] = 1;
    fill[2] = 1;
    fill[3] = 1;
    stroke = 0xFF000000;
  }
  public Billboard(Graphic img, int newFill){
    super((byte)-120);
    sprite = img;
    fill(newFill);
    stroke = 0xFF000000;

  }

  public void setScale(float xScale, float yScale){
    scale[0] = xScale;
    scale[1] = yScale;
    scale[2] = 0;
  }

  
  public int returnWidth(){
    return sprite.returnWidth();
  }
  public int returnHeight(){
    return sprite.returnHeight();
  }

  public void setImage(String imagePath){
    sprite.setImage(imagePath);
  }
  public void setImage(int[] newImage, int newWidth, int newHeight){
    sprite.setImage(newImage, newWidth, newHeight);
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
  

  public int[] returnPixel(int x, int y){
    return sprite.returnPixel(x, y);
  }

  public int returnInvisColour(){
    return sprite.returnInvisColour((byte)0);
  }
  public boolean shouldDrawPixel(int x, int y){
    return sprite.shouldDrawPixel(x, y) || (flags & 64) == 64;
  }
    //Setting the fill colour of the sprite
    public void fill(short r, short g, short b){
        fill[0] = 1;
        fill[1] = (r & 0xFF)*INV_255;
        fill[2] = (g & 0xFF)*INV_255;
        fill[3] = (b & 0xFF)*INV_255;
    }
    public void fill(short r, short g, short b, short a){
        fill[0] = (a & 0xFF)*INV_255;
        fill[1] = (r & 0xFF)*INV_255;
        fill[2] = (g & 0xFF)*INV_255;
        fill[3] = (b & 0xFF)*INV_255;
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
        fill[0] = (colour >>> 24)*INV_255;
        fill[1] = ((colour >>> 16) & 0xFF)*INV_255;
        fill[2] = ((colour >>> 8) & 0xFF)*INV_255;
        fill[3] = (colour & 0xFF)*INV_255;
    }
    public void fill(int colour, short alpha){
        colour = (colour & 0xFFFFFF);
        fill[0] = (alpha & 0xFF)*INV_255;
        if(colour > 0xFF){
            fill[1] = ((colour >>> 16) & 0xFF)*INV_255;
            fill[2] = ((colour >>> 8) & 0xFF)*INV_255;
            fill[3] = (colour & 0xFF)*INV_255;
        }
        else{
            fill[1] = colour*INV_255;
            fill[2] = colour*INV_255;
            fill[3] = colour*INV_255;
        }
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

    public int[] fill(){
        int[] out = {Math.round(fill[0]*255), Math.round(fill[1]*255), Math.round(fill[2]*255), Math.round(fill[3]*255)};
        return out;
    }
    public int fillAsInt(){
        return (Math.round(fill[0]*255) << 24)|(Math.round(fill[1]*255) << 16)|(Math.round(fill[2]*255) << 8)|Math.round(fill[3]*255);
    }

    public int[] fillWithTexelChannel(int x, int y){
        int[] texel = sprite.returnPixel(x, y);
        int[] out = {Math.round(fill[0]*255), 
                    Math.round(Math.min(texel[0]*fill[1], 255)), 
                    Math.round(Math.min(texel[1]*fill[2], 255)),
                    Math.round(Math.min(texel[2]*fill[3], 255))};
        return out;
    }
    public int fillWithTexelSingleColour(int x, int y){
        int[] texel = sprite.returnPixel(x, y);
        int[] out = {Math.round(fill[0]*255), 
                    Math.round(Math.min(texel[0]*fill[1], 255)), 
                    Math.round(Math.min(texel[1]*fill[2], 255)),
                    Math.round(Math.min(texel[2]*fill[3], 255))};
        return (out[0] << 24)|(out[1] << 16)|(out[2] << 8)|out[3];
    }

    public int stroke(){
        return stroke;
    }

  public boolean hasImage(){
    return (flags & -120) == -120 && sprite != null && sprite.returnWidth() != 0 && sprite.returnHeight() != 0;
  }
  public boolean hasRemoval(){
    return (flags & 64) == 64;
  }

  public void copy(Object o){
    if(o instanceof Billboard){
      Billboard b = (Billboard)o;
      super.copy(b);
      sprite = b.sprite;
      fill[0] = b.fill[0];
      fill[1] = b.fill[1];
      fill[2] = b.fill[2];
      fill[3] = b.fill[3];
      stroke = b.stroke;
    }
  }
  public void copy(Billboard b){
    super.copy(b);
    sprite = b.sprite;
    fill[0] = b.fill[0];
    fill[1] = b.fill[1];
    fill[2] = b.fill[2];
    fill[3] = b.fill[3];
    stroke = b.stroke;
  }

  public boolean equals(Object o){
    if(o instanceof Billboard){
      Billboard b = (Billboard)o;
      boolean isEquals = super.equals(b);
      isEquals&=(sprite == b.sprite);
      isEquals&=(fill[0] == b.fill[0]);
      isEquals&=(fill[1] == b.fill[1]);
      isEquals&=(fill[2] == b.fill[2]);
      isEquals&=(fill[3] == b.fill[3]);
      isEquals&=(stroke == b.stroke);
      return isEquals;
    }
    else
      return false;
  }
  public boolean equals(Billboard b){
    boolean isEquals = super.equals(b);
    isEquals&=(sprite == b.sprite);
    isEquals&=(fill[0] == b.fill[0]);
    isEquals&=(fill[1] == b.fill[1]);
    isEquals&=(fill[2] == b.fill[2]);
    isEquals&=(fill[3] == b.fill[3]);
    isEquals&=(stroke == b.stroke);
    return isEquals;
  }
}
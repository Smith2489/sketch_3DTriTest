package Renderer.Objects.SceneEntities.SceneObjects;
import Wrapper.*;
import Actions.ObjectActions.*;
import Renderer.Objects.Parents.*;
//Class for abstracting away camera object data
public class Camera extends ScalableEntity{
  private IntWrapper colour = new IntWrapper();
  private float[] invColour = {1, 1, 1}; 
  private BooleanWrapper alwaysMultiply = new BooleanWrapper();
  private FloatWrapper drawDistance = new FloatWrapper();
  public Camera(){
    super();
    colour.val = 0xFFFFFFFF;
    invColour[0] = 1;
    invColour[1] = 1;
    invColour[2] = 1;
    alwaysMultiply.val = false;
    drawDistance.val = 2000;
  }
  public Camera(float[] position, float[] rotation, float[] scl, float[][] shr){
    super(position, rotation, scl, shr);
    colour.val = 0xFFFFFFFF;
    invColour[0] = 1;
    invColour[1] = 1;
    invColour[2] = 1;
    alwaysMultiply.val = false;
    drawDistance.val = 2000;
  }
  public Camera(float posX, float posY, float posZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ, float[] shearX, float[] shearY, float[] shearZ){
    super(posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, shearX, shearY, shearZ);
    colour.val = 0xFFFFFFFF;
    invColour[0] = 1;
    invColour[1] = 1;
    invColour[2] = 1;
    alwaysMultiply.val = false;
    drawDistance.val = 2000;
  }
  public void addAction(CameraAction newAction){
    if(newAction != null){
      newAction.setColour(colour, invColour);
      newAction.setDrawDistance(drawDistance);
      newAction.setAlwaysMultiply(alwaysMultiply);
      super.appendAction(newAction);
      actionList.add(newAction);
    }
    else
      System.out.println(NULL_ACTION);
  }

  //A pair of functions which set the camera colour
  public void colour(int rgb){
    rgb&=0xFFFFFF;
    if(rgb <= 0xFF){
      if(rgb > 0){
        invColour[0] = 255f/rgb;
        invColour[1] = 255f/rgb;
        invColour[2] = 255f/rgb;
      }
      else{
        invColour[0] = 0;
        invColour[1] = 0;
        invColour[2] = 0;
      }
      colour.val = 0xFF000000 | (rgb << 16) | (rgb << 8) | rgb;
    }
    else{
      if(((rgb >>> 16) & 0xFF) > 0)
        invColour[0] = 255f/((rgb >>> 16) & 0xFF);
      else  
        invColour[0] = 0;
      if(((rgb >>> 8) & 0xFF) > 0)
        invColour[1] = 255f/((rgb >>> 8) & 0xFF);
      else  
        invColour[1] = 0;
      if((rgb & 0xFF) > 0)
        invColour[2] = 255f/(rgb & 0xFF);
      else  
        invColour[2] = 0;
      colour.val = 0xFF000000 | rgb;
    }
  }

  public void colour(int red, int green, int blue){
    red = (red & 0xFF);
    green = (green & 0xFF);
    blue = blue & 0xFF;
    if(red > 0)
      invColour[0] = 255f/red;
    else
      invColour[0] = 0;
    if(green > 0)
      invColour[1] = 255f/green;
    else
      invColour[1] = 0;
    if(blue > 0)
      invColour[2] = 255f/blue;
    else
      invColour[2] = 0;
    colour.val = 0xFF000000 | (red << 16) | (green << 8) | blue;
  }

  public float[] returnInvColour(int otherColour){
    float[] outputColour = {1, 1, 1};
    if(colour.val == otherColour && !alwaysMultiply.val){
      outputColour[0] = invColour[0];
      outputColour[1] = invColour[1];
      outputColour[2] = invColour[2];
    }
    return outputColour;
  }

  public void alwaysMultiply(){
    alwaysMultiply.val = true;
  }
  public void sometimesMultiply(){
    alwaysMultiply.val = false;
  }

  public void setDrawDistance(float newDistance){
    if(Float.isInfinite(newDistance) || Float.isNaN(newDistance))
      drawDistance.val = 2000;
    else
      drawDistance.val = Math.abs(newDistance);
  }

  public float getDrawDistance(){
    return drawDistance.val;
  }
  
  public int returnColour(){
    return colour.val;
  }
  
  public void copy(Object o){
    if(o instanceof Camera){
      Camera c = (Camera)o;
      super.copy(c);
      colour.val = c.colour.val;
      drawDistance.val = c.drawDistance.val;
      alwaysMultiply.val = c.alwaysMultiply.val;
      invColour[0] = c.invColour[0];
      invColour[1] = c.invColour[1];
      invColour[2] = c.invColour[2];
    }
   }
  public void copy(Camera c){
    super.copy(c);
    colour.val = c.colour.val;
    drawDistance.val = c.drawDistance.val;
    alwaysMultiply.val = c.alwaysMultiply.val;
    invColour[0] = c.invColour[0];
    invColour[1] = c.invColour[1];
    invColour[2] = c.invColour[2];
  }
  public boolean equals(Object o){
    if(o instanceof Camera){
      Camera c = (Camera)o;
      boolean isEqual = super.equals(c);
      isEqual&=(colour.val == c.colour.val);
      isEqual&=(Math.abs(drawDistance.val - c.drawDistance.val) <= EPSILON);
      isEqual&=(alwaysMultiply.val == c.alwaysMultiply.val);
      for(byte i = 0; i < 3; i++)
        isEqual&=(Math.abs(invColour[i] - c.invColour[i]) <= EPSILON);
      return isEqual;
    }
    return false;
  }
  public boolean equals(Camera c){
    boolean isEqual = super.equals(c);
    isEqual&=(colour.val == c.colour.val);
    isEqual&=(Math.abs(drawDistance.val - c.drawDistance.val) <= EPSILON);
    isEqual&=(alwaysMultiply.val == c.alwaysMultiply.val);
    for(byte i = 0; i < 3; i++)
      isEqual&=(Math.abs(invColour[i] - c.invColour[i]) <= EPSILON);
    return isEqual;
  }
  
  public String toString(){
    String out = "POSITION: ("+pos[0]+", "+pos[1]+", "+pos[2]+")";
    out+="\nROTATION: ("+rot[0]+", "+rot[1]+", "+rot[2]+")";
    out+="\nSCALE: ("+scale[0]+", "+scale[1]+", "+scale[2]+")";
    out+="\nSHEAR: (("+shear[0][0]+", "+shear[0][1]+"), ("+shear[1][0]+", "+shear[1][1]+"), ("+shear[2][0]+", "+shear[2][1]+"))";
    return out;
  }
}
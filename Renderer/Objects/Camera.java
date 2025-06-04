package Renderer.Objects;
import java.util.*;
import Wrapper.*;
import Actions.*;
import Renderer.ScreenDraw.MVP;
import Renderer.Objects.Physics.*;
import Maths.LinearAlgebra.*;
public class Camera{
  private float[] pos = {0, 0, 0}; //Holds the camera's position in 3D space
  private float[] rot = {0, 0, 0}; //Holds the camera's rotation in 3D space
  private float[] scale = {1, 1, 1}; //Holds the scale of the camera
  private float[][] shear = {{0, 0}, {0, 0}, {0, 0}}; //Holds how the camera is sheared
  private Matrix camAsMod = new Matrix();
  private LinkedList<Action> actionList = new LinkedList<Action>();
  private Action tempAction;
  private boolean alwaysPerform = true;
  private IntWrapper colour = new IntWrapper();
  private float[] invColour = {1, 1, 1}; 
  private BooleanWrapper alwaysMultiply = new BooleanWrapper();
  private FloatWrapper drawDistance = new FloatWrapper();
  private Physics physics = new Physics(pos, rot);
  public Camera(){
    alwaysPerform = true;
    camAsMod = new Matrix();
    actionList = new LinkedList<Action>();
    for(byte i = 0; i < 3; i++){
      pos[i] = 0;
      rot[i] = 0;
      scale[i] = 1;
      shear[i][0] = 0;
      shear[i][1] = 0;
    }
    physics = new Physics(pos, rot);
    colour.val = 0xFFFFFFFF;
    invColour[0] = 1;
    invColour[1] = 1;
    invColour[2] = 1;
    alwaysMultiply.val = false;
    drawDistance.val = 2000;
  }
  public Camera(float[] position, float[] rotation, float[] scl, float[][] shr){
    alwaysPerform = true;
    camAsMod = new Matrix();
    actionList = new LinkedList<Action>();
    for(byte i = 0; i < 3; i++){
      pos[i] = position[i];
      rot[i] = rotation[i];
      scale[i] = scl[i];
      shear[i][0] = shr[i][0];
      shear[i][1] = shr[i][1];
    }
    physics = new Physics(pos, rot);
    colour.val = 0xFFFFFFFF;
    invColour[0] = 1;
    invColour[1] = 1;
    invColour[2] = 1;
    alwaysMultiply.val = false;
    drawDistance.val = 2000;
  }
  public Camera(float posX, float posY, float posZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ, float[] shearX, float[] shearY, float[] shearZ){
    alwaysPerform = true;
    camAsMod = new Matrix();
    pos[0] = posX;
    pos[1] = posY;
    pos[2] = posZ;
    rot[0] = rotX;
    rot[1] = rotY;
    rot[2] = rotZ;
    scale[0] = scaleX;
    scale[1] = scaleY;
    scale[2] = scaleZ;
    shear[0][0] = shearX[0];
    shear[0][1] = shearX[1];
    shear[1][0] = shearY[0];
    shear[1][1] = shearY[1];
    shear[2][0] = shearZ[0];
    shear[2][1] = shearZ[1];
    physics = new Physics(pos, rot);
    actionList = new LinkedList<Action>();
    colour.val = 0xFFFFFFFF;
    invColour[0] = 1;
    invColour[1] = 1;
    invColour[2] = 1;
    alwaysMultiply.val = false;
    drawDistance.val = 2000;
  }
  public void addAction(CameraAction newAction){
    if(newAction != null){
      newAction.setPos(pos);
      newAction.setRot(rot);
      newAction.setScale(scale);
      newAction.setShear(shear);
      newAction.setMatrix(camAsMod);
      newAction.setColour(colour, invColour);
      newAction.setDrawDistance(drawDistance);
      newAction.setAlwaysMultiply(alwaysMultiply);
      newAction.setPhysics(physics);
      actionList.add(newAction);
    }
    else
      System.out.println("ERROR: CANNOT BE NULL OR AN OBJECT ACTION");
  }
  public Action removeFirstAction(){
    return actionList.removeFirst();
  }
  public Action removeLastAction(){
    return actionList.removeLast();
  }
  public Action removeAction(int i){
    return actionList.remove(i);
  }
  public void clearActionList(){
    actionList.clear();
  }
  public boolean hasActions(){
    return !actionList.isEmpty();
  }
  public int numOfActions(){
    return actionList.size();
  }
  public void executeActions(){
    int length = actionList.size();
    for(int i = 0; i < length; i++){
      tempAction = actionList.removeFirst();
      actionList.add(tempAction);
      tempAction.perform();
    }
  }
  public void alwaysPerform(boolean perform){
    alwaysPerform = perform;
  }
  public boolean alwaysPerform(){
    return alwaysPerform;
  }
  public float[] returnPosition(){
    return pos;
  }
  public Physics returnPhysicsPtr(){
    return physics;
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
  public float[] returnRotation(){
    return rot;
  }
  
  public float[] returnScale(){
    return scale;
  }
  
  public float[][] returnShear(){
    return shear;
  }
  public float[] returnShearX(){
    return shear[0];
  }
  public float[] returnShearY(){
    return shear[1];
  }
  public float[] returnShearZ(){
    return shear[2];
  }



  public Matrix returnModelMatrix(){
    return camAsMod;
  }
  
  public void computeInverseView(){
    camAsMod.copy(MVP.inverseViewMatrix(pos, rot, scale, shear));
  }
  


  public void setPosition(float[] position){
    pos[0] = position[0];
    pos[1] = position[1];
    pos[2] = position[2];
  }
  public void setPosition(float posX, float posY, float posZ){
    pos[0] = posX;
    pos[1] = posY;
    pos[2] = posZ;
  }
  public void setPositionX(float posX){
    pos[0] = posX;
  }
  public void setPositionY(float posY){
    pos[1] = posY;
  }
  public void setPositionZ(float posZ){
    pos[2] = posZ;
  }
  
 public void setRotation(float[] rotation){
    rot[0] = rotation[0];
    rot[1] = rotation[1];
    rot[2] = rotation[2];
  }
  public void setRotation(float rotX, float rotY, float rotZ){
    rot[0] = rotX;
    rot[1] = rotY;
    rot[2] = rotZ;
  }
  public void setRotationX(float rotX){
    rot[0] = rotX;
  }
  public void setRotationY(float rotY){
    rot[1] = rotY;
  }
  public void setRotationZ(float rotZ){
    rot[2] = rotZ;
  }
  
 public void setScale(float[] scl){
    scale[0] = scl[0];
    scale[1] = scl[1];
    scale[2] = scl[2];
  }
  public void setScale(float scaleX, float scaleY, float scaleZ){
    scale[0] = scaleX;
    scale[1] = scaleY;
    scale[2] = scaleZ;
  }
  public void setScaleX(float scaleX){
    scale[0] = scaleX;
  }
  public void setScaleY(float scaleY){
    scale[1] = scaleY;
  }
  public void setScaleZ(float scaleZ){
    scale[2] = scaleZ;
  }
  
  public void setShear(float[][] shr){
    shear[0][0] = shr[0][0];
    shear[0][1] = shr[0][1];
    shear[1][0] = shr[1][0];
    shear[1][1] = shr[1][1];
    shear[2][0] = shr[2][0];
    shear[2][1] = shr[2][1];
  }
  public void setShear(float[] shearX, float[] shearY, float[] shearZ){
    shear[0][0] = shearX[0];
    shear[0][1] = shearX[1];
    shear[1][0] = shearY[0];
    shear[1][1] = shearY[1];
    shear[2][0] = shearZ[0];
    shear[2][1] = shearZ[1];
  }
  public void setShearX(float[] shearX){
    shear[0][0] = shearX[0];
    shear[0][1] = shearX[1];
  }
  public void setShearY(float[] shearY){
    shear[1][0] = shearY[0];
    shear[1][1] = shearY[1];
  }
  public void setShearZ(float[] shearZ){
    shear[2][0] = shearZ[0];
    shear[2][1] = shearZ[1];
  }
  public void setShearX(float shearX1, float shearX2){
    shear[0][0] = shearX1;
    shear[0][1] = shearX2;
  }
  public void setShearY(float shearY1, float shearY2){
    shear[1][0] = shearY1;
    shear[1][1] = shearY2;
  }
  public void setShearZ(float shearZ1, float shearZ2){
    shear[2][0] = shearZ1;
    shear[2][1] = shearZ2;
  }
  
  public void copy(Object o){
    if(o instanceof Camera){
      Camera c = (Camera)o;
      camAsMod.copy(c.camAsMod);
      colour.val = c.colour.val;
      alwaysPerform = c.alwaysPerform;
      drawDistance.val = c.drawDistance.val;
      alwaysMultiply.val = c.alwaysMultiply.val;
      for(byte i = 0; i < 3; i++){
        pos[i] = c.pos[i];
        rot[i] = c.rot[i];
        scale[i] = c.scale[i];
        shear[i][0] = c.shear[i][0];
        shear[i][1] = c.shear[i][1];
        invColour[i] = c.invColour[i];
      }
    }
   }
  public void copy(Camera c){
    camAsMod.copy(c.camAsMod);
    colour.val = c.colour.val;
    alwaysPerform = c.alwaysPerform;
    drawDistance.val = c.drawDistance.val;
    alwaysMultiply.val = c.alwaysMultiply.val;
    for(byte i = 0; i < 3; i++){
      pos[i] = c.pos[i];
      rot[i] = c.rot[i];
      scale[i] = c.scale[i];
      shear[i][0] = c.shear[i][0];
      shear[i][1] = c.shear[i][1];
      invColour[i] = c.invColour[i];
    }
  }
  public boolean equals(Object o){
    if(o instanceof Camera){
      Camera c = (Camera)o;
      boolean isEqual = camAsMod.equals(c.camAsMod);
      isEqual&=(alwaysPerform == c.alwaysPerform);
      isEqual&=(colour.val == c.colour.val);
      isEqual&=(Math.abs(drawDistance.val - c.drawDistance.val) < 0.0001);
      isEqual&=(alwaysMultiply.val == c.alwaysMultiply.val);
      for(byte i = 0; i < 3; i++){
        isEqual&=(Math.abs(pos[i] - c.pos[i]) <= 0.0001);
        isEqual&=(Math.abs(rot[i] - c.rot[i]) <= 0.0001);
        isEqual&=(Math.abs(scale[i] - c.scale[i]) <= 0.0001);
        isEqual&=(Math.abs(shear[i][0] - c.shear[i][0]) <= 0.0001);
        isEqual&=(Math.abs(shear[i][1] - c.shear[i][1]) <= 0.0001);
        isEqual&=(Math.abs(invColour[i] - c.invColour[i]) <= 0.0001);
      }
      return isEqual;
    }
    return false;
  }
  public boolean equals(Camera c){
    boolean isEqual = camAsMod.equals(c.camAsMod);
    isEqual&=(alwaysPerform == c.alwaysPerform);
    isEqual&=(colour.val == c.colour.val);
    isEqual&=(Math.abs(drawDistance.val - c.drawDistance.val) < 0.0001);
    isEqual&=(alwaysMultiply.val == c.alwaysMultiply.val);
    for(byte i = 0; i < 3; i++){
      isEqual&=(Math.abs(pos[i] - c.pos[i]) <= 0.0001);
      isEqual&=(Math.abs(rot[i] - c.rot[i]) <= 0.0001);
      isEqual&=(Math.abs(scale[i] - c.scale[i]) <= 0.0001);
      isEqual&=(Math.abs(shear[i][0] - c.shear[i][0]) <= 0.0001);
      isEqual&=(Math.abs(shear[i][1] - c.shear[i][1]) <= 0.0001);
      isEqual&=(Math.abs(invColour[i] - c.invColour[i]) <= 0.0001);
    }
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

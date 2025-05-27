import java.util.*;
public class Billboard {
  private BillboardImg image;
  private float[] pos = {0, 0, 0}; //The position of the billboard
  private float[] scale = {1, 1, 1}; //The scale of the billboard
  private float[] rot = {0, 0, 0};
  private float[][] shear = {{0, 0}, {0, 0}, {0, 0}};
  private Matrix billModel = new Matrix();
  private int fill = 0xFFFFFFFF;
  private int stroke = 0xFF000000;
  //bit 0 = attached to camera, 
  //bit 1 = draw in front, 
  //bit 2 = outline, 
  //bit 3 = inside pixels, 
  //bit 4 = removal enable
  //bit 5 = always perform actions
  private byte flags = 8; 
  private LinkedList<Action> actionList = new LinkedList<Action>();
  private Action tempAction;
  private float brightness = 1;
  private float shininess = 4;
  private float maxFizzel = 1;
  private float fizzelThreshold = 1.1f;
  private FloatWrapper uniTint = new FloatWrapper();
  public Billboard(){
    image = new BillboardImg();
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    fill = 0xFFFFFFFF;
    brightness = 1;
    shininess = 4;
    flags = 8;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    actionList = new LinkedList<Action>();
    uniTint.val = 1;
  }

  public Billboard(String imagePath){
    image = new BillboardImg(imagePath);
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    fill = 0xFFFFFFFF;
    brightness = 1;
    shininess = 4;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    uniTint.val = 1;
    flags = 8;
  }
  public Billboard(String imagePath, int newFill){
    image = new BillboardImg(imagePath);
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    fill(newFill);
    brightness = 1;
    shininess = 4;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    uniTint.val = 1;
    flags = 8;
    actionList = new LinkedList<Action>();
  }
  public Billboard(int[] newImage, int newWidth, int newHeight){
    image = new BillboardImg(newImage, newWidth, newHeight);
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    brightness = 1;
    shininess = 4;
    fill = 0xFFFFFFFF;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    flags = 8;
    uniTint.val = 1;
    actionList = new LinkedList<Action>();
  }
  public Billboard(int[] newImage, int newWidth, int newHeight, byte newTint){
    image = new BillboardImg(newImage, newWidth, newHeight);
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    brightness = 1;
    shininess = 4;
    fill = 0xFFFFFFFF;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    uniTint.val = 1;
    flags = 8;
    actionList = new LinkedList<Action>();
  }

  public Billboard(BillboardImg img){
    image = img;
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    brightness = 1;
    shininess = 4;
    fill = 0xFFFFFFFF;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    uniTint.val = 1;
    flags = 8;
    actionList = new LinkedList<Action>();
  }
  public Billboard(BillboardImg img, int newFill){
    image = img;
    pos[0] = 0;
    pos[1] = 0;
    pos[2] = 0;
    scale[0] = 1;
    scale[1] = 1;
    brightness = 1;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    shininess = 4;
    uniTint.val = 1;
    fill(newFill); 
    flags = 8;
    actionList = new LinkedList<Action>();
  }

  public void setFizzelParameters(float newMax, float newThreshold){
    maxFizzel = newMax;
    fizzelThreshold = newThreshold;
  }
  public float returnMaxFizzel(){
    return maxFizzel;
  }
  public float returnFizzelThreshold(){
    return fizzelThreshold;
  }

  public void addAction(ModelAction newAction){
    if(newAction != null){
      newAction.setPos(pos);
      newAction.setRot(rot);
      newAction.setScale(scale);
      newAction.setShear(shear);
      newAction.setMatrix(billModel);
      newAction.setModelTint(uniTint);
      actionList.add(newAction);
    }
    else
      System.out.println("ERROR: ACTION CANNOT BE NULL");
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
    if(perform)
      flags|=32;
    else
      flags&=-33;
  }
  public boolean alwaysPerform(){
    return (flags & 32) == 32;
  }
  public void setModelTint(float newTint){
    uniTint.val = Math.max(0, Math.min(newTint, 1));
  }
  public float returnModelTint(){
    return uniTint.val;
  }
  public float returnBrightness(){
    return brightness;
  }
  public void setBrightness(float newBrightness){
    brightness = Math.max(0, Math.min(newBrightness, 1));
  }
  public float returnShininess(){
    return shininess;
  }
  public void setShininess(float newShininess){
    shininess = Math.max(0, newShininess);
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

  public void setRotation(float alpha, float beta, float gamma){
    rot[0] = alpha;
    rot[1] = beta;
    rot[2] = gamma;
  }
  public void setRotation(float[] newRot){
    rot[0] = newRot[0];
    rot[1] = newRot[1];
    rot[2] = newRot[2];
  }
  public void setShear(float[][] newShear){
    shear[0][0] = newShear[0][1];
    shear[0][1] = newShear[0][1];
    shear[1][0] = newShear[1][0];
    shear[1][1] = newShear[1][1];
    shear[2][0] = newShear[2][0];
    shear[2][1] = newShear[2][1];
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
  public void setShearX(float shearX1, float shearX2){
    shear[0][0] = shearX1;
    shear[0][1] = shearX2;
  }

  public void setShearY(float[] shearY){
    shear[1][0] = shearY[0];
    shear[1][1] = shearY[1];
  }
  public void setShearY(float shearY1, float shearY2){
    shear[1][0] = shearY1;
    shear[1][1] = shearY2;
  }
  public void setShearZ(float[] shearZ){
    shear[2][0] = shearZ[0];
    shear[2][1] = shearZ[1];
  }
  public void setShearZ(float shearZ1, float shearZ2){
    shear[2][0] = shearZ1;
    shear[2][1] = shearZ2;
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
  
  public void setBillBoardModelMatrix(Matrix newModel){
    if(Objects.nonNull(newModel) && newModel.returnWidth() == 4 && newModel.returnHeight() == 4)
      billModel.copy(newModel);
    else
      System.out.println("ERROR: MATRIX MUST BE A VALID 4x4 MATRIX");
  }
  public void setBillBoardModelMatrix(){
    billModel.copy(MVP.inverseViewMatrix(pos, rot, scale, shear));
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

  public boolean hasOutline(){
    return (flags & 4) == 4;
  }
  public boolean hasImage(){
    return (flags & 8) == 8;
  }
  public boolean hasRemoval(){
    return (flags & 16) == 16;
  }

  public boolean equals(Object o){
    if(o instanceof Billboard){
      Billboard b = (Billboard)o;
      boolean isEqual = true;
      isEqual&=image.equals(b.image);
      isEqual&=billModel.equals(b.billModel);
      isEqual&=(stroke == b.stroke);
      for(byte i = 0; i < 3; i++){
        isEqual&=(Math.abs(pos[i] - b.pos[i]) <= 0.0001);
        isEqual&=(Math.abs(rot[i] - b.rot[i]) <= 0.0001);
        isEqual&=(Math.abs(scale[i] - b.scale[i]) <= 0.0001);
        isEqual&=(Math.abs(shear[i][0] - b.shear[i][0]) <= 0.0001);
        isEqual&=(Math.abs(shear[i][1] - b.shear[i][1]) <= 0.0001);
      }
      isEqual&=(fill == b.fill);
      isEqual&=(Math.abs(brightness - b.brightness) <= 0.0001);
      isEqual&=(Math.abs(shininess - b.shininess) <= 0.0001);
      isEqual&=(flags == b.flags);
      isEqual&=(Math.abs(maxFizzel - b.maxFizzel) <= 0.0001);
      isEqual&=(Math.abs(fizzelThreshold - b.fizzelThreshold) <= 0.0001);
      isEqual&=(Math.abs(uniTint.val - b.uniTint.val) <= 0.0001);
      return isEqual;
    }
    else
      return false;
  }
  public boolean equals(Billboard b){
    boolean isEqual = true;
    isEqual&=image.equals(b.image);
    isEqual&=billModel.equals(b.billModel);
    isEqual&=(stroke == b.stroke);
    for(byte i = 0; i < 3; i++){
      isEqual&=(Math.abs(pos[i] - b.pos[i]) <= 0.0001);
      isEqual&=(Math.abs(rot[i] - b.rot[i]) <= 0.0001);
      isEqual&=(Math.abs(scale[i] - b.scale[i]) <= 0.0001);
      isEqual&=(Math.abs(shear[i][0] - b.shear[i][0]) <= 0.0001);
      isEqual&=(Math.abs(shear[i][1] - b.shear[i][1]) <= 0.0001);
    }
    isEqual&=(fill == b.fill);
    isEqual&=(Math.abs(brightness - b.brightness) <= 0.0001);
    isEqual&=(Math.abs(shininess - b.shininess) <= 0.0001);
    isEqual&=(flags == b.flags);
    isEqual&=(Math.abs(maxFizzel - b.maxFizzel) <= 0.0001);
    isEqual&=(Math.abs(fizzelThreshold - b.fizzelThreshold) <= 0.0001);
    isEqual&=(Math.abs(uniTint.val - b.uniTint.val) <= 0.0001);
    return isEqual;
  }
  public void copy(Object o){
    if(o instanceof Billboard){
      Billboard b = (Billboard)o;
      image = b.image;
      billModel.copy(b.billModel);
      stroke = b.stroke;
      for(byte i = 0; i < 3; i++){
        pos[i] = b.pos[i];
        rot[i] = b.rot[i];
        scale[i] = b.scale[i];
        shear[i][0] = b.shear[i][0];
        shear[i][1] = b.shear[i][1];
      }
      fill = b.fill;
      brightness = b.brightness;
      shininess = b.shininess;
      uniTint.val = b.uniTint.val;
      maxFizzel = b.maxFizzel;
      fizzelThreshold = b.fizzelThreshold;
    }
  }
  public void copy(Billboard b){
    image = b.image;
    billModel.copy(b.billModel);
    stroke = b.stroke;
    for(byte i = 0; i < 3; i++){
      pos[i] = b.pos[i];
      rot[i] = b.rot[i];
      scale[i] = b.scale[i];
      shear[i][0] = b.shear[i][0];
      shear[i][1] = b.shear[i][1];
    }
    fill = b.fill;
    brightness = b.brightness;
    shininess = b.shininess;
    uniTint.val = b.uniTint.val;
    maxFizzel = b.maxFizzel;
    fizzelThreshold = b.fizzelThreshold;
  }
}

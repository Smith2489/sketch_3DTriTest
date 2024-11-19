public class Camera{
  private float[] pos = {0, 0, 0}; //Holds the camera's position in 3D space
  private float[] rot = {0, 0, 0}; //Holds the camera's rotation in 3D space
  private float[] scale = {1, 1, 1}; //Holds the scale of the camera
  private float[][] shear = {{0, 0}, {0, 0}, {0, 0}}; //Holds how the camera is sheared
  public Camera(){
    for(byte i = 0; i < 3; i++){
      pos[i] = 0;
      rot[i] = 0;
      scale[i] = 1;
      shear[i][0] = 0;
      shear[i][1] = 0;
    }
  }
  public Camera(float[] position, float[] rotation, float[] scl, float[][] shr){
    for(byte i = 0; i < 3; i++){
      pos[i] = position[i];
      rot[i] = rotation[i];
      scale[i] = scl[i];
      shear[i][0] = shr[i][0];
      shear[i][1] = shr[i][1];
    }
  }
  public Camera(float posX, float posY, float posZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ, float[] shearX, float[] shearY, float[] shearZ){
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
  }
  
  public float[] returnPosition(){
    return pos;
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
  
  public void copy(Camera c){
   for(byte i = 0; i < 3; i++){
      pos[i] = c.pos[i];
      rot[i] = c.rot[i];
      scale[i] = c.scale[i];
      shear[i][0] = c.shear[i][0];
      shear[i][1] = c.shear[i][1];
    }
  }
  
  public String toString(){
    String out = "POSITION: ("+pos[0]+", "+pos[1]+", "+pos[2]+")";
    out+="\nROTATION: ("+rot[0]+", "+rot[1]+", "+rot[2]+")";
    out+="\nSCALE: ("+scale[0]+", "+scale[1]+", "+scale[2]+")";
    out+="\nSHEAR: (("+shear[0][0]+", "+shear[0][1]+"), ("+shear[1][0]+", "+shear[1][1]+"), ("+shear[2][0]+", "+shear[2][1]+"))";
    return out;
  }
}

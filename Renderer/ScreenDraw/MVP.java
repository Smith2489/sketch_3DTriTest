package Renderer.ScreenDraw;
import Maths.LinearAlgebra.*;
public class MVP{
  public static final float DEG_TO_RADS = (float)Math.PI/180; //Converstion formula for going from degrees to radians
  private static float[] modelAngles = {0, 0, 0}; //Angles for Model matrix
  private static float[] modelPos = {0, 0, 0}; //Position of Model matrix
  private static float[] modelScale = {1, 1, 1}; //Scalling for Model matrix
  private static float[][] modelShear = {{0, 0}, {0, 0}, {0, 0}}; //Shearing for the model
  private static float[] eyePos = {0, 0, 0}; //View matrix position 
  private static float[] eyeScale = {1, 1, 1}; //View matrix scale
  private static float[] inverseEyeScale = {1, 1, 1};
  private static float[] eyeAngles = {0, 0, 0}; //View matrix angles
  private static float[][] eyeShear = {{0, 0}, {0, 0}, {0, 0}}; //Camera shear

  
  //Computing matrices for MVP
  public static void setModelPos(float x, float y, float z){
    modelPos[0] = x;
    modelPos[1] = y;
    modelPos[2] = z;
  }
  
  public static void setModelPos(float[] newPos){
    modelPos[0] = newPos[0];
    modelPos[1] = newPos[1];
    modelPos[2] = newPos[2];
  }
  
  public static void setModelAngles(float alpha, float beta, float gamma){
    modelAngles[0] = alpha;
    modelAngles[1] = beta;
    modelAngles[2] = gamma;
  }
  
  public static void setModelAngls(float[] angles){
    modelAngles[0] = angles[0];
    modelAngles[1] = angles[1];
    modelAngles[2] = angles[2];
  }
  
  public static void setModelScale(float scaleX, float scaleY, float scaleZ){
    modelScale[0] = scaleX;
    modelScale[1] = scaleY;
    modelScale[2] = scaleZ;
  }
  public static void setModelScale(float[] scale){
    modelScale[0] = scale[0];
    modelScale[1] = scale[1];
    modelScale[2] = scale[2];
  }
  public static void setModelShear(float[][] shear){
    for(byte i = 0; i < 3; i++){
      modelShear[i][0] = shear[i][0];
      modelShear[i][1] = shear[i][1];
    }
  }
  
  public static void setModelShear(float[] shearX, float[] shearY, float[] shearZ){
    modelShear[0][0] = shearX[0];
    modelShear[0][1] = shearX[1];
    modelShear[1][0] = shearY[0];
    modelShear[1][1] = shearY[1];
    modelShear[2][0] = shearZ[0];
    modelShear[2][1] = shearZ[1];
  }
  
  
  
  
  //Setting up the variables for the view matrix
  public static void setEyePos(float x, float y, float z){
    eyePos[0] = x;
    eyePos[1] = y;
    eyePos[2] = z;
  }

  public static void setEyePos(float[] newEyePos){
    eyePos[0] = newEyePos[0];
    eyePos[1] = newEyePos[1];
    eyePos[2] = newEyePos[2];
  }
  
  public static void setEyeScale(float scaleX, float scaleY, float scaleZ){
    eyeScale[0] = (Math.abs(scaleX) >= 0.0001) ? 1/scaleX : 0;
    eyeScale[1] = (Math.abs(scaleY) >= 0.0001) ? 1/scaleY : 0;
    eyeScale[2] = (Math.abs(scaleZ) >= 0.0001) ? 1/scaleZ : 0;
    inverseEyeScale[0] = scaleX;
    inverseEyeScale[1] = scaleY;
    inverseEyeScale[2] = scaleZ;
  }
  
  public static void setEyeScale(float[] scale){
    eyeScale[0] = (Math.abs(scale[0]) >= 0.0001) ? 1/scale[0] : 0;
    eyeScale[1] = (Math.abs(scale[1]) >= 0.0001) ? 1/scale[1] : 0;
    eyeScale[2] = (Math.abs(scale[2]) >= 0.0001) ? 1/scale[2] : 0;
    inverseEyeScale[0] = scale[0];
    inverseEyeScale[1] = scale[1];
    inverseEyeScale[2] = scale[2];
  }
  
  public static void setEyeAngles(float alpha, float beta, float gamma){
    eyeAngles[0] = alpha;
    eyeAngles[1] = beta;
    eyeAngles[2] = gamma;
  }
  public static void setEyeAngles(float[] angles){
    eyeAngles[0] = angles[0];
    eyeAngles[1] = angles[1];
    eyeAngles[2] = angles[2];
  }
  
  public static void setEyeShear(float[][] shear){
    for(byte i = 0; i < 3; i++){
      eyeShear[i][0] = -shear[i][0];
      eyeShear[i][1] = -shear[i][1];
    }
  }
  public static void setEyeShear(float[] shearX, float[] shearY, float[] shearZ){
    eyeShear[0][0] = -shearX[0];
    eyeShear[0][1] = -shearX[1];
    eyeShear[1][0] = -shearY[0];
    eyeShear[1][1] = -shearY[1];
    eyeShear[2][0] = -shearZ[0];
    eyeShear[2][1] = -shearZ[1];
  }
  
  public static float returnModelPos(byte index){
    if(index >= 0 && index < 3)
      return modelPos[index];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  public static float[] returnModelPos(){
    float[] returnPos = {modelPos[0], modelPos[1], modelPos[2]};
    return returnPos;
  }
  public static float returnModelScale(byte index){
    if(index >= 0 && index < 3)
      return modelScale[index];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  public static float returnModelShear(byte axisIndex, byte shearIndex){
    if(axisIndex >= 0 && axisIndex < 3 && shearIndex >= 0 && shearIndex < 2)
      return modelShear[axisIndex][shearIndex];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  public static float returnModelRotation(byte index){
    if(index >= 0 && index < 3)
      return modelAngles[index];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  
  public static float returnEyePos(byte index){
    if(index >= 0 && index < 3)
      return eyePos[index];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  public static float returnEyeScale(byte index){
    if(index >= 0 && index < 3)
      return eyeScale[index];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  public static float returnEyeShear(byte axisIndex, byte shearIndex){
    if(axisIndex >= 0 && axisIndex < 3 && shearIndex >= 0 && shearIndex < 2)
      return eyeShear[axisIndex][shearIndex];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  public static float returnEyeRotation(byte index){
    if(index >= 0 && index < 3)
      return eyeAngles[index];
    System.out.println("ERROR: INDEX OUT OF RANGE");
    System.exit(1);
    return -1;
  }
  
  //Rotation Matrix for Model (M)
  public static Matrix4x4 returnRotation(float alpha, float beta, float gamma){
    //Degrees to radians conversion
    alpha = alpha*DEG_TO_RADS-0.0001f;
    beta = beta*DEG_TO_RADS-0.0001f;
    gamma = gamma*DEG_TO_RADS-0.0001f;
    //Rotation matrices
    Matrix4x4 xRotation = new Matrix4x4();
    xRotation.setData((float)Math.cos(alpha), 1, 1);
    xRotation.setData((float)-Math.sin(alpha), 1, 2);
    xRotation.setData((float)Math.sin(alpha), 2, 1);
    xRotation.setData((float)Math.cos(alpha), 2, 2);

    Matrix4x4 yRotation = new Matrix4x4();
    yRotation.setData((float)Math.cos(beta), 0, 0);
    yRotation.setData((float)Math.sin(beta), 0, 2);
    yRotation.setData((float)-Math.sin(beta), 2, 0);
    yRotation.setData((float)Math.cos(beta), 2, 2);

    Matrix4x4 zRotation = new Matrix4x4();
    zRotation.setData((float)Math.cos(gamma), 0, 0);
    zRotation.setData((float)-Math.sin(gamma), 0, 1);
    zRotation.setData((float)Math.sin(gamma), 1, 0);
    zRotation.setData((float)Math.cos(gamma), 1, 1);

    Matrix4x4 firstStep = MatrixOperations.matrixMultiply(zRotation, yRotation);
    return MatrixOperations.matrixMultiply(firstStep, xRotation);
  }
  //Rotation Matrix for Model (M)
  private static Matrix4x4 reverseRotation(float alpha, float beta, float gamma){
    //Degrees to radians conversion
    alpha = alpha*DEG_TO_RADS-0.0001f;
    beta = beta*DEG_TO_RADS-0.0001f;
    gamma = gamma*DEG_TO_RADS-0.0001f;

    //Rotation matrices
    Matrix4x4 xRotation = new Matrix4x4();
    xRotation.setData((float)Math.cos(alpha), 1, 1);
    xRotation.setData((float)-Math.sin(alpha), 1, 2);
    xRotation.setData((float)Math.sin(alpha), 2, 1);
    xRotation.setData((float)Math.cos(alpha), 2, 2);

    Matrix4x4 yRotation = new Matrix4x4();
    yRotation.setData((float)Math.cos(beta), 0, 0);
    yRotation.setData((float)Math.sin(beta), 0, 2);
    yRotation.setData((float)-Math.sin(beta), 2, 0);
    yRotation.setData((float)Math.cos(beta), 2, 2);

    Matrix4x4 zRotation = new Matrix4x4();
    zRotation.setData((float)Math.cos(gamma), 0, 0);
    zRotation.setData((float)-Math.sin(gamma), 0, 1);
    zRotation.setData((float)Math.sin(gamma), 1, 0);
    zRotation.setData((float)Math.cos(gamma), 1, 1);

    Matrix4x4 firstStep = MatrixOperations.matrixMultiply(xRotation, yRotation);
    return MatrixOperations.matrixMultiply(firstStep, zRotation);
  }
  public static Matrix4x4 returnRotation(float[] angles){
    //Degrees to radians conversion
    float[] anglesRad = new float[3];
    anglesRad[0] = angles[0]*DEG_TO_RADS-0.0001f;
    anglesRad[1] = angles[1]*DEG_TO_RADS-0.0001f;
    anglesRad[2] = angles[2]*DEG_TO_RADS-0.0001f;
    //Rotation matrices
    Matrix4x4 xRotation = new Matrix4x4();
    xRotation.setData((float)Math.cos(anglesRad[0]), 1, 1);
    xRotation.setData((float)-Math.sin(anglesRad[0]), 1, 2);
    xRotation.setData((float)Math.sin(anglesRad[0]), 2, 1);
    xRotation.setData((float)Math.cos(anglesRad[0]), 2, 2);

    Matrix4x4 yRotation = new Matrix4x4();
    yRotation.setData((float)Math.cos(anglesRad[1]), 0, 0);
    yRotation.setData((float)Math.sin(anglesRad[1]), 0, 2);
    yRotation.setData((float)-Math.sin(anglesRad[1]), 2, 0);
    yRotation.setData((float)Math.cos(anglesRad[1]), 2, 2);

    Matrix4x4 zRotation = new Matrix4x4();
    zRotation.setData((float)Math.cos(anglesRad[2]), 0, 0);
    zRotation.setData((float)-Math.sin(anglesRad[2]), 0, 1);
    zRotation.setData((float)Math.sin(anglesRad[2]), 1, 0);
    zRotation.setData((float)Math.cos(anglesRad[2]), 1, 1);

    Matrix4x4 firstStep = MatrixOperations.matrixMultiply(zRotation, yRotation);
    return MatrixOperations.matrixMultiply(firstStep, xRotation);
  }
  public static Matrix4x4 reverseRotation(float[] angles){
    //Degrees to radians conversion
    float[] anglesRad = new float[3];
    anglesRad[0] = angles[0]*DEG_TO_RADS-0.0001f;
    anglesRad[1] = angles[1]*DEG_TO_RADS-0.0001f;
    anglesRad[2] = angles[2]*DEG_TO_RADS-0.0001f;

    //Rotation matrices
    Matrix4x4 xRotation = new Matrix4x4();
    xRotation.setData((float)Math.cos(anglesRad[0]), 1, 1);
    xRotation.setData((float)-Math.sin(anglesRad[0]), 1, 2);
    xRotation.setData((float)Math.sin(anglesRad[0]), 2, 1);
    xRotation.setData((float)Math.cos(anglesRad[0]), 2, 2);

    Matrix4x4 yRotation = new Matrix4x4();
    yRotation.setData((float)Math.cos(anglesRad[1]), 0, 0);
    yRotation.setData((float)Math.sin(anglesRad[1]), 0, 2);
    yRotation.setData((float)-Math.sin(anglesRad[1]), 2, 0);
    yRotation.setData((float)Math.cos(anglesRad[1]), 2, 2);

    Matrix4x4 zRotation = new Matrix4x4();
    zRotation.setData((float)Math.cos(anglesRad[2]), 0, 0);
    zRotation.setData((float)-Math.sin(anglesRad[2]), 0, 1);
    zRotation.setData((float)Math.sin(anglesRad[2]), 1, 0);
    zRotation.setData((float)Math.cos(anglesRad[2]), 1, 1);

    Matrix4x4 firstStep = MatrixOperations.matrixMultiply(xRotation, yRotation);
    return MatrixOperations.matrixMultiply(firstStep, zRotation);
  }

  //Translation Matrix for Model (M)
  public static Matrix4x4 returnTranslation(float x, float y, float z){
    Matrix4x4 translation = new Matrix4x4();
    translation.setData(x, 0, 3);
    translation.setData(y, 1, 3);
    translation.setData(z, 2, 3);
    return translation;
  }

  public static Matrix4x4 returnTranslation(float[] pos){
    Matrix4x4 translation = new Matrix4x4();
    translation.setData(pos[0], 0, 3);
    translation.setData(pos[1], 1, 3);
    translation.setData(pos[2], 2, 3);
    return translation;
  }

  //Scaling Matrix for Model (M)
  public static Matrix4x4 returnScale(float sX, float sY, float sZ){
    Matrix4x4 scaleMatrix = new Matrix4x4();
    scaleMatrix.setData(sX, 0, 0);
    scaleMatrix.setData(sY, 1, 1);
    scaleMatrix.setData(sZ, 2, 2);
    return scaleMatrix;
  }
  public static Matrix4x4 returnScale(float[] scale){
    Matrix4x4 scaleMatrix = new Matrix4x4();
    scaleMatrix.setData(scale[0], 0, 0);
    scaleMatrix.setData(scale[1], 1, 1);
    scaleMatrix.setData(scale[2], 2, 2);
    return scaleMatrix;
  }
  public static Matrix4x4 returnShear(float sX1, float sX2, float sY1, float sY2, float sZ1, float sZ2){
    Matrix4x4 shearX = new Matrix4x4();
    shearX.setData(sX1, 0, 1);
    shearX.setData(sX2, 0, 2);

    Matrix4x4 shearY = new Matrix4x4();
    shearY.setData(sY1, 1, 0);
    shearY.setData(sY2, 1, 2);

    Matrix4x4 shearZ = new Matrix4x4();
    shearZ.setData(sZ1, 2, 0);
    shearZ.setData(sZ2, 2, 1);

    return MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(shearZ, shearY), shearX);
  }
  private static Matrix4x4 reverseShear(float sX1, float sX2, float sY1, float sY2, float sZ1, float sZ2){
    Matrix4x4 shearX = new Matrix4x4();
    shearX.setData(sX1, 0, 1);
    shearX.setData(sX2, 0, 2);

    Matrix4x4 shearY = new Matrix4x4();
    shearY.setData(sY1, 1, 0);
    shearY.setData(sY2, 1, 2);

    Matrix4x4 shearZ = new Matrix4x4();
    shearZ.setData(sZ1, 2, 0);
    shearZ.setData(sZ2, 2, 1);

    return MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(shearX, shearY), shearZ);
  }

  //Model Matrix (M)
  public static Matrix4x4 modelMatrix(){
    Matrix4x4 translate = returnTranslation(modelPos[0], modelPos[1], modelPos[2]);
    Matrix4x4 rotate = returnRotation(modelAngles[0], modelAngles[1], modelAngles[2]);
    Matrix4x4 scale = returnScale(modelScale[0], modelScale[1], modelScale[2]);
    Matrix4x4 shear = returnShear(modelShear[0][0], modelShear[0][1], modelShear[1][0], modelShear[1][1], modelShear[2][0], modelShear[2][1]);
    Matrix4x4 step1 = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(translate, scale), rotate);
    return MatrixOperations.matrixMultiply(step1, shear);
  }


  //Computing the view matrix (V)
  public static Matrix4x4 viewMatrix(){
    Matrix4x4 translate = returnTranslation(-eyePos[0], -eyePos[1], -eyePos[2]);
    Matrix4x4 rotate = reverseRotation(-eyeAngles[0], -eyeAngles[1], -eyeAngles[2]);
    Matrix4x4 scale = returnScale(eyeScale[0], eyeScale[1], eyeScale[2]);
    Matrix4x4 shear = reverseShear(eyeShear[0][0], eyeShear[0][1], eyeShear[1][0], eyeShear[1][1], eyeShear[2][0], eyeShear[2][1]);
    Matrix4x4 step1 = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(shear, rotate), scale);
    return MatrixOperations.matrixMultiply(step1, translate);
  }

  public static Matrix4x4 inverseViewMatrix(){
    Matrix4x4 translate = returnTranslation(eyePos[0], eyePos[1], eyePos[2]);
    Matrix4x4 rotate = returnRotation(eyeAngles);
    Matrix4x4 scale = returnScale(inverseEyeScale[0], inverseEyeScale[1], inverseEyeScale[2]);
    Matrix4x4 shear = returnShear(-modelShear[0][0], -modelShear[0][1], -modelShear[1][0], -modelShear[1][1], -modelShear[2][0], -modelShear[2][1]);
    Matrix4x4 step1 = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(translate, scale), rotate);
    return MatrixOperations.matrixMultiply(step1, shear);
  }
  public static Matrix4x4 inverseViewMatrix(float[] pos, float[] angles, float[] scale, float[][] shear){
    Matrix4x4 translate = returnTranslation(pos[0], pos[1], pos[2]);
    Matrix4x4 rotate = returnRotation(angles);
    Matrix4x4 scaling = returnScale(scale[0], scale[1], scale[2]);
    Matrix4x4 shearing = returnShear(shear[0][0], shear[0][1], shear[1][0], shear[1][1], shear[2][0], shear[2][1]);
    Matrix4x4 step1 = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(translate, scaling), rotate);
    return MatrixOperations.matrixMultiply(step1, shearing);
  }


  //Computing the perspective projection matrix (P)
  public static Matrix4x4 perspMatrix(float aspectRatio, float fovY, float zNear, float zFar){
    //Computing the dimensions of the display
    float top = ((float)Math.tan(fovY*DEG_TO_RADS/2))*Math.abs(zNear);
    float right = aspectRatio*top;
    float farMinusNear = zNear-zFar;
    if(Math.abs(right) > 0.0001 && Math.abs(top) > 0.0001 && Math.abs(farMinusNear) > 0.0001){
      float[][] perspMatrix = {{1/right-0.0001f, 0, 0, 0},
                               {0, 1/top-0.0001f, 0, 0},
                               {0, 0, (-zNear-zFar)/farMinusNear-0.0001f, 2*zFar*zNear/farMinusNear-0.0001f},
                               {0, 0, 1, 0}};
      return new Matrix4x4(perspMatrix);
    }
    else{
      System.out.println("ERROR: DIVISION BY ZERO");
      System.exit(1);
      return new Matrix4x4();
    }

  }
    //Computing the orthographic projection matrix (P)
  public static Matrix4x4 orthoMatrix(float aspectRatio, float fovY, float zNear, float zFar){
    //Computing the dimensions of the display
    float top = ((float)Math.tan(fovY*DEG_TO_RADS/2))*Math.abs(zNear);
    float right = aspectRatio*top;
    float farMinusNear = zNear-zFar;
    if(Math.abs(right) > 0.0001 && Math.abs(top) > 0.0001 && Math.abs(farMinusNear) > 0.0001){
      float[][] orthoMatrix = {{1/right-0.0001f, 0, 0, 0},
                               {0, 1/top-0.0001f, 0, 0},
                               {0, 0, -2/farMinusNear-0.0001f, -(zFar+zNear)/farMinusNear-0.0001f},
                               {0, 0, 0, 1}};
      return new Matrix4x4(orthoMatrix);
          }
    else{
      System.out.println("ERROR: DIVISION BY ZERO");
      System.exit(1);
      return new Matrix4x4();
    }
  }
}

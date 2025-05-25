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
  public static Matrix returnRotation(float alpha, float beta, float gamma){
    //Degrees to radians conversion
    alpha = alpha*DEG_TO_RADS-0.0001f;
    beta = beta*DEG_TO_RADS-0.0001f;
    gamma = gamma*DEG_TO_RADS-0.0001f;
    //Rotation matrices
    float[][] xRotation = {{1, 0, 0, 0},
                           {0, (float)Math.cos(alpha), (float)(-Math.sin(alpha)), 0},
                           {0, (float)Math.sin(alpha), (float)Math.cos(alpha), 0},
                           {0, 0, 0, 1}};
    float[][] yRotation = {{(float)Math.cos(beta), 0, (float)Math.sin(beta), 0},
                           {0, 1, 0, 0},
                           {(float)(-Math.sin(beta)), 0, (float)(Math.cos(beta)), 0},
                           {0, 0, 0, 1}};
    float[][] zRotation = {{(float)Math.cos(gamma), (float)(-Math.sin(gamma)), 0, 0},
                           {(float)(Math.sin(gamma)), (float)Math.cos(gamma), 0, 0},
                           {0, 0, 1, 0},
                           {0, 0, 0, 1}};
    Matrix firstStep = new Matrix(MatrixOperations.matrixMultiply(zRotation, yRotation));
    Matrix xRot = new Matrix(xRotation);
  
    return MatrixOperations.matrixMultiply(firstStep, xRot);
  }
  //Rotation Matrix for Model (M)
  private static Matrix reverseRotation(float alpha, float beta, float gamma){
      //Degrees to radians conversion
      alpha = alpha*DEG_TO_RADS-0.0001f;
      beta = beta*DEG_TO_RADS-0.0001f;
      gamma = gamma*DEG_TO_RADS-0.0001f;
      //Rotation matrices
      float[][] xRotation = {{1, 0, 0, 0},
                             {0, (float)Math.cos(alpha), (float)(-Math.sin(alpha)), 0},
                             {0, (float)Math.sin(alpha), (float)Math.cos(alpha), 0},
                             {0, 0, 0, 1}};
      float[][] yRotation = {{(float)Math.cos(beta), 0, (float)Math.sin(beta), 0},
                             {0, 1, 0, 0},
                             {(float)(-Math.sin(beta)), 0, (float)(Math.cos(beta)), 0},
                             {0, 0, 0, 1}};
      float[][] zRotation = {{(float)Math.cos(gamma), (float)(-Math.sin(gamma)), 0, 0},
                             {(float)(Math.sin(gamma)), (float)Math.cos(gamma), 0, 0},
                             {0, 0, 1, 0},
                             {0, 0, 0, 1}};
      Matrix firstStep = new Matrix(MatrixOperations.matrixMultiply(xRotation, yRotation));
      Matrix xRot = new Matrix(zRotation);
    
      return MatrixOperations.matrixMultiply(firstStep, xRot);
    }
  public static Matrix returnRotation(float[] angles){
    //Degrees to radians conversion
    float[] anglesRad = new float[3];
    anglesRad[0] = angles[0]*DEG_TO_RADS-0.0001f;
    anglesRad[1] = angles[1]*DEG_TO_RADS-0.0001f;
    anglesRad[2] = angles[2]*DEG_TO_RADS-0.0001f;
    //Rotation matrices
    float[][] xRotation = {{1, 0, 0, 0},
                           {0, (float)Math.cos(anglesRad[0]), (float)(-Math.sin(anglesRad[0])), 0},
                           {0, (float)Math.sin(anglesRad[0]), (float)Math.cos(anglesRad[0]), 0},
                           {0, 0, 0, 1}};
    float[][] yRotation = {{(float)Math.cos(anglesRad[1]), 0, (float)Math.sin(anglesRad[1]), 0},
                           {0, 1, 0, 0},
                           {(float)(-Math.sin(anglesRad[1])), 0, (float)(Math.cos(anglesRad[1])), 0},
                           {0, 0, 0, 1}};
    float[][] zRotation = {{(float)Math.cos(anglesRad[2]), (float)(-Math.sin(anglesRad[2])), 0, 0},
                           {(float)(Math.sin(anglesRad[2])), (float)Math.cos(anglesRad[2]), 0, 0},
                           {0, 0, 1, 0},
                           {0, 0, 0, 1}};
    Matrix firstStep = new Matrix(MatrixOperations.matrixMultiply(zRotation, yRotation));
    Matrix xRot = new Matrix(xRotation);
  
    return MatrixOperations.matrixMultiply(firstStep, xRot);
  }
  public static Matrix reverseRotation(float[] angles){
    //Degrees to radians conversion
    float[] anglesRad = new float[3];
    anglesRad[0] = angles[0]*DEG_TO_RADS-0.0001f;
    anglesRad[1] = angles[1]*DEG_TO_RADS-0.0001f;
    anglesRad[2] = angles[2]*DEG_TO_RADS-0.0001f;
    //Rotation matrices
    float[][] xRotation = {{1, 0, 0, 0},
                           {0, (float)Math.cos(anglesRad[0]), (float)(-Math.sin(anglesRad[0])), 0},
                           {0, (float)Math.sin(anglesRad[0]), (float)Math.cos(anglesRad[0]), 0},
                           {0, 0, 0, 1}};
    float[][] yRotation = {{(float)Math.cos(anglesRad[1]), 0, (float)Math.sin(anglesRad[1]), 0},
                           {0, 1, 0, 0},
                           {(float)(-Math.sin(anglesRad[1])), 0, (float)(Math.cos(anglesRad[1])), 0},
                           {0, 0, 0, 1}};
    float[][] zRotation = {{(float)Math.cos(anglesRad[2]), (float)(-Math.sin(anglesRad[2])), 0, 0},
                           {(float)(Math.sin(anglesRad[2])), (float)Math.cos(anglesRad[2]), 0, 0},
                           {0, 0, 1, 0},
                           {0, 0, 0, 1}};
    Matrix firstStep = new Matrix(MatrixOperations.matrixMultiply(xRotation, yRotation));
    Matrix xRot = new Matrix(zRotation);
  
    return MatrixOperations.matrixMultiply(firstStep, xRot);
  }

  //Translation Matrix for Model (M)
  public static Matrix returnTranslation(float x, float y, float z){
    float[][] translation = {{1, 0, 0, x},
                             {0, 1, 0, y},
                             {0, 0, 1, z},
                             {0, 0, 0, 1}};
    return new Matrix(translation);
  }

  public static Matrix returnTranslation(float[] pos){
    float[][] translation = {{1, 0, 0, pos[0]},
                             {0, 1, 0, pos[1]},
                             {0, 0, 1, pos[2]},
                             {0, 0, 0, 1}};
    return new Matrix(translation);
  }

  //Scaling Matrix for Model (M)
  public static Matrix returnScale(float sX, float sY, float sZ){
    float[][] scaleMatrix = {{sX, 0, 0, 0},
                             {0, sY, 0, 0},
                             {0, 0, sZ, 0},
                             {0, 0, 0, 1}};

    return new Matrix(scaleMatrix);
  }
  public static Matrix returnScale(float[] scale){
    float[][] scaleMatrix = {{scale[0], 0, 0, 0},
                             {0, scale[1], 0, 0},
                             {0, 0, scale[2], 0},
                             {0, 0, 0, 1}};
    return new Matrix(scaleMatrix);
  }
  public static Matrix returnShear(float sX1, float sX2, float sY1, float sY2, float sZ1, float sZ2){
    float[][] shearX = {{1, sX1, sX2, 0},
                        {0, 1, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}};
    float[][] shearY = {{1, 0, 0, 0},
                        {sY1, 1, sY2, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}};
    float[][] shearZ = {{1, 0, 0, 0},
                        {0, 1, 0, 0},
                        {sZ1, sZ2, 1, 0},
                        {0, 0, 0, 1}};
    return new Matrix(MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(shearZ, shearY), shearX));
  }
  private static Matrix reverseShear(float sX1, float sX2, float sY1, float sY2, float sZ1, float sZ2){
    float[][] shearX = {{1, sX1, sX2, 0},
                        {0, 1, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}};
    float[][] shearY = {{1, 0, 0, 0},
                        {sY1, 1, sY2, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}};
    float[][] shearZ = {{1, 0, 0, 0},
                        {0, 1, 0, 0},
                        {sZ1, sZ2, 1, 0},
                        {0, 0, 0, 1}};
    return new Matrix(MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(shearX, shearY), shearZ));
  }

  //Model Matrix (M)
  public static Matrix modelMatrix(){
    Matrix translate = returnTranslation(modelPos[0], modelPos[1], modelPos[2]);
    Matrix rotate = returnRotation(modelAngles[0], modelAngles[1], modelAngles[2]);
    Matrix scale = returnScale(modelScale[0], modelScale[1], modelScale[2]);
    Matrix shear = returnShear(modelShear[0][0], modelShear[0][1], modelShear[1][0], modelShear[1][1], modelShear[2][0], modelShear[2][1]);
    Matrix step1 = new Matrix(MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(translate, shear), rotate));
    return MatrixOperations.matrixMultiply(step1, scale);
  }


  //Computing the view matrix (V)
  public static Matrix viewMatrix(){
    Matrix translate = returnTranslation(-eyePos[0], -eyePos[1], -eyePos[2]);
    Matrix rotate = reverseRotation(-eyeAngles[0], -eyeAngles[1], -eyeAngles[2]);
    Matrix scale = returnScale(eyeScale[0], eyeScale[1], eyeScale[2]);
    Matrix shear = reverseShear(eyeShear[0][0], eyeShear[0][1], eyeShear[1][0], eyeShear[1][1], eyeShear[2][0], eyeShear[2][1]);
    Matrix step1 = new Matrix(MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(rotate, scale), shear));
    return MatrixOperations.matrixMultiply(step1, translate);
  }

  public static Matrix inverseViewMatrix(){
    Matrix translate = returnTranslation(eyePos[0], eyePos[1], eyePos[2]);
    Matrix rotate = returnRotation(eyeAngles);
    Matrix scale = returnScale(inverseEyeScale[0], inverseEyeScale[1], inverseEyeScale[2]);
    Matrix shear = returnShear(-modelShear[0][0], -modelShear[0][1], -modelShear[1][0], -modelShear[1][1], -modelShear[2][0], -modelShear[2][1]);
    Matrix step1 = new Matrix(MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(translate, shear), rotate));
    return MatrixOperations.matrixMultiply(step1, scale);
  }
  public static Matrix inverseViewMatrix(float[] pos, float[] angles, float[] scale, float[][] shear){
    Matrix translate = returnTranslation(pos[0], pos[1], pos[2]);
    Matrix rotate = returnRotation(angles);
    Matrix scaling = returnScale(scale[0], scale[1], scale[2]);
    Matrix shearing = returnShear(shear[0][0], shear[0][1], shear[1][0], shear[1][1], shear[2][0], shear[2][1]);
    Matrix step1 = new Matrix(MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(translate, shearing), rotate));
    return MatrixOperations.matrixMultiply(step1, scaling);
  }


  //Computing the perspective projection matrix (P)
  public static Matrix perspMatrix(float aspectRatio, float fovY, float zNear, float zFar){
    //Computing the dimensions of the display
    float top = ((float)Math.tan(fovY*DEG_TO_RADS/2))*Math.abs(zNear);
    float right = aspectRatio*top;
    float farMinusNear = zNear-zFar;
    if(Math.abs(right) > 0.0001 && Math.abs(top) > 0.0001 && Math.abs(farMinusNear) > 0.0001){
      float[][] perspMatrix = {{1/right-0.0001f, 0, 0, 0},
                               {0, 1/top-0.0001f, 0, 0},
                               {0, 0, (-zNear-zFar)/farMinusNear-0.0001f, 2*zFar*zNear/farMinusNear-0.0001f},
                               {0, 0, 1, 0}};
      return new Matrix(perspMatrix);
    }
    else{
      System.out.println("ERROR: DIVISION BY ZERO");
      System.exit(1);
      return new Matrix();
    }

  }
    //Computing the orthographic projection matrix (P)
  public static Matrix orthoMatrix(float aspectRatio, float fovY, float zNear, float zFar){
    //Computing the dimensions of the display
    float top = ((float)Math.tan(fovY*DEG_TO_RADS/2))*Math.abs(zNear);
    float right = aspectRatio*top;
    float farMinusNear = zNear-zFar;
    if(Math.abs(right) > 0.0001 && Math.abs(top) > 0.0001 && Math.abs(farMinusNear) > 0.0001){
      float[][] orthoMatrix = {{1/right-0.0001f, 0, 0, 0},
                               {0, 1/top-0.0001f, 0, 0},
                               {0, 0, -2/farMinusNear-0.0001f, -(zFar+zNear)/farMinusNear-0.0001f},
                               {0, 0, 0, 1}};
      return new Matrix(orthoMatrix);
          }
    else{
      System.out.println("ERROR: DIVISION BY ZERO");
      System.exit(1);
      return new Matrix();
    }
  }
}

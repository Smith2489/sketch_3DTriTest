public class MVP{
  private static float[] modelAngles = {0, 0, 0}; //Angles for Model matrix
  private static float[] modelPos = {0, 0, 0}; //Position of Model matrix
  private static float[] modelScale = {1, 1, 1}; //Scalling for Model matrix
  private static float[] eyePos = {0, 0, 0}; //View matrix position 
  private static float[] eyeScale = {1, 1, 1};
  private static float[] eyeAngles = {0, 0, 0};
  
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
    eyeScale[0] = scaleX;
    eyeScale[1] = scaleY;
    eyeScale[2] = scaleZ;
  }
  
  public static void setEyeScale(float[] scale){
    eyeScale[0] = scale[0];
    eyeScale[1] = scale[1];
    eyeScale[2] = scale[2];
  }
  
  public static void setEyeAngles(float alpha, float beta, float gamma){
    eyeAngles[0] = alpha;
    eyeAngles[1] = beta;
    eyeAngles[2] = gamma;
  }
  public static void setEyeAngls(float[] angles){
    eyeAngles[0] = angles[0];
    eyeAngles[1] = angles[1];
    eyeAngles[2] = angles[2];
  }
  
  //Rotation Matrix for Model (M)
  public static Matrix returnRotation(float alpha, float beta, float gamma){
    alpha = (float)((alpha*Math.PI)/180+0.0001);
    beta = (float)((beta*Math.PI)/180+0.0001);
    gamma = (float)((gamma*Math.PI)/180+0.0001);
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
    Matrix firstStep = new Matrix(MatrixOperations.matrixDotProduct(zRotation, yRotation));
    Matrix xRot = new Matrix(xRotation);
  
    return MatrixOperations.matrixDotProduct(firstStep, xRot);
  
  }

  //Translation Matrix for Model (M)
  public static Matrix returnTranslation(float x, float y, float z){
    float[][] translation = {{1, 0, 0, x},
                             {0, 1, 0, y},
                             {0, 0, 1, z},
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


  //Model Matrix (M)
  public static Matrix returnModel(){
    Matrix translate = returnTranslation(modelPos[0], modelPos[1], modelPos[2]);
    Matrix rotate = returnRotation(modelAngles[0], modelAngles[1], modelAngles[2]);
    Matrix scale = returnScale(modelScale[0], modelScale[1], modelScale[2]);
    Matrix step1 = new Matrix(MatrixOperations.matrixDotProduct(translate, rotate));
    return MatrixOperations.matrixDotProduct(step1, scale);
  }


  //Computing the view matrix (V)
  public static Matrix viewMatrix(){
    Matrix translate = returnTranslation(-eyePos[0], -eyePos[1], -eyePos[2]);
    Matrix rotate = returnRotation(-eyeAngles[0], -eyeAngles[1], -eyeAngles[2]);
    Matrix scale = returnScale(1/eyeScale[0], 1/eyeScale[1], 1/eyeScale[2]);
    Matrix step1 = new Matrix(MatrixOperations.matrixDotProduct(translate, rotate));
    return MatrixOperations.matrixDotProduct(step1, scale);
  }



  //Computing the perspective projection matrix (P)
  public static Matrix perspMatrix(float aspectRatio, float fovY, float zNear, float zFar){
    //fovY = ((float)(fovY*Math.PI))/180;
    float top = (float)(Math.tan(fovY/2)*Math.abs(zNear)+0.0001);
    //float bottom = -top;
    float right = (float)(aspectRatio*top+0.0001);
    //float left = -right;
  
    /*
    Insert into matrix if code is faulty
  
    2/(right-left) (goes in (0, 0)
    2/(top-bottom) (goes in (1, 1)
  
    -(top+bottom)/(top-bottom) (goes in (0, 3)
    -(right+left)/(right-left) (goes in (1, 3)
    */
    float[][] orthoMatrix1 = {{(float)(1/(right)+0.0001), 0, 0, 0},
                              {0, (float)(1/(top)+0.0001), 0, 0},
                              {0, 0, (float)(2/(zFar-zNear)+0.0001), (float)(-(zNear+zFar)/(zFar-zNear)+0.0001)},
                              {0, 0, 0, 1}};
                            
    //float[][] orthoMatrix2 = {{1, 0, 0, -(right+left)/2},
    //                          {0, 1, 0, -(top+bottom)/2},
    //                          {0, 0, 1, -(zFar+zNear)/2},
    //                          {0, 0, 0, 1}};
                            
    float[][] orthoToPerspMatrix = {{(float)(zFar+0.0001), 0, 0, 0},
                                    {0, (float)(zFar+0.0001), 0, 0},
                                    {0, 0, (float)(zFar+zNear+0.0001), (float)(-zNear*zFar+0.0001)},
                                    {0, 0, 1, 0}};
    Matrix ortho1 = new Matrix(orthoMatrix1);
    //Matrix ortho2 = new Matrix(orthoMatrix2);
    Matrix orthoToPersp = new Matrix(orthoToPerspMatrix);
    //Matrix orthoConstruct = MatrixOperations.matrixDotProduct(ortho1, ortho2); 
    //return ortho1;
    return MatrixOperations.matrixDotProduct(ortho1, orthoToPersp);
    //return output;
  }
}

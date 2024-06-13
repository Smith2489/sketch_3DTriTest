public void sortTriangles(Triangle[] triArray, int l, int r){
  if(l < r){
    int midPoint = l+((r-l) >>> 1);
    sortTriangles(triArray, l, midPoint);
    sortTriangles(triArray, midPoint+1, r);
    mergeTris(triArray, l, midPoint, r);
  }
}

public void mergeTris(Triangle[] triArray, int l, int m, int r){
  int leftSize = m-l+1;
  int rightSize = r-m;
  Triangle[] leftTris = new Triangle[leftSize];
  Triangle[] rightTris = new Triangle[rightSize];
  for(int i = 0; i < leftSize; i++){
      leftTris[i] = new Triangle();
      leftTris[i].copy(triArray[i+l]);
  }
  for(int i = 0; i < rightSize; i++){
     rightTris[i] = new Triangle();
     rightTris[i].copy(triArray[m+1+i]);
  }
  int i = 0, j = 0, k = l;
  while(i < leftSize && j < rightSize){
    float[] rightTriBary = new float[3];
    boolean[] isInsideTri = new boolean[3];
    for(int s = 0; s < 3; s++){
      float alpha = BaryCentricCoords.returnAlpha(rightTris[j].getVertices()[0][0], rightTris[j].getVertices()[0][1], rightTris[j].getVertices()[1][0], rightTris[j].getVertices()[1][1], rightTris[j].getVertices()[2][0], rightTris[j].getVertices()[2][1], leftTris[i].getVertices()[s][0], leftTris[i].getVertices()[s][1]);
      float beta = BaryCentricCoords.returnBeta(rightTris[j].getVertices()[0][0], rightTris[j].getVertices()[0][1], rightTris[j].getVertices()[1][0], rightTris[j].getVertices()[1][1], rightTris[j].getVertices()[2][0], rightTris[j].getVertices()[2][1], leftTris[i].getVertices()[s][0], leftTris[i].getVertices()[s][1]);
      float gamma = BaryCentricCoords.returnGamma(alpha, beta);
      isInsideTri[s] = alpha >= 0 && beta >= 0 && gamma >= 0;
      if(isInsideTri[s])
        rightTriBary[s] = (rightTris[j].getVertices()[0][2]*alpha)+(rightTris[j].getVertices()[1][2]*beta)+(rightTris[j].getVertices()[2][2]*gamma);
      else
        rightTriBary[s] = 0;
    }
    boolean isLess = (leftTris[i].getVertices()[0][2] <= rightTriBary[0] || !isInsideTri[0]) && (leftTris[i].getVertices()[1][2] <= rightTriBary[1] || !isInsideTri[1]) && (leftTris[i].getVertices()[2][2] <= rightTriBary[2] || !isInsideTri[2]);
    if(isLess && leftTris[i].getAverageZ() <= rightTris[j].getAverageZ()){
      triArray[k].copy(leftTris[i]);
      i++;
    }
    else{
      triArray[k].copy(rightTris[j]);
      j++;
    }
    k++;


  }
  while(i < leftSize){
    triArray[k].copy(leftTris[i]);
    i++;
    k++;
  }
  while(j < rightSize){
    triArray[k].copy(rightTris[j]);
    j++;
    k++;
  }
   
}

void drawTriangleList(PApplet screen, Model[] modelList, float fovY, float zNear, float zFar, float[] eyePos, float[] eyeScale, float[] eyeRotation){
  int totalTriCount = 0;
  for(int i = 0; i < modelList.length; i++)
    totalTriCount+=modelList[i].returnPolygonCount();
  Triangle[] triangleList = new Triangle[2048];
  Triangle[] displayList = new Triangle[2048];
  float f1 = (zFar - zNear)/2;
  float f2 = (zFar + zNear)/2;
  TriangleRasterizer.initBuffers();
  MVP.setEyePos(0, 0, 0);
  int index = 0;
  int numberOfTris = 0;
  int totalWorkedTris = 0;
  MVP.setEyeAngles(eyeRotation[0], eyeRotation[1], eyeRotation[2]);
  MVP.setEyePos(eyePos[0], eyePos[1], eyePos[2]);
  MVP.setEyeScale(eyeScale[0], eyeScale[1], eyeScale[2]);
  MVP.setModelAngles(modelList[0].returnAngle()[0], modelList[0].returnAngle()[1], modelList[0].returnAngle()[2]);
  MVP.setModelScale(modelList[0].returnScale()[0], modelList[0].returnScale()[1], modelList[0].returnScale()[2]);
  MVP.setModelPos(modelList[0].returnPosition()[0], modelList[0].returnPosition()[1], modelList[0].returnPosition()[2]);
  
  Matrix mvp = MatrixOperations.matrixDotProduct(MVP.perspMatrix((float)screen.width/screen.height, fovY, zNear, zFar), MVP.viewMatrix());
  Matrix mvpFull = MatrixOperations.matrixDotProduct(mvp, MVP.returnModel());
 // System.out.println(totalTriCount);
  for(int i = 0; i < totalTriCount; i++){

    triangleList[i] = new Triangle();
    displayList[i] = new Triangle();
    if(index < modelList.length && i >= totalWorkedTris+modelList[index].returnPolygonCount()){
      totalWorkedTris+=modelList[index].returnPolygonCount();
      index++;
      MVP.setModelAngles(modelList[index].returnAngle()[0], modelList[index].returnAngle()[1], modelList[index].returnAngle()[2]);
      MVP.setModelScale(modelList[index].returnScale()[0], modelList[index].returnScale()[1], modelList[index].returnScale()[2]);
      MVP.setModelPos(modelList[index].returnPosition()[0], modelList[index].returnPosition()[1], modelList[index].returnPosition()[2]);
      mvpFull = MatrixOperations.matrixDotProduct(mvp, MVP.returnModel());
    }
    //System.out.println(i-totalWorkedTris);
    float[][] points = new float[3][4];
    boolean isBehindCamera = true;
    for(int j = 0; j < 3; j++){
      Matrix projection = MatrixOperations.matrixDotProduct(mvpFull, MatrixOperations.transposeMatrix(VectorOperations.from3DVecTo4DVec(modelList[index].returnPolygons()[i-totalWorkedTris][j], 1.0)));
      points[j][0] = projection.returnData(0, 0);
      points[j][1] = projection.returnData(1, 0);
      points[j][2] = projection.returnData(2, 0);
      points[j][3] = projection.returnData(3, 0);
      if(points[j][3] != 0){
        points[j][0] = (float)(points[j][0]/points[j][3]+0.0001);
        points[j][1] = (float)(points[j][1]/points[j][3]+0.0001);
        points[j][2] = (float)(points[j][2]/points[j][3]+0.0001);
        points[j][3]/=points[j][3];
      }
      points[j][0] = 0.5*screen.width*(points[j][0]+1)+0.0001;
      points[j][1] = 0.5*screen.height*(points[j][1]+1)+0.0001;
      points[j][2] = points[j][2]*f1 + f2+0.0001;
      //System.out.println(i+" "+j+" "+points[j][2]);
      isBehindCamera&=(points[j][2] <= f1);
    }
    if(!isBehindCamera){
      triangleList[numberOfTris] = new Triangle(points, modelList[index].returnColours()[i-totalWorkedTris][0], modelList[index].returnColours()[i-totalWorkedTris][1], modelList[index].returnHasStroke(), modelList[index].returnHasFill());
      numberOfTris++;
    }
  }
  for(int i = 0; i < numberOfTris && i < displayList.length && i < 2048; i++)
    displayList[i].copy(triangleList[i]);
  //sortTriangles(displayList, 0, triangleList.length-1);

  for(int i = 0; i < numberOfTris; i++){
     
     //boolean[] vertIn = new boolean[13];
     //float[][] quarterCoords = new float[9][3];
     //for(int j = 0; j < 3; j++){
     //  quarterCoords[0][j] = (3*displayList[i].getVertices()[0][j]+displayList[i].getVertices()[1][j])/4;
     //  quarterCoords[1][j] = (displayList[i].getVertices()[0][j]+displayList[i].getVertices()[1][j])/2;
     //  quarterCoords[2][j] = ((displayList[i].getVertices()[0][j]+3*displayList[i].getVertices()[1][j]))/4;
     //  quarterCoords[3][j] = (3*displayList[i].getVertices()[1][j]+displayList[i].getVertices()[2][j])/4;
     //  quarterCoords[4][j] = (displayList[i].getVertices()[1][j]+displayList[i].getVertices()[2][j])/2;
     //  quarterCoords[5][j] = ((displayList[i].getVertices()[1][j]+3*displayList[i].getVertices()[2][j]))/4;
     //  quarterCoords[6][j] = (3*displayList[i].getVertices()[2][j]+displayList[i].getVertices()[1][j])/4;
     //  quarterCoords[7][j] = (displayList[i].getVertices()[2][j]+displayList[i].getVertices()[1][j])/2;
     //  quarterCoords[8][j] = ((displayList[i].getVertices()[2][j]+3*displayList[i].getVertices()[1][j]))/4;
     //}
     //for(int j = 0; j < 3; j++){
     //  int zBuffCoord = screen.width*(int)displayList[i].getVertices()[j][1]+(int)displayList[i].getVertices()[j][0];
     //  if(zBuffCoord >= 0 && zBuffCoord < 480000)
     //     vertIn[j] = displayList[i].getVertices()[j][2] > TriangleRasterizer.returnDepthBuffer()[zBuffCoord] || Float.isNaN(TriangleRasterizer.returnDepthBuffer()[zBuffCoord]);
     //}
     //for(int j = 0; j < 6; j++){
     //   int zBuffCoord = screen.width*(int)quarterCoords[j][1]+(int)quarterCoords[j][0];
     //  if(zBuffCoord >= 0 && zBuffCoord < 480000)
     //     vertIn[j+4] = quarterCoords[j][2] > TriangleRasterizer.returnDepthBuffer()[zBuffCoord] || Float.isNaN(TriangleRasterizer.returnDepthBuffer()[zBuffCoord]);
     //}
     //int zBuffCoord = screen.width*(int)triangleList[i].getAverageY()+(int)triangleList[i].getAverageX();

     //if(zBuffCoord >= 0 && zBuffCoord < 480000)
     //  vertIn[3] = displayList[i].getAverageZ() > TriangleRasterizer.returnDepthBuffer()[zBuffCoord] || Float.isNaN(TriangleRasterizer.returnDepthBuffer()[zBuffCoord]);
     //boolean isIn = false;
     //for(int j = 0; j < vertIn.length; j++){
     //  isIn|=vertIn[j];
     //}
     //if(isIn){
       TriangleRasterizer.draw(displayList[i]);

     //}
  }
  screen.loadPixels();
  for(int i = 0; i < screen.width; i++)
    for(int j = 0; j < screen.height; j++)
      screen.pixels[j*screen.width+i] = TriangleRasterizer.returnFrame()[j*screen.width+i];
  screen.updatePixels();
}

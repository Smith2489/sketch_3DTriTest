public class MatrixOperations{
  public static Matrix matrixDotProduct(Matrix matrix1, Matrix matrix2){
    if(matrix2.returnHeight() == matrix1.returnWidth()){
      float[][] outputMatrix = new float[matrix1.returnHeight()][matrix2.returnWidth()];
      for(int i = 0; i < matrix1.returnHeight(); i++){
        for(int j = 0; j < matrix2.returnWidth(); j++){
          for(int p = 0; p < matrix1.returnWidth(); p++)
            outputMatrix[i][j]+=matrix1.returnData(i, p)*matrix2.returnData(p, j);
        }
      }
      return new Matrix(outputMatrix);
    }
    else{
      System.out.println("ERROR: NUMBER OF COLUMNS IN MATRIX 2 DOES NOT EQUAL THE NUMBER OF ROWS IN MATRIX 1!");
      return new Matrix();
    }
  }

  public static Matrix matrixAddition(Matrix matrix1, Matrix matrix2){
    if(matrix1.returnWidth() == matrix2.returnWidth() && matrix1.returnHeight() == matrix2.returnHeight()){
      float[][] outputMatrix = new float[matrix1.returnHeight()][matrix1.returnWidth()];
      for(int i = 0; i < matrix1.returnHeight(); i++){
        for(int j = 0; j < matrix1.returnWidth(); j++)
          outputMatrix[i][j] = matrix1.returnData(i, j)+matrix2.returnData(i, j);
      }
      return new Matrix(outputMatrix);
    }
    else{
      System.out.println("ERROR: DIMENTIONS MUST MATCH");
      return new Matrix();
    }
  }

  public static Matrix matrixSubtraction(Matrix matrix1, Matrix matrix2){
    if(matrix1.returnWidth() == matrix2.returnWidth() && matrix1.returnHeight() == matrix2.returnHeight()){
      float[][] outputMatrix = new float[matrix1.returnHeight()][matrix1.returnWidth()];
      for(int i = 0; i < matrix1.returnHeight(); i++){
        for(int j = 0; j < matrix1.returnWidth(); j++)
          outputMatrix[i][j] = matrix1.returnData(i, j)-matrix2.returnData(i, j);
      }
      return new Matrix(outputMatrix);
    }
    else{
      System.out.println("ERROR: DIMENTIONS MUST MATCH");
      return new Matrix();
    }
  }

  public static Matrix transposeMatrix(Matrix matrix){
    float[][] outputMatrix = new float[matrix.returnWidth()][matrix.returnHeight()];
    for(int i = 0; i < matrix.returnHeight(); i++){
       for(int j = 0; j < matrix.returnWidth(); j++){
         outputMatrix[j][i] = matrix.returnData(i, j);
       }
    }
    return new Matrix(outputMatrix);
  }
  
  public static Matrix multByConst(Matrix matrix, final float k){
    float[][] outputMatrix = new float[matrix.returnHeight()][matrix.returnWidth()];
    for(int i = 0; i < matrix.returnHeight(); i++){
      for(int j = 0; j < matrix.returnWidth(); j++)
        outputMatrix[i][j] = matrix.returnData(i, j)*k;
    }
    return new Matrix(outputMatrix);
  }
  
  public static Matrix transposeMatrix(float[] matrix){
    float[][] outputMatrix = new float[matrix.length][1];
    for(int i = 0; i < matrix.length; i++)
      outputMatrix[i][0] = matrix[i];
    return new Matrix(outputMatrix);
  }
  

  
  public static float[][] matrixDotProduct(float[][] matrix1, float[][] matrix2){
    if(matrix2.length == matrix1[0].length){
      float[][] outputMatrix = new float[matrix1.length][matrix2[0].length];
      for(int i = 0; i < matrix1.length; i++){
        for(int j = 0; j < matrix2[0].length; j++){
          for(int p = 0; p < matrix1[0].length; p++)
            outputMatrix[i][j]+=matrix1[i][p]*matrix2[p][j];
        }
      }
      return outputMatrix;
    }
    else{
      System.out.println("ERROR: NUMBER OF COLUMNS IN MATRIX 2 DOES NOT EQUAL THE NUMBER OF ROWS IN MATRIX 1!");
      float[][] outputMatrix = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
      return outputMatrix;
    }
  }
  public static float[][] matrixAddition(float[][] matrix1, float[][] matrix2){
    if(matrix1[0].length == matrix2[0].length && matrix1.length == matrix2.length){
      float[][] outputMatrix = new float[matrix1.length][matrix1[0].length];
      for(int i = 0; i < matrix1.length; i++){
        for(int j = 0; j < matrix1[0].length; j++)
          outputMatrix[i][j] = matrix1[i][j]+matrix2[i][j];
      }
      return outputMatrix;
    }
    else{
      System.out.println("ERROR: DIMENTIONS MUST MATCH");
      float[][] outputMatrix = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
      return outputMatrix;
    }
  }
  public static float[][] matrixSubtraction(float[][] matrix1, float[][] matrix2){
    if(matrix1[0].length == matrix2[0].length && matrix1.length == matrix2.length){
      float[][] outputMatrix = new float[matrix1.length][matrix1[0].length];
      for(int i = 0; i < matrix1.length; i++){
        for(int j = 0; j < matrix1[0].length; j++)
          outputMatrix[i][j] = matrix1[i][j]-matrix2[i][j];
      }
      return outputMatrix;
    }
    else{
      System.out.println("ERROR: DIMENTIONS MUST MATCH");
      float[][] outputMatrix = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
      return outputMatrix;
    }
  }
  public static float[][] transposeMatrix(float[][] matrix){
    float[][] outputMatrix = new float[matrix[0].length][matrix.length];
    for(int i = 0; i < matrix.length; i++){
       for(int j = 0; j < matrix[0].length; j++){
         outputMatrix[j][i] = matrix[i][j];
       }
    }
    return outputMatrix;
  }
  public static float[][] multByConst(float[][] matrix, final float k){
    float[][] outputMatrix = new float[matrix.length][matrix[0].length];
    for(int i = 0; i < matrix.length; i++){
      for(int j = 0; j < matrix[0].length; j++)
        outputMatrix[i][j] = matrix[i][j]*k;
    }
    return outputMatrix;
  }
}

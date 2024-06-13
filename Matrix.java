public class Matrix{
  private float[][] matrix = new float[4][4];
  public Matrix(){
    matrix = new float[4][4];
    for(int i = 0; i < 4; i++){
      matrix[i][i] = 1;
    }
  }
  public Matrix(float[][] newMatrix){
    matrix = new float[newMatrix.length][newMatrix[0].length];
    for(int i = 0; i < newMatrix.length; i++){
       for(int j = 0; j < newMatrix[0].length; j++)
         matrix[i][j] = newMatrix[i][j];
    }
  }
  public Matrix(float[] newMatrix){
    matrix = new float[1][newMatrix.length];
    for(int i = 0; i < newMatrix.length; i++){
      matrix[0][i] = newMatrix[i];
    }
  }
  public Matrix(Matrix newMatrix){
      matrix = new float[newMatrix.returnHeight()][newMatrix.returnWidth()];
      for(int i = 0; i < matrix.length; i++){
         for(int j = 0; j < matrix[0].length; j++){
           matrix[i][j] = newMatrix.returnData(i, j);
         }
      }
  }

  public void setData(float newData, int row, int col){
    matrix[col][row] = newData;
  }

  public void copy(Matrix otherMatrix){
      matrix = new float[otherMatrix.returnHeight()][otherMatrix.returnWidth()];
      for(int i = 0; i < matrix.length; i++){
         for(int j = 0; j < matrix[0].length; j++){
           matrix[i][j] = otherMatrix.returnData(i, j);
         }
      }
  }

  public void copy(float[][] otherMatrix){
    matrix = new float[otherMatrix.length][otherMatrix[0].length];
    for(int i = 0; i < matrix.length; i++){
       for(int j = 0; j < matrix[0].length; j++){
         matrix[i][j] = otherMatrix[i][j];
       }
    }
  }

  public void copy(float[] otherMatrix){
    matrix = new float[1][otherMatrix.length];
    for(int i = 0; i < otherMatrix.length; i++){
      matrix[0][i] = otherMatrix[i];
    }
}

  public String toString(){
    String matrixString = "";
    for(int i = 0; i < matrix.length; i++){
       for(int j = 0; j < matrix[0].length; j++)
         matrixString+=matrix[i][j]+"  ";
       matrixString+="\n";
    }
    return matrixString;
  }


  public int returnWidth(){
    return matrix[0].length;
  }
  public int returnHeight(){
    return matrix.length;
  }
  public float returnData(int row, int col){
    return matrix[row][col];
  }

  public boolean isEqual(Matrix otherMatrix){
    if(otherMatrix.returnWidth() != matrix[0].length || otherMatrix.returnHeight() != matrix.length)
      return false;
    for(int i = 0; i < otherMatrix.returnHeight(); i++){
      for(int j = 0; j < otherMatrix.returnWidth(); j++){
        if(otherMatrix.returnData(i, j) != matrix[i][j])
          return false;
      }
    }
    return true;
  }
  public boolean isEqual(float[][] otherMatrix){
    if(otherMatrix[0].length != matrix[0].length || otherMatrix.length != matrix.length)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      for(int j = 0; j < otherMatrix[0].length; j++){
        if(otherMatrix[i][j] != matrix[i][j])
          return false;
      }
    }
    return true;
  }
  public boolean isEqual(float[] otherMatrix){
    if(otherMatrix.length != matrix[0].length || matrix.length > 1)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      if(otherMatrix[i] != matrix[0][i])
        return false;
    }
    return true;
  }
  public boolean isTransposition(Matrix otherMatrix){
    if(otherMatrix.returnWidth() != matrix.length || otherMatrix.returnHeight() != matrix[0].length)
      return false;
    for(int i = 0; i < otherMatrix.returnHeight(); i++){
      for(int j = 0; j < otherMatrix.returnWidth(); j++){
        if(otherMatrix.returnData(i, j) != matrix[j][i])
          return false;
       }
    }
    return true;
  }
  public boolean isTransposition(float[][] otherMatrix){
    if(otherMatrix[0].length != matrix.length || otherMatrix.length != matrix[0].length)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      for(int j = 0; j < otherMatrix[0].length; j++){
        if(otherMatrix[i][j] != matrix[j][i])
          return false;
      }
    }
    return true;
  }
  public boolean isTransposition(float[] otherMatrix){
    if(matrix[0].length > 1 || otherMatrix.length != matrix.length)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      if(otherMatrix[i] != matrix[i][0])
        return false;
    }
    return true;
  }
}

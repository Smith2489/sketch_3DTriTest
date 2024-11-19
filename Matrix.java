//Class for abstracting away 2D arrays as matrices
public class Matrix{
  private float[][] matrix = new float[4][4];
  //Default constructor
  public Matrix(){
    matrix = new float[4][4];
    for(int i = 0; i < 4; i++){
      matrix[i][i] = 1;
    }
  }
  //Constructor with 2D array of sizes x*y
  public Matrix(float[][] newMatrix){
    matrix = new float[newMatrix.length][newMatrix[0].length];
    for(int i = 0; i < newMatrix.length; i++){
       for(int j = 0; j < newMatrix[0].length; j++)
         matrix[i][j] = newMatrix[i][j];
    }
  }
  //Constructor with 1D array cast to a 1*x size 2D array
  public Matrix(float[] newMatrix){
    matrix = new float[1][newMatrix.length];
    for(int i = 0; i < newMatrix.length; i++){
      matrix[0][i] = newMatrix[i];
    }
  }
  //Constructor with anther matrix
  public Matrix(Matrix newMatrix){
      matrix = new float[newMatrix.returnHeight()][newMatrix.returnWidth()];
      for(int i = 0; i < matrix.length; i++){
         for(int j = 0; j < matrix[0].length; j++){
           matrix[i][j] = newMatrix.returnData(i, j);
         }
      }
  }
  //Sets the data in the matrix at a specific spot
  public void setData(float newData, int row, int col){
    matrix[col][row] = newData;
  }
  
  //Copies one matrix's data into another
  public void copy(Matrix otherMatrix){
      matrix = new float[otherMatrix.returnHeight()][otherMatrix.returnWidth()];
      for(int i = 0; i < matrix.length; i++){
         for(int j = 0; j < matrix[0].length; j++){
           matrix[i][j] = otherMatrix.returnData(i, j);
         }
      }
  }
  //Copies a 2D array's data into a matrix
  public void copy(float[][] otherMatrix){
    matrix = new float[otherMatrix.length][otherMatrix[0].length];
    for(int i = 0; i < matrix.length; i++){
       for(int j = 0; j < matrix[0].length; j++){
         matrix[i][j] = otherMatrix[i][j];
       }
    }
  }
  //Copies a 1D array into a matrix
  public void copy(float[] otherMatrix){
    matrix = new float[1][otherMatrix.length];
    for(int i = 0; i < otherMatrix.length; i++){
      matrix[0][i] = otherMatrix[i];
    }
  }
  
  //Casts the matrix to a string
  public String toString(){
    String matrixString = "";
    for(int i = 0; i < matrix.length; i++){
       for(int j = 0; j < matrix[0].length; j++)
         matrixString+=matrix[i][j]+"  ";
       matrixString+="\n";
    }
    return matrixString;
  }

  //Returns the width of the matrix
  public int returnWidth(){
    return matrix[0].length;
  }
  //Returns the height of the matrix
  public int returnHeight(){
    return matrix.length;
  }
  //Returns the data at a specific index in the matrix
  public float returnData(int row, int col){
    return matrix[row][col];
  }
  public float[][] toFloat(){
    float[][] output = new float[matrix.length][matrix[0].length];
    for(int i = 0; i < matrix.length; i++){
      for(int j = 0; j < matrix[0].length; j++)
        output[i][j] = matrix[i][j];
    }
    return output;
  }
  //Checks if the matrix is the same as another matrix
  public boolean equals(Matrix otherMatrix){
    if(otherMatrix.returnWidth() != matrix[0].length || otherMatrix.returnHeight() != matrix.length)
      return false;
    for(int i = 0; i < otherMatrix.returnHeight(); i++){
      for(int j = 0; j < otherMatrix.returnWidth(); j++){
        if(Math.abs(otherMatrix.returnData(i, j) - matrix[i][j]) > 0.0001)
          return false;
      }
    }
    return true;
  }
  //Checks if the matrix is the same as a 2D array
  public boolean equals(float[][] otherMatrix){
    if(otherMatrix[0].length != matrix[0].length || otherMatrix.length != matrix.length)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      for(int j = 0; j < otherMatrix[0].length; j++){
        if(Math.abs(otherMatrix[i][j] - matrix[i][j]) > 0.0001)
          return false;
      }
    }
    return true;
  }
  
  //Checks if the matrix holds the same data as a 1D array
  public boolean equals(float[] otherMatrix){
    if(otherMatrix.length != matrix[0].length || matrix.length > 1)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      if(Math.abs(otherMatrix[i] - matrix[0][i]) > 0.0001)
        return false;
    }
    return true;
  }
  //Checks if the matrix is the transposition of another matrix
  public boolean isTransposition(Matrix otherMatrix){
    if(otherMatrix.returnWidth() != matrix.length || otherMatrix.returnHeight() != matrix[0].length)
      return false;
    for(int i = 0; i < otherMatrix.returnHeight(); i++){
      for(int j = 0; j < otherMatrix.returnWidth(); j++){
        if(Math.abs(otherMatrix.returnData(i, j) - matrix[j][i]) > 0.0001)
          return false;
       }
    }
    return true;
  }
  //Checks if the matrix is the transposition of a 2D array
  public boolean isTransposition(float[][] otherMatrix){
    if(otherMatrix[0].length != matrix.length || otherMatrix.length != matrix[0].length)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      for(int j = 0; j < otherMatrix[0].length; j++){
        if(Math.abs(otherMatrix[i][j] - matrix[j][i]) > 0.0001)
          return false;
      }
    }
    return true;
  }
  //Checks if the matrix is the transposition of a 1D array
  public boolean isTransposition(float[] vector){
    if(matrix[0].length > 1 || vector.length != matrix.length)
      return false;
    for(int i = 0; i < vector.length; i++){
      if(Math.abs(vector[i] - matrix[i][0]) > 0.0001)
        return false;
    }
    return true;
  }
  //Checks if a matrix is an identity matrix
  public boolean isIdentity(){
    if(matrix.length != matrix[0].length)
      return false;
    for(int i = 0; i < matrix.length; i++)
      for(int j = 0; j < matrix[0].length; j++)
        if((j == i && Math.abs(matrix[i][j]-1) > 0.0001) || (j != i && Math.abs(matrix[i][j]) > 0.0001))
          return false;
    return true;
  }
  //Checks if a matrix is symmetric
  public boolean isSymmetric(){
    if(matrix[0].length != matrix.length)
      return false;
    for(int i = 0; i < matrix.length; i++)
      for(int j = 0; j < matrix[0].length; j++)
        if(Math.abs(matrix[j][i] - matrix[i][j]) > 0.0001)
          return false;
    return true;
  }
}

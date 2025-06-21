package Maths.LinearAlgebra;
//Class for abstracting away 2D arrays as matrices
public class Matrix{
  protected float[] matrix = new float[16];
  protected int width = 4;
  protected int height = 4;
  //Default constructor
  public Matrix(){
    matrix = new float[16];
    matrix[0] = 1;
    matrix[5] = 1;
    matrix[10] = 1;
    matrix[15] = 1;
  }

  public Matrix(int wid, int heig){
    width = wid;
    height = heig;
    matrix = new float[width*height];
    if(width == height){
      for(int i = 0; i < width; i++)
        matrix[i*width+i] = 1;
    }
  }

  //Constructor with 2D array of sizes x*y
  public Matrix(float[][] newMatrix){
    width = newMatrix[0].length;
    height = newMatrix.length;
    matrix = new float[width*height];
    for(int i = 0; i < height; i++){
       for(int j = 0; j < width; j++)
         matrix[j+width*i] = newMatrix[i][j];
    }
  }
  //Constructor with 1D array cast to a 1*x size 2D array
  public Matrix(float[] newMatrix){
    width = 1;
    height = newMatrix.length;
    matrix = new float[height];
    for(int i = 0; i < newMatrix.length; i++){
      matrix[i] = newMatrix[i];
    }
  }
  //Constructor with anther matrix
  public Matrix(Matrix newMatrix){
    width = newMatrix.width;
    height = newMatrix.height;
      matrix = new float[width*height];
      for(int i = 0; i < height; i++){
         for(int j = 0; j < width; j++){
           matrix[j+width*i] = newMatrix.matrix[j+width*i];
         }
      }
  }
  //Sets the data in the matrix at a specific spot
  public void setData(float newData, int row, int col){
    matrix[col+width*row] = newData;
  }

  //Copies one matrix's data into another
  public void copy(Object o){
    if(o instanceof Matrix){
      Matrix otherMatrix = (Matrix)o;
      width = otherMatrix.width;
      height = otherMatrix.height;
      matrix = new float[height*width];
      for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
          matrix[j+width*i] = otherMatrix.matrix[j+width*i];
        }
      }
    }
  }
  public void copy(Matrix otherMatrix){
    width = otherMatrix.width;
    height = otherMatrix.height;
    matrix = new float[height*width];
    for(int i = 0; i < height; i++){
      for(int j = 0; j < width; j++){
        matrix[j+width*i] = otherMatrix.matrix[j+width*i];
      }
    }
  }
  //Copies a 2D array's data into a matrix
  public void copy(float[][] otherMatrix){
    height = otherMatrix.length;
    width = otherMatrix[0].length;
    matrix = new float[height*width];
    for(int i = 0; i < height; i++){
       for(int j = 0; j < width; j++){
         matrix[j+width*i] = otherMatrix[i][j];
       }
    }
  }
  //Copies a 1D array into a matrix
  public void copy(float[] otherMatrix){
    width = 1;
    height = otherMatrix.length;
    matrix = new float[width];
    for(int i = 0; i < otherMatrix.length; i++){
      matrix[i] = otherMatrix[i];
    }
  }
  
  //Casts the matrix to a string
  public String toString(){
    String matrixString = "";
    for(int i = 0; i < height; i++){
       for(int j = 0; j < width; j++)
         matrixString+=matrix[j+width*i]+"  ";
       matrixString+="\n";
    }
    return matrixString;
  }

  //Returns the width of the matrix
  public int returnWidth(){
    return width;
  }
  //Returns the height of the matrix
  public int returnHeight(){
    return height;
  }
  //Returns the data at a specific index in the matrix
  public float returnData(int row, int col){
    return matrix[row*width+col];
  }

  public float[] returnCol(int col){
    float[] returnCol = new float[height];
    for(int i = 0; i < height; i++)
      returnCol[i] = matrix[i*width+col];
    return returnCol;
  }

  public float[] returnRow(int row){
    float[] returnRow = new float[width];
    for(int i = 0; i < width; i++)
      returnRow[i] = matrix[row*width+i];
    return returnRow;
  }

  public float[][] toFloat(){
    float[][] output = new float[height][width];
    for(int i = 0; i < height; i++){
      for(int j = 0; j < width; j++)
        output[i][j] = matrix[i*width+j];
    }
    return output;
  }
  public float[] toFloat1D(){
    float[] output = new float[matrix.length];
    for(int i = 0; i < matrix.length; i++)
      output[i] = matrix[i];
    return output;

  }
  //Checks if the matrix is the same as another matrix
  public boolean equals(Object o){
    if(o instanceof Matrix){
      Matrix otherMatrix = (Matrix)o;
      if(otherMatrix.width != width || otherMatrix.height != height)
        return false;
      for(int i = 0; i < otherMatrix.height; i++){
        for(int j = 0; j < otherMatrix.width; j++){
          if(Math.abs(otherMatrix.matrix[i*width+j] - matrix[i*width+j]) > 0.0001)
            return false;
        }
      }
      return true;
    }
    else
      return false;
  }
  public boolean equals(Matrix otherMatrix){
    if(otherMatrix.width != width || otherMatrix.height != height)
      return false;
    for(int i = 0; i < otherMatrix.height; i++){
      for(int j = 0; j < otherMatrix.width; j++){
        if(Math.abs(otherMatrix.matrix[i*width+j] - matrix[i*width+j]) > 0.0001)
          return false;
      }
    }
    return true;
  }
  //Checks if the matrix is the same as a 2D array
  public boolean equals(float[][] otherMatrix){
    if(otherMatrix[0].length != width || otherMatrix.length != height)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      for(int j = 0; j < otherMatrix[0].length; j++){
        if(Math.abs(otherMatrix[i][j] - matrix[i*width+j]) > 0.0001)
          return false;
      }
    }
    return true;
  }
  
  //Checks if the matrix holds the same data as a 1D array
  public boolean equals(float[] otherMatrix){
    if(otherMatrix.length != height || matrix.length > 1)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      if(Math.abs(otherMatrix[i] - matrix[i]) > 0.0001)
        return false;
    }
    return true;
  }
  //Checks if the matrix is the transposition of another matrix
  public boolean isTransposition(Matrix otherMatrix){
    if(otherMatrix.width != height || otherMatrix.height != width)
      return false;
    for(int i = 0; i < otherMatrix.height; i++){
      for(int j = 0; j < otherMatrix.width; j++){
        if(Math.abs(otherMatrix.matrix[i*otherMatrix.width+j] - matrix[j*width+i]) > 0.0001)
          return false;
       }
    }
    return true;
  }
  //Checks if the matrix is the transposition of a 2D array
  public boolean isTransposition(float[][] otherMatrix){
    if(otherMatrix[0].length != height || otherMatrix.length != width)
      return false;
    for(int i = 0; i < otherMatrix.length; i++){
      for(int j = 0; j < otherMatrix[0].length; j++){
        if(Math.abs(otherMatrix[i][j] - matrix[j*width+i]) > 0.0001)
          return false;
      }
    }
    return true;
  }
  //Checks if the matrix is the transposition of a 1D array
  public boolean isTransposition(float[] vector){
    if(width > 1 || vector.length != height)
      return false;
    for(int i = 0; i < vector.length; i++){
      if(Math.abs(vector[i] - matrix[i]) > 0.0001)
        return false;
    }
    return true;
  }
  //Checks if a matrix is an identity matrix
  public boolean isIdentity(){
    if(width != height)
      return false;
    for(int i = 0; i < height; i++)
      for(int j = 0; j < width; j++)
        if((j == i && Math.abs(matrix[i+width*j]-1) > 0.0001) || (j != i && Math.abs(matrix[i+width*j]) > 0.0001))
          return false;
    return true;
  }
  //Checks if a matrix is symmetric
  public boolean isSymmetric(){
    if(width != height)
      return false;
    for(int i = 0; i < height; i++)
      for(int j = 0; j < width; j++)
        if(Math.abs(matrix[j+width*i] - matrix[i+width*j]) > 0.0001)
          return false;
    return true;
  }
}
package Maths.LinearAlgebra;
//A class for defining functions that can be performed on matrices
public class MatrixOperations{
  private static final float[] POWS_10 = {0.0000000000000001f, 0.000000000000001f, 0.00000000000001f, 0.0000000000001f, 0.000000000001f,
                                          0.00000000001f, 0.0000000001f, 0.000000001f, 0.00000001f, 0.0000001f, 0.000001f,
                                          0.00001f, 0.0001f, 0.001f, 0.01f, 0.1f, 1, 10, 100, 1000, 10000, 100000, 1000000,
                                          10000000, 100000000, 1000000000, 10000000000f, 100000000000f, 1000000000000f,
                                          10000000000000f, 100000000000000f, 1000000000000000f, 10000000000000000f};
  private static final int ABS_POW = POWS_10.length >>> 1;
  private static float[] sigFigs = {100000, 0.00001f};
  private static boolean round = true;
  public static void setSigFigs(int pow10){
    if(pow10 >= -ABS_POW && pow10 <= ABS_POW){
      sigFigs[0] = POWS_10[ABS_POW+pow10];
      sigFigs[1] = POWS_10[ABS_POW-pow10];
    }
    else{
      System.out.println("ERROR: POWER "+pow10+" OUT OF RANGE");
      System.exit(1);
    }
  }

  public static void enableRound(boolean enable){
    round = enable;
  }

  //Takes two matrices where wid1 = heigh2 and performs a matrix operation on them
  public static Matrix matrixMultiply(Matrix matrix1, Matrix matrix2){
    if(matrix2.returnHeight() == matrix1.returnWidth()){
      float[][] outputMatrix = new float[matrix1.returnHeight()][matrix2.returnWidth()];
      for(int i = 0; i < matrix1.returnHeight(); i++){
        for(int j = 0; j < matrix2.returnWidth(); j++){
          for(int p = 0; p < matrix1.returnWidth(); p++)
            outputMatrix[i][j]+=matrix1.returnData(i, p)*matrix2.returnData(p, j);
          if(round)
            outputMatrix[i][j] = (float)Math.round(outputMatrix[i][j]*sigFigs[0])*sigFigs[1];
        }
      }
      return new Matrix(outputMatrix);
    }
    System.out.println("ERROR: NUMBER OF COLUMNS IN MATRIX 2 DOES NOT EQUAL THE NUMBER OF ROWS IN MATRIX 1!");
    System.exit(1);
    return new Matrix();
  }

  //Computes the inverse of a square matrix
  public static Matrix invertMatrix(Matrix matrix1){
    if(matrix1.returnWidth() == matrix1.returnHeight()){
      float determinant = calcDeterminant(matrix1);
      if(Math.abs(determinant) > sigFigs[1]){
        Matrix adjoint = getAdjoint(matrix1);
        adjoint.copy(divByConst(adjoint, determinant));
        return adjoint;
      }
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    System.out.println("ERROR: MATRIX IS NOT A SQUARE");
    System.exit(1);
    return new Matrix();
  }

  //Returns a new matrix that is the adjoint of the input matrix
  public static Matrix getAdjoint(Matrix matrix){
    if(matrix.returnWidth() == matrix.returnHeight()){
      float[][] newMatrix = new float[matrix.returnHeight()][matrix.returnHeight()];
      for(int i = 0; i < matrix.returnHeight(); i++)
        for(int j = 0; j < matrix.returnHeight(); j++)
          newMatrix[i][j] = getCofactor(matrix, i, j);
      return new Matrix(newMatrix);
    }
    System.out.println("ERROR: MATRIX IS NOT SQUARE");
    System.exit(1);
    return new Matrix();
  }

  //Computes the cofactor of an element in a square matrix
  public static float getCofactor(Matrix matrix, int row, int col){
    if(matrix.returnHeight() == matrix.returnWidth())
      return (1 - (((row+col) & 1) << 1))*calcDeterminant(getMinourMatrix(matrix, row, col));
    System.out.println("ERROR: MATRIX IS NOT A SQUARE");
    System.exit(1);
    return -1;
  }
  
  //Computes the determinant of a square matrix
  public static float calcDeterminant(Matrix matrix){
    if(matrix.returnHeight() == matrix.returnWidth()){
      //Base cases
      switch(matrix.returnWidth()){
        case 1:
          return matrix.returnData(0, 0);
        case 2:
          return matrix.returnData(0, 0)*matrix.returnData(1, 1)-matrix.returnData(0, 1)*matrix.returnData(1, 0);
        }
        //Recursive case
        float determinant = 0;
        byte sign = 1;
        for(int i = 0; i < matrix.returnWidth(); i++){
          determinant+=sign*matrix.returnData(0, i)*calcDeterminant(getMinourMatrix(matrix, i, 0));
          sign = (byte)((~sign)+1);
        }
        return determinant;
    }
    System.out.println("ERROR: MATRIX IS NOT A SQUARE");
    System.exit(1);
    return -1;
  }

  //Creates a sub matrix for an element by deleting elements the occupy the same row or column as the element
  public static Matrix getMinourMatrix(Matrix matrix, int rowNum, int colNum){
    float[][] subMatrix = new float[matrix.returnWidth()-1][matrix.returnHeight()-1];
    int tempJ = 0;
    for(int j = 0; j < matrix.returnWidth(); j++){
      if(j != colNum){
        int tempK = 0;
        for(int k = 0; k < matrix.returnWidth(); k++){
          if(k != rowNum){
            subMatrix[tempJ][tempK] = matrix.returnData(j, k);
            tempK++;
          }
        }
        tempJ++;
      }
    }
    return new Matrix(subMatrix);
  }

  //Multiplies a matrix by a vector
  public static Matrix matrixMultiply(Matrix matrix1, float[] vector){
    if(vector.length == matrix1.returnWidth()){
      float[][] outputMatrix = new float[matrix1.returnHeight()][1];
      for(int i = 0; i < matrix1.returnHeight(); i++){
        for(int p = 0; p < matrix1.returnWidth(); p++)
          outputMatrix[i][0]+=matrix1.returnData(i, p)*vector[p];
        if(round)
          outputMatrix[i][0] = (float)Math.round(outputMatrix[i][0]*sigFigs[0])*sigFigs[1];
      }
      return new Matrix(outputMatrix);
    }
    System.out.println("ERROR: NUMBER OF ROWS IN MATRIX DOES NOT EQUAL THE NUMBER OF ELEMENTS IN VECTOR!");
    System.exit(1);
    return new Matrix();
  }
  //Takes two matrices of the same width and same height and adds their elements together
  public static Matrix matrixAddition(Matrix matrix1, Matrix matrix2){
    if(matrix1.returnWidth() == matrix2.returnWidth() && matrix1.returnHeight() == matrix2.returnHeight()){
      float[][] outputMatrix = new float[matrix1.returnHeight()][matrix1.returnWidth()];
      for(int i = 0; i < matrix1.returnHeight(); i++){
        for(int j = 0; j < matrix1.returnWidth(); j++)
          outputMatrix[i][j] = matrix1.returnData(i, j)+matrix2.returnData(i, j);
      }
      return new Matrix(outputMatrix);
    }
    System.out.println("ERROR: DIMENTIONS MUST MATCH");
    System.exit(1);
    return new Matrix();
  }
  
  //Takes two matrices of same width and height and subtracts the second's elements from the first one's elements
  public static Matrix matrixSubtraction(Matrix matrix1, Matrix matrix2){
    if(matrix1.returnWidth() == matrix2.returnWidth() && matrix1.returnHeight() == matrix2.returnHeight()){
      float[][] outputMatrix = new float[matrix1.returnHeight()][matrix1.returnWidth()];
      for(int i = 0; i < matrix1.returnHeight(); i++){
        for(int j = 0; j < matrix1.returnWidth(); j++)
          outputMatrix[i][j] = matrix1.returnData(i, j)-matrix2.returnData(i, j);
      }
      return new Matrix(outputMatrix);
    }
    System.out.println("ERROR: DIMENTIONS MUST MATCH");
    System.exit(1);
    return new Matrix();
  }
  
  //Takes in a matrix and swaps around which axis is i and which one is j
  public static Matrix transposeMatrix(Matrix matrix){
    float[][] outputMatrix = new float[matrix.returnWidth()][matrix.returnHeight()];
    for(int i = 0; i < matrix.returnHeight(); i++){
       for(int j = 0; j < matrix.returnWidth(); j++){
         outputMatrix[j][i] = matrix.returnData(i, j);
       }
    }
    return new Matrix(outputMatrix);
  }
  
  //Takes a matrix and a constant number k and multiplies the matrix's elements by k
  public static Matrix multByConst(Matrix matrix, final float k){
    float[][] outputMatrix = new float[matrix.returnHeight()][matrix.returnWidth()];
    for(int i = 0; i < matrix.returnHeight(); i++){
      for(int j = 0; j < matrix.returnWidth(); j++)
        outputMatrix[i][j] = matrix.returnData(i, j)*k;
    }
    return new Matrix(outputMatrix);
  }

  //Takes a matrix and a constant number k and divides the matrix's elements by k
  public static Matrix divByConst(Matrix matrix, float k){
    if(Math.abs(k) > sigFigs[1]){
      k = 1/k;
      float[][] outputMatrix = new float[matrix.returnHeight()][matrix.returnWidth()];
      for(int i = 0; i < matrix.returnHeight(); i++){
        for(int j = 0; j < matrix.returnWidth(); j++)
          outputMatrix[i][j] = matrix.returnData(i, j)*k;
      }
      return new Matrix(outputMatrix);
    }
    System.out.println("ERROR: DIV BY ZERO");
    System.exit(1);
    return new Matrix();
  }
  
  //Takes a 1D array of width x and produces a matrix of dimentions x*1
  public static Matrix transposeMatrix(float[] matrix){
    float[][] outputMatrix = new float[matrix.length][1];
    for(int i = 0; i < matrix.length; i++)
      outputMatrix[i][0] = matrix[i];
    return new Matrix(outputMatrix);
  }

  //Sets a matrix to an integer power
  public static Matrix pow(Matrix m, int pow){
    //Returns an error should the matrix be nonsquare
    if(m.returnWidth() != m.returnHeight()){
      System.out.println("ERROR: MATRIX MUST BE SQUARE");
      System.exit(-1);
    }
    //Will be the output matrix
    Matrix output = new Matrix(m.returnWidth(), m.returnWidth());
    Matrix mTemp = new Matrix(m);
    //Returns an identity matrix should the power be 0
    if(pow == 0)
      return output;
    //Computes the inverse of M when the power is less than one
    else if(pow < 0){
      mTemp = invertMatrix(m); //Setting mTemp to M^(-1)
      pow = (~pow)+1; //Negating pow
    }
    //Exponentiation by squaring
    while(pow > 1){
      if((pow & 1) == 1){
        output = matrixMultiply(output, mTemp);
        pow--;
      }
      mTemp = matrixMultiply(mTemp, mTemp);
      pow>>>=1;
    }
    return matrixMultiply(output, mTemp);
  }
  
  public static Matrix4x4 matrixMultiply(Matrix4x4 matrix1, Matrix4x4 matrix2){
    float[][] output = new float[4][4];

    //Row 1
    output[0][0] = matrix1.returnData(0, 0)*matrix2.returnData(0, 0)+matrix1.returnData(0, 1)*matrix2.returnData(1, 0)+matrix1.returnData(0, 2)*matrix2.returnData(2, 0)+matrix1.returnData(0, 3)*matrix2.returnData(3, 0);
    output[0][1] = matrix1.returnData(0, 0)*matrix2.returnData(0, 1)+matrix1.returnData(0, 1)*matrix2.returnData(1, 1)+matrix1.returnData(0, 2)*matrix2.returnData(2, 1)+matrix1.returnData(0, 3)*matrix2.returnData(3, 1);
    output[0][2] = matrix1.returnData(0, 0)*matrix2.returnData(0, 2)+matrix1.returnData(0, 1)*matrix2.returnData(1, 2)+matrix1.returnData(0, 2)*matrix2.returnData(2, 2)+matrix1.returnData(0, 3)*matrix2.returnData(3, 2);
    output[0][3] = matrix1.returnData(0, 0)*matrix2.returnData(0, 3)+matrix1.returnData(0, 1)*matrix2.returnData(1, 3)+matrix1.returnData(0, 2)*matrix2.returnData(2, 3)+matrix1.returnData(0, 3)*matrix2.returnData(3, 3);

    //Row 2
    output[1][0] = matrix1.returnData(1, 0)*matrix2.returnData(0, 0)+matrix1.returnData(1, 1)*matrix2.returnData(1, 0)+matrix1.returnData(1, 2)*matrix2.returnData(2, 0)+matrix1.returnData(1, 3)*matrix2.returnData(3, 0);
    output[1][1] = matrix1.returnData(1, 0)*matrix2.returnData(0, 1)+matrix1.returnData(1, 1)*matrix2.returnData(1, 1)+matrix1.returnData(1, 2)*matrix2.returnData(2, 1)+matrix1.returnData(1, 3)*matrix2.returnData(3, 1);
    output[1][2] = matrix1.returnData(1, 0)*matrix2.returnData(0, 2)+matrix1.returnData(1, 1)*matrix2.returnData(1, 2)+matrix1.returnData(1, 2)*matrix2.returnData(2, 2)+matrix1.returnData(1, 3)*matrix2.returnData(3, 2);
    output[1][3] = matrix1.returnData(1, 0)*matrix2.returnData(0, 3)+matrix1.returnData(1, 1)*matrix2.returnData(1, 3)+matrix1.returnData(1, 2)*matrix2.returnData(2, 3)+matrix1.returnData(1, 3)*matrix2.returnData(3, 3);

    //Row 3
    output[2][0] = matrix1.returnData(2, 0)*matrix2.returnData(0, 0)+matrix1.returnData(2, 1)*matrix2.returnData(1, 0)+matrix1.returnData(2, 2)*matrix2.returnData(2, 0)+matrix1.returnData(2, 3)*matrix2.returnData(3, 0);
    output[2][1] = matrix1.returnData(2, 0)*matrix2.returnData(0, 1)+matrix1.returnData(2, 1)*matrix2.returnData(1, 1)+matrix1.returnData(2, 2)*matrix2.returnData(2, 1)+matrix1.returnData(2, 3)*matrix2.returnData(3, 1);
    output[2][2] = matrix1.returnData(2, 0)*matrix2.returnData(0, 2)+matrix1.returnData(2, 1)*matrix2.returnData(1, 2)+matrix1.returnData(2, 2)*matrix2.returnData(2, 2)+matrix1.returnData(2, 3)*matrix2.returnData(3, 2);
    output[2][3] = matrix1.returnData(2, 0)*matrix2.returnData(0, 3)+matrix1.returnData(2, 1)*matrix2.returnData(1, 3)+matrix1.returnData(2, 2)*matrix2.returnData(2, 3)+matrix1.returnData(2, 3)*matrix2.returnData(3, 3);

    //Row 4
    output[3][0] = matrix1.returnData(3, 0)*matrix2.returnData(0, 0)+matrix1.returnData(3, 1)*matrix2.returnData(1, 0)+matrix1.returnData(3, 2)*matrix2.returnData(2, 0)+matrix1.returnData(3, 3)*matrix2.returnData(3, 0);
    output[3][1] = matrix1.returnData(3, 0)*matrix2.returnData(0, 1)+matrix1.returnData(3, 1)*matrix2.returnData(1, 1)+matrix1.returnData(3, 2)*matrix2.returnData(2, 1)+matrix1.returnData(3, 3)*matrix2.returnData(3, 1);
    output[3][2] = matrix1.returnData(3, 0)*matrix2.returnData(0, 2)+matrix1.returnData(3, 1)*matrix2.returnData(1, 2)+matrix1.returnData(3, 2)*matrix2.returnData(2, 2)+matrix1.returnData(3, 3)*matrix2.returnData(3, 2);
    output[3][3] = matrix1.returnData(3, 0)*matrix2.returnData(0, 3)+matrix1.returnData(3, 1)*matrix2.returnData(1, 3)+matrix1.returnData(3, 2)*matrix2.returnData(2, 3)+matrix1.returnData(3, 3)*matrix2.returnData(3, 3);
    
    if(round){
      output[0][0] = (float)Math.round(output[0][0]*sigFigs[0])*sigFigs[1];
      output[0][1] = (float)Math.round(output[0][1]*sigFigs[0])*sigFigs[1];
      output[0][2] = (float)Math.round(output[0][2]*sigFigs[0])*sigFigs[1];
      output[0][3] = (float)Math.round(output[0][3]*sigFigs[0])*sigFigs[1];

      output[1][0] = (float)Math.round(output[1][0]*sigFigs[0])*sigFigs[1];
      output[1][1] = (float)Math.round(output[1][1]*sigFigs[0])*sigFigs[1];
      output[1][2] = (float)Math.round(output[1][2]*sigFigs[0])*sigFigs[1];
      output[1][3] = (float)Math.round(output[1][3]*sigFigs[0])*sigFigs[1];

      output[2][0] = (float)Math.round(output[2][0]*sigFigs[0])*sigFigs[1];
      output[2][1] = (float)Math.round(output[2][1]*sigFigs[0])*sigFigs[1];
      output[2][2] = (float)Math.round(output[2][2]*sigFigs[0])*sigFigs[1];
      output[2][3] = (float)Math.round(output[2][3]*sigFigs[0])*sigFigs[1];

      output[3][0] = (float)Math.round(output[3][0]*sigFigs[0])*sigFigs[1];
      output[3][1] = (float)Math.round(output[3][1]*sigFigs[0])*sigFigs[1];
      output[3][2] = (float)Math.round(output[3][2]*sigFigs[0])*sigFigs[1];
      output[3][3] = (float)Math.round(output[3][3]*sigFigs[0])*sigFigs[1];
    }

    return new Matrix4x4(output);
  }

  public static Matrix matrixMultiply(Matrix4x4 matrix1, float[] vector){
    if(vector.length == 4){
      float[][] output = new float[4][1];
      output[0][0] = matrix1.returnData(0, 0)*vector[0]+matrix1.returnData(0, 1)*vector[1]+matrix1.returnData(0, 2)*vector[2]+matrix1.returnData(0, 3)*vector[3];
      output[1][0] = matrix1.returnData(1, 0)*vector[0]+matrix1.returnData(1, 1)*vector[1]+matrix1.returnData(1, 2)*vector[2]+matrix1.returnData(1, 3)*vector[3];
      output[2][0] = matrix1.returnData(2, 0)*vector[0]+matrix1.returnData(2, 1)*vector[1]+matrix1.returnData(2, 2)*vector[2]+matrix1.returnData(2, 3)*vector[3];
      output[3][0] = matrix1.returnData(3, 0)*vector[0]+matrix1.returnData(3, 1)*vector[1]+matrix1.returnData(3, 2)*vector[2]+matrix1.returnData(3, 3)*vector[3];
      return new Matrix(output);
    }
    System.out.println("ERROR: VECTOR MUST BE OF LENGTH 4");
    return new Matrix4x4();
  }

  public static Matrix4x4 matrixAddition(Matrix4x4 matrix1, Matrix4x4 matrix2){
    float[][] output = new float[4][4];
    output[0][0] = matrix1.returnData(0,0)+matrix2.returnData(0, 0);
    output[0][1] = matrix1.returnData(0,1)+matrix2.returnData(0, 1);
    output[0][2] = matrix1.returnData(0,2)+matrix2.returnData(0, 2);
    output[0][3] = matrix1.returnData(0,3)+matrix2.returnData(0, 3);

    output[1][0] = matrix1.returnData(1,0)+matrix2.returnData(1, 0);
    output[1][1] = matrix1.returnData(1,1)+matrix2.returnData(1, 1);
    output[1][2] = matrix1.returnData(1,2)+matrix2.returnData(1, 2);
    output[1][3] = matrix1.returnData(1,3)+matrix2.returnData(1, 3);

    output[2][0] = matrix1.returnData(2,0)+matrix2.returnData(2, 0);
    output[2][1] = matrix1.returnData(2,1)+matrix2.returnData(2, 1);
    output[2][2] = matrix1.returnData(2,2)+matrix2.returnData(2, 2);
    output[2][3] = matrix1.returnData(2,3)+matrix2.returnData(2, 3);

    output[3][0] = matrix1.returnData(3,0)+matrix2.returnData(3, 0);
    output[3][1] = matrix1.returnData(3,1)+matrix2.returnData(3, 1);
    output[3][2] = matrix1.returnData(3,2)+matrix2.returnData(3, 2);
    output[3][3] = matrix1.returnData(3,3)+matrix2.returnData(3, 3);

    return new Matrix4x4(output);
  }

  public static Matrix4x4 matrixSubtraction(Matrix4x4 matrix1, Matrix4x4 matrix2){
    float[][] output = new float[4][4];
    output[0][0] = matrix1.returnData(0,0)-matrix2.returnData(0, 0);
    output[0][1] = matrix1.returnData(0,1)-matrix2.returnData(0, 1);
    output[0][2] = matrix1.returnData(0,2)-matrix2.returnData(0, 2);
    output[0][3] = matrix1.returnData(0,3)-matrix2.returnData(0, 3);

    output[1][0] = matrix1.returnData(1,0)-matrix2.returnData(1, 0);
    output[1][1] = matrix1.returnData(1,1)-matrix2.returnData(1, 1);
    output[1][2] = matrix1.returnData(1,2)-matrix2.returnData(1, 2);
    output[1][3] = matrix1.returnData(1,3)-matrix2.returnData(1, 3);

    output[2][0] = matrix1.returnData(2,0)-matrix2.returnData(2, 0);
    output[2][1] = matrix1.returnData(2,1)-matrix2.returnData(2, 1);
    output[2][2] = matrix1.returnData(2,2)-matrix2.returnData(2, 2);
    output[2][3] = matrix1.returnData(2,3)-matrix2.returnData(2, 3);

    output[3][0] = matrix1.returnData(3,0)-matrix2.returnData(3, 0);
    output[3][1] = matrix1.returnData(3,1)-matrix2.returnData(3, 1);
    output[3][2] = matrix1.returnData(3,2)-matrix2.returnData(3, 2);
    output[3][3] = matrix1.returnData(3,3)-matrix2.returnData(3, 3);

    return new Matrix4x4(output);
  }

  public static Matrix4x4 multByConst(Matrix4x4 matrix, final float k){
    float[][] output = new float[4][4];
    output[0][0] = matrix.returnData(0, 0)*k;
    output[0][1] = matrix.returnData(0, 1)*k;
    output[0][2] = matrix.returnData(0, 2)*k;
    output[0][3] = matrix.returnData(0, 3)*k;

    output[1][0] = matrix.returnData(1, 0)*k;
    output[1][1] = matrix.returnData(1, 1)*k;
    output[1][2] = matrix.returnData(1, 2)*k;
    output[1][3] = matrix.returnData(1, 3)*k;

    output[2][0] = matrix.returnData(2, 0)*k;
    output[2][1] = matrix.returnData(2, 1)*k;
    output[2][2] = matrix.returnData(2, 2)*k;
    output[2][3] = matrix.returnData(2, 3)*k;

    output[3][0] = matrix.returnData(3, 0)*k;
    output[3][1] = matrix.returnData(3, 1)*k;
    output[3][2] = matrix.returnData(3, 2)*k;
    output[3][3] = matrix.returnData(3, 3)*k;

    return new Matrix4x4(output);
  }

  public static Matrix4x4 divByConst(Matrix4x4 matrix, float k){
    if(Math.abs(k) <= sigFigs[1]){
      System.out.println("ERROR: DIV BY ZERO");
      return new Matrix4x4();
    }
    k = 1/k;

    float[][] output = new float[4][4];
    output[0][0] = matrix.returnData(0, 0)*k;
    output[0][1] = matrix.returnData(0, 1)*k;
    output[0][2] = matrix.returnData(0, 2)*k;
    output[0][3] = matrix.returnData(0, 3)*k;

    output[1][0] = matrix.returnData(1, 0)*k;
    output[1][1] = matrix.returnData(1, 1)*k;
    output[1][2] = matrix.returnData(1, 2)*k;
    output[1][3] = matrix.returnData(1, 3)*k;

    output[2][0] = matrix.returnData(2, 0)*k;
    output[2][1] = matrix.returnData(2, 1)*k;
    output[2][2] = matrix.returnData(2, 2)*k;
    output[2][3] = matrix.returnData(2, 3)*k;

    output[3][0] = matrix.returnData(3, 0)*k;
    output[3][1] = matrix.returnData(3, 1)*k;
    output[3][2] = matrix.returnData(3, 2)*k;
    output[3][3] = matrix.returnData(3, 3)*k;

    return new Matrix4x4(output);
  }

  public static Matrix4x4 transposeMatrix(Matrix4x4 matrix){
    float[][] output = new float[4][4];

    output[0][0] = matrix.returnData(0, 0);
    output[0][1] = matrix.returnData(1, 0);
    output[0][2] = matrix.returnData(2, 0);
    output[0][3] = matrix.returnData(3, 0);

    output[1][0] = matrix.returnData(0, 1);
    output[1][1] = matrix.returnData(1, 1);
    output[1][2] = matrix.returnData(2, 1);
    output[1][3] = matrix.returnData(3, 1);

    output[2][0] = matrix.returnData(0, 2);
    output[2][1] = matrix.returnData(1, 2);
    output[2][2] = matrix.returnData(2, 2);
    output[2][3] = matrix.returnData(3, 2);

    output[3][0] = matrix.returnData(0, 3);
    output[3][1] = matrix.returnData(1, 3);
    output[3][2] = matrix.returnData(2, 3);
    output[3][3] = matrix.returnData(3, 3);

    return new Matrix4x4(output);
  }

  //Sets a matrix to an integer power
  public static Matrix4x4 pow(Matrix4x4 m, int pow){
    //Will be the output matrix
    Matrix4x4 output = new Matrix4x4();
    float[][] mFloat = m.toFloat();
    Matrix4x4 mTemp = new Matrix4x4(mFloat);

    //Returns an identity matrix should the power be 0
    if(pow == 0)
      return output;
    //Computes the inverse of M when the power is less than one
    else if(pow < 0){
      mTemp = new Matrix4x4(invertMatrix(mFloat)); //Setting mTemp to M^(-1)
      pow = (~pow)+1; //Negating pow
    }
    //Exponentiation by squaring
    while(pow > 1){
      if((pow & 1) == 1){
        output = matrixMultiply(output, mTemp);
        pow--;
      }
      mTemp = matrixMultiply(mTemp, mTemp);
      pow>>>=1;
    }
    return matrixMultiply(output, mTemp);
  }

  //Takes in two 2D arrays where wid1 == heigh2 and multiplies them together
  public static float[][] matrixMultiply(float[][] matrix1, float[][] matrix2){
    if(matrix2.length == matrix1[0].length){
      float[][] outputMatrix = new float[matrix1.length][matrix2[0].length];
      for(int i = 0; i < matrix1.length; i++){
        for(int j = 0; j < matrix2[0].length; j++){
          for(int p = 0; p < matrix1[0].length; p++)
            outputMatrix[i][j]+=matrix1[i][p]*matrix2[p][j];
          if(round)
            outputMatrix[i][j] = (float)Math.round(outputMatrix[i][j]*sigFigs[0])*sigFigs[1];
        }
      }
      return outputMatrix;
    }
    System.out.println("ERROR: NUMBER OF COLUMNS IN MATRIX 2 DOES NOT EQUAL THE NUMBER OF ROWS IN MATRIX 1!");
    float[][] outputMatrix = {{0}};
    System.exit(1);
    return outputMatrix;
  }

   //Computes the inverse of a square matrix
  public static float[][] invertMatrix(float[][] matrix1){
    if(matrix1.length == matrix1[0].length){
      float determinant = calcDeterminant(matrix1);
      if(Math.abs(determinant) > sigFigs[1]){
        float[][] adjoint = getAdjoint(matrix1);
        adjoint = divByConst(adjoint, determinant);
        return adjoint;
      }
      System.out.println("ERROR: DIV BY 0");
      float[][] newMatrix = {{0}};
      System.exit(1);
      return newMatrix;
    }
    System.out.println("ERROR: MATRIX IS NOT A SQUARE");
    float[][] newMatrix = {{0}};
    System.exit(1);
    return newMatrix;
  }

  //Returns a new matrix that is the adjoint of the input matrix
  public static float[][] getAdjoint(float[][] matrix){
    if(matrix[0].length == matrix.length){
      float[][] newMatrix = new float[matrix.length][matrix.length];
      for(int i = 0; i < matrix.length; i++)
        for(int j = 0; j < matrix.length; j++)
          newMatrix[i][j] = getCofactor(matrix, i, j);
      return newMatrix;
    }
    System.out.println("ERROR: MATRIX IS NOT SQUARE");
    float[][] newMatrix = {{0}};
    System.exit(1);
    return newMatrix;
  }

  //Computes the cofactor of an element in a square matrix
  public static float getCofactor(float[][] matrix, int row, int col){
    if(matrix.length == matrix[0].length)
      return (1 - (((row+col) & 1) << 1))*calcDeterminant(getMinourMatrix(matrix, row, col));
    System.out.println("ERROR: MATRIX IS NOT A SQUARE");
    System.exit(1);
    return -1;
  }
  
  //Computes the determinant of a square matrix
  public static float calcDeterminant(float[][] matrix){
    if(matrix[0].length == matrix.length){
      //Base cases
      switch(matrix[0].length){
        case 1:
          return matrix[0][0];
        case 2:
          return matrix[0][0]*matrix[1][1]-matrix[0][1]*matrix[1][0];
        }
        //Recursive case
        float determinant = 0;
        byte sign = 1;
        for(int i = 0; i < matrix[0].length; i++){
          determinant+=sign*matrix[0][i]*calcDeterminant(getMinourMatrix(matrix, i, 0));
          sign = (byte)((~sign)+1);
        }
        return determinant;
    }
    System.out.println("ERROR: MATRIX IS NOT A SQUARE");
    System.exit(1);
    return -1;
  }

  //Creates a sub matrix for an element by deleting elements the occupy the same row or column as the element
  public static float[][] getMinourMatrix(float[][] matrix, int rowNum, int colNum){
    float[][] subMatrix = new float[matrix[0].length-1][matrix.length-1];
    int tempJ = 0;
    for(int j = 0; j < matrix[0].length; j++){
      if(j != colNum){
        int tempK = 0;
        for(int k = 0; k < matrix[0].length; k++){
          if(k != rowNum){
            subMatrix[tempJ][tempK] = matrix[j][k];
            tempK++;
          }
        }
        tempJ++;
      }
    }
    return subMatrix;
  }

  //Multiplies a 2D array by a 1D array
  public static float[][] matrixMultiply(float[][] matrix1, float[] vector){
    if(vector.length == matrix1[0].length){
      float[][] outputMatrix = new float[matrix1.length][1];
      for(int i = 0; i < matrix1.length; i++){
        for(int p = 0; p < matrix1[0].length; p++)
          outputMatrix[i][0]+=matrix1[i][p]*vector[p];
        if(round)
          outputMatrix[i][0] = (float)Math.round(outputMatrix[i][0]*sigFigs[0])*sigFigs[1];
      }
      return outputMatrix;
    }
    System.out.println("ERROR: NUMBER OF ROWS IN MATRIX DOES NOT EQUAL THE NUMBER OF ELEMENTS IN VECTOR!");
    float[][] outputMatrix = {{0}};
    System.exit(1);
    return outputMatrix;
  }
  
  
  //Takes two 2D arrays with same width and same height and adds their elements together
  public static float[][] matrixAddition(float[][] matrix1, float[][] matrix2){
    if(matrix1[0].length == matrix2[0].length && matrix1.length == matrix2.length){
      float[][] outputMatrix = new float[matrix1.length][matrix1[0].length];
      for(int i = 0; i < matrix1.length; i++){
        for(int j = 0; j < matrix1[0].length; j++)
          outputMatrix[i][j] = matrix1[i][j]+matrix2[i][j];
      }
      return outputMatrix;
    }
    System.out.println("ERROR: DIMENTIONS MUST MATCH");
    float[][] outputMatrix = {{0}};
    System.exit(1);
    return outputMatrix;
  }
  
  //Takes in two 2D arrays of same width and same height and subtracts the second's elements from the first's
  public static float[][] matrixSubtraction(float[][] matrix1, float[][] matrix2){
    if(matrix1[0].length == matrix2[0].length && matrix1.length == matrix2.length){
      float[][] outputMatrix = new float[matrix1.length][matrix1[0].length];
      for(int i = 0; i < matrix1.length; i++){
        for(int j = 0; j < matrix1[0].length; j++)
          outputMatrix[i][j] = matrix1[i][j]-matrix2[i][j];
      }
      return outputMatrix;
    }
    System.out.println("ERROR: DIMENTIONS MUST MATCH");
    float[][] outputMatrix = {{0}};
    System.exit(1);
    return outputMatrix;
  }
  
  //Takes a 2D array and swaps i and j around
  public static float[][] transposeMatrix(float[][] matrix){
    float[][] outputMatrix = new float[matrix[0].length][matrix.length];
    for(int i = 0; i < matrix.length; i++){
       for(int j = 0; j < matrix[0].length; j++){
         outputMatrix[j][i] = matrix[i][j];
       }
    }
    return outputMatrix;
  }
  
  //Takes a 2D array and a constant k and multiplies the array's elements by k 
  public static float[][] multByConst(float[][] matrix, final float k){
    float[][] outputMatrix = new float[matrix.length][matrix[0].length];
    for(int i = 0; i < matrix.length; i++){
      for(int j = 0; j < matrix[0].length; j++)
        outputMatrix[i][j] = matrix[i][j]*k;
    }
    return outputMatrix;
  }
  //Takes a 2D array and a constant k and divides the array's elements by k 
  public static float[][] divByConst(float[][] matrix, float k){
    if(Math.abs(k) > sigFigs[1]){
      k = 1/k;
      float[][] outputMatrix = new float[matrix.length][matrix[0].length];
      for(int i = 0; i < matrix.length; i++){
        for(int j = 0; j < matrix[0].length; j++)
          outputMatrix[i][j] = matrix[i][j]*k;
      }
      return outputMatrix;
    }
    System.out.println("ERROR: DIV BY ZERO");
    System.exit(-1);
    float[][] outputMatrix = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
    return outputMatrix;
  }

  public static float[][] createIdentityMatrixF(int dim){
    float[][] output = new float[dim][dim];
    for(int i = 0; i < dim; i++)
      output[i][i] = 1;
    return output;
  }
  
  public static float[][] pow(float[][] m, int pow){
    //Returns an error should the matrix be nonsquare
    if(m.length != m[0].length){
      System.out.println("ERROR: MATRIX MUST BE SQUARE");
      System.exit(-1);
    }
    //Will be the output matrix
    float[][] output = createIdentityMatrixF(m.length);
    float[][] mTemp = new float[m.length][m.length];
    //Creating a deep copy of M
    for(int i = 0; i < m.length; i++)
      for(int j = 0; j < m.length; j++)
        mTemp[i][j] = m[i][j];
    //Returns an identity matrix should the power be 0
    if(pow == 0)
      return output;
    //Computes the inverse of M when the power is less than one
    else if(pow < 0){
      mTemp = invertMatrix(m); //Setting mTemp to M^(-1)
      pow = (~pow)+1; //Negating pow
    }
    //Exponentiation by squaring
    while(pow > 1){
      if((pow & 1) == 1){
        output = matrixMultiply(output, mTemp);
        pow--;
      }
      mTemp = matrixMultiply(mTemp, mTemp);
      pow>>>=1;
    }
    return matrixMultiply(output, mTemp);
  }
}
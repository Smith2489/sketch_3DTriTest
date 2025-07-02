package Maths.LinearAlgebra;

public class Matrix4x4 extends Matrix{
    public Matrix4x4(){
        super();
    }

    public Matrix4x4(float[][] newMatrix){
        if(newMatrix.length != 4 || newMatrix[0].length != 4){
            System.out.println("ERROR: MATRIX MUST BE 4X4! IS "+newMatrix[0].length+"X"+newMatrix.length+"!");
            System.exit(1);
        }
        matrix = new float[16];
        width = 4;
        height = 4;
        matrix[0] = newMatrix[0][0];
        matrix[1] = newMatrix[0][1];
        matrix[2] = newMatrix[0][2];
        matrix[3] = newMatrix[0][3];

        matrix[4] = newMatrix[1][0];
        matrix[5] = newMatrix[1][1];
        matrix[6] = newMatrix[1][2];
        matrix[7] = newMatrix[1][3];

        matrix[8] = newMatrix[2][0];
        matrix[9] = newMatrix[2][1];
        matrix[10] = newMatrix[2][2];
        matrix[11] = newMatrix[2][3];

        matrix[12] = newMatrix[3][0];
        matrix[13] = newMatrix[3][1];
        matrix[14] = newMatrix[3][2];
        matrix[15] = newMatrix[3][3];
    }

    public void setData(float newData, int row, int col){
        matrix[col+(row << 2)] = newData;
    }

    public float returnData(int row, int col){
        return matrix[col+(row << 2)];
    }

    public void setToAllOnes(){
        matrix[0] = 1;
        matrix[1] = 1;
        matrix[2] = 1;
        matrix[3] = 1;

        matrix[4] = 1;
        matrix[5] = 1;
        matrix[6] = 1;
        matrix[7] = 1;

        matrix[8] = 1;
        matrix[9] = 1;
        matrix[10] = 1;
        matrix[11] = 1;
        
        matrix[12] = 1;
        matrix[13] = 1;
        matrix[14] = 1;
        matrix[15] = 1;
    }

    public float[] returnCol(int col){
        float[] returnCol = {matrix[col], matrix[4+col], matrix[8+col], matrix[12+col]};
        return returnCol;
    }

    public float[] returnRow(int row){
        float[] returnRow = {matrix[(row << 2)], matrix[(row << 2)+1], matrix[(row << 2)+2], matrix[(row << 2)+3]};
        return returnRow;
    }

    public float[][] toFloat(){
        float[][] output = {{matrix[0], matrix[1], matrix[2], matrix[3]},
                            {matrix[4], matrix[5], matrix[6], matrix[7]},
                            {matrix[8], matrix[9], matrix[10], matrix[11]},
                            {matrix[12], matrix[13], matrix[14], matrix[15]}};
        return output;
    }

    public float[] toFloat1D(){
        float[] output = {matrix[0], matrix[1], matrix[2], matrix[3], 
                          matrix[4], matrix[5], matrix[6], matrix[7],
                          matrix[8], matrix[9], matrix[10], matrix[11],
                          matrix[12], matrix[13], matrix[14], matrix[15]};
        return output;
    }

    public boolean isTransposition(Matrix4x4 otherMatrix){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(Math.abs(otherMatrix.matrix[(i << 2)+j] - matrix[(j << 2)+i]) > 0.0001)
                    return false;
            }
        }
        return true;
    }

    public boolean isIdentity(){
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 4; j++)
                if((j == i && Math.abs(matrix[i+(j << 2)]-1) > 0.0001) || (j != i && Math.abs(matrix[i+(j << 2)]) > 0.0001))
                    return false;
        return true;
    }

    public boolean isSymmetric(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(Math.abs(matrix[(i << 2)+j] - matrix[(j << 2)+i]) > 0.0001)
                    return false;
            }
        }
        return true;
    }

    public void copy(Object o){
        if(o instanceof Matrix4x4)
            super.copy((Matrix4x4)o);
    }
    public void copy(Matrix4x4 m){
        super.copy(m);
    }

    public boolean equals(Object o){
        if(o instanceof Matrix4x4)
            return super.equals((Matrix4x4)o);
        else
            return false;
    }
    public boolean equals(Matrix4x4 m){
        return super.equals(m);
    }
}

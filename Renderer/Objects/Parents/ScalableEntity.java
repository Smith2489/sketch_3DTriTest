package Renderer.Objects.Parents;
import java.util.Objects;
import Maths.LinearAlgebra.*;
import Renderer.ScreenDraw.MVP;
import Actions.*;
//Super class for scalable entities (cameras, scene objects)
public class ScalableEntity extends SceneEntity{
    protected float[] scale = {1, 1, 1};
    protected float[][] shear = {{0, 0}, {0, 0}, {0, 0}};
    protected Matrix modelMatrix = new Matrix();

    public ScalableEntity(){
        super();
        scale[0] = 1;
        scale[1] = 1;
        scale[2] = 1;
        shear[0][0] = 0;
        shear[0][1] = 0;
        shear[1][0] = 0;
        shear[1][1] = 0;
        shear[2][0] = 0;
        shear[2][1] = 0;
    }
    public ScalableEntity(float[] newPos){
        super(newPos);
        scale[0] = 1;
        scale[1] = 1;
        scale[2] = 1;
        shear[0][0] = 0;
        shear[0][1] = 0;
        shear[1][0] = 0;
        shear[1][1] = 0;
        shear[2][0] = 0;
        shear[2][1] = 0;
    }
    public ScalableEntity(float x, float y, float z){
        super(x, y, z);
        scale[0] = 1;
        scale[1] = 1;
        scale[2] = 1;
        shear[0][0] = 0;
        shear[0][1] = 0;
        shear[1][0] = 0;
        shear[1][1] = 0;
        shear[2][0] = 0;
        shear[2][1] = 0;
    }
    public ScalableEntity(float[] position, float[] rotation, float[] scl, float[][] shr){
        super(position);
        for(byte i = 0; i < 3; i++){
            scale[i] = scl[i];
            shear[i][0] = shr[i][0];
            shear[i][1] = shr[i][1];
        }
    }
    public ScalableEntity(float posX, float posY, float posZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ, float[] shearX, float[] shearY, float[] shearZ){
        super(posX, posY, posZ, rotX, rotY, rotZ);
        scale[0] = scaleX;
        scale[1] = scaleY;
        scale[2] = scaleZ;
        shear[0][0] = shearX[0];
        shear[0][1] = shearX[1];
        shear[1][0] = shearY[0];
        shear[1][1] = shearY[1];
        shear[2][0] = shearZ[0];
        shear[2][1] = shearZ[1];
    }
    //Methods for setting the scale in all three axes
    public void setScale(float[] scl){
        scale[0] = scl[0];
        scale[1] = scl[1];
        scale[2] = scl[2];
    }
    public void setScale(float scaleX, float scaleY, float scaleZ){
        scale[0] = scaleX;
        scale[1] = scaleY;
        scale[2] = scaleZ;
    }

    public void setScale(float newScale){
        scale[0] = newScale;
        scale[1] = newScale;
        scale[2] = newScale;
    }
    //Methods for setting the scale in one axis
    public void setScaleX(float scaleX){
        scale[0] = scaleX;
    }
    public void setScaleY(float scaleY){
        scale[1] = scaleY;
    }
    public void setScaleZ(float scaleZ){
        scale[2] = scaleZ;
    }

    
    protected void addAction(ObjectAction newAction){
        super.addAction(newAction);
        newAction.setScale(scale);
        newAction.setShear(shear);
        newAction.setMatrix(modelMatrix);
    }

    //Methods for setting the shear in all three axes
    public void setShear(float[][] shr){
        shear[0][0] = shr[0][0];
        shear[0][1] = shr[0][1];
        shear[1][0] = shr[1][0];
        shear[1][1] = shr[1][1];
        shear[2][0] = shr[2][0];
        shear[2][1] = shr[2][1];
    }
    public void setShear(float[] shearX, float[] shearY, float[] shearZ){
        shear[0][0] = shearX[0];
        shear[0][1] = shearX[1];
        shear[1][0] = shearY[0];
        shear[1][1] = shearY[1];
        shear[2][0] = shearZ[0];
        shear[2][1] = shearZ[1];
    }

    //Methods for setting the shear in one axis
    public void setShearX(float[] shearX){
        shear[0][0] = shearX[0];
        shear[0][1] = shearX[1];
    }
    public void setShearY(float[] shearY){
        shear[1][0] = shearY[0];
        shear[1][1] = shearY[1];
    }
    public void setShearZ(float[] shearZ){
        shear[2][0] = shearZ[0];
        shear[2][1] = shearZ[1];
    }
    public void setShearX(float shearX1, float shearX2){
        shear[0][0] = shearX1;
        shear[0][1] = shearX2;
    }
    public void setShearY(float shearY1, float shearY2){
        shear[1][0] = shearY1;
        shear[1][1] = shearY2;
    }
    public void setShearZ(float shearZ1, float shearZ2){
        shear[2][0] = shearZ1;
        shear[2][1] = shearZ2;
    }

    //Sets the the model matrix for the object to an existing matrix
    public void setModelMatrix(Matrix newModel){
        if(Objects.nonNull(newModel) && newModel.returnWidth() == 4 && newModel.returnHeight() == 4)
            modelMatrix.copy(newModel);
        else
            System.out.println("ERROR: MATRIX MUST BE A VALID 4x4 MATRIX");
    }

    //Sets the model matrix for the object to a new matrix
    public void setModelMatrix(){
        modelMatrix.copy(MVP.inverseViewMatrix(pos, rot, scale, shear));
    }

    public float[] returnScale(){
        return scale;
    }

    public float[][] returnShear(){
        return shear;
    }
    public Matrix returnModelMatrix(){
        return modelMatrix;
    }

    public void copy(Object o){
        if(o instanceof ScalableEntity){
            ScalableEntity e = (ScalableEntity)o;
            super.copy(e);
            modelMatrix.copy(e.modelMatrix);
            for(byte i = 0; i < 3; i++){
                scale[i] = e.scale[i];
                shear[i][0] = e.shear[i][0];
                shear[i][1] = e.shear[i][1];
            }
        }
    }
    public void copy(ScalableEntity e){
        super.copy(e);
        modelMatrix.copy(e.modelMatrix);
        for(byte i = 0; i < 3; i++){
            scale[i] = e.scale[i];
            shear[i][0] = e.shear[i][0];
            shear[i][1] = e.shear[i][1];
        }
    }

    public boolean equals(Object o){
        if(o instanceof ScalableEntity){
            ScalableEntity e = (ScalableEntity)o;
            boolean isEquals = super.equals(e);
            isEquals&=(modelMatrix.equals(e.modelMatrix));
            for(byte i = 0; i < 3; i++){
                isEquals&=(Math.abs(scale[i] - e.scale[i]) <= EPSILON);
                isEquals&=(Math.abs(shear[i][0] - e.shear[i][0]) <= EPSILON);
                isEquals&=(Math.abs(shear[i][1] - e.shear[i][1]) <= EPSILON);
            }
            return isEquals;
        }
        else
            return false;
    }
    public boolean equals(ScalableEntity e){
        boolean isEquals = super.equals(e);
        isEquals&=(modelMatrix.equals(e.modelMatrix));
        for(byte i = 0; i < 3; i++){
            isEquals&=(Math.abs(scale[i] - e.scale[i]) <= EPSILON);
            isEquals&=(Math.abs(shear[i][0] - e.shear[i][0]) <= EPSILON);
            isEquals&=(Math.abs(shear[i][1] - e.shear[i][1]) <= EPSILON);
        }
        return isEquals;
    }
}
public class ObjectAction extends Action{
    protected float[] scale = {1, 1, 1};
    protected float[][] shr = new float[3][2];
    protected Matrix model = new Matrix();
    public void perform(){
        System.out.println("NO OBJECT ACTION ATTACHED. PLEASE OVERIDE METHOD perform() IN CLASS (OR CLASSES) WHICH EXTEND(S) THIS CLASS");
    }
    public void setScale(float[] newScale){
        scale = newScale;
    }
    public void setShear(float[][] newShr){
        shr = newShr;
    }
    public void setMatrix(Matrix newModel){
        model = newModel;
    }
    protected float[] getForward(){
        float[] forward = {model.returnData(0, 2), model.returnData(1, 2), model.returnData(2, 2)};
        forward =  VectorOperations.vectorNormalization3D(forward);
        forward[0]-=0.0001f;
        forward[1]-=0.0001f;
        forward[2]-=0.0001f;
        return forward;
    }
    protected float[] getBackward(){
        float[] backward = {-model.returnData(0, 2), -model.returnData(1, 2), -model.returnData(2, 2)};
        backward = VectorOperations.vectorNormalization3D(backward);
        backward[0]-=0.0001f;
        backward[1]-=0.0001f;
        backward[2]-=0.0001f;
        return backward;
    }
    protected float[] getRight(){
        float[] right = {model.returnData(0, 0), model.returnData(1, 0), model.returnData(2, 0)};
        right =  VectorOperations.vectorNormalization3D(right);
        right[0]-=0.0001f;
        right[1]-=0.0001f;
        right[2]-=0.0001f;
        return right;
    }
    protected float[] getLeft(){
        float[] left = {-model.returnData(0, 0), -model.returnData(1, 0), -model.returnData(2, 0)};
        left =  VectorOperations.vectorNormalization3D(left);
        left[0]-=0.0001f;
        left[1]-=0.0001f;
        left[2]-=0.0001f;
        return left;
    }
    protected float[] getUp(){
        float[] up = {-model.returnData(0, 1), -model.returnData(1, 1), -model.returnData(2, 1)};
        up =  VectorOperations.vectorNormalization3D(up);
        up[0]-=0.0001f;
        up[1]-=0.0001f;
        up[2]-=0.0001f;
        return up;
    }
    protected float[] getDown(){
        float[] down = {model.returnData(0, 1), model.returnData(1, 1), model.returnData(2, 1)};
        down =  VectorOperations.vectorNormalization3D(down);
        down[0]-=0.0001f;
        down[1]-=0.0001f;
        down[2]-=0.0001f;
        return down;
    }
}

package Actions.ObjectActions;
public class ObjectAction extends Action{
    private float[] scale = {1, 1, 1};
    private float[][] shr = new float[3][2];
    public void perform(){
        System.out.println("NO OBJECT ACTION ATTACHED. PLEASE OVERIDE METHOD perform() IN CLASS (OR CLASSES) WHICH EXTEND(S) THIS CLASS");
    }
    public void setScale(float[] newScale){
        scale = newScale;
    }
    public void setShear(float[][] newShr){
        shr = newShr;
    }

    protected void addToScale(float rate, byte index){
        scale[index]+=rate;
    }

    protected void hardSetScale(float xS, float yS, float zS){
        scale[0] = xS;
        scale[1] = yS;
        scale[2] = zS;
    }
    protected void hardSetScale(float[] newScale){
        scale[0] = newScale[0];
        scale[1] = newScale[1];
        scale[2] = newScale[2];
    }

    protected float[] getScale(){
        float[] scaleCopy = {scale[0], scale[1], scale[2]};
        return scaleCopy;
    }

    protected void addToShear(float rate, byte outerIndex, byte innerIndex){
        shr[outerIndex][innerIndex]+=rate;
    }
    protected void addToShear(float rate, byte index){
        shr[index][0]+=rate;
        shr[index][1]+=rate;
    }
    protected void addToShear(float[] rate, byte index){
        shr[index][0]+=rate[0];
        shr[index][1]+=rate[1];
    }

    protected void hardSetShear(float[][] newShear){
        shr[0][0] = newShear[0][0];
        shr[0][1] = newShear[0][1];
        shr[1][0] = newShear[1][0];
        shr[1][1] = newShear[1][1];
        shr[2][0] = newShear[2][0];
        shr[2][1] = newShear[2][1];
    }
    protected void hardSetShear(float[] shearX, float[] shearY, float[] shearZ){
        shr[0][0] = shearX[0];
        shr[0][1] = shearX[1];
        shr[1][0] = shearY[0];
        shr[1][1] = shearY[1];
        shr[2][0] = shearZ[0];
        shr[2][1] = shearZ[1];
    }

    protected void hardSetShear(float shearX1, float shearX2, float shearY1, float shearY2, float shearZ1, float shearZ2){
        shr[0][0] = shearX1;
        shr[0][1] = shearX2;
        shr[1][0] = shearY1;
        shr[1][1] = shearY2;
        shr[2][0] = shearZ1;
        shr[2][1] = shearZ2;
    }

    

    protected float[][] getShear(){
        float[][] shearCopy = {{shr[0][0], shr[0][1]}, {shr[1][0], shr[1][1]}, {shr[2][0], shr[2][1]}};
        return shearCopy;
    }

    
}

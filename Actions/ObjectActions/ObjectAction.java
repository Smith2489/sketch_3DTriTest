package Actions.ObjectActions;
public class ObjectAction extends Action{
    protected float[] scale = {1, 1, 1};
    protected float[][] shr = new float[3][2];
    public void perform(){
        System.out.println("NO OBJECT ACTION ATTACHED. PLEASE OVERIDE METHOD perform() IN CLASS (OR CLASSES) WHICH EXTEND(S) THIS CLASS");
    }
    public void setScale(float[] newScale){
        scale = newScale;
    }
    public void setShear(float[][] newShr){
        shr = newShr;
    }
    
}

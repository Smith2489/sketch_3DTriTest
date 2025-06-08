package Actions.ObjectActions;
import Wrapper.*;
public class ModelAction extends ObjectAction{
    private FloatWrapper tint = new FloatWrapper();
    public void perform(){
        System.out.println("NO MODEL ACTION ATTACHED. PLEASE OVERIDE METHOD perform() IN CLASS (OR CLASSES) WHICH EXTEND(S) THIS CLASS");
    }
    public void setModelTint(FloatWrapper newTint){
        tint = newTint;
    }

    protected void setModelTint(float newTint){
        tint.val = Math.max(0, Math.min(newTint, 1));
    }
    protected float getModelTint(){
        return tint.val;
    }
}

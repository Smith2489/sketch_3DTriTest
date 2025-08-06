package Actions.ObjectActions;
import Wrapper.*;
public abstract class ModelAction extends ObjectAction{
    private FloatWrapper tint = new FloatWrapper();
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

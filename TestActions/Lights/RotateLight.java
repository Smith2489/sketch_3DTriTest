package TestActions.Lights;
import Actions.ObjectActions.*;
public class RotateLight extends LightAction{
    public void init(){
      
    }
    private float angularVelocity = 1f;
    public void perform(){
      addToRotation(angularVelocity, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] > 360){
        addToRotation(-360, (byte)1);
      }
      else if(tempRot[1] < 0){
          addToRotation(360, (byte)1);
      }
    }
  }

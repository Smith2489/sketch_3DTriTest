package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateMess extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(-1.5f*speed, (byte)0);
      addToRotation(-1.5f*speed, (byte)2);
      float[] tempRot = getRot();
      if(tempRot[0] < 0)
        addToRotation(360, (byte)0);
      if(tempRot[2] < 0)
        addToRotation(360, (byte)2);
    }
  }

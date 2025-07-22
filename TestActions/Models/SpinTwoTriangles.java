package TestActions.Models;
import Actions.ObjectActions.*;
public class SpinTwoTriangles extends ModelAction{
    private int direction = 0;
    public void init(){
      direction = (int)(Math.random()*2)-1;
      if(direction == 0)
        direction = 1;
    }
    public void perform(){
      addToRotation(-0.5f*direction*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      else if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
    }
  }

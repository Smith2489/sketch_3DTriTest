package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateRhombohedron extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(-2*frameRateNorm(), (byte)0);
      addToRotation(2*frameRateNorm(), (byte)1);
      float[] tempRot = getRotDegrees();
      if(tempRot[0] < 0)
        rotatePlus360((byte)0);
      if(tempRot[1] >= 360)
        rotateMinus360((byte)1);
    }
  }

package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateBillboard extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(0.25f*speed, (byte)2);
      if(getRot()[2] >= 360)
        addToRotation(-360, (byte)2);
    }
  }

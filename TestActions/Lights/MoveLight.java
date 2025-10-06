package TestActions.Lights;
import Actions.ObjectActions.*;
public class MoveLight extends LightAction{
    private float velocity = 0.05f;
    private final float[] DIR = {0, 0, 1};
    public void init(){

      
    }
    public void perform(){
        addToPosition(velocity*speed(), DIR);
        float[] tempPos = getPos();
        if(tempPos[2] > 100){
          hardSetPosition(tempPos[0], tempPos[1], 100);
          velocity*=-1;
        }
        else if(tempPos[2] < -100){
          hardSetPosition(tempPos[0], tempPos[1], 100);
          velocity*=-1;
        }
    }
  }

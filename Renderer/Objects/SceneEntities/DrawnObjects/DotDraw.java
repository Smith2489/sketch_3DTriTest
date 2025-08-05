package Renderer.Objects.SceneEntities.DrawnObjects;

public class DotDraw extends DispParent{
    private float[] pos = {0, 0, 0};
    public DotDraw(){
        super();
        pos[0] = 0;
        pos[1] = 0;
        pos[2] = 0;
    }
    public DotDraw(float[] newPos){
        super();
        pos[0] = newPos[0];
        pos[1] = newPos[1];
        pos[2] = newPos[2];
    }
    public DotDraw(float[] newPos, int rgba){
        super(rgba);
        pos[0] = newPos[0];
        pos[1] = newPos[1];
        pos[2] = newPos[2];
    }

    public void setPosition(float[] newPos){
        pos[0] = newPos[0];
        pos[1] = newPos[1];
        pos[2] = newPos[2];
    }

    public float[] returnPosition(){
        return pos;
    }
    public float returnX(){
        return pos[0];
    }
    public float returnY(){
        return pos[1];
    }
    public float returnZ(){
        return pos[2];
    }

    public boolean equals(Object o){
        if(o instanceof DotDraw){
            DotDraw d = (DotDraw)o;
            boolean isEquals = super.equals(d);
            isEquals&=(Math.abs(pos[0] - d.pos[0]) <= 0.000001);
            isEquals&=(Math.abs(pos[1] - d.pos[1]) <= 0.000001);
            isEquals&=(Math.abs(pos[2] - d.pos[2]) <= 0.000001);
            return isEquals;
        }
        return false;
    }
    public boolean equals(DotDraw d){
        boolean isEquals = super.equals(d);
        isEquals&=(Math.abs(pos[0] - d.pos[0]) <= 0.000001);
        isEquals&=(Math.abs(pos[1] - d.pos[1]) <= 0.000001);
        isEquals&=(Math.abs(pos[2] - d.pos[2]) <= 0.000001);
        return isEquals;
    }

    public void copy(Object o){
        if(o instanceof DotDraw){
            DotDraw d = (DotDraw)o;
            super.copy(d);
            pos[0] = d.pos[0];
            pos[1] = d.pos[1];
            pos[2] = d.pos[2];
        }
    }
    public void copy(DotDraw d){
        super.copy(d);
        pos[0] = d.pos[0];
        pos[1] = d.pos[1];
        pos[2] = d.pos[2];
    }
}

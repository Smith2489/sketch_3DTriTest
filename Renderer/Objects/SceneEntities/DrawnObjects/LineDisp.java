package Renderer.Objects.SceneEntities.DrawnObjects;
//A class for defining lines that get drawn
public class LineDisp extends DispParent{
    private float[][] endPoints = {{-0.5f, 0, 0}, {0.5f, 0, 0}}; //The endpoints of the line
    public LineDisp(){
        super();
        endPoints[0][0] = -0.5f;
        endPoints[0][1] = 0;
        endPoints[0][2] = 0;
        endPoints[1][0] = 0.5f;
        endPoints[1][1] = 0;
        endPoints[1][2] = 0;
    }
    public LineDisp(float[][] newEnds, int rgba){
        super(rgba);
        if(newEnds.length >= 2 && newEnds[0].length >= 3 && newEnds[1].length >= 3){
            endPoints[0][0] = newEnds[0][0];
            endPoints[0][1] = newEnds[0][1];
            endPoints[0][2] = newEnds[0][2];
            endPoints[1][0] = newEnds[1][0];
            endPoints[1][1] = newEnds[1][1];
            endPoints[1][2] = newEnds[1][2];
        }
        else{
            System.out.println("ERROR: MUST HAVE AT LEAST TWO END POINTS WITH AT LEAST 3 DIMENTIONS");
            System.exit(1);
        }
    }
    //Sets the endpoints of the line
    public void setEndPoint(float[] newPoint, int index){
        if(index >= 0 && index < 2 && newPoint.length >= 3){
            endPoints[index][0] = newPoint[0];
            endPoints[index][1] = newPoint[1];
            endPoints[index][2] = newPoint[2];
        }
        else{
            System.out.println("ERROR: INDEX OUT OF BOUNDS");
            System.exit(1);
        }
    }

    //Returns hte endpoints of the line
    public float[][] returnEndPoints(){
        return endPoints;
    }
    
    //Returns the point that is in the middle of the line
    public float[] returnPosition(){
        float[] mid = {(endPoints[0][0]+endPoints[1][0])*0.5f,
                       (endPoints[0][1]+endPoints[1][1])*0.5f,
                       (endPoints[0][2]+endPoints[1][2])*0.5f};
        return mid;
    }

    //Returns the x-position of the midpoint
    public float returnX(){
        return (endPoints[0][0]+endPoints[1][0])*0.5f;
    }
    //Returns the y-position of the midpoint
    public float returnY(){
        return (endPoints[0][1]+endPoints[1][1])*0.5f;
    }
    //Returns the z-position of the midpoint
    public float returnZ(){
        return (endPoints[0][2]+endPoints[1][2])*0.5f;
    }

    //Returns a string representing the line
    public String toString(){
        String endPointsString = "END POINTS: {("+endPoints[0][0]+", "+endPoints[0][1]+", "+endPoints[0][2]+"), ";
        endPointsString+="("+endPoints[1][0]+", "+endPoints[1][1]+", "+endPoints[1][2]+")}\n";
        String strokeString = "COLOUR: 0x"+Integer.toHexString(stroke)+"\n";
        String noDepthString = "DRAWN ON TOP: "+((flags & 1) == 1)+"\n";
        return endPointsString+strokeString+noDepthString;
    }

    //Returns if two lines are equal
    public boolean equals(Object o){
        if(o instanceof LineDisp){
            LineDisp l = (LineDisp)o;
            boolean isEquals = super.equals(l);
            isEquals&=(Math.abs(endPoints[0][0] - l.endPoints[0][0]) > 0.0001);
            isEquals&=(Math.abs(endPoints[0][1] - l.endPoints[0][2]) > 0.0001);
            isEquals&=(Math.abs(endPoints[0][2] - l.endPoints[0][2]) > 0.0001);
            isEquals&=(Math.abs(endPoints[1][0] - l.endPoints[1][0]) > 0.0001);
            isEquals&=(Math.abs(endPoints[1][1] - l.endPoints[1][2]) > 0.0001);
            isEquals&=(Math.abs(endPoints[1][2] - l.endPoints[1][2]) > 0.0001);
            return isEquals;
        }
        else
            return false;
    }
    public boolean equals(LineDisp l){
        boolean isEquals = super.equals(l);
        isEquals&=(Math.abs(endPoints[0][0] - l.endPoints[0][0]) > 0.0001);
        isEquals&=(Math.abs(endPoints[0][1] - l.endPoints[0][2]) > 0.0001);
        isEquals&=(Math.abs(endPoints[0][2] - l.endPoints[0][2]) > 0.0001);
        isEquals&=(Math.abs(endPoints[1][0] - l.endPoints[1][0]) > 0.0001);
        isEquals&=(Math.abs(endPoints[1][1] - l.endPoints[1][2]) > 0.0001);
        isEquals&=(Math.abs(endPoints[1][2] - l.endPoints[1][2]) > 0.0001);
        return isEquals;
    }
    //Copies a line object into the current object
    public void copy(Object o){
        if(o instanceof LineDisp){
            LineDisp l = (LineDisp)o;
            super.copy(l);
            endPoints[0][0] = l.endPoints[0][0];
            endPoints[0][1] = l.endPoints[0][1];
            endPoints[0][2] = l.endPoints[0][2];
            endPoints[1][0] = l.endPoints[1][0];
            endPoints[1][1] = l.endPoints[1][1];
            endPoints[1][2] = l.endPoints[1][2];
        }
    }
    public void copy(LineDisp l){
        super.copy(l);
        endPoints[0][0] = l.endPoints[0][0];
        endPoints[0][1] = l.endPoints[0][1];
        endPoints[0][2] = l.endPoints[0][2];
        endPoints[1][0] = l.endPoints[1][0];
        endPoints[1][1] = l.endPoints[1][1];
        endPoints[1][2] = l.endPoints[1][2];
    }
}

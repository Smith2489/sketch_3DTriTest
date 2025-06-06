package Renderer.Objects.SceneEntities;
//A class for defining lines that get drawn
public class LineDisp {
    private float[][] endPoints = {{-0.5f, 0, 0}, {0.5f, 0, 0}}; //The endpoints of the line
    private int stroke = 0xFF000000; //The colour of the line
    private boolean noDepth = false; //If the line gets drawn in front of everything else
    public LineDisp(){
        endPoints[0][0] = -0.5f;
        endPoints[0][1] = 0;
        endPoints[0][2] = 0;
        endPoints[1][0] = 0.5f;
        endPoints[1][1] = 0;
        endPoints[1][2] = 0;
        stroke = 0xFF000000;
        noDepth = false;
    }
    public LineDisp(float[][] newEnds, int rgba){
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
        if((rgba >>> 24) == 0){
            if(rgba <= 0xFF)
                stroke = 0xFF000000 | (rgba << 16) | (rgba << 8) | rgba;
            else if(rgba <= 0xFFFF)
                stroke = ((rgba & 0xFF00) << 16) | ((rgba & 0xFF) << 16) | ((rgba & 0xFF) << 8) | (rgba & 0xFF);
            else
                stroke = 0xFF000000 | rgba;
        }
        else
            stroke = rgba;
        noDepth = false;
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
       //Modifies the colour of the line
       public void stroke(short r, short g, short b){
        stroke = 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
       }
       public void stroke(short r, short g, short b, short a){
        stroke = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
       }
       public void stroke(int rgb, short a){
        if((rgb & 0xFFFFFF) <= 0xFF)
            stroke = ((a & 255) << 24) | (rgb << 16) | (rgb << 8) | rgb;
        else
            stroke = ((a & 255) << 24) | (rgb & 0xFFFFFF);
       }
       public void stroke(int rgba){
        if((rgba >>> 24) == 0){
            if(rgba <= 0xFF)
                stroke = 0xFF000000 | (rgba << 16) | (rgba << 8) | rgba;
            else if(rgba <= 0xFFFF)
                stroke = ((rgba & 0xFF00) << 16) | ((rgba & 0xFF) << 16) | ((rgba & 0xFF) << 8) | (rgba & 0xFF);
            else
                stroke = 0xFF000000 | rgba;
        }
        else
            stroke = rgba;
    }
    public void setAlpha(short alpha){
        stroke&=0xFFFFFF;
        alpha&=0xFF;
        stroke = (alpha << 24)|stroke;
    }
    //Sets whether or not the line should be drawn on top of everything
    public void setDepthWrite(boolean depthDisable){
        noDepth = depthDisable;
    }

    //Returns if the line should be drawn on top of everything
    public boolean returnDepthDisable(){
        return noDepth;
    }
    public int returnStroke(){
        return stroke;
    }

    //Returns hte endpoints of the line
    public float[][] returnEndPoints(){
        return endPoints;
    }
    
    //Returns the point that is in the middle of the line
    public float[] returnMidPoint(){
        float[] mid = {(endPoints[0][0]+endPoints[1][0])*0.5f,
                       (endPoints[0][1]+endPoints[1][1])*0.5f,
                       (endPoints[0][2]+endPoints[1][2])*0.5f};
        return mid;
    }

    //Returns the x-position of the midpoint
    public float returnMidX(){
        return (endPoints[0][0]+endPoints[1][0])*0.5f;
    }
    //Returns the y-position of the midpoint
    public float returnMidY(){
        return (endPoints[0][1]+endPoints[1][1])*0.5f;
    }
    //Returns the z-position of the midpoint
    public float returnMidZ(){
        return (endPoints[0][2]+endPoints[1][2])*0.5f;
    }

    //Returns a string representing the line
    public String toString(){
        String endPointsString = "END POINTS: {("+endPoints[0][0]+", "+endPoints[0][1]+", "+endPoints[0][2]+"), ";
        endPointsString+="("+endPoints[1][0]+", "+endPoints[1][1]+", "+endPoints[1][2]+")}\n";
        String strokeString = "COLOUR: 0x"+Integer.toHexString(stroke)+"\n";
        String noDepthString = "DRAWN ON TOP: "+noDepth+"\n";
        return endPointsString+strokeString+noDepthString;
    }

    //Returns if two lines are equal
    public boolean equals(Object o){
        if(o instanceof LineDisp){
            LineDisp l = (LineDisp)o;
            boolean isEquals = true;
            isEquals&=(Math.abs(endPoints[0][0] - l.endPoints[0][0]) > 0.0001);
            isEquals&=(Math.abs(endPoints[0][1] - l.endPoints[0][2]) > 0.0001);
            isEquals&=(Math.abs(endPoints[0][2] - l.endPoints[0][2]) > 0.0001);
            isEquals&=(Math.abs(endPoints[1][0] - l.endPoints[1][0]) > 0.0001);
            isEquals&=(Math.abs(endPoints[1][1] - l.endPoints[1][2]) > 0.0001);
            isEquals&=(Math.abs(endPoints[1][2] - l.endPoints[1][2]) > 0.0001);
            isEquals&=(stroke == l.stroke);
            isEquals&=(noDepth == l.noDepth);
            return isEquals;
        }
        else
            return false;
    }
    public boolean equals(LineDisp l){
        boolean isEquals = true;
        isEquals&=(Math.abs(endPoints[0][0] - l.endPoints[0][0]) > 0.0001);
        isEquals&=(Math.abs(endPoints[0][1] - l.endPoints[0][2]) > 0.0001);
        isEquals&=(Math.abs(endPoints[0][2] - l.endPoints[0][2]) > 0.0001);
        isEquals&=(Math.abs(endPoints[1][0] - l.endPoints[1][0]) > 0.0001);
        isEquals&=(Math.abs(endPoints[1][1] - l.endPoints[1][2]) > 0.0001);
        isEquals&=(Math.abs(endPoints[1][2] - l.endPoints[1][2]) > 0.0001);
        isEquals&=(stroke == l.stroke);
        isEquals&=(noDepth == l.noDepth);
        return isEquals;
    }
    //Copies a line object into the current object
    public void copy(Object o){
        if(o instanceof LineDisp){
            LineDisp l = (LineDisp)o;
            endPoints[0][0] = l.endPoints[0][0];
            endPoints[0][1] = l.endPoints[0][1];
            endPoints[0][2] = l.endPoints[0][2];
            endPoints[1][0] = l.endPoints[1][0];
            endPoints[1][1] = l.endPoints[1][1];
            endPoints[1][2] = l.endPoints[1][2];
            stroke = l.stroke;
            noDepth = l.noDepth;
        }
    }
    public void copy(LineDisp l){
        endPoints[0][0] = l.endPoints[0][0];
        endPoints[0][1] = l.endPoints[0][1];
        endPoints[0][2] = l.endPoints[0][2];
        endPoints[1][0] = l.endPoints[1][0];
        endPoints[1][1] = l.endPoints[1][1];
        endPoints[1][2] = l.endPoints[1][2];
        stroke = l.stroke;
        noDepth = l.noDepth;
    }
}

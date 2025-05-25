//A class for defining a mesh comprised of lines
public class LineModel {
    ModelVertices m;
    private int[][] lines = new int[1][2]; //Indices into the vertices in order to determine the endpoints of the line
    private int lineCount = 1; //The number of lines for the mesh
    private int[] stroke = {0xFF000000}; //The colour of the lines
    public LineModel(){
        float[][] vertices = {{-0.5f, 0, 0}, {0.5f, 0, 0}};
        m = new ModelVertices(vertices);
        lines[0][0] = 0;
        lines[0][1] = 1;
        lineCount = 1;
        stroke[0] = 0xFF000000;
    }
    public LineModel(float[][] newEnds, int[][] newLines, int[] rgba){
        if(newEnds.length >= 2 && newEnds[0].length >= 3 && newEnds[1].length >= 3)
            m = new ModelVertices(newEnds);
        else{
            System.out.println("ERROR: MUST HAVE AT LEAST TWO VERTICES WITH AT LEAST 3 DIMENTIONS");
            System.exit(1);
        }
        if(newLines.length >= 1 && newLines[0].length >= 2){
            lines = new int[newLines.length][2];
            lineCount = newLines.length;
            for(int i = 0; i < newLines.length; i++){
                lines[i][0] = newLines[i][0];
                lines[i][1] = newLines[i][1];
            }
        }
        else{
            System.out.println("ERROR: MUST HAVE AT LEAST TWO END POINTS");
            System.exit(1);
        }
        stroke = new int[lineCount];
        for(int i = 0; i < stroke.length; i++){
            int j = i%rgba.length;
            if((rgba[j] >>> 24) == 0){
                if(rgba[j] <= 0xFF)
                    stroke[i] = 0xFF000000 | (rgba[j] << 16) | (rgba[j] << 8) | rgba[j];
                else if(rgba[i] <= 0xFFFF)
                    stroke[i] = ((rgba[j] & 0xFF00) << 16) | ((rgba[j] & 0xFF) << 16) | ((rgba[j] & 0xFF) << 8) | (rgba[j] & 0xFF);
                else
                    stroke[i] = 0xFF000000 | rgba[j];
            }
            else
                stroke[i] = rgba[j];
        }
    }
    public LineModel(ModelVertices newM, int[][] newLines, int[] rgba){
        if(newM.returnVertices().length >= 2 && newM.returnVertices()[0].length >= 3 && newM.returnVertices()[1].length >= 3)
            m = newM;
        else{
            System.out.println("ERROR: MUST HAVE AT LEAST TWO VERTICES WITH AT LEAST 3 DIMENTIONS");
            System.exit(1);
        }
        if(newLines.length >= 1 && newLines[0].length >= 2){
            lines = new int[newLines.length][2];
            lineCount = newLines.length;
            for(int i = 0; i < newLines.length; i++){
                lines[i][0] = newLines[i][0];
                lines[i][1] = newLines[i][1];
            }
        }
        else{
            System.out.println("ERROR: MUST HAVE AT LEAST TWO END POINTS");
            System.exit(1);
        }
        stroke = new int[lineCount];
        for(int i = 0; i < stroke.length; i++){
            int j = i%rgba.length;
            if((rgba[j] >>> 24) == 0){
                if(rgba[j] <= 0xFF)
                    stroke[i] = 0xFF000000 | (rgba[j] << 16) | (rgba[j] << 8) | rgba[j];
                else if(rgba[i] <= 0xFFFF)
                    stroke[i] = ((rgba[j] & 0xFF00) << 16) | ((rgba[j] & 0xFF) << 16) | ((rgba[j] & 0xFF) << 8) | (rgba[j] & 0xFF);
                else
                    stroke[i] = 0xFF000000 | rgba[j];
            }
            else
                stroke[i] = rgba[j];
        }
    }
    //Constructs a set of vertices and a new set of max and min points
    public void setVertices(float[][] newEnds){
        if(newEnds.length >= 2 && newEnds[0].length >= 3 && newEnds[1].length >= 3){
            m = new ModelVertices(newEnds);
        }
        else{
            System.out.println("ERROR: MUST HAVE AT LEAST TWO END POINTS WITH AT LEAST 3 DIMENTIONS");
            System.exit(1);
        }
   }
   public void setVerticesPtr(ModelVertices newM){
    m = newM;
   } 
   //Sets the stroke of every line
    public void setStroke(int[] rgba){
        stroke = new int[lineCount];
        for(int i = 0; i < stroke.length; i++){
            int j = i%rgba.length;
            if((rgba[j] >>> 24) == 0){
                if(rgba[j] <= 0xFF)
                    stroke[i] = 0xFF000000 | (rgba[j] << 16) | (rgba[j] << 8) | rgba[j];
                else if(rgba[i] <= 0xFFFF)
                    stroke[i] = ((rgba[j] & 0xFF00) << 16) | ((rgba[j] & 0xFF) << 16) | ((rgba[j] & 0xFF) << 8) | (rgba[j] & 0xFF);
                else
                    stroke[i] = 0xFF000000 | rgba[j];
            }
            else
                stroke[i] = rgba[j];
        }
    }

   //Constructs a new set of lines
   public void setLines(int[][] newLines){
        if(newLines.length >= 1 && newLines[0].length >= 2){
            lines = new int[newLines.length][2];
            lineCount = newLines.length;
            for(int i = 0; i < newLines.length; i++){
                lines[i][0] = newLines[i][0];
                lines[i][1] = newLines[i][1];
            }
        }
        else{
            System.out.println("ERROR: MUST HAVE AT LEAST TWO END POINTS");
            System.exit(1);
        }
   }
   //Modifies the colour of one of the lines
   public void stroke(short r, short g, short b, int index){
        stroke[index] = 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
   }
   public void stroke(short r, short g, short b, short a, int index){
        stroke[index] = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
   }
   public void stroke(int rgb, short a, int index){
        if((rgb & 0xFFFFFF) <= 0xFF)
            stroke[index] = ((a & 255) << 24) | (rgb << 16) | (rgb << 8) | rgb;
        else
            stroke[index] = ((a & 255) << 24) | (rgb & 0xFFFFFF);
   }
   public void stroke(int rgba, int index){
        if((rgba >>> 24) == 0){
            if(rgba <= 0xFF)
                stroke[index] = 0xFF000000 | (rgba << 16) | (rgba << 8) | rgba;
            else if(rgba <= 0xFFFF)
                stroke[index] = ((rgba & 0xFF00) << 16) | ((rgba & 0xFF) << 16) | ((rgba & 0xFF) << 8) | (rgba & 0xFF);
            else
                stroke[index] = 0xFF000000 | rgba;
        }
        else
            stroke[index] = rgba;
   }
   //Returns the colour of a line
   public int returnStroke(int index){
        return stroke[index];
   }
   public int returnLineCount(){
        return lineCount;
   }
   public float[][] returnVertices(){
        return m.returnVertices();
   }
   public ModelVertices returnModelVerticesPtr(){
    return m;
  }
   //Returns the set of lines' endpoints
   public int[][] returnPoints(){
        return lines;
   }

   //Returns the min x, y, and z
   public float[] returnMinVertices(){
    return m.returnMinVertices();
   }
   //Returns the max x, y, and z
   public float[] returnMaxVertices(){
     return m.returnMaxVertices();
   }
   public float[] returnModelCentre(){
     return m.returnModelCentre();
   }

   //Returns the bounding box
   public float[][] returnBoundingBox(){
     return m.returnBoundingBox();
   }
   //Returns if two line models are equal
   public boolean equals(Object o){
        if(o instanceof LineModel){
            LineModel l = (LineModel)o;
            if(m.returnVertices().length != l.m.returnVertices().length || lines.length != l.lines.length || stroke.length != l.stroke.length)
                return false;
            boolean isEqual = true;
            isEqual&=(lineCount == l.lineCount);
            isEqual&=(m.equals(l.m));
            for(int i = 0; i < lines.length; i++){
                isEqual&=(lines[i][0] == l.lines[i][0]);
                isEqual&=(lines[i][1] == l.lines[i][1]);
            }
            for(int i = 0; i < stroke.length; i++)
                isEqual&=(stroke[i] == l.stroke[i]);
            return isEqual;
        }
        else
            return false;
    }
   public boolean equals(LineModel l){
        if(m.returnVertices().length != l.m.returnVertices().length || lines.length != l.lines.length || stroke.length != l.stroke.length)
            return false;
        boolean isEqual = true;
        isEqual&=(lineCount == l.lineCount);
        isEqual&=(m.equals(l.m));
        for(int i = 0; i < lines.length; i++){
            isEqual&=(lines[i][0] == l.lines[i][0]);
            isEqual&=(lines[i][1] == l.lines[i][1]);
        }
        for(int i = 0; i < stroke.length; i++)
            isEqual&=(stroke[i] == l.stroke[i]);
        return isEqual;
   }
   //Copies a line model into the current one
    public void copy(Object o){
        if(o instanceof LineModel){
            LineModel l = (LineModel)o;
            stroke = new int[l.stroke.length];
            for(int i = 0; i < stroke.length; i++)
                stroke[i] = l.stroke[i];
            m = l.m;
            lines = new int[l.lines.length][2];
            lineCount = l.lineCount;
            for(int i = 0; i < lines.length; i++){
                lines[i][0] = l.lines[i][0];
                lines[i][1] = l.lines[i][1];
            }
        }
    }
   public void copy(LineModel l){
        stroke = new int[l.stroke.length];
        for(int i = 0; i < stroke.length; i++)
            stroke[i] = l.stroke[i];
        m = l.m;
        lines = new int[l.lines.length][2];
        lineCount = l.lineCount;
        for(int i = 0; i < lines.length; i++){
            lines[i][0] = l.lines[i][0];
            lines[i][1] = l.lines[i][1];
        }
    }
}

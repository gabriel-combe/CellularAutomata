public class CA1D extends CellularAutomata{
    private int currentLine = 0;

    public CA1D(int width, int height, int scale, int knn){
        super("Cellular Automata 1D", width, height, scale, knn);
        
        if(knn%2 != 1){
            System.out.println("KNN should be odd");
            System.exit(0);
        }
        
        this.currentLine = 0;  
    }
    
    // Initialize the first line of the grid with a proba of 0.5 of being black or white
    @Override
    public void init(){
        for(int x = 0; x < width; x++)
            cells[x][0] = Math.random() < 0.5;
    }
    
    // Initialize the first line of the grid using the given proba
    @Override
    public void init(double density){
        for(int x = 0; x < width; x++)
            cells[x][0] = Math.random() < density;
    }

    // Apply the set of rules on the current line to get the next line
    @Override
    public void nextGeneration(){
        if(this.currentLine == this.height-1){
            this.isFinished = true;
            return;
        }

        int currentPattern = this.startingNeighbourPattern();
        this.cells[0][this.currentLine+1] = this.rules.nextState(currentPattern);
        
        for(int x = 1; x < this.width; x++){
            currentPattern = this.neighbourPattern(currentPattern, x);
            this.cells[x][this.currentLine+1] = this.rules.nextState(currentPattern);
        }
        
        this.currentLine++;
        updateGridImage();
    }

    // Get the neighbour pattern of the first cell of the line
    private int startingNeighbourPattern(){
        int pattern = 0;
        int offset = this.knn / 2;
        
        for(int i = -offset; i <= offset; i++){
            int column = i;

            // Check column to make the line wrap
            if(column < 0)
                column += this.width;
            else if(column >= this.width)
                column -= this.width;
            
            // encode the neighbour state
            if(this.cells[column][this.currentLine])
                pattern = pattern | 1;
            
            // left shift to let space for the next neighbour
            pattern = pattern << 1;
        }
        
        // unsigned right shift due to one to many right shift
        pattern = (pattern >>> 1) & ~(1 << this.knn);

        return pattern;
    }
    
    // Get the neighbour pattern of cells using the previous pattern
    private int neighbourPattern(int previousPattern, int index) {
        int pattern = (previousPattern << 1) & ~(1 << this.knn);
        
        int newBitIndex = index + (this.knn / 2);

        // Check column to make the line wrap
        if(newBitIndex >= this.width)
            newBitIndex -= this.width;
        
        // encode the neighbour state
        if(this.cells[newBitIndex][this.currentLine])
            pattern = pattern | 1;

        return pattern;
    }
}

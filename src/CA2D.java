public class CA2D extends CellularAutomata{
    
    public CA2D(int width, int height, int scale, int knn){
        super("Cellular Automata 2D", width, height, scale, knn);
        
        if(knn%2 != 1 || knn < 9 || (int)Math.sqrt(knn)*(int)Math.sqrt(knn) != knn){
            System.out.println("KNN should be odd, greater or equal to 9, and a squared number.");
            System.exit(0);
        }
    }

    // Initialise a rectangle of living cells at the centre of the grid
    public void rectangleCenter(int rwidth, int rheight){
        
        for(int i = 0; i < rwidth; i++)
            for(int j = 0; j < rheight; j++)
                this.cells[width/2 - rwidth/2 + i][height/2 - rheight/2 + j] = true;

        updateGridImage();
    }

    // Initialize the first line of the grid with a proba of 0.5 of being black or white
    @Override
    public void init() {
        for(int i = 0; i < this.width; i++)
            for(int j = 0; j < this.height; j++)
                cells[i][j] = Math.random() < 0.5;
        
        updateGridImage();
    }

    // Initialize the first line of the grid using the given proba
    @Override
    public void init(double density) {
        for(int i = 0; i < this.width; i++)
            for(int j = 0; j < this.height; j++)
                cells[i][j] = Math.random() < density;
        
        updateGridImage();
    }

    // Apply the set of rules on the current line to get the next line
    @Override
    public void nextGeneration()
    {
        boolean[][] newCells = new boolean[width][height];
        copyGrid(this.cells, newCells);
        
        for(int i = 0; i < this.width; i++){
            for(int j = 0; j < this.height; j++){
                int currentPattern = this.neighbourPattern(i, j);
                newCells[i][j] = this.rules.nextState(currentPattern);
            }
        }

        copyGrid(newCells, this.cells);
        
        updateGridImage();
    }
    
    // Get the neighbour pattern of cells using the previous pattern
    private int neighbourPattern(int x, int y) {
        int pattern = 0;
        int offset = (int) Math.sqrt(this.knn) / 2;
        
        for(int j = -offset+y; j <= offset+y; j++){
            int row = j;

            // Check row to make the patch wrap
            if(row < 0)
                row += this.height;
            else if(row >= this.height)
                row -= this.height;

            for(int i = -offset+x; i <= offset+x; i++){
                int column = i;
    
                // Check column to make the patch wrap
                if(column < 0)
                    column += this.width;
                else if(column >= this.width)
                    column -= this.width;

                // encode the neighbour state
                if(this.cells[column][row])
                    pattern = pattern | 1;
                
                // left shift to let space for the next neighbour
                pattern = pattern << 1;
            }
        }
        
        // unsigned right shift due to one to many right shift
        pattern = (pattern >>> 1) & ~(1 << this.knn);

        return pattern;
    }
}

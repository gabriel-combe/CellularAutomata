// Imports
import java.io.*;

public class LoadMap {
    private static boolean[][] grid;
    private static int currentGridRow;
    private static int currentGridColumn;
    private static int[] size;
    private static boolean terminate = false;

    // Load a map from an RLE file format
    public static void load(String map) throws IOException{
        // Load RLE file
        BufferedReader br = new BufferedReader(new FileReader(map));
        terminate = false;

        // Process the file line by line
        while(!terminate){
            // Read a line from the file
            String line = br.readLine();

            // Remove comments
            if(line.charAt(0) == '#') continue;

            // Process grid size
            if(line.charAt(0) == 'x'){
                size = processSize(line);

                // Set grid size (double the processed size)
                grid = new boolean[2*size[0]][2*size[1]];

                // Set initial start
                currentGridRow = size[1]/2;
                currentGridColumn = size[0]/2;
                
                continue;
            }
            
            // Process each grid line
            // Format [number or blank][b(for blank) or o(for filled)]*
            processGridLine(line);
        }
        br.close();
    }

    private static void processGridLine(String line){
        int charIndex = 0;
        String fillNumber = "";

        while(charIndex < line.length()){
            char symbol = line.charAt(charIndex);

            switch (symbol) {
                case '!':
                    terminate = true;
                    break;

                case '$':
                    currentGridRow += processBlank(fillNumber);
                    currentGridColumn = size[0]/2;
                    fillNumber = "";
                    break;

                case 'o':
                    fillState(fillNumber, true);
                    fillNumber = "";
                    break;

                case 'b':
                    fillState(fillNumber, false);
                    fillNumber = "";
                    break;

                default:
                    fillNumber += symbol;
                    break;
            }

            charIndex++;
        }
    }

    private static void fillState(String fillNumber, boolean state){
        int length = 1;
        
        if(!fillNumber.isEmpty()) length = Integer.parseInt(fillNumber);

        for(int index = 0; index < length; index++)
            grid[currentGridColumn++][currentGridRow] = state;  
    }

    private static int processBlank(String blankNumber){
        int length = 1;
        
        if(!blankNumber.isEmpty()) length = Integer.parseInt(blankNumber);

        return length;
    }

    private static int[] processSize(String line){
        int[] size = new int[2];

        String textRemovedSize = line.replaceAll("[a-z= /]*", "");
        String[] splitSize = textRemovedSize.split(",");

        try {
            size[0] = Integer.parseInt(splitSize[0]);
            size[1] = Integer.parseInt(splitSize[1]);
        } catch (NumberFormatException e) {
            System.out.println("Your RLE file has not the right format: " + e);
        }

        return size;
    }

    public static boolean[][] getGrid(){
        return grid;
    }
}

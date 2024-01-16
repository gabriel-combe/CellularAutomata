import java.util.ArrayList;
import java.util.Random;

public class Rule {
    private int knn, ruleSize;

    private int[] rules;

    private ArrayList<Integer> bornRule;
    private ArrayList<Integer> surviveRule;

    private static Random rand = new Random();

    public Rule(int knn){
        this.knn = knn;
        this.ruleSize = (int) Math.pow(2, this.knn - 5);
        this.ruleSize = this.ruleSize == 0 ? 1 : this.ruleSize;
        
        this.rules = new int[this.ruleSize];
        this.rules = randomRules(this.rules.length);

        this.bornRule = new ArrayList<Integer>();
        this.surviveRule = new ArrayList<Integer>();
    }

    // Generate random rules (Integer format)
    public static int[] randomRules(int length){
        int[] rules = new int[length];
        for(int i = 0; i < length; i++)
            rules[i] = rand.nextInt();
        return rules;
    }

    // Display the set of rules (Integer format)
    public void printRules(){
        for(int rule : this.rules)
            System.out.print(rule + " ");
        System.out.println();
    }

    // Set the rules of the cellular automata
    public void setRules(int[] newRules){
        assert this.rules.length == newRules.length;
        this.rules = newRules;    
    }

    // Retrieve the new state corresponding to the pattern
    protected boolean nextState(int pattern){
        int subRuleIndex = pattern >>> 5;
        int stateIndex = pattern & 31;
        boolean newState = ((this.rules[subRuleIndex] >>> stateIndex) & 1) == 1;
        return newState;
    }

    private void processRule(int config, int cellState){
        int msb = config >>> (this.knn/2);
        int lsb = config & ((int) Math.pow(2, this.knn/2) - 1);

        int pattern = (msb << ((this.knn/2) + 1)) | (cellState << (this.knn/2)) | lsb;

        int subRuleIndex = pattern >>> 5;
        int stateIndex = pattern & 31;

        this.rules[subRuleIndex] = this.rules[subRuleIndex] | (1 << stateIndex);
    }

    private void permutation(int permut, int kSwap, int nSpace, int cellState){
        
        processRule(permut, cellState);
        
        if(kSwap==0) return;

        for(int i = 0; i < nSpace; i++){
            permut += (int) Math.pow(2, kSwap-1+i);
            permutation(permut, kSwap-1, i+1, cellState);
        }
    }

    public void genRules(String stringRule) throws Exception{
        this.rules = new int[this.ruleSize];

        parseRuleString(stringRule);

        for (Integer born : this.bornRule)
            permutation((int)Math.pow(2, born)-1, born, this.knn-1-born, 0);

        for (Integer survive : this.surviveRule)
            permutation((int)Math.pow(2, survive)-1, survive, this.knn-1-survive, 1);
    }

    private void parseRuleString(String stringRule) throws Exception{
        String[] bornSurvive = stringRule.split("/");
        String[] bornRuleString;
        String[] surviveRuleString;

        if(bornSurvive.length != 2)
            throw new Exception("Error, too many '/' separator (only 1 required).");
        
        if(bornSurvive[0].charAt(0) != 'B' && bornSurvive[1].charAt(0) != 'B')
            throw new Exception("Born identifier ('B') was not found.");
        
        if(bornSurvive[0].charAt(0) != 'S' && bornSurvive[1].charAt(0) != 'S')
            throw new Exception("Survive identifier ('S') was not found.");

        if(bornSurvive[0].charAt(0) == bornSurvive[1].charAt(0))
            throw new Exception("Same identifier used twice.");

        if(bornSurvive[0].charAt(0) == 'B'){
            bornRuleString = bornSurvive[0].substring(1).split(",");
            surviveRuleString = bornSurvive[1].substring(1).split(",");
        }else{
            bornRuleString = bornSurvive[1].substring(1).split(",");
            surviveRuleString = bornSurvive[0].substring(1).split(",");
        }

        for (String number : bornRuleString) {
            if(number.equals("")) break;

            int neighbourCount = Integer.parseInt(number);

            if(neighbourCount < 0 || neighbourCount >= this.knn)
                throw new Exception("Neighbour count needs to be positive and less than " + this.knn);

            this.bornRule.add(neighbourCount);
        }

        for (String number : surviveRuleString) {
            if(number.equals("")) break;
            
            int neighbourCount = Integer.parseInt(number);

            if(neighbourCount < 0 || neighbourCount >= this.knn)
                throw new Exception("Neighbour count needs to be positive and less than " + this.knn);

            this.surviveRule.add(neighbourCount);
        }
    }
}

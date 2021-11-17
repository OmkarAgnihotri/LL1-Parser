import java.util.*;

public class LL1Parser {
    public static void main(String args[]){

//        Rule rule0 = new Rule("S", new ArrayList<String>(){{
//            add("A C B");add("C b B");add("B a");
//        }});
//        Rule rule1 = new Rule("A", new ArrayList<String>(){{
//            add("d a");add("B C");
//        }});
//        Rule rule2 = new Rule("B", new ArrayList<String>(){{
//            add("g");add("");
//        }});
//        Rule rule3 = new Rule("C", new ArrayList<String>(){{
//            add("h");add("");
//        }});
//
//        List<Rule> rules = new ArrayList<>();
//        rules.add(rule0);
//        rules.add(rule1);
//        rules.add(rule2);
//        rules.add(rule3);
        Rule rule0 = new Rule("E", new ArrayList<String>(){{
            add("T E'");
        }});
        Rule rule1 = new Rule("E'", new ArrayList<String>(){{
            add("+ T E'");add("");
        }});
        Rule rule2 = new Rule("T", new ArrayList<String>(){{
            add("F T'");
        }});
        Rule rule3 = new Rule("T'", new ArrayList<String>(){{
            add("* F T'");add("");
        }});
        Rule rule4 = new Rule("F", new ArrayList<String>(){{
            add("( E )");add("id");
        }});

        List<Rule> rules = new ArrayList<>();
        rules.add(rule0);
        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        rules.add(rule4);

        Grammar g = new Grammar(rules);

        g.print();

        Util util = new Util();

        System.out.println("\nGrammar after removing left recursion \n");
        util.removeLeftRecursion(g);

        for(Rule rule : g.rules){
            if(rule.first == null){
                util.FIRST(rule, g);
            }
        }

        System.out.println("\nFIRST sets of the grammar are : \n");
        g.printFIRST();

        g.rules.get(0).follow.add("$");
        util.FOLLOW(g);

        System.out.println("\nFOLLOW sets of the grammar are : \n");
        g.printFOLLOW();

        util.buildParseTable(g);

        util.findTerminalsAndNonTerminals(g);

        System.out.println("\nParse tree for the Grammar and input string : \n");
        try {
            List<String[]> parseTree = util.parse(g, "id + id * id $");
            for(String[] tuple : parseTree){
                if(tuple[1].length() > 0)System.out.println(tuple[0] + " => " + tuple[1]);
                else System.out.println(tuple[0] + " => " + "\u03B5");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

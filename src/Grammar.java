import java.util.*;

public class Grammar {
    List<Rule> rules;

    Set<String> terminals = new HashSet<>(), nonTerminals = new HashSet<>();

    Map2D<String, String, Integer[]> parseTable;
    
    Grammar (List<Rule> rules){
        this.rules = rules;
    }

    void print(){
        for(Rule rule : rules){
            System.out.println(rule.toString());
        }
    }

    Rule getRule(String lhs){
        for(Rule rule : this.rules){
            if(rule.lhs.equals(lhs))return rule;
        }

        return null;
    }

    void printFIRST(){
        for(Rule rule : this.rules){
            if(rule.first == null){
                System.out.println("first not found");
                continue;
            }
            System.out.print(rule.lhs + " { ");
            for(String symbol : rule.first){
                if(symbol.length() == 0)System.out.print("\u03B5" + " ");
                else System.out.print(symbol + " ");
            }
            System.out.print("} ");
            System.out.println();
        }
    }

    void printFOLLOW(){
        for(Rule rule : this.rules){
            if(rule.first == null){
                System.out.println("first not found");
                continue;
            }
            System.out.print(rule.lhs + " { ");
            for(String symbol : rule.follow){
                if(symbol.length() == 0)System.out.print("\u03B5" + " ");
                else System.out.print(symbol + " ");
            }
            System.out.print("}");
            System.out.println();
        }
    }

    void printParseTable(){

        if(this.parseTable == null)return;

        for(String nonTerminal : this.nonTerminals){

            for(String terminal : this.terminals){
                System.out.print(
                        nonTerminal + " " + terminal + " => "
                );

                Integer[] val = this.parseTable.get(nonTerminal, terminal);

                if(val != null){
                    String rule = this.rules.get(val[0])
                                    .rhs.get(val[1]);

                    if(rule.length() > 0)System.out.println(rule);
                    else System.out.println("\u03B5");
                }
                else System.out.println(val);
            }
        }
    }
}

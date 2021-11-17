
import java.util.*;

public class Util {

    private String enumerate(List<String> list){
        String ret = "";

        for(int i = 1;i < list.size(); i++){
            ret += list.get(i);

            if(i == list.size() - 1)continue;

            ret += " ";
        }

        return ret;
    }

    private List<String> addNewRules(String src, Rule dest){
        List<String> newRules = new ArrayList<>();

        for(String production : dest.rhs){

            if(src.equals("")){
                newRules.add(production);
                continue;
            }


            List<String> symbols = new ArrayList<>(Arrays.asList(src.split(" ")));
            if(!production.equals(""))symbols.add(1, production);

            newRules.add(enumerate(symbols));
        }

        return newRules;
    }

    private boolean isTerminal (String production){
        // NULL is a production
        if(production.length() == 0)return true;

        char first = production.charAt(0);

        if('A' <= first && first <= 'Z')return false;

        return true;
    }

    private String compliment(String s){
        if(s.length() > 4)System.exit(0);
        return s + "'";

    }

    void removeLeftRecursion(Grammar g){

        List<String> seen = new ArrayList<>();
        for (int i = 0; i < g.rules.size(); i++){
            Rule rule = g.rules.get(i);
            List<Integer> indexesToBeRemoved = new ArrayList<>();

            List<Integer> left = new ArrayList<>();

            HashMap<String, Integer> visited = new HashMap<>();

            for (int j = 0;j < rule.rhs.size(); j++){

                String production = rule.rhs.get(j);

                // NULL production
                if(visited.get(production) != null)continue;

                if(production.length() == 0){
                    visited.put(production, 1);
                    continue;
                }

                String first = production.split(" ")[0];

                if(isTerminal(first)){
                    visited.put(production, 1);
                    continue;
                }

                if(first.equals(rule.lhs)){
                    // do nothing as of now
                }
                else {
                    boolean flag = false;
                    for(String symbol : seen){
                        if(symbol.equals(first)){
                            flag = true;
                            break;
                        }
                    }

                    if(flag){
                        List<String> newProductions = addNewRules(production, g.getRule(first));

                        for(String each : newProductions){
                            if(rule.lhs.equals(each))continue;
                            if(visited.get(each) == null)rule.rhs.add(each);
                        }

                        indexesToBeRemoved.add(j);
                    }
                }


            }

            indexesToBeRemoved.sort(Collections.reverseOrder());
            for(Integer index : indexesToBeRemoved)rule.rhs.remove((int)index);

            for(int j = rule.rhs.size() - 1;j >= 0;j--){

                String curr = rule.rhs.get(j);
                if(rule.lhs.equals(curr.split(" ")[0])){
                    left.add(j);
                }
            }

            if(left.size() > 0){
                Rule newRule = new Rule(compliment(rule.lhs), null);
                List<String> newProductions = new ArrayList<>();

                for(Integer index : left){
                    String symbols[] = rule.rhs.get(index).split(" ");
                    String newProduction = "";

                    for(int k = 1;k < symbols.length;k++){
                        newProduction += symbols[k] + " ";
                    }
                    newProduction += newRule.lhs;
                    if(!newRule.lhs.equals(newProduction))newProductions.add(newProduction);

                    rule.rhs.remove((int)index);
                }

                newRule.rhs = newProductions;

                for(int k = 0;k < rule.rhs.size();k++){
                    if(rule.rhs.get(k).length() > 0)rule.rhs.set(k, rule.rhs.get(k) + " " + newRule.lhs);
                    else rule.rhs.set(k, newRule.lhs);
                }

                newRule.rhs.add("");

                g.rules.add(i + 1, newRule);

            }

            seen.add(rule.lhs);
        }
        g.print();
    }

    Set<String> FIRST(Rule rule, Grammar g){
//        System.out.println(rule.lhs);
        Set<String> s = new HashSet<>();

        if(rule.first != null)return rule.first;

        for(String each : rule.rhs){
            if(each.length() == 0 ){
                s.add("");
                continue;
            }
            String symbols[] = each.split(" ");

            if(isTerminal(symbols[0])){
                s.add(symbols[0]);
                continue;
            }

            // first symbol is Non terminal
            for(int i = 0;i < symbols.length;i++){
                if(isTerminal(symbols[i])){
                    s.add(symbols[i]);
                    break;
                }

                Rule next = g.getRule(symbols[i]);

                Set<String> ret = FIRST(next, g);
                s.addAll(ret);

                if(!ret.contains("")){
                    break;
                }
            }

        }

        rule.first = s;

        return s;
    }

    private boolean isNullable(int start, int end, String[] seq, Grammar g){

        for(int i = start; i <= end;i++){
            if(isTerminal(seq[i]))return false;

            Rule curr = g.getRule(seq[i]);

            if(!curr.first.contains(""))return false;
        }

        return true;
    }

    private int followCount(Grammar g){
        int count = 0;
        for(Rule rule : g.rules){
            count += rule.follow.size();
        }

        return count;
    }

    void FOLLOW(Grammar g){
        int prevcount = 0;
        while(true){
            for(Rule rule : g.rules){
                for(String production : rule.rhs){

                    if(production.length() == 0){
                        continue;
                    }

                    String symbols[] = production.split(" ");
                    int n = symbols.length;

                    for(int i = 0;i < n;i++){

                        Rule left = g.getRule(symbols[i]);

                        for(int j = i + 1;j < n;j++){
                            if(isTerminal(symbols[i]))continue;

                            if(isNullable(i + 1, j - 1, symbols, g)){
                                if(isTerminal(symbols[j]))left.follow.add(symbols[j]);
                                else {
                                    Rule right = g.getRule(symbols[j]);
//                                    System.out.println(symbols[i] + " " + symbols[j]);
                                    left.follow.addAll(right.first);
                                }
                            }
                        }

                        if(left != null && isNullable(i + 1, n - 1, symbols, g)){
                            left.follow.addAll(rule.follow);
                        }
                    }
                }
            }
//            g.printFOLLOW();

            int itercount = followCount(g);

            if(itercount > prevcount){
                prevcount = itercount;
            }
            else break;

        }

        for ( Rule rule : g.rules){
            rule.follow.remove("");
        }
    }

    void findTerminalsAndNonTerminals(Grammar g){
        for(Rule rule : g.rules){
            g.nonTerminals.add(rule.lhs);
            for(String production : rule.rhs){
                if(production.length() == 0){
                    g.terminals.add("");
                    continue;
                }
                String[] symbols = production.split(" ");

                for(String symbol : symbols){
                    if(isTerminal(symbol))g.terminals.add(symbol);
                    else g.nonTerminals.add(symbol);
                }
            }
        }

        g.terminals.add("$");
    }

    public void buildParseTable(Grammar g){
        Map<String, Integer> terminalMapping = new HashMap(),
                                nonTerminalMapping = new HashMap<>();

        Map2D<String, String, Integer[]> table = new Map2D<>();

        try {
            for(int i = 0;i < g.rules.size(); i++){
                Rule rule = g.rules.get(i);
                for(int j = 0;j < rule.rhs.size(); j++){
                    String production = rule.rhs.get(j);

                    Set<String> first = new HashSet<>();
                    String[] symbols = production.split(" ");

                    for (String symbol : symbols){
                        if(!isTerminal(symbol)){
                            Rule r = g.getRule(symbol);

                            Set<String> ret = r.first;
                            first.addAll(ret);
                            if(!ret.contains(""))break;
                        }
                        else {
                            first.add(symbol);
                            break;
                        }
                    }


                    for(String symbol : first){
                        if(symbol.length() == 0){
                            continue;
                        }
                        else {
                            if(isTerminal(symbol)){
                                if(table.get(rule.lhs, symbol) == null){
                                    table.put(rule.lhs, symbol, new Integer[]{i, j});
                                }
                                else throw new Exception("The grammar is not LL(1)" + i + " " + j);
                            }
                        }
                    }

                    if(first.contains("")){

                        for(String symbol : rule.follow){
                            if(table.get(rule.lhs, symbol) == null){
                                table.put(rule.lhs, symbol, new Integer[]{i, j});
                            }
                            else {
                                throw new Exception("The grammar is not LL(1)" + i + " " + j);
                            }
                        }
                    }
                }
            }

            g.parseTable = table;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    List<String[]> parse(Grammar g, String ip) throws Exception{
        Stack<String> stack = new Stack<>();

        stack.push("$");
        stack.push(g.rules.get(0).lhs);

        List<String[]> output = new ArrayList<>();

        String[] input = ip.split( " ");

        int ptr = 0;
        while(stack.peek() != "$"){
            String top = stack.peek();

            if(isTerminal(top) || top.equals("$")){
                if(top.equals(input[ptr])){
                    stack.pop();
                    ptr++;
                }
                else if(top.equals("")){
                    stack.pop();
                }
                else throw new Exception("Input string cannot be parsed using the provided grammar!");
            }
            else {
                if(g.parseTable.get(top, input[ptr]) != null){
                    stack.pop();

                    Integer[] val = g.parseTable.get(top, input[ptr]);
                    int i = val[0], j = val[1];

                    String production = g.rules.get(i).rhs.get(j);

                    String[] symbols = production.split(" ");

                    for(int k = symbols.length - 1;k >= 0; k--){
                        stack.push(symbols[k]);
                    }

                    output.add(new String[]{top, production});
                }
                else throw new Exception("Input string cannot be parsed using the provided grammar!");
            }
        }

        return output;
    }
}

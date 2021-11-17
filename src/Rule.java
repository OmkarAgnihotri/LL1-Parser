import java.util.*;

public class Rule {
    String lhs;
    List<String> rhs;

    Set<String> first, follow = new HashSet<>();

    Rule(String lhs, List<String> rhs){
        this.lhs = lhs;
        this.rhs = rhs;
    }



    public String toString(){

        String segment = "";
        segment += lhs + " => ";

        for(int i = 0; i < rhs.size();i++){
            if(rhs.get(i).equals(""))segment += "\u03B5";
            else segment += rhs.get(i);

            if(i == rhs.size() - 1)continue;
            segment += " | ";
        }

        return segment;
    }



}

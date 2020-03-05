package cs2731.hw2;

import java.io.IOException;

public class ProbCKY {
    public static void main(String[] args) throws IOException {
//        PCKY pcky = new PCKY(args[0], args[1], args[2]);
        PCKY pcky = new PCKY("src/pcfg.txt", "I book a flight to Houston", "[S [NP [Pronoun I]] [VP [Verb book] [NP [Det a] [Nominal [Noun flight]]] [PP [Preposition to] [NP [Proper-Noun houston]]]]]");
    }
}

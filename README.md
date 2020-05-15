# CS 2731 NLP - PCKY

## Introduction

CS 2731 Natural Language Processing Assignment 2 - Probabilistic CKY Parsing.

## Timeline

- Start Date: 2019/09/26
- Last Update: 2019/10/10

## GitHub Link

https://github.com/zecwang/CS2731-NLP-ProbCKY

<!--more-->

## Related Materials

Read "slp12_f19.pdf" and "slp13_f20.pdf".

## Demo

```zsh
$ java -cp ZechenWang.jar cs2731.hw2.ProbCKY pcfg.txt "Book the flight through Houston" "[S [VP [Verb Book] [NP[Det the][Nominal [Noun flight][PP[Preposition through][NP[Proper-Noun Houston]]]]]]]"
Sentence accepted
Sentence Probability: 5.102999999999999E-7

[S[VP[VP[Verb book][NP[Det the][Nominal[Noun flight]]]][PP[Preposition through][NP[Proper-Noun houston]]]]]
1.0934999999999997E-7
Precision: 0.5714285714285714
Recall: 0.6666666666666666

[S[VP[Verb book][NP[Det the][Nominal[Noun flight]]][PP[Preposition through][NP[Proper-Noun houston]]]]]
3.644999999999999E-7
Precision: 0.6666666666666666
Recall: 0.6666666666666666

[S[VP[Verb book][NP[Det the][Nominal[Nominal[Noun flight]][PP[Preposition through][NP[Proper-Noun houston]]]]]]]
3.6450000000000005E-8
Precision: 0.8571428571428571
Recall: 1.0
```

```zsh
$ java -cp ZechenWang.jar cs2731.hw2.ProbCKY pcfg.txt "The flight includes a meal" "[S[NP[Det The][Nominal[Noun flight]]][VP[Verb includes][NP[Det a][Nominal[Noun meal]]]]]"
Sentence accepted
Sentence Probability: 2.9159999999999997E-6

[S[NP[Det the][Nominal[Noun flight]]][VP[Verb includes][NP[Det a][Nominal[Noun meal]]]]]
2.9159999999999997E-6
Precision: 1.0
Recall: 1.0
```

```zsh
$ java -cp ZechenWang.jar cs2731.hw2.ProbCKY pcfg.txt "I book a flight to Houston" "[S [NP [Pronoun I]] [VP [Verb book] [NP [Det a] [Nominal [Noun flight]]] [PP [Preposition to] [NP [Proper-Noun houston]]]]]"
Sentence accepted
Sentence Probability: 3.429215999999999E-6

[S[NP[Pronoun i]][VP[VP[Verb book][NP[Det a][Nominal[Noun flight]]]][PP[Preposition to][NP[Proper-Noun houston]]]]]
7.348319999999998E-7
Precision: 0.875
Recall: 1.0

[S[NP[Pronoun i]][VP[Verb book][NP[Det a][Nominal[Noun flight]]][PP[Preposition to][NP[Proper-Noun houston]]]]]
2.449439999999999E-6
Precision: 1.0
Recall: 1.0

[S[NP[Pronoun i]][VP[Verb book][NP[Det a][Nominal[Nominal[Noun flight]][PP[Preposition to][NP[Proper-Noun houston]]]]]]]
2.4494399999999997E-7
Precision: 0.75
Recall: 0.8571428571428571
```


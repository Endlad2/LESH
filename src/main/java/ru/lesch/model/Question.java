package ru.lesch.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
@JsonSubTypes.Type(value = Question.Single.class,    name = "single"),
@JsonSubTypes.Type(value = Question.TrueFalse.class, name = "truefalse"),
@JsonSubTypes.Type(value = Question.Short.class,     name = "short"),
@JsonSubTypes.Type(value = Question.Open.class,      name = "open"),
@JsonSubTypes.Type(value = Question.Match.class,     name = "match")
})
public abstract class Question {

public String prompt;

public abstract int maxScore();

public static class Single extends Question {
public List<String> options;
public int correct;
@Override public int maxScore() { return 1; }
}

public static class TrueFalse extends Question {
public boolean correct;
@Override public int maxScore() { return 1; }
}

public static class Short extends Question {
public List<String> answers;
@Override public int maxScore() { return 1; }
}

public static class Open extends Question {
public List<String> keywords;
public int minHits = 1;
@Override public int maxScore() { return 1; }
}

public static class Match extends Question {
public List<String> left;
public List<String> right;
public List<Integer> correct;
@Override public int maxScore() { return 1; }
}
}
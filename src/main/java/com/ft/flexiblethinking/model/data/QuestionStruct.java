package com.ft.flexiblethinking.model.data;

public class QuestionStruct {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getCasein() {
        return casein;
    }

    public void setCasein(String casein) {
        this.casein = casein;
    }

    public String getCaseout() {
        return caseout;
    }

    public void setCaseout(String caseout) {
        this.caseout = caseout;
    }

    private String title;
    private String problem;
    private String casein;
    private String caseout;

    public QuestionStruct(){}
}

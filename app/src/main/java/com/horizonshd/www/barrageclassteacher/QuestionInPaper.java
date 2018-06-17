/**
 * 试卷中的试题类
 */
package com.horizonshd.www.barrageclassteacher;

public class QuestionInPaper {
    private String description;
    private String optiona;
    private String optionb;
    private String optionc;
    private String optiond;
    private String answer;

    public QuestionInPaper(String description,String optiona,String optionb,String optionc,String optiond,String answer){
        this.description = description;
        this.optiona = optiona;
        this.optionb = optionb;
        this.optionc = optionc;
        this.optiond = optiond;
        this.answer = answer;
    }

    public String getDescription() {
        return description;
    }

    public String getOptiona() {
        return optiona;
    }

    public String getOptionb() {
        return optionb;
    }

    public String getOptionc() {
        return optionc;
    }

    public String getOptiond() {
        return optiond;
    }

    public String getAnswer() {
        return answer;
    }
}

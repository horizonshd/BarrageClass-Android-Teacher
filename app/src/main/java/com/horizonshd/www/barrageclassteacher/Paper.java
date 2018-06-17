/**
 * 试卷类
 */
package com.horizonshd.www.barrageclassteacher;

public class Paper {
    private String paperid;
    private String papername;

    public Paper(String paperid,String papername){
        this.paperid = paperid;
        this.papername = papername;
    }

    public String getPaperid() {
        return paperid;
    }

    public String getPapername() {
        return papername;
    }
}

package com.samsung.SMT.lang.smtshellwatch.watch;

import android.view.View;

public class SMTCapability {
    private final String title;
    private final String desc;
    private final String btnText;

    private final View.OnClickListener onClick;

    public SMTCapability(String title, String desc, String btnText, View.OnClickListener onClick) {
        this.title = title;
        this.desc = desc;
        this.btnText = btnText;
        this.onClick = onClick;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getBtnText() {
        return btnText;
    }

    public View.OnClickListener getOnClick() { return onClick;}

}

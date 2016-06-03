/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.filter.session_historical;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * body内に指定されたURLにセッション履歴IDを付与する。
 * bodyタグを省略もしくは、isIdOnly属性を記述した場合は、
 * セッション履歴IDパラメタのみを出力する。
**/
public class SessionHistoricalIdTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	public int doStartTag() throws JspException {
        sessionHistoricalId = (String)pageContext.findAttribute("lastHistricalSessionId");
        isWriting = false;
        try {
            if(isIdOnly != null) {
                JspWriter writer = pageContext.getOut();
                isWriting = true;
                writer.print(getHistoricalIdParameter());
                return SKIP_BODY;
            } else {
                return EVAL_BODY_BUFFERED;
            }
        } catch(Exception ex) {
            throw new JspException(ex);
        }
    }

    public int doAfterBody() throws JspException {
        try {
            BodyContent bc = getBodyContent();
            final String content = bc.getString();
            JspWriter writer = bc.getEnclosingWriter();
            if(content.trim().length() == 0) {
                isWriting = true;
                writer.print(getHistoricalIdParameter());
            } else {
                if(content.indexOf("?") == -1) {
                    writer.print(content + "?" + getHistoricalIdParameter());
                } else {
                    writer.print(content + "&" + getHistoricalIdParameter());
                }
                isWriting = true;
            }
            bc.clearBody();
            return SKIP_BODY;
        } catch(Exception ex) {
            throw new JspException(ex);
        }
    }

    public int doEndTag() throws JspException {
        try {
            if(!isWriting) {
                JspWriter writer = pageContext.getOut();
                writer.print(getHistoricalIdParameter());
            }
            return SKIP_BODY;
        } catch(Exception ex) {
            throw new JspException(ex);
        }
    }

    public void setIsIdOnly(String isIdOnly) {
        this.isIdOnly = isIdOnly;
    }

    public void release() {
        this.isIdOnly = null;
        this.sessionHistoricalId = null;
    }

    private String getHistoricalIdParameter() {
        sessionHistoricalId = (sessionHistoricalId == null) ? "" : sessionHistoricalId;
        if(sessionHistoricalId.length() != 0) {
            return "histricalId=" + sessionHistoricalId;
        } else {
            return "";
        }
    }

    private String sessionHistoricalId = null;
    private String isIdOnly = null;
    private boolean isWriting;
}
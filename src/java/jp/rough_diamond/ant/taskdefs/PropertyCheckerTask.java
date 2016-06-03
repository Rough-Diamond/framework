/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.ant.taskdefs;

import jp.rough_diamond.tools.property_checker.PropertyChecker;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class PropertyCheckerTask extends Task {
    private String base;
    private String target;

    public String getBase() {
        return base;
    }
    public void setBase(String base) {
        this.base = base;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    @Override
    public void execute() throws BuildException {
        try {
            PropertyChecker pc = new PropertyChecker(getBase(), getTarget());
            if(!pc.doIt()) {
                throw new BuildException();
            }
        } catch(BuildException e) {
            throw e;
        } catch(Exception e) {
            throw new BuildException(e);
        }
    }
}

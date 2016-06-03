/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.testdata;

import jp.rough_diamond.commons.testing.DBInitializer;
import jp.rough_diamond.commons.testing.ResourceNames;
import jp.rough_diamond.framework.service.ServiceLocator;

@ResourceNames(resources = {"UnitLoader", "NumberingLoader"})
public class Resetter extends DBInitializer {
    public static void main(String[] args) throws Exception {
        Resetter resetter = ServiceLocator.getService(Resetter.class);
        resetter.cleanInsert();
        System.out.println("-------------- Finish --------------");
    }
}

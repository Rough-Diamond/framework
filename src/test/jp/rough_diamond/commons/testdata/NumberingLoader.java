/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.testdata;

import jp.rough_diamond.commons.testing.DBInitializer;
import jp.rough_diamond.commons.testing.ResetterType;
import jp.rough_diamond.commons.testing.ResourceNames;

@ResourceNames(resources = {"NUMBERING.xls"})
@ResetterType(type=Resetter.class)
public class NumberingLoader extends DBInitializer {
}

/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.ant.taskdefs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.DirectoryScanner;

public class FileScanUtil {
	public static DirectoryScanner getDirectoryScanner(String file) {
		if(file == null) {
			return null;
		}
        System.out.println(file);
		try {
            File base = new File(file);
            StringBuffer pattern = new StringBuffer();
            String delimiter = "";
            Map<File, String> map = new HashMap<File, String>();
            File lastWildCardFile = null;
            File f = base;
            while(f.getParentFile() != null) {
                if(!".".equals(f.getName())) {
                    pattern.insert(0, delimiter);
                    pattern.insert(0, f.getName());
                    delimiter = "/";
                    map.put(f, pattern.toString());
                    if(f.getName().indexOf('*') != -1) {
                        lastWildCardFile = f;
                    }
                }
                f = f.getParentFile();
            }
            String includes;
            File rootDir;
            if(lastWildCardFile == null) {
                rootDir =  base.getParentFile();
                includes = base.getName();
            } else {
                rootDir = lastWildCardFile.getParentFile();
                includes = map.get(lastWildCardFile);
            }
            includes = includes.replaceAll("^\\./", "");
            includes = includes.replaceAll("/\\./", "/");
            System.out.println(rootDir.getAbsolutePath());
            System.out.println(includes);

            DirectoryScanner ds = new DirectoryScanner();
            ds.setBasedir(rootDir);
            ds.setIncludes(new String[]{includes});
            ds.scan();
            return ds;
		} catch(RuntimeException re) {
			re.printStackTrace();
			throw re;
		}
	}
}

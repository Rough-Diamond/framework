/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.ant.taskdefs;

import java.io.File;

import jp.rough_diamond.tools.beangen.JavaBeansGenerator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class BeanGenerator extends Task {
	private String root;
//	private List<FileSet> fileSets = new ArrayList<FileSet>();
	private String input;
	private String encoding = "Shift_JIS";
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
//	public void addFileSet(FileSet set) {
//		log("call addFileSet...");
//		fileSets.add(set);
//	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void execute() throws BuildException {
		try {
	    	String root = getRoot();
	    	if(root == null) {
	    		throw new BuildException("root is not specification.");
	    	}
	    	File rootF = new File(root);
	    	if(!rootF.exists()) {
	    		throw new BuildException(rootF.getCanonicalPath() + "is not exists.");
	    	}
	    	String input = getInput();
	    	if(input == null) {
	    		throw new BuildException("input is not specification.");
	    	}
	    	File inputF = new File(input);
	    	if(!inputF.exists()) {
	    		throw new BuildException(inputF.getCanonicalPath() + "is not exists.");
	    	}
	    	JavaBeansGenerator jbg = new JavaBeansGenerator(inputF, rootF, getEncoding());
	    	jbg.doIt();
		} catch(Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}	

//TDB.
//    public void execute() throws BuildException {
//    	try {
//	    	String root = getRoot();
//	    	if(root == null) {
//	    		throw new BuildException("root is not specification.");
//	    	}
//	    	File rootF = new File(root);
//	    	if(!rootF.exists()) {
//	    		throw new BuildException(rootF.getCanonicalPath() + "is not exists.");
//	    	}
//	    	String packageName;
//	    	Package pkg = this.getClass().getPackage();
//	    	if(pkg == null) {
//	    		packageName = getType();
//	    	} else {
//	    		packageName = pkg.getName() + "." + getType();
//	    	}
//	    	packageName = packageName.replaceAll("\\.", "/");
//	    	for(FileSet fs : fileSets) {
//	    		DirectoryScanner ds = fs.getDirectoryScanner(getProject());
//	    		File[] files = fs.getDir(getProject());
//	    		
//	    	}
//    	} catch(IOException e) {
//    		throw new BuildException(e);
//    	}
//    }
}

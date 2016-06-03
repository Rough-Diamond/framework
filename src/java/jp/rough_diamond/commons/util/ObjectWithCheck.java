/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 母集合と補集合をまとめて管理するコレクション
**/
public class ObjectWithCheck implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 本生成子はデシリアライズ時に使用するものであり、
     * 通常のオブジェクト生成では利用しないこと
    **/
    public ObjectWithCheck() {}

    /**
     * 生成子
     * @param   allCollection       全集合
     * @param   selectedCollection  選択集合
     * @param   getCodeMethodName   キー取得メソッド名
     * @param   getNameMethodName   名前取得メソッド名
    **/
    public ObjectWithCheck(Object[] allCollection,
            Object[] selectedCollection, String getCodeMethodName,
            String getNameMethodName) {
        this(allCollection, selectedCollection,
                    getMethod(allCollection, getCodeMethodName),
                    getMethod(allCollection, getNameMethodName));
    }

    /**
     * 生成子
     * @param   allCollection       全集合
     * @param   selectedCode        選択キー
     * @param   getCodeMethodName   キー取得メソッド名
     * @param   getNameMethodName   名前取得メソッド名
    **/
    public ObjectWithCheck(Object[] allCollection,
            Object selectedCode, String getCodeMethodName,
            String getNameMethodName) {
        this(   allCollection,
                getSelectedCollection(
                        allCollection, selectedCode, getMethod(
                                allCollection, getCodeMethodName)),
                getMethod(allCollection, getCodeMethodName),
                getMethod(allCollection, getNameMethodName));
    }

    private ObjectWithCheck(Object[] allCollection, Object[]
            selectedCollection, Method getCodeMethod, Method getNameMethod) {
        this.allCollection = allCollection;
        this.selectedCollection = selectedCollection;
        this.getCodeMethod = getCodeMethod;
        this.getNameMethod = getNameMethod;
        makeList();
    }

    /**
     * 件数取得
     * @return 件数
    **/
    public int getSize() {
        return allCollection.length;
    }

    /**
     * 選択コード取得。複数選択されている場合は最初の選択コードを返却する。
     * @return 選択コード
    **/
    public Object getSelectedCode() {
        if(selectedCollection.length == 0) {
            return "";
        } else {
            return getCodeByIndex(0);
        }
    }

    /**
     * 選択コード群取得。
     * @return 選択コード群
    **/
    public Object[] getSelectedCodes() {
        Object[] ret = new String[selectedCollection.length];
        for(int i = 0 ; i < ret.length ; i++) {
            ret[i] = getCodeByIndex(i);
        }
        return ret;
    }

    /**
     * 選択オブジェクト取得。複数選択されている場合は最初の選択オブジェクトを返却する。
     * @return 選択オブジェクト
    **/
    public Object getSelectedObject() {
        if(selectedCollection.length == 0) {
            return null;
        } else {
            return selectedCollection[0];
        }
    }

    /**
     * 選択オブジェクト群取得。
     * @return 選択オブジェクト群
    **/
    public Object[] getSelectedObjects() {
        return selectedCollection;
    }

    /**
     * 一覧表示用リスト取得
     * @return 一覧表示用リスト
    **/
    public List<Item> getList() {
        return list;
    }

    private void makeList() {
        Set<Object> set = new HashSet<Object> ();
        for(int i = 0 ; i < selectedCollection.length ; i++) {
            set.add(invoke(getCodeMethod, selectedCollection[i]));
        }
        List<Item> tmp = new ArrayList<Item>();
        for(int i = 0 ; i < allCollection.length ; i++) {
            tmp.add(new Item(allCollection[i],
                set.contains(invoke(getCodeMethod, allCollection[i])), this));
        }
        list = Collections.unmodifiableList(tmp);
    }

    private Object getCodeByIndex(int index) {
        return invoke(getCodeMethod, selectedCollection[index]);
    }

    private static Object[] getSelectedCollection(
            Object[] allCollection, Object selectedCode, Method getCodeMethod) {
        for(int i = 0 ; i < allCollection.length ; i++) {
            Object code = invoke(getCodeMethod, allCollection[i]);
            if(code.equals(selectedCode)) {
                Object[] ret = new Object[1];
                ret[0] = allCollection[i];
                return ret;
            }
        }
        return new Object[0];
    }

    private static Method getMethod(Object[] collection, String methodName) {
        try {
            if(collection.length == 0) {
                return null;
            }
            return collection[0].getClass().getMethod(methodName, new Class<?>[]{});
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static Object invoke(Method m, Object o) {
        try {
            return m.invoke(o, new Object[]{});
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    private Object[] allCollection;
    private Object[] selectedCollection;
    private List<Item> list;
    private transient Method getCodeMethod;
    private transient Method getNameMethod;

    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.writeObject(allCollection);
    	out.writeObject(selectedCollection);
    	out.writeObject(list);
    	String getCodeMethodName = getCodeMethod.getName();
    	String getNameMthodName = getNameMethod.getName();
    	out.writeObject(getCodeMethodName);
    	out.writeObject(getNameMthodName);
    }
    
    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	allCollection = (Object[])in.readObject();
    	selectedCollection = (Object[])in.readObject();
    	list = (List<Item>)in.readObject();
    	String getCodeMethodName = (String)in.readObject();
    	String getNameMthodName = (String)in.readObject();
    	getCodeMethod = getMethod(allCollection, getCodeMethodName);
    	getNameMethod = getMethod(allCollection, getNameMthodName);
    }

    /**
     * 一覧表示用項目
    **/
    public static class Item implements Serializable {
		private static final long serialVersionUID = 1L;
		private Item(Object item, boolean selected, ObjectWithCheck target) {
            this.item = item;
            this.selected = selected;
            this.target = target;
        }

        /**
         * オブジェクト取得
         * @return オブジェクト
        **/
        public Object getObject() {
            return item;
        }

        /**
         * checked取得
         * @return checkboxのチェック文字列を返却する。未選択は空文字
        **/
        public String getChecked() {
            return (selected) ? "checked" : "";
        }

        /**
         * selected取得
         * @return optionの選択文字列を返却する。未選択は空文字
        **/
        public String getSelected() {
            return (selected) ? "selected" : "";
        }

        /**
         * コード取得
         * @return コード
        **/
        public Object getCode() {
            return ObjectWithCheck.invoke(
            		target.getCodeMethod, item);
        }

        /**
         * 名前取得
         * @return 名前
        **/
        public String getName() {
            return ObjectWithCheck.invoke(
            		target.getNameMethod, item).toString();
        }

        private Object item;
        private boolean selected;
        private ObjectWithCheck target;
    }
}
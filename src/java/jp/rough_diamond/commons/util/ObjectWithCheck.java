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
 * ��W���ƕ�W�����܂Ƃ߂ĊǗ�����R���N�V����
**/
public class ObjectWithCheck implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * �{�����q�̓f�V���A���C�Y���Ɏg�p������̂ł���A
     * �ʏ�̃I�u�W�F�N�g�����ł͗��p���Ȃ�����
    **/
    public ObjectWithCheck() {}

    /**
     * �����q
     * @param   allCollection       �S�W��
     * @param   selectedCollection  �I���W��
     * @param   getCodeMethodName   �L�[�擾���\�b�h��
     * @param   getNameMethodName   ���O�擾���\�b�h��
    **/
    public ObjectWithCheck(Object[] allCollection,
            Object[] selectedCollection, String getCodeMethodName,
            String getNameMethodName) {
        this(allCollection, selectedCollection,
                    getMethod(allCollection, getCodeMethodName),
                    getMethod(allCollection, getNameMethodName));
    }

    /**
     * �����q
     * @param   allCollection       �S�W��
     * @param   selectedCode        �I���L�[
     * @param   getCodeMethodName   �L�[�擾���\�b�h��
     * @param   getNameMethodName   ���O�擾���\�b�h��
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
     * �����擾
     * @return ����
    **/
    public int getSize() {
        return allCollection.length;
    }

    /**
     * �I���R�[�h�擾�B�����I������Ă���ꍇ�͍ŏ��̑I���R�[�h��ԋp����B
     * @return �I���R�[�h
    **/
    public Object getSelectedCode() {
        if(selectedCollection.length == 0) {
            return "";
        } else {
            return getCodeByIndex(0);
        }
    }

    /**
     * �I���R�[�h�Q�擾�B
     * @return �I���R�[�h�Q
    **/
    public Object[] getSelectedCodes() {
        Object[] ret = new String[selectedCollection.length];
        for(int i = 0 ; i < ret.length ; i++) {
            ret[i] = getCodeByIndex(i);
        }
        return ret;
    }

    /**
     * �I���I�u�W�F�N�g�擾�B�����I������Ă���ꍇ�͍ŏ��̑I���I�u�W�F�N�g��ԋp����B
     * @return �I���I�u�W�F�N�g
    **/
    public Object getSelectedObject() {
        if(selectedCollection.length == 0) {
            return null;
        } else {
            return selectedCollection[0];
        }
    }

    /**
     * �I���I�u�W�F�N�g�Q�擾�B
     * @return �I���I�u�W�F�N�g�Q
    **/
    public Object[] getSelectedObjects() {
        return selectedCollection;
    }

    /**
     * �ꗗ�\���p���X�g�擾
     * @return �ꗗ�\���p���X�g
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
     * �ꗗ�\���p����
    **/
    public static class Item implements Serializable {
		private static final long serialVersionUID = 1L;
		private Item(Object item, boolean selected, ObjectWithCheck target) {
            this.item = item;
            this.selected = selected;
            this.target = target;
        }

        /**
         * �I�u�W�F�N�g�擾
         * @return �I�u�W�F�N�g
        **/
        public Object getObject() {
            return item;
        }

        /**
         * checked�擾
         * @return checkbox�̃`�F�b�N�������ԋp����B���I���͋󕶎�
        **/
        public String getChecked() {
            return (selected) ? "checked" : "";
        }

        /**
         * selected�擾
         * @return option�̑I�𕶎����ԋp����B���I���͋󕶎�
        **/
        public String getSelected() {
            return (selected) ? "selected" : "";
        }

        /**
         * �R�[�h�擾
         * @return �R�[�h
        **/
        public Object getCode() {
            return ObjectWithCheck.invoke(
            		target.getCodeMethod, item);
        }

        /**
         * ���O�擾
         * @return ���O
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
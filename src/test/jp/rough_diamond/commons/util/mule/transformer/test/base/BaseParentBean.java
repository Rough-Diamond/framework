//$Id: BeansBaseTemplate.vm,v 1.1 2005/10/27 15:43:53 yamane Exp $
package jp.rough_diamond.commons.util.mule.transformer.test.base;
import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import jp.rough_diamond.commons.util.mule.transformer.test.ChildBean;

/**
 * MuleTransformer�e�X�g�pBean
**/
abstract public class BaseParentBean  implements Serializable {
    /**
     * ���X�g
    **/
    private  String[]   array;

    /**
     * �u�[���A���iprimitive)
    **/
    private  boolean   boolean1;

    /**
     * �J�����_�[
    **/
    private  Calendar   cal;

    /**
     * Java�I�u�W�F�N�g
    **/
    private  ChildBean   child;

    /**
     * ���t
    **/
    private  Date   date;

    /**
     * ���l
    **/
    private  Integer   int1;

    /**
     * ���X�g
    **/
    private  List<String>   list;

    /**
     * ����
    **/
    private  String   xxx;

    /**
     * ���X�g���擾����
     * @return ���X�g
    **/
    public String[] getArray() {
        return this.array;
    }

    /**
     * �u�[���A���iprimitive)���擾����
     * @return �u�[���A���iprimitive)
    **/
    public boolean isBoolean1() {
        return this.boolean1;
    }

    /**
     * �J�����_�[���擾����
     * @return �J�����_�[
    **/
    public Calendar getCal() {
        return this.cal;
    }

    /**
     * Java�I�u�W�F�N�g���擾����
     * @return Java�I�u�W�F�N�g
    **/
    public ChildBean getChild() {
        return this.child;
    }

    /**
     * ���t���擾����
     * @return ���t
    **/
    public Date getDate() {
        return this.date;
    }

    /**
     * ���l���擾����
     * @return ���l
    **/
    public Integer getInt1() {
        return this.int1;
    }

    /**
     * ���X�g���擾����
     * @return ���X�g
    **/
    public List<String> getList() {
        return this.list;
    }

    /**
     * �������擾����
     * @return ����
    **/
    public String getXxx() {
        return this.xxx;
    }


    /**
     * ���X�g��ݒ肷��
     * @param array ���X�g
    **/
    public void setArray(String[] array) {
        this.array = array;
    }

    /**
     * �u�[���A���iprimitive)��ݒ肷��
     * @param boolean1 �u�[���A���iprimitive)
    **/
    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }

    /**
     * �J�����_�[��ݒ肷��
     * @param cal �J�����_�[
    **/
    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    /**
     * Java�I�u�W�F�N�g��ݒ肷��
     * @param child Java�I�u�W�F�N�g
    **/
    public void setChild(ChildBean child) {
        this.child = child;
    }

    /**
     * ���t��ݒ肷��
     * @param date ���t
    **/
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * ���l��ݒ肷��
     * @param int1 ���l
    **/
    public void setInt1(Integer int1) {
        this.int1 = int1;
    }

    /**
     * ���X�g��ݒ肷��
     * @param list ���X�g
    **/
    public void setList(List<String> list) {
        this.list = list;
    }

    /**
     * ������ݒ肷��
     * @param xxx ����
    **/
    public void setXxx(String xxx) {
        this.xxx = xxx;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("[���X�g:");
      buf.append(array + "]");
      buf.append("[�u�[���A���iprimitive):");
      buf.append(boolean1 + "]");
      buf.append("[�J�����_�[:");
      buf.append(cal + "]");
      buf.append("[Java�I�u�W�F�N�g:");
      buf.append(child + "]");
      buf.append("[���t:");
      buf.append(date + "]");
      buf.append("[���l:");
      buf.append(int1 + "]");
      buf.append("[���X�g:");
      buf.append(list + "]");
      buf.append("[����:");
      buf.append(xxx + "]");
      return buf.toString();
    }

    private static final long serialVersionUID = 1L;
}

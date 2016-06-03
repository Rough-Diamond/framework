//$Id: BeansBaseTemplate.vm,v 1.1 2005/10/27 15:43:53 yamane Exp $
package jp.rough_diamond.commons.util.mule.transformer.test.base;
import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import jp.rough_diamond.commons.util.mule.transformer.test.ChildBean;

/**
 * MuleTransformerテスト用Bean
**/
abstract public class BaseParentBean  implements Serializable {
    /**
     * リスト
    **/
    private  String[]   array;

    /**
     * ブーリアン（primitive)
    **/
    private  boolean   boolean1;

    /**
     * カレンダー
    **/
    private  Calendar   cal;

    /**
     * Javaオブジェクト
    **/
    private  ChildBean   child;

    /**
     * 日付
    **/
    private  Date   date;

    /**
     * 数値
    **/
    private  Integer   int1;

    /**
     * リスト
    **/
    private  List<String>   list;

    /**
     * 文字
    **/
    private  String   xxx;

    /**
     * リストを取得する
     * @return リスト
    **/
    public String[] getArray() {
        return this.array;
    }

    /**
     * ブーリアン（primitive)を取得する
     * @return ブーリアン（primitive)
    **/
    public boolean isBoolean1() {
        return this.boolean1;
    }

    /**
     * カレンダーを取得する
     * @return カレンダー
    **/
    public Calendar getCal() {
        return this.cal;
    }

    /**
     * Javaオブジェクトを取得する
     * @return Javaオブジェクト
    **/
    public ChildBean getChild() {
        return this.child;
    }

    /**
     * 日付を取得する
     * @return 日付
    **/
    public Date getDate() {
        return this.date;
    }

    /**
     * 数値を取得する
     * @return 数値
    **/
    public Integer getInt1() {
        return this.int1;
    }

    /**
     * リストを取得する
     * @return リスト
    **/
    public List<String> getList() {
        return this.list;
    }

    /**
     * 文字を取得する
     * @return 文字
    **/
    public String getXxx() {
        return this.xxx;
    }


    /**
     * リストを設定する
     * @param array リスト
    **/
    public void setArray(String[] array) {
        this.array = array;
    }

    /**
     * ブーリアン（primitive)を設定する
     * @param boolean1 ブーリアン（primitive)
    **/
    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }

    /**
     * カレンダーを設定する
     * @param cal カレンダー
    **/
    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    /**
     * Javaオブジェクトを設定する
     * @param child Javaオブジェクト
    **/
    public void setChild(ChildBean child) {
        this.child = child;
    }

    /**
     * 日付を設定する
     * @param date 日付
    **/
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 数値を設定する
     * @param int1 数値
    **/
    public void setInt1(Integer int1) {
        this.int1 = int1;
    }

    /**
     * リストを設定する
     * @param list リスト
    **/
    public void setList(List<String> list) {
        this.list = list;
    }

    /**
     * 文字を設定する
     * @param xxx 文字
    **/
    public void setXxx(String xxx) {
        this.xxx = xxx;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("[リスト:");
      buf.append(array + "]");
      buf.append("[ブーリアン（primitive):");
      buf.append(boolean1 + "]");
      buf.append("[カレンダー:");
      buf.append(cal + "]");
      buf.append("[Javaオブジェクト:");
      buf.append(child + "]");
      buf.append("[日付:");
      buf.append(date + "]");
      buf.append("[数値:");
      buf.append(int1 + "]");
      buf.append("[リスト:");
      buf.append(list + "]");
      buf.append("[文字:");
      buf.append(xxx + "]");
      return buf.toString();
    }

    private static final long serialVersionUID = 1L;
}

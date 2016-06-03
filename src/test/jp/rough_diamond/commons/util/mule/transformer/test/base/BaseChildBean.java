//$Id: BeansBaseTemplate.vm,v 1.1 2005/10/27 15:43:53 yamane Exp $
package jp.rough_diamond.commons.util.mule.transformer.test.base;
import java.io.Serializable;


/**
 * MuleTransformerテスト用Bean
**/
abstract public class BaseChildBean  implements Serializable {
    /**
     * 文字
    **/
    private  String   yyy;

    /**
     * 文字
    **/
    private  String   zzz;

    /**
     * 文字を取得する
     * @return 文字
    **/
    public String getYyy() {
        return this.yyy;
    }

    /**
     * 文字を取得する
     * @return 文字
    **/
    public String getZzz() {
        return this.zzz;
    }


    /**
     * 文字を設定する
     * @param yyy 文字
    **/
    public void setYyy(String yyy) {
        this.yyy = yyy;
    }

    /**
     * 文字を設定する
     * @param zzz 文字
    **/
    public void setZzz(String zzz) {
        this.zzz = zzz;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("[文字:");
      buf.append(yyy + "]");
      buf.append("[文字:");
      buf.append(zzz + "]");
      return buf.toString();
    }

    private static final long serialVersionUID = 1L;
}

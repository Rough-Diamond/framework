//$Id: BeansBaseTemplate.vm,v 1.1 2005/10/27 15:43:53 yamane Exp $
package jp.rough_diamond.commons.util.mule.transformer.test.base;
import java.io.Serializable;

import java.util.*;

/**
 * MuleTransformerテスト用Bean
**/
@SuppressWarnings("all")
abstract public class BaseHasMapBean  implements Serializable {
    /**
     * 文字
    **/
    private  Map<String,String>   map;

    /**
     * 文字を取得する
     * @return 文字
    **/
    public Map<String,String> getMap() {
        return this.map;
    }


    /**
     * 文字を設定する
     * @param map 文字
    **/
    public void setMap(Map<String,String> map) {
        this.map = map;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("[文字:");
      buf.append(map + "]");
      return buf.toString();
    }

    private static final long serialVersionUID = 1L;
}

//$Id: BeansBaseTemplate.vm,v 1.1 2005/10/27 15:43:53 yamane Exp $
package jp.rough_diamond.commons.util.mule.transformer.test.base;
import java.io.Serializable;

import java.util.*;

/**
 * MuleTransformer�e�X�g�pBean
**/
@SuppressWarnings("all")
abstract public class BaseHasMapBean  implements Serializable {
    /**
     * ����
    **/
    private  Map<String,String>   map;

    /**
     * �������擾����
     * @return ����
    **/
    public Map<String,String> getMap() {
        return this.map;
    }


    /**
     * ������ݒ肷��
     * @param map ����
    **/
    public void setMap(Map<String,String> map) {
        this.map = map;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("[����:");
      buf.append(map + "]");
      return buf.toString();
    }

    private static final long serialVersionUID = 1L;
}

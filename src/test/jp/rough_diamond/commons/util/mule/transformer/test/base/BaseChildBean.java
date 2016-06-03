//$Id: BeansBaseTemplate.vm,v 1.1 2005/10/27 15:43:53 yamane Exp $
package jp.rough_diamond.commons.util.mule.transformer.test.base;
import java.io.Serializable;


/**
 * MuleTransformer�e�X�g�pBean
**/
abstract public class BaseChildBean  implements Serializable {
    /**
     * ����
    **/
    private  String   yyy;

    /**
     * ����
    **/
    private  String   zzz;

    /**
     * �������擾����
     * @return ����
    **/
    public String getYyy() {
        return this.yyy;
    }

    /**
     * �������擾����
     * @return ����
    **/
    public String getZzz() {
        return this.zzz;
    }


    /**
     * ������ݒ肷��
     * @param yyy ����
    **/
    public void setYyy(String yyy) {
        this.yyy = yyy;
    }

    /**
     * ������ݒ肷��
     * @param zzz ����
    **/
    public void setZzz(String zzz) {
        this.zzz = zzz;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("[����:");
      buf.append(yyy + "]");
      buf.append("[����:");
      buf.append(zzz + "]");
      return buf.toString();
    }

    private static final long serialVersionUID = 1L;
}

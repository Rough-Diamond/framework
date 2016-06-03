/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.velocity;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.entity.Quantity;
import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.resource.ResourceManager;
import jp.rough_diamond.framework.user.User;
import jp.rough_diamond.framework.user.UserController;

/**
 * Velocity便利ツール
 */
public class VelocityUtils extends VelocityUtilsDepracated {
	private final static Log log = LogFactory.getLog(VelocityUtils.class);
	/**
	 * 現在のユーザーオブジェクトを返却する
	 * @return
	 */
	public User getUser() {
		return UserController.getController().getUser();
	}
	
	/**
	 * DIコンテナを返却する
	 * @return
	 */
	public DIContainer getDIContainer() {
		return DIContainerFactory.getDIContainer();
	}

	public String getApplicationPath(HttpServletRequest request) {
	    String scheme = request.getScheme();
	    scheme = scheme.toLowerCase();
	    String hostAndPort = request.getServerName() + ":" + request.getServerPort();
	    String applicationName = request.getContextPath();
	    if(applicationName.startsWith("/") || applicationName.length() == 0) {
	        return scheme + "://" + hostAndPort + applicationName;
	    } else {
	        return scheme + "://" + hostAndPort + "/" + applicationName;
	    }
	}
    
    /**
     * key にひもづくリソースを返す. リソースが存在しない場合は渡されたキーを返す
     * @param key
     * @return　リソース
     */
    public String message(String key) {
        try {
            return ResourceManager.getResource().getString(key);
        } catch(MissingResourceException mre) {
            return key;
        } 
    }

	public String formatQuantity(Quantity quantity) {
		return formatQuantity(quantity, true, false);
	}

	public String formatQuantity(Quantity quantity, boolean isPrefix, boolean isSuffix) {
		return formatQuantity(quantity, isPrefix, isSuffix, "#,##0", quantity.getUnit().getScale());
	}

	public String formatQuantity(Quantity quantity, String formatOfIntegralPart) {
		return formatQuantity(quantity, true, false, formatOfIntegralPart, quantity.getUnit().getScale());
	}

	public String formatQuantity(Quantity quantity, int scale) {
		return formatQuantity(quantity, true, false, "#,##0", scale);
	}

	public String formatQuantity(Quantity quantity, boolean isPrefix,
			boolean isSuffix, String formatOfIntegralPart) {
		return formatQuantity(quantity, isPrefix, isSuffix, formatOfIntegralPart,
				quantity.getUnit().getScale());
	}

	public String formatQuantity(Quantity quantity, boolean isPrefix,
			boolean isSuffix, int scale) {
		return formatQuantity(quantity, isPrefix, isSuffix, "#,##0", scale);
	}

	public String formatQuantity(Quantity quantity, String formatOfIntegralPart,
			int scale) {
		return formatQuantity(quantity, true, false, formatOfIntegralPart, scale);
	}

	/**
	 * 量をフォーマッティングする
	 * 
	 * @param quantity
	 *            量
	 * @param isPrefix
	 *            単位名を先頭に付与する
	 * @param isSuffix
	 *            単位名を末尾に付与する
	 * @param formatOfIntegralPart
	 *            整数部のフォーマット
	 * @param scale
	 *            小数点以下の桁数
	 * @return
	 * @exception IllegalArgumentException
	 *                isPrefix、isSuffixともにtrueの場合
	 * @author imai
	 */
	public String formatQuantity(Quantity quantity, boolean isPrefix,
			boolean isSuffix, String formatOfIntegralPart, int scale) {
		if (isPrefix == true && isSuffix == true) {
			throw new IllegalArgumentException();
		}
		Unit unit = quantity.getUnit();
		StringBuilder formatSB = new StringBuilder(formatOfIntegralPart);
		if (scale > 0) {
			formatSB.append(".");
			char[] array = new char[scale];
			Arrays.fill(array, '0');
			formatSB.append(array);
		}
		log.debug(formatSB);
		DecimalFormat df = new DecimalFormat(formatSB.toString());
		StringBuilder sb = new StringBuilder();
		if (isPrefix) {
			sb.append(unit.getName());
		}
		sb.append(df.format(quantity.getAmount().decimal()));
		if (isSuffix) {
			sb.append(unit.getName());
		}
		return sb.toString();
	}
}

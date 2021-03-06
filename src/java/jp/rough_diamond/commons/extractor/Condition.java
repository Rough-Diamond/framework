/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * 抽出条件を表すクラス
 */
@SuppressWarnings("unchecked")
abstract public class Condition<T extends Value> implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * And条件オブジェクトを生成する
	 * @return And条件オブジェクト
	 */
	public static <T extends Value> CombineCondition<T> and() {
		return new And();
	}
	
	/**
	 * And条件オブジェクトを生成する
	 * @param conditions	And結合する条件群
	 * @return 			And条件オブジェクト
	 */
	public static <T extends Value> CombineCondition<T> and(Collection<Condition<T>> conditions) {
		return new And(conditions);
	}
	
	/**
	 * And条件オブジェクトを生成する
	 * @param conditions	And結合する条件群
	 * @return 			And条件オブジェクト
	 */
	public static <T extends Value> CombineCondition<T> and(Condition<T>... conditions) {
		return new And(Arrays.asList(conditions));
	}
	
	/**
	 * Eq条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @param value		値
	 * @return				Eq条件オブジェクト
	 * @deprecated ValueHoldingCondition eq(T, Object)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> eq(String propertyName, Object value) {
		return new Eq(propertyName, null, null, value);
	}
	
    /**
     * Eq条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値
     * @return              Eq条件オブジェクト
	 * @deprecated ValueHoldingCondition eq(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> eq(String propertyName, Class target, String aliase, Object value) {
        return new Eq(propertyName, target, aliase, value);
    }
    
    /**
     * Eq条件オブジェクトを生成する
     * @param label    	比較対照ラベル
     * @param value     値
     * @return              Eq条件オブジェクト
     */
    public static <T extends Value> ValueHoldingCondition<T> eq(T label, Object value) {
        return new Eq(label, value);
    }
    
    /**
     * Ge条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param value     値
     * @return              Ge条件オブジェクト
	 * @deprecated ValueHoldingCondition ge(T, Object)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> ge(String propertyName, Object value) {
		return new Ge(propertyName, null, null, value);
	}

	/**
	 * Ge条件オブジェクトを生成する
	 * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
	 * @param value		値
	 * @return				Ge条件オブジェクト
	 * @deprecated ValueHoldingCondition ge(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> ge(String propertyName, Class target, String aliase, Object value) {
        return new Ge(propertyName, target, aliase, value);
    }

    /**
     * Ge条件オブジェクトを生成する
     * @param label 	比較対照ラベル
     * @param value     値
     * @return              Ge条件オブジェクト
	 */
    public static <T extends Value> ValueHoldingCondition<T> ge(T label, Object value) {
        return new Ge(label, value);
    }

	/**
	 * Gt条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @param value		値
	 * @return				Gt条件オブジェクト
	 * @deprecated ValueHoldingCondition gt(T, Object)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> gt(String propertyName, Object value) {
		return new Gt(propertyName, null, null, value);
	}

    /**
     * Gt条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値
     * @return              Gt条件オブジェクト
	 * @deprecated ValueHoldingCondition gt(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> gt(String propertyName, Class target, String aliase, Object value) {
        return new Gt(propertyName, target, aliase, value);
    }

    /**
     * Gt条件オブジェクトを生成する
     * @param label 	比較対照ラベル
     * @param value     値
     * @return              Gt条件オブジェクト
	 */
    public static <T extends Value> ValueHoldingCondition<T> gt(T label, Object value) {
        return new Gt(label, value);
    }

	/**
	 * In条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @param value		値群
	 * @return				In条件オブジェクト
	 * @deprecated ValueHoldingCondition in(T, Object...)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> in(String propertyName, Object... values) {
		return in(propertyName, Arrays.asList(values));
	}

	/**
	 * In条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @param value		値群
	 * @return				In条件オブジェクト
	 * @deprecated ValueHoldingCondition in(T, Object...)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> in(String propertyName, Collection values) {
		return new In(propertyName, null, null, values);
	}

    /**
     * In条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値群
     * @return              In条件オブジェクト
	 * @deprecated ValueHoldingCondition in(T, Object...)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> in(String propertyName, Class target, String aliase, Object... values) {
        return in(propertyName, target, aliase, Arrays.asList(values));
    }
    
    /**
     * In条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値群
     * @return              In条件オブジェクト
	 * @deprecated ValueHoldingCondition in(T, Object...)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> in(String propertyName, Class target, String aliase, Collection values) {
        return new In(propertyName, target, aliase, values);
    }
    
    /**
     * In条件オブジェクトを生成する
	 * @param label		比較対照ラベル
     * @param value     値群
     * @return              In条件オブジェクト
     */
    public static <T extends Value> ValueHoldingCondition<T> in(T label, Object... values) {
        return new In(label, Arrays.asList(values));
    }
    
    /**
     * In条件オブジェクトを生成する
	 * @param label		比較対照ラベル
     * @param value     値群
     * @return              In条件オブジェクト
     */
    public static <T extends Value> ValueHoldingCondition<T> in(T label, Collection values) {
        return new In(label, values);
    }
    
   /**
     * NotIn条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param values 値群
     * @return NotIn	条件オブジェクト
 	 * @deprecated ValueHoldingCondition notIn(T, Object...)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Collection values) {
    	return new NotIn(propertyName, null, null, values);
    }
    
    /**
     * NotIn条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param values 値群
     * @return NotIn	条件オブジェクト
	 * @deprecated ValueHoldingCondition notIn(T, Object...)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Object... values) {
    	return notIn(propertyName, null, null, values);
    }

    /**
     * NotIn条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値群
     * @return              NotIn条件オブジェクト
	 * @deprecated ValueHoldingCondition notIn(T, Object...)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Class target, String aliase, Object... values) {
        return new NotIn(propertyName, target, aliase, Arrays.asList(values));
    }

    /**
     * NotIn条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値群
     * @return              In条件オブジェクト
	 * @deprecated ValueHoldingCondition notIn(T, Object...)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Class target, String aliase, Collection values) {
        return new NotIn(propertyName, target, aliase, values);
    }
    
    /**
     * NotIn条件オブジェクトを生成する
	 * @param label		比較対照ラベル
     * @param value     値群
     * @return              In条件オブジェクト
     */
    public static <T extends Value> ValueHoldingCondition<T> notIn(T label, Object... values) {
        return new NotIn(label, Arrays.asList(values));
    }
    
    /**
     * NotIn条件オブジェクトを生成する
	 * @param label		比較対照ラベル
     * @param value     値群
     * @return              In条件オブジェクト
     */
    public static <T extends Value> ValueHoldingCondition<T> notIn(T label, Collection values) {
        return new NotIn(label, values);
    }
    
    /**
     * 内部結合オブジェクトを生成する
     * @param target            ターゲットクラス
     * @param targetProperty    ターゲットプロパティ null、空白の場合はtargetと直接結合する
     * @param targetAlias       ターゲットエイリアス null、空白の場合はエイリアスなし
     * @param joined            結合先クラス
     * @param joinedProperty   結合対応プロパティ null、空白の場合はjoinedと直接結合する
     * @param alias             結合先エイリアス null、空白の場合はエイリアスなし
     * @return 内部結合オブジェクト
     */
    public static InnerJoin innerJoin(Class target, String targetProperty, String targetAlias, Class joined, String joinedProperty, String joinedAlias) {
       return new InnerJoin(target, targetProperty, targetAlias, joined, joinedProperty, joinedAlias); 
    }
    
	/**
	 * IsNotNull条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @return				IsNotNull条件オブジェクト
	 * @deprecated ValueHoldingCondition isNotNull(T)の使用を推奨します
	 */
	@Deprecated 
	public static LabelHoldingCondition<Property> isNotNull(String propertyName) {
		return new IsNotNull(propertyName, null, null);
	}

    /**
     * IsNotNull条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @return              IsNotNull条件オブジェクト
	 * @deprecated ValueHoldingCondition isNotNull(T)の使用を推奨します
	 */
	@Deprecated 
    public static LabelHoldingCondition<Property> isNotNull(String propertyName, Class target, String aliase) {
        return new IsNotNull(propertyName, target, aliase);
    }

	/**
	 * IsNotNull条件オブジェクトを生成する
	 * @param label		比較対照ラベル
	 * @return				IsNotNull条件オブジェクト
	 */
	public static <T extends Value> LabelHoldingCondition<T> isNotNull(T label) {
		return new IsNotNull(label);
	}

	/**
	 * IsNull条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @return				IsNull条件オブジェクト
	 * @deprecated ValueHoldingCondition isNull(T)の使用を推奨します
	 */
	@Deprecated 
	public static LabelHoldingCondition<Property> isNull(String propertyName) {
		return new IsNull(propertyName, null, null);
	}

    /**
     * IsNull条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @return              IsNull条件オブジェクト
	 * @deprecated ValueHoldingCondition isNull(T)の使用を推奨します
	 */
	@Deprecated 
    public static LabelHoldingCondition<Property> isNull(String propertyName, Class target, String aliase) {
        return new IsNull(propertyName, target, aliase);
    }

	/**
	 * IsNull条件オブジェクトを生成する
	 * @param label		比較対照ラベル
	 * @return				IsNull条件オブジェクト
	 */
	public static <T extends Value> LabelHoldingCondition<T> isNull(T label) {
		return new IsNull(label);
	}

	/**
	 * 外部結合条件オブジェクトを生成する
	 * @param entityName 	外部エンティティ名
	 * @return				外部条件結合オブジェクト
	 * @deprecated	innerJoinの使用を推奨します
	 */
	@Deprecated 
	public static Join join(String entityName) {
		return new Join(entityName);
	}

	/**
	 * Le条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @param value		値
	 * @return				Le条件オブジェクト
	 * @deprecated ValueHoldingCondition le(T, Object)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> le(String propertyName, Object value) {
		return new Le(propertyName, null, null, value);
	}

    /**
     * Le条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
	 * @param value		値
     * @return              Le条件オブジェクト
	 * @deprecated ValueHoldingCondition le(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> le(String propertyName, Class target, String aliase, Object value) {
        return new Le(propertyName, target, aliase, value);
    }

	/**
	 * Le条件オブジェクトを生成する
	 * @param label		比較対照ラベル
	 * @param value		値
	 * @return				Le条件オブジェクト
	 */
	public static <T extends Value> ValueHoldingCondition<T> le(T label, Object value) {
		return new Le(label, value);
	}

	/**
	 * Like条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @param value		値
	 * @return				Like条件オブジェクト
	 * @deprecated ValueHoldingCondition like(T, Object)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> like(String propertyName, Object value) {
		return new Like(propertyName, null, null, value);
	}

    /**
     * Like条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値
     * @return              Like条件オブジェクト
	 * @deprecated ValueHoldingCondition like(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> like(String propertyName, Class target, String aliase, Object value) {
        return new Like(propertyName, target, aliase, value);
    }

	/**
	 * Like条件オブジェクトを生成する
	 * @param label		比較対照ラベル
	 * @param value		値
	 * @return				Like条件オブジェクト
	 */
	public static <T extends Value> ValueHoldingCondition<T> like(T label, Object value) {
		return new Like(label, value);
	}

	/**
	 * Lt条件オブジェクトを生成する
	 * @param propertyName プロパティ名
	 * @param value		値
	 * @return				Lt条件オブジェクト
	 * @deprecated ValueHoldingCondition lt(T, Object)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> lt(String propertyName, Object value) {
		return new Lt(propertyName, null, null, value);
	}
	
    /**
     * Lt条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値
     * @return              Lt条件オブジェクト
	 * @deprecated ValueHoldingCondition lt(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> lt(String propertyName, Class target, String aliase, Object value) {
        return new Lt(propertyName, target, aliase, value);
    }
    
	/**
	 * Lt条件オブジェクトを生成する
	 * @param label		比較対照ラベル
	 * @param value		値
	 * @return				Lt条件オブジェクト
	 */
	public static <T extends Value> ValueHoldingCondition<T> lt(T label, Object value) {
		return new Lt(label, value);
	}
	
    /**
     * NotEq条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param value     値
     * @return              NotEq条件オブジェクト
	 * @deprecated ValueHoldingCondition notEq(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notEq(String propertyName, Object value) {
        return new NotEq(propertyName, null, null, value);
    }

	/**
	 * NotEq条件オブジェクトを生成する
	 * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
	 * @param value		値
	 * @return				NotEq条件オブジェクト
	 * @deprecated ValueHoldingCondition notEq(T, Object)の使用を推奨します
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> notEq(String propertyName, Class target, String aliase, Object value) {
		return new NotEq(propertyName, target, aliase, value);
	}

    /**
     * NotEq条件オブジェクトを生成する
	 * @param label		比較対照ラベル
     * @param value     値
     * @return              NotEq条件オブジェクト
     */
    public static <T extends Value> ValueHoldingCondition<T> notEq(T label, Object value) {
        return new NotEq(label, value);
    }

	/**
	 * Or条件オブジェクトを生成する
	 * @return 			Or条件オブジェクト
	 */
	public static <T extends Value> CombineCondition<T> or() {
		return new Or();
	}

	/**
	 * Or条件オブジェクトを生成する
	 * @param conditions	Or結合する条件群
	 * @return 			Or条件オブジェクト
	 */
    public static <T extends Value> CombineCondition<T> or(Collection<Condition<T>> conditions) {
		return new Or(conditions);
	}
	
	/**
	 * Or条件オブジェクトを生成する
	 * @param conditions	Or結合する条件群
	 * @return 			Or条件オブジェクト
	 */
	public static <T extends Value> CombineCondition<T> or(Condition<T>... conditions) {
		return or(Arrays.asList(conditions));
	}

    /**
     * RegEx条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param value     値
     * @return              RegExp条件オブジェクト
	 * @deprecated ValueHoldingCondition regex(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> regex(String propertyName, Object value) {
        return regex(propertyName, null, null, value);
    }

    /**
     * RegEx条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @param value     値
     * @return              RegExp条件オブジェクト
	 * @deprecated ValueHoldingCondition regex(T, Object)の使用を推奨します
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> regex(String propertyName, Class target, String aliase, Object value) {
        return new RegularExp(propertyName, target, aliase, value);
    }

    /**
     * RegEx条件オブジェクトを生成する
     * @param propertyName プロパティ名
     * @param value     値
     * @return              RegExp条件オブジェクト
     */
    public static <T extends Value> ValueHoldingCondition<T> regex(T label, Object value) {
        return new RegularExp(label, value);
    }
}

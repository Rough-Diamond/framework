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
 * ���o������\���N���X
 */
@SuppressWarnings("unchecked")
abstract public class Condition<T extends Value> implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * And�����I�u�W�F�N�g�𐶐�����
	 * @return And�����I�u�W�F�N�g
	 */
	public static <T extends Value> CombineCondition<T> and() {
		return new And();
	}
	
	/**
	 * And�����I�u�W�F�N�g�𐶐�����
	 * @param conditions	And������������Q
	 * @return 			And�����I�u�W�F�N�g
	 */
	public static <T extends Value> CombineCondition<T> and(Collection<Condition<T>> conditions) {
		return new And(conditions);
	}
	
	/**
	 * And�����I�u�W�F�N�g�𐶐�����
	 * @param conditions	And������������Q
	 * @return 			And�����I�u�W�F�N�g
	 */
	public static <T extends Value> CombineCondition<T> and(Condition<T>... conditions) {
		return new And(Arrays.asList(conditions));
	}
	
	/**
	 * Eq�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @param value		�l
	 * @return				Eq�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition eq(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> eq(String propertyName, Object value) {
		return new Eq(propertyName, null, null, value);
	}
	
    /**
     * Eq�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l
     * @return              Eq�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition eq(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> eq(String propertyName, Class target, String aliase, Object value) {
        return new Eq(propertyName, target, aliase, value);
    }
    
    /**
     * Eq�����I�u�W�F�N�g�𐶐�����
     * @param label    	��r�Ώƃ��x��
     * @param value     �l
     * @return              Eq�����I�u�W�F�N�g
     */
    public static <T extends Value> ValueHoldingCondition<T> eq(T label, Object value) {
        return new Eq(label, value);
    }
    
    /**
     * Ge�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param value     �l
     * @return              Ge�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition ge(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> ge(String propertyName, Object value) {
		return new Ge(propertyName, null, null, value);
	}

	/**
	 * Ge�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param value		�l
	 * @return				Ge�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition ge(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> ge(String propertyName, Class target, String aliase, Object value) {
        return new Ge(propertyName, target, aliase, value);
    }

    /**
     * Ge�����I�u�W�F�N�g�𐶐�����
     * @param label 	��r�Ώƃ��x��
     * @param value     �l
     * @return              Ge�����I�u�W�F�N�g
	 */
    public static <T extends Value> ValueHoldingCondition<T> ge(T label, Object value) {
        return new Ge(label, value);
    }

	/**
	 * Gt�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @param value		�l
	 * @return				Gt�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition gt(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> gt(String propertyName, Object value) {
		return new Gt(propertyName, null, null, value);
	}

    /**
     * Gt�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l
     * @return              Gt�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition gt(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> gt(String propertyName, Class target, String aliase, Object value) {
        return new Gt(propertyName, target, aliase, value);
    }

    /**
     * Gt�����I�u�W�F�N�g�𐶐�����
     * @param label 	��r�Ώƃ��x��
     * @param value     �l
     * @return              Gt�����I�u�W�F�N�g
	 */
    public static <T extends Value> ValueHoldingCondition<T> gt(T label, Object value) {
        return new Gt(label, value);
    }

	/**
	 * In�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @param value		�l�Q
	 * @return				In�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition in(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> in(String propertyName, Object... values) {
		return in(propertyName, Arrays.asList(values));
	}

	/**
	 * In�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @param value		�l�Q
	 * @return				In�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition in(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> in(String propertyName, Collection values) {
		return new In(propertyName, null, null, values);
	}

    /**
     * In�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l�Q
     * @return              In�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition in(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> in(String propertyName, Class target, String aliase, Object... values) {
        return in(propertyName, target, aliase, Arrays.asList(values));
    }
    
    /**
     * In�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l�Q
     * @return              In�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition in(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> in(String propertyName, Class target, String aliase, Collection values) {
        return new In(propertyName, target, aliase, values);
    }
    
    /**
     * In�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
     * @param value     �l�Q
     * @return              In�����I�u�W�F�N�g
     */
    public static <T extends Value> ValueHoldingCondition<T> in(T label, Object... values) {
        return new In(label, Arrays.asList(values));
    }
    
    /**
     * In�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
     * @param value     �l�Q
     * @return              In�����I�u�W�F�N�g
     */
    public static <T extends Value> ValueHoldingCondition<T> in(T label, Collection values) {
        return new In(label, values);
    }
    
   /**
     * NotIn�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param values �l�Q
     * @return NotIn	�����I�u�W�F�N�g
 	 * @deprecated ValueHoldingCondition notIn(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Collection values) {
    	return new NotIn(propertyName, null, null, values);
    }
    
    /**
     * NotIn�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param values �l�Q
     * @return NotIn	�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition notIn(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Object... values) {
    	return notIn(propertyName, null, null, values);
    }

    /**
     * NotIn�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l�Q
     * @return              NotIn�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition notIn(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Class target, String aliase, Object... values) {
        return new NotIn(propertyName, target, aliase, Arrays.asList(values));
    }

    /**
     * NotIn�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l�Q
     * @return              In�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition notIn(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notIn(String propertyName, Class target, String aliase, Collection values) {
        return new NotIn(propertyName, target, aliase, values);
    }
    
    /**
     * NotIn�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
     * @param value     �l�Q
     * @return              In�����I�u�W�F�N�g
     */
    public static <T extends Value> ValueHoldingCondition<T> notIn(T label, Object... values) {
        return new NotIn(label, Arrays.asList(values));
    }
    
    /**
     * NotIn�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
     * @param value     �l�Q
     * @return              In�����I�u�W�F�N�g
     */
    public static <T extends Value> ValueHoldingCondition<T> notIn(T label, Collection values) {
        return new NotIn(label, values);
    }
    
    /**
     * ���������I�u�W�F�N�g�𐶐�����
     * @param target            �^�[�Q�b�g�N���X
     * @param targetProperty    �^�[�Q�b�g�v���p�e�B null�A�󔒂̏ꍇ��target�ƒ��ڌ�������
     * @param targetAlias       �^�[�Q�b�g�G�C���A�X null�A�󔒂̏ꍇ�̓G�C���A�X�Ȃ�
     * @param joined            ������N���X
     * @param joinedProperty   �����Ή��v���p�e�B null�A�󔒂̏ꍇ��joined�ƒ��ڌ�������
     * @param alias             ������G�C���A�X null�A�󔒂̏ꍇ�̓G�C���A�X�Ȃ�
     * @return ���������I�u�W�F�N�g
     */
    public static InnerJoin innerJoin(Class target, String targetProperty, String targetAlias, Class joined, String joinedProperty, String joinedAlias) {
       return new InnerJoin(target, targetProperty, targetAlias, joined, joinedProperty, joinedAlias); 
    }
    
	/**
	 * IsNotNull�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @return				IsNotNull�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition isNotNull(T)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static LabelHoldingCondition<Property> isNotNull(String propertyName) {
		return new IsNotNull(propertyName, null, null);
	}

    /**
     * IsNotNull�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @return              IsNotNull�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition isNotNull(T)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static LabelHoldingCondition<Property> isNotNull(String propertyName, Class target, String aliase) {
        return new IsNotNull(propertyName, target, aliase);
    }

	/**
	 * IsNotNull�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @return				IsNotNull�����I�u�W�F�N�g
	 */
	public static <T extends Value> LabelHoldingCondition<T> isNotNull(T label) {
		return new IsNotNull(label);
	}

	/**
	 * IsNull�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @return				IsNull�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition isNull(T)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static LabelHoldingCondition<Property> isNull(String propertyName) {
		return new IsNull(propertyName, null, null);
	}

    /**
     * IsNull�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @return              IsNull�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition isNull(T)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static LabelHoldingCondition<Property> isNull(String propertyName, Class target, String aliase) {
        return new IsNull(propertyName, target, aliase);
    }

	/**
	 * IsNull�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @return				IsNull�����I�u�W�F�N�g
	 */
	public static <T extends Value> LabelHoldingCondition<T> isNull(T label) {
		return new IsNull(label);
	}

	/**
	 * �O�����������I�u�W�F�N�g�𐶐�����
	 * @param entityName 	�O���G���e�B�e�B��
	 * @return				�O�����������I�u�W�F�N�g
	 * @deprecated	innerJoin�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static Join join(String entityName) {
		return new Join(entityName);
	}

	/**
	 * Le�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @param value		�l
	 * @return				Le�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition le(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> le(String propertyName, Object value) {
		return new Le(propertyName, null, null, value);
	}

    /**
     * Le�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param value		�l
     * @return              Le�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition le(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> le(String propertyName, Class target, String aliase, Object value) {
        return new Le(propertyName, target, aliase, value);
    }

	/**
	 * Le�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l
	 * @return				Le�����I�u�W�F�N�g
	 */
	public static <T extends Value> ValueHoldingCondition<T> le(T label, Object value) {
		return new Le(label, value);
	}

	/**
	 * Like�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @param value		�l
	 * @return				Like�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition like(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> like(String propertyName, Object value) {
		return new Like(propertyName, null, null, value);
	}

    /**
     * Like�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l
     * @return              Like�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition like(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> like(String propertyName, Class target, String aliase, Object value) {
        return new Like(propertyName, target, aliase, value);
    }

	/**
	 * Like�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l
	 * @return				Like�����I�u�W�F�N�g
	 */
	public static <T extends Value> ValueHoldingCondition<T> like(T label, Object value) {
		return new Like(label, value);
	}

	/**
	 * Lt�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
	 * @param value		�l
	 * @return				Lt�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition lt(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> lt(String propertyName, Object value) {
		return new Lt(propertyName, null, null, value);
	}
	
    /**
     * Lt�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l
     * @return              Lt�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition lt(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> lt(String propertyName, Class target, String aliase, Object value) {
        return new Lt(propertyName, target, aliase, value);
    }
    
	/**
	 * Lt�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l
	 * @return				Lt�����I�u�W�F�N�g
	 */
	public static <T extends Value> ValueHoldingCondition<T> lt(T label, Object value) {
		return new Lt(label, value);
	}
	
    /**
     * NotEq�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param value     �l
     * @return              NotEq�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition notEq(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> notEq(String propertyName, Object value) {
        return new NotEq(propertyName, null, null, value);
    }

	/**
	 * NotEq�����I�u�W�F�N�g�𐶐�����
	 * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param value		�l
	 * @return				NotEq�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition notEq(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
	public static ValueHoldingCondition<Property> notEq(String propertyName, Class target, String aliase, Object value) {
		return new NotEq(propertyName, target, aliase, value);
	}

    /**
     * NotEq�����I�u�W�F�N�g�𐶐�����
	 * @param label		��r�Ώƃ��x��
     * @param value     �l
     * @return              NotEq�����I�u�W�F�N�g
     */
    public static <T extends Value> ValueHoldingCondition<T> notEq(T label, Object value) {
        return new NotEq(label, value);
    }

	/**
	 * Or�����I�u�W�F�N�g�𐶐�����
	 * @return 			Or�����I�u�W�F�N�g
	 */
	public static <T extends Value> CombineCondition<T> or() {
		return new Or();
	}

	/**
	 * Or�����I�u�W�F�N�g�𐶐�����
	 * @param conditions	Or������������Q
	 * @return 			Or�����I�u�W�F�N�g
	 */
    public static <T extends Value> CombineCondition<T> or(Collection<Condition<T>> conditions) {
		return new Or(conditions);
	}
	
	/**
	 * Or�����I�u�W�F�N�g�𐶐�����
	 * @param conditions	Or������������Q
	 * @return 			Or�����I�u�W�F�N�g
	 */
	public static <T extends Value> CombineCondition<T> or(Condition<T>... conditions) {
		return or(Arrays.asList(conditions));
	}

    /**
     * RegEx�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param value     �l
     * @return              RegExp�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition regex(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> regex(String propertyName, Object value) {
        return regex(propertyName, null, null, value);
    }

    /**
     * RegEx�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @param value     �l
     * @return              RegExp�����I�u�W�F�N�g
	 * @deprecated ValueHoldingCondition regex(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated 
    public static ValueHoldingCondition<Property> regex(String propertyName, Class target, String aliase, Object value) {
        return new RegularExp(propertyName, target, aliase, value);
    }

    /**
     * RegEx�����I�u�W�F�N�g�𐶐�����
     * @param propertyName �v���p�e�B��
     * @param value     �l
     * @return              RegExp�����I�u�W�F�N�g
     */
    public static <T extends Value> ValueHoldingCondition<T> regex(T label, Object value) {
        return new RegularExp(label, value);
    }
}

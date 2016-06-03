/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.velocity;

import jp.rough_diamond.commons.entity.Amount;
import jp.rough_diamond.commons.entity.Quantity;

/**
 *
 */
@SuppressWarnings("deprecation")
abstract public class VelocityUtilsDepracated {
	@Deprecated
	public String formatAmount(Amount amount) {
		return formatAmount(amount, true, false);
	}

	@Deprecated
	public String formatAmount(Amount amount, boolean isPrefix, boolean isSuffix) {
		return formatAmount(amount, isPrefix, isSuffix, "#,##0", amount
				.getUnit().getScale());
	}

	@Deprecated
	public String formatAmount(Amount amount, String formatOfIntegralPart) {
		return formatAmount(amount, true, false, formatOfIntegralPart, amount
				.getUnit().getScale());
	}

	@Deprecated
	public String formatAmount(Amount amount, int scale) {
		return formatAmount(amount, true, false, "#,##0", scale);
	}

	@Deprecated
	public String formatAmount(Amount amount, boolean isPrefix,
			boolean isSuffix, String formatOfIntegralPart) {
		return formatAmount(amount, isPrefix, isSuffix, formatOfIntegralPart,
				amount.getUnit().getScale());
	}

	@Deprecated
	public String formatAmount(Amount amount, boolean isPrefix,
			boolean isSuffix, int scale) {
		return formatAmount(amount, isPrefix, isSuffix, "#,##0", scale);
	}

	@Deprecated
	public String formatAmount(Amount amount, String formatOfIntegralPart,
			int scale) {
		return formatAmount(amount, true, false, formatOfIntegralPart, scale);
	}

	@Deprecated
	public String formatAmount(Quantity quantity) {
		return formatAmount(quantity, true, false);
	}

	@Deprecated
	public String formatAmount(Quantity quantity, boolean isPrefix, boolean isSuffix) {
		return formatAmount(quantity, isPrefix, isSuffix, "#,##0", quantity.getUnit().getScale());
	}

	@Deprecated
	public String formatAmount(Quantity quantity, String formatOfIntegralPart) {
		return formatAmount(quantity, true, false, formatOfIntegralPart, quantity.getUnit().getScale());
	}

	@Deprecated
	public String formatAmount(Quantity quantity, int scale) {
		return formatAmount(quantity, true, false, "#,##0", scale);
	}

	@Deprecated
	public String formatAmount(Quantity quantity, boolean isPrefix,
			boolean isSuffix, String formatOfIntegralPart) {
		return formatAmount(quantity, isPrefix, isSuffix, formatOfIntegralPart,
				quantity.getUnit().getScale());
	}

	@Deprecated
	public String formatAmount(Quantity quantity, boolean isPrefix,
			boolean isSuffix, int scale) {
		return formatAmount(quantity, isPrefix, isSuffix, "#,##0", scale);
	}

	@Deprecated
	public String formatAmount(Quantity quantity, String formatOfIntegralPart,
			int scale) {
		return formatAmount(quantity, true, false, formatOfIntegralPart, scale);
	}

	/**
	 * �ʂ��t�H�[�}�b�e�B���O����
	 * 
	 * @param amount
	 *            ��
	 * @param isPrefix
	 *            �P�ʖ���擪�ɕt�^����
	 * @param isSuffix
	 *            �P�ʖ��𖖔��ɕt�^����
	 * @param formatOfIntegralPart
	 *            �������̃t�H�[�}�b�g
	 * @param scale
	 *            �����_�ȉ��̌���
	 * @return
	 * @exception IllegalArgumentException
	 *                isPrefix�AisSuffix�Ƃ���true�̏ꍇ
	 * @author imai
	 */
	@Deprecated
	public String formatAmount(Amount amount, boolean isPrefix,
			boolean isSuffix, String formatOfIntegralPart, int scale) {
		Quantity q = new Quantity(amount.getQuantity(), amount.getUnit());
		return formatQuantity(q, isPrefix, isSuffix, formatOfIntegralPart, scale);
	}

	@Deprecated
	public String formatAmount(Quantity quantity, boolean isPrefix,
			boolean isSuffix, String formatOfIntegralPart, int scale) {
		return formatQuantity(quantity, isPrefix, isSuffix, formatOfIntegralPart, scale);
	}

	/**
	 * �ʂ��t�H�[�}�b�e�B���O����
	 * 
	 * @param amount
	 *            ��
	 * @param isPrefix
	 *            �P�ʖ���擪�ɕt�^����
	 * @param isSuffix
	 *            �P�ʖ��𖖔��ɕt�^����
	 * @param formatOfIntegralPart
	 *            �������̃t�H�[�}�b�g
	 * @param scale
	 *            �����_�ȉ��̌���
	 * @return
	 * @exception IllegalArgumentException
	 *                isPrefix�AisSuffix�Ƃ���true�̏ꍇ
	 */
	abstract public String formatQuantity(Quantity q, boolean isPrefix, boolean isSuffix, String formatOfIntegralPart, int scale);
}

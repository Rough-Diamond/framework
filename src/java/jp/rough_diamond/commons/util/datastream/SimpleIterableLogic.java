package jp.rough_diamond.commons.util.datastream;

import java.util.Iterator;
import java.util.Queue;

/**
 * DataSource���ȈՂɍ쐬���邽�߂̒��ۃN���X
 * �P�̃\�[�X�I�u�W�F�N�g���畡���̃f�B�X�e�B�l�[�V�����I�u�W�F�N�g��ԋp����K�v������������́A���̋t�̃P�[�X�̏ꍇ�́A
 * Transformer���ȈՂɍ쐬�ł���B
 * @param <D>
 * @param <S>
 */
abstract public class SimpleIterableLogic<D, S> implements IterableLogic<D> {
	final Iterator<S> iterator;
	
	public SimpleIterableLogic(DataSource<S> ds) {
		this.iterator = ds.iterator();
	}
	
	@Override
	public Queue<D> getNextQueue() {
		return getNextQueue(iterator);
	}

	/**
	/**
	 * �K�v�ȃf�[�^���P�ȏ�L���[�ɋl�߂ĕԋp����
	 * @param iterator	���ƂƂȂ�f�[�^�\�[�X
	 * @return�@�f�[�^������ȏ㑶�݂��Ȃ��ꍇ�ɂ�null��ԋp���鎖
	 */
	abstract protected Queue<D> getNextQueue(Iterator<S> iterator);
}

package jp.rough_diamond.commons.util.datastream;

import java.util.Queue;

/**
 * Iterable�I�u�W�F�N�g����邽�߂̃��W�b�N 
 * @param <T>
 */
public interface IterableLogic<T> {
	/**
	 * �K�v�ȃf�[�^���P�ȏ�L���[�ɋl�߂ĕԋp����
	 * @return�@�f�[�^������ȏ㑶�݂��Ȃ��ꍇ�ɂ�null��ԋp���鎖
	 */
	public Queue<T> getNextQueue();
}

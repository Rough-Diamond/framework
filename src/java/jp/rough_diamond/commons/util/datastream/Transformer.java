package jp.rough_diamond.commons.util.datastream;

import java.util.Iterator;

/**
 * ����f�[�^�\�[�X����Ⴄ�^�֕ϊ��������͓����^�ł����e�����H���邽�߂̃f�[�^�\�[�X
 *�@��{�I�ɂP�̃\�[�X�I�u�W�F�N�g����P�̃f�B�X�e�B�l�[�V�����I�u�W�F�N�g�����̂ɗp����B
 * ��Ώ̂̏ꍇ��SimpleIterableLogic���p�������N���X��p���č쐬���������ǂ�
 * @param <S>
 * @param <D>
 */
abstract public class Transformer<S, D> implements DataSource<D> {
	final DataSource<S> srcDS;
	/**
	 * @param srcDS	�\�[�X�I�u�W�F�N�g�̃f�[�^�\�[�X
	 */
	public Transformer(DataSource<S> srcDS) {
		this.srcDS = srcDS;
	}

	@Override
	public Iterator<D> iterator() {
		Iterator<S> srcIterator = srcDS.iterator();
		return new IteratorImpl<D>(srcIterator);
	}
	
	/**
	 * iterator()�ŕԋp�����Iterator��hasNext�̕ԋp����
	 * @param iterator
	 * @return see {@link Iterator#hasNext()}
	 */
	protected boolean hasNext(Iterator<S> iterator) {
		return iterator.hasNext();
	}
	
	/**
	 * iterator()�ŕԋp�����Iterator��next�̕ԋp����
	 * @param srcIterator
	 * @return see {@link Iterator#next()}
	 */
	protected D next(Iterator<S> srcIterator) {
		S src = srcIterator.next();
		return transform(src);
	}

	/**
	 * �I�u�W�F�N�g�̕ϊ����s�����ۃ��\�b�h
	 * @param src
	 * @return
	 */
	abstract protected D transform(S src);
	
	class IteratorImpl<E> implements Iterator<E> {
		final Iterator<S> srcIterator;
		IteratorImpl(Iterator<S> iterator) {
			this.srcIterator = iterator;
		}
		
		@Override
		public boolean hasNext() {
			return Transformer.this.hasNext(srcIterator);
		}

		@Override
		@SuppressWarnings("unchecked")
		public E next() {
			return (E)Transformer.this.next(srcIterator);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}

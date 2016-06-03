package jp.rough_diamond.commons.util.datastream;

import java.util.Queue;

/**
 * Iterableオブジェクトを作るためのロジック 
 * @param <T>
 */
public interface IterableLogic<T> {
	/**
	 * 必要なデータを１つ以上キューに詰めて返却する
	 * @return　データがこれ以上存在しない場合にはnullを返却する事
	 */
	public Queue<T> getNextQueue();
}

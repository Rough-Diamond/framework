package jp.rough_diamond.commons.util.datastream;

/**
 * データソース
 * 何らかのシーケンシャルリード可能なオブジェクトの集合を表すインタフェース
 * @param <T>
 */
public interface DataSource<T> extends Iterable<T> {
}

/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
/*
 * $Id: Pager.java 157 2006-02-14 10:22:14Z Matsuda_Kazuto@ogis-ri.co.jp $
 * $Header$
 */
package jp.rough_diamond.commons.pager;

import java.io.Serializable;
import java.util.List;

/**
 * コレクションをページングするインタフェース
 * @author $Author: Matsuda_Kazuto@ogis-ri.co.jp $
 */
public interface Pager<E> extends Serializable {
    /**
     * 現在のページを取得
    **/
    public int getCurrentPage();

    /**
     * 総ページ数を取得
     * @return ページ総数
    **/
    public int getPageSize();

    /**
     * コレクションの総数を取得
     * @return コレクション総数
    **/
    public long getSize();
    
    /**
     * １ページに表示するコレクション件数を取得
     * @return １ページに表示するコレクション件数
    **/
    public int getSizePerPage();
    
    /**
     * 現在のページに表示するコレクションを表示する
     * @return 現在のページに表示するコレクション
    **/
    public List<E> getCurrentPageCollection();
    
    /**
     * 先頭ページチェック
     * @return 先頭ページの場合にtrue
    **/
    public boolean isFirst();
    
    /**
     * 最終ページチェック
     * @return 最終ページの場合にtrue
    **/
    public boolean isLast();
    
    /**
     * 前ページへ遷移する
     * @precondition 先頭ページではないこと
    **/
    public void previous();
    
    /**
     * 次ページへ遷移する
     * @precondition 最終ページではないこと
    **/
    public void next();
    
    /**
     * 指定ページへ遷移する
     * @precondition 1 <= page <= getPageSize()
     * @param   page    ジャンプするページ
    **/
    public void gotoPage(int page);
    
    /**
     * 表示開始ページ数を取得する
     * @return 表示開始ページ
    **/
    public int getWindFirst();
    
    /**
     * 表示終了ページ数を取得する
     * @return 表示終了ページ
    **/
    public int getWindFinish();
    
    /**
     * 表示している最初のエレメントの位置(1相対)を取得する
     * @return 表示している最初のエレメントの位置
    **/
    public long getIndexAtFirstElement();
    
    /**
     * 表示している最後のエレメントの位置(1相対）を返却する
     * @return 表示している最後のエレメントの位置
    **/
    public long getIndexAtLastElement();
}
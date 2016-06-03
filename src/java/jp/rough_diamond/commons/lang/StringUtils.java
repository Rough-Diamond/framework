/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 文字列ユーティリティ
 * 本クラスは、jakarta commons langのStringUtilsの拡張APIです。
 * @since 1.0
 * @see <a href="http://commons.apache.org/lang/" target="_blank">jakarta commons lang</a> 
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {
    public static enum Direction {
        /**
         * 左方向から文字詰め
         */
        LEFT, 
        /**
         * 右方向から文字詰め
         */
        RIGHT;
    }

    private final static Map<Direction, SubstringStrategy> substringStrategy;
    static {
        Map<Direction, SubstringStrategy> tmp = new HashMap<Direction, SubstringStrategy>();
        tmp.put(Direction.LEFT, new ReverseStrategy());
        tmp.put(Direction.RIGHT, new NotReverseStrategy());
        substringStrategy = Collections.unmodifiableMap(tmp);
    }

    /**
     * 指定された文字コードで指定された文字列長を超えない文字列を返却する
     * 元の文字列のバイト数＞最大バイト数の場合は、文字列の後半を削除します 
     * @param str       元の文字列
     * @param length    最大バイト数
     * @param charset   文字コード
     */
    public static String substring(String str, int length, String charset) {
    	return substring(str, length, charset, Direction.RIGHT);
    }
    
    /**
     * 指定された文字コードで指定された文字列長を超えない文字列を返却する 
     * @param str       元の文字列
     * @param length    最大バイト数
     * @param charset   文字コード
     * @param direction 文字列を削除する方向
     * 　　　　　　　　　	Direction.LEFT  文字列の前半を削除します
     * 						Direction.RIGHT 文字列の後半を削除します
     * @return
     */
    public static String substring(String str, int length, String charset, Direction direction) {
        try {
            byte[] array = str.getBytes(charset);
            if(array.length <= length) {
                return str;
            }
            SubstringStrategy strategy = substringStrategy.get(direction);
            str = strategy.reverse(str);
            char[] charArray = str.toCharArray();
            String ret = "";
            StringBuilder b = new StringBuilder();
            for(char ch : charArray) {
                b.append(ch);
                String tmp = b.toString();
                if(tmp.getBytes(charset).length > length) {
                    break;
                }
                ret = tmp;
            }
            return strategy.reverse(ret);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    

    /**
     * 文字列の前後のホワイトスペースを全て取り除いたものを返却する。
     * 文字列がnullの場合はnullを返却する。
     * 主に、全角空白を取り除きたいときに使用すると良い。
     * @param str
     * @return nullの場合はnull、nullではない場合は、文字列前後のホワイトスペースを取り除いた文字列を返却する
     */
    public static String trimWhitespace(String str) {
    	if(str == null) {
    		return null;
    	}
    	int start = -1;
    	char[] array = str.toCharArray();
    	for(int i = 0 ; i < array.length ; i++) {
    		if(!Character.isWhitespace(array[i])) {
    			start = i;
    			break;
    		}
    	}
    	if(start == -1) {
    		return EMPTY;
    	}
    	int fin = -1;
    	for(int i = array.length - 1 ; i >= 0 ; i--) {
    		if(!Character.isWhitespace(array[i])) {
    			fin = i;
    			break;
    		}
    	}
    	return str.substring(start, fin + 1);
    }

    /**
     * 文字列の前後のホワイトスペースを全て取り除いたものを返却する。
     * ホワイトスペースを取り除いた結果が空文字列の場合は、nullを返却する
     * 文字列がnullの場合はnullを返却する。
     * 主に、全角空白を取り除きたいときに使用すると良い。
     * @param str
     * @return　nullの場合はnull、nullではない場合は、文字列前後のホワイトスペースを取り除いた文字列を返却する
     *           但し、ホワイトスペースを取り除いた結果が空文字列の場合はnullを返却する。
     */
    public static String trimWhitespaceToNull(String str) {
    	String ret = trimWhitespace(str);
    	return isEmpty(ret) ? null : ret;
    }
    
    /**
     * 文字列の前後のホワイトスペースを全て取り除いたものを返却する。
     * 文字列がnullの場合は空文字列を返却する。
     * 主に、全角空白を取り除きたいときに使用すると良い。
     * @param str
     * @return nullの場合は空文字列、nullではない場合は、文字列前後のホワイトスペースを取り除いた文字列を返却する
     */
    public static String trimWhitespaceToEmpty(String str) {
    	String ret = trimWhitespace(str);
    	return ret == null ? EMPTY : ret;
    }

    /**
     * Integer.parseInt(String)が可能な場合にtrueを返却する
     * 但し、全角文字を含んでいる場合は、falseを返却する。
     * @param str
     * @return trueの場合は、全て半角文字でInteger.parseInt可能である
     */
    public static boolean isParseInt(String str) {
    	return isParse(PARSE_INT, str);
    }
    
    /**
     * Long.parseLong(String)が可能な場合にtrueを返却する
     * 但し、全角文字を含んでいる場合は、falseを返却する。
     * @param str
     * @return trueの場合は、全て半角文字でLong.parseLong可能である
     */
    public static boolean isParseLong(String str) {
    	return isParse(PARSE_LONG, str);
    }

    /**
     * 全ての文字が半角の場合にtrueを返却する。
     * ここで言う半角文字とは、UTF-8で１文字＝１バイトの文字を指す。
     * nullの場合は、falseを返却する。
     * @param str
     * @return
     */
    public static boolean isHalfCharacterString(String str) {
    	if(str == null) {
    		return false;
    	}
    	try {
			byte[] array = str.getBytes("UTF-8");
			return array.length == str.length();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }
    
    private final static Method PARSE_INT;
    private final static Method PARSE_LONG;
    static {
    	try {
			PARSE_INT = Integer.class.getMethod("parseInt", String.class);
			PARSE_LONG = Long.class.getMethod("parseLong", String.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
    }
    
    private static boolean isParse(Method m, String str) {
    	if(!isHalfCharacterString(str)) {
    		return false;
    	}
    	try {
    		m.invoke(null, str);
    		return true;
    	} catch(Exception e) {
    		return false;
    	}
    }
    
    private static interface SubstringStrategy {
        public String reverse(String str);
    }
    
    private static class NotReverseStrategy implements SubstringStrategy {
        public String reverse(String str) {
            return str;
        }
    }
    
    private static class ReverseStrategy implements SubstringStrategy {
        public String reverse(String str) {
            return StringUtils.reverse(str);
        }
    }
}

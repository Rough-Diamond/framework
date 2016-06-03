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
 * �����񃆁[�e�B���e�B
 * �{�N���X�́Ajakarta commons lang��StringUtils�̊g��API�ł��B
 * @since 1.0
 * @see <a href="http://commons.apache.org/lang/" target="_blank">jakarta commons lang</a> 
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {
    public static enum Direction {
        /**
         * ���������當���l��
         */
        LEFT, 
        /**
         * �E�������當���l��
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
     * �w�肳�ꂽ�����R�[�h�Ŏw�肳�ꂽ�����񒷂𒴂��Ȃ��������ԋp����
     * ���̕�����̃o�C�g�����ő�o�C�g���̏ꍇ�́A������̌㔼���폜���܂� 
     * @param str       ���̕�����
     * @param length    �ő�o�C�g��
     * @param charset   �����R�[�h
     */
    public static String substring(String str, int length, String charset) {
    	return substring(str, length, charset, Direction.RIGHT);
    }
    
    /**
     * �w�肳�ꂽ�����R�[�h�Ŏw�肳�ꂽ�����񒷂𒴂��Ȃ��������ԋp���� 
     * @param str       ���̕�����
     * @param length    �ő�o�C�g��
     * @param charset   �����R�[�h
     * @param direction ��������폜�������
     * �@�@�@�@�@�@�@�@�@	Direction.LEFT  ������̑O�����폜���܂�
     * 						Direction.RIGHT ������̌㔼���폜���܂�
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
     * ������̑O��̃z���C�g�X�y�[�X��S�Ď�菜�������̂�ԋp����B
     * ������null�̏ꍇ��null��ԋp����B
     * ��ɁA�S�p�󔒂���菜�������Ƃ��Ɏg�p����Ɨǂ��B
     * @param str
     * @return null�̏ꍇ��null�Anull�ł͂Ȃ��ꍇ�́A������O��̃z���C�g�X�y�[�X����菜�����������ԋp����
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
     * ������̑O��̃z���C�g�X�y�[�X��S�Ď�菜�������̂�ԋp����B
     * �z���C�g�X�y�[�X����菜�������ʂ��󕶎���̏ꍇ�́Anull��ԋp����
     * ������null�̏ꍇ��null��ԋp����B
     * ��ɁA�S�p�󔒂���菜�������Ƃ��Ɏg�p����Ɨǂ��B
     * @param str
     * @return�@null�̏ꍇ��null�Anull�ł͂Ȃ��ꍇ�́A������O��̃z���C�g�X�y�[�X����菜�����������ԋp����
     *           �A���A�z���C�g�X�y�[�X����菜�������ʂ��󕶎���̏ꍇ��null��ԋp����B
     */
    public static String trimWhitespaceToNull(String str) {
    	String ret = trimWhitespace(str);
    	return isEmpty(ret) ? null : ret;
    }
    
    /**
     * ������̑O��̃z���C�g�X�y�[�X��S�Ď�菜�������̂�ԋp����B
     * ������null�̏ꍇ�͋󕶎����ԋp����B
     * ��ɁA�S�p�󔒂���菜�������Ƃ��Ɏg�p����Ɨǂ��B
     * @param str
     * @return null�̏ꍇ�͋󕶎���Anull�ł͂Ȃ��ꍇ�́A������O��̃z���C�g�X�y�[�X����菜�����������ԋp����
     */
    public static String trimWhitespaceToEmpty(String str) {
    	String ret = trimWhitespace(str);
    	return ret == null ? EMPTY : ret;
    }

    /**
     * Integer.parseInt(String)���\�ȏꍇ��true��ԋp����
     * �A���A�S�p�������܂�ł���ꍇ�́Afalse��ԋp����B
     * @param str
     * @return true�̏ꍇ�́A�S�Ĕ��p������Integer.parseInt�\�ł���
     */
    public static boolean isParseInt(String str) {
    	return isParse(PARSE_INT, str);
    }
    
    /**
     * Long.parseLong(String)���\�ȏꍇ��true��ԋp����
     * �A���A�S�p�������܂�ł���ꍇ�́Afalse��ԋp����B
     * @param str
     * @return true�̏ꍇ�́A�S�Ĕ��p������Long.parseLong�\�ł���
     */
    public static boolean isParseLong(String str) {
    	return isParse(PARSE_LONG, str);
    }

    /**
     * �S�Ă̕��������p�̏ꍇ��true��ԋp����B
     * �����Ō������p�����Ƃ́AUTF-8�łP�������P�o�C�g�̕������w���B
     * null�̏ꍇ�́Afalse��ԋp����B
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

/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.serializer;

import java.io.*;
import  java.lang.reflect.*;
import  java.text.ParseException;
import  java.util.*;

import org.apache.commons.logging.*;

/**
 * 何らかの文字で値が分割されている文字列を二次元配列に展開する
**/
public class SVParser implements Iterable<String[]> {
    public final static Log log = LogFactory.getLog(SVParser.class); 

    private BufferedReader  reader;
    private String          separator;
    private String          quote;
    private String          lineSeparator;
    private String          whiteSpaces;
    private ParseException  pe = null;
    
    /**
     * 生成子
     * @param   target          解析対象文字列
     * @param   separator       値を分割する文字列
     * @param   quote           特殊な値を認識するための文字列。quoteを値に含める場合は、quoteを重ねる。
     * @param   lineSeparator   行区切り文字
     * @param   whiteSpaces     無為文字群
     * @exception   ParseException  データが適切なフォーマットで格納されていない
    **/
    public SVParser(Reader reader, String separator, 
            String quote, String lineSeparator, 
                    String whiteSpaces) throws IOException {
        this.reader = new BufferedReader(reader);
        this.separator = separator;
        this.quote = quote;
        this.lineSeparator = lineSeparator;
        this.whiteSpaces = whiteSpaces;
    }
    
    /**
     * 行区切りIterator取得
     * @return 行区切りIterator内部には、Stringの配列が格納されている
    **/
    public Iterator<String[]> getLineIterator() {
        try {
            return new RowIterator();
        } catch(IOException e) {
            log.warn("生成時にエラーになってないのにエラーがでる。おかしい", e);
            throw new RuntimeException(e);
        }
    }
    
    public Iterator<String[]> iterator() {
        return getLineIterator();
    }

    public boolean isParseError() {
        return (pe != null);
    }
    
    public ParseException getException() {
        return pe;
    }
    
    final static Map<String, Method>    METHOD_MAP;
    final static String STRING = "string";
    final static String SEPARATOR = "separator";
    final static String QUOTE = "quote";
    final static String LINE_SEPARATOR = "lineSeparator";
    final static String WHITE_SPACE = "whiteSpace";
    final static String FINISH = "finish";
    
    static {
        try {
            HashMap<String, Method> map = new HashMap<String, Method>();
            Class<State> cl = State.class;
            Class<?>[] paramTypes = new Class[]{String.class};
            map.put(STRING, cl.getMethod(
                    "fireStringEvent", paramTypes));
            map.put(SEPARATOR, cl.getMethod(
                    "fireSeparatorEvent", paramTypes));
            map.put(QUOTE, cl.getMethod(
                    "fireQuoteEvent", paramTypes));
            map.put(LINE_SEPARATOR, cl.getMethod(
                    "fireLineSeparatorEvent", paramTypes));
            map.put(WHITE_SPACE, cl.getMethod(
                    "fireWhiteSpaceEvent", paramTypes));
            METHOD_MAP = Collections.unmodifiableMap(map);
        } catch(Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * 解析状態
    **/
    protected static interface State {
        /**
         * 文字列出現イベント
         * @return 出現した文字列
        **/
        public void fireStringEvent(String text) throws ParseException;
        /**
         * セパレーター出現イベント
         * @return 出現した文字列
        **/
        public void fireSeparatorEvent(String text) throws ParseException;
        /**
         * クォート出現イベント
         * @return 出現した文字列
        **/
        public void fireQuoteEvent(String text) throws ParseException;
        /**
         * 改行出現イベント
         * @return 出現した文字列
        **/
        public void fireLineSeparatorEvent(String text) throws ParseException;
        /**
         * 無為文字出現イベント
         * @return 出現した文字列
        **/
        public void fireWhiteSpaceEvent(String text) throws ParseException;
        /**
         * 解析完了イベント
        **/
        public void fireFinishEvent() throws ParseException;
    }
    
    /**
     * StateインタフェースのNullObjectパターン
    **/
    protected final static class NullState implements State {
        private NullState() { }
        public void fireStringEvent(String text) throws ParseException {
            throw new ParseException("", -1);
        }
        
        public void fireSeparatorEvent(String text) throws ParseException {
            throw new ParseException("", -1);
        }
            
        public void fireQuoteEvent(String text) throws ParseException {
            throw new ParseException("", -1);
        }
                
        public void fireLineSeparatorEvent(String text) throws ParseException {
            throw new ParseException("", -1);
        }
                    
        public void fireWhiteSpaceEvent(String text) throws ParseException {
            throw new ParseException("", -1);
        }
                        
        public void fireFinishEvent() throws ParseException {
            throw new ParseException("", -1);
        }
        
        /**
         * シングルトンオブジェクトを返却
         * @return  NullStateのシングルトンオブジェクト
        **/
        protected static NullState getInstance() {
            return singleton;
        }
        
        private final static NullState singleton = new NullState();
    }
    
    static class Token {
        Token(String token, String kind) {
            this.token = token;
            this.tokenKind = kind;
        }
        
        final String token;
        final String tokenKind;
    }
    
    class RowIterator implements Iterator<String[]> {
        private String[] preReadRow;
        private TokenIterator iterator;
        private StringBuffer    columnBuffer;
        private List<String>    line;
        private List<String[]>  lines;          //溜めてない
        private boolean         isLineFinish;
        private String[]        bafferdLine;
        private State           initState;
        private State           stringState;
        private State           innerQuoteStringState;
        private State           columnWaitState;
        private State           quoteState;
        private State           delimiterWaitState;
        private State           currentState;
        private State           lastState;
        
        RowIterator() throws IOException {
            preParse();
            iterator = new TokenIterator();
        }
        
        public boolean hasNext() {
            if(preReadRow == null) {
                try {
                    preReadRow = getNextRow();
                } catch (ParseException e) {
                    pe = e;
                    preReadRow = null;
                }
            }
            return (preReadRow != null);
        }

        public String[] next() {
            String[] ret = preReadRow;
            preReadRow = null;
            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        String[] getNextRow() throws ParseException {
            isLineFinish = false;
            bafferdLine = null;
            Object[] params = new String[1];
            try {
                while(iterator.hasMoreTokens()) {
                    Token token = iterator.nextToken();
                    if(log.isTraceEnabled()) {
	                    log.trace("kind   :" + token.tokenKind);
	                    log.trace("content:" + token.token);
                    }
                    State state = getCurrentState();
                    if(log.isTraceEnabled()) {
                    	log.trace("state  :" + state.getClass().getName());
                    }
                    Method m = (Method)METHOD_MAP.get(token.tokenKind);
                    if(m == null) {
                        throw new ParseException("", -1);
                    }
                    params[0] = token.token;
                    m.invoke(state, params);
                    if(isLineFinish) {
                        return bafferdLine;
                    }
                }
                getCurrentState().fireFinishEvent();
                return bafferdLine;
            } catch(InvocationTargetException ite) {
                Throwable t = ite.getTargetException();
                if(t instanceof ParseException) {
                    throw (ParseException)t;
                }
                log.error(t.getMessage(), t);
                throw new RuntimeException(t);
            } catch(Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }

        private void preParse() throws IOException {
            columnBuffer = new StringBuffer();
            initState = new InitState();
            stringState = new StringState();
            quoteState = new QuoteState();
            columnWaitState = new ColumnWaitState();
            innerQuoteStringState = new InnerQuoteStringState();
            delimiterWaitState = new DelimiterWaitState();
            line = new LinkedList<String>();
            lines = new LinkedList<String[]>();
            setCurrentState(getInitState());
        }

        /**
         * 現在編集文字にテキストを追加する
         * @param   text    追加対象文字列
        **/
        protected void addText(String text) {
            getColumnBuffer().append(text);
        }
        
        /**
         * 現在編集中の行に現在編集中の文字をカラムとして追加する
        **/
        protected void addColumn() {
            StringBuffer buf = getColumnBuffer();
            getCurrentLine().add(buf.toString());
            buf.setLength(0);
        }

        /**
         * 現在編集中の行をトラバース対象に加える
        **/
        protected void addLine() {
            bafferdLine = getCurrentLine().toArray(new String[0]);
            isLineFinish = true;
            setCurrentLine(new LinkedList<String>());
        }

        /**
         * 現在編集中の文字列バッファを取得する
         * @return 現在編集中の文字列バッファ
        **/
        protected StringBuffer getColumnBuffer() {
            return columnBuffer;
        }

        /**
         * 現在編集中の行を取得する
         * @return 現在編集中の行
        **/
        protected List<String> getCurrentLine() {
            return line;
        }
        
        /**
         * トラバース可能行集合を取得する
         * @return  トラバース可能行集合
        **/
        protected List<String[]> getLines() {
            return lines;
        }

        /**
         * 現在の解析状態を取得する
         * @return 現在の解析状態
        **/
        protected State getCurrentState() {
            return currentState;
        }
        
        /**
         * 直前の解析状態を取得する
         * @return 直前の解析状態
        **/
        protected State getLastState() {
            return lastState;
        }
        
        /**
         * 初期状態の取得
         * @return 初期状態
        **/
        protected State getInitState() {
            return initState;
        }

        /**
         * 文字列解析状態の取得
         * @return 文字列解析状態の取得
        **/
        protected State getStringState() {
            return stringState;
        }
        
        /**
         * クォート内文字列解析状態の取得
         * @return クォート内文字列解析状態
        **/
        protected State getInnerQuoteStringState() {
            return innerQuoteStringState;
        }
        
        /**
         * クォート出現状態の取得
         * @return クォート出現状態
        **/
        protected State getQuoteState() {
            return quoteState;
        }
        
        /**
         * カラム出現待ち状態の取得
         * @return カラム出現待ち状態
        **/
        protected State getColumnWaitState() {
            return columnWaitState;
        }
        
        /**
         * デリミタ出現待ち状態の取得
         * @return デリミタ出現待ち状態
        **/
        protected State getDelimiterWaitState() {
            return delimiterWaitState;
        }
        
        /**
         * 現在編集行の作成
         * @param   以降利用する現在編集行
        **/
        protected void setCurrentLine(List<String> line) {
            this.line = line;
        }

        /**
         * 現在の状態の設定
         * @param   現在の状態
        **/
        protected void setCurrentState(State state) {
            setLastState(this.currentState);
            this.currentState = state;
        }
        
        private void setLastState(State state) {
            this.lastState = state;
        }
        
        class TokenIterator {
            TokenIterator() throws IOException {
                this.hasMoreTokens = true;
                readLine();
                prepareToken();
            }
            
            boolean hasMoreTokens() {
                return (nextToken != null);
            }
            
            Token nextToken() throws NoSuchElementException, IOException {
                if(hasMoreTokens()) {
                    Token ret = nextToken;
                    if(hasMoreTokens) {
                        prepareToken();
                    } else {
                        nextToken = null;
                    }
                    return ret;
                } else {
                    throw new NoSuchElementException();
                }
            }
            
            private void prepareToken() throws IOException {
                while(line != null) {
                    for( ; targetIndex < targetLength ; targetIndex++) {
                    	if(log.isTraceEnabled()) {
	                        log.trace("target      :" + (int)line.charAt(targetIndex) + 
	                                                    "(" + line.charAt(targetIndex) + ")");
                    	}
                        if(isSpecialText(separator, SEPARATOR) ||
                           isSpecialText(quote, QUOTE) ||
                           isSpecialText(lineSeparator, LINE_SEPARATOR) ||
                           isWhiteSpaces()) {
                            lastIndex = targetIndex;
                            return;
                        }
                    }
                    nextToken = makeStringToken();
                    readLine();
                }
                hasMoreTokens = false;
            }

            private boolean isWhiteSpaces() {
                boolean ret = whiteSpaces.indexOf(line.charAt(targetIndex)) != -1;
                if(ret) {
                    if(lastIndex == targetIndex) {
                        nextToken = makeWhiteSpaceToken();
                    } else {
                        nextToken = makeStringToken();
                    }
                }
                return ret;
            }
            
            private boolean isSpecialText(String specialText, String kind) {
                boolean ret = isSpecialText(specialText);
                if(ret) {
                    makeToken(specialText, kind);
                }
                return ret;
            }
                    
            private boolean isSpecialText(String specialText) {
                return (line.indexOf(specialText, targetIndex) == targetIndex);
            }
            
            private Token makeWhiteSpaceToken() {
                int startIndex = targetIndex;
                for(targetIndex++ ; targetIndex < targetLength ; targetIndex++) {
                    if(whiteSpaces.indexOf(line.charAt(targetIndex)) == -1) {
                        break;
                    }
                }
                return new Token(line.substring(
                        startIndex, targetIndex), WHITE_SPACE);
            }

            private Token makeStringToken() {
                if((targetLength == targetIndex) &&
                   (targetIndex == lastIndex)) {
                    return null;
                }
                if(log.isDebugEnabled()) {
	                log.debug(line);
	                log.debug(lastIndex);
	                log.debug(targetIndex);
                }
                return new Token(line.substring(
                            lastIndex, targetIndex), STRING);
            }

            private void makeToken(String specialText, String tokenKind) {
                if(lastIndex == targetIndex) {
                    nextToken = new Token(specialText, tokenKind);
                    targetIndex = targetIndex + specialText.length();
                } else {
                    nextToken = makeStringToken();
                }
            }
            
            private void readLine() throws IOException {
                StringBuilder builder = new StringBuilder();
                int ch = reader.read();
                while(ch != -1) {
                    if((char)ch != '\r') {
                        builder.append((char)ch);
                        if(builder.indexOf(lineSeparator) != -1) {
                            break;
                        }
                    }
                    ch = reader.read();
                }
                if(builder.length() == 0) {
                    line = null;
                } else {
                    line = builder.toString();
                }
                lastIndex = 0;
                targetIndex = 0;
                targetLength = (line == null) ? -1 : line.length();
            }
            
            private int         lastIndex;
            private int         targetIndex;
            private int         targetLength;
            private String      line;
            private Token       nextToken;
            private boolean     hasMoreTokens;
        }

        /**
         * Stateインタフェーステンプレート実装
        **/
        protected class SimpleState implements State {
            public void fireStringEvent(String text) throws ParseException { }
            public void fireSeparatorEvent(String text) throws ParseException { }
            public void fireQuoteEvent(String text) throws ParseException { }
            public void fireLineSeparatorEvent(String text)throws ParseException { }
            public void fireWhiteSpaceEvent(String text) throws ParseException { }
            public void fireFinishEvent() throws ParseException { 
                setState(SVParser.NullState.getInstance());
            }

            protected void setState(State state) {
                RowIterator.this.setCurrentState(state);
            }
        }
        
        /**
         * 初期状態
        **/
        protected class InitState extends SimpleState {
            public void fireStringEvent(String text) { 
                RowIterator.this.addText(text);
                setState(RowIterator.this.getStringState());
            }
            public void fireSeparatorEvent(String text) { 
                addColumn();
                setState(getColumnWaitState());
            }
            public void fireQuoteEvent(String text) {
                setState(getInnerQuoteStringState());
            }
            public void fireLineSeparatorEvent(String text) {
                addLine();
            }
        }
        
        /**
         * 表意文字列出現状態
        **/
        protected class StringState extends SimpleState {
            public void fireStringEvent(String text) { 
                addText(text);
            }
            public void fireSeparatorEvent(String text) { 
                addColumn();
                setState(getColumnWaitState());
            }
            public void fireQuoteEvent(String text) throws ParseException {
                throw new ParseException("", -1);
            }
            public void fireLineSeparatorEvent(String text) {
                addColumn();
                addLine();
                setState(getInitState());
            }
            public void fireWhiteSpaceEvent(String text) { 
                addText(text);
            }
            public void fireFinishEvent() throws ParseException {
                addColumn();
                addLine();
                super.fireFinishEvent();
            }
        }
        
        /**
         * クォート内表意文字列出現状態
        **/
        protected class InnerQuoteStringState extends SimpleState {
            public void fireStringEvent(String text) { 
                addText(text);
            }
            public void fireSeparatorEvent(String text) { 
                addText(text);
            }
            public void fireQuoteEvent(String text) throws ParseException {
                setState(getQuoteState());
            }
            public void fireLineSeparatorEvent(String text) {
                addText(text);
            }
            public void fireWhiteSpaceEvent(String text) { 
                addText(text);
            }
            public void fireFinishEvent() throws ParseException {
                throw new ParseException("", -1);
            }
        }
        
        /**
         * クォート出現状態
        **/
        protected class QuoteState extends SimpleState {
            public void fireStringEvent(String text) throws ParseException { 
                throw new ParseException("", -1);
            }
            public void fireSeparatorEvent(String text) { 
                addColumn();
                setState(getColumnWaitState());
            }
            public void fireQuoteEvent(String text) {
                addText(text);
                setState(getLastState());
            }
            public void fireLineSeparatorEvent(String text) {
                addColumn();
                addLine();
                setState(getInitState());
            }
            public void fireWhiteSpaceEvent(String text) { 
                addColumn();
                setState(getDelimiterWaitState());
            }
            public void fireFinishEvent() throws ParseException {
                addColumn();
                addLine();
                super.fireFinishEvent();
            }
        }
        
        /**
         * カラム出現待ち状態
        **/
        protected class ColumnWaitState extends SimpleState {
            public void fireStringEvent(String text) {
                addText(text);
                setState(getStringState());
            }
            public void fireSeparatorEvent(String text) { 
                addColumn();
            }
            public void fireQuoteEvent(String text) {
                setState(getInnerQuoteStringState());
            }
            public void fireLineSeparatorEvent(String text) {
                addColumn();
                addLine();
                setState(getInitState());
            }
            public void fireFinishEvent() throws ParseException {
                addLine();
                super.fireFinishEvent();
            }
        }

        /**
         * デリミタ出現待ち状態
        **/
        protected class DelimiterWaitState extends SimpleState {
            public void fireStringEvent(String text) throws ParseException {
                throw new ParseException("", -1);
            }
            public void fireSeparatorEvent(String text) { 
                setState(getColumnWaitState());
            }
            public void fireQuoteEvent(String text) throws ParseException {
                throw new ParseException("", -1);
            }
            public void fireLineSeparatorEvent(String text) {
                addLine();
                setState(getInitState());
            }
            public void fireFinishEvent() throws ParseException {
                addLine();
                super.fireFinishEvent();
            }
        }
    }
}
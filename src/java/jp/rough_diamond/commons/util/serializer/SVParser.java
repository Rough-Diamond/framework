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
 * ���炩�̕����Œl����������Ă��镶�����񎟌��z��ɓW�J����
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
     * �����q
     * @param   target          ��͑Ώە�����
     * @param   separator       �l�𕪊����镶����
     * @param   quote           ����Ȓl��F�����邽�߂̕�����Bquote��l�Ɋ܂߂�ꍇ�́Aquote���d�˂�B
     * @param   lineSeparator   �s��؂蕶��
     * @param   whiteSpaces     ���ו����Q
     * @exception   ParseException  �f�[�^���K�؂ȃt�H�[�}�b�g�Ŋi�[����Ă��Ȃ�
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
     * �s��؂�Iterator�擾
     * @return �s��؂�Iterator�����ɂ́AString�̔z�񂪊i�[����Ă���
    **/
    public Iterator<String[]> getLineIterator() {
        try {
            return new RowIterator();
        } catch(IOException e) {
            log.warn("�������ɃG���[�ɂȂ��ĂȂ��̂ɃG���[���ł�B��������", e);
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
     * ��͏��
    **/
    protected static interface State {
        /**
         * ������o���C�x���g
         * @return �o������������
        **/
        public void fireStringEvent(String text) throws ParseException;
        /**
         * �Z�p���[�^�[�o���C�x���g
         * @return �o������������
        **/
        public void fireSeparatorEvent(String text) throws ParseException;
        /**
         * �N�H�[�g�o���C�x���g
         * @return �o������������
        **/
        public void fireQuoteEvent(String text) throws ParseException;
        /**
         * ���s�o���C�x���g
         * @return �o������������
        **/
        public void fireLineSeparatorEvent(String text) throws ParseException;
        /**
         * ���ו����o���C�x���g
         * @return �o������������
        **/
        public void fireWhiteSpaceEvent(String text) throws ParseException;
        /**
         * ��͊����C�x���g
        **/
        public void fireFinishEvent() throws ParseException;
    }
    
    /**
     * State�C���^�t�F�[�X��NullObject�p�^�[��
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
         * �V���O���g���I�u�W�F�N�g��ԋp
         * @return  NullState�̃V���O���g���I�u�W�F�N�g
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
        private List<String[]>  lines;          //���߂ĂȂ�
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
         * ���ݕҏW�����Ƀe�L�X�g��ǉ�����
         * @param   text    �ǉ��Ώە�����
        **/
        protected void addText(String text) {
            getColumnBuffer().append(text);
        }
        
        /**
         * ���ݕҏW���̍s�Ɍ��ݕҏW���̕������J�����Ƃ��Ēǉ�����
        **/
        protected void addColumn() {
            StringBuffer buf = getColumnBuffer();
            getCurrentLine().add(buf.toString());
            buf.setLength(0);
        }

        /**
         * ���ݕҏW���̍s���g���o�[�X�Ώۂɉ�����
        **/
        protected void addLine() {
            bafferdLine = getCurrentLine().toArray(new String[0]);
            isLineFinish = true;
            setCurrentLine(new LinkedList<String>());
        }

        /**
         * ���ݕҏW���̕�����o�b�t�@���擾����
         * @return ���ݕҏW���̕�����o�b�t�@
        **/
        protected StringBuffer getColumnBuffer() {
            return columnBuffer;
        }

        /**
         * ���ݕҏW���̍s���擾����
         * @return ���ݕҏW���̍s
        **/
        protected List<String> getCurrentLine() {
            return line;
        }
        
        /**
         * �g���o�[�X�\�s�W�����擾����
         * @return  �g���o�[�X�\�s�W��
        **/
        protected List<String[]> getLines() {
            return lines;
        }

        /**
         * ���݂̉�͏�Ԃ��擾����
         * @return ���݂̉�͏��
        **/
        protected State getCurrentState() {
            return currentState;
        }
        
        /**
         * ���O�̉�͏�Ԃ��擾����
         * @return ���O�̉�͏��
        **/
        protected State getLastState() {
            return lastState;
        }
        
        /**
         * ������Ԃ̎擾
         * @return �������
        **/
        protected State getInitState() {
            return initState;
        }

        /**
         * �������͏�Ԃ̎擾
         * @return �������͏�Ԃ̎擾
        **/
        protected State getStringState() {
            return stringState;
        }
        
        /**
         * �N�H�[�g���������͏�Ԃ̎擾
         * @return �N�H�[�g���������͏��
        **/
        protected State getInnerQuoteStringState() {
            return innerQuoteStringState;
        }
        
        /**
         * �N�H�[�g�o����Ԃ̎擾
         * @return �N�H�[�g�o�����
        **/
        protected State getQuoteState() {
            return quoteState;
        }
        
        /**
         * �J�����o���҂���Ԃ̎擾
         * @return �J�����o���҂����
        **/
        protected State getColumnWaitState() {
            return columnWaitState;
        }
        
        /**
         * �f���~�^�o���҂���Ԃ̎擾
         * @return �f���~�^�o���҂����
        **/
        protected State getDelimiterWaitState() {
            return delimiterWaitState;
        }
        
        /**
         * ���ݕҏW�s�̍쐬
         * @param   �ȍ~���p���錻�ݕҏW�s
        **/
        protected void setCurrentLine(List<String> line) {
            this.line = line;
        }

        /**
         * ���݂̏�Ԃ̐ݒ�
         * @param   ���݂̏��
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
         * State�C���^�t�F�[�X�e���v���[�g����
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
         * �������
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
         * �\�ӕ�����o�����
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
         * �N�H�[�g���\�ӕ�����o�����
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
         * �N�H�[�g�o�����
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
         * �J�����o���҂����
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
         * �f���~�^�o���҂����
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
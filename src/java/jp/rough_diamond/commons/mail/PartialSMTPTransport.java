/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.mail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.smtp.SMTPTransport;

public class PartialSMTPTransport extends SMTPTransport {
	private final static Log log = LogFactory.getLog(PartialSMTPTransport.class);
	
    private static final String[] ignoreList = { "Bcc", "Content-Length" };	//継承もとよりprivate メンバーコピー

    public PartialSMTPTransport(Session arg0, URLName arg1) {
		super(arg0, arg1);
	}

	public void sendMessage(Message msg, Address[] addrs) throws MessagingException, SendFailedException {
		
        int maximumSize = getMaximumSize();
        if(maximumSize <= 0) {
            log.debug("分割せずに送信します。");
            super.sendMessage(msg, addrs);
            return;
        }
		MimeMessage mMsg = (MimeMessage)msg;
		try {
			File f = File.createTempFile("sendLog", ".eml");
			try {
				f.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(f);
				try {
					mMsg.writeTo(fos, ignoreList);
				} finally {
					fos.close();
				}
				int size = (int)f.length();
				if(size <= maximumSize) {
					log.debug("分割せずに送信します。");
					super.sendMessage(msg, addrs);
				} else {
					log.debug("分割します。");
					String messageId = msg.getHeader("Message-ID")[0];
					if(messageId.indexOf("<") != -1) {
						messageId = messageId.replaceAll("<", "");
						messageId = messageId.replaceAll(">", "");
					}
					int partSize = (size / maximumSize);
					if(size % maximumSize != 0) {
						partSize++;
					}
					byte[] array = new byte[maximumSize];
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f), 512);
					try {
						int readable = bis.read(array);
						int index = 1;
						String subject = msg.getSubject();
						String references = null;
						while(readable > 0) {
							String messageIdTmp = MessageFormat.format(MESSAGE_ID_PATTERN, new Object[]{"" + index, messageId});
							msg.setHeader("Message-ID", messageIdTmp);
							if(references != null) {
								msg.setHeader("References", references);
							}
							references = messageIdTmp;
							String subjectTmp = MessageFormat.format(SUBJECT_PATTERN, new Object[]{subject, "" + index, "" + partSize});
							msg.setSubject(subjectTmp);
							String body = new String(array, 0, readable);
							msg.setText(body);
							String contentType = MessageFormat.format(CONTENT_TYPE_PATTERN, new Object[]{"" + index, "" + partSize, messageId});
							msg.setHeader("Content-Type", contentType);
							super.sendMessage(msg, addrs);
							readable = bis.read(array);
							index++;
						}
					} finally {
						bis.close();
					}
				}
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
				throw new MessagingException(e.getMessage());
			} finally {
				boolean b = f.delete();
				if(b){}						//Find Bugs回避戻り値を捨てる
			}
		} catch(IOException e) {
			log.warn(e.getMessage(), e);
			throw new MessagingException(e.getMessage());
		}
	}

	public int getMaximumSize() {
		String sizeStr = session.getProperty("mail.maximumsize");
		try {
			return Integer.parseInt(sizeStr);
		} catch(Exception ex) {
			return Integer.MAX_VALUE;
		}
	}
	
	private final static String CONTENT_TYPE_PATTERN = "message/partial; number={0}; total={1}; id=\"<{2}>\"";
	private final static String MESSAGE_ID_PATTERN = "<{0}.{1}>";
	private final static String SUBJECT_PATTERN = "{0}[{1}/{2}]";
}

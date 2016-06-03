/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import jp.rough_diamond.commons.util.DateManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ÉÅÅ[ÉãSender 
 */
public class MailSender {
    public final static Log log = LogFactory.getLog(MailSender.class); 

    //DIóp
    private Properties prop;
    public void setProperties(Properties prop) {
        this.prop = prop;
    }
    
    protected Session getSession() {
        return Session.getInstance(prop);
    }

    public void send(MailProperty prop) throws MessagingException {
        send(new MailProperty[]{prop});
    }
    
    public void send(MailProperty... props) throws MessagingException {
        Session session = getSession();
        MimeMessage[] msgs = new MimeMessage[props.length];
        for(int i = 0 ; i < props.length ; i++) {
            MailProperty prop = props[i];
            MimeMessage msg = new MimeMessage(session);
            if(prop.from == null) {
                msg.setFrom();
            } else {
                msg.setFrom(parseAddress(prop.from)[0]);
            }
            InternetAddress[] recpeientTo = parseAddress(prop.to);
            if(recpeientTo != null) {
                msg.addRecipients(MimeMessage.RecipientType.TO, recpeientTo);
            }
            recpeientTo = parseAddress(prop.cc);
            if(recpeientTo != null) {
                msg.addRecipients(MimeMessage.RecipientType.CC, recpeientTo);
            }
            recpeientTo = parseAddress(prop.bcc);
            if(recpeientTo != null) {
                msg.addRecipients(MimeMessage.RecipientType.BCC, recpeientTo);
            }
            if(prop.headers != null) {
                for(Map.Entry<String, String> entry : prop.headers.entrySet()) {
                    msg.addHeader(entry.getKey(), entry.getValue());
                }
            }
            String subject = translateString(prop.subject);
            String body = translateString(prop.body);
            msg.setSubject(subject, "ISO-2022-JP");
            if(prop.parts != null && prop.parts.size() != 0) {
                Multipart multipart = new MimeMultipart();
                if(body != null && body.length() != 0) {
                    MimeBodyPart bodyPart = new MimeBodyPart();
                    bodyPart.setText(body, "ISO-2022-JP");
                    multipart.addBodyPart(bodyPart);
                }
                for(BodyPart part : prop.parts) {
                    multipart.addBodyPart(part);
                }
                msg.setContent(multipart);
            } else {
                msg.setText(body, "ISO-2022-JP");
            }
            msgs[i] = msg;
        }
        send(msgs);
    }
    
    public void send(String subject, String body, String to,
                    String cc, String bcc, BodyPart... parts)
                                            throws MessagingException {
        MailProperty prop = new MailProperty();
        prop.subject = subject;
        prop.body = body;
        prop.to = to;
        prop.cc = cc;
        prop.bcc = bcc;
        prop.parts.addAll(Arrays.asList(parts));
        prop.from = null;
        send(prop);
    }
    
    public void send(String subject, String body, 
                    String to, String cc, String bcc)
                                            throws MessagingException {
        send(subject, body, to, cc, bcc, new BodyPart[0]);
    }
    
    public void send(String subject, String body, String to, String cc)
                                            throws MessagingException {
        send(subject, body, to, cc, null);
    }

    public void send(String subject, String body, String to)
                                            throws MessagingException {
        send(subject, body, to, null, null);
    }
    
    public void send(Message msg) throws MessagingException {
        send(new Message[]{msg});
    }
    
    public void send(Message[] msgs) throws MessagingException {
        Session session = getSession();
        String protocol = session.getProperty("mail.transport.protocol");
        if(protocol == null) {
        	protocol = "smtp";
        }
        Transport t = session.getTransport(protocol);
        t.connect();
        try {
            for(int i = 0 ; i < msgs.length ; i++) {
                Message msg = msgs[i];
                msg.setSentDate(DateManager.DM.newDate());
                try {
                    t.sendMessage(msg, msg.getAllRecipients());
                } catch(SendFailedException sfe) {
                    log.error(msg.toString());
                    log.error(sfe.getMessage(), sfe);
                }
            }
        } finally {
            t.close();
        }
    }

    public static void send(
                Session session, String subject, String body, String to) 
                                                throws MessagingException {
        MailSender sender = new SessionRedirector(session);
        sender.send(subject, body, to);
    }
    
    public static void send(Session session, MimeMessage msg) 
                                                throws MessagingException {
        MailSender sender = new SessionRedirector(session);
        sender.send(msg);
    }

    private static class SessionRedirector extends MailSender {
        private Session session;
        
        private SessionRedirector(Session session) {
            this.session = session;
        }
        
        protected Session getSession() {
            return session;
        }
    }

    private InternetAddress[] parseAddress(String address) throws MessagingException {
        if(address == null) { return null; }
        InternetAddress[] recpeientTo = InternetAddress.parse(address, true);
        for(int i = 0 ; i < recpeientTo.length ; i++) {
            InternetAddress ia = recpeientTo[i];
            String personal = ia.getPersonal();
            if(personal != null) {
                try {
                    ia.setPersonal(
                        MimeUtility.encodeText(personal, "ISO-2022-JP", "B"));
                } catch(java.io.UnsupportedEncodingException uee) {
                    throw new RuntimeException(uee.getMessage());
                }
            }
        }
        return recpeientTo;
    }
    
    public static class MailProperty {
        public String               subject;
        public String               body;
        public String               to;
        public String               cc;
        public String               bcc;
        public String               from;
        public List<BodyPart>       parts = new ArrayList<BodyPart>();
        public Map<String, String>  headers = new HashMap<String, String>(); 
    }

    private String translateString(String src) {
        try {
            sun.misc.HexDumpEncoder encoder = new sun.misc.HexDumpEncoder();
            if(log.isTraceEnabled()) {
            	log.trace(encoder.encodeBuffer(src.getBytes("iso-2022-jp")));
            }
            String ret = src.replace((char)0xFF0D, (char)0x2212);
            ret = ret.replace((char)0xFF5E, (char)0x301C);
            if(log.isTraceEnabled()) {
            	log.trace(encoder.encodeBuffer(ret.getBytes("iso-2022-jp")));
            }
            return ret;
        } catch(java.io.UnsupportedEncodingException uee) {
        	log.warn(uee.getMessage(), uee);
            throw new RuntimeException(uee.getMessage());
        }
    }
    
    public static void main(String argv[]) throws Throwable {
        MailSender sender = new MailSender() {
            protected Session getSession() {
                Properties p = new Properties();
                p.put("mail.from", "e-yamane@kcc.zaq.ne.jp");
                p.put("mail.smtp.host", "mail.kcc.zaq.ne.jp");
                return Session.getInstance(p, null);
            }
        };
        sender.send("ÇŸÇ∞", "Ç€Ç∞", "\"éRç™ âpéü\" <e-yamane@kcc.zaq.ne.jp>, \"Eiji Yamane\" <fb9e-ymn@asahi-net.or.jp>");
//        sender.send("ÇŸÇ∞", "Ç€Ç∞", 
//          new String[]{"e-yamane@kcc.zaq.ne.jp"}, new String[]{"éRç™ âpéü"});
/*
        Properties p = new Properties();
        p.put("mail.from", "e-yamane@osaka.apricotinc.jp");
        p.put("mail.smtp.host", "mail.osaka.apricotinc.jp");
        MailSender.send(
            Session.getInstance(p, null),
                "ÇŸÇ∞", "Ç€Ç∞", "e-yamane@osaka.apricotinc.jp");
*/
    }
}
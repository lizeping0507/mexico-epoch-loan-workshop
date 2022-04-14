package com.epoch.loan.workshop.common.util;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @Package com.yinda.platform.util.email
 * @Description: 集成github上邮件工具类
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/10/9 16:30
 */
public class OhMyEmailUtils {

    private static Session session;
    private static String user;
    private MimeMessage msg;
    private String text;
    private String html;
    private List<MimeBodyPart> attachments = new ArrayList();

    private OhMyEmailUtils() {
    }

    public static Properties defaultConfig(Boolean debug) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", null != debug ? debug.toString() : "false");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.port", "465");
        return props;
    }

    public static Properties SMTP_ENT_QQ(boolean debug) {
        Properties props = defaultConfig(debug);
        props.put("mail.smtp.host", "smtp.exmail.qq.com");
        return props;
    }

    public static Properties SMTP_QQ(boolean debug) {
        Properties props = defaultConfig(debug);
        props.put("mail.smtp.host", "smtp.qq.com");
        return props;
    }

    public static Properties SMTP_163(Boolean debug) {
        Properties props = defaultConfig(debug);
        props.put("mail.smtp.host", "smtp.163.com");
        return props;
    }

    public static void config(Properties props, String username, String password) {
        props.setProperty("username", username);
        props.setProperty("password", password);
        config(props);
    }

    public static void config(Properties props) {
        final String username = props.getProperty("username");
        final String password = props.getProperty("password");
        user = username;
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static OhMyEmailUtils subject(String subject) throws Exception {
        OhMyEmailUtils ohMyEmail = new OhMyEmailUtils();
        ohMyEmail.msg = new MimeMessage(session);

        try {
            ohMyEmail.msg.setSubject(subject, "UTF-8");
            return ohMyEmail;
        } catch (Exception var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils from(String nickName) throws Exception {
        return this.from(nickName, user);
    }

    public OhMyEmailUtils from(String nickName, String from) throws Exception {
        try {
            String encodeNickName = MimeUtility.encodeText(nickName);
            this.msg.setFrom(new InternetAddress(encodeNickName + " <" + from + ">"));
            return this;
        } catch (Exception var4) {
            throw new Exception(var4);
        }
    }

    public OhMyEmailUtils replyTo(String... replyTo) throws Exception {
        String result = Arrays.asList(replyTo).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");

        try {
            this.msg.setReplyTo(InternetAddress.parse(result));
            return this;
        } catch (Exception var4) {
            throw new Exception(var4);
        }
    }

    public OhMyEmailUtils replyTo(String replyTo) throws Exception {
        try {
            this.msg.setReplyTo(InternetAddress.parse(replyTo.replace(";", ",")));
            return this;
        } catch (Exception var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils to(String... to) throws Exception {
        try {
            return this.addRecipients(to, Message.RecipientType.TO);
        } catch (MessagingException var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils to(String to) throws Exception {
        try {
            return this.addRecipient(to, Message.RecipientType.TO);
        } catch (MessagingException var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils to(List<String> emailList) throws Exception {
        try {
            String[] to = ArrayUtil.toArray(emailList, String.class);
            return this.addRecipients(to, Message.RecipientType.TO);
        } catch (MessagingException var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils cc(String... cc) throws Exception {
        try {
            return this.addRecipients(cc, Message.RecipientType.CC);
        } catch (MessagingException var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils cc(String cc) throws Exception {
        try {
            return this.addRecipient(cc, Message.RecipientType.CC);
        } catch (MessagingException var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils bcc(String... bcc) throws Exception {
        try {
            return this.addRecipients(bcc, Message.RecipientType.BCC);
        } catch (MessagingException var3) {
            throw new Exception(var3);
        }
    }

    public OhMyEmailUtils bcc(String bcc) throws MessagingException {
        return this.addRecipient(bcc, Message.RecipientType.BCC);
    }

    private OhMyEmailUtils addRecipients(String[] recipients, Message.RecipientType type) throws MessagingException {
        String result = Arrays.asList(recipients).toString().replace("[", "").replace("]", "").replace(", ", ",");
        this.msg.setRecipients(type, InternetAddress.parse(result));
        return this;
    }

    private OhMyEmailUtils addRecipient(String recipient, Message.RecipientType type) throws MessagingException {
        this.msg.setRecipients(type, InternetAddress.parse(recipient.replace(";", ",")));
        return this;
    }

    public OhMyEmailUtils text(String text) {
        this.text = text;
        return this;
    }

    public OhMyEmailUtils html(String html) {
        this.html = html;
        return this;
    }

    public OhMyEmailUtils attach(File file) throws Exception {
        this.attachments.add(this.createAttachment(file, (String) null));
        return this;
    }

    public OhMyEmailUtils attach(List<File> fileList) throws Exception {
        for (File file : fileList) {
            this.attachments.add(this.createAttachment(file, (String) null));
        }
        return this;
    }

    public OhMyEmailUtils attach(File file, String fileName) throws Exception {
        this.attachments.add(this.createAttachment(file, fileName));
        return this;
    }

    public OhMyEmailUtils attach(byte[] bytes, String fileName) throws Exception {
        this.attachments.add(this.createAttachment(bytes, fileName));
        return this;
    }

    public OhMyEmailUtils attachURL(URL url, String fileName) throws Exception {
        this.attachments.add(this.createURLAttachment(url, fileName));
        return this;
    }

    private MimeBodyPart createAttachment(File file, String fileName) throws Exception {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(file);

        try {
            attachmentPart.setDataHandler(new DataHandler(fds));
            attachmentPart.setFileName(null == fileName ? MimeUtility.encodeText(fds.getName()) : MimeUtility.encodeText(fileName));
            return attachmentPart;
        } catch (Exception var6) {
            throw new Exception(var6);
        }
    }


    private MimeBodyPart createAttachment(byte[] bytes, String fileName) throws Exception {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        try {
            attachmentPart.setDataHandler(new DataHandler(bytes, "application/octet-stream"));
            attachmentPart.setFileName(fileName);
            return attachmentPart;
        } catch (Exception var6) {
            throw new Exception(var6);
        }
    }

    private MimeBodyPart createURLAttachment(URL url, String fileName) throws Exception {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataHandler dataHandler = new DataHandler(url);

        try {
            attachmentPart.setDataHandler(dataHandler);
            attachmentPart.setFileName(null == fileName ? MimeUtility.encodeText(fileName) : MimeUtility.encodeText(fileName));
            return attachmentPart;
        } catch (Exception var6) {
            throw new Exception(var6);
        }
    }

    public void send() throws Exception {
        if (this.text == null && this.html == null) {
            throw new IllegalArgumentException("At least one context has to be provided: Text or Html");
        } else {
            boolean usingAlternative = false;
            boolean hasAttachments = this.attachments.size() > 0;

            try {
                MimeMultipart cover;
                if (this.text != null && this.html == null) {
                    cover = new MimeMultipart("mixed");
                    cover.addBodyPart(this.textPart());
                } else if (this.text == null && this.html != null) {
                    cover = new MimeMultipart("mixed");
                    cover.addBodyPart(this.htmlPart());
                } else {
                    cover = new MimeMultipart("alternative");
                    cover.addBodyPart(this.textPart());
                    cover.addBodyPart(this.htmlPart());
                    usingAlternative = true;
                }

                MimeMultipart content = cover;
                if (usingAlternative && hasAttachments) {
                    content = new MimeMultipart("mixed");
                    content.addBodyPart(this.toBodyPart(cover));
                }

                Iterator var5 = this.attachments.iterator();

                while (var5.hasNext()) {
                    MimeBodyPart attachment = (MimeBodyPart) var5.next();
                    content.addBodyPart(attachment);
                }

                this.msg.setContent(content);
                this.msg.setSentDate(new Date());
                Transport.send(this.msg);
            } catch (Exception var7) {
                throw new Exception(var7);
            }
        }
    }

    private MimeBodyPart toBodyPart(MimeMultipart cover) throws MessagingException {
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(cover);
        return wrap;
    }

    private MimeBodyPart textPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(this.text);
        return bodyPart;
    }

    private MimeBodyPart htmlPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(this.html, "text/html; charset=utf-8");
        return bodyPart;
    }


}

/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * 画像を取り扱うためのユーティリティ
 */
public class ImageContentUtils {
    /**
     * 指定されたコンテントタイプが画像の場合はtrueを返却する
     * @param contentType
     * @return
     */
    public static boolean isImageContentType(String contentType) {
        return EXTENSION_MAP.containsKey(contentType);
    }

    /**
     * 指定された拡張子が画像の場合はtrueを返却する
     * @param mimeType
     * @return
     */
    public static boolean isImageExtention(String mimeType) {
        return CONTENT_TYPE_MAP.containsKey(mimeType.toLowerCase());
    }

    /**
     * 指定されたファイル名が画像のファイル名として妥当な場合はtrueを返却する
     * @param name
     * @return
     */
    public static boolean isImageFileName(String name) {
        String ext = getExtention(name);
        return isImageExtention(ext);
    }

    /**
     * 拡張子に対応したMimeTypeを返却する
     * @param ext
     * @return
     */
    public static String getMimeTypeByExtention(String ext) {
    	return CONTENT_TYPE_MAP.get(ext);
    }
    
    private static String getExtention(String name) {
        if(name.lastIndexOf(".") == -1) {
            return "";
        }
        String ext = name.substring(name.lastIndexOf(".") + 1);
        return ext.toLowerCase();
    }

    /**
     * サムネイル作成
     * @param dim
     * @param content
     * @return
     * @throws IOException
     */
    public static BufferedImage makeThubnail(Dimension dim, Content content) throws IOException {
        if(content.isImage()) {
            return makeThubnail(dim, content.content, content.contentType);
        } else {
            throw new RuntimeException("画像じゃないよ");
        }
    }

    /**
     * サムネイル作成
     * @param dim
     * @param is
     * @param contentType
     * @return
     * @throws IOException
     */
    public static BufferedImage makeThubnail(Dimension dim, InputStream is, String contentType) throws IOException {
        BufferedImage bi = ImageIO.read(is);
        is.close();
        return makeThubnail(dim, bi, contentType);
    }

    /**
     * サムネイル作成
     * @param dim
     * @param bi
     * @param contentType
     * @return
     */
    public static BufferedImage makeThubnail(Dimension dim, BufferedImage bi, String contentType) {
        double wRatio = dim.getWidth() / bi.getWidth();
        double hRatio = dim.getHeight() / bi.getHeight();
        double ratio = Math.min(wRatio, hRatio);
        if(ratio >= 1.0D) {
            ratio = 1.0D;
        }
        String formatName = IMAGE_CONVERT_EXTENTION.get(contentType);
        int compX = (int)(bi.getWidth() * ratio);
        int compY = (int)(int)(bi.getHeight() * ratio);
        BufferedImage compImage = new BufferedImage(
                compX, compY, IMAGE_TYPE_BY_EXTENTION.get(formatName));
        Graphics2D g2d = compImage.createGraphics();
        g2d.drawImage(bi, 0, 0, compX, compY, null);
        return compImage;
    }

    /**
     * サムネイルを保存する保存後。サムネイルを作成した場合にコンテントタイプが
     * 変化する場合があるため、変更後のコンテントタイプを返却する
     * @param f
     * @param image
     * @param contentType
     * @throws IOException
     */
    public static String storeThubnail(File f, BufferedImage image, String contentType) throws IOException {
        String formatName = IMAGE_CONVERT_EXTENTION.get(contentType);
        String storeContentType = CONTENT_TYPE_MAP.get(formatName);
        ImageIO.write(image, formatName, f);
        return storeContentType;
    }

    /**
     * コンテントタイプから拡張子を決定する
     */
    public final static Map<String, String> EXTENSION_MAP;

    /**
     * 拡張子からコンテントタイプを決定する
     */
    public final static Map<String, String> CONTENT_TYPE_MAP;

    /**
     * 画像変換時の変換後のフォーマット名（see ImageI/O)
     */
    public final static Map<String, String> IMAGE_CONVERT_EXTENTION;

    /**
     * 画像のコンテントタイプに対応するタイプ
     */
    public final static Map<String, Integer> IMAGE_TYPE_BY_EXTENTION;

    static {
        Map<String, String> tmp = new HashMap<String, String>();
        tmp.put("image/gif",    "gif");
        tmp.put("image/jpeg",   "jpg");
        tmp.put("image/pjpeg",  "jpg");
        tmp.put("image/png",    "png");
        EXTENSION_MAP = Collections.unmodifiableMap(tmp);

        tmp = new HashMap<String, String>();
        tmp.put("gif",  "image/gif");
        tmp.put("jpg",  "image/jpeg");
        tmp.put("jpeg", "image/jpeg");
        tmp.put("png",  "image/png");
        CONTENT_TYPE_MAP = Collections.unmodifiableMap(tmp);

        tmp = new HashMap<String, String>();
        tmp.put("image/gif",    "png");
        tmp.put("image/jpeg",   "jpg");
        tmp.put("image/pjpeg",  "jpg");
        tmp.put("image/png",    "png");
        IMAGE_CONVERT_EXTENTION = Collections.unmodifiableMap(tmp);

        Map<String, Integer> tmp2 = new HashMap<String, Integer>();
        tmp2.put("jpg", BufferedImage.TYPE_3BYTE_BGR);
        tmp2.put("png", BufferedImage.TYPE_INT_RGB);
        IMAGE_TYPE_BY_EXTENTION = Collections.unmodifiableMap(tmp2);
    }
}

package com.epoch.loan.workshop.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

/**
 * @author 魏玉强
 * @packagename : com.epoch.loan.workshop.common.util
 * @className : ImageUtil
 * @createTime : 2022/06/17 12:05
 * @Description:
 */
public class ImageUtil {
    /**
     * 将图片压缩到指定大小以内
     *
     * @param byteFile 源图片二进制
     * @param maxSize 目的图片大小
     * @param zipFile 压缩后的图片
     * @param format 图片格式
     * @return
     * @author CY
     * @date 2020年11月18日
     */
    public static void compressUnderSize(byte[] byteFile, long maxSize, File zipFile, String format) throws IOException {

        byte[] data = getByteByPic(byteFile,format);
        byte[] imgData = Arrays.copyOf(data, data.length);

        // 根据大小判断是否进行压缩
        while (imgData.length > maxSize) {
            try {
                imgData = compress(imgData, 0.9, format);
            } catch (IOException e) {
                throw new IllegalStateException("压缩图片过程中出错,请及时联系管理员!", e);
            }
        }

        // 根据宽高判断是否进行压缩
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imgData));
        // 图宽度
        int width = bi.getWidth();
        // 图高度
        int height = bi.getHeight();
        if (width > 4096 || height > 4096 || width < 256 || height < 256) {
            imgData = compress(imgData, 0.9, format);
        }

        byteToImage(imgData, zipFile);
    }

    /**
     * 获取图片文件字节
     *
     * @param byteFile
     * @param format
     * @return
     * @throws IOException
     */
    public static byte[] getByteByPic(byte[] byteFile,String format) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteFile);
        BufferedImage bm = ImageIO.read(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bm, format, bos);
        bos.flush();
        byte[] data = bos.toByteArray();
        return data;
    }

    /**
     * 按照宽高比例压缩
     *
     * @param imgData 待压缩图片输入流
     * @param scale 压缩刻度
     * @param format 图片格式
     * @return
     * @throws IOException
     */
    public static byte[] compress(byte[] imgData, double scale ,String format) throws IOException {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imgData));
        // 源图宽度
        int width = (int) (bi.getWidth() * scale);
        // 源图高度
        int height = (int) (bi.getHeight() * scale);

        int count = 0;
        while ((width < 256 || height < 256) && count < 5) {
            width = (int) (width * 1.2);
            height = (int) (height * 1.2);
            count ++;
        }

        LogUtil.sysInfo("图片压缩前： 宽度--{} ， 高度--{}",width ,height);
        while (width > 4096 || height > 4096) {
            width = (int) (width * scale);
            height = (int) (height * scale);
        }

        LogUtil.sysInfo("图片压缩后： 宽度--{} ， 高度--{}",width ,height);
        Image image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.setColor(Color.RED);
        // 绘制处理后的图
        g.drawImage(image, 0, 0, null);
        g.dispose();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ImageIO.write(tag, format.toUpperCase(), bOut);
        return bOut.toByteArray();
    }

    /**
     * byte数组转图片
     *
     * @param data 图片二进制数据
     * @param zipFile 压缩后的图片
     */
    public static void byteToImage(byte[] data, File zipFile) {
        if (data.length < 3) {
            return;
        }
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(zipFile);
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package com.xiaoxin.experience.util;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiaoxin
 */
public class ResizeImgBase64Util {

        private ResizeImgBase64Util(){}

        private static final long KIL_BYTES = 1024;

        public static String resizeImageStreamByLimit(InputStream in, Long limitSize)
        {
            return resizeImageByLimit(Base64ChangeUtil.readInputStreamToBase64(in),limitSize);
        }

        /**
         * 调整图片大小到指定大小
         * @param imgBase64 图片base64编码
         * @param limitSize 限制大小 (单位:kb)
         * @return 符合限定大小
         */
        public static String resizeImageByLimit(String imgBase64, Long limitSize)
        {
            Assert.hasText(imgBase64,"img base64 can not be null");
            Assert.isTrue(limitSize > 0, "limit size is valid");
            limitSize = limitSize * KIL_BYTES;
            long imageSize = imageSize(imgBase64);
            while (imageSize > limitSize){
                imgBase64 = resizeImage(imgBase64);
                imageSize = imageSize(imgBase64);
            }
            return imgBase64;
        }

        /**
         * 重置图片大小
         * @param base64Img 图片base64编码
         * @return 图片压缩后的base64编码
         */
        public static String resizeImage(String base64Img) {
            try {
                BufferedImage src = base64String2BufferedImage(base64Img);
                BufferedImage output = Thumbnails.of(src).size(src.getWidth(), src.getHeight()).asBufferedImage();
                output = Thumbnails.of(output).scale(0.8).asBufferedImage();
                return imageToBase64(output);
            } catch (Exception e) {
                throw new RuntimeException("Compressed picture fail");
            }
        }

        public static BufferedImage base64String2BufferedImage(String base64string) {
            BufferedImage image = null;
            try {
                InputStream stream = base64ToInputStream(base64string);
                image = ImageIO.read(stream);
            } catch (IOException e) {
                throw new RuntimeException("base64 to picture fail");
            }
            return image;
        }

        private static InputStream base64ToInputStream(String base64string) {
            ByteArrayInputStream stream = null;
            try {
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] bytes1 = decoder.decodeBuffer(base64string);
                stream = new ByteArrayInputStream(bytes1);
            } catch (Exception e) {
                throw new RuntimeException("base64 string loading fail");
            }
            return stream;
        }

        /**
         * bufferedImage对象转成base64字符串
         * @param bufferedImage 图片对象
         * @return base64字符串
         */
        public static String imageToBase64(BufferedImage bufferedImage) {
            Base64 encoder = new Base64();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage, "jpg", baos);
            } catch (IOException e) {
                throw new RuntimeException("Picture conversion fail");
            }
            return new String(encoder.encode((baos.toByteArray())));
        }

        /**
         *通过图片base64流判断图片等于多少字节
         *image 图片流
         */
        public static int imageSize(String image){
            // 1.需要计算文件流大小，首先把头部的data:image/png;base64,（注意有逗号）去掉。
            String str = image.substring(22);

            //2.找到等号，把等号也去掉
            int equalIndex = str.indexOf("=");
            if(str.indexOf("=")>0) {
                str=str.substring(0, equalIndex);
            }
            //3.原来的字符流大小，单位为字节
            int strLength = str.length();
            return strLength - (strLength/8) * 2;
        }
}

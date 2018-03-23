package com.bruce.utils;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liwenlong - 2018/1/22 14:10
 */
public class FileUtils {

    public static void fileCopyByNio(String resource, String destination) {
        try {
            try (FileInputStream fis = new FileInputStream(resource);
                 FileOutputStream fos = new FileOutputStream(destination)) {
                FileChannel readChannel = fis.getChannel();
                FileChannel writeChannel = fos.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int len = 0;
                while (len != -1) {
                    len = readChannel.read(byteBuffer);
                    byteBuffer.flip();
                    writeChannel.write(byteBuffer);
                    byteBuffer.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fileRead(String file){
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                FileChannel readChannel = fileInputStream.getChannel();
                MappedByteBuffer mappedBuffer = readChannel.map(FileChannel.MapMode.READ_ONLY, 0, readChannel.size());
                IntBuffer intBuffer = mappedBuffer.asIntBuffer();
                while (intBuffer.hasRemaining()){
                    intBuffer.get();
                }
                readChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将网络文件保存在指定的目录
     * @param urlStr  网路文件url
     * @param dir 文件保存目录
     */
    public static void saveToDir(String urlStr,String dir){
        try {
            String fileName = urlStr.substring(urlStr.lastIndexOf("/"));
            System.out.println(fileName);
            URL url = new URL(urlStr);
            InputStream inputStream = url.openStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            //bufferedInputStream.


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 读取字符串文本,list一个元素对应一行
     */
    public static List<String> readTextLines(String filePath) {
        File file = new File(filePath);
        InputStreamReader reader;
        BufferedReader bufferedReader;
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                reader = new InputStreamReader(fileInputStream);
                bufferedReader = new BufferedReader(reader);
                return bufferedReader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> readTextLines(InputStream inputStream){
        try{
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            return bufferedReader.lines().collect(Collectors.toList());
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readText(InputStream inputStream){
        List<String> lines = readTextLines(inputStream);
        StringBuilder builder = new StringBuilder();
        lines.forEach(builder::append);
        return builder.toString();
    }


    /**
     * 读取字符串文本
     */
    public static String readText(String filePath) {
        List<String> lines = readTextLines(filePath);
        if (lines == null)
            return null;
        StringBuilder builder = new StringBuilder();
        lines.forEach(builder::append);
        return builder.toString();
    }


}

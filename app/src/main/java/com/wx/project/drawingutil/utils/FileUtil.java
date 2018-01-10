package com.wx.project.drawingutil.utils;

import android.content.Context;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by WangXin on 2017/1/5.
 */

public class FileUtil {

    /**
     * 将bitmap保存到文件夹中，并给bitmap命名
     * @param packageName 文件夹名称
     * @param bitmapName  图片名称
     * @param bitmap
     * @return
     */
    public static String saveBitmapWithName(String packageName, String bitmapName, Bitmap bitmap) {
        String filePath = null;
        try {
            File rootFile = new File(SDCardPathManager.getInstance().getPath());
            if (!rootFile.exists()) {
                rootFile.getParentFile().mkdirs();
            }
            String saveDir = rootFile.getAbsolutePath() + "/" + packageName + "/";
            //创建文件夹
            makeRootDir(saveDir);
            filePath = saveDir + bitmapName;
            File file = new File(filePath);
            file.delete();
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println("保存:");
            FileOutputStream out = new FileOutputStream(file);
            //进行压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return filePath;
    }


    /**
     * 检测路径是否完整，不完整则新建
     * **/
    public static void makeRootDir(String filePath) {
        File file = null;
        String newPath = null;
        String[] path = filePath.split("/");
        for (int i = 0; i < path.length; i++) {
            if (newPath == null) {
                newPath = path[i];
            } else {
                newPath = newPath + "/" + path[i];
            }
            file = new File(newPath);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }


    /**
     * 将asset里的数据拷贝的sd卡里
     **/
    public static void copyBigDataToSD(String sdPath, String fileName, Context context) {
        try {
            //检测sdPath是否是完整路径，不是则新建
            makeRootDir(sdPath);
            InputStream myInput;
            OutputStream myOutput = new FileOutputStream(sdPath);
            myInput = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        }catch (Exception e){}
    }


    /**
     * 根据文件路径，得到文件数据
     * FileChannel 使用
     **/
    public static byte[] readFileData(String filename) {
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size()).load();
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

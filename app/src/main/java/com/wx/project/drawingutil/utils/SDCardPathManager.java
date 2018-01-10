package com.wx.project.drawingutil.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class SDCardPathManager {
	
	private static SDCardPathManager sdCardPathManager = null;
	public static SDCardPathManager getInstance(){
		if(sdCardPathManager == null)
			sdCardPathManager = new SDCardPathManager();
		return sdCardPathManager;
	}

	private String hongWaiPath = null;
	private String sdcardPath = null;
	private String cacheImagesPath = null;

    public String getSdcardPath(){
    	if(sdcardPath == null) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				sdcardPath = Environment.getExternalStorageDirectory().getPath();
			} else {
				sdcardPath= Environment.getDataDirectory().getPath();
			}
		}
    	return sdcardPath;
    }

    public String getPath(){
    	if(hongWaiPath == null)
			hongWaiPath = getSdcardPath() + "/DrawingUtil/";
    	return hongWaiPath;
    }
    
    public String getCacheImagesPath(){
    	if(cacheImagesPath == null) {
			cacheImagesPath = getPath() + "images/";
		}
    	return cacheImagesPath;
    }

	public float getAvailableSize(){
		long availableSize = 0;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			availableSize = getSDAvailableSize();
		} else {
			availableSize = getRomAvailableSize();
		}
		return availableSize/1024.0f/1024.0f;
	}

	/* 根据路径获取内存状态
  * @param path
  * @return
          */
	private static long getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		// 获得一个磁盘状态对象
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();   // 获得一个扇区的大小
		long totalBlocks = stat.getBlockCount();    // 获得扇区的总数
		long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量
		// 可用空间
		long availableMemory = availableBlocks * blockSize;
		return availableMemory;
	}


	/**
	 * 获得机身可用内存
	 *
	 * @return
	 */
	private long getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long availableMemory = availableBlocks * blockSize;
		return availableMemory;
	}

}

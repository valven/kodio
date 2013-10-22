package com.valven.kodio.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

public class ImageLoader {
	private com.nostra13.universalimageloader.core.ImageLoader loader = com.nostra13.universalimageloader.core.ImageLoader
			.getInstance();

	private static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(0).showImageForEmptyUri(0).resetViewBeforeLoading()
			.cacheInMemory().cacheOnDisc()
			.imageScaleType(ImageScaleType.POWER_OF_2).build();

	private static ImageLoader instance;

	private ImageLoader(Context context) {
		File cacheDir = context.getCacheDir();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPoolSize(3)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LRULimitedMemoryCache(1 * 1024 * 1024))
				// 1MB
				.discCache(
						new TotalSizeLimitedDiscCache(cacheDir,
								10 * 1024 * 1024))
				// 10MB
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.imageDownloader(new ImageDownloader() {
					@Override
					protected InputStream getStreamFromNetwork(URI arg0)
							throws IOException {
						try {
							URL imageUrl = arg0.toURL();
							URLConnection conn = (URLConnection) imageUrl
									.openConnection();
							conn.setConnectTimeout(5000);
							conn.setReadTimeout(20000);
							conn.setUseCaches(true);
							return conn.getInputStream();
						} catch (Exception ex) {
							ex.printStackTrace();
							return null;
						}
					}
				}).defaultDisplayImageOptions(options).build();
		loader.init(config);
	}

	public static void init(Context context) {
		if (instance == null) {
			instance = new ImageLoader(context);
		}
	}

	public static ImageLoader getInstance() {
		if (instance == null) {
			throw new IllegalStateException();
		}
		return instance;
	}

	public void displayImage(String url, ImageView imageView) {
		loader.displayImage(url, imageView);
	}
}

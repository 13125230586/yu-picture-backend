package com.yupi.yupicturebackend.api.imagesearch.baidu;

import com.yupi.yupicturebackend.api.imagesearch.baidu.model.ImageSearchResult;
import com.yupi.yupicturebackend.api.imagesearch.baidu.sub.GetImageFirstUrlApi;
import com.yupi.yupicturebackend.api.imagesearch.baidu.sub.GetImageListApi;
import com.yupi.yupicturebackend.api.imagesearch.baidu.sub.GetImagePageUrlApi;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    // 最大重试次数
    private static final int MAX_RETRIES = 5;
    // 支持的图片格式（按优先级排序）
    private static final String[] FORMATS = {"jpg", "png", "jpeg"};

    /**
     * 搜索图片（带重试机制和格式转换）
     *
     * @param originalUrl 原始图片 URL
     * @return 图片搜索结果列表
     */
    public static List<ImageSearchResult> searchImage(String originalUrl) {
        List<ImageSearchResult> result = null;

        // 1. 尝试不同的图片格式
        for (String format : FORMATS) {
            String convertedUrl = convertImageFormat(originalUrl, format);
            log.info("尝试使用格式: {}, URL: {}", format, convertedUrl);
            result = tryWithRetries(convertedUrl);
            if (isValidResult(result)) {
                log.info("成功找到结果，使用格式: {}", format);
                return result;
            }
        }

        // 2. 全部失败后抛出异常
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片搜索失败，已尝试所有格式且重试 " + MAX_RETRIES + " 次");
    }

    /**
     * 对指定 URL 进行多次重试（最多 MAX_RETRIES 次）
     *
     * @param url 图片 URL
     * @return 图片搜索结果列表，失败返回 null
     */
    private static List<ImageSearchResult> tryWithRetries(String url) {
        int retryCount = 0;
        List<ImageSearchResult> result = null;

        while (retryCount < MAX_RETRIES) {
            try {
                result = doSearchImage(url);
                if (isValidResult(result)) {
                    log.info("第 {} 次尝试成功 (URL: {})", retryCount + 1, url);
                    return result; // 成功则立即返回
                }
            } catch (Exception e) {
                log.warn("第 {} 次尝试失败 (URL: {}): {}", retryCount + 1, url, e.getMessage());
            }

            retryCount++;
        }

        return null; // 全部失败返回 null
    }

    /**
     * 执行图片搜索（原始逻辑）
     *
     * @param imageUrl 图片 URL
     * @return 图片搜索结果列表
     */
    private static List<ImageSearchResult> doSearchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }

    /**
     * 转换图片格式（替换后缀）
     *
     * @param originalUrl 原始 URL
     * @param newFormat 新格式（如 jpg, png, jpeg）
     * @return 转换后的 URL
     */
    private static String convertImageFormat(String originalUrl, String newFormat) {
        int lastDotIndex = originalUrl.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return originalUrl + "." + newFormat;
        }
        return originalUrl.substring(0, lastDotIndex) + "." + newFormat;
    }

    /**
     * 检查结果是否有效
     *
     * @param resultList 结果列表
     * @return 是否有效
     */
    private static boolean isValidResult(List<ImageSearchResult> resultList) {
        return resultList != null && !resultList.isEmpty();
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}

package com.yupi.yupicturebackend.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cos.client") //指定前缀
@Data
public class CosClientConfig {  
  
    /**  
     * 域名  
     */  
    private String host;  
  
    /**  
     * secretId  
     */  
    private String secretId;  
  
    /**  
     * 密钥（注意不要泄露）  
     */  
    private String secretKey;  
  
    /**  
     * 区域  
     */  
    private String region;  
  
    /**  
     * 桶名  
     */  
    private String bucket;

    /*
    https://cloud.tencent.com/document/product/436/10199#.E5.88.9D.E5.A7.8B.E5.8C.96.E5.AE.A2.E6.88.B7.E7.AB.AF
    官方文档说明：
    // 1 初始化用户身份信息（secretId, secretKey）。
    // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
    String secretId = System.getenv("secretId");//用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
    String secretKey = System.getenv("secretKey");//用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
    COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
    // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
    // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
    Region region = new Region("COS_REGION");
    ClientConfig clientConfig = new ClientConfig(region);
    // 这里建议设置使用 https 协议
    // 从 5.6.54 版本开始，默认使用了 https
    clientConfig.setHttpProtocol(HttpProtocol.https);
    // 3 生成 cos 客户端。
    COSClient cosClient = new COSClient(cred, clientConfig);
    *
    */

    //这里注意依赖导入对象存储的： com.qcloud
    //将 COSClient 实例注册到 Spring 容器中供其他组件使用
    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息(secretId, secretKey)  
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224  
        ClientConfig clientConfig = new ClientConfig(new Region(region)); //region = COS_REGION
        // 生成cos客户端  
        return new COSClient(cred, clientConfig);  
    }  
}

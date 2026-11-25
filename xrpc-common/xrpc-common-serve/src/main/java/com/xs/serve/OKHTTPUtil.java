package com.xs.serve;


import com.xs.exception.DefineException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
//import okhttp3.*;

/**
 * Http通讯工具类
 */
public class OKHTTPUtil {


    private static final Logger log = LoggerFactory.getLogger(OKHTTPUtil.class);

        public static String post(String url, Map<String, String> params, Map<String, String> headers) throws DefineException {
            try {
                OkHttpClient client = new OkHttpClient();
                client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                // 1.参数头设置
                Request.Builder headBuilder = new Request.Builder().head();
                if (headers != null && !headers.isEmpty()) {
                    headers.entrySet().forEach(entry -> {
                        headBuilder.addHeader(entry.getKey(), entry.getValue());
                    });
                }
                // 2.参数体设置
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                if (params != null && !params.isEmpty()) {
                    params.entrySet().forEach(entry -> {
                        bodyBuilder.add(entry.getKey(), entry.getValue());
                    });
                }
                // 3.请求url
                Request request = headBuilder
                        .url(url)
                        .post(bodyBuilder.build())
                        .build();
                // 4.响应信息处理
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    throw new DefineException("对方服务通讯异常" + response);
                }
                return response.body().string();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
                throw new DefineException("865","转换失败");
            }
        }

        public static String postFormStr(String url, String type, String params) throws DefineException {
            return OKHTTPUtil.postFormStr(url, type, "UFT-8", params);
        }

        public static String postFormStr(String url,String type, String codeType, String params) throws DefineException {
            try {
                OkHttpClient client = new OkHttpClient();
                client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                MediaType Type = MediaType.parse("application/" + type + ";charset="+ codeType);

                RequestBody body = RequestBody.create(Type, params);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new DefineException("对方服务通讯异常" + response);
                }
                String respBody = new String(response.body().bytes(), codeType);
                return respBody;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
                throw new DefineException("865","转换失败,请检查配置或上送格式");
            }
        }

        public static String postFormBody(String url,String type, HashMap<String, String> params) throws DefineException {
            try {
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                for (String key : params.keySet()) {
                    builder.add(key, params.get(key));
                }
                RequestBody formBody = builder.build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new DefineException("对方服务通讯异常" + response);
                }
                return response.body().string();
            }catch (Exception e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
                throw new DefineException("865","转换失败");
            }
        }

        public static String getData(String url) throws DefineException {
            try {
                OkHttpClient client = new OkHttpClient();
                client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.get().url(url).build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new DefineException("对方服务通讯异常" + response);
                }
                return response.body().string();
            }catch (Exception e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
                throw new DefineException("865","转换失败");
            }
        }

    }



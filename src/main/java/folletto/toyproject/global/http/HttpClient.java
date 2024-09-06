package folletto.toyproject.global.http;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import okhttp3.*;
import okhttp3.OkHttpClient;

public class HttpClient {

    private final OkHttpClient okHttpClient;

    public HttpClient() {
        this.okHttpClient = new OkHttpClient();
    }

    public Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return okHttpClient.newCall(request).execute();
    }

    public Response get(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + token) // Authorization 헤더 추가
                .build();
        return okHttpClient.newCall(request).execute();
    }

    public Response post(String url, Object object) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), toJson(object));
        Request request = new Request.Builder().url(url).post(body).build();
        return okHttpClient.newCall(request).execute();
    }

    public Response post(String url, String token, Object object) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), toJson(object));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + token)
                .build();
        return okHttpClient.newCall(request).execute();
    }

    public Response postForm(String url, Map<String, String> formParams) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : formParams.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody formBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        return okHttpClient.newCall(request).execute();
    }

    public Response postForm(String url, String token, Map<String, String> formParams) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : formParams.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody formBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .header("Authorization", "Bearer " + token)
                .build();

        return okHttpClient.newCall(request).execute();
    }

    private String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

}

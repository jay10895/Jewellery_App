package com.example.anuragjewellers.network;

import com.example.anuragjewellers.AppController;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class apiClient {

    private static Retrofit retrofit = null;
    public static String retrofit_id = "aHR0cDovL2xpY2Vuc2UudmlybWFuYS5jb20vYXBpLw==";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static String BASE_URL = "http://18.204.230.74/jewels/";
    public static Retrofit initClient(){
        String text = "";
        byte[] data = android.util.Base64.decode(apiClient.retrofit_id, android.util.Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(text)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    public static Retrofit getClient() {
        if (retrofit==null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor( provideHttpLoggingInterceptor() )
                    .addInterceptor( provideOfflineCacheInterceptor() )
                    .addNetworkInterceptor( provideCacheInterceptor() )
                    //.cache( provideCache() )
                    .connectTimeout(160, TimeUnit.SECONDS)
                    .readTimeout(160, TimeUnit.SECONDS)
                    .writeTimeout(160, TimeUnit.SECONDS)
                    .build();

            OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttpClient);
            Picasso picasso = new Picasso.Builder(AppController.getInstance())
                    .downloader(okHttp3Downloader)
                    .build();
            Picasso.setSingletonInstance(picasso);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getOrderClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                //  .cache(provideCache())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttpClient);
        Picasso picasso = new Picasso.Builder(AppController.getInstance())
                .downloader(okHttp3Downloader)
                .build();
        // Picasso.setSingletonInstance(picasso);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor ()
    {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor( new HttpLoggingInterceptor.Logger()
                {
                    @Override
                    public void log (String message)
                    {
                        // Timber.d( message );
                    }
                } );
        //httpLoggingInterceptor.setLevel( BuildConfig.DEBUG ? HEADERS : NONE );
        return httpLoggingInterceptor;
    }
    public static Interceptor provideOfflineCacheInterceptor ()
    {
        return new Interceptor()
        {
            @Override
            public Response intercept (Chain chain) throws IOException
            {
                Request request = chain.request();

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale( 30, TimeUnit.DAYS )
                        .build();
                request = request.newBuilder()
                        .cacheControl( cacheControl )
                        .build();

                return chain.proceed( request );
            }
        };
    }
    public static Interceptor provideCacheInterceptor ()
    {
        return new Interceptor()
        {
            @Override
            public Response intercept (Chain chain) throws IOException
            {
                Response response = chain.proceed( chain.request() );
                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge( 2, TimeUnit.SECONDS )
                        .build();
                return response.newBuilder()
                        .header( CACHE_CONTROL, cacheControl.toString() )
                        .build();
            }
        };
    }
    private static Cache provideCache ()
    {
        Cache cache = null;
        try
        {
            cache = new Cache( new File( AppController.getInstance().getCacheDir(), "wallpaper-cache" ),
                    10 * 1024 * 1024 ); // 10 MB
        }
        catch (Exception e)
        {
            // Timber.e( e, "Could not create Cache!" );
        }
        return cache;
    }
}

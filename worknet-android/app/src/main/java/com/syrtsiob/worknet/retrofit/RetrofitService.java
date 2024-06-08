package com.syrtsiob.worknet.retrofit;

import android.content.Context;


import com.syrtsiob.worknet.R;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    // Created custom Trust manager in order to bypass hostname not verified error.
    // common practice in Android dev - used only for dev purposes. SSL/TLS handshake still stands.
    // Retrofit is used to connect the android app with the backend endpoints in the Spring Boot app.
    public static Retrofit getRetrofitInstance(Context context) {
        try {
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // Load the certificate from res/raw
            InputStream caInput = context.getResources().openRawResource(R.raw.worknet);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing the trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in the KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Hostname verifier to avoid the error.
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                            return hostname.equals("10.0.2.2");
                        }
                    })
                    .build();


            // Build Retrofit instance
            return new Retrofit.Builder()
                    .baseUrl("https://10.0.2.2:8443/")
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

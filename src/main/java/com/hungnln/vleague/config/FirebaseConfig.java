package com.hungnln.vleague.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class FirebaseConfig {
    private final ResourceLoader resourceLoader;
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:serviceAccountKey.json");
        InputStream inputStream = resource.getInputStream();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build();
        if(FirebaseApp.getApps().isEmpty()){
            return FirebaseApp.initializeApp(options);
        }else{
            return FirebaseApp.getApps().get(0);
        }
    }
}

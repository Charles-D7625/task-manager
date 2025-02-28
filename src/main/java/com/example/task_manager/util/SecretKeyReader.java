package com.example.task_manager.util;

import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.exception.IllegalArgumentException;

import io.github.cdimascio.dotenv.Dotenv;

public class SecretKeyReader {
        
	private static final String SECRET_KEY;

    static {

        String tempSecretKey = System.getenv("SECRET_KEY");

        if (tempSecretKey == null) {

            Dotenv dotenv = Dotenv.load();
            tempSecretKey = dotenv.get("SECRET_KEY");
        }

        if(tempSecretKey == null) {

            throw new EntityNotFoundException("SECRET_KEY not found");
        }

        SECRET_KEY = tempSecretKey;
    }

    private SecretKeyReader() {

        throw new IllegalArgumentException("Utility class");
    }

    public static String getSecretKey() {

        return SECRET_KEY;
    }
}

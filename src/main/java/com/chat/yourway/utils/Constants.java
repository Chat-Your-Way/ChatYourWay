package com.chat.yourway.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class Constants {
    public static final Pattern PATTERN_SPACE = Pattern.compile(".*\\s+.*");
    public static final int MIN_EMAIL_LENGTH = 6;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9.\\-_]+@[A-Za-z]+\\.[A-Za-z]{2,3}");
    public static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9а-яА-ЯІіЇї!@#$%^&*_\\-+=~?]{4,20}$");
    public static final int MIN_PASSWORD_LENGTH = 4;
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final Pattern PASSWORD_SPECIAL_SYMBOLS_PATTERN = Pattern.compile(".*[!@#$%^&*_\\-+=~?].*");
    public static final Pattern PASSWORD_UPPER_CASE_PATTERN = Pattern.compile(".*[A-Z].*");
    public static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    public static final Pattern PASSWORD_FORBIDEN_SYMBOLS_PATTERN = Pattern.compile(".*[<>;/.:'(),\\[\\]\"].*");
    public static final Pattern PASSWORD_FORBIDEN_CYRILLIC_LETTERS_PATTERN = Pattern.compile(".*[а-яА-ЯІіЇї].*");
    public static final String[] WHITELIST_URLS = {
            //OpenApi
            "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui", "/configuration/security", "/swagger-ui/**", "/webjars/**",
            "/swagger-ui.html",

            //Authentication
            "/auth/**",

            //WebSocket
            "/ws/**", "/chat/**",

            //Other
            "/*",

            //Change password
            "/change/password/**"
    };
    public static final String SLASH = "/";
    public static final String UUID_REGEX_PATTERN =
            "\\b[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\\b";
    public static final String TOKEN_PARAMETER = "?token=";
}

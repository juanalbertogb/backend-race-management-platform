package com.maskilometros.backend.constants;

public class ApplicationConstants {

    private ApplicationConstants(){

        throw new AssertionError("Utility class cannot be instantiated");
    }
    //JwtUtil
    public static final String JWT_SECRET_KEY="JWT_SECRET";
    //AuthServiceImpl
    public static final String ROLE_USER="ROLE_USER";
    //JwtTokenValidatorFilter
    public static final String JWT_HEADER="Authorization";
    public static final String BEARER="Bearer ";
    //RegistrationServiceImpl
    public static final String FULL_REFUND_BEFORE_20_DAYS = "Refund by cancellation before than more of 20 days of the competition day";
    public static final String PARTIAL_REFUND_BEFORE_10_DAYS = "Refund by cancellation before of 10 days of the competition day";
    public static final String NO_REFUND_TOO_LATE = "Refund is not possible before of 10 days";
}

package com.ssm.util;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class SSMUtil {

    public static String randomPassword() {
        Random random = new Random();
        String generatePin = String.format("%04d", random.nextInt(10000));
        return "RP" + generatePin + "EG";
    }

    public static boolean isWithin7DaysOfOneYearAfterPaymentDate(LocalDate paymentDate) {
        if (paymentDate != null) {
            LocalDate oneYearAfterPaymentDate = paymentDate.plus(1, ChronoUnit.YEARS);
            LocalDate currentDate = LocalDate.now();

            // Check if the current date is within 7 days of one year after the payment date
            return currentDate.isAfter(oneYearAfterPaymentDate.minus(7, ChronoUnit.DAYS))
                    && currentDate.isBefore(oneYearAfterPaymentDate.plus(7, ChronoUnit.DAYS));
        }
        return false;
    }

    public static boolean isPastOneYearAfterPaymentDate(LocalDate paymentDate) {
        if (paymentDate != null) {
            LocalDate oneYearAfterPaymentDate = paymentDate.plus(1, ChronoUnit.YEARS);
            LocalDate currentDate = LocalDate.now();

            // Check if the current date is past one year after the payment date
            return currentDate.isAfter(oneYearAfterPaymentDate);
        }
        return false;
    }

    public static boolean isExactly1Year3DaysAfterPaymentDate(LocalDate paymentDate) {
        if (paymentDate != null) {
            LocalDate oneYearThreeDaysAfterPaymentDate = paymentDate.plus(1, ChronoUnit.YEARS).plus(3, ChronoUnit.DAYS);
            LocalDate currentDate = LocalDate.now();

            // Check if the current date is exactly 1 year and 3 days after the payment date
            return currentDate.isAfter(oneYearThreeDaysAfterPaymentDate);
        }
        return false;
    }

    public static long daysBefore7DaysOfOneYearAnniversary(LocalDate paymentDate) {
        if (paymentDate != null) {
            LocalDate oneYearAfterPaymentDate = paymentDate.plus(1, ChronoUnit.YEARS);
            LocalDate currentDate = LocalDate.now();
            return ChronoUnit.DAYS.between(currentDate, oneYearAfterPaymentDate);
        }
        return -1;
    }


}

package cirrb.com.cirrab.util;

import java.util.regex.Pattern;

/**
 * Created by yuvasoft on 28/11/16.
 */

public class EmailChecker {

        public static boolean checkEmail(String email) {

            Pattern EMAIL_ADDRESS_PATTERN = Pattern
                    .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                            + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                            + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
            System.out.println("mail"+email);
            return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
        }
    }


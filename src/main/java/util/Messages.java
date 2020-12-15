package util;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

public class Messages {

    private ReloadableResourceBundleMessageSource messageSource;

    private final Locale locale;

    public Messages(String userLanguage, String userCountry) {
        locale = new Locale(userLanguage, userCountry);
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
    }

    public String getMessage(String key) {
        return getMessage(key, (Object) null);
    }

    public String getMessage(String key, Object... arguments) {
        String message;
        try {
            message = this.messageSource.getMessage(key, arguments, locale);
        } catch (NoSuchMessageException e) {
            message = "Undefined message for key " + key;
        }
        return message;
    }

}

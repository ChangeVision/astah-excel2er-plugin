package excel2er;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.change_vision.jude.api.inf.ui.IMessageProvider;

public class Messages implements IMessageProvider {

	public static final String DEFAULT_BUNDLE = "excel2er.messages";

	private static ResourceBundle INTERNAL_MESSAGES = ResourceBundle.getBundle(
			DEFAULT_BUNDLE, Locale.getDefault(),
			Messages.class.getClassLoader());

	Messages() {

	}

	public static String getMessage(String key, Object... parameters) {
		String entry = INTERNAL_MESSAGES.getString(key);
		return MessageFormat.format(entry, parameters);
	}

	@Override
	public String provideMessage(String key, Object... parameters) {
		return getMessage(key, parameters);
	}

}

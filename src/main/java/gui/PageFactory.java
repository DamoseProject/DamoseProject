package gui;

public class PageFactory {

    public static GeneralPanel createPage(PageType pageType, MainFrame frame) {
        return switch (pageType) {
            case LOGIN -> new LoginPage(frame);
            case REGISTRATION -> new RegistrationPage(frame);
            case HELP -> new HelpPage(frame);
            case EMAIL_VERIFICATION -> new EmailVerificationPage(frame);
            case MAP_LOGGED -> new MapLogPage(frame);
            case MAP_GUEST -> new MapNotLogPage(frame);
        };
    }
}

package gui;

public class ButtonMapPageConfig {
    private final boolean favoritesEnabled;
    private final boolean viewFavoritesEnabled;
    private final String favoritesErrorMessage;
    private final String viewFavoritesErrorMessage;

    public ButtonMapPageConfig(boolean favoritesEnabled, boolean viewFavoritesEnabled,
                               String favoritesErrorMessage, String viewFavoritesErrorMessage) {
        this.favoritesEnabled = favoritesEnabled;
        this.viewFavoritesEnabled = viewFavoritesEnabled;
        this.favoritesErrorMessage = favoritesErrorMessage;
        this.viewFavoritesErrorMessage = viewFavoritesErrorMessage;
    }

    // Configurazioni predefinite
    public static ButtonMapPageConfig forLoggedUser() {
        return new ButtonMapPageConfig(true, true, null, null);
    }

    public static ButtonMapPageConfig forGuestUser() {
        return new ButtonMapPageConfig(false, false,
                ErrorMessages.LOGIN_REQUIRED_FAVORITES,
                ErrorMessages.LOGIN_REQUIRED_LIST);
    }

    // Getters
    public boolean isFavoritesEnabled() { return favoritesEnabled; }
    public boolean isViewFavoritesEnabled() { return viewFavoritesEnabled; }
    public String getFavoritesErrorMessage() { return favoritesErrorMessage; }
    public String getViewFavoritesErrorMessage() { return viewFavoritesErrorMessage; }
}
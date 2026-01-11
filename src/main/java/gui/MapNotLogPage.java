package gui;

public class MapNotLogPage extends BaseMapPage {
    public MapNotLogPage(MainFrame frame) {
        super(frame);
    }

    @Override
    public ButtonMapPageConfig getButtonConfig() {
        return ButtonMapPageConfig.forGuestUser();
    }
}





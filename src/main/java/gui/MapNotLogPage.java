package gui;

public class MapNotLogPage extends BaseMapPage {
    public MapNotLogPage(MainFrame frame) {
        super(frame);
    }

    @Override
    protected ButtonMapPageConfig getButtonConfig() {
        return ButtonMapPageConfig.forGuestUser();
    }
}





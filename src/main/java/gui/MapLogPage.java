package gui;

public class MapLogPage extends BaseMapPage {
    public MapLogPage(MainFrame frame) {
        super(frame);
    }

    @Override
    protected ButtonMapPageConfig getButtonConfig() {
        return ButtonMapPageConfig.forLoggedUser();
    }

}

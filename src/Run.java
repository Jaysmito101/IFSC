public class Run {
    public static void main(String[] args) {
        App main_instance = App.getInstance();
        main_instance.setVisible(true);
        main_instance.centerWindow();
    }
}
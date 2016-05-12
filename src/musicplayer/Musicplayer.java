package musicplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Light
 */
public class Musicplayer extends Application{
    
    private MediaPlayer player;
    private Label time, vol, currentSong;
    private Duration duration;
    private Scene scene;
    private Media media;
    double width, height;
    //private boolean endOfFile = false;
    private boolean endOfFiles = false;
    private boolean replay = false;
    private boolean shuffle = false;
    private Slider timeSlider, volumeSlider;
    private Button playButton, stopButton, backButton, forwardButton, nextButton, previousButton, addButton;
    private ContextMenu contextMenu;
    private MenuBar menuBar;
    private BorderPane borderPane;
    private File file = null;
    private TreeView<File> fileView;
    private ObservableList<String> tracklist = FXCollections.observableArrayList();
    private ListView<String> list = new ListView<String>();
    private List<File> files;
    private ArrayList<String> musicNames = new ArrayList<>();
    private ArrayList<MediaPlayer> players = new ArrayList<>();
    private Image playButtonImage = new Image(getClass().getResourceAsStream("Play.png"));
    private Image pausedButtonImage = new Image(getClass().getResourceAsStream("Pause.png"));
    private ImageView playingStt = new ImageView(playButtonImage);
    private ImageView pausedStt = new ImageView(pausedButtonImage);
    private Image stopButtonImage = new Image(getClass().getResourceAsStream("Stop.png"));
    private ImageView stopStt = new ImageView(stopButtonImage);
    private Image backButtonImage = new Image(getClass().getResourceAsStream("Back.png"));
    private ImageView backStt = new ImageView(backButtonImage);
    private Image forwardButtonImage = new Image(getClass().getResourceAsStream("Forward.png"));
    private ImageView forwardStt = new ImageView(forwardButtonImage);
    private Image nextButtonImage = new Image(getClass().getResourceAsStream("Next.png"));
    private ImageView nextStt = new ImageView(nextButtonImage);
    private Image previousButtonImage = new Image(getClass().getResourceAsStream("previous.png"));
    private ImageView previousStt = new ImageView(previousButtonImage);
    private Image addButtonImage = new Image(getClass().getResourceAsStream("Add.png"));
    private ImageView addStt = new ImageView(addButtonImage);
    private int songID = 0;
    private Random random = new Random();
    private FileWriter writer = null;
    private FileReader reader = null;
    
    @Override
    public void start(Stage stage) {
        
        stage.setTitle("Music Player Program - #Project_20152");
        Group root = new Group();
        scene = new Scene(root, 900, 400);
        
        borderPane = new BorderPane();
        list = new ListView<String>();
        borderPane.setCenter(list);
        
        borderPane.setLeft(addLeftBar());
        borderPane.setRight(addRightBar());
        
        borderPane.setBottom(addToolBar());
        menuBar = new MenuBar();
        menuBar = addMenuBar();
        borderPane.setTop(menuBar);
        menuBar.setContextMenu(addContextMenu());
        borderPane.setStyle("-fx-background-color: Grey");
        scene = new Scene(borderPane, 900, 400);
        scene.setFill(Color.ORANGE);
        
        
        stage.setScene(scene);
        
        stage.show();
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private VBox addLeftBar() {
        VBox leftBar = new VBox();
        fileView = new TreeView<File>(new FileTreeItem(new File("D:\\Musics")));
        fileView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {            
                if(mouseEvent.getClickCount() == 2) {
                    clearListView();
                    file = fileView.getSelectionModel().getSelectedItem().getValue();
                    String path = file.getAbsolutePath();
                    path = path.replace("\\", "/");
                    media = new Media(new File(path).toURI().toString());
                    player = new MediaPlayer(media);
                    players.add(player);
                    musicNames.add(file.getName());
                    tracklist.add(file.getName());
                    list.setItems(tracklist);
                    playMusics();
                }
            }
        });
        
        addButton = new Button();
        addButton.setGraphic(addStt);
        addButton.setStyle("-fx-background-color: Orange");
        addButton.setOnAction((ActionEvent e) -> {
            if (writer == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Sorry...");
                alert.setContentText("Ooops, there was an error!");
                alert.showAndWait();
            } else {
            if (list != null) list.getItems().clear();
            file = fileView.getSelectionModel().getSelectedItem().getValue();
            String path = file.getAbsolutePath();
            path = path.replace("\\", "/");
            tracklist.add(file.getName() + " has just added to your playlist");
            list.setItems(tracklist);
            
            try {
                    writer.write(path);
                    writer.write("\n");
                } catch (IOException ex) {
                    Logger.getLogger(Musicplayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        leftBar.getChildren().addAll(fileView, addButton);
        return leftBar;
    }
    
    private void clearListView() {
        if (player != null) player.stop();
        if (players != null) {players.clear(); songID = 0;}
        if (musicNames != null) {musicNames.clear(); songID = 0;}
        if (tracklist != null) tracklist.clear();
        if (list != null) list.getItems().clear();
    }
    
    private void playMusics() {
        player = players.get(songID);
        realTime();
        player.play();
        playButton.setGraphic(pausedStt);
        currentSong.setText("Playing: " + musicNames.get(songID));
        setOnEndOfMusic();
    }
    
    private void setOnEndOfMusic() {
        player.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (shuffle) {
                    player.stop();
                    songID = random.nextInt(players.size());
                    playMusics();
                }
                else {
                songID++;
                if (songID == players.size()) {
                    songID = 0;
                    endOfFiles = true;
                    if (replay) {
                        player.stop();
                        playMusics();
                    }
                    else {
                        player.seek(player.getStartTime());
                        player.stop();
                        currentSong.setText("End: " + musicNames.get(songID));
                        playButton.setGraphic(playingStt);
                    }
                }
                else playMusics();
                }
            }
        });
    }
    
    private void openPlaylist() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open a playlist");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Playlist File", "*.txt")
        );
        File playlistFile = fc.showOpenDialog(null);
        files = null;
        clearListView();
        try {
            reader = new FileReader(playlistFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Musicplayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                media = new Media(new File(line).toURI().toString());
                player = new MediaPlayer(media);
                players.add(player);
                String[] parts = line.split(":/Musics/");
                line = parts[1];
                musicNames.add(line);
                tracklist.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Musicplayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        list.setItems(tracklist);
        if (players != null ) playMusics();
        
    }
    
    private void fileChooser() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open music file");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac")
        );
        files = fc.showOpenMultipleDialog(null);
        clearListView();
        String path;
        int i;
        for (i = 0; i < files.size(); i++) {
            file = files.get(i);
            path = file.getAbsolutePath();
            path = path.replace("\\", "/");
            media = new Media(new File(path).toURI().toString());
            player = new MediaPlayer(media);
            players.add(player);
            tracklist.add(file.getName());
            musicNames.add(file.getName());
        }
        list.setItems(tracklist);
        if (files != null ) playMusics();
    }
    
    private MenuBar addMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: Orange");
        Menu menuFile = new Menu("File");
        MenuItem menuOpenFile = new MenuItem("Open file");
        menuOpenFile.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        menuOpenFile.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                fileChooser();
            }
        });
        MenuItem menuOpenPlaylist = new MenuItem("Open playlist");
        menuOpenPlaylist.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+O"));
        menuOpenPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                openPlaylist();
            }
        });
        MenuItem menuClose = new MenuItem("Close");
        menuClose.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        menuClose.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        menuFile.getItems().addAll(menuOpenFile, menuOpenPlaylist, menuClose);
        
        Menu menuTool = new Menu("Tool");
        MenuItem menuCreatePlayList = new MenuItem("Create a playlist");
        menuCreatePlayList.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        menuCreatePlayList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Create your playlist");
                dialog.setHeaderText("Enter your playlist name you want");
                dialog.setContentText("Please enter:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                String path = "D:\\Musics\\" + result.get() + ".txt";
                File playlistFile = new File(path);
                try {
                    writer = new FileWriter(playlistFile);
                } catch (IOException ex) {
                    Logger.getLogger(Musicplayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Successful");
                alert.setHeaderText(null);
                alert.setContentText("Your playlist has already been created!");

                alert.showAndWait();
                } else return;
        }
        });
        MenuItem menuSavePlayList = new MenuItem("Save playlist");
        menuSavePlayList.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        menuSavePlayList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                if (writer == null) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Sorry...");
                    alert.setContentText("Ooops, there was an error!");
                    alert.showAndWait();
                }
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(Musicplayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Successful");
                alert.setHeaderText(null);
                alert.setContentText("Your playlist has already saved!");

                alert.showAndWait();
            }
        });
        menuTool.getItems().addAll(menuCreatePlayList, menuSavePlayList);
        menuBar.getMenus().addAll(menuFile, menuTool);
        return menuBar;
    }
    
    private VBox addRightBar() {
        VBox rightBar = new VBox();
        rightBar.setStyle("-fx-background-color: Orange");
        vol = new Label("    Volume");
        vol.setTextFill(Color.WHITE);
        vol.setPrefWidth(80);
        volumeSlider = new Slider();
        volumeSlider = addVolumeSlider(volumeSlider);
        volumeSlider.setOrientation(Orientation.VERTICAL);
        rightBar.getChildren().addAll(vol, volumeSlider);
        return rightBar;
    }
    
    private HBox addToolBar() {
        HBox toolBar = new HBox();
        toolBar.setPadding(new Insets(1));
        toolBar.setAlignment(Pos.CENTER);
        toolBar.alignmentProperty().isBound();
        toolBar.setSpacing(5);
        toolBar.setStyle("-fx-background-color: Orange");
        
        addButton();
        
        timeSlider = new Slider();
        timeSlider = addTimeSlider(timeSlider);
        
        time = new Label("         Time:");
        
        time.setTextFill(Color.WHITE);
        time.setPrefWidth(80);
        
        currentSong = new Label("");
        currentSong.setMaxSize(200, 50);
        currentSong.setMinSize(200, 50);
        
        toolBar.getChildren().addAll(previousButton, backButton, playButton, forwardButton, nextButton, stopButton, time, timeSlider, currentSong);
        
        
        return toolBar;
    }
    
    
    private ContextMenu addContextMenu() {
        contextMenu = new ContextMenu();
        CheckMenuItem replayCheck = new CheckMenuItem("Replay");
        replayCheck.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                replay = !replay;
            }
        });
        contextMenu.getItems().add(replayCheck);
        
        CheckMenuItem shuffleCheck = new CheckMenuItem("Shuffle");
        shuffleCheck.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                shuffle = !shuffle;
            }
        });
        contextMenu.getItems().add(shuffleCheck);
        
        return contextMenu;
    }
    
    private void checkInvalidOperation() {
        if (player == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Sorry...");
                alert.setContentText("Ooops, there was an error!");
                alert.showAndWait();
            }
    }
    
    private void addButton() {
        //Add play button
        playButton = new Button();
        playButton.setGraphic(playingStt);
        playButton.setStyle("-fx-background-color: Orange");
        playButton.setOnAction((ActionEvent e) -> {
            checkInvalidOperation();
            Status stt = player.getStatus();
            if (stt == Status.UNKNOWN || stt == Status.HALTED) {
                return;
            }
            if (stt == Status.PAUSED || stt == Status.STOPPED || stt == Status.READY) {
                if (endOfFiles) {
                    songID = 0;
                    playMusics();
                }
                else {
                    //realTime();
                    player.play();
                    playButton.setGraphic(pausedStt);
                    currentSong.setText("Playing: " + musicNames.get(songID));
                    setOnEndOfMusic();
                }
            }
            else {
                player.pause();
                playButton.setGraphic(playingStt);
                currentSong.setText("Paused: " + musicNames.get(songID));
            }
            
        });
        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                playButton.setStyle("-fx-background-color: Orange");
                playButton.setStyle("-fx-body-color: Black");
        });
        playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                playButton.setStyle("-fx-background-color: Orange");
        });
        
        //Add stop button
        stopButton = new Button();
        stopButton.setGraphic(stopStt);
        stopButton.setStyle("-fx-background-color: Orange");
        stopButton.setOnAction((ActionEvent e) -> {
            checkInvalidOperation();
            player.seek(player.getStartTime());
            player.pause();
            playButton.setGraphic(playingStt);
            currentSong.setText("Stopped: " + musicNames.get(songID));
        });
        stopButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                stopButton.setStyle("-fx-background-color: Orange");
                stopButton.setStyle("-fx-body-color: Black");
        });
        stopButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                stopButton.setStyle("-fx-background-color: Orange");
        });
        
        //Add back button
        backButton = new Button();
        backButton.setGraphic(backStt);
        backButton.setStyle("-fx-background-color: Orange");
        backButton.setOnAction((ActionEvent e) -> {
            checkInvalidOperation();
            player.seek(player.getCurrentTime().divide(1.5));
        });
        backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                backButton.setStyle("-fx-background-color: Orange");
                backButton.setStyle("-fx-body-color: Black");
        });
        backButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                backButton.setStyle("-fx-background-color: Orange");
        });
        
        //Add forward button
        forwardButton = new Button();
        forwardButton.setGraphic(forwardStt);
        forwardButton.setStyle("-fx-background-color: Orange");
        forwardButton.setOnAction((ActionEvent e) -> {
            checkInvalidOperation();
            player.seek(player.getCurrentTime().multiply(1.5));
        });
        forwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                forwardButton.setStyle("-fx-background-color: Orange");
                forwardButton.setStyle("-fx-body-color: Black");
        });
        forwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                forwardButton.setStyle("-fx-background-color: Orange");
        });
        
        //Add next button
        nextButton = new Button();
        nextButton.setGraphic(nextStt);
        nextButton.setStyle("-fx-background-color: Orange");
        nextButton.setOnAction((ActionEvent e) -> {
            checkInvalidOperation();
            if (songID >= players.size()-1) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Sorry...");
                alert.setContentText("Ooops, there was an error!");
                alert.showAndWait();
            } else {
            player.stop();
            songID++;
            playMusics();
            }
        });
        nextButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                nextButton.setStyle("-fx-background-color: Orange");
                nextButton.setStyle("-fx-body-color: Black");
        });
        nextButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                nextButton.setStyle("-fx-background-color: Orange");
        });
        
        
        //Add previous button
        previousButton = new Button();
        previousButton.setGraphic(previousStt);
        previousButton.setStyle("-fx-background-color: Orange");
        previousButton.setOnAction((ActionEvent e) -> {
            checkInvalidOperation();
            if (songID == 0) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Sorry...");
                alert.setContentText("Ooops, there was an error!");
                alert.showAndWait();
            } else {
            player.stop();
            songID--;
            playMusics();
            }
        });
        previousButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                previousButton.setStyle("-fx-background-color: Orange");
                previousButton.setStyle("-fx-body-color: Black");
        });
        previousButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                previousButton.setStyle("-fx-background-color: Orange");
        });
        
    }
    
    
    private void realTime() {
        player.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        player.setOnReady(new Runnable() {
            public void run() {
                duration = player.getMedia().getDuration();
                updateValues();
            }
        });
        
        setOnEndOfMusic();
        
    }
    
    private Slider addVolumeSlider(Slider volumeSlider) {
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    player.setVolume(volumeSlider.getValue() / 100.0);
                }
                
            }
        });
        return volumeSlider;
    }
    
    private Slider addTimeSlider(Slider timeSlider) {
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMaxSize(200, 50);
        timeSlider.setMinSize(200, 50);

        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                
                    if (duration != null) {
                        player.seek(duration.multiply(timeSlider.getValue() / 100.0));
                    }
                    updateValues();

                }
            }
        });

        return timeSlider;
    }
    
    protected void updateValues() {
        duration = player.getMedia().getDuration();
        if (time != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                @SuppressWarnings("deprecation")
				public void run() {
                    Duration currentTime = player.getCurrentTime();
                    time.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(player.getVolume()
                                * 100));
                    }
                }
            });
        }
    }
    
    
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
    
    
}
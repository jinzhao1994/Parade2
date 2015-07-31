package sample;

import java.util.Vector;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        Main.launch(args);
    }
    ModelBack modelBack=new ModelBack();
    Model model;
    View view;
    AnchorPane root=new AnchorPane();
    ButtonArea buttonArea=new ButtonArea();
    ImageView menu=new ImageView(new Image("menu.png"));
    ImageView background=new ImageView(new Image("background.jpg"));
    AudioClip backgroundMusic=new AudioClip(getClass().getResource("../start.wav").toString());
    public void start(Stage stage) {
        System.out.println(this.getClass().getResource("").toString());
        AnchorPane.setTopAnchor(menu,13.0);
        AnchorPane.setLeftAnchor(menu,963.0);
        AnchorPane.setTopAnchor(buttonArea,0.0);
        AnchorPane.setLeftAnchor(buttonArea,700.0);
        root.getChildren().add(background);
        root.getChildren().add(menu);
        menu.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent evn) {
                root.getChildren().add(buttonArea);
            }
        });
        Rectangle bkg=new Rectangle(500.0,150.0);
        bkg.setStrokeWidth(0.0);
        bkg.setFill(Color.BLACK);
        bkg.setOpacity(0.7);
        Text hintText=new Text("Welcome to Parade Game!\n"
                + "This is a simple game coded by Cs\n"
                + "Move the mouse to the top right\n"
                + "and put it on the menu button to start the game.\n"
                + "Thank you for playing!");
        hintText.setFont(new Font(20));
        hintText.setFill(Color.WHITE);
        hintText.setEffect(new DropShadow(2.0,2.0,2.0,Color.BLACK));
        AnchorPane.setTopAnchor(bkg,200.0);
        AnchorPane.setLeftAnchor(bkg,250.0);
        AnchorPane.setTopAnchor(hintText,210.0);
        AnchorPane.setLeftAnchor(hintText,260.0);
        backgroundMusic.setCycleCount(AudioClip.INDEFINITE);
        backgroundMusic.play();
        root.getChildren().add(bkg);
        root.getChildren().add(hintText);
        stage.setTitle("Parade - 1.1 - Cs");
        stage.setScene(new Scene(root, 1000, 600));
        stage.setMinHeight(639);
        stage.setMinWidth(1016);
        stage.setMaxHeight(639);
        stage.setMaxWidth(1016);
        stage.show();
    }
    public class ButtonArea extends AnchorPane {
        boolean first=true;
        CheckBox cheat=new CheckBox("Cheat");
        Text hintText=new Text("No Hint");
        RulesHelp rulesHelp=new RulesHelp();
        AboutHelp aboutHelp=new AboutHelp();
        public ButtonArea() {
            this.setPrefSize(300.0,600.0);
            this.setMinSize(300.0,600.0);
            this.setMaxSize(300.0,600.0);
            this.setOnMouseExited(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent evn) {
                    root.getChildren().remove(buttonArea);
                }
            });

            Rectangle btnBkg=new Rectangle(300.0,600.0);
            btnBkg.setStrokeWidth(0.0);
            btnBkg.setFill(new Color(218.0/255,243.0/255,239.0/255,1));
            btnBkg.setOpacity(0.8);
            this.getChildren().add(btnBkg);

            Text newGame=new Text("~ New Game ~");
            newGame.setFont(new Font("Blackadder ITC",30));
            newGame.setFill(Color.RED);
            AnchorPane.setTopAnchor(newGame,30.0);
            AnchorPane.setLeftAnchor(newGame,65.0);
            this.getChildren().add(newGame);

            ToggleGroup group = new ToggleGroup();
            RadioButton firstBtn = new RadioButton("offensive position");
            firstBtn.setToggleGroup(group);
            firstBtn.setSelected(true);
            RadioButton notFirstBtn = new RadioButton("defensive position");
            notFirstBtn.setToggleGroup(group);
            firstBtn.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    first=true;
                }
            });
            notFirstBtn.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    first=false;
                }
            });
            AnchorPane.setTopAnchor(firstBtn,80.0);
            AnchorPane.setLeftAnchor(firstBtn,85.0);
            AnchorPane.setTopAnchor(notFirstBtn,100.0);
            AnchorPane.setLeftAnchor(notFirstBtn,85.0);
            this.getChildren().add(firstBtn);
            this.getChildren().add(notFirstBtn);

            this.getChildren().add(new NewGameButton("Stupid Computer",0));
            this.getChildren().add(new NewGameButton("Normal Computer",1));
            this.getChildren().add(new NewGameButton("Normal Computer",2)); //smart
            this.getChildren().add(new NewGameButton("Normal Computer",3)); //wise
            this.getChildren().add(new NewGameButton("Normal Computer",4)); //cheating

            Text option=new Text("~ Option ~");
            option.setFont(new Font("Blackadder ITC",30));
            option.setFill(Color.RED);
            AnchorPane.setTopAnchor(option,280.0);
            AnchorPane.setLeftAnchor(option,80.0);
            this.getChildren().add(option);

            cheat.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    if (view!=null) {
                        CheckBox cheat=(CheckBox)evn.getSource();
                        view.setCheat(cheat.isSelected());
                    }
                }
            });
            AnchorPane.setTopAnchor(cheat,330.0);
            AnchorPane.setLeftAnchor(cheat,120.0);
            this.getChildren().add(cheat);

            Button goBack=new Button("Go Back A Step");
            goBack.disableProperty().bind(modelBack.empty);
            goBack.setPrefWidth(110.0);
            goBack.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    model=modelBack.remove(modelBack.size()-1);
                    view=new View(model,modelBack);
                    SequentialTransition seq=new SequentialTransition(view.fadeAnimation());
                    seq.setOnFinished(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent evn) {
                            view.ani--;
                        }
                    });
                    view.ani++;
                    seq.play();
                    root.getChildren().clear();
                    root.getChildren().add(background);
                    root.getChildren().add(view);
                    root.getChildren().add(menu);
                }
            });
            AnchorPane.setTopAnchor(goBack,355.0);
            AnchorPane.setLeftAnchor(goBack,95.0);
            this.getChildren().add(goBack);

            Button hintBtn=new Button("Hint");
            hintBtn.setOnAction(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent evn) {
                    if (model==null||model.state==-1) hintText.setText("No Hint");
                    else {
                        if (model.state!=5)
                            hintText.setText(String.valueOf(1+model.me.chooseHandCards()));
                        else if (model.me.handCards[3]!=null)
                            hintText.setText(String.valueOf(1+model.me.chooseEndCards(4)));
                        else
                            hintText.setText(String.valueOf(1+model.me.chooseEndCards(3)));
                    }
                }
            });
            AnchorPane.setTopAnchor(hintBtn,380.0);
            AnchorPane.setLeftAnchor(hintBtn,95.0);
            AnchorPane.setTopAnchor(hintText,385.0);
            AnchorPane.setLeftAnchor(hintText,150.0);
            this.getChildren().add(hintText);
            this.getChildren().add(hintBtn);

            Text help=new Text("~ Help ~");
            help.setFont(new Font("Blackadder ITC",30));
            help.setFill(Color.RED);
            AnchorPane.setTopAnchor(help,430.0);
            AnchorPane.setLeftAnchor(help,92.0);
            this.getChildren().add(help);

            Button rules=new Button("Rules");
            rules.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    if (root.getChildren().indexOf(rulesHelp)==-1)
                        root.getChildren().add(rulesHelp);
                    root.getChildren().remove(buttonArea);
                }
            });
            rules.setPrefWidth(80.0);
            AnchorPane.setTopAnchor(rules,480.0);
            AnchorPane.setLeftAnchor(rules,110.0);
            this.getChildren().add(rules);

            Button about=new Button("About");
            about.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    if (root.getChildren().indexOf(aboutHelp)==-1)
                        root.getChildren().add(aboutHelp);
                    root.getChildren().remove(buttonArea);
                }
            });
            about.setPrefWidth(80.0);
            AnchorPane.setTopAnchor(about,505.0);
            AnchorPane.setLeftAnchor(about,110.0);
            this.getChildren().add(about);

            Text version=new Text("Version - 1.1\n"
                    + "    Coded by Cs");
            AnchorPane.setBottomAnchor(version,5.0);
            AnchorPane.setRightAnchor(version,5.0);
            this.getChildren().add(version);
        }
        public class RulesHelp extends AnchorPane {
            public RulesHelp() {
                AnchorPane.setTopAnchor(this,85.0);
                AnchorPane.setLeftAnchor(this,135.0);
                Rectangle bkg=new Rectangle(730.0,430.0);
                bkg.setStrokeWidth(0.0);
                bkg.setFill(Color.WHITE);
                bkg.setOpacity(0.9);
                Text hintText1=new Text("~ RULES ~");
                Text hintText2=new Text("Setup:\n"
                        + "Choose a starting Player.\n"
                        + "Shuffle the cards.\n"
                        + "Deal 5 cards to each player.\n"
                        + "Put 6 cards face up next to the deck as the Parade.\n"
                        + "\n"
                        + "Game Flow:\n"
                        + "On their turn, a player:\n"
                        + "  Plays 1 card to the Parade.\n"
                        + "  Update the Parade.\n"
                        + "  Draws 1 card from the deck.\n"
                        + "\n"
                        + "Updating The Parade:\n"
                        + "The number on the new card in the Parade\n"
                        + "determines how many cards are SAFE (counting\n"
                        + "from the back of the Parade, and does not include\n"
                        + "the new card in the count).\n"
                        + "Non-SAFE Cards are taken by the player if:\n"
                        + "  - same color as the played card.\n"
                        + "  OR\n"
                        + "  - #(any color) <= to the # on the played card.\n");
                Text hintText3=new Text("Game End:\n"
                        + "When a player has takoen a card of each of the 6\n"
                        + "colors OR the deck has run out the last round\n"
                        + "begins.\n"
                        + "\n"
                        + "In last round, players each play one more card and\n"
                        + "update the Parade, but do not draw a card.\n"
                        + "After the last round, each player will have 4 cards.\n"
                        + "They will pick 2 cards to put face down in front of\n"
                        + "them that will be added to the cards they took from\n"
                        + "the Parade, and discard the 2 other cards.\n"
                        + "Once both players have made their decision, the\n"
                        + "game is over and the score is calculated\n"
                        + "\n"
                        + "For each color, check to see if a player has a\n"
                        + "majority (number of cards in a color compared to\n"
                        + "other player >= 2 cards is a majority).\n"
                        + "If a majority, then each card in that color is 1 point.\n"
                        + "Otherwise the points are equal to the number on\n"
                        + "the card.\n"
                        + "LOWEST SCORE WINS.\n");
                hintText1.setFont(new Font("Blackadder ITC",20));
                hintText1.setFill(Color.RED);
                hintText2.setFont(new Font(14));
                hintText3.setFont(new Font(14));
                AnchorPane.setTopAnchor(bkg,0.0);
                AnchorPane.setLeftAnchor(bkg,0.0);
                AnchorPane.setTopAnchor(hintText1,10.0);
                AnchorPane.setLeftAnchor(hintText1,305.0);
                AnchorPane.setTopAnchor(hintText2,45.0);
                AnchorPane.setLeftAnchor(hintText2,10.0);
                AnchorPane.setTopAnchor(hintText3,45.0);
                AnchorPane.setLeftAnchor(hintText3,370.0);
                this.getChildren().add(bkg);
                this.getChildren().add(hintText1);
                this.getChildren().add(hintText2);
                this.getChildren().add(hintText3);
                Exit exit=new Exit();
                AnchorPane.setTopAnchor(exit,0.0);
                AnchorPane.setRightAnchor(exit,0.0);
                this.getChildren().add(exit);
            }
            public class Exit extends ImageView {
                public Exit() {
                    this.setImage(new Image("close.png",20.0,20.0,true,true));
                    this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent evn) {
                            root.getChildren().remove(rulesHelp);
                        }
                    });
                }
            }
        }
        public class AboutHelp extends AnchorPane {
            public AboutHelp() {
                AnchorPane.setTopAnchor(this,180.0);
                AnchorPane.setLeftAnchor(this,325.0);
                Rectangle bkg=new Rectangle(350.0,200.0);
                bkg.setStrokeWidth(0.0);
                bkg.setFill(Color.WHITE);
                bkg.setOpacity(0.9);
                Text hintText=new Text("This game is coded by Cs.\n"
                        + " The version is 1.1.\n"
                        + "If you found any bug,\n"
                        + " or you have any advice,\n"
                        + "please send email to\n"
                        + " jinzhao1994@126.com to tell me.\n"
                        + "Thanks~");
                hintText.setFont(new Font(20));
                AnchorPane.setTopAnchor(bkg,0.0);
                AnchorPane.setLeftAnchor(bkg,0.0);
                AnchorPane.setTopAnchor(hintText,10.0);
                AnchorPane.setLeftAnchor(hintText,10.0);
                this.getChildren().add(bkg);
                this.getChildren().add(hintText);
                Exit exit=new Exit();
                AnchorPane.setTopAnchor(exit,0.0);
                AnchorPane.setRightAnchor(exit,0.0);
                this.getChildren().add(exit);
            }
            public class Exit extends ImageView {
                public Exit() {
                    this.setImage(new Image("close.png",20.0,20.0,true,true));
                    this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent evn) {
                            root.getChildren().remove(aboutHelp);
                        }
                    });
                }
            }
        }
        public class NewGameButton extends Button {
            public NewGameButton(String text,int lv) {
                super(text);
                this.setPrefWidth(140.0);
                AnchorPane.setTopAnchor(this,130.0+25.0*lv);
                AnchorPane.setLeftAnchor(this,80.0);
                this.setOnAction(new StartGame(lv));
            }
        }
        public class StartGame implements EventHandler<ActionEvent> {
            int lv;
            public StartGame(int i) {
                lv=i;
            }
            public void handle(ActionEvent evn) {
                modelBack.clear();
                model=new Model(lv,buttonArea.first);
                view=new View(model,modelBack);
                if (buttonArea.cheat.isSelected()) view.setCheat(true);
                SequentialTransition seq=new SequentialTransition(view.fadeAnimation(),view.startAnimation());
                seq.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent evn) {
                        view.ani--;
                        if (model.isNextMe==false) view.computerRound();
                    }
                });
                view.ani++;
                seq.play();
                root.getChildren().clear();
                root.getChildren().add(background);
                root.getChildren().add(view);
                root.getChildren().add(menu);
            }
        }
    }
    public class ModelBack extends Vector<Model> {
        private static final long serialVersionUID = 9041645490335629936L;
        SimpleBooleanProperty empty=new SimpleBooleanProperty(true);
        public void clear() {
            empty.set(true);
            super.clear();
        }
        public boolean add(Model x) {
            empty.set(false);
            return super.add(x);
        }
        public Model remove(int i) {
            Model ans=super.remove(i);
            if (super.size()==0) empty.set(true);
            return ans;
        }
    }
}

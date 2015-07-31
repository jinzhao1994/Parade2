package sample;

import java.util.Vector;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class View extends AnchorPane {
    static Image cardBack;
    static Image[] cards;
    public Vector<Model> modelBack;
    public Model model;
    public boolean isMeCheat=false;
    public int ani=0;
    public LeaveCardsView myLeaveCards,oppLeaveCards;
    public HandCardsView myHandCards,oppHandCards;
    public ScoreView myScore,oppScore;
    public ParadeCardsView parade=new ParadeCardsView();
    public DeckView deck;
    public int cei=-1,cej=-1;

    public View(Model model,Vector<Model> modelBack) {
        if (cardBack==null) {
            cards=new Image[66];
            for (int i=0;i<6;i++) {
                for (int j=0;j<=10;j++) {
                    cards[i*11+j]=new Image("Card"+String.valueOf(i*11+j)+".jpg",100,150,false,true);
                }
            }
            cardBack=new Image("CardBack.jpg",100,150,false,true);
        }
        this.model=model;
        this.modelBack=modelBack;
        this.setPrefSize(1000.0,600.0);
        this.setMinSize(1000.0,600.0);
        this.setMaxSize(1000.0,600.0);

        myLeaveCards=new LeaveCardsView(true);
        oppLeaveCards=new LeaveCardsView(false);
        myHandCards=new HandCardsView(true);
        oppHandCards=new HandCardsView(false);
        myScore=new ScoreView();
        oppScore=new ScoreView();
        deck=new DeckView();

        this.getChildren().add(myLeaveCards);
        AnchorPane.setTopAnchor(myLeaveCards,150.0);
        AnchorPane.setLeftAnchor(myLeaveCards,50.0);
        this.getChildren().add(oppLeaveCards);
        AnchorPane.setTopAnchor(oppLeaveCards,0.0);
        AnchorPane.setLeftAnchor(oppLeaveCards,800.0);

        this.getChildren().add(deck);
        AnchorPane.setTopAnchor(deck,200.0);
        AnchorPane.setLeftAnchor(deck,233.0);

        this.getChildren().add(parade);
        AnchorPane.setTopAnchor(parade,200.0);
        AnchorPane.setLeftAnchor(parade,366.0);

        this.getChildren().add(myHandCards);
        AnchorPane.setTopAnchor(myHandCards,450.0);
        AnchorPane.setLeftAnchor(myHandCards,250.0);
        this.getChildren().add(oppHandCards);
        AnchorPane.setTopAnchor(oppHandCards,0.0);
        AnchorPane.setLeftAnchor(oppHandCards,250.0);

        this.getChildren().add(myScore);
        AnchorPane.setTopAnchor(myScore,510.0);
        AnchorPane.setLeftAnchor(myScore,780.0);
        this.getChildren().add(oppScore);
        AnchorPane.setTopAnchor(oppScore,60.0);
        AnchorPane.setLeftAnchor(oppScore,40.0);

        update(model);
    }
    public void update(Model model) {
        myLeaveCards.update(model.me.leaveCards,model.me.numOfColor,model.opp.numOfColor);
        oppLeaveCards.update(model.opp.leaveCards,model.opp.numOfColor,model.me.numOfColor);
        myHandCards.update(model.me.handCards);
        oppHandCards.update(model.opp.handCards);
        myScore.updateScore(model.me.getScore(model.opp));
        oppScore.updateScore(model.opp.getScore(model.me));
        parade.update(model.parade);
        deck.update(model.deck.n);
        if (cei!=-1&&oppHandCards.cards[cei]!=null)
            AnchorPane.setTopAnchor(oppHandCards.cards[cei],30.0);
        if (cej!=-1&&oppHandCards.cards[cej]!=null)
            AnchorPane.setTopAnchor(oppHandCards.cards[cej],30.0);
    }
    public void computerRound() {
        int i=model.opp.chooseHandCards();

        Model back=model;
        Model.Card cc=back.opp.handCards[i];
        model=(Model)model.clone();
        model.opp.updateLeaveCards(model.opp.play(i));
        if (model.state==1)	model.opp.draw();
        if (model.state==2||model.state==3) model.state++;

        SequentialTransition seq=new SequentialTransition();
        PauseTransition pause=new PauseTransition(Duration.millis(0.0));
        pause.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent evn) {
                AudioClip play=new AudioClip(getClass().getResource("../play.mp3").toString());
                play.setCycleCount(1);
                play.play();
            }
        });
        seq.getChildren().add(pause);
        seq.getChildren().add(oppHandCards.playAnimation(i));
        seq.getChildren().add(parade.playAnimation(back.parade.stateIfAdd(cc),cc));
        seq.getChildren().add(oppLeaveCards.playAnimaTion(back.opp.leaveCards,model.opp.leaveCards));
        if (back.state==1) seq.getChildren().add(oppHandCards.drawAnimation(model.opp.handCards[4]));
        ani++;
        seq.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent evn) {
                model.isNextMe=true;
                ani--;
                checkState();
                View.this.update(model);
            }
        });
        seq.play();
    }
    public void setCheat(boolean cheat) {
        isMeCheat=cheat;
        oppHandCards.show=cheat;
        for (int i=0;i<5;i++) {
            if (oppHandCards.cards[i]!=null) oppHandCards.cards[i].setShow(cheat);
        }
    }
    public void playEndAnimation() {
        Model back=(Model)model.clone();
        model.opp.noMoveEnd(cei);
        model.opp.noMoveEnd(cej);
        SequentialTransition seq=new SequentialTransition();
        ParallelTransition fade=new ParallelTransition();
        fade.getChildren().add(oppHandCards.cards[cei].fadeAnimation(false));
        fade.getChildren().add(oppHandCards.cards[cej].fadeAnimation(false));
        seq.getChildren().add(fade);
        seq.getChildren().add(oppLeaveCards.playAnimaTion(back.opp.leaveCards,model.opp.leaveCards));
        seq.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent evn) {
                View.this.update(model);
                ani--;
                int myScore=View.this.myScore.v;
                int oppScore=View.this.oppScore.v;
                String s="Game End!\n";
                AudioClip play;
                if (myScore>oppScore) {
                    s=s.concat("You Lose!");
                    play=new AudioClip(getClass().getResource("../lose.mp3").toString());
                } else if (myScore==oppScore) {
                    s=s.concat("Tie!");
                    play=new AudioClip(getClass().getResource("../win.mp3").toString());
                } else {
                    s=s.concat("You Win!");
                    play=new AudioClip(getClass().getResource("../win.mp3").toString());
                }
                play.setCycleCount(1);
                play.play();
                final Text t=new Text(s);
                t.setFont(new Font(50));
                t.setFill(Color.RED);
                View.this.getChildren().add(t);
                AnchorPane.setTopAnchor(t,250.0);
                AnchorPane.setLeftAnchor(t,400.0);
                FadeTransition fa=new FadeTransition(Duration.millis(2500),t);
                fa.setFromValue(1.0);
                fa.setToValue(0.1);
                fa.setOnFinished(new EventHandler<ActionEvent>(){
                    public void handle(ActionEvent evn) {
                        View.this.getChildren().remove(t);
                    }
                });
                fa.play();
            }
        });
        ani++;
        seq.play();
    }
    public void checkState() {
        if (model.check()) {
            String s="";
            if (model.state==2) s="Final Round!";
            else if (model.state==5) s="End State!";
            final Text t=new Text(s);
            t.setFont(new Font(50));
            t.setFill(Color.RED);
            this.getChildren().add(t);
            AnchorPane.setTopAnchor(t,250.0);
            AnchorPane.setLeftAnchor(t,400.0);
            FadeTransition fa=new FadeTransition(Duration.millis(2500),t);
            fa.setFromValue(1.0);
            fa.setToValue(0.1);
            fa.setOnFinished(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent evn) {
                    View.this.getChildren().remove(t);
                }
            });
            fa.play();
        }
    }
    public Transition startAnimation() {
        SequentialTransition seq1=new SequentialTransition();
        seq1.getChildren().add(oppHandCards.startAnimation());
        seq1.getChildren().add(myHandCards.startAnimation());
        seq1.getChildren().add(parade.startAnimation());
        SequentialTransition seq2=new SequentialTransition();
        deck.update(66);
        for (int i=0;i<16;i++)
            seq2.getChildren().add(deck.drawAnimation());
        return new ParallelTransition(seq1,seq2);
    }
    public Transition fadeAnimation() {
        FadeTransition ans=new FadeTransition(Duration.millis(500.0),this);
        ans.setFromValue(0.0);
        ans.setToValue(1.0);
        return ans;
    }
    public class CardView extends ImageView{
        public Model.Card data;
        public boolean show;
        public CardView(Model.Card c,boolean show) {
            data=c;
            setShow(show);
        }
        public void setShow(boolean show) {
            this.show=show;
            if (show) this.setImage(cards[data.v]);
            else this.setImage(cardBack);
        }
        public Transition fadeAnimation(boolean appear) {
            FadeTransition ans=new FadeTransition(Duration.millis(500.0),this);
            if (appear) {
                ans.setFromValue(0.0);
                ans.setToValue(1.0);
                ans.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent evn) {
                        CardView.this.setOpacity(1.0);
                    }
                });
            } else {
                ans.setFromValue(1.0);
                ans.setToValue(0.0);
                ans.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent evn) {
                        CardView.this.setOpacity(0.0);
                    }
                });
            }
            return ans;
        }
    }
    public class HandCardsView extends AnchorPane {
        public CardView[] cards=new CardView[5];
        public boolean my,show;
        public HandCardsView(boolean my) {
            this.setPrefSize(500.0,150.0);
            this.setMinSize(500.0,150.0);
            this.setMaxSize(500.0,150.0);
            this.my=my;
            if (my||isMeCheat) show=true;
            else show=false;
        }
        public void update(Model.Card[] cardsData) {
            this.getChildren().clear();
            for (int i=0;i<5;i++) {
                if (cardsData[i]!=null) {
                    cards[i]=new CardView(cardsData[i],show);
                    this.getChildren().add(cards[i]);
                    AnchorPane.setLeftAnchor(cards[i],i*100.0);
                    AnchorPane.setTopAnchor(cards[i],0.0);
                } else cards[i]=null;
            }
            if (my) {
                this.setOnMouseClicked(new HandCardClick());
                for (int i=0;i<5;i++) if (cards[i]!=null) {
                    cards[i].setOnMouseEntered(new HandCardChoose());
                    cards[i].setOnMouseExited(new HandCardUnchoose());
                }
            }
        }
        public Transition startAnimation() {
            SequentialTransition ans=new SequentialTransition();
            for (int i=0;i<5;i++) {
                if (cards[i]!=null) {
                    cards[i].setOpacity(0.0);
                    ans.getChildren().add(cards[i].fadeAnimation(true));
                }
            }
            return ans;
        }
        public Transition playAnimation(int i) {
            SequentialTransition ans=new SequentialTransition();
            ans.getChildren().add(cards[i].fadeAnimation(false));
            ParallelTransition par=new ParallelTransition();
            for (i=i+1;i<5;i++) {
                TranslateTransition mov=new TranslateTransition(Duration.millis(500.0),cards[i]);
                mov.setByX(-100.0);
                par.getChildren().add(mov);
            }
            ans.getChildren().add(par);
            return ans;
        }
        public Transition drawAnimation(Model.Card c) {
            ParallelTransition ans=new ParallelTransition();
            ans.getChildren().add(deck.drawAnimation());
            CardView cur=new CardView(c,show);
            AnchorPane.setLeftAnchor(cur,400.0);
            AnchorPane.setTopAnchor(cur,0.0);
            cur.setOpacity(0.0);
            this.getChildren().add(cur);
            ans.getChildren().add(cur.fadeAnimation(true));
            return ans;
        }
        public class HandCardChoose implements EventHandler<MouseEvent> {
            public void handle(MouseEvent mouse) {
                if (ani!=0||model.isNextMe==false||model.state==-1) return;
                CardView cur=(CardView)mouse.getSource();
                cur.setOpacity(0.7);
                Model tmp=(Model)model.clone();
                int i=0;
                while (!cur.data.equal(tmp.me.handCards[i])) i++;
                if (model.state<5) {
                    parade.notify(cur.data);
                    tmp.me.updateLeaveCards(tmp.me.play(i));
                } else {
                    tmp.me.end(i);
                }
                int oms=myScore.v,oos=oppScore.v;
                int nms=tmp.me.getScore(tmp.opp);
                int nos=tmp.opp.getScore(tmp.me);
                if (oms-oos<nms-nos) {
                    myScore.virtualUpdateScore(nms,-1);
                    oppScore.virtualUpdateScore(nos,1);
                } else if (oms-oos==nms-nos) {
                    myScore.virtualUpdateScore(nms,0);
                    oppScore.virtualUpdateScore(nos,0);
                } else {
                    myScore.virtualUpdateScore(nms,1);
                    oppScore.virtualUpdateScore(nos,-1);
                }
            }
        }
        public class HandCardUnchoose implements EventHandler<MouseEvent> {
            public void handle(MouseEvent mouse) {
                if (ani!=0||model.isNextMe==false||model.state==-1) return;
                CardView cur=(CardView)mouse.getSource();
                cur.setOpacity(1.0);
                if (model.state<5) parade.unnotify();
                myScore.updateScore(myScore.v);
                oppScore.updateScore(oppScore.v);
            }
        }
    }
    public class DeckView extends AnchorPane {
        public int n;
        public Text text;
        public DeckView() {
            this.setPrefSize(100.0,200.0);
            this.setMinSize(100.0,200.0);
            this.setMaxSize(100.0,200.0);
            text=new Text();
            text.setFill(Color.BLUE);
            text.setFont(new Font(20));
            ImageView back=new ImageView(View.cardBack);
            this.getChildren().add(back);
            this.getChildren().add(text);
            AnchorPane.setLeftAnchor(text,40.0);
            AnchorPane.setTopAnchor(text,20.0);
            AnchorPane.setTopAnchor(back,50.0);
        }
        public void update(int n) {
            this.n=n;
            text.setText(String.valueOf(n));
        }
        public Transition drawAnimation() {
            SequentialTransition ans=new SequentialTransition();
            PauseTransition p1=new PauseTransition(Duration.millis(0.0));
            p1.setOnFinished(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    update(n-1);
                }
            });
            ans.getChildren().add(p1);
            PauseTransition p2=new PauseTransition(Duration.millis(500.0));
            ans.getChildren().add(p2);
            return ans;
        }
    }
    public class LeaveCardsView extends AnchorPane {
        public Vector<CardView> cards=new Vector<CardView>();
        public boolean my;
        public LeaveCardsView(boolean my) {
            this.setPrefSize(100.0,450.0);
            this.setMinSize(100.0,450.0);
            this.setMaxSize(100.0,450.0);
            this.my=my;
        }
        public void update(Vector<Model.Card> c,int[] myColor,int[] oppColor) {
            this.getChildren().clear();
            cards.clear();
            int n=c.size();
            for (int i=0;i<n;i++) {
                CardView cur=new CardView(c.elementAt(i),true);
                cards.add(cur);
                this.getChildren().add(cur);
                if (myColor[cur.data.getColor()]>=oppColor[cur.data.getColor()]+2)
                    cur.setEffect(new Lighting());
                if (my) AnchorPane.setLeftAnchor(cur,0.0);
                else AnchorPane.setLeftAnchor(cur,50.0);
                if (n<4) {
                    if (my) AnchorPane.setTopAnchor(cur,(3-n)*150.0+i*150.0);
                    else AnchorPane.setTopAnchor(cur,i*150.0);
                } else {
                    AnchorPane.setTopAnchor(cur,300.0/(n-1)*i);
                }
            }
        }
        public Transition playAnimaTion(Vector<Model.Card> oldData,Vector<Model.Card> newData) {
            ParallelTransition par1=new ParallelTransition();
            ParallelTransition par2=new ParallelTransition();
            ParallelTransition par3=new ParallelTransition();
            this.getChildren().clear();
            int on=oldData.size(),nn=newData.size(),i=0,j=0;
            for (j=0;j<nn;j++) {
                Model.Card curData=newData.elementAt(j);
                CardView cur;
                if (i<oldData.size()&&oldData.elementAt(i).equal(curData)) {
                    cur=cards.elementAt(i);
                    if (my) AnchorPane.setLeftAnchor(cur,0.0);
                    else AnchorPane.setLeftAnchor(cur,50.0);
                    double oy,ny;
                    if (on<4) {
                        if (my) oy=(3-on)*150.0+i*150.0;
                        else oy=i*150.0;
                    } else oy=300.0/(on-1)*i;
                    if (nn<4) {
                        if (my) ny=(3-nn)*150.0+j*150.0;
                        else ny=j*150.0;
                    } else ny=300.0/(nn-1)*j;
                    AnchorPane.setTopAnchor(cur,oy);
                    TranslateTransition mov=new TranslateTransition(Duration.millis(500.0),cur);
                    mov.setByY(ny-oy);
                    if (ny!=oy) par1.getChildren().add(mov);
                    i++;
                } else {
                    cur=new CardView(curData,true);
                    if (my) AnchorPane.setLeftAnchor(cur,50.0);
                    else AnchorPane.setLeftAnchor(cur,0.0);
                    cur.setOpacity(0.0);
                    double ny;
                    if (nn<4) {
                        if (my) ny=(3-nn)*150.0+j*150.0;
                        else ny=j*150.0;
                    } else ny=300.0/(nn-1)*j;
                    AnchorPane.setTopAnchor(cur,ny);
                    par2.getChildren().add(cur.fadeAnimation(true));
                    TranslateTransition mov=new TranslateTransition(Duration.millis(500.0),cur);
                    if (my) mov.setByX(-50.0);
                    else mov.setByX(50.0);
                    par3.getChildren().add(mov);
                }
                this.getChildren().add(cur);
            }
            return new SequentialTransition(par1,par2,par3);
        }
    }
    public class ScoreView extends Text {
        public int v;
        public ScoreView() {
            v=0;
            this.setFont(new Font(20));
            this.setFill(Color.WHITE);
            this.setText("Score: "+v);
        }
        public void updateScore(int v) {
            this.v=v;
            this.setFill(Color.WHITE);
            this.setText("Score: "+v);
        }
        public void virtualUpdateScore(int vv,int safe) {
            if (safe>0) this.setFill(Color.GREEN);
            else if (safe==0) this.setFill(Color.WHITE);
            else if (safe<0) this.setFill(Color.RED);
            this.setText("Score: "+v+" -> "+vv);
        }
    }
    public class ParadeCardsView extends AnchorPane {
        public Vector<CardView> cards=new Vector<CardView>();
        public ParadeCardsView() {
            this.setPrefSize(400.0,200.0);
            this.setMinSize(400.0,200.0);
            this.setMaxSize(400.0,200.0);
        }
        public void update(Model.Parade p) {
            this.getChildren().clear();
            cards.clear();
            int n=p.cards.size();
            for (int i=0;i<n;i++) {
                cards.add(new CardView(p.cards.elementAt(i),true));
                this.getChildren().add(cards.elementAt(i));
                AnchorPane.setTopAnchor(cards.elementAt(i),50.0);
                if (n<=4) AnchorPane.setLeftAnchor(cards.elementAt(i),i*100.0);
                else AnchorPane.setLeftAnchor(cards.elementAt(i),i*300.0/(n-1));
            }
        }
        public Transition startAnimation() {
            SequentialTransition ans=new SequentialTransition();
            int n=cards.size();
            for (int i=0;i<n;i++) {
                CardView cur=cards.elementAt(i);
                if (cur!=null) {
                    cur.setOpacity(0.0);
                    ans.getChildren().add(cur.fadeAnimation(true));
                }
            }
            return ans;
        }
        public Transition playAnimation(Vector<Integer> state,Model.Card c) {
            ParallelTransition fade=new ParallelTransition();
            ParallelTransition move=new ParallelTransition();
            int n=cards.size(),nn=1,l=0;
            for (int i=0;i<n;i++)
                if (state.elementAt(i)<2) nn++;
            for (int i=0;i<n;i++) {
                CardView cur=cards.elementAt(i);
                int t=state.elementAt(i);
                if (t==2)
                    fade.getChildren().add(cur.fadeAnimation(false));
                else {
                    TranslateTransition curMove=new TranslateTransition(Duration.millis(500.0),cur);
                    double ox,nx;
                    if (n<=4) ox=i*100.0;
                    else ox=i*300.0/(n-1);
                    if (nn<=4) nx=l*100.0;
                    else nx=l*300.0/(nn-1);
                    curMove.setByX(nx-ox);
                    if (nx!=ox)	move.getChildren().add(curMove);
                    l++;
                }
            }
            move.setOnFinished(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    unnotify();
                }
            });
            CardView las=new CardView(c,true);
            las.setOpacity(0.0);
            AnchorPane.setTopAnchor(las,50.0);
            if (nn<=4) AnchorPane.setLeftAnchor(las,(nn-1)*100.0);
            else AnchorPane.setLeftAnchor(las,300.0);
            this.getChildren().add(las);
            return new SequentialTransition(fade,move,las.fadeAnimation(true));
        }
        void notify(Model.Card c) {
            Vector<Integer> tmp=model.parade.stateIfAdd(c);
            int n=cards.size();
            for (int i=0;i<n;i++) {
                int t=tmp.elementAt(i);
                if (t==1) cards.elementAt(i).setEffect(new Lighting());
                else if (t==2||t==3) {
                    AnchorPane.setTopAnchor(cards.elementAt(i),0.0);
                }
            }
        }
        void unnotify() {
            int n=cards.size();
            for (int i=0;i<n;i++) {
                cards.elementAt(i).setEffect(null);
                AnchorPane.setTopAnchor(cards.elementAt(i),50.0);
            }
        }
    }
    public class HandCardClick implements EventHandler<MouseEvent> {
        public void handle(MouseEvent mouse) {
            if (ani!=0||model.isNextMe==false||model.state==-1) return;
            int i=(int)(mouse.getX()+0.5)/100;
            if (i<0||i>=5||cards[i]==null) return;
            AudioClip play=new AudioClip(getClass().getResource("../play.mp3").toString());
            play.setCycleCount(1);
            play.play();
            Model back=model;
            Model.Card cc=back.me.handCards[i];
            modelBack.add(model);
            model=(Model)model.clone();
            if (model.state!=5)	model.me.updateLeaveCards(model.me.play(i));
            if (model.state==1)	model.me.draw();
            if (model.state==2||model.state==3) model.state++;
            if (model.state==5) model.me.end(i);

            SequentialTransition seq=new SequentialTransition();
            seq.getChildren().add(myHandCards.playAnimation(i));
            if (back.state!=5)
                seq.getChildren().add(parade.playAnimation(back.parade.stateIfAdd(cc),cc));
            seq.getChildren().add(myLeaveCards.playAnimaTion(back.me.leaveCards,model.me.leaveCards));
            if (back.state==1) seq.getChildren().add(myHandCards.drawAnimation(model.me.handCards[4]));
            ani++;
            seq.setOnFinished(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent evn) {
                    ani--;
                    checkState();
                    View.this.update(model);
                    if (model.state==-1) playEndAnimation();
                    if (model.state!=-1&&model.state!=5) computerRound();
                    if (model.state==5&&myHandCards.cards[3]!=null) {
                        Model tmp=(Model)model.clone();
                        cei=tmp.opp.chooseEndCards(4);
                        tmp.opp.end(cei);
                        cej=tmp.opp.chooseEndCards(3);
                        if (cej>=cei) cej++;
                        ParallelTransition par=new ParallelTransition();
                        TranslateTransition mov1=new TranslateTransition(Duration.millis(500.0),oppHandCards.cards[cei]);
                        TranslateTransition mov2=new TranslateTransition(Duration.millis(500.0),oppHandCards.cards[cej]);
                        mov1.setByY(30.0);
                        mov2.setByY(30.0);
                        par.getChildren().add(mov1);
                        par.getChildren().add(mov2);
                        par.play();
                    }
                }
            });
            seq.play();
        }
    }
}

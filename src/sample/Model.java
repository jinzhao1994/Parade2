package sample;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;


public class Model implements Cloneable{
    Deck deck=new Deck();
    Player me,opp;
    Parade parade;
    boolean isNextMe;
    int state,level;
    public Model(int lv,boolean first) {
        isNextMe=first;
        state=1;
        level=lv;
        deck=new Deck();
        me=new Player();
        opp=new Player();
        parade=new Parade();
    }
    public Object clone() {
        Model ans=new Model(0,true);
        for (int i=0;i<66;i++) ans.deck.cards[i]=(Card)deck.cards[i].clone();
        ans.deck.n=deck.n;
        ans.parade.cards.clear();
        for (int i=0;i<parade.cards.size();i++)
            ans.parade.cards.add((Card)parade.cards.elementAt(i).clone());

        ans.me.leaveCards.clear();
        for (int i=0;i<me.leaveCards.size();i++)
            ans.me.leaveCards.add((Card)me.leaveCards.elementAt(i).clone());
        for (int i=0;i<5;i++)
            if (me.handCards[i]!=null) ans.me.handCards[i]=(Card)me.handCards[i].clone();
            else ans.me.handCards[i]=null;
        for (int i=0;i<6;i++)
            ans.me.numOfColor[i]=me.numOfColor[i];

        ans.opp.leaveCards.clear();
        for (int i=0;i<opp.leaveCards.size();i++)
            ans.opp.leaveCards.add((Card)opp.leaveCards.elementAt(i).clone());
        for (int i=0;i<5;i++)
            if (opp.handCards[i]!=null) ans.opp.handCards[i]=(Card)opp.handCards[i].clone();
            else ans.opp.handCards[i]=null;
        for (int i=0;i<6;i++)
            ans.opp.numOfColor[i]=opp.numOfColor[i];

        ans.isNextMe=isNextMe;
        ans.state=state;
        ans.level=level;
        return ans;
    }
    public boolean check() {
        if (state==1) {
            if (deck.n==0) state=2;
            else if (me.hasAllColor()||opp.hasAllColor()) state=2;
            else return false;
            return true;
        } else if (state==4) {
            state=5;
            return true;
        } else if (state==5) {
            if (me.handCards[2]==null) {
                state=-1;
                return true;
            }
        }
        return false;
    }
    public void print() {
        System.out.println(this);
        System.out.println("--- Deck "+deck.n+" ---");
        for (int i=0;i<66;i++)
            System.out.print(deck.cards[i].v+" ");
        System.out.println();
        System.out.println("--- Parade ---");
        for (int i=0;i<parade.cards.size();i++)
            System.out.print(parade.cards.elementAt(i).v+" ");
        System.out.println();
        System.out.println("--- My Hand Cards ---");
        for (int i=0;i<5;i++)
            System.out.print(me.handCards[i].v+" ");
        System.out.println();
        System.out.println("--- My Leave Cards ---");
        for (int i=0;i<me.leaveCards.size();i++)
            System.out.print(me.leaveCards.elementAt(i).v+" ");
        System.out.println();
        System.out.println("--- Opp Hand Cards ---");
        for (int i=0;i<5;i++)
            System.out.print(opp.handCards[i].v+" ");
        System.out.println();
        System.out.println("--- Opp Leave Cards ---");
        for (int i=0;i<opp.leaveCards.size();i++)
            System.out.print(opp.leaveCards.elementAt(i).v+" ");
        System.out.println();
        System.out.println();
    }
    public class Card {
        public int v;
        public Card(int vv) {
            v=vv;
        }
        public Object clone() {
            return new Card(v);
        }
        public int getNumber() {
            return v%11;
        }
        public int getColor() {
            return v/11;
        }
        public boolean equal(Card b) {
            return v==b.v;
        }
    }
    public class Deck {
        public Card[] cards=new Card[66];
        public int n=66;
        public Deck() {
            for (int i=0;i<n;i++) cards[i]=new Card(i);
            Random rand=new Random();
            for (int i=0;i<n;i++) {
                int j=rand.nextInt(n-i)+i;
                Card t=cards[j];
                cards[j]=cards[i];
                cards[i]=t;
            }
        }
        public Card draw() {
            return cards[--n];
        }
    }
    public class Parade {
        public Vector<Card> cards=new Vector<Card>();
        Parade() {
            for (int i=0;i<6;i++) cards.add(deck.draw());
        }
        public Vector<Integer> stateIfAdd(Card c) {
            Vector<Integer> ans=new Vector<Integer>();
            int n=cards.size();
            for (int i=0;i<n;i++) {
                Card cur=cards.elementAt(i);
                if (i+c.getNumber()>=n) ans.add(1);
                else if (cur.getColor()==c.getColor()||cur.getNumber()<=c.getNumber()) ans.add(2);
                else ans.add(0);
            }
            return ans;
        }
        public Vector<Card> addCard(Card c) {
            Vector<Integer> sta=stateIfAdd(c);
            Vector<Card> leave=new Vector<Card>();
            Vector<Card> residual=new Vector<Card>();
            int n=cards.size();
            for (int i=0;i<n;i++) {
                Card cur=cards.elementAt(i);
                if (sta.elementAt(i)!=2) residual.add(cur);
                else leave.add(cur);
            }
            cards=residual;
            cards.add(c);
            Collections.sort(leave,new Comparator<Card>() {
                public int compare(Card left,Card right) {
                    return left.v-right.v;
                }
            });
            return leave;
        }
    }
    public class Player {
        public Vector<Card> leaveCards=new Vector<Card>();
        public Card[] handCards=new Card[5];
        public int[] numOfColor=new int[6];
        Player() {
            for (int i=0;i<5;i++) handCards[i]=deck.draw();
        }
        public void updateHandCards() {
            int i=0,j=0;
            while (i<5) {
                if (handCards[i]!=null) handCards[j++]=handCards[i++];
                else i++;
            }
            while (j<5) handCards[j++]=null;
        }
        public void draw() {
            handCards[4]=deck.draw();
        }
        public boolean hasAllColor() {
            for (int i=0;i<6;i++) if (numOfColor[i]==0) return false;
            return true;
        }
        public int randomChooseHandCards() {
            return new Random().nextInt(5);
        }
        public int normalChooseHandCards() {
            int ans=-1,mind=0,d;
            for (int i=0;i<5;i++) {
                Model tmp=(Model)Model.this.clone();
                if (this==opp) {
                    tmp.opp.updateLeaveCards(tmp.opp.play(i));
                    d=tmp.opp.getScore(tmp.me)-tmp.me.getScore(tmp.opp);
                } else {
                    tmp.me.updateLeaveCards(tmp.me.play(i));
                    d=tmp.me.getScore(tmp.opp)-tmp.opp.getScore(tmp.me);
                }
                if (ans==-1||d<mind) {
                    mind=d;
                    ans=i;
                }
            }
            return ans;
        }
        public int randomChooseEndCards(int n) {
            return new Random().nextInt(n);
        }
        public int normalChooseEndCards(int n) {
            Model[] test=new Model[n];
            for (int i=0;i<n;i++) {
                test[i]=(Model)Model.this.clone();
                if (this==opp) test[i].opp.end(i);
                else test[i].me.end(i);
            }
            int ans=-1,mind=0,d;
            for (int i=0;i<n;i++) {
                if (this==opp) d=test[i].opp.getScore(test[i].me)-test[i].me.getScore(test[i].opp);
                else d=test[i].me.getScore(test[i].opp)-test[i].opp.getScore(test[i].me);
                if (ans==-1||d<mind) {
                    mind=d;
                    ans=i;
                }
            }
            return ans;
        }
        public int chooseHandCards() {
            if (level==1) return normalChooseHandCards();
            else return randomChooseHandCards();
        }
        public int chooseEndCards(int n) {
            if (level==1) return normalChooseEndCards(n);
            else return randomChooseEndCards(n);
        }
        public Vector<Card> play(int t) {
            Vector<Card> leave=parade.addCard(handCards[t]);
            handCards[t]=null;
            updateHandCards();
            return leave;
        }
        public void updateLeaveCards(Vector<Card> leave) {
            Vector<Integer> moveUp=new Vector<Integer>();
            Vector<Card> newLeaveCards=new Vector<Card>();
            int i=0,j=0;
            while (i<leaveCards.size()&&j<leave.size()) {
                Card ii=leaveCards.elementAt(i);
                Card jj=leave.elementAt(j);
                if (ii.v<jj.v) {
                    newLeaveCards.add(ii);
                    moveUp.add(i);
                    i++;
                } else {
                    newLeaveCards.add(jj);
                    moveUp.add(-1);
                    numOfColor[jj.getColor()]++;
                    j++;
                }
            }
            for (;i<leaveCards.size();i++) {
                Card ii=leaveCards.elementAt(i);
                newLeaveCards.add(ii);
                moveUp.add(i);
            }
            for (;j<leave.size();j++) {
                Card jj=leave.elementAt(j);
                newLeaveCards.add(jj);
                moveUp.add(-1);
                numOfColor[jj.getColor()]++;
            }
            leaveCards=newLeaveCards;
            //return moveUp;
        }
        public void end(int t) {
            Vector<Card> leave=new Vector<Card>();
            leave.add(handCards[t]);
            handCards[t]=null;
            updateHandCards();
            updateLeaveCards(leave);
        }
        public void noMoveEnd(int t) {
            Vector<Card> leave=new Vector<Card>();
            leave.add(handCards[t]);
            handCards[t]=null;
            updateLeaveCards(leave);
        }
        public int getScore(Player b) {
            int ans=0;
            for (int i=0;i<leaveCards.size();i++) {
                Card cur=leaveCards.elementAt(i);
                if (numOfColor[cur.getColor()]-2>=b.numOfColor[cur.getColor()]) ans++;
                else ans+=cur.getNumber();
            }
            return ans;
        }
    }
}
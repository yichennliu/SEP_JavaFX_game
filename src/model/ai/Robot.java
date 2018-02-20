package model.ai;

import javafx.geometry.Point2D;
import model.enums.InputDirection;
import model.enums.Token;
import model.enums.WinningStatus;
import model.game.Feld;
import model.game.Level;

import java.util.*;

public class Robot implements AI {

    private Level level;
    private int maxWaittime = 5;
    private InputDirection[] inputDirections;
    private Target currentTarget;
    private HashSet<Token> passableTokens = new HashSet<Token>();

    public Robot(Level level, int maxWaittime){
        this.level = level;
        this.maxWaittime = maxWaittime;
        inputDirections = InputDirection.values();
        initPassableTokens();
    }

    private void initPassableTokens(){
        passableTokens.add(Token.PATH);
        passableTokens.add(Token.MUD);
        passableTokens.add(Token.GEM);
        passableTokens.add(Token.EXIT);
    }

    public InputDirection getNextMove() {
        /*continue way to last Target if avalaible*/
        Feld me = getMe();
        Feld target = (currentTarget!=null) ? (level.getFeld(currentTarget.getRow(),currentTarget.getColumn())) : null;
        /*reset target, if reached*/
        if(me!=null && target!=null &&
                (target == me ||
                target.isToken(Token.EXIT) && distance(me,target)<2)){
            currentTarget=null;
        }

        if(currentTarget!=null){
            Feld feld = level.getFeld(currentTarget.getRow(),currentTarget.getColumn());
            Breadcrumb stepTo = findSecurePath(getMe(),feld,20);
            if(stepTo!=null) return stepTo.getInputDirection();
            currentTarget = null;
        }
        List<FeldWithDistance> feldDistList = (getNearest(getMe(), Token.GEM, 20));
        /*find Felds with GEMS*/
        if(feldDistList.size()>0){
            for(int i = 0; i<feldDistList.size(); i++){
                Breadcrumb stepTo = findSecurePath(getMe(),feldDistList.get(i).getFeld(),50);
                if (stepTo == null) continue;
                return stepTo.getInputDirection();
            }
            currentTarget = null;
        }
        /*no GEMS left*/
        List<FeldWithDistance> exitList = getNearest(getMe(),Token.EXIT,level.getWidth()+level.getHeight());
            if(exitList.size()>0) {
                System.out.println("EXIT gefunden");
                Breadcrumb goTo = findSecurePath(getMe(),exitList.get(0).getFeld(),50);
                if(goTo!=null){
                    return goTo.getInputDirection();
                }
                else  {
                    System.out.println("Was da los?!?"+goToFreePlace() );
                    return goToFreePlace();
                }
            }

        currentTarget =null;
        return null;
    }

    private InputDirection goToFreePlace(){
        System.out.println("Go to free place!!!");
                    Feld me = getMe();
                    if(me!=null){
                        for(InputDirection d: inputDirections){
                            if(!d.isDigOnly()){
                                Feld neighbour = me.getNeighbour(d);
                                if(passableTokens.contains(neighbour.getToken())){
                                    if(neighbour!=me.getCurrentTokenCameFrom())
                                        return d;
                                }
                }
            }
        }
        return null;
    }


    private Feld getMe(){
        return this.level.whereAmI();
    }

    /*seeks for save (no death) shortest Path to specified Feld, returns null, if no path found*/
    private Breadcrumb findSecurePath(Feld start, Feld end, int maxSteps){
        System.out.println("Wegsuche gestartet (von " + start.getRow() + " | " + start.getColumn() + " Nach " + end.getRow() + " | " + end.getColumn() + ")");
        Level levelClone = level.clone();
        Feld levelCloneStart = level.getFeld(start.getRow(),start.getColumn());
        Breadcrumb startBreadcrumb = new Breadcrumb(levelCloneStart,null, null,levelClone,0,0);
        List<Breadcrumb> path = breadthFirstSearch(startBreadcrumb,end.getRow(),end.getColumn(),maxSteps);
        if(path!=null) this.currentTarget = new Target(path.get(path.size()-1).getCurrentFeld());
        return (path!=null) ? path.get(1) : null;
    }

    /*finds secure Path to endRow and -column*/
    private List<Breadcrumb> breadthFirstSearch(Breadcrumb start, int rowEnd, int colEnd, int maxSteps){

        ArrayDeque<Breadcrumb> deque = new ArrayDeque<>();
        deque.add(start);
        HashSet<Point2D> visited = new HashSet<>();


        while(!deque.isEmpty()){
            Breadcrumb currentBreadcrumb = deque.pollFirst();
            int currentStep = currentBreadcrumb.getStep();
            if(currentBreadcrumb.getWaittime()> maxWaittime || currentStep > maxSteps) {
                continue;
            }
            Level currentLevel = currentBreadcrumb.getLevel();
            Feld currentFeld = currentBreadcrumb.getCurrentFeld();

            visited.add(new Point2D(currentFeld.getColumn(), currentFeld.getRow()));
            if(currentFeld.getRow() == rowEnd && currentFeld.getColumn() == colEnd){
               List<Breadcrumb> path = rebuildPath(currentBreadcrumb);
               return path;
            }
            Set<Map.Entry<InputDirection,Feld>> neighbours = getNeighboursToVisit(currentBreadcrumb);
            removeUnpassable(neighbours);
            if(currentStep==0) {
                removeUnsafe(neighbours,currentLevel);
            }
            for(Map.Entry<InputDirection,Feld> neighbour : neighbours){
                if(neighbour.getKey().isDigOnly()) continue;
                    Feld newPositionMe = neighbour.getValue();
                    boolean stay = (newPositionMe.getColumn() == currentFeld.getColumn() &&
                                    newPositionMe.getRow() == currentFeld.getRow());
                    int waittime = (stay) ? currentBreadcrumb.getWaittime()+1 : 0;
                    if(!stay) {
                        Point2D newPos = new Point2D(newPositionMe.getColumn(),newPositionMe.getRow());
                        if(visited.contains(newPos)) continue;
                        else visited.add(newPos);
                    }
                    Breadcrumb newBreadcrumb = new Breadcrumb(newPositionMe,currentBreadcrumb, neighbour.getKey(),
                            currentLevel, waittime,currentStep+1);
                    deque.addLast(newBreadcrumb);
                }
            }
        return null;
    }

    private void removeUnsafe(Set<Map.Entry<InputDirection, Feld>> neighbours,Level level) {
        Iterator<Map.Entry<InputDirection,Feld>> it = neighbours.iterator();
        while(it.hasNext()){
            Map.Entry<InputDirection,Feld> entry = it.next();
            Level levelCopy = level.clone();
            levelCopy.setInputDirection(entry.getKey());
            tick(levelCopy);
            levelCopy.setInputDirection(null);
            tick(levelCopy);
            if(levelCopy.getWinningStatus()==WinningStatus.LOST){
                System.out.println("Going " +entry.getKey()+" would kill");
                it.remove();
            }
        }
    }

    private void removeUnpassable(Set<Map.Entry<InputDirection,Feld>> set){
        Iterator<Map.Entry<InputDirection,Feld>> it = set.iterator();
        while(it.hasNext()){
            Map.Entry<InputDirection,Feld> entry = it.next();
            if(entry.getValue()!=null) {
                if (!passableTokens.contains(entry.getValue().getToken())) {
                    it.remove();
                }
            }
        }
    }

    /*gets set of felds linked with their InputDirection*/
    private Set<Map.Entry<InputDirection,Feld>> getNeighboursToVisit(Breadcrumb breadcrumb){
        Feld feld = breadcrumb.getCurrentFeld();
        Set<Map.Entry<InputDirection,Feld>> neighbours = new HashSet<>();
        for(InputDirection id : inputDirections){
            Feld neighbour = feld.getNeighbour(id);
            if(neighbour!=null){
                Map.Entry<InputDirection,Feld> entry = new AbstractMap.SimpleEntry<>(id,neighbour);
                neighbours.add(entry);
            }

        }
        return neighbours;
    }

    /*rebuilds Path based on last breadcrumb in Path*/
    private List<Breadcrumb> rebuildPath(Breadcrumb breadcrumb){
        Breadcrumb current = breadcrumb;
        List<Breadcrumb> list = new ArrayList<>();
        while(current!=null){
            list.add(0,current);
            current = current.getFrom();
        }
        return list;
    }

    /*returns sorted List of Fields with specified token*/
    private List<FeldWithDistance> getNearest(Feld startFeld, Token token, double distance){
        Set<FeldWithDistance> found = lookFor(startFeld,startFeld,token,new HashSet<>(),new HashSet<>(),distance);
        List<FeldWithDistance> list = new ArrayList<>(found);
        Collections.sort(list);
        return list;
    }

    /**/
    private boolean isLockedIn(Feld me){
        for(Feld feld : me.getNeighboursDirect()){
//            if()
        }return true;
    }

    /*starts deepSearch for Fields with specified Token in specified radius around provided field*/
    private Set<FeldWithDistance> lookFor(Feld current, Feld start, Token token,Set<FeldWithDistance> found, Set<Feld> alreadyScanned, double maxDistance){
        double distance = distance(current,start);
        if(distance<=maxDistance) {
            Token currentToken = current.getToken();
            if (currentToken == token) {
                found.add(new FeldWithDistance(current,distance));
            }
            alreadyScanned.add(current);
                for(Feld neighbour: current.getNeighbours()){
                    if(!alreadyScanned.contains(neighbour)){
                        found.addAll(lookFor(neighbour,start,token,found,alreadyScanned,maxDistance));
                    }
            }
        }
        return found;
    }

    /*calculates the distance between two Felds*/
    private double distance(Feld f1, Feld f2){
        Point2D f1P = new Point2D(f1.getColumn(),f1.getRow());
        Point2D f2P = new Point2D(f2.getColumn(),f2.getRow());
        return Math.abs(f1P.distance(f2P));
    }

    /*computes a tick on a provided level. returns the winningStatus*/
    private WinningStatus tick(Level levelToTick){
        /* Compute a tick */
        levelToTick.resetProperties();
        //levelToTick.executePre();
        levelToTick.executeMainRules();
        //levelToTick.executePost();

        levelToTick.tick();
        levelToTick.setInputDirection(null);
        return levelToTick.getWinningStatus();
    }


}

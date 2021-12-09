package educate.timetabling.entity;

import java.util.ArrayList;

public class AcademicCalendarRecord {
    private ArrayList activity = new ArrayList();
    private AcademicActivityType  ActivityType;
    public AcademicCalendarRecord (){
        ActivityType = null;
    }
    public void setActivityType(AcademicActivityType ActivityType){
        this.ActivityType = ActivityType;
    }
    public void addActivity(AcademicActivity aa){
        activity.add(aa);
    }
    public int getActivitySize(){
        return activity.size();
    }
    public AcademicActivityType getActivityType(){
        return ActivityType;
    }
    public ArrayList getActivity(){
        return activity;
    }
}

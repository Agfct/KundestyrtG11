package modules;

public enum TimeLineChanges {
	MEDIAOBJECTADDED, MEDIAOBJECTREMOVED, MEDIAOBJECTMODIFIED, MEDIAOBJECTORDER, MODIFIED, ADDED, REMOVED, ORDER;

	public String getModificationType(){
		return "yes";
	}
}
